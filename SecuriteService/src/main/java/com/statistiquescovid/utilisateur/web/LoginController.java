package com.statistiquescovid.utilisateur.web;

import java.util.Locale;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.statistiquescovid.utilisateur.entites.CompteUtilisateur;
import com.statistiquescovid.utilisateur.entites.VerificationToken;
import com.statistiquescovid.utilisateur.jwt.Utilisateur;
import com.statistiquescovid.utilisateur.service.IUtilisateurService;
import com.statistiquescovid.utilisateur.service.MailService;
import com.statistiquescovid.utilisateur.service.ISecurityUserService;
import com.statistiquescovid.utilisateur.web.dto.GenericResponse;
import com.statistiquescovid.utilisateur.web.dto.JWTResponse;
import com.statistiquescovid.utilisateur.web.dto.LoginDTO;
import com.statistiquescovid.utilisateur.web.dto.LogoutResponse;
import com.statistiquescovid.utilisateur.web.dto.PasswordDTO;
import com.statistiquescovid.utilisateur.web.dto.RefreshTokenDTO;
import com.statistiquescovid.utilisateur.web.dto.UserDTO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("api/covid/v1/comptes-utilisateurs")
@Api(value = "Gestion des utilisateurs et préférecences utilisateurs")
public class LoginController {
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private IUtilisateurService utilisateurService;
	@Autowired
	private ISecurityUserService securiteUserService;
	@Autowired
	private MailService mailService;
	@Autowired
	private MessageSource messages;
	@Autowired
	private Environment environment;
	final Locale locale = Locale.FRANCE;

	//Configuration adresses/URLs du client Angular dans le fichier de configuration
	protected String CLIENT_ADDRESS_BASE = "http://localhost:4200";

	@ApiOperation(value = "Inscription d'un nouvel utilisateur")
	@PostMapping({"inscription"})
	public ResponseEntity<?> registerUserAccount(
			@ApiParam(required = true, name = "utilisateurDTO", value = "Les informations de l'utilisateur qui s'inscrit") 
			@Valid @RequestBody UserDTO utilisateurDTO, HttpServletRequest request) {
		LOGGER.info("Registering user account with information: {}", utilisateurDTO);
		System.err.println("Registering :: " + utilisateurDTO);

		CompteUtilisateur compteUtilisateur = utilisateurService.registerNewUserAccount(utilisateurDTO);
		if(compteUtilisateur == null) {
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Echec d'ouverture du compte");
		}

		String activationToken = UUID.randomUUID().toString();
		utilisateurService.createVerificationTokenForUser(compteUtilisateur, activationToken);

		// TODO: A s'assurer !
		String appContextPath = getAppContextUrl(request);

		this.mailService.envoyerMailConfirmationInscription(compteUtilisateur, appContextPath, activationToken);
		String message = "Vous devriez recevoir un mail bientôt pour activer votre compte";
		return new ResponseEntity<GenericResponse>(
				new GenericResponse(message),
				HttpStatus.OK);
	}

	// Activer le compte utilisateur par le lien envoyé par mail
	@GetMapping("confirmer-inscription")
	public RedirectView confirmRegistration(@RequestParam("token") final String token) {
		LOGGER.info("*** Confirmation d'un nouveau compte utilisateur par un lien envoyé par mail ... token = " + token);
		String clientUrlBase = environment.getProperty("client.adress-base");
		if(clientUrlBase == null || clientUrlBase.trim().isEmpty()) {
			clientUrlBase = this.CLIENT_ADDRESS_BASE;
		}
		RedirectView redirectView = new RedirectView();
		final String result = utilisateurService.validateVerificationToken(token);
		if (result.equals("valid")) {
			// On peut authentifier automatiquement l'utilisateur
			final CompteUtilisateur compteUtilisateur = utilisateurService.getUser(token);
			JWTResponse jwtResponse = this.automaticAuthentication(compteUtilisateur);
			String message =  messages.getMessage("message.accountVerified", null, locale);
			if(message == null) {
				message = "Compte activé avec succès";
			}

			// C'est ce qui est souhaité !
			LOGGER.info("*** Activate account BY LINK in sented Mail --> REDIRECT TO " + clientUrlBase + "/espace-prive/?user=" + compteUtilisateur.getId() + "&accessToken=" + jwtResponse.getAccess_token());
			redirectView.setUrl(clientUrlBase + "/espace-prive/?actif=true&message="+ message +"&user=" + compteUtilisateur.getId() + "&accessToken=" + jwtResponse.getAccess_token());
			return redirectView;
		}

		String message =  messages.getMessage("auth.message." + result, null, locale);
		if(message == null) {
			message = "Compte non activé";
		}		

		// si le Token (que le serveur a envoyé par le mail) est invalid (erreur) ou qu'il a expiré (l'utilisateur doit En demander un autre)
		LOGGER.info("*** Confirmation d'un nouveau compte utilisateur --> REDIRECT TO " + clientUrlBase + "/login?actif=false&message="+ message + "&token=" + token);
		redirectView.setUrl(clientUrlBase + "/login?actif=false&message="+ message + "&token=" + token);
		return redirectView;
	}

	// Re-envoyer l'e-mail d'activation et de vérification du compte
	@GetMapping("renvoyer-mail-confirmation-inscription")
	public ResponseEntity<GenericResponse> resendRegistrationToken(final HttpServletRequest request, @RequestParam("token") final String existingToken) {
		final VerificationToken newToken = utilisateurService.generateNewVerificationToken(existingToken);
		final CompteUtilisateur compteUtilisateur = utilisateurService.getUser(newToken.getToken());
		if(newToken == null || compteUtilisateur == null) {
			return new ResponseEntity<GenericResponse>(
					new GenericResponse("Impossible d'identitifer le compte utilisateur"),
					HttpStatus.OK);
		}

		String activationToken = newToken.getToken();
		// TODO: A s'assurer !
		String appContextPath = getAppContextUrl(request);

		boolean mailEnvoye = this.mailService.envoyerMailConfirmationInscription(compteUtilisateur, appContextPath, activationToken);
		if(mailEnvoye) {
			String message = messages.getMessage("message.resendToken", null, request.getLocale());
			if(message == null) {
				message = "Vous devriez recevoir un mail bientôt pour activer votre compte";
			}
			return new ResponseEntity<GenericResponse>(
					new GenericResponse(message),
					HttpStatus.OK);
		}

		LOGGER.info("*** Email d'activation non envoyé");
		return new ResponseEntity<GenericResponse>(
				new GenericResponse(true, "Echec", "Erreur : Aucun compte utilisateur avec l'adresse email ["+ compteUtilisateur.getEmail() +"]"),
				HttpStatus.OK);
	}

	@PostMapping("mot-de-passe-oublie/{email}")
	public ResponseEntity<GenericResponse> resetPassword(final HttpServletRequest request, @PathVariable("email") final String userEmail) {
		LOGGER.info("*** Demande de réinitialisation de mot de passe pour l'Email = " + userEmail + " ...");
		final CompteUtilisateur compteUtilisateur = utilisateurService.findUserByEmail(userEmail);
		if (compteUtilisateur != null) {
			LOGGER.info("*** CompteUtilisateur email existe ...");
			final String token = UUID.randomUUID().toString();
			utilisateurService.createPasswordResetTokenForUser(compteUtilisateur, token);

			// TODO: A s'assurer !
			String contextPath = getAppContextUrl(request);

			boolean mailEnvoye = this.mailService.envoyerMailReinitialisationMotDePasse(compteUtilisateur, contextPath, token);
			if(mailEnvoye) {
				LOGGER.info("Email bien envoyé");
				String message = messages.getMessage("message.resetPasswordEmail", null, request.getLocale());
				if(message == null || message.trim().isEmpty()) {
					message = "Vous devriez recevoir un courriel de réinitialisation du mot de passe sous peu";
				}
				LOGGER.info("*** CompteUtilisateur email -- REPONSE :: " + message);
				return new ResponseEntity<GenericResponse>(new GenericResponse(message), HttpStatus.OK);
			}
			else {
				LOGGER.error("Echec d'envoie du mail");
				// .. A gérer !
			}			
		}

		String message = messages.getMessage("message.resetPasswordEmailNotFound", null, request.getLocale());
		if(message == null || message.trim().isEmpty()) {
			message = "Nous n'avons pas pu identifier votre adresse e-mail";
		}
		LOGGER.info("*** CompteUtilisateur email -- REPONSE :: " + message);
		return new ResponseEntity<GenericResponse>(
				new GenericResponse(true, message, "Erreur : Aucun compte utilisateur avec l'adresse email ["+ userEmail +"]"),
				HttpStatus.OK);
	}

	// Via un lien envoyé par e-mail
	@GetMapping("changer-mot-de-passe/{utilisateurId}/{token}")
	public RedirectView showChangePasswordPage(
			@PathVariable("utilisateurId") final String id, 
			@PathVariable("token") final String token) {
		LOGGER.info("*** CHANGE password BY LINK on sented Mail ...");
		String clientUrlBase = environment.getProperty("client.adress-base");
		if(clientUrlBase == null || clientUrlBase.trim().isEmpty()) {
			clientUrlBase = this.CLIENT_ADDRESS_BASE;
		}
		final String result = securiteUserService.validatePasswordResetToken(id, token);
		RedirectView redirectView = new RedirectView();
		if (result != null) { // si le Token (que le serveur a envoyé par le mail) est invalid (erreur) ou qu'il a expiré (l'utilisateur doit S'enregistrer/En demander un autre)
			LOGGER.info("*** CHANGE password BY LINK on sented Mail --> REDIRECT TO " + clientUrlBase + "/login");
			redirectView.setUrl(clientUrlBase + "/login");
			return redirectView;
		}

		// C'est ce qui est souhaité !
		LOGGER.info("*** CHANGE password BY LINK in sented Mail --> REDIRECT TO " + clientUrlBase + "/reinitialiser-mot-de-passe/" + id + "/" + token +"?lang=fr");
		redirectView.setUrl(clientUrlBase + "/reinitialiser-mot-de-passe/" + id + "/" + token);
		return redirectView;
	}

	// Doit être accompagné du Token envoyé par e-mail !
	@PostMapping("reinitialiser-mot-de-passe")
	public ResponseEntity<GenericResponse> savePassword(@RequestBody PasswordDTO passwordDTO) {
		LOGGER.info("*** RESET password ...");
		try {
			// On refait le controle du Token avec Id utilisateur
			final String result = securiteUserService.validatePasswordResetToken(passwordDTO.getUserId(), passwordDTO.getUserTokenResetPassword());
			if(result == null) { // si le result est Null alors l'utilisateur peut bien changer son mot de passe
				LOGGER.info("*** RESET password --> Contrôle réussit --> recherche de l'CompteUtilisateur...");
				// On cherche l'utilisateur par son Id, et on modifie son mot de passe
				CompteUtilisateur compteUtilisateur = utilisateurService.getUserByPasswordResetToken(passwordDTO.getUserTokenResetPassword());
				if(compteUtilisateur != null) {
					boolean saved = utilisateurService.changeUserPassword(compteUtilisateur, passwordDTO.getNewPassword());
					// Si le changement est bien effectué, on supprime le Token
					if(saved) {
						String message = messages.getMessage("message.resetPasswordSuc", null, locale);
						if(message == null || message.trim().isEmpty()) {
							message = "Mot de passe réinitialisé avec succès";
						}
						LOGGER.info("*** RESET password --> Contrôle réussit --> CompteUtilisateur trouvé --> changement du Password effectué --> suppression du Token ... --> REPONSE :: " + message);
						return new ResponseEntity<GenericResponse>(
								new GenericResponse(message), 
								HttpStatus.OK);
						// implementer et exécuter la méthode de suppression du token
						// ...
					} else {
						String message = messages.getMessage("message.error", null, locale);
						if(message == null || message.trim().isEmpty()) {
							message = "Une erreur est survenue";
						}
						LOGGER.warn("*** RESET password --> Contrôle réussit --> CompteUtilisateur trouvé --> changement du Password Non effectué ! -- REPONSE :: " + message);
						return new ResponseEntity<GenericResponse>(
								new GenericResponse(true, message, "Erreur de réinitialisation du mot de passe, lors de l'enregistement du nouveau mot de passe"), 
								HttpStatus.OK);
					}
				} else {
					String message = messages.getMessage("message.error", null, locale);
					if(message == null || message.trim().isEmpty()) {
						message = "Une erreur est survenue";
					}
					LOGGER.warn("*** RESET password --> Contrôle réussit --> CompteUtilisateur NON trouvé --> Penser à implementer une methode pour trouver l'user par son Id ou revoir celle par Token !! -- REPONSE :: " + message);
					return new ResponseEntity<GenericResponse>(
							new GenericResponse(true, message, "Erreur de réinitialisation du mot de passe, compte utilisateur non identifié"), 
							HttpStatus.OK);
				}
			}
			else {
				String message = messages.getMessage("message.resetPasswordEmailLinkNotValid", null, locale);
				if(message == null || message.trim().isEmpty()) {
					message = "Erreur, le lien est invalide ou a été corrompu";
				}
				LOGGER.warn("*** RESET password --> Contrôle echoué ! -- REPONSE :: " + message);
				return new ResponseEntity<GenericResponse>(
						new GenericResponse(true, message, "Erreur de réinitialisation du mot de passe, lien invalide ou corrompu"), 
						HttpStatus.OK);
			}
		} catch (Exception e) {
			String message = messages.getMessage("message.error", null, locale);
			if(message == null || message.trim().isEmpty()) {
				message = "Une erreur est survenue";
			}
			LOGGER.error("*** RESET password --> Exception :: " + e.getMessage() + " -- REPONSE :: " + message);
			return new ResponseEntity<GenericResponse>(
					new GenericResponse(true, message, "Erreur de réinitialisation du mot de passe"), 
					HttpStatus.OK);
		}
	}

	// Mise à jour volontaire du mot de passe
	// 1. S'assurer que l'uilisateur soit authentifié et 
	// 2. Vérifier si l'ancien mot de passe est correcte avant de procéder à la mise à jour !
	@PostMapping("modifier-mot-de-passe")
	public ResponseEntity<?> updateUserPassword(@RequestBody PasswordDTO passwordDTO) {
		LOGGER.info("*** UPDATE password ...");
		// 1. S'assurer que l'utilisateur soit authentifié
		Utilisateur utilisateurCourant = this.securiteUserService.getUtilisateurCourant();
		if (utilisateurCourant == null) {
			LOGGER.error("******* Utilisateur non authentifié");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		final CompteUtilisateur compteUtilisateur = utilisateurService.findUserByEmail(utilisateurCourant.getEmail());
		if (!utilisateurService.checkIfValidOldPassword(compteUtilisateur, passwordDTO.getOldPassword())) {
			return new ResponseEntity<GenericResponse>(new GenericResponse("Ancien mot de passe incorrect", false, true), HttpStatus.OK);
		}

		utilisateurService.changeUserPassword(compteUtilisateur, passwordDTO.getNewPassword());
		String message = messages.getMessage("message.updatePasswordSuc", null, locale);
		if(message == null || message.trim().isEmpty()) {
			message = "Mot de passe mis à jour avec succès";
		}
		LOGGER.info("*** UPDATE password -- REPONSE :: " + message);
		return new ResponseEntity<GenericResponse>(new GenericResponse(message, true, false), HttpStatus.OK);
	}

	@PostMapping("connexion")
	public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
		if(loginDTO == null || loginDTO.getLogin() == null || loginDTO.getPassword() == null) {
			return ResponseEntity.badRequest().body("L'email et le mot de passe sont requis");
		}
		System.err.println("**** connexion ... ");
		// Authentifier l'utilisateur depuis la couche service : avec le mot de passe Hashé
		// Ensuite, s'assurer que l'utilisateur soit authentifié et Générer le Token avec des infos basiques (Nom, prénom, Email, Roles)

		// 1. Chercher l'utilisateur
		CompteUtilisateur compteUtilisateur = utilisateurService.login(loginDTO.getLogin(), loginDTO.getPassword());
		if(compteUtilisateur == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		// 2. Trouver le Token d'authentification
		System.out.println("# Authentification de l'utilisateur ...");
		try {
			String accessToken = securiteUserService.generateJWTByUser(compteUtilisateur);
			if(accessToken != null) {
				// 3. Valider le Token et placer l'utilisateur dans le context !
				securiteUserService.placerUtilisateurDansLeContext(accessToken);

				// 4. Générer l'Id du Token permettant de raffraichir ce Token, déconnecter l'utilisateur ...
				String refreshToken = UUID.randomUUID().toString().replace("-", "");
				// 5. Pour une meilleur implémentation professionnelle, il faut sauvegarder les deux Tokens dans la BD
				// ...

				// 6. Renvoyer la reponse
				return ResponseEntity.ok(new JWTResponse(accessToken, refreshToken, compteUtilisateur.getEmail(), compteUtilisateur.getNom(), Long.valueOf(72000 * 60)));
			} 
			else {
				LOGGER.info("Problème d'Authentification de l'utilisateur !");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
		} catch (Exception e) {
			LOGGER.error("Erreur d'Authentification de l'utilisateur : " + e.getMessage());
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}

	// Pas necessaire mais très bien pour un usage assez professionnel !
	@PostMapping("refreshToken")
	public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenDTO refreshTokenDTO) {
		if(refreshTokenDTO == null || refreshTokenDTO.getRefreshToken() == null || refreshTokenDTO.getRefreshToken().trim().isEmpty()) {
			return ResponseEntity.badRequest().body("L'd du Token (RefreshToken) est requis");
		}

		// TODO: S'assurer que le Refresh Token et valid, et regénérer un nouveau Token (Nouvelle authentification)
		// ... 
		//

		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
	}

	/// GET /deconnexion/' + utilisateurId + '?refreshToken=' + refreshToken
	@ApiOperation(value = "Déconnexion d'un utilisateur")
	@PostMapping({"deconnexion/{utilisateurId}"})
	public ResponseEntity<LogoutResponse> logout(
			@ApiParam(required = true, name = "utilisateurId", value = "L'Id de l'utilisateur") 
			@PathVariable(name = "utilisateurId") String utilisateurId,
			@ApiParam(required = false, name = "refreshToken", value = "L'Id du token à deconnecter ; il se peut que l'utilisateur soit connecté sur plusieurs appareils") 
			@RequestParam(name = "refreshToken", required = false) String refreshToken, HttpServletRequest request) {
		LOGGER.info("Déconnexion d'un utilisateur {} sur le tokenRefresh {}", utilisateurId, refreshToken);
		System.err.println("Déconnexion :: " + utilisateurId);
		//LogoutResponse logout = utilisateurService.logout(utilisateurId, refreshToken);
		//if(logout == null) {
		//	return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Echec de la déconnexion du compte");
		//}
		return new ResponseEntity<LogoutResponse>(new LogoutResponse(true), HttpStatus.OK);
	}

	private JWTResponse automaticAuthentication(CompteUtilisateur compteUtilisateur) {
		if(compteUtilisateur == null || compteUtilisateur.getEmail() == null || compteUtilisateur.getId() == null) {
			return null;
		}
		// 2. Trouver le Token d'authentification
		System.out.println("# Authentification de l'utilisateur ...");
		try {
			String accessToken = securiteUserService.generateJWTByUser(compteUtilisateur);
			if(accessToken != null) {
				// 3. Valider le Token et placer l'utilisateur dans le context !
				securiteUserService.placerUtilisateurDansLeContext(accessToken);

				// 4. Générer l'Id du Token permettant de raffraichir ce Token, déconnecter l'utilisateur ...
				String refreshToken = UUID.randomUUID().toString().replace("-", "");
				// 5. Pour une meilleur implémentation professionnelle, il faut sauvegarder les deux Tokens dans la BD
				// ...

				// 6. Renvoyer la reponse
				return new JWTResponse(accessToken, refreshToken, compteUtilisateur.getEmail(), compteUtilisateur.getNom(), Long.valueOf(72000 * 60));
			} 
			else {
				LOGGER.info("Problème d'Authentification de l'utilisateur !");
				return null;
			}
		} catch (Exception e) {
			LOGGER.error("Erreur d'Authentification de l'utilisateur : " + e.getMessage());
		}
		return null;
	}

	// TODO : A tester et s'assurrer, sinon mettre directement l'adresse du micro-service web SecuriteService
	private String getAppContextUrl(HttpServletRequest request) {
		return "http://" + request.getServerName() + ":" + request.getServerPort() /*+ request.getContextPath()*/ + "/api/covid/v1/comptes-utilisateurs";
	}
}
