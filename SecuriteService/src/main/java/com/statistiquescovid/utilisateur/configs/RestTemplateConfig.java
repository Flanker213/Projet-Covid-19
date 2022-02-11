package com.statistiquescovid.utilisateur.configs;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.statistiquescovid.utilisateur.jwt.JwtProvider;

@Configuration
public class RestTemplateConfig {

	@Autowired
	private JwtProvider jwtProvider;

//	@LoadBalanced
	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
		restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		return restTemplate;
	}

	@PostConstruct
	public void addInterceptors() {
		// Intercepter les requêtes Http et Https pour leurs ajouter notre Token s'il est présent
		List<ClientHttpRequestInterceptor> interceptors = restTemplate().getInterceptors();
		if(interceptors == null)
			interceptors = new ArrayList<ClientHttpRequestInterceptor>();
		interceptors.add(new RestTemplateInterceptor(jwtProvider));
		restTemplate().setInterceptors(interceptors);
	}

}
