package com.statistiquescovid.utilisateur.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "fichier")
public class FileStorageProperties {
	
    private String photosRepertoire;
    private String autresRepertoire;
    
	public String getPhotosRepertoire() {
		return photosRepertoire;
	}
	public void setPhotosRepertoire(String photosRepertoire) {
		this.photosRepertoire = photosRepertoire;
	}
	public String getAutresRepertoire() {
		return autresRepertoire;
	}
	public void setAutresRepertoire(String autresRepertoire) {
		this.autresRepertoire = autresRepertoire;
	}

}
