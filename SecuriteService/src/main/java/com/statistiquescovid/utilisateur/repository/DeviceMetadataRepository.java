package com.statistiquescovid.utilisateur.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.statistiquescovid.utilisateur.entites.DeviceMetadata;

import java.util.List;

public interface DeviceMetadataRepository extends JpaRepository<DeviceMetadata, Long> {

    public List<DeviceMetadata> findByUserId(Long userId);
    
}
