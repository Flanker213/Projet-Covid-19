package com.statistiquescovid.utilisateur.jwt;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

   @Override
   public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
	   System.err.println("# OUPS (AccessDeniedHandler) : " + accessDeniedException.getMessage());
	   // Ceci est appelé lorsque l'utilisateur essaie d'accéder à une ressource REST sécurisée sans l'autorisation nécessaire
	   // Nous devrions simplement envoyer une réponse interdite 403 car il n'y a pas de page «erreur» vers laquelle rediriger.
	   // Ici, vous pouvez placer le message d'ereeur que vous souhaitez au lieu de 'Access is denied'
	   response.sendError(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage());
   }
}

