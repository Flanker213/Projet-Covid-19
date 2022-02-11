package com.statistiquescovid.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.statistiquescovid.scheduler.services.DataCovidRecuperationService;

@SpringBootApplication
public class SchedulerServiceApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext appContext = SpringApplication.run(SchedulerServiceApplication.class, args);
		if (appContext != null) {
			DataCovidRecuperationService service  = appContext.getBean(DataCovidRecuperationService.class);
			if (service != null) {
				service.planifierRecuperationDonnees();
				System.out.println("# Le Service Scheduler est lancé !");
			}
		}
	}

}
