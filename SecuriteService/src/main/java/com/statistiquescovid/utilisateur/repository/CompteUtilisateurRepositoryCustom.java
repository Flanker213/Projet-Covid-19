package com.statistiquescovid.utilisateur.repository;

import com.statistiquescovid.utilisateur.entites.CompteUtilisateur;

public interface CompteUtilisateurRepositoryCustom {

    public CompteUtilisateur login(String username, String hashPassword);
    
}
