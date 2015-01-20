package com.realtech.socialsurvey.core.services.registration.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
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
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.OrganizationLevelSetting;
import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserInvite;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.exception.DatabaseException;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.exception.UserAlreadyExistsException;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
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

	@Autowired
	private GenericDao<UserProfile, Long> userProfileDao;

	@Autowired
	private GenericDao<Company, Long> companyDao;

	@Autowired
	private GenericDao<ProfilesMaster, Integer> profilesMasterDao;

	@Autowired
	private GenericDao<User, Long> userDao;

	@Autowired
	private GenericDao<OrganizationLevelSetting, Long> organizationLevelSettingDao;

	@Autowired
	private UserManagementService userManagementService;

	@Override
	@Transactional(rollbackFor = { NonFatalException.class, FatalException.class })
	public void inviteCorporateToRegister(String firstName, String lastName, String emailId) throws InvalidInputException, UndeliveredEmailException,
			NonFatalException {
		LOG.info("Inviting corporate to register. Details\t first name:" + firstName + "\t lastName: " + lastName + "\t email id: " + emailId);

		Map<String, String> urlParams = new HashMap<String, String>();
		urlParams.put(CommonConstants.FIRST_NAME, firstName);
		urlParams.put(CommonConstants.LAST_NAME, lastName);
		urlParams.put(CommonConstants.EMAIL_ID, emailId);

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
	public Map<String, String> validateRegistrationUrl(String encryptedUrlParameter) throws InvalidInputException {
		LOG.info("Method validateRegistrationUrl() called ");

		Map<String, String> urlParameters = urlGenerator.decryptParameters(encryptedUrlParameter);
		validateCompanyRegistrationUrlParameters(encryptedUrlParameter);

		LOG.info("Method validateRegistrationUrl() finished ");
		return urlParameters;
	}

	// JIRA: SS-27: By RM05: BOC
	/**
	 * This method creates a new user, user profile post validation of URL and also invalidates the
	 * registration link used by the user to register
	 * 
	 * @throws UserAlreadyExistsException
	 * @throws UndeliveredEmailException
	 */
	@Override
	@Transactional(rollbackFor = { NonFatalException.class, FatalException.class })
	public User addCorporateAdminAndUpdateStage(String firstName, String lastName, String emailId, String password, boolean isDirectRegistration)
			throws InvalidInputException, UserAlreadyExistsException, UndeliveredEmailException {
		LOG.info("Method to add corporate admin called for emailId : " + emailId);
		if (userExists(emailId)) {
			throw new UserAlreadyExistsException("User with User ID : " + emailId + " already exists");
		}
		Company company = companyDao.findById(Company.class, CommonConstants.DEFAULT_COMPANY_ID);
		String encryptedPassword = encryptionHelper.encryptSHA512(password);
		int status = CommonConstants.STATUS_ACTIVE;

		/**
		 * If the registration is not through an invite, status of the user is "not verified" and a
		 * verification link is sent. For an invitation, email is already verified hence status is
		 * active
		 */
		if (isDirectRegistration) {
			status = CommonConstants.STATUS_NOT_VERIFIED;
		}

		LOG.debug("Creating new user with emailId : " + emailId + " and verification status : " + status);
		String displayName = getDisplayName(firstName, lastName);
		User user = createUser(company, encryptedPassword, emailId, displayName, status);
		user = userDao.save(user);

		LOG.debug("Creating user profile for :" + emailId + " with profile completion stage : " + CommonConstants.ADD_COMPANY_STAGE);
		UserProfile userProfile = createUserProfile(user, company, emailId, CommonConstants.DEFAULT_AGENT_ID, CommonConstants.DEFAULT_BRANCH_ID,
				CommonConstants.DEFAULT_REGION_ID, CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID, CommonConstants.ADD_COMPANY_STAGE,
				CommonConstants.STATUS_INACTIVE, String.valueOf(user.getUserId()), String.valueOf(user.getUserId()));
		// add the company admin profile with the user object
		List<UserProfile> userProfiles = new ArrayList<UserProfile>();
		userProfiles.add(userProfile);
		user.setUserProfiles(userProfiles);
		userProfileDao.save(userProfile);

		/**
		 * if it is direct registration, send verification link else invalidate the invitation link
		 */
		if (isDirectRegistration) {
			LOG.debug("Calling method for sending verification link for user : " + user.getUserId());
			sendVerificationLink(user);
		}
		else {
			LOG.debug("Invalidating registration link for emailId : " + emailId);
			invalidateRegistrationInvite(emailId);
		}

		LOG.info("Successfully executed method to add corporate admin for emailId : " + emailId);
		return user;
	}

	/**
	 * Method to generate and send verification link
	 * 
	 * @param user
	 * @throws InvalidInputException
	 * @throws UndeliveredEmailException
	 */
	private void sendVerificationLink(User user) throws InvalidInputException, UndeliveredEmailException {
		LOG.debug("Method sendVerificationLink of Registration service called");
		String verificationUrl = null;
		try {

			Map<String, String> params = new HashMap<String, String>();
			params.put(CommonConstants.EMAIL_ID, user.getEmailId());
			params.put(CommonConstants.USER_ID, String.valueOf(user.getUserId()));

			LOG.debug("Calling url generator to generate verification link");
			verificationUrl = urlGenerator.generateUrl(params, applicationBaseUrl + CommonConstants.REQUEST_MAPPING_MAIL_VERIFICATION);
		}
		catch (InvalidInputException e) {
			throw new InvalidInputException("Could not generate url for verification.Reason : " + e.getMessage(), e);
		}

		try {
			LOG.debug("Calling email services to send verification mail for user " + user.getEmailId());
			emailServices.sendVerificationMail(verificationUrl, user.getEmailId(), user.getDisplayName());
		}
		catch (InvalidInputException e) {
			throw new InvalidInputException("Could not send mail for verification.Reason : " + e.getMessage(), e);
		}
		catch (UndeliveredEmailException e) {
			throw new UndeliveredEmailException("Could not send mail for verification.Reason : " + e.getMessage(), e);
		}

		LOG.debug("Method sendVerificationLink of Registration service finished");
	}

	// JIRA: SS-27: By RM05: EOC
	/**
	 * Method to invite user for registration,includes storing invite in db, calling services to
	 * send mail
	 * 
	 * @param url
	 * @param emailId
	 * @param firstName
	 * @param lastName
	 * @throws UserAlreadyExistsException
	 * @throws InvalidInputException
	 * @throws UndeliveredEmailException
	 * @throws NonFatalException
	 */
	private void inviteUser(String url, String emailId, String firstName, String lastName) throws UserAlreadyExistsException, InvalidInputException,
			UndeliveredEmailException {
		LOG.debug("Method inviteUser called with url : " + url + " emailId : " + emailId + " firstname : " + firstName + " lastName : " + lastName);

		String queryParam = extractUrlQueryParam(url);
		if (doesUserWithEmailIdExists(emailId)) {
			throw new UserAlreadyExistsException("user with specified email id already exists");
		}

		LOG.debug("Calling method to store the registration invite");
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

	/**
	 * Method to store a registration invite, inserts a new invite if it doesn't exist otherwise
	 * updates it with new timestamp values
	 * 
	 * @param queryParam
	 * @param emailId
	 * @throws NonFatalException
	 */
	private void storeCompanyAdminInvitation(String queryParam, String emailId) {
		LOG.debug("Method storeInvitation called with query param : " + queryParam + " and emailId : " + emailId);
		UserInvite userInvite = null;
		Company company = companyDao.findById(Company.class, CommonConstants.DEFAULT_COMPANY_ID);
		ProfilesMaster profilesMaster = profilesMasterDao.findById(ProfilesMaster.class, CommonConstants.PROFILES_MASTER_NO_PROFILE_ID);

		LOG.debug("Checking if an invite already exists for queryParam :" + queryParam);
		userInvite = checkExistingInviteWithSameParams(queryParam);
		/**
		 * if invite doesn't exist create a new one and store itF
		 */
		if (userInvite == null) {
			userInvite = new UserInvite();
			userInvite.setCompany(company);
			userInvite.setProfilesMaster(profilesMaster);
			userInvite.setInvitationEmailId(emailId);
			userInvite.setInvitationParameters(queryParam);
			userInvite.setInvitationTime(new Timestamp(System.currentTimeMillis()));
			userInvite.setInvitationValidUntil(new Timestamp(CommonConstants.EPOCH_TIME_IN_MILLIS));
			userInvite.setStatus(CommonConstants.STATUS_ACTIVE);
			userInvite.setModifiedBy(CommonConstants.GUEST_USER_NAME);
			userInvite.setModifiedOn(new Timestamp(System.currentTimeMillis()));
			userInvite.setCreatedBy(CommonConstants.GUEST_USER_NAME);
			userInvite.setCreatedOn(new Timestamp(System.currentTimeMillis()));

			LOG.debug("Inserting user invite");
			userInvite = userInviteDao.save(userInvite);
		}
		/**
		 * else update the timestamp of existing invite
		 */
		else {
			userInvite.setStatus(CommonConstants.STATUS_ACTIVE);
			userInvite.setModifiedBy(CommonConstants.GUEST_USER_NAME);
			userInvite.setModifiedOn(new Timestamp(System.currentTimeMillis()));
			userInvite.setInvitationTime(new Timestamp(System.currentTimeMillis()));
			userInvite.setInvitationValidUntil(new Timestamp(CommonConstants.EPOCH_TIME_IN_MILLIS));

			LOG.debug("Updating user invite");
			userInviteDao.update(userInvite);
		}

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
		List<UserInvite> userInvites = userInviteDao.findByUrlParameter(encryptedUrlParameter);
		if (userInvites == null || userInvites.isEmpty()) {
			LOG.error("Exception caught while validating company registration URL parameters.");
			throw new InvalidInputException("URL parameter provided is inappropriate.");
		}
		LOG.debug("Method validateUrlParameters finished.");
		return true;
	}

	/**
	 * Method to create a new user
	 * 
	 * @param company
	 * @param password
	 * @param emailId
	 * @param displayName
	 * @return
	 */
	private User createUser(Company company, String password, String emailId, String displayName, int status) {
		LOG.debug("Method createUser called for email-id : " + emailId + " and status : " + status);
		User user = new User();
		user.setCompany(company);
		user.setLoginName(emailId);
		user.setLoginPassword(password);
		user.setEmailId(emailId);
		user.setDisplayName(displayName);
		user.setSource(CommonConstants.DEFAULT_SOURCE_APPLICATION);
		user.setIsAtleastOneUserprofileComplete(CommonConstants.STATUS_ACTIVE);
		user.setStatus(status);
		Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
		user.setCreatedOn(currentTimestamp);
		user.setModifiedOn(currentTimestamp);
		user.setCreatedBy(CommonConstants.ADMIN_USER_NAME);
		user.setModifiedBy(CommonConstants.ADMIN_USER_NAME);
		LOG.debug("Method createUser finished for email-id : " + emailId);
		return user;
	}

	/**
	 * Method to check if an invite already exists for the same query parameters, if yes returns the
	 * invite
	 * 
	 * @param queryParam
	 * @return
	 */
	private UserInvite checkExistingInviteWithSameParams(String queryParam) {
		LOG.debug("Method checkExistingInviteWithSameParams started for queryparam : " + queryParam);
		List<UserInvite> userinvites = userInviteDao.findByUrlParameter(queryParam);
		UserInvite userInvite = null;
		if (userinvites != null && !userinvites.isEmpty()) {
			userInvite = userinvites.get(0);
		}
		LOG.debug("Method checkExistingInviteWithSameParams finished.Returning : " + userInvite);
		return userInvite;
	}

	/**
	 * Method to invalidate the registration invite link based on emailIds
	 * 
	 * @param emailId
	 * @throws InvalidInputException
	 */
	private void invalidateRegistrationInvite(String emailId) throws InvalidInputException {
		if (emailId == null || emailId.isEmpty()) {
			throw new InvalidInputException("Email id is null for invalidating registration invite");
		}
		LOG.debug("Method to invalidate registration invite called for emailId : " + emailId);
		List<UserInvite> userInvites = userInviteDao.findByColumn(UserInvite.class, CommonConstants.INVITATION_EMAIL_ID_COLUMN, emailId);
		if (userInvites != null && !userInvites.isEmpty()) {
			for (UserInvite userInvite : userInvites) {
				userInvite.setStatus(CommonConstants.STATUS_INACTIVE);
				userInvite.setModifiedBy(CommonConstants.GUEST_USER_NAME);
				userInvite.setModifiedOn(new Timestamp(System.currentTimeMillis()));
				userInviteDao.update(userInvite);
			}
		}
		else {
			LOG.debug("Registration invite link to be invalidated is not present");
		}
		LOG.debug("Method to invalidate registration invite finished for emailId : " + emailId);

	}

	/**
	 * Method to check whether a user with selected user name exists
	 * 
	 * @param userName
	 * @return
	 */
	private boolean userExists(String userName) {
		LOG.debug("Method to check if user exists called for username : " + userName);
		try {
			List<User> users = userDao.findByColumn(User.class, CommonConstants.USER_LOGIN_NAME_COLUMN, userName);
			if (!users.isEmpty() && users != null)
				return true;
		}
		catch (DatabaseException databaseException) {
			LOG.error("Exception caught in method userExists() while trying to fetch list of users with same username.", databaseException);
			return false;
		}
		LOG.debug("Method to check if user exists finished for username : " + userName);
		return false;
	}

	/*
	 * Method to tell whether email id is already present in users table.
	 */
	private boolean doesUserWithEmailIdExists(String emailId) {
		LOG.debug("Method isEmailIdAlreadyPresent started.");
		try {
			Map<String, Object> columns = new HashMap<>();
			columns.put("emailId", emailId);
			columns.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
			List<User> users = userDao.findByKeyValue(User.class, columns);
			LOG.debug("Method isEmailIdAlreadyPresent finished.");
			if (users != null && !users.isEmpty())
				return true;
		}
		catch (DatabaseException databaseException) {
			LOG.error("Exception caughr while chnecking for email id in USERS.");
			return false;
		}
		return false;
	}

	// JIRA: SS-27: By RM05: EOC

	/**
	 * JIRA SS-35 BY RM02 Method to update the profile completion stage of user i.e the stage which
	 * user has completed while registration, stores the next step to be taken by user while
	 * registration process
	 * 
	 * @throws InvalidInputException
	 */
	@Override
	@Transactional
	public void updateProfileCompletionStage(User user, int profileMasterId, String profileCompletionStage) throws InvalidInputException {
		if (profileCompletionStage == null || profileCompletionStage.isEmpty()) {
			throw new InvalidInputException("Profile completion stage is not set for updation");
		}
		if (user == null) {
			throw new InvalidInputException("UserId is not null for updation of Profile completion stage");
		}
		if (profileMasterId <= 0) {
			throw new InvalidInputException("Profile master id is not set for updation of Profile completion stage");
		}
		LOG.info("Mehtod updateProfileCompletionStage called for profileCompletionStage : " + profileCompletionStage + " and profileMasterId : "
				+ profileMasterId + " and userId : " + user.getUserId());
		Map<String, Object> queries = new HashMap<String, Object>();
		queries.put(CommonConstants.USER_COLUMN, user);
		queries.put(CommonConstants.PROFILE_MASTER_COLUMN, userManagementService.getProfilesMasterById(profileMasterId));
		List<UserProfile> userProfiles = userProfileDao.findByKeyValue(UserProfile.class, queries);

		if (userProfiles != null && !userProfiles.isEmpty()) {
			for (UserProfile userProfile : userProfiles) {
				userProfile.setProfileCompletionStage(profileCompletionStage);
				userProfile.setModifiedOn(new Timestamp(System.currentTimeMillis()));
				userProfile.setModifiedBy(String.valueOf(user.getUserId()));
				userProfileDao.update(userProfile);
			}
		}
		else {
			LOG.warn("No profile found for updating profile completion stage");
		}
		LOG.info("Mehtod updateProfileCompletionStage finished for profileCompletionStage : " + profileCompletionStage);
	}

	/**
	 * Method to verify a user's account
	 * 
	 * @param encryptedUrlParams
	 * @throws InvalidInputException
	 */
	@Transactional(rollbackFor = { NonFatalException.class, FatalException.class })
	public void verifyAccount(String encryptedUrlParams) throws InvalidInputException {
		LOG.info("Method to verify account called for encryptedUrlParams");
		Map<String, String> urlParams = urlGenerator.decryptParameters(encryptedUrlParams);
		if (urlParams == null || urlParams.isEmpty()) {
			throw new InvalidInputException("Url params are invalid for account verification");
		}
		if (urlParams.containsKey(CommonConstants.USER_ID)) {
			long userId = Long.parseLong(urlParams.get(CommonConstants.USER_ID));

			LOG.debug("Calling user management service for updating user status to active");
			userManagementService.updateUserStatus(userId, CommonConstants.STATUS_ACTIVE);
		}
		else {
			throw new InvalidInputException("User id field not present in url params");
		}
		LOG.info("Successfully completed method to verify account");
	}

	/**
	 * Method to get display name from first and last names
	 * 
	 * @param firstName
	 * @param lastName
	 * @return
	 */
	private String getDisplayName(String firstName, String lastName) {
		LOG.debug("Getting display name for first name: " + firstName + " and last name : " + lastName);
		String displayName = firstName;
		/**
		 * if address line 2 is present, append it to address1 else the complete address is address1
		 */
		if (firstName != null && !firstName.isEmpty() && lastName != null && !lastName.isEmpty()) {
			displayName = firstName + " " + lastName;
		}
		LOG.debug("Returning display name" + displayName);
		return displayName;
	}

	private UserProfile createUserProfile(User user, Company company, String emailId, long agentId, long branchId, long regionId,
			int profileMasterId, String profileCompletionStage, int isProfileComplete, String createdBy, String modifiedBy) {
		LOG.debug("Method createUserProfile called for username : " + user.getLoginName());
		UserProfile userProfile = new UserProfile();
		userProfile.setAgentId(agentId);
		userProfile.setBranchId(branchId);
		userProfile.setCompany(company);
		userProfile.setEmailId(emailId);
		userProfile.setIsProfileComplete(isProfileComplete);
		userProfile.setProfilesMaster(profilesMasterDao.findById(ProfilesMaster.class, profileMasterId));
		userProfile.setProfileCompletionStage(profileCompletionStage);
		userProfile.setRegionId(regionId);
		userProfile.setStatus(CommonConstants.STATUS_ACTIVE);
		userProfile.setUser(user);
		Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
		userProfile.setCreatedOn(currentTimestamp);
		userProfile.setModifiedOn(currentTimestamp);
		userProfile.setCreatedBy(createdBy);
		userProfile.setModifiedBy(modifiedBy);
		LOG.debug("Method createUserProfile() finished");
		return userProfile;
	}
}
