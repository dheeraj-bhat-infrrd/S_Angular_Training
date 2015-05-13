package com.realtech.socialsurvey.core.services.authentication.impl;

import java.io.IOException;
import java.util.Map;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.core.entities.Captcha;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.authentication.CaptchaValidation;

/**
 * Google captcha implementation
 *
 */
@Component("nocaptcha")
public class GoogleCaptchaValidation implements CaptchaValidation {

	private static final Logger LOG = LoggerFactory.getLogger(GoogleCaptchaValidation.class);
	
	private static final String GOOGLE_CAPTCHA_ENDPOINT="https://www.google.com/recaptcha/api/siteverify";
	
	@Override
	public boolean isCaptchaValid(String remoteAddress, String secret, String response) throws InvalidInputException {
		LOG.info("Validating google captcha");
		boolean status = false;
		if(remoteAddress == null || remoteAddress.isEmpty() || secret == null || secret.isEmpty() || response == null || response.isEmpty()){
			LOG.warn("Invalid input for captcha validation.");
			throw new InvalidInputException("Invalid input for captcha validation");
		}
		// Make a httppost request
		HttpClient httpclient = HttpClientBuilder.create().build();
		HttpPost httpPost = new HttpPost(GOOGLE_CAPTCHA_ENDPOINT+"?secret="+secret+"&response="+response+"&remoteip="+remoteAddress);
		String statusResponse;
		try {
			statusResponse = httpclient.execute(httpPost, new BasicResponseHandler());
			Map<String, Object> map = new Gson().fromJson(statusResponse, new TypeToken<Map<String, String>>() {}.getType());
			String successResponse = (String)map.get("success");
			if(successResponse.equals("true")){
				status = true;
			}else{
				status = false;
			}
		}
		catch (IOException e) {
			LOG.warn("IOException while validating captcha",e);
			status = false;
		}
		
		//GoogleCaptchaResponse captchaResponse = captchaValidator.validateGoogleCaptcha(secret, response, remoteAddress, 0);
		return status;
	}

	@Override
	public Captcha presentCaptchaQuestion(String previousCaptchaId) {
		return null;
	}

	@Override
	public boolean validateCaptcha(String captchaId, String answer) throws InvalidInputException {
		throw new InvalidInputException("Unimplemented");
	}


}
