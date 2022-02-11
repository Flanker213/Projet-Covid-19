package com.statistiquescovid.utilisateur.service;

import java.util.Locale;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.statistiquescovid.utilisateur.entites.CompteUtilisateur;

@Service
public class MailServiceImpl implements MailService {

	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private Environment environment;
	final Locale locale = Locale.FRANCE;
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Override
	public boolean envoyerMailConfirmationInscription(CompteUtilisateur compteUtilisateur, String appContextPath,
			String tokenActivation) {
		// TODO Auto-generated method stub
		// Envoie de mail Html
		try {
			mailSender.send(constructHTMLWelcomeEmail(appContextPath, tokenActivation, compteUtilisateur));
			LOGGER.info("Email envoyé à l'adresse ", compteUtilisateur.getEmail());
			return true;
		} catch (MailException | MessagingException e) {
			LOGGER.error("Error while sending mail ", e);
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean envoyerMailReinitialisationMotDePasse(CompteUtilisateur compteUtilisateur, String appContextPath,
			String tokenResetPassword) {
		// TODO Auto-generated method stub
		if(environment.getProperty("support.email.html") != null && environment.getProperty("support.email.html") == "true") {
			// Envoie de mail Html
			try {
				mailSender.send(constructHTMLResetTokenEmail(appContextPath, tokenResetPassword, compteUtilisateur));
				LOGGER.info("Email envoyé à l'adresse ", compteUtilisateur.getEmail());
				return true;
			} catch (MailException | MessagingException e) {
				LOGGER.error("Error while sending mail ", e);
				e.printStackTrace();
				return false;
			}
		} 
		else {
			// Envoie de mail simple
			try {
				mailSender.send(constructTEXTResetTokenEmail(appContextPath, tokenResetPassword, compteUtilisateur));
				LOGGER.info("Email envoyé à l'adresse ", compteUtilisateur.getEmail());
				return true;
			} catch (MailException e) {
				LOGGER.error("Error while sending mail ", e);
				e.printStackTrace();
				return false;
			}
		}
	}

	private SimpleMailMessage constructTEXTResetTokenEmail(final String contextPath, final String token, final CompteUtilisateur compteUtilisateur) {
		final String url = contextPath + "/changer-mot-de-passe/ " + compteUtilisateur.getId() + "/" + token + "?userId=" + compteUtilisateur.getId() + "&token=" + token;
		// Former le sujet et le contenu du message
		final String subject = "Réinitialiser le mot de passe COVID 19 - FR";
		final String message = TextMessageResetPassword.getEmailContent(url, compteUtilisateur);

		final SimpleMailMessage emailMessage = new SimpleMailMessage();
		emailMessage.setSubject(subject);
		emailMessage.setText(message);
		emailMessage.setTo(compteUtilisateur.getEmail());
		emailMessage.setFrom(environment.getProperty("support.email"));
		emailMessage.setReplyTo(environment.getProperty("support.email"));
		return emailMessage;
	}

	private MimeMessage constructHTMLResetTokenEmail(final String contextPath, 
			final String token, 
			final CompteUtilisateur compteUtilisateur) throws AddressException, MessagingException {
		final String urlReinitialisationPassword = contextPath + "/changer-mot-de-passe/ " + compteUtilisateur.getId() + "/" + token + "?userId=" + compteUtilisateur.getId() + "&token=" + token;
		// sets SMTP server properties
		Properties properties = new Properties();
		properties.put("mail.smtp.host", environment.getProperty("spring.mail.host"));
		properties.put("mail.smtp.port", environment.getProperty("spring.mail.port"));
		properties.put("mail.smtp.auth", environment.getProperty("spring.mail.properties.mail.smtps.auth"));
		properties.put("mail.smtp.starttls.enable", environment.getProperty("spring.mail.properties.mail.smtps.starttls.enable"));

		// creates a new session with an authenticator
		Authenticator auth = new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(environment.getProperty("spring.mail.username"), environment.getProperty("spring.mail.password"));
			}
		};
		Session session = Session.getInstance(properties, auth);
		// creates a new e-mail message
		MimeMessage emailMessage = new MimeMessage(session);

		emailMessage.setFrom(new InternetAddress(environment.getProperty("spring.mail.username")));
		emailMessage.setReplyTo(new InternetAddress[] {new InternetAddress(environment.getProperty("support.email"))});
		InternetAddress[] toAddresses = { new InternetAddress(compteUtilisateur.getEmail()) };
		emailMessage.setRecipients(Message.RecipientType.TO, toAddresses);
		// Former le sujet et le contenu du message
		final String subject = "Réinitialiser le mot de passe COVID 19 - FR";
		final String clientAngularBaseUrl = environment.getProperty("client.adress-base");
		final String message = HTMLMessageResetPassword.getEmailContent(urlReinitialisationPassword, compteUtilisateur, clientAngularBaseUrl);
		emailMessage.setSubject(subject);
		emailMessage.setContent(message, "text/html");
		return emailMessage;
	}

	private MimeMessage constructHTMLWelcomeEmail(final String contextPath, final String verificationToken, final CompteUtilisateur compteUtilisateur) throws AddressException, MessagingException {
		String activationUrl = contextPath + "/confirmer-inscription?token=" + verificationToken;
		// sets SMTP server properties
		Properties properties = new Properties();
		properties.put("mail.smtp.host", environment.getProperty("spring.mail.host"));
		properties.put("mail.smtp.port", environment.getProperty("spring.mail.port"));
		properties.put("mail.smtp.auth", environment.getProperty("spring.mail.properties.mail.smtps.auth"));
		properties.put("mail.smtp.starttls.enable", environment.getProperty("spring.mail.properties.mail.smtps.starttls.enable"));

		// creates a new session with an authenticator
		Authenticator auth = new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(environment.getProperty("spring.mail.username"), environment.getProperty("spring.mail.password"));
			}
		};
		Session session = Session.getInstance(properties, auth);
		// creates a new e-mail message
		MimeMessage emailMessage = new MimeMessage(session);

		emailMessage.setFrom(new InternetAddress(environment.getProperty("spring.mail.username")));
		emailMessage.setReplyTo(new InternetAddress[] {new InternetAddress(environment.getProperty("support.email"))});
		InternetAddress[] toAddresses = { new InternetAddress(compteUtilisateur.getEmail()) };
		emailMessage.setRecipients(Message.RecipientType.TO, toAddresses);
		//emailMessage.setSentDate(new Date());
		// Former le sujet et le contenu du message
		final String subject = "Bienvenue chez Covid 19 - FR";
		final String nomCompletRealisateurProjet = environment.getProperty("nom-complet-realisateur-projet");
		final String message = HTMLMessageWelcomeUser.getEmailContent(activationUrl, compteUtilisateur, nomCompletRealisateurProjet);
		emailMessage.setSubject(subject);
		emailMessage.setContent(message, "text/html");
		// On peut envoyer directement le mail avec Transport à la place de JavaMailSender
		//javax.mail.Transport.send(message);
		return emailMessage;
	}

}
