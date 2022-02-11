package com.statistiquescovid.utilisateur.service;

import com.statistiquescovid.utilisateur.entites.CompteUtilisateur;

public class TextMessageResetPassword {

	public static String getEmailContent(String url, CompteUtilisateur compteUtilisateur) {
		StringBuilder sb = new StringBuilder();
		sb.append("Bonjour " + compteUtilisateur.getNom() + ", \n");
		sb.append("Quelqu'un a demandé un nouveau mot de passe pour votre compte Covid-FR \r\n" 
				+ "Aucun changement n'est encore effectué à votre compte. Si vous êtes à l'origine de cette opération, "
				+ "veuillez cliquer le lien ci-dessous pour réinitialiser le mot de passe de votre compte client.\r\n");
		sb.append(url + "\r\n");
		sb.append("Si vous n'avez pas demandé de nouveau mot de passe, vous pouvez ignorer cet email en toute sécurité. Cette réitinialisation sera valide seulement dans les prochaines 30 minutes.\r\n");
		
		return sb.toString();
	}
}
