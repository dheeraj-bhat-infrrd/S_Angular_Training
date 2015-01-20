package com.realtech.socialsurvey.core.services.authentication;

import com.realtech.socialsurvey.core.entities.Captcha;
// JIRA: SS-12: Captcha Validation: RM06
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
	
	/**
	 * Present a captcha. If the the previous captcha id is not null, then skip that captcha and present a new one
	 * @param previousCaptchaId
	 * @return
	 */
	public Captcha presentCaptchaQuestion(String previousCaptchaId);
	
	/**
	 * Validates the answer against the captcha id
	 * @param captchaId
	 * @param answer
	 * @return
	 */
	public boolean validateCaptcha(String captchaId, String answer) throws InvalidInputException;
	
}
