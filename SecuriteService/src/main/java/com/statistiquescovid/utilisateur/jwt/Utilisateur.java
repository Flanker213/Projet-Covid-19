package com.statistiquescovid.utilisateur.jwt;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.io.Serializable;
import java.util.Collection;

public class Utilisateur extends User implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String id;
	private String email;
	private String nom;
	private Boolean actif;

	public Utilisateur(String id, String password, Collection<GrantedAuthority> grantedAuthorities) {
		super(id, password, grantedAuthorities);
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Boolean getActif() {
		return actif;
	}

	public void setActif(Boolean actif) {
		this.actif = actif;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "Utilisateur [id=" + id + ", email=" + email + ", nom=" + nom
				+ ", actif=" + actif + "]";
	}

}
