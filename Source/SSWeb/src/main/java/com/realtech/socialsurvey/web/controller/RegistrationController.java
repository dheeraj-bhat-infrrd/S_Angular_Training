package com.realtech.socialsurvey.web.controller;

// JIRA : SS-13 by RM-06 : BOC

/**
 * Registration Controller Sends an invitation to the corporate admin
 */

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.authentication.CaptchaValidation;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.registration.RegistrationService;
import com.realtech.socialsurvey.web.common.JspResolver;

@Controller
public class RegistrationController {

	private static final Logger LOG = LoggerFactory.getLogger(RegistrationController.class);

	@Autowired
	CaptchaValidation captchaValidation;
	@Autowired
	private RegistrationService registrationService;

	@RequestMapping(value = "/invitation")
	public String initRegisterPage() {
		LOG.info("Registration Step 1");
		return JspResolver.INVITATION;
	}

	@RequestMapping(value = "/corporateinvite", method = RequestMethod.POST)
	public String inviteCorporate(Model model, HttpServletRequest request) {
		LOG.info("Sending invitation to corporate");
		LOG.debug("Validating form elements");

		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String emailId = request.getParameter("emailId");

		// validate request parameters from the form
		if (isFormParametersValid(firstName, lastName, emailId)) {
			LOG.debug("Valid parameters passed");
		}
		else {
			LOG.error("Invalid arguments passed in form");
			model.addAttribute("displaymessage", "Invalid arguments passed in form");
		}

		// validate captcha
		try {
			if (validateCaptcha(request)) {
				LOG.debug("Captcha validation successful");

				// continue with the invitation
				try {
					registrationService.inviteCorporateToRegister(firstName, lastName, emailId);
				}
				catch (InvalidInputException e) {
					LOG.error("InvalidInputException while inviting corporate to register", e);
				}
				catch (UndeliveredEmailException e) {
					LOG.error("UndeliveredEmailException while inviting corporate to register", e);
				}
				catch (NonFatalException e) {
					LOG.error("NonFatalException while inviting corporate to register", e);
				}
			}
			else {
				LOG.debug("Captcha validation failed");
				model.addAttribute("displaymessage", "Captcha Validation failed");
			}
		}
		catch (InvalidInputException e) {
			LOG.error("InvalidInputException while inviting corporate to register", e);
		}
		return JspResolver.CORPORATE_INVITATION;
	}

	/*
	 * Check if captcha is valid
	 */
	private boolean validateCaptcha(HttpServletRequest request) throws InvalidInputException {
		LOG.debug("Validating captcha informations");
		String remoteAddress = request.getRemoteAddr();
		String captchaChallenge = request.getParameter("recaptcha_challenge_field");
		String captchaResponse = request.getParameter("recaptcha_response_field");
		return captchaValidation.isCaptchaValid(remoteAddress, captchaChallenge, captchaResponse);
	}

	/*
	 * Check if input parameters from form are valid
	 */
	private boolean isFormParametersValid(String fName, String lName, String emailId) {

		String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
		String ALPHA_REGEX = "[a-zA-Z]+";

		// check if first name is null or empty and only contains alpahabets
		if (fName == null || fName.isEmpty() || !fName.matches(ALPHA_REGEX))
			return false;
		// check if first name is not null and not empty and only contains alpahabets
		if (!(lName != null && !lName.isEmpty() && lName.matches(ALPHA_REGEX)))
			return false;
		// check if email Id isEmpty, null or whether it matches the regular expression or not
		if (emailId == null || emailId.isEmpty() || !emailId.matches(EMAIL_REGEX))
			return false;

		return true;
	}
}

// JIRA : SS-13 by RM-06 : EOC
