package com.statistiquescovid.scheduler.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.statistiquescovid.scheduler.entities.StatistiquesCovidDepartement;

public interface DataCovidRepository extends MongoRepository<StatistiquesCovidDepartement, String>, DataCovidRepositoryCustom {

	public boolean existsByDateBetween(Date dateDebut, Date dateFin);
	
	public boolean existsByDep(String departementId);
	
	public List<StatistiquesCovidDepartement> findByDateBetween(Date dateDebut, Date dateFin);
	
}
