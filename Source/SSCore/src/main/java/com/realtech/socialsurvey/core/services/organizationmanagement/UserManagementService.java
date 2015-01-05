package com.realtech.socialsurvey.core.services.organizationmanagement;

import java.util.List;
import java.util.Map;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.entities.UserSettings;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.exception.UserAlreadyExistsException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;

// JIRA SS-34 BY RM02 BOC
/**
 * Interface with methods defined to manage user
 */
public interface UserManagementService {

	public ProfilesMaster getProfilesMasterById(int profileId) throws InvalidInputException;

	public User assignBranchAdmin(User user, long branchId, long userId) throws InvalidInputException;

	public User assignRegionAdmin(User user, long regionId, long userId) throws InvalidInputException;

	public void updateUserStatus(long userId, int status) throws InvalidInputException;

	// JIRA SS-42 BY RM02 BOC

	public void inviteUserToRegister(User admin, String firstName, String lastName, String emailId) throws InvalidInputException,
			UserAlreadyExistsException, UndeliveredEmailException;

	public User inviteNewUser(User admin, String firstName, String lastName, String emailId) throws InvalidInputException,
			UserAlreadyExistsException, UndeliveredEmailException;

	public void removeExistingUser(User admin, long userIdToBeDeactivated) throws InvalidInputException;

	public void unassignBranchAdmin(User admin, long branchId, long userIdToRemove) throws InvalidInputException;

	public void unassignRegionAdmin(User admin, long regionId, long userIdToRemove) throws InvalidInputException;

	public User getUserByEmailId(User admin, String emailId) throws InvalidInputException, NoRecordsFetchedException;

	public List<User> getUsersBySimilarEmailId(User admin, String emailId) throws InvalidInputException;

	public boolean isUserAdditionAllowed(User user) throws NoRecordsFetchedException;

	public User getUserByUserId(long userId);

	public List<Branch> getBranchesAssignedToUser(User user) throws NoRecordsFetchedException;

	public List<User> getUsersForCompany(User user) throws InvalidInputException, NoRecordsFetchedException;

	public void assignUserToBranch(User admin, long userId, long branchId) throws InvalidInputException;

	public void unassignUserFromBranch(User admin, long userId, long branchId) throws InvalidInputException;

	public void updateUser(User admin, long userIdToUpdate, boolean isActive) throws InvalidInputException;

	public List<Branch> getBranchesForUser(User user) throws InvalidInputException, NoRecordsFetchedException;

	// JIRA SS-42 BY RM05 EOC

	/**
	 * Sends invitation to corporate to register
	 * 
	 * @throws NonFatalException
	 */
	public void inviteCorporateToRegister(String firstName, String lastName, String emailId) throws InvalidInputException, UndeliveredEmailException,
			NonFatalException;

	public Map<String, String> validateRegistrationUrl(String encryptedUrlParameter) throws InvalidInputException;

	public User addCorporateAdminAndUpdateStage(String firstName, String lastName, String emailId, String password, boolean isDirectRegistration)
			throws InvalidInputException, UserAlreadyExistsException, UndeliveredEmailException;

	public void updateProfileCompletionStage(User user, int profilesMasterId, String profileCompletionStage) throws InvalidInputException;

	public void verifyAccount(String encryptedUrlParams) throws InvalidInputException;
	
	//JIRA SS-42 by RM-06:BOC
	
	public List<UserProfile> getAllUserProfilesForUser(User user) throws InvalidInputException;
	
	public UserProfile getHighestUserProfile(List<UserProfile> userProfiles) throws InvalidInputException;
	
	public UserProfile getHighestUserProfileForUser(User user) throws NoRecordsFetchedException, InvalidInputException;
	
	//JIRA SS-42 by RM-06:EOC
	
	/**
	 * Gets the user settings according to the heirarchy
	 * @param user
	 * @param accountType
	 * @return
	 * @throws InvalidInputException
	 */
	public UserSettings getCanonicalUserSettings(User user, AccountType accountType) throws InvalidInputException;
	
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
