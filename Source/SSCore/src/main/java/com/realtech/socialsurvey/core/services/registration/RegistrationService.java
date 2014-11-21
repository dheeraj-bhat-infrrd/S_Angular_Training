package com.realtech.socialsurvey.core.services.registration;

import com.realtech.socialsurvey.core.exception.InvalidInputException;

/**
 * Holds methods to register a user or a company to the application
 *
 */
public interface RegistrationService {

	/**
	 * Sends invitation to corporate to register
	 */
	public void inviteCorporateToRegister(String firstName, String lastName, String emailId)  throws InvalidInputException;
}
