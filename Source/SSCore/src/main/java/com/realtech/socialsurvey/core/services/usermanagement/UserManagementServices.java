package com.realtech.socialsurvey.core.services.usermanagement;

import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

// JIRA SS-34 BY RM02 BOC
/**
 * Interface with methods defined to manage user
 */
public interface UserManagementServices {

	public ProfilesMaster getProfilesMasterById(int profileId) throws InvalidInputException;

}
// JIRA SS-34 BY RM02 BOC
