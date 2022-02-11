package com.statistiquescovid.utilisateur.web.dto;

import java.io.Serializable;
import java.util.Date;

public class UploadFileResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String nomFichier;
    private String url;
    private String mimeType;
    private long tailleEnOctet;
    private String extension;
    private String nomOriginal;
	private Date dateCreation;
    
    public UploadFileResponse() {
    	super();
    }
	public UploadFileResponse(String nomFichier, String url, String mimeType,
			long tailleEnOctet, String extension, String nomOriginal, Date dateCreation) {
		super();
		this.nomFichier = nomFichier;
		this.url = url;
		this.mimeType = mimeType;
		this.tailleEnOctet = tailleEnOctet;
		this.extension = extension;
		this.nomOriginal = nomOriginal;
		this.dateCreation = dateCreation;
	}
	public String getNomFichier() {
		return nomFichier;
	}
	public void setNomFichier(String nomFichier) {
		this.nomFichier = nomFichier;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	public long getTailleEnOctet() {
		return tailleEnOctet;
	}
	public void setTailleEnOctet(long tailleEnOctet) {
		this.tailleEnOctet = tailleEnOctet;
	}
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	public String getNomOriginal() {
		return nomOriginal;
	}
	public void setNomOriginal(String nomOriginal) {
		this.nomOriginal = nomOriginal;
	}
	public Date getDateCreation() {
		return dateCreation;
	}
	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}
    
}
