package com.statistiquescovid.utilisateur.configs;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "emails-notifications")
public class EmailNotificationsProperties {
	
	private List<String> adressesEmailsANotifier;

	public List<String> getAdressesEmailsANotifier() {
		return adressesEmailsANotifier;
	}
	public void setAdressesEmailsANotifier(List<String> adressesEmailsANotifier) {
		this.adressesEmailsANotifier = adressesEmailsANotifier;
	}

}
