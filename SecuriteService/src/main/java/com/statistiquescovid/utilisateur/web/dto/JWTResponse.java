package com.statistiquescovid.utilisateur.web.dto;

public class JWTResponse {
	private String access_token;
	private String refresh_token;
	private String type = "Bearer";
	private Long expire;
	private String email;
	private String nom;

	public JWTResponse(String access_token, String refresh_token, String email, String nom, Long expire) {
		this.email = email;
		this.nom = nom;
		this.access_token = access_token;
		this.refresh_token = refresh_token;
		this.expire = expire;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getRefresh_token() {
		return refresh_token;
	}

	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public Long getExpire() {
		return expire;
	}

	public void setExpire(Long expire) {
		this.expire = expire;
	}

}
