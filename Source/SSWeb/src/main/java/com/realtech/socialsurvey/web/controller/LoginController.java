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
import com.realtech.socialsurvey.core.entities.VerticalsMaster;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.authentication.AuthenticationService;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.common.JspResolver;

@Controller
public class LoginController {

	private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);
	private static final String STATUS_PARAM = "s";
	private static final String AUTH_ERROR = "autherror";
	private static final String SESSION_ERROR = "sessionerror";
	private static final String LOGOUT = "logout";

	@Autowired
	private AuthenticationService authenticationService;
	@Autowired
	private MessageUtils messageUtils;
	@Autowired
	private URLGenerator urlGenerator;
	@Autowired
	private OrganizationManagementService organizationManagementService;
	@Autowired
	private UserManagementService userManagementService;
	@Autowired
	private SessionHelper sessionHelper;

	@RequestMapping(value = "/login")
	public String initLoginPage(Model model, @RequestParam(value = STATUS_PARAM, required = false) String status) {
		if (status != null) {
			switch (status) {
				case AUTH_ERROR:
					model.addAttribute("message",
							messageUtils.getDisplayMessage(DisplayMessageConstants.INVALID_USER_CREDENTIALS, DisplayMessageType.ERROR_MESSAGE));
					break;
				case SESSION_ERROR:
					model.addAttribute("message",
							messageUtils.getDisplayMessage(DisplayMessageConstants.SESSION_EXPIRED, DisplayMessageType.ERROR_MESSAGE));
					break;
				case LOGOUT:
					model.addAttribute("message",
							messageUtils.getDisplayMessage(DisplayMessageConstants.USER_LOGOUT_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE));
					break;
			}
		}
		return JspResolver.LOGIN;
	}

	@RequestMapping(value = "/landing")
	public String initLandingPage() {
		LOG.info("Login Page started");
		return JspResolver.LANDING;
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
	@RequestMapping(value = "/userlogin", method = RequestMethod.GET)
	public String login(Model model, HttpServletRequest request, HttpServletResponse response) {
		LOG.info("Login controller called for user login");
		User user = null;
		AccountType accountType = null;
		String redirectTo = null;

		try {
			user = sessionHelper.getCurrentUser();
			HttpSession session = request.getSession(true);
			
			List<LicenseDetail> licenseDetails = user.getCompany().getLicenseDetails();
			if (licenseDetails != null && !licenseDetails.isEmpty()) {
				LicenseDetail licenseDetail = licenseDetails.get(0);
				accountType = AccountType.getAccountType(licenseDetail.getAccountsMaster().getAccountsMasterId());
				LOG.debug("Adding account type in session");
				session.setAttribute(CommonConstants.ACCOUNT_TYPE_IN_SESSION, accountType);
			}
			else {
				LOG.debug("License details not found for the user's company");
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
				if(redirectTo.equals(JspResolver.COMPANY_INFORMATION)){
					List<VerticalsMaster> verticalsMasters = null;
					try {
						verticalsMasters = organizationManagementService.getAllVerticalsMaster();
					}
					catch (InvalidInputException e) {
						throw new InvalidInputException("Invalid Input exception occured in method getAllVerticalsMaster()",
								DisplayMessageConstants.GENERAL_ERROR, e);
					}
					model.addAttribute("verticals",verticalsMasters);
				}
			}
			else {
				LOG.debug("Company profile complete, check any of the user profiles is entered");
				if (user.getIsAtleastOneUserprofileComplete() == CommonConstants.PROCESS_COMPLETE) {
					
					/*UserProfile highestUserProfile = null;
					UserProfile companyAdminProfile = null;
					// fetch the highest user profile for user
					try {
						highestUserProfile = userManagementService.getHighestUserProfileForUser(user);
						companyAdminProfile = authenticationService.getCompanyAdminProfileForUser(user);
					}
					catch (NoRecordsFetchedException e) {
						LOG.error("No user profiles found for the user");
						return JspResolver.ERROR_PAGE;
					}*/
					//Compute all conditions for user and if user is CA then check for profile completion stage.
					
					if(user.isCompanyAdmin()){
						UserProfile adminProfile=null;
						for(UserProfile userProfile:user.getUserProfiles()){
							if((userProfile.getCompany().getCompanyId()==user.getCompany().getCompanyId())&&(userProfile.getProfilesMaster().getProfileId()==CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID))
								adminProfile = userProfile;
						}
						redirectTo = getRedirectionFromProfileCompletionStage(adminProfile.getProfileCompletionStage());
					}
					else
						redirectTo = JspResolver.LANDING;
					if(redirectTo.equals(JspResolver.LANDING)){
						// get the user's canonical settings
						LOG.info("Fetching the user's canonical settings and setting it in session");
						sessionHelper.getCanonicalSettings(session);
						// Set the session variables
						sessionHelper.setSettingVariablesInSession(session);
					}
				}
				else {
					LOG.info("No User profile present");
					//TODO: add logic for what happens when no user profile present
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
		return JspResolver.DASHBOARD;
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
			if (emailId == null || emailId.isEmpty() || !emailId.matches(CommonConstants.EMAIL_REGEX)) {
				LOG.error("Invalid email id passed");
				throw new InvalidInputException("Invalid email id passed", DisplayMessageConstants.INVALID_EMAILID);
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
			LOG.error("NonFatalException while sending the reset password link. Reason : " + e.getStackTrace(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.FORGOT_PASSWORD;
		}

		return JspResolver.FORGOT_PASSWORD;
	}

	// RM-06 : BOC
	/**
	 * Controller method to display the reset password page
	 */
	@RequestMapping(value = "/resetpassword")
	public String showResetPasswordPage(@RequestParam("q") String encryptedUrlParams, Model model) {
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

	// RM-06 : EOC

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
			long companyId = 0;
			try {
				companyId = Long.parseLong(urlParams.get(CommonConstants.COMPANY));
			}
			catch (NumberFormatException | NullPointerException e) {
				LOG.error("Invalid company id found in URL parameters. Reason " + e.getStackTrace(), e);
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e);
			}
			// update user's password
			try {
				// fetch user object with email Id
				user = authenticationService.getUserWithLoginNameAndCompanyId(emailId, companyId);
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
	 * method for logging out
	 * 
	 * @param
	 * @param request
	 * @param response
	 * @return
	 */

	@RequestMapping(value = "/logout")
	public String initLogoutPage(Model model, HttpServletRequest request, HttpServletResponse response) {
		LOG.info("logging out");
		request.getSession(false).invalidate();
		model.addAttribute("message",
				messageUtils.getDisplayMessage(DisplayMessageConstants.USER_LOGOUT_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE));
		return JspResolver.LOGIN;
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
		if (emailId == null || emailId.isEmpty() || !emailId.matches(CommonConstants.EMAIL_REGEX)) {
			LOG.error("Invalid email id passed");
			throw new InvalidInputException("Invalid email id passed", DisplayMessageConstants.INVALID_EMAILID);
		}
		if (password == null || password.isEmpty() || !password.matches(CommonConstants.PASSWORD_REG_EX)) {
			LOG.error("Invalid password");
			throw new InvalidInputException("Invalid password", DisplayMessageConstants.INVALID_PASSWORD);
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

	/*
	 * private void setSettingVariablesInSession(HttpSession session) {
	 * LOG.info("Settings related session values being set."); if
	 * (session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION) != null) { //
	 * setting the logo name UserSettings userSettings = (UserSettings)
	 * session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION); // check if company
	 * has a logo if (userSettings.getCompanySettings().getLogo() != null) {
	 * LOG.debug("Settings logo image from company settings");
	 * session.setAttribute(CommonConstants.LOGO_DISPLAY_IN_SESSION,
	 * userSettings.getCompanySettings().getLogo()); } else {
	 * LOG.debug("Could not find logo settings in company. Checking in lower heirarchy."); // TODO:
	 * Check the lower level hierarchy for logo } // check for the mail content String body = null;
	 * FileContentReplacements replacements = new FileContentReplacements();
	 * replacements.setFileName(EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER +
	 * EmailTemplateConstants.SURVEY_PARTICIPATION_MAIL_BODY); if
	 * (userSettings.getCompanySettings().getMail_content() == null) {
	 * LOG.debug("Setting default survey participation mail body."); // set the mail contents try {
	 * body = fileOperations.replaceFileContents(replacements);
	 * session.setAttribute(CommonConstants.SURVEY_PARTICIPATION_MAIL_BODY_IN_SESSION, body);
	 * session.setAttribute(CommonConstants.SURVEY_PARTICIPATION_REMINDER_MAIL_BODY_IN_SESSION,
	 * body); } catch (InvalidInputException e) {
	 * LOG.warn("Could not set mail content for survey participation"); } } else {
	 * LOG.debug("Company already has mail body settings. Hence, setting the same"); if
	 * (userSettings.getCompanySettings().getMail_content().getTake_survey_mail() != null) {
	 * session.setAttribute(CommonConstants.SURVEY_PARTICIPATION_MAIL_BODY_IN_SESSION,
	 * userSettings.getCompanySettings() .getMail_content().getTake_survey_mail().getMail_body()); }
	 * else { try { body = fileOperations.replaceFileContents(replacements);
	 * session.setAttribute(CommonConstants.SURVEY_PARTICIPATION_MAIL_BODY_IN_SESSION, body); }
	 * catch (InvalidInputException e) {
	 * LOG.warn("Could not set mail content for survey participation"); } } if
	 * (userSettings.getCompanySettings().getMail_content().getTake_survey_reminder_mail() != null)
	 * { session.setAttribute(CommonConstants.SURVEY_PARTICIPATION_REMINDER_MAIL_BODY_IN_SESSION,
	 * userSettings.getCompanySettings()
	 * .getMail_content().getTake_survey_reminder_mail().getMail_body()); } else { try { body =
	 * fileOperations.replaceFileContents(replacements);
	 * session.setAttribute(CommonConstants.SURVEY_PARTICIPATION_REMINDER_MAIL_BODY_IN_SESSION,
	 * body); } catch (InvalidInputException e) {
	 * LOG.warn("Could not set mail content for survey participation reminder"); } } } } }
	 */

}

// JIRA SS-21 : by RM-06 : EOC