package com.statistiquescovid.scheduler.services;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;

import com.statistiquescovid.scheduler.entities.StatistiquesCovidDepartement;
import com.statistiquescovid.scheduler.models.StatistiquesCovidRegion;

public interface DataCovidService {
	
	public boolean departementExiste(String departementId);
	
	public StatistiquesCovidDepartement enregistrer(StatistiquesCovidDepartement statistiquesCovidDepartement);
	
	public Page<StatistiquesCovidDepartement> rechercher(String departementId, String regionId, Date dateDebut, Date dateFin, String trie, Integer numeroPage, Integer tailleMax);
	
	public List<StatistiquesCovidRegion> getStatistiquesRegionEvolution(String departementId, String regionId, Date dateDebut, Date dateFin, String trie, Integer topNVariation);
	
	public StatistiquesCovidRegion getStatistiquesGlobales(String departementId, String regionId, Date dateDebut, Date dateFin);
	
}
