package com.statistiquescovid.utilisateur.web.dto;


public class UserDTO {

	private String nom;

	private String password;

	private String confirmPassword;

	private String email;

	private String role;

	public UserDTO() {
		super();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	@Override
	public String toString() {
		return "UserDTO [nom=" + nom + ", password=" + password + ", confirmPassword="
				+ confirmPassword + ", email=" + email + ", role=" + role + "]";
	}

}
