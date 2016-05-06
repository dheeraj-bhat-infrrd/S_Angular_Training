package com.realtech.socialsurvey.api.exceptions;

import org.springframework.validation.Errors;

public class BadRequestException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private Errors errors;

	public BadRequestException(String message, Errors errors) {
		super(message);
		this.errors = errors;
	}

	public Errors getErrors() {
		return errors;
	}
}