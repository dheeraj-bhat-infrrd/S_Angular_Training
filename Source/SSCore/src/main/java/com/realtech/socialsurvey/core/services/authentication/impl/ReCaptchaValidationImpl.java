package com.realtech.socialsurvey.core.services.authentication.impl;

import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.services.authentication.CaptchaValidation;

@Component
public class ReCaptchaValidationImpl implements CaptchaValidation {

	@Override
	public boolean isCaptchaValid(String remoteAddress, String challenge, String response) {
		// TODO Auto-generated method stub
		return false;
	}

}
