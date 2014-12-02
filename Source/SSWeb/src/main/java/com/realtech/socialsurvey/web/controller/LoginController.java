package com.realtech.socialsurvey.web.controller;

// JIRA SS-21 : by RM-06 : BOC

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.authentication.AuthenticationService;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.common.JspResolver;

@Controller
public class LoginController {

	private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	private AuthenticationService authenticationService;
	@Autowired
	private MessageUtils messageUtils;
	@Autowired
	private URLGenerator urlGenerator;

	@RequestMapping(value = "/login")
	public String initLoginPage() {
		LOG.info("Login Page started");
		return JspResolver.LOGIN;
	}

	@RequestMapping(value = "/forgotPassword")
	public String initForgotPassword() {
		LOG.info("Forgot Password Page started");
		return JspResolver.FORGOT_PASSWORD;
	}

	@RequestMapping(value = "/userlogin", method = RequestMethod.POST)
	public String login(Model model, HttpServletRequest request, HttpServletResponse response) {

		String loginName = request.getParameter("loginName");
		String password = request.getParameter("password");
		LOG.info("User login with user Id :" + loginName);
		User user = null;
		List<UserProfile> userProfiles = null;

		try {
			LOG.debug("Validation login form parameters");
			// check if form parameters valid
			verifyLoginFormParameters(loginName, password);

			try {
				user = authenticationService.getUserWithLoginName(loginName);
			}
			catch (InvalidInputException e) {
				LOG.error("Invalid Input exception in fetching User. Reason " + e.getMessage(), e);
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e);
			}
			LOG.debug("Check if company status active");
			// check if user company active
			if (user.getCompany().getStatus() == CommonConstants.STATUS_INACTIVE) {
				throw new InvalidInputException("Company is inactive", DisplayMessageConstants.COMPANY_INACTIVE);
			}
			// check if User active
			LOG.debug("Check if user status active");
			if (user.getStatus() == CommonConstants.STATUS_INACTIVE) {
				throw new InvalidInputException("User not active", DisplayMessageConstants.USER_INACTIVE);
			}

			// Authenticate user
			try {
				authenticationService.validateUser(user, password);
				HttpSession session = request.getSession(true);
				session.setAttribute(CommonConstants.USER_IN_SESSION, user);
			}
			catch (InvalidInputException e) {
				LOG.error("Invalid Input exception in validating User. Reason " + e.getMessage(), e);
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e);
			}

			// Check if user Company Profile complete, if company registration not done redirect
			// to company registration page
			LOG.debug("Check if company profile registration complete");
			if (user.getCompany().getIsRegistrationComplete() != CommonConstants.PROCESS_COMPLETE) {
				// redirect to company information page
				LOG.debug("Company profile not complete, redirecting to company information page");
				return JspResolver.COMPANY_INFORMATION;
			}
			else {
				// check if at least one of the user profiles are complete
				LOG.debug("Company profile complete, check any of the user profiles is entered");
				if (user.getIsAtleastOneUserprofileComplete() != CommonConstants.PROCESS_COMPLETE) {
					// redirect user to complete the top priority profile
					// Priority Company->region->branch->agent
					LOG.debug("None of the user profiles are complete , Redirect to top priority profile first");
					// fetch user profiles
					userProfiles = authenticationService.getUserProfileForUser(user);
				}
			}
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while logging in. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.MESSAGE_HEADER;
		}

		return JspResolver.DASHBOARD;
	}

	@RequestMapping(value = "/companyinformation")
	public String initCompanyInformationPage() {
		return JspResolver.COMPANY_INFORMATION;
	}

	/**
	 * Controller method to send reset password link to the user email ID
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/sendresetpasswordlink", method = RequestMethod.POST)
	public String sendResetPasswordLink(Model model, HttpServletRequest request) {
		LOG.info("Send password reset link to User");
		String emailId = request.getParameter("emailId");

		User user = null;
		// check if form parameters passed are null
		try {
			if (emailId == null || emailId.isEmpty()) {
				LOG.error("Emaild passed can not be null or empty");
				throw new InvalidInputException("Emaild passed can not be null or empty", DisplayMessageConstants.INVALID_EMAILID);
			}
			try {
				// verify if the user exists with the registered emailId
				user = authenticationService.verifyRegisteredUser(emailId);
			}
			catch (InvalidInputException e) {
				LOG.error("Invalid Input exception in verifying registered user. Reason " + e.getMessage(), e);
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e);
			}
			// Send reset password link
			try {
				authenticationService.sendResetPasswordLink(emailId, user.getDisplayName());
			}
			catch (InvalidInputException e) {
				LOG.error("Invalid Input exception in sending reset password link. Reason " + e.getMessage(), e);
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e);
			}

		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while sending the reset password link. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.MESSAGE_HEADER;
		}

		return JspResolver.LOGIN;
	}

	/**
	 * Controller method to display the reset password page
	 */
	@RequestMapping(value = "/resetpassword")
	public String showResetPasswordPage(@RequestParam("q") String encryptedUrlParams) {
		LOG.info("Forgot Password Page started with encrypter url : " + encryptedUrlParams);
		return JspResolver.RESET_PASSWORD;
	}

	/**
	 * Controller method to set a new password from the reset password link
	 */
	@RequestMapping(value = "/setnewpassword", method = RequestMethod.POST)
	public String resetPassword(Model model, HttpServletRequest request) {
		LOG.info("Reset the user password");

		String emailId = request.getParameter("emailId");
		String password = request.getParameter("password");
		String confirmPassword = request.getParameter("confirmPassword");

		Map<String, String> urlParams = null;
		String encryptedUrlParameters;
		encryptedUrlParameters = request.getParameter("q");

		User user = null;

		try {

			// Checking if any of the form parameters are null or empty
			validateResetPasswordFormParameters(emailId, password, confirmPassword);

			// Decrypte Url parameters
			try {
				urlParams = urlGenerator.decryptParameters(encryptedUrlParameters);
			}
			catch (InvalidInputException e) {
				LOG.error("Invalid Input exception in decrypting Url. Reason " + e.getMessage(), e);
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e);
			}

			// check if email ID entered matches with the one in the encrypted url
			if (!urlParams.get("emailId").equals(emailId)) {
				LOG.error("Invalid Input exception. Reason emailId entered does not match with the one to which the mail was sent");
				throw new InvalidInputException("Invalid Input exception", DisplayMessageConstants.INVALID_EMAILID);
			}

			// update user's password
			try {
				// fetch user object with email Id
				user = authenticationService.getUserWithEmailId(emailId);
			}
			catch (InvalidInputException e) {
				LOG.error("Invalid Input exception in fetching user object. Reason " + e.getMessage(), e);
				throw new InvalidInputException(e.getMessage(), e.getErrorCode(), e);
			}
			try {
				// change user's password
				authenticationService.changePassword(user, password);
			}
			catch (InvalidInputException e) {
				LOG.error("Invalid Input exception in changing the user's password. Reason " + e.getMessage(), e);
				throw new InvalidInputException(e.getMessage(), e.getErrorCode(), e);
			}

		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while setting new Password. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.MESSAGE_HEADER;
		}

		return JspResolver.LOGIN;
	}

	/**
	 * Verify the login Form Parameters
	 * 
	 * @param loginName
	 * @param password
	 * @throws InvalidInputException
	 */
	private void verifyLoginFormParameters(String loginName, String password) throws InvalidInputException {
		if (loginName == null || loginName.isEmpty()) {
			throw new InvalidInputException("User name passed can not be null", DisplayMessageConstants.INVALID_USERNAME);
		}
		if (password == null || password.isEmpty()) {
			throw new InvalidInputException("Password passed can not be null");
		}
	}

	/**
	 * validate reset form parameters
	 * 
	 * @param emailId
	 * @param password
	 * @param confirmPassword
	 * @throws InvalidInputException
	 */
	private void validateResetPasswordFormParameters(String emailId, String password, String confirmPassword) throws InvalidInputException {
		if (emailId == null || emailId.isEmpty()) {
			LOG.error("Emaild passed can not be null or empty");
			throw new InvalidInputException("Emaild passed can not be null or empty", DisplayMessageConstants.INVALID_EMAILID);
		}
		if (password == null || password.isEmpty()) {
			LOG.error("Password can not be null or empty");
			throw new InvalidInputException("Password can not be null or empty", DisplayMessageConstants.INVALID_PASSWORD);
		}
		if (confirmPassword == null || confirmPassword.isEmpty()) {
			LOG.error("Confirm Password can not be null or empty");
			throw new InvalidInputException("Confirm Password can not be null or empty", DisplayMessageConstants.INVALID_PASSWORD);
		}

		// check if password and confirm password field match
		if (!password.equals(confirmPassword)) {
			LOG.error("Password and confirm password fields do not match");
			throw new InvalidInputException("Password and confirm password fields do not match", DisplayMessageConstants.PASSWORDS_MISMATCH);
		}
	}

}

// JIRA SS-21 : by RM-06 : EOC