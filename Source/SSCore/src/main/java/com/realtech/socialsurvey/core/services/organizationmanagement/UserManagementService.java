package com.realtech.socialsurvey.core.services.organizationmanagement;

import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

// JIRA SS-34 BY RM02 BOC
/**
 * Interface with methods defined to manage user
 */
public interface UserManagementService {

	public ProfilesMaster getProfilesMasterById(int profileId) throws InvalidInputException;
	
	public User createBranchAdmin(User user, long branchId, long userId) throws InvalidInputException;

	public User createRegionAdmin(User user, long regionId, long userId) throws InvalidInputException;

}
// JIRA SS-34 BY RM02 BOC
