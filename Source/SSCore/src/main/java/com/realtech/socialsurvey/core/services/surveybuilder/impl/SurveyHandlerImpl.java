package com.realtech.socialsurvey.core.services.surveybuilder.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
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
import com.realtech.socialsurvey.core.commons.EmailTemplateConstants;
import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.SurveyDetailsDao;
import com.realtech.socialsurvey.core.dao.SurveyPreInitiationDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.AbusiveSurveyReportWrapper;
import com.realtech.socialsurvey.core.entities.AgentMediaPostDetails;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.BranchMediaPostDetails;
import com.realtech.socialsurvey.core.entities.BulkSurveyDetail;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.CompanyIgnoredEmailMapping;
import com.realtech.socialsurvey.core.entities.CompanyMediaPostDetails;
import com.realtech.socialsurvey.core.entities.MailContent;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.RegionMediaPostDetails;
import com.realtech.socialsurvey.core.entities.SocialMediaPostDetails;
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
import com.realtech.socialsurvey.core.services.generator.UrlService;
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
import com.realtech.socialsurvey.core.utils.FileOperations;


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
    private FileOperations fileOperations;

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

    @Value ( "${MAX_SOCIAL_POST_REMINDER_INTERVAL}")
    private int maxSocialPostReminderInterval;


    @Value ( "${PARAM_ORDER_TAKE_SURVEY_SUBJECT}")
    String paramOrderTakeSurveySubject;
    @Value ( "${PARAM_ORDER_TAKE_SURVEY}")
    String paramOrderTakeSurvey;
    @Value ( "${PARAM_ORDER_TAKE_SURVEY_CUSTOMER}")
    String paramOrderTakeSurveyCustomer;
    @Value ( "${PARAM_ORDER_TAKE_SURVEY_REMINDER}")
    String paramOrderTakeSurveyReminder;
    @Value ( "${PARAM_ORDER_SURVEY_COPLETION_MAIL}")
    String paramOrderSurveyCompletionMail;
    @Value ( "${PARAM_ORDER_SOCIAL_POST_REMINDER}")
    String paramOrderSocialPostReminder;
    @Value ( "${PARAM_ORDER_INCOMPLETE_SURVEY_REMINDER}")
    String paramOrderIncompleteSurveyReminder;
    @Value ( "${PARAM_ORDER_SURVEY_COMPLETION_UNPLEASANT_MAIL}")
    String paramOrderSurveyCompletionUnpleasantMail;

    @Autowired
    private Utils utils;

    @Autowired
    private UrlService urlService;

    @Autowired
    private GenericDao<CompanyIgnoredEmailMapping, Long> companyIgnoredEmailMappingDao;


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
        if ( lastName != null && !lastName.isEmpty() && !lastName.equalsIgnoreCase( "null" ) )
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

        String surveyUrl = ( baseUrl.contains( "\\?q=" ) ) ? baseUrl : getSurveyUrl( agentId, customerEmail, baseUrl );
        surveyDetails.setUrl( surveyUrl );
        surveyDetails.setEditable( true );
        surveyDetails.setSource( source );
        surveyDetails.setShowSurveyOnUI( true );

        SurveyDetails survey = surveyDetailsDao
            .getSurveyByAgentIdAndCustomerEmail( agentId, customerEmail, firstName, lastName );
        LOG.info( "Method to store initial details of survey, storeInitialSurveyAnswers() finished." );

        if ( survey == null ) {
            surveyDetailsDao.insertSurveyDetails( surveyDetails );
            // LOG.info( "Updating modified on column in aagent hierarchy fro agent " );
            // updateModifiedOnColumnForAgentHierachy( agentId );
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
        if ( surveyDetails.getAgentId() > 0l ) {
            LOG.info( "Updating modified on column in aagent hierarchy fro agent " );
            try {
                updateModifiedOnColumnForAgentHierachy( surveyDetails.getAgentId() );
            } catch ( InvalidInputException e ) {
                LOG.error( "passed agent id in method updateModifiedOnColumnForAgentHierachy() is invalid" );
            }
        }
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
    public List<SurveyDetails> getSurveyDetailsByAgentAndCompany( long companyId )
    {
        LOG.info( "Method getSurveyDetails() to return survey details by agent id and customer email started." );
        List<SurveyDetails> surveys = surveyDetailsDao.getSurveyDetailsByAgentAndCompany( companyId );
        LOG.info( "Method getSurveyDetails() to return survey details by agent id and customer email finished." );
        return surveys;
    }


    @Override
    public void updateSurveyAsAbusive( String surveymongoId, String reporterEmail, String reporterName )
    {
        LOG.info( "Method updateSurveyAsAbusive() to mark the survey as abusive, started" );
        surveyDetailsDao.updateSurveyAsAbusive( surveymongoId, reporterEmail, reporterName );
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
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
        List<UserProfile> agentProfiles = userProfileDao.findByKeyValue( UserProfile.class, queries );
        for ( UserProfile agentProfile : agentProfiles ) {
            queries.clear();
            queries.put( CommonConstants.BRANCH_ID_COLUMN, agentProfile.getBranchId() );
            queries.put( CommonConstants.PROFILE_MASTER_COLUMN,
                userManagementService.getProfilesMasterById( CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID ) );
            queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
            admins.addAll( userProfileDao.findByKeyValue( UserProfile.class, queries ) );
            queries.clear();
            queries.put( CommonConstants.REGION_ID_COLUMN, agentProfile.getRegionId() );
            queries.put( CommonConstants.PROFILE_MASTER_COLUMN,
                userManagementService.getProfilesMasterById( CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID ) );
            queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
            admins.addAll( userProfileDao.findByKeyValue( UserProfile.class, queries ) );
            queries.clear();
            if ( agentProfile.getCompany() != null ) {
                queries.put( CommonConstants.COMPANY_COLUMN, agentProfile.getCompany() );
                queries.put( CommonConstants.PROFILE_MASTER_COLUMN,
                    userManagementService.getProfilesMasterById( CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID ) );
                queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
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
        Criterion companyCriteria = Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, company.getCompanyId() );
        Criterion statusCriteria = Restrictions.in( CommonConstants.STATUS_COLUMN,
            Arrays.asList( CommonConstants.STATUS_SURVEYPREINITIATION_PROCESSED, CommonConstants.SURVEY_STATUS_INITIATED ) );
        incompleteSurveyCustomers = surveyPreInitiationDao.findByCriteria( SurveyPreInitiation.class, companyCriteria,
            statusCriteria );
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
        List<SurveyDetails> incompleteSocialPostCustomers = new ArrayList<>();
        OrganizationUnitSettings organizationUnitSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(
            companyId, MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
        // Fetching surveyReminderInterval and max number of reminders for a company.
        if ( organizationUnitSettings != null ) {
            SurveySettings surveySettings = organizationUnitSettings.getSurvey_settings();
            if ( surveySettings != null ) {
                if ( !surveySettings.getIsSocialPostReminderDisabled()
                    && surveySettings.getSocial_post_reminder_interval_in_days() > 0 ) {
                    reminderInterval = surveySettings.getSocial_post_reminder_interval_in_days();
                    maxReminders = surveySettings.getMax_number_of_social_pos_reminders();
                }
            }
        }
        if ( maxReminders == 0 ) {
            maxReminders = maxSocialpostReminders;
        }
        if ( reminderInterval == 0 ) {
            reminderInterval = socialPostReminderInterval;
        }

        if ( reminderInterval > maxSocialPostReminderInterval ) {
            reminderInterval = maxSocialPostReminderInterval;
        }

        incompleteSocialPostCustomers = surveyDetailsDao.getIncompleteSocialPostCustomersEmail( companyId, reminderInterval,
            maxReminders );
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
    public void increaseSurveyCountForAgent( long agentId ) throws SolrException, NoRecordsFetchedException,
        InvalidInputException
    {
        LOG.info( "Method to increase survey count for agent started." );
        organizationUnitSettingsDao.updateCompletedSurveyCountForAgent( agentId, 1 );
        solrSearchService.updateCompletedSurveyCountForUserInSolr( agentId, 1 );
        LOG.info( "Method to increase survey count for agent finished." );
    }


    @Override
    public void decreaseSurveyCountForAgent( long agentId ) throws SolrException, NoRecordsFetchedException,
        InvalidInputException
    {
        LOG.info( "Method to decrease survey count for agent started." );
        organizationUnitSettingsDao.updateCompletedSurveyCountForAgent( agentId, -1 );
        solrSearchService.updateCompletedSurveyCountForUserInSolr( agentId, -1 );
        LOG.info( "Method to decrease survey count for agent finished." );
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
        //JIRA SS-1363 begin
        /*if ( organizationUnit == OrganizationUnit.COMPANY ) {
            OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( companyId );
            logoUrl = companySettings.getLogoThumbnail();
        } else if ( organizationUnit == OrganizationUnit.REGION ) {
            OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( regionId );
            logoUrl = regionSettings.getLogoThumbnail();
        } else if ( organizationUnit == OrganizationUnit.BRANCH ) {
            OrganizationUnitSettings branchSettings = null;
            try {
                branchSettings = organizationManagementService.getBranchSettingsDefault( branchId );
            } catch ( NoRecordsFetchedException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if ( branchSettings != null ) {
                logoUrl = branchSettings.getLogoThumbnail();
            }
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
        //JIRA SS-1363 end

        if ( logoUrl == null || logoUrl.equalsIgnoreCase( "" ) ) {
            logoUrl = appLogoUrl;
        }

        OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user.getCompany()
            .getCompanyId() );
        preInitiateSurvey( user, custEmail, custFirstName, custLastName, 0, custRelationWithAgent,
            CommonConstants.SURVEY_REQUEST_AGENT );

        //get mail subject and body
        String mailBody = "";
        String mailSubject = "";
        if ( companySettings != null && companySettings.getMail_content() != null
            && companySettings.getMail_content().getRestart_survey_mail() != null ) {

            MailContent restartSurvey = companySettings.getMail_content().getRestart_survey_mail();
            mailBody = emailFormatHelper.replaceEmailBodyWithParams( restartSurvey.getMail_body(),
                restartSurvey.getParam_order() );
            LOG.info( "Initiating URL Service to shorten the url " + surveyUrl );
            surveyUrl = urlService.shortenUrl( surveyUrl );
            LOG.info( "Finished calling URL Service to shorten the url.Shortened URL : " + surveyUrl );

            mailSubject = restartSurvey.getMail_subject();
            if ( mailSubject == null || mailSubject.isEmpty() ) {
                mailSubject = CommonConstants.RESTART_SURVEY_MAIL_SUBJECT;
            }
        } else {
            mailSubject = fileOperations.getContentFromFile( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
                + EmailTemplateConstants.SURVEY_RESTART_MAIL_SUBJECT );

            mailBody = fileOperations.getContentFromFile( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
                + EmailTemplateConstants.SURVEY_RESTART_MAIL_BODY );
            mailBody = emailFormatHelper.replaceEmailBodyWithParams( mailBody,
                new ArrayList<String>( Arrays.asList( paramOrderIncompleteSurveyReminder.split( "," ) ) ) );
        }
        //replace the legends
        mailSubject = emailFormatHelper.replaceLegends( true, mailSubject, applicationBaseUrl, logoUrl, surveyUrl,
            custFirstName, custLastName, agentName, agentSignature, custEmail, user.getEmailId(), companyName,
            dateFormat.format( new Date() ), currentYear, fullAddress, "", user.getProfileName() );

        mailBody = emailFormatHelper.replaceLegends( false, mailBody, applicationBaseUrl, logoUrl, surveyUrl, custFirstName,
            custLastName, agentName, agentSignature, custEmail, user.getEmailId(), companyName,
            dateFormat.format( new Date() ), currentYear, fullAddress, "", user.getProfileName() );

        //send mail
        try {
            emailServices.sendSurveyInvitationMail( custEmail, mailSubject, mailBody, user.getEmailId(), agentName,
                user.getUserId() );
        } catch ( InvalidInputException | UndeliveredEmailException e ) {
            LOG.error( "Exception caught while sending mail to " + custEmail + ". Nested exception is ", e );
        }
        LOG.info( "sendSurveyRestartMail() finished." );
    }


    @Override
    public void sendSurveyCompletionMail( String custEmail, String custFirstName, String custLastName, User user )
        throws InvalidInputException, UndeliveredEmailException, ProfileNotFoundException
    {
        LOG.info( "sendSurveyCompletionMail() started." );
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
        //JIRA SS-1363 begin
        /*if ( organizationUnit == OrganizationUnit.COMPANY ) {
            OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( companyId );
            logoUrl = companySettings.getLogoThumbnail();
        } else if ( organizationUnit == OrganizationUnit.REGION ) {
            OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( regionId );
            logoUrl = regionSettings.getLogoThumbnail();
        } else if ( organizationUnit == OrganizationUnit.BRANCH ) {
            OrganizationUnitSettings branchSettings = null;
            try {
                branchSettings = organizationManagementService.getBranchSettingsDefault( branchId );
            } catch ( NoRecordsFetchedException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if ( branchSettings != null ) {
                logoUrl = branchSettings.getLogoThumbnail();
            }
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

        if ( logoUrl == null || logoUrl.equalsIgnoreCase( "" ) ) {
            logoUrl = appLogoUrl;
        }
        //JIRA SS-1363 end

        OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user.getCompany()
            .getCompanyId() );


        //get mail subject and body
        String mailSubject = "";
        String mailBody = "";
        if ( companySettings != null && companySettings.getMail_content() != null
            && companySettings.getMail_content().getSurvey_completion_mail() != null ) {

            MailContent surveyCompletion = companySettings.getMail_content().getSurvey_completion_mail();
            mailBody = emailFormatHelper.replaceEmailBodyWithParams( surveyCompletion.getMail_body(),
                surveyCompletion.getParam_order() );
            mailSubject = surveyCompletion.getMail_subject();
            if ( mailSubject == null || mailSubject.isEmpty() ) {
                mailSubject = CommonConstants.SURVEY_COMPLETION_MAIL_SUBJECT;
            }
        } else {
            mailSubject = fileOperations.getContentFromFile( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
                + EmailTemplateConstants.SURVEY_COMPLETION_MAIL_SUBJECT );

            mailBody = fileOperations.getContentFromFile( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
                + EmailTemplateConstants.SURVEY_COMPLETION_MAIL_BODY );
            mailBody = emailFormatHelper.replaceEmailBodyWithParams( mailBody,
                new ArrayList<String>( Arrays.asList( paramOrderSurveyCompletionMail.split( "," ) ) ) );
        }

        //replace the legends
        mailSubject = emailFormatHelper.replaceLegends( true, mailSubject, applicationBaseUrl, logoUrl, null, custFirstName,
            custLastName, agentName, agentSignature, custEmail, user.getEmailId(), companyName,
            dateFormat.format( new Date() ), currentYear, fullAddress, "", user.getProfileName() );

        mailBody = emailFormatHelper.replaceLegends( false, mailBody, applicationBaseUrl, logoUrl, null, custFirstName,
            custLastName, agentName, agentSignature, custEmail, user.getEmailId(), companyName,
            dateFormat.format( new Date() ), currentYear, fullAddress, "", user.getProfileName() );

        //send mail
        try {
            emailServices.sendSurveyInvitationMail( custEmail, mailSubject, mailBody, user.getEmailId(), user.getFirstName()
                + ( user.getLastName() != null ? " " + user.getLastName() : "" ), user.getUserId() );
        } catch ( InvalidInputException | UndeliveredEmailException e ) {
            LOG.error( "Exception caught while sending mail to " + custEmail + ". Nested exception is ", e );
        }
        LOG.info( "sendSurveyCompletionMail() finished." );
    }


    @Override
    public void sendSurveyCompletionUnpleasantMail( String custEmail, String custFirstName, String custLastName, User user )
        throws InvalidInputException, UndeliveredEmailException, ProfileNotFoundException
    {
        LOG.info( "sendSurveyCompletionUnpleasantMail() started." );
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
        //JIRA SS-1363 begin
        /*if ( organizationUnit == OrganizationUnit.COMPANY ) {
            OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( companyId );
            logoUrl = companySettings.getLogoThumbnail();
        } else if ( organizationUnit == OrganizationUnit.REGION ) {
            OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( regionId );
            logoUrl = regionSettings.getLogoThumbnail();
        } else if ( organizationUnit == OrganizationUnit.BRANCH ) {
            OrganizationUnitSettings branchSettings = null;
            try {
                branchSettings = organizationManagementService.getBranchSettingsDefault( branchId );
            } catch ( NoRecordsFetchedException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if ( branchSettings != null ) {
                logoUrl = branchSettings.getLogoThumbnail();
            }
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
        //JIRA SS-1363 end

        OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user.getCompany()
            .getCompanyId() );

        if ( companySettings != null && companySettings.getMail_content() != null
            && companySettings.getMail_content().getSurvey_completion_unpleasant_mail() != null ) {

            MailContent surveyCompletionUnpleasant = companySettings.getMail_content().getSurvey_completion_unpleasant_mail();

            // If Mail Body is empty redirect to survey completion mail method
            if ( surveyCompletionUnpleasant.getMail_body() == null
                || surveyCompletionUnpleasant.getMail_body().trim().length() == 0 ) {
                sendSurveyCompletionMail( custEmail, custFirstName, custLastName, user );
                return;
            }

            String mailBody = emailFormatHelper.replaceEmailBodyWithParams( surveyCompletionUnpleasant.getMail_body(),
                surveyCompletionUnpleasant.getParam_order() );

            if ( logoUrl == null || logoUrl.equalsIgnoreCase( "" ) ) {
                logoUrl = appLogoUrl;
            }

            mailBody = emailFormatHelper.replaceLegends( false, mailBody, applicationBaseUrl, logoUrl, null, custFirstName,
                custLastName, agentName, agentSignature, custEmail, user.getEmailId(), companyName,
                dateFormat.format( new Date() ), currentYear, fullAddress, "", user.getProfileName() );

            String mailSubject = surveyCompletionUnpleasant.getMail_subject();
            if ( mailSubject == null || mailSubject.isEmpty() ) {
                mailSubject = CommonConstants.SURVEY_COMPLETION_UNPLEASANT_MAIL_SUBJECT;
            }

            mailSubject = emailFormatHelper.replaceLegends( true, mailSubject, applicationBaseUrl, logoUrl, null,
                custFirstName, custLastName, agentName, agentSignature, custEmail, user.getEmailId(), companyName,
                dateFormat.format( new Date() ), currentYear, fullAddress, "", user.getProfileName() );
            try {
                emailServices.sendSurveyInvitationMail( custEmail, mailSubject, mailBody, user.getEmailId(),
                    user.getFirstName() + ( user.getLastName() != null ? " " + user.getLastName() : "" ), user.getUserId() );
            } catch ( InvalidInputException | UndeliveredEmailException e ) {
                LOG.error( "Exception caught while sending mail to " + custEmail + ". Nested exception is ", e );
            }
        } else {
            sendSurveyCompletionMail( custEmail, custFirstName, custLastName, user );
        }
        LOG.info( "sendSurveyCompletionUnpleasantMail() finished." );
    }


    @Override
    public void sendSocialPostReminderMail( String custEmail, String custFirstName, String custLastName, User user, String links )
        throws InvalidInputException, UndeliveredEmailException, ProfileNotFoundException
    {
        LOG.info( "sendSocialPostReminderMail() started." );
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
        //JIRA SS-1363 begin
        /*if ( organizationUnit == OrganizationUnit.COMPANY ) {
            OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( companyId );
            logoUrl = companySettings.getLogoThumbnail();
        } else if ( organizationUnit == OrganizationUnit.REGION ) {
            OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( regionId );
            logoUrl = regionSettings.getLogoThumbnail();
        } else if ( organizationUnit == OrganizationUnit.BRANCH ) {
            OrganizationUnitSettings branchSettings = null;
            try {
                branchSettings = organizationManagementService.getBranchSettingsDefault( branchId );
            } catch ( NoRecordsFetchedException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if ( branchSettings != null ) {
                logoUrl = branchSettings.getLogoThumbnail();
            }
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

        if ( logoUrl == null || logoUrl.equalsIgnoreCase( "" ) ) {
            logoUrl = appLogoUrl;
        }
        //JIRA SS-1363 end

        OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user.getCompany()
            .getCompanyId() );

        //get mail subject and body
        String mailSubject = "";
        String mailBody = "";
        if ( companySettings != null && companySettings.getMail_content() != null
            && companySettings.getMail_content().getSocial_post_reminder_mail() != null ) {

            MailContent socialPostReminder = companySettings.getMail_content().getSocial_post_reminder_mail();
            mailBody = emailFormatHelper.replaceEmailBodyWithParams( socialPostReminder.getMail_body(),
                socialPostReminder.getParam_order() );

            mailSubject = socialPostReminder.getMail_subject();
            if ( mailSubject == null || mailSubject.isEmpty() ) {
                mailSubject = CommonConstants.SOCIAL_POST_REMINDER_MAIL_SUBJECT;
            }

        } else {
            mailSubject = fileOperations.getContentFromFile( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
                + EmailTemplateConstants.SOCIALPOST_REMINDER_MAIL_SUBJECT );

            mailBody = fileOperations.getContentFromFile( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
                + EmailTemplateConstants.SOCIALPOST_REMINDER_MAIL_BODY );
            mailBody = emailFormatHelper.replaceEmailBodyWithParams( mailBody,
                new ArrayList<String>( Arrays.asList( paramOrderSocialPostReminder.split( "," ) ) ) );
        }

        //replace legends
        mailSubject = emailFormatHelper.replaceLegends( true, mailSubject, applicationBaseUrl, logoUrl, "", custFirstName,
            custLastName, agentName, agentSignature, custEmail, user.getEmailId(), companyName,
            dateFormat.format( new Date() ), currentYear, fullAddress, links, user.getProfileName() );

        mailBody = emailFormatHelper.replaceLegends( false, mailBody, applicationBaseUrl, logoUrl, "", custFirstName,
            custLastName, agentName, agentSignature, custEmail, user.getEmailId(), companyName,
            dateFormat.format( new Date() ), currentYear, fullAddress, links, user.getProfileName() );
        //send mail
        try {
            emailServices.sendSurveyInvitationMail( custEmail, mailSubject, mailBody, user.getEmailId(), user.getFirstName(),
                user.getUserId() );
        } catch ( InvalidInputException | UndeliveredEmailException e ) {
            LOG.error( "Exception caught while sending mail to " + custEmail + ". Nested exception is ", e );
        }

        LOG.info( "sendSocialPostReminderMail() finished." );
    }


    // Method to fetch initial survey details from MySQL based upn agent id and customer email.
    @Override
    @Transactional
    public SurveyPreInitiation getPreInitiatedSurvey( long agentId, String customerEmail, String custFirstName,
        String custLastName ) throws NoRecordsFetchedException
    {
        LOG.info( "Method getPreInitiatedSurvey() started. " );
        /*Map<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.AGENT_ID_COLUMN, agentId );
        queries.put( "customerEmailId", customerEmail );*/
        Criterion agentIdCriteria = Restrictions.eq( CommonConstants.AGENT_ID_COLUMN, agentId );
        Criterion emailCriteria = Restrictions.eq( "customerEmailId", customerEmail );
        Criterion statusCriteria = Restrictions.and(
            Restrictions.ne( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_SURVEYPREINITIATION_COMPLETE ),
            Restrictions.ne( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_SURVEYPREINITIATION_DELETED ) );
        Criterion firstNameCriteria = null;
        Criterion lastNameCriteria = null;

        if ( custFirstName != null && !custFirstName.isEmpty() ) {
            //queries.put( "customerFirstName", custFirstName );
            firstNameCriteria = Restrictions.eq( "customerFirstName", custFirstName );
        }
        if ( custLastName != null && !custFirstName.isEmpty() ) {
            //queries.put( "customerLastName", custLastName );
            lastNameCriteria = Restrictions.eq( "customerLastName", custLastName );
        }
        List<SurveyPreInitiation> surveyPreInitiations;
        if ( firstNameCriteria != null && lastNameCriteria != null ) {
            surveyPreInitiations = surveyPreInitiationDao.findByCriteria( SurveyPreInitiation.class, agentIdCriteria,
                emailCriteria, firstNameCriteria, lastNameCriteria, statusCriteria );
        } else if ( firstNameCriteria != null ) {
            surveyPreInitiations = surveyPreInitiationDao.findByCriteria( SurveyPreInitiation.class, agentIdCriteria,
                emailCriteria, firstNameCriteria, statusCriteria );
        } else if ( lastNameCriteria != null ) {
            surveyPreInitiations = surveyPreInitiationDao.findByCriteria( SurveyPreInitiation.class, agentIdCriteria,
                emailCriteria, lastNameCriteria, statusCriteria );
        } else {
            surveyPreInitiations = surveyPreInitiationDao.findByCriteria( SurveyPreInitiation.class, agentIdCriteria,
                emailCriteria, statusCriteria );
        }

        /*List<SurveyPreInitiation> surveyPreInitiations = surveyPreInitiationDao.findByKeyValue( SurveyPreInitiation.class,
            queries );*/
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
            surveyPreInitiation.setStatus( CommonConstants.STATUS_SURVEYPREINITIATION_COMPLETE );
        surveyPreInitiationDao.saveOrUpdate( surveyPreInitiation );
        //surveyPreInitiationDao.delete( surveyPreInitiation );
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
        return urlGenerator.generateUrl( urlParams, getApplicationBaseUrl() + CommonConstants.SHOW_SURVEY_PAGE_FOR_URL );
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
        List<SurveyPreInitiation> ignoredEmailRecords = new ArrayList<>();
        List<SurveyPreInitiation> oldRecords = new ArrayList<>();

        Set<Long> companies = new HashSet<>();
        for ( SurveyPreInitiation survey : surveys ) {
            int status = CommonConstants.STATUS_SURVEYPREINITIATION_PROCESSED;
            User user = null;
            if ( survey.getAgentEmailId() != null ) {
                try {
                    user = userManagementService.getActiveUserByEmailAndCompany( survey.getCompanyId(),
                        survey.getAgentEmailId() );
                } catch ( InvalidInputException | NoRecordsFetchedException e ) {
                    LOG.error( "No user found in database for the email id: " + survey.getAgentEmailId() + " and company id : "
                        + survey.getCompanyId() );
                }

                if ( user != null ) {
                    // check if survey has already been sent to the email id
                    // check the pre-initiation and then the survey table
                    HashMap<String, Object> queries = new HashMap<>();
                    queries.put( CommonConstants.AGENT_ID_COLUMN, user.getUserId() );
                    queries.put( CommonConstants.CUSTOMER_EMAIL_ID_KEY_COLUMN, survey.getCustomerEmailId() );
                    List<SurveyPreInitiation> incompleteSurveyCustomers = surveyPreInitiationDao.findByKeyValue(
                        SurveyPreInitiation.class, queries );
                    if ( incompleteSurveyCustomers != null && incompleteSurveyCustomers.size() > 0 ) {
                        LOG.warn( "Survey request already sent" );
                        status = CommonConstants.STATUS_SURVEYPREINITIATION_DUPLICATE_RECORD;
                        survey.setStatus( status );
                    }
                    // check the survey collection
                    SurveyDetails surveyDetail = surveyDetailsDao.getSurveyByAgentIdAndCustomerEmail( user.getUserId(),
                        survey.getCustomerEmailId(), null, null );
                    if ( surveyDetail != null ) {
                        LOG.warn( "Survey request already sent and completed" );
                        status = CommonConstants.STATUS_SURVEYPREINITIATION_DUPLICATE_RECORD;
                        survey.setStatus( status );
                    }

                    LOG.debug( "Mapping the agent to this survey " );
                    if ( survey.getAgentId() == 0 ) {
                        survey.setAgentId( user.getUserId() );
                    }
                    surveyPreInitiationDao.update( survey );
                }

            }
            Timestamp engagementClosedTime = survey.getEngagementClosedTime();
            Calendar calendar = Calendar.getInstance();
            calendar.add( Calendar.DATE, -validSurveyInterval );
            Date date = calendar.getTime();

            if ( engagementClosedTime.before( date ) ) {
                LOG.info( "An old record found : " + survey.getSurveyPreIntitiationId() );
                status = CommonConstants.STATUS_SURVEYPREINITIATION_OLD_RECORD;
                oldRecords.add( survey );
                companies.add( survey.getCompanyId() );
            } else if ( user == null && isEmailIsIgnoredEmail( survey.getAgentEmailId(), survey.getCompanyId() ) ) {
                LOG.error( "no agent found with this email id and its an ignored record" );
                status = CommonConstants.STATUS_SURVEYPREINITIATION_IGNORED_RECORD;
                ignoredEmailRecords.add( survey );
                companies.add( survey.getCompanyId() );
            } else if ( survey.getAgentEmailId() == null || survey.getAgentEmailId().isEmpty() ) {
                LOG.error( "Agent email not found , invalid survey " + survey.getSurveyPreIntitiationId() );
                status = CommonConstants.STATUS_SURVEYPREINITIATION_CORRUPT_RECORD;
                unavailableAgents.add( survey );
                companies.add( survey.getCompanyId() );
            } else if ( ( survey.getCustomerFirstName() == null || survey.getCustomerFirstName().isEmpty() )
                && ( survey.getCustomerLastName() == null || survey.getCustomerLastName().isEmpty() ) ) {
                LOG.error( "No Name found for customer, hence this is an invalid survey " + survey.getSurveyPreIntitiationId() );
                status = CommonConstants.STATUS_SURVEYPREINITIATION_CORRUPT_RECORD;
                customersWithoutName.add( survey );

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
            if ( status != CommonConstants.STATUS_SURVEYPREINITIATION_CORRUPT_RECORD
                && status != CommonConstants.STATUS_SURVEYPREINITIATION_IGNORED_RECORD
                && status != CommonConstants.STATUS_SURVEYPREINITIATION_OLD_RECORD ) {
                if ( survey.getSurveySource().equalsIgnoreCase( CommonConstants.CRM_SOURCE_DOTLOOP ) ) {
                    status = validateUnitsettingsForDotloop( user, survey );
                    if ( status == CommonConstants.STATUS_SURVEYPREINITIATION_CORRUPT_RECORD ) {
                        unavailableAgents.add( survey );
                        companies.add( survey.getCompanyId() );
                    }
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
        corruptRecords.put( "ignoredEmailRecords", ignoredEmailRecords );
        corruptRecords.put( "oldRecords", oldRecords );
        corruptRecords.put( "companies", companies );
        return corruptRecords;
    }


    /**
     * 
     * @param emailId
     * @return
     */
    boolean isEmailIsIgnoredEmail( String emailId, long companyId )
    {
        LOG.debug( "Inside method isEmailIsIgnoredEmail for email : " + emailId );
        Map<String, Object> queries = new HashMap<String, Object>();
        queries.put( "emailId", emailId );
        queries.put( "company.companyId", companyId );
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
        List<CompanyIgnoredEmailMapping> companyIgnoredEmailMapping = companyIgnoredEmailMappingDao.findByKeyValue(
            CompanyIgnoredEmailMapping.class, queries );
        if ( companyIgnoredEmailMapping == null || companyIgnoredEmailMapping.size() == 0 )
            return false;
        else
            return true;
    }


    /**
     * 
     * @param user
     * @param surveyPreInitiation
     * @return
     */
    int validateUnitsettingsForDotloop( User user, SurveyPreInitiation surveyPreInitiation )
    {
        LOG.info( "Inside method validateUnitSettingsForDotloop " );
        int status = CommonConstants.STATUS_SURVEYPREINITIATION_PROCESSED;
        if ( surveyPreInitiation != null ) {
            LOG.info( "Processing survey pre initiation id: " + surveyPreInitiation.getSurveyPreIntitiationId() );
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

        if ( logoUrl == null || logoUrl.equalsIgnoreCase( "" ) ) {
            logoUrl = appLogoUrl;
        }

        OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user.getCompany()
            .getCompanyId() );

        //get mail subject and body
        String mailBody = "";
        String mailSubject = "";
        if ( companySettings != null && companySettings.getMail_content() != null
            && companySettings.getMail_content().getTake_survey_mail() != null ) {

            MailContent takeSurvey = companySettings.getMail_content().getTake_survey_mail();
            mailBody = emailFormatHelper.replaceEmailBodyWithParams( takeSurvey.getMail_body(), takeSurvey.getParam_order() );

            LOG.info( "Initiating URL Service to shorten the url " + surveyUrl );
            surveyUrl = urlService.shortenUrl( surveyUrl );
            LOG.info( "Finished calling URL Service to shorten the url.Shortened URL : " + surveyUrl );

            // Adding mail subject
            mailSubject = CommonConstants.SURVEY_MAIL_SUBJECT + agentName;
            if ( takeSurvey.getMail_subject() != null && !takeSurvey.getMail_subject().isEmpty() ) {
                mailSubject = takeSurvey.getMail_subject();
            }
        } else {

            mailSubject = fileOperations.getContentFromFile( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
                + EmailTemplateConstants.SURVEY_INVITATION_MAIL_SUBJECT );
            
            mailSubject = emailFormatHelper.replaceEmailBodyWithParams( mailSubject, Arrays.asList( paramOrderTakeSurveySubject.split( "," ) ) );

            mailBody = fileOperations.getContentFromFile( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
                + EmailTemplateConstants.SURVEY_INVITATION_MAIL_BODY );

            mailBody = emailFormatHelper.replaceEmailBodyWithParams( mailBody,
                new ArrayList<String>( Arrays.asList( paramOrderTakeSurvey.split( "," ) ) ) );
        }

        //replace the legends
        mailSubject = emailFormatHelper.replaceLegends( true, mailSubject, applicationBaseUrl, logoUrl, surveyUrl,
            custFirstName, custLastName, agentName, agentSignature, custEmail, user.getEmailId(), companyName,
            dateFormat.format( new Date() ), currentYear, fullAddress, "", user.getProfileName() );

        mailBody = emailFormatHelper.replaceLegends( false, mailBody, applicationBaseUrl, logoUrl, surveyUrl, custFirstName,
            custLastName, agentName, agentSignature, custEmail, user.getEmailId(), companyName,
            dateFormat.format( new Date() ), currentYear, fullAddress, "", user.getProfileName() );

        //send the mail
        try {
            emailServices.sendSurveyInvitationMail( custEmail, mailSubject, mailBody, user.getEmailId(), agentName,
                user.getUserId() );
        } catch ( InvalidInputException | UndeliveredEmailException e ) {
            LOG.error( "Exception caught while sending mail to " + custEmail + ". Nested exception is ", e );
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

        String mailBody = "";
        String mailSubject = "";
        if ( companySettings != null && companySettings.getMail_content() != null
            && companySettings.getMail_content().getTake_survey_mail_customer() != null ) {

            MailContent takeSurveyCustomer = companySettings.getMail_content().getTake_survey_mail_customer();
            mailBody = emailFormatHelper.replaceEmailBodyWithParams( takeSurveyCustomer.getMail_body(),
                takeSurveyCustomer.getParam_order() );
            mailSubject = CommonConstants.SURVEY_MAIL_SUBJECT_CUSTOMER;
        } else {
            mailSubject = fileOperations.getContentFromFile( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
                + EmailTemplateConstants.SURVEY_INVITATION_MAIL_SUBJECT );

            mailBody = fileOperations.getContentFromFile( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
                + EmailTemplateConstants.SURVEY_INVITATION_MAIL_BODY );

            mailBody = emailFormatHelper.replaceEmailBodyWithParams( mailBody,
                new ArrayList<String>( Arrays.asList( paramOrderTakeSurveyCustomer.split( "," ) ) ) );

        }
        //replace legends
        mailBody = emailFormatHelper.replaceLegends( false, mailBody, applicationBaseUrl, appLogoUrl, link, custFirstName,
            custLastName, user.getFirstName() + " " + user.getLastName(), null, null, null, null, null, null, null, "",
            user.getProfileName() );
        mailSubject = emailFormatHelper.replaceLegends( true, mailSubject, applicationBaseUrl, appLogoUrl, link, custFirstName,
            custLastName, user.getFirstName() + " " + user.getLastName(), null, null, null, null, null, null, null, "",
            user.getProfileName() );

        //send mail
        try {
            emailServices.sendSurveyInvitationMailByCustomer( custEmail, mailSubject, mailBody, user.getEmailId(),
                user.getFirstName(), user.getUserId() );
        } catch ( InvalidInputException | UndeliveredEmailException e ) {
            LOG.error( "Exception caught while sending mail to " + custEmail + ". Nested exception is ", e );
        }
        LOG.debug( "sendInvitationMailByCustomer() finished." );
    }


    // Method to store details of a customer in mysql at the time of sending invite.
    void preInitiateSurvey( User user, String custEmail, String custFirstName, String custLastName, int i,
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


    @Override
    public Boolean checkIfTimeIntervalHasExpired( long lastRemindedTime, long systemTime, int reminderInterval )
    {
        LOG.debug( "Checking time interval expiry: lastRemindedTime " + lastRemindedTime + "\t systemTime: " + systemTime
            + "\t reminderInterval: " + reminderInterval );
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
            || !organizationManagementService.validateEmail( recipientEmailId ) ) {
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
        /*HashMap<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.AGENT_ID_COLUMN, agentId );
        queries.put( CommonConstants.CUSTOMER_EMAIL_ID_KEY_COLUMN, recipientEmailId );
        List<SurveyPreInitiation> incompleteSurveyCustomers = surveyPreInitiationDao.findByKeyValue( SurveyPreInitiation.class,
            queries );*/

        Criterion agentIdCriteria = Restrictions.eq( CommonConstants.AGENT_ID_COLUMN, agentId );
        Criterion emailCriteria = Restrictions.eq( CommonConstants.CUSTOMER_EMAIL_ID_KEY_COLUMN, recipientEmailId );
        Criterion statusCriteria = Restrictions.and(
            Restrictions.ne( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_SURVEYPREINITIATION_COMPLETE ),
            Restrictions.ne( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_SURVEYPREINITIATION_DELETED ) );
        List<SurveyPreInitiation> incompleteSurveyCustomers = surveyPreInitiationDao.findByCriteria( SurveyPreInitiation.class,
            agentIdCriteria, emailCriteria, statusCriteria );

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


    //    Commented as Zillow surveys are not stored in database, SS-1276
    //    @Override
    //    @Transactional
    //    public void deleteZillowSurveysByEntity( String entityType, long entityId ) throws InvalidInputException
    //    {
    //        LOG.info( "Method deleteZillowSurveysByEntity() started" );
    //        if ( entityType == null || entityType.isEmpty() ) {
    //            throw new InvalidInputException( "Entity Type is invalid" );
    //        }
    //        if ( entityId <= 0 ) {
    //            throw new InvalidInputException( "Entity ID is invalid" );
    //        }
    //        surveyDetailsDao.removeZillowSurveysByEntity( entityType, entityId );
    //        LOG.info( "Method deleteZillowSurveysByEntity() finished" );
    //    }

    //    Commented as Zillow surveys are not stored in database, SS-1276
    //    @Override
    //    @Transactional
    //    public void deleteExcessZillowSurveysByEntity( String entityType, long entityId ) throws InvalidInputException
    //    {
    //         LOG.info( "Method deleteExcessZillowSurveysByEntity() started" );
    //         if ( entityType == null || entityType.isEmpty() ) {
    //             throw new InvalidInputException( "Entity Type is invalid" );
    //         }
    //         if ( entityId <= 0 ) {
    //             throw new InvalidInputException( "Entity ID is invalid" );
    //         }
    //         surveyDetailsDao.removeExcessZillowSurveysByEntity( entityType, entityId );
    //         LOG.info( "Method deleteExcessZillowSurveysByEntity() finished" );
    //    }

    @Override
    @Transactional
    public void deleteExistingZillowSurveysByEntity( String entityType, long entityId ) throws InvalidInputException
    {
        LOG.info( "Method deleteExistingZillowSurveysByEntity() started" );
        if ( entityType == null || entityType.isEmpty() ) {
            throw new InvalidInputException( "Entity Type is invalid" );
        }
        if ( entityId <= 0 ) {
            throw new InvalidInputException( "Entity ID is invalid" );
        }
        surveyDetailsDao.removeExistingZillowSurveysByEntity( entityType, entityId );
        LOG.info( "Method deleteExistingZillowSurveysByEntity() finished" );
    }


    @Override
    public List<AbusiveSurveyReportWrapper> getSurveysReportedAsAbusive( int startIndex, int numOfRows )
    {
        LOG.info( "Method getSurveysReporetedAsAbusive() to retrieve surveys marked as abusive, started" );
        List<AbusiveSurveyReportWrapper> abusiveSurveyReports = surveyDetailsDao.getSurveysReporetedAsAbusive( startIndex,
            numOfRows );
        LOG.info( "Method getSurveysReporetedAsAbusive() to retrieve surveys marked as abusive, finished" );
        return abusiveSurveyReports;
    }


    @Override
    @Transactional
    public void updateSurveyDetails( SurveyDetails surveyDetails )
    {
        surveyDetailsDao.updateSurveyDetails( surveyDetails );
    }


    public boolean validateDecryptedApiParams( Map<String, String> params )
    {
        boolean valid = false;
        if ( params != null ) {
            String comapnyId = params.get( CommonConstants.COMPANY_ID_COLUMN );
            String apiKey = params.get( CommonConstants.API_KEY_COLUMN );
            String apiSecret = params.get( CommonConstants.API_SECRET_COLUMN );
            try {
                if ( userManagementService.validateUserApiKey( apiKey, apiSecret, Long.valueOf( comapnyId ) ) ) {
                    valid = true;
                }
            } catch ( NumberFormatException e ) {
                LOG.error( "Exception caught ", e );
            } catch ( InvalidInputException e ) {
                LOG.error( "Exception caught ", e );
            }
        }
        return valid;
    }


    public void updateModifiedOnColumnForEntity( String entityType, long entityId )
    {
        LOG.debug( "method updateModifiedOnColumnForEntity() started" );
        if ( entityType.equalsIgnoreCase( CommonConstants.COMPANY_ID_COLUMN ) ) {
            organizationUnitSettingsDao.updateKeyOrganizationUnitSettingsByCriteria(
                MongoOrganizationUnitSettingDaoImpl.KEY_MODIFIED_ON, System.currentTimeMillis(),
                MongoOrganizationUnitSettingDaoImpl.KEY_IDENTIFIER, entityId,
                MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
        } else if ( entityType.equalsIgnoreCase( CommonConstants.REGION_ID_COLUMN ) ) {
            organizationUnitSettingsDao.updateKeyOrganizationUnitSettingsByCriteria(
                MongoOrganizationUnitSettingDaoImpl.KEY_MODIFIED_ON, System.currentTimeMillis(),
                MongoOrganizationUnitSettingDaoImpl.KEY_IDENTIFIER, entityId,
                MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
        } else if ( entityType.equalsIgnoreCase( CommonConstants.BRANCH_ID_COLUMN ) ) {
            organizationUnitSettingsDao.updateKeyOrganizationUnitSettingsByCriteria(
                MongoOrganizationUnitSettingDaoImpl.KEY_MODIFIED_ON, System.currentTimeMillis(),
                MongoOrganizationUnitSettingDaoImpl.KEY_IDENTIFIER, entityId,
                MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );
        } else if ( entityType.equalsIgnoreCase( CommonConstants.AGENT_ID_COLUMN ) ) {
            organizationUnitSettingsDao.updateKeyOrganizationUnitSettingsByCriteria(
                MongoOrganizationUnitSettingDaoImpl.KEY_MODIFIED_ON, System.currentTimeMillis(),
                MongoOrganizationUnitSettingDaoImpl.KEY_IDENTIFIER, entityId,
                MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
        }


    }


    @Override
    @Transactional
    public void updateModifiedOnColumnForAgentHierachy( long agentId ) throws InvalidInputException
    {
        LOG.debug( "method updateModifiedOnColumnForAgentHierachy() started" );
        if ( agentId <= 0l ) {
            throw new InvalidInputException( "passend agentid is incorrect" );
        }

        User agent = userDao.findById( User.class, agentId );
        if ( agent == null ) {
            throw new InvalidInputException( "No user in db for passed userId" );
        }

        if ( agent.getCompany() == null ) {
            throw new InvalidInputException( "No Company in db for passed userId" );
        }

        long companyId = agent.getCompany().getCompanyId();
        List<Object> branchIdList = new ArrayList<Object>();
        List<Object> regionIdList = new ArrayList<Object>();
        List<UserProfile> userProfiles = agent.getUserProfiles();

        for ( UserProfile profile : userProfiles ) {
            if ( profile.getBranchId() > 0l && !branchIdList.contains( profile.getBranchId() ) ) {
                branchIdList.add( profile.getBranchId() );
            }

            if ( profile.getRegionId() > 0l && !regionIdList.contains( profile.getRegionId() ) ) {
                regionIdList.add( profile.getRegionId() );
            }
        }

        if ( branchIdList != null ) {
            for ( Object branchId : branchIdList ) {
                Long longId = ( (Number) branchId ).longValue();
                Branch branch = userManagementService.getBranchById( longId );
                if ( branch != null ) {
                    Region region = branch.getRegion();
                    if ( region != null ) {
                        long regionId = region.getRegionId();
                        if ( regionId > 0l && !regionIdList.contains( regionId ) ) {
                            regionIdList.add( regionId );
                        }
                    }
                }
            }
        }


        if ( companyId > 0l ) {
            organizationUnitSettingsDao.updateKeyOrganizationUnitSettingsByCriteria(
                MongoOrganizationUnitSettingDaoImpl.KEY_MODIFIED_ON, System.currentTimeMillis(),
                MongoOrganizationUnitSettingDaoImpl.KEY_IDENTIFIER, companyId,
                MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
        }

        if ( agentId > 0l ) {
            organizationUnitSettingsDao.updateKeyOrganizationUnitSettingsByCriteria(
                MongoOrganizationUnitSettingDaoImpl.KEY_MODIFIED_ON, System.currentTimeMillis(),
                MongoOrganizationUnitSettingDaoImpl.KEY_IDENTIFIER, agentId,
                MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
        }

        if ( !branchIdList.isEmpty() ) {
            organizationUnitSettingsDao.updateKeyOrganizationUnitSettingsByInCriteria(
                MongoOrganizationUnitSettingDaoImpl.KEY_MODIFIED_ON, System.currentTimeMillis(),
                MongoOrganizationUnitSettingDaoImpl.KEY_IDENTIFIER, branchIdList,
                MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );
        }
        if ( !regionIdList.isEmpty() ) {
            organizationUnitSettingsDao.updateKeyOrganizationUnitSettingsByInCriteria(
                MongoOrganizationUnitSettingDaoImpl.KEY_MODIFIED_ON, System.currentTimeMillis(),
                MongoOrganizationUnitSettingDaoImpl.KEY_IDENTIFIER, regionIdList,
                MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
        }
        LOG.debug( "method updateModifiedOnColumnForAgentHierachy() finished" );

    }


    public void updateSurveyAsUnderResolution( String surveyId )
    {

        LOG.info( "Method updateSurveyAsUnderResolution() to mark a survey as under resolution started, started" );
        surveyDetailsDao.updateSurveyAsUnderResolution( surveyId );
        LOG.info( "Method updateSurveyAsUnderResolution() to mark a survey as under resolution started, ended" );
    }


    @Override
    public SocialMediaPostDetails getSocialMediaPostDetailsBySurvey( SurveyDetails surveyDetails,
        OrganizationUnitSettings companyUnitSettings, List<OrganizationUnitSettings> regionUnitSettings,
        List<OrganizationUnitSettings> branchUnitSettings )
    {
        SocialMediaPostDetails socialMediaPostDetails = null;
        AgentMediaPostDetails agentMediaPostDetails = null;
        CompanyMediaPostDetails companyMediaPostDetails = null;
        List<BranchMediaPostDetails> branchMediaPostDetailsList = null;
        List<RegionMediaPostDetails> regionMediaPostDetailsList = null;
        if ( surveyDetails == null ) {
            throw new FatalException( "Survey cannot be null  " );
        } else {
            socialMediaPostDetails = surveyDetails.getSocialMediaPostDetails();
            if ( socialMediaPostDetails == null ) {
                socialMediaPostDetails = new SocialMediaPostDetails();
            }
            agentMediaPostDetails = socialMediaPostDetails.getAgentMediaPostDetails();
            if ( agentMediaPostDetails == null ) {
                agentMediaPostDetails = new AgentMediaPostDetails();
                agentMediaPostDetails.setAgentId( surveyDetails.getAgentId() );

            }
            companyMediaPostDetails = socialMediaPostDetails.getCompanyMediaPostDetails();
            if ( companyMediaPostDetails == null ) {
                companyMediaPostDetails = new CompanyMediaPostDetails();
                companyMediaPostDetails.setCompanyId( surveyDetails.getCompanyId() );

            }
            regionMediaPostDetailsList = socialMediaPostDetails.getRegionMediaPostDetailsList();
            if ( regionMediaPostDetailsList == null ) {
                regionMediaPostDetailsList = new ArrayList<RegionMediaPostDetails>();
            }
            for ( OrganizationUnitSettings setting : regionUnitSettings ) {
                boolean found = false;
                for ( int i = 0; i < regionMediaPostDetailsList.size(); i++ ) {

                    RegionMediaPostDetails regionMediaPostDetails = regionMediaPostDetailsList.get( i );
                    if ( regionMediaPostDetails.getRegionId() == setting.getIden() ) {
                        found = true;
                        break;
                    }
                }
                if ( !found ) {
                    RegionMediaPostDetails regionMediaPostDetails = new RegionMediaPostDetails();
                    regionMediaPostDetails.setRegionId( setting.getIden() );
                    regionMediaPostDetailsList.add( regionMediaPostDetails );
                }
            }
            branchMediaPostDetailsList = socialMediaPostDetails.getBranchMediaPostDetailsList();
            if ( branchMediaPostDetailsList == null ) {
                branchMediaPostDetailsList = new ArrayList<BranchMediaPostDetails>();
            }
            for ( OrganizationUnitSettings setting : branchUnitSettings ) {
                boolean found = false;
                BranchMediaPostDetails branchMediaPostDetails = null;
                for ( int i = 0; i < branchMediaPostDetailsList.size(); i++ ) {
                    BranchMediaPostDetails branchMediaPostDetailsObject = branchMediaPostDetailsList.get( i );
                    if ( branchMediaPostDetailsObject.getBranchId() == setting.getIden() ) {
                        found = true;
                        break;
                    }
                }
                if ( !found ) {
                    branchMediaPostDetails = new BranchMediaPostDetails();
                    branchMediaPostDetails.setBranchId( setting.getIden() );

                    LOG.debug( "Adding the region this branch belongs too " );
                    try {
                        OrganizationUnitSettings regionSetting = profileManagementService.getRegionProfileByBranch( setting );
                        boolean regionFound = false;
                        for ( int i = 0; i < regionMediaPostDetailsList.size(); i++ ) {

                            RegionMediaPostDetails regionMediaPostDetails = regionMediaPostDetailsList.get( i );
                            if ( regionMediaPostDetails.getRegionId() == regionSetting.getIden() ) {
                                regionFound = true;
                                break;
                            }
                        }
                        if ( !regionFound ) {
                            RegionMediaPostDetails regionMediaPostDetails = new RegionMediaPostDetails();
                            regionMediaPostDetails.setRegionId( regionSetting.getIden() );
                            regionMediaPostDetailsList.add( regionMediaPostDetails );
                        }
                        branchMediaPostDetails.setRegionId( regionSetting.getIden() );
                    } catch ( ProfileNotFoundException e ) {
                        LOG.error( "Unable to find the profile", e );
                    }

                    branchMediaPostDetailsList.add( branchMediaPostDetails );
                }


            }
            socialMediaPostDetails.setAgentMediaPostDetails( agentMediaPostDetails );
            socialMediaPostDetails.setBranchMediaPostDetailsList( branchMediaPostDetailsList );
            socialMediaPostDetails.setRegionMediaPostDetailsList( regionMediaPostDetailsList );
            socialMediaPostDetails.setCompanyMediaPostDetails( companyMediaPostDetails );

        }
        return socialMediaPostDetails;
    }


    public List<AbusiveSurveyReportWrapper> getSurveysReportedAsAbusive( long companyId, int startIndex, int numOfRows )
    {
        LOG.info( "Method getSurveysReportedAsAbusive() to retrieve surveys marked as abusive for a company, started" );
        List<AbusiveSurveyReportWrapper> abusiveSurveyReports = surveyDetailsDao.getSurveysReporetedAsAbusive( companyId,
            startIndex, numOfRows );
        LOG.info( "Method getSurveysReportedAsAbusive() to retrieve surveys marked as abusive for a company, finished" );
        return abusiveSurveyReports;
    }


    @Override
    public Boolean canPostOnSocialMedia( OrganizationUnitSettings unitSetting, Double rating )
    {
        boolean canPost = false;
        if ( unitSetting != null ) {
            if ( unitSetting.getSurvey_settings() != null ) {
                if ( unitSetting.getSurvey_settings().getAuto_post_score() <= rating ) {
                    canPost = true;
                }
            } else {
                if ( CommonConstants.DEFAULT_AUTOPOST_SCORE <= rating ) {
                    canPost = true;

                }
            }
        } else {
            if ( CommonConstants.DEFAULT_AUTOPOST_SCORE <= rating ) {
                canPost = true;

            }
        }
        return canPost;
    }


    @Transactional
    public List<BulkSurveyDetail> processBulkSurvey( List<BulkSurveyDetail> bulkSurveyDetailList, long companyId )
    {
        List<BulkSurveyDetail> list = new ArrayList<BulkSurveyDetail>();
        LOG.debug( "Inside method processBulkSurvey " );

        for ( BulkSurveyDetail bulkSurveyDetail : bulkSurveyDetailList ) {
            boolean error = false;
            String status = CommonConstants.BULK_SURVEY_VALID;
            String message = "";
            if ( bulkSurveyDetail != null ) {
                LOG.debug( "processing survey for agent " + bulkSurveyDetail.getAgentFirstName() );
                if ( bulkSurveyDetail.getAgentFirstName() == null || bulkSurveyDetail.getAgentFirstName().isEmpty() ) {
                    message = "Invalid Agent Name ";
                    status = CommonConstants.BULK_SURVEY_INVALID;
                    error = true;
                } else if ( bulkSurveyDetail.getAgentEmailId() == null || bulkSurveyDetail.getAgentEmailId().isEmpty() ) {
                    message = "Agent Email Address Not Found ";
                    status = CommonConstants.BULK_SURVEY_INVALID;
                    error = true;
                } else if ( bulkSurveyDetail.getCustomerFirstName() == null
                    || bulkSurveyDetail.getCustomerFirstName().isEmpty() ) {
                    message = "Customer name Not Found ";
                    status = CommonConstants.BULK_SURVEY_INVALID;
                    error = true;
                } else if ( bulkSurveyDetail.getCustomerEmailId() == null || bulkSurveyDetail.getCustomerEmailId().isEmpty() ) {
                    message = "Customer Email Address Not Found ";
                    status = CommonConstants.BULK_SURVEY_INVALID;
                    error = true;
                }
                User user = null;
                if ( !error ) {
                    String agentEmailId = bulkSurveyDetail.getAgentEmailId();
                    try {
                        user = userManagementService.getUserByEmailAndCompany( companyId, agentEmailId );
                    } catch ( InvalidInputException e ) {
                        message = "Agent does not belong to this Company " + companyId;
                        status = CommonConstants.BULK_SURVEY_INVALID;
                        error = true;
                    } catch ( NoRecordsFetchedException e ) {
                        message = "Agent does not belong to this Company " + companyId;
                        status = CommonConstants.BULK_SURVEY_INVALID;
                        error = true;
                    }
                    if ( user == null ) {
                        message = "Agent does not belong to this Company ";
                        status = CommonConstants.BULK_SURVEY_INVALID;
                        error = true;
                    }
                }
                if ( !error ) {
                    try {
                        initiateSurveyRequest( user.getUserId(), bulkSurveyDetail.getCustomerEmailId(),
                            bulkSurveyDetail.getCustomerFirstName(), bulkSurveyDetail.getCustomerLastName(),
                            CommonConstants.SURVEY_SOURCE_BULK_UPLOAD );
                        status = CommonConstants.BULK_SURVEY_VALID;
                    } catch ( DuplicateSurveyRequestException | InvalidInputException | SelfSurveyInitiationException
                        | SolrException | NoRecordsFetchedException | UndeliveredEmailException | ProfileNotFoundException e ) {
                        status = CommonConstants.BULK_SURVEY_INVALID;
                        message = e.getMessage();
                    }
                }
                bulkSurveyDetail.setStatus( status );
                if ( status.equalsIgnoreCase( CommonConstants.BULK_SURVEY_INVALID ) ) {
                    bulkSurveyDetail.setReason( message );
                }
                list.add( bulkSurveyDetail );

            }

        }
        return list;
    }


    @Override
    public List<SurveyDetails> getSurveysUnderResolution( long companyId, int startIndex, int numOfRows )
    {
        LOG.info( "Method getSurveysUnderResolution() to retrieve surveys marked as under resolution for a company, started" );
        List<SurveyDetails> surveyDetails = surveyDetailsDao.getSurveysUnderResolution( companyId, startIndex, numOfRows );
        LOG.info( "Method getSurveysUnderResolution() to retrieve surveys marked as under resolution for a company, finished" );
        return surveyDetails;
    }


    @Override
    public SurveyDetails getSurveyDetails( String surveyMongoId )
    {
        LOG.info( "Method getSurveyDetails() to return survey details by surveyMongoId started." );
        SurveyDetails surveyDetails;
        surveyDetails = surveyDetailsDao.getSurveyBySurveyMongoId( surveyMongoId );
        LOG.info( "Method getSurveyDetails() to return survey details by surveyMongoId finished." );
        return surveyDetails;
    }


    @Override
    public void updateSurveyAsUnAbusive( String surveyId )
    {
        LOG.info( "Method unMarkAbusiveSurvey() started" );
        surveyDetailsDao.updateSurveyAsUnAbusive( surveyId );
        LOG.info( "Method unMarkAbusiveSurvey() finished" );
    }


    /**
     * Returns array of swear words. Its used only for testing. Not for development(non-Javadoc)
     * @see com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler#getSwearList()
     */
    @Override
    public String[] getSwearList()
    {
        LOG.debug( "Returning swear list" );
        String[] swearList = new Gson().fromJson( SWEAR_WORDS, String[].class );
        return swearList;
    }


    @Override
    public double getFormattedSurveyScore( double surveyScore )
    {
        DecimalFormat ratingFormat = CommonConstants.SOCIAL_RANKING_FORMAT;
        ratingFormat.setMinimumFractionDigits( 1 );
        ratingFormat.setMaximumFractionDigits( 1 );
        try {
            //get formatted survey score using rating format
            surveyScore = Double.parseDouble( ratingFormat.format( surveyScore ) );
        } catch ( NumberFormatException e ) {
            LOG.error( "Exception caught while formatting survey ratting using rattingformat" );
        }
        return surveyScore;
    }


    /**
     * Method to move surveys belonging to one user to another user
     * @param fromUserId
     * @param toUserId
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     * @throws SolrException
     * */
    @Override
    @Transactional
    public void moveSurveysToAnotherUser( long fromUserId, long toUserId ) throws InvalidInputException,
        NoRecordsFetchedException, SolrException
    {
        if ( fromUserId <= 0 ) {
            LOG.error( "Invalid from user id passed as parameter" );
            throw new InvalidInputException( "Invalid from user id passed as parameter" );
        }
        if ( toUserId <= 0 ) {
            LOG.error( "Invalid to user id passed as parameter" );
            throw new InvalidInputException( "Invalid to user id passed as parameter" );
        }
        LOG.info( "Method to move surveys from one user to another user,moveSurveysToAnotherUser() call started" );
        List<Long> userIds = new ArrayList<Long>();
        userIds.add( fromUserId );
        userIds.add( toUserId );
        LOG.info( "Fetching from and to user information" );
        List<User> userList = null;
        try {
            userList = userDao.getUsersForUserIds( userIds );
        } catch ( Exception e ) {
            throw new NoRecordsFetchedException( "Error occurred while fetching information for users" );
        }
        if ( userList == null || userList.size() != 2 )
            throw new NoRecordsFetchedException( "Either from user or to user or both could not be found" );
        LOG.info( "Fetched from and to user information" );
        User fromUser = userList.get( 0 ).getUserId() == fromUserId ? userList.get( 0 ) : userList.get( 1 );
        User toUser = userList.get( 1 ).getUserId() == toUserId ? userList.get( 1 ) : userList.get( 0 );

        // check if both user belong to same company
        //UserProfile fromUserProfile = getUserProfileWhereAgentForUser( fromUser );
        UserProfile toUserProfile = getUserProfileWhereAgentForUser( toUser );
        LOG.info( "Validating whether both from and to user are agents" );
        // check if to user id is an agent
        if ( toUserProfile == null )
            throw new NoRecordsFetchedException( "To user id : " + toUser.getUserId() + " is not an agent" );
        LOG.info( "Validating whether both from and to user belong to same company" );
        if ( fromUser.getCompany().getCompanyId() != toUser.getCompany().getCompanyId() )
            throw new UnsupportedOperationException( "From user : " + fromUser.getUserId() + " and to user id : "
                + toUser.getUserId() + " do not belong to same company" );

        // replace agent id Survey Pre Initiation
        LOG.info( "Moving all incomplete surveys of user : " + fromUserId + " to user : " + toUserId );
        surveyPreInitiationDao.updateAgentInfoOfPreInitiatedSurveys( fromUserId, toUser );
        // replace agent id in Surveys
        LOG.info( "Moving all started & completed surveys of user : " + fromUserId + " to user : " + toUserId );
        surveyDetailsDao.updateAgentInfoInSurveys( fromUserId, toUser, toUserProfile );
        // update to user solr review count
        LOG.info( "Updating review count of user : " + toUserId );
        solrSearchService.updateReviewCountOfUserInSolr( toUser );
        if ( fromUser.getStatus() == CommonConstants.STATUS_ACTIVE ) {
            // update from user solr review count
            LOG.info( "Updating review count of user : " + fromUserId );
            solrSearchService.updateReviewCountOfUserInSolr( fromUser );
        }
        LOG.info( "Method to move surveys from one user to another user,moveSurveysToAnotherUser() call ended" );
    }


    UserProfile getUserProfileWhereAgentForUser( User user )
    {
        LOG.info( "Method to find user profile where user is agent, getUserProfileWhereAgentForUser started" );
        UserProfile agentUserProfile = null;
        if ( user.getUserProfiles() != null ) {
            for ( UserProfile userProfile : user.getUserProfiles() ) {
                if ( userProfile.getStatus() == CommonConstants.STATUS_ACTIVE
                    && userProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) {
                    agentUserProfile = userProfile;
                    if ( userProfile.getIsPrimary() == CommonConstants.YES ) {
                        break;
                    }
                }
            }
        }
        LOG.info( "Method to find user profile where user is agent, getUserProfileWhereAgentForUser ended" );
        return agentUserProfile;
    }


    @Override
    public void updateZillowSummaryInExistingSurveyDetails( SurveyDetails surveyDetails )
    {
        surveyDetailsDao.updateZillowSummaryInExistingSurveyDetails( surveyDetails );
    }
}
// JIRA SS-119 by RM-05:EOC