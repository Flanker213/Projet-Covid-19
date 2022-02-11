package com.statistiquescovid.scheduler.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class HealthCheckController {

	@GetMapping({ "/HealthCheck" })
	public ResponseEntity<String> healtCheck() {
		return new ResponseEntity<>("Il semble que tout va bien sur le service 'SchedulerService'.", HttpStatus.OK);
	}

}
