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
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.ContactNumberSettings;
import com.realtech.socialsurvey.core.entities.MailIdSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserEmailMapping;
import com.realtech.socialsurvey.core.entities.WebAddressSettings;
import com.realtech.socialsurvey.core.entities.api.RegistrationStage;
import com.realtech.socialsurvey.core.entities.api.UserProfile;
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

    @Value ( "${SALES_LEAD_EMAIL_ADDRESS}")
    private String salesLeadEmail;


    @Autowired
    public UserServiceImpl( OrganizationManagementService organizationManagementService,
        OrganizationUnitSettingsDao organizationUnitSettingsDao, UserManagementService userManagementService,
        SolrSearchService solrSearchService, UserDao userDao, UserProfileDao userProfileDao,
        GenericDao<ProfilesMaster, Integer> profilesMasterDao, EmailServices emailServices, Utils utils,
        ProfileManagementService profileManagementService, GenericDao<UserEmailMapping, Long> userEmailMappingDao )
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
    }


    @Override
    public void updateUserProfile( int userId, UserProfile userProfile ) throws SolrException, InvalidInputException
    {
        LOGGER.info( "Method updateUserProfile started for userId: " + userId );
        User user = userDao.findById( User.class, (long) userId );
        updateUserDetailsInMySql( user, userProfile );
        updateUserDetailsInMongo( user, userProfile );
        updateUserDetailsInSolr( user, userProfile );
        LOGGER.info( "Method updateUserProfile finished for userId: " + userId );
    }


    @Override
    public UserProfile getUserProfileDetails( int userId ) throws InvalidInputException
    {
        LOGGER.info( "Method getUserProfileDetails started for userId: " + userId );
        UserProfile userProfile = new UserProfile();
        User user = userManagementService.getUserByUserId( userId );
        OrganizationUnitSettings agentSettings = organizationUnitSettingsDao.fetchAgentSettingsById( userId );
        userProfile.setUserId( userId );
        userProfile.setFirstName( user.getFirstName() );
        userProfile.setLastName( user.getLastName() );
        if ( agentSettings != null ) {
            userProfile.setProfilePhotoUrl( agentSettings.getProfileImageUrl() );
            if ( agentSettings.getContact_details() != null ) {
                userProfile.setLocation( agentSettings.getContact_details().getLocation() );
                userProfile.setTitle( agentSettings.getContact_details().getTitle() );
                if ( agentSettings.getContact_details().getContact_numbers() != null ) {
                    userProfile.setPhone1( agentSettings.getContact_details().getContact_numbers().getPhone1() );
                    userProfile.setPhone2( agentSettings.getContact_details().getContact_numbers().getPhone2() );
                }
                if ( agentSettings.getContact_details().getWeb_addresses() != null ) {
                    userProfile.setWebsite( agentSettings.getContact_details().getWeb_addresses().getWork() );
                }
            }
        }
        LOGGER.info( "Method getUserProfileDetails finished for userId: " + userId );
        return userProfile;
    }


    @Override
    public void deleteUserProfileImage( int userId ) throws InvalidInputException
    {
        LOGGER.info( "Method deleteUserProfileImage started for userId: " + userId );
        AgentSettings agentSettings = userManagementService.getAgentSettingsForUserProfiles( userId );
        agentSettings.setProfileImageUrl( null );
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_IMAGE, agentSettings.getProfileImageUrl(), agentSettings,
            MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
        LOGGER.info( "Method deleteUserProfileImage finished for userId: " + userId );
    }


    @Override
    public void updateUserProfileImage( int userId, String imageUrl ) throws InvalidInputException
    {
        LOGGER.info( "Method updateUserProfileImage started for userId: " + userId );
        AgentSettings agentSettings = userManagementService.getAgentSettingsForUserProfiles( userId );
        agentSettings.setProfileImageUrl( imageUrl );
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_IMAGE, agentSettings.getProfileImageUrl(), agentSettings,
            MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
        LOGGER.info( "Method updateUserProfileImage finished for userId: " + userId );
    }


    @Override
    @Transactional
    public void updateStage( int userId, String stage )
    {
        LOGGER.info( "Method updateStage started for userId: " + userId + ", stage: " + stage );
        User user = userDao.findById( User.class, (long) userId );
        user.setRegistrationStage( stage );
        userDao.update( user );

        com.realtech.socialsurvey.core.entities.UserProfile userProfile = userProfileDao
            .findById( com.realtech.socialsurvey.core.entities.UserProfile.class, (long) userId );
        userProfile.setProfileCompletionStage( stage );
        userProfileDao.update( userProfile );
        LOGGER.info( "Method updateStage finished for userId: " + userId + ", stage: " + stage );
    }


    @Override
    public User addUser( String firstName, String lastName, String emailId, Company company )
        throws InvalidInputException, SolrException, NoRecordsFetchedException
    {
        LOGGER.info( "Method addUser started for emailId: " + emailId );
        User user = addUserDetailsInMySql( emailId, firstName, lastName, company );
        addUserDetailsInMongo( user );
        addUserDetailsInSolr( user );
        LOGGER.info( "Method addUser finished for emailId: " + emailId );
        return user;
    }


    @Override
    public void sendRegistrationEmail( User user ) throws NonFatalException
    {
        LOGGER.info( "Method sendRegistrationEmail started for user: " + user.getUserId() );
        //Send registration email to user
        userManagementService.inviteCorporateToRegister( user.getFirstName(), user.getLastName(), user.getEmailId(), false,
            null );

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


    private void updateUserDetailsInSolr( User user, UserProfile userProfile ) throws SolrException, InvalidInputException
    {
        LOGGER.info( "Method updateUserDetailsInSolr started for user: " + user.getUserId() );
        Map<String, Object> userMap = new HashMap<>();
        userMap.put( CommonConstants.USER_DISPLAY_NAME_SOLR,
            getFullName( userProfile.getFirstName(), userProfile.getLastName() ) );
        userMap.put( CommonConstants.USER_FIRST_NAME_SOLR, userProfile.getFirstName() );
        userMap.put( CommonConstants.USER_LAST_NAME_SOLR, userProfile.getLastName() );
        solrSearchService.editUserInSolrWithMultipleValues( user.getUserId(), userMap );
        LOGGER.info( "Method updateUserDetailsInSolr finished for user: " + user.getUserId() );
    }


    private String getFullName( String firstName, String lastName )
    {
        String fullName = firstName;
        if ( lastName != null && lastName != "" ) {
            fullName += " " + lastName;
        }
        return fullName;
    }


    private void updateUserDetailsInMongo( User user, UserProfile userProfile ) throws InvalidInputException
    {
        LOGGER.info( "Method updateUserDetailsInMongo started for user: " + user.getUserId() );
        AgentSettings agentSettings = userManagementService.getAgentSettingsForUserProfiles( user.getUserId() );

        ContactDetailsSettings contactDetails = agentSettings.getContact_details();
        if ( contactDetails == null ) {
            contactDetails = new ContactDetailsSettings();
        }
        contactDetails.setFirstName( userProfile.getFirstName() );
        contactDetails.setLastName( userProfile.getLastName() );
        contactDetails.setName( getFullName( userProfile.getFirstName(), userProfile.getLastName() ) );
        contactDetails.setLocation( userProfile.getLocation() );
        contactDetails.setTitle( userProfile.getTitle() );
        if ( contactDetails.getContact_numbers() == null ) {
            contactDetails.setContact_numbers( new ContactNumberSettings() );
        }
        contactDetails.getContact_numbers().setPhone1( userProfile.getPhone1() );
        contactDetails.getContact_numbers().setPhone2( userProfile.getPhone2() );
        if ( userProfile.getPhone1() != null ) {
            contactDetails.getContact_numbers().setWork( userProfile.getPhone1().getCountryCode() + "-"
                + userProfile.getPhone1().getNumber() + "x" + userProfile.getPhone1().getExtension() );
        }
        if ( userProfile.getPhone2() != null ) {
            contactDetails.getContact_numbers().setPersonal( userProfile.getPhone2().getCountryCode() + "-"
                + userProfile.getPhone2().getNumber() + "x" + userProfile.getPhone2().getExtension() );
        }

        if ( contactDetails.getWeb_addresses() == null ) {
            contactDetails.setWeb_addresses( new WebAddressSettings() );
        }
        contactDetails.getWeb_addresses().setWork( userProfile.getWebsite() );
        if ( contactDetails.getMail_ids() == null ) {
            contactDetails.setMail_ids( new MailIdSettings() );
        }
        contactDetails.getMail_ids().setWork( user.getEmailId() );

        agentSettings.setContact_details( contactDetails );
        agentSettings.setProfileImageUrl( userProfile.getProfilePhotoUrl() );
        String profileName = userManagementService.generateIndividualProfileName( user.getUserId(), contactDetails.getName(),
            user.getEmailId() );
        agentSettings.setProfileName( profileName );
        String profileUrl = utils.generateAgentProfileUrl( profileName );
        agentSettings.setProfileUrl( profileUrl );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_URL, agentSettings.getProfileUrl(), agentSettings,
            MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_NAME, agentSettings.getProfileName(), agentSettings,
            MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_IMAGE, agentSettings.getProfileImageUrl(), agentSettings,
            MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );

        profileManagementService.updateContactDetails( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION,
            agentSettings, contactDetails );

        LOGGER.info( "Method updateUserDetailsInMongo finished for user: " + user.getUserId() );
    }


    @Transactional
    private void updateUserDetailsInMySql( User user, UserProfile userProfile ) throws InvalidInputException
    {
        LOGGER.info( "Method updateUserDetailsInMySql started for user: " + user.getUserId() );
        user.setFirstName( userProfile.getFirstName() );
        user.setLastName( userProfile.getLastName() );
        userDao.merge( user );
        LOGGER.info( "Method updateUserDetailsInMySql finished for user: " + user.getUserId() );
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


    private void addUserDetailsInMongo( User user ) throws InvalidInputException, NoRecordsFetchedException
    {
        LOGGER.info( "Method addUserDetailsInMongo started for user: " + user.getUserId() );
        userManagementService.insertAgentSettings( user );
        OrganizationUnitSettings unitSettings = organizationManagementService.getAgentSettings( user.getUserId() );
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings( MongoOrganizationUnitSettingDaoImpl.KEY_STATUS,
            CommonConstants.STATUS_INCOMPLETE_MONGO, unitSettings,
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
}
