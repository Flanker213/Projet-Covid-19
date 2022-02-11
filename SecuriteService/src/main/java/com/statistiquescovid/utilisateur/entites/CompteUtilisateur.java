package com.statistiquescovid.utilisateur.entites;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import javax.persistence.Id;

@Entity
public class CompteUtilisateur {

	@Id
	@Column(name = "compte_utilisateur_id", unique = true, nullable = false)

    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String nom;
	private String email;

	private String password;
	private boolean actif;

	private String role;
	private String nomFichierAvatar;

	public CompteUtilisateur() {
		super();
		this.actif = false;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isActif() {
		return actif;
	}
	public void setActif(boolean actif) {
		this.actif = actif;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getNomFichierAvatar() {
		return nomFichierAvatar;
	}
	public void setNomFichierAvatar(String nomFichierAvatar) {
		this.nomFichierAvatar = nomFichierAvatar;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((email == null) ? 0 : email.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final CompteUtilisateur compteUtilisateur = (CompteUtilisateur) obj;
		if (!email.equals(compteUtilisateur.email)) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return "CompteUtilisateur [id=" + id + ", nom=" + nom + ", email=" + email + ", password=" + password
				+ ", actif=" + actif + ", role=" + role + ", nomFichierAvatar=" + nomFichierAvatar + "]";
	}

}