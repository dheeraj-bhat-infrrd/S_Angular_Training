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
import org.springframework.web.bind.annotation.ResponseBody;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.authentication.CaptchaValidation;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.registration.RegistrationService;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.common.JspResolver;

@Controller
public class RegistrationController {

	private static final Logger LOG = LoggerFactory.getLogger(RegistrationController.class);

	@Autowired
	private CaptchaValidation captchaValidation;
	@Autowired
	private RegistrationService registrationService;
	@Autowired
	private MessageUtils messageUtils;

	@RequestMapping(value = "/invitation")
	public String initRegisterPage() {
		LOG.info("Registration Step 1");
		return JspResolver.INVITATION;
	}

	@RequestMapping(value = "/corporateinvite", method = RequestMethod.POST)
	public String inviteCorporate(Model model, HttpServletRequest request) {
		LOG.info("Sending invitation to corporate");

		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String emailId = request.getParameter("emailId");

		// validate request parameters from the form
		try {
			LOG.debug("Validating form elements");
			validateFormParameters(firstName, lastName, emailId);
			LOG.debug("Form parameters validation passed for firstName: " + firstName + " lastName : " + lastName + " and emailID : " + emailId);

			// validate captcha
			try {
				validateCaptcha(request);
				LOG.debug("Captcha validation successful");
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.INVALID_CAPTCHA, e);
			}
			// continue with the invitation
			try {
				LOG.debug("Calling service for sending the registration invitation");
				registrationService.inviteCorporateToRegister(firstName, lastName, emailId);
				LOG.debug("Service for sending the registration invitation excecuted successfully");
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.REGISTRATION_INVITE_GENERAL_ERROR, e);
			}
			catch (UndeliveredEmailException e) {
				throw new UndeliveredEmailException(e.getMessage(), DisplayMessageConstants.REGISTRATION_INVITE_GENERAL_ERROR, e);
			}

			LOG.info("Invitation to corporate for registration completed successfully");
			model.addAttribute("message",
					messageUtils.getDisplayMessage(DisplayMessageConstants.REGISTRATION_INVITE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE));
			//throw new FatalException("testing fatal exception");
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while sending registration invite. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		return JspResolver.MESSAGE_HEADER;
	}

	/**
	 * Check if captcha is valid
	 * 
	 * @param request
	 * @throws InvalidInputException
	 */
	private void validateCaptcha(HttpServletRequest request) throws InvalidInputException {
		LOG.debug("Validating captcha information");

		boolean isCaptchaValid = false;
		String remoteAddress = request.getRemoteAddr();
		String captchaChallenge = request.getParameter("recaptcha_challenge_field");
		String captchaResponse = request.getParameter("recaptcha_response_field");
		isCaptchaValid = captchaValidation.isCaptchaValid(remoteAddress, captchaChallenge, captchaResponse);

		/**
		 * if captcha code entered by user is not valid, throw invalid input exception
		 */
		if (!isCaptchaValid) {
			throw new InvalidInputException("Captcha is not valid");
		}
	}

	/**
	 * Check if input parameters from form are valid
	 * 
	 * @param firstName
	 * @param lastName
	 * @param emailId
	 * @throws InvalidInputException
	 */
	private void validateFormParameters(String firstName, String lastName, String emailId) throws InvalidInputException {

		String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
		String ALPHA_REGEX = "[a-zA-Z]+";

		// check if first name is null or empty and only contains alphabets
		if (firstName == null || firstName.isEmpty() || !firstName.matches(ALPHA_REGEX)) {
			throw new InvalidInputException("Firstname is invalid in registration", DisplayMessageConstants.INVALID_FIRSTNAME);
		}

		// check if last name only contains alphabets
		if (lastName != null && !lastName.isEmpty()) {
			if (!(lastName.matches(ALPHA_REGEX))) {
				throw new InvalidInputException("Last name is invalid in registration", DisplayMessageConstants.INVALID_LASTNAME);
			}
		}

		// check if email Id isEmpty, null or whether it matches the regular expression or not
		if (emailId == null || emailId.isEmpty() || !emailId.matches(EMAIL_REGEX)) {
			throw new InvalidInputException("Email address is invalid in registration", DisplayMessageConstants.INVALID_EMAILID);
		}

	}
}

// JIRA : SS-13 by RM-06 : EOC