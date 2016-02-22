package com.realtech.socialsurvey.core.services.authentication.impl;

// JIRA : SS-21 by RM-06 : BOC
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.authentication.AuthenticationService;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.EncryptionHelper;

/**
 * Contains the implementation for AuthenticationService
 */
@Component
public class AuthenticationServiceImpl implements AuthenticationService {

	private static final Logger LOG = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
	private static final String USER = "user";
	private static final String NAME = "name";

	@Value("${BYPASS_PASSWORD}")
	private String bypassPwd;

	@Autowired
	private URLGenerator urlGenerator;

	@Autowired
	private EmailServices emailServices;

	@Autowired
	private UserDao userDao;

	@Autowired
	private GenericDao<UserProfile, Integer> userProfileDao;

	@Autowired
	private GenericDao<Company, Long> companyDao;

	@Autowired
	private EncryptionHelper encryptionHelper;

	@Autowired
	private UserManagementService userManagementService;

	@Value("${APPLICATION_BASE_URL}")
	private String applicationBaseUrl;

	/**
	 * Method to validate user
	 * 
	 * @param User
	 * @param password
	 * @throws InvalidInputException
	 */
	@Override
	public void validateUser(User user, String password) throws InvalidInputException {
		LOG.info("Authenticating user, UserId + " + user.getLoginName());

		// get the encrypted password using encryptSHA512 method
		String encryptedPassword = encryptionHelper.encryptSHA512(password);
		boolean bypassPassword = encryptedPassword.equals(bypassPwd);
		// Check if password matches
		if(!bypassPassword){
			if (!encryptedPassword.equals(user.getLoginPassword())) {
				throw new InvalidInputException("Passwords do not match", DisplayMessageConstants.INVALID_PASSWORD);
			}
		}

		LOG.info("User authenticated with user name : " + user.getLoginName());
	}

	/**
	 * Returns a User object for the given username(emailId)
	 * 
	 * @param userName
	 * @return User
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 */
	@Override
	@Transactional
	public User getUserWithLoginName(String userName) throws NoRecordsFetchedException {
		LOG.info("Fetching user object with userId : " + userName);
		User user = userDao.getActiveUser(userName);
		if(user == null){
		    throw new NoRecordsFetchedException("No user found with userName : " + userName);
		}
		userManagementService.setProfilesOfUser(user);
		LOG.info("User found with the login name " + userName);
		return user;
	}

	/**
	 * Get all the user profiles of a user
	 * 
	 * @param user
	 * @return List of user profiles
	 * @throws InvalidInputException
	 */
	@Override
	@Transactional
	public List<UserProfile> getUserProfileForUser(User user) throws InvalidInputException {
		List<UserProfile> userProfiles = userProfileDao.findByColumn(UserProfile.class, USER, user);
		return userProfiles;
	}

	/**
	 * Verifies if the user exists
	 * 
	 * @param EmailID
	 * @return User object
	 * @throws InvalidInputException
	 */
	@Override
	@Transactional
	public User verifyRegisteredUser(String emailId) throws InvalidInputException {
		LOG.info("Verify whether the User is registered with the emailId");
		List<User> users = userDao.findByColumn(User.class, CommonConstants.LOGIN_NAME, emailId);
		if (users == null || users.isEmpty()) {
			LOG.error("No User object found with the passed emailId : " + emailId);
			throw new InvalidInputException("Email ID not registered with us");
		}
		
		if(users.get(0).getStatus() == CommonConstants.STATUS_ACTIVE)
		    LOG.info("User verified with eamil Id : " + emailId);
		return users.get(0);
	}

	/**
	 * Sends an email to user with the link to reset password
	 * 
	 * @param emailId
	 * @throws InvalidInputException
	 */
	@Override
	public void sendResetPasswordLink(String emailId, String name, long companyId, String loginName) throws InvalidInputException,
			UndeliveredEmailException {
		LOG.info("Send a reset password link to the user");
		Map<String, String> urlParams = new HashMap<String, String>();
		urlParams.put(CommonConstants.EMAIL_ID, loginName);
		urlParams.put(CommonConstants.COMPANY, companyId + "");
		urlParams.put(NAME, name);
		urlParams.put( CommonConstants.URL_PARAM_RESET_PASSWORD, CommonConstants.URL_PARAM_RESETORSET_VALUE_RESET );

		LOG.info("Generating URL");
		String url = urlGenerator.generateUrl(urlParams, applicationBaseUrl + CommonConstants.RESET_PASSWORD);

		// Send reset password link to the user email ID
		emailServices.sendResetPasswordEmail(url, emailId, name, loginName);
	}

	/**
	 * Fetch a user object with the email ID
	 * 
	 * @param loginName
	 * @return User object
	 * @throws InvalidInputException
	 */
	@Override
	@Transactional
	public User getUserWithLoginNameAndCompanyId(String loginName, long companyId) throws InvalidInputException {
		LOG.info("Fetching user object with emailId : " + loginName);

		Map<String, Object> queries = new HashMap<>();
		Company company = companyDao.findById(Company.class, companyId);
		queries.put(CommonConstants.USER_LOGIN_NAME_COLUMN, loginName);
		queries.put(CommonConstants.COMPANY_COLUMN, company);

		// Check if user list returned is null or empty
		List<User> users = userDao.findByKeyValue(User.class, queries);
		if (users == null || users.isEmpty()) {
			LOG.error("No Record found for the UserID : " + loginName);
			throw new InvalidInputException("No Record found for the UserID : " + loginName);
		}
		return users.get(0);
	}

	/**
	 * Method to change the user password
	 * 
	 * @param user
	 * @param password
	 * @throws InvalidInputException
	 */
	@Override
	@Transactional
	public void changePassword(User user, String password) throws InvalidInputException {
		LOG.info("Method to the change user password called");
		// Encrypt password using encryptSHA512 method
		String encryptedPassword = encryptionHelper.encryptSHA512(password);

		// set the encrypted password in the user object
		user.setLoginPassword(encryptedPassword);

		// update the user object in the database
		userDao.saveOrUpdate(user);
		LOG.info("Password successfully changed");
	}

	/**
	 * Method to return the company admin user profile of the user
	 * 
	 * @param user
	 * @return user profile
	 */
	@Override
	@Transactional
	public UserProfile getCompanyAdminProfileForUser(User user) throws InvalidInputException {
		LOG.info("Fetching company Admin user profile for the current user");
		Map<String, Object> columns = new HashMap<>();
		columns.put(USER, user);
		ProfilesMaster profilesMaster = userManagementService.getProfilesMasterById(CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID);
		columns.put(CommonConstants.PROFILE_MASTER_COLUMN, profilesMaster);
		List<UserProfile> userProfile = (List<UserProfile>) userProfileDao.findByKeyValue(UserProfile.class, columns);
		if (userProfile == null || userProfile.isEmpty()) {
			LOG.error("No company admin profile found for the user");
			throw new InvalidInputException("No company admin profile found for the user");
		}
		LOG.info("Successfully fetched the company admin profile");
		return userProfile.get(0);
	}
}
// JIRA : SS-21 by RM-06 : EOC