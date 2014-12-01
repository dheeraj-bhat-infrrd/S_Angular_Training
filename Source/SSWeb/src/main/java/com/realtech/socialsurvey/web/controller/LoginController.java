package com.realtech.socialsurvey.web.controller;

// JIRA SS-21 : by RM-06 : BOC

import java.util.ArrayList;
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

		HttpSession session = request.getSession(true);

		User user = null;
		List<UserProfile> userProfiles = new ArrayList<>();

		try {
			LOG.debug("Validation login form parameters");
			// check if form parameters valid
			if (loginName == null || loginName.isEmpty()) {
				LOG.error("User Id can not be null or empty : ");
				throw new InvalidInputException("User name passed can not be null");
			}
			if (password == null || password.isEmpty()) {
				LOG.error("Passwrod can not be null or empty : ");
				throw new InvalidInputException("Password passed can not be null");
			}
			try {
				user = authenticationService.getUserObjWithLoginName(loginName);
				LOG.debug("Check if company status active");
				// check if user company active
				if (user.getCompany().getStatus() == CommonConstants.STATUS_INACTIVE)
					throw new InvalidInputException("Company is inactive. Reason : " + DisplayMessageConstants.COMPANY_INACTIVE);

				// check if User active
				LOG.debug("Check if user status active");
				if (user.getStatus() == CommonConstants.STATUS_INACTIVE)
					throw new InvalidInputException("User not active. Reason : " + DisplayMessageConstants.USER_INACTIVE);

				// Authenticate user
				authenticationService.validateUser(user, password);
				session.setAttribute("user", user);

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
			catch (InvalidInputException e) {
				LOG.error("Invalid Input exception in validating User. Reason " + e.getMessage(), e);
				throw new InvalidInputException(e.getMessage(), e.getErrorCode(), e);
			}
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while logging in. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}

		return JspResolver.DASHBOARD;
	}

	@RequestMapping(value = "/companyinformation")
	public String initCompanyInformationPage() {
		return JspResolver.COMPANY_INFORMATION;
	}

	@RequestMapping(value = "/sendresetpasswordlink", method = RequestMethod.POST)
	public String resetPasswordLink(Model model, HttpServletRequest request) {
		LOG.info("Send password reset link to User");
		String emailId = request.getParameter("emailId");

		User user = null;
		// check if form parameters passed are null
		try {
			if (emailId == null || emailId.isEmpty()) {
				LOG.error("Emaild passed can not be null or empty");
				throw new InvalidInputException("Emaild passed can not be null or empty");
			}
			// verify if the user exists with the registered emailId
			user = authenticationService.verifyRegisteredUser(emailId);

			// Send reset password link
			authenticationService.sendResetPasswordLink(emailId, user.getDisplayName());

		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while logging in. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}

		return JspResolver.LOGIN;
	}

	@RequestMapping(value = "/resetpassword")
	public String showResetPasswordPage(@RequestParam("q") String encryptedUrlParams, HttpServletRequest request, Model model) {
		LOG.info("Forgot Password Page started with encrypter url : " + encryptedUrlParams);

		/*
		 * Map<String, String> urlParams = null; try { try { // Decrypt url parameters urlParams =
		 * urlGenerator.decryptParameters(encryptedUrlParams); } catch (InvalidInputException e) {
		 * LOG.error("Invalid Input exception in validating decrypting Url. Reason " +
		 * e.getMessage(), e); throw new InvalidInputException(e.getMessage(), e.getErrorCode(), e);
		 * } } catch (NonFatalException e) {
		 * LOG.error("NonFatalException in reset password page. Reason : " + e.getMessage(), e);
		 * model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(),
		 * DisplayMessageType.ERROR_MESSAGE)); }
		 */
		return JspResolver.RESET_PASSWORD;
	}

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

		// Checking if any of the form parameters are null or empty
		try {
			if (emailId == null || emailId.isEmpty()) {
				LOG.error("Emaild passed can not be null or empty");
				throw new InvalidInputException("Emaild passed can not be null or empty");
			}
			if (password == null || password.isEmpty()) {
				LOG.error("Password can not be null or empty");
				throw new InvalidInputException("Password can not be null or empty");
			}
			if (confirmPassword == null || confirmPassword.isEmpty()) {
				LOG.error("Confirm Password can not be null or empty");
				throw new InvalidInputException("Confirm Password can not be null or empty");
			}

			// check if password and confirm password field match
			if (!password.equals(confirmPassword)) {
				LOG.error("Password and confirm password fields do not match");
				throw new InvalidInputException("Password and confirm password fields do not match");
			}

			// Decrypte Url parameters
			try {
				urlParams = urlGenerator.decryptParameters(encryptedUrlParameters);
			}
			catch (InvalidInputException e) {
				LOG.error("Invalid Input exception in validating decrypting Url. Reason " + e.getMessage(), e);
				throw new InvalidInputException(e.getMessage(), e.getErrorCode(), e);
			}

			// check if email ID entered matches with the one in the encrypted url
			if (!urlParams.get("emailId").equals(emailId)) {
				LOG.error("Invalid Input exception. Reason emailId entered does not match with the one to which the mail was sent");
				throw new InvalidInputException(
						"Invalid Input exception. Reason emailId entered does not match with the one to which the mail was sent");
			}

			// update user's password
			try {
				// fetch user object with email Id
				user = authenticationService.getUserObjWithEmailId(emailId);
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
		}

		return JspResolver.LOGIN;
	}

}

// JIRA SS-21 : by RM-06 : EOC