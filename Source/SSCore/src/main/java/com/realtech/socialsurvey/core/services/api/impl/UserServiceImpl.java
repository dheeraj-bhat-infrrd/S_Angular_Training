package com.realtech.socialsurvey.core.services.api.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.api.RegistrationStage;
import com.realtech.socialsurvey.core.entities.api.UserProfile;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.api.UserService;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
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
    private GenericDao<UserProfile, Integer> userProfileApiDao;

    @Value ( "${SALES_LEAD_EMAIL_ADDRESS}")
    private String salesLeadEmail;


    @Autowired
    public UserServiceImpl( OrganizationManagementService organizationManagementService,
        OrganizationUnitSettingsDao organizationUnitSettingsDao, UserManagementService userManagementService,
        SolrSearchService solrSearchService, UserDao userDao, UserProfileDao userProfileDao,
        GenericDao<ProfilesMaster, Integer> profilesMasterDao, EmailServices emailServices,
        GenericDao<UserProfile, Integer> userProfileApiDao )
    {
        this.organizationManagementService = organizationManagementService;
        this.organizationUnitSettingsDao = organizationUnitSettingsDao;
        this.userManagementService = userManagementService;
        this.solrSearchService = solrSearchService;
        this.userDao = userDao;
        this.userProfileDao = userProfileDao;
        this.profilesMasterDao = profilesMasterDao;
        this.emailServices = emailServices;
        this.userProfileApiDao = userProfileApiDao;
    }


    @Override
    public void updateUserProfile( int userId, UserProfile userProfile )
    {
        // TODO Auto-generated method stub
        // Registration stage should be set as 2 for user

    }


    @Override
    public UserProfile getUserProfileDetails( int userId )
    {
        UserProfile userProfile = userProfileApiDao.findById( UserProfile.class, userId );
        AgentSettings agentSettings = organizationUnitSettingsDao.fetchAgentSettingsById( userId );
        return userProfile;
    }


    @Override
    public void deleteUserProfileImage( int userId )
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void updateUserProfileImage( int userId, String imageUrl )
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void updateStage( int userId, String stage )
    {
        User user = userDao.findById( User.class, (long) userId );
        user.setRegistrationStage( stage );
        userDao.update( user );

        com.realtech.socialsurvey.core.entities.UserProfile userProfile = userProfileDao
            .findById( com.realtech.socialsurvey.core.entities.UserProfile.class, (long) userId );
        userProfile.setProfileCompletionStage( stage );
        userProfileDao.update( userProfile );
    }


    @Override
    public User addUser( String firstName, String lastName, String emailId, Company company )
        throws InvalidInputException, SolrException, NoRecordsFetchedException
    {
        User user = addUserDetailsInMySql( emailId, firstName, lastName, company );
        addUserDetailsInMongo( user );
        addUserDetailsInSolr( user );
        return user;
    }


    @Override
    public void sendRegistrationEmail( User user ) throws NonFatalException
    {
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
    }


    private void addUserDetailsInSolr( User user ) throws InvalidInputException, SolrException
    {
        try {
            solrSearchService.addUserToSolr( user );
        } catch ( SolrException exception ) {
            LOGGER.error( "SolrException caught in inviteUser(). Nested exception is ", exception );
            organizationManagementService.removeOrganizationUnitSettings( user.getUserId(),
                MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
            throw exception;
        }
    }


    private void addUserDetailsInMongo( User user ) throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementService.insertAgentSettings( user );
        updateUserDetailsInMongo( user.getUserId(), MongoOrganizationUnitSettingDaoImpl.KEY_STATUS,
            CommonConstants.STATUS_INCOMPLETE_MONGO );
    }


    private void updateUserDetailsInMongo( long userId, String key, Object value )
        throws InvalidInputException, NoRecordsFetchedException
    {
        OrganizationUnitSettings unitSettings = organizationManagementService.getAgentSettings( userId );
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings( key, value, unitSettings,
            MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
    }


    @Transactional
    private User addUserDetailsInMySql( String email, String firstName, String lastName, Company company )
    {
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
        return user;
    }
}
