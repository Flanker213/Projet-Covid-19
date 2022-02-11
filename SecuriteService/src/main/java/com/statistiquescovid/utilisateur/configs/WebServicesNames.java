package com.statistiquescovid.utilisateur.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "service")
public class WebServicesNames {

	private String robotService;
	private String securiteService;
	private String statistiquesService;
	
	public String getRobotService() {
		return robotService;
	}
	public void setRobotService(String robotService) {
		this.robotService = robotService;
	}
	public String getSecuriteService() {
		return securiteService;
	}
	public void setSecuriteService(String securiteService) {
		this.securiteService = securiteService;
	}
	public String getStatistiquesService() {
		return statistiquesService;
	}
	public void setStatistiquesService(String statistiquesService) {
		this.statistiquesService = statistiquesService;
	}	
}
