package com.statistiquescovid.scheduler.services;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.statistiquescovid.scheduler.configs.QuartzConfig;
import com.statistiquescovid.scheduler.entities.StatistiquesCovidDepartement;
import com.statistiquescovid.scheduler.utils.DateUtils;

// https://github.com/florianzemma/CoronavirusAPI-France
// https://coronavirusapifr.herokuapp.com/data/live/departements

@Service
public class DataCovidRecuperationServiceImpl implements DataCovidRecuperationService {

	@Autowired
	private Environment environnement;
	@Autowired
	private DataCovidService dataCovidService;
	///@Autowired
	///private Scheduler scheduler; // Si on souhaite utiliser les options rapides par défaut du Scheduler
	@Autowired
	private QuartzConfig quartzConfig;

	private final String URL_RECHERCHE_DONNEES = "url-serveur-recherche-donnees";
	private final String URL_RECHERCHE_DONNEES_VALEUR_DEFAUT = "https://coronavirusapifr.herokuapp.com/data";
	private static final Logger logger = LoggerFactory.getLogger(DataCovidRecuperationServiceImpl.class);
	private static final String ID_Job_Detail = "JOB-DATA-COVID";
	private static final String MINUTES_DEFAUT = "30";
	private static final String HEURE_DEFAUT = "23:59";
	private static final String RECUPERER_AU_DEMARRAGE = "true";
	private Instant instantProchaineRecuperation = null;
	// Variable de classe permettant de verfier si les données ont été déjà recupérées au démarrage
	private boolean donneesRecupereesAuDemarrage = false;

	@Override
	public List<StatistiquesCovidDepartement> recupererDonneesActuellesSurTousLesDepartements() {
		List<StatistiquesCovidDepartement> donneesDepartements = new ArrayList<StatistiquesCovidDepartement>();
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

			String urlRechercheDonnees = this.environnement.getProperty(URL_RECHERCHE_DONNEES, URL_RECHERCHE_DONNEES_VALEUR_DEFAUT);
			String urlRechercheDonneesDepartements = urlRechercheDonnees + "/live/departements";
			logger.info("# Recherche Données des départements - URL = " + urlRechercheDonneesDepartements + " ...");

			ParameterizedTypeReference<List<StatistiquesCovidDepartement>> typeResultat = new ParameterizedTypeReference<List<StatistiquesCovidDepartement>>() {};
			ResponseEntity<List<StatistiquesCovidDepartement>> reponseRequete = this.getRestTemplate().exchange(
					urlRechercheDonneesDepartements, 
					HttpMethod.GET, 
					new HttpEntity<>(null, new HttpHeaders()),
					typeResultat
					);
			if (reponseRequete != null && reponseRequete.getStatusCode() == HttpStatus.OK && reponseRequete.getBody() != null) {
				donneesDepartements = reponseRequete.getBody();
			}
			else if (reponseRequete != null) {
				logger.error("# Echec de la recherche des données - status-code = " + reponseRequete.getStatusCodeValue());
			}
			else {
				logger.error("# ERREUR lors de la recherche des données !");
			}
		} catch (Exception e) {
			logger.error("# ERREUR lors de la recherche des données '{}' !", e.getMessage());
			e.printStackTrace();
		}

		// TODO: Programmer les traitments à effectuer pour les données obtenues
		logger.info("# Nombre de départements observés = " + donneesDepartements.size());
		if (donneesDepartements != null && !donneesDepartements.isEmpty()) {
			for (Iterator<StatistiquesCovidDepartement> iterator = donneesDepartements.iterator(); iterator.hasNext();) {
				StatistiquesCovidDepartement statistiquesCovidDepartement = (StatistiquesCovidDepartement) iterator.next();

				// Vérifier si les données du Departement existe et les mettre à jour,
				// Sinon enregistrer en tant que nouvel Enregistrement
				// Enregistrement des données observées !
				StatistiquesCovidDepartement savedData = dataCovidService.enregistrer(statistiquesCovidDepartement);
				if (savedData == null) {
					logger.info("# Echec de traitement des données " + statistiquesCovidDepartement);
				}
			}
		}

		return donneesDepartements;
	}

	@Override
	public Date planifierRecuperationDonnees() {
		System.out.println("************* Plannification du Job ****************");
		// TODO LA méthode 'la plus importante' du présent mircoservice
		// Lire les configurations et déterminer 'QUAND ?' recupérer les statistiques
		// 1. D'abord : Est ce que commencer de récuperer les statistiques dès le lancement de l'application
		// Par défaut 'OUI'
		Boolean recupererAuDemarrage = Boolean.valueOf(environnement.getProperty("recuperer-statistiques.au-demarrage", RECUPERER_AU_DEMARRAGE));
		if (!donneesRecupereesAuDemarrage && recupererAuDemarrage != null && recupererAuDemarrage.booleanValue()) {
			this.recupererDonneesActuellesSurTousLesDepartements();
			this.donneesRecupereesAuDemarrage = true; // Pour indiquer qu'on vient de démarrer le projet et recupérer les données
		}

		// 2. Ensuite plannifier la prochaine récupération des données
		this.determinerInstantProchainExecutionJob();

		boolean planifie = this.demarrerRecuperationStatistiques();
		if(planifie) {
			return Date.from(instantProchaineRecuperation);
		}
		else {
			return null;
		}
	}

	@Override
	public boolean arreterPlannification() {
		// On supprime directement le Job
		try {
			Scheduler scheduler = quartzConfig.schedulerFactoryBean().getScheduler();

			scheduler.deleteJob(new JobKey(ID_Job_Detail));

			TriggerKey triggerKey = new TriggerKey(ID_Job_Detail);
			scheduler.unscheduleJob(triggerKey);

			logger.info("# Suppression du JobDetail-Key = " + ID_Job_Detail + " efféctué avec Succès  !");
			return true;
		} catch (IOException | SchedulerException e) {
			logger.error("Erreur: Plannification du Job '{}'", e.getMessage());
			e.printStackTrace();
		}

		logger.info("# Problème d'arrêt du Job '"+ ID_Job_Detail +"'");
		return false;
	}

	private boolean demarrerRecuperationStatistiques() {
		if(instantProchaineRecuperation == null) {
			logger.error("# Erreur: La date de la prochaine récuperation des statistiques est requise !");
			return false;
		}

		try {
			// S'assurer que le temps ne soit pas déjà passé !
			ZonedDateTime dateTime = instantProchaineRecuperation.atZone(ZoneId.systemDefault());
			if (dateTime.isBefore(ZonedDateTime.now())) {
				logger.error("# Erreur: La date de la prochaine récuperation des statistiques doit être ultérieure à la date courante !");
				return false;
			}

			// Pour des raisons d'optimisation, on arrête la plannification précédente avant de plannifier une autre
			boolean arreterJobPrecedent = this.arreterPlannification();
			if(!arreterJobPrecedent) {
				logger.error("# Il est impossible d'arreter les Jobs précédents !");
			}

			// Créer le JobDetail
			JobDetail jobDetail = buildJobDetail();

			// Planifier le temps où éxecuter la Tâche (Job)
			Date dateDeclenchementJob = Date.from(instantProchaineRecuperation);
			Trigger trigger = this.buildJobTriggerFacturation(jobDetail, dateDeclenchementJob);

			// Initialiser l'instance du planificateur
			// Solution 1 : Avec les options par défaut
			//scheduler.scheduleJob(jobDetail, trigger);
			// Solution 2 : Avec les configurations de Bean
			Scheduler scheduler = quartzConfig.schedulerFactoryBean().getScheduler();
			scheduler.scheduleJob(jobDetail, trigger);
			scheduler.start();

			String message = "######## Planification réussie : JobDetail-Key = " + jobDetail.getKey().getName() + " planifié avec Succès le " + DateUtils.formatDateHeure(dateDeclenchementJob);
			System.err.println(message);
			logger.info(message);
			return true;
		} catch (SchedulerException | IOException ex) {
			String message = "####### Erreur Planification (Scheduling), Veuillez prévoir une autre tentative !";
			logger.error("####### Erreur Planification (Scheduling) : '{}' !", ex.getMessage());
			System.err.println(message);
			ex.printStackTrace();
			return false;
		}
	}

	private JobDetail buildJobDetail() {
		// L'attribuer un IDENTIFIANT pouvant être reconnu plustard !
		String idJobDetail = String.valueOf(ID_Job_Detail);
		//JobDetail jobDetail = JobBuilder.newJob(JobRecuperationStatistiquesV1.class)
		JobDetail jobDetail = JobBuilder.newJob(JobRecuperationStatistiques.class)
				.withIdentity(idJobDetail)
				.withDescription("Job pour la récupération des statistiques Covid 1")
				.storeDurably()
				.build();

		return jobDetail;
	}

	private Trigger buildJobTriggerFacturation(JobDetail jobDetail, Date dateDeclenchementJob) {
		return TriggerBuilder.newTrigger()
				.forJob(jobDetail)
				.withIdentity(jobDetail.getKey().getName())
				.withDescription("Déclencheur (Trigger) des tâches de recupération de statistiques Covid 19")
				.startAt(dateDeclenchementJob)
				.withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
				.build();
	}

	private void determinerInstantProchainExecutionJob() {
		// Cas 1: Si chaque Fréquence d'interval de temps (En nombre de minutes)
		// Cas 2: Sinon est ce repéter 'par jour' à une heure précise de la journée
		// Autres CAS pouvant être traités
		// Sinon est ce repéter 'par semaine' à une date précise de la semaine
		// Sinon est ce repéter 'par mois' à une date précise du mois
		Boolean frequenceParMinutes = Boolean.valueOf(environnement.getProperty("recuperer-statistiques.frequence-minutes.activer"));
		Boolean frequenceParDefinie = Boolean.valueOf(environnement.getProperty("recuperer-statistiques.frequence-definie.activer"));
		if ((frequenceParMinutes == null || !frequenceParMinutes.booleanValue()) && frequenceParDefinie != null && frequenceParDefinie.booleanValue()) {
			int heure = 23;
			int minutes = 59;
			String configsHeureExecution = environnement.getProperty("recuperer-statistiques.frequence-definie.heure-execution", HEURE_DEFAUT);
			String[] tokens = configsHeureExecution.trim().split(":");
			if(tokens != null && tokens.length >= 2) {
				Integer hr = Integer.valueOf(tokens[0]);
				if (hr != null && hr.intValue() >= 0) {
					heure = hr;
				}
				Integer min = Integer.valueOf(tokens[1]);
				if (min != null && min.intValue() >= 0) {
					minutes = min;
				}
			}

			ZonedDateTime zonedDateTime = Instant.now().atZone(ZoneId.systemDefault());
			zonedDateTime = zonedDateTime.toLocalDate().atStartOfDay(ZoneId.systemDefault());
			zonedDateTime = zonedDateTime.withHour(heure);
			zonedDateTime = zonedDateTime.withMinute(minutes);
			// Si l'heure est déjà passéee alors prendre le lendemain (Prochain jour à la même heure)
			if (zonedDateTime.isBefore(ZonedDateTime.now())) {
				zonedDateTime = zonedDateTime.plusDays(1);
			}
			this.instantProchaineRecuperation = zonedDateTime.toInstant();
			System.err.println("**** Plannification du Job par heure journalier = " + heure + ":" + minutes + " --> Prochaine = " + DateUtils.formatDateHeure(Date.from(instantProchaineRecuperation)));
		} // Par défaut frequenceParMinutes == 'true'
		else {
			Long minutesFrequence = Long.valueOf(environnement.getProperty("recuperer-statistiques.frequence-minutes.minutes", MINUTES_DEFAUT));
			if (minutesFrequence == null || minutesFrequence.longValue() <= 0) {
				minutesFrequence = 30L;
			}
			ZonedDateTime zonedDateTime = Instant.now().atZone(ZoneId.systemDefault());
			zonedDateTime = zonedDateTime.plusMinutes(minutesFrequence);
			this.instantProchaineRecuperation = zonedDateTime.toInstant();
			//this.instantProchaineRecuperation = Instant.now().plusSeconds(minutesFrequence * 60);
			System.err.println("**** Plannification du Job par interval de minutes = " + minutesFrequence + " minute(s) --> Prochaine = " + DateUtils.formatDateHeure(Date.from(instantProchaineRecuperation)));
		}
	}

	private RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
		restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
		MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
		//mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM));
		restTemplate.getMessageConverters().add(mappingJackson2HttpMessageConverter);	
				
		return restTemplate;
	}

}
