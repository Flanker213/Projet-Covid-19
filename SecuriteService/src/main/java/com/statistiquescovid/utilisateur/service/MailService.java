package com.statistiquescovid.utilisateur.service;

import com.statistiquescovid.utilisateur.entites.CompteUtilisateur;

public interface MailService {

	public boolean envoyerMailConfirmationInscription(CompteUtilisateur compteUtilisateur, String appContextPath, String tokenActivation);
	
	public boolean envoyerMailReinitialisationMotDePasse(CompteUtilisateur compteUtilisateur, String appContextPath, String tokenResetPassword);
	
}
