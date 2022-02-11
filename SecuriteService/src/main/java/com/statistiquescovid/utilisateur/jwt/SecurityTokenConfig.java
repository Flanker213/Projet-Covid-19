package com.statistiquescovid.utilisateur.jwt;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity	// Activez la configuration de la sécurité; pour Spring Security.
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityTokenConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private JwtProvider jwtProvider;
	@Autowired
	private CorsFilter corsFilter;
	@Autowired
	private JwtAuthenticationEntryPoint authenticationErrorHandler;
	@Autowired
	private JwtAccessDeniedHandler jwtAccessDeniedHandler;

	// Configurer les chemins et les requetes qui devraient être ignorés par Spring Security
	@Override
	public void configure(WebSecurity web) {
		web.ignoring()
		.antMatchers(HttpMethod.OPTIONS, "/**")
		.antMatchers("/api/covid/v1/comptes-utilisateurs/preferences/telecharger-photo/**")
		// allow anonymous resource requests
		.antMatchers(
				"/",
				"/*.html",
				"/favicon.ico",
				"/**/*.html", // tel que "swagger-ui.html"
				"/**/*.css",
				"/**/*.js",
				"/h2-console/**"
				);
	}

	// Configurer les parametres de Sécurité
	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
		// Nous n'avons pas besoin de CSRF car notre token est invulnérable
		.csrf().disable()

		.addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)

		// Ne créer aucune session (STATELESS) - TODO Peut être placé entre «h2-console» et «autorisation»
		// S'assurer que nous utilisons une session sans état; la session ne sera pas utilisée pour stocker l'état de l'utilisateur.
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) 	
		.and()
		// Gérer une tentative autorisée 
		.exceptionHandling()
		.authenticationEntryPoint(authenticationErrorHandler)
		.accessDeniedHandler(jwtAccessDeniedHandler)
		.and()
		// Activer la console de BD H2 (h2-console)
		.headers()
		.frameOptions()
		.sameOrigin()
		.and()
		// TODO: Optionel - Ajoutez un filtre pour valider les Tokens à chaque requête
		.addFilterAfter(new JwtFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
		// Configuration des requetes d'autorisation
		.authorizeRequests()
		// Autoriser à tous ceux qui accèdent "HealthCheck" service
		.antMatchers("/HealthCheck").permitAll()
		.antMatchers("/api/covid/v1/comptes-utilisateurs/preferences/telecharger-photo/**").permitAll()
		.antMatchers("/").permitAll()
		.antMatchers("/api/covid/v1/comptes-utilisateurs/login").permitAll()
		// Autoriser à tous ceux qui accèdent "/login" dans l'url
		.antMatchers(HttpMethod.POST, jwtProvider.getUri()).permitAll() 
		// Ceux qui souhaite accéder à leur profils doivent avoir un role !
		.antMatchers("/api/covid/v1/comptes-utilisateurs/preferences/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
		//// Tout autre requete necessite l'authentification
		//.anyRequest().authenticated()
		.and()
		.apply(securityConfigurerAdapter()); 
	}

	private JwtConfigurer securityConfigurerAdapter() {
		return new JwtConfigurer(jwtProvider);
	}

}
