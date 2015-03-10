package com.realtech.socialsurvey.core.services.organizationmanagement;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
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
import com.realtech.socialsurvey.core.services.search.exception.SolrException;

// JIRA SS-34 BY RM02 BOC
/**
 * Interface with methods defined to manage user
 */
public interface UserManagementService {

	public ProfilesMaster getProfilesMasterById(int profileId) throws InvalidInputException;

	public User assignBranchAdmin(User user, long branchId, long userId) throws InvalidInputException;

	public User assignRegionAdmin(User user, long regionId, long userId) throws InvalidInputException;

	public void updateUserStatus(long userId, int status) throws InvalidInputException, SolrException;

	// JIRA SS-42 BY RM02 BOC

	public void inviteUserToRegister(User admin, String firstName, String lastName, String emailId) throws InvalidInputException,
			UserAlreadyExistsException, UndeliveredEmailException;

	// Method to invite a new user to register.
	// Corporate Admin can send requests to new users to join the company.
	public User inviteNewUser(User admin, String firstName, String lastName, String emailId) throws InvalidInputException,
			UserAlreadyExistsException, UndeliveredEmailException;
	
	// Method to remove an existing user. Soft deletion is done.
	public void removeExistingUser(User admin, long userIdToBeDeactivated) throws InvalidInputException;

	// Method to remove profile of a branch admin.
	public void unassignBranchAdmin(User admin, long branchId, long userIdToRemove) throws InvalidInputException;

	// Method to remove profile of a region admin.
	public void unassignRegionAdmin(User admin, long regionId, long userIdToRemove) throws InvalidInputException;

	// Method to return users with provided login name
	public User getUserByLoginName(User admin, String loginName) throws InvalidInputException, NoRecordsFetchedException;

	// Method to return users whose email id matches with the provided email id.
	public List<User> getUsersBySimilarEmailId(User admin, String emailId) throws InvalidInputException;

	// Method to check if new users can be added to the current compeny or limit for the same is exhausted.
	public boolean isUserAdditionAllowed(User user) throws NoRecordsFetchedException;

	// Method to get User object for the given user id.
	public User getUserByUserId(long userId);

	// Method to get list of the branches assigned to the given user.
	public List<Branch> getBranchesAssignedToUser(User user) throws NoRecordsFetchedException;

	// Method to return list of users belonging to the same company as that of user passed.
	public List<User> getUsersForCompany(User user) throws InvalidInputException, NoRecordsFetchedException;

	// Method to assign a user to a particular branch.
	public void assignUserToBranch(User admin, long userId, long branchId) throws InvalidInputException, SolrException;
	
	/**
	 * Assign a user directly under the company.
	 * @param admin
	 * @param userId
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 * @throws SolrException
	 */
	public void assignUserToCompany(User admin, long userId) throws InvalidInputException, NoRecordsFetchedException, SolrException;
	
	/**
	 * Assign a user directly to a region 
	 * @param admin
	 * @param userId
	 * @param regionId
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 * @throws SolrException
	 */
	public void assignUserToRegion(User admin, long userId, long regionId) throws InvalidInputException, NoRecordsFetchedException, SolrException;

	// Method to unassign a user from branch.
	public void unassignUserFromBranch(User admin, long userId, long branchId) throws InvalidInputException;

	// Method to update status of the given user as active or inactive.
	public void updateUser(User admin, long userIdToUpdate, boolean isActive) throws InvalidInputException;

	/* Sends an email to user with the link to complete registration.
	 User has to provide password to set. Also, user can choose to change name.*/
	public void sendRegistrationCompletionLink(String emailId, String firstName, String lastName, long companyId) throws InvalidInputException, UndeliveredEmailException;

	// Method to set properties of a user based upon active profiles available for the user.
	public void setProfilesOfUser(User user);
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

	public void verifyAccount(String encryptedUrlParams) throws InvalidInputException, SolrException;
	
	//JIRA SS-42 by RM-06:BOC
	
	public List<UserProfile> getAllUserProfilesForUser(User user) throws InvalidInputException;

	public boolean userExists(String userName);
	
	//JIRA SS-42 by RM-06:EOC
	
	/**
	 * Gets the user settings according to the hierarchy
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
	
	/**
	 * Returns the LinkedIn request token for a particular URL
	 * @return
	 */
	public LinkedInRequestToken getLinkedInRequestToken();
	/**
	 * Adds the LinkedIn access tokens to the agent's settings in mongo
	 * @param user
	 * @param accessToken
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 */
	public void setLinkedInAccessTokenForUser(User user,String accessToken, String accessTokenSecret,Collection<AgentSettings> agentSettings) throws InvalidInputException, NoRecordsFetchedException;

	public void setFacebookAccessTokenForUser(User user, String accessToken, long accessTokenExpiresOn, OrganizationUnitSettings companySettings)
			throws InvalidInputException, NoRecordsFetchedException;
	
	/**
	 * Method to insert agent settings into mongo
	 * 
	 * @param branch
	 * @throws InvalidInputException
	 */
	public void insertAgentSettings(User user) throws InvalidInputException;
}
// JIRA SS-34 BY RM02 BOC
