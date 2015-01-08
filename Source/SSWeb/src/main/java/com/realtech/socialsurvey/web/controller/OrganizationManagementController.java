package com.realtech.socialsurvey.web.controller;

import java.io.IOException;
import java.util.HashMap;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.CRMInfo;
import com.realtech.socialsurvey.core.entities.MailContentSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SurveySettings;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserSettings;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.payment.Payment;
import com.realtech.socialsurvey.core.services.registration.RegistrationService;
import com.realtech.socialsurvey.core.services.upload.AmazonUploadService;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.EncryptionHelper;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.common.JspResolver;

// JIRA: SS-24 BY RM02 BOC

/**
 * Controller to manage the organizational settings and information provided by the user.
 */
@Controller
public class OrganizationManagementController {
	private static final Logger LOG = LoggerFactory.getLogger(OrganizationManagementController.class);

	@Autowired
	private MessageUtils messageUtils;

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private OrganizationManagementService organizationManagementService;

	@Autowired
	private UserManagementService userManagementService;

	@Autowired
	private Payment gateway;

	@Autowired
	private AmazonUploadService fileUploadService;

	@Autowired
	private EncryptionHelper encryptionHelper;
	
	/**
	 * Method to upload logo image for a company
	 * 
	 * @param fileLocal
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/uploadcompanylogo", method = RequestMethod.POST)
	public String imageUpload(Model model, @RequestParam("logo") MultipartFile fileLocal, HttpServletRequest request) {
		LOG.info("Method imageUpload of OrganizationManagementController called");
		String logoUrl = "";

		LOG.debug("Overriding Logo image name in Session");
		if (request.getSession(false).getAttribute(CommonConstants.LOGO_URL) != null) {
			request.getSession(false).removeAttribute(CommonConstants.LOGO_URL);
		}

		try {
			logoUrl = fileUploadService.fileUploadHandler(fileLocal, request.getParameter("logo_name"));
			model.addAttribute("message", messageUtils.getDisplayMessage("LOGO_UPLOAD_SUCCESSFUL", DisplayMessageType.SUCCESS_MESSAGE));
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while uploading Logo. Reason :" + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.MESSAGE_HEADER;
		}
		LOG.debug("Setting Logo image name to Session");
		request.getSession(false).setAttribute(CommonConstants.LOGO_URL, logoUrl);

		LOG.info("Method imageUpload of OrganizationManagementController completed successfully");
		return JspResolver.MESSAGE_HEADER;
	}

	/**
	 * Method to call service for adding company information for a user
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/addcompanyinformation", method = RequestMethod.POST)
	public String addCompanyInformation(Model model, HttpServletRequest request) {
		LOG.info("Method addCompanyInformation of UserManagementController called");
		String companyName = request.getParameter("company");
		String address1 = request.getParameter("address1");
		String address2 = request.getParameter("address2");
		String zipCode = request.getParameter("zipcode");
		String companyContactNo = request.getParameter("contactno");

		try {
			validateCompanyInfoParams(companyName, address1, zipCode, companyContactNo);
			String address = getCompleteAddress(address1, address2);

			HttpSession session = request.getSession(false);
			User user = (User) session.getAttribute(CommonConstants.USER_IN_SESSION);
			String logoUrl = null;
			if (session.getAttribute(CommonConstants.LOGO_URL) != null) {
				logoUrl = session.getAttribute(CommonConstants.LOGO_URL).toString();
			}
			session.removeAttribute(CommonConstants.LOGO_URL);

			Map<String, String> companyDetails = new HashMap<String, String>();
			companyDetails.put(CommonConstants.COMPANY_NAME, companyName);
			companyDetails.put(CommonConstants.ADDRESS, address);
			companyDetails.put(CommonConstants.ZIPCODE, zipCode);
			companyDetails.put(CommonConstants.COMPANY_CONTACT_NUMBER, companyContactNo);
			if (logoUrl != null) {
				companyDetails.put(CommonConstants.LOGO_URL, logoUrl);
			}

			LOG.debug("Calling services to add company details");
			user = organizationManagementService.addCompanyInformation(user, companyDetails);

			LOG.debug("Updating profile completion stage");
			registrationService.updateProfileCompletionStage(user, CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID,
					CommonConstants.ADD_ACCOUNT_TYPE_STAGE);

			LOG.debug("Successfully executed service to add company details");

		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while adding company information. Reason :" + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.COMPANY_INFORMATION;
		}
		LOG.info("Method addCompanyInformation of UserManagementController completed successfully");
		return JspResolver.ACCOUNT_TYPE_SELECTION;

	}

	/**
	 * Method to validate form parameters of company information provided by the user
	 * 
	 * @param companyName
	 * @param address
	 * @param zipCode
	 * @param companyContactNo
	 * @throws InvalidInputException
	 */
	private void validateCompanyInfoParams(String companyName, String address, String zipCode, String companyContactNo) throws InvalidInputException {
		LOG.debug("Method validateCompanyInfoParams called  for companyName : " + companyName + " address : " + address + " zipCode : " + zipCode
				+ " companyContactNo : " + companyContactNo);

		if (companyName == null || companyName.isEmpty() || !companyName.matches(CommonConstants.COMPANY_NAME_REGEX)) {
			throw new InvalidInputException("Company name is null or empty while adding company information",
					DisplayMessageConstants.INVALID_COMPANY_NAME);
		}
		if (address == null || address.isEmpty()) {
			throw new InvalidInputException("Address is null or empty while adding company information", DisplayMessageConstants.INVALID_ADDRESS);
		}

		if (zipCode == null || zipCode.isEmpty() || !zipCode.matches(CommonConstants.ZIPCODE_REGEX)) {
			throw new InvalidInputException("Zipcode is not valid while adding company information", DisplayMessageConstants.INVALID_ZIPCODE);
		}
		if (companyContactNo == null || companyContactNo.isEmpty() || !companyContactNo.matches(CommonConstants.PHONENUMBER_REGEX)) {
			throw new InvalidInputException("Company contact number is not valid while adding company information",
					DisplayMessageConstants.INVALID_COMPANY_PHONEN0);
		}
		LOG.debug("Returning from validateCompanyInfoParams after validating parameters");
	}

	/**
	 * Method to get complete address from multiple address lines
	 * 
	 * @param address1
	 * @param address2
	 * @return
	 */
	private String getCompleteAddress(String address1, String address2) {
		LOG.debug("Getting complete address for address1 : " + address1 + " and address2 : " + address2);
		String address = address1;
		/**
		 * if address line 2 is present, append it to address1 else the complete address is address1
		 */
		if (address1 != null && !address1.isEmpty() && address2 != null && !address2.isEmpty()) {
			address = address1 + " " + address2;
		}
		LOG.debug("Returning complete address" + address);
		return address;
	}

	/**
	 * Method to call services for saving the selected account type(plan)
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/addaccounttype", method = RequestMethod.POST)
	public String addAccountType(Model model, HttpServletRequest request) {
		LOG.info("Method addAccountType of UserManagementController called");
		String strAccountType = request.getParameter("accounttype");
		try {
			if (strAccountType == null || strAccountType.isEmpty()) {
				throw new InvalidInputException("Accounttype is null for adding account type", DisplayMessageConstants.INVALID_ADDRESS);
			}
			LOG.debug("AccountType obtained : " + strAccountType);
			
			User user = (User) request.getSession().getAttribute(CommonConstants.USER_IN_SESSION);
			
			LOG.debug("Checking if payment has already been made.");

			if(gateway.checkIfPaymentMade(user.getCompany())){
				LOG.debug("Payment for this company has already been made. Redirecting to dashboard.");
				return JspResolver.PAYMENT_ALREADY_MADE;
			}
			
			model.addAttribute("accounttype", strAccountType);
			model.addAttribute("clienttoken", gateway.getClientToken());
			model.addAttribute("message",
					messageUtils.getDisplayMessage(DisplayMessageConstants.ACCOUNT_TYPE_SELECTION_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE));

			LOG.info("Method addAccountType of UserManagementController completed successfully");
		}
		catch (NonFatalException e) {
			LOG.error("NonfatalException while adding account type. Reason: " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.MESSAGE_HEADER;
		}
		return JspResolver.PAYMENT;

	}

	/**
	 * Method to show Company settings on edit company
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/showcompanysettings", method = RequestMethod.GET)
	public String showCompanySettings(Model model, HttpServletRequest request) {
		LOG.info("Method showCompanySettings of UserManagementController called");
		HttpSession session = request.getSession(false);
		User user = (User) session.getAttribute(CommonConstants.USER_IN_SESSION);
		try {
			LOG.debug("Getting company settings");
			OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings(user);
			LOG.debug("Showing company settings: " + companySettings.toString());
			// setting the object in settings
			session.setAttribute("companysettings", companySettings);
		}
		catch (NonFatalException e) {
			LOG.error("NonfatalException while adding account type. Reason: " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.MESSAGE_HEADER;
		}
		return JspResolver.COMPANY_SETTINGS;
	}

	/**
	 * Method to save encompass details / CRM info
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/saveencompassdetails", method = RequestMethod.POST)
	@ResponseBody
	public String saveEncompassDetails(Model model, HttpServletRequest request) {
		LOG.info("Saving encompass details");
		HttpSession session = request.getSession(false);
		request.setAttribute("saveencompassdetails", "true");
		String message;

		try {
			testEncompassConnection(model, request);
			
			// Encrypting the password
			String plainPassword = request.getParameter("encompass-password");
			String cipherPassword = encryptionHelper.encryptAES(plainPassword, "");

			CRMInfo crmInfo = new CRMInfo();
			crmInfo.setCrm_source(CommonConstants.CRM_INFO_SOURCE_ENCOMPASS);
			crmInfo.setCrm_username(request.getParameter("encompass-username"));
			crmInfo.setCrm_password(cipherPassword);
			crmInfo.setUrl(request.getParameter("encompass-url"));
			crmInfo.setConnection_successful(true);
			OrganizationUnitSettings companySettings = ((UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION))
					.getCompanySettings();
			organizationManagementService.updateCRMDetails(companySettings, crmInfo);
			
			// set the updated settings value in session with plain password
			crmInfo.setCrm_password(plainPassword);
			companySettings.setCrm_info(crmInfo);
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.ENCOMPASS_DATA_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE).getMessage();
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while testing encompass detials. Reason : " + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
		}
		return message;
	}

	/**
	 * Method to test encompass details / CRM info
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/testencompassconnection", method = RequestMethod.POST)
	@ResponseBody
	public String testEncompassConnection(Model model, HttpServletRequest request) throws NonFatalException {
		LOG.info("Testing connections");
		String message;
		try {
			// validate the parameters
			if (!validateEncompassParameters(request)) {
				// TODO: code to test connection
			}
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.ENCOMPASS_CONNECTION_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE).getMessage();
		}
		catch (NonFatalException e) {
			if (request.getAttribute("saveencompassdetails") != null) {
				throw e;
			}
			else {
				LOG.error("NonFatalException while testing encompass detials. Reason : " + e.getMessage(), e);
			}
			message = messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
		}
		return message;
	}

	/**
	 * Method to validate encompass details / CRM info
	 * 
	 * @param request
	 * @return
	 */
	private boolean validateEncompassParameters(HttpServletRequest request) throws InvalidInputException {
		LOG.debug("Validating encompass parameters");
		String userName = request.getParameter("encompass-username");
		String password = request.getParameter("encompass-password");
		String url = request.getParameter("encompass-url");
		if (userName == null || userName.isEmpty() || password == null || password.isEmpty() || url == null || url.isEmpty()) {
			LOG.warn("Encompass validation failed");
			throw new InvalidInputException("All fields not set for encompass", DisplayMessageConstants.GENERAL_ERROR);
		}
		LOG.debug("Encompass validation passed.");
		return true;
	}

	/**
	 * Method to save survey Mailbody content
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/savesurveyparticipationmail", method = RequestMethod.POST)
	@ResponseBody
	public String setSurveyParticipationMailBody(Model model, HttpServletRequest request) {
		LOG.info("Saving survey participation mail body");
		HttpSession session = request.getSession(false);
		String mailCategory = request.getParameter("mailcategory");
		String mailBody = null;
		String message = "";
		
		try {
			OrganizationUnitSettings companySettings = ((UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION))
					.getCompanySettings();
			MailContentSettings updatedMailContentSettings = null;
			if(mailCategory != null && mailCategory.equals("participationmail")){
				mailBody = request.getParameter("survey-participation-mailcontent");
				if (mailBody == null || mailBody.isEmpty()) {
					LOG.warn("Survey participation mail body is blank.");
					throw new InvalidInputException("Survey participation mail body is blank.", DisplayMessageConstants.GENERAL_ERROR);
				}
				updatedMailContentSettings = organizationManagementService.updateSurveyParticipationMailBody(companySettings, mailBody, CommonConstants.SURVEY_MAIL_BODY_CATEGORY);
				// set the value back in session
				session.setAttribute(CommonConstants.SURVEY_PARTICIPATION_MAIL_BODY_IN_SESSION, mailBody);
				message = messageUtils.getDisplayMessage(DisplayMessageConstants.SURVEY_PARTICIPATION_MAILBODY_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE).getMessage();
			}
			
			else if(mailCategory != null && mailCategory.equals("participationremindermail")){
				mailBody = request.getParameter("survey-participation-reminder-mailcontent");
				if (mailBody == null || mailBody.isEmpty()) {
					LOG.warn("Survey participation reminder mail body is blank.");
					throw new InvalidInputException("Survey participation mail body is blank.", DisplayMessageConstants.GENERAL_ERROR);
				}
				updatedMailContentSettings = organizationManagementService.updateSurveyParticipationMailBody(companySettings, mailBody, CommonConstants.SURVEY_REMINDER_MAIL_BODY_CATEGORY);
				// set the value back in session
				session.setAttribute(CommonConstants.SURVEY_PARTICIPATION_REMINDER_MAIL_BODY_IN_SESSION, mailBody);
				message = messageUtils.getDisplayMessage(DisplayMessageConstants.SURVEY_PARTICIPATION_REMINDERMAILBODY_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE).getMessage();
			}
			// update the mail content settings in session
			companySettings.setMail_content(updatedMailContentSettings);
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while saving survey participation mail body. Reason : " + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
		}
		return message;
	}
	
	/**
	 * Method to update Survey Settings
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/updatesurveysettings", method = RequestMethod.POST)
	@ResponseBody
	public String updateSurveySettings(Model model, HttpServletRequest request) {
		LOG.info("Updating Survey Settings");
		HttpSession session = request.getSession(false);
		String ratingCategory = request.getParameter("ratingcategory");
		SurveySettings originalSurveySettings = null;
		SurveySettings surveySettings = null;
		String message = "";

		try {
			OrganizationUnitSettings companySettings = ((UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION))
					.getCompanySettings();

			if (ratingCategory != null && ratingCategory.equals("rating-auto-post")) {
				double autopostRating = Double.parseDouble(request.getParameter("rating-auto-post"));
				if (autopostRating == 0) {
					LOG.warn("Auto Post rating score is 0.");
					throw new InvalidInputException("Auto Post rating score is 0.", DisplayMessageConstants.GENERAL_ERROR);
				}
				
				originalSurveySettings = companySettings.getSurvey_settings();
				surveySettings = new SurveySettings();
				surveySettings.setAuto_post_score((float)autopostRating);
				if(originalSurveySettings != null){
					surveySettings.setShow_survey_above_score(originalSurveySettings.getShow_survey_above_score());
					surveySettings.setMax_number_of_survey_reminders(originalSurveySettings.getMax_number_of_survey_reminders());
					surveySettings.setSurvey_reminder_interval_in_days(originalSurveySettings.getSurvey_reminder_interval_in_days());
					surveySettings.setReminderDisabled(originalSurveySettings.getIsReminderDisabled());
				}
				LOG.info("Updating Survey Settings Post score");
				message = messageUtils.getDisplayMessage(DisplayMessageConstants.SURVEY_AUTO_POST_SCORE_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE).getMessage();
			}

			else if (ratingCategory != null && ratingCategory.equals("rating-min-post")) {
				double minPostRating = Double.parseDouble(request.getParameter("rating-min-post"));
				if (minPostRating == 0) {
					LOG.warn("Minimum Post rating score is 0.");
					throw new InvalidInputException("Mimimum Post rating score is 0.", DisplayMessageConstants.GENERAL_ERROR);
				}
				
				originalSurveySettings = companySettings.getSurvey_settings();
				surveySettings = new SurveySettings();
				surveySettings.setShow_survey_above_score((float)minPostRating);
				if(originalSurveySettings != null){
					surveySettings.setAuto_post_score(originalSurveySettings.getAuto_post_score());
					surveySettings.setMax_number_of_survey_reminders(originalSurveySettings.getMax_number_of_survey_reminders());
					surveySettings.setSurvey_reminder_interval_in_days(originalSurveySettings.getSurvey_reminder_interval_in_days());
					surveySettings.setReminderDisabled(originalSurveySettings.getIsReminderDisabled());
				}
				LOG.info("Updating Survey Settings Min score");
				message = messageUtils.getDisplayMessage(DisplayMessageConstants.SURVEY_MIN_POST_SCORE_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE).getMessage();
			}

			if(organizationManagementService.updateSurveySettings(companySettings, surveySettings)) {
				companySettings.setSurvey_settings(surveySettings);
				LOG.info("Updated Survey Settings");
			}
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while updating survey settings. Reason : " + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
		}
		return message;
	}
	
	/**
	 * Method to update Survey Reminder Settings
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/updatesurveyremindersettings", method = RequestMethod.POST)
	@ResponseBody
	public String updateSurveyReminderSettings(Model model, HttpServletRequest request) {
		LOG.info("Updating Survey Reminder Settings");
		HttpSession session = request.getSession(false);
		String mailCategory = request.getParameter("mailcategory");
		SurveySettings originalSurveySettings = null;
		SurveySettings surveySettings = null;
		String message = "";
		
		try {
			OrganizationUnitSettings companySettings = ((UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION))
					.getCompanySettings();

			if (mailCategory != null && mailCategory.equals("reminder-interval")) {
				int reminderInterval = Integer.parseInt(request.getParameter("reminder-interval"));
				if (reminderInterval == 0) {
					LOG.warn("Reminder Interval is 0.");
					throw new InvalidInputException("Reminder Interval is 0.", DisplayMessageConstants.GENERAL_ERROR);
				}
				
				originalSurveySettings = companySettings.getSurvey_settings();
				surveySettings = new SurveySettings();
				surveySettings.setSurvey_reminder_interval_in_days(reminderInterval);
				if(originalSurveySettings != null){
					surveySettings.setAuto_post_score(originalSurveySettings.getAuto_post_score());
					surveySettings.setMax_number_of_survey_reminders(originalSurveySettings.getMax_number_of_survey_reminders());
					surveySettings.setShow_survey_above_score(originalSurveySettings.getShow_survey_above_score());
					surveySettings.setReminderDisabled(originalSurveySettings.getIsReminderDisabled());
				}
				LOG.info("Updating Survey Settings Reminder Interval");
				message = messageUtils.getDisplayMessage(DisplayMessageConstants.SURVEY_REMINDER_INTERVAL_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE).getMessage();
			}

			else if (mailCategory != null && mailCategory.equals("reminder-needed")) {
				boolean isReminderDisabled = Boolean.parseBoolean(request.getParameter("reminder-needed-hidden"));
				
				originalSurveySettings = companySettings.getSurvey_settings();
				surveySettings = new SurveySettings();
				surveySettings.setReminderDisabled(isReminderDisabled);
				if(originalSurveySettings != null){
					surveySettings.setAuto_post_score(originalSurveySettings.getAuto_post_score());
					surveySettings.setMax_number_of_survey_reminders(originalSurveySettings.getMax_number_of_survey_reminders());
					surveySettings.setShow_survey_above_score(originalSurveySettings.getShow_survey_above_score());
					surveySettings.setSurvey_reminder_interval_in_days(originalSurveySettings.getSurvey_reminder_interval_in_days());
				}
				LOG.info("Updating Survey Settings Reminder Needed");
				message = messageUtils.getDisplayMessage(DisplayMessageConstants.SURVEY_REMINDER_ENABLED_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE).getMessage();
			}

			if(organizationManagementService.updateSurveySettings(companySettings, surveySettings)) {
				companySettings.setSurvey_settings(surveySettings);
				LOG.info("Updated Survey Settings");
			}
		}
		catch (NumberFormatException e) {
			LOG.error("NumberFormatException while updating Reminder Interval. Reason : " + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.INVALID_SURVEY_REMINDER_INTERVAL, DisplayMessageType.ERROR_MESSAGE).getMessage();
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while updating survey settings. Reason : " + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
		}
		return message;
	}
	
	/**
	 * Method to update Other Company Settings
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/updateothersettings", method = RequestMethod.POST)
	@ResponseBody
	public String updateOtherSettings(Model model, HttpServletRequest request) {
		LOG.info("Updating Location Settings");
		HttpSession session = request.getSession(false);
		String otherCategory = request.getParameter("othercategory");
		String message = "";
		
		try {
			OrganizationUnitSettings companySettings = ((UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION))
					.getCompanySettings();

			if (otherCategory != null && otherCategory.equals("other-location")) {
				boolean isLocationEnabled = Boolean.parseBoolean(request.getParameter("other-location"));
				organizationManagementService.updateLocationEnabled(companySettings, isLocationEnabled);

				// set the updated settings value in session
				companySettings.setLocationEnabled(isLocationEnabled);
				message = messageUtils.getDisplayMessage(DisplayMessageConstants.LOCATION_SETTINGS_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE).getMessage();
				LOG.info("Updated Location Settings");
			}
			
			else if (otherCategory != null && otherCategory.equals("other-account")) {
				boolean isAccountDisabled = Boolean.parseBoolean(request.getParameter("other-account"));
				
				// Calling services to update DB
				organizationManagementService.updateAccountDisabled(companySettings, isAccountDisabled);
				if(isAccountDisabled) {
					organizationManagementService.addDisabledAccount(companySettings.getIden());
				} else {
					organizationManagementService.deleteDisabledAccount(companySettings.getIden());
				}

				// set the updated settings value in session
				companySettings.setAccountDisabled(isAccountDisabled);
				message = messageUtils.getDisplayMessage(DisplayMessageConstants.ACCOUNT_SETTINGS_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE).getMessage();
				LOG.info("Updated Location Settings");
			}
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while updating other settings. Reason : " + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
		}
		return message;
	}
}
// JIRA: SS-24 BY RM02 EOC