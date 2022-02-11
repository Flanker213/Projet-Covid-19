package com.statistiquescovid.utilisateur;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.ApplicationContext;

import com.statistiquescovid.utilisateur.configs.EmailNotificationsProperties;
import com.statistiquescovid.utilisateur.configs.FileStorageProperties;
import com.statistiquescovid.utilisateur.configs.WebServicesNames;

@SpringBootApplication
@EnableConfigurationProperties({
	FileStorageProperties.class,
	EmailNotificationsProperties.class,
	WebServicesNames.class
})
public class SecuriteServiceApplication {

	public static void main(String[] args) {
		/*ApplicationContext applicationContex = */SpringApplication.run(SecuriteServiceApplication.class, args);		
		System.err.println("******* Service Securité demarré !");
	}

}
