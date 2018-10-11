package com.realtech.socialsurvey.core.services.authentication.impl;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.realtech.socialsurvey.core.entities.Captcha;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.authentication.CaptchaValidation;

public class SimpleCaptchaValidation implements CaptchaValidation {

	private static final Logger LOG = LoggerFactory.getLogger(SimpleCaptchaValidation.class);

	private static final String[] CAPTCHA_OPTIONS_LIST = new String[] { "102b4213debb8e1e7f340b901667075a,3+9,12",
			"1144390c90172889010a485a522a59cb,5+4,9", "13b6ce8a305be83e4bf7eda67f63934c,12-8,4", "2f6ac2b7a33953766eac06ba8560765e,6-5,1",
			"5f907657d021e2f708cef94fd7c000b8,3+7,10" };

	// should be the list of keys in the same order mentioned in the list
	private static final String[] CAPTCHA_KEYS = new String[] { "102b4213debb8e1e7f340b901667075a", "1144390c90172889010a485a522a59cb",
			"13b6ce8a305be83e4bf7eda67f63934c", "2f6ac2b7a33953766eac06ba8560765e", "5f907657d021e2f708cef94fd7c000b8" };

	private static final Map<String, Captcha> CAPTCHA_OPTIONS_MAP;

	static {
		LOG.info("Mapping captcha options");
		CAPTCHA_OPTIONS_MAP = new HashMap<String, Captcha>();
		Captcha captcha = null;
		for (String captchaOption : CAPTCHA_OPTIONS_LIST) {
			String[] options = captchaOption.split(",");
			captcha = new Captcha();
			captcha.setCaptchaId(options[0]);
			captcha.setCaptchaQuestion(options[1]);
			captcha.setCaptchaAnswer(options[2]);
			CAPTCHA_OPTIONS_MAP.put(captcha.getCaptchaId(), captcha);
		}
	}

	// unimplemented method
	@Override
	public boolean isCaptchaValid(String remoteAddress, String challenge, String response) throws InvalidInputException {
		throw new InvalidInputException("Unimplemented");
	}

	/**
	 * Present a captcha option
	 */
	@Override
	public Captcha presentCaptchaQuestion(String previousCaptchaId) {
		LOG.info("Presenting captcha. Previous captcha id: " + previousCaptchaId);
		Captcha captcha = null;
		// pick a captcha randomly
		int captchaMapIndex = generateCaptchaIndex();
		LOG.debug("Fetch captcha at index :" + captchaMapIndex);
		// check if current captcha index is same as the previous one. If so, generate a new one.
		if (previousCaptchaId != null && !previousCaptchaId.isEmpty()) {
			if ((CAPTCHA_KEYS[captchaMapIndex].equals(previousCaptchaId))) {
				presentCaptchaQuestion(previousCaptchaId);
			}
			else {
				captcha = CAPTCHA_OPTIONS_MAP.get(CAPTCHA_KEYS[captchaMapIndex]);
			}
		}
		else {
			captcha = CAPTCHA_OPTIONS_MAP.get(CAPTCHA_KEYS[captchaMapIndex]);
		}
		return captcha;
	}

	@Override
	public boolean validateCaptcha(String captchaId, String answer) throws InvalidInputException {
		LOG.info("Validation captcha for id: " + captchaId + " against answer: " + answer);
		boolean isCaptchaValid = false;
		Captcha captcha = CAPTCHA_OPTIONS_MAP.get(captchaId);
		if (captcha == null) {
			LOG.error("Invalid captcha id: " + captchaId);
			throw new InvalidInputException("Invalid captcha id: " + captchaId);
		}
		if (answer.trim().equals(captcha.getCaptchaAnswer())) {
			isCaptchaValid = true;
		}
		else {
			isCaptchaValid = false;
		}
		return isCaptchaValid;
	}
	
	private int generateCaptchaIndex(){
		return (int) Math.floor(Math.random() * CAPTCHA_OPTIONS_MAP.size());
	}

	public static void main(String[] args) throws InvalidInputException {
		// EncryptionHelper().encryptAES(String.valueOf(System.currentTimeMillis()), ""));
		new SimpleCaptchaValidation().presentCaptchaQuestion("5f907657d021e2f708cef94fd7c000b8");
	}

}
