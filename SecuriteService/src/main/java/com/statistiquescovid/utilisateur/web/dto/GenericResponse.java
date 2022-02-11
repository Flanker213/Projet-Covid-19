package com.statistiquescovid.utilisateur.web.dto;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

public class GenericResponse {
	private String message;
	private boolean success;
	private boolean error;

	public GenericResponse(final String message) {
		super();
		this.message = message;
	}

	public GenericResponse(final String message, final boolean success, final boolean error) {
		super();
		this.message = message;
		this.success = success;
		this.error = error;
	}

	public GenericResponse(boolean error, List<ObjectError> allErrors, String message_error) {
		this.error = error;
		String temp = allErrors.stream().map(e -> {
			if (e instanceof FieldError) {
				return "{\"field\":\"" + ((FieldError) e).getField() + "\",\"defaultMessage\":\"" + e.getDefaultMessage() + "\"}";
			} else {
				return "{\"object\":\"" + e.getObjectName() + "\",\"defaultMessage\":\"" + e.getDefaultMessage() + "\"}";
			}
		}).collect(Collectors.joining(","));
		this.message = "[" + message_error + " - " + temp + "]";
	}

	public GenericResponse(boolean error, String message_error, String typeError) {
		this.error = error;
		this.message = typeError + " - " + message_error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(final String message) {
		this.message = message;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

}
