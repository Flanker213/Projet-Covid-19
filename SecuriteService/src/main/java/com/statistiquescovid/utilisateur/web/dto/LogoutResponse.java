package com.statistiquescovid.utilisateur.web.dto;

public class LogoutResponse {

	private boolean logout;

	public LogoutResponse() {
		super();
	}

	public LogoutResponse(boolean logout) {
		super();
		this.logout = logout;
	}

	public boolean isLogout() {
		return logout;
	}

	public void setLogout(boolean logout) {
		this.logout = logout;
	}
	
}
