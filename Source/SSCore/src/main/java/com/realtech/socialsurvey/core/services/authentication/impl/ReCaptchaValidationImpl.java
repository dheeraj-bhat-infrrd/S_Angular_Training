//JIRA: SS-12: Captcha Validation implementation: RM06: BOC
package com.realtech.socialsurvey.core.services.authentication.impl;

import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.entities.Captcha;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.authentication.CaptchaValidation;

@Component("recaptcha")
public class ReCaptchaValidationImpl implements CaptchaValidation {

	private static final Logger LOG = LoggerFactory.getLogger(CaptchaValidation.class);

	@Autowired
	ReCaptcha reCaptcha;

	@Override
	public boolean isCaptchaValid(String remoteAddress, String challenge, String response) throws InvalidInputException {

		LOG.info("Checking if Captch is Valid for remoteAddress : " + remoteAddress + "\t challenge : " + challenge + "\t Response : " + response);

		if (remoteAddress == null || remoteAddress.isEmpty()) {
			LOG.error("Invalid remote address passed");
			throw new InvalidInputException("Remote address can not be null or empty");
		}
		if (challenge == null || challenge.isEmpty()) {
			LOG.error("Invalid challenge passed");
			throw new InvalidInputException("challenge field can not be null or empty");
		}
		if (response == null || response.isEmpty()) {
			LOG.error("Invalid response passed");
			throw new InvalidInputException("response field can not be null or empty");
		}
		boolean isCaptchaValid = validateCaptcha(remoteAddress, challenge, response);

		LOG.debug("isCaptcha valid " + isCaptchaValid);

		return isCaptchaValid;
	}
	
	// validates the captcha
	private boolean validateCaptcha(String remoteAddress, String challenge, String response){
		LOG.debug("Validationg captcha for remote address: "+remoteAddress+"\t challenge: "+challenge+"\t response: "+response);
		ReCaptchaResponse captchaResponse = reCaptcha.checkAnswer(remoteAddress, challenge, response);
		return captchaResponse.isValid();
	}

	// null implementation
	@Override
	public Captcha presentCaptchaQuestion(String previousCaptchaId) {
		return null;
	}

	// null implementation
	@Override
	public boolean validateCaptcha(String captchaId, String answer) throws InvalidInputException{
		throw new InvalidInputException("Unimplemented");
	}

}

//JIRA: SS-12: Captcha Validation implementation: RM06: EOC
