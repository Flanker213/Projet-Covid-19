package com.statistiquescovid.utilisateur.configs;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import com.statistiquescovid.utilisateur.jwt.JwtProvider;

public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {

	private JwtProvider jwtProvider;

	public RestTemplateInterceptor(JwtProvider jwtProvider) {
		this.jwtProvider = jwtProvider;
	}

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		// TODO Auto-generated method stub
		if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null) {
			String token = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
			if (token != null && StringUtils.hasText(token)) {
				String tokenWithPrefix = token;
				if (StringUtils.hasText(jwtProvider.getPrefix())) {
					tokenWithPrefix = jwtProvider.getPrefix() + " " + token;
				}
				else {
					tokenWithPrefix = "Bearer " + token;
				}
				request.getHeaders().add(jwtProvider.getHeader(), tokenWithPrefix);
			}
		}

		return execution.execute(request, body);
	}

}
