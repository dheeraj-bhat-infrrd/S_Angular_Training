package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.ProfileCompletionList;
import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.dao.BranchDao;
import com.realtech.socialsurvey.core.dao.CompanyDao;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.SettingsSetterDao;
import com.realtech.socialsurvey.core.dao.SurveyDetailsDao;
import com.realtech.socialsurvey.core.dao.SurveyPreInitiationDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.dao.UserEmailMappingDao;
import com.realtech.socialsurvey.core.dao.UserInviteDao;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.BranchSettings;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.CompanyIgnoredEmailMapping;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.EmailAttachment;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.entities.MailIdSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.ProListUser;
import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.RemovedUser;
import com.realtech.socialsurvey.core.entities.SettingsDetails;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.SurveySettings;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserApiKey;
import com.realtech.socialsurvey.core.entities.UserEmailMapping;
import com.realtech.socialsurvey.core.entities.UserFromSearch;
import com.realtech.socialsurvey.core.entities.UserInvite;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.entities.UserSettings;
import com.realtech.socialsurvey.core.entities.UsercountModificationNotification;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.enums.OrganizationUnit;
import com.realtech.socialsurvey.core.enums.SettingsForApplication;
import com.realtech.socialsurvey.core.enums.SocialMediaConnectionStatus;
import com.realtech.socialsurvey.core.enums.SurveyErrorCode;
import com.realtech.socialsurvey.core.exception.DatabaseException;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.HierarchyAlreadyExistsException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.exception.UserAlreadyExistsException;
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.EmailUnsubscribeService;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserAssignmentException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UtilityService;
import com.realtech.socialsurvey.core.services.referral.ReferralService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.settingsmanagement.impl.InvalidSettingsStateException;
import com.realtech.socialsurvey.core.services.social.SocialManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.core.services.upload.FileUploadService;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.EncryptionHelper;
import com.realtech.socialsurvey.core.vo.SocialMediaVO;
import com.realtech.socialsurvey.core.vo.UserList;


/**
 * JIRA:SS-34 BY RM02 Implementation for User management services
 */
@DependsOn ( "generic")
@Service
public class UserManagementServiceImpl implements UserManagementService, InitializingBean
{

    private static final String NAME = "name";

    private static final Logger LOG = LoggerFactory.getLogger( UserManagementServiceImpl.class );
    private static Map<Integer, ProfilesMaster> profileMasters = new HashMap<Integer, ProfilesMaster>();
    private String companyAdminEnabled;
    private String adminEmailId;
    private String adminName;


    @Value ( "${FILE_DIRECTORY_LOCATION}")
    private String fileDirectoryLocation;

    @Autowired
    private URLGenerator urlGenerator;

    @Value ( "${APPLICATION_BASE_URL}")
    private String applicationBaseUrl;

    @Autowired
    private SurveyHandler surveyHandler;

    @Autowired
    private EmailServices emailServices;
    
    @Autowired
    private FileUploadService fileUploadService;

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
    private GenericDao<LicenseDetail, Long> licenseDetailsDao;

    @Autowired
    private CompanyDao companyDao;

    @Autowired
    private GenericDao<ProfilesMaster, Integer> profilesMasterDao;

    @Autowired
    private GenericDao<UsercountModificationNotification, Long> userCountModificationDao;

    @Autowired
    private BatchTrackerService batchTrackerService;

    @Autowired
    private Utils utils;

    @Resource
    @Qualifier ( "user")
    private UserDao userDao;

    @Resource
    @Qualifier ( "userProfile")
    private UserProfileDao userProfileDao;

    @Resource
    @Qualifier ( "userEmailMapping")
    private UserEmailMappingDao userEmailMappingDao;

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

    @Autowired
    private SocialManagementService socialManagementService;

    @Autowired
    private ReferralService referralService;

    @Autowired
    private SurveyPreInitiationDao surveyPreInitiationDao;

    @Autowired
    private GenericDao<CompanyIgnoredEmailMapping, Long> companyIgnoredEmailMappingDao;
    
    @Autowired
    SessionFactory sessionFactory;

    @Value ( "${PARAM_ORDER_TAKE_SURVEY_REMINDER}")
    private String paramOrderTakeSurveyReminder;

    @Value ( "${PARAM_ORDER_TAKE_SURVEY}")
    private String paramOrderTakeSurvey;

    @Value ( "${PARAM_ORDER_TAKE_SURVEY_SUBJECT}")
    String paramOrderTakeSurveySubject;

    @Value ( "${APPLICATION_LOGO_URL}")
    private String applicationLogoUrl;
    
    @Autowired
    private EmailUnsubscribeService unsubscribeService;


    /**
     * Method to get profile master based on profileId, gets the profile master from Map which is
     * pre-populated with afterPropertiesSet method
     */
    @Override
    @Transactional
    public ProfilesMaster getProfilesMasterById( int profileId ) throws InvalidInputException
    {
        LOG.debug( "Method getProfilesMasterById called for profileId : " + profileId );
        if ( profileId <= 0 ) {
            throw new InvalidInputException( "profile Id is not set for getting profile master" );
        }
        ProfilesMaster profilesMaster = null;
        if ( profileMasters.containsKey( profileId ) ) {
            profilesMaster = profileMasters.get( profileId );
        } else {
            throw new InvalidInputException( "No profile master detected for profileID : " + profileId );
        }
        LOG.debug( "Method getProfilesMasterById finished for profileId : " + profileId );
        return profilesMaster;
    }


    // Moved code from RegistrationServiceImpl By RM-05:BOC

    @Override
    @Transactional ( rollbackFor = { NonFatalException.class, FatalException.class })
    public void inviteCorporateToRegister( String firstName, String lastName, String emailId, boolean isReinvitation,
        String referralCode ) throws InvalidInputException, UndeliveredEmailException, NonFatalException
    {
        LOG.debug( "Inviting corporate to register. Details\t first name:" + firstName + "\t lastName: " + lastName
            + "\t email id: " + emailId );

        Map<String, String> urlParams = new HashMap<String, String>();
        urlParams.put( CommonConstants.FIRST_NAME, firstName );
        urlParams.put( CommonConstants.LAST_NAME, lastName );
        urlParams.put( CommonConstants.EMAIL_ID, emailId );
        urlParams.put( CommonConstants.CURRENT_TIMESTAMP, String.valueOf( System.currentTimeMillis() ) );
        urlParams.put( CommonConstants.UNIQUE_IDENTIFIER, generateUniqueIdentifier() );
        if ( referralCode != null && !referralCode.isEmpty() ) {
            urlParams.put( CommonConstants.REFERRAL_CODE, referralCode );
        }
        LOG.debug( "Generating URL" );
        String url = urlGenerator.generateUrl( urlParams,
            applicationBaseUrl + CommonConstants.REQUEST_MAPPING_SHOW_REGISTRATION );
        LOG.debug( "Sending invitation for registration" );
        inviteUser( url, emailId, firstName, lastName, isReinvitation );

        LOG.debug( "Successfully sent invitation to :" + emailId + " for registration" );
    }


    @Override
    @Transactional ( rollbackFor = { NonFatalException.class, FatalException.class })
    public void validateAndInviteCorporateToRegister( String firstName, String lastName, String emailId, boolean isReinvitation,
        String referralCode ) throws InvalidInputException, UserAlreadyExistsException, NonFatalException
    {
        LOG.debug( "Validating and inviting corporate to register. Details\t first name:" + firstName + "\t lastName: "
            + lastName + "\t email id: " + emailId );
        LOG.debug( "Validating form elements" );
        validateFormParametersForInvitation( firstName, lastName, emailId );
        LOG.debug( "Form parameters validation passed for firstName: " + firstName + " lastName: " + lastName + " and emailID: "
            + emailId );
        // validating referral code if exists
        if ( referralCode != null && !referralCode.isEmpty() ) {
            if ( !referralService.validateReferralCode( referralCode ) ) {
                LOG.warn( "Invalid referral code" );
                throw new InvalidInputException( "Could not find referral code " + referralCode );
            }
        }
        // check if email id already exists
        if ( userExists( emailId.trim() ) ) {
            LOG.warn( emailId + " is already present" );
            throw new UserAlreadyExistsException( "Email address " + emailId + " already exists." );
        }

        // send verification mail and then redirect to index page
        LOG.debug( "Calling service for sending the registration invitation" );
        inviteCorporateToRegister( firstName, lastName, emailId, false, referralCode );
        LOG.debug( "Service for sending the registration invitation excecuted successfully" );
    }


    @Override
    public void validateFormParametersForInvitation( String firstName, String lastName, String emailId )
        throws InvalidInputException
    {
        LOG.debug( "Validating invitation form parameters" );

        // check if first name is null or empty and only contains alphabets
        if ( firstName == null || firstName.isEmpty() || !firstName.matches( CommonConstants.FIRST_NAME_REGEX ) ) {
            throw new InvalidInputException( "Firstname is invalid in registration",
                DisplayMessageConstants.INVALID_FIRSTNAME );
        }

        // check if last name only contains alphabets
        if ( lastName != null && !lastName.isEmpty() ) {
            if ( !( lastName.matches( CommonConstants.LAST_NAME_REGEX ) ) ) {
                throw new InvalidInputException( "Last name is invalid in registration",
                    DisplayMessageConstants.INVALID_LASTNAME );
            }
        }

        // check if email Id isEmpty, null or whether it matches the regular expression or not
        if ( emailId == null || emailId.isEmpty() || !organizationManagementService.validateEmail( emailId ) ) {
            throw new InvalidInputException( "Email address is invalid in registration",
                DisplayMessageConstants.INVALID_EMAILID );
        }
        LOG.debug( "Invitation form parameters validated successfully" );
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
        LOG.debug( "Method validateRegistrationUrl() called " );

        Map<String, String> urlParameters = urlGenerator.decryptParameters( encryptedUrlParameter );
        validateCompanyRegistrationUrlParameters( encryptedUrlParameter );

        LOG.debug( "Method validateRegistrationUrl() finished " );
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
        // check added to include/exclude link expiry logic.
        if ( days > 0 ) {
            long milliseconds = days * 24 * 60 * 60 * 1000;
            if ( systemTimestamp - userTimestamp > milliseconds ) {
                linkExpired = true;
            }
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
        LOG.debug( "Method to add corporate admin called for emailId : " + emailId );
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
        User user = createUser( company, encryptedPassword, emailId, firstName, lastName, CommonConstants.STATUS_ACTIVE, status,
            CommonConstants.ADMIN_USER_NAME );
        user = userDao.save( user );
        //delete the record if marked as ignored
        deleteIgnoredEmailMapping( user.getEmailId() );
        //update the corrupted record for newly registered user's email id
        surveyPreInitiationDao.updateAgentIdOfPreInitiatedSurveysByAgentEmailAddress( user, user.getLoginName() );


        LOG.debug(
            "Creating user profile for :" + emailId + " with profile completion stage : " + CommonConstants.ADD_COMPANY_STAGE );
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
        LOG.debug( "Successfully executed method to add corporate admin for emailId : " + emailId );
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
        LOG.debug( "Mehtod updateProfileCompletionStage called for profileCompletionStage : " + profileCompletionStage
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
        LOG.debug( "Mehtod updateProfileCompletionStage finished for profileCompletionStage : " + profileCompletionStage );
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
        LOG.debug( "Method to verify account called for encryptedUrlParams" );
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
        LOG.debug( "Successfully completed method to verify account" );
    }


    /**
     * Method to add a new user into a company. Admin sends the invite to user for registering.
     * @throws NoRecordsFetchedException 
     */
    @Transactional
    @Override
    public User inviteUserToRegister( User admin, String firstName, String lastName, String emailId, boolean holdSendingMail,
        boolean sendMail, boolean isForHierarchyUpload, boolean isAddedByRealtechOrSSAdmin ) throws InvalidInputException, UserAlreadyExistsException, UndeliveredEmailException, NoRecordsFetchedException
    {
        if ( firstName == null || firstName.isEmpty() ) {
            throw new InvalidInputException( "First name is either null or empty in inviteUserToRegister()." );
        }
        if ( emailId == null || emailId.isEmpty() ) {
            throw new InvalidInputException( "Email Id is either null or empty in inviteUserToRegister()." );
        }
        LOG.debug( "Method to add a new user, inviteUserToRegister() called for email id : " + emailId );

        if ( userExists( emailId ) ) {
            throw new UserAlreadyExistsException( "User with User ID : " + emailId + " already exists" );
        }

        User user = createUser( admin.getCompany(), null, emailId, firstName, lastName, CommonConstants.STATUS_ACTIVE,
            CommonConstants.STATUS_NOT_VERIFIED, CommonConstants.ADMIN_USER_NAME );
        user = userDao.save( user );
        //delete the record if marked as ignored
        deleteIgnoredEmailMapping( user.getEmailId() );
        //update the corrupted record for newly registered user's email id
        surveyPreInitiationDao.updateAgentIdOfPreInitiatedSurveysByAgentEmailAddress( user, user.getLoginName() );

        LOG.debug( "Inserting agent settings for the user:" + user );
        insertAgentSettings( user );
        
        // send user addition mail
        if( !isForHierarchyUpload && !isAddedByRealtechOrSSAdmin ) {
            organizationManagementService.sendUserAdditionMail( admin, user );
        }

        String profileName = getUserSettings( user.getUserId() ).getProfileName();
        if ( sendMail ) {
            sendRegistrationCompletionLink( emailId, firstName, lastName, admin.getCompany().getCompanyId(), profileName,
                user.getLoginName(), holdSendingMail );
        }
        LOG.debug( "Method to add a new user, inviteUserToRegister finished for email id : " + emailId );
        return user;
    }


    /*
     * Method to invite new user
     */
    @Override
    @Transactional
    public User inviteNewUser( User admin, String firstName, String lastName, String emailId )
        throws InvalidInputException, UserAlreadyExistsException, UndeliveredEmailException
    {
        if ( firstName == null || firstName.isEmpty() ) {
            throw new InvalidInputException( "First name is either null or empty in inviteUserToRegister()." );
        }
        if ( emailId == null || emailId.isEmpty() ) {
            throw new InvalidInputException( "Email Id is either null or empty in inviteUserToRegister()." );
        }
        LOG.debug( "Method to add a new user, inviteNewUser() called for email id : " + emailId );

        if ( userExists( emailId ) ) {
            throw new UserAlreadyExistsException( "User with User ID : " + emailId + " already exists" );
        }

        User user = createUser( admin.getCompany(), null, emailId, firstName, lastName, CommonConstants.STATUS_INACTIVE,
            CommonConstants.STATUS_NOT_VERIFIED, String.valueOf( admin.getUserId() ) );
        user = userDao.save( user );
        //delete the record if marked as ignored
        deleteIgnoredEmailMapping( user.getEmailId() );
        //update the corrupted record for newly registered user's email id
        surveyPreInitiationDao.updateAgentIdOfPreInitiatedSurveysByAgentEmailAddress( user, user.getLoginName() );

        LOG.debug( "Method to add a new user, inviteNewUser() finished for email id : " + emailId );
        return user;
    }


    /*
     * Method to deactivate an existing user.
     */
    @Transactional
    @Override
    public void removeExistingUser( User admin, long userIdToRemove, int status ) throws InvalidInputException
    {
        if ( admin == null ) {
            throw new InvalidInputException( "Admin user is null in deactivateExistingUser" );
        }
        if ( userIdToRemove <= 0l ) {
            throw new InvalidInputException( "User id is invalid in deactivateExistingUser" );
        }

        LOG.debug( "Method to deactivate user " + userIdToRemove + " called." );
        User userToBeDeactivated = userDao.findById( User.class, userIdToRemove );
        if ( userToBeDeactivated == null ) {
            throw new InvalidInputException( "No user found in databse for user id : " + userIdToRemove );
        }

        userToBeDeactivated.setLoginName( userToBeDeactivated.getLoginName() + "_" + System.currentTimeMillis() );
        userToBeDeactivated.setStatus( status );
        userToBeDeactivated.setModifiedBy( String.valueOf( admin.getUserId() ) );
        userToBeDeactivated.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );

        LOG.debug( "Deactivating user " + userToBeDeactivated.getFirstName() );
        userDao.update( userToBeDeactivated );

        // Create an entry into the RemovedUser table for keeping historical records of users.
        RemovedUser removedUser = new RemovedUser();
        removedUser.setCompany( userToBeDeactivated.getCompany() );
        removedUser.setUser( userToBeDeactivated );
        removedUser.setCreatedBy( String.valueOf( admin.getUserId() ) );
        removedUser.setCreatedOn( new Timestamp( System.currentTimeMillis() ) );
        removedUserDao.save( removedUser );

        //Deactivate all email addresses for the user
        List<UserEmailMapping> userEmailMappings = userToBeDeactivated.getUserEmailMappings();
        for ( UserEmailMapping userEmailMapping : userEmailMappings ) {
            userEmailMapping.setStatus( status );
            userEmailMappingDao.update( userEmailMapping );
        }

        // Marks all the user profiles for given user as inactive.
        userProfileDao.deactivateAllUserProfilesForUser( admin, userToBeDeactivated, status );

        //update profile url in mongo if needed
        organizationManagementService.updateProfileUrlAndStatusForDeletedEntity( CommonConstants.AGENT_ID_COLUMN,
            userIdToRemove );

        //Disconnect social connections(ensure that social connections history is updated)
        socialManagementService.disconnectAllSocialConnections( CommonConstants.AGENT_ID_COLUMN, userIdToRemove );

        //Delete entries from SurveyPreInitiation table
        surveyPreInitiationDao.deletePreInitiatedSurveysForAgent( userIdToRemove, status );

        //Delete entries from the Survey Details Collection
        surveyDetailsDao.deleteIncompleteSurveysForAgent( userIdToRemove );

        LOG.debug( "Method to deactivate user " + userToBeDeactivated.getFirstName() + " finished." );
    }


    /*
     * Method to reactivate a deleted user.
     */
    @Transactional
    @Override
    public void restoreDeletedUser( long userId, boolean restoreSocial ) throws InvalidInputException, SolrException
    {
        if ( userId <= 0l ) {
            throw new InvalidInputException( "User id is invalid in restoreDeletedUser" );
        }

        LOG.debug( "Method to activate user " + userId + " called." );

        if ( userId <= 0l ) {
            throw new InvalidInputException( "Invalid userId" );
        }
        User user = getUserByUserId( userId );
        if ( user == null ) {
            throw new InvalidInputException( "No user having userId : " + userId + " exists." );
        }
        if ( user.getStatus() != CommonConstants.STATUS_INACTIVE ) {
            throw new InvalidInputException( "User with userId : " + userId + " already exists." );
        }
        //Check if any user has emailId = user's emailId.
        try {
            User userWithSameEmail = getUserByEmailAddress( user.getEmailId() );
            if ( user.getUserId() != userWithSameEmail.getUserId() ) {
                throw new InvalidInputException(
                    "Another User exists with the same Email ID. UserId : " + userWithSameEmail.getUserId() );
            }
        } catch ( NoRecordsFetchedException e1 ) {
            LOG.debug( "No existing user found. Restoring." );
        }

        //Start restoring the user
        //Set status = 1 if password field is present, 2 otherwise, and loginId = emailId
        boolean isVerified = true;
        if ( user.getLoginPassword() == null || user.getLoginPassword().isEmpty() ) {
            user.setStatus( CommonConstants.STATUS_NOT_VERIFIED );
            isVerified = false;
        } else {
            user.setStatus( CommonConstants.STATUS_ACTIVE );
        }
        user.setLoginName( user.getEmailId() );
        updateUser( user );

        //Set the status of all user profiles for that user as 1
        userProfileDao.activateAllUserProfilesForUser( user );

        //update the mismatched record for  restored user's email id
        surveyPreInitiationDao.updateAgentIdOfPreInitiatedSurveysByAgentEmailAddress( user, user.getLoginName() );
        
        //Restore mapped emailIds if possible
        List<UserEmailMapping> userEmailMappings = userEmailMappingDao.findByColumn( UserEmailMapping.class,
            CommonConstants.USER_COLUMN, user );
        for ( UserEmailMapping userEmailMapping : userEmailMappings ) {
            try {
                User foundUser = getUserByEmailAddress( userEmailMapping.getEmailId() );
                LOG.error( "An active user : " + foundUser.getUserId() + " was found having emailId : "
                    + userEmailMapping.getEmailId() + ".Could not restore" );
            } catch ( NoRecordsFetchedException e ) {
                userEmailMapping.setStatus( CommonConstants.STATUS_ACTIVE );
                userEmailMappingDao.update( userEmailMapping );
            }
        }

        //Remove entry from RemovedUser table

        List<RemovedUser> entriesToDelete = removedUserDao.findByColumn( RemovedUser.class, CommonConstants.USER_COLUMN, user );
        if ( entriesToDelete != null && !( entriesToDelete.isEmpty() ) ) {
            for ( RemovedUser removedUser : entriesToDelete ) {
                removedUserDao.delete( removedUser );
            }
        }

        //Update Agent settings
        AgentSettings agentSettings = getUserSettings( userId );


        //Update profileName and profileUrl if possible
        String profileNameForUpdate = null;
        String profileName = agentSettings.getProfileName();
        String newProfileName = null;
        String fullName = null;
        
        
        
        if( ! StringUtils.isEmpty( user.getFirstName()) ){
            fullName = user.getFirstName().toLowerCase();
            if( ! StringUtils.isEmpty( user.getLastName() )){
                fullName  +=  " " + user.getLastName().toLowerCase();
            }
        }
             
        newProfileName = generateIndividualProfileName( user.getUserId(), fullName, user.getEmailId() );
        
        user.setProfileName( profileName );
        user.setProfileUrl( "/" + profileName );
        if ( !profileName.equals( newProfileName ) ) {
            OrganizationUnitSettings agentWithProfile = organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileName(
                newProfileName, MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
            if ( agentWithProfile == null ) {
                profileNameForUpdate = newProfileName;
                user.setProfileName( newProfileName );
                user.setProfileUrl( "/" + newProfileName );
            }
        }

        organizationUnitSettingsDao.updateAgentSettingsForUserRestoration( profileNameForUpdate, agentSettings, restoreSocial,
            isVerified );

        //Add user to Solr
        solrSearchService.addUserToSolr( user );

        //Update review count for user
        List<Long> userIdList = new ArrayList<Long>();
        userIdList.add( userId );

        Map<Long, Integer> agentsReviewCount;
        try {
            agentsReviewCount = batchTrackerService.getReviewCountForAgents( userIdList );
        } catch ( ParseException e ) {
            LOG.error( "Error while parsing the data fetched from mongo for survey count", e );
            throw new InvalidInputException(
                "Error while parsing the data fetched from mongo for survey count. Reason :" + e.getMessage() );
        }
        if ( agentsReviewCount != null && !agentsReviewCount.isEmpty() )
            solrSearchService.updateCompletedSurveyCountForMultipleUserInSolr( agentsReviewCount );

        LOG.debug( "Method to reactivate user " + user.getFirstName() + " finished." );
    }


    /*
     * Method to get user with login name of a company
     */
    @Transactional
    @Override
    public User getUserByLoginName( User admin, String loginName ) throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "Method to fetch list of users on the basis of email id is called." );

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
        LOG.debug( "Method to fetch list of users on the basis of email id is finished." );
        return users.get( CommonConstants.INITIAL_INDEX );
    }


    /**
     * Method to get user by email address
     * @param emailId
     * @return
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     */
    @Transactional
    @Override
    public User getUserByEmailAddress( String emailId ) throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "Method to fetch user having emailId : " + emailId + " started." );
        if ( emailId == null || emailId.isEmpty() ) {
            throw new InvalidInputException( "Email id is null or empty" );
        }
        Map<String, Object> queries = new HashMap<String, Object>();
        queries.put( CommonConstants.EMAIL_ID, emailId );
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
        List<UserEmailMapping> userEmailMappings = userEmailMappingDao.findByKeyValue( UserEmailMapping.class, queries );
        if ( userEmailMappings == null || userEmailMappings.isEmpty() ) {
            User user = userDao.getActiveUser( emailId );
            return user;
        } else {
            return userEmailMappings.get( CommonConstants.INITIAL_INDEX ).getUser();
        }
    }


    /**
     * Method to get user by email address and companyId from userEmailMapping
     * @param emailId
     * @return
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     */
    @Transactional
    @Override
    public User getUserByEmailAndCompanyFromUserEmailMappings( Company company, String emailId )
        throws InvalidInputException, NoRecordsFetchedException
    {
    	
    		if ( emailId == null || emailId.isEmpty() ) {
            throw new InvalidInputException( "Email id is null or empty" );
        }
    		if ( company == null ) {
                throw new InvalidInputException( "Invalid value passed for comapany" );
            }
        LOG.debug( "Method getUserByEmailAndCompanyFromUserEmailMappings having emailId : {} companyId:{} started.", emailId,
        		company.getCompanyId() );
        
        Map<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.EMAIL_ID, emailId );
        queries.put( CommonConstants.COMPANY + "." + CommonConstants.COMPANY_ID_COLUMN, company.getCompanyId() );
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
        List<UserEmailMapping> userEmailMappings = userEmailMappingDao.findByKeyValue( UserEmailMapping.class, queries );
        LOG.info( "List of UserEmailMapping got by getUserByEmailAddress: {} ", userEmailMappings );
        if ( userEmailMappings == null || userEmailMappings.isEmpty() ) {
            User user = userDao.getActiveUserByEmailAndCompany(emailId, company);
            return user;
        } else {
            return userEmailMappings.get( CommonConstants.INITIAL_INDEX ).getUser();
        }
    }


    // Method to return user with provided email and company
    @Transactional
    @Override
    public User getUserByEmailAndCompany( long companyId, String emailId )
        throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "Method getUserByEmailAndCompany() called from UserManagementService" );

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

        LOG.debug( "Method getUserByEmailAndCompany() finished from UserManagementService" );
        return users.get( CommonConstants.INITIAL_INDEX );
    }


    // Method to return user with provided email
    @Transactional
    @Override
    public User getUserByEmail( String emailId ) throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "Method getUserByEmail() called from UserManagementService" );

        if ( emailId == null || emailId.isEmpty() ) {
            throw new InvalidInputException( "Email id is null or empty in getUserByEmailAndCompany()" );
        }

        Map<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.LOGIN_NAME, emailId );

        List<User> users = userDao.findByKeyValue( User.class, queries );
        if ( users == null || users.isEmpty() ) {
            throw new NoRecordsFetchedException( "No users found with the login name : {}", emailId );
        }

        LOG.debug( "Method getUserByEmail() finished from UserManagementService" );
        return users.get( CommonConstants.INITIAL_INDEX );
    }


    @Transactional
    @Override
    public List<User> getUsersBySimilarEmailId( User admin, String emailId ) throws InvalidInputException
    {
        LOG.debug( "Method to fetch list of users on the basis of email id is called." );

        if ( admin == null ) {
            throw new InvalidInputException( "Admin user is null in getUsersByEmailId()" );
        }
        if ( emailId == null || emailId.isEmpty() ) {
            throw new InvalidInputException( "Email id is null or empty in getUsersByEmailId()" );
        }

        List<User> users = userDao.fetchUsersBySimilarEmailId( admin, emailId );

        LOG.debug( "Method to fetch list of users on the basis of email id is finished." );
        return users;
    }


    @Transactional
    @Override
    public boolean isUserAdditionAllowed( User user ) throws NoRecordsFetchedException, InvalidInputException
    {
        LOG.debug( "Method to check whether users can be added or not started." );
        boolean isUserAdditionAllowed = false;

        if ( user == null ) {
            throw new InvalidInputException( "passed user parameter is null" );
        }
        if ( user.getCompany() == null ) {
            throw new InvalidInputException( "passed user parameter doesnt have the company" );
        }

        List<LicenseDetail> licenseDetails = licenseDetailsDao.findByColumn( LicenseDetail.class, CommonConstants.COMPANY,
            user.getCompany() );

        if ( licenseDetails == null || licenseDetails.isEmpty() ) {
            LOG.error( "Could not find any record in License_Details for : " + user.getCompany().getCompany() );
            throw new NoRecordsFetchedException(
                "Could not find any record in License_Details for : " + user.getCompany().getCompany() );
        }

        int maxUsersAllowed = licenseDetails.get( CommonConstants.INITIAL_INDEX ).getAccountsMaster().getMaxUsersAllowed();
        if ( maxUsersAllowed != CommonConstants.NO_LIMIT ) {
            long currentNumberOfUsers = userDao.getUsersCountForCompany( user.getCompany() );
            isUserAdditionAllowed = ( currentNumberOfUsers < maxUsersAllowed ) ? true : false;
        } else {
            LOG.debug( "No limit for number of user" );
            isUserAdditionAllowed = true;
        }

        LOG.debug( "Method to check whether users can be added or not finished." );
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
        LOG.debug( "Method to removeBranchAdmin called for branchId : " + branchId + " and userId : " + userIdToRemove );

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

        LOG.debug( "Method to removeBranchAdmin finished for branchId : " + branchId + " and userId : " + userIdToRemove );
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
        LOG.debug( "Method to removeRegionAdmin called for regionId : " + regionId + " and userId : " + userId );

        LOG.debug( "Selecting user for the userId provided for region admin : " + userId );
        User userToBeDeactivated = userDao.findById( User.class, userId );
        if ( userToBeDeactivated == null ) {
            throw new InvalidInputException( "No user found for userId specified in unassignRegionAdmin" );
        }

        userProfileDao.deactivateUserProfileForRegion( admin, regionId, userToBeDeactivated );

        LOG.debug( "Method to unassignRegionAdmin finished for regionId : " + regionId + " and userId : " + userId );

    }


    /**
     * Method to get user object for the given user id, fetches user along with profile name and
     * profile url
     */
    @Transactional
    @Override
    public User getUserByUserId( long userId ) throws InvalidInputException
    {
        LOG.debug( "Method to find user on the basis of user id started for user id " + userId );
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

        LOG.debug( "Method to find user on the basis of user id finished for user id " + userId );
        return user;
    }


    /**
     * Method to get user object for the given user id
     */
    @Transactional
    @Override
    public User getUserObjByUserId( long userId ) throws InvalidInputException
    {
        LOG.debug( "Method to find user on the basis of user id started for user id {}" , userId );
        User user = null;
        user = userDao.findById( User.class, userId );
        if ( user == null ) {
            throw new InvalidInputException( "User not found for userId:" + userId );
        }
        LOG.debug( "Method to find user on the basis of user id finished for user id {}", userId );
        return user;
    }


    @Override
    @Transactional
    public User getUserByProfileId( long profileId ) throws InvalidInputException
    {
        LOG.debug( "Method to find userprofile on the basis of profile id started for profileId " + profileId );

        UserProfile userProfile = userProfileDao.findById( UserProfile.class, profileId );
        if ( userProfile == null ) {
            throw new InvalidInputException( "UserProfile not found for userId:" + profileId );
        }

        LOG.debug( "Method to find userprofile on the basis of user id finished for profileId " + profileId );
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
        if ( userIds == null ) {
            throw new InvalidInputException( "Invalid parameter passed : passed parameter user id list is null" );
        }
        LOG.debug( "Method to find multiple users on the basis of list of user id started for user ids " + userIds );
        List<ProListUser> users = new ArrayList<ProListUser>();
        List<AgentSettings> agentSettingsList = new ArrayList<AgentSettings>();
        for ( Long id : userIds ) {
            AgentSettings agentSettings = null;
            try {
                agentSettings = organizationManagementService.getAgentSettings( id );
            } catch ( NoRecordsFetchedException e ) {
                LOG.error( "Exception caught ", e );
            }
            if ( agentSettings != null ) {
                agentSettingsList.add( agentSettings );
            }

        }

        // Commented as Zillow reviews are stored in Social Survey, SS-307
        /*List<User> userList = userDao.getUsersForUserIds( userIds );
        Map<Long, Integer> userIdReviewCountMap = new HashMap<Long, Integer>();
        Map<Long, Double> userIdReviewScoreMap = new HashMap<Long, Double>();
        for ( User user : userList ) {
            if ( user.getIsZillowConnected() == CommonConstants.YES ) {
                userIdReviewCountMap.put( user.getUserId(), user.getZillowReviewCount() );
                userIdReviewScoreMap.put( user.getUserId(), user.getZillowReviewCount() * user.getZillowAverageScore() );
            }
        }*/

        for ( AgentSettings agentSettings : agentSettingsList ) {
            ProListUser user = new ProListUser();
            user.setUserId( agentSettings.getIden() );
            user.setDisplayName( agentSettings.getContact_details().getName() );
            user.setProfileName( agentSettings.getProfileName() );
            user.setProfileUrl( agentSettings.getProfileUrl() );
            user.setProfileImageUrl( agentSettings.getProfileImageUrlThumbnail() );
            user.setEmailId( agentSettings.getContact_details().getMail_ids().getWork() );
            user.setTitle( agentSettings.getContact_details().getTitle() );
            user.setLocation( agentSettings.getContact_details().getLocation() );
            user.setIndustry( agentSettings.getContact_details().getIndustry() );
            if( agentSettings.getContact_details().getAbout_me() != null ){
            user.setAboutMe( Jsoup.clean( agentSettings.getContact_details().getAbout_me(), Whitelist.none() ) );
            }
            //JIRA SS-1104 search results not updated with correct number of reviews
            long reviewCount = profileManagementService.getReviewsCount( agentSettings.getIden(), 0, 5,
                CommonConstants.PROFILE_LEVEL_INDIVIDUAL, false, false );
            // Commented as Zillow reviews are stored in Social Survey, SS-307
            /*if ( userIdReviewCountMap.get( agentSettings.getIden() ) != null
                && userIdReviewCountMap.get( agentSettings.getIden() ) > 0 ) {
                reviewCount += userIdReviewCountMap.get( agentSettings.getIden() );
            }*/

            user.setReviewCount( reviewCount );
            // Commented as Zillow reviews are stored in Social Survey, SS-307
            /* if ( userIdReviewScoreMap.get( agentSettings.getIden() ) != null
                 && userIdReviewScoreMap.get( agentSettings.getIden() ) > 0 ) {
                 user.setReviewScore( surveyDetailsDao.getRatingForPastNdays( CommonConstants.AGENT_ID, agentSettings.getIden(),
                     CommonConstants.NO_LIMIT, false, false, true, userIdReviewCountMap.get( agentSettings.getIden() ),
                     userIdReviewScoreMap.get( agentSettings.getIden() ) ) );
             } else*/
            user.setReviewScore( surveyDetailsDao.getRatingForPastNdays( CommonConstants.AGENT_ID, agentSettings.getIden(),
                CommonConstants.NO_LIMIT, false, false, false, 0, 0 ) );
            users.add( user );
        }
        LOG.debug( "Method to find multiple users on the basis of list of user id finished for user ids " + userIds );
        return users;
    }


    /*
     * Method to return list of branches assigned to the user passed.
     */
    @Transactional
    @Override
    public List<Branch> getBranchesAssignedToUser( User user ) throws NoRecordsFetchedException, InvalidInputException
    {
        if ( user == null ) {
            throw new InvalidInputException( "Invalid parameter passed : passed parameter user is null" );
        }
        LOG.debug( "Method to find branches assigned to the user started for " + user.getFirstName() );
        List<Long> branchIds = userProfileDao.getBranchIdsForUser( user );
        if ( branchIds == null || branchIds.isEmpty() ) {
            LOG.error( "No branch found for user : " + user.getUserId() );
            throw new NoRecordsFetchedException( "No branch found for user : " + user.getUserId() );
        }
        List<Branch> branches = branchDao.findByColumnForMultipleValues( Branch.class, "branchId", branchIds );
        LOG.debug( "Method to find branches assigned to the user finished for " + user.getFirstName() );
        return branches;
    }


    /*
     * Method to return list of users belonging to the same company as that of user passed.
     */
    @Transactional
    @Override
    public List<User> getUsersForCompany( User user ) throws InvalidInputException, NoRecordsFetchedException
    {
        if ( user == null || user.getCompany() == null ) {
            LOG.error( "User cannote be null." );
            throw new InvalidInputException( "Null value found  user found for userId specified in getUsersForCompany()" );
        }
        LOG.debug( "Method getUsersForCompany() started for " + user.getUserId() );
        List<User> users = userDao.getUsersForCompany( user.getCompany() );
        if ( users == null || users.isEmpty() ) {
            LOG.error( "No user found for company : " + user.getCompany().getCompany() );
            throw new NoRecordsFetchedException( "No user found for company : " + user.getCompany().getCompany() );
        }
        LOG.debug( "Method getUsersForCompany() started for " + user.getUserId() );
        return users;
    }


    // JIRA SS-42 BY RM05 EOC

    @Override
    public void afterPropertiesSet() throws Exception
    {
        LOG.debug( "afterPropertiesSet for UserManagementServiceImpl called" );
        Map<Integer, ProfilesMaster> profilesMap = new HashMap<>();
        LOG.debug( "Populating profile master from db into the hashMap" );
        profilesMap = utilityService.populateProfileMastersMap();
        if ( !profilesMap.isEmpty() ) {
            profileMasters.putAll( profilesMap );
        }
        LOG.debug( "Successfully populated profile master from db into the hashMap" );

        LOG.debug( "afterPropertiesSet for UserManagementServiceImpl completed" );
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
        LOG.debug( "Method to assign branch admin called for branchId : " + branchId + " and userId : " + userId );

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
            LOG.debug( "User profile for same user and branch already exists. Activating the same." );
            userProfile.setStatus( CommonConstants.STATUS_ACTIVE );
            userProfile.setModifiedBy( String.valueOf( admin.getUserId() ) );
            userProfile.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
            //check that new profile will be primary or not
            int isPrimary = checkWillNewProfileBePrimary( userProfile, user.getUserProfiles() );
            userProfile.setIsPrimary( isPrimary );
            userProfileDao.update( userProfile );
        }

        LOG.debug( "Method to assignBranchAdmin finished for branchId : " + branchId + " and userId : " + userId );

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
        LOG.debug(
            "Method updateUserStatus of user management services called for userId : " + userId + " and status :" + status );
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
        LOG.debug( "Successfully completed method to update user status" );
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
        LOG.debug( "Method to assign user to a branch called for user : " + admin.getUserId() );
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
                userProfile.setProfilesMaster(
                    profilesMasterDao.findById( ProfilesMaster.class, CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) );
                userProfile.setModifiedBy( String.valueOf( admin.getUserId() ) );
                userProfile.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
            }
        }
        //check if user profile will be primary or not
        int isPrimary = checkWillNewProfileBePrimary( userProfile, user.getUserProfiles() );
        userProfile.setIsPrimary( isPrimary );
        userProfileDao.saveOrUpdate( userProfile );

        if ( user.getIsAtleastOneUserprofileComplete() == CommonConstants.STATUS_INACTIVE ) {
            LOG.debug( "Updating isAtleastOneProfileComplete as 1 for user : " + user.getFirstName() );
            user.setIsAtleastOneUserprofileComplete( CommonConstants.STATUS_ACTIVE );
            userDao.update( user );
        }
        setProfilesOfUser( user );
        solrSearchService.addUserToSolr( user );
        LOG.debug( "Method to assign user to a branch finished for user : " + admin.getUserId() );
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
        LOG.debug( "Method to unassign user from a branch called for user : " + admin.getUserId() );
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
            throw new InvalidInputException(
                "No user profile present for the user with user Id " + userId + " with the branch " + branchId );
        } else {
            userProfile = userProfiles.get( CommonConstants.INITIAL_INDEX );
            if ( userProfile.getStatus() == CommonConstants.STATUS_ACTIVE ) {
                userProfile.setStatus( CommonConstants.STATUS_INACTIVE );
                userProfile.setModifiedBy( String.valueOf( admin.getUserId() ) );
                userProfile.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
            }
        }
        userProfileDao.saveOrUpdate( userProfile );
        LOG.debug( "Method to unassign user from a branch finished for user : " + admin.getUserId() );

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


    /**
     * 
     * @param user
     * @param adminUser
     * @param profileId
     * @throws UserAssignmentException
     */
    @Transactional
    @Override
    public void removeUserProfile( User user, User adminUser, Long profileId ) throws UserAssignmentException
    {
        try {
            List<UserProfile> userprofileList = getAllUserProfilesForUser( user );
            if ( userprofileList.size() == 1 && userprofileList.get( 0 ).getUserProfileId() == profileId ) {
                throw new UserAssignmentException( "Cannot remove last user assignment." );
            }

            updateUserProfile( user, profileId, CommonConstants.STATUS_INACTIVE );
            updateUserProfilesStatus( user, profileId );
            removeUserProfile( profileId );

            updatePrimaryProfileOfUser( user );
            user = getUserByUserId( user.getUserId() );
            updateUserInSolr( user );
        } catch ( InvalidInputException | SolrException e ) {
            LOG.error( "An exception occured while removing user assignment. Reason : ", e );
            throw new UserAssignmentException( "An exception occured while removing user assignment. Reason : ", e );
        }
    }


    /*
     * Method to update the given user as active or inactive.
     */
    @Transactional
    @Override
    public void updateUser( User admin, long userIdToUpdate, boolean isActive ) throws InvalidInputException
    {
        LOG.debug( "Method to update a user called for user : " + userIdToUpdate );
        if ( admin == null ) {
            throw new InvalidInputException( "No admin user present." );
        }
        LOG.debug( "Method to assign user to a branch called for user : " + admin.getUserId() );
        User user = userDao.findById( User.class, userIdToUpdate );

        if ( user == null ) {
            throw new InvalidInputException( "No user present for the specified userId" );
        }

        user.setModifiedBy( String.valueOf( admin.getUserId() ) );
        user.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        LOG.debug( "Setting the user {} as {}", user.getFirstName(), isActive );
        if ( isActive ) {
            user.setStatus( CommonConstants.STATUS_ACTIVE );
        } else {
            user.setStatus( CommonConstants.STATUS_TEMPORARILY_INACTIVE );
        }

        userDao.update( user );

        LOG.debug( "Method to update a user finished for user : " + userIdToUpdate );
    }


    /*
     * Method to update the given userprofile as active or inactive.
     */
    @Transactional
    @Override
    public void updateUserProfile( User admin, long profileIdToUpdate, int status ) throws InvalidInputException
    {
        LOG.debug( "Method to update a user called for user profile: " + profileIdToUpdate );
        if ( admin == null ) {
            throw new InvalidInputException( "No admin user present." );
        }

        LOG.debug( "Method to assign user to a branch called by user : " + admin.getUserId() );
        UserProfile userProfile = userProfileDao.findById( UserProfile.class, profileIdToUpdate );
        if ( userProfile == null ) {
            throw new InvalidInputException( "No user profile present for the specified userId" );
        }

        userProfile.setModifiedBy( String.valueOf( admin.getUserId() ) );
        userProfile.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        userProfile.setStatus( status );

        userProfileDao.update( userProfile );
        LOG.debug( "Method to update a user finished for user : " + profileIdToUpdate );
    }


    @Override
    @Transactional
    public void removeUserProfile( long profileIdToDelete ) throws InvalidInputException
    {
        LOG.debug( "Method to delete a profile called for user profile: " + profileIdToDelete );

        UserProfile userProfile = userProfileDao.findById( UserProfile.class, profileIdToDelete );

        if ( userProfile == null ) {
            throw new InvalidInputException( "No user profile present for the specified profileId" );
        }

        userProfile.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        userProfile.setStatus( CommonConstants.STATUS_INACTIVE );
        userProfileDao.update( userProfile );
        LOG.debug( "Method to delete a profile finished for profile : " + profileIdToDelete );
    }


    @Override
    @Transactional
    public void updateUserInSolr( User user ) throws InvalidInputException, SolrException
    {

        LOG.debug( "Method to updateUserInSolr started" );
        //update user in solr
        if ( user == null ) {
            throw new InvalidInputException( "Method to updateUserInSolr ended" );
        }
        solrSearchService.addUserToSolr( user );
    }


    /*
     * Method to update the given user as active based on profiles completed
     */
    @Override
    @Transactional
    public void updateUserProfilesStatus( User admin, long profileIdToUpdate ) throws InvalidInputException
    {
        LOG.debug( "Method to update a user called for user: " + profileIdToUpdate );
        if ( admin == null ) {
            throw new InvalidInputException( "No admin user present." );
        }

        LOG.debug( "Method to assign user to a branch called by user : " + admin.getUserId() );
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
        LOG.debug( "Method to update a user finished for user : " + profileIdToUpdate );
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
                if ( currentProfile.getStatus() == CommonConstants.STATUS_ACTIVE ) {
                    if ( currentProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID
                        && branch.getIsDefaultBySystem() == CommonConstants.IS_DEFAULT_BY_SYSTEM_NO ) {
                        agentProfileWithoutDefaultBranch = currentProfile;
                    } else if ( currentProfile.getProfilesMaster()
                        .getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID
                        && branch.getIsDefaultBySystem() == CommonConstants.IS_DEFAULT_BY_SYSTEM_YES ) {
                        agentProfileWithDefaultBranch = currentProfile;
                    } else if ( currentProfile.getProfilesMaster()
                        .getProfileId() == CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID ) {
                        branchAdminProfile = currentProfile;
                    } else if ( currentProfile.getProfilesMaster()
                        .getProfileId() == CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID ) {
                        regionAdminProfile = currentProfile;
                    } else if ( currentProfile.getProfilesMaster()
                        .getProfileId() == CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID ) {
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

            if ( profileToMakePrimary == null ) {
                throw new InvalidInputException( "No user profile present for the specified userId" );
            }
            profileToMakePrimary.setIsPrimary( CommonConstants.IS_PRIMARY_TRUE );
            profileToMakePrimary.setModifiedOn(new Timestamp(System.currentTimeMillis()));
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
        String profileName, String loginName, boolean holdSendingMail ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "Method to send profile completion link to the user started." );
        if ( emailId == null || emailId.isEmpty() ) {
            throw new InvalidInputException( "Invalid parameter passed : passed parameter emailId is null or empty" );
        }
        if ( profileName == null || profileName.isEmpty() ) {
            throw new InvalidInputException( "Invalid parameter passed : passed parameter profileName is null or empty" );
        }
        if ( companyId <= 0l ) {
            throw new InvalidInputException( "Invalid parameter passed : passed parameter company id ininvald" );
        }
        Map<String, String> urlParams = new HashMap<String, String>();
        urlParams.put( CommonConstants.EMAIL_ID, emailId );
        urlParams.put( CommonConstants.FIRST_NAME, firstName );
        if ( lastName != null && !lastName.isEmpty() ) {
            urlParams.put( CommonConstants.LAST_NAME, lastName );
        }
        urlParams.put( CommonConstants.COMPANY, String.valueOf( companyId ) );

        LOG.debug( "Generating URL" );
        String url = urlGenerator.generateUrl( urlParams,
            applicationBaseUrl + CommonConstants.SHOW_COMPLETE_REGISTRATION_PAGE );
        String name = firstName;
        if ( lastName != null && !lastName.isEmpty() ) {
            name = name + " " + lastName;
        }

        OrganizationUnitSettings companySettings = null;
        boolean hiddenSection = false;
        User user = null;
        try {
            user = this.getUserByEmail( emailId );
            if ( user != null ) {
                companySettings = organizationManagementService.getCompanySettings( user.getCompany().getCompanyId() );
                if ( companySettings != null ) {
                    hiddenSection = companySettings.isHiddenSection();
                }
            }
        } catch ( NoRecordsFetchedException e ) {
            LOG.error( "No record fetched for user with email id: " + emailId );
        }

        // Send reset password link to the user email ID
        emailServices.sendRegistrationCompletionEmail( url, emailId, name, profileName, loginName, holdSendingMail,
            hiddenSection );
        
        // if the email is supposed to be either sent immediately or by batch without holding it, then update the invite sent date for the user 
        if( !holdSendingMail ){
        updateLastInviteSentDateIfUserExistsInDB( emailId );
        }
    }


    /*
     * Method to set properties of a user based upon active profiles available for the user.
     */
    @Override
    public void setProfilesOfUser( User user )
    {
        LOG.debug(
            "Method setProfilesOfUser() to set properties of a user based upon active profiles available for the user started." );
        if ( user != null ) {
            List<UserProfile> userProfiles = user.getUserProfiles();
            if ( userProfiles != null ) {
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
                                LOG.error( "Invalid profile id found for user {} in setProfilesOfUser().",
                                    user.getFirstName() );
                        }
                    }
                }
            }
        }

        LOG.debug(
            "Method setProfilesOfUser() to set properties of a user based upon active profiles available for the user finished." );
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
        LOG.debug( "Method getAllUserProfilesForUser() called to fetch the list of user profiles for the user" );
        Map<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
        queries.put( CommonConstants.USER_COLUMN, user );
        queries.put( CommonConstants.COMPANY_COLUMN, user.getCompany() );
        List<UserProfile> userProfiles = userProfileDao.findByKeyValue( UserProfile.class, queries );
        LOG.debug( "Method getAllUserProfilesForUser() finised successfully" );
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
        if ( user == null ) {
            throw new InvalidInputException( "Invalid parameter passed : passed parameter user is null " );
        }
        String verificationUrl = null;

        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put( CommonConstants.EMAIL_ID, user.getEmailId() );
            params.put( CommonConstants.USER_ID, String.valueOf( user.getUserId() ) );

            LOG.debug( "Calling url generator to generate verification link" );
            verificationUrl = urlGenerator.generateUrl( params,
                applicationBaseUrl + CommonConstants.REQUEST_MAPPING_MAIL_VERIFICATION );
        } catch ( InvalidInputException e ) {
            throw new InvalidInputException( "Could not generate url for verification.Reason : " + e.getMessage(), e );
        }

        try {
            LOG.debug( "Calling email services to send verification mail for user " + user.getEmailId() );
            String profileName = getUserSettings( user.getUserId() ).getProfileName();

            OrganizationUnitSettings companySettings = null;
            boolean hiddenSection = false;
            if ( user != null ) {
                companySettings = organizationManagementService.getCompanySettings( user.getCompany().getCompanyId() );
                if ( companySettings != null ) {
                    hiddenSection = companySettings.isHiddenSection();
                }
            }

            emailServices.sendVerificationMail( verificationUrl, user.getEmailId(),
                user.getFirstName() + " " + ( user.getLastName() != null ? user.getLastName() : "" ), profileName,
                user.getLoginName(), hiddenSection );
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

        if ( emailId == null || emailId.isEmpty() ) {
            throw new InvalidInputException( "Invalid parameter passed : passed parameter email id is null or empty" );
        }

        String queryParam = extractUrlQueryParam( url );
        if ( !isReinvitation ) {
            if ( userWithEmailIdExists( emailId ) ) {
                throw new UserAlreadyExistsException( "user with specified email id already exists" );
            }
        }

        LOG.debug( "Calling method to store the registration invite" );
        Company company = companyDao.findById( Company.class, CommonConstants.DEFAULT_COMPANY_ID );
        storeCompanyAdminInvitation( queryParam, emailId, company );

        LOG.debug( "Calling email services to send registration invitation mail" );
        emailServices.sendRegistrationInviteMail( url, emailId, firstName, lastName );
        
        updateLastInviteSentDateIfUserExistsInDB( emailId );

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
    private void storeCompanyAdminInvitation( String queryParam, String emailId, Company company )
    {
        LOG.debug( "Method storeInvitation called with query param : " + queryParam + " and emailId : " + emailId );
        UserInvite userInvite = null;
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
     * @throws InvalidInputException 
     */
    @Transactional
    @Override
    public boolean userExists( String userName ) throws InvalidInputException
    {
        LOG.debug( "Method to check if user exists called for username : " + userName );
        if ( userName == null || userName.isEmpty() ) {
            throw new InvalidInputException( "Invalid parameter passed : passed parameter user name is null or empty" );
        }
        boolean isUserPresent = false;
        try {
            Map<String, Object> queries = new HashMap<String, Object>();
            queries.put( CommonConstants.EMAIL_ID, userName );
            queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
            List<UserEmailMapping> userEmailMappings = userEmailMappingDao.findByKeyValue( UserEmailMapping.class, queries );

            if ( userEmailMappings == null || userEmailMappings.isEmpty() ) {
                userDao.getActiveUser( userName );
            }
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
    public UserSettings getCanonicalUserSettings( User user, AccountType accountType )
        throws InvalidInputException, NoRecordsFetchedException
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
        LOG.debug( "Getting the canonical settings for the user: " + user.toString() );

        // get the settings according to the profile and account type
        LOG.debug( "Getting the company settings for the user" );
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
                if ( userProfile.getProfilesMaster()
                    .getProfileId() == CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID ) {
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
                            BranchSettings branchSetting = organizationManagementService
                                .getBranchSettings( userProfile.getBranchId() );
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
        LOG.debug( "Getting agent settings for agent id: {}", agentId );
        if ( agentId <= 0l ) {
        	LOG.warn("Invalid agent id for fetching user settings.");
            throw new InvalidInputException( "Invalid agent id for fetching user settings." );
        }
        AgentSettings agentSettings = organizationUnitSettingsDao.fetchAgentSettingsById( agentId );
        if ( agentSettings != null && agentSettings.getProfileStages() != null ) {
            agentSettings
                .setProfileStages( profileCompletionList.getProfileCompletionList( agentSettings.getProfileStages() ) );
        }
        return agentSettings;
    }


    @Override
    public ContactDetailsSettings fetchAgentContactDetailByEncryptedId( String userEncryptedId ) throws InvalidInputException
    {
        LOG.debug( "Getting agent settings for userEncryptedId id: " + userEncryptedId );
        if ( userEncryptedId == null || userEncryptedId.isEmpty() ) {
            throw new InvalidInputException( "Invalid userEncrypted id for fetching user settings" );
        }
        ContactDetailsSettings contactDetailsSettings = organizationUnitSettingsDao
            .fetchAgentContactDetailByEncryptedId( userEncryptedId );

        return contactDetailsSettings;
    }


    @Override
    public AgentSettings getAgentSettingsForUserProfiles( long userId ) throws InvalidInputException
    {
        LOG.debug( "Getting agent settings for user id: " + userId );
        AgentSettings agentSettings = getUserSettings( userId );
        return agentSettings;
    }


    /*
     * Method to check if current user is authorized to assign a user to the given branch.
     */
    private boolean isAssigningAllowed( long branchId, User admin )
    {
        LOG.debug(
            "Method isAssigningAllowed() started to check if current user is authorized to assign a user to the given branch" );
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
    public void assignUserToCompany( User admin, long userId )
        throws InvalidInputException, NoRecordsFetchedException, SolrException
    {
        if ( admin == null ) {
            LOG.error( "assignUserToCompany : admin parameter is null" );
            throw new InvalidInputException( "assignUserToCompany : admin parameter is null" );
        }
        if ( userId <= 0l ) {
            LOG.error( "assignUserToCompany : userId parameter is null" );
            throw new InvalidInputException( "assignUserToCompany : userId parameter is null" );
        }
        LOG.debug( "Method to assign user to a branch called for user : " + admin.getUserId() );
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
        LOG.debug( "Method to assign user to a company finished for user : " + admin.getUserId() );
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

        LOG.debug(
            "Method canAddUsersToRegion() called to check if current user is authorized to assign a user to the given region" );

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
    public void assignUserToRegion( User admin, long userId, long regionId )
        throws InvalidInputException, NoRecordsFetchedException, SolrException
    {

        if ( admin == null ) {
            LOG.error( "assignUserToRegion : admin parameter is null" );
            throw new InvalidInputException( "assignUserToRegion : admin parameter is null" );
        }

        if ( userId <= 0l ) {
            LOG.error( "assignUserToRegion : userId parameter is null" );
            throw new InvalidInputException( "assignUserToRegion : userId parameter is null" );
        }
        if ( regionId <= 0l ) {
            LOG.error( "assignUserToRegion : regionId parameter is null" );
            throw new InvalidInputException( "assignUserToRegion : regionId parameter is null" );
        }
        LOG.debug( "Method to assign user to a branch called for user : " + admin.getUserId() );
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
            throw new InvalidInputException(
                "User : " + admin.getUserId() + " is not authorized to assign users to region " + regionId );
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
        LOG.debug( "Method to assign user to a company finished for user : " + admin.getUserId() );

    }


    /**
     * Method to insert basic settings for a user
     */
    @Override
    public void insertAgentSettings( User user ) throws InvalidInputException
    {
        if ( user == null ) {
            throw new InvalidInputException( "passed parameter user is null" );
        }
        LOG.debug( "Inserting agent settings. User id: " + user.getUserId() );
        AgentSettings agentSettings = new AgentSettings();
        agentSettings.setIden( user.getUserId() );
        agentSettings.setCompanyId(user.getCompany().getCompanyId());
        agentSettings.setCreatedBy( user.getCreatedBy() );
        agentSettings.setCreatedOn( System.currentTimeMillis() );
        agentSettings.setModifiedBy( user.getModifiedBy() );
        agentSettings.setModifiedOn( System.currentTimeMillis() );
        agentSettings.setVertical( user.getCompany().getVerticalsMaster().getVerticalName() );

        //set encrypted id
        agentSettings.setUserEncryptedId( generateUserEncryptedId( user.getUserId() ) );


        //Set status to incomplete
        agentSettings.setStatus( CommonConstants.STATUS_INCOMPLETE_MONGO );

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
            surveySettings.setAuto_post_score( CommonConstants.DEFAULT_AUTOPOST_SCORE );
            agentSettings.setSurvey_settings( surveySettings );
        }

        //get company setting
        OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user.getCompany().getCompanyId() );
        if(companySettings != null && companySettings.isAllowPartnerSurvey() ){
            agentSettings.setAllowPartnerSurvey(true);
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
        agentSettings.setShowSummitPopup( false );

        organizationUnitSettingsDao.insertAgentSettings( agentSettings );
        LOG.debug( "Inserted into agent settings" );
    }


    @Override
    public String generateUserEncryptedId( long userId ) throws InvalidInputException
    {
        LOG.debug( "method generateUserEncryptedId started for user id  " + userId );

        long hashedUserId = String.valueOf( userId ).hashCode();
        String hashedUserIdStr = String.valueOf( hashedUserId );
        String paddedBitString = "";
        if ( hashedUserIdStr.length() < 12 ) {
            for ( int i = 0; i < 12 - hashedUserIdStr.length(); i++ ) {
                paddedBitString += (int) ( 10.0 * Math.random() );
            }
            hashedUserIdStr += paddedBitString;
        }

        String userEncryptedId = encryptionHelper.encodeBase64( hashedUserIdStr );
        return userEncryptedId;

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
        LOG.debug( "Method generateIndividualProfileName called for userId:" + userId + " and emailId:" + emailId );
        if ( emailId == null || emailId.isEmpty() ) {
            throw new InvalidInputException( "emailId is null or empty while generating agent profile name" );
        }
        if ( userId <= 0l ) {
            throw new InvalidInputException( "Wrong parameter passed : passed userId is invalid" );
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
        LOG.debug( "Method generateIndividualProfileName finished successfully.Returning profileName: " + profileName );
        return profileName;
    }


    /**
     * Method to update profile name and url in agent settings
     * 
     * @param profileName
     * @param profileUrl
     * @param agentSettings
     * @throws InvalidInputException 
     */
    @Override
    public void updateProfileUrlInAgentSettings( String profileName, String profileUrl, AgentSettings agentSettings )
        throws InvalidInputException
    {
        LOG.debug( "Method to update profile name and url in AGENT SETTINGS started" );
        if ( agentSettings == null ) {
            throw new InvalidInputException( "passsed input parameter agentSettings is null" );
        }
        organizationUnitSettingsDao.updateParticularKeyAgentSettings( MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_NAME,
            profileName, agentSettings );
        organizationUnitSettingsDao.updateParticularKeyAgentSettings( MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_URL,
            profileUrl, agentSettings );
        LOG.debug( "Method to update profile name and url in AGENT SETTINGS finished" );
    }


    /**
     * Method to update profile name and url in branch settings
     * 
     * @param profileName
     * @param profileUrl
     * @param branchSettings
     */
    @Override
    public void updateProfileUrlInBranchSettings( String profileName, String profileUrl,
        OrganizationUnitSettings branchSettings ) throws InvalidInputException
    {
        LOG.debug( "Method to update profile name and url in BRANCH SETTINGS started" );
        LOG.debug( "Method to update profile name and url in AGENT SETTINGS started" );
        if ( branchSettings == null ) {
            throw new InvalidInputException( "passsed input parameter agentSettings is null" );
        }
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_NAME, profileName, branchSettings,
            MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_URL, profileUrl, branchSettings,
            MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );

        LOG.debug( "Method to update profile name and url in BRANCH SETTINGS finished" );
    }


    /**
     * Method to update profile name and url in region settings
     * 
     * @param profileName
     * @param profileUrl
     * @param regionSettings
     */
    @Override
    public void updateProfileUrlInRegionSettings( String profileName, String profileUrl,
        OrganizationUnitSettings regionSettings ) throws InvalidInputException
    {
        LOG.debug( "Method to update profile name and url in REGION SETTINGS started" );
        LOG.debug( "Method to update profile name and url in AGENT SETTINGS started" );
        if ( regionSettings == null ) {
            throw new InvalidInputException( "passsed input parameter agentSettings is null" );
        }
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_NAME, profileName, regionSettings,
            MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_URL, profileUrl, regionSettings,
            MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );

        LOG.debug( "Method to update profile name and url in REGION SETTINGS finished" );
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
        OrganizationUnitSettings companySettings ) throws InvalidInputException
    {
        LOG.debug( "Method to update profile name and url in COMPANY SETTINGS started" );
        LOG.debug( "Method to update profile name and url in AGENT SETTINGS started" );
        if ( companySettings == null ) {
            throw new InvalidInputException( "passsed input parameter agentSettings is null" );
        }
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_NAME, profileName, companySettings,
            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_URL, profileUrl, companySettings,
            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        LOG.debug( "Method to update profile name and url in COMPANY SETTINGS finished" );
    }


    /**
     * Method to check which all users can perform edit and set the boolean as true or false in user
     * objects
     */
    @Override
    public List<UserFromSearch> checkUserCanEdit( User admin, UserFromSearch adminFromSearch, List<UserFromSearch> users )
        throws InvalidInputException
    {
        LOG.debug( "Method checkUserCanEdit called for admin:{} and adminUser:{}" ,admin, adminFromSearch );
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
        LOG.debug( "Method checkUserCanEdit executed successfully" );
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
        LOG.debug( "Method updateUserOnCompleteRegistration called" );
        if ( user == null ) {
            throw new InvalidInputException( "User id null in updateUserOnCompleteRegistration" );
        }
        user.setFirstName( firstName );
        user.setLastName( lastName );
        user.setIsAtleastOneUserprofileComplete( CommonConstants.STATUS_ACTIVE );
        user.setStatus( CommonConstants.STATUS_ACTIVE );
        user.setModifiedBy( String.valueOf( user.getUserId() ) );
        user.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );

        //Set AdoptionCompletionDate
        user.setAdoptionCompletionDate( new Timestamp( System.currentTimeMillis() ) );
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
        //Update status from incomplete to active
        agentSettings.setStatus( CommonConstants.STATUS_ACTIVE_MONGO );
        organizationUnitSettingsDao.updateParticularKeyAgentSettings( CommonConstants.STATUS_COLUMN,
            CommonConstants.STATUS_ACTIVE_MONGO, agentSettings );
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
        solrSearchService.editUserInSolr( user.getUserId(), CommonConstants.USER_DISPLAY_NAME_SOLR,
            user.getFirstName() + " " + ( user.getLastName() != null ? user.getLastName() : "" ) );
        solrSearchService.editUserInSolr( user.getUserId(), CommonConstants.PROFILE_URL_SOLR, profileUrl );
        solrSearchService.editUserInSolr( user.getUserId(), CommonConstants.PROFILE_NAME_SOLR, profileName );
        LOG.debug( "Successfully modified user detail in solr" );

        LOG.debug( "Method updateUserOnCompleteRegistration executed successfully" );

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
        LOG.debug( "Updating users login time and number of logins for user: " + user.toString() );
        user.setLastLogin( new Timestamp( System.currentTimeMillis() ) );
        user.setNumOfLogins( user.getNumOfLogins() + 1 );
        userDao.update( user );
        LOG.debug( "Updated user login time and number of login" );
    }


    @Transactional
    @Override
    public void updateUserCountModificationNotification( Company company ) throws InvalidInputException
    {
        if ( company == null ) {
            throw new InvalidInputException( "Company passed in updateUserCountModificationNotification is null" );
        }
        LOG.debug( "Adding a record in user count modification notification table for company " + company.getCompany() );
        // search for the record in the table. it might be possible that record is already present.
        List<UsercountModificationNotification> userCountNotifications = userCountModificationDao
            .findByColumn( UsercountModificationNotification.class, CommonConstants.COMPANY_COLUMN, company );
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

        LOG.debug(
            "Finished adding a record in user count modification notification table for company " + company.getCompany() );
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
    public void updateUser( User user, Map<String, Object> map ) throws SolrException, InvalidInputException
    {
        LOG.debug( "Method updateUser() started to update user." );
        user.setModifiedOn(new Timestamp(System.currentTimeMillis()));
        userDao.merge( user );
        userProfileDao.updateEmailIdForUserProfile( user.getUserId(), user.getEmailId() );
        solrSearchService.editUserInSolrWithMultipleValues( user.getUserId(), map );
        LOG.debug( "Method updateUser() finished to update user." );
    }


    // Moved user addition from Controller.
    @Override
    @Transactional ( rollbackFor = { NonFatalException.class, FatalException.class })
    public User inviteUser( User admin, String firstName, String lastName, String emailId, boolean isAddedByRealtechOrSSAdmin )
        throws InvalidInputException, UserAlreadyExistsException, UndeliveredEmailException, SolrException, NoRecordsFetchedException
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
        
        if( !isAddedByRealtechOrSSAdmin ) {
            // send user addition mail
            organizationManagementService.sendUserAdditionMail( admin, user );
        }
        
        LOG.debug( "Added newly added user {} to solr", user.getFirstName() );

        return user;
    }


    @Override
    @Transactional ( rollbackFor = { NonFatalException.class, FatalException.class })
    public User addCorporateAdmin( String firstName, String lastName, String emailId, String confirmPassword,
        boolean isDirectRegistration )
        throws InvalidInputException, UserAlreadyExistsException, UndeliveredEmailException, SolrException, NoRecordsFetchedException
    {

        User user = addCorporateAdminAndUpdateStage( firstName, lastName, emailId, confirmPassword, isDirectRegistration );
        LOG.debug( "Succesfully completed registration of user with emailId : " + emailId );

        LOG.debug( "Adding newly added user {} to mongo", user.getFirstName() );
        insertAgentSettings( user );
        
        LOG.debug( "Added newly added user {} to mongo", user.getFirstName() );

        LOG.debug( "Adding newly added user {} to solr", user.getFirstName() );
        //        try {
        //            solrSearchService.addUserToSolr( user );
        //        } catch ( SolrException e ) {
        //            LOG.error( "SolrException caught in addCorporateAdmin(). Nested exception is ", e );
        //            organizationManagementService.removeOrganizationUnitSettings( user.getUserId(),
        //                MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
        //            throw e;
        //        }
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

        LOG.debug(
            "Method checkWillNewProfileBePrimary called in UserManagementService for email id" + userProfileNew.getEmailId() );

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
                            && userProfileNew.getProfilesMaster()
                                .getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) {

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
                        LOG.debug(
                            "Old primary profile is an admin profile of type " + profile.getProfilesMaster().getProfile() );
                        //if old profile is for admin and new is for agent than remove primary from old and mark new profile as primary
                        if ( userProfileNew.getProfilesMaster()
                            .getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) {
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
    public String fetchAppropriateLogoUrlFromHierarchyForUser( long userId )
        throws InvalidInputException, NoRecordsFetchedException, ProfileNotFoundException

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
            e.printStackTrace();
        }
        OrganizationUnit organizationUnit = map.get( SettingsForApplication.LOGO );
        //JIRA SS-1363 begin
        /*if ( organizationUnit == OrganizationUnit.COMPANY ) {
            OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( companyId );
            logoUrl = companySettings.getLogoThumbnail();
        } else if ( organizationUnit == OrganizationUnit.REGION ) {
            OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( regionId );
            logoUrl = regionSettings.getLogoThumbnail();
        } else if ( organizationUnit == OrganizationUnit.BRANCH ) {
            OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( branchId );
            logoUrl = branchSettings.getLogoThumbnail();
        } else if ( organizationUnit == OrganizationUnit.AGENT ) {
            logoUrl = agentSettings.getLogoThumbnail();
        }*/
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
        //JIRA SS-1363 end

        return logoUrl;
    }


    @Transactional
    private void makeAProfileAsPrimaryOfAUser( long userId ) throws InvalidInputException, ProfileNotFoundException
    {
        LOG.debug( "method makeAProfileAsPrimaryOfAUser started with userid : {}", userId );
        User user = userDao.findById( User.class, userId );
        if ( user == null ) {
            LOG.warn( "User Id passed was null" );
            throw new InvalidInputException( "Passed User id is null." );
        }

        String errorMessage = "No primary user profile found for user " + user.getFirstName() + user.getLastName()
            + ", with user id : " + user.getUserId();
        try {
            emailServices.sendReportBugMailToAdmin( applicationAdminName, errorMessage, applicationAdminEmail );
        } catch ( UndeliveredEmailException e ) {
            LOG.error( "error while sending report bug mail to admin. ", e );
        }

        //List<UserProfile> userProfileList = userProfileDao.findByColumn(UserProfile.class, CommonConstants.USER_COLUMN, user);
        List<UserProfile> userProfileList = user.getUserProfiles();
        if ( userProfileList == null || userProfileList.isEmpty() ) {
        	LOG.warn("No profile found for user with id : {}",user.getUserId());
            throw new ProfileNotFoundException( "No profile found for user with id : "+ user.getUserId() );
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
            } else if ( currentProfile.getProfilesMaster()
                .getProfileId() == CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID ) {
                branchAdminProfile = currentProfile;
            } else if ( currentProfile.getProfilesMaster()
                .getProfileId() == CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID ) {
                regionAdminProfile = currentProfile;
            } else if ( currentProfile.getProfilesMaster()
                .getProfileId() == CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID ) {
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
        if(LOG.isDebugEnabled()){
        	LOG.debug( "marked a profile with profile id {} as primary for user with user id {}",profileToMakePrimary.getUserProfileId(),user.getUserId() );
        }    
        LOG.debug( "method makeAProfileAsPrimaryOfAUser ended for user with userid : {}", user.getUserId() );
    }


    @Override
    @Transactional
    public List<SettingsDetails> getSettingScoresById( long companyId, long regionId, long branchId )
    {
        LOG.debug(
            "Inside method getSettingScoresById for company " + companyId + " region " + regionId + " branch " + branchId );
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
    		//update modified on
    		company.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        companyDao.update( company );

    }


    @Override
    @Transactional
    public void updateBranch( Branch branch )
    {	
    		//update modified on
    		branch.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        branchDao.update( branch );

    }


    @Override
    @Transactional
    public void updateRegion( Region region )
    {
    		//update modified on
    		region.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        regionDao.update( region );

    }


    @Override
    @Transactional
    public void updateUser( User user )
    {
    		//update modified on
    		user.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        userDao.update( user );

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
    public Map<String, Long> getPrimaryUserProfileByAgentId( long entityId )
        throws InvalidInputException, ProfileNotFoundException
    {
        LOG.debug( "method getPrimaryUserProfileByAgentId started with user id {}", entityId );
        Map<String, Long> userProfileDetailMap = userProfileDao.findPrimaryUserProfileByAgentId( entityId );
        if ( userProfileDetailMap == null || userProfileDetailMap.isEmpty() ) {
            LOG.warn( "No primary profile found for the user with id {}", entityId );
            makeAProfileAsPrimaryOfAUser( entityId );
            // after making a profile primary, again fetch the data
            userProfileDetailMap = userProfileDao.findPrimaryUserProfileByAgentId( entityId );
        }

        LOG.debug( "method getPrimaryUserProfileByAgentId ended with user id {}", entityId );
        return userProfileDetailMap;
    }


    @Override
    @Transactional
    public boolean validateUserApiKey( String apiKey, String apiSecret, long companyId ) throws InvalidInputException
    {
        boolean valid = false;
        LOG.debug( "Validating whether the values provided are valid for company " + companyId );
        if ( apiSecret == null || apiSecret.isEmpty() ) {
            LOG.warn( "Api Secret is null" );
            throw new InvalidInputException( "Invalid api secret" );
        }
        if ( apiKey == null || apiKey.isEmpty() ) {
            LOG.warn( "Api key is null" );
            throw new InvalidInputException( "Invalid api key" );
        }

        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put( CommonConstants.API_SECRET_COLUMN, apiSecret.trim() );
        queryMap.put( CommonConstants.API_KEY_COLUMN, apiKey.trim() );
        queryMap.put( CommonConstants.COMPANY_ID_COLUMN, companyId );
        queryMap.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
        long count = apiKeyDao.findNumberOfRowsByKeyValue( UserApiKey.class, queryMap );
        LOG.debug( "Found " + count + " records from the api keys" );
        if ( count > 0l ) {
            LOG.debug( "API key is valid" );
            valid = true;
        }
        return valid;
    }


    /**
     * 
     */
    @Override
    @Transactional
    public UserApiKey getUserApiKeyForCompany( long companyId ) throws InvalidInputException
    {
        LOG.debug( "method getUserApiKeyForCompany started for companyId " + companyId );

        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put( CommonConstants.COMPANY_ID_COLUMN, companyId );
        queryMap.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
        List<UserApiKey> apiKeys = apiKeyDao.findByKeyValue( UserApiKey.class, queryMap );

        LOG.debug( "method getUserApiKeyForCompany ended for companyId " + companyId );

        if ( apiKeys != null && apiKeys.size() > 0 )
            return apiKeys.get( CommonConstants.INITIAL_INDEX );
        else
            return null;
    }


    /**
     * 
     * @param apiKey
     * @param apiSecret
     * @param companyId
     * @return
     * @throws InvalidInputException
     */
    @Override
    @Transactional
    public UserApiKey generateAndSaveUserApiKey( long companyId ) throws InvalidInputException
    {
        LOG.debug( "method saveUserApiKey started " );

        UserApiKey userApiKey = new UserApiKey();

        OrganizationUnitSettings settings = organizationManagementService.getCompanySettings( companyId );
        String apiKey = settings.getProfileName();
        String apiSecret = String.valueOf( companyId ) + "_" + String.valueOf( System.currentTimeMillis() );

        userApiKey.setApiKey( apiKey );
        userApiKey.setApiSecret( apiSecret );
        userApiKey.setCompanyId( companyId );
        userApiKey.setStatus( CommonConstants.STATUS_ACTIVE );
        userApiKey.setCreatedOn( new Timestamp( System.currentTimeMillis() ) );
        userApiKey.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );

        userApiKey = apiKeyDao.save( userApiKey );
        LOG.debug( "method saveUserApiKey ended" );

        return userApiKey;
    }


    @Override
    @Transactional
    public void updateStatusOfUserApiKey( long userApiKeyId, int status ) throws NoRecordsFetchedException
    {
        LOG.debug( "method updateStatusOfUserApiKey started " );

        UserApiKey userApiKey = apiKeyDao.findById( UserApiKey.class, userApiKeyId );
        if ( userApiKey == null ) {
            throw new NoRecordsFetchedException( "No Api key found with id " + userApiKeyId );
        }

        userApiKey.setStatus( status );
        apiKeyDao.update( userApiKey );
        LOG.debug( "method updateStatusOfUserApiKey ended" );

    }


    @Override
    @Transactional
    public List<UserApiKey> getActiveUserApiKeys()
    {
        LOG.debug( "method getActiveUserApiKeys started " );
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
        List<UserApiKey> apiKeys = apiKeyDao.findByKeyValue( UserApiKey.class, queryMap );

        Set<Long> companyIds = new HashSet<Long>();
        for ( UserApiKey apiKey : apiKeys ) {
            companyIds.add( apiKey.getCompanyId() );
        }

        Map<Long, Company> companiesById = companyDao.getCompaniesByIds( companyIds );
        for ( UserApiKey apiKey : apiKeys ) {
            Company currCompany = companiesById.get( apiKey.getCompanyId() );
            if ( currCompany != null )
                apiKey.setCompanyName( currCompany.getCompany() );
        }

        LOG.debug( "method getActiveUserApiKeys ended " );
        return apiKeys;
    }


    @Override
    public int getUsersUnderBranchAdminCount( User admin )
    {
        return userProfileDao.getUsersUnderBranchAdminCount( admin );
    }


    @Override
    public int getUsersUnderRegionAdminCount( User admin )
    {
        return userProfileDao.getUsersUnderRegionAdminCount( admin );
    }


    @Override
    public int getUsersUnderCompanyAdminCount( User admin )
    {
        return userProfileDao.getUsersUnderCompanyAdminCount( admin );
    }


    @Override
    public List<UserFromSearch> getUsersUnderBranchAdmin( User admin, int startIndex, int batchSize )
    {
        return userProfileDao.findUsersUnderBranchAdmin( admin, startIndex, batchSize );
    }


    @Override
    public List<UserFromSearch> getUsersUnderRegionAdmin( User admin, int startIndex, int batchSize )
    {
        return userProfileDao.findUsersUnderRegionAdmin( admin, startIndex, batchSize );
    }


    @Override
    public List<UserFromSearch> getUsersUnderCompanyAdmin( User admin, int startIndex, int batchSize )
    {
        return userProfileDao.findUsersUnderCompanyAdmin( admin, startIndex, batchSize );
    }


    @Override
    public List<UserFromSearch> getUsersByUserIds( Set<Long> userIds ) throws InvalidInputException
    {
        LOG.debug( "Method to find users on the basis of user ids started for user ids : " + userIds );
        if ( userIds == null || userIds.size() <= 0 ) {
            throw new InvalidInputException( "Invalid input parameter : Null or empty User Id List passed " );
        }
        List<UserFromSearch> userList = userProfileDao.getUserFromSearchByUserIds( userIds );
        if ( userList == null ) {
            throw new InvalidInputException( "User not found for userId:" + userIds );
        }
        LOG.debug( "Method to find users on the basis of user ids ended for user ids : " + userIds );
        return userList;
    }


    // Method to return active user with provided email and company
    @Transactional
    @Override
    public User getActiveUserByEmailAndCompany( long companyId, String emailId )
        throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "Method getUserByEmailAndCompany() called from UserManagementService" );

        if ( emailId == null || emailId.isEmpty() ) {
            throw new InvalidInputException( "Email id is null or empty in getUserByEmailAndCompany()" );
        }

        Company company = companyDao.findById( Company.class, companyId );
        if ( company == null ) {
            throw new NoRecordsFetchedException( "No company found with the id " + companyId );
        }

        User user = getUserByEmailAddress( emailId );
        if ( user.getCompany().getCompanyId() != companyId ) {
            throw new InvalidInputException( "The user is not part of the specified company" );
        }
        //User user = userDao.getActiveUserByEmailAndCompany( emailId, company );

        LOG.debug( "Method getUserByEmailAndCompany() finished from UserManagementService" );
        return user;
    }


 // Method to return active agent with provided email and company
    @Transactional
    @Override
    public User getActiveAgentByEmailAndCompany( long companyId, String emailId )
        throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "Method getActiveAgentByEmailAndCompany() called from UserManagementService" );

        if ( emailId == null || emailId.isEmpty() ) {
            throw new InvalidInputException( "Email id is null or empty in getUserByEmailAndCompany()" );
        }

        Company company = companyDao.findById( Company.class, companyId );
        if ( company == null ) {
            throw new NoRecordsFetchedException( "No company found with the id " + companyId );
        }

        User user = getUserByEmailAndCompanyFromUserEmailMappings( company, emailId );
        
        List<UserProfile> userProfiles = user.getUserProfiles();
        for(UserProfile profile : userProfiles){
            if(profile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID)
                return user;
        }
        //if no agent profile found throw an exception
        throw new NoRecordsFetchedException( "No agent found" );
    }
    
    /**
     *  Method to get a map of userId - review count given a list of userIds
     * @param userIds
     * @return
     * @throws InvalidInputException
     */
    @Override
    @Transactional
    public Map<Long, Integer> getUserIdReviewCountMapFromUserIdList( List<Long> userIds ) throws InvalidInputException
    {

        List<User> userList = userDao.getUsersForUserIds( userIds );
        Map<Long, Integer> userIdReviewCountMap = new HashMap<Long, Integer>();
        for ( User user : userList ) {
            if ( user.getIsZillowConnected() == CommonConstants.YES ) {
                userIdReviewCountMap.put( user.getUserId(), user.getZillowReviewCount() );
            }
        }
        return userIdReviewCountMap;
    }


    /**
     * Method to search users in company by criteria
     * @param queries
     * @return
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     */
    @Override
    @Transactional
    public List<User> searchUsersInCompanyByMultipleCriteria( Map<String, Object> queries )
        throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "Method searchUsersInCompanyByMultipleCriteria started." );

        if ( queries == null || queries.isEmpty() ) {
            throw new InvalidInputException( "The search criteria cannot be empty" );
        }
        List<User> users = userDao.findByKeyValueAscending( User.class, queries, CommonConstants.FIRST_NAME );

        if ( users == null || users.isEmpty() ) {
            throw new NoRecordsFetchedException( "No users found for the specified criteria" );
        }
        LOG.debug( "Method searchUsersInCompanyByMultipleCriteria finished." );
        return users;
    }


    /**
     * Method to get User Profile for user id where user is agent
     * @throws InvalidInputException
     * */
    @Override
    @Transactional
    public UserProfile getAgentUserProfileForUserId( long userId ) throws InvalidInputException
    {
        if ( userId == 0 ) {
            LOG.error( "Invalid user id is passed in getAgentUserProfileForUserId" );
            throw new InvalidInputException( "Invalid user id is passed in getAgentUserProfileForUserId" );
        }
        UserProfile agentUserProfile = null;
        User user = userDao.findById( User.class, userId );
        if ( user == null ) {
            LOG.error( "User does not exist for user id : " + userId );
            throw new InvalidInputException( "User does not exist for user id : " + userId );
        }
        for ( UserProfile userProfile : user.getUserProfiles() ) {
            if ( userProfile.getAgentId() == userId && userProfile.getIsPrimary() == CommonConstants.YES
                && userProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) {
                agentUserProfile = userProfile;
                break;
            }
            if ( userProfile.getAgentId() == userId
                && userProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) {
                agentUserProfile = userProfile;
            }
        }
        return agentUserProfile;
    }


    /**
     * Method to add a new user into a company. Admin sends the invite to user for registering.
     * @throws UndeliveredEmailException 
     */
    @Transactional
    @Override
    public User createSocialSurveyAdmin( User admin, String firstName, String lastName, String emailId )
        throws InvalidInputException, UserAlreadyExistsException, UndeliveredEmailException
    {
        if ( firstName == null || firstName.isEmpty() ) {
            throw new InvalidInputException( "First name is either null or empty in inviteUserToRegister()." );
        }
        if ( emailId == null || emailId.isEmpty() ) {
            throw new InvalidInputException( "Email Id is either null or empty in inviteUserToRegister()." );
        }
        LOG.debug( "Method to add a new user, inviteUserToRegister() called for email id : " + emailId );

        if ( userExists( emailId ) ) {
            throw new UserAlreadyExistsException( "User with User ID : " + emailId + " already exists" );
        }

        String password = utils.generateRandomAlphaNumericString();
        String encryptedPassword = encryptionHelper.encryptSHA512( password );
        User user = createUser( admin.getCompany(), encryptedPassword, emailId, firstName, lastName,
            CommonConstants.STATUS_ACTIVE, CommonConstants.STATUS_ACTIVE, CommonConstants.ADMIN_USER_NAME );
        user = userDao.save( user );

        UserProfile userProfileNew = createUserProfile( user, admin.getCompany(), user.getEmailId(), user.getUserId(), 0, 0,
            CommonConstants.PROFILES_MASTER_SS_ADMIN_PROFILE_ID, CommonConstants.IS_PRIMARY_TRUE,
            CommonConstants.PROFILE_STAGES_COMPLETE, CommonConstants.STATUS_ACTIVE, String.valueOf( admin.getUserId() ),
            String.valueOf( admin.getUserId() ) );

        userProfileDao.save( userProfileNew );
        sendInviteMailToSocialSurveyAdmin( emailId, user.getFirstName() + " " + user.getLastName(),
            admin.getCompany().getCompanyId() );


        LOG.debug( "Method to add a new user, inviteUserToRegister finished for email id : " + emailId );
        return user;
    }


    private void sendInviteMailToSocialSurveyAdmin( String emailId, String name, long companyId )
        throws InvalidInputException, UndeliveredEmailException
    {
        Map<String, String> urlParams = new HashMap<String, String>();
        urlParams.put( CommonConstants.EMAIL_ID, emailId );
        urlParams.put( CommonConstants.COMPANY, companyId + "" );
        urlParams.put( NAME, name );
        urlParams.put( CommonConstants.URL_PARAM_RESET_PASSWORD, CommonConstants.URL_PARAM_RESETORSET_VALUE_SET );

        LOG.debug( "Generating URL" );
        String url = urlGenerator.generateUrl( urlParams, applicationBaseUrl + CommonConstants.RESET_PASSWORD );

        emailServices.sendInvitationToSocialSurveyAdmin( url, emailId, name, emailId );

    }


    @Override
    public List<User> getSocialSurveyAdmins( User admin )
    {
        Map<String, Object> queries = new HashMap<String, Object>();
        queries.put( CommonConstants.COMPANY, admin.getCompany() );
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
        List<User> userList = userDao.findByKeyValue( User.class, queries );
        List<User> ssAdminList = new ArrayList<User>();
        for ( User user : userList ) {
            List<UserProfile> userProfiles = user.getUserProfiles();
            for ( UserProfile userProfile : userProfiles ) {
                if ( userProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_SS_ADMIN_PROFILE_ID
                    && userProfile.getStatus() == CommonConstants.STATUS_ACTIVE )
                    ssAdminList.add( user );
            }
        }
        return ssAdminList;
    }


    @Override
    @Transactional
    public void deleteSSAdmin( User admin, long ssAdminId ) throws InvalidInputException
    {

        LOG.debug( "Method to deleteSSAdmin user {} called.", ssAdminId );
        User userToBeDeactivated = userDao.findById( User.class, ssAdminId );
        if ( admin == null ) {
            throw new InvalidInputException( "Passed parameter admin is null" );
        }
        if ( userToBeDeactivated == null ) {
            throw new InvalidInputException( "No user found in databse for user id : " + ssAdminId );
        }

        // Create an entry into the RemovedUser table for keeping historical records of users.
        RemovedUser removedUser = new RemovedUser();
        removedUser.setCompany( userToBeDeactivated.getCompany() );
        removedUser.setUser( userToBeDeactivated );
        removedUser.setCreatedBy( String.valueOf( admin.getUserId() ) );
        removedUser.setCreatedOn( new Timestamp( System.currentTimeMillis() ) );
        removedUserDao.save( removedUser );

        userProfileDao.deactivateAllUserProfilesForUser( admin, userToBeDeactivated, CommonConstants.STATUS_INACTIVE );

        userToBeDeactivated.setLoginName( userToBeDeactivated.getLoginName() + "_" + System.currentTimeMillis() );
        userToBeDeactivated.setStatus( CommonConstants.STATUS_INACTIVE );
        userToBeDeactivated.setModifiedBy( String.valueOf( admin.getUserId() ) );
        userToBeDeactivated.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );

        LOG.debug( "Deactivating user {}", userToBeDeactivated.getFirstName() );
        userDao.update( userToBeDeactivated );
    }


    @Override
    public User saveEmailUserMapping( String emailId, long userId , String createdAndModifiedBy) throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "Method to saveEmailUserMapping for : " + emailId + " started." );
        if ( emailId == null || emailId.isEmpty() ) {
            throw new InvalidInputException( "Email id is null or empty" );
        }
        User user = userDao.findById( User.class, userId );

        if ( user == null ) {
            throw new InvalidInputException( "No user found for agent id : " + userId );
        }

        UserEmailMapping userEmailMapping = new UserEmailMapping();
        userEmailMapping.setCompany( user.getCompany() );
        userEmailMapping.setEmailId( emailId );
        userEmailMapping.setUser( user );
        userEmailMapping.setStatus( CommonConstants.STATUS_ACTIVE );

        userEmailMapping.setCreatedOn( new Timestamp( System.currentTimeMillis() ) );
        //TODO : Modify createdBy and modifiedBy to store the actual admin's ID
        userEmailMapping.setCreatedBy( createdAndModifiedBy );
        userEmailMapping.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        userEmailMapping.setModifiedBy( createdAndModifiedBy );
        userEmailMappingDao.save( userEmailMapping );
        return user;
    }


    @Override
    public CompanyIgnoredEmailMapping saveIgnoredEmailCompanyMapping( String emailId, long companyId )
        throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "Method to saveIgnoredEmailCompanyMapping for  : " + emailId + " started." );
        if ( emailId == null || emailId.isEmpty() ) {
            throw new InvalidInputException( "Email id is null or empty" );
        }
        Company company = companyDao.findById( Company.class, companyId );

        if ( company == null ) {
            throw new InvalidInputException( "No company found for company id : " + companyId );
        }

        CompanyIgnoredEmailMapping companyIgnoredEmailMapping = new CompanyIgnoredEmailMapping();


        //check if entry is already there with the eamil id
        Map<String, Object> queries = new HashMap<String, Object>();
        queries.put( CommonConstants.EMAIL_ID, emailId );
        queries.put( CommonConstants.COMPANY, company );
        List<CompanyIgnoredEmailMapping> CompanyIgnoredEmailMappingList = companyIgnoredEmailMappingDao
            .findByKeyValue( CompanyIgnoredEmailMapping.class, queries );

        if ( CompanyIgnoredEmailMappingList != null && CompanyIgnoredEmailMappingList.size() > 0 ) {
            companyIgnoredEmailMapping = CompanyIgnoredEmailMappingList.get( CommonConstants.INITIAL_INDEX );
            companyIgnoredEmailMapping.setStatus( CommonConstants.STATUS_ACTIVE );
            companyIgnoredEmailMapping.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        } else {
            companyIgnoredEmailMapping.setCompany( company );
            companyIgnoredEmailMapping.setEmailId( emailId );
            companyIgnoredEmailMapping.setStatus( CommonConstants.STATUS_ACTIVE );

            companyIgnoredEmailMapping.setCreatedOn( new Timestamp( System.currentTimeMillis() ) );
            companyIgnoredEmailMapping.setCreatedBy( "ADMIN" );
            companyIgnoredEmailMapping.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
            companyIgnoredEmailMapping.setModifiedBy( "ADMIN" );
        }

        companyIgnoredEmailMapping = companyIgnoredEmailMappingDao.saveOrUpdate( companyIgnoredEmailMapping );

        return companyIgnoredEmailMapping;
    }


    @Transactional
    @Override
    public UserList getUsersAndEmailMappingForCompany( long companyId, int startIndex, int batchSize , long count)
        throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "Method to getUsersAndEmailMappingForCompant for  companyId : " + companyId + " started." );
        UserList userList = new UserList();
        Company company = companyDao.findById( Company.class, companyId );

        if ( company == null ) {
            throw new InvalidInputException( "No company found for company id : " + companyId );
        }
        List<User> usersFromSearch = userDao.getUsersAndEmailMappingForCompany( company, startIndex, batchSize );
        List<User> users = new ArrayList<User>();
        for ( User user : usersFromSearch ) {
            User userVO = new User();
            userVO.setUserId( user.getUserId() );
            userVO.setFirstName( user.getFirstName() );
            userVO.setLastName( user.getLastName() );
            userVO.setEmailId( user.getEmailId() );
            userVO.setLoginName( user.getLoginName() );

            StringBuilder mappedEmails = new StringBuilder();
            for ( UserEmailMapping emailMapping : user.getUserEmailMappings() ) {
                if ( emailMapping.getStatus() == CommonConstants.STATUS_ACTIVE ) {
                    mappedEmails.append( emailMapping.getEmailId() );
                    mappedEmails.append( ", " );
                }
            }
            String mappedEmailsString = mappedEmails.toString();
            if ( mappedEmailsString != null && mappedEmailsString.contains( "," ) )
                mappedEmailsString = mappedEmailsString.substring( 0, mappedEmailsString.lastIndexOf( "," ) );
            userVO.setMappedEmails( mappedEmailsString );

            users.add( userVO );
        }
        userList.setUsers( users );
        if(count == -1){
            userList.setTotalRecord( userDao.getCountOfUsersAndEmailMappingForCompany( company ) ); 
        }else{
            userList.setTotalRecord( count );
        }

        return userList;
    }


    @Transactional
    @Override
    public List<UserEmailMapping> getUserEmailMappingsForUser( long agentId )
        throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "Method to getUserEmailMappingsForUser for  agentId : " + agentId + " started." );

        User user = userDao.findById( User.class, agentId );

        if ( user == null ) {
            throw new InvalidInputException( "No user found for agent id : " + agentId );
        }


        List<UserEmailMapping> emailMappings = userEmailMappingDao.findByColumn( UserEmailMapping.class,
            CommonConstants.USER_COLUMN, user );
        List<UserEmailMapping> emailMappingsVO = new ArrayList<UserEmailMapping>();
        for ( UserEmailMapping emailMapping : emailMappings ) {
            UserEmailMapping emailMappingVO = new UserEmailMapping();
            emailMappingVO.setUserEmailMappingId( emailMapping.getUserEmailMappingId() );
            emailMappingVO.setEmailId( emailMapping.getEmailId() );
            emailMappingVO.setCreatedOn( emailMapping.getCreatedOn() );
            emailMappingVO.setStatus( emailMapping.getStatus() );

            emailMappingsVO.add( emailMappingVO );
        }

        return emailMappingsVO;
    }


    @Transactional
    @Override
    public void deleteUserEmailMapping( User agent, long emailMappingId ) throws InvalidInputException
    {
        LOG.debug( "Method to deleteUserEmailMapping for  emailMappingId : " + emailMappingId + " started." );
        if ( agent == null ) {
            throw new InvalidInputException( "Passed parameter agent is null " );
        }

        Map<String, Object> queries = new HashMap<String, Object>();
        queries.put( "userEmailMappingId", emailMappingId );
        List<UserEmailMapping> userEmailMappings = userEmailMappingDao.findByKeyValue( UserEmailMapping.class, queries );


        if ( userEmailMappings == null || userEmailMappings.size() <= 0 || userEmailMappings.get( 0 ) == null ) {
            throw new InvalidInputException( "No userEmailMapping found for emailMapping id : " + emailMappingId );
        }

        UserEmailMapping userEmailMapping = userEmailMappings.get( 0 );
        userEmailMapping.setStatus( CommonConstants.STATUS_INACTIVE );
        userEmailMapping.setModifiedBy( String.valueOf( agent.getUserId() ) );
        userEmailMapping.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        userEmailMappingDao.update( userEmailMapping );

        LOG.debug( "Method to deleteUserEmailMapping for  emailMappingId : " + emailMappingId + " ended." );
    }


    @Transactional
    @Override
    public void updateUserEmailMapping( String modifiedBy, long emailMappingId, int status ) throws InvalidInputException
    {
        LOG.debug( "Method to updateUserEmailMapping for  emailMappingId : " + emailMappingId + " started." );

        Map<String, Object> queries = new HashMap<String, Object>();
        queries.put( "userEmailMappingId", emailMappingId );
        List<UserEmailMapping> userEmailMappings = userEmailMappingDao.findByKeyValue( UserEmailMapping.class, queries );


        if ( userEmailMappings == null || userEmailMappings.size() <= 0 || userEmailMappings.get( 0 ) == null ) {
            throw new InvalidInputException( "No userEmailMapping found for emailMapping id : " + emailMappingId );
        }

        UserEmailMapping userEmailMapping = userEmailMappings.get( 0 );
        userEmailMapping.setStatus( status );
        userEmailMapping.setModifiedBy( modifiedBy );
        userEmailMapping.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        userEmailMappingDao.update( userEmailMapping );

        LOG.debug( "Method to deleteUserEmailMapping for  emailMappingId : " + emailMappingId + " ended." );
    }
    
    @Transactional
    @Override
    public void updateUserEmailMapping( UserEmailMapping userEmailMapping ) throws InvalidInputException
    {
        LOG.debug( "Method to updateUserEmailMapping started." );
        if ( userEmailMapping == null ) {
            throw new InvalidInputException( "Passed parameter userEmailMapping is null " );
        }
         
        long userEmailMappingId = userEmailMapping.getUserEmailMappingId();
        userEmailMapping = userEmailMappingDao.findById( UserEmailMapping.class, userEmailMapping.getUserEmailMappingId() );


        if ( userEmailMapping == null ) {
            throw new InvalidInputException( "No userEmailMapping found for emailMapping id : " + userEmailMappingId );
        }

        userEmailMappingDao.update( userEmailMapping );

        LOG.debug( "Method to  update UserEmailMapping ended." );
    }


    @Transactional
    @Override
    public void deleteIgnoredEmailMapping( String emailId ) throws InvalidInputException
    {
        LOG.debug( "method deleteIgnoredEmailMapping  started for email id : " + emailId );

        if ( emailId == null || emailId.isEmpty() ) {
            throw new InvalidInputException( "Passed parameter emailId is invalid" );
        }

        List<CompanyIgnoredEmailMapping> ignoredEmails = companyIgnoredEmailMappingDao
            .findByColumn( CompanyIgnoredEmailMapping.class, CommonConstants.EMAIL_ID, emailId );
        if ( ignoredEmails != null && !ignoredEmails.isEmpty() ) {
            for ( CompanyIgnoredEmailMapping ignoredEmail : ignoredEmails ) {
                ignoredEmail.setStatus( CommonConstants.STATUS_INACTIVE );
                ignoredEmail.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
                companyIgnoredEmailMappingDao.update( ignoredEmail );
            }
        }

        LOG.debug( "method deleteIgnoredEmailMapping finished" );
    }


    @Transactional
    @Override
    public boolean isUserSocialSurveyAdmin( long userId ) throws InvalidInputException
    {
        LOG.debug( "method isUserIsSocialSurveyAdmin  started for userId : " + userId );

        User user = null;
        user = userDao.findById( User.class, userId );
        if ( user == null ) {
            throw new InvalidInputException( "User not found for userId:" + userId );
        }
        return isUserSocialSurveyAdmin( user );
    }


    @Override
    @Transactional
    public void deleteUserDataFromAllSources( User loggedInUser, long userIdToBeDeleted, int status, boolean isForHierarchyUpload, boolean isDeletedByRealtechOrSSAdmin )
        throws InvalidInputException, SolrException
    {
        LOG.debug( "Method deleteUserDataFromAllSources called for userId:" + userIdToBeDeleted );
        
        //send user deletion mail
        if( !isForHierarchyUpload && !isDeletedByRealtechOrSSAdmin ) {
            try {
                organizationManagementService.sendUserDeletionMail( loggedInUser, getUserByUserId( userIdToBeDeleted ) );
            } catch ( NoRecordsFetchedException error ) {
                LOG.warn( "Unable to send user deletion mail" );
                throw new InvalidInputException( "unable to send user deletion mail", error );
            } catch( UndeliveredEmailException sendgridError ) {
                LOG.warn( "Unable to send user deletion mail" );
                // continue            
            }
        }

        // Removing user data from MySql DB & MongoDB.
        this.removeExistingUser( loggedInUser, userIdToBeDeleted, status );

        // Updating user count modification notification.
        this.updateUserCountModificationNotification( loggedInUser.getCompany() );

        // Removing user data from solr.
        solrSearchService.removeUserFromSolr( userIdToBeDeleted );

        LOG.debug( "Method deleteUserDataFromAllSources executed successfully" );
    }


    @Override
    public void crmDataAgentIdMApper()
    {
        try {
            // update last start time
            batchTrackerService.getLastRunEndTimeAndUpdateLastStartTimeByBatchType(
                CommonConstants.BATCH_TYPE_CRM_DATA_AGENT_ID_MAPPER, CommonConstants.BATCH_NAME_CRM_DATA_AGENT_ID_MAPPER );

            Map<String, Object> corruptRecords = surveyHandler.mapAgentsInSurveyPreInitiation();
            sendCorruptDataFromCrmNotificationMail( corruptRecords );

            //updating last run time for batch in database
            batchTrackerService.updateLastRunEndTimeByBatchType( CommonConstants.BATCH_TYPE_CRM_DATA_AGENT_ID_MAPPER );
            LOG.debug( "Completed CrmDataAgentIdMapper" );
        } catch ( Exception e ) {
            LOG.error( "Error in CrmDataAgentIdMapper", e );
            try {
                //update batch tracker with error message
                batchTrackerService.updateErrorForBatchTrackerByBatchType( CommonConstants.BATCH_TYPE_CRM_DATA_AGENT_ID_MAPPER,
                    e.getMessage() );
                //send report bug mail to admin
                batchTrackerService.sendMailToAdminRegardingBatchError( CommonConstants.BATCH_NAME_CRM_DATA_AGENT_ID_MAPPER,
                    System.currentTimeMillis(), e );
            } catch ( NoRecordsFetchedException | InvalidInputException e1 ) {
                LOG.error( "Error while updating error message in CrmDataAgentIdMapper " );
            } catch ( UndeliveredEmailException e1 ) {
                LOG.error( "Error while sending report excption mail to admin " );
            }
        }
    }


    @SuppressWarnings ( "unchecked")
    private void sendCorruptDataFromCrmNotificationMail( Map<String, Object> corruptRecords )
    {
        List<SurveyPreInitiation> unavailableAgents = (List<SurveyPreInitiation>) corruptRecords.get( "unavailableAgents" );
        List<SurveyPreInitiation> invalidAgents = (List<SurveyPreInitiation>) corruptRecords.get( "invalidAgents" );
        List<SurveyPreInitiation> customersWithoutName = (List<SurveyPreInitiation>) corruptRecords
            .get( "customersWithoutName" );
        List<SurveyPreInitiation> customersWithoutEmailId = (List<SurveyPreInitiation>) corruptRecords
            .get( "customersWithoutEmailId" );

        List<SurveyPreInitiation> ignoredEmailRecords = (List<SurveyPreInitiation>) corruptRecords.get( "ignoredEmailRecords" );
        List<SurveyPreInitiation> oldRecords = (List<SurveyPreInitiation>) corruptRecords.get( "oldRecords" );

        Set<Long> companies = (Set<Long>) corruptRecords.get( "companies" );

        for ( Long companyId : companies ) {
            int rownum = 1;
            int count = 1;
            boolean excelCreated = false;
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet( "Corrupt Records" );
            sheet = fillHeaders( sheet );

            for ( SurveyPreInitiation survey : unavailableAgents ) {

                if ( survey.getCompanyId() == companyId ) {
                    Row row = sheet.createRow( rownum++ );
                    row = fillCellsInRow( row, survey, count++, "Agent Not Available For This Organization " );
                }
            }
            for ( SurveyPreInitiation survey : invalidAgents ) {
                if ( survey.getCompanyId() == companyId ) {
                    Row row = sheet.createRow( rownum++ );
                    row = fillCellsInRow( row, survey, count++, "Agent Does Not Exist" );
                }
            }
            for ( SurveyPreInitiation survey : customersWithoutName ) {
                if ( survey.getCompanyId() == companyId ) {
                    Row row = sheet.createRow( rownum++ );
                    row = fillCellsInRow( row, survey, count++, "Customer Name Is Not Present" );
                }
            }
            for ( SurveyPreInitiation survey : customersWithoutEmailId ) {
                if ( survey.getCompanyId() == companyId ) {
                    Row row = sheet.createRow( rownum++ );
                    row = fillCellsInRow( row, survey, count++, "Customer Email Id Is Not Present" );
                }
            }

            for ( SurveyPreInitiation survey : ignoredEmailRecords ) {
                if ( survey.getCompanyId() == companyId ) {
                    Row row = sheet.createRow( rownum++ );
                    row = fillCellsInRow( row, survey, count++, "Agent Email Id is ignored" );
                }
            }

            for ( SurveyPreInitiation survey : oldRecords ) {
                if ( survey.getCompanyId() == companyId ) {
                    Row row = sheet.createRow( rownum++ );
                    row = fillCellsInRow( row, survey, count++, "Old record" );
                }
            }

            String fileName = companyId + "_" + System.currentTimeMillis();
            FileOutputStream fileOutput = null;
            InputStream inputStream = null;
            File file = null;
            String filePath = null;
            try {
                file = new File( fileDirectoryLocation + File.separator + fileName + ".xls" );
                fileOutput = new FileOutputStream( file );
                file.createNewFile();
                workbook.write( fileOutput );
                filePath = file.getPath();
                excelCreated = true;
            } catch ( FileNotFoundException fe ) {
                LOG.error( "Exception caught " + fe.getMessage() );
                excelCreated = false;
            } catch ( IOException e ) {
                LOG.error( "Exception caught " + e.getMessage() );
                excelCreated = false;
            } finally {
                try {
                    if ( fileOutput != null ) {
                        fileOutput.close();
                    }
                    if ( inputStream != null ) {
                        inputStream.close();
                    }
                } catch ( IOException e ) {
                    LOG.error( "Exception caught " + e.getMessage() );
                    excelCreated = false;
                }
            }
            
            
            boolean excelUploaded = false;
            if ( excelCreated ) {
                try {
                    filePath = fileUploadService.uploadOldReport( file, fileName );
                    
                    excelUploaded = true;
                } catch ( NonFatalException e ) {
                    LOG.error( "Exception caught while uploading old report", e);
                }
                LOG.debug( "fileUpload on s3 step is done for filename : {}", fileName );
            } else {
                LOG.warn( "Could not write into file {}", fileName );
            }

            try {
                if ( excelCreated && excelUploaded) {
                    List<EmailAttachment> attachments = new ArrayList<EmailAttachment>();
                    attachments.add( new EmailAttachment("CorruptRecords.xls", filePath) );
                    
                    if ( companyAdminEnabled == "1" ) {
                        User companyAdmin = getCompanyAdmin( companyId );
                        if ( companyAdmin != null ) {
                            emailServices.sendCorruptDataFromCrmNotificationMail( companyAdmin.getFirstName(),
                                companyAdmin.getLastName(), companyAdmin.getEmailId(), attachments);
                        }
                    } else {
                        emailServices.sendCorruptDataFromCrmNotificationMail( adminName, "", adminEmailId, attachments );
                    }
                }
            } catch ( InvalidInputException | UndeliveredEmailException e ) {
                LOG.error( "Exception caught in sendCorruptDataFromCrmNotificationMail() while sending mail to company admin" );
            }
        }
    }


    private Row fillCellsInRow( Row row, SurveyPreInitiation survey, int counter, String reasonForFailure )
    {
        int cellnum = 0;
        Cell cell1 = row.createCell( cellnum++ );
        cell1.setCellValue( counter );
        Cell cell2 = row.createCell( cellnum++ );
        cell2.setCellValue( survey.getSurveySource() );
        Cell cell3 = row.createCell( cellnum++ );
        if ( survey.getAgentEmailId() != null ) {
            cell3.setCellValue( survey.getAgentEmailId() );
        } else {
            cell3.setCellValue( "" );
        }
        Cell cell4 = row.createCell( cellnum++ );
        if ( survey.getCustomerFirstName() != null ) {
            cell4.setCellValue( survey.getCustomerFirstName() );
        } else {
            cell4.setCellValue( "" );
        }
        Cell cell5 = row.createCell( cellnum++ );
        if ( survey.getCustomerLastName() != null ) {
            cell5.setCellValue( survey.getCustomerLastName() );
        } else {
            cell5.setCellValue( "" );
        }
        Cell cell6 = row.createCell( cellnum++ );
        if ( survey.getCustomerEmailId() != null ) {
            cell6.setCellValue( survey.getCustomerEmailId() );
        } else {
            cell6.setCellValue( "" );
        }
        Cell cell7 = row.createCell( cellnum++ );
        cell7.setCellValue( reasonForFailure );
        return row;

    }


    public HSSFSheet fillHeaders( HSSFSheet sheet )
    {
        int cellnum = 0;
        Row row = sheet.createRow( 0 );
        Cell cell1 = row.createCell( cellnum++ );
        cell1.setCellValue( "S.No" );
        Cell cell2 = row.createCell( cellnum++ );
        cell2.setCellValue( "Source" );
        Cell cell3 = row.createCell( cellnum++ );
        cell3.setCellValue( "Agent Email Id" );
        Cell cell4 = row.createCell( cellnum++ );
        cell4.setCellValue( "Customer First Name" );
        Cell cell5 = row.createCell( cellnum++ );
        cell5.setCellValue( "Customer Last Name" );
        Cell cell6 = row.createCell( cellnum++ );
        cell6.setCellValue( "Customer Email Id" );
        Cell cell7 = row.createCell( cellnum++ );
        cell7.setCellValue( "Reason For Failure" );
        return sheet;
    }


    @Override
    @Transactional
    public User getAdminUserByCompanyId( long companyId )
    {
        Map<String, Object> queries = new HashMap<>();
        queries.put( "company.companyId", companyId );
        queries.put( "isOwner", CommonConstants.STATUS_ACTIVE );
        List<User> user = userDao.findByKeyValue( User.class, queries );
        if ( user != null && !user.isEmpty() ) {
            return user.get( CommonConstants.INITIAL_INDEX );
        }
        return null;
    }


    @Override
    public void incompleteSurveyReminderSender()
    {
        try {
            //update last run start time
            batchTrackerService.getLastRunEndTimeAndUpdateLastStartTimeByBatchType(
                CommonConstants.BATCH_TYPE_INCOMPLETE_SURVEY_REMINDER_SENDER,
                CommonConstants.BATCH_NAME_INCOMPLETE_SURVEY_REMINDER_SENDER );

            LOG.info( "Batch incompleteSurveyReminderSender started" );

            for ( Company company : organizationManagementService.getAllCompanies() ) {

                LOG.info( "getting reminder information for company with id {}", company.getCompanyId() );

                Map<String, Object> reminderMap = surveyHandler.getReminderInformationForCompany( company.getCompanyId() );
                int reminderInterval = (int) reminderMap.get( CommonConstants.SURVEY_REMINDER_INTERVAL );
                int reminderCount = (int) reminderMap.get( CommonConstants.SURVEY_REMINDER_COUNT );
                boolean isReminderDisabled = (boolean) reminderMap.get( CommonConstants.IS_SURVEY_REMINDER_DISABLED );

                //getting epoch date
                SimpleDateFormat sdf = new SimpleDateFormat( "dd/MM/yyyy" );
                Date epochReminderDate = sdf.parse( CommonConstants.EPOCH_REMINDER_TIME );

                //get survey list to send invitation mail
                LOG.info( "getting survey list to send invitation mail for company with id {}", company.getCompanyId() );
                List<SurveyPreInitiation> surveysForInvitationMail = surveyHandler.getSurveyListToSendInvitationMail( company,
                    epochReminderDate );
                LOG.info( "Found {} surveysForInvitationMail for company id {}",
                    ( surveysForInvitationMail != null ? surveysForInvitationMail.size() : 0 ), company.getCompanyId() );

                //iterating through the surveys
                for ( SurveyPreInitiation survey : surveysForInvitationMail ) {
                    LOG.info( "iterating through the surveys" );
                    //TODO: remove this check
                    long DAY_IN_MS = 1000 * 60 * 60 * 24;
                    Date CRITERIA_DATE = new Date( System.currentTimeMillis() - ( 365 * DAY_IN_MS ) );
                    if ( survey.getCreatedOn().before( new Timestamp( CRITERIA_DATE.getTime() ) ) ) {
                        LOG.info( "First loop : skipping survey for survey pre id {}", survey.getSurveyPreIntitiationId() );
                        continue;
                    }

                    User user = null;
                    try {
                        user = getUserByUserId( survey.getAgentId() );
                    } catch ( InvalidInputException ie ) {
                        LOG.warn( "Invalid user mapped to the agent id" );
                        continue;
                    }
                    //If agent is deleted, mark survey as corrupt and fetch next survey
                    if ( user != null && checkIfSurveyAgentIsDeleted( user, survey ) ) {
                        LOG.debug( "The agent id : {} is deleted. Skipping record.", survey.getAgentId() );
                        continue;
                    }

                    //check if we have send invitation mail already
                    if ( survey.getLastReminderTime().compareTo( epochReminderDate ) > 0 ) {
                        LOG.warn( "We have already send invitation mail for customer {}", survey.getCustomerEmailId() );
                        continue;
                    }

                    //Check if customer email id is unsubscribed.
                    if ( unsubscribeService.isUnsubscribed( survey.getCustomerEmailId(), survey.getCompanyId() ) ) {
                        LOG.debug( "Customer has unsubscribed his email {} either for the company {} or for social survey.",
                            survey.getCustomerEmailId(), survey.getCompanyId() );
                        continue;
                    }

                    try {
                        LOG.debug( "Sending survey initiation mail" );
                        surveyHandler.prepareAndSendInvitationMail( survey );
                        surveyHandler.markSurveyAsSent( survey );
                        surveyHandler.updateReminderCount( survey.getSurveyPreIntitiationId(), false );

                    } catch ( ProfileNotFoundException | InvalidInputException e ) {
                        LOG.error(
                            "ProfileNotFoundException / InvalidInputException caught in executeInternal() method of IncompleteSurveyReminderSender. Nested exception is ",
                            e );
                    }
                }

                //Do not send reminder email if it is disabled for company
                if ( isReminderDisabled ) {
                    LOG.info( "Auto Reminder is disabled for company : {}", company.getCompanyId() );
                    continue;
                }

                //getting minLastReminderTime and maxLastReminderTime
                long currentTime = System.currentTimeMillis();
                Map<String, Date> lastReminderTimeCriterias = surveyHandler.getMinMaxLastSurveyReminderTime( currentTime,
                    reminderInterval );
                Date minLastReminderTime = lastReminderTimeCriterias.get( "minLastReminderTime" );
                Date maxLastReminderTime = lastReminderTimeCriterias.get( "maxLastReminderTime" );
                LOG.info( "minLastReminderTime is {} and maxLastReminderTime is {} for company with id {}", minLastReminderTime,
                    maxLastReminderTime, company.getCompanyId() );

                //get survey list to send survey reminder mail
                LOG.info( "getting survey list to send survey reminder mail for company with id {}", company.getCompanyId() );
                List<SurveyPreInitiation> incompleteSurveyCustomers = surveyHandler
                    .getIncompleteSurveyForReminderEmail( company, minLastReminderTime, maxLastReminderTime, reminderCount );
                LOG.info( "Found {} incompleteSurveyCustomers for company id {}",
                    ( incompleteSurveyCustomers != null ? incompleteSurveyCustomers.size() : 0 ), company.getCompanyId() );

                for ( SurveyPreInitiation survey : incompleteSurveyCustomers ) {
                    LOG.debug( "Processing survey pre initiation id: {}", survey.getSurveyPreIntitiationId() );

                    //TODO: remove this check
                    long DAY_IN_MS = 1000 * 60 * 60 * 24;
                    Date CRITERIA_DATE = new Date( System.currentTimeMillis() - ( 15 * DAY_IN_MS ) );
                    if ( survey.getCreatedOn().before( new Timestamp( CRITERIA_DATE.getTime() ) ) ) {
                        continue;
                    }

                    User user = null;
                    try {
                        user = getUserByUserId( survey.getAgentId() );
                    } catch ( InvalidInputException ie ) {
                        LOG.warn( "Invalid user mapped to the agent id" );
                        continue;
                    }
                    //If agent is deleted, mark survey as corrupt and fetch next survey
                    if ( user != null && checkIfSurveyAgentIsDeleted( user, survey ) ) {
                        LOG.debug( "The agent id : {} is deleted. Skipping record.", survey.getAgentId() );
                        continue;
                    }

                    //check if we have send reminder mail already
                    if ( survey.getReminderCounts() >= reminderCount ) {
                        LOG.warn( "We have already send max reminder mail for customer {}", survey.getCustomerEmailId() );
                        continue;
                    }

                    try {
                        LOG.info( "Sending Survey Reminder Email." );
                        // send a survey invitation mail if reminder is false or a reminder mail if reminder is true
                        surveyHandler.sendSurveyReminderEmail( survey );
                        surveyHandler.markSurveyAsSent( survey );
                        surveyHandler.updateReminderCount( survey.getSurveyPreIntitiationId(), true );
                    } catch ( ProfileNotFoundException | InvalidInputException e ) {
                        LOG.error(
                            "ProfileNotFoundException / InvalidInputException caught in executeInternal() method of IncompleteSurveyReminderSender. Nested exception is ",
                            e );
                    }
                }
            }
            LOG.info( "Completed IncompleteSurveyReminderSender" );
            //Update last build time in batch tracker table
            batchTrackerService.updateLastRunEndTimeByBatchType( CommonConstants.BATCH_TYPE_INCOMPLETE_SURVEY_REMINDER_SENDER );
        } catch ( Exception e ) {
            LOG.error( "Error in IncompleteSurveyReminderSender", e );
            try {
                //update batch tracker with error message
                batchTrackerService.updateErrorForBatchTrackerByBatchType(
                    CommonConstants.BATCH_TYPE_INCOMPLETE_SURVEY_REMINDER_SENDER, e.getMessage() );
                //send report bug mail to admin
                batchTrackerService.sendMailToAdminRegardingBatchError(
                    CommonConstants.BATCH_NAME_INCOMPLETE_SURVEY_REMINDER_SENDER, System.currentTimeMillis(), e );
            } catch ( NoRecordsFetchedException | InvalidInputException e1 ) {
                LOG.error( "Error while updating error message in IncompleteSurveyReminderSender " );
            } catch ( UndeliveredEmailException e1 ) {
                LOG.error( "Error while sending report excption mail to admin " );
            }
        }
    }


    @Transactional
    @Override
    public User activateCompanyAdmin( User companyAdmin )
        throws InvalidInputException, HierarchyAlreadyExistsException, SolrException
    {
        LOG.debug( "UserManagementService.activateCompanyAdmin started" );
        //Update the USER table status
        companyAdmin.setStatus( CommonConstants.STATUS_ACTIVE );
        companyAdmin.setIsAtleastOneUserprofileComplete( 1 );
        userDao.update( companyAdmin );

        //Update the user's user profiles
        userProfileDao.activateAllUserProfilesForUser( companyAdmin );

        //Activate agent in mongo
        AgentSettings agentSettings = getUserSettings( companyAdmin.getUserId() );
        organizationUnitSettingsDao.updateParticularKeyAgentSettings( CommonConstants.STATUS_COLUMN,
            CommonConstants.STATUS_ACTIVE_MONGO, agentSettings );

        //Activate agent in Solr
        solrSearchService.editUserInSolr( companyAdmin.getUserId(), CommonConstants.STATUS_COLUMN,
            String.valueOf( CommonConstants.STATUS_ACTIVE ) );
        LOG.debug( "UserManagementService.activateCompanyAdmin finished" );
        return companyAdmin;
    }


    /**
     * Method to check if agent is deleted and mark the corresponding survey as corrupted, if it is.
     *
     * @param user
     * @param survey
     * @return
     */
    private boolean checkIfSurveyAgentIsDeleted( User user, SurveyPreInitiation survey )
    {
        //If user is deleted, mark the survey status as corrupt
        if ( user.getStatus() == CommonConstants.STATUS_INACTIVE ) {
            survey.setStatus( CommonConstants.STATUS_SURVEYPREINITIATION_CORRUPT_RECORD );
            survey.setErrorCode( SurveyErrorCode.USER_DELETED.name() );
            survey.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
            surveyPreInitiationDao.update( survey );
            return true;
        }
        return false;
    }


    @Override
    public void inviteCorporateToRegister( User user, int planId ) throws InvalidInputException, UndeliveredEmailException
    {
        Map<String, String> urlParams = new HashMap<String, String>();
        urlParams.put( CommonConstants.FIRST_NAME, user.getFirstName() );
        urlParams.put( CommonConstants.LAST_NAME, user.getLastName() );
        urlParams.put( CommonConstants.USER_ID, String.valueOf( user.getUserId() ) );
        urlParams.put( CommonConstants.COMPANY_ID, String.valueOf( user.getCompany().getCompanyId() ) );
        urlParams.put( CommonConstants.PLAN_ID, String.valueOf( planId ) );
        urlParams.put( CommonConstants.CURRENT_TIMESTAMP, String.valueOf( System.currentTimeMillis() ) );
        urlParams.put( CommonConstants.UNIQUE_IDENTIFIER, generateUniqueIdentifier() );

        String url = urlGenerator.generateUrl( urlParams, applicationBaseUrl + CommonConstants.SET_REGISTRATION_PASSWORD );
        String queryParam = extractUrlQueryParam( url );

        Company company = companyDao.findById( Company.class, user.getCompany().getCompanyId() );
        storeCompanyAdminInvitation( queryParam, user.getEmailId(), company );

        LOG.debug( "Calling email services to send registration invitation mail" );
        //emailServices.sendRegistrationInviteMail( url, user.getEmailId(), user.getFirstName(), user.getLastName() );
        emailServices.sendNewRegistrationInviteMail( url, user.getEmailId(), user.getFirstName(), user.getLastName(), planId );
    }


    @Override
    public List<Long> getExcludedUserIds()
    {
        List<Long> userIds = new ArrayList<Long>();
        List<Long> companyIdsWithHiddenAttribute = organizationUnitSettingsDao.fetchCompanyIdsWithHiddenSection();
        if(companyIdsWithHiddenAttribute != null && !companyIdsWithHiddenAttribute.isEmpty()) {
        List<User> users = userDao.findByColumnForMultipleValues( User.class, "company.companyId",
            companyIdsWithHiddenAttribute );
        if ( users != null && !users.isEmpty() ) {
            for ( User user : users ) {
                userIds.add( user.getUserId() );
            }
        }
        }
        return userIds;
    }


    /*
     * (non-Javadoc)
     * @see com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService#getUserIdsUnderAdmin(com.realtech.socialsurvey.core.entities.User)
     */
    @Override
    public Set<Long> getUserIdsUnderAdmin( User adminUser ) throws InvalidInputException
    {

        if ( adminUser == null ) {
            throw new InvalidInputException( "Passed parameter adminUser is null" );
        }

        LOG.info( "Method getUserIdsUnderAdmin started for adminUserId " + adminUser.getUserId() );

        Set<Long> userIds = new HashSet<Long>();
        List<UserProfile> adminUserProfiles = getAllUserProfilesForUser( adminUser );

        ProfilesMaster agentProfileMaster = profilesMasterDao.findById( ProfilesMaster.class,
            CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID );

        for ( UserProfile userProfile : adminUserProfiles ) {
            if ( userProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID ) {
                Map<String, Object> queries = new HashMap<>();
                queries.put( CommonConstants.PROFILE_MASTER_COLUMN, agentProfileMaster );
                queries.put( CommonConstants.BRANCH_ID_COLUMN, userProfile.getBranchId() );
                List<UserProfile> userProfiles = userProfileDao.findByKeyValue( UserProfile.class, queries );

                for ( UserProfile agentProfile : userProfiles ) {
                    userIds.add( agentProfile.getAgentId() );
                }
            } else if ( userProfile.getProfilesMaster()
                .getProfileId() == CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID ) {
                Map<String, Object> queries = new HashMap<>();
                queries.put( CommonConstants.PROFILE_MASTER_COLUMN, agentProfileMaster );
                queries.put( CommonConstants.REGION_ID_COLUMN, userProfile.getRegionId() );
                List<UserProfile> userProfiles = userProfileDao.findByKeyValue( UserProfile.class, queries );

                for ( UserProfile agentProfile : userProfiles ) {
                    userIds.add( agentProfile.getAgentId() );
                }
            } else if ( userProfile.getProfilesMaster()
                .getProfileId() == CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID ) {
                Map<String, Object> queries = new HashMap<>();
                queries.put( CommonConstants.PROFILE_MASTER_COLUMN, agentProfileMaster );
                queries.put( CommonConstants.COMPANY_COLUMN, userProfile.getCompany() );
                List<UserProfile> userProfiles = userProfileDao.findByKeyValue( UserProfile.class, queries );

                for ( UserProfile agentProfile : userProfiles ) {
                    userIds.add( agentProfile.getAgentId() );
                }
            }
        }

        return userIds;
    }


    @Override
    @Transactional ( rollbackFor = Exception.class)
    public void saveEmailUserMappingAndUpdateAgentIdInSurveyPreinitiation( String emailId, long userId , String createdAndModifiedBy)
        throws InvalidInputException, NoRecordsFetchedException
    {
        User user = userDao.findById(User.class, userId);
        socialManagementService.updateAgentIdOfSurveyPreinitiationRecordsForEmail( user, emailId );
        this.saveEmailUserMapping( emailId, userId , createdAndModifiedBy);
    }



    @Override
    @Transactional ( rollbackFor = Exception.class)
    public void saveIgnoredEmailCompanyMappingAndUpdateSurveyPreinitiation( String emailId, long companyId )
        throws InvalidInputException, NoRecordsFetchedException
    {
        this.saveIgnoredEmailCompanyMapping( emailId, companyId );
        socialManagementService.updateSurveyPreinitiationRecordsAsIgnored( emailId );
    }
    
    
    @Override
    @Transactional
    public void temporaryInactiveCompanyAdmin(long companyId){
        LOG.info( "method temporaryInactiveCompanyAdmin started for companyId : " + companyId );
        User admin = getAdminUserByCompanyId( companyId );
        if(admin != null){
            admin.setStatus( CommonConstants.STATUS_TEMPORARILY_INACTIVE );    
            admin.setLoginName( admin.getEmailId() );
            updateUser( admin );
        }
        LOG.info( "method temporaryInactiveCompanyAdmin finished for companyId : " + companyId );

    }


    @Override
    @Transactional
    public void updateUserProfileObject( UserProfile userProfile ) throws InvalidInputException
    {     
        LOG.info( "method updateUserProfileObject started" ); 
        if ( userProfile == null ){
            throw new InvalidInputException( "Passed parameter userProfile is null" );
        }
        userProfile.setModifiedOn(new Timestamp(System.currentTimeMillis()));
        userProfileDao.update( userProfile );
        LOG.info( "method updateUserProfileObject finished for User : " + userProfile.getAgentId() );
    }
    
    /**
     * method to update the last invitation sent date in users table in MySQL if the user entry exists.
     *
     * @param emailId
     * @return
     */
    @Override
    public void updateLastInviteSentDateIfUserExistsInDB( String emailId )
    {
        LOG.debug( "Method updateLastInviteSentDateIfUserExists started." );
        try {
            Map<String, Object> columns = new HashMap<>();
            columns.put( "emailId", emailId );
            List<User> usersWithTheGivenEmailId = userDao.findByKeyValue( User.class, columns );
            if ( usersWithTheGivenEmailId != null && !usersWithTheGivenEmailId.isEmpty() ) {
                usersWithTheGivenEmailId.get( CommonConstants.INITIAL_INDEX )
                    .setLastInvitationSentDate( new Timestamp( System.currentTimeMillis() ) );
                userDao.update( usersWithTheGivenEmailId.get( CommonConstants.INITIAL_INDEX ) );
            }
            LOG.debug( "Method updateLastInviteSentDateIfUserExists finished." );
        } catch ( Exception databaseException ) {
            LOG.error( "Exception caught while checking for email id in USERS table." );
        }
    }
    
    
    
    @Override
    public boolean isUserSocialSurveyAdmin( User user ) {
        LOG.trace( "method isUserSocialSurveyAdmin() called for {}", user );
        if( user == null ) {
            LOG.warn( "User object can't be null" );
            return false;
        } else {
            
            //get primary profile profile of user
            List<UserProfile> userProfiles = user.getUserProfiles();
            for ( UserProfile userProfile : userProfiles ) {
                if ( userProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_SS_ADMIN_PROFILE_ID ) {
                    // social survey administrator
                    return true;
                }
            }
            return false;
        }
        
        
    }


    @Override
    public List<UserProfile> getUserProfiles( long userId ) throws InvalidInputException
    {
        LOG.debug( "method getUserProfiles() started" );
        
        if( userId <= 0 ) {
            LOG.warn( "Invalid user ID" );
            throw new InvalidInputException( "Invalid user ID" );
        }
        
        List<UserProfile> profiles = userProfileDao.getUserProfiles( userId ); 

        LOG.debug( "method getUserProfiles() finished" );
        return profiles;
    }
    

    /*
     * Method to fetch all the agent/admin user profiles for the user
     */
    @SuppressWarnings ( "unchecked")
    @Override
    @Transactional
    public List<UserProfile> getAllAgentAdminProfilesForUser( User user ) throws InvalidInputException
    {
        if ( user == null ) {
            LOG.error( "User object passed was null" );
            throw new InvalidInputException( "User object passed was null" );
        }
        LOG.debug( "Method getAllAgentAdminProfilesForUser() called to fetch the list of agent/Admin profiles for the user" );
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria( UserProfile.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ) );
            criteria.add( Restrictions.eq( CommonConstants.USER_COLUMN, user ) );
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_COLUMN, user.getCompany() ) );
            criteria.add( Restrictions.ne( CommonConstants.PROFILE_MASTER_COLUMN,
                getProfilesMasterById( CommonConstants.PROFILES_MASTER_SM_ADMIN_PROFILE_ID ) ) );
        } catch ( HibernateException hibernateException ) {
            LOG.error( "HibernateException caught in getAllAgentAdminProfilesForUser().", hibernateException );
            throw new DatabaseException( "HibernateException caught in getAllAgentAdminProfilesForUser().", hibernateException );
        }
        LOG.debug( "Method getAllAgentAdminProfilesForUser() finised successfully" );
        return (List<UserProfile>) criteria.list();
    }
    
    @Override
    @Transactional
    public List<UserFromSearch> getUserSocialMediaList( List<UserFromSearch> usersList ) throws InvalidInputException
    {
        SocialMediaVO socialMediaVO;
        for(UserFromSearch user: usersList) {
            List<SocialMediaVO> socialMediaVOS = new ArrayList<>(  );
            //get the details of the socialmedia which the user has connected from mongo using user
            SocialMediaTokens socialMediaTokens = organizationUnitSettingsDao.fetchSocialMediaTokens(
                MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, user.getUserId() );

                //facebook
                socialMediaVO = new SocialMediaVO( CommonConstants.FACEBOOK_SOCIAL_SITE );
                if ( socialMediaTokens!= null && socialMediaTokens.getFacebookToken() != null ) {
                    if ( socialMediaTokens.getFacebookToken().isTokenExpiryAlertSent() )
                        socialMediaVO.setStatus( SocialMediaConnectionStatus.EXPIRED );
                    else
                        socialMediaVO.setStatus( SocialMediaConnectionStatus.CONNECTED );
                }
                socialMediaVOS.add( socialMediaVO );

                //instagram
                socialMediaVO = new SocialMediaVO( CommonConstants.INSTAGRAM_SOCIAL_SITE );
                if ( socialMediaTokens!= null && socialMediaTokens.getInstagramToken() != null ) {
                    if ( socialMediaTokens.getInstagramToken().isTokenExpiryAlertSent() )
                        socialMediaVO.setStatus( SocialMediaConnectionStatus.EXPIRED );
                    else
                        socialMediaVO.setStatus( SocialMediaConnectionStatus.CONNECTED );
                }
                socialMediaVOS.add( socialMediaVO );

                //facebookpixel
                socialMediaVO = new SocialMediaVO( CommonConstants.FACEBOOK_PIXEL );
                if ( socialMediaTokens!= null && socialMediaTokens.getFacebookPixelToken() != null ) {
                    socialMediaVO.setStatus( SocialMediaConnectionStatus.CONNECTED );
                }
                socialMediaVOS.add( socialMediaVO );

                //google business
                socialMediaVO = new SocialMediaVO( CommonConstants.GOOGLE_BUSINESS_SOCIAL_SITE );
                if ( socialMediaTokens!= null && socialMediaTokens.getGoogleBusinessToken() != null ) {
                    socialMediaVO.setStatus( SocialMediaConnectionStatus.CONNECTED );
                }
                socialMediaVOS.add( socialMediaVO );

                //google
                socialMediaVO = new SocialMediaVO( CommonConstants.GOOGLE_SOCIAL_SITE );
                if ( socialMediaTokens!= null && socialMediaTokens.getGoogleToken() != null ) {
                    socialMediaVO.setStatus( SocialMediaConnectionStatus.CONNECTED );
                }
                socialMediaVOS.add( socialMediaVO );

                //lendingtree
                socialMediaVO = new SocialMediaVO( CommonConstants.LENDINGTREE_SOCIAL_SITE );
                if ( socialMediaTokens!= null && socialMediaTokens.getLendingTreeToken() != null ) {
                    socialMediaVO.setStatus( SocialMediaConnectionStatus.CONNECTED );
                }
                socialMediaVOS.add( socialMediaVO );

                //linkedin
                socialMediaVO = new SocialMediaVO( CommonConstants.LINKEDIN_SOCIAL_SITE );
                if ( socialMediaTokens!= null && socialMediaTokens.getLinkedInToken() != null ) {
                    if ( socialMediaTokens.getLinkedInToken().isTokenExpiryAlertSent() )
                        socialMediaVO.setStatus( SocialMediaConnectionStatus.EXPIRED );
                    else
                        socialMediaVO.setStatus( SocialMediaConnectionStatus.CONNECTED );
                }
                socialMediaVOS.add( socialMediaVO );

                //realtor
                socialMediaVO = new SocialMediaVO( CommonConstants.REALTOR_SOCIAL_SITE );
                if ( socialMediaTokens!= null && socialMediaTokens.getRealtorToken() != null ) {
                    socialMediaVO.setStatus( SocialMediaConnectionStatus.CONNECTED );
                }
                socialMediaVOS.add( socialMediaVO );

                //twitter
                socialMediaVO = new SocialMediaVO( CommonConstants.TWITTER_SOCIAL_SITE );
                if ( socialMediaTokens!= null && socialMediaTokens.getTwitterToken() != null ) {
                    socialMediaVO.setStatus( SocialMediaConnectionStatus.CONNECTED );
                }
                socialMediaVOS.add( socialMediaVO );

                //yelp
                socialMediaVO = new SocialMediaVO( CommonConstants.YELP_SOCIAL_SITE );
                if ( socialMediaTokens!= null && socialMediaTokens.getYelpToken() != null ) {
                    socialMediaVO.setStatus( SocialMediaConnectionStatus.CONNECTED );
                }
                socialMediaVOS.add( socialMediaVO );

                //zillow
                socialMediaVO = new SocialMediaVO( CommonConstants.ZILLOW_SOCIAL_SITE );
                if ( socialMediaTokens!= null && socialMediaTokens.getZillowToken() != null ) {
                    socialMediaVO.setStatus( SocialMediaConnectionStatus.CONNECTED );
                }
                socialMediaVOS.add( socialMediaVO );

            user.setSocialMediaVOs( socialMediaVOS );
        }
        return usersList;
    }
    
    @Override
    @Transactional ( rollbackFor = Exception.class)
    public void updateAgentIdInSurveyPreinitiation( String emailId)
        throws InvalidInputException, NoRecordsFetchedException
    {
        User user = userDao.getActiveUser(emailId);
        socialManagementService.updateAgentIdOfSurveyPreinitiationRecordsForEmailForMismatch( user, emailId );
    }
    
    
    
    //one time run job
    @Override
	public void updateSurveyDetails() {
		int startingIndex = 0, limit = 2000, finalCount=0, notFound=0, updated;
		
		try {
			LOG.info("Batch one time run job updateSurveyDetails started.");

			// setting the status value
			List<Integer> status = new ArrayList<>();
			status.add(CommonConstants.SURVEY_STATUS_INITIATED);
			status.add(CommonConstants.STATUS_SURVEYPREINITIATION_COMPLETE);
			long recordCount = surveyPreInitiationDao.surveyPreInitiationCount(status);
			LOG.info("Fetched rows {}",recordCount);
			while (startingIndex <= recordCount) {
				for (SurveyPreInitiation survey : surveyPreInitiationDao.fetchSurveysByStatus(status, startingIndex,
						limit)) {
					LOG.debug( "Updating participantType and surveySentDate for spi id {} "
							+ survey.getSurveyPreIntitiationId());
					updated=surveyDetailsDao.updateSurveyDetailsFields(survey.getSurveyPreIntitiationId(),
							survey.getParticipantType(), survey.getCreatedOn());
					finalCount++;
					if(updated==0)
					{
						notFound++;
					}
				}
				startingIndex += limit;
			}
			LOG.info("NotFound {}",notFound);
			LOG.info("Final count {}",finalCount);
		} catch (Exception e) {
			LOG.error(" Error occured in updateSurveyDetails()");
			e.printStackTrace();
		}
		LOG.info("Batch one time run job updateSurveyDetails finished.");
	}
}