package com.realtech.socialsurvey.web.controller;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.authentication.AuthenticationService;
import com.realtech.socialsurvey.core.services.authentication.CaptchaValidation;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.common.JspResolver;

/**
 * Controller for testing jsp pages styles directly This is meant for UI testing
 */
@Controller
public class TestController {

	@Autowired
	private AuthenticationService authenticationService;
	@Autowired
	private SessionHelper sessionHelper;
	@Autowired
	private MessageUtils messageUtils;
	@Resource
	@Qualifier("nocaptcha")
	private CaptchaValidation captchaValidation;

	private static final Logger LOG = LoggerFactory.getLogger(TestController.class);

	@RequestMapping(value = "/jumptodashboard")
	public String jumpToDashboard(Model model, HttpServletRequest req, HttpServletResponse response) {
		LOG.info("Jumping to Dashboard with ");

		// TODO Fill active username and password for testing
		sessionHelper.loginOnRegistration("nishit@raremile.com", "");
		return JspResolver.LANDING;
	}

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

	@RequestMapping("/pro")
	public String form() {
		return "pro_lis";
	}

	@RequestMapping("/validat")
	public String validate(@RequestParam("recaptcha_challenge_field") String challangeField,
			@RequestParam("recaptcha_response_field") String responseField, ServletRequest servletRequest) {

		String remoteAddress = servletRequest.getRemoteAddr();
		try {
			captchaValidation.isCaptchaValid(remoteAddress, challangeField, responseField);
		}
		catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "test";
	}

	public static void main(String[] args) throws IOException {
		URL u = new URL("https://www.dotloop.com/my/api/v1_0/profile");
		HttpURLConnection huc = (HttpURLConnection) u.openConnection();
		huc.setRequestMethod("GET"); // OR huc.setRequestMethod ("HEAD");
		huc.setRequestProperty("Authorization", "Bearer 1234-5678-90123");
		huc.connect();
		int status = huc.getResponseCode();
		System.out.println(status);
	}
}