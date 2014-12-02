package com.realtech.socialsurvey.core.services.authentication.impl;

// JIRA : SS-21 by RM-06 : BOC

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.authentication.AuthenticationService;
import com.realtech.socialsurvey.core.utils.EncryptionHelper;

/**
 * Contains the implementation for AuthenticationService
 */

@Component
public class AuthenticationServiceImpl implements AuthenticationService {

	private static final Logger LOG = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
	
	private static final String USER_NAME = "LOGIN_NAME"; 

	@Autowired
	private GenericDao<User, Integer> userDao;

	@Autowired
	private EncryptionHelper encryptionHelper;

	@Override
	@Transactional
	public User validateUser(String userId, String password) throws InvalidInputException {

		List<User> users = userDao.findByColumn(User.class, USER_NAME, userId);

		// Check if user list returned is null or empty
		if (users == null || users.isEmpty()) {
			LOG.error("No Record found for the UserID : " + userId);
			throw new InvalidInputException("No Record found for the UserID : " + userId);
		}

		// get the encrypted password using encryptSHA512 method
		String encryptedPassword = encryptionHelper.encryptSHA512(password);

		// Substring the password length to 30 as helper return 512 bytes long encrypted value
		encryptedPassword = encryptedPassword.substring(0, 30);

		//Get the first entry from list as userId is unique
		User userObj = users.get(0);

		//Check if password matches
		if (!encryptedPassword.equals(userObj.getLoginPassword())) {
			LOG.error("User authenticated with user Id : " + userId);
			throw new InvalidInputException("User authenticated with user Id : " + userId);
		}

		return userObj;
	}

}

// JIRA : SS-21 by RM-06 : EOC