package com.realtech.socialsurvey.core.services.organizationmanagement;

import java.util.List;
import java.util.Map;
import com.realtech.socialsurvey.core.entities.AbridgedUserProfile;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.ProListUser;
import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserFromSearch;
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

	public void updateUserStatus(long userId, int status) throws InvalidInputException, SolrException;

	// JIRA SS-42 BY RM02 BOC

	/**
	 * Method to add a new user into a company. Admin sends the invite to user for registering.
	 * 
	 * @param admin
	 * @param firstName
	 * @param lastName
	 * @param emailId
	 * @throws InvalidInputException
	 * @throws UserAlreadyExistsException
	 * @throws UndeliveredEmailException
	 */
	public User inviteUserToRegister(User admin, String firstName, String lastName, String emailId) throws InvalidInputException,
			UserAlreadyExistsException, UndeliveredEmailException;

	/**
	 * Method to invite a new user to register
	 * 
	 * @param admin
	 * @param firstName
	 * @param lastName
	 * @param emailId
	 * @return
	 * @throws InvalidInputException
	 * @throws UserAlreadyExistsException
	 * @throws UndeliveredEmailException
	 */
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

	// Method to return user with provided email and company
	public User getUserByEmailAndCompany(long companyId, String emailId) throws InvalidInputException, NoRecordsFetchedException;

	// Method to return user with provided email
	public User getUserByEmail(String emailId) throws InvalidInputException, NoRecordsFetchedException;

	// Method to return users whose email id matches with the provided email id.
	public List<User> getUsersBySimilarEmailId(User admin, String emailId) throws InvalidInputException;

	// Method to check if new users can be added to the current compeny or limit for the same is
	// exhausted.
	public boolean isUserAdditionAllowed(User user) throws NoRecordsFetchedException;

	/**
	 * Method to get user object for the given user id, fetches user along with profile name and
	 * profile url
	 * 
	 * @param userId
	 * @return
	 * @throws InvalidInputException
	 */
	public User getUserByUserId(long userId) throws InvalidInputException;

	public List<ProListUser> getMultipleUsersByUserId(List<Long> userIds) throws InvalidInputException;

	// Method to get list of the branches assigned to the given user.
	public List<Branch> getBranchesAssignedToUser(User user) throws NoRecordsFetchedException;

	// Method to return list of users belonging to the same company as that of user passed.
	public List<User> getUsersForCompany(User user) throws InvalidInputException, NoRecordsFetchedException;

	// Method to assign a user to a particular branch.
	public void assignUserToBranch(User admin, long userId, long branchId) throws InvalidInputException, SolrException;

	/**
	 * Assign a user directly under the company.
	 * 
	 * @param admin
	 * @param userId
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 * @throws SolrException
	 */
	public void assignUserToCompany(User admin, long userId) throws InvalidInputException, NoRecordsFetchedException, SolrException;

	/**
	 * Assign a user directly to a region
	 * 
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

	// Method to update status of the given user profile as active or inactive.
	public void updateUserProfile(User admin, long profileIdToUpdate, int status) throws InvalidInputException;

	/*
	 * Sends an email to user with the link to complete registration. User has to provide password
	 * to set. Also, user can choose to change name.
	 */
	public void sendRegistrationCompletionLink(String emailId, String firstName, String lastName, long companyId, String profileName, String loginName)
			throws InvalidInputException, UndeliveredEmailException;

	// Method to set properties of a user based upon active profiles available for the user.
	public void setProfilesOfUser(User user);

	// JIRA SS-42 BY RM05 EOC

	/**
	 * Sends invitation to corporate to register
	 * 
	 * @throws NonFatalException
	 */
	public void inviteCorporateToRegister(String firstName, String lastName, String emailId, boolean isReinvitation) throws InvalidInputException,
			UndeliveredEmailException, NonFatalException;

	public Map<String, String> validateRegistrationUrl(String encryptedUrlParameter) throws InvalidInputException;

	public User addCorporateAdminAndUpdateStage(String firstName, String lastName, String emailId, String password, boolean isDirectRegistration)
			throws InvalidInputException, UserAlreadyExistsException, UndeliveredEmailException;

	public void updateProfileCompletionStage(User user, int profilesMasterId, String profileCompletionStage) throws InvalidInputException;

	public void verifyAccount(String encryptedUrlParams) throws InvalidInputException, SolrException;

	// JIRA SS-42 by RM-06:BOC

	public List<UserProfile> getAllUserProfilesForUser(User user) throws InvalidInputException;

	public boolean userExists(String userName);

	// JIRA SS-42 by RM-06:EOC

	/**
	 * Gets the user settings according to the hierarchy
	 * 
	 * @param user
	 * @param accountType
	 * @return
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 */
	public UserSettings getCanonicalUserSettings(User user, AccountType accountType) throws InvalidInputException, NoRecordsFetchedException;

	/**
	 * Gets user settings
	 * 
	 * @param agentId
	 * @return agentSettings
	 * @throws InvalidInputException
	 */
	public AgentSettings getUserSettings(long agentId) throws InvalidInputException;

	/**
	 * Get all the agent settings linked to the user profile
	 * 
	 * @param userProfiles
	 * @return
	 * @throws InvalidInputException
	 */
	public AgentSettings getAgentSettingsForUserProfiles(long userId) throws InvalidInputException;

	/**
	 * Method to insert basic settings for a user
	 * 
	 * @param user
	 * @throws InvalidInputException
	 */
	public void insertAgentSettings(User user) throws InvalidInputException;

	/**
	 * Method to create a user profile based on the details provided
	 * 
	 * @param user
	 * @param company
	 * @param emailId
	 * @param agentId
	 * @param branchId
	 * @param regionId
	 * @param profileMasterId
	 * @param profileCompletionStage
	 * @param isProfileComplete
	 * @param createdBy
	 * @param modifiedBy
	 * @return
	 */
	public UserProfile createUserProfile(User user, Company company, String emailId, long agentId, long branchId, long regionId, int profileMasterId,
			String profileCompletionStage, int isProfileComplete, String createdBy, String modifiedBy);

	/**
	 * Method to check which all users can perform edit and set the boolean as true or false in user
	 * objects
	 * 
	 * @param admin
	 * @param adminFromSearch
	 * @param users
	 * @return
	 * @throws InvalidInputException
	 */
	public List<UserFromSearch> checkUserCanEdit(User admin, UserFromSearch adminFromSearch, List<UserFromSearch> users) throws InvalidInputException;

	/**
	 * Method to update user details on completing registration
	 * 
	 * @param existingUser
	 * @param emailId
	 * @param companyId
	 * @param firstName
	 * @param lastName
	 * @param password
	 * @return
	 * @throws InvalidInputException
	 * @throws SolrException
	 */
	public User updateUserOnCompleteRegistration(User existingUser, String emailId, long companyId, String firstName, String lastName, String password)
			throws InvalidInputException, SolrException;

	/**
	 * Method to user profiles data in session and return selected profile
	 * 
	 * @param user
	 * @param session
	 * @return
	 * @throws NonFatalException
	 */
	public Map<Long, AbridgedUserProfile> processedUserProfiles(User user, AccountType accountType, Map<Long, UserProfile> profileMap)
			throws NonFatalException;

	public UserProfile updateSelectedProfile(User user, AccountType accountType, Map<Long, UserProfile> profileMap, String profileIdStr);

	/**
	 * Updates the users last login time and num of login
	 * 
	 * @param user
	 * @throws NonFatalException
	 */
	public void updateUserLoginTimeAndNum(User user) throws NonFatalException;

	/**
	 * Updates the user modification notification table
	 * 
	 * @param company
	 * @throws InvalidInputException
	 */
	public void updateUserCountModificationNotification(Company company) throws InvalidInputException;

	public void sendVerificationLink(User user) throws InvalidInputException, UndeliveredEmailException;

	/**
	 * Checks if the api secret and api key is a valid combination
	 * 
	 * @param apiSecret
	 * @param apiKey
	 * @return
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 */
	public boolean isValidApiKey(String apiSecret, String apiKey) throws InvalidInputException, NoRecordsFetchedException;
}
// JIRA SS-34 BY RM02 BOC