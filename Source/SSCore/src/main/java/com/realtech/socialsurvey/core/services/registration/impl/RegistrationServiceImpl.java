package com.realtech.socialsurvey.core.services.registration.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.realtech.socialsurvey.core.dao.UserInviteDao;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.OrganizationLevelSetting;
import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserInvite;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.generator.InvalidUrlException;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.registration.RegistrationService;
import com.realtech.socialsurvey.core.utils.EncryptionHelper;

@Component
public class RegistrationServiceImpl implements RegistrationService {

	private static final Logger LOG = LoggerFactory.getLogger(RegistrationServiceImpl.class);

	@Autowired
	private URLGenerator urlGenerator;

	@Value("${APPLICATION_BASE_URL}")
	private String applicationBaseUrl;

	@Autowired
	private EmailServices emailServices;

	@Autowired
	private EncryptionHelper encryptionHelper;

	@Resource
	@Qualifier("userInvite")
	private UserInviteDao userInviteDao;
	
	@Resource
	@Qualifier("userProfile")
	private UserProfileDao userProfileDao;

	@Autowired
	private GenericDao<Company, Integer> companyDao;

	@Autowired
	private GenericDao<ProfilesMaster, Integer> profilesMasterDao;

	@Autowired
	private GenericDao<User, Integer> userDao;

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
		LOG.info("Method validateRegistrationUrl() called ");
		Map<String, String> urlParameters = urlGenerator.decryptParameters(encryptedUrlParameter);
		validateCompanyRegistrationUrlParameters(encryptedUrlParameter);
		LOG.info("Method validateRegistrationUrl() finished ");
		return urlParameters;
	}

	// JIRA: SS-27: By RM05: BOC
	/*
	 * This method creates a new user post validation of URL.
	 */
	@Override
	@Transactional(rollbackFor = { NonFatalException.class, FatalException.class })
	public User addCorporateAdmin(String firstName, String lastName, String emailId, String username, String password) throws InvalidInputException,
			InvalidUrlException {
		Company company = companyDao.findById(Company.class, CommonConstants.DEFAULT_COMPANY_ID);
		String encryptedPassword = encryptionHelper.encryptSHA512(password);
		User user = createUser(company, username, encryptedPassword, emailId);
		userProfileDao.createUserProfile(user, company, emailId, CommonConstants.DEFAULT_AGENT_ID, CommonConstants.DEFAULT_BRANCH_ID,
				CommonConstants.DEFAULT_REGION_ID, CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID);
		return user;
	}

	// JIRA: SS-27: By RM05: EOC

	private void inviteUser(String url, String emailId, String firstName, String lastName) throws InvalidInputException, UndeliveredEmailException,
			NonFatalException {
		LOG.debug("Method inviteUser called with url : " + url + " emailId : " + emailId + " firstname : " + firstName + " lastName : " + lastName);

		String queryParam = extractUrlQueryParam(url);
		deactivateExistingInvitesWithSameParameters(queryParam);
		LOG.debug("Adding a new inviatation into the user_invite table");
		storeCompanyAdminInvitation(queryParam, emailId);
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
	private void storeCompanyAdminInvitation(String queryParam, String emailId) throws NonFatalException {
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

	// JIRA: SS-27: By RM05: BOC
	/*
	 * This method takes the URL query parameter sent for authentication to a new user. Validates
	 * the Registration URL and returns true if matches. Throws InvalidInputException, if URL does
	 * not match.
	 */
	private boolean validateCompanyRegistrationUrlParameters(String encryptedUrlParameter) throws InvalidInputException {
		LOG.debug("Method validateUrlParameters called.");
		List<UserInvite> userInvites = userInviteDao.findByColumn(encryptedUrlParameter);
		if (userInvites == null || userInvites.isEmpty()) {
			LOG.error("Exception caught while validating company registration URL parameters.");
			throw new InvalidInputException("URL parameter provided is inappropriate.");
		}
		LOG.debug("Method validateUrlParameters finished.");
		return true;
	}

	/*
	 * To add a new User into the USERS table.
	 */
	public User createUser(Company company, String username, String password, String emailId) {
		LOG.info("Method createUser called for username : " + username + " and email-id : " + emailId);
		User user = new User();
		user.setCompany(company);
		user.setLoginName(username);
		user.setLoginPassword(password);
		user.setEmailId(emailId);
		user.setSource(CommonConstants.DEFAULT_SOURCE_APPLICATION);
		user.setIsAtleastOneUserprofileComplete(CommonConstants.STATUS_INACTIVE);
		user.setStatus(CommonConstants.STATUS_ACTIVE);
		Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
		user.setCreatedOn(currentTimestamp);
		user.setModifiedOn(currentTimestamp);
		user.setCreatedBy(CommonConstants.ADMIN_USER_NAME);
		user.setModifiedBy(CommonConstants.ADMIN_USER_NAME);
		LOG.debug("Method createUser finished");
		user = userDao.save(user);
		userDao.flush();
		LOG.info("Method createUser finished for username : " + username);
		return user;
	}

	/*
	 * This method gets the list of all the UserInvites already existing with the same parameter and
	 * marks them as inactive.
	 */
	private void deactivateExistingInvitesWithSameParameters(String queryParam) {
		LOG.debug("Method deactivateExistingCustomersWithSameParameters started.");
		List<UserInvite> userinvites = userInviteDao.findByColumn(UserInvite.class, CommonConstants.USER_INVITE_INVITATION_PARAMETERS_COLUMN,
				queryParam);
		for (UserInvite userInvite : userinvites) {
			deactivateUserInvite(userInvite);
		}
		LOG.debug("Method deactivateExistingCustomersWithSameParameters finished.");
	}

	/*
	 * It marks a selected row in UserInvite as inactive.
	 */
	private void deactivateUserInvite(UserInvite userInvite) {
		userInvite.setStatus(CommonConstants.STATUS_INACTIVE);
		userInviteDao.update(userInvite);
	}
	// JIRA: SS-27: By RM05: EOC
}
