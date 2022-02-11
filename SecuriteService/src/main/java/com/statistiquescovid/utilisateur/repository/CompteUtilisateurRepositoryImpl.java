package com.statistiquescovid.utilisateur.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.statistiquescovid.utilisateur.entites.CompteUtilisateur;

@Repository
public class CompteUtilisateurRepositoryImpl implements CompteUtilisateurRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public CompteUtilisateur login(String email, String encryptedPassword) {
		// TODO Auto-generated method stub
		String queryString = "SELECT u FROM CompteUtilisateur u WHERE u.email = :email AND u.password = :password";
		TypedQuery<CompteUtilisateur> query = entityManager.createQuery(queryString, CompteUtilisateur.class);
		query.setParameter("email", email);
		query.setParameter("password", encryptedPassword);
		CompteUtilisateur resultedUser = query.getSingleResult();

		return resultedUser;
	}

}
