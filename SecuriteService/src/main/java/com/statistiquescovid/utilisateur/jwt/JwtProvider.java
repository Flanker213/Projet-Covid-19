package com.statistiquescovid.utilisateur.jwt;

import io.jsonwebtoken.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.statistiquescovid.utilisateur.entites.CompteUtilisateur;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtProvider {

	private final Logger LOGGER = LoggerFactory.getLogger(JwtProvider.class);

	@Value("${security.jwt.uri:/login/**}")
	private String Uri;
	@Value("${security.jwt.header:Authorization}")
	private String header;
	@Value("${security.jwt.prefix:Bearer }")
	private String prefix;
	@Value("${security.jwt.expiration:72000}")
	public int expiration; // En Secondes
	@Value("${security.jwt.secret:c'est ma cle publique :)}")
	private String secret;
	@Value("${security.jwt.issuer}")
	private String issuer;
	@Value("${security.jwt.audience}")
	private String audience;


	public String generateJWTByUser(CompteUtilisateur compteUtilisateur) {
		// Il est inute de générer un Token d'un utilisateur non valide !
		// On peut également exigé que le compte soit actif, dispose d'un rôle ... !
		if(compteUtilisateur == null 
				|| compteUtilisateur.getEmail() == null || compteUtilisateur.getEmail().isEmpty()
				|| compteUtilisateur.getId() == null || compteUtilisateur.getId().longValue()<0) {
			return null;
		}
		
		Claims claims = Jwts.claims().setSubject(compteUtilisateur.getEmail());
		claims.put("nameidentifier", compteUtilisateur.getEmail());
		if (compteUtilisateur.getNom() != null && !compteUtilisateur.getNom().isEmpty()) {
			claims.put("nom", compteUtilisateur.getNom());
		}
		claims.put("actif", compteUtilisateur.isActif());
		claims.put("role", compteUtilisateur.getRole());

		Instant instantNow = Instant.now();
		Date dateIssuer = Date.from(instantNow);
		Date dateExpiration = Date.from(instantNow.plusSeconds(expiration));
		String accessToken = Jwts.builder()
				.setSubject(compteUtilisateur.getEmail())
				.setClaims(claims)
				.setAudience(audience)
				.setIssuer(issuer)
				.setIssuedAt(dateIssuer)
				.setExpiration(dateExpiration)
				.signWith(SignatureAlgorithm.HS512, secret)
				.compact();
		return accessToken;
	}

	public Authentication getAuthentication(String token) {
		try {	
			// Des exceptions peuvent être levées lors de la création des revendications (Claims) si, par exemple, le jeton a expiré
			// 4. Valider le Token
			Claims claims = Jwts.parser()
					.setSigningKey(secret)
					.parseClaimsJws(token)
					.getBody();

			String utilisateurId = (String)claims.get("nameidentifier");
			String email = claims.getSubject();
			String nom = (String) claims.get("nom");
			Boolean isUserActive = (Boolean) claims.get("actif");
			List<String> rolesAuthorities = new ArrayList<String>();
			Object claimsRole = claims.get("role");
			if (claimsRole != null && claimsRole instanceof String) {
				rolesAuthorities.add((String)claimsRole);
			} else if (claimsRole != null && claimsRole instanceof Collection<?>) {
				@SuppressWarnings("unchecked")
				List<String> roles = (List<String>)claimsRole;
				rolesAuthorities.addAll(roles);
			}

			if(email != null && isUserActive != null && isUserActive.booleanValue() /*&& !rolesAuthorities.isEmpty()*/) {
				List<GrantedAuthority> grantedAuthorities = rolesAuthorities.stream().map(a -> new SimpleGrantedAuthority(a)).collect(Collectors.toList());
				Utilisateur utilisateur = new Utilisateur(utilisateurId, "", grantedAuthorities);
				utilisateur.setEmail(email);
				utilisateur.setId(utilisateurId);
				utilisateur.setActif(isUserActive);
				utilisateur.setNom(nom);
				String jti = (String) claims.get("jti");
				String aud = (String) claims.get("aud");
				String iss = (String) claims.get("iss");
				System.out.println("# Jti = " + jti);
				System.out.println("# Audience = " + aud);
				System.out.println("# Iss = " + iss);
				Long iat = castLongObject(claims.get("iat"));
				Long nbf = castLongObject(claims.get("nbf"));
				Long exp = castLongObject(claims.get("exp"));
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(iat * 1000);
				Date dateIat = c.getTime();
				c.setTimeInMillis(nbf * 1000);
				Date dateNbf = c.getTime();
				c.setTimeInMillis(exp * 1000);
				Date dateExp = c.getTime();
				System.out.println("# Issuer At (Iat) = " + claims.get("iat") + " [" + dateIat + "]");
				System.out.println("# Not before (Nbf) = " + claims.get("nbf") + " [" + dateNbf + "]");
				System.out.println("# Expires (Exp) = " + claims.get("exp") + " [" + dateExp + "]");
				System.err.println("USER Ath = " + utilisateur);
				UsernamePasswordAuthenticationToken userAuth = new UsernamePasswordAuthenticationToken(utilisateur, token, grantedAuthorities);
				return userAuth;
			}

			System.err.println("**** Utilisateur non reconnu !");
			return null;
		} catch (Exception e) {
			LOGGER.error("JWT token compact of handler are invalid trace: {}", e.getMessage());
			return null;
		}
	}

	public boolean validateToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(secret).parseClaimsJws(authToken);
			return true;
		} catch (SecurityException | MalformedJwtException e) {
			LOGGER.info("Signature JWT non valide.");
			LOGGER.trace("Signature JWT non valide - Trace: {}", e);
			e.printStackTrace();
		} catch (ExpiredJwtException e) {
			LOGGER.info("Jeton JWT expiré.");
			LOGGER.trace("Jeton JWT expiré - Trace: {}", e);
			e.printStackTrace();
		} catch (UnsupportedJwtException e) {
			LOGGER.info("Jeton JWT non pris en charge.");
			LOGGER.trace("Jeton JWT non pris en charge - Trace: {}", e);
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			LOGGER.info("Le jeton JWT compact du gestionnaire n'est pas valide.");
			LOGGER.trace("Le jeton JWT compact du gestionnaire n'est pas valide - Trace : {}", e);
			e.printStackTrace();
		} catch (Exception e) {
			LOGGER.info("Le jeton JWT n'est pas valide.");
			LOGGER.trace("Le jeton JWT n'est pas valide - Trace : {}", e);
			e.printStackTrace();
		}

		return false;
	}

	public String getUri() {
		return Uri;
	}

	public void setUri(String uri) {
		Uri = uri;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public int getExpiration() {
		return expiration;
	}

	public void setExpiration(int expiration) {
		this.expiration = expiration;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	private Long castLongObject(Object object) {
		Long result = 0l;
		try {
			if (object instanceof Long)
				result = ((Long) object).longValue();
			else if (object instanceof Integer) {
				result = ((Integer) object).longValue();
			} else if (object instanceof String) {
				result = Long.valueOf((String) object);
			}
		} catch (Exception e) {
			LOGGER.info("Erreurr : l'Objet ne peut pas être convertit en Long");
			LOGGER.trace("Error : l'Objet ne peut pas être convertit en Long - Trace : {}", e);
		}
		return result;
	}

}

