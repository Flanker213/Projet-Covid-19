package com.statistiquescovid.utilisateur.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;

import com.statistiquescovid.utilisateur.entites.CompteUtilisateur;

public interface CompteUtilisateurRepository extends JpaRepository<CompteUtilisateur, Long>, CompteUtilisateurRepositoryCustom {
	
    public CompteUtilisateur findByEmail(String email);
    
    public boolean existsByEmail(String email);

    public Collection<CompteUtilisateur> findAllByRole(String role);
    
    @Override
    public void delete(CompteUtilisateur compteUtilisateur);

}
