package com.realtech.socialsurvey.core.services.authentication.impl;

import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.services.authentication.CaptchaValidaion;

@Component
public class ReCaptchaValidationImpl implements CaptchaValidaion {

	@Override
	public boolean isCaptchaValid(String remoteAddress, String challenge, String response) {
		// TODO Auto-generated method stub
		return false;
	}

}
