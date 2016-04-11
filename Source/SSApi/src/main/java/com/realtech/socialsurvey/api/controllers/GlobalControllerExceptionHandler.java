package com.realtech.socialsurvey.api.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.realtech.socialsurvey.api.exceptions.BadRequestException;
import com.realtech.socialsurvey.api.exceptions.ValidationException;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

	private final static String ERRORS = "errors";

	@ExceptionHandler(ValidationException.class)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> handleValidationException(
			ValidationException validationException) {

		Map<String, Object> result = createErrorResponse(validationException.getErrors());
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	@ExceptionHandler(BadRequestException.class)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> handleBadRequestException(
			BadRequestException badRequestException) {

		Map<String, Object> result = createErrorResponse(badRequestException.getErrors());
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.BAD_REQUEST);
	}

	private Map<String, Object> createErrorResponse(Errors errors) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, String> errorResults = new HashMap<String, String>();

		for (ObjectError error: errors.getAllErrors()) {
			errorResults.put(error.getCode(), error.getDefaultMessage());
		}

		result.put(ERRORS, errorResults);
		return result;
	}
}