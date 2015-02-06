package com.realtech.socialsurvey.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import com.google.gson.Gson;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.Achievement;
import com.realtech.socialsurvey.core.entities.Association;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.ContactNumberSettings;
import com.realtech.socialsurvey.core.entities.FacebookToken;
import com.realtech.socialsurvey.core.entities.Licenses;
import com.realtech.socialsurvey.core.entities.LinkedInToken;
import com.realtech.socialsurvey.core.entities.MailIdSettings;
import com.realtech.socialsurvey.core.entities.MiscValues;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.entities.TwitterToken;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserSettings;
import com.realtech.socialsurvey.core.entities.WebAddressSettings;
import com.realtech.socialsurvey.core.entities.YelpToken;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.upload.FileUploadService;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.core.utils.UrlValidationHelper;
import com.realtech.socialsurvey.web.common.JspResolver;

@Controller
public class ProfileManagementController {

	private static final Logger LOG = LoggerFactory.getLogger(ProfileManagementController.class);

	// JIRA SS-97 by RM-06 : BOC
	@Autowired
	private MessageUtils messageUtils;

	@Autowired
	private SessionHelper sessionHelper;

	@Autowired
	private UrlValidationHelper urlValidationHelper;

	@Autowired
	private OrganizationManagementService organizationManagementService;

	@Autowired
	private ProfileManagementService profileManagementService;

	@Autowired
	private FileUploadService fileUploadService;

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

	@RequestMapping(value = "/fetchprofilesociallinks", method = RequestMethod.GET)
	public String fetchProfileSocialLinks() {
		LOG.info("Fecthing profile image");
		return JspResolver.PROFILE_SOCIAL_LINKS;
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
		// String zipcode = request.getParameter(CommonConstants.ZIPCODE);
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
			// contactDetailsSettings.setZipcode(zipcode);
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
				try {
					urlValidationHelper.validateUrl(value);
				}
				catch (IOException ioException) {
					throw new InvalidInputException("Web address passed was invalid", DisplayMessageConstants.GENERAL_ERROR, ioException);
				}
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
			webAddressSettings.setOthers(others);
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

	@RequestMapping(value = "/updatefacebooklink", method = RequestMethod.POST)
	public String updateFacebookLink(Model model, HttpServletRequest request) {

		String fbLink = request.getParameter("fblink");
		HttpSession session = request.getSession(false);
		try {
			if (fbLink == null || fbLink.isEmpty()) {
				throw new InvalidInputException("Facebook link passed was null or empty", DisplayMessageConstants.GENERAL_ERROR);
			}
			try {
				urlValidationHelper.validateUrl(fbLink);
			}
			catch (IOException ioException) {
				throw new InvalidInputException("Facebook link passed was invalid", DisplayMessageConstants.GENERAL_ERROR, ioException);
			}
			UserSettings userSettings = (UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);
			if (userSettings == null) {
				throw new InvalidInputException("No user settings found in session", DisplayMessageConstants.GENERAL_ERROR);
			}
			OrganizationUnitSettings unitSettings = userSettings.getCompanySettings();
			if (unitSettings == null) {
				throw new InvalidInputException("No company settings found in current session", DisplayMessageConstants.GENERAL_ERROR);
			}
			SocialMediaTokens socialMediaTokens = unitSettings.getSocialMediaTokens();
			if (socialMediaTokens == null) {
				LOG.debug("No social media token in profile added");
				socialMediaTokens = new SocialMediaTokens();
			}
			FacebookToken facebookToken = new FacebookToken();
			facebookToken.setFacebookPageLink(fbLink);
			socialMediaTokens.setFacebookToken(facebookToken);
			try {
				organizationManagementService.updateSocialMediaTokens(MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, unitSettings,
						socialMediaTokens);
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException("Invalid input exception ocurred while updating social media tokens",
						DisplayMessageConstants.GENERAL_ERROR, e);
			}
			unitSettings.setSocialMediaTokens(socialMediaTokens);
			userSettings.setCompanySettings(unitSettings);
			session.setAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION, userSettings);
			LOG.info("Facebook link updated successfully");
		}
		catch (NonFatalException nonFatalException) {
			LOG.error("NonFatalException while updating facebook link in profile. Reason :" + nonFatalException.getMessage(), nonFatalException);
			model.addAttribute("message", messageUtils.getDisplayMessage(nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		return JspResolver.MESSAGE_HEADER;
	}

	@RequestMapping(value = "/updatetwitterlink", method = RequestMethod.POST)
	public String updateTwitterLink(Model model, HttpServletRequest request) {

		String twitterLink = request.getParameter("twitterlink");
		HttpSession session = request.getSession(false);
		try {
			if (twitterLink == null || twitterLink.isEmpty()) {
				throw new InvalidInputException("Twitter link passed was null or empty", DisplayMessageConstants.GENERAL_ERROR);
			}
			try {
				urlValidationHelper.validateUrl(twitterLink);
			}
			catch (IOException ioException) {
				throw new InvalidInputException("LinkedIn link passed was invalid", DisplayMessageConstants.GENERAL_ERROR, ioException);
			}
			UserSettings userSettings = (UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);
			if (userSettings == null) {
				throw new InvalidInputException("No user settings found in session", DisplayMessageConstants.GENERAL_ERROR);
			}
			OrganizationUnitSettings unitSettings = userSettings.getCompanySettings();
			if (unitSettings == null) {
				throw new InvalidInputException("No company settings found in current session", DisplayMessageConstants.GENERAL_ERROR);
			}
			SocialMediaTokens socialMediaTokens = unitSettings.getSocialMediaTokens();
			if (socialMediaTokens == null) {
				LOG.debug("No social media token in profile added");
				socialMediaTokens = new SocialMediaTokens();
			}
			TwitterToken twitterToken = new TwitterToken();
			twitterToken.setTwitterPageLink(twitterLink);
			socialMediaTokens.setTwitterToken(twitterToken);
			try {
				organizationManagementService.updateSocialMediaTokens(MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, unitSettings,
						socialMediaTokens);
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException("Invalid input exception ocurred while updating social media tokens",
						DisplayMessageConstants.GENERAL_ERROR, e);
			}
			unitSettings.setSocialMediaTokens(socialMediaTokens);
			userSettings.setCompanySettings(unitSettings);
			session.setAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION, userSettings);
			LOG.info("Twitter link updated successfully");
		}
		catch (NonFatalException nonFatalException) {
			LOG.error("NonFatalException while updating twitter link in profile. Reason :" + nonFatalException.getMessage(), nonFatalException);
			model.addAttribute("message", messageUtils.getDisplayMessage(nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		return JspResolver.MESSAGE_HEADER;
	}

	@RequestMapping(value = "/updatelinkedinlink", method = RequestMethod.POST)
	public String updateLinkedInLink(Model model, HttpServletRequest request) {

		String linkedinLink = request.getParameter("linkedinlink");
		HttpSession session = request.getSession(false);
		try {
			if (linkedinLink == null || linkedinLink.isEmpty()) {
				throw new InvalidInputException("LinkedIn link passed was null or empty", DisplayMessageConstants.GENERAL_ERROR);
			}
			try {
				urlValidationHelper.validateUrl(linkedinLink);
			}
			catch (IOException ioException) {
				throw new InvalidInputException("LinkedIn link passed was invalid", DisplayMessageConstants.GENERAL_ERROR, ioException);
			}
			UserSettings userSettings = (UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);
			if (userSettings == null) {
				throw new InvalidInputException("No user settings found in session", DisplayMessageConstants.GENERAL_ERROR);
			}
			OrganizationUnitSettings unitSettings = userSettings.getCompanySettings();
			if (unitSettings == null) {
				throw new InvalidInputException("No company settings found in current session", DisplayMessageConstants.GENERAL_ERROR);
			}
			SocialMediaTokens socialMediaTokens = unitSettings.getSocialMediaTokens();
			if (socialMediaTokens == null) {
				LOG.debug("No social media token in profile added");
				socialMediaTokens = new SocialMediaTokens();
			}
			LinkedInToken linkedIntoken = new LinkedInToken();
			linkedIntoken.setLinkedInPageLink(linkedinLink);
			socialMediaTokens.setLinkedInToken(linkedIntoken);
			try {
				organizationManagementService.updateSocialMediaTokens(MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, unitSettings,
						socialMediaTokens);
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException("Invalid input exception ocurred while updating social media tokens",
						DisplayMessageConstants.GENERAL_ERROR, e);
			}
			unitSettings.setSocialMediaTokens(socialMediaTokens);
			userSettings.setCompanySettings(unitSettings);
			session.setAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION, userSettings);
			LOG.info("LinkedIn link updated successfully");
		}
		catch (NonFatalException nonFatalException) {
			LOG.error("NonFatalException while updating linkedIn link in profile. Reason :" + nonFatalException.getMessage(), nonFatalException);
			model.addAttribute("message", messageUtils.getDisplayMessage(nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		return JspResolver.MESSAGE_HEADER;
	}

	@RequestMapping(value = "/updateyelplink", method = RequestMethod.POST)
	public String updateYelpLink(Model model, HttpServletRequest request) {

		String yelpLink = request.getParameter("yelplink");
		HttpSession session = request.getSession(false);
		try {
			if (yelpLink == null || yelpLink.isEmpty()) {
				throw new InvalidInputException("Yelp link passed was null or empty", DisplayMessageConstants.GENERAL_ERROR);
			}
			try {
				urlValidationHelper.validateUrl(yelpLink);
			}
			catch (IOException ioException) {
				throw new InvalidInputException("Yelp link passed was invalid", DisplayMessageConstants.GENERAL_ERROR, ioException);
			}
			UserSettings userSettings = (UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);
			if (userSettings == null) {
				throw new InvalidInputException("No user settings found in session", DisplayMessageConstants.GENERAL_ERROR);
			}
			OrganizationUnitSettings unitSettings = userSettings.getCompanySettings();
			if (unitSettings == null) {
				throw new InvalidInputException("No company settings found in current session", DisplayMessageConstants.GENERAL_ERROR);
			}
			SocialMediaTokens socialMediaTokens = unitSettings.getSocialMediaTokens();
			if (socialMediaTokens == null) {
				LOG.debug("No social media token in profile added");
				socialMediaTokens = new SocialMediaTokens();
			}
			YelpToken yelpToken = new YelpToken();
			yelpToken.setYelpPageLink(yelpLink);
			socialMediaTokens.setYelpToken(yelpToken);
			try {
				organizationManagementService.updateSocialMediaTokens(MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, unitSettings,
						socialMediaTokens);
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException("Invalid input exception ocurred while updating social media tokens",
						DisplayMessageConstants.GENERAL_ERROR, e);
			}
			unitSettings.setSocialMediaTokens(socialMediaTokens);
			userSettings.setCompanySettings(unitSettings);
			session.setAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION, userSettings);
			LOG.info("YelpLinked in link updated successfully");
		}
		catch (NonFatalException nonFatalException) {
			LOG.error("NonFatalException while updating yelp link in profile. Reason :" + nonFatalException.getMessage(), nonFatalException);
			model.addAttribute("message", messageUtils.getDisplayMessage(nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		return JspResolver.MESSAGE_HEADER;
	}
	// JIRA SS-97 by RM-06 : EOC
	
	@ResponseBody
	@RequestMapping(value = "/fetchprofile", method = RequestMethod.GET)
	public String fetchProfileDetail(Model model, HttpServletRequest request) {
		LOG.info("Fecthing profile");
		
		HttpSession session = request.getSession(false);
		User user = sessionHelper.getCurrentUser();
		AccountType accountType = (AccountType) session.getAttribute(CommonConstants.ACCOUNT_TYPE_IN_SESSION);
		UserSettings settings = (UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);

		OrganizationUnitSettings profile = null;
		try {
			profile = profileManagementService.finalizeProfileDetail(user, accountType, settings);
		}
		catch (InvalidInputException e) {
			LOG.error("InvalidInputException while fetching profile. Reason :" + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		
		return new Gson().toJson(profile);
	}
}