package com.statistiquescovid.utilisateur.service;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.statistiquescovid.utilisateur.entites.PasswordResetToken;
import com.statistiquescovid.utilisateur.configs.FileStorageProperties;
import com.statistiquescovid.utilisateur.entites.CompteUtilisateur;
import com.statistiquescovid.utilisateur.entites.VerificationToken;
import com.statistiquescovid.utilisateur.excepion.UserAlreadyExistException;
import com.statistiquescovid.utilisateur.repository.PasswordResetTokenRepository;
import com.statistiquescovid.utilisateur.repository.CompteUtilisateurRepository;
import com.statistiquescovid.utilisateur.repository.VerificationTokenRepository;
import com.statistiquescovid.utilisateur.web.dto.UserDTO;

@Service
@Transactional
public class UtilisateurService implements IUtilisateurService {

	@Autowired
	private CompteUtilisateurRepository compteUtilisateurRepository;

	@Autowired
	private VerificationTokenRepository tokenRepository;

	@Autowired
	private PasswordResetTokenRepository passwordTokenRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public static final String ROLE_USER = "ROLE_USER";
	public static final String TOKEN_INVALID = "invalidToken";
	public static final String TOKEN_EXPIRED = "expired";
	public static final String TOKEN_VALID = "valid";
	public static final String PREFIX_PHOTOS = "photo_";
	private static final Logger logger = LoggerFactory.getLogger(UtilisateurService.class);

	private final Path repertoirePhotosProfils;

	@Autowired
	public UtilisateurService(FileStorageProperties fileStorageProperties) {
		this.repertoirePhotosProfils = Paths.get(fileStorageProperties.getPhotosRepertoire());
		try {
			Files.createDirectories(repertoirePhotosProfils);
		} catch (IOException e) {
			throw new RuntimeException("Impossible d'initialiser le repértoire racine pour les photos de profils utilisateurs ", e);
		}
	}

	public CompteUtilisateur login(String username, String noEncryptedPassword) {
		if(username == null || noEncryptedPassword == null) {
			return null;
		}
		// TODO: A optimiser
		String encryptedPassword = passwordEncoder.encode(noEncryptedPassword);
		System.err.println("Login = " + username + ", pass= " + encryptedPassword);

		CompteUtilisateur compteUtilisateur = compteUtilisateurRepository.findByEmail(username);
		if(compteUtilisateur == null) {
			System.err.println("*** CompteUtilisateur not found");
			return null;
		}
		if(!compteUtilisateur.isActif()) {
			System.err.println("*** CompteUtilisateur not active");
			return null;
		}
		
		if(!passwordEncoder.matches(noEncryptedPassword, compteUtilisateur.getPassword())) {
			System.err.println("*** CompteUtilisateur password don't match");
			return null;
		}
		System.err.println("CompteUtilisateur password = " + compteUtilisateur.getPassword());
		return compteUtilisateur;
	}

	@Override
	public CompteUtilisateur registerNewUserAccount(final UserDTO userDTO) {
		if(userDTO == null || userDTO.getEmail() == null) {
			return null;
		}

		if (compteUtilisateurRepository.existsByEmail(userDTO.getEmail())) {
			throw new UserAlreadyExistException("There is an account with that email adress: " + userDTO.getEmail());
		}
		CompteUtilisateur compteUtilisateur = new CompteUtilisateur();

		System.err.println("NEW USERDTO = " + userDTO);

		compteUtilisateur.setNom(userDTO.getNom());
		compteUtilisateur.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		compteUtilisateur.setEmail(userDTO.getEmail());
		compteUtilisateur.setRole(ROLE_USER);
		return compteUtilisateurRepository.save(compteUtilisateur);
	}

	@Override
	public CompteUtilisateur update(String email, UserDTO userDTO) {
		CompteUtilisateur compteUtilisateur = this.findUserByEmail(email);
		if (compteUtilisateur == null) {
			System.err.println("*** CompteUtilisateur not found");
			return null;
		}

		compteUtilisateur.setNom(userDTO.getNom());
		return compteUtilisateurRepository.save(compteUtilisateur);
	}

	@Override
	public CompteUtilisateur getUser(final String verificationToken) {
		final VerificationToken token = tokenRepository.findByToken(verificationToken);
		if (token != null) {
			return token.getCompteUtilisateur();
		}
		return null;
	}

	@Override
	public VerificationToken getVerificationToken(final String VerificationToken) {
		return tokenRepository.findByToken(VerificationToken);
	}

	@Override
	public void saveRegisteredUser(final CompteUtilisateur compteUtilisateur) {
		compteUtilisateurRepository.save(compteUtilisateur);
	}

	@Override
	public void deleteUser(final CompteUtilisateur compteUtilisateur) {
		final VerificationToken verificationToken = tokenRepository.findByCompteUtilisateur(compteUtilisateur);

		if (verificationToken != null) {
			tokenRepository.delete(verificationToken);
		}

		final PasswordResetToken passwordToken = passwordTokenRepository.findByCompteUtilisateur(compteUtilisateur);

		if (passwordToken != null) {
			passwordTokenRepository.delete(passwordToken);
		}

		compteUtilisateurRepository.delete(compteUtilisateur);
	}

	@Override
	public void createVerificationTokenForUser(final CompteUtilisateur compteUtilisateur, final String token) {
		final VerificationToken myToken = new VerificationToken(token, compteUtilisateur);
		tokenRepository.save(myToken);
	}

	@Override
	public VerificationToken generateNewVerificationToken(final String existingVerificationToken) {
		VerificationToken vToken = tokenRepository.findByToken(existingVerificationToken);
		vToken.updateToken(UUID.randomUUID()
				.toString());
		vToken = tokenRepository.save(vToken);
		return vToken;
	}

	@Override
	public void createPasswordResetTokenForUser(final CompteUtilisateur compteUtilisateur, final String token) {
		final PasswordResetToken myToken = new PasswordResetToken(token, compteUtilisateur);
		passwordTokenRepository.save(myToken);
	}

	@Override
	public CompteUtilisateur findUserByEmail(final String email) {
		return compteUtilisateurRepository.findByEmail(email);
	}

	@Override
	public PasswordResetToken getPasswordResetToken(final String token) {
		return passwordTokenRepository.findByToken(token);
	}

	@Override
	public CompteUtilisateur getUserByPasswordResetToken(final String token) {
		return passwordTokenRepository.findByToken(token).getCompteUtilisateur();
	}

	@Override
	public CompteUtilisateur getUserByID(final Long id) {
		Optional<CompteUtilisateur> optCompte = compteUtilisateurRepository.findById(id);
		if(optCompte.isPresent()) {
			return optCompte.get();
		}

		return null;
	}

	@Override
	public boolean changeUserPassword(final CompteUtilisateur compteUtilisateur, final String password) {
		compteUtilisateur.setPassword(passwordEncoder.encode(password));
		return compteUtilisateurRepository.save(compteUtilisateur) != null;
	}

	@Override
	public boolean checkIfValidOldPassword(final CompteUtilisateur compteUtilisateur, final String oldPassword) {
		return passwordEncoder.matches(oldPassword, compteUtilisateur.getPassword());
	}

	@Override
	public String validateVerificationToken(String token) {
		final VerificationToken verificationToken = tokenRepository.findByToken(token);
		if (verificationToken == null) {
			return TOKEN_INVALID;
		}

		final CompteUtilisateur compteUtilisateur = verificationToken.getCompteUtilisateur();
		final Calendar cal = Calendar.getInstance();
		if ((verificationToken.getExpiryDate()
				.getTime()
				- cal.getTime()
				.getTime()) <= 0) {
			tokenRepository.delete(verificationToken);
			return TOKEN_EXPIRED;
		}

		compteUtilisateur.setActif(true);
		// tokenRepository.delete(verificationToken);
		compteUtilisateurRepository.save(compteUtilisateur);
		return TOKEN_VALID;
	}

	@Override
	public String storePhoto(String utilisateurId, MultipartFile file) {
		if(file == null || file.getOriginalFilename() == null)
			return null;

		Path pathNouveauLogo = null;
		// Normalize file name
		String filename = StringUtils.cleanPath(file.getOriginalFilename());
		String extension = filename.substring(filename.lastIndexOf("."));
		String newFilename = PREFIX_PHOTOS + utilisateurId + extension.toLowerCase();
		logger.info("# enregistrement du fichier d'un logo pour la societé ["+ newFilename +"] ...");
		System.out.println("# enregistrement du fichier d'un logo pour la societé ["+ newFilename +"] ...");
		try {
			if (file.isEmpty()) {
				logger.error("Erreur d'enregistrement d'un fichier vide : ", filename);
				return null;
			}
			// Copy file to the target location (Replacing existing file with the same name)
			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, this.repertoirePhotosProfils.resolve(newFilename), StandardCopyOption.REPLACE_EXISTING);
				pathNouveauLogo = this.repertoirePhotosProfils.resolve(newFilename);
			}
		}
		catch (IOException e) {
			logger.error("Erreur d'enregistrement du fichier logo de la Société : ", e.getMessage());
			return null;
		}

		// Si le fichier de la photo de l'utilisateur est réellement enregistré
		// Alors mettre à jour les infos de la photo de profil
		if(pathNouveauLogo != null) {
			// Mettre à jour toutes les infos : Taille de la photo, extension, ... bien que dans cette version on s'est limiter au nom du fichier !
			CompteUtilisateur compteUtilisateur = this.compteUtilisateurRepository.findByEmail(utilisateurId);
			if (compteUtilisateur != null) {
				compteUtilisateur.setNomFichierAvatar(newFilename);
				this.compteUtilisateurRepository.save(compteUtilisateur);
			}
		}

		return newFilename;
	}

	@Override
	public Resource loadPhotoResource(String filename, String utilisateurId) {
		// Si ni le nom du fichier ni l'Id de l'utisateur n'a été précisé alors renvoyer NULL
		if(filename == null && utilisateurId == null) {
			logger.warn("# Erreur : pour recupérer la photo, il faut au moins l'une des infos utilisateurId ou nomFichier !");
			return null;
		}

		try {
			Path filePath = loadPathFichier(filename, utilisateurId);
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			}
			else {
				throw new RuntimeException("Impossible de lire le fichier : " + filename);
			}
		}
		catch (MalformedURLException e) {
			throw new RuntimeException("Impossible de lire le fichier : " + filename, e);
		}
	}

	private Path loadPathFichier(String filename, String utilisateurId) {
		if ((filename == null || filename.trim().isEmpty()) && utilisateurId != null && !utilisateurId.trim().isEmpty()) {
			logger.warn("# Nom de la photo non spécifié --> recherche du logo par id de l'utilisateur " + utilisateurId);
			String motifLogo = PREFIX_PHOTOS + utilisateurId;

			File dir = repertoirePhotosProfils.toFile();
			File[] logosTrouves = dir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.startsWith(motifLogo) || name.contains(utilisateurId);
				}
			});

			logger.warn("# " + logosTrouves.length + " photo(s) trouvée(s) dont le nom contient l'id utilisateur " + utilisateurId);
			if(logosTrouves.length > 0) {
				filename = logosTrouves[0].getName();
				logger.warn("# photo choisie est '" + filename + "'.");
			}			
		}

		return repertoirePhotosProfils.resolve(filename);
	}

}
