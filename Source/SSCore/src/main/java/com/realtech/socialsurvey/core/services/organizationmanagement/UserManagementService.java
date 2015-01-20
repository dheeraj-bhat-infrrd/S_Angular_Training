package com.realtech.socialsurvey.core.services.organizationmanagement;

import java.util.List;
import java.util.Map;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.entities.UserSettings;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;

// JIRA SS-34 BY RM02 BOC
/**
 * Interface with methods defined to manage user
 */
public interface UserManagementService {

	public ProfilesMaster getProfilesMasterById(int profileId) throws InvalidInputException;

	public User createBranchAdmin(User user, long branchId, long userId) throws InvalidInputException;

	public User createRegionAdmin(User user, long regionId, long userId) throws InvalidInputException;

	public void updateUserStatus(long userId, int status) throws InvalidInputException;
	
	/**
	 * Gets the user settings according to the heirarchy
	 * @param user
	 * @param accountType
	 * @return
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException 
	 */
	public UserSettings getCanonicalUserSettings(User user, AccountType accountType) throws InvalidInputException, NoRecordsFetchedException;
	
	/**
	 * Gets user settings
	 * @param agentId
	 * @return agentSettings
	 * @throws InvalidInputException
	 */
	public AgentSettings getUserSettings(long agentId) throws InvalidInputException;
	
	/**
	 * Get all the agent settings linked to the user profile
	 * @param userProfiles
	 * @return
	 * @throws InvalidInputException
	 */
	public Map<Long, AgentSettings> getAgentSettingsForUserProfiles(List<UserProfile> userProfiles) throws InvalidInputException;
}
// JIRA SS-34 BY RM02 BOC
