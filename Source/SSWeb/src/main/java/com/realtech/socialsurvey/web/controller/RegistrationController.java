package com.realtech.socialsurvey.web.controller;

// JIRA : SS-13 by RM-06 : BOC

/**
 * Registration Controller Sends an invitation to the corporate admin
 */

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.User;
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

	/**
	 * JIRA:SS-19 BY RM02 Method to validate invitation form parameters and call service to invite
	 * user for registration
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
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
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while sending registration invite. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		return JspResolver.MESSAGE_HEADER;
	}

	/**
	 * JIRA:SS-26 BY RM02 Method to validate the url and present registration jsp with pre-populated
	 * user details
	 * 
	 * @param encryptedUrlParams
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/showregistrationpage")
	public String showRegistrationPage(@RequestParam("q") String encryptedUrlParams, HttpServletRequest request, Model model) {
		LOG.info("Method showRegistrationPage of Registration Controller called with encryptedUrl : " + encryptedUrlParams);
		try {
			LOG.debug("Calling registration service for validating registration url and extracting parameters from it");
			Map<String, String> urlParams = null;
			try {
				urlParams = registrationService.validateRegistrationUrl(encryptedUrlParams);
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.INVALID_REGISTRATION_INVITE, e);
			}
			if (urlParams == null || urlParams.isEmpty()) {
				throw new InvalidInputException("Url params are null or empty in showRegistrationPage");
			}
			model.addAttribute("firstname", urlParams.get("firstName"));
			model.addAttribute("lastname", urlParams.get("lastName"));
			model.addAttribute("emailid", urlParams.get("emailId"));

			LOG.debug("Validation of url completed. Service returning params to be prepopulated in registration page");

		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while showing registration page. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.LOGIN;
		}
		return JspResolver.REGISTRATION;
	}

	/**
	 * JIRA:SS-26 BY RM02 Method to validate registration form parameters and call service to add a
	 * new user in application
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String registerUser(Model model, HttpServletRequest request) {
		LOG.info("Method registerUser of Registration Controller called");

		String firstName = request.getParameter("firstname");
		String lastName = request.getParameter("lastname");
		String emailId = request.getParameter("emailid");
		String originalEmailId = request.getParameter("originalemailid");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String confirmPassword = request.getParameter("confirmpassword");

		try {
			/**
			 * Validate the parameters obtained from registration form
			 */
			validateRegistrationForm(firstName, lastName, emailId, username, password, confirmPassword);

			/**
			 * If emailId sent in the link and emailId entered by the user are same, register the
			 * user else send a registration invite on the changed emailId
			 */
			try {
				if (emailId.equals(originalEmailId)) {
					LOG.debug("Registering user with emailId : " + emailId);
					User user = registrationService.addCorporateAdmin(firstName, lastName, originalEmailId, username, confirmPassword);
					LOG.debug("Succesfully completed registration of user with emailId : " + emailId);

					LOG.debug("Adding newly registered user to session");
					HttpSession session = request.getSession(true);
					session.setAttribute(CommonConstants.USER_IN_SESSION, user);
					LOG.debug("Successfully added registered user to session");

				}
				/**
				 * Commenting the code as now emailId is non editable in registration
				 */
				/*else {
					LOG.debug("Sending registration invite link on the new emailId : " + emailId + " added by the user");
					registrationService.inviteCorporateToRegister(firstName, lastName, emailId);
					model.addAttribute("message", messageUtils.getDisplayMessage(DisplayMessageConstants.REGISTRATION_INVITE_SUCCESSFUL,
							DisplayMessageType.SUCCESS_MESSAGE));

					LOG.debug("Registration invite link on the new emailId : " + emailId + " sent successfully");
					return JspResolver.MESSAGE_HEADER;
				}*/

				// Set the success message
				model.addAttribute("message",
						messageUtils.getDisplayMessage(DisplayMessageConstants.USER_REGISTRATION_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE));

			}
			catch (InvalidInputException e) {
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.REGISTRATION_GENERAL_ERROR, e);
			}
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while registering user. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.MESSAGE_HEADER;
		}
		LOG.info("Method registerUser of Registration Controller finished");
		return JspResolver.COMPANY_INFORMATION;

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
	 * Method to validate form parameters of invitation form
	 * 
	 * @param firstName
	 * @param lastName
	 * @param emailId
	 * @throws InvalidInputException
	 */
	private void validateFormParameters(String firstName, String lastName, String emailId) throws InvalidInputException {
		LOG.debug("Validating invitation form parameters");
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

		// check if email Id isEmpty, null or whether it matches the regular
		// expression or not
		if (emailId == null || emailId.isEmpty() || !emailId.matches(EMAIL_REGEX)) {
			throw new InvalidInputException("Email address is invalid in registration", DisplayMessageConstants.INVALID_EMAILID);
		}
		LOG.debug("Invitation form parameters validated successfully");

	}

	/**
	 * JIRA:SS-26 BY RM02 Method to validate form parameters of registration form
	 * 
	 * @param firstName
	 * @param lastName
	 * @param emailId
	 * @param username
	 * @param password
	 * @param confirmPassword
	 * @throws InvalidInputException
	 */
	private void validateRegistrationForm(String firstName, String lastName, String emailId, String username, String password, String confirmPassword)
			throws InvalidInputException {
		LOG.debug("Validating registration form parameters");
		/**
		 * call the invitation form parameters validation as the form parameters and validation
		 * criteria are same
		 */
		validateFormParameters(firstName, lastName, emailId);

		if (username == null || username.isEmpty()) {
			throw new InvalidInputException("Username is not valid in registration", DisplayMessageConstants.INVALID_USERNAME);
		}
		if (password == null || password.isEmpty() || confirmPassword == null || confirmPassword.isEmpty()) {
			throw new InvalidInputException("Password is not valid in registration", DisplayMessageConstants.INVALID_PASSWORD);
		}
		if (!password.equals(confirmPassword)) {
			throw new InvalidInputException("Passwords do not match in registration", DisplayMessageConstants.PASSWORDS_MISMATCH);
		}
		LOG.debug("Registration form parameters validated successfully");
	}
}

// JIRA : SS-13 by RM-06 : EOC