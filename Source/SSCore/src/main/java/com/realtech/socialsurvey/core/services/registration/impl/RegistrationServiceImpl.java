package com.realtech.socialsurvey.core.services.registration.impl;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.ParentDao;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.OrganizationLevelSetting;
import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserInvite;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.generator.InvalidUrlException;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.registration.RegistrationService;

@Component
public class RegistrationServiceImpl implements RegistrationService {

	private static final Logger LOG = LoggerFactory.getLogger(RegistrationServiceImpl.class);

	@Autowired
	private URLGenerator urlGenerator;

	@Value("${APPLICATION_BASE_URL}")
	private String applicationBaseUrl;

	@Autowired
	private EmailServices emailServices;

	@Resource
	@Qualifier("userInvite")
	private ParentDao<UserInvite, Integer> userInviteDao;

	@Autowired
	private GenericDao<Company, Integer> companyDao;

	@Autowired
	private GenericDao<ProfilesMaster, Integer> profilesMasterDao;

	@Autowired
	private GenericDao<User, Integer> userDao;

	@Autowired
	private GenericDao<UserProfile, Integer> userProfileDao;

	@Autowired
	private GenericDao<OrganizationLevelSetting, Integer> organizationLevelSettingDao;

	@Override
	@Transactional(rollbackFor = { NonFatalException.class, FatalException.class })
	public void inviteCorporateToRegister(String firstName, String lastName, String emailId) throws InvalidInputException, UndeliveredEmailException,
			NonFatalException {
		LOG.info("Inviting corporate to register. Details\t first name:" + firstName + "\t lastName: " + lastName + "\t email id: " + emailId);

		Map<String, String> urlParams = new HashMap<String, String>();
		urlParams.put("firstName", firstName);
		urlParams.put("lastName", lastName);
		urlParams.put("emailId", emailId);

		LOG.debug("Generating URL");
		String url = urlGenerator.generateUrl(urlParams, applicationBaseUrl + CommonConstants.REQUEST_MAPPING_SHOW_REGISTRATION);
		LOG.debug("Sending invitation for registration");
		inviteUser(url, emailId, firstName, lastName);

		LOG.info("Successfully sent invitation to :" + emailId + " for registration");
	}

	/*
	 * This method contains the process to be done after URL is hit by the User. It involves
	 * decrypting URL and validating parameters.
	 */
	@Override
	@Transactional(rollbackFor = { NonFatalException.class, FatalException.class })
	public Map<String, String> validateRegistrationUrl(String encryptedUrlParameter) throws InvalidInputException, InvalidUrlException {
		Map<String, String> urlParameters = urlGenerator.decryptParameters(encryptedUrlParameter);
		validateUrlParameters(encryptedUrlParameter);
		return urlParameters;
	}

	/*
	 * This method creates a new user post validation of URL.
	 */
	@Override
	@Transactional(rollbackFor = { NonFatalException.class, FatalException.class })
	public void addIndependentUser(String firstName, String lastName, String emailId, String username, String password) throws InvalidInputException,
			InvalidUrlException {
		Company company = companyDao.findById(Company.class, CommonConstants.DEFAULT_COMPANY_ID);
		createUserProfile(createUser(company, username, password, emailId), company, emailId);
	}

	private void inviteUser(String url, String emailId, String firstName, String lastName) throws InvalidInputException, UndeliveredEmailException,
			NonFatalException {
		LOG.debug("Method inviteUser called with url : " + url + " emailId : " + emailId + " firstname : " + firstName + " lastName : " + lastName);

		String queryParam = extractUrlQueryParam(url);

		LOG.debug("Adding a new inviatation into the user_invite table");
		storeInvitation(queryParam, emailId);
		LOG.debug("Calling email services to send registration invitation mail");
		emailServices.sendRegistrationInviteMail(url, emailId, firstName, lastName);
		LOG.debug("Method inviteUser finished successfully");

	}

	/**
	 * Method to extract the query parameter from encrypted url
	 * 
	 * @param url
	 * @return queryParam
	 * @throws InvalidInputException
	 */
	private String extractUrlQueryParam(String url) throws InvalidInputException {
		if (url == null || url.isEmpty()) {
			throw new InvalidInputException("Url is found null or empty while extracting the query param");
		}
		LOG.debug("Getting query param from the encrypted url " + url);
		String queryParam = url.substring(url.indexOf("q=") + 2, url.length());

		LOG.debug("Returning query param : " + queryParam);
		return queryParam;

	}

	/*
	 * This method stores the invitation related info into user_invite. It sets all the required
	 * values in table and puts status as 0.
	 */
	private void storeInvitation(String queryParam, String emailId) throws NonFatalException {
		LOG.debug("Method storeInvitation called with query param : " + queryParam + " and emailId : " + emailId);
		UserInvite userInvite = new UserInvite();

		Company company = companyDao.findById(Company.class, CommonConstants.DEFAULT_COMPANY_ID);
		ProfilesMaster profilesMaster = profilesMasterDao.findById(ProfilesMaster.class, CommonConstants.PROFILES_MASTER_NO_PROFILE_ID);
		userInvite.setCompany(company);
		userInvite.setProfilesMaster(profilesMaster);
		userInvite.setInvitationEmailId(emailId);
		userInvite.setInvitationParameters(queryParam);
		userInvite.setStatus(CommonConstants.STATUS_ACTIVE);
		userInvite.setModifiedBy(CommonConstants.GUEST_USER_NAME);
		userInvite.setCreatedBy(CommonConstants.GUEST_USER_NAME);
		userInvite = userInviteDao.save(userInvite);
		LOG.debug("Method storeInvitation finished");
	}

	private boolean validateUrlParameters(String encryptedUrlParameter) throws InvalidInputException {
		LOG.debug("Method validateUrlParameters called.");
		Map<String, String> queries = new HashMap<>();
		queries.put(CommonConstants.USER_INVITE_INVITATION_PARAMETERS_COLUMN, encryptedUrlParameter);
		List<UserInvite> userInvite = userInviteDao.findByColumn(encryptedUrlParameter);
		if (userInvite != null && !userInvite.isEmpty()){
			LOG.debug("Method validateUrlParameters finished.");
			return true;
		}
		else{
			InvalidInputException invalidInputException = new InvalidInputException("URL parameter provided is inappropriate."); 
			LOG.error("Exception caught in validateUrlParameters()", invalidInputException);
			throw invalidInputException;
		}
	}
	
	
	public User createUser(Company company, String username, String password, String emailId) {
		LOG.info("Method createUser called for username : " + username + " and email-id : " + emailId);
		User user = new User();
		user.setUserId(1);
		user.setCompany(company);
		user.setLoginName(username);
		user.setLoginPassword(password);
		user.setEmailId(emailId);
		user.setSource(CommonConstants.DEFAULT_SOURCE_APPLICATION);
		user.setIsAtleastOneUserprofileComplete(CommonConstants.STATUS_INACTIVE);
		user.setStatus(CommonConstants.STATUS_ACTIVE);
		Timestamp currentTimestamp = new Timestamp(Calendar.getInstance().getTimeInMillis());
		user.setCreatedOn(currentTimestamp);
		user.setModifiedOn(currentTimestamp);
		user.setCreatedBy(CommonConstants.ADMIN_USER_NAME);
		user.setModifiedBy(CommonConstants.ADMIN_USER_NAME);
		LOG.debug("Method createUser finished");
		user = userDao.save(user);
		userDao.flush();
		LOG.info("Method createUser finished for username : "+username);
		return user;
	}

	
	public void createUserProfile(User user, Company company, String emailId) {
		LOG.info("Method createUserProfile called for username : " + user.getLoginName());
		UserProfile userProfile = new UserProfile();
		userProfile.setAgentId(CommonConstants.DEFAULT_AGENT_ID);
		userProfile.setBranchId(CommonConstants.DEFAULT_BRANCH_ID);
		userProfile.setCompany(company);
		userProfile.setEmailId(emailId);
		userProfile.setIsProfileComplete(CommonConstants.STATUS_INACTIVE);
		userProfile.setProfilesMaster(profilesMasterDao.findById(ProfilesMaster.class, CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID));
		userProfile.setRegionId(CommonConstants.DEFAULT_REGION_ID);
		userProfile.setStatus(CommonConstants.STATUS_ACTIVE);
		userProfile.setUser(user);
		Timestamp currentTimestamp = new Timestamp(Calendar.getInstance().getTimeInMillis());
		userProfile.setCreatedOn(currentTimestamp);
		userProfile.setModifiedOn(currentTimestamp);
		userProfile.setCreatedBy(CommonConstants.ADMIN_USER_NAME);
		userProfile.setModifiedBy(CommonConstants.ADMIN_USER_NAME);
		LOG.debug("Method createUserProfile finished");
		userProfileDao.save(userProfile);
		LOG.info("Method createUserProfile() finished");
	}


	private void addOrganizationalDetails(Company company, Map<String, String> organizationalDetails) {
		LOG.debug("Method addOrganizationalDetails called for company id : " + company.getCompanyId());
		OrganizationLevelSetting organizationLevelSetting = new OrganizationLevelSetting();
		organizationLevelSetting.setAgentId(CommonConstants.DEFAULT_AGENT_ID);
		organizationLevelSetting.setBranchId(CommonConstants.DEFAULT_BRANCH_ID);
		organizationLevelSetting.setCompany(company);
		organizationLevelSetting.setRegionId(CommonConstants.DEFAULT_REGION_ID);
		organizationLevelSetting.setStatus(CommonConstants.STATUS_ACTIVE);
		for (Entry<String, String> organizationalDetail : organizationalDetails.entrySet()) {
			organizationLevelSetting.setSettingKey(organizationalDetail.getKey());
			organizationLevelSetting.setSettingValue(organizationalDetail.getValue());
			organizationLevelSettingDao.save(organizationLevelSetting);
			organizationLevelSettingDao.flush();
		}
		LOG.debug("Method addCompanyDetails finished");
	}
}
