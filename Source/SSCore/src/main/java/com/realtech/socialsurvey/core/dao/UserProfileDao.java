package com.realtech.socialsurvey.core.dao;

import java.util.List;
import java.util.Map;
import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;

/*
 * Interface for UserProfileDao to perform various operations on UserProfile.
 */
public interface UserProfileDao extends GenericDao<UserProfile, Long> {

	public void deactivateAllUserProfilesForUser(User admin, User userToBeDeactivated);

	public void deactivateUserProfileForBranch(User admin, long branchId, User userToBeDeactivated);

	public void deactivateUserProfileForRegion(User admin, long regionId, User userToBeDeactivated);

	public List<Long> getBranchIdsForUser(User user);

	public List<Long> getBranchesForAdmin(User user, List<ProfilesMaster> profilesMasters);

	public void deleteUserProfilesByCompany(long companyId);

	public List<UserProfile> findUserProfilesInBatch(Map<String, Object> queries, int startIndex, int batchSize);
}
