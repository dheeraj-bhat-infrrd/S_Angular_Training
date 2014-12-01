package com.realtech.socialsurvey.core.services.authentication;

import java.util.List;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;

// JIRA : SS-21 by RM-06 : BOC

/**
 * Contains the methods to be implemented for authentication
 */
public interface AuthenticationService {

	/**
	 * Method to validate user
	 * 
	 * @param User
	 * @param password
	 * @throws InvalidInputException
	 */
	public void validateUser(User user, String password) throws InvalidInputException;

	/**
	 * Returns a User object for the given userId
	 * 
	 * @param userId
	 * @return User
	 * @throws InvalidInputException
	 */
	public User getUserObjWithLoginName(String userId) throws InvalidInputException;

	/**
	 * Get all the user profiles of a user
	 * 
	 * @param user
	 * @return List of user profiles
	 * @throws InvalidInputException
	 */
	public List<UserProfile> getUserProfileForUser(User user) throws InvalidInputException;

	/**
	 * Verifies if the user exists
	 * 
	 * @param EmailID
	 * @return User object
	 * @throws InvalidInputException
	 */
	public User verifyRegisteredUser(String emailId) throws InvalidInputException;

	/**
	 * Sends an email to user with the link to reset password
	 * 
	 * @param emailId
	 * @throws InvalidInputException
	 */
	public void sendResetPasswordLink(String emailId, String name) throws InvalidInputException, UndeliveredEmailException;

	/**
	 * Change the user password
	 * 
	 * @param user
	 * @param password
	 * @throws InvalidInputException
	 */
	public void changePassword(User user, String password) throws InvalidInputException;
	
	/**
	 * Fetch a user object for the email ID
	 * @param emailId
	 * @return User object
	 * @throws InvalidInputException
	 */
	public User getUserObjWithEmailId(String emailId) throws InvalidInputException;

}

// JIRA : SS-21 by RM-06 : EOC