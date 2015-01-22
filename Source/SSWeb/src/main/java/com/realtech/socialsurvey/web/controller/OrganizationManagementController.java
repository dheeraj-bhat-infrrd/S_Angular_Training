package com.realtech.socialsurvey.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.Achievement;
import com.realtech.socialsurvey.core.entities.Association;
import com.realtech.socialsurvey.core.entities.CRMInfo;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.ContactNumberSettings;
import com.realtech.socialsurvey.core.entities.Licenses;
import com.realtech.socialsurvey.core.entities.MailContentSettings;
import com.realtech.socialsurvey.core.entities.MailIdSettings;
import com.realtech.socialsurvey.core.entities.MiscValues;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SurveySettings;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserSettings;
import com.realtech.socialsurvey.core.entities.WebAddressSettings;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.payment.Payment;
import com.realtech.socialsurvey.core.services.payment.exception.PaymentException;
import com.realtech.socialsurvey.core.services.payment.exception.SubscriptionPastDueException;
import com.realtech.socialsurvey.core.services.payment.exception.SubscriptionUpgradeUnsuccessfulException;
import com.realtech.socialsurvey.core.services.upload.FileUploadService;
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
	private OrganizationManagementService organizationManagementService;

	@Autowired
	private UserManagementService userManagementService;

	@Autowired
	private Payment gateway;

	@Autowired
	private FileUploadService fileUploadService;

	@Autowired
	private EncryptionHelper encryptionHelper;

	@Autowired
	private SessionHelper sessionHelper;

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
		String logoName = "";

		LOG.debug("Overriding Logo image name in Session");
		if (request.getSession(false).getAttribute(CommonConstants.LOGO_NAME) != null) {
			request.getSession(false).removeAttribute(CommonConstants.LOGO_NAME);
		}

		try {
			logoName = fileUploadService.fileUploadHandler(fileLocal, request.getParameter("logo_name"));
			model.addAttribute("message", messageUtils.getDisplayMessage("LOGO_UPLOAD_SUCCESSFUL", DisplayMessageType.SUCCESS_MESSAGE));
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while uploading Logo. Reason :" + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.MESSAGE_HEADER;
		}
		LOG.debug("Setting Logo image name to Session");
		request.getSession(false).setAttribute(CommonConstants.LOGO_NAME, logoName);

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
			User user = sessionHelper.getCurrentUser();
			String logoName = null;
			if (session.getAttribute(CommonConstants.LOGO_NAME) != null) {
				logoName = session.getAttribute(CommonConstants.LOGO_NAME).toString();
			}
			session.removeAttribute(CommonConstants.LOGO_NAME);

			Map<String, String> companyDetails = new HashMap<String, String>();
			companyDetails.put(CommonConstants.COMPANY_NAME, companyName);
			companyDetails.put(CommonConstants.ADDRESS, address);
			companyDetails.put(CommonConstants.ADDRESS1, address1);
			if (address2 != null) {
				companyDetails.put(CommonConstants.ADDRESS2, address2);
			}
			companyDetails.put(CommonConstants.ZIPCODE, zipCode);
			companyDetails.put(CommonConstants.COMPANY_CONTACT_NUMBER, companyContactNo);
			if (logoName != null) {
				companyDetails.put(CommonConstants.LOGO_NAME, logoName);
			}

			LOG.debug("Calling services to add company details");
			user = organizationManagementService.addCompanyInformation(user, companyDetails);

			LOG.debug("Updating profile completion stage");
			userManagementService.updateProfileCompletionStage(user, CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID,
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

			User user = sessionHelper.getCurrentUser();

			LOG.debug("Checking if payment has already been made.");
			if (gateway.checkIfPaymentMade(user.getCompany())) {
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
		User user = sessionHelper.getCurrentUser();
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
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.ENCOMPASS_DATA_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE)
					.getMessage();
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
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.ENCOMPASS_CONNECTION_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE)
					.getMessage();
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
			if (mailCategory != null && mailCategory.equals("participationmail")) {
				mailBody = request.getParameter("survey-participation-mailcontent");
				if (mailBody == null || mailBody.isEmpty()) {
					LOG.warn("Survey participation mail body is blank.");
					throw new InvalidInputException("Survey participation mail body is blank.", DisplayMessageConstants.GENERAL_ERROR);
				}
				updatedMailContentSettings = organizationManagementService.updateSurveyParticipationMailBody(companySettings, mailBody,
						CommonConstants.SURVEY_MAIL_BODY_CATEGORY);
				// set the value back in session
				session.setAttribute(CommonConstants.SURVEY_PARTICIPATION_MAIL_BODY_IN_SESSION, mailBody);
				message = messageUtils.getDisplayMessage(DisplayMessageConstants.SURVEY_PARTICIPATION_MAILBODY_UPDATE_SUCCESSFUL,
						DisplayMessageType.SUCCESS_MESSAGE).getMessage();
			}

			else if (mailCategory != null && mailCategory.equals("participationremindermail")) {
				mailBody = request.getParameter("survey-participation-reminder-mailcontent");
				if (mailBody == null || mailBody.isEmpty()) {
					LOG.warn("Survey participation reminder mail body is blank.");
					throw new InvalidInputException("Survey participation mail body is blank.", DisplayMessageConstants.GENERAL_ERROR);
				}
				updatedMailContentSettings = organizationManagementService.updateSurveyParticipationMailBody(companySettings, mailBody,
						CommonConstants.SURVEY_REMINDER_MAIL_BODY_CATEGORY);
				// set the value back in session
				session.setAttribute(CommonConstants.SURVEY_PARTICIPATION_REMINDER_MAIL_BODY_IN_SESSION, mailBody);
				message = messageUtils.getDisplayMessage(DisplayMessageConstants.SURVEY_PARTICIPATION_REMINDERMAILBODY_UPDATE_SUCCESSFUL,
						DisplayMessageType.SUCCESS_MESSAGE).getMessage();
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
				surveySettings.setAuto_post_score((float) autopostRating);
				if (originalSurveySettings != null) {
					surveySettings.setShow_survey_above_score(originalSurveySettings.getShow_survey_above_score());
					surveySettings.setMax_number_of_survey_reminders(originalSurveySettings.getMax_number_of_survey_reminders());
					surveySettings.setSurvey_reminder_interval_in_days(originalSurveySettings.getSurvey_reminder_interval_in_days());
					surveySettings.setReminderDisabled(originalSurveySettings.getIsReminderDisabled());
				}
				LOG.info("Updating Survey Settings Post score");
				message = messageUtils.getDisplayMessage(DisplayMessageConstants.SURVEY_AUTO_POST_SCORE_UPDATE_SUCCESSFUL,
						DisplayMessageType.SUCCESS_MESSAGE).getMessage();
			}

			else if (ratingCategory != null && ratingCategory.equals("rating-min-post")) {
				double minPostRating = Double.parseDouble(request.getParameter("rating-min-post"));
				if (minPostRating == 0) {
					LOG.warn("Minimum Post rating score is 0.");
					throw new InvalidInputException("Mimimum Post rating score is 0.", DisplayMessageConstants.GENERAL_ERROR);
				}

				originalSurveySettings = companySettings.getSurvey_settings();
				surveySettings = new SurveySettings();
				surveySettings.setShow_survey_above_score((float) minPostRating);
				if (originalSurveySettings != null) {
					surveySettings.setAuto_post_score(originalSurveySettings.getAuto_post_score());
					surveySettings.setMax_number_of_survey_reminders(originalSurveySettings.getMax_number_of_survey_reminders());
					surveySettings.setSurvey_reminder_interval_in_days(originalSurveySettings.getSurvey_reminder_interval_in_days());
					surveySettings.setReminderDisabled(originalSurveySettings.getIsReminderDisabled());
				}
				LOG.info("Updating Survey Settings Min score");
				message = messageUtils.getDisplayMessage(DisplayMessageConstants.SURVEY_MIN_POST_SCORE_UPDATE_SUCCESSFUL,
						DisplayMessageType.SUCCESS_MESSAGE).getMessage();
			}

			if (organizationManagementService.updateSurveySettings(companySettings, surveySettings)) {
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
				if (originalSurveySettings != null) {
					surveySettings.setAuto_post_score(originalSurveySettings.getAuto_post_score());
					surveySettings.setMax_number_of_survey_reminders(originalSurveySettings.getMax_number_of_survey_reminders());
					surveySettings.setShow_survey_above_score(originalSurveySettings.getShow_survey_above_score());
					surveySettings.setReminderDisabled(originalSurveySettings.getIsReminderDisabled());
				}
				LOG.info("Updating Survey Settings Reminder Interval");
				message = messageUtils.getDisplayMessage(DisplayMessageConstants.SURVEY_REMINDER_INTERVAL_UPDATE_SUCCESSFUL,
						DisplayMessageType.SUCCESS_MESSAGE).getMessage();
			}

			else if (mailCategory != null && mailCategory.equals("reminder-needed")) {
				boolean isReminderDisabled = Boolean.parseBoolean(request.getParameter("reminder-needed-hidden"));

				originalSurveySettings = companySettings.getSurvey_settings();
				surveySettings = new SurveySettings();
				surveySettings.setReminderDisabled(isReminderDisabled);
				if (originalSurveySettings != null) {
					surveySettings.setAuto_post_score(originalSurveySettings.getAuto_post_score());
					surveySettings.setMax_number_of_survey_reminders(originalSurveySettings.getMax_number_of_survey_reminders());
					surveySettings.setShow_survey_above_score(originalSurveySettings.getShow_survey_above_score());
					surveySettings.setSurvey_reminder_interval_in_days(originalSurveySettings.getSurvey_reminder_interval_in_days());
				}
				LOG.info("Updating Survey Settings Reminder Needed");
				message = messageUtils.getDisplayMessage(DisplayMessageConstants.SURVEY_REMINDER_ENABLED_UPDATE_SUCCESSFUL,
						DisplayMessageType.SUCCESS_MESSAGE).getMessage();
			}

			if (organizationManagementService.updateSurveySettings(companySettings, surveySettings)) {
				companySettings.setSurvey_settings(surveySettings);
				LOG.info("Updated Survey Settings");
			}
		}
		catch (NumberFormatException e) {
			LOG.error("NumberFormatException while updating Reminder Interval. Reason : " + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.INVALID_SURVEY_REMINDER_INTERVAL, DisplayMessageType.ERROR_MESSAGE)
					.getMessage();
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
				message = messageUtils.getDisplayMessage(DisplayMessageConstants.LOCATION_SETTINGS_UPDATE_SUCCESSFUL,
						DisplayMessageType.SUCCESS_MESSAGE).getMessage();
				LOG.info("Updated Location Settings");
			}

			else if (otherCategory != null && otherCategory.equals("other-account")) {
				boolean isAccountDisabled = Boolean.parseBoolean(request.getParameter("other-account"));

				// Calling services to update DB
				organizationManagementService.updateAccountDisabled(companySettings, isAccountDisabled);
				if (isAccountDisabled) {
					organizationManagementService.addDisabledAccount(companySettings.getIden());
				}
				else {
					organizationManagementService.deleteDisabledAccount(companySettings.getIden());
				}

				// set the updated settings value in session
				companySettings.setAccountDisabled(isAccountDisabled);
				message = messageUtils.getDisplayMessage(DisplayMessageConstants.ACCOUNT_SETTINGS_UPDATE_SUCCESSFUL,
						DisplayMessageType.SUCCESS_MESSAGE).getMessage();
				LOG.info("Updated Location Settings");
			}
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while updating other settings. Reason : " + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
		}
		return message;
	}

	@RequestMapping(value = "/upgradeplan", method = RequestMethod.POST)
	@ResponseBody
	public String upgradePlanForUserInSession(HttpServletRequest request, Model model) {
		LOG.info("Upgrading the user's subscription");
		String accountType = request.getParameter(CommonConstants.ACCOUNT_TYPE_IN_SESSION);
		String message = "";

		LOG.info("Fetching the user in session");
		User user = sessionHelper.getCurrentUser();

		try {
			LOG.info("Making the braintree API call to upgrade and updating the database!");

			if (accountType == null || accountType.isEmpty()) {
				throw new InvalidInputException("Account type parameter passed is null or empty", DisplayMessageConstants.GENERAL_ERROR);
			}

			int accountTypeValue = 0;
			try {
				accountTypeValue = Integer.parseInt(accountType);
			}
			catch (NumberFormatException e) {
				throw new InvalidInputException("Error while parsing account type ", DisplayMessageConstants.GENERAL_ERROR, e);
			}

			gateway.upgradePlanForSubscription(user.getCompany(), accountTypeValue);
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.SUBSCRIPTION_UPGRADE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE)
					.getMessage();
			LOG.info("Upgrade successful");
		}
		catch (InvalidInputException | NoRecordsFetchedException | PaymentException e) {
			LOG.error("NonFatalException while upgrading subscription. Message : " + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(null, DisplayMessageType.ERROR_MESSAGE).getMessage();
		}
		catch (SubscriptionPastDueException e) {
			LOG.error("SubscriptionPastDueException while upgrading subscription. Message : " + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
		}
		catch (SubscriptionUpgradeUnsuccessfulException e) {
			LOG.error("SubscriptionUpgradeUnsuccessfulException while upgrading subscription. Message : " + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
		}

		return message;

	}

	// JIRA SS-97 by RM-06 : BOC

	/**
	 * Method land on the profile settings page
	 */
	@RequestMapping(value = "/showprofilepage", method = RequestMethod.GET)
	public String showProfilePage() {
		LOG.info("Started the profile page");
		return JspResolver.COMPANY_PROFILE;
	}

	@RequestMapping(value = "/fetchcontactdetails", method = RequestMethod.GET)
	public String fetchContactDetails() {
		LOG.info("Fecthing contact details for profile");
		return JspResolver.CONTACT_DETAILS_LIST;
	}

	@RequestMapping(value = "/fetchassociations", method = RequestMethod.GET)
	public String fetchAssociations() {
		LOG.info("Fecthing association list for profile");
		return JspResolver.ASSOCIATION_LIST;
	}

	@RequestMapping(value = "/fetchachievements", method = RequestMethod.GET)
	public String fetchAchievements() {
		LOG.info("Fecthing achievement list for profile");
		return JspResolver.ACHIEVEMENT_LIST;
	}

	@RequestMapping(value = "/fetchlicences", method = RequestMethod.GET)
	public String fetchLicences() {
		LOG.info("Fecthing license details for profile");
		return JspResolver.LICENSE_LIST;
	}

	@RequestMapping(value = "/fetchaddressdetails", method = RequestMethod.GET)
	public String fetchAddressDetails() {
		LOG.info("Fecthing address details for rofile");
		return JspResolver.ADDRESS_DETAILS;
	}

	@RequestMapping(value = "/fetchprofileimage", method = RequestMethod.GET)
	public String fetchProfileImage() {
		LOG.info("Fecthing profile image");
		return JspResolver.PROFILE_IMAGE_CONTENT;
	}

	/**
	 * Method to update associations in profile
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/updateassociations", method = RequestMethod.POST)
	public String updateAssociations(Model model, HttpServletRequest request) {

		HttpSession session = request.getSession(false);
		LOG.info("Updating associations list");
		String payload = request.getParameter("associationList");
		UserSettings userSettings = (UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);
		OrganizationUnitSettings unitSettings = userSettings.getCompanySettings();
		try {
			if (payload == null || payload.isEmpty()) {
				throw new InvalidInputException("Association passed was null or empty");
			}
			ObjectMapper mapper = new ObjectMapper();
			List<Association> associations = null;
			try {
				associations = mapper.readValue(payload, TypeFactory.defaultInstance().constructCollectionType(List.class, Association.class));
			}
			catch (IOException ioException) {
				throw new NonFatalException("Error occurred while parsing the Json.", DisplayMessageConstants.GENERAL_ERROR, ioException);
			}
			try {
				associations = organizationManagementService.addAssociations(MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION,
						unitSettings, associations);
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException("Invalid input exception occurred in adding associations.", DisplayMessageConstants.GENERAL_ERROR, e);
			}
			unitSettings.setAssociations(associations);
			userSettings.setCompanySettings(unitSettings);
			session.setAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION, userSettings);
			LOG.info("Associations updated successfully");
			model.addAttribute("message",
					messageUtils.getDisplayMessage(DisplayMessageConstants.ASSOCIATION_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE));
		}
		catch (NonFatalException nonFatalException) {
			LOG.error("NonFatalException while updating associations. Reason :" + nonFatalException.getMessage(), nonFatalException);
			model.addAttribute("message", messageUtils.getDisplayMessage(nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		return JspResolver.MESSAGE_HEADER;
	}

	/**
	 * Method to update profile addresses in profile
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/updateprofileaddress", method = RequestMethod.POST)
	public String updateProfileAddress(Model model, HttpServletRequest request) {
		LOG.info("Updating contact detail info");
		// Get the profile address parameters
		String name = request.getParameter("profName");
		String address1 = request.getParameter(CommonConstants.ADDRESS1);
		String address2 = request.getParameter(CommonConstants.ADDRESS2);
		try {
			// null checks for parameters
			if (name == null || name.isEmpty()) {
				throw new InvalidInputException("Name passed can not be null or empty", DisplayMessageConstants.GENERAL_ERROR);
			}
			if (address1 == null || address1.isEmpty()) {
				throw new InvalidInputException("Address 1 passed can not be null or empty", DisplayMessageConstants.GENERAL_ERROR);
			}
			HttpSession session = request.getSession(false);
			UserSettings userSettings = (UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);
			OrganizationUnitSettings companySettings = userSettings.getCompanySettings();
			ContactDetailsSettings contactDetailsSettings = companySettings.getContact_details();
			contactDetailsSettings.setName(name);
			contactDetailsSettings.setAddress1(address1);
			contactDetailsSettings.setAddress2(address2);
			try {
				organizationManagementService.updateContactDetails(MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings,
						contactDetailsSettings);
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException("Error occurred while updating contact details.", DisplayMessageConstants.GENERAL_ERROR, e);
			}
			companySettings.setContact_details(contactDetailsSettings);
			userSettings.setCompanySettings(companySettings);
			session.setAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION, userSettings);
			LOG.info("Profile addresses updated successfully");
			model.addAttribute("message",
					messageUtils.getDisplayMessage(DisplayMessageConstants.PROFILE_ADDRESSES_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE));
		}
		catch (NonFatalException nonFatalException) {
			LOG.error("NonFatalException while updating profile address. Reason :" + nonFatalException.getMessage(), nonFatalException);
			model.addAttribute("message", messageUtils.getDisplayMessage(nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		return JspResolver.MESSAGE_HEADER;
	}

	/**
	 * Method to update achievements in profile
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/updateachievements", method = RequestMethod.POST)
	public String updateAchievements(Model model, HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		LOG.info("Updating achievements list");
		String payload = request.getParameter("achievementList");
		UserSettings userSettings = (UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);
		OrganizationUnitSettings unitSettings = userSettings.getCompanySettings();
		try {
			if (payload == null || payload.isEmpty()) {
				throw new InvalidInputException("Association passed was null or empty");
			}
			ObjectMapper mapper = new ObjectMapper();
			List<Achievement> achievements = null;
			try {
				achievements = mapper.readValue(payload, TypeFactory.defaultInstance().constructCollectionType(List.class, Achievement.class));
			}
			catch (IOException ioException) {
				throw new NonFatalException("Error occurred while parsing json", DisplayMessageConstants.GENERAL_ERROR, ioException);
			}
			try {
				achievements = organizationManagementService.addAchievements(MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION,
						unitSettings, achievements);
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException("Error occurred in adding achievements.", DisplayMessageConstants.GENERAL_ERROR, e);
			}
			unitSettings.setAchievements(achievements);
			userSettings.setCompanySettings(unitSettings);
			session.setAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION, userSettings);
			LOG.info("Achievements updated successfully");
			model.addAttribute("message",
					messageUtils.getDisplayMessage(DisplayMessageConstants.ACHIEVEMENT_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE));
		}
		catch (NonFatalException nonFatalException) {
			LOG.error("NonFatalException while updating achievements. Reason :" + nonFatalException.getMessage(), nonFatalException);
			model.addAttribute("message", messageUtils.getDisplayMessage(nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		return JspResolver.MESSAGE_HEADER;
	}

	/**
	 * Method to update licenses for profile
	 * 
	 * @param model
	 * @param request
	 */
	@RequestMapping(value = "/updatelicenses", method = RequestMethod.POST)
	public String updateProfileLicenses(Model model, HttpServletRequest request) {

		HttpSession session = request.getSession(false);
		LOG.info("Update profile licences");
		String payload = request.getParameter("licenceList");
		UserSettings userSettings = (UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);
		OrganizationUnitSettings unitSettings = userSettings.getCompanySettings();
		try {
			if (payload == null || payload.isEmpty()) {
				throw new InvalidInputException("Association passed was null or empty");
			}
			ObjectMapper mapper = new ObjectMapper();
			List<String> authorisedIn = null;
			try {
				authorisedIn = mapper.readValue(payload, TypeFactory.defaultInstance().constructCollectionType(List.class, String.class));
			}
			catch (IOException ioException) {
				throw new NonFatalException("Error occurred while parsing json.", DisplayMessageConstants.GENERAL_ERROR, ioException);
			}
			Licenses licenses = null;
			try {
				licenses = organizationManagementService.addLicences(MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, unitSettings,
						authorisedIn);
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException("Error occurred while adding licenses.", DisplayMessageConstants.GENERAL_ERROR, e);
			}
			unitSettings.setLicenses(licenses);
			userSettings.setCompanySettings(unitSettings);
			session.setAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION, userSettings);
			LOG.info("Licence details updated successfully");
			model.addAttribute("message",
					messageUtils.getDisplayMessage(DisplayMessageConstants.LICENSES_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE));
		}
		catch (NonFatalException nonFatalException) {
			LOG.error("NonFatalException while updating licence details. Reason :" + nonFatalException.getMessage(), nonFatalException);
			model.addAttribute("message", messageUtils.getDisplayMessage(nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		return JspResolver.MESSAGE_HEADER;
	}

	/**
	 * Method to update about profile details
	 * 
	 * @param model
	 * @param request
	 */
	@RequestMapping(value = "/addorupdateaboutme", method = RequestMethod.POST)
	public String addOrUpdateAboutMe(Model model, HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		LOG.info("Update about me details");
		String aboutMe = request.getParameter("aboutMe");
		try {
			UserSettings userSettings = (UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);
			if (userSettings == null) {
				throw new InvalidInputException("No user settings found in session");
			}
			OrganizationUnitSettings unitSettings = userSettings.getCompanySettings();
			if (unitSettings == null) {
				throw new InvalidInputException("No company settings found in current session");
			}
			ContactDetailsSettings contactDetailsSettings = unitSettings.getContact_details();
			contactDetailsSettings.setAbout_me(aboutMe);
			try {
				contactDetailsSettings = organizationManagementService.updateContactDetails(
						MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, unitSettings, contactDetailsSettings);
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException("Error occurred while updating about me details.", DisplayMessageConstants.GENERAL_ERROR, e);
			}
			unitSettings.setContact_details(contactDetailsSettings);
			userSettings.setCompanySettings(unitSettings);
			session.setAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION, userSettings);
			LOG.info("About me details updated successfully");
			model.addAttribute("message",
					messageUtils.getDisplayMessage(DisplayMessageConstants.ABOUT_ME_DETAILS_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE));
		}
		catch (NonFatalException nonFatalException) {
			LOG.error("NonFatalException while updating licence details. Reason :" + nonFatalException.getMessage(), nonFatalException);
			model.addAttribute("message", messageUtils.getDisplayMessage(nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		return JspResolver.MESSAGE_HEADER;
	}

	/**
	 * Method to add or update profile logo
	 * 
	 * @param model
	 * @param request
	 * @param fileLocal
	 */
	@RequestMapping(value = "/addoruploadlogo", method = RequestMethod.POST)
	public String addOrUpdateLogo(Model model, HttpServletRequest request, @RequestParam("logo") MultipartFile fileLocal) {
		HttpSession session = request.getSession(false);
		LOG.info("Update profile logo");
		String logoName = "";
		String logoFileName = request.getParameter("logoFileName");
		try {
			UserSettings userSettings = (UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);
			if (userSettings == null) {
				throw new InvalidInputException("No user settings found in session");
			}
			OrganizationUnitSettings unitSettings = userSettings.getCompanySettings();
			if (unitSettings == null) {
				throw new InvalidInputException("No company settings found in current session");
			}
			try {
				logoName = fileUploadService.fileUploadHandler(fileLocal, logoFileName);
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException("Error occurred while uploading logo.", DisplayMessageConstants.GENERAL_ERROR, e);
			}
			try {
				organizationManagementService.updateLogo(MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, unitSettings, logoName);
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException("Error occurred while updating logo.", DisplayMessageConstants.GENERAL_ERROR, e);
			}
			unitSettings.setLogo(logoName);
			userSettings.setCompanySettings(unitSettings);
			session.setAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION, userSettings);
			sessionHelper.setLogoInSession(session, userSettings);
			LOG.info("About me details updated successfully");
			model.addAttribute("message",
					messageUtils.getDisplayMessage(DisplayMessageConstants.LOGO_UPLOAD_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE));
		}
		catch (NonFatalException nonFatalException) {
			LOG.error("NonFatalException while updating licence details. Reason :" + nonFatalException.getMessage(), nonFatalException);
			model.addAttribute("message", messageUtils.getDisplayMessage(nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		return JspResolver.MESSAGE_HEADER;
	}

	/**
	 * Method to update email id for profile
	 * 
	 * @param model
	 * @param request
	 */
	@RequestMapping(value = "/updateemailids", method = RequestMethod.POST)
	public String updateEmailds(Model model, HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		LOG.info("Update mail ids");
		try {
			String payload = request.getParameter("mailIds");
			if (payload == null || payload.isEmpty()) {
				throw new InvalidInputException("Maild ids passed was null or empty");
			}
			UserSettings userSettings = (UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);
			if (userSettings == null) {
				throw new InvalidInputException("No user settings found in session");
			}
			OrganizationUnitSettings unitSettings = userSettings.getCompanySettings();
			if (unitSettings == null) {
				throw new InvalidInputException("No company settings found in current session");
			}
			ContactDetailsSettings contactDetailsSettings = unitSettings.getContact_details();
			if (contactDetailsSettings == null) {
				throw new InvalidInputException("No contact details object found for user");
			}
			MailIdSettings mailIdSettings = contactDetailsSettings.getMail_ids();
			if (mailIdSettings == null) {
				LOG.debug("No maild ids added, create new mail id object in contact details");
				mailIdSettings = new MailIdSettings();
			}
			ObjectMapper mapper = new ObjectMapper();
			List<MiscValues> mailIds = null;
			try {
				mailIds = mapper.readValue(payload, TypeFactory.defaultInstance().constructCollectionType(List.class, MiscValues.class));
			}
			catch (IOException ioException) {
				throw new NonFatalException("Error occurred while parsing json.", DisplayMessageConstants.GENERAL_ERROR, ioException);
			}
			List<MiscValues> others = null;
			for (MiscValues mailId : mailIds) {
				String key = mailId.getKey();
				String value = mailId.getValue();
				if (key.equalsIgnoreCase("work")) {
					mailIdSettings.setWork(value);
				}
				else if (key.equalsIgnoreCase("personal")) {
					mailIdSettings.setPersonal(value);
				}
				else {
					if (others == null) {
						others = new ArrayList<>();
					}
					others.add(mailId);
				}
			}
			mailIdSettings.setOthers(others);
			contactDetailsSettings.setMail_ids(mailIdSettings);
			try {
				organizationManagementService.updateContactDetails(MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, unitSettings,
						contactDetailsSettings);
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException("Invalid input exception ocurred while updating mail ids in contact details",
						DisplayMessageConstants.GENERAL_ERROR, e);
			}
			userSettings.setCompanySettings(unitSettings);
			session.setAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION, userSettings);
			LOG.info("Maild ids updated successfully");
			model.addAttribute("message",
					messageUtils.getDisplayMessage(DisplayMessageConstants.MAIL_IDS_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE));
		}
		catch (NonFatalException nonFatalException) {
			LOG.error("NonFatalException while updating emaild ids in contact details. Reason :" + nonFatalException.getMessage(), nonFatalException);
			model.addAttribute("message", messageUtils.getDisplayMessage(nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		return JspResolver.MESSAGE_HEADER;
	}

	/**
	 * Method to update phone numbers of a profile
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/updatephonenumbers", method = RequestMethod.POST)
	public String updatePhoneNumbers(Model model, HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		LOG.info("Update phone numbers");
		try {
			String payload = request.getParameter("phoneNumbers");
			if (payload == null || payload.isEmpty()) {
				throw new InvalidInputException("Phone numbers passed was null or empty");
			}
			UserSettings userSettings = (UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);
			if (userSettings == null) {
				throw new InvalidInputException("No user settings found in session");
			}
			OrganizationUnitSettings unitSettings = userSettings.getCompanySettings();
			if (unitSettings == null) {
				throw new InvalidInputException("No company settings found in current session");
			}
			ContactDetailsSettings contactDetailsSettings = unitSettings.getContact_details();
			if (contactDetailsSettings == null) {
				throw new InvalidInputException("No contact details object found for user");
			}
			ContactNumberSettings phoneNumberSettings = contactDetailsSettings.getContact_numbers();
			if (phoneNumberSettings == null) {
				LOG.debug("No phone numbers are added, create phone numbers object in contact details");
				phoneNumberSettings = new ContactNumberSettings();
			}
			ObjectMapper mapper = new ObjectMapper();
			List<MiscValues> phoneNumbers = null;
			try {
				phoneNumbers = mapper.readValue(payload, TypeFactory.defaultInstance().constructCollectionType(List.class, MiscValues.class));
			}
			catch (IOException ioException) {
				throw new NonFatalException("Error occurred while parsing json.", DisplayMessageConstants.GENERAL_ERROR, ioException);
			}
			List<MiscValues> others = null;
			for (MiscValues phoneNumber : phoneNumbers) {
				String key = phoneNumber.getKey();
				String value = phoneNumber.getValue();
				if (key.equalsIgnoreCase("work")) {
					phoneNumberSettings.setWork(value);
				}
				else if (key.equalsIgnoreCase("personal")) {
					phoneNumberSettings.setPersonal(value);
				}
				else if (key.equalsIgnoreCase("fax")) {
					phoneNumberSettings.setFax(value);
				}
				else {
					if (others == null) {
						others = new ArrayList<>();
					}
					others.add(phoneNumber);
				}
			}
			phoneNumberSettings.setOthers(others);
			contactDetailsSettings.setContact_numbers(phoneNumberSettings);
			try {
				organizationManagementService.updateContactDetails(MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, unitSettings,
						contactDetailsSettings);
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException("Invalid input exception ocurred while updating mail ids in contact details",
						DisplayMessageConstants.GENERAL_ERROR, e);
			}
			userSettings.setCompanySettings(unitSettings);
			session.setAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION, userSettings);
			LOG.info("Contact numbers updated successfully");
			model.addAttribute("message",
					messageUtils.getDisplayMessage(DisplayMessageConstants.CONTACT_NUMBERS_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE));
		}
		catch (NonFatalException nonFatalException) {
			LOG.error("NonFatalException while updating phone numbers in contact details. Reason :" + nonFatalException.getMessage(),
					nonFatalException);
			model.addAttribute("message", messageUtils.getDisplayMessage(nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		return JspResolver.MESSAGE_HEADER;
	}

	/**
	 * Method to update web addresses for a profile
	 * 
	 * @param model
	 * @param request
	 */
	@RequestMapping(value = "/updatewebaddresses", method = RequestMethod.POST)
	public String updateWebAddresses(Model model, HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		LOG.info("Update web addresses");
		try {
			String payload = request.getParameter("webAddresses");
			if (payload == null || payload.isEmpty()) {
				throw new InvalidInputException("Web addresses passed was null or empty");
			}
			UserSettings userSettings = (UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);
			if (userSettings == null) {
				throw new InvalidInputException("No user settings found in session");
			}
			OrganizationUnitSettings unitSettings = userSettings.getCompanySettings();
			if (unitSettings == null) {
				throw new InvalidInputException("No company settings found in current session");
			}
			ContactDetailsSettings contactDetailsSettings = unitSettings.getContact_details();
			if (contactDetailsSettings == null) {
				throw new InvalidInputException("No contact details object found for user");
			}
			WebAddressSettings webAddressSettings = contactDetailsSettings.getWeb_addresses();
			if (webAddressSettings == null) {
				LOG.debug("No web addresses are added, create new web address object in contact details");
				webAddressSettings = new WebAddressSettings();
			}
			ObjectMapper mapper = new ObjectMapper();
			List<MiscValues> webAddresses = null;
			try {
				webAddresses = mapper.readValue(payload, TypeFactory.defaultInstance().constructCollectionType(List.class, MiscValues.class));
			}
			catch (IOException ioException) {
				throw new NonFatalException("Error occurred while parsing json.", DisplayMessageConstants.GENERAL_ERROR, ioException);
			}
			List<MiscValues> others = null;
			for (MiscValues webAddress : webAddresses) {
				String key = webAddress.getKey();
				String value = webAddress.getValue();
				if (key.equalsIgnoreCase("work")) {
					webAddressSettings.setWork(value);
				}
				else if (key.equalsIgnoreCase("personal")) {
					webAddressSettings.setPersonal(value);
				}
				else {
					if (others == null) {
						others = new ArrayList<>();
					}
					others.add(webAddress);
				}
			}
			webAddressSettings.setOthers(webAddresses);
			contactDetailsSettings.setWeb_addresses(webAddressSettings);
			try {
				organizationManagementService.updateContactDetails(MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, unitSettings,
						contactDetailsSettings);
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException("Invalid input exception ocurred while updating web addresses in contact details",
						DisplayMessageConstants.GENERAL_ERROR, e);
			}
			userSettings.setCompanySettings(unitSettings);
			session.setAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION, userSettings);
			LOG.info("Web addresses updated successfully");
			model.addAttribute("message",
					messageUtils.getDisplayMessage(DisplayMessageConstants.WEB_ADDRESSES_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE));
		}
		catch (NonFatalException nonFatalException) {
			LOG.error("NonFatalException while updating web addresses in contact details. Reason :" + nonFatalException.getMessage(),
					nonFatalException);
			model.addAttribute("message", messageUtils.getDisplayMessage(nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		return JspResolver.MESSAGE_HEADER;
	}
	// JIRA SS-97 by RM-06 : EOC

}
// JIRA: SS-24 BY RM02 EOC