package com.statistiquescovid.utilisateur.web;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.statistiquescovid.utilisateur.entites.CompteUtilisateur;
import com.statistiquescovid.utilisateur.jwt.Utilisateur;
import com.statistiquescovid.utilisateur.service.IUtilisateurService;
import com.statistiquescovid.utilisateur.service.ISecurityUserService;
import com.statistiquescovid.utilisateur.web.dto.UploadFileResponse;
import com.statistiquescovid.utilisateur.web.dto.UserDTO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("api/covid/v1/comptes-utilisateurs/preferences")
@Api(value = "Gestion des utilisateurs et préférecences utilisateurs")
public class UserAccountController {

	@Autowired
	private IUtilisateurService utilisateurService;
	@Autowired
	private ISecurityUserService securiteUserService;
	private final Logger logger = LoggerFactory.getLogger(UserAccountController.class);

	@GetMapping("mon-profil")
	public ResponseEntity<CompteUtilisateur> getProfile() {
		// 1. S'assurer que l'utilisateur soit authentifié
		Utilisateur utilisateurCourant = this.securiteUserService.getUtilisateurCourant();
		if (utilisateurCourant == null || utilisateurCourant.getEmail() == null) {
			logger.error("******* Utilisateur non authentifié");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		// 2. Retrouver l'utilisateur authentifié
		return ResponseEntity.ok(utilisateurService.findUserByEmail(utilisateurCourant.getEmail()));
	}

	@PutMapping("mon-profil")
	public ResponseEntity<CompteUtilisateur> update(@RequestBody UserDTO userDTO) {
		System.err.println("******** Mise à jour du profil utilisateur ... " + userDTO);
		// 1. S'assurer que l'utilisateur soit authentifié
		Utilisateur utilisateurCourant = this.securiteUserService.getUtilisateurCourant();
		if (utilisateurCourant == null) {
			logger.error("******* Utilisateur non authentifié");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		// 2. S'assurer qu'il a préciser un nom à mettre à jour !
		if(userDTO == null || userDTO.getNom() == null || userDTO.getNom().isEmpty()) {
			logger.error("******* BAD REQUEST ! le nom est requis !");
			return ResponseEntity.badRequest().build();
		}

		logger.info("******* Mis à jour du profil utilisateur ...");
		// 3. Procéder à la mise à jour du profil de l'utilisateur
		return ResponseEntity.ok(utilisateurService.update(utilisateurCourant.getEmail(), userDTO));
	}

	@ApiOperation(value = "Charger un fichier JPG ou PNG correspondant à la photo de profil d'un utilisateur")
	@PostMapping({"televerser-photo"})
	public ResponseEntity<UploadFileResponse> uploadLogoFile(
			@ApiParam(required = true, name = "file", value = "La photo (fichier PNG, JPEG ou JPG)") 
			@RequestParam("file") MultipartFile file) {
		// 1. S'assurer que l'utilisateur soit authentifié
		Utilisateur utilisateurCourant = this.securiteUserService.getUtilisateurCourant();
		if (utilisateurCourant == null) {
			logger.error("******* Utilisateur non authentifié");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		System.out.println("# Uploading 'logo' file...");
		String contentType = file.getContentType();
		if(contentType == null || (!contentType.equalsIgnoreCase("image/jpeg") && !contentType.equalsIgnoreCase("image/png"))) {
			System.out.println("# Le type de fichier n'est pas valide ! (attendu : JPEG, JPG, PNG");
			return new ResponseEntity<UploadFileResponse>(HttpStatus.BAD_REQUEST);
		}

		String fileName = utilisateurService.storePhoto(utilisateurCourant.getId(), file);
		if(fileName == null)
			return new ResponseEntity<UploadFileResponse>(HttpStatus.BAD_REQUEST);

		String extension = fileName.substring(fileName.lastIndexOf("."));
		String fileDownloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/api/covid/v1/comptes-utilisateurs/preferences/telecharger-photo/")
				.path(fileName)
				.toUriString();
		System.out.println("# File uploaded :: URI = " + fileName);
		Date dateCreation = new Date();

		UploadFileResponse repFile = new UploadFileResponse(fileName, fileDownloadUrl, file.getContentType(), file.getSize(), extension, file.getOriginalFilename(), dateCreation);
		return new ResponseEntity<UploadFileResponse>(repFile, HttpStatus.OK);
	}

	@ApiOperation(value = "Télécharger la photo d'utilisateur authentifié")
	@GetMapping({"telecharger-photo/{fileName:.+}", "telecharger-photo"})
	public ResponseEntity<Resource> downloadFactureFournisseurFile(
			@ApiParam(required = false, name = "fileName", value = "Le nom du logo (non requis si l'utilisateur est authentifié)") 
			@PathVariable(name = "fileName", required = false) String fileName) {
		String utilisateurId = null;
		// 1. Vérifier si l'utilisateur soit authentifié
		Utilisateur utilisateurCourant = this.securiteUserService.getUtilisateurCourant();
		if (utilisateurCourant != null) {
			logger.error("******* Utilisateur non authentifié");
			utilisateurId = utilisateurCourant.getId();
			//return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		// Load file as Resource
		Resource resource = utilisateurService.loadPhotoResource(fileName, utilisateurId);

		// File's content type
		String contentType = "application/octet-stream";
		try {
			contentType = Files.probeContentType(resource.getFile().toPath());
		} catch (IOException ex) {
			logger.info("Impossible de determiner le type du fichier.");
		}

		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

	// TODO: Enregistrer: Ajouter/Modifier les préférences de l'utilisateur

}
