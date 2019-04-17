package com.realtech.socialsurvey.core.services.organizationmanagement;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.CompanyIgnoredEmailMapping;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.ProListUser;
import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.SettingsDetails;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserApiKey;
import com.realtech.socialsurvey.core.entities.UserEmailMapping;
import com.realtech.socialsurvey.core.entities.UserFromSearch;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.entities.UserSettings;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.exception.HierarchyAlreadyExistsException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.exception.UserAlreadyExistsException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.vo.UserList;


// JIRA SS-34 BY RM02 BOC
/**
 * Interface with methods defined to manage user
 */
public interface UserManagementService
{

    public List<User> getAllActiveUsers();


    public ProfilesMaster getProfilesMasterById( int profileId ) throws InvalidInputException;


    public User assignBranchAdmin( User user, long branchId, long userId ) throws InvalidInputException;


    public void updateUserStatus( long userId, int status ) throws InvalidInputException, SolrException;


    // JIRA SS-42 BY RM02 BOC

    /**
     * Method to add a new user into a company. Admin sends the invite to user for registering.
     * 
     * @param admin
     * @param firstName
     * @param lastName
     * @param emailId
     * @param holdSendingMail
     * @param isForHierarchyUpload
     * @param isAddedByRealtechOrSSAdmin
     * @throws InvalidInputException
     * @throws UserAlreadyExistsException
     * @throws UndeliveredEmailException
     * @throws NoRecordsFetchedException 
     */
    public User inviteUserToRegister( User admin, String firstName, String lastName, String emailId, boolean holdSendingMail,
        boolean sendMail, boolean isForHierarchyUpload, boolean isAddedByRealtechOrSSAdmin ) throws InvalidInputException, UserAlreadyExistsException, UndeliveredEmailException, NoRecordsFetchedException;


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
    public User inviteNewUser( User admin, String firstName, String lastName, String emailId )
        throws InvalidInputException, UserAlreadyExistsException, UndeliveredEmailException;


    // Method to remove an existing user. Soft deletion is done.
    public void removeExistingUser( User admin, long userIdToBeDeactivated, int status ) throws InvalidInputException;


    // Method to remove profile of a branch admin.
    public void unassignBranchAdmin( User admin, long branchId, long userIdToRemove ) throws InvalidInputException;


    // Method to remove profile of a region admin.
    public void unassignRegionAdmin( User admin, long regionId, long userIdToRemove ) throws InvalidInputException;


    // Method to return users with provided login name
    public User getUserByLoginName( User admin, String loginName ) throws InvalidInputException, NoRecordsFetchedException;


    // Method to return user with provided email and company
    public User getUserByEmailAndCompany( long companyId, String emailId )
        throws InvalidInputException, NoRecordsFetchedException;


    // Method to return user with provided email
    public User getUserByEmail( String emailId ) throws InvalidInputException, NoRecordsFetchedException;


    // Method to return users whose email id matches with the provided email id.
    public List<User> getUsersBySimilarEmailId( User admin, String emailId ) throws InvalidInputException;


    // Method to check if new users can be added to the current compeny or limit for the same is
    // exhausted.
    public boolean isUserAdditionAllowed( User user ) throws NoRecordsFetchedException, InvalidInputException;


    /**
     * Method to get user object for the given user id, fetches user along with profile name and
     * profile url
     * 
     * @param userId
     * @return
     * @throws InvalidInputException
     */
    public User getUserByUserId( long userId ) throws InvalidInputException;


    public User getUserByProfileId( long profileId ) throws InvalidInputException;


    public List<ProListUser> getMultipleUsersByUserId( List<Long> userIds ) throws InvalidInputException;


    // Method to get list of the branches assigned to the given user.
    public List<Branch> getBranchesAssignedToUser( User user ) throws NoRecordsFetchedException, InvalidInputException;


    // Method to return list of users belonging to the same company as that of user passed.
    public List<User> getUsersForCompany( User user ) throws InvalidInputException, NoRecordsFetchedException;


    // Method to assign a user to a particular branch.
    public void assignUserToBranch( User admin, long userId, long branchId ) throws InvalidInputException, SolrException;


    /**
     * Assign a user directly under the company.
     * 
     * @param admin
     * @param userId
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     * @throws SolrException
     */
    public void assignUserToCompany( User admin, long userId )
        throws InvalidInputException, NoRecordsFetchedException, SolrException;


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
    public void assignUserToRegion( User admin, long userId, long regionId )
        throws InvalidInputException, NoRecordsFetchedException, SolrException;


    // Method to unassign a user from branch.
    public void unassignUserFromBranch( User admin, long userId, long branchId ) throws InvalidInputException;


    // Method to update status of the given user as active or inactive.
    public void updateUser( User admin, long userIdToUpdate, boolean isActive ) throws InvalidInputException;


    // Method to update status of the given user profile as active or inactive.
    public void updateUserProfile( User admin, long profileIdToUpdate, int status ) throws InvalidInputException;


    public void updateUserProfilesStatus( User admin, long profileIdToUpdate ) throws InvalidInputException;


    /**
     * Sends registration completion mail
     * @param emailId
     * @param firstName
     * @param lastName
     * @param companyId
     * @param profileName
     * @param loginName
     * @param holdSendingMail
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    public void sendRegistrationCompletionLink( String emailId, String firstName, String lastName, long companyId,
        String profileName, String loginName, boolean holdSendingMail ) throws InvalidInputException, UndeliveredEmailException;


    // Method to set properties of a user based upon active profiles available for the user.
    public void setProfilesOfUser( User user );


    // JIRA SS-42 BY RM05 EOC

    /**
     * Sends invitation to corporate to register
     * 
     * @throws NonFatalException
     */
    public void inviteCorporateToRegister( String firstName, String lastName, String emailId, boolean isReinvitation,
        String referralCode ) throws InvalidInputException, UndeliveredEmailException, NonFatalException;


    /**
     * Validates the input and then invites corporate to register
     * @param firstName
     * @param lastName
     * @param emailId
     * @param isReinvitation
     * @param referralCode
     * @throws InvalidInputException
     * @throws UserAlreadyExistsException
     */
    public void validateAndInviteCorporateToRegister( String firstName, String lastName, String emailId, boolean isReinvitation,
        String referralCode ) throws InvalidInputException, UserAlreadyExistsException, NonFatalException;


    /**
     * Method to validate form parameters of invitation form
     * 
     * @param firstName
     * @param lastName
     * @param emailId
     * @throws InvalidInputException
     */
    public void validateFormParametersForInvitation( String firstName, String lastName, String emailId )
        throws InvalidInputException;


    public Map<String, String> validateRegistrationUrl( String encryptedUrlParameter ) throws InvalidInputException;


    public Boolean checkIfTheLinkHasExpired( String encryptedUrlParameter ) throws InvalidInputException;


    public User addCorporateAdminAndUpdateStage( String firstName, String lastName, String emailId, String password,
        boolean isDirectRegistration ) throws InvalidInputException, UserAlreadyExistsException, UndeliveredEmailException;


    public void updateProfileCompletionStage( User user, int profilesMasterId, String profileCompletionStage )
        throws InvalidInputException;


    public void verifyAccount( String encryptedUrlParams ) throws InvalidInputException, SolrException;


    // JIRA SS-42 by RM-06:BOC

    public List<UserProfile> getAllUserProfilesForUser( User user ) throws InvalidInputException;


    public boolean userExists( String userName ) throws InvalidInputException;


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
    public UserSettings getCanonicalUserSettings( User user, AccountType accountType )
        throws InvalidInputException, NoRecordsFetchedException;


    /**
     * Gets user settings
     * 
     * @param agentId
     * @return agentSettings
     * @throws InvalidInputException
     */
    public AgentSettings getUserSettings( long agentId ) throws InvalidInputException;


    /**
     * Get all the agent settings linked to the user profile
     * 
     * @param userProfiles
     * @return
     * @throws InvalidInputException
     */
    public AgentSettings getAgentSettingsForUserProfiles( long userId ) throws InvalidInputException;


    /**
     * Method to insert basic settings for a user
     * 
     * @param user
     * @throws InvalidInputException
     */
    public void insertAgentSettings( User user ) throws InvalidInputException;


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
    public UserProfile createUserProfile( User user, Company company, String emailId, long agentId, long branchId,
        long regionId, int profileMasterId, int isPrimary, String profileCompletionStage, int isProfileComplete,
        String createdBy, String modifiedBy );


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
    public List<UserFromSearch> checkUserCanEdit( User admin, UserFromSearch adminFromSearch, List<UserFromSearch> users )
        throws InvalidInputException;


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
    public User updateUserOnCompleteRegistration( User existingUser, String emailId, long companyId, String firstName,
        String lastName, String password ) throws InvalidInputException, SolrException;


    public UserProfile updateSelectedProfile( User user, AccountType accountType, Map<Long, UserProfile> profileMap,
        String profileIdStr );


    /**
     * Updates the users last login time and num of login
     * 
     * @param user
     * @throws NonFatalException
     */
    public void updateUserLoginTimeAndNum( User user ) throws NonFatalException;


    /**
     * Updates the user modification notification table
     * 
     * @param company
     * @throws InvalidInputException
     */
    public void updateUserCountModificationNotification( Company company ) throws InvalidInputException;


    public void sendVerificationLink( User user ) throws InvalidInputException, UndeliveredEmailException;


    public User getCompanyAdmin( long companyId ) throws InvalidInputException;


    public void updateUser( User user, Map<String, Object> map ) throws SolrException, InvalidInputException;


    public User inviteUser( User admin, String firstName, String lastName, String emailId, boolean isAddedByRealtechOrSSAdmin )
        throws InvalidInputException, UserAlreadyExistsException, UndeliveredEmailException, SolrException, NoRecordsFetchedException;


    public User addCorporateAdmin( String firstName, String lastName, String emailId, String confirmPassword,
        boolean isDirectRegistration )
        throws InvalidInputException, UserAlreadyExistsException, UndeliveredEmailException, SolrException, NoRecordsFetchedException;


    public String generateIndividualProfileName( long userId, String name, String emailId ) throws InvalidInputException;


    public User getUserObjByUserId( long userId ) throws InvalidInputException;


    public Company getCompanyById( long id );


    public Region getRegionById( long id );


    public Branch getBranchById( long id );


    public void updateRegion( Region region );


    public void updateBranch( Branch branch );


    public void updateCompany( Company company );


    public Map<String, Long> getPrimaryUserProfileByAgentId( long entityId )
        throws InvalidInputException, ProfileNotFoundException;


    void updateProfileUrlInAgentSettings( String profileName, String profileUrl, AgentSettings agentSettings )
        throws InvalidInputException;


    void updateProfileUrlInBranchSettings( String profileName, String profileUrl, OrganizationUnitSettings branchSettings )
        throws InvalidInputException;


    void updateProfileUrlInRegionSettings( String profileName, String profileUrl, OrganizationUnitSettings regionSettings )
        throws InvalidInputException;


    void updateProfileUrlInCompanySettings( String profileName, String profileUrl, OrganizationUnitSettings companySettings )
        throws InvalidInputException;


    public String fetchAppropriateLogoUrlFromHierarchyForUser( long userId )
        throws InvalidInputException, NoRecordsFetchedException, ProfileNotFoundException;


    void updatePrimaryProfileOfUser( User user ) throws InvalidInputException;


    void removeUserProfile( long profileIdToDelete ) throws InvalidInputException;


    /**
     * @param apiKey
     * @param apiSecret
     * @param companyId
     * @return
     * @throws InvalidInputException
     */
    public boolean validateUserApiKey( String apiKey, String apiSecret, long companyId ) throws InvalidInputException;


    void updateUserInSolr( User user ) throws InvalidInputException, SolrException;


    public int getUsersUnderBranchAdminCount( User admin );


    public int getUsersUnderRegionAdminCount( User admin );


    public int getUsersUnderCompanyAdminCount( User admin );


    public List<UserFromSearch> getUsersUnderBranchAdmin( User admin, int startIndex, int batchSize );


    public List<UserFromSearch> getUsersUnderRegionAdmin( User admin, int startIndex, int batchSize );


    public List<UserFromSearch> getUsersUnderCompanyAdmin( User admin, int startIndex, int batchSize );


    public List<UserFromSearch> getUsersByUserIds( Set<Long> userIds ) throws InvalidInputException;


    public void updateUser( User user );


    public User getActiveUserByEmailAndCompany( long companyId, String emailId )
        throws InvalidInputException, NoRecordsFetchedException;


    /**
     *  Method to get a map of userId - review count given a list of userIds
     * @param userIds
     * @return
     * @throws InvalidInputException
     */
    public Map<Long, Integer> getUserIdReviewCountMapFromUserIdList( List<Long> userIds ) throws InvalidInputException;


    /**
     * Method to search users in company by criteria
     * @param queries
     * @return
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     */
    public List<User> searchUsersInCompanyByMultipleCriteria( Map<String, Object> queries )
        throws InvalidInputException, NoRecordsFetchedException;


    public UserProfile getAgentUserProfileForUserId( long userId ) throws InvalidInputException;


	/**
	 * Method to restore deleted user
	 * @param userId
	 * @param restoreSocial
	 * @param branchId
	 * @throws InvalidInputException
	 * @throws SolrException
	 */
	public void restoreDeletedUser(long userId, boolean restoreSocial, long branchId)
			throws InvalidInputException, SolrException;


    public User getUserByEmailAddress( String emailId ) throws InvalidInputException, NoRecordsFetchedException;


    User createSocialSurveyAdmin( User admin, String firstName, String lastName, String emailId )
        throws InvalidInputException, UserAlreadyExistsException, UndeliveredEmailException;


    List<User> getSocialSurveyAdmins( User admin );


    void deleteSSAdmin( User admin, long ssAdminId ) throws InvalidInputException;


    User saveEmailUserMapping( String emailId, long userId , String createdAndModifiedBy) throws InvalidInputException, NoRecordsFetchedException;


    CompanyIgnoredEmailMapping saveIgnoredEmailCompanyMapping( String emailId, long companyId )
        throws InvalidInputException, NoRecordsFetchedException;


    public void removeUserProfile( User user, User adminUser, Long profileId ) throws UserAssignmentException;


    UserList getUsersAndEmailMappingForCompany( long companyId, int startIndex, int batchSize, long count )
        throws InvalidInputException, NoRecordsFetchedException;


    List<UserEmailMapping> getUserEmailMappingsForUser( long agentId ) throws InvalidInputException, NoRecordsFetchedException;


    void deleteUserEmailMapping( User agent, long emailMappingId ) throws InvalidInputException;


    void deleteIgnoredEmailMapping( String emailId ) throws InvalidInputException;


    boolean isUserSocialSurveyAdmin( long userId ) throws InvalidInputException;


    void updateUserEmailMapping( String modifiedBy, long emailMappingId, int status ) throws InvalidInputException;


    public void deleteUserDataFromAllSources( User loggedInUser, long userIdToBeDeleted, int status, boolean isForHierarchyUpload, boolean isDeletedByRealtechOrSSAdmin )
        throws InvalidInputException, SolrException;


    /**
     * Method to map CRM Data with AgentIds
     */
    public void crmDataAgentIdMApper();


    public User getAdminUserByCompanyId( long companyId );


    public ContactDetailsSettings fetchAgentContactDetailByEncryptedId( String userEncryptedId ) throws InvalidInputException;


    public String generateUserEncryptedId( long userId ) throws InvalidInputException;


    public void incompleteSurveyReminderSender();


    public void inviteCorporateToRegister( User user, int planId ) throws InvalidInputException, UndeliveredEmailException;


    public User activateCompanyAdmin( User companyAdmin )
        throws InvalidInputException, HierarchyAlreadyExistsException, SolrException;


    public UserApiKey getUserApiKeyForCompany( long companyId ) throws InvalidInputException;


    public UserApiKey generateAndSaveUserApiKey( long companyId ) throws InvalidInputException;


    public List<UserApiKey> getActiveUserApiKeys();


    public void updateStatusOfUserApiKey( long userApiKeyId, int status ) throws NoRecordsFetchedException;


    public List<Long> getExcludedUserIds();


    public Set<Long> getUserIdsUnderAdmin( User adminUser ) throws InvalidInputException;


    public void saveEmailUserMappingAndUpdateAgentIdInSurveyPreinitiation( String emailId, long userId , String createdAndModifiedBy)
            throws InvalidInputException, NoRecordsFetchedException;


    public void saveIgnoredEmailCompanyMappingAndUpdateSurveyPreinitiation( String emailId, long companyId )
        throws InvalidInputException, NoRecordsFetchedException;


    void temporaryInactiveCompanyAdmin( long companyId );


    public void updateUserEmailMapping( UserEmailMapping userEmailMapping ) throws InvalidInputException;


    public void updateUserProfileObject( UserProfile userProfile ) throws InvalidInputException;


    public void updateLastInviteSentDateIfUserExistsInDB( String emailId );

    
    public User getActiveAgentByEmailAndCompany( long companyId, String emailId ) throws InvalidInputException,
        NoRecordsFetchedException;


    public boolean isUserSocialSurveyAdmin( User user );

    
    public List<UserProfile> getUserProfiles( long userId ) throws InvalidInputException;
    
    public List<UserProfile> getAllAgentAdminProfilesForUser( User user ) throws InvalidInputException;


    public User getUserByEmailAndCompanyFromUserEmailMappings( Company company, String emailId )
        throws InvalidInputException, NoRecordsFetchedException;


    /**
     * @param usersList
     * @return
     * @throws InvalidInputException
     */
    public List<UserFromSearch> getUserSocialMediaList( List<UserFromSearch> usersList ) throws InvalidInputException;


	void updateAgentIdInSurveyPreinitiation(String emailId) throws InvalidInputException, NoRecordsFetchedException;
	
	
	public void updateSurveyDetails();
	
	
	/**
	 * Method to get all active roles for userIds
	 * @param userIds
	 * @return
	 * @throws InvalidInputException
	 */
	public List<UserFromSearch> getActiveUsersByUserIds( Set<Long> userIds ) throws InvalidInputException;

	
	/**
	 * Method to check if  branch and region admins can Add or Delete User.
	 * @param entityType
	 * @param companyId
	 * @param addOrDeleteFlag
	 * @return
	 * @throws InvalidInputException
	 */
	public boolean canAddAndDeleteUser(String entityType, long companyId, boolean addOrDeleteFlag) throws InvalidInputException;


    /**
     * Enables deletion of an agent's logo image.
     * @param collection Collection name in Mongo
     * @param unitSettings Representing organization settings. 
     * @throws InvalidInputException
     */
    void removeLogoImage( String collection, OrganizationUnitSettings unitSettings ) throws InvalidInputException;
}
// JIRA SS-34 BY RM02 BOC