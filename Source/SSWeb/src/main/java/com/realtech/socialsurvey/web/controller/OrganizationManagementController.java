package com.realtech.socialsurvey.web.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.AccountsMaster;
import com.realtech.socialsurvey.core.entities.EncompassCrmInfo;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.entities.MailContentSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SurveySettings;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.entities.AbridgedUserProfile;
import com.realtech.socialsurvey.core.entities.UserSettings;
import com.realtech.socialsurvey.core.entities.VerticalsMaster;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.payment.Payment;
import com.realtech.socialsurvey.core.services.payment.exception.CreditCardException;
import com.realtech.socialsurvey.core.services.payment.exception.PaymentException;
import com.realtech.socialsurvey.core.services.payment.exception.SubscriptionPastDueException;
import com.realtech.socialsurvey.core.services.payment.exception.SubscriptionUnsuccessfulException;
import com.realtech.socialsurvey.core.services.payment.exception.SubscriptionUpgradeUnsuccessfulException;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyBuilder;
import com.realtech.socialsurvey.core.services.upload.FileUploadService;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.EmailFormatHelper;
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
	private ProfileManagementService profileManagementService;

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

	@Autowired
	private EmailFormatHelper emailFormatHelper;

	@Autowired
	private SurveyBuilder surveyBuilder;
	
	@Autowired
	private Payment payment;

	@Value("${AMAZON_ENDPOINT}")
	private String endpoint;

	@Value("${AMAZON_BUCKET}")
	private String bucket;

	/**
	 * Method to upload logo image for a company
	 * 
	 * @param fileLocal
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping(value = "/uploadcompanylogo", method = RequestMethod.POST)
	public String imageUpload(Model model, @RequestParam("logo") MultipartFile fileLocal, HttpServletRequest request) {
		LOG.info("Method imageUpload of OrganizationManagementController called");
		String message = "";
		String logoName = "";

		LOG.debug("Overriding Logo image name in Session");
		if (request.getSession(false).getAttribute(CommonConstants.LOGO_NAME) != null) {
			request.getSession(false).removeAttribute(CommonConstants.LOGO_NAME);
		}

		try {
			logoName = fileUploadService.fileUploadHandler(fileLocal, request.getParameter("logo_name"));
			// Setting the complete logo url in session
			logoName = endpoint + "/" + bucket + "/" + logoName;

			LOG.debug("Setting Logo image name to Session");
			request.getSession(false).setAttribute(CommonConstants.LOGO_NAME, logoName);

			LOG.info("Method imageUpload of OrganizationManagementController completed successfully");
			message = messageUtils.getDisplayMessage("LOGO_UPLOAD_SUCCESSFUL", DisplayMessageType.SUCCESS_MESSAGE).getMessage();
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while uploading Logo. Reason :" + e.getMessage(), e);
			message = e.getMessage();
		}
		return message;
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
		String country = request.getParameter("country");
		String countryCode = request.getParameter("countrycode");
		String zipCode = request.getParameter("zipcode");
		String state = request.getParameter("state");
		String city = request.getParameter("city");
		String companyContactNo = request.getParameter("contactno");
		String vertical = request.getParameter("vertical");
		// JIRA SS-536: Added for manual registration via invitation
		String strIsDirectRegistration = request.getParameter("isDirectRegistration");

		try {
			try {
				validateCompanyInfoParams(companyName, address1, country, countryCode, zipCode, companyContactNo, vertical);
			}
			catch (InvalidInputException e) {
				List<VerticalsMaster> verticalsMasters = null;
				try {
					verticalsMasters = organizationManagementService.getAllVerticalsMaster();
					model.addAttribute("verticals", verticalsMasters);

					model.addAttribute("companyName", companyName);
					model.addAttribute("address1", address1);
					model.addAttribute("address2", address2);
					model.addAttribute("country", country);
					model.addAttribute("countryCode", countryCode);
					model.addAttribute("zipCode", zipCode);
					model.addAttribute("state", state);
					model.addAttribute("city", city);
					model.addAttribute("companyContactNo", companyContactNo);
					model.addAttribute("isDirectRegistration", strIsDirectRegistration);
				}
				catch (InvalidInputException e1) {
					throw new InvalidInputException("Invalid Input exception occured in method getAllVerticalsMaster()",
							DisplayMessageConstants.GENERAL_ERROR, e1);
				}

				throw new InvalidInputException("Invalid input exception occured while validating form parameters", e.getErrorCode(), e);
			}

			HttpSession session = request.getSession(false);
			User user = sessionHelper.getCurrentUser();
			String logoName = null;
			if (session.getAttribute(CommonConstants.LOGO_NAME) != null) {
				logoName = session.getAttribute(CommonConstants.LOGO_NAME).toString();
			}
			session.removeAttribute(CommonConstants.LOGO_NAME);

			Map<String, String> companyDetails = new HashMap<String, String>();
			companyDetails.put(CommonConstants.COMPANY_NAME, companyName);
			companyDetails.put(CommonConstants.ADDRESS, getCompleteAddress(address1, address2));
			companyDetails.put(CommonConstants.ADDRESS1, address1);
			if (address2 != null) {
				companyDetails.put(CommonConstants.ADDRESS2, address2);
			}
			companyDetails.put(CommonConstants.COUNTRY, country);
			companyDetails.put(CommonConstants.STATE, state);
			companyDetails.put(CommonConstants.CITY, city);
			companyDetails.put(CommonConstants.COUNTRY_CODE, countryCode);
			companyDetails.put(CommonConstants.ZIPCODE, zipCode);
			companyDetails.put(CommonConstants.COMPANY_CONTACT_NUMBER, companyContactNo);
			if (logoName != null) {
				companyDetails.put(CommonConstants.LOGO_NAME, logoName);
			}
			companyDetails.put(CommonConstants.VERTICAL, vertical);
			// JIRA SS-536: Added for manual registration via invitation
			if (strIsDirectRegistration.equalsIgnoreCase("false")) {
				companyDetails.put(CommonConstants.BILLING_MODE_COLUMN, CommonConstants.BILLING_MODE_INVOICE);
				model.addAttribute("skippayment","true");
			}
			else {
				companyDetails.put(CommonConstants.BILLING_MODE_COLUMN, CommonConstants.BILLING_MODE_AUTO);
				model.addAttribute("skippayment","false");
			}

			LOG.debug("Calling services to add company details");
			user = organizationManagementService.addCompanyInformation(user, companyDetails);

			LOG.debug("Updating profile completion stage");
			userManagementService.updateProfileCompletionStage(user, CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID,
					CommonConstants.ADD_ACCOUNT_TYPE_STAGE);
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while adding company information. Reason :" + e.getMessage(), e);
			model.addAttribute("status", DisplayMessageType.ERROR_MESSAGE);
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
	private void validateCompanyInfoParams(String companyName, String address, String country, String countryCode, String zipCode,
			String companyContactNo, String vertical) throws InvalidInputException {
		LOG.debug("Method validateCompanyInfoParams called  for companyName : " + companyName + " address : " + address + " zipCode : " + zipCode
				+ " companyContactNo : " + companyContactNo);

		if (companyName == null || companyName.isEmpty() || companyName.contains("\"")) {
			throw new InvalidInputException("Company name is null or empty while adding company information",
					DisplayMessageConstants.INVALID_COMPANY_NAME);
		}
		if (address == null || address.isEmpty()) {
			throw new InvalidInputException("Address is null or empty while adding company information", DisplayMessageConstants.INVALID_ADDRESS);
		}

		if (country == null || country.isEmpty()) {
			throw new InvalidInputException("Country is null or empty while adding company information", DisplayMessageConstants.INVALID_COUNTRY);
		}

		if (countryCode == null || countryCode.isEmpty()) {
			throw new InvalidInputException("Country code is null or empty while adding company information", DisplayMessageConstants.INVALID_COUNTRY);
		}

		if (zipCode == null || zipCode.isEmpty()) {
			throw new InvalidInputException("Zipcode is not valid while adding company information", DisplayMessageConstants.INVALID_ZIPCODE);
		}

		if (companyContactNo == null || companyContactNo.isEmpty() || !companyContactNo.matches(CommonConstants.PHONENUMBER_REGEX)) {
			throw new InvalidInputException("Company contact number is not valid while adding company information",
					DisplayMessageConstants.INVALID_COMPANY_PHONEN0);
		}

		if (vertical == null || vertical.isEmpty()) {
			throw new InvalidInputException("Vertical selected is not valid", DisplayMessageConstants.INVALID_VERTICAL);
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
	public String addAccountType(Model model, HttpServletRequest request, HttpServletResponse response) {
		LOG.info("Method addAccountType of UserManagementController called");
		String strAccountType = request.getParameter("accounttype");
		String returnPage = null;
		try {
			if (strAccountType == null || strAccountType.isEmpty()) {
				throw new InvalidInputException("Accounttype is null for adding account type", DisplayMessageConstants.INVALID_ADDRESS);
			}
			LOG.debug("AccountType obtained : " + strAccountType);

			User user = sessionHelper.getCurrentUser();

			// JIRA - SS-536
			
			// We check if there is mapped survey for the company and add a default survey if
			// not.
			if (surveyBuilder.checkForExistingSurvey(user) == null) {
				surveyBuilder.addDefaultSurveyToCompany(user);
			}
			
			// check the company and see if manual registaration. Skip payment in that case
			if (user.getCompany().getBillingMode().equals(CommonConstants.BILLING_MODE_INVOICE)) {
				// do what is done after payment
				// insert into license table
				// the account type is the accounts master id
				payment.insertIntoLicenseTable(Integer.parseInt(strAccountType), user, CommonConstants.INVOICE_BILLED_DEFULAT_SUBSCRIPTION_ID);
				// set profile completion flag for the company admin
				LOG.debug("Calling sevices for updating profile completion stage");
				userManagementService.updateProfileCompletionStage(user, CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID,
						CommonConstants.PRE_PROCESSING_BEFORE_LOGIN_STAGE);
				LOG.debug("Successfully executed sevices for updating profile completion stage");
				returnPage = "redirect:./" + CommonConstants.PRE_PROCESSING_BEFORE_LOGIN_STAGE;
			}
			else {

				LOG.debug("Checking if payment has already been made.");
				if (gateway.checkIfPaymentMade(user.getCompany())
						&& user.getCompany().getLicenseDetails().get(CommonConstants.INITIAL_INDEX).getAccountsMaster().getAccountsMasterId() != CommonConstants.ACCOUNTS_MASTER_FREE) {
					LOG.debug("Payment for this company has already been made. Redirecting to dashboard.");
					return JspResolver.PAYMENT_ALREADY_MADE;
				}

				if (Integer.parseInt(strAccountType) == CommonConstants.ACCOUNTS_MASTER_FREE) {
					LOG.debug("Since its a free account type returning no popup jsp");
					return null;
				}

				model.addAttribute("accounttype", strAccountType);
				model.addAttribute("clienttoken", gateway.getClientToken());
				model.addAttribute("message",
						messageUtils.getDisplayMessage(DisplayMessageConstants.ACCOUNT_TYPE_SELECTION_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE));

				LOG.info("Method addAccountType of UserManagementController completed successfully");
				returnPage = JspResolver.PAYMENT;
			}
		}
		catch (NonFatalException e) {
			LOG.error("NonfatalException while adding account type. Reason: " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.MESSAGE_HEADER;
		}
		return returnPage;

	}

	/**
	 * Method to show Company settings on edit company
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/showcompanysettings", method = RequestMethod.GET)
	public String showCompanySettings(Model model, HttpServletRequest request) {
		LOG.info("Method showCompanySettings of UserManagementController called");
		HttpSession session = request.getSession(false);
		User user = sessionHelper.getCurrentUser();

		AccountType accountType = (AccountType) session.getAttribute(CommonConstants.ACCOUNT_TYPE_IN_SESSION);
		UserSettings userSettings = (UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);

		UserProfile selectedProfile = null;
		String profileIdStr = request.getParameter("profileId");
		if (profileIdStr == null) {
			selectedProfile = (UserProfile) session.getAttribute(CommonConstants.USER_PROFILE);
		}
		if (selectedProfile == null) {
			Map<Long, UserProfile> profileMap = (Map<Long, UserProfile>) session.getAttribute(CommonConstants.USER_PROFILE_MAP);
			Map<Long, AbridgedUserProfile> profileAbridgedMap = (Map<Long, AbridgedUserProfile>) session
					.getAttribute(CommonConstants.USER_PROFILE_LIST);

			selectedProfile = userManagementService.updateSelectedProfile(user, accountType, profileMap, profileIdStr);

			session.setAttribute(CommonConstants.USER_PROFILE, selectedProfile);
			session.setAttribute(CommonConstants.PROFILE_NAME_COLUMN, profileAbridgedMap.get(selectedProfile.getUserProfileId()).getUserProfileName());
		}

		OrganizationUnitSettings unitSettings = null;
		int profileMasterId = selectedProfile.getProfilesMaster().getProfileId();
		int accountMasterId = accountType.getValue();
		if (profileMasterId == CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID
				|| accountMasterId == CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL) {
			unitSettings = userSettings.getCompanySettings();
			if(unitSettings != null && unitSettings.getSocialMediaTokens() != null && 
					unitSettings.getSocialMediaTokens().getFacebookToken()!= null && 
					unitSettings.getSocialMediaTokens().getFacebookToken().getFacebookPageLink() != null){
				model.addAttribute("facebookLink", unitSettings.getSocialMediaTokens().getFacebookToken().getFacebookPageLink());
			}
			else{
				model.addAttribute("facebookLink", "");
			}
		}
		else if (profileMasterId == CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID) {
			unitSettings = userSettings.getRegionSettings().get(selectedProfile.getRegionId());
			if(unitSettings != null && unitSettings.getSocialMediaTokens() != null && 
					unitSettings.getSocialMediaTokens().getTwitterToken()!= null && 
					unitSettings.getSocialMediaTokens().getTwitterToken().getTwitterPageLink() != null){
				model.addAttribute("twitterLink", unitSettings.getSocialMediaTokens().getTwitterToken().getTwitterPageLink());
			}
			else{
				model.addAttribute("twitterLink", "");
			}
		}
		else if (profileMasterId == CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID) {
			unitSettings = userSettings.getBranchSettings().get(selectedProfile.getBranchId());
		}
		else if (profileMasterId == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID) {
			unitSettings = userSettings.getAgentSettings();
		}
		
		model.addAttribute("facebookLink", "");
		model.addAttribute("twitterLink", "");
		model.addAttribute("linkedinLink", "");
		model.addAttribute("googleLink", "");
		
		if(unitSettings != null && unitSettings.getSocialMediaTokens() != null) {
			
			if( unitSettings.getSocialMediaTokens().getFacebookToken()!= null && 
					unitSettings.getSocialMediaTokens().getFacebookToken().getFacebookPageLink() != null){
				model.addAttribute("facebookLink", unitSettings.getSocialMediaTokens().getFacebookToken().getFacebookPageLink());
			}
			if( unitSettings.getSocialMediaTokens().getTwitterToken()!= null && 
					unitSettings.getSocialMediaTokens().getTwitterToken().getTwitterPageLink() != null){
				model.addAttribute("twitterLink", unitSettings.getSocialMediaTokens().getTwitterToken().getTwitterPageLink());
			}
			if( unitSettings.getSocialMediaTokens().getLinkedInToken()!= null && 
					unitSettings.getSocialMediaTokens().getLinkedInToken().getLinkedInPageLink() != null){
				model.addAttribute("linkedinLink", unitSettings.getSocialMediaTokens().getLinkedInToken().getLinkedInPageLink());
			}
			if( unitSettings.getSocialMediaTokens().getGoogleToken()!= null && 
					unitSettings.getSocialMediaTokens().getGoogleToken().getProfileLink() != null){
				model.addAttribute("googleLink", unitSettings.getSocialMediaTokens().getGoogleToken().getProfileLink());
			}
		}
		
		
		session.setAttribute(CommonConstants.USER_ACCOUNT_SETTINGS, unitSettings);
		return JspResolver.EDIT_SETTINGS;
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

			EncompassCrmInfo encompassCrmInfo = new EncompassCrmInfo();
			encompassCrmInfo.setCrm_source(CommonConstants.CRM_INFO_SOURCE_ENCOMPASS);
			encompassCrmInfo.setCrm_username(request.getParameter("encompass-username"));
			encompassCrmInfo.setCrm_password(cipherPassword);
			encompassCrmInfo.setUrl(request.getParameter("encompass-url"));
			encompassCrmInfo.setConnection_successful(true);
			OrganizationUnitSettings companySettings = ((UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION))
					.getCompanySettings();
			organizationManagementService.updateCRMDetails(companySettings, encompassCrmInfo);

			// set the updated settings value in session with plain password
			encompassCrmInfo.setCrm_password(plainPassword);
			companySettings.setCrm_info(encompassCrmInfo);
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
		String autopost = request.getParameter("autopost");
		SurveySettings originalSurveySettings = null;
		SurveySettings surveySettings = null;
		String message = "";

		try {
			boolean isAutopostEnabled = Boolean.parseBoolean(autopost);
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
					surveySettings.setAutoPostEnabled(isAutopostEnabled);
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
					surveySettings.setAutoPostEnabled(isAutopostEnabled);
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

	@RequestMapping(value = "/updateautopostforsurvey", method = RequestMethod.POST)
	@ResponseBody
	public String updateAutoPostForSurvey(HttpServletRequest request) {
		LOG.info("Method to update autopost for a survey started");
		try {
			HttpSession session = request.getSession(false);
			String autopost = request.getParameter("autopost");
			boolean isAutoPostEnabled = false;
			if (autopost != null && !autopost.isEmpty()) {
				isAutoPostEnabled = Boolean.parseBoolean(autopost);
				OrganizationUnitSettings companySettings = ((UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION))
						.getCompanySettings();
				SurveySettings surveySettings = companySettings.getSurvey_settings();
				surveySettings.setAutoPostEnabled(isAutoPostEnabled);
				if (organizationManagementService.updateSurveySettings(companySettings, surveySettings)) {
					companySettings.setSurvey_settings(surveySettings);
					LOG.info("Updated Survey Settings");
				}
			}
		}
		catch (Exception e) {
			LOG.error("Exception occured in updateAutoPostForSurvey() while updating whether to enable autopost or not. Nested exception is ", e);
			return e.getMessage();
		}

		LOG.info("Method to update autopost for a survey finished");
		return "Successfully updated autopost setting";
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

	private String makeJsonMessage(int status, String message) {

		JSONObject jsonMessage = new JSONObject();
		LOG.debug("Building json response");
		try {
			jsonMessage.put("success", status);
			jsonMessage.put("message", message);
		}
		catch (JSONException e) {
			LOG.error("Exception occured while building json response : " + e.getMessage(), e);
		}

		LOG.info("Returning json response : " + jsonMessage.toString());
		return jsonMessage.toString();
	}

	/**
	 * Method to upgrade a plan
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/upgradeplan", method = RequestMethod.POST)
	@ResponseBody
	public Object upgradePlanForUserInSession(HttpServletRequest request, Model model) {
		LOG.info("Upgrading the user's subscription");
		String accountType = request.getParameter(CommonConstants.ACCOUNT_TYPE_IN_SESSION);
		String nonce = request.getParameter(CommonConstants.PAYMENT_NONCE);
		String message = null;

		LOG.info("Fetching the user in session");
		User user = sessionHelper.getCurrentUser();

		try {
			LOG.info("Making the braintree API call to upgrade and updating the database!");

			if (accountType == null || accountType.isEmpty()) {
				LOG.error("Account type parameter passed is null or empty");
				throw new InvalidInputException("Account type parameter passed is null or empty", DisplayMessageConstants.GENERAL_ERROR);
			}

			int newAccountsMasterId = 0;
			try {
				newAccountsMasterId = Integer.parseInt(accountType);
			}
			catch (NumberFormatException e) {
				LOG.error("Error while parsing account type ");
				throw new InvalidInputException("Error while parsing account type ", DisplayMessageConstants.GENERAL_ERROR, e);
			}
			LOG.info("Making the API call to upgrade");
			gateway.upgradePlanForSubscription(user, newAccountsMasterId, nonce);
			LOG.info("Upgrade successful");

			switch (newAccountsMasterId) {
				case CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL:
					message = messageUtils.getDisplayMessage(DisplayMessageConstants.TO_INDIVIDUAL_SUBSCRIPTION_UPGRADE_SUCCESSFUL,
							DisplayMessageType.SUCCESS_MESSAGE).getMessage();
					break;
				case CommonConstants.ACCOUNTS_MASTER_TEAM:
					message = messageUtils.getDisplayMessage(DisplayMessageConstants.TO_TEAM_SUBSCRIPTION_UPGRADE_SUCCESSFUL,
							DisplayMessageType.SUCCESS_MESSAGE).getMessage();
					break;
				case CommonConstants.ACCOUNTS_MASTER_COMPANY:
					message = messageUtils.getDisplayMessage(DisplayMessageConstants.TO_COMPANY_SUBSCRIPTION_UPGRADE_SUCCESSFUL,
							DisplayMessageType.SUCCESS_MESSAGE).getMessage();
					break;
				case CommonConstants.ACCOUNTS_MASTER_ENTERPRISE:
					message = messageUtils.getDisplayMessage(DisplayMessageConstants.TO_ENTERPRISE_SUBSCRIPTION_UPGRADE_SUCCESSFUL,
							DisplayMessageType.SUCCESS_MESSAGE).getMessage();
					break;
			}
			LOG.info("message returned : " + message);
		}
		catch (InvalidInputException | NoRecordsFetchedException | SolrException | UndeliveredEmailException e) {
			LOG.error("NonFatalException while upgrading subscription. Message : " + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(null, DisplayMessageType.ERROR_MESSAGE).getMessage();

			return makeJsonMessage(CommonConstants.STATUS_INACTIVE, message);

		}
		catch (PaymentException e) {
			LOG.error("NonFatalException while upgrading subscription. Message : " + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();

			return makeJsonMessage(CommonConstants.STATUS_INACTIVE, message);
		}
		catch (SubscriptionPastDueException e) {
			LOG.error("SubscriptionPastDueException while upgrading subscription. Message : " + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();

			return makeJsonMessage(CommonConstants.STATUS_INACTIVE, message);
		}
		catch (SubscriptionUpgradeUnsuccessfulException e) {
			LOG.error("SubscriptionUpgradeUnsuccessfulException while upgrading subscription. Message : " + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();

			return makeJsonMessage(CommonConstants.STATUS_INACTIVE, message);
		}
		catch (CreditCardException e) {
			LOG.error("Exception has occured : " + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
			return makeJsonMessage(CommonConstants.STATUS_INACTIVE, message);
		}
		catch (SubscriptionUnsuccessfulException e) {
			LOG.error("Exception has occured : " + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
			return makeJsonMessage(CommonConstants.STATUS_INACTIVE, message);
		}

		// After all the updates are done we set the account type in the session to reflect changes
		HttpSession session = request.getSession();
		session.setAttribute(CommonConstants.ACCOUNT_TYPE_IN_SESSION, AccountType.getAccountType(Integer.parseInt(accountType)));

		LOG.info("returning message : " + message);
		return makeJsonMessage(CommonConstants.STATUS_ACTIVE, message);
	}

	/**
	 * Method for displaying the upgrade page
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/upgradepage", method = RequestMethod.GET)
	public String upgradePage(HttpServletRequest request, Model model) {

		LOG.info("Upgrade page requested.");

		LOG.debug("Retrieveing the user from session to get his current plan details");
		User user = sessionHelper.getCurrentUser();
		LicenseDetail currentLicenseDetail = user.getCompany().getLicenseDetails().get(CommonConstants.INITIAL_INDEX);

		LOG.debug("Adding the current plan in the model and the upgrade flag");
		model.addAttribute(CommonConstants.CURRENT_LICENSE_ID, currentLicenseDetail.getAccountsMaster().getAccountsMasterId());
		model.addAttribute(CommonConstants.UPGRADE_FLAG, CommonConstants.YES);

		LOG.info("Returning the upgrade account selection page");
		return JspResolver.ACCOUNT_TYPE_SELECTION;

	}

	/**
	 * Method for displaying the upgrade page to upgrade from free account
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/upgradetopaidplanpage", method = RequestMethod.GET)
	public String upgradeToPaidPlanPage(HttpServletRequest request, Model model) {

		LOG.info("Upgrade page requested.");

		LOG.debug("Retrieveing the user from session to get his current plan details");

		LOG.debug("Adding the current plan in the model and the upgrade flag");
		model.addAttribute(CommonConstants.PAID_PLAN_UPGRADE_FLAG, CommonConstants.YES);

		LOG.info("Returning the upgrade account selection page");
		return JspResolver.ACCOUNT_TYPE_SELECTION;

	}

	/**
	 * Returns the upgrade confirmation page
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/upgradeconfirmation", method = RequestMethod.POST)
	public String getUpgradeConfirmationPage(HttpServletRequest request, Model model) {

		LOG.info("Upgrade confirmation page requested");
		String accountType = request.getParameter(CommonConstants.ACCOUNT_TYPE_IN_SESSION);
		User user = sessionHelper.getCurrentUser();

		try {
			// We need to calculate the balance amount and put it into the model
			if (accountType == null || accountType.isEmpty()) {
				LOG.error("Account type parameter passed is null or empty");
				throw new InvalidInputException("Account type parameter passed is null or empty", DisplayMessageConstants.GENERAL_ERROR);
			}

			int toAccountsMasterId = 0;
			try {
				toAccountsMasterId = Integer.parseInt(accountType);
			}
			catch (NumberFormatException e) {
				LOG.error("Error while parsing account type ");
				throw new InvalidInputException("Error while parsing account type ", DisplayMessageConstants.GENERAL_ERROR, e);
			}
			int fromAccountsMasterId = user.getCompany().getLicenseDetails().get(CommonConstants.INITIAL_INDEX).getAccountsMaster()
					.getAccountsMasterId();

			model.addAttribute("balanceAmount",
					String.format("%.02f", gateway.getBalacnceAmountForPlanUpgrade(user.getCompany(), fromAccountsMasterId, toAccountsMasterId)));
			model.addAttribute(CommonConstants.ACCOUNT_TYPE_IN_SESSION, toAccountsMasterId);
		}
		catch (InvalidInputException e) {
			LOG.error("Exception has occured : " + e.getMessage(), e);
		}

		LOG.info("Returning the confirmation page");
		return JspResolver.UPGRADE_CONFIRMATION;
	}

	/**
	 * This controller is called to initialize the default branches and regions in case they arent
	 * done after payment.
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/defaultbrandandregioncreation", method = RequestMethod.GET)
	public String createDefaultBranchesAndRegions(HttpServletRequest request, Model model) {
		LOG.info("createDefaultBranchesAndRegions called to do pre processing before log in");
		User user = sessionHelper.getCurrentUser();

		try {
			if (user == null) {
				LOG.error("createDefaultBranchesAndRegions : user not found in session!");
				throw new InvalidInputException("createDefaultBranchesAndRegions : user not found in session!");
			}
			LicenseDetail currentLicenseDetail = user.getCompany().getLicenseDetails().get(CommonConstants.INITIAL_INDEX);
			HttpSession session = request.getSession(false);
			AccountType accountType = null;

			if (currentLicenseDetail == null) {
				LOG.error("createDefaultBranchesAndRegions : License details not found for user with id : " + user.getUserId());
				throw new InvalidInputException("createDefaultBranchesAndRegions : License details not found for user with id : " + user.getUserId());
			}

			AccountsMaster currentAccountsMaster = currentLicenseDetail.getAccountsMaster();
			if (currentAccountsMaster == null) {
				LOG.error("createDefaultBranchesAndRegions : Accounts Master not found for license details with id : "
						+ currentLicenseDetail.getLicenseId());
				throw new InvalidInputException("createDefaultBranchesAndRegions : Accounts Master not found for license details with id : "
						+ currentLicenseDetail.getLicenseId());
			}

			try {
				LOG.debug("Calling sevices for adding account type of company");
				accountType = organizationManagementService.addAccountTypeForCompany(user,
						String.valueOf(currentAccountsMaster.getAccountsMasterId()));
				LOG.debug("Successfully executed sevices for adding account type of company.Returning account type : " + accountType);

				LOG.debug("Adding account type in session");
				session.setAttribute(CommonConstants.ACCOUNT_TYPE_IN_SESSION, accountType);
				// get the settings
				sessionHelper.getCanonicalSettings(session);
				// set the session variable
				sessionHelper.setSettingVariablesInSession(session);
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException("InvalidInputException in addAccountType. Reason :" + e.getMessage(),
						DisplayMessageConstants.GENERAL_ERROR, e);
			}
			try {
				/**
				 * For each account type, only the company admin's profile completion stage is
				 * updated, all the other profiles created by default need no action so their
				 * profile completion stage is marked completed at the time of insert
				 */
				LOG.debug("Calling sevices for updating profile completion stage");
				userManagementService.updateProfileCompletionStage(user, CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID,
						CommonConstants.DASHBOARD_STAGE);
				LOG.debug("Successfully executed sevices for updating profile completion stage");
			}
			catch (InvalidInputException e) {
				LOG.error("InvalidInputException while updating profile completion stage. Reason : " + e.getMessage(), e);
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e);
			}

			// Setting session variable to show linkedin signup and sendsurvey popups only once
			String popupStatus = (String) session.getAttribute(CommonConstants.POPUP_FLAG_IN_SESSION);
			if (popupStatus == null) {
				session.setAttribute(CommonConstants.POPUP_FLAG_IN_SESSION, CommonConstants.YES_STRING);
			}
			else if (popupStatus.equals(CommonConstants.YES_STRING)) {
				session.setAttribute(CommonConstants.POPUP_FLAG_IN_SESSION, CommonConstants.NO_STRING);
			}
			
			// setting popup attributes
			boolean showLinkedInPopup = false;
			boolean showSendSurveyPopup = false;
			user = userManagementService.getUserByUserId(user.getUserId());
			for (UserProfile profile : user.getUserProfiles()) {
				if (profile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID) {
					showLinkedInPopup = true;
					showSendSurveyPopup = true;
					break;
				}
			}
			model.addAttribute("showLinkedInPopup", String.valueOf(showLinkedInPopup));
			model.addAttribute("showSendSurveyPopup", String.valueOf(showSendSurveyPopup));

			// update the last login time and number of logins
			userManagementService.updateUserLoginTimeAndNum(user);
		}
		catch (NonFatalException e) {
			LOG.error("NonfatalException while adding account type. Reason: " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.ERROR_PAGE;
		}

		LOG.info("createDefaultBranchesAndRegions : Default branches and regions created. Returing the landing page!");
		return JspResolver.LANDING;
	}

	/**
	 * This controller is called to store text to be displayed to a customer after choosing the
	 * flow(happy/neutral/sad).
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/storetextforflow", method = RequestMethod.GET)
	public String storeTextForFlow(HttpServletRequest request, Model model) {
		LOG.info("Method to store text to be displayed to a customer after choosing the flow, storeTextForFlow() started.");
		User user = sessionHelper.getCurrentUser();
		String status = "";

		try {
			String text = request.getParameter("text");
			String mood = request.getParameter("mood");
			if (text == null || text.isEmpty()) {
				LOG.error("Null or empty value found in storeTextForFlow() for text.");
				throw new InvalidInputException("Null or empty value found in storeTextForFlow() for text.");
			}
			if (mood == null || mood.isEmpty()) {
				LOG.error("Null or empty value found in storeTextForFlow() for mood.");
				throw new InvalidInputException("Null or empty value found in storeTextForFlow() for mood.");
			}
			
			OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings(user);
			
			SurveySettings surveySettings = companySettings.getSurvey_settings();
			if (mood.equalsIgnoreCase("happy"))
				surveySettings.setHappyText(text);
			else if (mood.equalsIgnoreCase("neutral"))
				surveySettings.setNeutralText(text);
			else if (mood.equalsIgnoreCase("sad"))
				surveySettings.setSadText(text);
			else if (mood.equalsIgnoreCase("happyComplete"))
				surveySettings.setHappyTextComplete(text);
			else if (mood.equalsIgnoreCase("neutralComplete"))
				surveySettings.setNeutralTextComplete(text);
			else if (mood.equalsIgnoreCase("sadComplete"))
				surveySettings.setSadTextComplete(text);

			organizationManagementService.updateSurveySettings(companySettings, surveySettings);
			status = CommonConstants.SUCCESS_ATTRIBUTE;
		}
		catch (NonFatalException e) {
			LOG.error("Non fatal exception caught in storeTextForFlow(). Nested exception is ", e);
		}
		
		LOG.info("Method to store text to be displayed to a customer after choosing the flow, storeTextForFlow() finished.");
		return status;
	}
}
// JIRA: SS-24 BY RM02 EOC