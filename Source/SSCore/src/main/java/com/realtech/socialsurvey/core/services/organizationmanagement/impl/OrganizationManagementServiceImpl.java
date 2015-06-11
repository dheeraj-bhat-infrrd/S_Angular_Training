package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

// JIRA: SS-27: By RM05: BOC
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.solr.common.SolrDocumentList;
import org.noggit.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.ProfileCompletionList;
import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.dao.BranchDao;
import com.realtech.socialsurvey.core.dao.DisabledAccountDao;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.RegionDao;
import com.realtech.socialsurvey.core.dao.RemovedUserDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.dao.UserInviteDao;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.dao.UsercountModificationNotificationDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.BranchFromSearch;
import com.realtech.socialsurvey.core.entities.BranchSettings;
import com.realtech.socialsurvey.core.entities.CRMInfo;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.ContactNumberSettings;
import com.realtech.socialsurvey.core.entities.DisabledAccount;
import com.realtech.socialsurvey.core.entities.EncompassCrmInfo;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.entities.LockSettings;
import com.realtech.socialsurvey.core.entities.MailContent;
import com.realtech.socialsurvey.core.entities.MailContentSettings;
import com.realtech.socialsurvey.core.entities.MailIdSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.RegionFromSearch;
import com.realtech.socialsurvey.core.entities.RetriedTransaction;
import com.realtech.socialsurvey.core.entities.StateLookup;
import com.realtech.socialsurvey.core.entities.SurveyCompanyMapping;
import com.realtech.socialsurvey.core.entities.SurveySettings;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserFromSearch;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.entities.VerticalCrmMapping;
import com.realtech.socialsurvey.core.entities.VerticalsMaster;
import com.realtech.socialsurvey.core.entities.ZipCodeLookup;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.exception.DatabaseException;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.exception.UserAlreadyExistsException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserAssignmentException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UtilityService;
import com.realtech.socialsurvey.core.services.payment.Payment;
import com.realtech.socialsurvey.core.services.payment.exception.PaymentException;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.EmailFormatHelper;
import com.realtech.socialsurvey.core.utils.EncryptionHelper;
import com.realtech.socialsurvey.core.utils.ZipCodeExclusionStrategy;

@DependsOn("generic")
@Component
public class OrganizationManagementServiceImpl implements OrganizationManagementService, InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(OrganizationManagementServiceImpl.class);
	private static Map<Integer, VerticalsMaster> verticalsMastersMap = new HashMap<Integer, VerticalsMaster>();

	@Autowired
	private GenericDao<Company, Long> companyDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private GenericDao<LicenseDetail, Long> licenceDetailDao;

	@Autowired
	private GenericDao<ProfilesMaster, Integer> profilesMasterDao;

	@Autowired
	private GenericDao<VerticalsMaster, Integer> verticalMastersDao;

	@Autowired
	private UserManagementService userManagementService;

	@Autowired
	private GenericDao<StateLookup, Integer> stateLookupDao;

	@Autowired
	private GenericDao<ZipCodeLookup, Integer> zipCodeLookupDao;

	@Autowired
	private DisabledAccountDao disabledAccountDao;
	
	@Resource
	@Qualifier("branch")
	private BranchDao branchDao;

	@Autowired
	private OrganizationUnitSettingsDao organizationUnitSettingsDao;

	@Autowired
	private RegionDao regionDao;

	@Autowired
	private UserProfileDao userProfileDao;

	@Autowired
	private SolrSearchService solrSearchService;
	
	@Autowired
	private RemovedUserDao removedUserDao;
	
	@Autowired
	private UserInviteDao userInviteDao;
	
	@Autowired
	private GenericDao<VerticalCrmMapping, Long> verticalCrmMappingDo;
	
	@Autowired
	private GenericDao<SurveyCompanyMapping, Long> surveyCompanyMappingDao;
	
	@Autowired
	private UsercountModificationNotificationDao usercountModificationNotificationDao;

	@Autowired
	private Utils utils;

	@Autowired
	private Payment gateway;

	@Autowired
	private EncryptionHelper encryptionHelper;

	@Autowired
	private EmailFormatHelper emailFormatHelper;

	@Autowired
	private ProfileManagementService profileManagementService;
	
	@Autowired
	private GenericDao<LicenseDetail, Long> licenseDetailDao;
	
	@Autowired
	private GenericDao<RetriedTransaction, Long> retriedTransactionDao;

	@Value("${HAPPY_TEXT}")
	private String happyText;
	@Value("${NEUTRAL_TEXT}")
	private String neutralText;
	@Value("${SAD_TEXT}")
	private String sadText;

	@Value("${HAPPY_TEXT_COMPLETE}")
	private String happyTextComplete;
	@Value("${NEUTRAL_TEXT_COMPLETE}")
	private String neutralTextComplete;
	@Value("${SAD_TEXT_COMPLETE}")
	private String sadTextComplete;

	@Value("${PARAM_ORDER_TAKE_SURVEY}")
	String paramOrderTakeSurvey;
	@Value("${PARAM_ORDER_TAKE_SURVEY_CUSTOMER}")
	String paramOrderTakeSurveyCustomer;
	@Value("${PARAM_ORDER_TAKE_SURVEY_REMINDER}")
	String paramOrderTakeSurveyReminder;

	@Autowired
	private ProfileCompletionList profileCompletionList;
	
	@Autowired
	private UtilityService utilityService;

	/**
	 * This method adds a new company and updates the same for current user and all its user
	 * profiles.
	 * 
	 * @throws SolrException
	 * @throws InvalidInputException
	 */
	@Override
	@Transactional(rollbackFor = { NonFatalException.class, FatalException.class })
	public User addCompanyInformation(User user, Map<String, String> organizationalDetails) throws SolrException, InvalidInputException {
		LOG.info("Method addCompanyInformation started for user " + user.getLoginName());
		Company company = addCompany(user, organizationalDetails.get(CommonConstants.COMPANY_NAME), CommonConstants.STATUS_ACTIVE,
				organizationalDetails.get(CommonConstants.VERTICAL), organizationalDetails.get(CommonConstants.BILLING_MODE_COLUMN));

		LOG.debug("Calling method for updating company of user");
		updateCompanyForUser(user, company);

		LOG.debug("Calling method for updating company for user profiles");
		updateCompanyForUserProfile(user, company);

		LOG.debug("Calling method for adding organizational details");
		addOrganizationalDetails(user, company, organizationalDetails);
		
		// update vertical in mongo
		AgentSettings agentSettings = organizationUnitSettingsDao.fetchAgentSettingsById(user.getUserId());
		organizationUnitSettingsDao.updateParticularKeyAgentSettings(MongoOrganizationUnitSettingDaoImpl.KEY_VERTICAL,
				organizationalDetails.get(CommonConstants.VERTICAL), agentSettings);

		LOG.info("Method addCompanyInformation finished for user " + user.getLoginName());
		return user;
	}

	// JIRA: SS-28: By RM05: BOC
	/*
	 * To add account as per the choice of User.
	 */
	@Override
	@Transactional(rollbackFor = { NonFatalException.class, FatalException.class })
	public AccountType addAccountTypeForCompany(User user, String strAccountType) throws InvalidInputException, SolrException {
		LOG.info("Method addAccountTypeForCompany started for user : " + user.getLoginName());
		if (strAccountType == null || strAccountType.isEmpty()) {
			throw new InvalidInputException("account type is null or empty while adding account type fro company");
		}
		int accountTypeValue = 0;
		try {
			accountTypeValue = Integer.parseInt(strAccountType);
		}
		catch (NumberFormatException e) {
			LOG.error("NumberFormatException for account type :" + strAccountType);
			throw new InvalidInputException("account type is not valid while adding account type fro company");
		}
		AccountType accountType = AccountType.getAccountType(accountTypeValue);

		LOG.debug("Creating default hierarchy and user profiles for the selected account type :" + accountType.getName());
		createDefaultHierarchy(user, accountType);
		LOG.debug("Successfully created default hierarchy and user profiles");

		user = userManagementService.getUserByUserId(user.getUserId());
		userManagementService.setProfilesOfUser(user);
		solrSearchService.addUserToSolr(user);

		LOG.info("Method addAccountTypeForCompany finished.");
		return accountType;
	}

	/**
	 * Method to add default branch/region/user profiles for a user and account type
	 * 
	 * @param user
	 * @param accountType
	 * @throws InvalidInputException
	 * @throws SolrException
	 */
	private void createDefaultHierarchy(User user, AccountType accountType) throws InvalidInputException, SolrException {
		LOG.debug("Method createDefaultHierarchy started for user : " + user.getLoginName());

		LOG.debug("Adding the default region");
		// TODO:adding default comapany,state,city,zipcode as null
		Region region = addNewRegion(user, CommonConstants.DEFAULT_REGION_NAME, CommonConstants.YES, CommonConstants.DEFAULT_ADDRESS, null, null,
				null, null, null, null);
		ProfilesMaster profilesMaster = userManagementService.getProfilesMasterById(CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID);

		LOG.debug("Creating user profile for region admin");
		UserProfile userProfileRegionAdmin = userManagementService.createUserProfile(user, user.getCompany(), user.getEmailId(),
				CommonConstants.DEFAULT_AGENT_ID, CommonConstants.DEFAULT_BRANCH_ID, region.getRegionId(), profilesMaster.getProfileId(),
				CommonConstants.PROFILE_STAGES_COMPLETE, CommonConstants.STATUS_ACTIVE, String.valueOf(user.getUserId()),
				String.valueOf(user.getUserId()));
		userProfileDao.save(userProfileRegionAdmin);

		LOG.debug("Adding the default branch");
		// TODO:setting default country,state,city,zipcode null
		Branch branch = addNewBranch(user, region.getRegionId(), CommonConstants.YES, CommonConstants.DEFAULT_BRANCH_NAME,
				CommonConstants.DEFAULT_ADDRESS, null, null, null, null, null, null);
		profilesMaster = userManagementService.getProfilesMasterById(CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID);

		LOG.debug("Creating user profile for branch admin");
		UserProfile userProfileBranchAdmin = userManagementService.createUserProfile(user, user.getCompany(), user.getEmailId(),
				CommonConstants.DEFAULT_AGENT_ID, branch.getBranchId(), region.getRegionId(), profilesMaster.getProfileId(),
				CommonConstants.PROFILE_STAGES_COMPLETE, CommonConstants.STATUS_ACTIVE, String.valueOf(user.getUserId()),
				String.valueOf(user.getUserId()));
		userProfileDao.save(userProfileBranchAdmin);

		/**
		 * For an individual, a default agent profile is created
		 */
		if (accountType == AccountType.INDIVIDUAL || accountType == AccountType.FREE) {
			profilesMaster = userManagementService.getProfilesMasterById(CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID);

			LOG.debug("Creating user profile for agent");
			UserProfile userProfileAgent = userManagementService.createUserProfile(user, user.getCompany(), user.getEmailId(), user.getUserId(),
					branch.getBranchId(), region.getRegionId(), profilesMaster.getProfileId(), CommonConstants.PROFILE_STAGES_COMPLETE,
					CommonConstants.STATUS_ACTIVE, String.valueOf(user.getUserId()), String.valueOf(user.getUserId()));
			userProfileDao.save(userProfileAgent);

		}

		LOG.debug("Updating profile stage to payment stage for account type :" + accountType.getName());
		userManagementService.updateProfileCompletionStage(user, CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID,
				CommonConstants.DASHBOARD_STAGE);

		LOG.debug("Method createDefaultHierarchy finished.");
	}

	// JIRA: SS-28: By RM05: EOC

	/**
	 * Fetch the account type master id passing
	 * 
	 * @author RM-06
	 * @param company
	 * @return account master id
	 */
	@Override
	@Transactional
	public long fetchAccountTypeMasterIdForCompany(Company company) throws InvalidInputException {

		LOG.info("Fetch account type for company :" + company.getCompany());

		List<LicenseDetail> licenseDetails = licenceDetailDao.findByColumn(LicenseDetail.class, CommonConstants.COMPANY, company);
		if (licenseDetails == null || licenseDetails.isEmpty()) {
			LOG.error("No license object present for the company : " + company.getCompany());
			return 0;
		}
		LOG.info("Successfully fetched the License detail for the current user's company");

		// return the account type master ID
		return licenseDetails.get(0).getAccountsMaster().getAccountsMasterId();
	};

	/*
	 * This method adds a new company into the COMPANY table.
	 */
	private Company addCompany(User user, String companyName, int isRegistrationComplete, String vertical, String billingMode) {
		LOG.debug("Method addCompany started for user " + user.getLoginName());
		Company company = new Company();
		company.setCompany(companyName);
		company.setIsRegistrationComplete(isRegistrationComplete);
		company.setStatus(CommonConstants.STATUS_ACTIVE);
		company.setBillingMode(billingMode);
		// We fetch the vertical and set it
		VerticalsMaster verticalsMaster = verticalMastersDao.findByColumn(VerticalsMaster.class, CommonConstants.VERTICALS_MASTER_NAME_COLUMN,
				vertical).get(CommonConstants.INITIAL_INDEX);
		company.setVerticalsMaster(verticalsMaster);

		company.setCreatedBy(String.valueOf(user.getUserId()));
		company.setModifiedBy(String.valueOf(user.getUserId()));
		company.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		company.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		LOG.debug("Method addCompany finished.");
		return companyDao.save(company);
	}

	/**
	 * This method updates company details for current user
	 * 
	 * @param user
	 * @param company
	 * @return
	 */
	private User updateCompanyForUser(User user, Company company) {
		LOG.debug("Method updateCompanyForUser started for user " + user.getLoginName());
		user.setCompany(company);
		user.setIsOwner(CommonConstants.IS_OWNER);
		userDao.update(user);
		LOG.debug("Method updateCompanyForUser finished for user " + user.getLoginName());
		return user;
	}

	/**
	 * This method updates company details in all the user profiles of current user.
	 * 
	 * @param user
	 * @param company
	 */
	private void updateCompanyForUserProfile(User user, Company company) {
		LOG.debug("Method updateCompanyForUserProfile started for user " + user.getLoginName());
		user = userDao.findById(User.class, user.getUserId());

		List<UserProfile> userProfiles = user.getUserProfiles();
		if (userProfiles != null) {
			for (UserProfile userProfile : userProfiles) {
				userProfile.setCompany(company);
				userProfileDao.update(userProfile);
			}
		}
		else {
			LOG.warn("No profiles found for user : " + user.getUserId());
		}
		LOG.debug("Method updateCompanyForUserProfile finished for user " + user.getLoginName());
	}

	/**
	 * This method adds all the key and value pairs into mongo collection COMPANY_SETTINGS
	 * 
	 * @param user
	 * @param company
	 * @param organizationalDetails
	 * @throws InvalidInputException
	 */
	private void addOrganizationalDetails(User user, Company company, Map<String, String> organizationalDetails) throws InvalidInputException {
		LOG.debug("Method addOrganizationalDetails called.");

		// create a organization settings object
		OrganizationUnitSettings companySettings = new OrganizationUnitSettings();
		companySettings.setIden(company.getCompanyId());
		if (organizationalDetails.get(CommonConstants.LOGO_NAME) != null) {
			companySettings.setLogo(organizationalDetails.get(CommonConstants.LOGO_NAME));
		}

		ContactDetailsSettings contactDetailSettings = new ContactDetailsSettings();
		contactDetailSettings.setName(company.getCompany());
		contactDetailSettings.setAddress(organizationalDetails.get(CommonConstants.ADDRESS));
		contactDetailSettings.setAddress1(organizationalDetails.get(CommonConstants.ADDRESS1));
		contactDetailSettings.setAddress2(organizationalDetails.get(CommonConstants.ADDRESS2));
		contactDetailSettings.setZipcode(organizationalDetails.get(CommonConstants.ZIPCODE));
		contactDetailSettings.setCountry(organizationalDetails.get(CommonConstants.COUNTRY));
		contactDetailSettings.setCountryCode(organizationalDetails.get(CommonConstants.COUNTRY_CODE));
		contactDetailSettings.setState(organizationalDetails.get(CommonConstants.STATE));
		contactDetailSettings.setCity(organizationalDetails.get(CommonConstants.CITY));

		// Add work phone number in contact details
		ContactNumberSettings contactNumberSettings = new ContactNumberSettings();
		contactNumberSettings.setWork(organizationalDetails.get(CommonConstants.COMPANY_CONTACT_NUMBER));
		contactDetailSettings.setContact_numbers(contactNumberSettings);

		// Add work Mail id in contact details
		MailIdSettings mailIdSettings = new MailIdSettings();
		mailIdSettings.setWork(user.getEmailId());
		contactDetailSettings.setMail_ids(mailIdSettings);

		companySettings.setVertical(organizationalDetails.get(CommonConstants.VERTICAL));
		companySettings.setContact_details(contactDetailSettings);
		companySettings.setProfileName(generateProfileNameForCompany(company.getCompany(), company.getCompanyId()));
		companySettings.setProfileUrl("/" + companySettings.getProfileName());
		companySettings.setCreatedOn(System.currentTimeMillis());
		companySettings.setCreatedBy(String.valueOf(user.getUserId()));
		companySettings.setModifiedOn(System.currentTimeMillis());
		companySettings.setModifiedBy(String.valueOf(user.getUserId()));
		companySettings.setLockSettings(new LockSettings());

		// Adding default text for various flows of survey.
		SurveySettings surveySettings = new SurveySettings();
		surveySettings.setHappyText(happyText);
		surveySettings.setNeutralText(neutralText);
		surveySettings.setSadText(sadText);
		surveySettings.setHappyTextComplete(happyTextComplete);
		surveySettings.setNeutralTextComplete(neutralTextComplete);
		surveySettings.setSadTextComplete(sadTextComplete);
		surveySettings.setAutoPostEnabled(true);
		surveySettings.setShow_survey_above_score(CommonConstants.DEFAULT_AUTOPOST_SCORE);
		surveySettings.setSurvey_reminder_interval_in_days(CommonConstants.DEFAULT_REMINDERMAIL_INTERVAL);
		companySettings.setSurvey_settings(surveySettings);

		// set seo content flag
		companySettings.setSeoContentModified(true);

		// set default profile stages.
		companySettings.setProfileStages(profileCompletionList.getDefaultProfileCompletionList(false));

		// Setting default values for mail content in Mail content settings of company settings.
		String takeSurveyMail = "";
		String takeSurveyReminderMail = "";
		String takeSurveyByCustomerMail = "";
		try {
			takeSurveyMail = readMailContentFromFile(CommonConstants.SURVEY_REQUEST_MAIL_FILENAME);
			takeSurveyByCustomerMail = readMailContentFromFile(CommonConstants.SURVEY_CUSTOMER_REQUEST_MAIL_FILENAME);
			takeSurveyReminderMail = readMailContentFromFile(CommonConstants.SURVEY_REMINDER_MAIL_FILENAME);
		}
		catch (IOException e) {
			LOG.error("IOException occured in addOrganizationalDetails while copying default Email content. Nested exception is ", e);
		}

		MailContentSettings mailContentSettings = new MailContentSettings();
		MailContent mailContent = new MailContent();
		mailContent.setMail_body(takeSurveyMail);
		mailContent.setParam_order(new ArrayList<String>(Arrays.asList(paramOrderTakeSurvey.split(","))));
		mailContentSettings.setTake_survey_mail(mailContent);

		mailContent = new MailContent();
		mailContent.setMail_body(takeSurveyByCustomerMail);
		mailContent.setParam_order(new ArrayList<String>(Arrays.asList(paramOrderTakeSurveyCustomer.split(","))));
		mailContentSettings.setTake_survey_mail_customer(mailContent);

		mailContent = new MailContent();
		mailContent.setMail_body(takeSurveyReminderMail);
		mailContent.setParam_order(new ArrayList<String>(Arrays.asList(paramOrderTakeSurveyReminder.split(","))));
		mailContentSettings.setTake_survey_reminder_mail(mailContent);

		companySettings.setMail_content(mailContentSettings);

		LOG.debug("Inserting company settings.");
		organizationUnitSettingsDao.insertOrganizationUnitSettings(companySettings, MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION);

		LOG.debug("Method addOrganizationalDetails finished");
	}

	/**
	 * JIRA:SS-117 by RM02 Method to generate profile name for a company based on some rules
	 * 
	 * @param companyName
	 * @param iden
	 * @return
	 * @throws InvalidInputException
	 */
	private String generateProfileNameForCompany(String companyName, long iden) throws InvalidInputException {
		LOG.debug("Generating profile name for companyName:" + companyName + " and iden:" + iden);
		String profileName = null;
		if (companyName == null || companyName.isEmpty()) {
			throw new InvalidInputException("Company name is null or empty while generating profile name");
		}
		// profileName = companyName.replaceAll(" ", "-").toLowerCase();
		profileName = utils.prepareProfileName(companyName);

		LOG.debug("Checking uniqueness of profile name generated : " + profileName + " by querying mongo");

		OrganizationUnitSettings companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileName(profileName,
				MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION);
		/**
		 * if there exists a company with the profile name formed, append company iden to get the
		 * unique profile name
		 */
		if (companySettings != null) {

			LOG.debug("Profile name generated is already taken by a company, appending iden to get a new and unique one");
			profileName = utils.appendIdenToProfileName(profileName, iden);
		}
		LOG.debug("Successfully generated profile name. Returning : " + profileName);
		return profileName;

	}

	/**
	 * Method to add a new region
	 * 
	 * @param user
	 * @param isDefaultBySystem
	 * @param regionName
	 * @return
	 */
	@Override
	public Region addRegion(User user, int isDefaultBySystem, String regionName) {
		LOG.debug("Method addRegion started for user : " + user.getLoginName() + " isDefaultBySystem : " + isDefaultBySystem + " regionName :"
				+ regionName);
		Region region = new Region();
		region.setCompany(user.getCompany());
		region.setIsDefaultBySystem(isDefaultBySystem);
		region.setStatus(CommonConstants.STATUS_ACTIVE);
		region.setRegion(regionName);
		region.setCreatedBy(String.valueOf(user.getUserId()));
		region.setModifiedBy(String.valueOf(user.getUserId()));
		region.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		region.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		region = regionDao.save(region);
		LOG.debug("Method addRegion finished.");
		return region;
	}

	/**
	 * Method to add a new Branch
	 * 
	 * @param user
	 * @param region
	 * @param branchName
	 * @param isDefaultBySystem
	 * @return
	 */
	@Override
	public Branch addBranch(User user, Region region, String branchName, int isDefaultBySystem) {
		LOG.debug("Method addBranch started for user : " + user.getLoginName());
		Branch branch = new Branch();
		branch.setCompany(user.getCompany());
		branch.setRegion(region);
		branch.setStatus(CommonConstants.STATUS_ACTIVE);
		branch.setBranch(branchName);
		branch.setIsDefaultBySystem(isDefaultBySystem);
		branch.setCreatedBy(String.valueOf(user.getUserId()));
		branch.setModifiedBy(String.valueOf(user.getUserId()));
		branch.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		branch.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		branch = branchDao.save(branch);
		LOG.debug("Method addBranch finished.");
		return branch;
	}

	@Override
	public void editCompanySettings(User user) {
		LOG.info("Editing company information by user: " + user.toString());
	}

	@Override
	public OrganizationUnitSettings getCompanySettings(User user) throws InvalidInputException {
		if (user == null) {
			throw new InvalidInputException("User is not set");
		}
		LOG.info("Get company settings for the user: " + user.toString());
		// get the company id
		if (user.getCompany() == null) {
			throw new InvalidInputException("User object is partially set. Could not find the comany details");
		}
		long companyId = user.getCompany().getCompanyId();
		OrganizationUnitSettings companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(companyId,
				MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION);

		// Filter profile stages.
		if (companySettings != null && companySettings.getProfileStages() != null) {
			companySettings.setProfileStages(profileCompletionList.getProfileCompletionList(companySettings.getProfileStages()));
		}

		// Decrypting the encompass password
		if (companySettings != null && companySettings.getCrm_info() != null
				&& companySettings.getCrm_info().getCrm_source().equalsIgnoreCase(CommonConstants.CRM_SOURCE_ENCOMPASS)) {
			EncompassCrmInfo crmInfo = (EncompassCrmInfo) companySettings.getCrm_info();

			String encryptedPassword = crmInfo.getCrm_password();
			String decryptedPassword = encryptionHelper.decryptAES(encryptedPassword, "");

			crmInfo.setCrm_password(decryptedPassword);
		}
		return companySettings;
	}

	@Override
	public OrganizationUnitSettings getCompanySettings(long companyId) throws InvalidInputException {

		LOG.info("Get company settings for the companyId: " + companyId);

		OrganizationUnitSettings companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(companyId,
				MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION);

		// Filter profile stages.
		if (companySettings != null && companySettings.getProfileStages() != null) {
			companySettings.setProfileStages(profileCompletionList.getProfileCompletionList(companySettings.getProfileStages()));
		}

		return companySettings;
	}

	@Override
	public Map<Long, OrganizationUnitSettings> getRegionSettingsForUserProfiles(List<UserProfile> userProfiles) throws InvalidInputException {
		Map<Long, OrganizationUnitSettings> regionSettings = null;
		if (userProfiles != null && userProfiles.size() > 0) {
			LOG.info("Get region settings for the user profiles: " + userProfiles.toString());
			regionSettings = new HashMap<Long, OrganizationUnitSettings>();
			OrganizationUnitSettings regionSetting = null;
			// get the region profiles and get the settings for each of them.
			for (UserProfile userProfile : userProfiles) {
				regionSetting = new OrganizationUnitSettings();
				if (userProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID) {
					LOG.debug("Getting settings for " + userProfile);
					// get the region id and get the profile
					if (userProfile.getRegionId() > 0l) {
						regionSetting = getRegionSettings(userProfile.getRegionId());
						if (regionSetting != null) {
							regionSettings.put(userProfile.getRegionId(), regionSetting);
						}
					}
					else {
						LOG.warn("Not a valid region id for region profile: " + userProfile + ". Skipping the record");
					}
				}
			}
		}
		else {
			throw new InvalidInputException("User profiles are not set");
		}

		return regionSettings;
	}

	@Override
	public Map<Long, OrganizationUnitSettings> getBranchSettingsForUserProfiles(List<UserProfile> userProfiles) throws InvalidInputException,
			NoRecordsFetchedException {
		Map<Long, OrganizationUnitSettings> branchSettings = null;
		if (userProfiles != null && userProfiles.size() > 0) {
			LOG.info("Get branch settings for the user profiles: " + userProfiles.toString());
			branchSettings = new HashMap<Long, OrganizationUnitSettings>();
			BranchSettings branchSetting = null;
			// get the branch profiles and get the settings for each of them.
			for (UserProfile userProfile : userProfiles) {
				branchSetting = new BranchSettings();
				if (userProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID) {
					LOG.debug("Getting settings for " + userProfile);
					// get the branch id and get the profile
					if (userProfile.getBranchId() > 0l) {
						branchSetting = getBranchSettings(userProfile.getBranchId());
						if (branchSetting != null && branchSetting.getOrganizationUnitSettings() != null) {
							branchSettings.put(userProfile.getBranchId(), branchSetting.getOrganizationUnitSettings());
						}
					}
					else {
						LOG.warn("Not a valid branch id for branch profile: " + userProfile + ". Skipping the record");
					}
				}
			}
		}
		else {
			throw new InvalidInputException("User profiles are not set");
		}

		return branchSettings;
	}

	@Override
	public OrganizationUnitSettings getRegionSettings(long regionId) throws InvalidInputException {
		OrganizationUnitSettings regionSettings = null;
		if (regionId <= 0l) {
			throw new InvalidInputException("Invalid region id. :" + regionId);
		}
		LOG.info("Get the region settings for region id: " + regionId);
		regionSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(regionId,
				MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION);

		// Filter profile stages.
		if (regionSettings != null && regionSettings.getProfileStages() != null) {
			regionSettings.setProfileStages(profileCompletionList.getProfileCompletionList(regionSettings.getProfileStages()));
		}

		return regionSettings;
	}

	/**
	 * Method to fetch branch settings along with the required region settings of region to which
	 * the branch belongs
	 */
	@Transactional
	@Override
	public BranchSettings getBranchSettings(long branchId) throws InvalidInputException, NoRecordsFetchedException {
		OrganizationUnitSettings organizationUnitSettings = null;
		BranchSettings branchSettings = null;
		if (branchId <= 0l) {
			throw new InvalidInputException("Invalid branch id. :" + branchId);
		}
		LOG.info("Get the branch settings for branch id: " + branchId);
		organizationUnitSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(branchId,
				MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION);

		// Filter profile stages.
		if (organizationUnitSettings != null && organizationUnitSettings.getProfileStages() != null) {
			organizationUnitSettings.setProfileStages(profileCompletionList.getProfileCompletionList(organizationUnitSettings.getProfileStages()));
		}

		branchSettings = new BranchSettings();
		branchSettings.setOrganizationUnitSettings(organizationUnitSettings);

		Branch branch = branchDao.findById(Branch.class, branchId);
		if (branch == null) {
			throw new NoRecordsFetchedException("No branch present in db for branchId : " + branchId);
		}
		long regionId = branch.getRegion().getRegionId();

		if (branch.getRegion().getIsDefaultBySystem() != CommonConstants.YES) {
			LOG.debug("fetching region settings for regionId : " + regionId);
			OrganizationUnitSettings regionSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(regionId,
					MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION);
			if (regionSettings == null) {
				throw new NoRecordsFetchedException("No region settings present in mongo for regionId : " + regionId);
			}
			LOG.debug("Successfully fetched region settings for regionId : " + regionId + " adding the info to branch settings");
			branchSettings.setRegionId(regionSettings.getIden());
			branchSettings.setRegionName(regionSettings.getContact_details().getName());
		}
		else {
			LOG.debug("Branch belongs to default region");
		}

		LOG.info("Successfully fetched the branch settings for branch id: " + branchId + " returning : " + branchSettings);
		return branchSettings;
	}

	@Override
	public void updateCRMDetails(OrganizationUnitSettings companySettings, CRMInfo crmInfo, String fullyQualifiedClass) throws InvalidInputException {
		if (companySettings == null) {
			throw new InvalidInputException("Company settings cannot be null.");
		}
		if (crmInfo == null) {
			throw new InvalidInputException("CRM info cannot be null.");
		}
		LOG.info("Updating comapnySettings: " + companySettings + " with crm info: " + crmInfo);
		organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(MongoOrganizationUnitSettingDaoImpl.KEY_CRM_INFO, crmInfo,
				companySettings, MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION);
		organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(MongoOrganizationUnitSettingDaoImpl.KEY_CRM_INFO_CLASS, fullyQualifiedClass,
				companySettings, MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION);
		LOG.info("Updated the record successfully");
	}

	@Override
	public boolean updateSurveySettings(OrganizationUnitSettings companySettings, SurveySettings surveySettings) throws InvalidInputException {
		if (companySettings == null) {
			throw new InvalidInputException("Company settings cannot be null.");
		}

		LOG.info("Updating comapnySettings: " + companySettings + " with surveySettings: " + surveySettings);
		organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(MongoOrganizationUnitSettingDaoImpl.KEY_SURVEY_SETTINGS,
				surveySettings, companySettings, MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION);
		LOG.info("Updated the record successfully");

		return true;
	}

	@Override
	public void updateLocationEnabled(OrganizationUnitSettings companySettings, boolean isLocationEnabled) throws InvalidInputException {
		if (companySettings == null) {
			throw new InvalidInputException("Company settings cannot be null.");
		}

		LOG.info("Updating companySettings: " + companySettings + " with locationEnabled: " + isLocationEnabled);
		organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(MongoOrganizationUnitSettingDaoImpl.KEY_LOCATION_ENABLED,
				isLocationEnabled, companySettings, MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION);
		LOG.info("Updated the record successfully");
	}

	@Override
	public void updateAccountDisabled(OrganizationUnitSettings companySettings, boolean isAccountDisabled) throws InvalidInputException {
		if (companySettings == null) {
			throw new InvalidInputException("Company settings cannot be null.");
		}

		LOG.info("Updating companySettings: " + companySettings + " with AccountDisabled: " + isAccountDisabled);
		organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(MongoOrganizationUnitSettingDaoImpl.KEY_ACCOUNT_DISABLED,
				isAccountDisabled, companySettings, MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION);
		LOG.info("Updated the isAccountDisabled successfully");
	}

	@Override
	public MailContentSettings updateSurveyParticipationMailBody(OrganizationUnitSettings companySettings, String mailBody, String mailCategory)
			throws InvalidInputException {
		if (companySettings == null) {
			throw new InvalidInputException("Company settings cannot be null.");
		}
		if (mailBody == null || mailBody.isEmpty()) {
			throw new InvalidInputException("Mail body cannot be empty.");
		}
		if (mailCategory == null) {
			throw new InvalidInputException("Invalid mail category.");
		}
		LOG.debug("Updating " + mailCategory + " for settings: " + companySettings.toString() + " with mail body: " + mailBody);

		// updating mail body
		List<String> paramOrder = new ArrayList<String>();
		mailBody = emailFormatHelper.replaceEmailBodyParamsWithDefaultValue(mailBody, paramOrder);
		MailContent mailContent = new MailContent();
		mailContent.setMail_body(mailBody);
		mailContent.setParam_order(paramOrder);

		MailContentSettings originalContentSettings = companySettings.getMail_content();
		if (mailCategory.equals(CommonConstants.SURVEY_MAIL_BODY_CATEGORY)) {
			originalContentSettings.setTake_survey_mail(mailContent);
		}
		else if (mailCategory.equals(CommonConstants.SURVEY_REMINDER_MAIL_BODY_CATEGORY)) {
			originalContentSettings.setTake_survey_reminder_mail(mailContent);
		}
		else {
			throw new InvalidInputException("Invalid mail category");
		}

		LOG.info("Updating company settings mail content");
		organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(MongoOrganizationUnitSettingDaoImpl.KEY_MAIL_CONTENT,
				originalContentSettings, companySettings, MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION);
		LOG.info("Updated company settings mail content");

		return originalContentSettings;
	}

	@Override
	@Transactional
	public void addDisabledAccount(long companyId, boolean forceDisable) throws InvalidInputException, NoRecordsFetchedException, PaymentException {
		LOG.info("Adding the disabled account to the database for company id : " + companyId);
		if (companyId <= 0) {
			LOG.error("addDisabledAccount : Invalid companyId has been given.");
			throw new InvalidInputException("addDisabledAccount : Invalid companyId has been given.");
		}
		List<LicenseDetail> licenseDetails = null;

		// Fetching the company entity from database
		LOG.info("Fetching the company record from the database");
		Company company = companyDao.findById(Company.class, companyId);

		// Fetching the license details for the company
		LOG.info("Fetching the License Detail record from the database");
		HashMap<String, Object> queries = new HashMap<>();
		queries.put(CommonConstants.COMPANY_COLUMN, company);
		licenseDetails = licenceDetailDao.findByKeyValue(LicenseDetail.class, queries);

		if (licenseDetails == null || licenseDetails.isEmpty()) {
			LOG.error("No license detail records have been found for company id : " + companyId);
			throw new NoRecordsFetchedException("No license detail records have been found for company id : " + companyId);
		}

		LicenseDetail licenseDetail = licenseDetails.get(CommonConstants.INITIAL_INDEX);

		LOG.debug("Preparing the DisabledAccount entity to be saved in the database.");
		DisabledAccount disabledAccount = new DisabledAccount();
		disabledAccount.setCompany(company);
		disabledAccount.setLicenseDetail(licenseDetail);
		if(forceDisable){
			disabledAccount.setDisableDate(new Timestamp(System.currentTimeMillis()));
			disabledAccount.setStatus(CommonConstants.STATUS_INACTIVE);
		}
		else{
			disabledAccount.setDisableDate(gateway.getDateForCompanyDeactivation(licenseDetail.getSubscriptionId()));
			disabledAccount.setStatus(CommonConstants.STATUS_ACTIVE);
		}
		disabledAccount.setCreatedBy(CommonConstants.ADMIN_USER_NAME);
		disabledAccount.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		disabledAccount.setModifiedBy(CommonConstants.ADMIN_USER_NAME);
		disabledAccount.setModifiedOn(new Timestamp(System.currentTimeMillis()));

		LOG.info("Adding the Disabled Account entity to the database");
		disabledAccountDao.save(disabledAccount);
		LOG.info("Added Disabled Account entity to the database.");
	}

	@Override
	@Transactional
	public void deleteDisabledAccount(long companyId) throws InvalidInputException, NoRecordsFetchedException {
		LOG.info("Deleting the Disabled Account pertaining to company id : " + companyId);
		if (companyId <= 0) {
			LOG.error("addDisabledAccount : Invalid companyId has been given.");
			throw new InvalidInputException("addDisabledAccount : Invalid companyId has been given.");
		}
		List<DisabledAccount> disabledAccounts = null;

		// Fetching the company entity from database
		LOG.info("Fetching the company record from the database");
		Company company = companyDao.findById(Company.class, companyId);

		// Fetching the disabled account entity for the company
		LOG.info("Fetching the Disabled Account from the database");
		HashMap<String, Object> queries = new HashMap<>();
		queries.put(CommonConstants.COMPANY_COLUMN, company);
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
		disabledAccounts = disabledAccountDao.findByKeyValue(DisabledAccount.class, queries);

		if (disabledAccounts == null || disabledAccounts.isEmpty()) {
			LOG.error("No disabled account records have been found for company id : " + companyId);
			throw new NoRecordsFetchedException("No disabled account records have been found for company id : " + companyId);
		}

		DisabledAccount disabledAccount = disabledAccounts.get(CommonConstants.INITIAL_INDEX);
		disabledAccount.setStatus(CommonConstants.STATUS_INACTIVE);
		LOG.info("Removing the disabled account record with id : " + disabledAccount.getId() + "from the database.");

		// Perform soft delete of the record in the database
		disabledAccountDao.update(disabledAccount);
		LOG.info("Record successfully deleted from the database!");
	}

	/**
	 * Method to upgrade a default region to a region
	 * 
	 * @param company
	 * @return
	 * @throws InvalidInputException
	 */
	private Region upgradeDefaultRegion(Region region) throws InvalidInputException {

		LOG.info("Upgrading the default region to a user made region");
		if (region == null) {
			LOG.error("upgradeDefaultRegion Region parameter is invalid or null");
			throw new InvalidInputException("upgradeDefaultRegion Region parameter is invalid or null");
		}

		LOG.debug("Changing the record to change the flag IS_DEFAULT_BY_SYSTEM ");
		region.setIsDefaultBySystem(CommonConstants.STATUS_INACTIVE);
		region.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		LOG.debug("Updating the database to show change from default region to region");
		regionDao.update(region);
		LOG.info(" Region upgrade successful. Returning the region");

		return region;
	}

	/**
	 * Method to upgrade a default branch to a branch
	 * 
	 * @param company
	 * @return
	 * @throws InvalidInputException
	 */
	private Branch upgradeDefaultBranch(Branch branch) throws InvalidInputException {

		LOG.info("Upgrading default branch to a user made branch");
		if (branch == null) {
			LOG.error("upgradeDefaultBranch Branch parameter is invalid or null");
			throw new InvalidInputException("upgradeDefaultBranch Branch parameter is invalid or null");
		}

		// Update the branch record in the database to make it into a user made branch by changing
		// the
		// IS_DEFAULT_BY_SYSTEM flag in the record
		LOG.debug("Changing the record to change the flag IS_DEFAULT_BY_SYSTEM ");
		branch.setIsDefaultBySystem(CommonConstants.STATUS_INACTIVE);
		branch.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		LOG.debug("Updating the database to show change from default branch to branch");
		branchDao.update(branch);
		LOG.info(" Branch upgrade successful. Returning the branch");
		return branch;

	}

	/**
	 * Function to check if only default region exists for a company
	 * 
	 * @param company
	 * @return
	 * @throws InvalidInputException
	 */
	private Region fetchDefaultRegion(Company company) throws InvalidInputException {

		if (company == null) {
			LOG.error(" fetchDefaultRegion : Company parameter is null");
			throw new InvalidInputException(" fetchDefaultRegion : Company parameter is null");
		}

		LOG.info("Checking if only default region exists");
		Region defaultRegion = null;

		// We fetch all the regions for a particular company
		HashMap<String, Object> queries = new HashMap<>();
		queries.put(CommonConstants.COMPANY_COLUMN, company);
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);

		LOG.debug("Fetching all regions for company with id : " + company.getCompanyId());
		List<Region> regionList = regionDao.findByKeyValue(Region.class, queries);

		if (regionList.isEmpty() || regionList == null) {
			LOG.info("No regions found for company with id : " + company.getCompanyId());
			defaultRegion = null;
		}
		// Check if only default region exists
		else if (regionList.size() == CommonConstants.STATUS_ACTIVE
				&& regionList.get(CommonConstants.INITIAL_INDEX).getIsDefaultBySystem() == CommonConstants.STATUS_ACTIVE) {
			// Only default region exists
			LOG.info("Only default region exists for company with id : " + company.getCompanyId());
			defaultRegion = regionList.get(CommonConstants.INITIAL_INDEX);
		}
		else {
			// More than one regions exist so no default region exists. Return null.
			LOG.info("More than one regions exist. So returning null");
			defaultRegion = null;
		}
		return defaultRegion;

	}

	/**
	 * Function to check if only default branch exists for a company
	 * 
	 * @param company
	 * @return
	 * @throws InvalidInputException
	 */
	private Branch fetchDefaultBranch(Company company) throws InvalidInputException {

		if (company == null) {
			LOG.error(" fetchDefaultBranch : Company parameter is null");
			throw new InvalidInputException(" fetchDefaultBranch : Company parameter is null");
		}

		LOG.info("Checking if only default branch exists");
		Branch defaultBranch = null;

		// We fetch all the branches for a particular company
		HashMap<String, Object> queries = new HashMap<>();
		queries.put(CommonConstants.COMPANY_COLUMN, company);
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);

		LOG.debug("Fetching all branches for company with id : " + company.getCompanyId());
		List<Branch> branchList = branchDao.findByKeyValue(Branch.class, queries);

		if (branchList.isEmpty() || branchList == null) {
			LOG.info("No branches found for company with id : " + company.getCompanyId());
			defaultBranch = null;
		}
		// Check if only default branch exists
		else if (branchList.size() == CommonConstants.STATUS_ACTIVE
				&& branchList.get(CommonConstants.INITIAL_INDEX).getIsDefaultBySystem() == CommonConstants.STATUS_ACTIVE) {
			// Only default branch exists
			LOG.info("Only default branch exists for company with id : " + company.getCompanyId());
			defaultBranch = branchList.get(CommonConstants.INITIAL_INDEX);
		}
		else {
			// More than one branches exist so no default branch exists. Return null.
			LOG.info(branchList.size() + " branches found. So returning null.");
			defaultBranch = null;
		}

		return defaultBranch;

	}

	/**
	 * Method to upgrade plan to Company
	 * 
	 * @param company
	 * @throws InvalidInputException
	 * @throws SolrException
	 * @throws NoRecordsFetchedException
	 */
	private void upgradeToCompany(Company company) throws InvalidInputException, SolrException, NoRecordsFetchedException {

		LOG.info("Upgrading to Company");
		if (company == null) {
			LOG.error(" upgradeToCompany : Company parameter is null");
			throw new InvalidInputException(" upgradeToCompany : Company parameter is null");
		}

		/**
		 * In case of upgrading to Company plan we check if default branch exists and upgrade it to
		 * a branch We also add the branch settings to mongo collection BRANCH_SETTINGS and to solr.
		 */

		LOG.debug("checking if only default branch exists and fetching it");
		Branch defaultBranch = fetchDefaultBranch(company);
		if (defaultBranch != null) {
			LOG.debug("Default branch exists. Upgrading it to branch");
			Branch upgradedBranch = upgradeDefaultBranch(defaultBranch);
			LOG.debug("Adding the upgraded branch to mongo collection BRANCH_SETTINGS");
			insertBranchSettings(upgradedBranch);
			LOG.debug("Successfully added settings to mongo, adding the new branch to solr");
			solrSearchService.addOrUpdateBranchToSolr(upgradedBranch);
			LOG.debug("Solr update successful");
		}
		else {
			LOG.error("No default branch found for company with id : " + company.getCompanyId());
			throw new NoRecordsFetchedException("No default branch found for company with id : " + company.getCompanyId());
		}

		LOG.info("Databases upgraded successfully!");
	}

	/**
	 * Method to upgrade plan to Enterprise
	 * 
	 * @param company
	 * @param fromAccountsMasterId
	 * @throws InvalidInputException
	 * @throws SolrException
	 * @throws NoRecordsFetchedException
	 */
	private void upgradeToEnterprise(Company company, int fromAccountsMasterId) throws InvalidInputException, SolrException,
			NoRecordsFetchedException {

		LOG.info("Upgrading to Enterprise");
		if (company == null) {
			LOG.error(" upgradeToEnterprise : Company parameter is null");
			throw new InvalidInputException(" upgradeToEnterprise : Company parameter is null");
		}
		if (fromAccountsMasterId <= 0 || fromAccountsMasterId > 3) {
			LOG.error(" upgradeToCompany : fromAccountsMaster parameter is invalid");
			throw new InvalidInputException(" upgradeToCompany : fromAccountsMaster parameter is invalid");
		}

		/**
		 * In case of upgrading to Enterprise plan we check if default branch exists and upgrade it
		 * to a branch And then we upgrade find the default region and upgrade it user made region.
		 * We also add the branch settings to mongo collection BRANCH_SETTINGS and to solr.
		 */

		LOG.debug("checking if only default branch exists and fetching it");
		Branch defaultBranch = fetchDefaultBranch(company);
		if (defaultBranch != null) {

			LOG.debug("Default branch exists. Upgrading it to branch");
			Branch upgradedBranch = upgradeDefaultBranch(defaultBranch);

			LOG.debug("Adding the upgraded branch to mongo collection BRANCH_SETTINGS");
			insertBranchSettings(upgradedBranch);

			LOG.debug("Successfully added settings to mongo, adding the new branch to solr");
			solrSearchService.addOrUpdateBranchToSolr(upgradedBranch);
			LOG.debug("Solr update successful");

			LOG.debug("Fetching the default region");
			Region defaultRegion = fetchDefaultRegion(company);
			if (defaultRegion == null) {
				LOG.error("No default region found for company with id : " + company.getCompanyId());
				throw new NoRecordsFetchedException("No default region found for company with id : " + company.getCompanyId());
			}
			LOG.debug("Default region exists, upgrading it");
			Region upgradedRegion = upgradeDefaultRegion(defaultRegion);

			LOG.debug("Adding the upgraded region to mongo collection REGION_SETTINGS");
			insertRegionSettings(upgradedRegion);
			LOG.debug("Successfully added settings to mongo, adding the new region to solr");

			solrSearchService.addOrUpdateRegionToSolr(upgradedRegion);
			LOG.debug("Solr update successful");
		}
		else {
			if (fromAccountsMasterId != 3) {
				LOG.error("No default branch found for company with id : " + company.getCompanyId());
				throw new NoRecordsFetchedException("No default branch found for company with id : " + company.getCompanyId());
			}
			LOG.debug("Fetching the default region");
			Region defaultRegion = fetchDefaultRegion(company);
			if (defaultRegion == null) {
				LOG.error("No default region found for company with id : " + company.getCompanyId());
				throw new NoRecordsFetchedException("No default region found for company with id : " + company.getCompanyId());
			}
			LOG.debug("Default region exists, upgrading it");
			Region upgradedRegion = upgradeDefaultRegion(defaultRegion);

			LOG.debug("Adding the upgraded region to mongo collection REGION_SETTINGS");
			insertRegionSettings(upgradedRegion);

			LOG.debug("Successfully added settings to mongo, adding the new region to solr");
			solrSearchService.addOrUpdateRegionToSolr(upgradedRegion);
			LOG.debug("Solr update successful");
		}

		LOG.info("Databases upgraded successfully!");
	}

	/**
	 * Method called to update databases on plan upgrade
	 * 
	 * @param company
	 * @param newAccountsMasterPlanId
	 * @throws NoRecordsFetchedException
	 * @throws InvalidInputException
	 * @throws SolrException
	 */
	@Override
	@Transactional
	public void upgradeAccount(Company company, int newAccountsMasterPlanId) throws NoRecordsFetchedException, InvalidInputException, SolrException {

		if (company == null) {
			LOG.error("upgradePlanAtBackend Company parameter is invalid or null");
			throw new InvalidInputException("upgradePlanAtBackend Company parameter is invalid or null");
		}
		if (newAccountsMasterPlanId <= 0) {
			LOG.error("upgradePlanAtBackend AccountsMaster id parameter is invalid");
			throw new InvalidInputException("upgradePlanAtBackend AccountsMaster id parameter is invalid");
		}

		// We fetch the license detail record to find the current plan
		LOG.info("Finding the current plan");
		List<LicenseDetail> licenseDetails = null;
		LicenseDetail currentLicenseDetail = null;

		LOG.debug("Making the database call to find record for company with id : " + company.getCompanyId());
		Map<String, Object> queries = new HashMap<>();
		queries.put(CommonConstants.COMPANY_COLUMN, company);
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
		licenseDetails = licenceDetailDao.findByKeyValue(LicenseDetail.class, queries);

		// Check if license details exist
		if (licenseDetails == null || licenseDetails.isEmpty()) {
			LOG.error("No license details records found for company with id : " + company.getCompanyId());
			throw new NoRecordsFetchedException("No license details records found for company with id : " + company.getCompanyId());
		}

		currentLicenseDetail = licenseDetails.get(CommonConstants.INITIAL_INDEX);
		LOG.debug("License detail object for company with id : " + company.getCompanyId() + " fetched");
		int currentAccountsMasterId = currentLicenseDetail.getAccountsMaster().getAccountsMasterId();

		// Now we update the Region and Branch tables in the database to reflect changes
		LOG.info("Updating the regions and the branches for plan upgrade");

		switch (newAccountsMasterPlanId) {
			case CommonConstants.ACCOUNTS_MASTER_TEAM:
				if (currentAccountsMasterId <= 0 || currentAccountsMasterId > 1) {
					LOG.error(" upgradeAccount : fromAccountsMaster parameter is invalid : value is : " + currentAccountsMasterId);
					throw new InvalidInputException(" upgradeAccount : fromAccountsMaster parameter is invalid: value is : "
							+ currentAccountsMasterId);
				}
				/**
				 * In case of upgrading to the team account we need to change only the license
				 * details table and add default branch to solr.
				 */
				LOG.debug("checking if only default branch exists and fetching it");
				Branch defaultBranch = fetchDefaultBranch(company);
				if (defaultBranch != null) {
					LOG.debug("Adding the new branch to solr");
					solrSearchService.addOrUpdateBranchToSolr(defaultBranch);
					LOG.debug("Solr update successful");
				}
				else {
					LOG.error("No default branch found for company with id : " + company.getCompanyId());
					throw new NoRecordsFetchedException("No default branch found for company with id : " + company.getCompanyId());
				}
				LOG.info("Databases updated to Team plan");
				break;

			case CommonConstants.ACCOUNTS_MASTER_COMPANY:
				// We check if the plan we are changing from and the plan we are changing to are
				// correct
				if (currentAccountsMasterId <= 0 || currentAccountsMasterId > 2) {
					LOG.error(" upgradeAccount : fromAccountsMaster parameter is invalid: value is : " + currentAccountsMasterId);
					throw new InvalidInputException(" upgradeAccount : fromAccountsMaster parameter is invalid: value is : "
							+ currentAccountsMasterId);
				}
				LOG.info("Calling the database update method for Company plan");
				upgradeToCompany(company);
				LOG.info("Databases updated to Company plan");
				break;

			case CommonConstants.ACCOUNTS_MASTER_ENTERPRISE:
				// We check if the plan we are changing from and the plan we are changing to are
				// correct
				if (currentAccountsMasterId <= 0 || currentAccountsMasterId > 3) {
					LOG.error(" upgradeAccount : fromAccountsMaster parameter is invalid: value is : " + currentAccountsMasterId);
					throw new InvalidInputException(" upgradeAccount : fromAccountsMaster parameter is invalid: value is : "
							+ currentAccountsMasterId);
				}
				LOG.info("Calling the database update method for Enterprise plan");
				upgradeToEnterprise(company, currentAccountsMasterId);
				LOG.info("Databases updated to Enterprise plan");
				break;

			default:
				LOG.error(" upgradeAccount : Invalid accounts master id parameter given");
				throw new InvalidInputException(" upgradeAccount : Invalid accounts master id parameter given");
		}
		LOG.info("Upgrade successful!");
	}

	/**
	 * Method to fetch all regions of a company
	 * 
	 * @param companyProfileName
	 * @return
	 * @throws InvalidInputException
	 */
	@Override
	@Transactional
	public List<Region> getRegionsForCompany(String companyProfileName) throws InvalidInputException {
		LOG.info("Method getRegionsForCompany called for companyProfileName:" + companyProfileName);
		OrganizationUnitSettings companySettings = profileManagementService.getCompanyProfileByProfileName(companyProfileName);
		List<Region> regions = null;
		if (companySettings != null) {
			long companyId = companySettings.getIden();
			LOG.debug("Fetching regions for company : " + companyId);

			/**
			 * Adding columns to be fetched in the list
			 */
			List<String> columnNames = new ArrayList<String>();
			columnNames.add(CommonConstants.REGION_NAME_COLUMN);
			columnNames.add(CommonConstants.REGION_ID_COLUMN);
			columnNames.add(CommonConstants.PROFILE_NAME_COLUMN);

			/**
			 * Building criteria
			 */
			Map<String, Object> queries = new HashMap<String, Object>();
			queries.put(CommonConstants.COMPANY_COLUMN, companyDao.findById(Company.class, companyId));
			queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
			queries.put(CommonConstants.IS_DEFAULT_BY_SYSTEM, CommonConstants.STATUS_INACTIVE);
			regions = regionDao.findProjectionsByKeyValue(Region.class, columnNames, queries);
		}
		else {
			LOG.warn("No company settings found for profileName : " + companyProfileName);
		}
		return regions;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		LOG.info("afterPropertiesSet called for organization managemnet service");
		Map<Integer, VerticalsMaster> verticalsMap = new HashMap<>();
		// Populate the verticals master map
		verticalsMap = utilityService.populateVerticalMastersMap();
		if(!verticalsMap.isEmpty())
			verticalsMastersMap.putAll(verticalsMap);
		LOG.info("afterPropertiesSet finished for organization managemnet service");

	}

	@Override
	public List<VerticalsMaster> getAllVerticalsMaster() throws InvalidInputException {
		LOG.info("Method getAllVerticalsMaster called to fetch the list of vertical masters");
		List<VerticalsMaster> verticalsMasters = new ArrayList<>();
		for (Map.Entry<Integer, VerticalsMaster> entry : verticalsMastersMap.entrySet()) {
			verticalsMasters.add(entry.getValue());
		}
		if (verticalsMasters.isEmpty()) {
			throw new InvalidInputException("No verticals master found");
		}
		LOG.info("Method getAllVerticalsMaster successfully finished");
		return verticalsMasters;
	}

	/**
	 * Method to fetch all the branches that are directly linked to a company
	 * 
	 * @throws NoRecordsFetchedException
	 */
	@Override
	@Transactional
	public List<Branch> getBranchesUnderCompany(String companyProfileName) throws InvalidInputException, NoRecordsFetchedException {
		LOG.info("Method getBranchesUnderCompany called for companyProfileName : " + companyProfileName);
		if (companyProfileName == null || companyProfileName.isEmpty()) {
			throw new InvalidInputException("companyProfileName is null or empty in getBranchesUnderCompany");
		}
		List<Branch> branches = null;
		List<String> columnNames = new ArrayList<String>();
		columnNames.add(CommonConstants.BRANCH_ID_COLUMN);
		columnNames.add(CommonConstants.BRANCH_NAME_COLUMN);
		columnNames.add(CommonConstants.PROFILE_NAME_COLUMN);

		Map<String, Object> queries = new HashMap<String, Object>();

		OrganizationUnitSettings companySettings = profileManagementService.getCompanyProfileByProfileName(companyProfileName);
		if (companySettings != null) {
			Company company = companyDao.findById(Company.class, companySettings.getIden());
			queries.put(CommonConstants.COMPANY_COLUMN, company);
			queries.put(CommonConstants.REGION_COLUMN, getDefaultRegionForCompany(company));
			queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
			queries.put(CommonConstants.IS_DEFAULT_BY_SYSTEM, CommonConstants.NO);

			branches = branchDao.findProjectionsByKeyValue(Branch.class, columnNames, queries);
		}
		LOG.info("Method getBranchesUnderCompany executed sucessfully");

		return branches;
	}

	/**
	 * Method to get the default region of a company
	 * 
	 * @param company
	 * @return
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 */
	@Override
	@Transactional
	public Region getDefaultRegionForCompany(Company company) throws InvalidInputException, NoRecordsFetchedException {
		LOG.info("Method getDefaultRegionForCompany called for :" + company);
		Region region = null;
		Map<String, Object> queries = new HashMap<String, Object>();
		queries.put(CommonConstants.COMPANY_COLUMN, company);
		queries.put(CommonConstants.IS_DEFAULT_BY_SYSTEM, CommonConstants.YES);
		List<Region> regions = regionDao.findByKeyValue(Region.class, queries);
		if (regions == null || regions.isEmpty()) {
			throw new NoRecordsFetchedException("No default region found for company :" + company);
		}
		region = regions.get(0);

		LOG.info("Method getDefaultRegionForCompany excecuted successfully");
		return region;
	}

	/**
	 * Method to fetch the default branch associated with a region
	 * 
	 * @param region
	 * @return
	 * @throws NoRecordsFetchedException
	 */
	@Override
	@Transactional
	public Branch getDefaultBranchForRegion(long regionId) throws InvalidInputException, NoRecordsFetchedException {
		LOG.info("Method getDefaultBranchForRegion called for regionId : " + regionId);
		if (regionId <= 0l) {
			throw new InvalidInputException("region id is invalid in getDefaultBranchForRegion");
		}
		Branch branch = null;
		Map<String, Object> queries = new HashMap<String, Object>();
		queries.put(CommonConstants.REGION_COLUMN, regionDao.findById(Region.class, regionId));
		queries.put(CommonConstants.IS_DEFAULT_BY_SYSTEM, CommonConstants.YES);
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);

		List<Branch> branches = branchDao.findByKeyValue(Branch.class, queries);
		if (branches == null || branches.isEmpty()) {
			// TODO add condition for max default branches
			throw new NoRecordsFetchedException("No default branch present for regionId:" + regionId);
		}
		branch = branches.get(0);
		LOG.info("Method getDefaultBranchForRegion finished");
		return branch;
	}

	/**
	 * Method to get list of branches linked to a region
	 */
	@Override
	@Transactional
	public List<Branch> getBranchesForRegion(String companyProfileName, String regionProfileName) throws InvalidInputException,
			NoRecordsFetchedException {
		if (companyProfileName == null || companyProfileName.isEmpty()) {
			throw new InvalidInputException("companyProfileName is null or empty in getBranchesForRegion");
		}
		if (regionProfileName == null || regionProfileName.isEmpty()) {
			throw new InvalidInputException("regionProfileName is null or empty in getBranchesForRegion");
		}
		LOG.info("Method getBranchesForRegion called for companyProfileName:" + companyProfileName + " and regionProfileName:" + regionProfileName);
		List<Branch> branches = null;
		OrganizationUnitSettings regionSettings = profileManagementService.getRegionByProfileName(companyProfileName, regionProfileName);
		if (regionSettings != null) {
			branches = getBranchesByRegionId(regionSettings.getIden());
		}
		else {
			throw new NoRecordsFetchedException("No region settings found for regionProfileName : " + regionProfileName);
		}
		LOG.info("Method getBranchesForRegion executed successfully.");
		return branches;
	}

	@Override
	@Transactional
	public List<Branch> getBranchesByRegionId(long regionId) throws InvalidInputException {
		LOG.info("Method getBranchesByRegionId called for regionId:" + regionId);

		if (regionId <= 0l) {
			throw new InvalidInputException("region id is invalid while fetching branches");
		}
		List<Branch> branches = null;
		List<String> columnNames = new ArrayList<String>();
		columnNames.add(CommonConstants.BRANCH_ID_COLUMN);
		columnNames.add(CommonConstants.BRANCH_NAME_COLUMN);
		columnNames.add(CommonConstants.PROFILE_NAME_COLUMN);

		Map<String, Object> queries = new HashMap<String, Object>();
		queries.put(CommonConstants.REGION_COLUMN, regionDao.findById(Region.class, regionId));
		queries.put(CommonConstants.IS_DEFAULT_BY_SYSTEM, CommonConstants.NO);
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);

		branches = branchDao.findProjectionsByKeyValue(Branch.class, columnNames, queries);
		LOG.info("Method getBranchesByRegionId completed successfully");
		return branches;
	}

	/**
	 * Method to add a new region and assign the user to the newly created region if userId or
	 * emailId is provided
	 * 
	 * @throws UserAssignmentException
	 */
	@Override
	@Transactional
	public Region addNewRegionWithUser(User user, String regionName, int isDefaultBySystem, String address1, String address2, String country,
			String countryCode, String state, String city, String zipcode, long selectedUserId, String[] emailIdsArray, boolean isAdmin)
			throws InvalidInputException, SolrException, NoRecordsFetchedException, UserAssignmentException {
		LOG.info("Method addNewRegionWithUser called for user:" + user + " regionName:" + regionName + " isDefaultBySystem:" + isDefaultBySystem
				+ " selectedUserId:" + selectedUserId + " emailIdsArray:" + emailIdsArray + " isAdmin:" + isAdmin);

		Region region = addNewRegion(user, regionName, isDefaultBySystem, address1, address2, country, countryCode, state, city, zipcode);

		LOG.debug("Adding default branch for the new region created");
		addNewBranch(user, region.getRegionId(), CommonConstants.YES, CommonConstants.DEFAULT_BRANCH_NAME, CommonConstants.DEFAULT_ADDRESS, null,
				null, null, null, null, null);

		/**
		 * If userId or email is provided, call the service for adding and assigning user to the
		 * newly created region
		 */
		if (selectedUserId > 0l) {
			LOG.debug("Fetching user for selectedUserId " + selectedUserId + "to assign to the region");
			User assigneeUser = userDao.findById(User.class, selectedUserId);
			if (assigneeUser == null) {
				throw new NoRecordsFetchedException("No user found in db for selectedUserId:" + selectedUserId);
			}
			try {
				assignRegionToUser(user, region.getRegionId(), assigneeUser, isAdmin);
			}
			catch (InvalidInputException | NoRecordsFetchedException | SolrException e) {
				LOG.error("Exception while assigning region to a user. Reason:" + e.getMessage(), e);
				throw new UserAssignmentException(e.getMessage(), e);
			}
		}
		else if (emailIdsArray != null && emailIdsArray.length > 0) {
			LOG.debug("Fetching users list to assign to the region");
			List<User> assigneeUsers = getUsersFromEmailIds(emailIdsArray, user);

			if (assigneeUsers != null && !assigneeUsers.isEmpty()) {
				for (User assigneeUser : assigneeUsers) {
					try {
						assignRegionToUser(user, region.getRegionId(), assigneeUser, isAdmin);
					}
					catch (InvalidInputException | NoRecordsFetchedException | SolrException e) {
						LOG.error("Exception while assigning region to a user. Reason:" + e.getMessage(), e);
						throw new UserAssignmentException(e.getMessage(), e);
					}
				}
			}
		}
		LOG.info("Method addNewRegionWithUser completed successfully");
		return region;
	}

	/**
	 * Method to get the list of users for emailIds specified, and if the user doesn't exist for
	 * that company invite the user
	 * 
	 * @param emailIdsArray
	 * @param adminUser
	 * @return
	 * @throws InvalidInputException
	 */
	private List<User> getUsersFromEmailIds(String[] emailIdsArray, User adminUser) throws InvalidInputException {
		LOG.info("Method getUsersFromEmailIds called for emailIdsArray:" + emailIdsArray);
		List<User> users = new ArrayList<User>();
		for (String emailId : emailIdsArray) {
			User user = null;
			try {
				user = userManagementService.getUserByLoginName(adminUser, emailId);
			}
			catch (NoRecordsFetchedException e) {
				/**
				 * if no user is present with the specified emailId, send an invite to register
				 */
				String firstName = emailId.substring(0, emailId.indexOf("@"));
				try {
					user = userManagementService.inviteUserToRegister(adminUser, firstName, null, emailId);
				}
				catch (UserAlreadyExistsException | UndeliveredEmailException e1) {
					LOG.debug("Exception in getUsersFromEmailIds while inviting a new user. Reason:" + e1.getMessage(), e1);
				}
			}
			if (user != null) {
				users.add(user);
			}
		}
		LOG.info("Method getUsersFromEmailIds executed successfully. Returning users size :" + users.size());
		return users;
	}

	/**
	 * Method to assign a region to a user
	 * 
	 * @throws SolrException
	 */
	@Transactional
	@Override
	public void assignRegionToUser(User adminUser, long regionId, User assigneeUser, boolean isAdmin) throws InvalidInputException,
			NoRecordsFetchedException, SolrException {
		if (adminUser == null) {
			throw new InvalidInputException("Admin user is null in assignRegionToUser");
		}
		if (regionId <= 0l) {
			throw new InvalidInputException("Region id is invalid in assignRegionToUser");
		}
		if (assigneeUser == null) {
			throw new InvalidInputException("assignee user is null in assignRegionToUser");
		}
		LOG.info("Method to assignRegionToUser called for regionId : " + regionId + " and assigneeUser : " + assigneeUser.getUserId() + " isAdmin:"
				+ isAdmin);
		int profileMasterId = 0;
		if (isAdmin) {
			profileMasterId = CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID;
		}
		else {
			profileMasterId = CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID;
		}
		LOG.debug("Fetching default branch for region : " + regionId);
		Branch defaultBranch = getDefaultBranchForRegion(regionId);

		UserProfile userProfile = userManagementService.createUserProfile(assigneeUser, adminUser.getCompany(), assigneeUser.getEmailId(),
				assigneeUser.getUserId(), defaultBranch.getBranchId(), regionId, profileMasterId, CommonConstants.DASHBOARD_STAGE,
				CommonConstants.STATUS_ACTIVE, String.valueOf(adminUser.getUserId()), String.valueOf(adminUser.getUserId()));

		// check if user profile already exists
		if (assigneeUser.getUserProfiles() != null && !assigneeUser.getUserProfiles().isEmpty()) {
			for (UserProfile profile : assigneeUser.getUserProfiles()) {
				if (profile.getRegionId() == userProfile.getRegionId() && profile.getBranchId() == userProfile.getBranchId()
						&& profile.getProfilesMaster() == userProfile.getProfilesMaster() && profile.getStatus() == CommonConstants.STATUS_ACTIVE) {
					throw new InvalidInputException(DisplayMessageConstants.USER_ASSIGNMENT_ALREADY_EXISTS);
				}
			}
		}

		userProfileDao.save(userProfile);

		if (assigneeUser.getIsAtleastOneUserprofileComplete() == CommonConstants.STATUS_INACTIVE) {
			LOG.debug("Updating isAtleastOneProfileComplete as active for user : " + assigneeUser.getUserId());
			assigneeUser.setIsAtleastOneUserprofileComplete(CommonConstants.STATUS_ACTIVE);
			userDao.update(assigneeUser);
		}
		/**
		 * add newly created user profile to the list of user profiles in user object
		 */
		List<UserProfile> userProfiles = assigneeUser.getUserProfiles();
		if (userProfiles == null || userProfiles.isEmpty()) {
			userProfiles = new ArrayList<UserProfile>();
		}
		userProfiles.add(userProfile);
		assigneeUser.setUserProfiles(userProfiles);

		userManagementService.setProfilesOfUser(assigneeUser);
		
		AgentSettings agentSettings = organizationUnitSettingsDao.fetchAgentSettingsById(assigneeUser.getUserId());
		assigneeUser.setProfileName(agentSettings.getProfileName());
		assigneeUser.setProfileUrl(agentSettings.getProfileUrl());
		solrSearchService.addUserToSolr(assigneeUser);
		
		LOG.info("Method to assignRegionToUser finished for regionId : " + regionId + " and userId : " + assigneeUser.getUserId());
	}

	/**
	 * Method to add a new region and assign the user to the newly created branch if userId or
	 * emailId is provided
	 * 
	 * @throws UserAssignmentException
	 */
	@Override
	@Transactional
	public Branch addNewBranchWithUser(User user, String branchName, long regionId, int isDefaultBySystem, String address1, String address2,
			String country, String countryCode, String state, String city, String zipcode, long selectedUserId, String[] emailIdsArray,
			boolean isAdmin) throws InvalidInputException, SolrException, NoRecordsFetchedException, UserAssignmentException {
		LOG.info("Method addNewBranchWithUser called for user:" + user + " branchName:" + branchName + "regionId: " + regionId
				+ " isDefaultBySystem:" + isDefaultBySystem + " selectedUserId:" + selectedUserId + " emailIdsArray:" + emailIdsArray + " isAdmin:"
				+ isAdmin);

		Branch branch = addNewBranch(user, regionId, isDefaultBySystem, branchName, address1, address2, country, countryCode, state, city, zipcode);

		/**
		 * If userId or email is provided, call the service for adding and assigning user to the
		 * newly created branch
		 */
		if (selectedUserId > 0l) {
			LOG.debug("Fetching user for selectedUserId " + selectedUserId + "to assign to the branch");
			User assigneeUser = userDao.findById(User.class, selectedUserId);
			if (assigneeUser == null) {
				throw new NoRecordsFetchedException("No user found in db for selectedUserId:" + selectedUserId);
			}
			try {
				assignBranchToUser(user, branch.getBranchId(), branch.getRegion().getRegionId(), assigneeUser, isAdmin);
			}
			catch (InvalidInputException | NoRecordsFetchedException | SolrException e) {
				LOG.error("Exception while assigning branch to a user. Reason:" + e.getMessage(), e);
				throw new UserAssignmentException(e.getMessage(), e);
			}
		}
		else if (emailIdsArray != null && emailIdsArray.length > 0) {
			LOG.debug("Fetching users list to assign to the branch");
			List<User> assigneeUsers = getUsersFromEmailIds(emailIdsArray, user);

			if (assigneeUsers != null && !assigneeUsers.isEmpty()) {
				for (User assigneeUser : assigneeUsers) {
					try {
						assignBranchToUser(user, branch.getBranchId(), branch.getRegion().getRegionId(), assigneeUser, isAdmin);
					}
					catch (InvalidInputException | NoRecordsFetchedException | SolrException e) {
						LOG.error("Exception while assigning branch to a user. Reason:" + e.getMessage(), e);
						throw new UserAssignmentException(e.getMessage(), e);
					}
				}
			}
		}
		LOG.info("Method addNewBranchWithUser completed successfully");
		return branch;
	}

	/**
	 * Method to assign a branch to a user
	 */
	@Override
	@Transactional
	public void assignBranchToUser(User adminUser, long branchId, long regionId, User assigneeUser, boolean isAdmin) throws InvalidInputException,
			NoRecordsFetchedException, SolrException {
		if (adminUser == null) {
			throw new InvalidInputException("Admin user is null in assignBranchToUser");
		}
		if (branchId <= 0l) {
			throw new InvalidInputException("Branch id is invalid in assignBranchToUser");
		}
		if (regionId <= 0l) {
			throw new InvalidInputException("Region id is invalid in assignBranchToUser");
		}
		if (assigneeUser == null) {
			throw new InvalidInputException("assignee user is null in assignBranchToUser");
		}
		LOG.info("Method assignBranchToUser called for adminUser:" + adminUser + " branchId:" + branchId + " regionId" + regionId + "assigneeUser:"
				+ assigneeUser + " isAdmin:" + isAdmin);
		int profileMasterId = 0;
		if (isAdmin) {
			profileMasterId = CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID;
		}
		else {
			profileMasterId = CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID;
		}

		UserProfile userProfile = userManagementService.createUserProfile(assigneeUser, adminUser.getCompany(), assigneeUser.getEmailId(),
				assigneeUser.getUserId(), branchId, regionId, profileMasterId, CommonConstants.DASHBOARD_STAGE, CommonConstants.STATUS_ACTIVE,
				String.valueOf(adminUser.getUserId()), String.valueOf(adminUser.getUserId()));

		// check if user profile already exists
		if (assigneeUser.getUserProfiles() != null && !assigneeUser.getUserProfiles().isEmpty()) {
			for (UserProfile profile : assigneeUser.getUserProfiles()) {
				if (profile.getBranchId() == userProfile.getBranchId() && profile.getProfilesMaster() == userProfile.getProfilesMaster()
						&& profile.getStatus() == CommonConstants.STATUS_ACTIVE) {
					throw new InvalidInputException(DisplayMessageConstants.USER_ASSIGNMENT_ALREADY_EXISTS);
				}
			}
		}

		userProfileDao.save(userProfile);

		if (assigneeUser.getIsAtleastOneUserprofileComplete() == CommonConstants.STATUS_INACTIVE) {
			LOG.debug("Updating isAtleastOneProfileComplete as active for user : " + assigneeUser.getUserId());
			assigneeUser.setIsAtleastOneUserprofileComplete(CommonConstants.STATUS_ACTIVE);
			userDao.update(assigneeUser);
		}
		/**
		 * add newly created user profile to the list of user profiles in user object
		 */
		List<UserProfile> userProfiles = assigneeUser.getUserProfiles();
		if (userProfiles == null || userProfiles.isEmpty()) {
			userProfiles = new ArrayList<UserProfile>();
		}
		userProfiles.add(userProfile);
		assigneeUser.setUserProfiles(userProfiles);

		userManagementService.setProfilesOfUser(assigneeUser);
		
		AgentSettings agentSettings = organizationUnitSettingsDao.fetchAgentSettingsById(assigneeUser.getUserId());
		assigneeUser.setProfileName(agentSettings.getProfileName());
		assigneeUser.setProfileUrl(agentSettings.getProfileUrl());
		solrSearchService.addUserToSolr(assigneeUser);

		LOG.info("Method assignBranchToUser executed successfully");
	}

	/**
	 * Method to add a new user or assign existing user under a company/region or branch
	 * 
	 * @throws UserAssignmentException
	 */
	@Override
	@Transactional
	public void addIndividual(User adminUser, long selectedUserId, long branchId, long regionId, String[] emailIdsArray, boolean isAdmin)
			throws InvalidInputException, NoRecordsFetchedException, SolrException, UserAssignmentException {
		LOG.info("Method addIndividual called for adminUser:" + adminUser + " branchId:" + branchId + " regionId:" + regionId + " isAdmin:" + isAdmin);
		List<User> assigneeUsers = null;
		
		if (selectedUserId > 0l) {
			LOG.debug("Fetching user for selectedUserId " + selectedUserId);
			User assigneeUser = userDao.findById(User.class, selectedUserId);
			if (assigneeUser == null) {
				throw new NoRecordsFetchedException("No user found in db for selectedUserId:" + selectedUserId);
			}
			assigneeUsers = new ArrayList<User>();
			assigneeUsers.add(assigneeUser);
		}
		else if (emailIdsArray != null && emailIdsArray.length > 0) {
			LOG.debug("Fetching users list for the email addresses provided");
			assigneeUsers = getUsersFromEmailIds(emailIdsArray, adminUser);
		}
		
		if (assigneeUsers != null && !assigneeUsers.isEmpty()) {
			/**
			 * if branchId is provided, add the individual to specified branch
			 */
			if (branchId > 0l) {
				LOG.debug("assigning individual(s) to branch :" + branchId + " in addIndividual");
				for (User assigneeUser : assigneeUsers) {
					assignBranchToUser(adminUser, branchId, regionId, assigneeUser, isAdmin);
				}
			}
			/**
			 * else if regionId is provided, add the individual to specified region
			 */
			else if (regionId > 0l) {
				LOG.debug("assigning individual(s) to region :" + regionId + " in addIndividual");
				for (User assigneeUser : assigneeUsers) {
					assignRegionToUser(adminUser, regionId, assigneeUser, isAdmin);
				}
			}
			/**
			 * else assign the individual to company (i.e under default region)
			 */
			else {
				LOG.debug("assigning individual(s) to company in addIndividual");
				Region region = getDefaultRegionForCompany(adminUser.getCompany());
				if (region == null) {
					throw new NoRecordsFetchedException("No default region found for company while adding individual");
				}
				for (User assigneeUser : assigneeUsers) {
					assignRegionToUser(adminUser, region.getRegionId(), assigneeUser, isAdmin);
				}
			}

		}
		LOG.info("Method addNewIndividual executed successfully");
	}

	/**
	 * Fetch list of branches in a company
	 * 
	 * @param company
	 * @return List of branches
	 * @throws InvalidInputException
	 */
	@Override
	@Transactional
	public List<Branch> getAllBranchesForCompany(Company company) throws InvalidInputException {
		if (company == null) {
			LOG.error("Company object passed can not be null");
			throw new InvalidInputException("Invalid Company passed");
		}
		LOG.info("Fetching the list of branches for company :" + company.getCompany());
		Map<String, Object> queries = new HashMap<String, Object>();
		queries.put(CommonConstants.COMPANY_COLUMN, company);
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
		List<Branch> branchList = branchDao.findByKeyValue(Branch.class, queries);
		LOG.info("Branch list fetched for the company " + company);
		return branchList;
	}

	/**
	 * Fetch list of regions in a company
	 * 
	 * @param company
	 * @return List of regions
	 * @throws InvalidInputException
	 */
	@Override
	@Transactional
	public List<Region> getAllRegionsForCompany(Company company) throws InvalidInputException {
		if (company == null) {
			LOG.error("Company object passed can not be null");
			throw new InvalidInputException("Invalid Company passed");
		}

		LOG.info("Fetching the list of regions for company :" + company.getCompany());

		Map<String, Object> queries = new HashMap<String, Object>();
		queries.put(CommonConstants.COMPANY_COLUMN, company);
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);

		List<Region> regionList = regionDao.findByKeyValue(Region.class, queries);
		LOG.info("Region list fetched for the company " + company);
		return regionList;
	}

	/**
	 * Updates status of a branch
	 * 
	 * @param user
	 * @param branchId
	 * @param status
	 * @throws InvalidInputException
	 * @throws SolrException
	 */
	@Override
	@Transactional
	public void updateBranchStatus(User user, long branchId, int status) throws InvalidInputException, SolrException {
		LOG.info("Update branch of id :" + branchId + " status to :" + status);
		if (user == null) {
			throw new InvalidInputException("User is null in updateRegionStatus");
		}
		if (branchId <= 0l) {
			throw new InvalidInputException("BranchId is not set in updateRegionStatus");
		}

		LOG.debug("Fetching the branch object by ID");
		Branch branch = branchDao.findById(Branch.class, branchId);
		if (branch == null) {
			LOG.error("No branch present with the branch Id :" + branchId);
			throw new InvalidInputException("No branch present with the branch Id :" + branchId);
		}

		branch.setStatus(status);
		branch.setModifiedBy(String.valueOf(user.getUserId()));
		branch.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		branchDao.update(branch);

		LOG.debug("Updating document of the branch in solr");
		solrSearchService.addOrUpdateBranchToSolr(branch);
		LOG.info("Branch status for branch ID :" + branchId + "/t successfully updated to:" + status);
	}

	/**
	 * Updates the status of region
	 * 
	 * @param regionId
	 * @throws InvalidInputException
	 * @throws SolrException
	 */
	@Override
	@Transactional
	public void updateRegionStatus(User user, long regionId, int status) throws InvalidInputException, SolrException {
		LOG.info("Method updateRegionStatus called for regionId : " + regionId + " and status : " + status);
		if (user == null) {
			throw new InvalidInputException("User is null in updateRegionStatus");
		}
		if (regionId <= 0l) {
			throw new InvalidInputException("RegionId is not set in updateRegionStatus");
		}
		Region region = regionDao.findById(Region.class, regionId);
		if (region == null) {
			LOG.error("No region present with the region Id :" + regionId);
			throw new InvalidInputException("No region present with the region Id :" + regionId);
		}
		region.setStatus(status);
		region.setModifiedBy(String.valueOf(user.getUserId()));
		region.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		regionDao.update(region);

		LOG.debug("Updating document of the region in solr");
		solrSearchService.addOrUpdateRegionToSolr(region);

		LOG.info("Region status for region ID :" + regionId + "/t successfully updated to " + status);
	}

	/**
	 * Fetch list of branches in a company for a Region
	 * 
	 * @param regionId
	 * @return List of branches
	 * @throws InvalidInputException
	 */
	@Override
	@Transactional
	public List<Branch> getAllBranchesInRegion(long regionId) throws InvalidInputException {
		if (regionId <= 0l) {
			throw new InvalidInputException("RegionId is not set in getAllBranchesForRegion");
		}
		Region region = regionDao.findById(Region.class, regionId);
		if (region == null) {
			LOG.error("No region present with the region Id :" + regionId);
			throw new InvalidInputException("No region present with the region Id :" + regionId);
		}
		LOG.info("Fetching the list of branches for region :" + region);

		Map<String, Object> queries = new HashMap<String, Object>();
		queries.put(CommonConstants.REGION_COLUMN, region);
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
		List<Branch> branchList = branchDao.findByKeyValue(Branch.class, queries);

		LOG.info("Branch list fetched for the region " + region);
		return branchList;
	}

	/**
	 * Method to fetch count of branches in a company for a Region
	 * 
	 * @param regionId
	 * @return List of branches
	 * @throws InvalidInputException
	 */
	@Override
	@Transactional
	public long getCountBranchesInRegion(long regionId) throws InvalidInputException {
		if (regionId <= 0l) {
			throw new InvalidInputException("RegionId is not set in getAllBranchesForRegion");
		}
		Region region = regionDao.findById(Region.class, regionId);
		if (region == null) {
			LOG.error("No region present with the region Id :" + regionId);
			throw new InvalidInputException("No region present with the region Id :" + regionId);
		}
		LOG.info("Fetching the list of branches for region :" + region);

		Map<String, Object> queries = new HashMap<String, Object>();
		queries.put(CommonConstants.REGION_COLUMN, region);
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
		queries.put(CommonConstants.IS_DEFAULT_BY_SYSTEM, CommonConstants.NO);
		long branchCount = branchDao.findNumberOfRowsByKeyValue(Branch.class, queries);

		LOG.info("Branch list fetched for the region " + region);
		return branchCount;
	}

	/**
	 * Method to fetch UserProfiles associated with a branch
	 * 
	 * @param company
	 * @param branchId
	 * @return
	 * @throws InvalidInputException
	 */
	@Override
	@Transactional
	public List<UserProfile> getAllUserProfilesInBranch(long branchId) throws InvalidInputException {
		if (branchId <= 0l) {
			throw new InvalidInputException("RegionId is not set in getAllUserProfilesForBranch");
		}
		LOG.info("Fetching the list of users for branch :" + branchId);

		Map<String, Object> queries = new HashMap<String, Object>();
		queries.put(CommonConstants.BRANCH_ID_COLUMN, branchId);
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
		List<UserProfile> userList = userProfileDao.findByKeyValue(UserProfile.class, queries);

		LOG.info("Users list fetched for the branch " + branchId);
		return userList;
	}

	/**
	 * Method to fetch count of UserProfiles associated with a branch
	 * 
	 * @param company
	 * @param branchId
	 * @return
	 * @throws InvalidInputException
	 */
	@Override
	@Transactional
	public long getCountUsersInBranch(long branchId) throws InvalidInputException {
		if (branchId <= 0l) {
			throw new InvalidInputException("RegionId is not set in getAllUserProfilesForBranch");
		}
		LOG.info("Fetching the list of users for branch :" + branchId);

		Map<String, Object> queries = new HashMap<String, Object>();
		queries.put(CommonConstants.BRANCH_ID_COLUMN, branchId);
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
		long usersCount = userProfileDao.findNumberOfRowsByKeyValue(UserProfile.class, queries);

		LOG.info("Users list fetched for the branch " + branchId);
		return usersCount;
	}

	/**
	 * Method to check if branches allowed to be added have succeeded the max limit for a user and
	 * account type
	 * 
	 * @param user
	 * @param accountType
	 * @return
	 * @throws InvalidInputException
	 */
	@Override
	@Transactional
	public boolean isBranchAdditionAllowed(User user, AccountType accountType) throws InvalidInputException {
		LOG.info("Method to check if further branch addition is allowed, called for user : " + user);
		if (user == null) {
			throw new InvalidInputException("User is null in isBranchAdditionAllowed");
		}
		if (accountType == null) {
			throw new InvalidInputException("Account type is null in isBranchAdditionAllowed");
		}
		boolean isBranchAdditionAllowed = true;
		/**
		 * check is branch addition allowed on the basis of account type
		 */
		switch (accountType) {
			case INDIVIDUAL:
				LOG.debug("Checking branch addition for account type INDIVIDUAL");
				isBranchAdditionAllowed = false;
				break;
			case TEAM:
				LOG.debug("Checking branch addition for account type TEAM");
				isBranchAdditionAllowed = false;
				break;
			case COMPANY:
				LOG.debug("Checking branch addition for account type COMPANY");
				isBranchAdditionAllowed = true;
				break;
			case ENTERPRISE:
				LOG.debug("Checking branch addition for account type INDIVIDUAL");
				isBranchAdditionAllowed = true;
				break;
			default:
				throw new InvalidInputException("Account type is invalid in isBranchAdditionAllowed");
		}
		/**
		 * check is branch addition is allowed on the basis of profile level of the user, it is
		 * allowed only for the region and company admin
		 */
		if (isBranchAdditionAllowed) {
			if (user.isCompanyAdmin() || user.isRegionAdmin()) {
				isBranchAdditionAllowed = true;
			}
			else {
				isBranchAdditionAllowed = false;
			}
		}
		LOG.info("Returning from isBranchAdditionAllowed for user : " + user.getUserId() + " isBranchAdditionAllowed is :" + isBranchAdditionAllowed);

		return isBranchAdditionAllowed;
	}

	/**
	 * Method to check if regions allowed to be added have succeeded the max limit for a user and
	 * account type
	 * 
	 * @param user
	 * @param accountType
	 * @return
	 * @throws InvalidInputException
	 */
	@Override
	@Transactional
	public boolean isRegionAdditionAllowed(User user, AccountType accountType) throws InvalidInputException {
		LOG.info("Method to check if further region addition is allowed called for user : " + user);
		if (user == null) {
			throw new InvalidInputException("User is null in isRegionAdditionAllowed");
		}
		if (accountType == null) {
			throw new InvalidInputException("Account type is null in isRegionAdditionAllowed");
		}
		boolean isRegionAdditionAllowed = true;
		/**
		 * check is region addition allowed on the basis of account type
		 */
		switch (accountType) {
			case INDIVIDUAL:
				LOG.debug("Checking Region addition for account type INDIVIDUAL");
				isRegionAdditionAllowed = false;
				break;
			case TEAM:
				LOG.debug("Checking Region addition for account type TEAM");
				isRegionAdditionAllowed = false;
				break;
			case COMPANY:
				LOG.debug("Checking Region addition for account type COMPANY");
				isRegionAdditionAllowed = false;
				break;
			case ENTERPRISE:
				LOG.debug("Checking Region addition for account type ENTERPRISE");
				isRegionAdditionAllowed = true;
				break;
			default:
				throw new InvalidInputException("Account type is invalid in isRegionAdditionAllowed");
		}
		/**
		 * check is region allowed on the basis of profile level of the user, region addition is
		 * allowed only if the user is company admin
		 */
		if (isRegionAdditionAllowed) {
			if (user.isCompanyAdmin()) {
				isRegionAdditionAllowed = true;
			}
			else {
				isRegionAdditionAllowed = false;
			}
		}
		LOG.info("Returning from isRegionAdditionAllowed for user : " + user.getUserId() + " isRegionAdditionAllowed is :" + isRegionAdditionAllowed);
		return isRegionAdditionAllowed;
	}

	/**
	 * Method to add a new branch from UI
	 * 
	 * @param user
	 * @param regionId
	 * @param branchName
	 * @param branchAddress1
	 * @param branchAddress2
	 * @return
	 * @throws InvalidInputException
	 * @throws SolrException
	 */
	@Override
	@Transactional
	public Branch addNewBranch(User user, long regionId, int isDefaultBySystem, String branchName, String branchAddress1, String branchAddress2,
			String branchCountry, String branchCountryCode, String branchState, String branchCity, String branchZipcode)
			throws InvalidInputException, SolrException {
		if (user == null) {
			throw new InvalidInputException("User is null in addNewBranch");
		}
		if (branchName == null || branchName.isEmpty()) {
			throw new InvalidInputException("Branch name is null in addNewBranch");
		}
		if (branchAddress1 == null || branchAddress1.isEmpty()) {
			throw new InvalidInputException("Branch address is null in addNewBranch");
		}
		LOG.info("Method add new branch called for regionId : " + regionId + " and branchName : " + branchName);
		Region region = null;
		LOG.debug("Fetching region for branch to be added");
		/**
		 * If region is selected by user, select it from db
		 */
		if (regionId > 0l) {
			region = regionDao.findById(Region.class, regionId);
		}
		/**
		 * else select the default region from db for that company
		 */
		else {
			LOG.debug("Selecting the default region for company");
			Map<String, Object> queries = new HashMap<String, Object>();
			queries.put(CommonConstants.COMPANY, user.getCompany());
			queries.put(CommonConstants.IS_DEFAULT_BY_SYSTEM, CommonConstants.YES);
			List<Region> regions = regionDao.findByKeyValue(Region.class, queries);
			if (regions != null && !regions.isEmpty()) {
				region = regions.get(0);
			}
		}
		if (region == null) {
			throw new InvalidInputException("No region is present in db for the company while adding branch");
		}

		Branch branch = addBranch(user, region, branchName, isDefaultBySystem);
		branch.setAddress1(branchAddress1);
		branch.setAddress2(branchAddress2);
		branch.setCountry(branchCountry);
		branch.setState(branchState);
		branch.setCity(branchCity);
		branch.setZipcode(branchZipcode);
		branch.setCountryCode(branchCountryCode);
		branch.setBranchName(branchName);

		LOG.debug("Adding new branch into mongo");
		insertBranchSettings(branch);

		LOG.debug("Updating branch table with profile name");
		branchDao.update(branch);

		LOG.debug("Adding newly added branch to solr");
		solrSearchService.addOrUpdateBranchToSolr(branch);

		LOG.info("Successfully completed method add new branch for regionId : " + region.getRegionId() + " and branchName : " + branchName);
		return branch;

	}

	/**
	 * Method to generate profile name and profile url for a branch and also set them in
	 * organization unit settings
	 * 
	 * @param branch
	 * @param organizationSettings
	 * @throws InvalidInputException
	 */
	private void generateAndSetBranchProfileNameAndUrl(Branch branch, OrganizationUnitSettings organizationSettings) throws InvalidInputException {
		LOG.debug("Method to generate branch profile name called for branch: " + branch);
		String branchProfileName = null;
		if (branch == null) {
			throw new InvalidInputException("Branch is null in generateAndSetRegionProfileNameAndUrl");
		}
		String branchName = branch.getBranch();
		if (branchName == null || branchName.isEmpty()) {
			throw new InvalidInputException("Branch name is null or empty in generateAndSetRegionProfileNameAndUrl");
		}

		// branchProfileName = branchName.trim().replaceAll(" ", "-").toLowerCase();
		branchProfileName = utils.prepareProfileName(branchName);

		OrganizationUnitSettings companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(branch.getCompany().getCompanyId(),
				MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION);
		if (companySettings != null) {
			String companyProfileName = companySettings.getProfileName();
			String branchProfileUrl = utils.generateBranchProfileUrl(companyProfileName, branchProfileName);

			LOG.debug("Checking if profileName:" + branchProfileName + " is already taken by a branch in the company :" + branch.getCompany());
			/**
			 * Uniqueness of profile name is checked by url since combination of company profile
			 * name and branch profile name is unique
			 */
			OrganizationUnitSettings branchSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileUrl(branchProfileUrl,
					MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION);
			/**
			 * if there exists a branch with the profile name formed, append branch iden to get the
			 * unique profile name and also regenerate url with new profile name
			 */
			if (branchSettings != null) {
				LOG.debug("Profile name was not unique hence appending id to it to get a unique one");
				// branchProfileName = branchProfileName + "-" + branch.getBranchId();
				branchProfileName = utils.appendIdenToProfileName(branchProfileName, branch.getBranchId());
				branchProfileUrl = utils.generateBranchProfileUrl(companyProfileName, branchProfileName);
			}
			organizationSettings.setProfileName(branchProfileName);
			organizationSettings.setProfileUrl(branchProfileUrl);
			/**
			 * set profile name in branch for setting value in sql tables
			 */
			branch.setProfileName(branchProfileName);
		}
		else {
			LOG.warn("Company settings not found in generateAndSetRegionProfileNameAndUrl");
		}

		LOG.debug("Method to generate and set branch profile name and url excecuted successfully");
	}

	/**
	 * Method to form ContactDetailsSettings object from branch
	 * 
	 * @param branch
	 * @return
	 */
	private ContactDetailsSettings getContactDetailsSettingsFromBranch(Branch branch) {
		LOG.debug("Method getContactDetailsSettingsFromBranch called for branch :" + branch);
		ContactDetailsSettings contactSettings = new ContactDetailsSettings();
		contactSettings.setName(branch.getBranch());
		contactSettings.setAddress1(branch.getAddress1());

		if (branch.getAddress2() != null && !branch.getAddress2().isEmpty()) {
			contactSettings.setAddress(branch.getAddress1() + ", " + branch.getAddress2());
			contactSettings.setAddress2(branch.getAddress2());
		}
		else {
			contactSettings.setAddress(branch.getAddress1());
		}

		if (branch.getCountry() != null && !branch.getCountry().isEmpty()) {
			contactSettings.setCountry(branch.getCountry());
		}

		if (branch.getCountryCode() != null && !branch.getCountryCode().isEmpty()) {
			contactSettings.setCountryCode(branch.getCountryCode());
		}

		if (branch.getState() != null && !branch.getState().isEmpty()) {
			contactSettings.setState(branch.getState());
		}

		if (branch.getCity() != null && !branch.getCity().isEmpty()) {
			contactSettings.setCity(branch.getCity());
		}

		if (branch.getZipcode() != null && !branch.getZipcode().isEmpty()) {
			contactSettings.setZipcode(branch.getZipcode());
		}

		LOG.debug("Method getContactDetailsSettingsFromBranch finished.Returning :" + contactSettings);
		return contactSettings;
	}

	/**
	 * Method to form ContactDetailsSettings object from region
	 * 
	 * @param region
	 * @return
	 */
	private ContactDetailsSettings getContactDetailsSettingsFromRegion(Region region) {
		LOG.debug("Method getContactDetailsSettingsFromRegion called for branch :" + region);
		ContactDetailsSettings contactSettings = new ContactDetailsSettings();
		contactSettings.setName(region.getRegion());
		contactSettings.setAddress1(region.getAddress1());

		if (region.getAddress2() != null && !region.getAddress2().isEmpty()) {
			contactSettings.setAddress(region.getAddress1() + ", " + region.getAddress2());
			contactSettings.setAddress2(region.getAddress2());
		}
		else {
			contactSettings.setAddress(region.getAddress1());
		}

		if (region.getCountry() != null && !region.getCountry().isEmpty()) {
			contactSettings.setCountry(region.getCountry());
		}

		if (region.getCountryCode() != null && !region.getCountryCode().isEmpty()) {
			contactSettings.setCountryCode(region.getCountryCode());
		}

		if (region.getState() != null && !region.getState().isEmpty()) {
			contactSettings.setState(region.getState());
		}

		if (region.getCity() != null && !region.getCity().isEmpty()) {
			contactSettings.setCity(region.getCity());
		}

		if (region.getZipcode() != null && !region.getZipcode().isEmpty()) {
			contactSettings.setZipcode(region.getZipcode());
		}

		LOG.debug("Method getContactDetailsSettingsFromRegion finished.Returning :" + contactSettings);
		return contactSettings;
	}

	/**
	 * Method to add a new region
	 * 
	 * @param user
	 * @param regionName
	 * @param address1
	 * @param address2
	 * @return
	 * @throws InvalidInputException
	 * @throws SolrException
	 */
	@Override
	@Transactional
	public Region addNewRegion(User user, String regionName, int isDefaultBySystem, String address1, String address2, String country,
			String countryCode, String state, String city, String zipcode) throws InvalidInputException, SolrException {
		if (user == null) {
			throw new InvalidInputException("User is null in addNewRegion");
		}
		if (regionName == null || regionName.isEmpty()) {
			throw new InvalidInputException("Region name is null in addNewRegion");
		}
		LOG.info("Method add new region called for regionName : " + regionName);

		Region region = addRegion(user, isDefaultBySystem, regionName);
		region.setAddress1(address1);
		region.setAddress2(address2);
		region.setCountry(country);
		region.setCountryCode(countryCode);
		region.setState(state);
		region.setCity(city);
		region.setZipcode(zipcode);

		LOG.debug("Calling method to insert region settings");
		insertRegionSettings(region);

		regionDao.update(region);

		LOG.debug("Updating solr with newly inserted region");
		solrSearchService.addOrUpdateRegionToSolr(region);

		LOG.info("Successfully completed method add new region for regionName : " + regionName);
		return region;
	}

	/**
	 * Method to generate profile name and profile url for a region and also set them in
	 * organization unit settings
	 * 
	 * @param region
	 * @return
	 * @throws InvalidInputException
	 */
	private void generateAndSetRegionProfileNameAndUrl(Region region, OrganizationUnitSettings organizationSettings) throws InvalidInputException {
		LOG.debug("Method generateAndSetRegionProfileNameAndUrl called for region: " + region);
		String regionProfileName = null;
		if (region == null) {
			throw new InvalidInputException("Region is null in generateAndSetRegionProfileNameAndUrl");
		}
		String regionName = region.getRegion();
		if (regionName == null || regionName.isEmpty()) {
			throw new InvalidInputException("Region name is null or empty in generateAndSetRegionProfileNameAndUrl");
		}

		// regionProfileName = regionName.trim().replaceAll(" ", "-").toLowerCase();
		regionProfileName = utils.prepareProfileName(regionName);
		LOG.debug("Checking if profileName:" + regionProfileName + " is already taken by a region in the company :" + region.getCompany());

		OrganizationUnitSettings companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(region.getCompany().getCompanyId(),
				MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION);
		if (companySettings != null) {
			String companyProfileName = companySettings.getProfileName();
			String regionProfileUrl = utils.generateRegionProfileUrl(companyProfileName, regionProfileName);

			/**
			 * Uniqueness of profile name is checked by url since combination of company profile
			 * name and region profile name is unique
			 */
			OrganizationUnitSettings regionSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileUrl(regionProfileUrl,
					MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION);
			/**
			 * if there exists a region with the profile name formed, append region iden to get the
			 * unique profile name and also regenerate url with new profile name
			 */
			if (regionSettings != null) {
				LOG.debug("Profile name was not unique hence appending id to it to get a unique one");
				// regionProfileName = regionProfileName + "-" + region.getRegionId();
				regionProfileName = utils.appendIdenToProfileName(regionProfileName, region.getRegionId());
				regionProfileUrl = utils.generateRegionProfileUrl(companyProfileName, regionProfileName);
			}
			organizationSettings.setProfileName(regionProfileName);
			organizationSettings.setProfileUrl(regionProfileUrl);

			/**
			 * Set the profile name in region object to update in sql later
			 */
			region.setProfileName(regionProfileName);
		}
		else {
			LOG.warn("Company settings not found in generateAndSetRegionProfileNameAndUrl");
		}

		LOG.debug("Method generateAndSetRegionProfileNameAndUrl excecuted successfully");
	}

	/**
	 * Method to check whether a user can view region based on his profiles
	 * 
	 * @param userProfiles
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean isRegionViewAllowed(List<UserProfile> userProfiles) {
		// TODO implement this
		return true;
	}

	/**
	 * Method to check whether a user can view branch based on his profiles
	 * 
	 * @param userProfiles
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean isBranchViewAllowed(List<UserProfile> userProfiles) {
		// TODO implement this
		return true;
	}

	/**
	 * Method to check whether a user has privileges to build hierarchy
	 */
	@Override
	public boolean canBuildHierarchy(User user, AccountType accountType) {
		// TODO implement this
		return true;
	}

	/**
	 * Method to check whether a user has privileges to edit company information
	 */
	@Override
	public boolean canEditCompany(User user, AccountType accountType) {
		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * Method to insert region settings into mongo
	 * 
	 * @param region
	 * @throws InvalidInputException
	 */
	public void insertRegionSettings(Region region) throws InvalidInputException {
		LOG.info("Method for inserting region settings called for region : " + region);
		OrganizationUnitSettings organizationSettings = new OrganizationUnitSettings();
		organizationSettings.setIden(region.getRegionId());
		// set is default flag
		boolean isDefaultFlag = false;
		if (region.getIsDefaultBySystem() == CommonConstants.YES) {
			isDefaultFlag = true;
		}
		organizationSettings.setDefaultBySystem(isDefaultFlag);
		// set the seo content mdified to true, so that batch pick this record up
		organizationSettings.setSeoContentModified(true);
		organizationSettings.setCreatedBy(region.getCreatedBy());
		organizationSettings.setCreatedOn(System.currentTimeMillis());
		organizationSettings.setModifiedBy(region.getModifiedBy());
		organizationSettings.setModifiedOn(System.currentTimeMillis());
		organizationSettings.setVertical(region.getCompany().getVerticalsMaster().getVerticalName());

		// Calling method to generate and set region profile name and url
		generateAndSetRegionProfileNameAndUrl(region, organizationSettings);

		ContactDetailsSettings contactSettings = getContactDetailsSettingsFromRegion(region);
		organizationSettings.setContact_details(contactSettings);
		organizationSettings.setLockSettings(new LockSettings());

		// set default profile stages.
		organizationSettings.setProfileStages(profileCompletionList.getDefaultProfileCompletionList(false));

		organizationUnitSettingsDao.insertOrganizationUnitSettings(organizationSettings,
				MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION);
		LOG.info("Method for inserting region settings finished");
	}

	/**
	 * Method to insert branch settings into mongo
	 * 
	 * @param branch
	 * @throws InvalidInputException
	 */
	public void insertBranchSettings(Branch branch) throws InvalidInputException {
		LOG.info("Method to insert branch settings called for branch : " + branch);
		OrganizationUnitSettings organizationSettings = new OrganizationUnitSettings();
		organizationSettings.setIden(branch.getBranchId());
		// set is default flag
		boolean isDefaultFlag = false;
		if (branch.getIsDefaultBySystem() == CommonConstants.YES) {
			isDefaultFlag = true;
		}
		// set the seo content mdified to true, so that batch pick this record up
		organizationSettings.setSeoContentModified(true);
		organizationSettings.setDefaultBySystem(isDefaultFlag);
		organizationSettings.setCreatedBy(branch.getCreatedBy());
		organizationSettings.setCreatedOn(System.currentTimeMillis());
		organizationSettings.setModifiedBy(branch.getModifiedBy());
		organizationSettings.setModifiedOn(System.currentTimeMillis());
		organizationSettings.setVertical(branch.getCompany().getVerticalsMaster().getVerticalName());

		// Calling method to generate and set profile name and profile url
		generateAndSetBranchProfileNameAndUrl(branch, organizationSettings);

		ContactDetailsSettings contactSettings = getContactDetailsSettingsFromBranch(branch);
		organizationSettings.setContact_details(contactSettings);
		organizationSettings.setLockSettings(new LockSettings());

		// set default profile stages.
		organizationSettings.setProfileStages(profileCompletionList.getDefaultProfileCompletionList(false));

		organizationUnitSettingsDao.insertOrganizationUnitSettings(organizationSettings,
				MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION);
		LOG.info("Method to insert branch settings finished for branch : " + branch);
	}

	/**
	 * Method to get the list of region ids for a user and profile master id specified
	 */
	@Override
	@Transactional
	public Set<Long> getRegionIdsForUser(User user, int profileMasterId) throws InvalidInputException, NoRecordsFetchedException {
		if (profileMasterId <= 0l) {
			throw new InvalidInputException("Profile master id is not specified in getRegionIdsForUser");
		}
		if (user == null) {
			throw new InvalidInputException("User is null in getRegionIdsForUser");
		}
		LOG.info("Method getRegionIdsForUser called for profileMasterId: " + profileMasterId + " and user:" + user);
		Set<Long> regionIds = null;
		List<UserProfile> userProfiles = user.getUserProfiles();
		if (userProfiles == null || userProfiles.isEmpty()) {
			userProfiles = userManagementService.getAllUserProfilesForUser(user);
		}
		if (userProfiles == null || userProfiles.isEmpty()) {
			throw new NoRecordsFetchedException("No user profile found for the user in getRegionIdsForUser");
		}
		regionIds = new HashSet<Long>();
		for (UserProfile userProfile : userProfiles) {
			if (userProfile.getProfilesMaster().getProfileId() == profileMasterId) {
				regionIds.add(userProfile.getRegionId());
			}
		}
		LOG.info("Method getRegionIdsForUser executed successfully. Returning: " + regionIds);
		return regionIds;
	}

	/**
	 * Method to get the list of branch ids for a user and profile master id specified
	 */
	public Set<Long> getBranchIdsForUser(User user, int profileMasterId) throws InvalidInputException, NoRecordsFetchedException {
		if (profileMasterId <= 0l) {
			throw new InvalidInputException("Profile master id is not specified in getBranchIdsForUser");
		}
		if (user == null) {
			throw new InvalidInputException("User is null in getBranchIdsForUser");
		}
		LOG.info("Method getBranchIdsForUser called for profileMasterId: " + profileMasterId + " and user:" + user);
		Set<Long> branchIds = null;
		List<UserProfile> userProfiles = user.getUserProfiles();
		if (userProfiles == null || userProfiles.isEmpty()) {
			userProfiles = userManagementService.getAllUserProfilesForUser(user);
		}
		if (userProfiles == null || userProfiles.isEmpty()) {
			throw new NoRecordsFetchedException("No user profile found for the user in getBranchIdsForUser");
		}
		branchIds = new HashSet<Long>();
		for (UserProfile userProfile : userProfiles) {
			if (userProfile.getProfilesMaster().getProfileId() == profileMasterId) {
				branchIds.add(userProfile.getBranchId());
			}
		}
		return branchIds;
	}

	/**
	 * Method to get all branches under the regions specified
	 */
	@Override
	@Transactional
	public List<Branch> getBranchesByRegionIds(Set<Long> regionIds) throws InvalidInputException {
		LOG.info("Method getBranchesByRegionIds called for regionIds:" + regionIds);
		List<Branch> branches = null;
		if (regionIds != null && !regionIds.isEmpty()) {
			branches = new ArrayList<Branch>();
			for (long regionId : regionIds) {
				List<Branch> tempBranches = getBranchesByRegionId(regionId);
				if (tempBranches != null && !tempBranches.isEmpty()) {
					branches.addAll(tempBranches);
				}
			}
		}
		LOG.info("Method getBranchesByRegionIds executed successfully");
		return branches;
	}

	/**
	 * Method to get the list of all the company ids
	 */
	@Override
	@Transactional
	public Set<Company> getAllCompanies() {
		LOG.info("Method to get list of all companies, getAllCompanies() started");
		Set<Company> companies = new HashSet<Company>(companyDao.findAllActive(Company.class));
		LOG.info("Method to get list of all companies, getAllCompanies() finished");
		return companies;
	}

	@Override
	public Map<Long, BranchFromSearch> fetchBranchesMapByCompany(long companyId) throws InvalidInputException, SolrException, MalformedURLException {
		String branchesResult = solrSearchService.fetchBranchesByCompany(companyId);

		// convert branches to map
		Type searchedBranchesList = new TypeToken<List<BranchFromSearch>>() {}.getType();
		List<BranchFromSearch> branchList = new Gson().fromJson(branchesResult, searchedBranchesList);

		Map<Long, BranchFromSearch> branches = new HashMap<Long, BranchFromSearch>();
		for (BranchFromSearch branch : branchList) {
			branches.put(branch.getBranchId(), branch);
		}
		
		// Fetch all the branches' settings from Mongo
		List<OrganizationUnitSettings> branchSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsForMultipleIds(branches.keySet(), MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION);
		for(OrganizationUnitSettings setting : branchSettings){
			if(branches.containsKey(setting.getIden())){
				BranchFromSearch branchFromSearch = branches.get(setting.getIden());
				if(setting.getContact_details() != null){
					branchFromSearch.setCity(setting.getContact_details().getCity());
					branchFromSearch.setState(setting.getContact_details().getState());
					branchFromSearch.setZipcode(setting.getContact_details().getZipcode());
					branchFromSearch.setCountry(setting.getContact_details().getCountry());
					branchFromSearch.setCountryCode(setting.getContact_details().getCountryCode());
				}
			}
		}
		return branches;
	}

	@Override
	public Map<Long, RegionFromSearch> fetchRegionsMapByCompany(long companyId) throws InvalidInputException, SolrException, MalformedURLException {
		String regionsResult = solrSearchService.fetchRegionsByCompany(companyId);

		// convert regions to map
		Type searchedRegionsList = new TypeToken<List<RegionFromSearch>>() {}.getType();
		List<RegionFromSearch> regionsList = new Gson().fromJson(regionsResult, searchedRegionsList);

		Map<Long, RegionFromSearch> regions = new HashMap<Long, RegionFromSearch>();
		for (RegionFromSearch region : regionsList) {
			regions.put(region.getRegionId(), region);
		}
		return regions;
	}

	/**
	 * Method to get the list of branches from solr which are directly assigned to the company
	 * 
	 * @throws NoRecordsFetchedException
	 * @throws SolrException
	 */
	@Override
	@Transactional
	public List<BranchFromSearch> getBranchesUnderCompanyFromSolr(Company company, int start, int rows) throws InvalidInputException,
			NoRecordsFetchedException, SolrException {
		if (company == null) {
			throw new InvalidInputException("company is null in getBranchesUnderCompanyFromSolr");
		}
		LOG.info("Method getBranchesUnderCompanyFromSolr called for company:" + company + " and start:" + start + " rows:" + rows);
		List<BranchFromSearch> branches = null;
		Region defaultRegion = getDefaultRegionForCompany(company);
		String branchesJson = solrSearchService.searchBranchesByRegion(defaultRegion.getRegionId(), start, rows);
		LOG.debug("branchesJson obtained from solr is:" + branchesJson);

		Type searchedBranchesList = new TypeToken<List<BranchFromSearch>>() {}.getType();
		branches = new Gson().fromJson(branchesJson, searchedBranchesList);

		LOG.info("Method getBranchesUnderCompanyFromSolr executed successfully");
		return branches;
	}

	/**
	 * Method to get the list of users from solr which are directly assigned to the company
	 */
	@Override
	@Transactional
	public List<UserFromSearch> getUsersUnderCompanyFromSolr(Company company, int start, int rows) throws InvalidInputException,
			NoRecordsFetchedException, SolrException {
		if (company == null) {
			throw new InvalidInputException("company is null in getUsersUnderCompanyFromSolr");
		}
		LOG.info("Method getUsersUnderCompanyFromSolr called for company:" + company + " and start:" + start + " rows:" + rows);
		List<UserFromSearch> users = null;
		Region defaultRegion = getDefaultRegionForCompany(company);
		Branch defaultBranch = getDefaultBranchForRegion(defaultRegion.getRegionId());
		SolrDocumentList usersResult = solrSearchService.searchUsersByIden(defaultBranch.getBranchId(), CommonConstants.BRANCHES_SOLR, false, start,
				rows);
		String usersJson = JSONUtil.toJSON(usersResult);
		LOG.debug("Solr result returned for users of company is:" + usersJson);
		/**
		 * convert users to Object
		 */
		Type searchedUsersList = new TypeToken<List<UserFromSearch>>() {}.getType();
		users = new Gson().fromJson(usersJson, searchedUsersList);
		return users;
	}

	@Override
	@Transactional
	public List<UserFromSearch> getUsersUnderRegionFromSolr(Set<Long> regionIds, int start, int rows) throws InvalidInputException,
			NoRecordsFetchedException, SolrException {
		if (regionIds == null || regionIds.isEmpty()) {
			throw new InvalidInputException("region ids are null in getUsersUnderRegionFromSolr");
		}
		LOG.info("Method getUsersUnderRegionFromSolr called for regionIds:" + regionIds + " and start:" + start + " rows:" + rows);
		List<UserFromSearch> users = null;
		Set<Long> branchIds = new HashSet<Long>();
		for (long regionId : regionIds) {
			Branch branch = getDefaultBranchForRegion(regionId);
			branchIds.add(branch.getBranchId());
		}
		String usersJson = solrSearchService.searchUsersByBranches(branchIds, start, rows);
		LOG.debug("Solr result returned for users of regions is:" + usersJson);
		/**
		 * convert users to Object
		 */
		Type searchedUsersList = new TypeToken<List<UserFromSearch>>() {}.getType();
		users = new Gson().fromJson(usersJson, searchedUsersList);

		LOG.info("Method getUsersUnderRegionFromSolr executed successfully");
		return users;
	}

	/**
	 * Method to update a region and assign user if specified
	 */
	@Override
	@Transactional
	public Region updateRegion(User user, long regionId, String regionName, String address1, String address2, String country, String countryCode,
			String state, String city, String zipcode, long selectedUserId, String[] emailIdsArray, boolean isAdmin) throws InvalidInputException,
			SolrException, NoRecordsFetchedException, UserAssignmentException {
		if (user == null) {
			throw new InvalidInputException("User is null in update region");
		}
		if (regionName == null || regionName.isEmpty()) {
			throw new InvalidInputException("Region name is null in update region");
		}
		if (regionId <= 0l) {
			throw new InvalidInputException("Region id is invalid in update region");
		}
		LOG.info("Method update region called for regionId:" + regionId + " regionName : " + regionName + " ,address1:" + address1);
		Region region = regionDao.findById(Region.class, regionId);
		if (region == null) {
			throw new NoRecordsFetchedException("No region present for the required id in database while updating region");
		}
		region.setRegion(regionName);
		region.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		region.setModifiedBy(String.valueOf(user.getUserId()));
		region.setAddress1(address1);
		region.setAddress2(address2);
		region.setCountry(country);
		region.setCountryCode(countryCode);
		region.setState(state);
		region.setCity(city);
		region.setZipcode(zipcode);
		regionDao.update(region);

		LOG.debug("Updating region in mongo");
		ContactDetailsSettings contactDetailsSettings = getContactDetailsSettingsFromRegion(region);
		organizationUnitSettingsDao.updateKeyOrganizationUnitSettingsByCriteria(MongoOrganizationUnitSettingDaoImpl.KEY_CONTACT_DETAIL_SETTINGS,
				contactDetailsSettings, MongoOrganizationUnitSettingDaoImpl.KEY_IDENTIFIER, regionId,
				MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION);

		LOG.debug("Updating region in solr");
		solrSearchService.addOrUpdateRegionToSolr(region);

		/**
		 * If userId or email is provided, call the service for adding and assigning user to the
		 * updated region
		 */
		if (selectedUserId > 0l) {
			LOG.debug("Fetching user for selectedUserId " + selectedUserId + "to assign to the region");
			User assigneeUser = userDao.findById(User.class, selectedUserId);
			if (assigneeUser == null) {
				throw new NoRecordsFetchedException("No user found in db for selectedUserId:" + selectedUserId);
			}
			try {
				assignRegionToUser(user, region.getRegionId(), assigneeUser, isAdmin);
			}
			catch (InvalidInputException | NoRecordsFetchedException | SolrException e) {
				LOG.error("Exception while assigning region to a user. Reason:" + e.getMessage(), e);
				throw new UserAssignmentException(e.getMessage(), e);
			}
		}
		else if (emailIdsArray != null && emailIdsArray.length > 0) {
			LOG.debug("Fetching users list to assign to the region");
			List<User> assigneeUsers = getUsersFromEmailIds(emailIdsArray, user);

			if (assigneeUsers != null && !assigneeUsers.isEmpty()) {
				for (User assigneeUser : assigneeUsers) {
					try {
						assignRegionToUser(user, region.getRegionId(), assigneeUser, isAdmin);
					}
					catch (InvalidInputException | NoRecordsFetchedException | SolrException e) {
						LOG.error("Exception while assigning region to a user. Reason:" + e.getMessage(), e);
						throw new UserAssignmentException(e.getMessage(), e);
					}
				}
			}
		}
		LOG.info("Method to update region completed successfully");
		return region;
	}

	/**
	 * Method to update a branch
	 * 
	 * @throws SolrException
	 */
	@Override
	@Transactional
	public void updateBranch(long branchId, long regionId, String branchName, String branchAddress1, String branchAddress2, User user)
			throws InvalidInputException, SolrException {
		if (user == null) {
			throw new InvalidInputException("User is null in update branch");
		}
		if (branchName == null || branchName.isEmpty()) {
			throw new InvalidInputException("Branch name is null in update branch");
		}
		if (branchAddress1 == null || branchAddress1.isEmpty()) {
			throw new InvalidInputException("Branch address is null in update branch");
		}
		if (branchId <= 0l) {
			throw new InvalidInputException("Branch id is invalid in update branch");
		}

		LOG.info("Method update branch called for branchId:" + branchId + " ,regionId:" + regionId + " branchName : " + branchName
				+ " ,branchAddress:" + branchAddress1);
		Branch branch = branchDao.findById(Branch.class, branchId);
		if (branch == null) {
			throw new InvalidInputException("No branch present for the required id in database while updating branch");
		}
		LOG.debug("Checking if the region of branch is changed");

		/**
		 * In case of branch attached to default region, regionId is 0 hence perform update only
		 * when the regionId is not the default one
		 */
		if (regionId > 0l && regionId != branch.getRegion().getRegionId()) {
			Region region = regionDao.findById(Region.class, regionId);
			if (region == null) {
				throw new InvalidInputException("No region present for the required id in database while updating branch");
			}
			branch.setRegion(region);
		}
		branch.setBranch(branchName);
		branch.setAddress1(branchAddress1);
		branch.setAddress2(branchAddress2);
		branch.setModifiedBy(String.valueOf(user.getUserId()));
		branch.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		branchDao.update(branch);

		LOG.debug("Update branch in mongo");
		ContactDetailsSettings contactDetailsSettings = getContactDetailsSettingsFromBranch(branch);
		organizationUnitSettingsDao.updateKeyOrganizationUnitSettingsByCriteria(MongoOrganizationUnitSettingDaoImpl.KEY_CONTACT_DETAIL_SETTINGS,
				contactDetailsSettings, MongoOrganizationUnitSettingDaoImpl.KEY_IDENTIFIER, branchId,
				MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION);

		LOG.debug("Updating branch in solr");
		solrSearchService.addOrUpdateBranchToSolr(branch);
		LOG.info("Method to update branch completed successfully");
	}

	/**
	 * Method to update a branch
	 */
	@Override
	@Transactional
	public Branch updateBranch(User user, long branchId, long regionId, String branchName, String address1, String address2, String country,
			String countryCode, String state, String city, String zipcode, long selectedUserId, String[] emailIdsArray, boolean isAdmin)
			throws InvalidInputException, SolrException, NoRecordsFetchedException, UserAssignmentException {
		LOG.info("Method updateBranch called for branchId:" + branchId + " regionId:" + regionId + " branchName:" + branchName);
		if (user == null) {
			throw new InvalidInputException("User is null in update branch");
		}
		if (branchName == null || branchName.isEmpty()) {
			throw new InvalidInputException("Branch name is null in update branch");
		}
		if (branchId <= 0l) {
			throw new InvalidInputException("Branch id is invalid in update branch");
		}
		if (address1 == null || address1.isEmpty()) {
			throw new InvalidInputException("Branch address is null in update branch");
		}
		Branch branch = branchDao.findById(Branch.class, branchId);
		if (branch == null) {
			throw new NoRecordsFetchedException("No branch present for the required id in database while updating branch");
		}
		LOG.debug("Checking if the region of branch is changed");

		Region defaultRegion = getDefaultRegionForCompany(user.getCompany());
		if (regionId <= 0l) {
			regionId = defaultRegion.getRegionId();
		}

		/**
		 * Perform update only when the regionId is not same as the previous regionId
		 */
		if (regionId != branch.getRegion().getRegionId()) {
			Region region = null;
			if (regionId == defaultRegion.getRegionId()) {
				region = defaultRegion;
			}
			else {
				region = regionDao.findById(Region.class, regionId);
			}

			if (region == null) {
				throw new NoRecordsFetchedException("No region present for the required id in database while updating branch");
			}
			branch.setRegion(region);
		}
		branch.setBranch(branchName);
		branch.setAddress1(address1);
		branch.setAddress2(address2);
		branch.setCountry(country);
		branch.setCountryCode(countryCode);
		branch.setState(state);
		branch.setCity(city);
		branch.setZipcode(zipcode);
		branch.setModifiedBy(String.valueOf(user.getUserId()));
		branch.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		branchDao.update(branch);

		LOG.debug("Update branch in mongo");
		ContactDetailsSettings contactDetailsSettings = getContactDetailsSettingsFromBranch(branch);
		organizationUnitSettingsDao.updateKeyOrganizationUnitSettingsByCriteria(MongoOrganizationUnitSettingDaoImpl.KEY_CONTACT_DETAIL_SETTINGS,
				contactDetailsSettings, MongoOrganizationUnitSettingDaoImpl.KEY_IDENTIFIER, branchId,
				MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION);

		LOG.debug("Updating branch in solr");
		solrSearchService.addOrUpdateBranchToSolr(branch);

		/**
		 * If userId or email is provided, call the service for adding and assigning user to the
		 * newly created branch
		 */
		if (selectedUserId > 0l) {
			LOG.debug("Fetching user for selectedUserId " + selectedUserId + "to assign to the branch");
			User assigneeUser = userDao.findById(User.class, selectedUserId);
			if (assigneeUser == null) {
				throw new NoRecordsFetchedException("No user found in db for selectedUserId:" + selectedUserId);
			}
			try {
				assignBranchToUser(user, branch.getBranchId(), branch.getRegion().getRegionId(), assigneeUser, isAdmin);
			}
			catch (InvalidInputException | NoRecordsFetchedException | SolrException e) {
				LOG.error("Exception while assigning branch to a user. Reason:" + e.getMessage(), e);
				throw new UserAssignmentException(e.getMessage(), e);
			}
		}
		else if (emailIdsArray != null && emailIdsArray.length > 0) {
			LOG.debug("Fetching users list to assign to the branch");
			List<User> assigneeUsers = getUsersFromEmailIds(emailIdsArray, user);

			if (assigneeUsers != null && !assigneeUsers.isEmpty()) {
				for (User assigneeUser : assigneeUsers) {
					try {
						assignBranchToUser(user, branch.getBranchId(), branch.getRegion().getRegionId(), assigneeUser, isAdmin);
					}
					catch (InvalidInputException | NoRecordsFetchedException | SolrException e) {
						LOG.error("Exception while assigning branch to a user. Reason:" + e.getMessage(), e);
						throw new UserAssignmentException(e.getMessage(), e);
					}
				}
			}
		}
		LOG.info("Method to update branch completed successfully");
		return branch;
	}

	/*
	 * Method to read default survey mail content from EmailTemplate which will be store into the
	 * Company Settings.
	 */
	private String readMailContentFromFile(String fileName) throws IOException {
		LOG.debug("readSurveyReminderMailContentFromFile() started");
		BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(fileName)));
		StringBuilder content = new StringBuilder();
		String line = reader.readLine();
		try {
			while (line != null) {
				content.append(line);
				line = reader.readLine();
			}
		}
		finally {
			reader.close();
		}
		LOG.debug("readSurveyReminderMailContentFromFile() finished");
		return content.toString();
	}

	/*
	 * Method to fetch the list of states in US
	 */
	@Override
	@Transactional
	public List<StateLookup> getUsStateList() {
		LOG.debug("Method getUSStateList called to fetch the list of states in US");
		List<StateLookup> lookups = stateLookupDao.findByKeyValueAscending(StateLookup.class, new HashMap<String, Object>(), "statename");
		return lookups;
	}

	/*
	 * Method to fetch the list of cities and zipcodes in the state
	 */
	@Override
	@Transactional
	public String getZipCodesByStateId(int stateId) {
		LOG.debug("Method getZipCodesByStateId called to fetch the list of cities and zipcodes in the state");
		StateLookup state = stateLookupDao.findById(StateLookup.class, stateId);
		List<ZipCodeLookup> zipCodes = (List<ZipCodeLookup>) zipCodeLookupDao.findByColumn(ZipCodeLookup.class, "stateLookup", state);
		Gson gson = new GsonBuilder().setExclusionStrategies(new ZipCodeExclusionStrategy()).create();
		for (ZipCodeLookup s : zipCodes) {
			gson.toJson(s);
		}
		return gson.toJson(zipCodes);
	}

	/*
	 * Method to disable accounts who have exceeded their billing cycle and return their list.
	 */
	@Override
	@Transactional
	public List<DisabledAccount> disableAccounts(Date maxDisableDate) {
		LOG.info("Method getDisabledAccounts started.");
		try {
			
			List<DisabledAccount> disabledAccounts = disabledAccountDao.disableAccounts(maxDisableDate);
			for(DisabledAccount account : disabledAccounts){
				Company company = companyDao.findById(Company.class,account.getCompany().getCompanyId());
				company.setStatus(CommonConstants.STATUS_COMPANY_DISABLED);
				companyDao.update(company);
			}
			LOG.info("Method getDisabledAccounts finished.");
			return disabledAccounts;
		}
		catch (DatabaseException e) {
			LOG.error("Database exception caught in getDisabledAccounts(). Nested exception is ", e);
			throw e;
		}
	}
	
	/*
	 * Method to disable accounts who have exceeded their billing cycle and return their list.
	 */
	@Override
	@Transactional
	public List<DisabledAccount> getAccountsForPurge(int graceSpan) {
		LOG.info("Method getAccountsForPurge started.");
		try {
			LOG.info("Method getAccountsForPurge finished.");
			List<DisabledAccount> disabledAccounts = disabledAccountDao.getAccountsForPurge(graceSpan);
			return disabledAccounts;
		}
		catch (DatabaseException e) {
			LOG.error("Database exception caught in getAccountsForPurge(). Nested exception is ", e);
			throw e;
		}
	}
	
	/*
	 * Method to purge all the details of the given company(Not recoverable).
	 */
	@Override
	@Transactional
	public void purgeCompany(Company company) throws InvalidInputException, SolrException{
		LOG.info("Method getAccountsForPurge started.");
		try {
			LOG.info("Method getAccountsForPurge finished.");
			
			if(company == null){
				LOG.error("Null value passed for company in purgeCompany(). Returning...");
				throw new InvalidInputException("Null value passed for company in purgeCompany().");
			}
			
			List<Long> agentIds = solrSearchService.searchUserIdsByCompany(company.getCompanyId());
			organizationUnitSettingsDao.removeOganizationUnitSettings(agentIds, CommonConstants.AGENT_SETTINGS_COLLECTION);
			solrSearchService.removeUsersFromSolr(agentIds);
			// Deleting all the users of company from MySQL
			userProfileDao.deleteUserProfilesByCompany(company.getCompanyId());
			// Delete foreign key references from Removed users.
			removedUserDao.deleteRemovedUsersByCompany(company.getCompanyId());
			userDao.deleteUsersByCompanyId(company.getCompanyId());
			
			List<Long> branchIds = solrSearchService.searchBranchIdsByCompany(company.getCompanyId());
			organizationUnitSettingsDao.removeOganizationUnitSettings(branchIds, CommonConstants.BRANCH_SETTINGS_COLLECTION);
			solrSearchService.removeBranchesFromSolr(branchIds);
			// Deleting all the branches of company from MySQL
			branchDao.deleteBranchesByCompanyId(company.getCompanyId());
			
			List<Long> regionIds = solrSearchService.searchRegionIdsByCompany(company.getCompanyId());
			organizationUnitSettingsDao.removeOganizationUnitSettings(regionIds, CommonConstants.REGION_SETTINGS_COLLECTION);
			solrSearchService.removeRegionsFromSolr(regionIds);
			// Deleting all the regions of company from MySQL
			regionDao.deleteRegionsByCompanyId(company.getCompanyId());
			
			List<Long> companyIds = new ArrayList<>();
			companyIds.add(company.getCompanyId());
			organizationUnitSettingsDao.removeOganizationUnitSettings(companyIds, CommonConstants.COMPANY_SETTINGS_COLLECTION);
			// Delete all the details from tables which are related to current company.
			performPreCompanyDeletions(company.getCompanyId());
			// Deleting company from MySQL
			companyDao.delete(company);

		}
		catch (DatabaseException e) {
			LOG.error("Database exception caught in getAccountsForPurge(). Nested exception is ", e);
			throw e;
		}
	}
	

	// Method to update an existing company details.
	@Override
	@Transactional
	public void updateCompany(Company company) throws DatabaseException {
		LOG.info("Method to change company details updateCompany() started.");
		companyDao.merge(company);
		LOG.info("Method to change company details updateCompany() finished.");
	}

	private void performPreCompanyDeletions(long companyId) {
		Map<String, Object> queries = new HashMap<>();
		queries.put(CommonConstants.COMPANY_ID_COLUMN, companyId);
		List<LicenseDetail> licenseDetails = licenseDetailDao.findByColumn(LicenseDetail.class, "company.companyId", companyId);
		// Delete from PaymentRetry table
		List<String> conditions = new ArrayList<>();
		StringBuilder licenseIds = new StringBuilder("(");
		for(LicenseDetail license : licenseDetails){
			licenseIds.append(license.getLicenseId()).append(",");
		}
		int commaIndex = licenseIds.lastIndexOf(",");
		if(commaIndex != -1){
			licenseIds = new StringBuilder(licenseIds.substring(0, commaIndex));
			licenseIds.append(")");
		}
		conditions.add("licenseDetail.licenseId in " + licenseIds);
		retriedTransactionDao.deleteByCondition("RetriedTransaction", conditions);
		conditions.clear();
		
		conditions.add("company.companyId = " + companyId);
		
		disabledAccountDao.deleteByCondition("DisabledAccount", conditions);
		
		licenseDetailDao.deleteByCondition("LicenseDetail", conditions);
		
		userInviteDao.deleteByCondition("UserInvite", conditions);
		
		usercountModificationNotificationDao.deleteByCondition("UsercountModificationNotification", conditions);
		
		surveyCompanyMappingDao.deleteByCondition("SurveyCompanyMapping", conditions);
	}

	@Override
	@Transactional
    public List<VerticalCrmMapping> getCrmMapping(User user) {
		user = userDao.findById(User.class, user.getUserId());
		List<VerticalCrmMapping> mappings = user.getCompany().getVerticalsMaster().getVerticalCrmMappings();
		
		if(mappings == null || mappings.isEmpty()){
			VerticalCrmMapping defaultMapping = verticalCrmMappingDo.findById(VerticalCrmMapping.class, CommonConstants.DEFAULT_VERTICAL_CRM_ID);
			mappings.add(defaultMapping);
		}
		
		for(VerticalCrmMapping mapping : mappings){
			mapping.getCrmMaster().getCrmMasterId();
			mapping.getCrmMaster().getCrmName();
		}
	    return mappings;
    }

}
// JIRA: SS-27: By RM05: EOC