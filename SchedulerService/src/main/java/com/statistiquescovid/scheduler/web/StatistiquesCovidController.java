package com.statistiquescovid.scheduler.web;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.statistiquescovid.scheduler.entities.StatistiquesCovidDepartement;
import com.statistiquescovid.scheduler.models.StatistiquesCovidRegion;
import com.statistiquescovid.scheduler.services.DataCovidService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/v1/statistiques-covid")
@Api(value = "Gestion des Données de statitiques Covid")
@CrossOrigin({"*"})
public class StatistiquesCovidController {
	private static final Logger logger = LoggerFactory.getLogger(StatistiquesCovidController.class);
	@Autowired
	private DataCovidService dataCovidService;

	@ApiOperation(value = "Rechercher les données")
	@GetMapping({"departements"})
	public ResponseEntity<Page<StatistiquesCovidDepartement>> rechercher(
			@ApiParam(required = false, name = "departementId", value = "L'Id du département concerné") 
			@Valid @RequestParam(name = "departementId", required = false) String departementId,
			@ApiParam(required = false, name = "regionId", value = "L'Id de la région concernée") 
			@Valid @RequestParam(name = "regionId", required = false) String regionId,
			@ApiParam(required = false, name = "dateDebut", value = "La Date (yyyy-MM-dd) début de la période") 
			@Valid @RequestParam(name = "dateDebut", required = false) 
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dateDebut, 
			@ApiParam(required = false, name = "dateFin", value = "La Date (yyyy-MM-dd) qui marque la fin de la période") 
			@Valid @RequestParam(name = "dateFin", required = false) 
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dateFin,
			@ApiParam(required = false, name = "trie", defaultValue = "CC", value = "Ordre à trier les données (DC=Décès croissant, DD=Décès décroissant, CC=Cas croissant, CD=Cas décroissant, HC=Hospitalisation croissant, HD=Hospitalisation décroissant)") 
			@Valid @RequestParam(name = "trie", required = false, defaultValue = "CC") String trie,
			@ApiParam(required = false, name = "page", value = "Numéro de page") 
			@Valid @RequestParam(name = "page", required = false) Integer page,
			@ApiParam(required = false, name = "size", value = "Nombre de lignes") 
			@Valid @RequestParam(name = "size", required = false, defaultValue = "20") Integer size) {
		Page<StatistiquesCovidDepartement> response = this.dataCovidService.rechercher(departementId, regionId, dateDebut, dateFin, trie, page, size);
		if(response != null) {
			logger.info("# La méthode rechercher() vient d'être exécutée.");
			return new ResponseEntity<Page<StatistiquesCovidDepartement>>(response, HttpStatus.OK);
		}

		logger.info("# Erreur lors de l'exécution de la méthode rechercher()");
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@ApiOperation(value = "Afficher les données sous forme évolutive par rapport aux régions")
	@GetMapping({"regions"})
	public ResponseEntity<List<StatistiquesCovidRegion>> rechercherEvolution(
			@ApiParam(required = false, name = "departementId", value = "L'Id du département concerné") 
			@Valid @RequestParam(name = "departementId", required = false) String departementId,
			@ApiParam(required = false, name = "regionId", value = "L'Id de la région concernée") 
			@Valid @RequestParam(name = "regionId", required = false) String regionId,
			@ApiParam(required = false, name = "dateDebut", value = "La Date (yyyy-MM-dd) début de la période") 
			@Valid @RequestParam(name = "dateDebut", required = false) 
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dateDebut, 
			@ApiParam(required = false, name = "dateFin", value = "La Date (yyyy-MM-dd) qui marque la fin de la période") 
			@Valid @RequestParam(name = "dateFin", required = false) 
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dateFin,
			@ApiParam(required = false, name = "trie", defaultValue = "CC", value = "Ordre à trier les données (DC=Décès croissant, DD=Décès décroissant, CC=Cas croissant, CD=Cas décroissant, HC=Hospitalisation croissant, HD=Hospitalisation décroissant)") 
			@Valid @RequestParam(name = "trie", required = false, defaultValue = "CC") String trie,
			@ApiParam(required = false, name = "topN", value = "Nombre max de périodes") 
			@Valid @RequestParam(name = "topN", required = false, defaultValue = "20") Integer topN) {
		List<StatistiquesCovidRegion> response = this.dataCovidService.getStatistiquesRegionEvolution(departementId, regionId, dateDebut, dateFin, trie, topN);
		if(response != null) {
			logger.info("# La méthode rechercherEvolution() vient d'être exécutée.");
			return new ResponseEntity<List<StatistiquesCovidRegion>>(response, HttpStatus.OK);
		}

		logger.info("# Erreur lors de l'exécution de la méthode rechercherEvolution()");
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@ApiOperation(value = "Afficher les resumés par rapport aux régions")
	@GetMapping({"generales"})
	public ResponseEntity<StatistiquesCovidRegion> rechercherStatsGlobales(
			@ApiParam(required = false, name = "departementId", value = "L'Id du département concerné") 
			@Valid @RequestParam(name = "departementId", required = false) String departementId,
			@ApiParam(required = false, name = "regionId", value = "L'Id de la région concernée") 
			@Valid @RequestParam(name = "regionId", required = false) String regionId,
			@ApiParam(required = false, name = "dateDebut", value = "La Date (yyyy-MM-dd) début de la période") 
			@Valid @RequestParam(name = "dateDebut", required = false) 
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dateDebut, 
			@ApiParam(required = false, name = "dateFin", value = "La Date (yyyy-MM-dd) qui marque la fin de la période") 
			@Valid @RequestParam(name = "dateFin", required = false) 
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dateFin) {
		StatistiquesCovidRegion response = this.dataCovidService.getStatistiquesGlobales(departementId, regionId, dateDebut, dateFin);
		if(response != null) {
			logger.info("# La méthode rechercherStatsGlobales() vient d'être exécutée.");
			return new ResponseEntity<StatistiquesCovidRegion>(response, HttpStatus.OK);
		}

		logger.info("# Erreur lors de l'exécution de la méthode rechercherStatsGlobales()");
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}


	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////                                                              //////////////////
	/////////////////////////////   POUR LES TESTS de RECUPERATIONs Des DONNées MANUELLEMENT   //////////////////
	/////////////////////////////                                                              //////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	@Autowired
	//	private com.statistiquescovid.scheduler.services.DataCovidRecuperationService dataRecuperationService;
	//	@ApiOperation(value = "Recupérer manuellement les données")
	//	@GetMapping({"recuperer-manuellement"})
	//	public ResponseEntity<List<StatistiquesCovidDepartement>> recupererManuellement() {
	//		List<StatistiquesCovidDepartement> response = this.dataRecuperationService.recupererDonneesActuellesSurTousLesDepartements();
	//		if(response != null) {
	//			logger.info("# La méthode recupererManuellement() vient d'être exécutée.");
	//			return new ResponseEntity<List<StatistiquesCovidDepartement>>(response, HttpStatus.OK);
	//		}
	//
	//		logger.info("# Erreur lors de l'exécution de la méthode recupererManuellement()");
	//		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	//	}

}
