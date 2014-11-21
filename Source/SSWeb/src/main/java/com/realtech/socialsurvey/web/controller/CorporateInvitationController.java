package com.realtech.socialsurvey.web.controller;

/**
 * Sends an invitation to the corporate admin
 */

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.authentication.CaptchaValidation;
import com.realtech.socialsurvey.core.services.registration.RegistrationService;
import com.realtech.socialsurvey.web.common.JspResolver;

@Controller
public class CorporateInvitationController {

	private static final Logger LOG = LoggerFactory.getLogger(CorporateInvitationController.class);
	
	@Autowired
	CaptchaValidation captchaValidation;
	@Autowired
	private RegistrationService registrationService;
	
	@RequestMapping(value="/corporateinvite")
	public String inviteCorporate(Model model, HttpServletRequest request) {
		LOG.info("Sending invitation to corporate");
		LOG.debug("Validating form elements");

		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String emailId = request.getParameter("emailId");
		// TODO: validate request parameters from the form
		
		// validate captcha
		if(validateCaptcha(request)){
			LOG.debug("Captcha validation successful");
			// continue with the invitation
			try {
				registrationService.inviteCorporateToRegister(firstName, lastName, emailId);
			}
			catch (InvalidInputException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			LOG.debug("Captcha validation failed");
			model.addAttribute("displaymessage", "Get value from constants");
		}
		return JspResolver.CORPORATE_INVITATION;
	}
	
	private boolean validateCaptcha(HttpServletRequest request){
		LOG.debug("Validating captcha informations");
		String remoteAddress = request.getRemoteAddr();
		String captchaChallenge = request.getParameter("recaptcha_challenge_field");
		String captchaResponse = request.getParameter("recaptcha_response_field");
		return captchaValidation.isCaptchaValid(remoteAddress, captchaChallenge, captchaResponse);
	}
}
