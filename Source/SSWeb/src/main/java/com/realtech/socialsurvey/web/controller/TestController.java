package com.realtech.socialsurvey.web.controller;

import javax.servlet.ServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.authentication.CaptchaValidation;

@Controller
public class TestController {

	@Autowired
	private CaptchaValidation captchaValidation;

	@RequestMapping("/test")
	public String test() {

		return "test";
	}

	@RequestMapping("/Invitation")
	public String start() {

		return "invitation";
	}

	@RequestMapping("/Register_User")
	public String registerStepTwo() {

		return "registerUser";
	}

	@RequestMapping("/Company_Info")
	public String registerStepThree() {

		return "companyInfo";
	}

	@RequestMapping("/form")
	public String form() {

		return "form";
	}

	@RequestMapping("/validat")
	public String validate(@RequestParam("recaptcha_challenge_field") String challangeField,
			@RequestParam("recaptcha_response_field") String responseField, ServletRequest servletRequest) {

		boolean isCaptchaValid = false;
		String remoteAddress = servletRequest.getRemoteAddr();
		
		try {
			isCaptchaValid = captchaValidation.isCaptchaValid(remoteAddress, challangeField, responseField);
		}
		catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "test";
	}
}
