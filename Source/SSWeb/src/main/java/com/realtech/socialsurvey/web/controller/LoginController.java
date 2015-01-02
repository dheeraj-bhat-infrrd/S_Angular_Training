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
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.authentication.AuthenticationService;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
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
	@Autowired
	private OrganizationManagementService organizationManagementService;

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

	/**
	 * Method for logging in user
	 * 
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/userlogin", method = RequestMethod.POST)
	public String login(Model model, HttpServletRequest request, HttpServletResponse response) {
		LOG.info("Login controller called for user login");
		String loginName = request.getParameter("loginName");
		String password = request.getParameter("password");
		User user = null;
		UserProfile userProfile = null;
		String redirectTo = null;

		try {
			validateLoginFormParameters(loginName, password);

			try {
				user = authenticationService.getUserWithLoginName(loginName);
			}
			catch (NoRecordsFetchedException e) {
				LOG.error("No Records Fetched Exception in fetching User. Reason " + e.getMessage(), e);
				throw new NoRecordsFetchedException(e.getMessage(), DisplayMessageConstants.USER_NOT_PRESENT, e);
			}
			LOG.debug("Check if company status is active");
			if (user.getCompany().getStatus() == CommonConstants.STATUS_INACTIVE) {
				throw new InvalidInputException("Company is inactive in login", DisplayMessageConstants.COMPANY_INACTIVE);
			}

			LOG.debug("Checking if user is not in inactive mode");
			if (user.getStatus() == CommonConstants.STATUS_INACTIVE) {
				throw new InvalidInputException("User not active in login", DisplayMessageConstants.USER_INACTIVE);
			}

			try {
				LOG.debug("Calling authentication service to validate user while login");
				authenticationService.validateUser(user, password);
				LOG.debug("Successfully executed authentication service to validate user while login");

				HttpSession session = request.getSession(false);
				session.setAttribute(CommonConstants.USER_IN_SESSION, user);

				AccountType accountType = null;
				List<LicenseDetail> licenseDetails = user.getCompany().getLicenseDetails();
				if (licenseDetails != null && !licenseDetails.isEmpty()) {
					LicenseDetail licenseDetail = licenseDetails.get(0);
					accountType = AccountType.getAccountType(licenseDetail.getLicenseId());
					LOG.debug("Adding account type in session");
					session.setAttribute(CommonConstants.ACCOUNT_TYPE_IN_SESSION, accountType);
				}
				else {
					LOG.debug("License details not found for the user's company");
				}
			}
			catch (InvalidInputException e) {
				LOG.error("Invalid Input exception in validating User. Reason " + e.getMessage(), e);
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.INVALID_USER_CREDENTIALS, e);
			}

			/**
			 * Check if if the company inserted is default company or registration is not complete ,
			 * if company registration not done redirect to company registration page
			 */
			LOG.debug("Checking if company profile registration complete");
			if (user.getCompany().getCompanyId() == CommonConstants.DEFAULT_COMPANY_ID
					|| user.getCompany().getIsRegistrationComplete() != CommonConstants.PROCESS_COMPLETE) {

				LOG.debug("Company profile not complete, redirecting to company information page");
				redirectTo = JspResolver.COMPANY_INFORMATION;
			}
			else {
				LOG.debug("Company profile complete, check any of the user profiles is entered");
				if (user.getIsAtleastOneUserprofileComplete() == CommonConstants.PROCESS_COMPLETE) {
					/**
					 * redirect user to complete the top priority profile. Priority
					 * Company->region->branch->agent
					 */
					LOG.debug("None of the user profiles are complete , Redirect to top priority profile first");

					try {
						LOG.debug("Calling service for fetching company admin user profile");
						userProfile = authenticationService.getCompanyAdminProfileForUser(user);
					}
					catch (InvalidInputException e) {
						LOG.error("Invalid Input exception in validating User. Reason " + e.getMessage(), e);
						throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.INVALID_USER, e);
					}

					redirectTo = getRedirectionFromProfileCompletionStage(userProfile.getProfileCompletionStage());

				}
			}
			LOG.info("User login successful");
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while logging in. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.LOGIN;
		}

		return redirectTo;
	}

	/**
	 * Start the companyinformation page
	 * 
	 * @return
	 */
	@RequestMapping(value = "/addcompanyinformation")
	public String initCompanyInformationPage() {
		return JspResolver.COMPANY_INFORMATION;
	}

	/**
	 * Start the add account type page
	 */
	@RequestMapping(value = "/addaccounttype")
	public String initAddAccountTypePage() {
		LOG.info("Add account type page started");
		return JspResolver.ACCOUNT_TYPE_SELECTION;
	}

	/**
	 * Start the dashboard page
	 */
	@RequestMapping(value = "/dashboard")
	public String initDashboardPage() {
		LOG.info("Dashboard Page started");
		return JspResolver.LANDING;
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
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.USER_NOT_PRESENT, e);
			}
			// Send reset password link
			try {
				authenticationService.sendResetPasswordLink(emailId, user.getFirstName() + " " + user.getLastName());
			}
			catch (InvalidInputException e) {
				LOG.error("Invalid Input exception in sending reset password link. Reason " + e.getMessage(), e);
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e);
			}
			model.addAttribute("message",
					messageUtils.getDisplayMessage(DisplayMessageConstants.PASSWORD_RESET_LINK_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE));

		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while sending the reset password link. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.FORGOT_PASSWORD;
		}

		return JspResolver.FORGOT_PASSWORD;
	}

	//RM-06 : BOC
	/**
	 * Controller method to display the reset password page
	 */
	@RequestMapping(value = "/resetpassword")
	public String showResetPasswordPage(@RequestParam("q") String encryptedUrlParams,Model model) {
		LOG.info("Forgot Password Page started with encrypter url : " + encryptedUrlParams);
		try {
			try {
				Map<String, String> urlParams = urlGenerator.decryptParameters(encryptedUrlParams);
				model.addAttribute(CommonConstants.EMAIL_ID, urlParams.get(CommonConstants.EMAIL_ID));
			}
			catch (InvalidInputException e) {
				LOG.error("Invalid Input exception in decrypting url parameters. Reason " + e.getMessage(), e);
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e);
			}
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while setting new Password. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.MESSAGE_HEADER;
		}
		return JspResolver.RESET_PASSWORD;
	}
	//RM-06 : EOC
	
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
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.USER_NOT_PRESENT, e);
			}
			try {
				// change user's password
				authenticationService.changePassword(user, password);
			}
			catch (InvalidInputException e) {
				LOG.error("Invalid Input exception in changing the user's password. Reason " + e.getMessage(), e);
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e);
			}
			LOG.info("Reset user password executed successfully");
			model.addAttribute("message",
					messageUtils.getDisplayMessage(DisplayMessageConstants.PASSWORD_CHANGE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE));

		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while setting new Password. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.RESET_PASSWORD;
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
	private void validateLoginFormParameters(String loginName, String password) throws InvalidInputException {
		LOG.debug("Validating Login form paramters loginName :" + loginName);
		if (loginName == null || loginName.isEmpty()) {
			throw new InvalidInputException("User name passed can not be null", DisplayMessageConstants.INVALID_USERNAME);
		}
		if (password == null || password.isEmpty()) {
			throw new InvalidInputException("Password passed can not be null", DisplayMessageConstants.INVALID_PASSWORD);
		}
		LOG.debug("Login form parameters validated successfully");
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
		LOG.debug("Validating reset password form paramters");
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
		LOG.debug("Reset password form parameters validated successfully");
	}

	/**
	 * Method to get the redirect page from profile completion stage
	 * 
	 * @param profileCompletionStage
	 * @return
	 * @throws InvalidInputException
	 */
	private String getRedirectionFromProfileCompletionStage(String profileCompletionStage) throws InvalidInputException {
		LOG.debug("Method getRedirectionFromProfileCompletionStage called for profileCompletionStage: " + profileCompletionStage);
		String redirectTo = null;
		switch (profileCompletionStage) {
			case CommonConstants.ADD_COMPANY_STAGE:
				redirectTo = JspResolver.COMPANY_INFORMATION;
				break;
			case CommonConstants.ADD_ACCOUNT_TYPE_STAGE:
				redirectTo = JspResolver.ACCOUNT_TYPE_SELECTION;
				break;
			case CommonConstants.DASHBOARD_STAGE:
				redirectTo = JspResolver.LANDING;
				break;
			default:
				throw new InvalidInputException("Profile completion stage is invalid", DisplayMessageConstants.GENERAL_ERROR);
		}

		LOG.debug("Method getRedirectionFromProfileCompletionStage finished. Returning : " + redirectTo);
		return redirectTo;
	}

}

// JIRA SS-21 : by RM-06 : EOC