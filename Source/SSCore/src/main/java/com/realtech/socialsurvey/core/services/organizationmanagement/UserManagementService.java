package com.realtech.socialsurvey.core.services.organizationmanagement;

import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

// JIRA SS-34 BY RM02 BOC
/**
 * Interface with methods defined to manage user
 */
public interface UserManagementService {

	public ProfilesMaster getProfilesMasterById(int profileId)
			throws InvalidInputException;

	public User createBranchAdmin(User user, long branchId, long userId)
			throws InvalidInputException;

	public User createRegionAdmin(User user, long regionId, long userId)
			throws InvalidInputException;

	public void updateUserStatus(long userId, int status)
			throws InvalidInputException;

	// JIRA SS-42 BY RM02 BOC

	public void deactivateExistingUser(User admin, long userIdToBeDeactivated)
			throws InvalidInputException;

	public void removeBranchAdmin(User admin, long branchId, long userIdToRemove)
			throws InvalidInputException;

	public void removeRegionAdmin(User admin, long regionId, long userIdToRemove)
			throws InvalidInputException;

	// JIRA SS-42 BY RM05 EOC
}
// JIRA SS-34 BY RM02 BOC
