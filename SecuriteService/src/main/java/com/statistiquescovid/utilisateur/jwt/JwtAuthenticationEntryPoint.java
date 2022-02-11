package com.statistiquescovid.utilisateur.jwt;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

   @Override
   public void commence(HttpServletRequest request,
                        HttpServletResponse response,
                        AuthenticationException authException) throws IOException {
	   System.err.println("# Oups (AuthenticationEntryPoint) : " + authException.getMessage());
	   // Ceci est appelé lorsque l'utilisateur essaie d'accéder à une ressource REST sécurisée sans fournir aucune information d'identification
	   // Nous devrions simplement envoyer une réponse interdite 403 car il n'y a pas de page «erreur» vers laquelle rediriger.
	   // Ici, vous pouvez placer le message d'ereeur que vous souhaitez au lieu de 'Full authentication is required to access this resource'
	   response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
   }
}