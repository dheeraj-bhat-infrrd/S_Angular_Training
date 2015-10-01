package com.realtech.socialsurvey.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.EmailTemplateConstants;
import com.realtech.socialsurvey.core.commons.UserProfileComparator;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.FileContentReplacements;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.entities.MailContent;
import com.realtech.socialsurvey.core.entities.MailContentSettings;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserHierarchyAssignments;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.entities.UserSettings;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.exception.UserSessionInvalidateException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.utils.EmailFormatHelper;
import com.realtech.socialsurvey.core.utils.EncryptionHelper;
import com.realtech.socialsurvey.core.utils.FileOperations;
import com.realtech.socialsurvey.core.utils.PropertyFileReader;
import com.realtech.socialsurvey.web.common.JspResolver;
import com.realtech.socialsurvey.web.security.UserAuthProvider;

/**
 * Manipulates the values in session
 */
@Component
public class SessionHelper {

	private static final Logger LOG = LoggerFactory.getLogger(SessionHelper.class);

	@Autowired
	private FileOperations fileOperations;

	@Autowired
	private UserManagementService userManagementService;

	@Autowired
	private OrganizationManagementService organizationManagementService;

	@Autowired
	private PropertyFileReader propertyFileReader;

	@Autowired
	private UserAuthProvider userAuthProvider;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private EncryptionHelper encryptionHelper;

	@Autowired
	private EmailFormatHelper emailFormatHelper;

	@Value("${APPLICATION_LOGO_URL}")
	private String applicationLogoUrl;
	
	@Value ( "${PARAM_ORDER_TAKE_SURVEY}")
    String paramOrderTakeSurvey;
    @Value ( "${PARAM_ORDER_TAKE_SURVEY_CUSTOMER}")
    String paramOrderTakeSurveyCustomer;
    @Value ( "${PARAM_ORDER_TAKE_SURVEY_REMINDER}")
    String paramOrderTakeSurveyReminder;
    @Value ( "${PARAM_ORDER_SURVEY_COPLETION_MAIL}")
    String paramOrderSurveyCompletionMail;
    @Value ( "${PARAM_ORDER_SOCIAL_POST_REMINDER}")
    String paramOrderSocialPostReminder;
    @Value ( "${PARAM_ORDER_INCOMPLETE_SURVEY_REMINDER}")
    String paramOrderIncompleteSurveyReminder;

	@Transactional
	public void getCanonicalSettings(HttpSession session) throws InvalidInputException, NoRecordsFetchedException {
		LOG.info("Getting canonical settings");
		User user = getCurrentUser();
		AccountType accountType = (AccountType) session.getAttribute(CommonConstants.ACCOUNT_TYPE_IN_SESSION);
		LOG.info("Getting settings for " + user.toString() + " for account type " + accountType);
		UserSettings userSettings = userManagementService.getCanonicalUserSettings(user, accountType);
		session.setAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION, userSettings);
		LOG.info("Set the settings in session");
	}

	public void setSettingVariablesInSession(HttpSession session) {
		LOG.info("Settings related session values being set.");
		UserSettings userSettings = (UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);
		if (session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION) != null) {
			// setting the logo name
			setLogo(session, userSettings);
			setProfileImage(session, userSettings);
			// check for the mail content
			setMailContent(session, userSettings);
			// set the highest role from the user's profiles
			setHighestRole(session, getCurrentUser());
		}
	}

	// JIRA SS-97 by RM-06 : BOC
	public void setLogoInSession(HttpSession session, UserSettings userSettings) {
		LOG.info("Setting logo in session");
		setLogo(session, userSettings);
		LOG.info("Logo successfully updated in session");
	}

	public void setProfileImageInSession(HttpSession session, UserSettings userSettings) {
		LOG.info("Setting logo in session");
		setProfileImage(session, userSettings);
		LOG.info("Logo successfully updated in session");
	}

	// JIRA SS-97 by RM-06 : EOC

	private void setLogo(HttpSession session, UserSettings userSettings) {
		LOG.debug("Setting logo name in the session");
		// check if company has a logo
		if (userSettings.getCompanySettings() != null && userSettings.getCompanySettings().getLogo() != null) {
			LOG.debug("Settings logo image from company settings");
			String logoUrl = userSettings.getCompanySettings().getLogo();
			session.setAttribute(CommonConstants.LOGO_DISPLAY_IN_SESSION, logoUrl);
		}
		else {
			LOG.debug("Could not find logo settings in company. Checking in lower heirarchy.");
			// TODO: Check the lower level hierarchy for logo
		}
	}

	private void setProfileImage(HttpSession session, UserSettings userSettings) {
		LOG.debug("Setting profile image name in the session");
		// check if company has a logo
		if (userSettings.getCompanySettings() != null && userSettings.getCompanySettings().getProfileImageUrl() != null) {
			LOG.debug("Settings profile image from company settings");
			String imageUrl = userSettings.getCompanySettings().getProfileImageUrl();
			session.setAttribute(CommonConstants.IMAGE_DISPLAY_IN_SESSION, imageUrl);
		}
		else {
			LOG.debug("Could not find profile image settings in company. Checking in lower heirarchy.");
			// TODO: Check the lower level hierarchy for logo
		}
	}

	public void setMailContent(HttpSession session, UserSettings userSettings) {
		LOG.debug("Setting mail content in the session");
		String body = null;
		FileContentReplacements replacements = new FileContentReplacements();
		replacements.setFileName(EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_PARTICIPATION_MAIL_BODY);

		if (userSettings.getCompanySettings().getMail_content() == null) {
			LOG.debug("Setting default survey participation mail body.");

			try {
				List<String> paramOrder = new ArrayList<String>(Arrays.asList(paramOrderTakeSurveyReminder.split(",")));
				body = fileOperations.replaceFileContents(replacements);
				body = emailFormatHelper.replaceEmailBodyWithParams(body, paramOrder);
				/*body = body.replaceAll("\\[LogoUrl\\]", applicationLogoUrl);*/
				session.setAttribute(CommonConstants.SURVEY_PARTICIPATION_MAIL_BODY_IN_SESSION, body);
				session.setAttribute(CommonConstants.SURVEY_PARTICIPATION_MAIL_SUBJECT_IN_SESSION, CommonConstants.SURVEY_MAIL_SUBJECT
						+ "[AgentName]");

				session.setAttribute(CommonConstants.SURVEY_PARTICIPATION_REMINDER_MAIL_BODY_IN_SESSION, body);
				session.setAttribute(CommonConstants.SURVEY_PARTICIPATION_REMINDER_MAIL_SUBJECT_IN_SESSION, CommonConstants.SURVEY_MAIL_SUBJECT
						+ "[AgentName]");
			}
			catch (InvalidInputException e) {
				LOG.warn("Could not set mail content for survey participation");
			}
		}
		else {
			LOG.debug("Company already has mail body settings. Hence, setting the same");

			MailContentSettings mailSettings = userSettings.getCompanySettings().getMail_content();
			if (userSettings.getCompanySettings().getMail_content().getTake_survey_mail() != null) {
				MailContent mailContent = mailSettings.getTake_survey_mail();
				String mailBody = emailFormatHelper.replaceEmailBodyWithParams(mailContent.getMail_body(), mailContent.getParam_order());
				/*mailBody = mailBody.replaceAll("\\[LogoUrl\\]", applicationLogoUrl);*/
				mailSettings.getTake_survey_mail().setMail_body(mailBody);
				session.setAttribute(CommonConstants.SURVEY_PARTICIPATION_MAIL_BODY_IN_SESSION, mailBody);
				String remainderSubject = CommonConstants.SURVEY_MAIL_SUBJECT + "[AgentName]";
				if (mailContent.getMail_subject() != null) {
					remainderSubject = mailContent.getMail_subject();
				}
				session.setAttribute(CommonConstants.SURVEY_PARTICIPATION_MAIL_SUBJECT_IN_SESSION, remainderSubject);
			}
			else {
				try {
					List<String> paramOrder = new ArrayList<String>(Arrays.asList(paramOrderTakeSurvey.split(",")));
					replacements = new FileContentReplacements();
					replacements.setFileName(EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_INVITATION_MAIL_BODY);
					body = fileOperations.replaceFileContents(replacements);
					body = emailFormatHelper.replaceEmailBodyWithParams(body, paramOrder);
					/*body = body.replaceAll("\\[LogoUrl\\]", applicationLogoUrl);*/
					session.setAttribute(CommonConstants.SURVEY_PARTICIPATION_MAIL_BODY_IN_SESSION, body);
					session.setAttribute(CommonConstants.SURVEY_PARTICIPATION_MAIL_SUBJECT_IN_SESSION, CommonConstants.SURVEY_MAIL_SUBJECT
							+ "[AgentName]");
				}
				catch (InvalidInputException e) {
					LOG.warn("Could not set mail content for survey participation");
				}
			}

			//survey reminder mail
			if (userSettings.getCompanySettings().getMail_content().getTake_survey_reminder_mail() != null) {
				MailContent mailContent = mailSettings.getTake_survey_reminder_mail();
				String mailBody = emailFormatHelper.replaceEmailBodyWithParams(mailContent.getMail_body(), mailContent.getParam_order());
				/*mailBody = mailBody.replaceAll("\\[LogoUrl\\]", applicationLogoUrl);*/
				mailSettings.getTake_survey_reminder_mail().setMail_body(mailBody);
				session.setAttribute(CommonConstants.SURVEY_PARTICIPATION_REMINDER_MAIL_BODY_IN_SESSION, mailBody);
				String remainderSubject = CommonConstants.REMINDER_MAIL_SUBJECT;
				if (mailContent.getMail_subject() != null) {
					remainderSubject = mailContent.getMail_subject();
				}
				session.setAttribute(CommonConstants.SURVEY_PARTICIPATION_REMINDER_MAIL_SUBJECT_IN_SESSION, remainderSubject);
			}
			else {
				try {
					List<String> paramOrder = new ArrayList<String>(Arrays.asList(paramOrderTakeSurveyReminder.split(",")));
					replacements = new FileContentReplacements();
					replacements.setFileName(EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_REMINDER_MAIL_BODY);
					body = fileOperations.replaceFileContents(replacements);
					body = emailFormatHelper.replaceEmailBodyWithParams(body, paramOrder);
					/*body = body.replaceAll("\\[LogoUrl\\]", applicationLogoUrl);*/
					session.setAttribute(CommonConstants.SURVEY_PARTICIPATION_REMINDER_MAIL_BODY_IN_SESSION, body);
					session.setAttribute(CommonConstants.SURVEY_PARTICIPATION_REMINDER_MAIL_SUBJECT_IN_SESSION, CommonConstants.REMINDER_MAIL_SUBJECT);
				}
				catch (InvalidInputException e) {
					LOG.warn("Could not set mail content for survey participation reminder");
				}
			}
			
			// incomplete survey reminder mail
			if (userSettings.getCompanySettings().getMail_content().getRestart_survey_mail() != null) {
				MailContent mailContent = mailSettings.getRestart_survey_mail();
				String mailBody = emailFormatHelper.replaceEmailBodyWithParams(mailContent.getMail_body(), mailContent.getParam_order());
				/*mailBody = mailBody.replaceAll("\\[LogoUrl\\]", applicationLogoUrl);*/
				mailSettings.getRestart_survey_mail().setMail_body(mailBody);
				session.setAttribute(CommonConstants.RESTART_SURVEY_MAIL_BODY_IN_SESSION, mailBody);
				String incompleteSurveyReminderMailSubject = CommonConstants.RESTART_SURVEY_MAIL_SUBJECT;
				if (mailContent.getMail_subject() != null) {
					incompleteSurveyReminderMailSubject = mailContent.getMail_subject();
				}
				session.setAttribute(CommonConstants.RESTART_SURVEY_MAIL_SUBJECT_IN_SESSION, incompleteSurveyReminderMailSubject);
			}
			else {
				try {
					List<String> paramOrder = new ArrayList<String>(Arrays.asList(paramOrderIncompleteSurveyReminder.split(",")));
					replacements = new FileContentReplacements();
					replacements.setFileName(EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_RESTART_MAIL_BODY);
					body = fileOperations.replaceFileContents(replacements);
					body = emailFormatHelper.replaceEmailBodyWithParams(body, paramOrder);
					/*body = body.replaceAll("\\[LogoUrl\\]", applicationLogoUrl);*/
					session.setAttribute(CommonConstants.RESTART_SURVEY_MAIL_BODY_IN_SESSION, body);
					session.setAttribute(CommonConstants.RESTART_SURVEY_MAIL_SUBJECT_IN_SESSION, CommonConstants.RESTART_SURVEY_MAIL_SUBJECT);
				}
				catch (InvalidInputException e) {
					LOG.warn("Could not set mail content for incomplete survey reminder");
				}
			}
			
			//survey completion mail
			if (userSettings.getCompanySettings().getMail_content().getSurvey_completion_mail() != null) {
				MailContent mailContent = mailSettings.getSurvey_completion_mail();
				String mailBody = emailFormatHelper.replaceEmailBodyWithParams(mailContent.getMail_body(), mailContent.getParam_order());
				/*mailBody = mailBody.replaceAll("\\[LogoUrl\\]", applicationLogoUrl);*/
				mailSettings.getSurvey_completion_mail().setMail_body(mailBody);
				session.setAttribute(CommonConstants.SURVEY_COMPLETION_MAIL_BODY_IN_SESSION, mailBody);
				String surveyCompletionMailSubject = CommonConstants.SURVEY_COMPLETION_MAIL_SUBJECT;
				if (mailContent.getMail_subject() != null) {
					surveyCompletionMailSubject = mailContent.getMail_subject();
				}
				session.setAttribute(CommonConstants.SURVEY_COMPLETION_MAIL_SUBJECT_IN_SESSION, surveyCompletionMailSubject);
			}
			else {
				try {
					List<String> paramOrder = new ArrayList<String>(Arrays.asList(paramOrderSurveyCompletionMail.split(",")));
					replacements = new FileContentReplacements();
					replacements.setFileName(EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_COMPLETION_MAIL_BODY);
					body = fileOperations.replaceFileContents(replacements);
					body = emailFormatHelper.replaceEmailBodyWithParams(body, paramOrder);
					/*body = body.replaceAll("\\[LogoUrl\\]", applicationLogoUrl);*/
					session.setAttribute(CommonConstants.SURVEY_COMPLETION_MAIL_BODY_IN_SESSION, body);
					session.setAttribute(CommonConstants.SURVEY_COMPLETION_MAIL_SUBJECT_IN_SESSION, CommonConstants.SURVEY_COMPLETION_MAIL_SUBJECT);
				}
				catch (InvalidInputException e) {
					LOG.warn("Could not set mail content for survey completion mail");
				}
			}
			
			//social post reminder mail
			if (userSettings.getCompanySettings().getMail_content().getSocial_post_reminder_mail() != null) {
				MailContent mailContent = mailSettings.getSocial_post_reminder_mail();
				String mailBody = emailFormatHelper.replaceEmailBodyWithParams(mailContent.getMail_body(), mailContent.getParam_order());
				/*mailBody = mailBody.replaceAll("\\[LogoUrl\\]", applicationLogoUrl);*/
				mailSettings.getSocial_post_reminder_mail().setMail_body(mailBody);
				session.setAttribute(CommonConstants.SOCIAL_POST_REMINDER_MAIL_BODY_IN_SESSION, mailBody);
				String socialPostReminderMailSubject = CommonConstants.SOCIAL_POST_REMINDER_MAIL_SUBJECT;
				if (mailContent.getMail_subject() != null) {
					socialPostReminderMailSubject = mailContent.getMail_subject();
				}
				session.setAttribute(CommonConstants.SOCIAL_POST_REMINDER_MAIL_SUBJECT_IN_SESSION, socialPostReminderMailSubject);
			}
			else {
				try {
					List<String> paramOrder = new ArrayList<String>(Arrays.asList(paramOrderSocialPostReminder.split(",")));
					replacements = new FileContentReplacements();
					replacements.setFileName(EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SOCIALPOST_REMINDER_MAIL_BODY);
					body = fileOperations.replaceFileContents(replacements);
					body = emailFormatHelper.replaceEmailBodyWithParams(body, paramOrder);
					/*body = body.replaceAll("\\[LogoUrl\\]", applicationLogoUrl);*/
					session.setAttribute(CommonConstants.SOCIAL_POST_REMINDER_MAIL_BODY_IN_SESSION, body);
					session.setAttribute(CommonConstants.SOCIAL_POST_REMINDER_MAIL_SUBJECT_IN_SESSION, CommonConstants.SOCIAL_POST_REMINDER_MAIL_SUBJECT);
				}
				catch (InvalidInputException e) {
					LOG.warn("Could not set mail content for social post reminder reminder");
				}
			}
		}
	}

	private void setHighestRole(HttpSession session, User user) {
		LOG.debug("Checking the highest role");
		List<UserProfile> userProfiles = user.getUserProfiles();
		if (userProfiles != null) {
			// sort the user profiles
			Collections.sort(userProfiles, new UserProfileComparator());
			// get the first one. that one will be the highest
			session.setAttribute(CommonConstants.HIGHEST_ROLE_ID_IN_SESSION, userProfiles.get(0).getProfilesMaster().getProfileId());
		}
	}

	/**
	 * Method to add new user into Principal
	 * 
	 * @param emailId
	 * @param password
	 * @return
	 */
	public void loginOnRegistration(String username, String password) {
		LOG.debug("Adding newly registered user to session");
		try {
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
			userAuthProvider.authenticate(auth);

			if (auth.isAuthenticated()) {
				SecurityContextHolder.getContext().setAuthentication(auth);
			}

			if (getCurrentUser() == null) {
				throw new NullPointerException();
			}
		}
		catch (Exception e) {
			SecurityContextHolder.getContext().setAuthentication(null);
			LOG.error("Problem authenticating user" + username, e);
		}
	}

	/**
	 * Method loginAdminAs to login admin as user
	 * 
	 * @param username
	 * @param password
	 */
	public void loginAdminAs(String username, String password) {
		LOG.debug("Adding newly registered user to session");
		try {
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
			userAuthProvider.authenticate(auth);

			if (auth.isAuthenticated()) {
				SecurityContextHolder.getContext().setAuthentication(auth);
			}

			if (getCurrentUser() == null) {
				throw new NullPointerException();
			}
		}
		catch (Exception e) {
			SecurityContextHolder.getContext().setAuthentication(null);
			LOG.error("Problem authenticating user" + username, e);
		}
	}

	/**
	 * Method to get active user from Principal
	 * 
	 * @return User
	 */
	public User getCurrentUser() {
		final Object sessionUser = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User user = null;
		if (sessionUser instanceof User) {
			user = (User) sessionUser;
		}

		if (user == null) {
			throw new UserSessionInvalidateException("User session is no longer available.");
		}
		return user;
	}

	// Redirects user to Landing Page if session is active
	public void redirectToUserSessionIfExists(HttpServletResponse response) {
		LOG.debug("Checking for state of principal session");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			try {
				response.sendRedirect("./" + JspResolver.USER_LOGIN + ".do");
			}
			catch (IOException e) {
				LOG.error("IOException while redirecting logged in user. Reason : " + e.getMessage(), e);
			}
		}
	}

	// Redirects user to Landing Page and requests user to logout from previous session if active
	public boolean isUserActiveSessionExists() {
		LOG.debug("Checking for state of principal session");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public UserHierarchyAssignments processAssignments(HttpSession session, User user) throws NonFatalException {
		LOG.info("Method processAssignments() called from SessionHelper");
		UserHierarchyAssignments assignments = new UserHierarchyAssignments();
		Map<Long, String> regionsMap = new LinkedHashMap<>();
		Map<Long, String> branchesMap = new LinkedHashMap<>();

		user = userManagementService.getUserByUserId(user.getUserId());
		userManagementService.setProfilesOfUser(user);
		Company company = user.getCompany();
		
		// For individual account type
		AccountType accountType = null;
		List<LicenseDetail> licenseDetails = user.getCompany().getLicenseDetails();
		if (licenseDetails != null && !licenseDetails.isEmpty()) {
			LicenseDetail licenseDetail = licenseDetails.get(0);
			accountType = AccountType.getAccountType(licenseDetail.getAccountsMaster().getAccountsMasterId());
		}
		if (accountType.getValue() == CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL) {
			Map<Long, String> agents = new HashMap<Long, String>();
			agents.put(user.getUserId(), CommonConstants.PROFILE_AGENT_VIEW);
			assignments.setAgents(agents);
			
			session.setAttribute(CommonConstants.ENTITY_ID_COLUMN, user.getUserId());
			session.setAttribute(CommonConstants.ENTITY_NAME_COLUMN, CommonConstants.PROFILE_AGENT_VIEW);
			session.setAttribute(CommonConstants.ENTITY_TYPE_COLUMN, CommonConstants.AGENT_ID_COLUMN);
			session.setAttribute(CommonConstants.USER_ASSIGNMENTS, assignments);
			return assignments;
		}

		// Fetch regions data for company
		List<Region> regions = organizationManagementService.getAllRegionsForCompanyWithProjections(company);
		if (regions != null && !regions.isEmpty()) {
			for (Region region : regions) {
				regionsMap.put(region.getRegionId(), region.getRegion());
			}
		}

		// Fetch branches data for company
		List<Branch> branches = organizationManagementService.getAllBranchesForCompanyWithProjections(company);
		if (branches != null && !branches.isEmpty()) {
			for (Branch branch : branches) {
				branchesMap.put(branch.getBranchId(), branch.getBranch());
			}
		}

		if (user.isCompanyAdmin()) {
			Map<Long, String> companies = new HashMap<Long, String>();
			companies.put(company.getCompanyId(), company.getCompany());
			assignments.setCompanies(companies);

			assignments.setRegions(regionsMap);
			assignments.setBranches(branchesMap);
		}

		if (user.isRegionAdmin()) {
			Map<Long, String> regionsMapUser = assignments.getRegions();
			if (regionsMapUser == null) {
				regionsMapUser = new HashMap<Long, String>();
			}

			Map<Long, String> branchesMapUser = assignments.getBranches();
			if (branchesMapUser == null) {
				branchesMapUser = new HashMap<Long, String>();
			}

			for (UserProfile userProfile : user.getUserProfiles()) {

				// fetching for all regions
				if (userProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID) {
					if (userProfile.getRegionId() > 0l) {
						long regionId = userProfile.getRegionId();
						String regionName = regionsMap.get(regionId);
						if (regionName != null) {
							regionsMapUser.put(regionId, regionName);
						}

						// Fetching branches inside the region
						List<Branch> branchesInRegion = organizationManagementService.getAllBranchesInRegionWithProjections(regionId);
						if (branchesInRegion != null && !branchesInRegion.isEmpty()) {
							for (Branch branch : branchesInRegion) {
								branchesMapUser.put(branch.getBranchId(), branch.getBranch());
							}
						}
					}
				}
			}

			assignments.setRegions(regionsMapUser);
			assignments.setBranches(branchesMapUser);
		}
		if (user.isBranchAdmin()) {
			Map<Long, String> regionsMapUser = assignments.getRegions();
			if (regionsMapUser == null) {
				regionsMapUser = new HashMap<Long, String>();
			}

			Map<Long, String> branchesMapUser = assignments.getBranches();
			if (branchesMapUser == null) {
				branchesMapUser = new HashMap<Long, String>();
			}

			for (UserProfile userProfile : user.getUserProfiles()) {
				if (userProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID) {
					if (userProfile.getBranchId() > 0l) {
						long branchId = userProfile.getBranchId();
						String branchName = branchesMap.get(branchId);
						if (branchName != null) {
							branchesMapUser.put(branchId, branchName);
						}
					}
				}
			}

			assignments.setRegions(regionsMapUser);
			assignments.setBranches(branchesMapUser);
		}

		if (user.isAgent()) {
			Map<Long, String> agents = new HashMap<Long, String>();
			agents.put(user.getUserId(), CommonConstants.PROFILE_AGENT_VIEW);
			assignments.setAgents(agents);

			session.setAttribute(CommonConstants.ENTITY_ID_COLUMN, user.getUserId());
			session.setAttribute(CommonConstants.ENTITY_NAME_COLUMN, CommonConstants.PROFILE_AGENT_VIEW);
			session.setAttribute(CommonConstants.ENTITY_TYPE_COLUMN, CommonConstants.AGENT_ID_COLUMN);
		}
		else if (user.isCompanyAdmin()) {
			session.setAttribute(CommonConstants.ENTITY_ID_COLUMN, company.getCompanyId());
			session.setAttribute(CommonConstants.ENTITY_NAME_COLUMN, company.getCompany());
			session.setAttribute(CommonConstants.ENTITY_TYPE_COLUMN, CommonConstants.COMPANY_ID_COLUMN);
		}
		else if (assignments.getRegions() != null && !assignments.getRegions().isEmpty()) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>) assignments.getRegions().entrySet().toArray()[0];
			session.setAttribute(CommonConstants.ENTITY_ID_COLUMN, entry.getKey());
			session.setAttribute(CommonConstants.ENTITY_NAME_COLUMN, entry.getValue());
			session.setAttribute(CommonConstants.ENTITY_TYPE_COLUMN, CommonConstants.REGION_ID_COLUMN);
		}
		else if (assignments.getBranches() != null && !assignments.getBranches().isEmpty()) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>) assignments.getBranches().entrySet().toArray()[0];
			session.setAttribute(CommonConstants.ENTITY_ID_COLUMN, entry.getKey());
			session.setAttribute(CommonConstants.ENTITY_NAME_COLUMN, entry.getValue());
			session.setAttribute(CommonConstants.ENTITY_TYPE_COLUMN, CommonConstants.BRANCH_ID_COLUMN);
		}
		session.setAttribute(CommonConstants.USER_ASSIGNMENTS, assignments);

		LOG.info("Method processAssignments() finished from SessionHelper");
		return assignments;
	}

	public void updateSelectedProfile(HttpSession session, long entityId, String entityType) {
		String entityName = "";
		UserHierarchyAssignments assignments = (UserHierarchyAssignments) session.getAttribute(CommonConstants.USER_ASSIGNMENTS);
		if (entityType.equals(CommonConstants.COMPANY_ID_COLUMN)) {
			entityName = assignments.getCompanies().get(entityId);
		}
		else if (entityType.equals(CommonConstants.REGION_ID_COLUMN)) {
			entityName = assignments.getRegions().get(entityId);
		}
		else if (entityType.equals(CommonConstants.BRANCH_ID_COLUMN)) {
			entityName = assignments.getBranches().get(entityId);
		}
		else if (entityType.equals(CommonConstants.AGENT_ID_COLUMN)) {
			entityName = assignments.getAgents().get(entityId);
		}

		session.setAttribute(CommonConstants.ENTITY_TYPE_COLUMN, entityType);
		session.setAttribute(CommonConstants.ENTITY_ID_COLUMN, entityId);
		session.setAttribute(CommonConstants.ENTITY_NAME_COLUMN, entityName);
	}
}