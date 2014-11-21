package com.realtech.socialsurvey.core.services.authentication.impl;

import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.authentication.CaptchaValidation;

@Component
public class ReCaptchaValidationImpl implements CaptchaValidation {

	private static final Logger LOG = LoggerFactory.getLogger(CaptchaValidation.class);

	@Autowired
	ReCaptcha reCaptcha;

	@Override
	public boolean isCaptchaValid(String remoteAddress, String challenge, String response) throws InvalidInputException {

		LOG.info("Checking if Captch is Valid for remoteAddress : " + remoteAddress + "\t challenge : " + challenge + "\t Response : " + response);

		if (remoteAddress == null || remoteAddress.isEmpty()) {
			throw new InvalidInputException("Remote address can not be null or empty");
		}
		if (challenge == null || challenge.isEmpty()) {
			throw new InvalidInputException("challenge field can not be null or empty");
		}
		if (response == null || response.isEmpty()) {
			throw new InvalidInputException("response field can not be null or empty");
		}
		ReCaptchaResponse captchaResponse = reCaptcha.checkAnswer(remoteAddress, challenge, response);
		boolean isCaptchaValid = captchaResponse.isValid();

		LOG.debug("isCaptcha valid " + isCaptchaValid);

		return isCaptchaValid;
	}

}
