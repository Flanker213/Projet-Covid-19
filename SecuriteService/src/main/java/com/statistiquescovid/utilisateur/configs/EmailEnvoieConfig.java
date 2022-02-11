package com.statistiquescovid.utilisateur.configs;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@PropertySource("classpath:mail.properties")
public class EmailEnvoieConfig {

	@Value("${mail.protocol:smtp}")
	private String protocol;
	@Value("${mail.host:smtp.gmail.com}")
	private String host; // par défaut "smtp.gmail.com"
	@Value("${mail.port:#{587}}")
	private int port;
	@Value("${mail.smtp.auth:true}")
	private boolean auth; // true par défaut
	@Value("${mail.smtp.starttls.enable:true}")
	private boolean starttls; // true par défaut
	@Value("${mail.from}")
	private String from;
	@Value("${mail.username}")
	private String username;
	@Value("${mail.password}")
	private String password;
	@Value("${mail.debug}")
	private boolean debug;

	@Bean
	public JavaMailSender javaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

		// Properties mailProperties = mailSender.getJavaMailProperties();
		Properties mailProperties = new Properties();
		mailProperties.put("mail.smtp.auth", auth);
		mailProperties.put("mail.smtp.starttls.enable", starttls);
		mailProperties.put("mail.transport.protocol", protocol);
		mailProperties.put("mail.debug", debug);
		mailSender.setJavaMailProperties(mailProperties);

		mailSender.setHost(host);
		mailSender.setPort(port);
		mailSender.setProtocol(protocol);
		mailSender.setUsername(username);
		mailSender.setPassword(password);

		return mailSender;
	}
}
