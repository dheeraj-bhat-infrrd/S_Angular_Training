package com.realtech.socialsurvey.core.services.authentication;

import com.realtech.socialsurvey.core.exception.InvalidInputException;

/**
 * Validates the captcha information provided
 */
public interface CaptchaValidation {

	/**
	 * Checks if the provided captcha is valid.
	 * 
	 * @param remoteAdrress
	 * @param challenge
	 * @param response
	 * @return validity of captcha
	 */
	public boolean isCaptchaValid(String remoteAddress, String challenge, String response) throws InvalidInputException;
}
