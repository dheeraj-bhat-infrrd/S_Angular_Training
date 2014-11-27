package com.realtech.socialsurvey.core.services.authentication;

import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
//JIRA : SS-21 by RM-06 : BOC

/**
 * Contains the methods to be implemented for authentication
 */
public interface AuthenticationService {
	
	/**
	 * Method to validate user
	 * @param userId
	 * @param password
	 * @return
	 * @throws InvalidInputException
	 */
	public User validateUser(String userId, String password) throws InvalidInputException;

}

//JIRA : SS-21 by RM-06 : EOC