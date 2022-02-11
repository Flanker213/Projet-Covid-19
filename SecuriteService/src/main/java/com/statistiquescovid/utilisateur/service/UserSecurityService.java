package com.statistiquescovid.utilisateur.service;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.statistiquescovid.utilisateur.entites.PasswordResetToken;
import com.statistiquescovid.utilisateur.entites.CompteUtilisateur;
import com.statistiquescovid.utilisateur.jwt.JwtProvider;
import com.statistiquescovid.utilisateur.jwt.Utilisateur;
import com.statistiquescovid.utilisateur.repository.PasswordResetTokenRepository;

@Service
@Transactional
public class UserSecurityService implements ISecurityUserService {

	@Autowired
	private JwtProvider jwtProvider;
	@Autowired
	private UtilisateurService utilisateurService;
	@Autowired
	private PasswordResetTokenRepository passwordTokenRepository;
	private static final Logger LOGGER = LoggerFactory.getLogger(UserSecurityService.class);

	@Override
	public String validatePasswordResetToken(String userId, String token) {
		final PasswordResetToken passToken = passwordTokenRepository.findByToken(token);
		if ((passToken == null) || (!passToken.getCompteUtilisateur().getId().equals(userId))) {
			return "invalidToken";
		}

		final Calendar cal = Calendar.getInstance();
		if ((passToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
			return "expired";
		}

		final CompteUtilisateur compteUtilisateur = passToken.getCompteUtilisateur();
		final Authentication auth = new UsernamePasswordAuthenticationToken(compteUtilisateur, null, Arrays.asList(new SimpleGrantedAuthority("CHANGE_PASSWORD_PRIVILEGE")));
		SecurityContextHolder.getContext().setAuthentication(auth);
		return null;
	}

	@Override
	public void deletePasswordResetTokenByTokenValue(String token) {
		PasswordResetToken pswdTok = passwordTokenRepository.findByToken(token);
		if(pswdTok != null) {
			passwordTokenRepository.delete(pswdTok);
		}
	}

	@Override
	public void deletePasswordResetTokenExperedSinceNow() {
		final Calendar cal = Calendar.getInstance();
		passwordTokenRepository.deleteAllExpiredSince(new Date(cal.getTime().getTime()));
	}

	@Override
	public Utilisateur getUtilisateurCourant() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Object principal = auth != null && auth.isAuthenticated() ? auth.getPrincipal() : null;
		Utilisateur utilisateur = principal != null && (principal instanceof Utilisateur) ? (Utilisateur) principal : null; 
		// Pour plus de sécurité et optimisation, on s'assure que l'utilisateur authentifié dispose d'une adresse e-mail !
		if (utilisateur == null || utilisateur.getEmail() == null || utilisateur.getEmail().trim().isEmpty()) {
			return null;
		}
		return utilisateur;
	}

	@Override
	public void authentifierUtilisateurDansLeContext(String login, String password) {
		// 1. Chercher l'utilisateur
		CompteUtilisateur compteUtilisateur = utilisateurService.login(login, password);
		if(compteUtilisateur == null) {
			return;
		}
		// 2. Trouver le Token d'authentification
		System.out.println("# Authentification de l'utilisateur ...");
		try {
			String accessToken = jwtProvider.generateJWTByUser(compteUtilisateur);
			if(accessToken != null) {
				// 3. Valider le Token et placer l'utilisateur dans le context !
				this.placerUtilisateurDansLeContext(accessToken);
			} else {
				LOGGER.info("Problème d'Authentification de l'utilisateur TECH !");
			}
		} catch (Exception e) {
			LOGGER.error("Erreur d'Authentification de l'utilisateur TECH : " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void placerUtilisateurDansLeContext(String accessToken) {
		System.err.println("# Utilisateur authentifié dans le context - Access_Token = " + accessToken);
		// 2. Valider l'en-tête et vérifier le préfixe
		if (StringUtils.hasText(accessToken)) {
			// 3. Obtenir Token
			String token = accessToken;
			if(accessToken.startsWith(jwtProvider.getPrefix())) {
				token = accessToken.replace(jwtProvider.getPrefix(), "").trim();
			}
			System.err.println("## TOKEN EXTRAIT = [" + token + "]");
			if(jwtProvider.validateToken(token)) {
				System.err.println("# Utilisateur technique authentifié - Access_Token VALIDE !");
				Authentication authentication = jwtProvider.getAuthentication(token);
				if(authentication != null) {
					SecurityContextHolder.getContext().setAuthentication(authentication);
					LOGGER.info("Authentification de l'utilisateur sur le contexte de sécurité pour '{}'", authentication.getName());
				} 
				else {
					// En cas de défaillance. S'assurer que le Context est nettoyé afin de garantir que l'utilisateur ne sera pas authentifié
					SecurityContextHolder.clearContext();
					LOGGER.info("Erreur d'Authentification de l'utilisateur TECH !");
				}
			}
			else {
				System.err.println("# Utilisateur authentifié - Access_Token NON VALIDE !");
				// En cas de défaillance. S'assurer que le Context est nettoyé afin de garantir que l'utilisateur ne sera pas authentifié
				SecurityContextHolder.clearContext();
				LOGGER.info("Token JWT de l'utilisateur non valide {}", token);
			}
		} 
		else {// En cas de défaillance. S'assurer que le Context est nettoyé afin de garantir que l'utilisateur ne sera pas authentifié
			SecurityContextHolder.clearContext();
			LOGGER.info("Echec d'Authentification de l'utilisateur TECH !!");
		}
	}

	@Override
	public String generateJWTByUser(CompteUtilisateur compteUtilisateur) {
		// TODO Auto-generated method stub
		return jwtProvider.generateJWTByUser(compteUtilisateur);
	}

}
