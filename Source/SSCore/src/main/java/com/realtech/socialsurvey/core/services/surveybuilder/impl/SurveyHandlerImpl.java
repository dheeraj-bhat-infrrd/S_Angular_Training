package com.realtech.socialsurvey.core.services.surveybuilder.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.SurveyDetailsDao;
import com.realtech.socialsurvey.core.dao.SurveyPreInitiationDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.AbusiveSurveyReportWrapper;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.MailContent;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.SurveyResponse;
import com.realtech.socialsurvey.core.entities.SurveySettings;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.enums.OrganizationUnit;
import com.realtech.socialsurvey.core.enums.SettingsForApplication;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.settingsmanagement.impl.InvalidSettingsStateException;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.core.utils.EmailFormatHelper;


// JIRA SS-119 by RM-05:BOC
@Component
public class SurveyHandlerImpl implements SurveyHandler, InitializingBean
{

    private static final Logger LOG = LoggerFactory.getLogger( SurveyHandlerImpl.class );

    private static String SWEAR_WORDS;

    @Autowired
    private SolrSearchService solrSearchService;

    @Autowired
    private SurveyDetailsDao surveyDetailsDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserProfileDao userProfileDao;

    @Autowired
    private URLGenerator urlGenerator;

    @Autowired
    private EmailFormatHelper emailFormatHelper;

    @Autowired
    private OrganizationUnitSettingsDao organizationUnitSettingsDao;

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Autowired
    SurveyPreInitiationDao surveyPreInitiationDao;

    @Autowired
    ProfileManagementService profileManagementService;

    @Autowired
    private EmailServices emailServices;

    @Value ( "${APPLICATION_BASE_URL}")
    private String applicationBaseUrl;

    @Value ( "${MOODS_TO_SEND_MAIL}")
    private String moodsToSendMail;

    @Value ( "${GOOGLE_SHARE_URI}")
    private String googleShareUri;

    @Value ( "${APPLICATION_LOGO_URL}")
    private String appLogoUrl;

    @Value ( "${MAX_SURVEY_REMINDERS}")
    private int maxSurveyReminders;

    @Value ( "${VALID_SURVEY_INTERVAL}")
    private int validSurveyInterval;

    @Value ( "${MAX_SOCIAL_POST_REMINDERS}")
    private int maxSocialpostReminders;

    @Value ( "${SURVEY_REMINDER_INTERVAL}")
    private int surveyReminderInterval;

    @Value ( "${SOCIAL_POST_REMINDER_INTERVAL}")
    private int socialPostReminderInterval;


    /**
     * Method to store question and answer format into mongo.
     * 
     * @param agentId
     * @throws InvalidInputException
     * @throws Exception
     */
    @Override
    @Transactional
    public SurveyDetails storeInitialSurveyDetails( long agentId, String customerEmail, String firstName, String lastName,
        int reminderCount, String custRelationWithAgent, String baseUrl, String source ) throws SolrException,
        NoRecordsFetchedException, InvalidInputException
    {
        LOG.info( "Method to store initial details of survey, storeInitialSurveyAnswers() started." );
        String agentName;
        long branchId = 0;
        long companyId = 0;
        long regionId = 0;

        User user = userDao.findById( User.class, agentId );
        companyId = user.getCompany().getCompanyId();
        agentName = user.getFirstName() + " " + user.getLastName();
        for ( UserProfile userProfile : user.getUserProfiles() ) {
            if ( userProfile.getAgentId() == agentId ) {
                branchId = userProfile.getBranchId();
                regionId = userProfile.getRegionId();
            }
        }

        SurveyDetails surveyDetails = new SurveyDetails();
        surveyDetails.setAgentId( agentId );
        surveyDetails.setAgentName( agentName );
        surveyDetails.setBranchId( branchId );
        surveyDetails.setCustomerFirstName( firstName );
        surveyDetails.setCustomerLastName( lastName );
        surveyDetails.setCompanyId( companyId );
        surveyDetails.setCustomerEmail( customerEmail );
        surveyDetails.setRegionId( regionId );
        surveyDetails.setStage( CommonConstants.INITIAL_INDEX );
        surveyDetails.setReminderCount( reminderCount );
        surveyDetails.setModifiedOn( new Date( System.currentTimeMillis() ) );
        surveyDetails.setCreatedOn( new Date( System.currentTimeMillis() ) );
        surveyDetails.setSurveyResponse( new ArrayList<SurveyResponse>() );
        surveyDetails.setCustRelationWithAgent( custRelationWithAgent );
        surveyDetails.setUrl( getSurveyUrl( agentId, customerEmail, baseUrl ) );
        surveyDetails.setEditable( true );
        surveyDetails.setSource( source );

        SurveyDetails survey = surveyDetailsDao
            .getSurveyByAgentIdAndCustomerEmail( agentId, customerEmail, firstName, lastName );
        LOG.info( "Method to store initial details of survey, storeInitialSurveyAnswers() finished." );

        if ( survey == null ) {
            surveyDetailsDao.insertSurveyDetails( surveyDetails );
            return null;
        } else {
            return survey;
        }
    }


    @Override
    @Transactional
    public void insertSurveyDetails( SurveyDetails surveyDetails )
    {
        surveyDetailsDao.insertSurveyDetails( surveyDetails );
    }


    /*
     * Method to generate survey URL to start a survey directly based upon agentId and customer
     * email id.
     */
    @Override
    public String getSurveyUrl( long agentId, String customerEmail, String baseUrl ) throws InvalidInputException
    {
        Map<String, String> urlParam = new HashMap<>();
        urlParam.put( CommonConstants.AGENT_ID_COLUMN, agentId + "" );
        urlParam.put( CommonConstants.CUSTOMER_EMAIL_COLUMN, customerEmail );
        return urlGenerator.generateUrl( urlParam, baseUrl );
    }

    /*
     * Method to update answers to all the questions and current stage in MongoDB.
     * @param agentId
     * @param customerEmail
     * @param question
     * @param answer
     * @param stage
     * @throws Exception
     */
    @Override
    public void updateCustomerAnswersInSurvey( long agentId, String customerEmail, String question, String questionType,
        String answer, int stage )
    {
        LOG.info( "Method to update answers provided by customer in SURVEY_DETAILS, updateCustomerAnswersInSurvey() started." );
        SurveyResponse surveyResponse = new SurveyResponse();
        surveyResponse.setAnswer( answer );
        surveyResponse.setQuestion( question );
        surveyResponse.setQuestionType( questionType );
        surveyDetailsDao.updateCustomerResponse( agentId, customerEmail, surveyResponse, stage );
        LOG.info( "Method to update answers provided by customer in SURVEY_DETAILS, updateCustomerAnswersInSurvey() finished." );
    }


    /*
     * Method to update customer review and final score on the basis of rating questions in
     * SURVEY_DETAILS.
     */
    @Override
    public void updateGatewayQuestionResponseAndScore( long agentId, String customerEmail, String mood, String review,
        boolean isAbusive, String agreedToShare )
    {
        LOG.info( "Method to update customer review and final score on the basis of rating questions in SURVEY_DETAILS, updateCustomerAnswersInSurvey() started." );
        surveyDetailsDao.updateGatewayAnswer( agentId, customerEmail, mood, review, isAbusive, agreedToShare );
        surveyDetailsDao.updateFinalScore( agentId, customerEmail );
        LOG.info( "Method to update customer review and final score on the basis of rating questions in SURVEY_DETAILS, updateCustomerAnswersInSurvey() finished." );
    }


    @Override
    public SurveyDetails getSurveyDetails( long agentId, String customerEmail, String firstName, String lastName )
    {
        LOG.info( "Method getSurveyDetails() to return survey details by agent id and customer email started." );
        SurveyDetails surveyDetails;
        surveyDetails = surveyDetailsDao.getSurveyByAgentIdAndCustomerEmail( agentId, customerEmail, firstName, lastName );
        LOG.info( "Method getSurveyDetails() to return survey details by agent id and customer email finished." );
        return surveyDetails;
    }


    @Override
    public void updateSurveyAsAbusive( String surveymongoId, String reporterEmail, String reporterName )
    {
        LOG.info( "Method updateSurveyAsAbusive() to mark the survey as abusive, started" );
        surveyDetailsDao.updateSurveyAsAbusive(surveymongoId, reporterEmail, reporterName);
        LOG.info( "Method updateSurveyAsAbusive() to mark the survey as abusive, finished" );
    }


    @Override
    @Transactional
    public void saveSurveyPreInitiationObject( SurveyPreInitiation surveyPreInitiation ) throws InvalidInputException
    {
        if ( surveyPreInitiation == null ) {
            LOG.info( "SurveyPreInitiation object passed null for insert" );
            throw new InvalidInputException( "SurveyPreInitiation object passed null for insert" );
        }
        LOG.debug( "Inside method saveSurveyPreInitiationObject " );
        surveyPreInitiationDao.save( surveyPreInitiation );
    }


    /*
     * Method to update a survey as clicked when user triggers the survey and page of the first
     * question starts loading.
     */
    public void updateSurveyAsClicked( long agentId, String customerEmail )
    {
        LOG.info( "Method updateSurveyAsClicked() to mark the survey as clicked, started" );
        surveyDetailsDao.updateSurveyAsClicked( agentId, customerEmail );
        LOG.info( "Method updateSurveyAsClicked() to mark the survey as clicked, finished" );
    }


    /*
     * Method to increase reminder count by 1. This method is called every time a reminder mail is
     * sent to the customer.
     */
    @Override
    @Transactional
    public void updateReminderCount( long surveyPreInitiationId, boolean reminder )
    {
        LOG.info( "Method to increase reminder count by 1, updateReminderCount() started." );

        SurveyPreInitiation survey = surveyPreInitiationDao.findById( SurveyPreInitiation.class, surveyPreInitiationId );
        if ( survey != null ) {
            survey.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );

            survey.setLastReminderTime( new Timestamp( System.currentTimeMillis() ) );
            if ( reminder ) {
                survey.setReminderCounts( survey.getReminderCounts() + 1 );
            }
            surveyPreInitiationDao.merge( survey );
        }
        LOG.info( "Method to increase reminder count by 1, updateReminderCount() finished." );
    }


    @Override
    @Transactional
    public SurveyDetails getSurveyDetailsBySourceIdAndMongoCollection( String surveySourceId, long iden, String collectionName )
    {
        LOG.debug( "Inside method getSurveyDetailsBySourceId" );
        SurveyDetails surveyDetails = null;
        if ( surveySourceId != null ) {
            surveyDetails = surveyDetailsDao.getSurveyBySourceSourceIdAndMongoCollection( surveySourceId, iden, collectionName );
        }
        return surveyDetails;
    }


    @Override
    @Transactional
    public void markSurveyAsSent( SurveyPreInitiation surveyPreInitiation )
    {
        LOG.info( "Method to increase reminder count by 1, updateReminderCount() started." );
        if ( surveyPreInitiation != null ) {
            surveyPreInitiation.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
            surveyPreInitiation.setLastReminderTime( new Timestamp( System.currentTimeMillis() ) );
            surveyPreInitiationDao.merge( surveyPreInitiation );
        }
        LOG.info( "Method to increase reminder count by 1, updateReminderCount() finished." );
    }


    @Override
    public String getApplicationBaseUrl()
    {
        return applicationBaseUrl;
    }


    @Override
    public String getSwearWords()
    {
        return SWEAR_WORDS;
    }


    /*
     * Loads string of swear words from file containing all the swear words.
     */
    @Override
    public void afterPropertiesSet() throws Exception
    {
        Resource resource = new ClassPathResource( "swear-words" );
        try {
            BufferedReader br = new BufferedReader( new InputStreamReader( resource.getInputStream() ) );
            String text;
            List<String> swearWordsLst = new ArrayList<>();
            text = br.readLine();
            while ( text != null ) {
                swearWordsLst.add( text.trim() );
                text = br.readLine();
            }
            SWEAR_WORDS = new Gson().toJson( swearWordsLst );
        } catch ( IOException e ) {
            LOG.error( "Error parsing list of swear words. Nested exception is ", e );
            throw e;
        }
    }


    /*
     * Method to get list of all the admins' emailIds, an agent comes under. Later on these emailIds
     * are used for sending emails in case of any sad review for the agent.
     */
    @Transactional
    @Override
    public Map<String, String> getEmailIdsOfAdminsInHierarchy( long agentId ) throws InvalidInputException
    {
        Map<String, String> emailIdsOfAdmins = new HashMap<String, String>();
        List<UserProfile> admins = new ArrayList<>();
        User agent = userDao.findById( User.class, agentId );
        Map<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.USER_COLUMN, agent );
        queries.put( CommonConstants.PROFILE_MASTER_COLUMN,
            userManagementService.getProfilesMasterById( CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) );
        List<UserProfile> agentProfiles = userProfileDao.findByKeyValue( UserProfile.class, queries );
        for ( UserProfile agentProfile : agentProfiles ) {
            queries.clear();
            queries.put( CommonConstants.BRANCH_ID_COLUMN, agentProfile.getBranchId() );
            queries.put( CommonConstants.PROFILE_MASTER_COLUMN,
                userManagementService.getProfilesMasterById( CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID ) );
            admins.addAll( userProfileDao.findByKeyValue( UserProfile.class, queries ) );
            queries.clear();
            queries.put( CommonConstants.REGION_ID_COLUMN, agentProfile.getRegionId() );
            queries.put( CommonConstants.PROFILE_MASTER_COLUMN,
                userManagementService.getProfilesMasterById( CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID ) );
            admins.addAll( userProfileDao.findByKeyValue( UserProfile.class, queries ) );
            queries.clear();
            if ( agentProfile.getCompany() != null ) {
                queries.put( CommonConstants.COMPANY_COLUMN, agentProfile.getCompany() );
                queries.put( CommonConstants.PROFILE_MASTER_COLUMN,
                    userManagementService.getProfilesMasterById( CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID ) );
                admins.addAll( userProfileDao.findByKeyValue( UserProfile.class, queries ) );
            }
        }
        for ( UserProfile admin : admins ) {
            String name = admin.getUser().getFirstName();
            if ( admin.getUser().getLastName() != null )
                name += " " + admin.getUser().getLastName();
            emailIdsOfAdmins.put( admin.getEmailId(), name );
        }
        return emailIdsOfAdmins;
    }


    @Override
    @Transactional
    public Map<String, Integer> getReminderInformationForCompany( long companyId )
    {
        LOG.debug( "Inside method getReminderInformationForCompany" );
        Map<String, Integer> map = new HashMap<String, Integer>();
        int reminderInterval = 0;
        int maxReminders = 0;
        OrganizationUnitSettings organizationUnitSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(
            companyId, MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        if ( organizationUnitSettings != null ) {
            SurveySettings surveySettings = organizationUnitSettings.getSurvey_settings();
            if ( surveySettings != null ) {
                if ( !surveySettings.getIsReminderDisabled() && surveySettings.getSurvey_reminder_interval_in_days() > 0 ) {
                    reminderInterval = surveySettings.getSurvey_reminder_interval_in_days();
                    maxReminders = surveySettings.getMax_number_of_survey_reminders();
                }
            }
        }

        if ( maxReminders == 0 ) {
            LOG.debug( "No Reminder count found for company " + companyId + " hence setting default value" );
            maxReminders = maxSurveyReminders;
        }
        if ( reminderInterval == 0 ) {
            LOG.debug( "No Reminder interval found for company " + companyId + " hence setting default value " );
            reminderInterval = surveyReminderInterval;
        }

        map.put( CommonConstants.SURVEY_REMINDER_COUNT, maxReminders );
        map.put( CommonConstants.SURVEY_REMINDER_INTERVAL, reminderInterval );
        return map;
    }


    /*
     * Method to get list of customers' email ids who have not completed survey yet. It checks if
     * max number of reminder mails have been sent. It also checks if required number of days have
     * been passed since the last mail was sent.
     */
    @Override
    @Transactional
    public List<SurveyPreInitiation> getIncompleteSurveyCustomersEmail( Company company )
    {
        LOG.info( "started." );

        List<SurveyPreInitiation> incompleteSurveyCustomers = new ArrayList<>();

        LOG.debug( "Now fetching survey which are already processed " );
        HashMap<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.COMPANY_ID_COLUMN, company.getCompanyId() );
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_SURVEYPREINITIATION_PROCESSED );
        incompleteSurveyCustomers = surveyPreInitiationDao.findByKeyValue( SurveyPreInitiation.class, queries );
        LOG.info( "finished." );
        return incompleteSurveyCustomers;
    }


    @Override
    public void updateReminderCount( List<Long> agents, List<String> customers )
    {
        LOG.info( "Method to increase reminder count by 1, updateReminderCount() started." );
        surveyDetailsDao.updateReminderCount( agents, customers );
        LOG.info( "Method to increase reminder count by 1, updateReminderCount() finished." );
    }


    @Override
    public void updateReminderCountForSocialPosts( Long agentId, String customerEmail )
    {
        LOG.info( "Method to increase reminder count by 1, updateReminderCountForSocialPosts() started." );
        surveyDetailsDao.updateReminderCountForSocialPost( agentId, customerEmail );
        LOG.info( "Method to increase reminder count by 1, updateReminderCountForSocialPosts() finished." );
    }


    /*
     * Method to get surveys
     */
    @Override
    public List<SurveyDetails> getIncompleteSocialPostSurveys( long companyId )
    {
        LOG.info( "started." );
        int reminderInterval = 0;
        int maxReminders = 0;
        float autopostScore = 0;
        List<SurveyDetails> incompleteSocialPostCustomers = new ArrayList<>();
        OrganizationUnitSettings organizationUnitSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(
            companyId, MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
        // Fetching surveyReminderInterval and max number of reminders for a company.
        if ( organizationUnitSettings != null ) {
            SurveySettings surveySettings = organizationUnitSettings.getSurvey_settings();
            if ( surveySettings != null ) {
                if ( !surveySettings.getIsReminderDisabled() && surveySettings.getSurvey_reminder_interval_in_days() > 0 ) {
                    reminderInterval = surveySettings.getSurvey_reminder_interval_in_days();
                    maxReminders = surveySettings.getMax_number_of_survey_reminders();
                    autopostScore = surveySettings.getShow_survey_above_score();
                }
            }
        }
        if ( maxReminders == 0 ) {
            maxReminders = maxSocialpostReminders;
        }
        if ( reminderInterval == 0 ) {
            reminderInterval = socialPostReminderInterval;
        }
        incompleteSocialPostCustomers = surveyDetailsDao.getIncompleteSocialPostCustomersEmail( companyId, reminderInterval,
            maxReminders, autopostScore );
        LOG.info( "finished." );
        return incompleteSocialPostCustomers;
    }


    @Override
    public String getMoodsToSendMail()
    {
        return moodsToSendMail;
    }


    @Override
    public String getGoogleShareUri()
    {
        return googleShareUri;
    }


    @Override
    public void increaseSurveyCountForAgent( long agentId ) throws SolrException
    {
        LOG.info( "Method to increase survey count for agent started." );
        organizationUnitSettingsDao.updateCompletedSurveyCountForAgent( agentId, 1 );
        solrSearchService.updateCompletedSurveyCountForUserInSolr( agentId, 1 );
        LOG.info( "Method to increase survey count for agent finished." );
    }


    @Override
    public void decreaseSurveyCountForAgent( long agentId ) throws SolrException
    {
        LOG.info( "Method to decrease survey count for agent started." );
        organizationUnitSettingsDao.updateCompletedSurveyCountForAgent( agentId, -1 );
        solrSearchService.updateCompletedSurveyCountForUserInSolr( agentId, -1 );
        LOG.info( "Method to decrease survey count for agent finished." );
    }


    @Override
    public void updateSharedOn( String socialSite, long agentId, String customerEmail )
    {
        LOG.info( "Method to update sharedOn in SurveyDetails collection, updateSharedOn() started." );
        surveyDetailsDao.updateSharedOn( socialSite, agentId, customerEmail );
        LOG.info( "Method to update sharedOn in SurveyDetails collection, updateSharedOn() finished." );
    }


    @Override
    public void changeStatusOfSurvey( long agentId, String customerEmail, String firstName, String lastName, boolean editable )
    {
        LOG.info( "Method to update status of survey in SurveyDetails collection, changeStatusOfSurvey() started." );
        surveyDetailsDao.changeStatusOfSurvey( agentId, customerEmail, firstName, lastName, editable );
        LOG.info( "Method to update status of survey in SurveyDetails collection, changeStatusOfSurvey() finished." );
    }


    /*
     * Method to store initial details of customer to initiate survey and send a mail with link of
     * survey.
     */
    @Override
    @Transactional
    public void sendSurveyInvitationMail( String custFirstName, String custLastName, String custEmail,
        String custRelationWithAgent, User user, boolean isAgent, String source ) throws InvalidInputException, SolrException,
        NoRecordsFetchedException, UndeliveredEmailException, ProfileNotFoundException
    {
        Map<String, Long> hierarchyMap = null;
        Map<SettingsForApplication, OrganizationUnit> map = null;
        String logoUrl = null;
        LOG.debug( "Method sendSurveyInvitationMail() called from DashboardController." );
        if ( custFirstName == null || custFirstName.isEmpty() ) {
            LOG.error( "Null/Empty value found for customer's first name." );
            throw new InvalidInputException( "Null/Empty value found for customer's first name." );
        }
        if ( custLastName == null || custLastName.isEmpty() ) {
            custLastName = "";
        }
        if ( custEmail == null || custEmail.isEmpty() ) {
            LOG.error( "Null/Empty value found for customer's email id." );
            throw new InvalidInputException( "Null/Empty value found for customer's email id." );
        }

        String link = composeLink( user.getUserId(), custEmail, custFirstName, custLastName );
        preInitiateSurvey( user, custEmail, custFirstName, custLastName, 0, custRelationWithAgent, source );
        // storeInitialSurveyDetails(user.getUserId(), custEmail, custFirstName, custLastName, 0,
        // custRelationWithAgent, link);

        // if (isAgent)

        AgentSettings agentSettings = organizationUnitSettingsDao.fetchAgentSettingsById( user.getUserId() );
        hierarchyMap = profileManagementService.getPrimaryHierarchyByAgentProfile( agentSettings );

        long companyId = hierarchyMap.get( CommonConstants.COMPANY_ID_COLUMN );
        long regionId = hierarchyMap.get( CommonConstants.REGION_ID_COLUMN );
        long branchId = hierarchyMap.get( CommonConstants.BRANCH_ID_COLUMN );
        try {
            map = profileManagementService.getPrimaryHierarchyByEntity( CommonConstants.AGENT_ID_COLUMN, user.getUserId() );
            if ( map == null ) {
                LOG.error( "Unable to fetch primary profile for this user " );
                throw new FatalException( "Unable to fetch primary profile this user " + user.getUserId() );
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
        sendInvitationMailByAgent( user, custFirstName, custLastName, custEmail, link, logoUrl );
        // else
        // sendInvitationMailByCustomer(user, custFirstName, custLastName, custEmail, link);
        LOG.debug( "Method sendSurveyInvitationMail() finished from DashboardController." );
    }


    /*
     * Method to send email to customer by agent for restarting an already completed survey.
     */
    @Override
    public void sendSurveyRestartMail( String custFirstName, String custLastName, String custEmail,
        String custRelationWithAgent, User user, String surveyUrl ) throws InvalidInputException, UndeliveredEmailException,
        ProfileNotFoundException
    {
        LOG.info( "sendSurveyRestartMail() started." );
        Map<String, Long> hierarchyMap = null;
        Map<SettingsForApplication, OrganizationUnit> map = null;
        String logoUrl = null;
        AgentSettings agentSettings = userManagementService.getUserSettings( user.getUserId() );
        String companyName = user.getCompany().getCompany();
        String agentTitle = "";
        if ( agentSettings.getContact_details() != null && agentSettings.getContact_details().getTitle() != null ) {
            agentTitle = agentSettings.getContact_details().getTitle();
        }

        String agentPhone = "";
        if ( agentSettings.getContact_details() != null && agentSettings.getContact_details().getContact_numbers() != null
            && agentSettings.getContact_details().getContact_numbers().getWork() != null ) {
            agentPhone = agentSettings.getContact_details().getContact_numbers().getWork();
        }

        String agentName = user.getFirstName();
        if ( user.getLastName() != null && !user.getLastName().isEmpty() ) {
            agentName = user.getFirstName() + " " + user.getLastName();
        }
        String agentSignature = emailFormatHelper.buildAgentSignature( agentName, agentPhone, agentTitle, companyName );
        String currentYear = String.valueOf( Calendar.getInstance().get( Calendar.YEAR ) );
        DateFormat dateFormat = new SimpleDateFormat( "yyyy/MM/dd" );
        // TODO add address for mail footer
        String fullAddress = "";

        hierarchyMap = profileManagementService.getPrimaryHierarchyByAgentProfile( agentSettings );

        long companyId = hierarchyMap.get( CommonConstants.COMPANY_ID_COLUMN );
        long regionId = hierarchyMap.get( CommonConstants.REGION_ID_COLUMN );
        long branchId = hierarchyMap.get( CommonConstants.BRANCH_ID_COLUMN );
        try {
            map = profileManagementService.getPrimaryHierarchyByEntity( CommonConstants.AGENT_ID_COLUMN, user.getUserId() );
            if ( map == null ) {
                LOG.error( "Unable to fetch primary profile for this user " );
                throw new FatalException( "Unable to fetch primary profile this user " + user.getUserId() );
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
            OrganizationUnitSettings branchSettings = null;
            try {
                branchSettings = organizationManagementService.getBranchSettingsDefault( branchId );
            } catch ( NoRecordsFetchedException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if ( branchSettings != null ) {
                logoUrl = branchSettings.getLogo();
            }
        } else if ( organizationUnit == OrganizationUnit.AGENT ) {
            logoUrl = agentSettings.getLogo();
        }

        OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user.getCompany()
            .getCompanyId() );
        preInitiateSurvey( user, custEmail, custFirstName, custLastName, 0, custRelationWithAgent,
            CommonConstants.SURVEY_REQUEST_AGENT );
        if ( companySettings != null && companySettings.getMail_content() != null
            && companySettings.getMail_content().getRestart_survey_mail() != null ) {

            MailContent restartSurvey = companySettings.getMail_content().getRestart_survey_mail();
            String mailBody = emailFormatHelper.replaceEmailBodyWithParams( restartSurvey.getMail_body(),
                restartSurvey.getParam_order() );

            mailBody = mailBody.replaceAll( "\\[BaseUrl\\]", applicationBaseUrl );
            if ( logoUrl == null || logoUrl.equalsIgnoreCase( "" ) ) {
                mailBody = mailBody.replaceAll( "\\[LogoUrl\\]", appLogoUrl );
            } else {

                mailBody = mailBody.replaceAll( "\\[LogoUrl\\]", logoUrl );
            }
            mailBody = mailBody.replaceAll( "\\[Name\\]",
                emailFormatHelper.getCustomerDisplayNameForEmail( custFirstName, custLastName ) );
            mailBody = mailBody.replaceAll( "\\[AgentName\\]", agentName );
            mailBody = mailBody.replaceAll( "\\[AgentSignature\\]", agentSignature );
            mailBody = mailBody.replaceAll( "\\[RecipientEmail\\]", custEmail );
            mailBody = mailBody.replaceAll( "\\[SenderEmail\\]", user.getEmailId() );
            mailBody = mailBody.replaceAll( "\\[CompanyName\\]", companyName );
            mailBody = mailBody.replaceAll( "\\[InitiatedDate\\]", dateFormat.format( new Date() ) );
            mailBody = mailBody.replaceAll( "\\[CurrentYear\\]", currentYear );
            mailBody = mailBody.replaceAll( "\\[FullAddress\\]", fullAddress );
            mailBody = mailBody.replaceAll( "null", "" );

            String mailSubject = CommonConstants.SURVEY_MAIL_SUBJECT + agentName;
            try {
                emailServices.sendSurveyInvitationMail( custEmail, mailSubject, mailBody, user.getEmailId(),
                    user.getFirstName() + ( user.getLastName() != null ? " " + user.getLastName() : "" ) );
            } catch ( InvalidInputException | UndeliveredEmailException e ) {
                LOG.error( "Exception caught while sending mail to " + custEmail + ". Nested exception is ", e );
            }
        } else {
            emailServices.sendDefaultSurveyInvitationMail( custEmail,
                emailFormatHelper.getCustomerDisplayNameForEmail( custFirstName, custLastName ),
                user.getFirstName() + ( user.getLastName() != null ? " " + user.getLastName() : "" ), surveyUrl,
                user.getEmailId(), agentSignature, companyName, dateFormat.format( new Date() ), currentYear, fullAddress );
        }
        LOG.info( "sendSurveyRestartMail() finished." );
    }


    // Method to fetch initial survey details from MySQL based upn agent id and customer email.
    @Override
    @Transactional
    public SurveyPreInitiation getPreInitiatedSurvey( long agentId, String customerEmail, String custFirstName,
        String custLastName ) throws NoRecordsFetchedException
    {
        LOG.info( "Method getSurveyByAgentIdAndCutomerEmail() started. " );
        Map<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.AGENT_ID_COLUMN, agentId );
        queries.put( "customerEmailId", customerEmail );
        queries.put( "customerFirstName", custFirstName );
        queries.put( "customerLastName", custLastName );
        List<SurveyPreInitiation> surveyPreInitiations = surveyPreInitiationDao.findByKeyValue( SurveyPreInitiation.class,
            queries );
        LOG.info( "Method getSurveyByAgentIdAndCutomerEmail() finished. " );
        if ( surveyPreInitiations != null && !surveyPreInitiations.isEmpty() ) {
            return surveyPreInitiations.get( CommonConstants.INITIAL_INDEX );
        }
        return null;
    }


    @Override
    @Transactional
    public SurveyPreInitiation getPreInitiatedSurvey( long surveyPreInitiationId ) throws NoRecordsFetchedException
    {
        LOG.info( "Method getSurveyByAgentIdAndCutomerEmail() started. " );
        SurveyPreInitiation surveyPreInitiation = surveyPreInitiationDao.findById( SurveyPreInitiation.class,
            surveyPreInitiationId );
        LOG.info( "Method getSurveyByAgentIdAndCutomerEmail() finished. " );
        return surveyPreInitiation;
    }


    // MEthod to delete survey pre initiation record from MySQL after making an entry into Mongo.
    @Override
    @Transactional
    public void deleteSurveyPreInitiationDetailsPermanently( SurveyPreInitiation surveyPreInitiation )
    {
        LOG.info( "Method deleteSurveyPreInitiationDetailsPermanently() started." );
        if ( surveyPreInitiation != null )
            surveyPreInitiationDao.delete( surveyPreInitiation );
        LOG.info( "Method deleteSurveyPreInitiationDetailsPermanently() finished." );
    }


    /*
     * Method to compose link for sending to a user to start survey started.
     */
    @Override
    public String composeLink( long userId, String custEmail, String custFirstName, String custLastName )
        throws InvalidInputException
    {
        LOG.debug( "Method composeLink() started" );
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put( CommonConstants.AGENT_ID_COLUMN, userId + "" );
        urlParams.put( CommonConstants.CUSTOMER_EMAIL_COLUMN, custEmail );
        urlParams.put( CommonConstants.FIRST_NAME, custFirstName );
        urlParams.put( CommonConstants.LAST_NAME, custLastName );
        LOG.debug( "Method composeLink() finished" );
        return urlGenerator.generateUrl( urlParams, getApplicationBaseUrl() + "rest/survey/showsurveypageforurl" );
    }


    // Method to update status of the survey to started.
    @Override
    @Transactional
    public void markSurveyAsStarted( SurveyPreInitiation surveyPreInitiation )
    {
        LOG.info( "Method markSurveyAsStarted() started." );
        surveyPreInitiation.setStatus( CommonConstants.SURVEY_STATUS_INITIATED );
        surveyPreInitiationDao.update( surveyPreInitiation );
        LOG.info( "Method markSurveyAsStarted() finished." );
    }


    // Method to update agentId in SurveyPreInitiation table for each of the unmapped agent.
    @Override
    @Transactional
    public Map<String, Object> mapAgentsInSurveyPreInitiation()
    {

        LOG.debug( "Inside method mapAgentsInSurveyPreInitiation " );
        List<SurveyPreInitiation> surveys = surveyPreInitiationDao.findByColumn( SurveyPreInitiation.class,
            CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_SURVEYPREINITIATION_NOT_PROCESSED );
        List<SurveyPreInitiation> unavailableAgents = new ArrayList<>();
        List<SurveyPreInitiation> invalidAgents = new ArrayList<>();
        List<SurveyPreInitiation> customersWithoutName = new ArrayList<>();
        List<SurveyPreInitiation> customersWithoutEmailId = new ArrayList<>();
        Set<Long> companies = new HashSet<>();
        for ( SurveyPreInitiation survey : surveys ) {
            int status = CommonConstants.STATUS_SURVEYPREINITIATION_PROCESSED;
            User user = null;
            if ( survey.getAgentEmailId() != null ) {
                List<User> userList = userDao.findByColumn( User.class, CommonConstants.AGENT_EMAIL_ID_COLUMN,
                    survey.getAgentEmailId() );
                if ( userList != null && !userList.isEmpty() ) {
                    user = userList.get( 0 );
                    if ( user != null ) {
                        LOG.debug( "Mapping the agent to this survey " );
                        if ( survey.getAgentId() == 0 ) {
                            survey.setAgentId( user.getUserId() );
                        }
                        surveyPreInitiationDao.update( survey );
                        if ( survey.getSurveySource().equalsIgnoreCase( CommonConstants.CRM_INFO_SOURCE_ENCOMPASS ) ) {
                            if ( user.getLoginPassword() != null ) {
                                if ( user.getCreatedOn().after( survey.getEngagementClosedTime() ) ) {
                                    status = CommonConstants.STATUS_SURVEYPREINITIATION_CORRUPT_RECORD;
                                }
                            } else {
                                LOG.debug( "Only a user invite has been sent so far, hence can't mark it as an old record for user "
                                    + user.getUserId() );
                            }

                            long surveyClosedTime = survey.getEngagementClosedTime().getTime();
                            long currentTime = System.currentTimeMillis();
                            if ( checkIfRecordHasExpired( surveyClosedTime, currentTime, validSurveyInterval ) ) {
                                status = CommonConstants.STATUS_SURVEYPREINITIATION_CORRUPT_RECORD;
                            }
                        }
                    }
                }
            }
            if ( survey.getAgentEmailId() == null || survey.getAgentEmailId().isEmpty() ) {
                LOG.error( "Agent email not found , invalid survey " + survey.getSurveyPreIntitiationId() );
                status = CommonConstants.STATUS_SURVEYPREINITIATION_CORRUPT_RECORD;
                unavailableAgents.add( survey );
                companies.add( survey.getCompanyId() );
            } else if ( survey.getCustomerFirstName() == null || survey.getCustomerFirstName().isEmpty() ) {
                if ( survey.getCustomerLastName() == null || survey.getCustomerLastName().isEmpty() ) {
                    LOG.error( "No Name found for customer, hence this is an invalid survey "
                        + survey.getSurveyPreIntitiationId() );
                    status = CommonConstants.STATUS_SURVEYPREINITIATION_CORRUPT_RECORD;
                    customersWithoutName.add( survey );
                }

            } else if ( survey.getCustomerLastName() == null || survey.getCustomerLastName().isEmpty() ) {
                if ( survey.getCustomerFirstName() == null || survey.getCustomerFirstName().isEmpty() ) {
                    LOG.error( "No Name found for customer, hence this is an invalid survey "
                        + survey.getSurveyPreIntitiationId() );
                    status = CommonConstants.STATUS_SURVEYPREINITIATION_CORRUPT_RECORD;
                    customersWithoutName.add( survey );
                    companies.add( survey.getCompanyId() );
                }
            } else if ( survey.getCustomerEmailId() == null || survey.getCustomerEmailId().isEmpty() ) {
                LOG.error( "No customer email id found, invalid survey " + survey.getSurveyPreIntitiationId() );
                status = CommonConstants.STATUS_SURVEYPREINITIATION_CORRUPT_RECORD;
                customersWithoutEmailId.add( survey );
                companies.add( survey.getCompanyId() );
            } else if ( user == null ) {
                LOG.error( "no agent found with this email id" );
                status = CommonConstants.STATUS_SURVEYPREINITIATION_CORRUPT_RECORD;
                invalidAgents.add( survey );
                companies.add( survey.getCompanyId() );
            } else if ( user.getCompany() == null ) {
                LOG.error( "Agent doesnt have an company associated with it " );
                status = CommonConstants.STATUS_SURVEYPREINITIATION_CORRUPT_RECORD;
                invalidAgents.add( survey );
                companies.add( survey.getCompanyId() );
            } else if ( user.getCompany().getCompanyId() != survey.getCompanyId() ) {
                status = CommonConstants.STATUS_SURVEYPREINITIATION_CORRUPT_RECORD;
                unavailableAgents.add( survey );
                companies.add( survey.getCompanyId() );
            }

            if ( survey.getSurveySource().equalsIgnoreCase( CommonConstants.CRM_SOURCE_DOTLOOP ) ) {
                status = validateUnitsettingsForDotloop( user, survey );
                if ( status == CommonConstants.STATUS_SURVEYPREINITIATION_CORRUPT_RECORD ) {
                    unavailableAgents.add( survey );
                    companies.add( survey.getCompanyId() );
                }
            }
            survey.setStatus( status );
            survey.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
            surveyPreInitiationDao.merge( survey );
        }

        Map<String, Object> corruptRecords = new HashMap<>();
        corruptRecords.put( "unavailableAgents", unavailableAgents );
        corruptRecords.put( "customersWithoutName", customersWithoutName );
        corruptRecords.put( "customersWithoutEmailId", customersWithoutEmailId );
        corruptRecords.put( "invalidAgents", invalidAgents );
        corruptRecords.put( "companies", companies );
        return corruptRecords;
    }


    private int validateUnitsettingsForDotloop( User user, SurveyPreInitiation surveyPreInitiation )
    {
        LOG.info( "Inside method validateUnitSettingsForDotloop " );
        int status = CommonConstants.STATUS_SURVEYPREINITIATION_PROCESSED;
        if ( surveyPreInitiation != null ) {
            boolean found = false;
            if ( surveyPreInitiation.getCompanyId() == user.getCompany().getCompanyId() ) {
                LOG.debug( "Though the company id is same, the region or branch might be different " );
                if ( surveyPreInitiation.getCollectionName().equalsIgnoreCase(
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION ) ) {
                    LOG.debug( "The user region should be same " );
                    long regionId = surveyPreInitiation.getRegionCollectionId();
                    List<UserProfile> userProfileList = user.getUserProfiles();
                    if ( userProfileList != null ) {
                        for ( UserProfile userProfile : userProfileList ) {
                            long userRegionId = userProfile.getRegionId();
                            if ( regionId == userRegionId ) {
                                found = true;
                                break;
                            }
                        }
                    }
                    status = CommonConstants.STATUS_SURVEYPREINITIATION_CORRUPT_RECORD;
                } else if ( surveyPreInitiation.getCollectionName().equalsIgnoreCase(
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION ) ) {
                    LOG.debug( "The user region should be same " );
                    long branchId = surveyPreInitiation.getBranchCollectionId();
                    List<UserProfile> userProfileList = user.getUserProfiles();
                    if ( userProfileList != null ) {
                        for ( UserProfile userProfile : userProfileList ) {
                            long userBranchId = userProfile.getBranchId();
                            if ( branchId == userBranchId ) {
                                found = true;
                                break;
                            }
                        }
                    }

                } else if ( surveyPreInitiation.getCollectionName().equalsIgnoreCase(
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION ) ) {
                    found = true;
                }
            }
            if ( !found ) {
                status = CommonConstants.STATUS_SURVEYPREINITIATION_CORRUPT_RECORD;
            }
        }

        return status;
    }


    /*
     * Method to send email by agent to initiate survey.
     */
    private void sendInvitationMailByAgent( User user, String custFirstName, String custLastName, String custEmail,
        String surveyUrl, String logoUrl ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "sendInvitationMailByAgent() started." );

        // fetching params
        AgentSettings agentSettings = userManagementService.getUserSettings( user.getUserId() );
        String companyName = user.getCompany().getCompany();
        String agentTitle = "";
        if ( agentSettings.getContact_details() != null && agentSettings.getContact_details().getTitle() != null ) {
            agentTitle = agentSettings.getContact_details().getTitle();
        }

        String agentPhone = "";
        if ( agentSettings.getContact_details() != null && agentSettings.getContact_details().getContact_numbers() != null
            && agentSettings.getContact_details().getContact_numbers().getWork() != null ) {
            agentPhone = agentSettings.getContact_details().getContact_numbers().getWork();
        }

        String agentName = user.getFirstName();
        if ( user.getLastName() != null && !user.getLastName().isEmpty() ) {
            agentName = user.getFirstName() + " " + user.getLastName();
        }

        String agentSignature = emailFormatHelper.buildAgentSignature( agentName, agentPhone, agentTitle, companyName );
        String currentYear = String.valueOf( Calendar.getInstance().get( Calendar.YEAR ) );
        DateFormat dateFormat = new SimpleDateFormat( "yyyy/MM/dd" );
        // TODO add address for mail footer
        String fullAddress = "";

        OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user.getCompany()
            .getCompanyId() );
        if ( companySettings != null && companySettings.getMail_content() != null
            && companySettings.getMail_content().getTake_survey_mail() != null ) {

            MailContent takeSurvey = companySettings.getMail_content().getTake_survey_mail();
            String mailBody = emailFormatHelper.replaceEmailBodyWithParams( takeSurvey.getMail_body(),
                takeSurvey.getParam_order() );

            mailBody = mailBody.replaceAll( "\\[BaseUrl\\]", applicationBaseUrl );
            if ( logoUrl == null || logoUrl.equalsIgnoreCase( "" ) ) {
                mailBody = mailBody.replaceAll( "\\[LogoUrl\\]", appLogoUrl );
            } else {
                mailBody = mailBody.replaceAll( "\\[LogoUrl\\]", logoUrl );
            }

            mailBody = mailBody.replaceAll( "\\[Link\\]", surveyUrl );
            mailBody = mailBody.replaceAll( "\\[FirstName\\]", custFirstName );
            mailBody = mailBody.replaceAll( "\\[Name\\]",
                emailFormatHelper.getCustomerDisplayNameForEmail( custFirstName, custLastName ) );
            mailBody = mailBody.replaceAll( "\\[AgentName\\]", "" );
            mailBody = mailBody.replaceAll( "\\[AgentSignature\\]", agentSignature );
            mailBody = mailBody.replaceAll( "\\[RecipientEmail\\]", custEmail );
            mailBody = mailBody.replaceAll( "\\[SenderEmail\\]", user.getEmailId() );
            mailBody = mailBody.replaceAll( "\\[CompanyName\\]", companyName );
            mailBody = mailBody.replaceAll( "\\[InitiatedDate\\]", dateFormat.format( new Date() ) );
            mailBody = mailBody.replaceAll( "\\[CurrentYear\\]", currentYear );
            mailBody = mailBody.replaceAll( "\\[FullAddress\\]", fullAddress );
            mailBody = mailBody.replaceAll( "null", "" );

            // Adding mail subject
            String mailSubject = CommonConstants.SURVEY_MAIL_SUBJECT + agentName;
            if ( takeSurvey.getMail_subject() != null && !takeSurvey.getMail_subject().isEmpty() ) {
                mailSubject = takeSurvey.getMail_subject();
                mailSubject = mailSubject.replaceAll( "\\[AgentName\\]", agentName );
            }

            try {
                emailServices.sendSurveyInvitationMail( custEmail, mailSubject, mailBody, user.getEmailId(),
                    user.getFirstName() + ( user.getLastName() != null ? " " + user.getLastName() : "" ) );
            } catch ( InvalidInputException | UndeliveredEmailException e ) {
                LOG.error( "Exception caught while sending mail to " + custEmail + ". Nested exception is ", e );
            }
        } else {
            emailServices.sendDefaultSurveyInvitationMail( custEmail,
                emailFormatHelper.getCustomerDisplayNameForEmail( custFirstName, custLastName ),
                user.getFirstName() + ( user.getLastName() != null ? " " + user.getLastName() : "" ), surveyUrl,
                user.getEmailId(), agentSignature, companyName, dateFormat.format( new Date() ), currentYear, fullAddress );
        }
        LOG.debug( "sendInvitationMailByAgent() finished." );
    }


    /*
     * Method to send email by customer to initiate survey.
     */
    @SuppressWarnings ( "unused")
    private void sendInvitationMailByCustomer( User user, String custFirstName, String custLastName, String custEmail,
        String link ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "sendInvitationMailByCustomer() started." );

        OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user.getCompany()
            .getCompanyId() );
        if ( companySettings != null && companySettings.getMail_content() != null
            && companySettings.getMail_content().getTake_survey_mail_customer() != null ) {

            MailContent takeSurveyCustomer = companySettings.getMail_content().getTake_survey_mail_customer();
            String mailBody = emailFormatHelper.replaceEmailBodyWithParams( takeSurveyCustomer.getMail_body(),
                takeSurveyCustomer.getParam_order() );
            mailBody = mailBody.replaceAll( "\\[LogoUrl\\]", appLogoUrl );
            mailBody = mailBody.replaceAll( "\\[BaseUrl\\]", applicationBaseUrl );
            mailBody = mailBody.replaceAll( "\\[FirstName\\]", custFirstName );
            mailBody = mailBody.replaceAll( "\\[AgentName\\]", user.getFirstName() + " " + user.getLastName() );
            mailBody = mailBody.replaceAll( "\\[Name\\]",
                emailFormatHelper.getCustomerDisplayNameForEmail( custFirstName, custLastName ) );
            mailBody = mailBody.replaceAll( "\\[Link\\]", link );
            mailBody = mailBody.replaceAll( "null", "" );

            String mailSubject = CommonConstants.SURVEY_MAIL_SUBJECT_CUSTOMER;
            try {
                emailServices.sendSurveyInvitationMailByCustomer( custEmail, mailSubject, mailBody, user.getEmailId(),
                    user.getFirstName() + ( user.getLastName() != null ? " " + user.getLastName() : "" ) );
            } catch ( InvalidInputException | UndeliveredEmailException e ) {
                LOG.error( "Exception caught while sending mail to " + custEmail + ". Nested exception is ", e );
            }
        } else {
            emailServices.sendDefaultSurveyInvitationMailByCustomer( custEmail, custFirstName,
                user.getFirstName() + ( user.getLastName() != null ? " " + user.getLastName() : "" ), link, user.getEmailId() );
        }
        LOG.debug( "sendInvitationMailByCustomer() finished." );
    }


    // Method to store details of a customer in mysql at the time of sending invite.
    private void preInitiateSurvey( User user, String custEmail, String custFirstName, String custLastName, int i,
        String custRelationWithAgent, String source )
    {
        LOG.debug( "Method preInitiateSurvey() started to store details of a customer in mysql at the time of  sending invite" );

        SurveyPreInitiation surveyPreInitiation = new SurveyPreInitiation();
        surveyPreInitiation.setAgentId( user.getUserId() );
        surveyPreInitiation.setCompanyId( user.getCompany().getCompanyId() );
        surveyPreInitiation.setCreatedOn( new Timestamp( System.currentTimeMillis() ) );
        surveyPreInitiation.setCustomerEmailId( custEmail );
        surveyPreInitiation.setCustomerFirstName( custFirstName );
        surveyPreInitiation.setCustomerLastName( custLastName );
        surveyPreInitiation.setEngagementClosedTime( new Timestamp( System.currentTimeMillis() ) );
        surveyPreInitiation.setLastReminderTime( new Timestamp( System.currentTimeMillis() ) );
        surveyPreInitiation.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        surveyPreInitiation.setReminderCounts( 0 );
        surveyPreInitiation.setStatus( CommonConstants.SURVEY_STATUS_PRE_INITIATED );
        surveyPreInitiation.setSurveySource( source );
        surveyPreInitiationDao.save( surveyPreInitiation );

        LOG.debug( "Method preInitiateSurvey() finished." );
    }


    private Boolean checkIfRecordHasExpired( long surveyClosedDate, long systemTime, int expirationDays )
    {
        long totalDaysInMillseconds = systemTime - surveyClosedDate;
        int totalDays = (int) ( totalDaysInMillseconds / ( 1000 * 60 * 60 * 24 ) );
        if ( totalDays >= expirationDays ) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public Boolean checkIfTimeIntervalHasExpired( long lastRemindedTime, long systemTime, int reminderInterval )
    {
        long remainingTime = systemTime - lastRemindedTime;
        int remainingDays = (int) ( remainingTime / ( 1000 * 60 * 60 * 24 ) );
        if ( remainingDays >= reminderInterval ) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    @Transactional
    public SurveyPreInitiation getPreInitiatedSurveyById( long surveyPreInitiationId ) throws NoRecordsFetchedException
    {
        LOG.debug( "Method getPreInitiatedSurveyById() called" );

        SurveyPreInitiation surveyPreInitiation = surveyPreInitiationDao.findById( SurveyPreInitiation.class,
            surveyPreInitiationId );

        if ( surveyPreInitiation == null ) {
            throw new NoRecordsFetchedException( "No records found for surveyPreInitiation with id : " + surveyPreInitiationId );
        }

        return surveyPreInitiation;
    }


    @Override
    @Transactional
    public void initiateSurveyRequest( long agentId, String recipientEmailId, String recipientFirstname,
        String recipientLastname, String source ) throws DuplicateSurveyRequestException, InvalidInputException,
        SelfSurveyInitiationException, SolrException, NoRecordsFetchedException, UndeliveredEmailException,
        ProfileNotFoundException
    {
        LOG.info( "Sending survey request for agent id: " + agentId + " recipientEmailId: " + recipientEmailId
            + " recipientFirstname: " + recipientFirstname + " recipientLastname: " + recipientLastname );
        if ( agentId <= 0l ) {
            LOG.warn( "Agentid should be non zero value" );
            throw new InvalidInputException( "Agent id is invalid" );
        }
        if ( recipientEmailId == null || recipientEmailId.isEmpty()
            || !recipientEmailId.trim().matches( CommonConstants.EMAIL_REGEX ) ) {
            LOG.warn( "Recipent email id should be passed." );
            throw new InvalidInputException( "Recipent email id is invalid" );
        }
        if ( ( recipientFirstname == null || recipientFirstname.isEmpty() )
            && ( recipientLastname == null || recipientLastname.isEmpty() ) ) {
            LOG.warn( "Recipent name should be passed." );
            throw new InvalidInputException( "Recipent name is invalid" );
        }
        // get the agent details
        User agent = userManagementService.getUserObjByUserId( agentId );
        // check if the survey is for self
        if ( agent.getEmailId().equals( recipientEmailId ) ) {
            LOG.warn( "Survey initiated for self." );
            throw new SelfSurveyInitiationException( "Survey cannot be initiated for self" );
        }
        // check if survey has already been sent to the email id
        // check the pre-initiation and then the survey table
        HashMap<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.AGENT_ID_COLUMN, agentId );
        queries.put( CommonConstants.CUSTOMER_EMAIL_ID_KEY_COLUMN, recipientEmailId );
        List<SurveyPreInitiation> incompleteSurveyCustomers = surveyPreInitiationDao.findByKeyValue( SurveyPreInitiation.class,
            queries );
        if ( incompleteSurveyCustomers != null && incompleteSurveyCustomers.size() > 0 ) {
            LOG.warn( "Survey request already sent" );
            throw new DuplicateSurveyRequestException( "Survey request already sent" );
        }
        // check the survey collection
        SurveyDetails survey = surveyDetailsDao.getSurveyByAgentIdAndCustomerEmail( agentId, recipientEmailId, null, null );
        if ( survey != null ) {
            LOG.warn( "Survey request already sent and completed" );
            throw new DuplicateSurveyRequestException( "Survey request already sent and completed" );
        }
        LOG.debug( "Sending survey request mail." );
        sendSurveyInvitationMail( recipientFirstname, recipientLastname, recipientEmailId, null, agent, true, source );
    }


    @Override
    public void deleteZillowSurveysByEntity( String entityType, long entityId ) throws InvalidInputException
    {
        LOG.info( "Method deleteZillowSurveysByEntity() started" );
        if ( entityType == null || entityType.isEmpty() ) {
            throw new InvalidInputException( "Entity Type is invalid" );
        }
        if ( entityId <= 0 ) {
            throw new InvalidInputException( "Entity ID is invalid" );
        }
        surveyDetailsDao.removeZillowSurveysByEntity( entityType, entityId );
        LOG.info( "Method deleteZillowSurveysByEntity() finished" );
    }


    @Override
    public void deleteExcessZillowSurveysByEntity( String entityType, long entityId ) throws InvalidInputException
    {
        LOG.info( "Method deleteExcessZillowSurveysByEntity() started" );
        if ( entityType == null || entityType.isEmpty() ) {
            throw new InvalidInputException( "Entity Type is invalid" );
        }
        if ( entityId <= 0 ) {
            throw new InvalidInputException( "Entity ID is invalid" );
        }
        surveyDetailsDao.removeExcessZillowSurveysByEntity( entityType, entityId );
        LOG.info( "Method deleteExcessZillowSurveysByEntity() finished" );
    }

    @Override
    public List<AbusiveSurveyReportWrapper> getSurveysReporetedAsAbusive( int startIndex, int numOfRows )
    {
        LOG.info( "Method getSurveysReporetedAsAbusive() to retrieve surveys marked as abusive, started" );
        List<AbusiveSurveyReportWrapper> abusiveSurveyReports = surveyDetailsDao.getSurveysReporetedAsAbusive( startIndex, numOfRows );
        LOG.info( "Method getSurveysReporetedAsAbusive() to retrieve surveys marked as abusive, finished" );
        return abusiveSurveyReports;
    }
}
// JIRA SS-119 by RM-05:EOC