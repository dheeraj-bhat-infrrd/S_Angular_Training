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
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.authentication.AuthenticationService;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.EncryptionHelper;

/**
 * Contains the implementation for AuthenticationService
 */

@Component
public class AuthenticationServiceImpl implements AuthenticationService {

	private static final Logger LOG = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

	private static final String USER_NAME = "loginName";
	private static final String USER = "user";
	private static final String EMAIL_ID = "emailId";
	private static final String NAME = "name";

	@Autowired
	private URLGenerator urlGenerator;

	@Value("${APPLICATION_BASE_URL}")
	private String applicationBaseUrl;

	@Autowired
	private EmailServices emailServices;

	@Autowired
	private GenericDao<User, Integer> userDao;

	@Autowired
	private GenericDao<UserProfile, Integer> userProfileDao;

	@Autowired
	private EncryptionHelper encryptionHelper;

	@Override
	public void validateUser(User user, String password) throws InvalidInputException {

		LOG.info("Authenticating user, UserId + " + user.getLoginName());

		// get the encrypted password using encryptSHA512 method
		String encryptedPassword = encryptionHelper.encryptSHA512(password);

		// Check if password matches
		if (!encryptedPassword.equals(user.getLoginPassword())) {
			throw new InvalidInputException("Passwords do not match", DisplayMessageConstants.INVALID_PASSWORD);
		}

	}

	@Override
	@Transactional
	public User getUserObjWithLoginName(String userId) throws InvalidInputException {
		LOG.info("Fetching user object with userId : " + userId);
		List<User> users = userDao.findByColumn(User.class, USER_NAME, userId);
		// Check if user list returned is null or empty
		if (users == null || users.isEmpty()) {
			LOG.error("No Record found for the UserID : " + userId);
			throw new InvalidInputException("No Record found for the UserID : " + userId);
		}
		return users.get(0);
	}

	@Override
	@Transactional
	public List<UserProfile> getUserProfileForUser(User user) throws InvalidInputException {
		List<UserProfile> userProfiles = userProfileDao.findByColumn(UserProfile.class, USER, user);
		return userProfiles;
	}

	@Override
	@Transactional
	public User verifyRegisteredUser(String emailId) throws InvalidInputException {
		LOG.info("Verify whether the User is registered with the emailId");
		List<User> users = userDao.findByColumn(User.class, EMAIL_ID, emailId);
		if (users.get(0) == null) {
			LOG.error("No User object found with the passed emailId : " + emailId);
			throw new InvalidInputException("Email ID not registered with us");
		}
		return users.get(0);
	}

	@Override
	public void sendResetPasswordLink(String emailId, String name) throws InvalidInputException, UndeliveredEmailException {

		LOG.info("Send a reset password link to the user");
		Map<String, String> urlParams = new HashMap<String, String>();
		urlParams.put(EMAIL_ID, emailId);
		urlParams.put(NAME, name);

		LOG.info("Generating URL");
		String url = urlGenerator.generateUrl(urlParams, applicationBaseUrl + CommonConstants.RESET_PASSWORD);

		// Send reset password link to the user email ID
		emailServices.sendResetPasswordLink(url, emailId, name);
	}

	@Override
	@Transactional
	public User getUserObjWithEmailId(String emailId) throws InvalidInputException {
		LOG.info("Fetching user object with emailId : " + emailId);
		List<User> users = userDao.findByColumn(User.class, EMAIL_ID, emailId);
		// Check if user list returned is null or empty
		if (users == null || users.isEmpty()) {
			LOG.error("No Record found for the UserID : " + emailId);
			throw new InvalidInputException("No Record found for the UserID : " + emailId);
		}
		return users.get(0);
	}
	
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
}

// JIRA : SS-21 by RM-06 : EOC
