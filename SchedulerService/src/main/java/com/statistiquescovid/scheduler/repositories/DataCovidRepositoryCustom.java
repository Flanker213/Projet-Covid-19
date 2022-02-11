package com.statistiquescovid.scheduler.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.statistiquescovid.scheduler.entities.StatistiquesCovidDepartement;
import com.statistiquescovid.scheduler.models.StatistiquesCovidRegion;

public interface DataCovidRepositoryCustom {

	public Page<StatistiquesCovidDepartement> search(String departementId, String regionId, Date dateDebut, Date dateFin, String trie, Pageable pageable);

	public List<StatistiquesCovidRegion> getStatistiquesRegionEvolution(String departementId, String regionId, Date dateDebut, Date dateFin, String trie, Integer topNVariation);

	public StatistiquesCovidRegion getStatistiquesGlobales(String departementId, String regionId, Date dateDebut, Date dateFin);

}
