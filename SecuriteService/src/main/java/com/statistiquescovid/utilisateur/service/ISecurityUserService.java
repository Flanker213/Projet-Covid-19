package com.statistiquescovid.utilisateur.service;

import com.statistiquescovid.utilisateur.entites.CompteUtilisateur;
import com.statistiquescovid.utilisateur.jwt.Utilisateur;

public interface ISecurityUserService {

    public String validatePasswordResetToken(String userId, String token);
    public void deletePasswordResetTokenByTokenValue(String token);
    public void deletePasswordResetTokenExperedSinceNow();
    
    public String generateJWTByUser(CompteUtilisateur compteUtilisateur);
    public void placerUtilisateurDansLeContext(String accessToken);
    public void authentifierUtilisateurDansLeContext(String login, String password);
    public Utilisateur getUtilisateurCourant();
}
