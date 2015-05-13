package com.realtech.socialsurvey.web.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.VerticalsMaster;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.exception.UserAlreadyExistsException;
import com.realtech.socialsurvey.core.services.authentication.CaptchaValidation;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.common.JspResolver;

// JIRA : SS-13 by RM-06 : BOC
/**
 * Registration Controller Sends an invitation to the corporate admin
 */
@Controller
public class RegistrationController {

	private static final Logger LOG = LoggerFactory.getLogger(RegistrationController.class);

	@Resource
	@Qualifier("nocaptcha")
	private CaptchaValidation captchaValidation;
	@Autowired
	private UserManagementService userManagementService;
	@Autowired
	private MessageUtils messageUtils;
	@Autowired
	private SolrSearchService solrSearchService;
	@Autowired
	private SessionHelper sessionHelper;
	@Autowired
	private OrganizationManagementService organizationManagementService;
	@Autowired
	private EmailServices emailServices;

	@Value("${VALIDATE_CAPTCHA}")
	private String validateCaptcha;

	@Value("${APPLICATION_BASE_URL}")
	private String applicationBaseUrl;
	
	@Value("${CAPTCHA_SECRET}")
	private String captchaSecretKey;

	// JIRA - SS-536: Added for manual registration via invite
	@Autowired
	private URLGenerator urlGenerator;

	@RequestMapping(value = "/invitation")
	public String initInvitationPage(Model model) {
		LOG.info("Showing invitation page");
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

		try {
			LOG.debug("Validating form elements");
			validateFormParameters(firstName, lastName, emailId);
			LOG.debug("Form parameters validation passed for firstName: " + firstName + " lastName : " + lastName + " and emailID : " + emailId);

			// validate captcha
			// TODO remove comment when captcha validation is needed
			/*
			 * try { validateCaptcha(request); LOG.debug("Captcha validation successful"); } catch
			 * (InvalidInputException e) { throw new InvalidInputException(e.getMessage(),
			 * DisplayMessageConstants.INVALID_CAPTCHA, e); }
			 */
			// continue with the invitation
			try {
				LOG.debug("Calling service for sending the registration invitation");
				userManagementService.inviteCorporateToRegister(firstName, lastName, emailId, false);
				LOG.debug("Service for sending the registration invitation excecuted successfully");
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.REGISTRATION_INVITE_GENERAL_ERROR, e);
			}
			catch (UndeliveredEmailException e) {
				throw new UndeliveredEmailException(e.getMessage(), DisplayMessageConstants.REGISTRATION_INVITE_GENERAL_ERROR, e);
			}
			catch (UserAlreadyExistsException e) {
				throw new UserAlreadyExistsException(e.getMessage(), DisplayMessageConstants.EMAILID_ALREADY_TAKEN, e);
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
	 * user details when registration is done from invitation url
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
				urlParams = userManagementService.validateRegistrationUrl(encryptedUrlParams);
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.INVALID_REGISTRATION_INVITE, e);
			}

			if (urlParams == null || urlParams.isEmpty()) {
				throw new InvalidInputException("Url params are null or empty in showRegistrationPage");
			}
			model.addAttribute("firstname", urlParams.get(CommonConstants.FIRST_NAME));
			model.addAttribute("lastname", urlParams.get(CommonConstants.LAST_NAME));
			model.addAttribute("emailid", urlParams.get(CommonConstants.EMAIL_ID));
			model.addAttribute("isDirectRegistration", true);

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
	 * Method to show the registration page directly
	 * 
	 * @param model
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/registration")
	public String initDirectRegistration(Model model, HttpServletRequest request) {
		LOG.info("Method called for showing up the direct registration page");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String emailId = request.getParameter("emailId");

		try {
			LOG.debug("Validating form elements");
			validateFormParameters(firstName, lastName, emailId);
			LOG.debug("Form parameters validation passed for firstName: " + firstName + " lastName: " + lastName + " and emailID: " + emailId);
			// check if email id already exists
			if (userManagementService.userExists(emailId.trim())) {
				LOG.warn(emailId + " is already present");
				throw new UserAlreadyExistsException("Email address " + emailId + " already exists.");
			}
			if (validateCaptcha.equals(CommonConstants.YES_STRING)) {
				if (!captchaValidation.isCaptchaValid(request.getRemoteAddr(), captchaSecretKey, request.getParameter("g-recaptcha-response"))) {
					LOG.error("Captcha Validation failed!");
					throw new InvalidInputException("Captcha Validation failed!", DisplayMessageConstants.INVALID_CAPTCHA);
				}
				LOG.debug("Captcha validation complete!");
			}

			model.addAttribute("firstname", firstName);
			model.addAttribute("lastname", lastName);
			model.addAttribute("emailid", emailId);
			model.addAttribute("isDirectRegistration", true);
			// send verification mail and then redirect to index page
			LOG.debug("Calling service for sending the registration invitation");
			userManagementService.inviteCorporateToRegister(firstName, lastName, emailId, false);
			LOG.debug("Service for sending the registration invitation excecuted successfully");
			model.addAttribute("message",
					messageUtils.getDisplayMessage(DisplayMessageConstants.REGISTRATION_INVITE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE));
		}
		catch (UserAlreadyExistsException e) {
			model.addAttribute("message",
					messageUtils.getDisplayMessage(DisplayMessageConstants.USERNAME_ALREADY_TAKEN, DisplayMessageType.ERROR_MESSAGE));
			model.addAttribute("firstname", firstName);
			model.addAttribute("lastname", lastName);
			model.addAttribute("emailid", emailId);
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while showing registration page. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(DisplayMessageConstants.INVALID_CAPTCHA, DisplayMessageType.ERROR_MESSAGE));
			model.addAttribute("firstname", firstName);
			model.addAttribute("lastname", lastName);
			model.addAttribute("emailid", emailId);
		}
		return JspResolver.INDEX;
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
		String password = request.getParameter("password");
		String confirmPassword = request.getParameter("confirmpassword");
		String strIsDirectRegistration = request.getParameter("isDirectRegistration");

		try {
			boolean isDirectRegistration = false;
			if (strIsDirectRegistration != null && !strIsDirectRegistration.isEmpty()) {
				isDirectRegistration = Boolean.parseBoolean(strIsDirectRegistration);
			}
			/**
			 * Validate the parameters obtained from registration form
			 */
			validateRegistrationForm(firstName, lastName, emailId, password, confirmPassword);

			/**
			 * If emailId sent in the link and emailId entered by the user are same, register the
			 * user else send a registration invite on the changed emailId
			 */
			try {
				LOG.debug("Registering user with emailId : " + emailId);
				User user = userManagementService
						.addCorporateAdminAndUpdateStage(firstName, lastName, emailId, confirmPassword, isDirectRegistration);
				LOG.debug("Succesfully completed registration of user with emailId : " + emailId);

				LOG.debug("Adding newly added user {} to mongo", user.getFirstName());
				userManagementService.insertAgentSettings(user);
				LOG.debug("Added newly added user {} to mongo", user.getFirstName());

				LOG.debug("Adding newly added user {} to solr", user.getFirstName());
				solrSearchService.addUserToSolr(user);
				LOG.debug("Added newly added user {} to solr", user.getFirstName());

				LOG.debug("Adding newly registered user to principal session");
				sessionHelper.loginOnRegistration(emailId, password);
				LOG.debug("Successfully added registered user to principal session");

				// send verification mail
				// no need to send verification mail as the new sign up path doesn't need it
				/**
				 * if (isDirectRegistration) {
				 * LOG.debug("Calling method for sending verification link for user : " +
				 * user.getUserId()); userManagementService.sendVerificationLink(user); }
				 */
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.REGISTRATION_GENERAL_ERROR, e);
			}
			catch (UserAlreadyExistsException e) {
				throw new UserAlreadyExistsException(e.getMessage(), DisplayMessageConstants.USERNAME_ALREADY_TAKEN, e);
			}
			catch (UndeliveredEmailException e) {
				throw new UndeliveredEmailException(e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e);
			}

			List<VerticalsMaster> verticalsMasters = null;
			try {
				verticalsMasters = organizationManagementService.getAllVerticalsMaster();
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException("Invalid Input exception occured in method getAllVerticalsMaster()",
						DisplayMessageConstants.GENERAL_ERROR, e);
			}
			model.addAttribute("verticals", verticalsMasters);
			// JIRA - SS-536
			model.addAttribute("isDirectRegistration", strIsDirectRegistration);
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while registering user. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			/**
			 * Adding the attributes required in page after reloading
			 */
			model.addAttribute("firstname", firstName);
			model.addAttribute("lastname", lastName);
			model.addAttribute("emailid", originalEmailId);
			model.addAttribute("isDirectRegistration", strIsDirectRegistration);
			return JspResolver.REGISTRATION;
		}
		LOG.info("Method registerUser of Registration Controller finished");
		return JspResolver.COMPANY_INFORMATION;
	}

	/**
	 * Method to verify an account
	 * 
	 * @param encryptedUrlParams
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/verification")
	public String verifyAccount(@RequestParam("q") String encryptedUrlParams, HttpServletRequest request, Model model) {
		LOG.info("Method to verify account called");
		try {
			userManagementService.verifyAccount(encryptedUrlParams);
			model.addAttribute("message",
					messageUtils.getDisplayMessage(DisplayMessageConstants.EMAIL_VERIFICATION_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE));
		}
		catch (InvalidInputException e) {
			LOG.error("InvalidInputException while verifying account. Reason : " + e.getMessage(), e);
			model.addAttribute("message",
					messageUtils.getDisplayMessage(DisplayMessageConstants.INVALID_VERIFICATION_URL, DisplayMessageType.ERROR_MESSAGE));
		}
		catch (SolrException e) {
			LOG.error("SolrException while verifying account. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage("SolrException", DisplayMessageType.ERROR_MESSAGE));
		}
		LOG.info("Method to verify account finished");
		return JspResolver.LOGIN;

	}

	// JIRA - SS-536: Added for manual registration via invite
	@RequestMapping(value = "/invitetoregister")
	public String initManualRegistration(@RequestParam("q") String encryptedUrlParams, HttpServletRequest request, Model model) {
		LOG.info("Manual invitation for registration");
		// decrypt the url
		String creatorEmailId = null;
		String emailId = null;
		try {
			Map<String, String> urlParams = urlGenerator.decryptParameters(encryptedUrlParams);
			if (urlParams.get(CommonConstants.FIRST_NAME) != null) {
				model.addAttribute("firstname", URLDecoder.decode(urlParams.get(CommonConstants.FIRST_NAME), "UTF-8"));
			}
			else {
				throw new InvalidInputException("First name is not present");
			}
			if (urlParams.get(CommonConstants.LAST_NAME) != null) {
				model.addAttribute("lastname", URLDecoder.decode(urlParams.get(CommonConstants.LAST_NAME), "UTF-8"));
			}
			else {
				model.addAttribute("lastname", "");
			}
			if (urlParams.get(CommonConstants.EMAIL_ID) != null) {
				emailId = URLDecoder.decode(urlParams.get(CommonConstants.EMAIL_ID), "UTF-8");
				model.addAttribute("emailid", emailId);
			}
			else {
				throw new InvalidInputException("Email id is not present");
			}
			if (urlParams.get(CommonConstants.ACCOUNT_CRETOR_EMAIL_ID) != null) {
				creatorEmailId = URLDecoder.decode(urlParams.get(CommonConstants.ACCOUNT_CRETOR_EMAIL_ID), "UTF-8");
				model.addAttribute("creatorEmailId", creatorEmailId);
			}
			else {
				throw new InvalidInputException("Creator email id is not present");
			}
			if (urlParams.get(CommonConstants.API_KEY_FROM_URL) != null) {
				if (!userManagementService.isValidApiKey(creatorEmailId, urlParams.get(CommonConstants.API_KEY_FROM_URL))) {
					throw new InvalidInputException("Could not authenticate the API key");
				}
			}
			else {
				throw new InvalidInputException("No API Key present");
			}
			model.addAttribute("isDirectRegistration", false);
			// check if the email id exists.
			if (userManagementService.userExists(emailId)) {
				return JspResolver.LOGIN;
			}

		}
		catch (InvalidInputException | UnsupportedEncodingException | NoRecordsFetchedException e) {
			LOG.error("Exception while inviting user for manual registration", e);
			model.addAttribute("message",
					messageUtils.getDisplayMessage(DisplayMessageConstants.INVALID_VERIFICATION_URL, DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.NOT_FOUND_PAGE;
		}
		return JspResolver.REGISTRATION;
	}

	// JIRA - SS-536: Added for manual registration via invite
	@ResponseBody
	@RequestMapping(value = "/generateregistrationurl")
	public String geerateRegistrationUrlForManualCompanyCreation(@RequestParam("firstName") String firstName,
			@RequestParam("lastName") String lastName, @RequestParam("emailId") String emailId,
			@RequestParam("creatorEmailId") String creatorEmailId, @RequestParam("api_key") String apiKey) {
		LOG.info("Creating invitation url for " + firstName + " " + lastName + " and email " + emailId);
		String result = null;
		try {
			// generate the url
			Map<String, String> params = new HashMap<String, String>();
			params.put(CommonConstants.FIRST_NAME, URLEncoder.encode(firstName, "UTF-8"));
			if (lastName != null) {
				params.put(CommonConstants.LAST_NAME, URLEncoder.encode(lastName, "UTF-8"));
			}
			params.put(CommonConstants.EMAIL_ID, URLEncoder.encode(emailId, "UTF-8"));
			params.put(CommonConstants.ACCOUNT_CRETOR_EMAIL_ID, URLEncoder.encode(creatorEmailId, "UTF-8"));
			params.put(CommonConstants.API_KEY_FROM_URL, apiKey);
			LOG.debug("Validating api key");
			if (!userManagementService.isValidApiKey(creatorEmailId, apiKey)) {
				LOG.warn("Invalid api key");
				throw new InvalidInputException("Could not authenticate the API key.");
			}

			String url = urlGenerator.generateUrl(params, applicationBaseUrl + CommonConstants.MANUAL_REGISTRATION);
			emailServices.sendManualRegistrationLink(creatorEmailId, firstName, lastName, url);
			result = "Done";
		}
		catch (InvalidInputException | UndeliveredEmailException | UnsupportedEncodingException | NoRecordsFetchedException e) {
			LOG.error("Exception caught while sending mail to generating registration url", e);
			result = "Something went wrong. " + e.getMessage();
		}
		return result;
	}

	/**
	 * Check if captcha is valid
	 * 
	 * @param request
	 * @throws InvalidInputException
	 */
	// TODO remove unused when captcha is uncommented
	@SuppressWarnings("unused")
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

		// check if first name is null or empty and only contains alphabets
		if (firstName == null || firstName.isEmpty() || !firstName.matches(CommonConstants.FIRST_NAME_REGEX)) {
			throw new InvalidInputException("Firstname is invalid in registration", DisplayMessageConstants.INVALID_FIRSTNAME);
		}

		// check if last name only contains alphabets
		if (lastName != null && !lastName.isEmpty()) {
			if (!(lastName.matches(CommonConstants.LAST_NAME_REGEX))) {
				throw new InvalidInputException("Last name is invalid in registration", DisplayMessageConstants.INVALID_LASTNAME);
			}
		}

		// check if email Id isEmpty, null or whether it matches the regular expression or not
		if (emailId == null || emailId.isEmpty() || !emailId.matches(CommonConstants.EMAIL_REGEX)) {
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
	private void validateRegistrationForm(String firstName, String lastName, String emailId, String password, String confirmPassword)
			throws InvalidInputException {
		LOG.debug("Validating registration form parameters");

		/**
		 * call the invitation form parameters validation as the form parameters and validation
		 * criteria are same
		 */
		validateFormParameters(firstName, lastName, emailId);

		if (password == null || password.isEmpty() || password.length() < CommonConstants.PASSWORD_LENGTH || confirmPassword == null
				|| confirmPassword.isEmpty()) {
			throw new InvalidInputException("Password is not valid in registration", DisplayMessageConstants.INVALID_PASSWORD);
		}
		if (!password.equals(confirmPassword)) {
			throw new InvalidInputException("Passwords do not match in registration", DisplayMessageConstants.PASSWORDS_MISMATCH);
		}
		LOG.debug("Registration form parameters validated successfully");
	}
}

// JIRA : SS-13 by RM-06 : EOC