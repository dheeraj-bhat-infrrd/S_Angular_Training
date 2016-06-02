package com.realtech.socialsurvey.core.services.authentication;

import java.util.List;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;

// JIRA : SS-21 by RM-06 : BOC
/**
 * Contains the methods to be implemented for authentication
 */
public interface AuthenticationService {

	public void validateUser(User user, String password) throws InvalidInputException;

	public User getUserWithLoginName(String userId) throws NoRecordsFetchedException;

	public List<UserProfile> getUserProfileForUser(User user) throws InvalidInputException;

	public User verifyRegisteredUser(String emailId) throws InvalidInputException;

	public void sendResetPasswordLink(String emailId, String name, long companyId, String loginName) throws InvalidInputException,
			UndeliveredEmailException;

	public void changePassword(User user, String password) throws InvalidInputException;

	public User getUserWithLoginNameAndCompanyId(String loginName, long companyId) throws InvalidInputException;

	public UserProfile getCompanyAdminProfileForUser(User user) throws InvalidInputException;
	
	public User getActiveOrIncompleteUserWithLoginName(String userId) throws NoRecordsFetchedException;

}
// JIRA : SS-21 by RM-06 : EOC