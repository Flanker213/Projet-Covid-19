package com.statistiquescovid.utilisateur.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
* Filtre les requetes entrantes et installe un 'Principal Spring Security' (Utilisateur)
* si un en-tête correspondant à un Token d'utilisateur valide est trouvé.
*/
public class JwtFilter extends  OncePerRequestFilter {
    
	private JwtProvider jwtProvider;
	private static final Logger LOGGER = LoggerFactory.getLogger(JwtFilter.class);
	
	public JwtFilter(JwtProvider jwtProvider) {
		this.jwtProvider = jwtProvider;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)
			throws ServletException, IOException {

		String requestURI = httpServletRequest.getRequestURI();
		
		// 1. Trouver l'en-tête d'authentification. Les Tokens sont censés être passés dans l'en-tête d'authentification
		String headerToken = httpServletRequest.getHeader(jwtProvider.getHeader());
		System.out.println("## requestURI = " + requestURI);
		System.out.println("## HEADER = " + headerToken);
		// 2. Valider l'en-tête et vérifier le préfixe
		if (StringUtils.hasText(headerToken) && headerToken.startsWith(jwtProvider.getPrefix())) {
			// 3. Obtenir Token
			String token = headerToken.replace(jwtProvider.getPrefix(), "").trim();
			System.out.println("## TOKEN = [" + token + "]");
			if(jwtProvider.validateToken(token)) {
				Authentication authentication = jwtProvider.getAuthentication(token);
				if(authentication != null) {
		            SecurityContextHolder.getContext().setAuthentication(authentication);
					LOGGER.info("Définir l'authentification sur le contexte de sécurité pour '{}', uri: {}", authentication.getName(), requestURI);
		        } 
		        else {
		        	// En cas de défaillance. S'assurer que le Context est nettoyé afin de garantir que l'utilisateur ne sera pas authentifié
		        	SecurityContextHolder.clearContext();
					LOGGER.info("Aucun Token JWT d'authentification valide trouvé, uri: {}", requestURI);
		        }
			}
			else {
				// En cas de défaillance. S'assurer que le Context est nettoyé afin de garantir que l'utilisateur ne sera pas authentifié
	        	SecurityContextHolder.clearContext();
				LOGGER.info("Token JWT non valide {}, uri: {}", token, requestURI);
			}
		} 
		else {// En cas de défaillance. S'assurer que le Context est nettoyé afin de garantir que l'utilisateur ne sera pas authentifié
        	SecurityContextHolder.clearContext();
			LOGGER.info("Aucun Token JWT d'authentification valide trouvé, uri: {}", requestURI);
		}
		
		// S'il n'y a pas de jeton fourni et donc l'utilisateur ne sera pas authentifié.
		// C'est bon. Peut-être que l'utilisateur accède à un chemin public ou renouvelle son Token.
		
		// Tous les chemins sécurisés qui nécessitent un Token sont déjà définis et sécurisés dans la classe de configuration.
		// Et si l'utilisateur a tenté d'accéder sans Token d'accès, il ne sera pas authentifié et une exception sera levée.
		
		// Valide ou Non,
		// Passer au filtre suivant de la chaîne de filtrage
		filterChain.doFilter(httpServletRequest, httpServletResponse);
	}

}

