package com.statistiquescovid.scheduler.services;

import java.util.Date;
import java.util.List;

import com.statistiquescovid.scheduler.entities.StatistiquesCovidDepartement;

public interface DataCovidRecuperationService {

	public Date planifierRecuperationDonnees();
	
	public boolean arreterPlannification();
	
	public List<StatistiquesCovidDepartement> recupererDonneesActuellesSurTousLesDepartements();
	
}
