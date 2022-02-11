package com.statistiquescovid.utilisateur.jwt;


public class SecurityUtils {

	public static final long EXPIRATION_TIME = 864_000_000; // 10 jours
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER_STRING = "Authorization";




	private SecurityUtils() {
	}

}

