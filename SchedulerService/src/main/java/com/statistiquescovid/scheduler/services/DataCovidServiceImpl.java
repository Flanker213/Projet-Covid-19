package com.statistiquescovid.scheduler.services;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.statistiquescovid.scheduler.entities.StatistiquesCovidDepartement;
import com.statistiquescovid.scheduler.models.StatistiquesCovidRegion;
import com.statistiquescovid.scheduler.repositories.DataCovidRepository;

@Service
public class DataCovidServiceImpl implements DataCovidService {

	@Autowired
	private DataCovidRepository dataCovidRepository;
	public static int DEFAULT_SIZE = 20;

	@Override
	public StatistiquesCovidDepartement enregistrer(StatistiquesCovidDepartement statistiquesCovidDepartement) {
		// TODO Auto-generated method stub
		// Appliquer quelques vérifications sur les données avant de les enregistrer
		// ...
		// Vérifier si les données du Departement existe et les mettre à jour,
		// Sinon enregistrer en tant que nouvel Enregistrement
		String departementId = statistiquesCovidDepartement.getDep();
		if (this.departementExiste(departementId)) {
			// TODO: Ce qu'il faut ici, c'est de retrouver les données de la DB, et les mettre à jour attribut par attribut
			// Mais personnelement pour aller vite, vu qu'il n'y a que la date d'enregistrement qui soit necessaire d'être préervée
			StatistiquesCovidDepartement statististiqueDansDB = dataCovidRepository.findById(departementId).get();
			if(statististiqueDansDB != null && statististiqueDansDB.getDateEnregistrement() != null) {
				statistiquesCovidDepartement.setDateEnregistrement(statististiqueDansDB.getDateEnregistrement());
			}
			// Mise à jour des données du département 'departementId'
			statistiquesCovidDepartement.setDateModification(Date.from(Instant.now()));
		}
		else {
			// Nouvel enregistrement pour le département 'departementId'
			statistiquesCovidDepartement.setDateEnregistrement(Date.from(Instant.now()));
		}
		return dataCovidRepository.save(statistiquesCovidDepartement);
	}

	@Override
	public Page<StatistiquesCovidDepartement> rechercher(String departementId, String regionId, Date dateDebut, Date dateFin, String trie, Integer numeroPage, Integer tailleMax) {
		int page = numeroPage != null && numeroPage.intValue() > 0 ? (numeroPage - 1) : 0;
		int taille = tailleMax != null ? tailleMax : DEFAULT_SIZE;
		Pageable pageable = PageRequest.of(page, taille);

		return dataCovidRepository.search(departementId, regionId, dateDebut, dateFin, trie, pageable);
	}

	@Override
	public List<StatistiquesCovidRegion> getStatistiquesRegionEvolution(String departementId, String regionId, Date dateDebut,
			Date dateFin, String trie, Integer topNVariation) {
		// TODO Auto-generated method stub
		if (topNVariation == null || topNVariation <= 0) { // Limiter, par défaut, la sélection 
			topNVariation = DEFAULT_SIZE;
		}
		return dataCovidRepository.getStatistiquesRegionEvolution(departementId, regionId, dateDebut, dateFin, trie, topNVariation);
	}

	@Override
	public boolean departementExiste(String departementId) {
		// TODO Auto-generated method stub
		return dataCovidRepository.existsById(departementId);
	}

	@Override
	public StatistiquesCovidRegion getStatistiquesGlobales(String departementId, String regionId, Date dateDebut,
			Date dateFin) {
		// TODO Auto-generated method stub
		return dataCovidRepository.getStatistiquesGlobales(departementId, regionId, dateDebut, dateFin);
	}

}
