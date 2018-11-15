package com.realtech.socialsurvey.core.services.api.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.ContactNumberSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.Phone;
import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.entities.RegistrationStage;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserCompositeEntity;
import com.realtech.socialsurvey.core.entities.UserEmailMapping;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.api.UserService;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.utils.EncryptionHelper;


@Service
public class UserServiceImpl implements UserService
{
    private static final Logger LOGGER = LoggerFactory.getLogger( UserServiceImpl.class );
    private OrganizationManagementService organizationManagementService;
    private OrganizationUnitSettingsDao organizationUnitSettingsDao;
    private UserManagementService userManagementService;
    private SolrSearchService solrSearchService;
    private UserDao userDao;
    private UserProfileDao userProfileDao;
    private GenericDao<ProfilesMaster, Integer> profilesMasterDao;
    private EmailServices emailServices;
    private Utils utils;
    private ProfileManagementService profileManagementService;
    private GenericDao<UserEmailMapping, Long> userEmailMappingDao;
    private EncryptionHelper encryptionHelper;


    @Value ( "${SALES_LEAD_EMAIL_ADDRESS}")
    private String salesLeadEmail;


    @Autowired
    public UserServiceImpl( OrganizationManagementService organizationManagementService,
        OrganizationUnitSettingsDao organizationUnitSettingsDao, UserManagementService userManagementService,
        SolrSearchService solrSearchService, UserDao userDao, UserProfileDao userProfileDao,
        GenericDao<ProfilesMaster, Integer> profilesMasterDao, EmailServices emailServices, Utils utils,
        ProfileManagementService profileManagementService, GenericDao<UserEmailMapping, Long> userEmailMappingDao,
        EncryptionHelper encryptionHelper )
    {
        this.organizationManagementService = organizationManagementService;
        this.organizationUnitSettingsDao = organizationUnitSettingsDao;
        this.userManagementService = userManagementService;
        this.solrSearchService = solrSearchService;
        this.userDao = userDao;
        this.userProfileDao = userProfileDao;
        this.profilesMasterDao = profilesMasterDao;
        this.emailServices = emailServices;
        this.utils = utils;
        this.profileManagementService = profileManagementService;
        this.userEmailMappingDao = userEmailMappingDao;
        this.encryptionHelper = encryptionHelper;
    }


    @Override
    public void updateUserProfile( long userId, UserCompositeEntity userProfile ) throws SolrException, InvalidInputException
    {
        LOGGER.info( "Method updateUserProfile started for userId: " + userId );
        updateUserDetailsInMySql( userId, userProfile.getUser() );
        updateUserDetailsInMongo( userId, userProfile.getAgentSettings() );
        updateUserDetailsInSolr( userId, userProfile );
        LOGGER.info( "Method updateUserProfile finished for userId: " + userId );
    }


    @Override
    public UserCompositeEntity getUserProfileDetails( long userId ) throws InvalidInputException
    {
        LOGGER.info( "Method getUserProfileDetails started for userId: " + userId );
        UserCompositeEntity userProfile = new UserCompositeEntity();
        User user = userManagementService.getUserByUserId( userId );
        OrganizationUnitSettings agentSettings = organizationUnitSettingsDao.fetchAgentSettingsById( userId );
        userProfile.setUser( user );
        userProfile.setAgentSettings( (AgentSettings) agentSettings );
        LOGGER.info( "Method getUserProfileDetails finished for userId: " + userId );
        return userProfile;
    }


    @Override
    public void deleteUserProfileImage( long userId ) throws InvalidInputException
    {
        LOGGER.info( "Method deleteUserProfileImage started for userId: " + userId );
        AgentSettings agentSettings = userManagementService.getAgentSettingsForUserProfiles( userId );
        agentSettings.setProfileImageUrl( null );
        agentSettings.setProfileImageUrlThumbnail( null );
        agentSettings.setModifiedBy( String.valueOf( userId ) );
        agentSettings.setModifiedOn( System.currentTimeMillis() );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_IMAGE, agentSettings.getProfileImageUrl(), agentSettings,
            MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_MODIFIED_BY, agentSettings.getModifiedBy(), agentSettings,
            MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_MODIFIED_ON, agentSettings.getModifiedOn(), agentSettings,
            MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );

        LOGGER.info( "Method deleteUserProfileImage finished for userId: " + userId );
    }


    @Override
    public void updateUserProfileImage( long userId, String imageUrl ) throws InvalidInputException
    {
        LOGGER.info( "Method updateUserProfileImage started for userId: " + userId );
        AgentSettings agentSettings = userManagementService.getAgentSettingsForUserProfiles( userId );
        agentSettings.setProfileImageUrl( imageUrl );
        agentSettings.setProfileImageUrlThumbnail( imageUrl );
        agentSettings.setModifiedBy( String.valueOf( userId ) );
        agentSettings.setModifiedOn( System.currentTimeMillis() );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_IMAGE, agentSettings.getProfileImageUrl(), agentSettings,
            MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_MODIFIED_BY, agentSettings.getModifiedBy(), agentSettings,
            MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_MODIFIED_ON, agentSettings.getModifiedOn(), agentSettings,
            MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );

        LOGGER.info( "Method updateUserProfileImage finished for userId: " + userId );
    }


    @Override
    @Transactional
    public void updateStage( long userId, String stage )
    {
        LOGGER.info( "Method updateStage started for userId: " + userId + ", stage: " + stage );
        User user = userDao.findById( User.class, userId );
        user.setRegistrationStage( stage );
        user.setModifiedBy( String.valueOf( userId ) );
        user.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        userDao.update( user );

        for ( UserProfile profile : user.getUserProfiles() ) {
            UserProfile userProfile = userProfileDao.findById( UserProfile.class, profile.getUserProfileId() );
            userProfile.setProfileCompletionStage( stage );
            userProfile.setModifiedBy( String.valueOf( userId ) );
            userProfile.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
            userProfileDao.update( userProfile );
        }
        LOGGER.info( "Method updateStage finished for userId: " + userId + ", stage: " + stage );
    }


    @Override
    public User addUser( String firstName, String lastName, String emailId, Phone phone, Company company )
        throws InvalidInputException, SolrException, NoRecordsFetchedException
    {
        LOGGER.info( "Method addUser started for emailId: " + emailId );
        User user = addUserDetailsInMySql( emailId, firstName, lastName, company );
        addUserDetailsInMongo( user, phone );
        addUserDetailsInSolr( user );
        LOGGER.info( "Method addUser finished for emailId: " + emailId );
        return user;
    }


    @Override
    public void sendRegistrationEmail( User user, int planId ) throws NonFatalException
    {
        LOGGER.info( "Method sendRegistrationEmail started for user: " + user.getUserId() );

        //Send registration email to user
        userManagementService.inviteCorporateToRegister( user, planId );

        // Send mail to sales lead
        Date today = new Date( System.currentTimeMillis() );
        SimpleDateFormat utcDateFormat = new SimpleDateFormat( CommonConstants.DATE_FORMAT_WITH_TZ );
        utcDateFormat.setTimeZone( TimeZone.getTimeZone( "UTC" ) );

        SimpleDateFormat pstDateFormat = new SimpleDateFormat( CommonConstants.DATE_FORMAT_WITH_TZ );
        pstDateFormat.setTimeZone( TimeZone.getTimeZone( "PST" ) );

        SimpleDateFormat estDateFormat = new SimpleDateFormat( CommonConstants.DATE_FORMAT_WITH_TZ );
        estDateFormat.setTimeZone( TimeZone.getTimeZone( "EST" ) );
        String details = "First Name : " + user.getFirstName() + "<br/>" + "Last Name : " + user.getLastName() + "<br/>"
            + "Email Address : " + user.getEmailId() + "<br/>" + "Time : " + "<br/>" + utcDateFormat.format( today ) + "<br/>"
            + estDateFormat.format( today ) + "<br/>" + pstDateFormat.format( today );

        try {
            emailServices.sendCompanyRegistrationStageMail( user.getFirstName(), user.getLastName(),
                Arrays.asList( salesLeadEmail ), CommonConstants.COMPANY_REGISTRATION_STAGE_STARTED, user.getEmailId(), details,
                true );
        } catch ( InvalidInputException e ) {
            e.printStackTrace();
        } catch ( UndeliveredEmailException e ) {
            e.printStackTrace();
        }

        LOGGER.info( "Method sendRegistrationEmail finished for user: " + user.getUserId() );
    }


    private void updateUserDetailsInSolr( long userId, UserCompositeEntity userProfile )
        throws SolrException, InvalidInputException
    {
        LOGGER.info( "Method updateUserDetailsInSolr started for user: " + userId );
        Map<String, Object> userMap = new HashMap<>();
        userMap.put( CommonConstants.USER_DISPLAY_NAME_SOLR,
            getFullName( userProfile.getUser().getFirstName(), userProfile.getUser().getLastName() ) );
        userMap.put( CommonConstants.USER_FIRST_NAME_SOLR, userProfile.getUser().getFirstName() );
        userMap.put( CommonConstants.USER_LAST_NAME_SOLR, userProfile.getUser().getLastName() );
        solrSearchService.editUserInSolrWithMultipleValues( userId, userMap );
        LOGGER.info( "Method updateUserDetailsInSolr finished for user: " + userId );
    }


    private String getFullName( String firstName, String lastName )
    {
        String fullName = firstName;
        if ( lastName != null && lastName != "" ) {
            fullName += " " + lastName;
        }
        return fullName;
    }


    private void updateUserDetailsInMongo( long userId, AgentSettings agentSettings ) throws InvalidInputException
    {
        LOGGER.info( "Method updateUserDetailsInMongo started for user: " + userId );

        if ( agentSettings != null && agentSettings.getContact_details() != null ) {
            agentSettings.getContact_details().setName( getFullName( agentSettings.getContact_details().getFirstName(),
                agentSettings.getContact_details().getLastName() ) );
            if ( agentSettings.getContact_details().getContact_numbers() != null ) {
                if ( agentSettings.getContact_details().getContact_numbers().getPhone1() != null ) {
                    agentSettings.getContact_details().getContact_numbers().setWork(
                        agentSettings.getContact_details().getContact_numbers().getPhone1().getFormattedPhoneNumber() );
                }
                if ( agentSettings.getContact_details().getContact_numbers().getPhone2() != null ) {
                    agentSettings.getContact_details().getContact_numbers().setPersonal(
                        agentSettings.getContact_details().getContact_numbers().getPhone2().getFormattedPhoneNumber() );
                }
            }

            if ( agentSettings.getContact_details().getMail_ids() != null ) {
                String profileName = userManagementService.generateIndividualProfileName( userId,
                    agentSettings.getContact_details().getName(), agentSettings.getContact_details().getMail_ids().getWork() );
                agentSettings.setProfileName( profileName );
                String profileUrl = utils.generateAgentProfileUrl( profileName );
                agentSettings.setProfileUrl( profileUrl );
            }
        }

        agentSettings.setModifiedBy( String.valueOf( userId ) );
        agentSettings.setModifiedOn( System.currentTimeMillis() );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_URL, agentSettings.getProfileUrl(), agentSettings,
            MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_NAME, agentSettings.getProfileName(), agentSettings,
            MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_IMAGE, agentSettings.getProfileImageUrl(), agentSettings,
            MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_IMAGE_THUMBNAIL, agentSettings.getProfileImageUrlThumbnail(),
            agentSettings, MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_MODIFIED_BY, agentSettings.getModifiedBy(), agentSettings,
            MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_MODIFIED_ON, agentSettings.getModifiedOn(), agentSettings,
            MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );


        profileManagementService.updateContactDetails( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION,
            agentSettings, agentSettings.getContact_details() );

        LOGGER.info( "Method updateUserDetailsInMongo finished for user: " + userId );
    }


    @Transactional
    private void updateUserDetailsInMySql( long userId, User userProfile ) throws InvalidInputException
    {
        LOGGER.info( "Method updateUserDetailsInMySql started for user: " + userId );
        userProfile.setModifiedBy( String.valueOf( userId ) );
        userProfile.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        userDao.merge( userProfile );
        LOGGER.info( "Method updateUserDetailsInMySql finished for user: " + userId );
    }


    private void addUserDetailsInSolr( User user ) throws InvalidInputException, SolrException
    {
        LOGGER.info( "Method addUserDetailsInSolr started for user: " + user.getUserId() );
        try {
            solrSearchService.addUserToSolr( user );
        } catch ( SolrException exception ) {
            LOGGER.error( "SolrException caught in inviteUser(). Nested exception is ", exception );
            organizationManagementService.removeOrganizationUnitSettings( user.getUserId(),
                MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
            throw exception;
        }
        LOGGER.info( "Method addUserDetailsInSolr finished for user: " + user.getUserId() );
    }


    private void addUserDetailsInMongo( User user, Phone phone ) throws InvalidInputException, NoRecordsFetchedException
    {
        LOGGER.info( "Method addUserDetailsInMongo started for user: " + user.getUserId() );
        userManagementService.insertAgentSettings( user );
        AgentSettings agentSettings = organizationManagementService.getAgentSettings( user.getUserId() );
        if ( phone != null && agentSettings != null && agentSettings.getContact_details() != null ) {
            if ( agentSettings.getContact_details().getContact_numbers() == null ) {
                agentSettings.getContact_details().setContact_numbers( new ContactNumberSettings() );
            }
            agentSettings.getContact_details().getContact_numbers().setPhone1( phone );
            agentSettings.getContact_details().getContact_numbers()
                .setWork( agentSettings.getContact_details().getContact_numbers().getPhone1().getFormattedPhoneNumber() );
        }

        agentSettings.setCreatedBy( String.valueOf( user.getUserId() ) );
        agentSettings.setModifiedBy( String.valueOf( user.getUserId() ) );
        agentSettings.setModifiedOn( System.currentTimeMillis() );
        agentSettings.setCreatedOn( System.currentTimeMillis() );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings( MongoOrganizationUnitSettingDaoImpl.KEY_STATUS,
            CommonConstants.STATUS_INCOMPLETE_MONGO, agentSettings,
            MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_CONTACT_DETAIL_SETTINGS, agentSettings.getContact_details(), agentSettings,
            MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_CREATED_BY, agentSettings.getCreatedBy(), agentSettings,
            MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_CREATED_ON, agentSettings.getCreatedOn(), agentSettings,
            MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_MODIFIED_BY, agentSettings.getModifiedBy(), agentSettings,
            MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_MODIFIED_ON, agentSettings.getModifiedOn(), agentSettings,
            MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );

        LOGGER.info( "Method addUserDetailsInMongo finished for user: " + user.getUserId() );
    }


    @Transactional
    private User addUserDetailsInMySql( String email, String firstName, String lastName, Company company )
    {
        LOGGER.info( "Method addUserDetailsInMySql started for email: " + email );
        User user = new User();
        user.setCompany( company );
        user.setLoginName( email );
        user.setEmailId( email );
        user.setFirstName( firstName );
        user.setLastName( lastName );
        user.setSource( CommonConstants.DEFAULT_SOURCE_APPLICATION );
        user.setIsAtleastOneUserprofileComplete( 0 );
        user.setStatus( CommonConstants.STATUS_INCOMPLETE );
        Timestamp currentTimestamp = new Timestamp( System.currentTimeMillis() );
        user.setLastLogin( currentTimestamp );
        user.setNumOfLogins( CommonConstants.ONE );
        user.setCreatedOn( currentTimestamp );
        user.setModifiedOn( currentTimestamp );
        user.setCreatedBy( CommonConstants.ACCOUNT_REGISTER );
        user.setModifiedBy( CommonConstants.ACCOUNT_REGISTER );
        user.setRegistrationStage( RegistrationStage.INITIATE_REGISTRATION.getCode() );
        user.setIsForcePassword( 1 );
        user.setIsOwner( 1 );
        user.setZillowReviewCount( 0 );
        user.setIsZillowConnected( 0 );
        user.setSuperAdmin( 0 );
        user = userDao.save( user );

        com.realtech.socialsurvey.core.entities.UserProfile profile = new com.realtech.socialsurvey.core.entities.UserProfile();
        profile.setAgentId( user.getUserId() );
        profile.setCompany( company );
        profile.setCreatedBy( CommonConstants.ACCOUNT_REGISTER );
        profile.setCreatedOn( currentTimestamp );
        profile.setEmailId( email );
        profile.setModifiedBy( CommonConstants.ACCOUNT_REGISTER );
        profile.setModifiedOn( currentTimestamp );
        profile.setIsPrimary( 0 );
        profile.setProfilesMaster( profilesMasterDao.findById( ProfilesMaster.class, 1 ) );
        profile.setStatus( CommonConstants.STATUS_INCOMPLETE );
        profile.setUser( user );
        profile.setProfileCompletionStage( RegistrationStage.INITIATE_REGISTRATION.getCode() );
        userProfileDao.save( profile );

        LOGGER.info( "Method addUserDetailsInMySql finished for email: " + email );

        return user;
    }


    @Override
    public boolean isUserExist( String emailId ) throws InvalidInputException
    {
        LOGGER.info( "Method to check if user exists called for username : " + emailId );
        if ( emailId == null || emailId.isEmpty() ) {
            throw new InvalidInputException( "Invalid parameter passed : passed parameter user name is null or empty" );
        }
        Map<String, Object> queries = new HashMap<String, Object>();
        queries.put( CommonConstants.EMAIL_ID, emailId );
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
        List<UserEmailMapping> userEmailMappings = userEmailMappingDao.findByKeyValue( UserEmailMapping.class, queries );

        if ( ( userEmailMappings == null || userEmailMappings.isEmpty() ) && !userDao.isEmailAlreadyTaken( emailId ) ) {
            return false;
        }
        LOGGER.info( "Method to check if user exists finished for username : " + emailId );
        return true;
    }


    @Override
    @Transactional
    public void savePassword( long userId, String password ) throws InvalidInputException
    {
        LOGGER.info( "Method to save password called for userId : " + userId );
        User user = userManagementService.getUserByUserId( userId );
        String encryptedPassword = encryptionHelper.encryptSHA512( password );
        user.setLoginPassword( encryptedPassword );
        updateUserDetailsInMySql( userId, user );
        LOGGER.info( "Method to save password finished for userId : " + userId );
    }
    
    @Override
    public Long getOwnerByCompanyId(Long companyId) throws NonFatalException {
        LOGGER.debug("Method to get owner called for companyId : "+companyId);
        Long userId = null;
        userId = userDao.getOwnerForCompany(companyId);
        LOGGER.debug("Method to get owner finished for companyId : "+companyId);
        return userId;
    }
}
