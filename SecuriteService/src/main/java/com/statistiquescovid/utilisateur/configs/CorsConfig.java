package com.statistiquescovid.utilisateur.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

   @Bean
   public CorsFilter corsFilter() {
      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      
      CorsConfiguration config = new CorsConfiguration();
      config.applyPermitDefaultValues(); // Peut suffir !
      config.setAllowCredentials(true);
      config.addAllowedOrigin("*"); // Exemple: http://mon-siteweb.net
      config.addAllowedHeader("*");
      config.addAllowedMethod("*");

      source.registerCorsConfiguration("/**", config);
      return new CorsFilter(source);
   }
   
}