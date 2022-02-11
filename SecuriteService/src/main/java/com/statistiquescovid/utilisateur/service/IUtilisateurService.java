package com.statistiquescovid.utilisateur.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.statistiquescovid.utilisateur.entites.PasswordResetToken;
import com.statistiquescovid.utilisateur.entites.CompteUtilisateur;
import com.statistiquescovid.utilisateur.entites.VerificationToken;
import com.statistiquescovid.utilisateur.excepion.UserAlreadyExistException;
import com.statistiquescovid.utilisateur.web.dto.UserDTO;

public interface IUtilisateurService {

	public CompteUtilisateur registerNewUserAccount(UserDTO accountDto) throws UserAlreadyExistException;

	public CompteUtilisateur getUser(String verificationToken);

	public void saveRegisteredUser(CompteUtilisateur compteUtilisateur);

	public CompteUtilisateur update(String email, UserDTO userDTO);

	public void deleteUser(CompteUtilisateur compteUtilisateur);

	public void createVerificationTokenForUser(CompteUtilisateur compteUtilisateur, String token);

	public VerificationToken getVerificationToken(String VerificationToken);

	public VerificationToken generateNewVerificationToken(String token);

	public void createPasswordResetTokenForUser(CompteUtilisateur compteUtilisateur, String token);

	public CompteUtilisateur findUserByEmail(String email);

	public PasswordResetToken getPasswordResetToken(String token);

	public CompteUtilisateur getUserByPasswordResetToken(String token);

	public CompteUtilisateur getUserByID(Long id);

	public boolean changeUserPassword(CompteUtilisateur compteUtilisateur, String password);

	public boolean checkIfValidOldPassword(CompteUtilisateur compteUtilisateur, String password);

	public String validateVerificationToken(String token);

	public CompteUtilisateur login(String username, String noEncryptedPassword);

	public String storePhoto(String utilisateurId, MultipartFile file);
	public Resource loadPhotoResource(String filename, String userId);

}
