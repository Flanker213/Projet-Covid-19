package com.statistiquescovid.scheduler.services;

import java.util.Date;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.statistiquescovid.scheduler.entities.StatistiquesCovidDepartement;
import com.statistiquescovid.scheduler.utils.DateUtils;

public class JobRecuperationStatistiques implements Job {
	private static final Logger logger = LoggerFactory.getLogger(JobRecuperationStatistiques.class);
	@Autowired
	private DataCovidRecuperationService dataCovidRecuperationService;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobKey jobKey = context.getJobDetail().getKey();
		logger.info("# Exécution du Job avec clé '{}' pour la récupération des statistiques COVID", jobKey);

		List<StatistiquesCovidDepartement> statistiques = dataCovidRecuperationService.recupererDonneesActuellesSurTousLesDepartements();
		if (statistiques == null) {
			logger.error("# Attention : Pas de données observées par le Job avec clé '{}'", context.getJobDetail().getKey());
		}

		// TODO: On peut Supprimer le Job une fois, sa tâche efféctuée !
		try {
			Scheduler scheduler = context.getScheduler();

			scheduler.deleteJob(jobKey);

			TriggerKey triggerKey = new TriggerKey(jobKey.getName());
			scheduler.unscheduleJob(triggerKey);

			logger.info("# Suppression du JobDetail-Key = " + jobKey.getName() + " efféctué avec Succès Après son éxécution !");
		} catch (SchedulerException e) {
			logger.info("# Erreur de suppression du JobDetail-Key = " + jobKey.getName() + " Après son éxécution : '{}'", e.getMessage());
		}

		// TODO: Demander l'éxécution du prochain JOB
		Date datePlanification = dataCovidRecuperationService.planifierRecuperationDonnees();
		if(datePlanification != null) {
			logger.info("# Prochain Job planifié le '{}'", DateUtils.formatDateHeure(datePlanification));
		}
		else {
			logger.error("# Echec de plannification du prochain Job !");
		}
	}

}
