package com.statistiquescovid.utilisateur.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.statistiquescovid.utilisateur.entites.CompteUtilisateur;
import com.statistiquescovid.utilisateur.entites.VerificationToken;

import java.util.Date;
import java.util.stream.Stream;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    public VerificationToken findByToken(String token);

    public VerificationToken findByCompteUtilisateur(CompteUtilisateur compteUtilisateur);

    public Stream<VerificationToken> findAllByExpiryDateLessThan(Date now);

    public void deleteByExpiryDateLessThan(Date now);

    @Modifying
    @Query("delete from VerificationToken t where t.expiryDate <= ?1")
    public void deleteAllExpiredSince(Date now);
    
}
