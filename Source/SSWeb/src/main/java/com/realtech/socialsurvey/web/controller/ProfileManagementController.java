package com.realtech.socialsurvey.web.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.realtech.socialsurvey.core.entities.AgentSettings;
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
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.upload.FileUploadService;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.core.utils.UrlValidationHelper;
import com.realtech.socialsurvey.web.common.ErrorCodes;
import com.realtech.socialsurvey.web.common.ErrorMessages;
import com.realtech.socialsurvey.web.common.ErrorResponse;
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
	private ProfileManagementService profileManagementService;

	@Autowired
	private FileUploadService fileUploadService;

	@Autowired
	private SolrSearchService solrSearchService;

	/**
	 * Method to return company profile page
	 * 
	 * @param profileName
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/companyprofile/{profileName}", method = RequestMethod.GET)
	public String initCompanyProfilePage(@PathVariable String profileName, Model model) {
		LOG.info("Service to initiate company profile page called");
		String message = null;
		if (profileName == null || profileName.isEmpty()) {
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.INVALID_COMPANY_PROFILENAME, DisplayMessageType.ERROR_MESSAGE)
					.getMessage();
			model.addAttribute("message", message);
			return JspResolver.MESSAGE_HEADER;
		}
		model.addAttribute("companyProfileName", profileName);
		LOG.info("Service to initiate company profile page executed successfully");
		return JspResolver.PROFILE_COMPANY;
	}

	@RequestMapping(value = "/showprofilepage", method = RequestMethod.GET)
	public String showProfilePage() {
		LOG.info("Started the profile page");
		return JspResolver.PROFILE_EDIT_AGENT;
	}

	@RequestMapping(value = "/fetchcontactdetails", method = RequestMethod.GET)
	public String fetchContactDetails() {
		LOG.info("Fecthing contact details for profile");
		return JspResolver.PROFILE_CONTACT_DETAILS;
	}

	@RequestMapping(value = "/fetchassociations", method = RequestMethod.GET)
	public String fetchAssociations() {
		LOG.info("Fecthing association list for profile");
		return JspResolver.PROFILE_ASSOCIATIONS;
	}

	@RequestMapping(value = "/fetchachievements", method = RequestMethod.GET)
	public String fetchAchievements() {
		LOG.info("Fecthing achievement list for profile");
		return JspResolver.PROFILE_ACHIEVEMENTS;
	}

	@RequestMapping(value = "/fetchlicences", method = RequestMethod.GET)
	public String fetchLicences() {
		LOG.info("Fecthing license details for profile");
		return JspResolver.PROFILE_LICENSES;
	}

	@RequestMapping(value = "/fetchaddressdetails", method = RequestMethod.GET)
	public String fetchAddressDetails() {
		LOG.info("Fecthing address details for rofile");
		return JspResolver.PROFILE_ADDRESS_DETAILS;
	}

	@RequestMapping(value = "/fetchprofileimage", method = RequestMethod.GET)
	public String fetchProfileImage() {
		LOG.info("Fecthing profile image");
		return JspResolver.PROFILE_IMAGE;
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
	@ResponseBody
	@RequestMapping(value = "/updateassociations", method = RequestMethod.POST)
	public String updateAssociations(Model model, HttpServletRequest request) {
		LOG.info("Updating associations list");
		User user = sessionHelper.getCurrentUser();
		List<Association> associations = null;
		String message = "";

		try {
			HttpSession session = request.getSession(false);
			UserSettings userSettings = (UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);
			if (userSettings == null) {
				throw new InvalidInputException("No user settings found in session");
			}

			String payload = request.getParameter("associationList");
			try {
				if (payload == null || payload.isEmpty()) {
					throw new InvalidInputException("Association passed was null or empty");
				}
				ObjectMapper mapper = new ObjectMapper();
				associations = mapper.readValue(payload, TypeFactory.defaultInstance().constructCollectionType(List.class, Association.class));
			}
			catch (IOException ioException) {
				throw new NonFatalException("Error occurred while parsing the Json.", DisplayMessageConstants.GENERAL_ERROR, ioException);
			}

			if (user.isCompanyAdmin()) {
				OrganizationUnitSettings companySettings = userSettings.getCompanySettings();
				if (companySettings == null) {
					throw new InvalidInputException("No company settings found in current session");
				}
				associations = profileManagementService.addAssociations(MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION,
						companySettings, associations);
				companySettings.setAssociations(associations);
				userSettings.setCompanySettings(companySettings);
			}
			else if (user.isRegionAdmin()) {
				long regionId = Integer.parseInt(request.getParameter("region-id"));
				OrganizationUnitSettings regionSettings = userSettings.getRegionSettings().get(regionId);
				if (regionSettings == null) {
					throw new InvalidInputException("No Region settings found in current session");
				}
				associations = profileManagementService.addAssociations(MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION,
						regionSettings, associations);
				regionSettings.setAssociations(associations);
				userSettings.getRegionSettings().put(regionId, regionSettings);
			}
			else if (user.isBranchAdmin()) {
				long branchId = Integer.parseInt(request.getParameter("branch-id"));
				OrganizationUnitSettings branchSettings = userSettings.getBranchSettings().get(branchId);
				if (branchSettings == null) {
					throw new InvalidInputException("No Branch settings found in current session");
				}
				associations = profileManagementService.addAssociations(MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION,
						branchSettings, associations);
				branchSettings.setAssociations(associations);
				userSettings.getBranchSettings().put(branchId, branchSettings);
			}
			else if (user.isAgent()) {
				long agentId = Integer.parseInt(request.getParameter("agent-id"));
				AgentSettings agentSettings = userSettings.getAgentSettings().get(agentId);
				if (agentSettings == null) {
					throw new InvalidInputException("No Agent settings found in current session");
				}
				associations = profileManagementService.addAgentAssociations(MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION,
						agentSettings, associations);
				agentSettings.setAssociations(associations);
				userSettings.getAgentSettings().put(agentId, agentSettings);
			}
			else {
				throw new InvalidInputException("Invalid input exception occurred in adding associations.", DisplayMessageConstants.GENERAL_ERROR);
			}
			session.setAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION, userSettings);
			LOG.info("Associations updated successfully");
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.ASSOCIATION_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE)
					.getMessage();
		}
		catch (NonFatalException nonFatalException) {
			LOG.error("NonFatalException while updating associations. Reason :" + nonFatalException.getMessage(), nonFatalException);
			message = messageUtils.getDisplayMessage(nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
		}
		return message;
	}

	/**
	 * Method to update about profile details
	 * 
	 * @param model
	 * @param request
	 */
	@ResponseBody
	@RequestMapping(value = "/addorupdateaboutme", method = RequestMethod.POST)
	public String addOrUpdateAboutMe(Model model, HttpServletRequest request) {
		LOG.info("Update about me details");
		User user = sessionHelper.getCurrentUser();
		ContactDetailsSettings contactDetailsSettings = null;
		String message = "";

		try {
			HttpSession session = request.getSession(false);
			UserSettings userSettings = (UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);
			if (userSettings == null) {
				throw new InvalidInputException("No user settings found in session");
			}

			String aboutMe = request.getParameter("aboutMe");
			if (aboutMe == null || aboutMe.isEmpty()) {
				throw new InvalidInputException("About me can not be null or empty", DisplayMessageConstants.GENERAL_ERROR);
			}

			if (user.isCompanyAdmin()) {
				OrganizationUnitSettings unitSettings = userSettings.getCompanySettings();
				if (unitSettings == null) {
					throw new InvalidInputException("No company settings found in current session");
				}
				contactDetailsSettings = unitSettings.getContact_details();
				contactDetailsSettings.setAbout_me(aboutMe);
				contactDetailsSettings = profileManagementService.updateContactDetails(
						MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, unitSettings, contactDetailsSettings);
				unitSettings.setContact_details(contactDetailsSettings);
				userSettings.setCompanySettings(unitSettings);
			}
			else if (user.isRegionAdmin()) {
				long regionId = Integer.parseInt(request.getParameter("region-id"));
				OrganizationUnitSettings regionSettings = userSettings.getRegionSettings().get(regionId);
				if (regionSettings == null) {
					throw new InvalidInputException("No Region settings found in current session");
				}
				contactDetailsSettings = regionSettings.getContact_details();
				contactDetailsSettings.setAbout_me(aboutMe);
				contactDetailsSettings = profileManagementService.updateContactDetails(
						MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings, contactDetailsSettings);
				regionSettings.setContact_details(contactDetailsSettings);
				userSettings.getRegionSettings().put(regionId, regionSettings);
			}
			else if (user.isBranchAdmin()) {
				long branchId = Integer.parseInt(request.getParameter("branch-id"));
				OrganizationUnitSettings branchSettings = userSettings.getBranchSettings().get(branchId);
				if (branchSettings == null) {
					throw new InvalidInputException("No Branch settings found in current session");
				}
				contactDetailsSettings = branchSettings.getContact_details();
				contactDetailsSettings.setAbout_me(aboutMe);
				contactDetailsSettings = profileManagementService.updateContactDetails(
						MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings, contactDetailsSettings);
				branchSettings.setContact_details(contactDetailsSettings);
				userSettings.getRegionSettings().put(branchId, branchSettings);
			}
			else if (user.isAgent()) {
				long agentId = Integer.parseInt(request.getParameter("agent-id"));
				AgentSettings agentSettings = userSettings.getAgentSettings().get(agentId);
				if (agentSettings == null) {
					throw new InvalidInputException("No Agent settings found in current session");
				}
				contactDetailsSettings = agentSettings.getContact_details();
				contactDetailsSettings.setAbout_me(aboutMe);
				contactDetailsSettings = profileManagementService.updateAgentContactDetails(
						MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings, contactDetailsSettings);
				agentSettings.setContact_details(contactDetailsSettings);
				userSettings.getRegionSettings().put(agentId, agentSettings);
			}
			else {
				throw new InvalidInputException("Error occurred while checking user details.", DisplayMessageConstants.GENERAL_ERROR);
			}

			session.setAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION, userSettings);
			LOG.info("About me details updated successfully");
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.ABOUT_ME_DETAILS_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE)
					.getMessage();
		}
		catch (NonFatalException nonFatalException) {
			LOG.error("NonFatalException while updating licence details. Reason :" + nonFatalException.getMessage(), nonFatalException);
			message = messageUtils.getDisplayMessage(nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
		}
		return message;
	}

	/**
	 * Method to update profile addresses in profile
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/updateprofileaddress", method = RequestMethod.POST)
	public String updateProfileAddress(Model model, HttpServletRequest request) {
		LOG.info("Updating contact detail info");
		User user = sessionHelper.getCurrentUser();
		ContactDetailsSettings contactDetailsSettings = null;
		String message = "";

		try {
			HttpSession session = request.getSession(false);
			UserSettings userSettings = (UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);
			if (userSettings == null) {
				throw new InvalidInputException("No user settings found in session");
			}

			// Get the profile address parameters
			String name = request.getParameter("profName");
			String address1 = request.getParameter(CommonConstants.ADDRESS1);
			String address2 = request.getParameter(CommonConstants.ADDRESS2);
			if (name == null || name.isEmpty()) {
				throw new InvalidInputException("Name passed can not be null or empty", DisplayMessageConstants.GENERAL_ERROR);
			}
			if (address1 == null || address1.isEmpty()) {
				throw new InvalidInputException("Address 1 passed can not be null or empty", DisplayMessageConstants.GENERAL_ERROR);
			}

			if (user.isCompanyAdmin()) {
				OrganizationUnitSettings companySettings = userSettings.getCompanySettings();
				if (companySettings == null) {
					throw new InvalidInputException("No company settings found in current session");
				}
				contactDetailsSettings = companySettings.getContact_details();
				contactDetailsSettings.setName(name);
				contactDetailsSettings.setAddress1(address1);
				contactDetailsSettings.setAddress2(address2);
				contactDetailsSettings = profileManagementService.updateContactDetails(
						MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, contactDetailsSettings);
				companySettings.setContact_details(contactDetailsSettings);
				userSettings.setCompanySettings(companySettings);
			}
			else if (user.isRegionAdmin()) {
				long regionId = Integer.parseInt(request.getParameter("region-id"));
				OrganizationUnitSettings regionSettings = userSettings.getRegionSettings().get(regionId);
				if (regionSettings == null) {
					throw new InvalidInputException("No Region settings found in current session");
				}
				contactDetailsSettings = regionSettings.getContact_details();
				contactDetailsSettings.setName(name);
				contactDetailsSettings.setAddress1(address1);
				contactDetailsSettings.setAddress2(address2);
				contactDetailsSettings = profileManagementService.updateContactDetails(
						MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings, contactDetailsSettings);
				regionSettings.setContact_details(contactDetailsSettings);
				userSettings.getRegionSettings().put(regionId, regionSettings);
			}
			else if (user.isBranchAdmin()) {
				long branchId = Integer.parseInt(request.getParameter("branch-id"));
				OrganizationUnitSettings branchSettings = userSettings.getBranchSettings().get(branchId);
				if (branchSettings == null) {
					throw new InvalidInputException("No Branch settings found in current session");
				}
				contactDetailsSettings = branchSettings.getContact_details();
				contactDetailsSettings.setName(name);
				contactDetailsSettings.setAddress1(address1);
				contactDetailsSettings.setAddress2(address2);
				contactDetailsSettings = profileManagementService.updateContactDetails(
						MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings, contactDetailsSettings);
				branchSettings.setContact_details(contactDetailsSettings);
				userSettings.getBranchSettings().put(branchId, branchSettings);
			}
			else if (user.isAgent()) {
				long agentId = Integer.parseInt(request.getParameter("agent-id"));
				AgentSettings agentSettings = userSettings.getAgentSettings().get(agentId);
				if (agentSettings == null) {
					throw new InvalidInputException("No Agent settings found in current session");
				}
				contactDetailsSettings = agentSettings.getContact_details();
				contactDetailsSettings.setName(name);
				contactDetailsSettings.setAddress1(address1);
				contactDetailsSettings.setAddress2(address2);
				contactDetailsSettings = profileManagementService.updateAgentContactDetails(
						MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings, contactDetailsSettings);
				agentSettings.setContact_details(contactDetailsSettings);
				userSettings.getAgentSettings().put(agentId, agentSettings);
			}
			else {
				throw new InvalidInputException("Invalid input exception occurred in adding Contact details.", DisplayMessageConstants.GENERAL_ERROR);
			}

			session.setAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION, userSettings);
			LOG.info("Profile addresses updated successfully");
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.PROFILE_ADDRESSES_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE)
					.getMessage();
		}
		catch (NonFatalException nonFatalException) {
			LOG.error("NonFatalException while updating profile address. Reason :" + nonFatalException.getMessage(), nonFatalException);
			message = messageUtils.getDisplayMessage(nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
		}
		return message;
	}

	/**
	 * Method to update achievements in profile
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/updateachievements", method = RequestMethod.POST)
	public String updateAchievements(Model model, HttpServletRequest request) {
		LOG.info("Updating achievements list");
		User user = sessionHelper.getCurrentUser();
		List<Achievement> achievements = null;
		String message = "";

		try {
			HttpSession session = request.getSession(false);
			UserSettings userSettings = (UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);
			if (userSettings == null) {
				throw new InvalidInputException("No user settings found in session");
			}

			String payload = request.getParameter("achievementList");
			try {
				if (payload == null || payload.isEmpty()) {
					throw new InvalidInputException("Acheivements passed was null or empty");
				}
				ObjectMapper mapper = new ObjectMapper();
				achievements = mapper.readValue(payload, TypeFactory.defaultInstance().constructCollectionType(List.class, Achievement.class));
			}
			catch (IOException ioException) {
				throw new NonFatalException("Error occurred while parsing json", DisplayMessageConstants.GENERAL_ERROR, ioException);
			}

			if (user.isCompanyAdmin()) {
				OrganizationUnitSettings companySettings = userSettings.getCompanySettings();
				if (companySettings == null) {
					throw new InvalidInputException("No company settings found in current session");
				}
				achievements = profileManagementService.addAchievements(MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION,
						companySettings, achievements);
				companySettings.setAchievements(achievements);
				userSettings.setCompanySettings(companySettings);
			}
			else if (user.isRegionAdmin()) {
				long regionId = Integer.parseInt(request.getParameter("region-id"));
				OrganizationUnitSettings regionSettings = userSettings.getRegionSettings().get(regionId);
				if (regionSettings == null) {
					throw new InvalidInputException("No Region settings found in current session");
				}
				achievements = profileManagementService.addAchievements(MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION,
						regionSettings, achievements);
				regionSettings.setAchievements(achievements);
				userSettings.getRegionSettings().put(regionId, regionSettings);
			}
			else if (user.isBranchAdmin()) {
				long branchId = Integer.parseInt(request.getParameter("branch-id"));
				OrganizationUnitSettings branchSettings = userSettings.getBranchSettings().get(branchId);
				if (branchSettings == null) {
					throw new InvalidInputException("No Branch settings found in current session");
				}
				achievements = profileManagementService.addAchievements(MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION,
						branchSettings, achievements);
				branchSettings.setAchievements(achievements);
				userSettings.getBranchSettings().put(branchId, branchSettings);
			}
			else if (user.isAgent()) {
				long agentId = Integer.parseInt(request.getParameter("agent-id"));
				AgentSettings agentSettings = userSettings.getAgentSettings().get(agentId);
				if (agentSettings == null) {
					throw new InvalidInputException("No Agent settings found in current session");
				}
				achievements = profileManagementService.addAgentAchievements(MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION,
						agentSettings, achievements);
				agentSettings.setAchievements(achievements);
				userSettings.getAgentSettings().put(agentId, agentSettings);
			}
			else {
				throw new InvalidInputException("Invalid input exception occurred in adding achievements.", DisplayMessageConstants.GENERAL_ERROR);
			}

			session.setAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION, userSettings);
			LOG.info("Achievements updated successfully");
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.ACHIEVEMENT_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE)
					.getMessage();
		}
		catch (NonFatalException nonFatalException) {
			LOG.error("NonFatalException while updating associations. Reason :" + nonFatalException.getMessage(), nonFatalException);
			message = messageUtils.getDisplayMessage(nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
		}
		return message;
	}

	/**
	 * Method to update licenses for profile
	 * 
	 * @param model
	 * @param request
	 */
	@ResponseBody
	@RequestMapping(value = "/updatelicenses", method = RequestMethod.POST)
	public String updateProfileLicenses(Model model, HttpServletRequest request) {
		LOG.info("Update profile licences");
		User user = sessionHelper.getCurrentUser();
		Licenses licenses = null;
		String message = "";

		try {
			HttpSession session = request.getSession(false);
			UserSettings userSettings = (UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);
			if (userSettings == null) {
				throw new InvalidInputException("No user settings found in session");
			}

			String payload = request.getParameter("licenceList");
			List<String> authorisedIn = null;
			try {
				if (payload == null || payload.isEmpty()) {
					throw new InvalidInputException("Licenses passed was null or empty");
				}
				ObjectMapper mapper = new ObjectMapper();
				authorisedIn = mapper.readValue(payload, TypeFactory.defaultInstance().constructCollectionType(List.class, String.class));
			}
			catch (IOException ioException) {
				throw new NonFatalException("Error occurred while parsing json.", DisplayMessageConstants.GENERAL_ERROR, ioException);
			}

			if (user.isCompanyAdmin()) {
				OrganizationUnitSettings companySettings = userSettings.getCompanySettings();
				if (companySettings == null) {
					throw new InvalidInputException("No company settings found in current session");
				}
				licenses = profileManagementService.addLicences(MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings,
						authorisedIn);
				companySettings.setLicenses(licenses);
				userSettings.setCompanySettings(companySettings);
			}
			else if (user.isRegionAdmin()) {
				long regionId = Integer.parseInt(request.getParameter("region-id"));
				OrganizationUnitSettings regionSettings = userSettings.getRegionSettings().get(regionId);
				if (regionSettings == null) {
					throw new InvalidInputException("No Region settings found in current session");
				}
				licenses = profileManagementService.addLicences(MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings,
						authorisedIn);
				regionSettings.setLicenses(licenses);
				userSettings.getRegionSettings().put(regionId, regionSettings);
			}
			else if (user.isBranchAdmin()) {
				long branchId = Integer.parseInt(request.getParameter("branch-id"));
				OrganizationUnitSettings branchSettings = userSettings.getBranchSettings().get(branchId);
				if (branchSettings == null) {
					throw new InvalidInputException("No Branch settings found in current session");
				}
				licenses = profileManagementService.addLicences(MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings,
						authorisedIn);
				branchSettings.setLicenses(licenses);
				userSettings.getBranchSettings().put(branchId, branchSettings);
			}
			else if (user.isAgent()) {
				long agentId = Integer.parseInt(request.getParameter("agent-id"));
				AgentSettings agentSettings = userSettings.getAgentSettings().get(agentId);
				if (agentSettings == null) {
					throw new InvalidInputException("No Agent settings found in current session");
				}
				licenses = profileManagementService.addAgentLicences(MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings,
						authorisedIn);
				agentSettings.setLicenses(licenses);
				userSettings.getAgentSettings().put(agentId, agentSettings);
			}
			else {
				throw new InvalidInputException("Invalid input exception occurred in adding associations.", DisplayMessageConstants.GENERAL_ERROR);
			}

			session.setAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION, userSettings);
			LOG.info("Licence details updated successfully");
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.LICENSES_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE)
					.getMessage();
		}
		catch (NonFatalException nonFatalException) {
			LOG.error("NonFatalException while updating associations. Reason :" + nonFatalException.getMessage(), nonFatalException);
			message = messageUtils.getDisplayMessage(nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
		}
		return message;
	}

	/**
	 * Method to add or update profile logo
	 * 
	 * @param model
	 * @param request
	 * @param fileLocal
	 */
	@ResponseBody
	@RequestMapping(value = "/addoruploadlogo", method = RequestMethod.POST)
	public String addOrUpdateLogo(Model model, HttpServletRequest request, @RequestParam("logo") MultipartFile fileLocal) {
		LOG.info("Update profile logo");
		User user = sessionHelper.getCurrentUser();
		String logoName = "";
		String message = "";

		try {
			HttpSession session = request.getSession(false);
			UserSettings userSettings = (UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);
			if (userSettings == null) {
				throw new InvalidInputException("No user settings found in session");
			}

			String logoFileName = request.getParameter("logoFileName");
			try {
				if (logoFileName == null || logoFileName.isEmpty()) {
					throw new InvalidInputException("Logo passed is null or empty");
				}
				logoName = fileUploadService.fileUploadHandler(fileLocal, logoFileName);
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException("Error occurred while updating logo.", DisplayMessageConstants.GENERAL_ERROR, e);
			}

			if (user.isCompanyAdmin()) {
				OrganizationUnitSettings companySettings = userSettings.getCompanySettings();
				if (companySettings == null) {
					throw new InvalidInputException("No company settings found in current session");
				}
				profileManagementService.updateLogo(MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, logoName);
				companySettings.setLogo(logoName);
				userSettings.setCompanySettings(companySettings);
			}
			else if (user.isRegionAdmin()) {
				long regionId = Integer.parseInt(request.getParameter("region-id"));
				OrganizationUnitSettings regionSettings = userSettings.getRegionSettings().get(regionId);
				if (regionSettings == null) {
					throw new InvalidInputException("No Region settings found in current session");
				}
				profileManagementService.updateLogo(MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings, logoName);
				regionSettings.setLogo(logoName);
				userSettings.getRegionSettings().put(regionId, regionSettings);
			}
			else if (user.isBranchAdmin()) {
				long branchId = Integer.parseInt(request.getParameter("branch-id"));
				OrganizationUnitSettings branchSettings = userSettings.getBranchSettings().get(branchId);
				if (branchSettings == null) {
					throw new InvalidInputException("No Branch settings found in current session");
				}
				profileManagementService.updateLogo(MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings, logoName);
				branchSettings.setLogo(logoName);
				userSettings.getBranchSettings().put(branchId, branchSettings);
			}
			else if (user.isAgent()) {
				long agentId = Integer.parseInt(request.getParameter("agent-id"));
				AgentSettings agentSettings = userSettings.getAgentSettings().get(agentId);
				if (agentSettings == null) {
					throw new InvalidInputException("No Agent settings found in current session");
				}
				profileManagementService.updateLogo(MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings, logoName);
				agentSettings.setLogo(logoName);
				userSettings.getAgentSettings().put(agentId, agentSettings);
			}
			else {
				throw new InvalidInputException("Invalid input exception occurred in adding associations.", DisplayMessageConstants.GENERAL_ERROR);
			}

			session.setAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION, userSettings);
			sessionHelper.setLogoInSession(session, userSettings);
			LOG.info("Logo uploaded successfully");
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.LOGO_UPLOAD_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE).getMessage();
		}
		catch (NonFatalException nonFatalException) {
			LOG.error("NonFatalException while updating associations. Reason :" + nonFatalException.getMessage(), nonFatalException);
			message = messageUtils.getDisplayMessage(nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
		}
		return message;
	}

	/**
	 * Method to update email id for profile
	 * 
	 * @param model
	 * @param request
	 */
	@ResponseBody
	@RequestMapping(value = "/updateemailids", method = RequestMethod.POST)
	public String updateEmailds(Model model, HttpServletRequest request) {
		LOG.info("Update mail ids");
		User user = sessionHelper.getCurrentUser();
		ContactDetailsSettings contactDetailsSettings = null;
		String message = "";

		try {
			HttpSession session = request.getSession(false);
			UserSettings userSettings = (UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);
			if (userSettings == null) {
				throw new InvalidInputException("No user settings found in session");
			}

			List<MiscValues> mailIds = null;
			try {
				String payload = request.getParameter("mailIds");
				if (payload == null || payload.isEmpty()) {
					throw new InvalidInputException("Maild ids passed was null or empty");
				}
				ObjectMapper mapper = new ObjectMapper();
				mailIds = mapper.readValue(payload, TypeFactory.defaultInstance().constructCollectionType(List.class, MiscValues.class));
			}
			catch (IOException ioException) {
				throw new NonFatalException("Error occurred while parsing json.", DisplayMessageConstants.GENERAL_ERROR, ioException);
			}

			if (user.isCompanyAdmin()) {
				OrganizationUnitSettings companySettings = userSettings.getCompanySettings();
				if (companySettings == null) {
					throw new InvalidInputException("No company settings found in current session");
				}
				contactDetailsSettings = companySettings.getContact_details();
				updateMailSettings(contactDetailsSettings, mailIds);
				contactDetailsSettings = profileManagementService.updateContactDetails(
						MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, contactDetailsSettings);
				companySettings.setContact_details(contactDetailsSettings);
				userSettings.setCompanySettings(companySettings);
			}
			else if (user.isRegionAdmin()) {
				long regionId = Integer.parseInt(request.getParameter("region-id"));
				OrganizationUnitSettings regionSettings = userSettings.getRegionSettings().get(regionId);
				if (regionSettings == null) {
					throw new InvalidInputException("No Region settings found in current session");
				}
				contactDetailsSettings = regionSettings.getContact_details();
				updateMailSettings(contactDetailsSettings, mailIds);
				contactDetailsSettings = profileManagementService.updateContactDetails(
						MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings, contactDetailsSettings);
				regionSettings.setContact_details(contactDetailsSettings);
				userSettings.getRegionSettings().put(regionId, regionSettings);
			}
			else if (user.isBranchAdmin()) {
				long branchId = Integer.parseInt(request.getParameter("branch-id"));
				OrganizationUnitSettings branchSettings = userSettings.getBranchSettings().get(branchId);
				if (branchSettings == null) {
					throw new InvalidInputException("No Branch settings found in current session");
				}
				contactDetailsSettings = branchSettings.getContact_details();
				updateMailSettings(contactDetailsSettings, mailIds);
				contactDetailsSettings = profileManagementService.updateContactDetails(
						MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings, contactDetailsSettings);
				branchSettings.setContact_details(contactDetailsSettings);
				userSettings.getBranchSettings().put(branchId, branchSettings);
			}
			else if (user.isAgent()) {
				long agentId = Integer.parseInt(request.getParameter("agent-id"));
				AgentSettings agentSettings = userSettings.getAgentSettings().get(agentId);
				if (agentSettings == null) {
					throw new InvalidInputException("No Agent settings found in current session");
				}
				contactDetailsSettings = agentSettings.getContact_details();
				updateMailSettings(contactDetailsSettings, mailIds);
				contactDetailsSettings = profileManagementService.updateAgentContactDetails(
						MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings, contactDetailsSettings);
				agentSettings.setContact_details(contactDetailsSettings);
				userSettings.getAgentSettings().put(agentId, agentSettings);
			}
			else {
				throw new InvalidInputException("Invalid input exception occurred in adding associations.", DisplayMessageConstants.GENERAL_ERROR);
			}

			session.setAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION, userSettings);
			LOG.info("Maild ids updated successfully");
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.MAIL_IDS_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE)
					.getMessage();
		}
		catch (NonFatalException nonFatalException) {
			LOG.error("NonFatalException while updating associations. Reason :" + nonFatalException.getMessage(), nonFatalException);
			message = messageUtils.getDisplayMessage(nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
		}
		return message;
	}

	// Update mail ids
	private void updateMailSettings(ContactDetailsSettings contactDetailsSettings, List<MiscValues> mailIds) throws InvalidInputException {
		if (contactDetailsSettings == null) {
			throw new InvalidInputException("No contact details object found for user");
		}
		MailIdSettings mailIdSettings = contactDetailsSettings.getMail_ids();
		if (mailIdSettings == null) {
			LOG.debug("No maild ids added, create new mail id object in contact details");
			mailIdSettings = new MailIdSettings();
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
	}

	/**
	 * Method to update phone numbers of a profile
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/updatephonenumbers", method = RequestMethod.POST)
	public String updatePhoneNumbers(Model model, HttpServletRequest request) {
		LOG.info("Update phone numbers");
		User user = sessionHelper.getCurrentUser();
		ContactDetailsSettings contactDetailsSettings = null;
		String message = "";

		try {
			HttpSession session = request.getSession(false);
			UserSettings userSettings = (UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);
			if (userSettings == null) {
				throw new InvalidInputException("No user settings found in session");
			}

			String payload = request.getParameter("phoneNumbers");
			List<MiscValues> phoneNumbers = null;
			try {
				if (payload == null || payload.isEmpty()) {
					throw new InvalidInputException("Phone numbers passed was null or empty");
				}
				ObjectMapper mapper = new ObjectMapper();
				phoneNumbers = mapper.readValue(payload, TypeFactory.defaultInstance().constructCollectionType(List.class, MiscValues.class));
			}
			catch (IOException ioException) {
				throw new NonFatalException("Error occurred while parsing json.", DisplayMessageConstants.GENERAL_ERROR, ioException);
			}

			if (user.isCompanyAdmin()) {
				OrganizationUnitSettings companySettings = userSettings.getCompanySettings();
				if (companySettings == null) {
					throw new InvalidInputException("No company settings found in current session");
				}
				contactDetailsSettings = companySettings.getContact_details();
				updatePhoneNumbers(contactDetailsSettings, phoneNumbers);
				contactDetailsSettings = profileManagementService.updateContactDetails(
						MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, contactDetailsSettings);
				companySettings.setContact_details(contactDetailsSettings);
				userSettings.setCompanySettings(companySettings);
			}
			else if (user.isRegionAdmin()) {
				long regionId = Integer.parseInt(request.getParameter("region-id"));
				OrganizationUnitSettings regionSettings = userSettings.getRegionSettings().get(regionId);
				if (regionSettings == null) {
					throw new InvalidInputException("No Region settings found in current session");
				}
				contactDetailsSettings = regionSettings.getContact_details();
				updatePhoneNumbers(contactDetailsSettings, phoneNumbers);
				contactDetailsSettings = profileManagementService.updateContactDetails(
						MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings, contactDetailsSettings);
				regionSettings.setContact_details(contactDetailsSettings);
				userSettings.getRegionSettings().put(regionId, regionSettings);
			}
			else if (user.isBranchAdmin()) {
				long branchId = Integer.parseInt(request.getParameter("branch-id"));
				OrganizationUnitSettings branchSettings = userSettings.getBranchSettings().get(branchId);
				if (branchSettings == null) {
					throw new InvalidInputException("No Branch settings found in current session");
				}
				contactDetailsSettings = branchSettings.getContact_details();
				updatePhoneNumbers(contactDetailsSettings, phoneNumbers);
				contactDetailsSettings = profileManagementService.updateContactDetails(
						MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings, contactDetailsSettings);
				branchSettings.setContact_details(contactDetailsSettings);
				userSettings.getBranchSettings().put(branchId, branchSettings);
			}
			else if (user.isAgent()) {
				long agentId = Integer.parseInt(request.getParameter("agent-id"));
				AgentSettings agentSettings = userSettings.getAgentSettings().get(agentId);
				if (agentSettings == null) {
					throw new InvalidInputException("No Agent settings found in current session");
				}
				contactDetailsSettings = agentSettings.getContact_details();
				updatePhoneNumbers(contactDetailsSettings, phoneNumbers);
				contactDetailsSettings = profileManagementService.updateAgentContactDetails(
						MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings, contactDetailsSettings);
				agentSettings.setContact_details(contactDetailsSettings);
				userSettings.getAgentSettings().put(agentId, agentSettings);
			}
			else {
				throw new InvalidInputException("Invalid input exception occurred in adding associations.", DisplayMessageConstants.GENERAL_ERROR);
			}

			session.setAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION, userSettings);
			LOG.info("Contact numbers updated successfully");
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.CONTACT_NUMBERS_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE)
					.getMessage();
		}
		catch (NonFatalException nonFatalException) {
			LOG.error("NonFatalException while updating associations. Reason :" + nonFatalException.getMessage(), nonFatalException);
			message = messageUtils.getDisplayMessage(nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
		}
		return message;
	}

	// update phone numbers
	private void updatePhoneNumbers(ContactDetailsSettings contactDetailsSettings, List<MiscValues> phoneNumbers) throws InvalidInputException {
		if (contactDetailsSettings == null) {
			throw new InvalidInputException("No contact details object found for user");
		}
		ContactNumberSettings phoneNumberSettings = contactDetailsSettings.getContact_numbers();
		if (phoneNumberSettings == null) {
			LOG.debug("No phone numbers are added, create phone numbers object in contact details");
			phoneNumberSettings = new ContactNumberSettings();
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
	}

	/**
	 * Method to update web addresses for a profile
	 * 
	 * @param model
	 * @param request
	 */
	@ResponseBody
	@RequestMapping(value = "/updatewebaddresses", method = RequestMethod.POST)
	public String updateWebAddresses(Model model, HttpServletRequest request) {
		LOG.info("Update web addresses");
		User user = sessionHelper.getCurrentUser();
		ContactDetailsSettings contactDetailsSettings = null;
		String message = "";

		try {
			HttpSession session = request.getSession(false);
			UserSettings userSettings = (UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);
			if (userSettings == null) {
				throw new InvalidInputException("No user settings found in session");
			}

			List<MiscValues> webAddresses = null;
			try {
				String payload = request.getParameter("webAddresses");
				if (payload == null || payload.isEmpty()) {
					throw new InvalidInputException("Web addresses passed was null or empty");
				}
				ObjectMapper mapper = new ObjectMapper();
				webAddresses = mapper.readValue(payload, TypeFactory.defaultInstance().constructCollectionType(List.class, MiscValues.class));
			}
			catch (IOException ioException) {
				throw new NonFatalException("Error occurred while parsing json.", DisplayMessageConstants.GENERAL_ERROR, ioException);
			}

			if (user.isCompanyAdmin()) {
				OrganizationUnitSettings companySettings = userSettings.getCompanySettings();
				if (companySettings == null) {
					throw new InvalidInputException("No company settings found in current session");
				}
				contactDetailsSettings = companySettings.getContact_details();
				updateWebAddresses(contactDetailsSettings, webAddresses);
				contactDetailsSettings = profileManagementService.updateContactDetails(
						MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, contactDetailsSettings);
				companySettings.setContact_details(contactDetailsSettings);
				userSettings.setCompanySettings(companySettings);
			}
			else if (user.isRegionAdmin()) {
				long regionId = Integer.parseInt(request.getParameter("region-id"));
				OrganizationUnitSettings regionSettings = userSettings.getRegionSettings().get(regionId);
				if (regionSettings == null) {
					throw new InvalidInputException("No Region settings found in current session");
				}
				contactDetailsSettings = regionSettings.getContact_details();
				updateWebAddresses(contactDetailsSettings, webAddresses);
				contactDetailsSettings = profileManagementService.updateContactDetails(
						MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings, contactDetailsSettings);
				regionSettings.setContact_details(contactDetailsSettings);
				userSettings.getRegionSettings().put(regionId, regionSettings);
			}
			else if (user.isBranchAdmin()) {
				long branchId = Integer.parseInt(request.getParameter("branch-id"));
				OrganizationUnitSettings branchSettings = userSettings.getBranchSettings().get(branchId);
				if (branchSettings == null) {
					throw new InvalidInputException("No Branch settings found in current session");
				}
				contactDetailsSettings = branchSettings.getContact_details();
				updateWebAddresses(contactDetailsSettings, webAddresses);
				contactDetailsSettings = profileManagementService.updateContactDetails(
						MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings, contactDetailsSettings);
				branchSettings.setContact_details(contactDetailsSettings);
				userSettings.getBranchSettings().put(branchId, branchSettings);
			}
			else if (user.isAgent()) {
				long agentId = Integer.parseInt(request.getParameter("agent-id"));
				AgentSettings agentSettings = userSettings.getAgentSettings().get(agentId);
				if (agentSettings == null) {
					throw new InvalidInputException("No Agent settings found in current session");
				}
				contactDetailsSettings = agentSettings.getContact_details();
				updateWebAddresses(contactDetailsSettings, webAddresses);
				contactDetailsSettings = profileManagementService.updateAgentContactDetails(
						MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings, contactDetailsSettings);
				agentSettings.setContact_details(contactDetailsSettings);
				userSettings.getAgentSettings().put(agentId, agentSettings);
			}
			else {
				throw new InvalidInputException("Invalid input exception occurred in adding associations.", DisplayMessageConstants.GENERAL_ERROR);
			}

			session.setAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION, userSettings);
			LOG.info("Web addresses updated successfully");
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.WEB_ADDRESSES_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE)
					.getMessage();
		}
		catch (NonFatalException nonFatalException) {
			LOG.error("NonFatalException while updating associations. Reason :" + nonFatalException.getMessage(), nonFatalException);
			message = messageUtils.getDisplayMessage(nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
		}
		return message;
	}

	// update web addresses
	private void updateWebAddresses(ContactDetailsSettings contactDetailsSettings, List<MiscValues> webAddresses) throws InvalidInputException {
		if (contactDetailsSettings == null) {
			throw new InvalidInputException("No contact details object found for user");
		}
		WebAddressSettings webAddressSettings = contactDetailsSettings.getWeb_addresses();
		if (webAddressSettings == null) {
			LOG.debug("No web addresses are added, create new web address object in contact details");
			webAddressSettings = new WebAddressSettings();
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
	}

	// TODO
	@ResponseBody
	@RequestMapping(value = "/updatefacebooklink", method = RequestMethod.POST)
	public String updateFacebookLink(Model model, HttpServletRequest request) {
		LOG.info("Updating Facebook link");
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
				profileManagementService.updateSocialMediaTokens(MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, unitSettings,
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
				profileManagementService.updateSocialMediaTokens(MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, unitSettings,
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
				profileManagementService.updateSocialMediaTokens(MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, unitSettings,
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
				profileManagementService.updateSocialMediaTokens(MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, unitSettings,
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

	// TODO JIRA SS-97 by RM-06 : EOC

	/*
	 * Method to find a user on the basis of email id provided.
	 */
	@RequestMapping(value = "/findapro", method = RequestMethod.POST)
	public String findAProfile(Model model, HttpServletRequest request) {
		LOG.info("Method findAProfile called.");
		List<SolrDocument> users = new ArrayList<SolrDocument>();
		SolrDocumentList results = null;
		String patternFirst;
		String patternLast;
		int startIndex;
		int batchSize;

		try {
			patternFirst = request.getParameter("find-pro-first-name");
			patternLast = request.getParameter("find-pro-last-name");
			startIndex = Integer.parseInt(request.getParameter("find-pro-start-index"));
			batchSize = Integer.parseInt(request.getParameter("find-pro-row-size"));

			if (patternFirst == null && patternLast == null) {
				LOG.error("Invalid search key passed in method findAProfile().");
				throw new InvalidInputException("Invalid searchKey passed in method findAProfile().");
			}

			try {
				results = solrSearchService.searchUsersByFirstOrLastName(patternFirst, patternLast, startIndex, batchSize);
				for (SolrDocument solrDocument : results) {
					users.add(solrDocument);
				}
			}
			catch (MalformedURLException e) {
				LOG.error("Error occured while searching in findAProfile(). Reason is ", e);
				throw new NonFatalException("Error occured while searching in findAProfile(). Reason is ", e);
			}
		}
		catch (NonFatalException nonFatalException) {
			LOG.error("NonFatalException while searching in findAProfile(). Reason : " + nonFatalException.getMessage(), nonFatalException);
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrCode(ErrorCodes.REQUEST_FAILED);
			errorResponse.setErrMessage(ErrorMessages.REQUEST_FAILED);
			return new Gson().toJson(errorResponse);
		}
		model.addAttribute("users", users);
		model.addAttribute("size", users.size());
		model.addAttribute("numfound", results.getNumFound());
		model.addAttribute("patternFirst", patternFirst);
		model.addAttribute("patternLast", patternLast);

		LOG.info("Method findAProfile finished.");
		return JspResolver.PROFILE_LIST;
	}

	@ResponseBody
	@RequestMapping(value = "/findaproscroll", method = RequestMethod.POST)
	public String findAProfileScroll(Model model, HttpServletRequest request) {
		LOG.info("Method findAProfileScroll called.");
		List<SolrDocument> users = new ArrayList<SolrDocument>();
		SolrDocumentList results = null;
		String patternFirst;
		String patternLast;
		int startIndex;
		int batchSize;

		try {
			patternFirst = request.getParameter("find-pro-first-name");
			patternLast = request.getParameter("find-pro-last-name");
			startIndex = Integer.parseInt(request.getParameter("find-pro-start-index"));
			batchSize = Integer.parseInt(request.getParameter("find-pro-row-size"));

			if (patternFirst == null && patternLast == null) {
				LOG.error("Invalid search key passed in method findAProfileScroll().");
				throw new InvalidInputException("Invalid searchKey passed in method findAProfileScroll().");
			}

			try {
				results = solrSearchService.searchUsersByFirstOrLastName(patternFirst, patternLast, startIndex, batchSize);
				for (SolrDocument solrDocument : results) {
					users.add(solrDocument);
				}
			}
			catch (MalformedURLException e) {
				LOG.error("Error occured while searching in findAProfileScroll(). Reason is ", e);
				throw new NonFatalException("Error occured while searching in findAProfileScroll(). Reason is ", e);
			}
		}
		catch (NonFatalException nonFatalException) {
			LOG.error("NonFatalException while searching in findAProfileScroll(). Reason : " + nonFatalException.getMessage(), nonFatalException);
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrCode(ErrorCodes.REQUEST_FAILED);
			errorResponse.setErrMessage(ErrorMessages.REQUEST_FAILED);
			return new Gson().toJson(errorResponse);
		}
		LOG.info("Method findAProfileScroll finished.");
		return new Gson().toJson(users);
	}

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
			long agentId = user.getUserProfiles().get(0).getAgentId();
			long branchId = user.getUserProfiles().get(0).getBranchId();
			long regionId = user.getUserProfiles().get(0).getRegionId();
			LOG.info("agentId: " + agentId + ", branchId: " + branchId + ", regionId: " + regionId);
			profile = profileManagementService.finalizeProfile(user, accountType, settings, agentId, branchId, regionId);
		}
		catch (InvalidInputException e) {
			LOG.error("InvalidInputException while fetching profile. Reason :" + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}

		return new Gson().toJson(profile);
	}
}