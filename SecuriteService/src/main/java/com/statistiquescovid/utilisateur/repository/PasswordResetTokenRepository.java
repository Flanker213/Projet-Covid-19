package com.statistiquescovid.utilisateur.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.statistiquescovid.utilisateur.entites.PasswordResetToken;
import com.statistiquescovid.utilisateur.entites.CompteUtilisateur;

import java.util.Date;
import java.util.stream.Stream;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    public PasswordResetToken findByToken(String token);

    public PasswordResetToken findByCompteUtilisateur(CompteUtilisateur compteUtilisateur);

    public Stream<PasswordResetToken> findAllByExpiryDateLessThan(Date now);

    public void deleteByExpiryDateLessThan(Date now);

    @Modifying
    @Query("delete from PasswordResetToken t where t.expiryDate <= :date")
    public void deleteAllExpiredSince(@Param("date") Date now);
}
