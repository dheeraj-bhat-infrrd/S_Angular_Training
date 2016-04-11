package com.realtech.socialsurvey.api.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.realtech.socialsurvey.api.exceptions.BadRequestException;
import com.realtech.socialsurvey.api.models.request.LoginRequest;

@Component
public class LoginValidator implements Validator {

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	public boolean supports(Class<?> clazz) {
		return LoginRequest.class.isAssignableFrom(clazz);
	}

	public void validate(Object target, Errors errors) {
		LoginRequest loginRequest = (LoginRequest) target;

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", ErrorCodes.EMAIL_INVALID, "email cannot be empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", ErrorCodes.PASSWORD_INVALID, "password cannot be empty");

		if (errors.hasErrors()) {
			throw new BadRequestException("required fields cannot be empty", errors);
		}

		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(loginRequest.getEmail());

		if (!matcher.matches()) {
			errors.rejectValue("email", ErrorCodes.EMAIL_INVALID, "email address is invalid");
			throw new BadRequestException("email address is invalid", errors);
		}
	}
}