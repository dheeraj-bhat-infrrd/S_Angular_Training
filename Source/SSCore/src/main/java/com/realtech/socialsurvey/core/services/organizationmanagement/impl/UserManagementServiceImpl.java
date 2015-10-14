package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.http.impl.client.NoopUserTokenHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.ProfileCompletionList;
import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.dao.BranchDao;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.SettingsSetterDao;
import com.realtech.socialsurvey.core.dao.SurveyDetailsDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.dao.UserInviteDao;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.BranchSettings;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.entities.MailIdSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.ProListUser;
import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.RemovedUser;
import com.realtech.socialsurvey.core.entities.SettingsDetails;
import com.realtech.socialsurvey.core.entities.SurveySettings;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserApiKey;
import com.realtech.socialsurvey.core.entities.UserFromSearch;
import com.realtech.socialsurvey.core.entities.UserInvite;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.entities.UserSettings;
import com.realtech.socialsurvey.core.entities.UsercountModificationNotification;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.enums.OrganizationUnit;
import com.realtech.socialsurvey.core.enums.SettingsForApplication;
import com.realtech.socialsurvey.core.exception.DatabaseException;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.exception.UserAlreadyExistsException;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UtilityService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.settingsmanagement.impl.InvalidSettingsStateException;
import com.realtech.socialsurvey.core.utils.EncryptionHelper;


/**
 * JIRA:SS-34 BY RM02 Implementation for User management services
 */
@DependsOn ( "generic")
@Component
public class UserManagementServiceImpl implements UserManagementService, InitializingBean
{

    private static final Logger LOG = LoggerFactory.getLogger( UserManagementServiceImpl.class );
    private static Map<Integer, ProfilesMaster> profileMasters = new HashMap<Integer, ProfilesMaster>();

    @Autowired
    private URLGenerator urlGenerator;

    @Value ( "${APPLICATION_BASE_URL}")
    private String applicationBaseUrl;

    @Autowired
    private EmailServices emailServices;

    @Autowired
    private SettingsSetterDao settingsSetterDao;

    @Autowired
    private EncryptionHelper encryptionHelper;

    @Autowired
    private SolrSearchService solrSearchService;

    @Resource
    @Qualifier ( "userInvite")
    private UserInviteDao userInviteDao;

    @Autowired
    private GenericDao<Company, Long> companyDao;

    @Autowired
    private GenericDao<ProfilesMaster, Integer> profilesMasterDao;

    @Autowired
    private GenericDao<UsercountModificationNotification, Long> userCountModificationDao;

    @Autowired
    private Utils utils;

    @Resource
    @Qualifier ( "user")
    private UserDao userDao;

    @Resource
    @Qualifier ( "userProfile")
    private UserProfileDao userProfileDao;

    @Autowired
    private GenericDao<LicenseDetail, Long> licenseDetailsDao;

    @Resource
    @Qualifier ( "branch")
    private BranchDao branchDao;

    @Autowired
    private GenericDao<Region, Long> regionDao;

    @Autowired
    private GenericDao<UserApiKey, Long> apiKeyDao;

    @Autowired
    private OrganizationUnitSettingsDao organizationUnitSettingsDao;

    @Autowired
    private GenericDao<RemovedUser, Long> removedUserDao;

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Value ( "${ENABLE_KAFKA}")
    private String enableKafka;

    @Value ( "${APPLICATION_ADMIN_EMAIL}")
    private String applicationAdminEmail;

    @Value ( "${APPLICATION_ADMIN_NAME}")
    private String applicationAdminName;

    @Autowired
    private ProfileManagementService profileManagementService;

    @Autowired
    private ProfileCompletionList profileCompletionList;

    @Autowired
    private SurveyDetailsDao surveyDetailsDao;

    @Autowired
    private UtilityService utilityService;


    /**
     * Method to get profile master based on profileId, gets the profile master from Map which is
     * pre-populated with afterPropertiesSet method
     */
    @Override
    @Transactional
    public ProfilesMaster getProfilesMasterById( int profileId ) throws InvalidInputException
    {
        LOG.info( "Method getProfilesMasterById called for profileId : " + profileId );
        if ( profileId <= 0 ) {
            throw new InvalidInputException( "profile Id is not set for getting profile master" );
        }
        ProfilesMaster profilesMaster = null;
        if ( profileMasters.containsKey( profileId ) ) {
            profilesMaster = profileMasters.get( profileId );
        } else {
            throw new InvalidInputException( "No profile master detected for profileID : " + profileId );
        }
        LOG.info( "Method getProfilesMasterById finished for profileId : " + profileId );
        return profilesMaster;
    }


    // Moved code from RegistrationServiceImpl By RM-05:BOC

    @Override
    @Transactional ( rollbackFor = { NonFatalException.class, FatalException.class })
    public void inviteCorporateToRegister( String firstName, String lastName, String emailId, boolean isReinvitation )
        throws InvalidInputException, UndeliveredEmailException, NonFatalException
    {
        LOG.info( "Inviting corporate to register. Details\t first name:" + firstName + "\t lastName: " + lastName
            + "\t email id: " + emailId );

        Map<String, String> urlParams = new HashMap<String, String>();
        urlParams.put( CommonConstants.FIRST_NAME, firstName );
        urlParams.put( CommonConstants.LAST_NAME, lastName );
        urlParams.put( CommonConstants.EMAIL_ID, emailId );
        urlParams.put( CommonConstants.CURRENT_TIMESTAMP, String.valueOf( System.currentTimeMillis() ) );
        urlParams.put( CommonConstants.UNIQUE_IDENTIFIER, generateUniqueIdentifier() );
        LOG.debug( "Generating URL" );
        String url = urlGenerator.generateUrl( urlParams, applicationBaseUrl
            + CommonConstants.REQUEST_MAPPING_SHOW_REGISTRATION );
        LOG.debug( "Sending invitation for registration" );
        inviteUser( url, emailId, firstName, lastName, isReinvitation );

        LOG.info( "Successfully sent invitation to :" + emailId + " for registration" );
    }


    private synchronized String generateUniqueIdentifier()
    {
        String systemTimeStamp = String.valueOf( System.currentTimeMillis() );
        try {
            systemTimeStamp = encryptionHelper.encryptAES( systemTimeStamp, "" );
        } catch ( InvalidInputException e ) {
            LOG.error( "Exception Caught " + e.getMessage() );
        }
        return systemTimeStamp;

    }


    /*
     * This method contains the process to be done after URL is hit by the User. It involves
     * decrypting URL and validating parameters.
     */
    @Override
    @Transactional ( rollbackFor = { NonFatalException.class, FatalException.class })
    public Map<String, String> validateRegistrationUrl( String encryptedUrlParameter ) throws InvalidInputException
    {
        LOG.info( "Method validateRegistrationUrl() called " );

        Map<String, String> urlParameters = urlGenerator.decryptParameters( encryptedUrlParameter );
        validateCompanyRegistrationUrlParameters( encryptedUrlParameter );

        LOG.info( "Method validateRegistrationUrl() finished " );
        return urlParameters;
    }


    @Override
    public Boolean checkIfTheLinkHasExpired( String encryptedUrlParameter ) throws InvalidInputException
    {
        Map<String, String> urlParameters = urlGenerator.decryptParameters( encryptedUrlParameter );
        Boolean linkExpired = false;
        Long userTimestamp = null;
        Long systemTimestamp = System.currentTimeMillis();
        if ( urlParameters.containsKey( CommonConstants.CURRENT_TIMESTAMP ) ) {
            userTimestamp = Long.valueOf( urlParameters.get( CommonConstants.CURRENT_TIMESTAMP ) );
        }
        int days = CommonConstants.EXPIRE_AFTER_DAYS;
        long milliseconds = days * 24 * 60 * 60 * 1000;
        if ( systemTimestamp - userTimestamp > milliseconds ) {
            linkExpired = true;

        }
        return linkExpired;
    }


    // JIRA: SS-27: By RM05: BOC
    /**
     * This method creates a new user, user profile post validation of URL and also invalidates the
     * registration link used by the user to register
     * 
     * @throws UserAlreadyExistsException
     * @throws UndeliveredEmailException
     */
    @Override
    @Transactional ( rollbackFor = { NonFatalException.class, FatalException.class })
    public User addCorporateAdminAndUpdateStage( String firstName, String lastName, String emailId, String password,
        boolean isDirectRegistration ) throws InvalidInputException, UserAlreadyExistsException, UndeliveredEmailException
    {
        LOG.info( "Method to add corporate admin called for emailId : " + emailId );
        if ( userExists( emailId ) ) {
            throw new UserAlreadyExistsException( "User with User ID : " + emailId + " already exists" );
        }
        Company company = companyDao.findById( Company.class, CommonConstants.DEFAULT_COMPANY_ID );
        String encryptedPassword = encryptionHelper.encryptSHA512( password );
        int status = CommonConstants.STATUS_ACTIVE;

        /**
         * If the registration is not through an invite, status of the user is "not verified" and a
         * verification link is sent. For an invitation, email is already verified hence status is
         * active
         */
        /**
         * if (isDirectRegistration) { status = CommonConstants.STATUS_NOT_VERIFIED; } // JIRA -
         * SS-536: Added for manual registration via invite else{ // setting the status as active as
         * creation is done by admin status = CommonConstants.STATUS_ACTIVE; }
         **/
        // set the status as active as with the new sign up path, validation is not required.
        status = CommonConstants.STATUS_ACTIVE;
        LOG.debug( "Creating new user with emailId : " + emailId + " and verification status : " + status );
        User user = createUser( company, encryptedPassword, emailId, firstName, lastName, CommonConstants.STATUS_ACTIVE,
            status, CommonConstants.ADMIN_USER_NAME );
        user = userDao.save( user );

        LOG.debug( "Creating user profile for :" + emailId + " with profile completion stage : "
            + CommonConstants.ADD_COMPANY_STAGE );
        //the newlely creted profile will be primary because this is will be the first profile of user
        UserProfile userProfile = createUserProfile( user, company, emailId, CommonConstants.DEFAULT_AGENT_ID,
            CommonConstants.DEFAULT_BRANCH_ID, CommonConstants.DEFAULT_REGION_ID,
            CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID, CommonConstants.IS_PRIMARY_FALSE,
            CommonConstants.ADD_COMPANY_STAGE, CommonConstants.STATUS_INACTIVE, String.valueOf( user.getUserId() ),
            String.valueOf( user.getUserId() ) );


        // add the company admin profile with the user object
        List<UserProfile> userProfiles = new ArrayList<UserProfile>();
        userProfiles.add( userProfile );
        user.setUserProfiles( userProfiles );
        userProfileDao.save( userProfile );

        /**
         * if it is direct registration, send verification link else invalidate the invitation link
         */
        /*
         * if (isDirectRegistration) {
         * LOG.debug("Calling method for sending verification link for user : " + user.getUserId());
         * sendVerificationLink(user); }
         */
        // JIRA - SS-536 removed for manual registration via invite
        /*
         * else { LOG.debug("Invalidating registration link for emailId : " + emailId);
         * invalidateRegistrationInvite(emailId); }
         */
        setProfilesOfUser( user );
        LOG.info( "Successfully executed method to add corporate admin for emailId : " + emailId );
        return user;
    }


    /**
     * JIRA SS-35 BY RM02 Method to update the profile completion stage of user i.e the stage which
     * user has completed while registration, stores the next step to be taken by user while
     * registration process
     * 
     * @throws InvalidInputException
     */
    @Override
    @Transactional
    public void updateProfileCompletionStage( User user, int profileMasterId, String profileCompletionStage )
        throws InvalidInputException
    {
        if ( profileCompletionStage == null || profileCompletionStage.isEmpty() ) {
            throw new InvalidInputException( "Profile completion stage is not set for updation" );
        }
        if ( user == null ) {
            throw new InvalidInputException( "UserId is not null for updation of Profile completion stage" );
        }
        if ( profileMasterId <= 0 ) {
            throw new InvalidInputException( "Profile master id is not set for updation of Profile completion stage" );
        }
        LOG.info( "Mehtod updateProfileCompletionStage called for profileCompletionStage : " + profileCompletionStage
            + " and profileMasterId : " + profileMasterId + " and userId : " + user.getUserId() );
        Map<String, Object> queries = new HashMap<String, Object>();
        queries.put( CommonConstants.USER_COLUMN, user );
        queries.put( CommonConstants.PROFILE_MASTER_COLUMN, getProfilesMasterById( profileMasterId ) );
        List<UserProfile> userProfiles = userProfileDao.findByKeyValue( UserProfile.class, queries );

        if ( userProfiles != null && !userProfiles.isEmpty() ) {
            for ( UserProfile userProfile : userProfiles ) {
                userProfile.setProfileCompletionStage( profileCompletionStage );
                userProfile.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
                userProfile.setModifiedBy( String.valueOf( user.getUserId() ) );
                userProfileDao.update( userProfile );
            }
        } else {
            LOG.warn( "No profile found for updating profile completion stage" );
        }
        LOG.info( "Mehtod updateProfileCompletionStage finished for profileCompletionStage : " + profileCompletionStage );
    }


    /**
     * Method to verify a user's account
     * 
     * @param encryptedUrlParams
     * @throws InvalidInputException
     * @throws SolrException
     */
    @Transactional ( rollbackFor = { NonFatalException.class, FatalException.class })
    public void verifyAccount( String encryptedUrlParams ) throws InvalidInputException, SolrException
    {
        LOG.info( "Method to verify account called for encryptedUrlParams" );
        Map<String, String> urlParams = urlGenerator.decryptParameters( encryptedUrlParams );
        if ( urlParams == null || urlParams.isEmpty() ) {
            throw new InvalidInputException( "Url params are invalid for account verification" );
        }
        if ( urlParams.containsKey( CommonConstants.USER_ID ) ) {
            long userId = Long.parseLong( urlParams.get( CommonConstants.USER_ID ) );

            LOG.debug( "Calling user management service for updating user status to active" );
            updateUserStatus( userId, CommonConstants.STATUS_ACTIVE );
        } else {
            throw new InvalidInputException( "User id field not present in url params" );
        }
        LOG.info( "Successfully completed method to verify account" );
    }


    /**
     * Method to add a new user into a company. Admin sends the invite to user for registering.
     */
    @Transactional
    @Override
    public User inviteUserToRegister( User admin, String firstName, String lastName, String emailId )
        throws InvalidInputException, UserAlreadyExistsException, UndeliveredEmailException
    {
        if ( firstName == null || firstName.isEmpty() ) {
            throw new InvalidInputException( "First name is either null or empty in inviteUserToRegister()." );
        }
        if ( emailId == null || emailId.isEmpty() ) {
            throw new InvalidInputException( "Email Id is either null or empty in inviteUserToRegister()." );
        }
        LOG.info( "Method to add a new user, inviteUserToRegister() called for email id : " + emailId );

        if ( userExists( emailId ) ) {
            throw new UserAlreadyExistsException( "User with User ID : " + emailId + " already exists" );
        }

        User user = createUser( admin.getCompany(), null, emailId, firstName, lastName, CommonConstants.STATUS_ACTIVE,
            CommonConstants.STATUS_NOT_VERIFIED, CommonConstants.ADMIN_USER_NAME );
        user = userDao.save( user );

        LOG.debug( "Inserting agent settings for the user:" + user );
        insertAgentSettings( user );

        String profileName = getUserSettings( user.getUserId() ).getProfileName();
        sendRegistrationCompletionLink( emailId, firstName, lastName, admin.getCompany().getCompanyId(), profileName,
            user.getLoginName() );
        LOG.info( "Method to add a new user, inviteUserToRegister finished for email id : " + emailId );
        return user;
    }


    /*
     * Method to invite new user
     */
    @Override
    @Transactional
    public User inviteNewUser( User admin, String firstName, String lastName, String emailId ) throws InvalidInputException,
        UserAlreadyExistsException, UndeliveredEmailException
    {
        if ( firstName == null || firstName.isEmpty() ) {
            throw new InvalidInputException( "First name is either null or empty in inviteUserToRegister()." );
        }
        if ( emailId == null || emailId.isEmpty() ) {
            throw new InvalidInputException( "Email Id is either null or empty in inviteUserToRegister()." );
        }
        LOG.info( "Method to add a new user, inviteNewUser() called for email id : " + emailId );

        if ( userExists( emailId ) ) {
            throw new UserAlreadyExistsException( "User with User ID : " + emailId + " already exists" );
        }

        User user = createUser( admin.getCompany(), null, emailId, firstName, lastName, CommonConstants.STATUS_INACTIVE,
            CommonConstants.STATUS_NOT_VERIFIED, String.valueOf( admin.getUserId() ) );
        user = userDao.save( user );

        LOG.info( "Method to add a new user, inviteNewUser() finished for email id : " + emailId );
        return user;
    }


    /*
     * Method to deactivate an existing user.
     */
    @Transactional
    @Override
    public void removeExistingUser( User admin, long userIdToRemove ) throws InvalidInputException
    {
        if ( admin == null ) {
            throw new InvalidInputException( "Admin user is null in deactivateExistingUser" );
        }
        if ( userIdToRemove <= 0l ) {
            throw new InvalidInputException( "User id is invalid in deactivateExistingUser" );
        }

        LOG.info( "Method to deactivate user " + userIdToRemove + " called." );
        User userToBeDeactivated = userDao.findById( User.class, userIdToRemove );
        userToBeDeactivated.setLoginName( userToBeDeactivated.getLoginName() + "_" + System.currentTimeMillis() );
        userToBeDeactivated.setStatus( CommonConstants.STATUS_INACTIVE );
        userToBeDeactivated.setModifiedBy( String.valueOf( admin.getUserId() ) );
        userToBeDeactivated.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );

        LOG.info( "Deactivating user " + userToBeDeactivated.getFirstName() );
        userDao.update( userToBeDeactivated );

        // Create an entry into the RemovedUser table for keeping historical records of users.
        RemovedUser removedUser = new RemovedUser();
        removedUser.setCompany( userToBeDeactivated.getCompany() );
        removedUser.setUser( userToBeDeactivated );
        removedUser.setCreatedBy( String.valueOf( admin.getUserId() ) );
        removedUser.setCreatedOn( new Timestamp( System.currentTimeMillis() ) );
        removedUserDao.save( removedUser );

        // Marks all the user profiles for given user as inactive.
        userProfileDao.deactivateAllUserProfilesForUser( admin, userToBeDeactivated );

        LOG.info( "Method to deactivate user " + userToBeDeactivated.getFirstName() + " finished." );
    }


    /*
     * Method to get user with login name of a company
     */
    @Transactional
    @Override
    public User getUserByLoginName( User admin, String loginName ) throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.info( "Method to fetch list of users on the basis of email id is called." );

        if ( admin == null ) {
            throw new InvalidInputException( "Admin user is null in getUserByLoginName()" );
        }
        if ( loginName == null || loginName.isEmpty() ) {
            throw new InvalidInputException( "Email id is null or empty in getUsersByEmailId()" );
        }
        Map<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.LOGIN_NAME, loginName );
        queries.put( CommonConstants.COMPANY, admin.getCompany() );
        List<User> users = userDao.findByKeyValue( User.class, queries );
        if ( users == null || users.isEmpty() ) {
            throw new NoRecordsFetchedException( "No users found with the login name : {}", loginName );
        }
        LOG.info( "Method to fetch list of users on the basis of email id is finished." );
        return users.get( CommonConstants.INITIAL_INDEX );
    }


    // Method to return user with provided email and company
    @Transactional
    @Override
    public User getUserByEmailAndCompany( long companyId, String emailId ) throws InvalidInputException,
        NoRecordsFetchedException
    {
        LOG.info( "Method getUserByEmailAndCompany() called from UserManagementService" );

        if ( emailId == null || emailId.isEmpty() ) {
            throw new InvalidInputException( "Email id is null or empty in getUserByEmailAndCompany()" );
        }

        Company company = companyDao.findById( Company.class, companyId );

        Map<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.LOGIN_NAME, emailId );
        queries.put( CommonConstants.COMPANY, company );

        List<User> users = userDao.findByKeyValue( User.class, queries );
        if ( users == null || users.isEmpty() ) {
            throw new NoRecordsFetchedException( "No users found with the login name : {}", emailId );
        }

        LOG.info( "Method getUserByEmailAndCompany() finished from UserManagementService" );
        return users.get( CommonConstants.INITIAL_INDEX );
    }


    // Method to return user with provided email
    @Transactional
    @Override
    public User getUserByEmail( String emailId ) throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.info( "Method getUserByEmail() called from UserManagementService" );

        if ( emailId == null || emailId.isEmpty() ) {
            throw new InvalidInputException( "Email id is null or empty in getUserByEmailAndCompany()" );
        }

        Map<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.LOGIN_NAME, emailId );

        List<User> users = userDao.findByKeyValue( User.class, queries );
        if ( users == null || users.isEmpty() ) {
            throw new NoRecordsFetchedException( "No users found with the login name : {}", emailId );
        }

        LOG.info( "Method getUserByEmail() finished from UserManagementService" );
        return users.get( CommonConstants.INITIAL_INDEX );
    }


    @Transactional
    @Override
    public List<User> getUsersBySimilarEmailId( User admin, String emailId ) throws InvalidInputException
    {
        LOG.info( "Method to fetch list of users on the basis of email id is called." );

        if ( admin == null ) {
            throw new InvalidInputException( "Admin user is null in getUsersByEmailId()" );
        }
        if ( emailId == null || emailId.isEmpty() ) {
            throw new InvalidInputException( "Email id is null or empty in getUsersByEmailId()" );
        }

        List<User> users = userDao.fetchUsersBySimilarEmailId( admin, emailId );

        LOG.info( "Method to fetch list of users on the basis of email id is finished." );
        return users;
    }


    @Transactional
    @Override
    public boolean isUserAdditionAllowed( User user ) throws NoRecordsFetchedException
    {
        LOG.info( "Method to check whether users can be added or not started." );
        boolean isUserAdditionAllowed = false;

        List<LicenseDetail> licenseDetails = licenseDetailsDao.findByColumn( LicenseDetail.class, CommonConstants.COMPANY,
            user.getCompany() );

        if ( licenseDetails == null || licenseDetails.isEmpty() ) {
            LOG.error( "Could not find any record in License_Details for : " + user.getCompany().getCompany() );
            throw new NoRecordsFetchedException( "Could not find any record in License_Details for : "
                + user.getCompany().getCompany() );
        }

        int maxUsersAllowed = licenseDetails.get( CommonConstants.INITIAL_INDEX ).getAccountsMaster().getMaxUsersAllowed();
        if ( maxUsersAllowed != CommonConstants.NO_LIMIT ) {
            long currentNumberOfUsers = userDao.getUsersCountForCompany( user.getCompany() );
            isUserAdditionAllowed = ( currentNumberOfUsers < maxUsersAllowed ) ? true : false;
        } else {
            LOG.debug( "No limit for number of user" );
            isUserAdditionAllowed = true;
        }

        LOG.info( "Method to check whether users can be added or not finished." );
        return isUserAdditionAllowed;
    }


    // JIRA SS-42 BY RM05 EOC

    /**
     * JIRA SS-42 BY RM05 BOC Method to remove profile of a branch admin.
     */
    @Transactional
    @Override
    public void unassignBranchAdmin( User admin, long branchId, long userIdToRemove ) throws InvalidInputException
    {
        if ( admin == null ) {
            throw new InvalidInputException( "Admin user is null in removeBranchAdmin" );
        }
        if ( branchId <= 0l ) {
            throw new InvalidInputException( "Branch id is invalid in removeBranchAdmin" );
        }
        if ( userIdToRemove <= 0l ) {
            throw new InvalidInputException( "User id is invalid in removeBranchAdmin" );
        }
        LOG.info( "Method to removeBranchAdmin called for branchId : " + branchId + " and userId : " + userIdToRemove );

        LOG.debug( "Selecting user for the userId provided for branch admin : " + userIdToRemove );
        User userToBeDeactivated = userDao.findById( User.class, userIdToRemove );
        if ( userToBeDeactivated == null ) {
            throw new InvalidInputException( "No user found for userId specified in createBranchAdmin" );
        }
        /**
         * admin is the logged in user, userToBeDeactivated is the user passed by admin to
         * deactivate.
         */
        userProfileDao.deactivateUserProfileForBranch( admin, branchId, userToBeDeactivated );

        LOG.info( "Method to removeBranchAdmin finished for branchId : " + branchId + " and userId : " + userIdToRemove );
    }


    /**
     * Method to remove profile of a region admin.
     */
    @Transactional
    @Override
    public void unassignRegionAdmin( User admin, long regionId, long userId ) throws InvalidInputException
    {
        if ( admin == null ) {
            throw new InvalidInputException( "Admin user is null in unassignRegionAdmin" );
        }
        if ( regionId <= 0l ) {
            throw new InvalidInputException( "Region id is invalid in unassignRegionAdmin" );
        }
        if ( userId <= 0l ) {
            throw new InvalidInputException( "User id is invalid in unassignRegionAdmin" );
        }
        LOG.info( "Method to removeRegionAdmin called for regionId : " + regionId + " and userId : " + userId );

        LOG.debug( "Selecting user for the userId provided for region admin : " + userId );
        User userToBeDeactivated = userDao.findById( User.class, userId );
        if ( userToBeDeactivated == null ) {
            throw new InvalidInputException( "No user found for userId specified in unassignRegionAdmin" );
        }

        userProfileDao.deactivateUserProfileForRegion( admin, regionId, userToBeDeactivated );

        LOG.info( "Method to unassignRegionAdmin finished for regionId : " + regionId + " and userId : " + userId );

    }


    /**
     * Method to get user object for the given user id, fetches user along with profile name and
     * profile url
     */
    @Transactional
    @Override
    public User getUserByUserId( long userId ) throws InvalidInputException
    {
        LOG.info( "Method to find user on the basis of user id started for user id " + userId );
        User user = null;
        user = userDao.findById( User.class, userId );
        if ( user == null ) {
            throw new InvalidInputException( "User not found for userId:" + userId );
        }
        OrganizationUnitSettings agentSettings = organizationUnitSettingsDao.fetchAgentSettingsById( userId );
        if ( agentSettings == null ) {
            throw new InvalidInputException( "No settings found for user :" + userId + " in getUserByUserId" );
        }
        user.setProfileName( agentSettings.getProfileName() );
        user.setProfileUrl( agentSettings.getProfileUrl() );

        LOG.info( "Method to find user on the basis of user id finished for user id " + userId );
        return user;
    }


    /**
     * Method to get user object for the given user id
     */
    @Transactional
    @Override
    public User getUserObjByUserId( long userId ) throws InvalidInputException
    {
        LOG.info( "Method to find user on the basis of user id started for user id " + userId );
        User user = null;
        user = userDao.findById( User.class, userId );
        if ( user == null ) {
            throw new InvalidInputException( "User not found for userId:" + userId );
        }
        LOG.info( "Method to find user on the basis of user id finished for user id " + userId );
        return user;
    }


    @Override
    @Transactional
    public User getUserByProfileId( long profileId ) throws InvalidInputException
    {
        LOG.info( "Method to find userprofile on the basis of profile id started for profileId " + profileId );

        UserProfile userProfile = userProfileDao.findById( UserProfile.class, profileId );
        if ( userProfile == null ) {
            throw new InvalidInputException( "UserProfile not found for userId:" + profileId );
        }

        LOG.info( "Method to find userprofile on the basis of user id finished for profileId " + profileId );
        return userProfile.getUser();
    }


    /**
     * Method to get multiple users object for the given list of user ids, fetches users along with
     * profile name and profile url
     */
    @Transactional
    @Override
    public List<ProListUser> getMultipleUsersByUserId( List<Long> userIds ) throws InvalidInputException
    {
        LOG.info( "Method to find multiple users on the basis of list of user id started for user ids " + userIds );
        List<ProListUser> users = new ArrayList<ProListUser>();
        List<AgentSettings> agentSettingsList = organizationUnitSettingsDao.fetchMultipleAgentSettingsById( userIds );
        if ( agentSettingsList == null ) {
            throw new InvalidInputException( "No settings found for user :" + userIds + " in getUserByUserId" );
        }

        for ( AgentSettings agentSettings : agentSettingsList ) {
            ProListUser user = new ProListUser();
            user.setUserId( agentSettings.getIden() );
            user.setDisplayName( agentSettings.getContact_details().getName() );
            user.setProfileName( agentSettings.getProfileName() );
            user.setProfileUrl( agentSettings.getProfileUrl() );
            user.setProfileImageUrl( agentSettings.getProfileImageUrl() );
            user.setEmailId( agentSettings.getContact_details().getMail_ids().getWork() );
            user.setTitle( agentSettings.getContact_details().getTitle() );
            user.setLocation( agentSettings.getContact_details().getLocation() );
            user.setIndustry( agentSettings.getContact_details().getIndustry() );
            user.setAboutMe( agentSettings.getContact_details().getAbout_me() );
            //JIRA SS-1104 search results not updated with correct number of reviews
            long reviewCount = profileManagementService.getReviewsCount( agentSettings.getIden(), 0, 5,
                CommonConstants.PROFILE_LEVEL_INDIVIDUAL, true );
            user.setReviewCount( reviewCount );
            user.setReviewScore( surveyDetailsDao.getRatingForPastNdays( CommonConstants.AGENT_ID, agentSettings.getIden(),
                CommonConstants.NO_LIMIT, true, false ) );
            users.add( user );
        }
        LOG.info( "Method to find multiple users on the basis of list of user id finished for user ids " + userIds );
        return users;
    }


    /*
     * Method to return list of branches assigned to the user passed.
     */
    @Transactional
    @Override
    public List<Branch> getBranchesAssignedToUser( User user ) throws NoRecordsFetchedException
    {
        LOG.info( "Method to find branches assigned to the user started for " + user.getFirstName() );
        List<Long> branchIds = userProfileDao.getBranchIdsForUser( user );
        if ( branchIds == null || branchIds.isEmpty() ) {
            LOG.error( "No branch found for user : " + user.getUserId() );
            throw new NoRecordsFetchedException( "No branch found for user : " + user.getUserId() );
        }
        List<Branch> branches = branchDao.findByColumnForMultipleValues( Branch.class, "branchId", branchIds );
        LOG.info( "Method to find branches assigned to the user finished for " + user.getFirstName() );
        return branches;
    }


    /*
     * Method to return list of users belonging to the same company as that of user passed.
     */
    @Transactional
    @Override
    public List<User> getUsersForCompany( User user ) throws InvalidInputException, NoRecordsFetchedException
    {
        if ( user == null ) {
            LOG.error( "User cannote be null." );
            throw new InvalidInputException( "Null value found  user found for userId specified in getUsersForCompany()" );
        }
        LOG.info( "Method getUsersForCompany() started for " + user.getUserId() );
        List<User> users = userDao.getUsersForCompany( user.getCompany() );
        if ( users == null || users.isEmpty() ) {
            LOG.error( "No user found for company : " + user.getCompany().getCompany() );
            throw new NoRecordsFetchedException( "No user found for company : " + user.getCompany().getCompany() );
        }
        LOG.info( "Method getUsersForCompany() started for " + user.getUserId() );
        return users;
    }


    // JIRA SS-42 BY RM05 EOC

    @Override
    public void afterPropertiesSet() throws Exception
    {
        LOG.info( "afterPropertiesSet for UserManagementServiceImpl called" );
        Map<Integer, ProfilesMaster> profilesMap = new HashMap<>();
        LOG.debug( "Populating profile master from db into the hashMap" );
        profilesMap = utilityService.populateProfileMastersMap();
        if ( !profilesMap.isEmpty() ) {
            profileMasters.putAll( profilesMap );
        }
        LOG.debug( "Successfully populated profile master from db into the hashMap" );

        LOG.info( "afterPropertiesSet for UserManagementServiceImpl completed" );
    }


    /**
     * Method to create profile for a branch admin
     */
    @Override
    @Transactional
    public User assignBranchAdmin( User admin, long branchId, long userId ) throws InvalidInputException
    {
        if ( admin == null ) {
            throw new InvalidInputException( "Company is null in assignBranchAdmin" );
        }
        if ( branchId <= 0l ) {
            throw new InvalidInputException( "Branch id is invalid in assignBranchAdmin" );
        }
        if ( userId <= 0l ) {
            throw new InvalidInputException( "User id is invalid in assignBranchAdmin" );
        }
        LOG.info( "Method to assign branch admin called for branchId : " + branchId + " and userId : " + userId );

        LOG.debug( "Selecting user for the userId provided for branch admin : " + userId );

        User user = userDao.findById( User.class, userId );
        if ( user == null ) {
            throw new InvalidInputException( "No user found for userId specified in createBranchAdmin" );
        }

        // Re-activate the existing user profile if user for given branch
        // already exists.
        UserProfile userProfile = getUserProfileForBranch( branchId, user );
        if ( userProfile == null ) {
            LOG.debug( "Creating new User profile as User does not exist for given branch." );
            /**
             * created and modified by are of the logged in user, rest user attributes come from
             */
            userProfile = createUserProfile( user, admin.getCompany(), user.getEmailId(), CommonConstants.DEFAULT_AGENT_ID,
                branchId, CommonConstants.DEFAULT_REGION_ID, CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID,
                CommonConstants.IS_PRIMARY_FALSE, CommonConstants.DASHBOARD_STAGE, CommonConstants.STATUS_ACTIVE,
                String.valueOf( admin.getUserId() ), String.valueOf( admin.getUserId() ) );
            //check that new profile will be primary or not
            int isPrimary = checkWillNewProfileBePrimary( userProfile, user.getUserProfiles() );
            userProfile.setIsPrimary( isPrimary );
            userProfileDao.save( userProfile );
        } else if ( userProfile.getStatus() == CommonConstants.STATUS_INACTIVE ) {
            LOG.info( "User profile for same user and branch already exists. Activating the same." );
            userProfile.setStatus( CommonConstants.STATUS_ACTIVE );
            userProfile.setModifiedBy( String.valueOf( admin.getUserId() ) );
            userProfile.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
            //check that new profile will be primary or not
            int isPrimary = checkWillNewProfileBePrimary( userProfile, user.getUserProfiles() );
            userProfile.setIsPrimary( isPrimary );
            userProfileDao.update( userProfile );
        }

        LOG.info( "Method to assignBranchAdmin finished for branchId : " + branchId + " and userId : " + userId );

        return user;
    }


    @Override
    @Transactional
    public UserProfile createUserProfile( User user, Company company, String emailId, long agentId, long branchId,
        long regionId, int profileMasterId, int isPrimary, String profileCompletionStage, int isProfileComplete,
        String createdBy, String modifiedBy )
    {
        LOG.debug( "Method createUserProfile called for username : " + user.getLoginName() );
        UserProfile userProfile = new UserProfile();
        userProfile.setAgentId( agentId );
        userProfile.setBranchId( branchId );
        userProfile.setCompany( company );
        userProfile.setEmailId( emailId );
        userProfile.setIsProfileComplete( isProfileComplete );
        userProfile.setIsPrimary( isPrimary );
        userProfile.setProfilesMaster( profilesMasterDao.findById( ProfilesMaster.class, profileMasterId ) );
        userProfile.setProfileCompletionStage( profileCompletionStage );
        userProfile.setRegionId( regionId );
        userProfile.setStatus( CommonConstants.STATUS_ACTIVE );
        userProfile.setUser( user );
        Timestamp currentTimestamp = new Timestamp( System.currentTimeMillis() );
        userProfile.setCreatedOn( currentTimestamp );
        userProfile.setModifiedOn( currentTimestamp );
        userProfile.setCreatedBy( createdBy );
        userProfile.setModifiedBy( modifiedBy );
        LOG.debug( "Method createUserProfile() finished" );
        return userProfile;
    }


    // Method to check if a user is already assigned to a branch.
    // Returns the status of user if found, -1 otherwise.
    private UserProfile getUserProfileForBranch( long branchId, User user )
    {
        LOG.debug( "Method to check whether same user is already present in user profile with inactive state started." );

        UserProfile userProfile = null;

        Map<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.USER_COLUMN, user );
        queries.put( CommonConstants.BRANCH_ID_COLUMN, branchId );
        List<UserProfile> userProfiles = userProfileDao.findByKeyValue( UserProfile.class, queries );

        if ( userProfiles == null || userProfiles.isEmpty() ) {
            LOG.debug( "User " + user.getFirstName() + " has never been assigned to branch id " + branchId + " earlier." );
        }

        else {
            userProfile = userProfiles.get( CommonConstants.INITIAL_INDEX );
            LOG.debug( "User " + user.getFirstName() + " is already present for branch " + branchId + " with status "
                + userProfile.getStatus() );
        }
        LOG.debug( "Method to check whether same user is already present in user profile with inactive state completed." );
        return userProfile;
    }


    /**
     * Method to update a user's status
     * 
     * @throws SolrException
     */
    @Override
    @Transactional
    public void updateUserStatus( long userId, int status ) throws InvalidInputException, SolrException
    {
        LOG.info( "Method updateUserStatus of user management services called for userId : " + userId + " and status :"
            + status );
        User user = getUserByUserId( userId );
        if ( user == null ) {
            throw new InvalidInputException( "No user present for the specified userId" );
        }
        user.setStatus( status );
        user.setModifiedBy( String.valueOf( userId ) );
        user.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        userDao.update( user );

        /**
         * Updating status of user into solr
         */
        solrSearchService.editUserInSolr( user.getUserId(), CommonConstants.STATUS_SOLR, String.valueOf( status ) );
        LOG.info( "Successfully completed method to update user status" );
    }


    /*
     * Method to assign a user to a particular branch.
     */
    @Transactional
    @Override
    public void assignUserToBranch( User admin, long userId, long branchId ) throws InvalidInputException, SolrException
    {

        if ( admin == null ) {
            throw new InvalidInputException( "No admin user present." );
        }
        LOG.info( "Method to assign user to a branch called for user : " + admin.getUserId() );
        User user = getUserByUserId( userId );

        if ( user == null ) {
            throw new InvalidInputException( "No user present for the specified userId" );
        }
        // Checking if admin can assign a user to the given branch.
        if ( !isAssigningAllowed( branchId, admin ) ) {
            throw new InvalidInputException( "Not authorized to assign user to branch " + branchId );
        }
        long regionId = 0l;

        /**
         * fetching region for the branch selected
         */
        Branch branch = branchDao.findById( Branch.class, branchId );
        regionId = branch.getRegion().getRegionId();

        Map<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.USER_COLUMN, user );
        queries.put( CommonConstants.BRANCH_ID_COLUMN, branchId );
        queries.put( CommonConstants.REGION_ID_COLUMN, regionId );

        List<UserProfile> userProfiles = userProfileDao.findByKeyValue( UserProfile.class, queries );
        UserProfile userProfile;
        if ( userProfiles == null || userProfiles.isEmpty() ) {
            // Create a new entry in UserProfile to map user to the branch.
            userProfile = createUserProfile( user, user.getCompany(), user.getEmailId(), userId, branchId, regionId,
                CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID, CommonConstants.IS_PRIMARY_FALSE,
                CommonConstants.DASHBOARD_STAGE, CommonConstants.STATUS_INACTIVE, String.valueOf( admin.getUserId() ),
                String.valueOf( admin.getUserId() ) );
        } else {
            userProfile = userProfiles.get( CommonConstants.INITIAL_INDEX );
            if ( userProfile.getStatus() == CommonConstants.STATUS_INACTIVE ) {
                userProfile.setStatus( CommonConstants.STATUS_ACTIVE );
                userProfile.setProfilesMaster( profilesMasterDao.findById( ProfilesMaster.class,
                    CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) );
                userProfile.setModifiedBy( String.valueOf( admin.getUserId() ) );
                userProfile.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
            }
        }
        //check if user profile will be primary or not
        int isPrimary = checkWillNewProfileBePrimary( userProfile, user.getUserProfiles() );
        userProfile.setIsPrimary( isPrimary );
        userProfileDao.saveOrUpdate( userProfile );

        if ( user.getIsAtleastOneUserprofileComplete() == CommonConstants.STATUS_INACTIVE ) {
            LOG.info( "Updating isAtleastOneProfileComplete as 1 for user : " + user.getFirstName() );
            user.setIsAtleastOneUserprofileComplete( CommonConstants.STATUS_ACTIVE );
            userDao.update( user );
        }
        setProfilesOfUser( user );
        solrSearchService.addUserToSolr( user );
        LOG.info( "Method to assign user to a branch finished for user : " + admin.getUserId() );
    }


    /*
     * Method to unassign a user from branch
     */

    @Override
    @Transactional
    public void unassignUserFromBranch( User admin, long userId, long branchId ) throws InvalidInputException
    {

        if ( admin == null ) {
            throw new InvalidInputException( "No admin user present." );
        }
        LOG.info( "Method to unassign user from a branch called for user : " + admin.getUserId() );
        User user = userDao.findById( User.class, userId );

        if ( user == null ) {
            throw new InvalidInputException( "No user present for the specified userId" );
        }

        Map<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.USER_COLUMN, user );
        queries.put( CommonConstants.BRANCH_ID_COLUMN, branchId );
        List<UserProfile> userProfiles = userProfileDao.findByKeyValue( UserProfile.class, queries );
        UserProfile userProfile = null;
        if ( userProfiles == null || userProfiles.isEmpty() ) {
            LOG.error( "No user profile present for the user with user Id " + userId + " with the branch " + branchId );
            throw new InvalidInputException( "No user profile present for the user with user Id " + userId
                + " with the branch " + branchId );
        } else {
            userProfile = userProfiles.get( CommonConstants.INITIAL_INDEX );
            if ( userProfile.getStatus() == CommonConstants.STATUS_ACTIVE ) {
                userProfile.setStatus( CommonConstants.STATUS_INACTIVE );
                userProfile.setModifiedBy( String.valueOf( admin.getUserId() ) );
                userProfile.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
            }
        }
        userProfileDao.saveOrUpdate( userProfile );
        LOG.info( "Method to unassign user from a branch finished for user : " + admin.getUserId() );

    }


    @Transactional
    @Override
    public List<User> getAllActiveUsers()
    {
        Map<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
        List<User> users = userDao.findByKeyValue( User.class, queries );
        return users;

    }


    /*
     * Method to update the given user as active or inactive.
     */
    @Transactional
    @Override
    public void updateUser( User admin, long userIdToUpdate, boolean isActive ) throws InvalidInputException
    {
        LOG.info( "Method to update a user called for user : " + userIdToUpdate );
        if ( admin == null ) {
            throw new InvalidInputException( "No admin user present." );
        }
        LOG.info( "Method to assign user to a branch called for user : " + admin.getUserId() );
        User user = userDao.findById( User.class, userIdToUpdate );

        if ( user == null ) {
            throw new InvalidInputException( "No user present for the specified userId" );
        }

        user.setModifiedBy( String.valueOf( admin.getUserId() ) );
        user.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        LOG.info( "Setting the user {} as {}", user.getFirstName(), isActive );
        if ( isActive ) {
            user.setStatus( CommonConstants.STATUS_ACTIVE );
        } else {
            user.setStatus( CommonConstants.STATUS_TEMPORARILY_INACTIVE );
        }

        userDao.update( user );

        LOG.info( "Method to update a user finished for user : " + userIdToUpdate );
    }


    /*
     * Method to update the given userprofile as active or inactive.
     */
    @Transactional
    @Override
    public void updateUserProfile( User admin, long profileIdToUpdate, int status ) throws InvalidInputException
    {
        LOG.info( "Method to update a user called for user profile: " + profileIdToUpdate );
        if ( admin == null ) {
            throw new InvalidInputException( "No admin user present." );
        }

        LOG.info( "Method to assign user to a branch called by user : " + admin.getUserId() );
        UserProfile userProfile = userProfileDao.findById( UserProfile.class, profileIdToUpdate );
        if ( userProfile == null ) {
            throw new InvalidInputException( "No user profile present for the specified userId" );
        }

        userProfile.setModifiedBy( String.valueOf( admin.getUserId() ) );
        userProfile.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        userProfile.setStatus( status );

        userProfileDao.update( userProfile );
        LOG.info( "Method to update a user finished for user : " + profileIdToUpdate );
    }


    @Transactional
    @Override
    public void removeUserProfile( long profileIdToDelete ) throws InvalidInputException, SolrException
    {
        LOG.info( "Method to delete a profile called for user profile: " + profileIdToDelete );

        UserProfile userProfile = userProfileDao.findById( UserProfile.class, profileIdToDelete );
        if ( userProfile == null ) {
            throw new InvalidInputException( "No user profile present for the specified profileId" );
        }

        long userId = userProfile.getUser().getUserId();
        userProfileDao.delete( userProfile );
        //update user in solr
        User user = userDao.findById( User.class, userId );
        solrSearchService.addUserToSolr( user );


        LOG.info( "Method to delete a profile finished for profile : " + profileIdToDelete );
    }


    /*
     * Method to update the given user as active based on profiles completed
     */
    // TODO
    @Override
    @Transactional
    public void updateUserProfilesStatus( User admin, long profileIdToUpdate ) throws InvalidInputException
    {
        LOG.info( "Method to update a user called for user: " + profileIdToUpdate );
        if ( admin == null ) {
            throw new InvalidInputException( "No admin user present." );
        }

        LOG.info( "Method to assign user to a branch called by user : " + admin.getUserId() );
        UserProfile userProfile = userProfileDao.findById( UserProfile.class, profileIdToUpdate );
        if ( userProfile == null ) {
            throw new InvalidInputException( "No user profile present for the specified userId" );
        }

        User user = userProfile.getUser();

        int noOfActiveProfiles = getAllUserProfilesForUser( user ).size();
        if ( noOfActiveProfiles > 0 ) {
            user.setIsAtleastOneUserprofileComplete( CommonConstants.STATUS_ACTIVE );
        } else {
            user.setIsAtleastOneUserprofileComplete( CommonConstants.STATUS_INACTIVE );
        }
        user.setModifiedBy( String.valueOf( admin.getUserId() ) );
        user.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );

        userDao.update( user );
        LOG.info( "Method to update a user finished for user : " + profileIdToUpdate );
    }


    @Override
    @Transactional
    public void updatePrimaryProfileOfUser( User user ) throws InvalidInputException
    {

        if ( user == null ) {
            LOG.error( "User object passed was be null" );
            throw new InvalidInputException( "Passed User object is null" );
        }
        LOG.debug( "method updatePrimaryProfileOfUser started for user with userid : " + user.getUserId() );
        List<UserProfile> userProfileList = userProfileDao.findByColumn( UserProfile.class, CommonConstants.USER_COLUMN, user );
        if ( userProfileList != null && !userProfileList.isEmpty() ) {
            UserProfile profileToMakePrimary = null;

            UserProfile agentProfileWithoutDefaultBranch = null;
            UserProfile agentProfileWithDefaultBranch = null;
            UserProfile branchAdminProfile = null;
            UserProfile regionAdminProfile = null;
            UserProfile companyAdminProfile = null;

            for ( UserProfile currentProfile : userProfileList ) {
                Branch branch = branchDao.findById( Branch.class, currentProfile.getBranchId() );
                if(currentProfile.getStatus() == CommonConstants.STATUS_ACTIVE){
                    if ( currentProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID
                        && branch.getIsDefaultBySystem() == CommonConstants.IS_DEFAULT_BY_SYSTEM_NO ) {
                        agentProfileWithoutDefaultBranch = currentProfile;
                    } else if ( currentProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID
                        && branch.getIsDefaultBySystem() == CommonConstants.IS_DEFAULT_BY_SYSTEM_YES ) {
                        agentProfileWithDefaultBranch = currentProfile;
                    } else if ( currentProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID ) {
                        branchAdminProfile = currentProfile;
                    } else if ( currentProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID ) {
                        regionAdminProfile = currentProfile;
                    } else if ( currentProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID ) {
                        companyAdminProfile = currentProfile;
                    }
                }
                
            }

            if ( agentProfileWithoutDefaultBranch != null ) {
                profileToMakePrimary = agentProfileWithoutDefaultBranch;
            } else if ( agentProfileWithDefaultBranch != null ) {
                profileToMakePrimary = agentProfileWithDefaultBranch;
            } else if ( branchAdminProfile != null ) {
                profileToMakePrimary = branchAdminProfile;
            } else if ( regionAdminProfile != null ) {
                profileToMakePrimary = regionAdminProfile;
            } else if ( companyAdminProfile != null ) {
                profileToMakePrimary = companyAdminProfile;
            }

            if(profileToMakePrimary == null){
                throw new InvalidInputException( "No user profile present for the specified userId" );
            }
            profileToMakePrimary.setIsPrimary( CommonConstants.IS_PRIMARY_TRUE );
            userProfileDao.update( profileToMakePrimary );

            LOG.debug( "method updatePrimaryProfileOfUser ended for user with userid : " + user.getUserId() );
        }
    }


    /**
     * Sends an email to user with the link to complete registration. User has to provide password
     * to set. Also, user can choose to change name.
     * 
     * @param emailId
     * @throws InvalidInputException
     */
    @Override
    public void sendRegistrationCompletionLink( String emailId, String firstName, String lastName, long companyId,
        String profileName, String loginName ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.info( "Method to send profile completion link to the user started." );

        Map<String, String> urlParams = new HashMap<String, String>();
        urlParams.put( CommonConstants.EMAIL_ID, emailId );
        urlParams.put( CommonConstants.FIRST_NAME, firstName );
        if ( lastName != null && !lastName.isEmpty() ) {
            urlParams.put( CommonConstants.LAST_NAME, lastName );
        }
        urlParams.put( CommonConstants.COMPANY, String.valueOf( companyId ) );

        LOG.info( "Generating URL" );
        String url = urlGenerator.generateUrl( urlParams, applicationBaseUrl + CommonConstants.SHOW_COMPLETE_REGISTRATION_PAGE );
        String name = firstName;
        if ( lastName != null && !lastName.isEmpty() ) {
            name = name + " " + lastName;
        }

        // Send reset password link to the user email ID
        if ( enableKafka.equals( CommonConstants.YES ) ) {
            emailServices.queueRegistrationCompletionEmail( url, emailId, name, profileName, loginName );
        } else {
            emailServices.sendRegistrationCompletionEmail( url, emailId, name, profileName, loginName );
        }
    }


    /*
     * Method to set properties of a user based upon active profiles available for the user.
     */
    @Override
    public void setProfilesOfUser( User user )
    {
        LOG.debug( "Method setProfilesOfUser() to set properties of a user based upon active profiles available for the user started." );
        List<UserProfile> userProfiles = user.getUserProfiles();
        for ( UserProfile userProfile : userProfiles ) {
            if ( userProfile.getStatus() == CommonConstants.STATUS_ACTIVE ) {
                switch ( userProfile.getProfilesMaster().getProfileId() ) {
                    case CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID:
                        user.setCompanyAdmin( true );
                        continue;
                    case CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID:
                        user.setRegionAdmin( true );
                        continue;
                    case CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID:
                        user.setBranchAdmin( true );
                        continue;
                    case CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID:
                        user.setAgent( true );
                        continue;
                    default:
                        LOG.error( "Invalid profile id found for user {} in setProfilesOfUser().", user.getFirstName() );
                }
            }
        }
        LOG.debug( "Method setProfilesOfUser() to set properties of a user based upon active profiles available for the user finished." );
    }


    /*
     * Method to fetch all the user profiles for the user
     */
    @Override
    @Transactional
    public List<UserProfile> getAllUserProfilesForUser( User user ) throws InvalidInputException
    {
        if ( user == null ) {
            LOG.error( "User object passed was be null" );
            throw new InvalidInputException( "User object passed was be null" );
        }
        LOG.info( "Method getAllUserProfilesForUser() called to fetch the list of user profiles for the user" );
        Map<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
        queries.put( CommonConstants.USER_COLUMN, user );
        queries.put( CommonConstants.COMPANY_COLUMN, user.getCompany() );
        List<UserProfile> userProfiles = userProfileDao.findByKeyValue( UserProfile.class, queries );
        LOG.info( "Method getAllUserProfilesForUser() finised successfully" );
        return userProfiles;
    }


    /**
     * Method to generate and send verification link
     * 
     * @param user
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    @Override
    public void sendVerificationLink( User user ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "Method sendVerificationLink of Registration service called" );
        String verificationUrl = null;

        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put( CommonConstants.EMAIL_ID, user.getEmailId() );
            params.put( CommonConstants.USER_ID, String.valueOf( user.getUserId() ) );

            LOG.debug( "Calling url generator to generate verification link" );
            verificationUrl = urlGenerator.generateUrl( params, applicationBaseUrl
                + CommonConstants.REQUEST_MAPPING_MAIL_VERIFICATION );
        } catch ( InvalidInputException e ) {
            throw new InvalidInputException( "Could not generate url for verification.Reason : " + e.getMessage(), e );
        }

        try {
            LOG.debug( "Calling email services to send verification mail for user " + user.getEmailId() );
            String profileName = getUserSettings( user.getUserId() ).getProfileName();

            if ( enableKafka.equals( CommonConstants.YES ) ) {
                emailServices.queueVerificationMail( verificationUrl, user.getEmailId(),
                    user.getFirstName() + " " + ( user.getLastName() != null ? user.getLastName() : "" ), profileName,
                    user.getLoginName() );
            } else {
                emailServices.sendVerificationMail( verificationUrl, user.getEmailId(),
                    user.getFirstName() + " " + ( user.getLastName() != null ? user.getLastName() : "" ), profileName,
                    user.getLoginName() );
            }
        } catch ( InvalidInputException e ) {
            throw new InvalidInputException( "Could not send mail for verification.Reason : " + e.getMessage(), e );
        }

        LOG.debug( "Method sendVerificationLink of Registration service finished" );
    }


    // JIRA: SS-27: By RM05: EOC
    /**
     * Method to invite user for registration,includes storing invite in db, calling services to
     * send mail
     * 
     * @param url
     * @param emailId
     * @param firstName
     * @param lastName
     * @throws UserAlreadyExistsException
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     * @throws NonFatalException
     */
    private void inviteUser( String url, String emailId, String firstName, String lastName, boolean isReinvitation )
        throws UserAlreadyExistsException, InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "Method inviteUser called with url : " + url + " emailId : " + emailId + " firstname : " + firstName
            + " lastName : " + lastName );

        String queryParam = extractUrlQueryParam( url );
        if ( !isReinvitation ) {
            if ( userWithEmailIdExists( emailId ) ) {
                throw new UserAlreadyExistsException( "user with specified email id already exists" );
            }
        }
        LOG.debug( "Calling method to store the registration invite" );
        storeCompanyAdminInvitation( queryParam, emailId );

        LOG.debug( "Calling email services to send registration invitation mail" );
        if ( enableKafka.equals( CommonConstants.YES ) ) {
            emailServices.queueRegistrationInviteMail( url, emailId, firstName, lastName );
        } else {
            emailServices.sendRegistrationInviteMail( url, emailId, firstName, lastName );
        }

        LOG.debug( "Method inviteUser finished successfully" );
    }


    /**
     * Method to extract the query parameter from encrypted url
     * 
     * @param url
     * @return queryParam
     * @throws InvalidInputException
     */
    private String extractUrlQueryParam( String url ) throws InvalidInputException
    {

        if ( url == null || url.isEmpty() ) {
            throw new InvalidInputException( "Url is found null or empty while extracting the query param" );
        }
        LOG.debug( "Getting query param from the encrypted url " + url );
        String queryParam = url.substring( url.indexOf( "q=" ) + 2, url.length() );

        LOG.debug( "Returning query param : " + queryParam );
        return queryParam;

    }


    /**
     * Method to store a registration invite, inserts a new invite if it doesn't exist otherwise
     * updates it with new timestamp values
     * 
     * @param queryParam
     * @param emailId
     * @throws NonFatalException
     */
    private void storeCompanyAdminInvitation( String queryParam, String emailId )
    {
        LOG.debug( "Method storeInvitation called with query param : " + queryParam + " and emailId : " + emailId );
        UserInvite userInvite = null;
        Company company = companyDao.findById( Company.class, CommonConstants.DEFAULT_COMPANY_ID );
        ProfilesMaster profilesMaster = profilesMasterDao.findById( ProfilesMaster.class,
            CommonConstants.PROFILES_MASTER_NO_PROFILE_ID );

        LOG.debug( "Checking if an invite already exists for queryParam :" + queryParam );
        userInvite = checkExistingInviteWithSameParams( queryParam );
        /**
         * if invite doesn't exist create a new one and store itF
         */
        if ( userInvite == null ) {
            userInvite = new UserInvite();
            userInvite.setCompany( company );
            userInvite.setProfilesMaster( profilesMaster );
            userInvite.setInvitationEmailId( emailId );
            userInvite.setInvitationParameters( queryParam );
            userInvite.setInvitationTime( new Timestamp( System.currentTimeMillis() ) );
            userInvite.setInvitationValidUntil( new Timestamp( CommonConstants.EPOCH_TIME_IN_MILLIS ) );
            userInvite.setStatus( CommonConstants.STATUS_ACTIVE );
            userInvite.setModifiedBy( CommonConstants.GUEST_USER_NAME );
            userInvite.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
            userInvite.setCreatedBy( CommonConstants.GUEST_USER_NAME );
            userInvite.setCreatedOn( new Timestamp( System.currentTimeMillis() ) );

            LOG.debug( "Inserting user invite" );
            userInvite = userInviteDao.save( userInvite );
        }
        /**
         * else update the timestamp of existing invite
         */
        else {
            userInvite.setStatus( CommonConstants.STATUS_ACTIVE );
            userInvite.setModifiedBy( CommonConstants.GUEST_USER_NAME );
            userInvite.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
            userInvite.setInvitationTime( new Timestamp( System.currentTimeMillis() ) );
            userInvite.setInvitationValidUntil( new Timestamp( CommonConstants.EPOCH_TIME_IN_MILLIS ) );

            LOG.debug( "Updating user invite" );
            userInviteDao.update( userInvite );
        }

        LOG.debug( "Method storeInvitation finished" );
    }


    // JIRA: SS-27: By RM05: BOC
    /*
     * This method takes the URL query parameter sent for authentication to a new user. Validates
     * the Registration URL and returns true if matches. Throws InvalidInputException, if URL does
     * not match.
     */
    private boolean validateCompanyRegistrationUrlParameters( String encryptedUrlParameter ) throws InvalidInputException
    {
        LOG.debug( "Method validateUrlParameters called." );
        List<UserInvite> userInvites = userInviteDao.findByUrlParameter( encryptedUrlParameter );
        if ( userInvites == null || userInvites.isEmpty() ) {
            LOG.error( "Exception caught while validating company registration URL parameters." );
            throw new InvalidInputException( "URL parameter provided is inappropriate." );
        }
        LOG.debug( "Method validateUrlParameters finished." );
        return true;
    }


    /**
     * Method to create a new user
     * 
     * @param company
     * @param password
     * @param emailId
     * @param displayName
     * @return
     */
    private User createUser( Company company, String password, String emailId, String firstName, String lastName,
        int isAtleastOneProfileComplete, int status, String createdBy )
    {
        LOG.debug( "Method createUser called for email-id : " + emailId + " and status : " + status );

        if ( lastName != null && !lastName.equals( "" ) ) {
            lastName = lastName.trim();
        }
        if ( firstName != null && !firstName.equals( "" ) ) {
            firstName = firstName.trim();
        }

        User user = new User();
        user.setCompany( company );
        user.setLoginName( emailId );
        user.setLoginPassword( password );
        user.setEmailId( emailId );
        user.setFirstName( firstName );
        user.setLastName( lastName );
        user.setSource( CommonConstants.DEFAULT_SOURCE_APPLICATION );
        user.setIsAtleastOneUserprofileComplete( isAtleastOneProfileComplete );
        user.setStatus( status );
        Timestamp currentTimestamp = new Timestamp( System.currentTimeMillis() );
        user.setLastLogin( currentTimestamp );
        user.setNumOfLogins( CommonConstants.ONE );
        user.setCreatedOn( currentTimestamp );
        user.setModifiedOn( currentTimestamp );
        user.setCreatedBy( createdBy );
        user.setModifiedBy( createdBy );
        LOG.debug( "Method createUser finished for email-id : " + emailId );
        return user;
    }


    /**
     * Method to check if an invite already exists for the same query parameters, if yes returns the
     * invite
     * 
     * @param queryParam
     * @return
     */
    private UserInvite checkExistingInviteWithSameParams( String queryParam )
    {
        LOG.debug( "Method checkExistingInviteWithSameParams started for queryparam : " + queryParam );
        List<UserInvite> userinvites = userInviteDao.findByUrlParameter( queryParam );
        UserInvite userInvite = null;
        if ( userinvites != null && !userinvites.isEmpty() ) {
            userInvite = userinvites.get( 0 );
        }
        LOG.debug( "Method checkExistingInviteWithSameParams finished.Returning : " + userInvite );
        return userInvite;
    }


    /**
     * Method to check whether a user with selected user name exists
     * 
     * @param userName
     * @return
     */
    @Transactional
    @Override
    public boolean userExists( String userName )
    {
        LOG.debug( "Method to check if user exists called for username : " + userName );
        boolean isUserPresent = false;
        try {
            userDao.getActiveUser( userName );
            isUserPresent = true;
        } catch ( NoRecordsFetchedException e ) {
            LOG.debug( "No user found with the user name " + userName );
            return isUserPresent;
        }
        LOG.debug( "Method to check if user exists finished for username : " + userName );
        return isUserPresent;
    }


    /*
     * Method to tell whether email id is already present in users table.
     */
    private boolean userWithEmailIdExists( String emailId )
    {
        LOG.debug( "Method isEmailIdAlreadyPresent started." );
        try {
            Map<String, Object> columns = new HashMap<>();
            columns.put( "emailId", emailId );
            columns.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
            List<User> users = userDao.findByKeyValue( User.class, columns );
            LOG.debug( "Method isEmailIdAlreadyPresent finished." );
            if ( users != null && !users.isEmpty() )
                return true;
        } catch ( DatabaseException databaseException ) {
            LOG.error( "Exception caughr while chnecking for email id in USERS." );
            return false;
        }
        return false;
    }


    // JIRA: SS-27: By RM05: EOC

    @Override
    public UserSettings getCanonicalUserSettings( User user, AccountType accountType ) throws InvalidInputException,
        NoRecordsFetchedException
    {
        if ( user == null ) {
            throw new InvalidInputException( "User is not set." );
        }
        if ( accountType == null ) {
            throw new InvalidInputException( "Invalid account type." );
        }

        UserSettings canonicalUserSettings = new UserSettings();
        AgentSettings agentSettings = null;
        Map<Long, OrganizationUnitSettings> branchesSettings = null;
        Map<Long, OrganizationUnitSettings> regionsSettings = null;
        LOG.info( "Getting the canonical settings for the user: " + user.toString() );

        // get the settings according to the profile and account type
        LOG.info( "Getting the company settings for the user" );
        OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user );
        canonicalUserSettings.setCompanySettings( companySettings );

        /**
         * fetching all settings for all account types
         */
        // get the agent settings. If the user is not an agent then there would agent
        // settings would be null
        LOG.debug( "Gettings agent settings" );
        agentSettings = getAgentSettingsForUserProfiles( user.getUserId() );
        canonicalUserSettings.setAgentSettings( agentSettings );

        // get the branches profiles and then resolve the parent organization unit.
        LOG.debug( "Gettings branch settings for user profiles" );
        branchesSettings = getBranchesSettingsForUserProfile( user.getUserProfiles(), agentSettings );
        canonicalUserSettings.setBranchSettings( branchesSettings );

        // get the regions profiles and then resolve the parent organization unit.
        LOG.debug( "Gettings region settings for user profiles" );
        regionsSettings = getRegionSettingsForUserProfile( user.getUserProfiles(), branchesSettings );
        canonicalUserSettings.setRegionSettings( regionsSettings );
        return canonicalUserSettings;
    }


    private Map<Long, OrganizationUnitSettings> getRegionSettingsForUserProfile( List<UserProfile> userProfiles,
        Map<Long, OrganizationUnitSettings> branchesSettings ) throws InvalidInputException
    {
        LOG.debug( "Getting regions settings for the user profile list" );
        Map<Long, OrganizationUnitSettings> regionsSettings = organizationManagementService
            .getRegionSettingsForUserProfiles( userProfiles );
        // if branches settings is not null, the resolve the settings of region associated with the
        // user's branch profiles
        if ( branchesSettings != null && branchesSettings.size() > 0 ) {
            LOG.debug( "Resolving regions settings for branch profiles" );
            for ( UserProfile userProfile : userProfiles ) {
                if ( userProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID ) {
                    // get the branch profile if it is not present in the branch settings
                    if ( userProfile.getRegionId() > 0l ) {
                        if ( regionsSettings == null ) {
                            // there were no branch profiles associated with the profile.
                            LOG.debug( "No regions associated with the profile" );
                            regionsSettings = new HashMap<Long, OrganizationUnitSettings>();
                        }
                        if ( !regionsSettings.containsKey( userProfile.getRegionId() ) ) {
                            OrganizationUnitSettings regionSetting = organizationManagementService
                                .getRegionSettings( userProfile.getBranchId() );
                            regionsSettings.put( userProfile.getRegionId(), regionSetting );
                        }
                    }
                }
            }
        }
        return regionsSettings;
    }


    private Map<Long, OrganizationUnitSettings> getBranchesSettingsForUserProfile( List<UserProfile> userProfiles,
        AgentSettings agentSettings ) throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "Getting branches settings for the user profile list" );
        Map<Long, OrganizationUnitSettings> branchesSettings = organizationManagementService
            .getBranchSettingsForUserProfiles( userProfiles );
        // if agent settings is not null, the resolve the settings of branch associated with the
        // user's agent profiles
        if ( agentSettings != null ) {
            LOG.debug( "Resolving branches settings for agent profiles" );
            for ( UserProfile userProfile : userProfiles ) {
                if ( userProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) {
                    // get the branch profile if it is not present in the branch settings
                    if ( userProfile.getBranchId() > 0l ) {
                        if ( branchesSettings == null ) {
                            // there were no branch profiles associated with the profile.
                            LOG.debug( "No branches associated with the profile" );
                            branchesSettings = new HashMap<Long, OrganizationUnitSettings>();
                        }
                        if ( !branchesSettings.containsKey( userProfile.getBranchId() ) ) {
                            BranchSettings branchSetting = organizationManagementService.getBranchSettings( userProfile
                                .getBranchId() );
                            branchesSettings.put( userProfile.getBranchId(), branchSetting.getOrganizationUnitSettings() );
                        }
                    }
                }
            }
        }
        return branchesSettings;
    }


    @Override
    public AgentSettings getUserSettings( long agentId ) throws InvalidInputException
    {
        LOG.info( "Getting agent settings for agent id: " + agentId );
        if ( agentId <= 0l ) {
            throw new InvalidInputException( "Invalid agent id for fetching user settings" );
        }
        AgentSettings agentSettings = organizationUnitSettingsDao.fetchAgentSettingsById( agentId );
        if ( agentSettings != null && agentSettings.getProfileStages() != null ) {
            agentSettings.setProfileStages( profileCompletionList.getProfileCompletionList( agentSettings.getProfileStages() ) );
        }
        return agentSettings;
    }


    @Override
    public AgentSettings getAgentSettingsForUserProfiles( long userId ) throws InvalidInputException
    {
        LOG.info( "Getting agent settings for user id: " + userId );
        AgentSettings agentSettings = getUserSettings( userId );
        return agentSettings;
    }


    /*
     * Method to check if current user is authorized to assign a user to the given branch.
     */
    private boolean isAssigningAllowed( long branchId, User admin )
    {
        LOG.debug( "Method isAssigningAllowed() started to check if current user is authorized to assign a user to the given branch" );
        Branch branch = branchDao.findById( Branch.class, branchId );
        if ( admin.isCompanyAdmin() )
            return true;
        for ( UserProfile adminProfile : admin.getUserProfiles() ) {
            if ( adminProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID
                && admin.isRegionAdmin() && branch.getRegion().getRegionId() == adminProfile.getRegionId() )
                return true;
            else if ( adminProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID
                && admin.isBranchAdmin() && branch.getBranchId() == adminProfile.getBranchId() )
                return true;
        }
        LOG.debug( "Method isAssigningAllowed() finsihed." );
        return false;
    }


    private Region fetchDefaultRegion( Company company ) throws InvalidInputException, NoRecordsFetchedException
    {

        LOG.debug( "Fetching the default region for company" );
        if ( company == null ) {
            LOG.error( "fetchDefaultRegion : Company parameter is null" );
            throw new InvalidInputException( "fetchDefaultRegion : Company parameter is null" );
        }

        Region defaultRegion = null;

        Map<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.COMPANY_COLUMN, company );
        queries.put( CommonConstants.IS_DEFAULT_BY_SYSTEM, CommonConstants.STATUS_ACTIVE );
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );

        LOG.debug( "Making database call to fetch default region" );
        List<Region> regions = regionDao.findByKeyValue( Region.class, queries );

        if ( regions == null || regions.size() != CommonConstants.MAX_DEFAULT_REGIONS ) {
            LOG.error( "No default regions found for company with id : " + company.getCompanyId() );
            throw new NoRecordsFetchedException( "No default regions found for company with id : " + company.getCompanyId() );
        }

        LOG.debug( "Default region exists." );
        defaultRegion = regions.get( CommonConstants.INITIAL_INDEX );

        LOG.debug( "Returning default region with id : " + defaultRegion.getRegionId() );
        return defaultRegion;
    }


    private Branch fetchDefaultBranch( Region region, Company company ) throws InvalidInputException, NoRecordsFetchedException
    {

        LOG.debug( "Fetching the default branch for region" );
        if ( region == null ) {
            LOG.error( "fetchDefaultBranch : Region parameter is null" );
            throw new InvalidInputException( "fetchDefaultBranch : Region parameter is null" );
        }
        if ( company == null ) {
            LOG.error( "fetchDefaultBranch : Company parameter is null" );
            throw new InvalidInputException( "fetchDefaultBranch : Company parameter is null" );
        }

        Branch defaultBranch = null;

        Map<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.COMPANY_COLUMN, company );
        queries.put( CommonConstants.REGION_COLUMN, region );
        queries.put( CommonConstants.IS_DEFAULT_BY_SYSTEM, CommonConstants.STATUS_ACTIVE );
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );

        LOG.debug( "Making database call to fetch default branch" );
        List<Branch> branches = branchDao.findByKeyValue( Branch.class, queries );

        if ( branches == null || branches.size() != CommonConstants.MAX_DEFAULT_BRANCHES ) {
            LOG.error( "No default branches found for region with id : " + region.getRegionId() );
            throw new NoRecordsFetchedException( "No default branches found for region with id : " + region.getRegionId() );
        }

        LOG.debug( "Default branch exists." );
        defaultBranch = branches.get( CommonConstants.INITIAL_INDEX );

        LOG.debug( "Returning default branch with id : " + defaultBranch.getBranchId() );
        return defaultBranch;
    }


    /**
     * Assign a user directly under the company.
     * 
     * @param admin
     * @param userId
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     * @throws SolrException
     */
    @Override
    public void assignUserToCompany( User admin, long userId ) throws InvalidInputException, NoRecordsFetchedException,
        SolrException
    {
        if ( admin == null ) {
            LOG.error( "assignUserToCompany : admin parameter is null" );
            throw new InvalidInputException( "assignUserToCompany : admin parameter is null" );
        }
        LOG.info( "Method to assign user to a branch called for user : " + admin.getUserId() );
        User user = getUserByUserId( userId );

        if ( user == null ) {
            LOG.error( "No records fetched for user with id : " + userId );
            throw new NoRecordsFetchedException( "No records fetched for user with id : " + userId );
        }
        // Checking if admin can assign a user to the given branch.
        if ( !admin.isCompanyAdmin() ) {
            LOG.error( "User : " + admin.getUserId() + " is not authorized to assign users to company "
                + admin.getCompany().getCompanyId() );
            throw new InvalidInputException( "User : " + admin.getUserId() + " is not authorized to assign users to company "
                + admin.getCompany().getCompanyId() );
        }

        // Fetch the default region for company
        LOG.debug( "Fetching default region for company with id :" + admin.getCompany().getCompanyId() );
        Region defaultRegion = fetchDefaultRegion( admin.getCompany() );

        // Fetch the default branch for the region
        LOG.debug( "Fetching default branch for region with id : " + defaultRegion.getRegionId() );
        Branch defaultBranch = fetchDefaultBranch( defaultRegion, admin.getCompany() );

        UserProfile userProfile;
        // Create a new entry in UserProfile to map user to the branch.
        LOG.debug( "Updating the User Profile table" );
        userProfile = createUserProfile( user, user.getCompany(), user.getEmailId(), CommonConstants.DEFAULT_AGENT_ID,
            defaultBranch.getBranchId(), defaultRegion.getRegionId(), CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID,
            CommonConstants.IS_PRIMARY_FALSE, CommonConstants.DASHBOARD_STAGE, CommonConstants.STATUS_INACTIVE,
            String.valueOf( admin.getUserId() ), String.valueOf( admin.getUserId() ) );

        //check if user profile will be primary or not
        int isPrimary = checkWillNewProfileBePrimary( userProfile, user.getUserProfiles() );
        userProfile.setIsPrimary( isPrimary );

        userProfileDao.saveOrUpdate( userProfile );
        LOG.debug( "UserProfile table updated" );

        if ( user.getIsAtleastOneUserprofileComplete() == CommonConstants.STATUS_INACTIVE ) {
            LOG.debug( "Updating isAtleastOneProfileComplete as 1 for user : " + user.getFirstName() );
            user.setIsAtleastOneUserprofileComplete( CommonConstants.STATUS_ACTIVE );
            userDao.update( user );
        }
        setProfilesOfUser( user );
        LOG.debug( "Adding user to solr" );
        solrSearchService.addUserToSolr( user );
        LOG.info( "Method to assign user to a company finished for user : " + admin.getUserId() );
    }


    /**
     * Checks if a user can add users to a particular region
     * 
     * @param admin
     * @param regionId
     * @return
     * @throws InvalidInputException
     */
    private boolean canAddUsersToRegion( User admin, long regionId ) throws InvalidInputException
    {

        LOG.debug( "Method canAddUsersToRegion() called to check if current user is authorized to assign a user to the given region" );

        if ( admin == null ) {
            LOG.error( "canAddUsersToRegion : admin parameter is null" );
            throw new InvalidInputException( "canAddUsersToRegion : admin parameter is null" );
        }
        if ( regionId <= 0 ) {
            LOG.error( "canAddUsersToRegion : regionId parameter is null" );
            throw new InvalidInputException( "canAddUsersToRegion : regionId parameter is null" );
        }

        LOG.debug( "Fetching the region from the database for region id : " + regionId );
        Region region = regionDao.findById( Region.class, regionId );
        if ( admin.isCompanyAdmin() ) {
            LOG.debug( "User is a corporate admin. returning true" );
            return true;
        }
        LOG.debug( "Checking the user profiles to see if he is a region admin" );
        for ( UserProfile adminProfile : admin.getUserProfiles() ) {
            if ( adminProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID
                && admin.isRegionAdmin() && region.getRegionId() == adminProfile.getRegionId() ) {
                LOG.debug( "User is region admin. Returning true" );
                return true;
            }
        }
        LOG.debug( "User not allowed to add users to region with id : " + regionId );
        return false;

    }


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
    @Override
    public void assignUserToRegion( User admin, long userId, long regionId ) throws InvalidInputException,
        NoRecordsFetchedException, SolrException
    {

        if ( admin == null ) {
            LOG.error( "assignUserToRegion : admin parameter is null" );
            throw new InvalidInputException( "assignUserToRegion : admin parameter is null" );
        }
        LOG.info( "Method to assign user to a branch called for user : " + admin.getUserId() );
        User user = getUserByUserId( userId );

        if ( user == null ) {
            LOG.error( "No records fetched for user with id : " + userId );
            throw new NoRecordsFetchedException( "No records fetched for user with id : " + userId );
        }
        if ( regionId <= 0 ) {
            LOG.error( "assignUserToRegion : regionId parameter is null" );
            throw new InvalidInputException( "assignUserToRegion : regionId parameter is null" );
        }
        // Checking if admin can assign a user to the given region.
        if ( !canAddUsersToRegion( admin, regionId ) ) {
            LOG.error( "User : " + admin.getUserId() + " is not authorized to assign users to region " + regionId );
            throw new InvalidInputException( "User : " + admin.getUserId() + " is not authorized to assign users to region "
                + regionId );
        }

        // Get the region from the database
        LOG.debug( "Fetching the region from the database for region id : " + regionId );
        Region region = regionDao.findById( Region.class, regionId );
        if ( region == null ) {
            LOG.error( "No records fetched for region with id : " + regionId );
            throw new NoRecordsFetchedException( "No records fetched for region with id : " + regionId );
        }

        // Fetch the default branch for the region
        LOG.debug( "Fetching the default branch for region with id : " + regionId );
        Branch defaultBranch = fetchDefaultBranch( region, admin.getCompany() );

        UserProfile userProfile;
        // Create a new entry in UserProfile to map user to the branch.
        LOG.debug( "Updating the User Profile table" );
        userProfile = createUserProfile( user, user.getCompany(), user.getEmailId(), CommonConstants.DEFAULT_AGENT_ID,
            defaultBranch.getBranchId(), region.getRegionId(), CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID,
            CommonConstants.IS_PRIMARY_FALSE, CommonConstants.DASHBOARD_STAGE, CommonConstants.STATUS_INACTIVE,
            String.valueOf( admin.getUserId() ), String.valueOf( admin.getUserId() ) );

        //check if user profile will be primary or not
        int isPrimary = checkWillNewProfileBePrimary( userProfile, user.getUserProfiles() );
        userProfile.setIsPrimary( isPrimary );

        userProfileDao.saveOrUpdate( userProfile );
        LOG.debug( "UserProfile table updated" );

        if ( user.getIsAtleastOneUserprofileComplete() == CommonConstants.STATUS_INACTIVE ) {
            LOG.debug( "Updating isAtleastOneProfileComplete as 1 for user : " + user.getFirstName() );
            user.setIsAtleastOneUserprofileComplete( CommonConstants.STATUS_ACTIVE );
            userDao.update( user );
        }
        setProfilesOfUser( user );
        LOG.debug( "Adding user to solr" );
        solrSearchService.addUserToSolr( user );
        LOG.info( "Method to assign user to a company finished for user : " + admin.getUserId() );

    }


    /**
     * Method to insert basic settings for a user
     */
    @Override
    public void insertAgentSettings( User user ) throws InvalidInputException
    {
        LOG.info( "Inserting agent settings. User id: " + user.getUserId() );
        AgentSettings agentSettings = new AgentSettings();
        agentSettings.setIden( user.getUserId() );
        agentSettings.setCreatedBy( user.getCreatedBy() );
        agentSettings.setCreatedOn( System.currentTimeMillis() );
        agentSettings.setModifiedBy( user.getModifiedBy() );
        agentSettings.setModifiedOn( System.currentTimeMillis() );
        agentSettings.setVertical( user.getCompany().getVerticalsMaster().getVerticalName() );

        // set the seo flag to true
        agentSettings.setSeoContentModified( true );
        agentSettings.setReviewCount( 0 );

        ContactDetailsSettings contactSettings = new ContactDetailsSettings();
        if ( user.getLastName() != null ) {
            contactSettings.setName( user.getFirstName() + " " + user.getLastName() );
            contactSettings.setFirstName( user.getFirstName() );
            contactSettings.setLastName( user.getLastName() );
        } else {
            contactSettings.setName( user.getFirstName() );
            contactSettings.setFirstName( user.getFirstName() );
        }
        if ( agentSettings.getSurvey_settings() == null ) {
            SurveySettings surveySettings = new SurveySettings();
            surveySettings.setShow_survey_above_score( CommonConstants.DEFAULT_AUTOPOST_SCORE );
            surveySettings.setAutoPostEnabled( true );
            agentSettings.setSurvey_settings( surveySettings );
        }

        MailIdSettings mail_ids = new MailIdSettings();
        mail_ids.setWork( user.getEmailId() );
        contactSettings.setMail_ids( mail_ids );

        agentSettings.setContact_details( contactSettings );

        String profileName = generateIndividualProfileName( user.getUserId(), contactSettings.getName(), user.getEmailId() );
        agentSettings.setProfileName( profileName );

        String profileUrl = utils.generateAgentProfileUrl( profileName );
        agentSettings.setProfileUrl( profileUrl );

        /**
         * setting profile url and name to user object for further operations
         */
        user.setProfileName( profileName );
        user.setProfileUrl( profileUrl );

        // Set default profile stages.
        agentSettings.setProfileStages( profileCompletionList.getDefaultProfileCompletionList( true ) );


        organizationUnitSettingsDao.insertAgentSettings( agentSettings );
        LOG.info( "Inserted into agent settings" );
    }


    /**
     * Method to generate a unique profile name from emailid and userId of individual
     * 
     * @param userId
     * @param emailId
     * @return
     * @throws InvalidInputException
     */
    public String generateIndividualProfileName( long userId, String name, String emailId ) throws InvalidInputException
    {
        LOG.info( "Method generateIndividualProfileName called for userId:" + userId + " and emailId:" + emailId );
        if ( emailId == null || emailId.isEmpty() ) {
            throw new InvalidInputException( "emailId is null or empty while generating agent profile name" );
        }
        String profileName = null;
        String input = null;
        if ( name != null && !name.isEmpty() ) {
            input = name;
        } else {
            input = emailId.trim().substring( 0, emailId.indexOf( "@" ) );
        }
        // profileName = emailId.trim().substring(0, emailId.indexOf("@"));
        profileName = utils.prepareProfileName( input );

        LOG.debug( "Checking uniqueness of profileName:" + profileName );
        OrganizationUnitSettings agentSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileName(
            profileName, MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
        /**
         * If a profile already exists for the profile name generated, append userId to make it
         * unique
         */
        if ( agentSettings != null ) {
            if ( agentSettings.getIden() != userId ) {
                profileName = utils.appendIdenToProfileName( profileName, userId );
            }
        }
        LOG.info( "Method generateIndividualProfileName finished successfully.Returning profileName: " + profileName );
        return profileName;
    }


    /**
     * Method to update profile name and url in agent settings
     * 
     * @param profileName
     * @param profileUrl
     * @param agentSettings
     */
    @Override
    public void updateProfileUrlInAgentSettings( String profileName, String profileUrl, AgentSettings agentSettings )
    {
        LOG.info( "Method to update profile name and url in AGENT SETTINGS started" );
        organizationUnitSettingsDao.updateParticularKeyAgentSettings( MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_NAME,
            profileName, agentSettings );
        organizationUnitSettingsDao.updateParticularKeyAgentSettings( MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_URL,
            profileUrl, agentSettings );
        LOG.info( "Method to update profile name and url in AGENT SETTINGS finished" );
    }


    /**
     * Method to update profile name and url in branch settings
     * 
     * @param profileName
     * @param profileUrl
     * @param branchSettings
     */
    @Override
    public void updateProfileUrlInBranchSettings( String profileName, String profileUrl, OrganizationUnitSettings branchSettings )
    {
        LOG.info( "Method to update profile name and url in BRANCH SETTINGS started" );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_NAME, profileName, branchSettings,
            MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_URL, profileUrl, branchSettings,
            MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );

        LOG.info( "Method to update profile name and url in BRANCH SETTINGS finished" );
    }


    /**
     * Method to update profile name and url in region settings
     * 
     * @param profileName
     * @param profileUrl
     * @param regionSettings
     */
    @Override
    public void updateProfileUrlInRegionSettings( String profileName, String profileUrl, OrganizationUnitSettings regionSettings )
    {
        LOG.info( "Method to update profile name and url in REGION SETTINGS started" );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_NAME, profileName, regionSettings,
            MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_URL, profileUrl, regionSettings,
            MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );

        LOG.info( "Method to update profile name and url in REGION SETTINGS finished" );
    }


    /**
     * Method to update profile name and url in company settings
     * 
     * @param profileName
     * @param profileUrl
     * @param companySettings
     */
    @Override
    public void updateProfileUrlInCompanySettings( String profileName, String profileUrl,
        OrganizationUnitSettings companySettings )
    {
        LOG.info( "Method to update profile name and url in COMPANY SETTINGS started" );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_NAME, profileName, companySettings,
            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_URL, profileUrl, companySettings,
            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        LOG.info( "Method to update profile name and url in COMPANY SETTINGS finished" );
    }


    /**
     * Method to check which all users can perform edit and set the boolean as true or false in user
     * objects
     */
    @Override
    public List<UserFromSearch> checkUserCanEdit( User admin, UserFromSearch adminFromSearch, List<UserFromSearch> users )
        throws InvalidInputException
    {
        LOG.info( "Method checkUserCanEdit called for admin:" + admin + " and adminUser:" + adminFromSearch );
        /**
         * Company admin : able to edit any user and himself
         */
        if ( admin.getIsOwner() == CommonConstants.IS_OWNER ) {
            for ( UserFromSearch user : users ) {
                user.setCanEdit( true );
            }
        }

        /**
         * Region admin : able to edit users only in his region
         */
        else if ( admin.getIsOwner() != CommonConstants.IS_OWNER && admin.isRegionAdmin() ) {
            for ( UserFromSearch user : users ) {
                boolean hasCommon = Collections.disjoint( adminFromSearch.getRegions(), user.getRegions() );
                if ( user.getIsOwner() != 1 ) {
                    user.setCanEdit( !hasCommon );
                }
            }
        }

        /**
         * Branch admin : able to edit users only in his office
         */
        else if ( admin.getIsOwner() != CommonConstants.IS_OWNER && admin.isBranchAdmin() ) {
            for ( UserFromSearch user : users ) {
                boolean hasCommon = Collections.disjoint( adminFromSearch.getBranches(), user.getBranches() );
                if ( user.getIsOwner() != 1 ) {
                    user.setCanEdit( !hasCommon );
                }
            }
        }
        LOG.info( "Method checkUserCanEdit executed successfully" );
        return users;
    }


    /**
     * Method to update user details on completing registration
     */
    @Override
    @Transactional
    public User updateUserOnCompleteRegistration( User user, String emailId, long companyId, String firstName, String lastName,
        String password ) throws SolrException, InvalidInputException
    {
        LOG.info( "Method updateUserOnCompleteRegistration called" );
        if ( user == null ) {
            throw new InvalidInputException( "User id null in updateUserOnCompleteRegistration" );
        }
        user.setFirstName( firstName );
        user.setLastName( lastName );
        user.setIsAtleastOneUserprofileComplete( CommonConstants.STATUS_ACTIVE );
        user.setStatus( CommonConstants.STATUS_ACTIVE );
        user.setModifiedBy( String.valueOf( user.getUserId() ) );
        user.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );

        /**
         * Set the new password
         */
        String encryptedPassword = encryptionHelper.encryptSHA512( password );
        user.setLoginPassword( encryptedPassword );

        userDao.saveOrUpdate( user );

        /**
         * Updating Name, profile name and profile url
         */
        LOG.debug( "Updating newly activated user {} to mongo", user.getUserId() );
        AgentSettings agentSettings = getAgentSettingsForUserProfiles( user.getUserId() );
        ContactDetailsSettings contactDetails = agentSettings.getContact_details();

        String name = user.getFirstName() + " " + ( user.getLastName() != null ? user.getLastName() : "" );
        contactDetails.setName( name );
        contactDetails.setFirstName( firstName );
        contactDetails.setLastName( lastName );

        String profileName = generateIndividualProfileName( user.getUserId(), name, emailId );
        String profileUrl = utils.generateAgentProfileUrl( profileName );
        agentSettings.setProfileName( profileName );
        agentSettings.setProfileUrl( profileUrl );
        organizationUnitSettingsDao.updateParticularKeyAgentSettings( MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_NAME,
            profileName, agentSettings );
        organizationUnitSettingsDao.updateParticularKeyAgentSettings( MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_URL,
            profileUrl, agentSettings );
        profileManagementService.updateAgentContactDetails( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION,
            agentSettings, contactDetails );
        LOG.debug( "Updated newly activated user {} to mongo", user.getUserId() );

        /**
         * setting new profile url and profile name in user object
         */
        user.setProfileName( profileName );
        user.setProfileUrl( profileUrl );

        LOG.debug( "Modifying user in solr" );
        solrSearchService.editUserInSolr( user.getUserId(), CommonConstants.STATUS_SOLR, String.valueOf( user.getStatus() ) );
        solrSearchService.editUserInSolr( user.getUserId(), CommonConstants.USER_FIRST_NAME_SOLR, user.getFirstName() );
        solrSearchService.editUserInSolr( user.getUserId(), CommonConstants.USER_LAST_NAME_SOLR,
            ( user.getLastName() != null ? user.getLastName() : "" ) );
        solrSearchService.editUserInSolr( user.getUserId(), CommonConstants.USER_DISPLAY_NAME_SOLR, user.getFirstName() + " "
            + ( user.getLastName() != null ? user.getLastName() : "" ) );
        solrSearchService.editUserInSolr( user.getUserId(), CommonConstants.PROFILE_URL_SOLR, profileUrl );
        solrSearchService.editUserInSolr( user.getUserId(), CommonConstants.PROFILE_NAME_SOLR, profileName );
        LOG.debug( "Successfully modified user detail in solr" );

        LOG.info( "Method updateUserOnCompleteRegistration executed successfully" );

        return user;
    }


    @Override
    public UserProfile updateSelectedProfile( User user, AccountType accountType, Map<Long, UserProfile> profileMap,
        String profileIdStr )
    {
        long profileId = 0;
        try {
            if ( profileIdStr != null && !profileIdStr.equals( "" ) ) {
                profileId = Long.parseLong( profileIdStr );
            } else {
                profileId = 0l;
            }
        } catch ( NumberFormatException e ) {
            LOG.error( "Number format exception occurred while parsing the profile id. Reason :" + e.getMessage(), e );
        }

        // Selecting and Setting Profile in session
        UserProfile selectedProfile = null;
        switch ( accountType ) {
            case COMPANY:
            case ENTERPRISE:
                if ( profileId != 0l ) {
                    selectedProfile = profileMap.get( profileId );
                }
                break;

            default:
                selectedProfile = user.getUserProfiles().get( CommonConstants.INITIAL_INDEX );
                for ( UserProfile profile : user.getUserProfiles() ) {
                    if ( profile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) {
                        selectedProfile = profile;
                        break;
                    }
                }
        }

        return selectedProfile;
    }


    @Transactional
    @Override
    public void updateUserLoginTimeAndNum( User user ) throws NonFatalException
    {
        LOG.info( "Updating users login time and number of logins for user: " + user.toString() );
        user.setLastLogin( new Timestamp( System.currentTimeMillis() ) );
        user.setNumOfLogins( user.getNumOfLogins() + 1 );
        userDao.update( user );
        LOG.info( "Updated user login time and number of login" );
    }


    @Transactional
    @Override
    public void updateUserCountModificationNotification( Company company ) throws InvalidInputException
    {
        if ( company == null ) {
            throw new InvalidInputException( "Company passed in updateUserCountModificationNotification is null" );
        }
        LOG.info( "Adding a record in user count modification notification table for company " + company.getCompany() );
        // search for the record in the table. it might be possible that record is already present.
        List<UsercountModificationNotification> userCountNotifications = userCountModificationDao.findByColumn(
            UsercountModificationNotification.class, CommonConstants.COMPANY_COLUMN, company );
        UsercountModificationNotification userCountNotification = null;
        if ( userCountNotifications != null && !userCountNotifications.isEmpty() ) {
            // record is already present. if the status is active do nothing. if status is under
            // processing, set it to active
            userCountNotification = userCountNotifications.get( CommonConstants.INITIAL_INDEX );
            if ( userCountNotification.getStatus() == CommonConstants.STATUS_UNDER_PROCESSING ) {
                // set the status to active and update
                userCountNotification.setStatus( CommonConstants.STATUS_ACTIVE );
                userCountModificationDao.update( userCountNotification );
            }
        } else {
            // no records present. add a record
            userCountNotification = new UsercountModificationNotification();
            userCountNotification.setCompany( company );
            userCountNotification.setStatus( CommonConstants.STATUS_ACTIVE );
            userCountNotification.setCreatedOn( new Timestamp( System.currentTimeMillis() ) );
            userCountNotification.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
            userCountModificationDao.save( userCountNotification );
        }

        LOG.info( "Finished adding a record in user count modification notification table for company " + company.getCompany() );
    }


    @Transactional
    @Override
    public boolean isValidApiKey( String apiSecret, String apiKey ) throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.info( "Validation api key for secret : " + apiSecret + " and key : " + apiKey );
        if ( apiSecret == null || apiSecret.isEmpty() ) {
            LOG.warn( "Api Secret is null" );
            throw new InvalidInputException( "Invalid api secret" );
        }
        if ( apiKey == null || apiKey.isEmpty() ) {
            LOG.warn( "Api key is null" );
            throw new InvalidInputException( "Invalid api key" );
        }
        boolean isApiKeyValid = false;
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put( CommonConstants.API_SECRET_COLUMN, apiSecret.trim() );
        queryMap.put( CommonConstants.API_KEY_COLUMN, apiKey.trim() );
        queryMap.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
        long count = apiKeyDao.findNumberOfRowsByKeyValue( UserApiKey.class, queryMap );
        LOG.debug( "Found " + count + " records from the api keys" );
        if ( count > 0l ) {
            LOG.info( "API key is valid" );
            isApiKeyValid = true;
        }
        return isApiKeyValid;
    }


    /*
     * Method to get company admin for the company given.
     */
    @Transactional
    @Override
    public User getCompanyAdmin( long companyId ) throws InvalidInputException
    {
        Map<String, Object> queries = new HashMap<>();
        try {
            queries.put( "company", companyDao.findById( Company.class, companyId ) );
            queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
            queries.put( "profilesMaster", getProfilesMasterById( CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID ) );
        } catch ( InvalidInputException e ) {
            LOG.error( "InvalidInputException caught in getCompanyAdmin()." );
            throw e;
        }
        List<UserProfile> profiles = userProfileDao.findByKeyValue( UserProfile.class, queries );
        if ( profiles != null && !profiles.isEmpty() ) {
            return profiles.get( CommonConstants.INITIAL_INDEX ).getUser();
        }
        return null;
    }


    @Override
    @Transactional
    public void updateUser( User user, Map<String, Object> map ) throws SolrException
    {
        LOG.info( "Method updateUser() started to update user." );
        userDao.merge( user );
        solrSearchService.editUserInSolrWithMultipleValues( user.getUserId(), map );
        LOG.info( "Method updateUser() finished to update user." );
    }


    // Moved user addition from Controller.
    @Override
    @Transactional ( rollbackFor = { NonFatalException.class, FatalException.class })
    public User inviteUser( User admin, String firstName, String lastName, String emailId ) throws InvalidInputException,
        UserAlreadyExistsException, UndeliveredEmailException, SolrException
    {
        User user = inviteNewUser( admin, firstName, lastName, emailId );
        LOG.debug( "Adding user {} to solr server.", user.getFirstName() );

        LOG.debug( "Adding newly added user {} to mongo", user.getFirstName() );
        insertAgentSettings( user );
        LOG.debug( "Added newly added user {} to mongo", user.getFirstName() );

        LOG.debug( "Adding newly added user {} to solr", user.getFirstName() );
        try {
            solrSearchService.addUserToSolr( user );
        } catch ( SolrException e ) {
            LOG.error( "SolrException caught in inviteUser(). Nested exception is ", e );
            organizationManagementService.removeOrganizationUnitSettings( user.getUserId(),
                MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
            throw e;
        }
        LOG.debug( "Added newly added user {} to solr", user.getFirstName() );

        return user;
    }


    @Override
    @Transactional ( rollbackFor = { NonFatalException.class, FatalException.class })
    public User addCorporateAdmin( String firstName, String lastName, String emailId, String confirmPassword,
        boolean isDirectRegistration ) throws InvalidInputException, UserAlreadyExistsException, UndeliveredEmailException,
        SolrException
    {

        User user = addCorporateAdminAndUpdateStage( firstName, lastName, emailId, confirmPassword, isDirectRegistration );
        LOG.debug( "Succesfully completed registration of user with emailId : " + emailId );

        LOG.debug( "Adding newly added user {} to mongo", user.getFirstName() );
        insertAgentSettings( user );
        LOG.debug( "Added newly added user {} to mongo", user.getFirstName() );

        LOG.debug( "Adding newly added user {} to solr", user.getFirstName() );
        try {
            solrSearchService.addUserToSolr( user );
        } catch ( SolrException e ) {
            LOG.error( "SolrException caught in addCorporateAdmin(). Nested exception is ", e );
            organizationManagementService.removeOrganizationUnitSettings( user.getUserId(),
                MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
            throw e;
        }
        LOG.debug( "Added newly added user {} to solr", user.getFirstName() );

        return user;
    }


    /***
     * 
     * @param userProfileNew
     * @param userProfiles
     * @return
     */
    private int checkWillNewProfileBePrimary( UserProfile userProfileNew, List<UserProfile> userProfiles )
    {

        LOG.debug( "Method checkWillNewProfileBePrimary called in UserManagementService for email id"
            + userProfileNew.getEmailId() );

        int isPrimary = CommonConstants.IS_PRIMARY_FALSE;
        boolean noOldProfileIsPrimary = true;

        if ( userProfiles != null && !userProfiles.isEmpty() ) {
            for ( UserProfile profile : userProfiles ) {

                if ( profile.getIsPrimary() == CommonConstants.IS_PRIMARY_TRUE ) {

                    LOG.debug( "An old primary profile founded for email id " + userProfileNew.getEmailId() );

                    noOldProfileIsPrimary = false;

                    boolean isOldProfileDefault = false;
                    boolean isOldProfileAdmin = false;
                    boolean isOldProfileAgent = false;
                    //get the value of all three variables
                    Branch branch = branchDao.findById( Branch.class, profile.getBranchId() );
                    if ( branch != null && branch.getIsDefaultBySystem() == CommonConstants.IS_DEFAULT_BY_SYSTEM_YES ) {
                        isOldProfileDefault = true;
                    }

                    if ( profile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) {
                        isOldProfileAgent = true;
                    } else {
                        isOldProfileAdmin = true;
                    }
                    //if old primary profile is default than remove primary from that and mark new profile as primary
                    if ( isOldProfileDefault ) {
                        LOG.debug( "Old primary profile has a default branch " );
                        //check if new profile is for default branch
                        Branch newProfileBranch = branchDao.findById( Branch.class, userProfileNew.getBranchId() );
                        //if new profile's branch is default than new profile will not be primary
                        if ( newProfileBranch != null
                            && newProfileBranch.getIsDefaultBySystem() == CommonConstants.IS_DEFAULT_BY_SYSTEM_YES
                            && userProfileNew.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) {

                            Region newProfileRegion = regionDao.findById( Region.class, userProfileNew.getRegionId() );

                            // if both branches are default and if new profiles region is not default than make new profile as primary
                            if ( newProfileRegion != null
                                && newProfileRegion.getIsDefaultBySystem() != CommonConstants.IS_DEFAULT_BY_SYSTEM_YES ) {
                                isPrimary = CommonConstants.IS_PRIMARY_TRUE;
                            } else {
                                isPrimary = CommonConstants.IS_PRIMARY_FALSE;
                            }
                            // if new profile's branch is not default than make new profile as primary and change old one  	
                        } else {
                            profile.setIsPrimary( CommonConstants.IS_PRIMARY_FALSE );
                            userProfileDao.update( profile );
                            isPrimary = CommonConstants.IS_PRIMARY_TRUE;
                        }

                    } else if ( isOldProfileAdmin ) {
                        LOG.debug( "Old primary profile is an admin profile of type "
                            + profile.getProfilesMaster().getProfile() );
                        //if old profile is for admin and new is for agent than remove primary from old and mark new profile as primary
                        if ( userProfileNew.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) {
                            profile.setIsPrimary( CommonConstants.IS_PRIMARY_FALSE );
                            userProfileDao.update( profile );
                            isPrimary = CommonConstants.IS_PRIMARY_TRUE;
                        } else {
                            isPrimary = CommonConstants.IS_PRIMARY_FALSE;
                        }
                        // if old profile is for agent and its not default than mark new profile as not primary
                    } else if ( isOldProfileAgent ) {
                        LOG.debug( "old primary profile is an agent profile" );
                        isPrimary = CommonConstants.IS_PRIMARY_FALSE;
                    }

                }
            }
            //if no old profile is there for user than make new profile as primary
        } else {
            LOG.debug( "No old profile found for user. New Profile will be primary" );
            isPrimary = CommonConstants.IS_PRIMARY_TRUE;
        }

        // if no old profile is primary than also new profile will be primary
        if ( noOldProfileIsPrimary ) {
            LOG.debug( "No old profile is primary for user so new profile will be primary" );
            isPrimary = CommonConstants.IS_PRIMARY_TRUE;
        }
        return isPrimary;
    }


    @Override
    @Transactional
    public String fetchAppropriateLogoUrlFromHierarchyForUser( long userId ) throws InvalidInputException,
        NoRecordsFetchedException, ProfileNotFoundException

    {

        String logoUrl = null;
        //get the appropriate logo url from hierarchy 
        Map<String, Long> hierarchyMap = null;
        Map<SettingsForApplication, OrganizationUnit> map = null;
        AgentSettings agentSettings = organizationUnitSettingsDao.fetchAgentSettingsById( userId );
        hierarchyMap = profileManagementService.getPrimaryHierarchyByAgentProfile( agentSettings );

        long companyId = hierarchyMap.get( CommonConstants.COMPANY_ID_COLUMN );
        long regionId = hierarchyMap.get( CommonConstants.REGION_ID_COLUMN );
        long branchId = hierarchyMap.get( CommonConstants.BRANCH_ID_COLUMN );
        try {
            map = profileManagementService.getPrimaryHierarchyByEntity( CommonConstants.AGENT_ID_COLUMN, userId );
            if ( map == null ) {
                LOG.error( "Unable to fetch primary profile for this user " );
                throw new FatalException( "Unable to fetch primary profile this user " + userId );
            }
        } catch ( InvalidSettingsStateException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        OrganizationUnit organizationUnit = map.get( SettingsForApplication.LOGO );
        if ( organizationUnit == OrganizationUnit.COMPANY ) {
            OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( companyId );
            logoUrl = companySettings.getLogo();
        } else if ( organizationUnit == OrganizationUnit.REGION ) {
            OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( regionId );
            logoUrl = regionSettings.getLogo();
        } else if ( organizationUnit == OrganizationUnit.BRANCH ) {
            OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( branchId );
            logoUrl = branchSettings.getLogo();
        } else if ( organizationUnit == OrganizationUnit.AGENT ) {
            logoUrl = agentSettings.getLogo();
        }

        return logoUrl;
    }


    @Transactional
    private void makeAProfileAsPrimaryOfAUser( long userId ) throws InvalidInputException, ProfileNotFoundException
    {
        LOG.debug( "method makeAProfileAsPrimaryOfAUser started with userid : " + userId );
        User user = userDao.findById( User.class, userId );
        if ( user == null ) {
            LOG.error( "User Is passed was null" );
            throw new InvalidInputException( "Passed User id is null" );
        }

        String errorMessage = "No primary user profile found for user " + user.getFirstName() + user.getLastName()
            + ", with user id : " + user.getUserId();
        try {
            emailServices.sendReportBugMailToAdmin( applicationAdminName, errorMessage, applicationAdminEmail );
        } catch ( UndeliveredEmailException e ) {
            LOG.error( "error while sending report bug mail to admin ", e );
        }

        //List<UserProfile> userProfileList = userProfileDao.findByColumn(UserProfile.class, CommonConstants.USER_COLUMN, user);
        List<UserProfile> userProfileList = user.getUserProfiles();
        if ( userProfileList == null || userProfileList.isEmpty() ) {
            throw new ProfileNotFoundException( "No profile found for user with id : " + user.getUserId() );
        }


        UserProfile profileToMakePrimary = null;

        UserProfile agentProfileWithoutDefaultBranch = null;
        UserProfile agentProfileWithDefaultBranch = null;
        UserProfile branchAdminProfile = null;
        UserProfile regionAdminProfile = null;
        UserProfile companyAdminProfile = null;

        for ( UserProfile currentProfile : userProfileList ) {
            Branch branch = branchDao.findById( Branch.class, currentProfile.getBranchId() );

            if ( currentProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID
                && branch.getIsDefaultBySystem() == CommonConstants.IS_DEFAULT_BY_SYSTEM_NO ) {
                agentProfileWithoutDefaultBranch = currentProfile;
            } else if ( currentProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID
                && branch.getIsDefaultBySystem() == CommonConstants.IS_DEFAULT_BY_SYSTEM_YES ) {
                agentProfileWithDefaultBranch = currentProfile;
            } else if ( currentProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID ) {
                branchAdminProfile = currentProfile;
            } else if ( currentProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID ) {
                regionAdminProfile = currentProfile;
            } else if ( currentProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID ) {
                companyAdminProfile = currentProfile;
            }
        }

        if ( agentProfileWithoutDefaultBranch != null ) {
            profileToMakePrimary = agentProfileWithoutDefaultBranch;
        } else if ( agentProfileWithDefaultBranch != null ) {
            profileToMakePrimary = agentProfileWithDefaultBranch;
        } else if ( branchAdminProfile != null ) {
            profileToMakePrimary = branchAdminProfile;
        } else if ( regionAdminProfile != null ) {
            profileToMakePrimary = regionAdminProfile;
        } else if ( companyAdminProfile != null ) {
            profileToMakePrimary = companyAdminProfile;
        }

        profileToMakePrimary.setIsPrimary( CommonConstants.IS_PRIMARY_TRUE );
        userProfileDao.update( profileToMakePrimary );
        LOG.debug( "marked a profile with profile id " + profileToMakePrimary.getUserProfileId()
            + "  as primary for user with user id " + user.getUserId() );

        LOG.debug( "method makeAProfileAsPrimaryOfAUser ended for user with userid : " + user.getUserId() );

    }


    @Override
    @Transactional
    public List<SettingsDetails> getSettingScoresById( long companyId, long regionId, long branchId )
    {
        LOG.info( "Inside method getSettingScoresById for company " + companyId + " region " + regionId + " branch " + branchId );
        return settingsSetterDao.getScoresById( companyId, regionId, branchId );
    }


    @Override
    @Transactional
    public Company getCompanyById( long id )
    {
        Company company = companyDao.findById( Company.class, id );
        return company;
    }


    @Override
    @Transactional
    public void updateCompany( Company company )
    {
        companyDao.update( company );

    }


    @Override
    @Transactional
    public void updateBranch( Branch branch )
    {
        branchDao.update( branch );

    }


    @Override
    @Transactional
    public void updateRegion( Region region )
    {
        regionDao.update( region );

    }


    @Override
    @Transactional
    public Region getRegionById( long id )
    {
        Region region = regionDao.findById( Region.class, id );
        return region;
    }


    @Override
    @Transactional
    public Branch getBranchById( long id )
    {
        Branch branch = branchDao.findById( Branch.class, id );
        return branch;
    }


    @Override
    @Transactional
    public Map<String, Long> getPrimaryUserProfileByAgentId( long entityId ) throws InvalidInputException,
        ProfileNotFoundException
    {
        LOG.debug( "method getPrimaryUserProfileByAgentId started with user id " + entityId );
        Map<String, Long> userProfileDetailMap = userProfileDao.findPrimaryUserProfileByAgentId( entityId );
        if ( userProfileDetailMap == null || userProfileDetailMap.isEmpty() ) {
            LOG.warn( "No primary profile found for the user with id " + entityId );
            makeAProfileAsPrimaryOfAUser( entityId );
            // after making a profile primary, again fetch the data
            userProfileDetailMap = userProfileDao.findPrimaryUserProfileByAgentId( entityId );
        }

        LOG.debug( "method getPrimaryUserProfileByAgentId ended with user id " + entityId );
        return userProfileDetailMap;
    }


}