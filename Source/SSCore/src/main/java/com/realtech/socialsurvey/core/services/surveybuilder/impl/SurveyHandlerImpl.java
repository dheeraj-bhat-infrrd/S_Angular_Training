package com.realtech.socialsurvey.core.services.surveybuilder.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import com.realtech.socialsurvey.core.entities.BranchSettings;
import com.realtech.socialsurvey.core.entities.BulkSurveyDetail;
import com.realtech.socialsurvey.core.entities.CRMInfo;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.CompanyIgnoredEmailMapping;
import com.realtech.socialsurvey.core.entities.CompanyMediaPostDetails;
import com.realtech.socialsurvey.core.entities.MailContent;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.RegionMediaPostDetails;
import com.realtech.socialsurvey.core.entities.SocialMediaPostDetails;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyImportVO;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.SurveyResponse;
import com.realtech.socialsurvey.core.entities.SurveySettings;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.enums.OrganizationUnit;
import com.realtech.socialsurvey.core.enums.SettingsForApplication;
import com.realtech.socialsurvey.core.enums.SurveyErrorCode;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
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
import com.realtech.socialsurvey.core.services.social.SocialManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.core.utils.EmailFormatHelper;
import com.realtech.socialsurvey.core.utils.FileOperations;
import com.realtech.socialsurvey.core.vo.SurveysAndReviewsVO;


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
    private SurveyPreInitiationDao surveyPreInitiationDao;

    @Autowired
    private ProfileManagementService profileManagementService;

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

    @Value ( "${MAX_AUTO_SURVEY_REMINDERS}")
    private int maxAutoSurveyReminders;

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

    @Value ( "${MAX_SURVEY_REMINDER_INTERVAL}")
    private int maxSurveyReminderInterval;

    @Value ( "${DEFAULT_SURVEY_RETAKE_INTERVAL}")
    private int defaultSurveyRetakeInterval;


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

    @Value ( "${PARAM_ORDER_SURVEY_COPLETION_MAIL_CUSTOM}")
    String paramOrderSurveyCompletionMailCustom;

    @Value ( "${PARAM_ORDER_SOCIAL_POST_REMINDER}")
    String paramOrderSocialPostReminder;

    @Value ( "${PARAM_ORDER_INCOMPLETE_SURVEY_REMINDER}")
    String paramOrderIncompleteSurveyReminder;

    @Value ( "${PARAM_ORDER_SURVEY_COMPLETION_UNPLEASANT_MAIL}")
    String paramOrderSurveyCompletionUnpleasantMail;

    @Value ( "${3RD_PARTY_IMPORT_PATH}")
    private String thirdPartySurveyImportPath;

    @Value ( "${MASK_EMAIL_ADDRESS}")
    private String maskEmail;

    @Value ( "${APPLICATION_ADMIN_EMAIL}")
    private String applicationAdminEmail;

    @Value ( "${APPLICATION_ADMIN_NAME}")
    private String applicationAdminName;

    @Autowired
    private Utils utils;

    @Autowired
    private UrlService urlService;

    @Autowired
    private SocialManagementService socialManagementService;

    @Autowired
    private GenericDao<CompanyIgnoredEmailMapping, Long> companyIgnoredEmailMappingDao;

    private static final int USER_EMAIL_ID_INDEX = 3;
    private static final int SURVEY_SOURCE_ID_INDEX = 2;
    private static final int CUSTOMER_FIRSTNAME_INDEX = 4;
    private static final int CUSTOMER_LASTNAME_INDEX = 5;
    private static final int CUSTOMER_EMAIL_INDEX = 8;
    private static final int SURVEY_COMPLETION_INDEX = 0;
    private static final int SCORE_INDEX = 6;
    private static final int COMMENT_INDEX = 7;
    private static final int CITY_INDEX = 9;
    private static final int STATE_INDEX = 10;
    private static final String DEFAULT_CUSTOMER_EMAIL_ID_FOR_3RD_PARTY = "none@socialsurvey.com";


    /**
     * Method to store question and answer format into mongo.
     * 
     * @param agentId
     * @throws InvalidInputException
     * @throws Exception
     */
    @Override
    @Transactional
    public SurveyDetails storeInitialSurveyDetails( User user, SurveyPreInitiation surveyPreInitiation, String baseUrl,
        boolean isOldRecord, boolean retakeSurvey ) throws SolrException, NoRecordsFetchedException, InvalidInputException
    {
        LOG.debug( "Method to store initial details of survey, storeInitialSurveyAnswers() started." );
        String agentName;
        long branchId = 0;
        long companyId = 0;
        long regionId = 0;

        companyId = user.getCompany().getCompanyId();
        agentName = user.getFirstName() + " " + user.getLastName();
        for ( UserProfile userProfile : user.getUserProfiles() ) {
            if ( userProfile.getAgentId() == surveyPreInitiation.getAgentId() ) {
                branchId = userProfile.getBranchId();
                regionId = userProfile.getRegionId();
            }
        }

        SurveyDetails surveyDetails = new SurveyDetails();
        surveyDetails.setAgentId( surveyPreInitiation.getAgentId() );
        surveyDetails.setAgentName( agentName );
        surveyDetails.setBranchId( branchId );
        surveyDetails.setCustomerFirstName( surveyPreInitiation.getCustomerFirstName() );
        String lastName = surveyPreInitiation.getCustomerLastName();
        if ( lastName != null && !lastName.isEmpty() && !lastName.equalsIgnoreCase( "null" ) )
            surveyDetails.setCustomerLastName( lastName );
        surveyDetails.setCompanyId( companyId );
        surveyDetails.setCustomerEmail( surveyPreInitiation.getCustomerEmailId() );
        surveyDetails.setRegionId( regionId );
        surveyDetails.setStage( CommonConstants.INITIAL_INDEX );
        surveyDetails.setReminderCount( surveyPreInitiation.getReminderCounts() );
        surveyDetails.setModifiedOn( new Date( System.currentTimeMillis() ) );
        surveyDetails.setCreatedOn( new Date( System.currentTimeMillis() ) );
        surveyDetails.setSurveyResponse( new ArrayList<SurveyResponse>() );
        surveyDetails.setCustRelationWithAgent( surveyPreInitiation.getCustomerInteractionDetails() );

        String surveyUrl = baseUrl;
        surveyDetails.setUrl( surveyUrl );
        surveyDetails.setEditable( true );
        surveyDetails.setSource( surveyPreInitiation.getSurveySource() );
        surveyDetails.setSourceId( surveyPreInitiation.getSurveySourceId() );
        surveyDetails.setShowSurveyOnUI( true );

        surveyDetails.setRetakeSurvey( retakeSurvey );
        surveyDetails.setSurveyPreIntitiationId( surveyPreInitiation.getSurveyPreIntitiationId() );
        surveyDetails.setSurveyTransactionDate( surveyPreInitiation.getEngagementClosedTime() );
        surveyDetails.setState( surveyPreInitiation.getState() );
        surveyDetails.setCity( surveyPreInitiation.getCity() );

        SurveyDetails survey = null;
        //if survey request is old get survey by agent id and customer email
        if ( isOldRecord ) {
            survey = surveyDetailsDao.getSurveyByAgentIdAndCustomerEmail( surveyPreInitiation.getAgentId(),
                surveyPreInitiation.getCustomerEmailId(), surveyPreInitiation.getCustomerLastName(), lastName );
        } else {
            survey = surveyDetailsDao.getSurveyBySurveyPreIntitiationId( surveyPreInitiation.getSurveyPreIntitiationId() );
        }

        if ( survey == null ) {
            surveyDetailsDao.insertSurveyDetails( surveyDetails );
            // LOG.debug( "Updating modified on column in agent hierarchy fro agent " );
            // updateModifiedOnColumnForAgentHierachy( agentId );
            LOG.debug( "Method to store initial details of survey, storeInitialSurveyAnswers() finished." );
            return surveyDetails;
        } else {
            //update survey PreIntitiation Id for survey
            survey.setSurveyPreIntitiationId( surveyPreInitiation.getSurveyPreIntitiationId() );
            survey.setRetakeSurvey( retakeSurvey );
            surveyDetailsDao.updateSurveyDetailsBySurveyId( survey );
            LOG.debug( "Method storeInitialSurveyAnswers() finished." );
            return survey;
        }
    }


    @Override
    @Transactional
    public void insertSurveyDetails( SurveyDetails surveyDetails )
    {
        surveyDetailsDao.insertSurveyDetails( surveyDetails );
        if ( surveyDetails.getAgentId() > 0l ) {
            LOG.debug( "Updating modified on column in aagent hierarchy fro agent " );
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
    public void updateCustomerAnswersInSurvey( String surveyId, String question, String questionType, String answer, int stage,
        boolean isUserRankingQuestion )
    {
        LOG.debug(
            "Method to update answers provided by customer in SURVEY_DETAILS, updateCustomerAnswersInSurvey() started." );
        SurveyResponse surveyResponse = new SurveyResponse();
        surveyResponse.setAnswer( answer );
        surveyResponse.setQuestion( question );
        surveyResponse.setQuestionType( questionType );
        surveyResponse.setIsUserRankingQuestion( isUserRankingQuestion );
        surveyDetailsDao.updateCustomerResponse( surveyId, surveyResponse, stage );
        LOG.debug(
            "Method to update answers provided by customer in SURVEY_DETAILS, updateCustomerAnswersInSurvey() finished." );
    }


    /*
     * Method to update customer review and final score on the basis of rating questions in
     * SURVEY_DETAILS.
     */
    @Override
    public void updateGatewayQuestionResponseAndScore( String surveyId, String mood, String review, boolean isAbusive,
        String agreedToShare )
    {
        LOG.debug(
            "Method to update customer review and final score on the basis of rating questions in SURVEY_DETAILS, updateCustomerAnswersInSurvey() started." );
        surveyDetailsDao.updateFinalScore( surveyId );
        surveyDetailsDao.updateGatewayAnswer( surveyId, mood, review, isAbusive, agreedToShare );
        LOG.debug(
            "Method to update customer review and final score on the basis of rating questions in SURVEY_DETAILS, updateCustomerAnswersInSurvey() finished." );
    }


    @Override
    public SurveyDetails getSurveyDetails( long agentId, String customerEmail, String firstName, String lastName )
    {
        LOG.debug( "Method getSurveyDetails() to return survey details by agent id and customer email started." );
        SurveyDetails surveyDetails;
        surveyDetails = surveyDetailsDao.getSurveyByAgentIdAndCustomerEmail( agentId, customerEmail, firstName, lastName );
        LOG.debug( "Method getSurveyDetails() to return survey details by agent id and customer email finished." );
        return surveyDetails;
    }


    @Override
    public List<SurveyDetails> getSurveyDetailsByAgentAndCompany( long companyId )
    {
        LOG.debug( "Method getSurveyDetails() to return survey details by agent id and customer email started." );
        List<SurveyDetails> surveys = surveyDetailsDao.getSurveyDetailsByAgentAndCompany( companyId );
        LOG.debug( "Method getSurveyDetails() to return survey details by agent id and customer email finished." );
        return surveys;
    }


    @Override
    public void updateSurveyAsAbusive( String surveymongoId, String reporterEmail, String reporterName )
    {
        LOG.debug( "Method updateSurveyAsAbusive() to mark the survey as abusive, started" );
        surveyDetailsDao.updateSurveyAsAbusive( surveymongoId, reporterEmail, reporterName );
        LOG.debug( "Method updateSurveyAsAbusive() to mark the survey as abusive, finished" );
    }


    @Override
    @Transactional
    public SurveyPreInitiation saveSurveyPreInitiationObject( SurveyPreInitiation surveyPreInitiation )
        throws InvalidInputException
    {
        if ( surveyPreInitiation == null ) {
            LOG.debug( "SurveyPreInitiation object passed null for insert" );
            throw new InvalidInputException( "SurveyPreInitiation object passed null for insert" );
        }
        LOG.debug( "Inside method saveSurveyPreInitiationObject " );
        surveyPreInitiation = surveyPreInitiationDao.save( surveyPreInitiation );
        return surveyPreInitiation;
    }


    /*
     * Method to update a survey as clicked when user triggers the survey and page of the first
     * question starts loading.
     */
    public void updateSurveyAsClicked( String surveyMongoId )
    {
        LOG.debug( "Method updateSurveyAsClicked() to mark the survey as clicked, started" );
        surveyDetailsDao.updateSurveyAsClicked( surveyMongoId );
        LOG.debug( "Method updateSurveyAsClicked() to mark the survey as clicked, finished" );
    }


    /*
     * Method to increase reminder count by 1. This method is called every time a reminder mail is
     * sent to the customer.
     */
    @Override
    @Transactional
    public void updateReminderCount( long surveyPreInitiationId, boolean reminder )
    {
        LOG.debug( "Method to increase reminder count by 1, updateReminderCount() started." );

        SurveyPreInitiation survey = surveyPreInitiationDao.findById( SurveyPreInitiation.class, surveyPreInitiationId );
        if ( survey != null ) {
            survey.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );

            survey.setLastReminderTime( new Timestamp( System.currentTimeMillis() ) );
            if ( reminder ) {
                survey.setReminderCounts( survey.getReminderCounts() + 1 );
            }
            surveyPreInitiationDao.merge( survey );
        }
        LOG.debug( "Method to increase reminder count by 1, updateReminderCount() finished." );
    }


    @Override
    @Transactional
    public SurveyDetails getSurveyDetailsBySourceIdAndMongoCollection( String surveySourceId, long iden, String collectionName )
    {
        LOG.debug( "Inside method getSurveyDetailsBySourceId" );
        SurveyDetails surveyDetails = null;
        if ( surveySourceId != null ) {
            surveyDetails = surveyDetailsDao.getSurveyBySourceSourceIdAndMongoCollection( surveySourceId, iden,
                collectionName );
        }
        return surveyDetails;
    }


    @Override
    @Transactional
    public void markSurveyAsSent( SurveyPreInitiation surveyPreInitiation )
    {
        LOG.debug( "Method to increase reminder count by 1, updateReminderCount() started." );
        if ( surveyPreInitiation != null ) {
            surveyPreInitiation.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
            surveyPreInitiation.setLastReminderTime( new Timestamp( System.currentTimeMillis() ) );
            surveyPreInitiation.setIsSurveyRequestSent( 1 );
            surveyPreInitiationDao.merge( surveyPreInitiation );
        }
        LOG.debug( "Method to increase reminder count by 1, updateReminderCount() finished." );
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
    public Map<String, Object> getReminderInformationForCompany( long companyId )
    {
        LOG.debug( "Inside method getReminderInformationForCompany" );
        Map<String, Object> map = new HashMap<String, Object>();
        int reminderInterval = 0;
        int maxReminders = 0;
        boolean isReminderDisabled = false;

        OrganizationUnitSettings organizationUnitSettings = organizationUnitSettingsDao
            .fetchOrganizationUnitSettingsById( companyId, MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        if ( organizationUnitSettings != null ) {
            SurveySettings surveySettings = organizationUnitSettings.getSurvey_settings();
            if ( surveySettings != null ) {
                //set reminder interval
                if ( surveySettings.getSurvey_reminder_interval_in_days() > 0 ) {
                    reminderInterval = surveySettings.getSurvey_reminder_interval_in_days();
                }
                //set is reminder disabled
                isReminderDisabled = surveySettings.getIsReminderDisabled();
            }
        }

        //set max reminder to the default value for auto reminder.
        maxReminders = maxAutoSurveyReminders;

        if ( reminderInterval == 0 ) {
            LOG.debug( "No Reminder interval found for company " + companyId + " hence setting default value " );
            reminderInterval = surveyReminderInterval;
        }

        map.put( CommonConstants.IS_SURVEY_REMINDER_DISABLED, isReminderDisabled );
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
    public List<SurveyPreInitiation> getIncompleteSurveyForReminderEmail( Company company, Date minLastReminderDate,
        Date maxLastReminderDate, int maxReminderCount )
    {
        LOG.debug( "method getIncompleteSurveyForReminderEmail started." );

        List<SurveyPreInitiation> incompleteSurveyCustomers = new ArrayList<>();

        Criterion companyCriteria = Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, company.getCompanyId() );
        Criterion statusCriteria = Restrictions.in( CommonConstants.STATUS_COLUMN,
            Arrays.asList( CommonConstants.STATUS_SURVEYPREINITIATION_PROCESSED, CommonConstants.SURVEY_STATUS_INITIATED ) );

        Criterion minLastReminderCriteria = Restrictions.gt( CommonConstants.SURVEY_LAST_REMINDER_TIME, minLastReminderDate );
        Criterion maxLastReminderCriteria = Restrictions.lt( CommonConstants.SURVEY_LAST_REMINDER_TIME, maxLastReminderDate );

        Criterion reminderCountCriteria = Restrictions.lt( CommonConstants.SURVEY_REMINDER_COUNT, maxReminderCount );

        LOG.info( "Criteria to getIncompleteSurveyForReminderEmail is  " + companyCriteria.toString() + " "
            + statusCriteria.toString() + " " + minLastReminderCriteria.toString() + " " + maxLastReminderCriteria );

        incompleteSurveyCustomers = surveyPreInitiationDao.findByCriteria( SurveyPreInitiation.class, companyCriteria,
            statusCriteria, minLastReminderCriteria, maxLastReminderCriteria, reminderCountCriteria );
        LOG.debug( "method getIncompleteSurveyForReminderEmail finished." );
        return incompleteSurveyCustomers;
    }


    @Override
    @Transactional
    public List<SurveyPreInitiation> getSurveyListToSendInvitationMail( Company company, Date epochDate )
    {

        LOG.debug( "method getSurveyListToSendInvitationMail started." );

        List<SurveyPreInitiation> incompleteSurveyCustomers = new ArrayList<>();

        Criterion companyCriteria = Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, company.getCompanyId() );
        Criterion statusCriteria = Restrictions.in( CommonConstants.STATUS_COLUMN,
            Arrays.asList( CommonConstants.STATUS_SURVEYPREINITIATION_PROCESSED, CommonConstants.SURVEY_STATUS_INITIATED ) );

        Criterion lastReminderCriteria = Restrictions.le( CommonConstants.SURVEY_LAST_REMINDER_TIME, epochDate );

        LOG.info( "Criteria to getSurveyListToSendInvitationMail is  " + companyCriteria.toString() + " "
            + statusCriteria.toString() + " " + lastReminderCriteria.toString() );

        incompleteSurveyCustomers = surveyPreInitiationDao.findByCriteria( SurveyPreInitiation.class, companyCriteria,
            statusCriteria, lastReminderCriteria );
        LOG.debug( "method getSurveyListToSendInvitationMail finished." );
        return incompleteSurveyCustomers;
    }


    @Override
    public void updateReminderCount( List<Long> agents, List<String> customers )
    {
        LOG.debug( "Method to increase reminder count by 1, updateReminderCount() started." );
        surveyDetailsDao.updateReminderCount( agents, customers );
        LOG.debug( "Method to increase reminder count by 1, updateReminderCount() finished." );
    }


    @Override
    public void updateReminderCountForSocialPosts( Long agentId, String customerEmail )
    {
        LOG.debug( "Method to increase reminder count by 1, updateReminderCountForSocialPosts() started." );
        surveyDetailsDao.updateReminderCountForSocialPost( agentId, customerEmail );
        LOG.debug( "Method to increase reminder count by 1, updateReminderCountForSocialPosts() finished." );
    }


    /*
     * Method to get surveys
     */
    @Override
    public List<SurveyDetails> getIncompleteSocialPostSurveys( long companyId )
    {
        LOG.debug( "started." );
        int reminderInterval = 0;
        int maxReminders = 0;
        List<SurveyDetails> incompleteSocialPostCustomers = new ArrayList<>();
        OrganizationUnitSettings organizationUnitSettings = organizationUnitSettingsDao
            .fetchOrganizationUnitSettingsById( companyId, MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
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
        LOG.debug( "finished." );
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
    public void increaseSurveyCountForAgent( long agentId )
        throws SolrException, NoRecordsFetchedException, InvalidInputException
    {
        LOG.debug( "Method to increase survey count for agent started." );
        organizationUnitSettingsDao.updateCompletedSurveyCountForAgent( agentId, 1 );
        solrSearchService.updateCompletedSurveyCountForUserInSolr( agentId, 1 );
        LOG.debug( "Method to increase survey count for agent finished." );
    }


    @Override
    public void decreaseSurveyCountForAgent( long agentId )
        throws SolrException, NoRecordsFetchedException, InvalidInputException
    {
        LOG.debug( "Method to decrease survey count for agent started." );
        organizationUnitSettingsDao.updateCompletedSurveyCountForAgent( agentId, -1 );
        solrSearchService.updateCompletedSurveyCountForUserInSolr( agentId, -1 );
        LOG.debug( "Method to decrease survey count for agent finished." );
    }


    @Override
    public void changeStatusOfSurvey( String surveyId, boolean editable )
    {
        LOG.debug( "Method to update status of survey in SurveyDetails collection, changeStatusOfSurvey() started." );
        surveyDetailsDao.changeStatusOfSurvey( surveyId, editable );
        LOG.debug( "Method to update status of survey in SurveyDetails collection, changeStatusOfSurvey() finished." );
    }


    /*
     * Method to store initial details of customer to initiate survey and send a mail with link of
     * survey.
     */
    @Override
    @Transactional
    public void storeSPIandSendSurveyInvitationMail( String custFirstName, String custLastName, String custEmail,
        String custRelationWithAgent, User user, boolean isAgent, String source ) throws InvalidInputException, SolrException,
        NoRecordsFetchedException, UndeliveredEmailException, ProfileNotFoundException
    {
        LOG.debug( "Method storeSPIandSendSurveyInvitationMail() called." );
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

        SurveyPreInitiation surveyPreInitiation = preInitiateSurvey( user, custEmail, custFirstName, custLastName, 0,
            custRelationWithAgent, source );

        //prepare and send email
        prepareAndSendInvitationMail( surveyPreInitiation );

        LOG.debug( "Method storeSPIandSendSurveyInvitationMail() finished ." );
    }


    @Override
    public void sendSurveyReminderEmail( SurveyPreInitiation survey ) throws InvalidInputException, ProfileNotFoundException
    {
        // Send email to complete survey to each customer.
        OrganizationUnitSettings companySettings = null;
        String agentName = "";
        User user = null;
        Map<String, Long> hierarchyMap = null;
        Map<SettingsForApplication, OrganizationUnit> map = null;
        String logoUrl = null;
        user = userManagementService.getUserByUserId( survey.getAgentId() );

        if ( user != null ) {
            agentName = user.getFirstName();
            if ( user.getLastName() != null && !user.getLastName().isEmpty() ) {
                agentName = user.getFirstName() + " " + user.getLastName();
            }
        }

        String surveyLink = composeLink( survey.getAgentId(), survey.getCustomerEmailId(), survey.getCustomerFirstName(),
            survey.getCustomerLastName(), survey.getSurveyPreIntitiationId(), false );


        // Fetching agent settings.
        AgentSettings agentSettings = userManagementService.getUserSettings( survey.getAgentId() );
        hierarchyMap = profileManagementService.getPrimaryHierarchyByAgentProfile( agentSettings );
        try {
            map = profileManagementService.getPrimaryHierarchyByEntity( CommonConstants.AGENT_ID_COLUMN, user.getUserId() );
            if ( map == null ) {
                LOG.error( "Unable to fetch primary profile for this user " );
                throw new FatalException( "Unable to fetch primary profile this user " + user.getUserId() );
            }
        } catch ( InvalidSettingsStateException e ) {
        }

        long companyId = hierarchyMap.get( CommonConstants.COMPANY_ID_COLUMN );
        long regionId = hierarchyMap.get( CommonConstants.REGION_ID_COLUMN );
        long branchId = hierarchyMap.get( CommonConstants.BRANCH_ID_COLUMN );

        companySettings = organizationManagementService.getCompanySettings( companyId );

        String agentTitle = "";
        if ( agentSettings.getContact_details() != null && agentSettings.getContact_details().getTitle() != null ) {
            agentTitle = agentSettings.getContact_details().getTitle();
        }

        String agentPhone = "";
        if ( agentSettings.getContact_details() != null && agentSettings.getContact_details().getContact_numbers() != null
            && agentSettings.getContact_details().getContact_numbers().getWork() != null ) {
            agentPhone = agentSettings.getContact_details().getContact_numbers().getWork();
        }


        String companyName = user.getCompany().getCompany();
        String currentYear = String.valueOf( Calendar.getInstance().get( Calendar.YEAR ) );
        DateFormat dateFormat = new SimpleDateFormat( "yyyy/MM/dd" );
        String agentSignature = emailFormatHelper.buildAgentSignature( agentName, agentPhone, agentTitle, companyName );
        String fullAddress = "";


        OrganizationUnit organizationUnit = map.get( SettingsForApplication.LOGO );

        if ( organizationUnit == OrganizationUnit.COMPANY ) {
            logoUrl = companySettings.getLogo();
        } else if ( organizationUnit == OrganizationUnit.REGION ) {
            OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( regionId );
            logoUrl = regionSettings.getLogo();
        } else if ( organizationUnit == OrganizationUnit.BRANCH ) {
            OrganizationUnitSettings branchSettings = null;
            try {
                branchSettings = organizationManagementService.getBranchSettingsDefault( branchId );
            } catch ( NoRecordsFetchedException e ) {
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

        LOG.debug( "Initiating URL Service to shorten the url " + surveyLink );
        surveyLink = urlService.shortenUrl( surveyLink );
        LOG.debug( "Finished calling URL Service to shorten the url.Shortened URL : " + surveyLink );

        String mailBody = "";
        String mailSubject = "";
        if ( companySettings != null && companySettings.getMail_content() != null
            && companySettings.getMail_content().getTake_survey_reminder_mail() != null ) {

            MailContent mailContent = companySettings.getMail_content().getTake_survey_reminder_mail();

            mailBody = emailFormatHelper.replaceEmailBodyWithParams( mailContent.getMail_body(), mailContent.getParam_order() );
            mailSubject = CommonConstants.REMINDER_MAIL_SUBJECT;
            if ( mailContent.getMail_subject() != null && !mailContent.getMail_subject().isEmpty() ) {
                mailSubject = mailContent.getMail_subject();
            }


        } else {
            mailSubject = fileOperations.getContentFromFile(
                EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_REMINDER_MAIL_SUBJECT );

            mailBody = fileOperations.getContentFromFile(
                EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_REMINDER_MAIL_BODY );
            mailBody = emailFormatHelper.replaceEmailBodyWithParams( mailBody,
                new ArrayList<String>( Arrays.asList( paramOrderTakeSurveyReminder.split( "," ) ) ) );
        }
        //JIRA SS-473 begin
        String agentDisclaimer = "";
        String agentLicenses = "";
        String companyDisclaimer = "";

        if ( companySettings != null && companySettings.getDisclaimer() != null )
            companyDisclaimer = companySettings.getDisclaimer();

        if ( agentSettings.getDisclaimer() != null )
            agentDisclaimer = agentSettings.getDisclaimer();

        if ( agentSettings.getLicenses() != null && agentSettings.getLicenses().getAuthorized_in() != null ) {
            agentLicenses = StringUtils.join( agentSettings.getLicenses().getAuthorized_in(), ',' );
        }
        //replace legends
        mailSubject = emailFormatHelper.replaceLegends( true, mailSubject, applicationBaseUrl, logoUrl, surveyLink,
            survey.getCustomerFirstName(), survey.getCustomerLastName(), agentName, agentSignature, survey.getCustomerEmailId(),
            user.getEmailId(), companyName, dateFormat.format( new Date() ), currentYear, fullAddress, "",
            user.getProfileName(), companyDisclaimer, agentDisclaimer, agentLicenses );

        mailBody = emailFormatHelper.replaceLegends( false, mailBody, applicationBaseUrl, logoUrl, surveyLink,
            survey.getCustomerFirstName(), survey.getCustomerLastName(), agentName, agentSignature, survey.getCustomerEmailId(),
            user.getEmailId(), companyName, dateFormat.format( new Date() ), currentYear, fullAddress, "",
            user.getProfileName(), companyDisclaimer, agentDisclaimer, agentLicenses );

        //JIRA SS-473 end


        //For Company with hidden agents
        String senderName;
        if ( companySettings.isSendEmailFromCompany() ) {
            senderName = companyName;
        } else {
            senderName = agentName;
        }

        //send mail
        try {
            emailServices.sendSurveyInvitationMail( survey.getCustomerEmailId(), mailSubject, mailBody, user.getEmailId(),
                senderName, user.getUserId() );
        } catch ( InvalidInputException | UndeliveredEmailException e ) {
            LOG.error( "Exception caught while sending mail to " + survey.getCustomerEmailId() + " .Nested exception is ", e );
        }
    }


    /*
     * Method to send email to customer by agent for restarting an already completed survey.
     */
    @Override
    public void sendSurveyRestartMail( String custFirstName, String custLastName, String custEmail,
        String custRelationWithAgent, User user, String surveyUrl )
        throws InvalidInputException, UndeliveredEmailException, ProfileNotFoundException
    {
        LOG.debug( "sendSurveyRestartMail() started." );
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
            LOG.error( "Error: " + e );
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
                LOG.error( "Error: " + e );
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
                LOG.error( "Error: " + e );
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

        OrganizationUnitSettings companySettings = organizationManagementService
            .getCompanySettings( user.getCompany().getCompanyId() );

        //Set the source of survey as it was in the initial survey
        //Get the survey object
        SurveyDetails survey = getSurveyDetails( user.getUserId(), custEmail, custFirstName, custLastName );
        String surveySource = CommonConstants.SURVEY_REQUEST_AGENT;
        if ( survey != null && survey.getSource() != null )
            surveySource = survey.getSource();
        //preinitiate survey
        if ( survey != null ) {
            SurveyPreInitiation surveyPreInitiation = getPreInitiatedSurvey( user.getUserId(), custEmail, custFirstName,
                custLastName );
            if ( surveyPreInitiation != null ) {
                markSurveyAsStarted( surveyPreInitiation );
            } else
                preInitiateSurvey( user, custEmail, custFirstName, custLastName, 0, custRelationWithAgent, surveySource );
        } else {
            //TODO survey will be null, Need to handle this
            /*SurveyPreInitiation surveyPreInitiation = getPreInitiatedSurvey( survey.getSurveyPreIntitiationId() );
            if ( surveyPreInitiation != null ) {
                markSurveyAsStarted( surveyPreInitiation );
            } else
                preInitiateSurvey( user, custEmail, custFirstName, custLastName, 0, custRelationWithAgent, surveySource );*/
        }


        LOG.debug( "Initiating URL Service to shorten the url " + surveyUrl );
        surveyUrl = urlService.shortenUrl( surveyUrl );
        LOG.debug( "Finished calling URL Service to shorten the url.Shortened URL : " + surveyUrl );

        //get mail subject and body
        String mailBody = "";
        String mailSubject = "";
        if ( companySettings != null && companySettings.getMail_content() != null
            && companySettings.getMail_content().getRestart_survey_mail() != null ) {

            MailContent restartSurvey = companySettings.getMail_content().getRestart_survey_mail();
            mailBody = emailFormatHelper.replaceEmailBodyWithParams( restartSurvey.getMail_body(),
                restartSurvey.getParam_order() );

            mailSubject = restartSurvey.getMail_subject();
            if ( mailSubject == null || mailSubject.isEmpty() ) {
                mailSubject = CommonConstants.RESTART_SURVEY_MAIL_SUBJECT;
            }
        } else {
            mailSubject = fileOperations.getContentFromFile(
                EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_RESTART_MAIL_SUBJECT );

            mailBody = fileOperations.getContentFromFile(
                EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_RESTART_MAIL_BODY );
            mailBody = emailFormatHelper.replaceEmailBodyWithParams( mailBody,
                new ArrayList<String>( Arrays.asList( paramOrderIncompleteSurveyReminder.split( "," ) ) ) );
        }
        //JIRA SS-473 begin
        String agentDisclaimer = "";
        String agentLicenses = "";
        String companyDisclaimer = "";

        if ( companySettings != null && companySettings.getDisclaimer() != null )
            companyDisclaimer = companySettings.getDisclaimer();

        if ( agentSettings.getDisclaimer() != null )
            agentDisclaimer = agentSettings.getDisclaimer();

        if ( agentSettings.getLicenses() != null && agentSettings.getLicenses().getAuthorized_in() != null ) {
            agentLicenses = StringUtils.join( agentSettings.getLicenses().getAuthorized_in(), ',' );
        }
        //replace the legends
        mailSubject = emailFormatHelper.replaceLegends( true, mailSubject, applicationBaseUrl, logoUrl, surveyUrl,
            custFirstName, custLastName, agentName, agentSignature, custEmail, user.getEmailId(), companyName,
            dateFormat.format( new Date() ), currentYear, fullAddress, "", user.getProfileName(), companyDisclaimer,
            agentDisclaimer, agentLicenses );

        mailBody = emailFormatHelper.replaceLegends( false, mailBody, applicationBaseUrl, logoUrl, surveyUrl, custFirstName,
            custLastName, agentName, agentSignature, custEmail, user.getEmailId(), companyName, dateFormat.format( new Date() ),
            currentYear, fullAddress, "", user.getProfileName(), companyDisclaimer, agentDisclaimer, agentLicenses );

        //JIRA SS-473 end


        //For Company with hidden agents
        String senderName;
        if ( companySettings.isSendEmailFromCompany() ) {
            senderName = companyName;
        } else {
            senderName = agentName;
        }

        //send mail
        try {
            emailServices.sendSurveyInvitationMail( custEmail, mailSubject, mailBody, user.getEmailId(), senderName,
                user.getUserId() );
        } catch ( InvalidInputException | UndeliveredEmailException e ) {
            LOG.error( "Exception caught while sending mail to " + custEmail + ". Nested exception is ", e );
        }
        LOG.debug( "sendSurveyRestartMail() finished." );
    }


    @Override
    public void sendSurveyCompletionMail( String custEmail, String custFirstName, String custLastName, User user )
        throws InvalidInputException, UndeliveredEmailException, ProfileNotFoundException
    {
        LOG.debug( "sendSurveyCompletionMail() started." );
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
            LOG.error( "Error: " + e );
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
                LOG.error( "Error: " + e );
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

        OrganizationUnitSettings companySettings = organizationManagementService
            .getCompanySettings( user.getCompany().getCompanyId() );


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
            mailSubject = fileOperations.getContentFromFile(
                EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_COMPLETION_MAIL_SUBJECT );
            if ( companySettings != null && companySettings.isHiddenSection() ) {
                mailBody = fileOperations.getContentFromFile(
                    EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_COMPLETION_MAIL_BODY_CUSTOM );
                mailBody = emailFormatHelper.replaceEmailBodyWithParams( mailBody,
                    new ArrayList<String>( Arrays.asList( paramOrderSurveyCompletionMailCustom.split( "," ) ) ) );
            } else {
                mailBody = fileOperations.getContentFromFile(
                    EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_COMPLETION_MAIL_BODY );
                mailBody = emailFormatHelper.replaceEmailBodyWithParams( mailBody,
                    new ArrayList<String>( Arrays.asList( paramOrderSurveyCompletionMail.split( "," ) ) ) );
            }

        }
        //JIRA SS-473 begin
        String agentDisclaimer = "";
        String agentLicenses = "";
        String companyDisclaimer = "";

        if ( companySettings != null && companySettings.getDisclaimer() != null )
            companyDisclaimer = companySettings.getDisclaimer();

        if ( agentSettings.getDisclaimer() != null )
            agentDisclaimer = agentSettings.getDisclaimer();

        if ( agentSettings.getLicenses() != null && agentSettings.getLicenses().getAuthorized_in() != null ) {
            agentLicenses = StringUtils.join( agentSettings.getLicenses().getAuthorized_in(), ',' );
        }
        //replace the legends
        mailSubject = emailFormatHelper.replaceLegends( true, mailSubject, applicationBaseUrl, logoUrl, null, custFirstName,
            custLastName, agentName, agentSignature, custEmail, user.getEmailId(), companyName, dateFormat.format( new Date() ),
            currentYear, fullAddress, "", user.getProfileName(), companyDisclaimer, agentDisclaimer, agentLicenses );

        mailBody = emailFormatHelper.replaceLegends( false, mailBody, applicationBaseUrl, logoUrl, null, custFirstName,
            custLastName, agentName, agentSignature, custEmail, user.getEmailId(), companyName, dateFormat.format( new Date() ),
            currentYear, fullAddress, "", user.getProfileName(), companyDisclaimer, agentDisclaimer, agentLicenses );

        //JIRA SS-473 end


        //For Company with hidden agents
        String senderName;
        if ( companySettings.isSendEmailFromCompany() ) {
            senderName = companyName;
        } else {
            senderName = agentName;
        }

        //send mail
        try {
            emailServices.sendSurveyInvitationMail( custEmail, mailSubject, mailBody, user.getEmailId(), senderName,
                user.getUserId() );
        } catch ( InvalidInputException | UndeliveredEmailException e ) {
            LOG.error( "Exception caught while sending mail to " + custEmail + ". Nested exception is ", e );
        }
        LOG.debug( "sendSurveyCompletionMail() finished." );
    }


    @Override
    public void sendSurveyCompletionUnpleasantMail( String custEmail, String custFirstName, String custLastName, User user )
        throws InvalidInputException, UndeliveredEmailException, ProfileNotFoundException
    {
        LOG.debug( "sendSurveyCompletionUnpleasantMail() started." );
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
            LOG.error( "Error: " + e );
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
                LOG.error( "Error: " + e );
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
                LOG.error( "Error: " + e );
            }
            if ( branchSettings != null ) {
                logoUrl = branchSettings.getLogo();
            }
        } else if ( organizationUnit == OrganizationUnit.AGENT ) {
            logoUrl = agentSettings.getLogo();
        }
        //JIRA SS-1363 end

        OrganizationUnitSettings companySettings = organizationManagementService
            .getCompanySettings( user.getCompany().getCompanyId() );

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
            //JIRA SS-473 begin
            String agentDisclaimer = "";
            String agentLicenses = "";
            String companyDisclaimer = "";

            if ( companySettings != null && companySettings.getDisclaimer() != null )
                companyDisclaimer = companySettings.getDisclaimer();

            if ( agentSettings.getDisclaimer() != null )
                agentDisclaimer = agentSettings.getDisclaimer();

            if ( agentSettings.getLicenses() != null && agentSettings.getLicenses().getAuthorized_in() != null ) {
                agentLicenses = StringUtils.join( agentSettings.getLicenses().getAuthorized_in(), ',' );
            }
            mailBody = emailFormatHelper.replaceLegends( false, mailBody, applicationBaseUrl, logoUrl, null, custFirstName,
                custLastName, agentName, agentSignature, custEmail, user.getEmailId(), companyName,
                dateFormat.format( new Date() ), currentYear, fullAddress, "", user.getProfileName(), companyDisclaimer,
                agentDisclaimer, agentLicenses );

            String mailSubject = surveyCompletionUnpleasant.getMail_subject();
            if ( mailSubject == null || mailSubject.isEmpty() ) {
                mailSubject = CommonConstants.SURVEY_COMPLETION_UNPLEASANT_MAIL_SUBJECT;
            }

            mailSubject = emailFormatHelper.replaceLegends( true, mailSubject, applicationBaseUrl, logoUrl, null, custFirstName,
                custLastName, agentName, agentSignature, custEmail, user.getEmailId(), companyName,
                dateFormat.format( new Date() ), currentYear, fullAddress, "", user.getProfileName(), companyDisclaimer,
                agentDisclaimer, agentLicenses );
            //JIRA SS-473 end

            //For Company with hidden agents
            String senderName;
            if ( companySettings.isSendEmailFromCompany() ) {
                senderName = companyName;
            } else {
                senderName = agentName;
            }
            try {
                emailServices.sendSurveyInvitationMail( custEmail, mailSubject, mailBody, user.getEmailId(), senderName,
                    user.getUserId() );
            } catch ( InvalidInputException | UndeliveredEmailException e ) {
                LOG.error( "Exception caught while sending mail to " + custEmail + ". Nested exception is ", e );
            }
        } else {
            sendSurveyCompletionMail( custEmail, custFirstName, custLastName, user );
        }
        LOG.debug( "sendSurveyCompletionUnpleasantMail() finished." );
    }


    @Override
    public void sendSocialPostReminderMail( String custEmail, String custFirstName, String custLastName, User user,
        String links ) throws InvalidInputException, UndeliveredEmailException, ProfileNotFoundException
    {
        LOG.debug( "sendSocialPostReminderMail() started." );
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
            LOG.error( "Error: " + e );
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
                LOG.error( "Error: " + e );
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
                LOG.error( "Error: " + e );
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

        OrganizationUnitSettings companySettings = organizationManagementService
            .getCompanySettings( user.getCompany().getCompanyId() );

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
            mailSubject = fileOperations.getContentFromFile(
                EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SOCIALPOST_REMINDER_MAIL_SUBJECT );

            mailBody = fileOperations.getContentFromFile(
                EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SOCIALPOST_REMINDER_MAIL_BODY );
            mailBody = emailFormatHelper.replaceEmailBodyWithParams( mailBody,
                new ArrayList<String>( Arrays.asList( paramOrderSocialPostReminder.split( "," ) ) ) );
        }
        //JIRA SS-473 begin
        String agentDisclaimer = "";
        String agentLicenses = "";
        String companyDisclaimer = "";

        if ( companySettings != null && companySettings.getDisclaimer() != null )
            companyDisclaimer = companySettings.getDisclaimer();

        if ( agentSettings.getDisclaimer() != null )
            agentDisclaimer = agentSettings.getDisclaimer();

        if ( agentSettings.getLicenses() != null && agentSettings.getLicenses().getAuthorized_in() != null ) {
            agentLicenses = StringUtils.join( agentSettings.getLicenses().getAuthorized_in(), ',' );
        }
        //replace legends
        mailSubject = emailFormatHelper.replaceLegends( true, mailSubject, applicationBaseUrl, logoUrl, "", custFirstName,
            custLastName, agentName, agentSignature, custEmail, user.getEmailId(), companyName, dateFormat.format( new Date() ),
            currentYear, fullAddress, links, user.getProfileName(), companyDisclaimer, agentDisclaimer, agentLicenses );

        mailBody = emailFormatHelper.replaceLegends( false, mailBody, applicationBaseUrl, logoUrl, "", custFirstName,
            custLastName, agentName, agentSignature, custEmail, user.getEmailId(), companyName, dateFormat.format( new Date() ),
            currentYear, fullAddress, links, user.getProfileName(), companyDisclaimer, agentDisclaimer, agentLicenses );
        //JIRA SS-473 end


        //For Company with hidden agents
        String senderName;
        if ( companySettings.isSendEmailFromCompany() ) {
            senderName = companyName;
        } else {
            senderName = agentName;
        }

        //send mail
        try {
            emailServices.sendSurveyInvitationMail( custEmail, mailSubject, mailBody, user.getEmailId(), senderName,
                user.getUserId() );
        } catch ( InvalidInputException | UndeliveredEmailException e ) {
            LOG.error( "Exception caught while sending mail to " + custEmail + ". Nested exception is ", e );
        }

        LOG.debug( "sendSocialPostReminderMail() finished." );
    }


    // Method to fetch initial survey details from MySQL based upn agent id and customer email.
    @Override
    @Transactional
    public SurveyPreInitiation getPreInitiatedSurvey( long agentId, String customerEmail, String custFirstName,
        String custLastName )
    {
        LOG.debug( "Method getPreInitiatedSurvey() started. " );
        /*Map<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.AGENT_ID_COLUMN, agentId );
        queries.put( "customerEmailId", customerEmail );*/
        Criterion agentIdCriteria = Restrictions.eq( CommonConstants.AGENT_ID_COLUMN, agentId );
        Criterion emailCriteria = Restrictions.eq( "customerEmailId", customerEmail );

        Criterion statusCriteria = Restrictions.in( CommonConstants.STATUS_COLUMN,
            Arrays.asList( CommonConstants.SURVEY_STATUS_PRE_INITIATED, CommonConstants.SURVEY_STATUS_INITIATED,
                CommonConstants.STATUS_SURVEYPREINITIATION_COMPLETE, CommonConstants.STATUS_SURVEYPREINITIATION_DELETED ) );
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

        //get the oldest record
        SurveyPreInitiation surveyPreInitiation = null;
        if ( surveyPreInitiations != null && !surveyPreInitiations.isEmpty() ) {
            for ( SurveyPreInitiation currentSurveyPreInitiation : surveyPreInitiations ) {
                if ( surveyPreInitiation == null
                    || surveyPreInitiation.getCreatedOn().after( currentSurveyPreInitiation.getCreatedOn() ) )
                    surveyPreInitiation = currentSurveyPreInitiation;
            }
        }

        LOG.debug( "Method getSurveyByAgentIdAndCutomerEmail() finished. " );
        return surveyPreInitiation;
    }


    @Override
    @Transactional
    public SurveyPreInitiation getPreInitiatedSurvey( long surveyPreInitiationId )
    {
        LOG.debug( "Method getSurveyByAgentIdAndCutomerEmail() started for id " + surveyPreInitiationId );
        SurveyPreInitiation surveyPreInitiation = surveyPreInitiationDao.findById( SurveyPreInitiation.class,
            surveyPreInitiationId );
        LOG.debug( "Method getSurveyByAgentIdAndCutomerEmail() finished for id " + surveyPreInitiationId );
        return surveyPreInitiation;
    }


    // MEthod to delete survey pre initiation record from MySQL after making an entry into Mongo.
    @Override
    @Transactional
    public void deleteSurveyPreInitiationDetailsPermanently( SurveyPreInitiation surveyPreInitiation )
    {
        LOG.debug( "Method deleteSurveyPreInitiationDetailsPermanently() started." );
        if ( surveyPreInitiation != null )
            surveyPreInitiation.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        surveyPreInitiation.setStatus( CommonConstants.STATUS_SURVEYPREINITIATION_COMPLETE );
        surveyPreInitiationDao.saveOrUpdate( surveyPreInitiation );
        //surveyPreInitiationDao.delete( surveyPreInitiation );
        LOG.debug( "Method deleteSurveyPreInitiationDetailsPermanently() finished." );
    }


    /*
     * Method to compose link for sending to a user to start survey started.
     */
    @Override
    public String composeLink( long userId, String custEmail, String custFirstName, String custLastName,
        long surveyPreInitiationId, boolean retakeSurvey ) throws InvalidInputException
    {
        LOG.debug( "Method composeLink() started" );
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put( CommonConstants.AGENT_ID_COLUMN, userId + "" );
        urlParams.put( CommonConstants.CUSTOMER_EMAIL_COLUMN, custEmail );
        urlParams.put( CommonConstants.FIRST_NAME, custFirstName );
        urlParams.put( CommonConstants.LAST_NAME, custLastName );
        urlParams.put( CommonConstants.SURVEY_PREINITIATION_ID_COLUMN, surveyPreInitiationId + "" );
        urlParams.put( CommonConstants.URL_PARAM_RETAKE_SURVEY, retakeSurvey + "" );
        LOG.debug( "Method composeLink() finished" );
        return urlGenerator.generateUrl( urlParams, getApplicationBaseUrl() + CommonConstants.SHOW_SURVEY_PAGE_FOR_URL );
    }


    // Method to update status of the survey to started.
    @Override
    @Transactional
    public void markSurveyAsStarted( SurveyPreInitiation surveyPreInitiation )
    {
        LOG.debug( "Method markSurveyAsStarted() started." );
        surveyPreInitiation.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        surveyPreInitiation.setStatus( CommonConstants.SURVEY_STATUS_INITIATED );
        surveyPreInitiationDao.update( surveyPreInitiation );
        LOG.debug( "Method markSurveyAsStarted() finished." );
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

        OrganizationUnitSettings companySettings = null;
        AgentSettings agentSettings = null;
        Set<Long> companies = new HashSet<>();
        for ( SurveyPreInitiation survey : surveys ) {
            int status = CommonConstants.STATUS_SURVEYPREINITIATION_PROCESSED;
            String errorCode = SurveyErrorCode.NOT_KNOWN.name();
            User user = null;
            if ( survey.getAgentEmailId() != null ) {
                try {
                    user = userManagementService.getActiveAgentByEmailAndCompany( survey.getCompanyId(),
                        survey.getAgentEmailId() );
                } catch ( InvalidInputException | NoRecordsFetchedException e ) {
                    LOG.error( "No user found in database for the email id: " + survey.getAgentEmailId() + " and company id : "
                        + survey.getCompanyId() );
                }


                if ( user != null ) {
                    // check if survey has already been sent to the email id

                    companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(
                        user.getCompany().getCompanyId(), MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
                    try {
                        agentSettings = userManagementService.getUserSettings( user.getUserId() );
                    } catch ( InvalidInputException e ) {
                        LOG.error( "No settings found in database for the user id:" + user.getUserId() );
                    }

                    int duplicateSurveyInterval = 0;
                    if ( companySettings != null && companySettings.getSurvey_settings() != null
                        && companySettings.getSurvey_settings().getDuplicateSurveyInterval() > 0 ) {
                        duplicateSurveyInterval = companySettings.getSurvey_settings().getDuplicateSurveyInterval();
                    } else {
                        duplicateSurveyInterval = defaultSurveyRetakeInterval;
                    }

                    // check the pre-initiation and then the survey table
                    List<SurveyPreInitiation> incompleteSurveyCustomers = null;
                    if ( duplicateSurveyInterval > 0 ) {
                        incompleteSurveyCustomers = surveyPreInitiationDao.getSurveyByAgentIdAndCustomeEmailForPastNDays(
                            user.getUserId(), survey.getCustomerEmailId(), duplicateSurveyInterval );
                    } else {
                        incompleteSurveyCustomers = surveyPreInitiationDao.getSurveyByAgentIdAndCustomeEmail( user.getUserId(),
                            survey.getCustomerEmailId() );
                    }
                    if ( incompleteSurveyCustomers != null && incompleteSurveyCustomers.size() > 0 ) {
                        LOG.warn( "Survey request already sent" );
                        status = CommonConstants.STATUS_SURVEYPREINITIATION_DUPLICATE_RECORD;
                        errorCode = SurveyErrorCode.DUPLICATE_RECORD.name();
                        survey.setStatus( status );
                    }
                    // check the survey collection
                    SurveyDetails surveyDetail = null;
                    if ( duplicateSurveyInterval > 0 ) {
                        surveyDetail = surveyDetailsDao.getSurveyByAgentIdAndCustomerEmailAndNoOfDays( user.getUserId(),
                            survey.getCustomerEmailId(), null, null, duplicateSurveyInterval );
                    } else {
                        surveyDetail = surveyDetailsDao.getSurveyByAgentIdAndCustomerEmail( user.getUserId(),
                            survey.getCustomerEmailId(), null, null );
                    }

                    if ( surveyDetail != null ) {
                        LOG.warn( "Survey request already sent and completed" );
                        status = CommonConstants.STATUS_SURVEYPREINITIATION_DUPLICATE_RECORD;
                        errorCode = SurveyErrorCode.DUPLICATE_RECORD.name();
                        survey.setStatus( status );
                    }

                    LOG.debug( "Mapping the agent to this survey " );
                    if ( survey.getAgentId() == 0 ) {
                        survey.setAgentId( user.getUserId() );
                    }
                    survey.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
                    surveyPreInitiationDao.update( survey );
                }

            }
            Timestamp engagementClosedTime = survey.getEngagementClosedTime();
            Calendar calendar = Calendar.getInstance();
            calendar.add( Calendar.DATE, -validSurveyInterval );
            Date date = calendar.getTime();

            if ( engagementClosedTime.before( date ) ) {
                LOG.debug( "An old record found : " + survey.getSurveyPreIntitiationId() );
                status = CommonConstants.STATUS_SURVEYPREINITIATION_OLD_RECORD;
                errorCode = SurveyErrorCode.OLD_RECORD.name();
                oldRecords.add( survey );
                companies.add( survey.getCompanyId() );
            } else if ( user == null && isEmailIsIgnoredEmail( survey.getAgentEmailId(), survey.getCompanyId() ) ) {
                LOG.error( "no agent found with this email id and its an ignored record" );
                status = CommonConstants.STATUS_SURVEYPREINITIATION_IGNORED_RECORD;
                errorCode = SurveyErrorCode.IGNORED_RECORD.name();
                ignoredEmailRecords.add( survey );
                companies.add( survey.getCompanyId() );
            } else if ( survey.getAgentEmailId() == null || survey.getAgentEmailId().isEmpty() ) {
                LOG.error( "Agent email not found , invalid survey " + survey.getSurveyPreIntitiationId() );
                status = CommonConstants.STATUS_SURVEYPREINITIATION_CORRUPT_RECORD;
                errorCode = SurveyErrorCode.CORRUPT_RECORD_AGENT_EMAIL_ID_NULL.name();
                unavailableAgents.add( survey );
                companies.add( survey.getCompanyId() );
            } else if ( ( survey.getCustomerFirstName() == null || survey.getCustomerFirstName().isEmpty() )
                && ( survey.getCustomerLastName() == null || survey.getCustomerLastName().isEmpty() ) ) {
                LOG.error(
                    "No Name found for customer, hence this is an invalid survey " + survey.getSurveyPreIntitiationId() );
                status = CommonConstants.STATUS_SURVEYPREINITIATION_CORRUPT_RECORD;
                errorCode = SurveyErrorCode.CORRUPT_RECORD_CUSTOMER_FIRST_NAME_NULL.name();
                customersWithoutName.add( survey );

            } else if ( survey.getCustomerEmailId() == null || survey.getCustomerEmailId().isEmpty() ) {
                LOG.error( "No customer email id found, invalid survey " + survey.getSurveyPreIntitiationId() );
                status = CommonConstants.STATUS_SURVEYPREINITIATION_CORRUPT_RECORD;
                errorCode = SurveyErrorCode.CORRUPT_RECORD_CUSTOMER_EMAIL_ID_NULL.name();
                customersWithoutEmailId.add( survey );
                companies.add( survey.getCompanyId() );
            } else if ( user == null ) {
                LOG.error( "no agent found with this email id" );
                status = CommonConstants.STATUS_SURVEYPREINITIATION_MISMATCH_RECORD;
                errorCode = SurveyErrorCode.MISMATCH_RECORD_AGENT_NOT_FOUND.name();
                invalidAgents.add( survey );
                companies.add( survey.getCompanyId() );
            } else if ( user.getCompany() == null ) {
                LOG.error( "Agent doesnt have an company associated with it " );
                status = CommonConstants.STATUS_SURVEYPREINITIATION_MISMATCH_RECORD;
                errorCode = SurveyErrorCode.MISMATCH_RECORD_INCORRECT_COMPANY.name();
                invalidAgents.add( survey );
                companies.add( survey.getCompanyId() );
            } else if ( user.getCompany().getCompanyId() != survey.getCompanyId() ) {
                status = CommonConstants.STATUS_SURVEYPREINITIATION_MISMATCH_RECORD;
                errorCode = SurveyErrorCode.MISMATCH_RECORD_INCORRECT_COMPANY.name();
                invalidAgents.add( survey );
                companies.add( survey.getCompanyId() );
            } else if ( survey.getParticipantType() == CommonConstants.SURVEY_PARTICIPANT_TYPE_BUYER_AGENT
                || survey.getParticipantType() == CommonConstants.SURVEY_PARTICIPANT_TYPE_SELLER_AGENT ) {
                if ( !isPartnerSurveyAllowed( companySettings, agentSettings ) ) {
                    status = CommonConstants.STATUS_SURVEYPREINITIATION_SURVEY_NOT_ALLOWED;
                    errorCode = SurveyErrorCode.SURVEY_NOT_ALLOWED.name();
                    companies.add( survey.getCompanyId() );
                }
            }

            if ( status == CommonConstants.STATUS_SURVEYPREINITIATION_PROCESSED ) {
                if ( survey.getSurveySource().equalsIgnoreCase( CommonConstants.CRM_SOURCE_DOTLOOP ) ) {
                    status = validateUnitsettingsForDotloop( user, survey );
                    if ( status == CommonConstants.STATUS_SURVEYPREINITIATION_CORRUPT_RECORD ) {
                        unavailableAgents.add( survey );
                        errorCode = SurveyErrorCode.CORRUPT_RECORD_INCORRECT_REGION_BRANCH.name();
                        companies.add( survey.getCompanyId() );
                    }
                }
            }
            survey.setStatus( status );
            survey.setErrorCode( errorCode );
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
     * @param companySettings
     * @param agentSettings
     * @return
     */
    boolean isPartnerSurveyAllowed( OrganizationUnitSettings companySettings, AgentSettings agentSettings )
    {

        if ( companySettings != null && agentSettings != null ) {
            CRMInfo crmInfo = companySettings.getCrm_info();
            if ( crmInfo != null && crmInfo.isAllowPartnerSurvey() ) {
                //check if agent is allowed for partner survey
                if ( agentSettings.isAllowPartnerSurvey() )
                    return true;
            }
        }
        return false;
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
        List<CompanyIgnoredEmailMapping> companyIgnoredEmailMapping = companyIgnoredEmailMappingDao
            .findByKeyValue( CompanyIgnoredEmailMapping.class, queries );
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
        LOG.debug( "Inside method validateUnitSettingsForDotloop " );
        int status = CommonConstants.STATUS_SURVEYPREINITIATION_PROCESSED;
        if ( surveyPreInitiation != null ) {
            LOG.debug( "Processing survey pre initiation id: " + surveyPreInitiation.getSurveyPreIntitiationId() );
            boolean found = false;
            if ( surveyPreInitiation.getCompanyId() == user.getCompany().getCompanyId() ) {
                LOG.debug( "Though the company id is same, the region or branch might be different " );
                if ( surveyPreInitiation.getCollectionName()
                    .equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION ) ) {
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
                } else if ( surveyPreInitiation.getCollectionName()
                    .equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION ) ) {
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
                } else if ( surveyPreInitiation.getCollectionName()
                    .equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION ) ) {
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
    @Override
    public void prepareAndSendInvitationMail( SurveyPreInitiation survey )
        throws InvalidInputException, UndeliveredEmailException, ProfileNotFoundException
    {
        // Send email to complete survey to each customer.
        String agentName = "";
        User user = null;
        Map<String, Long> hierarchyMap = null;
        Map<SettingsForApplication, OrganizationUnit> map = null;
        String logoUrl = null;
        user = userManagementService.getUserByUserId( survey.getAgentId() );

        if ( user != null ) {
            agentName = user.getFirstName();
            if ( user.getLastName() != null && !user.getLastName().isEmpty() ) {
                agentName = user.getFirstName() + " " + user.getLastName();
            }
        }

        String surveyLink = composeLink( survey.getAgentId(), survey.getCustomerEmailId(), survey.getCustomerFirstName(),
            survey.getCustomerLastName(), survey.getSurveyPreIntitiationId(), false );

        // Fetching agent settings.
        AgentSettings agentSettings = userManagementService.getUserSettings( survey.getAgentId() );
        hierarchyMap = profileManagementService.getPrimaryHierarchyByAgentProfile( agentSettings );
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
        long companyId = hierarchyMap.get( CommonConstants.COMPANY_ID_COLUMN );
        long regionId = hierarchyMap.get( CommonConstants.REGION_ID_COLUMN );
        long branchId = hierarchyMap.get( CommonConstants.BRANCH_ID_COLUMN );

        OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( companyId );


        String agentTitle = "";
        if ( agentSettings.getContact_details() != null && agentSettings.getContact_details().getTitle() != null ) {
            agentTitle = agentSettings.getContact_details().getTitle();
        }

        String agentEmailId = "";
        if ( agentSettings.getContact_details().getMail_ids().getWork() != null ) {
            agentEmailId = agentSettings.getContact_details().getMail_ids().getWork();
        }

        String agentPhone = "";
        if ( agentSettings.getContact_details() != null && agentSettings.getContact_details().getContact_numbers() != null
            && agentSettings.getContact_details().getContact_numbers().getWork() != null ) {
            agentPhone = agentSettings.getContact_details().getContact_numbers().getWork();
        }


        String companyName = user.getCompany().getCompany();
        String currentYear = String.valueOf( Calendar.getInstance().get( Calendar.YEAR ) );
        DateFormat dateFormat = new SimpleDateFormat( "yyyy/MM/dd" );
        String agentSignature = emailFormatHelper.buildAgentSignature( agentName, agentPhone, agentTitle, companyName );
        String fullAddress = "";

        OrganizationUnit organizationUnit = map.get( SettingsForApplication.LOGO );

        if ( organizationUnit == OrganizationUnit.COMPANY ) {
            logoUrl = companySettings.getLogo();
        } else if ( organizationUnit == OrganizationUnit.REGION ) {
            OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( regionId );
            logoUrl = regionSettings.getLogo();
        } else if ( organizationUnit == OrganizationUnit.BRANCH ) {
            OrganizationUnitSettings branchSettings = null;
            try {
                branchSettings = organizationManagementService.getBranchSettingsDefault( branchId );
            } catch ( NoRecordsFetchedException e ) {
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
        LOG.debug( "Initiating URL Service to shorten the url " + surveyLink );
        try {
            surveyLink = urlService.shortenUrl( surveyLink );
        } catch ( InvalidInputException e ) {
            LOG.error( "InvalidInput Exception while url shortening url. Reason : ", e );
        }

        //get mail subject and body
        String mailBody = "";
        String mailSubject = "";
        if ( companySettings != null && companySettings.getMail_content() != null
            && companySettings.getMail_content().getTake_survey_mail() != null ) {

            MailContent mailContent = companySettings.getMail_content().getTake_survey_mail();

            mailBody = emailFormatHelper.replaceEmailBodyWithParams( mailContent.getMail_body(), mailContent.getParam_order() );


            LOG.debug( "Finished calling URL Service to shorten the url.Shortened URL : " + surveyLink );

            //JIRA SS-473 begin
            String agentDisclaimer = "";
            String agentLicenses = "";
            String companyDisclaimer = "";

            if ( companySettings != null && companySettings.getDisclaimer() != null )
                companyDisclaimer = companySettings.getDisclaimer();

            if ( agentSettings.getDisclaimer() != null )
                agentDisclaimer = agentSettings.getDisclaimer();

            if ( agentSettings.getLicenses() != null && agentSettings.getLicenses().getAuthorized_in() != null ) {
                agentLicenses = StringUtils.join( agentSettings.getLicenses().getAuthorized_in(), ',' );
            }

            mailBody = emailFormatHelper.replaceLegends( false, mailBody, applicationBaseUrl, logoUrl, surveyLink,
                survey.getCustomerFirstName(), survey.getCustomerLastName(), agentName, agentSignature,
                survey.getCustomerEmailId(), user.getEmailId(), companyName, dateFormat.format( new Date() ), currentYear,
                fullAddress, "", user.getProfileName(), companyDisclaimer, agentDisclaimer, agentLicenses );

            mailSubject = CommonConstants.SURVEY_MAIL_SUBJECT + agentName;
            if ( mailContent.getMail_subject() != null && !mailContent.getMail_subject().isEmpty() ) {
                mailSubject = mailContent.getMail_subject();
            }
            mailSubject = emailFormatHelper.replaceLegends( true, mailSubject, applicationBaseUrl, logoUrl, surveyLink,
                survey.getCustomerFirstName(), survey.getCustomerLastName(), agentName, agentSignature,
                survey.getCustomerEmailId(), user.getEmailId(), companyName, dateFormat.format( new Date() ), currentYear,
                fullAddress, "", user.getProfileName(), companyDisclaimer, agentDisclaimer, agentLicenses );

            //JIRA SS-473 end

        } else {

            mailSubject = fileOperations.getContentFromFile(
                EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_INVITATION_MAIL_SUBJECT );

            mailSubject = emailFormatHelper.replaceEmailBodyWithParams( mailSubject,
                Arrays.asList( paramOrderTakeSurveySubject.split( "," ) ) );

            mailBody = fileOperations.getContentFromFile(
                EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_INVITATION_MAIL_BODY );

            mailBody = emailFormatHelper.replaceEmailBodyWithParams( mailBody,
                new ArrayList<String>( Arrays.asList( paramOrderTakeSurvey.split( "," ) ) ) );
        }

        //JIRA SS-473 begin
        String agentDisclaimer = "";
        String agentLicenses = "";
        String companyDisclaimer = "";

        if ( companySettings != null && companySettings.getDisclaimer() != null )
            companyDisclaimer = companySettings.getDisclaimer();

        if ( agentSettings.getDisclaimer() != null )
            agentDisclaimer = agentSettings.getDisclaimer();

        if ( agentSettings.getLicenses() != null && agentSettings.getLicenses().getAuthorized_in() != null ) {
            agentLicenses = StringUtils.join( agentSettings.getLicenses().getAuthorized_in(), ',' );
        }
        //replace the legends
        mailBody = emailFormatHelper.replaceLegends( false, mailBody, applicationBaseUrl, logoUrl, surveyLink,
            survey.getCustomerFirstName(), survey.getCustomerLastName(), agentName, agentSignature, survey.getCustomerEmailId(),
            user.getEmailId(), companyName, dateFormat.format( new Date() ), currentYear, fullAddress, "",
            user.getProfileName(), companyDisclaimer, agentDisclaimer, agentLicenses );
        mailSubject = emailFormatHelper.replaceLegends( true, mailSubject, applicationBaseUrl, logoUrl, surveyLink,
            survey.getCustomerFirstName(), survey.getCustomerLastName(), agentName, agentSignature, survey.getCustomerEmailId(),
            user.getEmailId(), companyName, dateFormat.format( new Date() ), currentYear, fullAddress, "",
            user.getProfileName(), companyDisclaimer, agentDisclaimer, agentLicenses );

        //JIRA SS-473 end

        //For Company with hidden agents
        String senderName;
        if ( companySettings.isSendEmailFromCompany() ) {
            senderName = companyName;
        } else {
            senderName = agentName;
        }

        //send the mail
        try {
            emailServices.sendSurveyInvitationMail( survey.getCustomerEmailId(), mailSubject, mailBody, user.getEmailId(),
                senderName, user.getUserId() );
        } catch ( InvalidInputException | UndeliveredEmailException e ) {
            LOG.error( "Exception caught while sending mail to " + survey.getCustomerEmailId() + " .Nested exception is ", e );
        }
    }


    /*
     * Method to send email by customer to initiate survey.
     */
    @SuppressWarnings ( "unused")
    private void sendInvitationMailByCustomer( User user, String custFirstName, String custLastName, String custEmail,
        String link ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "sendInvitationMailByCustomer() started." );

        OrganizationUnitSettings companySettings = organizationManagementService
            .getCompanySettings( user.getCompany().getCompanyId() );

        String mailBody = "";
        String mailSubject = "";
        if ( companySettings != null && companySettings.getMail_content() != null
            && companySettings.getMail_content().getTake_survey_mail_customer() != null ) {

            MailContent takeSurveyCustomer = companySettings.getMail_content().getTake_survey_mail_customer();
            mailBody = emailFormatHelper.replaceEmailBodyWithParams( takeSurveyCustomer.getMail_body(),
                takeSurveyCustomer.getParam_order() );
            mailSubject = CommonConstants.SURVEY_MAIL_SUBJECT_CUSTOMER;
        } else {
            mailSubject = fileOperations.getContentFromFile(
                EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_INVITATION_MAIL_SUBJECT );

            mailBody = fileOperations.getContentFromFile(
                EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_INVITATION_MAIL_BODY );

            mailBody = emailFormatHelper.replaceEmailBodyWithParams( mailBody,
                new ArrayList<String>( Arrays.asList( paramOrderTakeSurveyCustomer.split( "," ) ) ) );

        }
        //JIRA SS-473 begin
        String agentDisclaimer = "";
        String agentLicenses = "";
        String companyDisclaimer = "";

        if ( companySettings != null && companySettings.getDisclaimer() != null )
            companyDisclaimer = companySettings.getDisclaimer();

        //replace legends
        mailBody = emailFormatHelper.replaceLegends( false, mailBody, applicationBaseUrl, appLogoUrl, link, custFirstName,
            custLastName, user.getFirstName() + " " + user.getLastName(), null, null, null, null, null, null, null, "",
            user.getProfileName(), companyDisclaimer, agentDisclaimer, agentLicenses );
        mailSubject = emailFormatHelper.replaceLegends( true, mailSubject, applicationBaseUrl, appLogoUrl, link, custFirstName,
            custLastName, user.getFirstName() + " " + user.getLastName(), null, null, null, null, null, null, null, "",
            user.getProfileName(), companyDisclaimer, agentDisclaimer, agentLicenses );

        //JIRA SS-473 end

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
    @Override
    public SurveyPreInitiation preInitiateSurvey( User user, String custEmail, String custFirstName, String custLastName, int i,
        String custRelationWithAgent, String source )
    {
        LOG.debug(
            "Method preInitiateSurvey() started to store details of a customer in mysql at the time of  sending invite" );

        SurveyPreInitiation surveyPreInitiation = new SurveyPreInitiation();
        surveyPreInitiation.setAgentId( user.getUserId() );
        surveyPreInitiation.setAgentEmailId( user.getLoginName() );
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
        surveyPreInitiation = surveyPreInitiationDao.save( surveyPreInitiation );

        LOG.debug( "Method preInitiateSurvey() finished." );
        return surveyPreInitiation;
    }


    @Override
    public boolean checkSurveyReminderEligibility( long lastRemindedTime, long systemTime, int reminderInterval )
    {
        LOG.debug( "Checking time interval expiry: lastRemindedTime " + lastRemindedTime + "\t systemTime: " + systemTime
            + "\t reminderInterval: " + reminderInterval );
        long remainingTime = systemTime - lastRemindedTime;
        int remainingDays = (int) ( remainingTime / ( 1000 * 60 * 60 * 24 ) );
        if ( remainingDays >= reminderInterval ) {
            // check if reminder is older than configured value
            if ( remainingDays <= maxSurveyReminderInterval ) {
                return true;
            } else {
                return false;
            }
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
            throw new NoRecordsFetchedException(
                "No records found for surveyPreInitiation with id : " + surveyPreInitiationId );
        }

        return surveyPreInitiation;
    }


    @Override
    @Transactional
    public void initiateSurveyRequest( long agentId, String recipientEmailId, String recipientFirstname,
        String recipientLastname, String source )
        throws DuplicateSurveyRequestException, InvalidInputException, SelfSurveyInitiationException, SolrException,
        NoRecordsFetchedException, UndeliveredEmailException, ProfileNotFoundException
    {
        LOG.debug( "Sending survey request for agent id: " + agentId + " recipientEmailId: " + recipientEmailId
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

        //Trim all the details
        recipientEmailId = recipientEmailId.trim();
        recipientFirstname = recipientFirstname.trim();
        if ( recipientLastname != null ) {
            recipientLastname = recipientLastname.trim();
        }


        // check if survey has already been sent to the email id
        // check the pre-initiation and then the survey table
        /*HashMap<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.AGENT_ID_COLUMN, agentId );
        queries.put( CommonConstants.CUSTOMER_EMAIL_ID_KEY_COLUMN, recipientEmailId );
        List<SurveyPreInitiation> incompleteSurveyCustomers = surveyPreInitiationDao.findByKeyValue( SurveyPreInitiation.class,
            queries );*/

        //check if already an incomplete

        int duplicateSurveyInterval = 0;
        User user = userDao.findById( User.class, agentId );
        if ( user != null && user.getCompany() != null ) {
            OrganizationUnitSettings companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(
                user.getCompany().getCompanyId(), MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
            if ( companySettings != null && companySettings.getSurvey_settings() != null
                && companySettings.getSurvey_settings().getDuplicateSurveyInterval() > 0 ) {
                duplicateSurveyInterval = companySettings.getSurvey_settings().getDuplicateSurveyInterval();
            } else {
                duplicateSurveyInterval = defaultSurveyRetakeInterval;
            }
        }


        List<SurveyPreInitiation> incompleteSurveyCustomers = null;
        if ( duplicateSurveyInterval > 0 ) {
            incompleteSurveyCustomers = surveyPreInitiationDao.getSurveyByAgentIdAndCustomeEmailForPastNDays( agentId,
                recipientEmailId, duplicateSurveyInterval );
        } else {
            incompleteSurveyCustomers = surveyPreInitiationDao.getSurveyByAgentIdAndCustomeEmail( agentId, recipientEmailId );
        }

        if ( incompleteSurveyCustomers != null && incompleteSurveyCustomers.size() > 0 ) {
            LOG.warn( "Survey request already sent" );
            throw new DuplicateSurveyRequestException( "Survey request already sent" );
        }
        // check the survey collection
        SurveyDetails survey;
        if ( duplicateSurveyInterval > 0 ) {
            survey = surveyDetailsDao.getSurveyByAgentIdAndCustomerEmailAndNoOfDays( agentId, recipientEmailId, null, null,
                duplicateSurveyInterval );
        } else {
            survey = surveyDetailsDao.getSurveyByAgentIdAndCustomerEmail( agentId, recipientEmailId, null, null );
        }
        if ( survey != null ) {
            LOG.warn( "Survey request already sent and completed" );
            throw new DuplicateSurveyRequestException( "Survey request already sent and completed" );
        }
        LOG.debug( "Sending survey request mail." );
        storeSPIandSendSurveyInvitationMail( recipientFirstname, recipientLastname, recipientEmailId, null, agent, true,
            source );
    }


    //    Commented as Zillow surveys are not stored in database, SS-1276
    //    @Override
    //    @Transactional
    //    public void deleteZillowSurveysByEntity( String entityType, long entityId ) throws InvalidInputException
    //    {
    //        LOG.debug( "Method deleteZillowSurveysByEntity() started" );
    //        if ( entityType == null || entityType.isEmpty() ) {
    //            throw new InvalidInputException( "Entity Type is invalid" );
    //        }
    //        if ( entityId <= 0 ) {
    //            throw new InvalidInputException( "Entity ID is invalid" );
    //        }
    //        surveyDetailsDao.removeZillowSurveysByEntity( entityType, entityId );
    //        LOG.debug( "Method deleteZillowSurveysByEntity() finished" );
    //    }


    //    Commented as Zillow surveys are not stored in database, SS-1276
    //    @Override
    //    @Transactional
    //    public void deleteExcessZillowSurveysByEntity( String entityType, long entityId ) throws InvalidInputException
    //    {
    //         LOG.debug( "Method deleteExcessZillowSurveysByEntity() started" );
    //         if ( entityType == null || entityType.isEmpty() ) {
    //             throw new InvalidInputException( "Entity Type is invalid" );
    //         }
    //         if ( entityId <= 0 ) {
    //             throw new InvalidInputException( "Entity ID is invalid" );
    //         }
    //         surveyDetailsDao.removeExcessZillowSurveysByEntity( entityType, entityId );
    //         LOG.debug( "Method deleteExcessZillowSurveysByEntity() finished" );
    //    }

    @Override
    @Transactional
    public void deleteExistingZillowSurveysByEntity( String entityType, long entityId ) throws InvalidInputException
    {
        LOG.debug( "Method deleteExistingZillowSurveysByEntity() started" );
        if ( entityType == null || entityType.isEmpty() ) {
            throw new InvalidInputException( "Entity Type is invalid" );
        }
        if ( entityId <= 0 ) {
            throw new InvalidInputException( "Entity ID is invalid" );
        }
        surveyDetailsDao.removeExistingZillowSurveysByEntity( entityType, entityId );
        LOG.debug( "Method deleteExistingZillowSurveysByEntity() finished" );
    }


    @Override
    public List<AbusiveSurveyReportWrapper> getSurveysReportedAsAbusive( int startIndex, int numOfRows )
    {
        LOG.debug( "Method getSurveysReporetedAsAbusive() to retrieve surveys marked as abusive, started" );
        List<AbusiveSurveyReportWrapper> abusiveSurveyReports = surveyDetailsDao.getSurveysReporetedAsAbusive( startIndex,
            numOfRows );
        LOG.debug( "Method getSurveysReporetedAsAbusive() to retrieve surveys marked as abusive, finished" );
        return abusiveSurveyReports;
    }


    @Override
    @Transactional
    public void updateSurveyDetails( SurveyDetails surveyDetails )
    {
        surveyDetailsDao.updateSurveyDetails( surveyDetails );
    }


    @Override
    @Transactional
    public void updateSurveyDetailsBySurveyId( SurveyDetails surveyDetails )
    {
        surveyDetailsDao.updateSurveyDetailsBySurveyId( surveyDetails );
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

        LOG.debug( "Method updateSurveyAsUnderResolution() to mark a survey as under resolution started, started" );
        surveyDetailsDao.updateSurveyAsUnderResolution( surveyId );
        LOG.debug( "Method updateSurveyAsUnderResolution() to mark a survey as under resolution started, ended" );
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
        LOG.debug( "Method getSurveysReportedAsAbusive() to retrieve surveys marked as abusive for a company, started" );
        List<AbusiveSurveyReportWrapper> abusiveSurveyReports = surveyDetailsDao.getSurveysReporetedAsAbusive( companyId,
            startIndex, numOfRows );
        LOG.debug( "Method getSurveysReportedAsAbusive() to retrieve surveys marked as abusive for a company, finished" );
        return abusiveSurveyReports;
    }


    @Override
    public Boolean canPostOnSocialMedia( OrganizationUnitSettings unitSetting, Double rating )
    {
        boolean canPost = false;
        
        if (  unitSetting != null  && unitSetting.getSurvey_settings() != null ) {
                if ( unitSetting.getSurvey_settings().isAutoPostEnabled() && unitSetting.getSurvey_settings().getAuto_post_score() <= rating ) {
                    canPost = true;
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
                /*if ( bulkSurveyDetail.getAgentFirstName() == null || bulkSurveyDetail.getAgentFirstName().isEmpty() ) {
                    message = "Invalid Agent Name ";
                    status = CommonConstants.BULK_SURVEY_INVALID;
                    error = true;
                } else */if ( bulkSurveyDetail.getAgentEmailId() == null || bulkSurveyDetail.getAgentEmailId().isEmpty() ) {
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
        LOG.debug( "Method getSurveysUnderResolution() to retrieve surveys marked as under resolution for a company, started" );
        List<SurveyDetails> surveyDetails = surveyDetailsDao.getSurveysUnderResolution( companyId, startIndex, numOfRows );
        LOG.debug(
            "Method getSurveysUnderResolution() to retrieve surveys marked as under resolution for a company, finished" );
        return surveyDetails;
    }


    @Override
    public SurveyDetails getSurveyDetails( String surveyMongoId )
    {
        LOG.debug( "Method getSurveyDetails() to return survey details by surveyMongoId started." );
        SurveyDetails surveyDetails;
        surveyDetails = surveyDetailsDao.getSurveyBySurveyMongoId( surveyMongoId );
        LOG.debug( "Method getSurveyDetails() to return survey details by surveyMongoId finished." );
        return surveyDetails;
    }


    @Override
    public void updateSurveyAsUnAbusive( String surveyId )
    {
        LOG.debug( "Method unMarkAbusiveSurvey() started" );
        surveyDetailsDao.updateSurveyAsUnAbusive( surveyId );
        LOG.debug( "Method unMarkAbusiveSurvey() finished" );
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
    public void moveSurveysToAnotherUser( long fromUserId, long toUserId )
        throws InvalidInputException, NoRecordsFetchedException, SolrException
    {
        if ( fromUserId <= 0 ) {
            LOG.error( "Invalid from user id passed as parameter" );
            throw new InvalidInputException( "Invalid from user id passed as parameter" );
        }
        if ( toUserId <= 0 ) {
            LOG.error( "Invalid to user id passed as parameter" );
            throw new InvalidInputException( "Invalid to user id passed as parameter" );
        }
        LOG.debug( "Method to move surveys from one user to another user,moveSurveysToAnotherUser() call started" );
        List<Long> userIds = new ArrayList<Long>();
        userIds.add( fromUserId );
        userIds.add( toUserId );
        LOG.debug( "Fetching from and to user information" );
        List<User> userList = null;
        try {
            userList = userDao.getUsersForUserIds( userIds );
        } catch ( Exception e ) {
            throw new NoRecordsFetchedException( "Error occurred while fetching information for users" );
        }
        if ( userList == null || userList.size() != 2 )
            throw new NoRecordsFetchedException( "Either from user or to user or both could not be found" );
        LOG.debug( "Fetched from and to user information" );
        User fromUser = userList.get( 0 ).getUserId() == fromUserId ? userList.get( 0 ) : userList.get( 1 );
        User toUser = userList.get( 1 ).getUserId() == toUserId ? userList.get( 1 ) : userList.get( 0 );

        // check if both user belong to same company
        //UserProfile fromUserProfile = getUserProfileWhereAgentForUser( fromUser );
        UserProfile toUserProfile = getUserProfileWhereAgentForUser( toUser );
        LOG.debug( "Validating whether both from and to user are agents" );
        // check if to user id is an agent
        if ( toUserProfile == null )
            throw new NoRecordsFetchedException( "To user id : " + toUser.getUserId() + " is not an agent" );
        LOG.debug( "Validating whether both from and to user belong to same company" );
        if ( fromUser.getCompany().getCompanyId() != toUser.getCompany().getCompanyId() )
            throw new UnsupportedOperationException( "From user : " + fromUser.getUserId() + " and to user id : "
                + toUser.getUserId() + " do not belong to same company" );

        // replace agent id Survey Pre Initiation
        LOG.debug( "Moving all incomplete surveys of user : " + fromUserId + " to user : " + toUserId );
        surveyPreInitiationDao.updateAgentInfoOfPreInitiatedSurveys( fromUserId, toUser );
        // replace agent id in Surveys
        LOG.debug( "Moving all started & completed surveys of user : " + fromUserId + " to user : " + toUserId );
        surveyDetailsDao.updateAgentInfoInSurveys( fromUserId, toUser, toUserProfile );
        // update to user solr review count
        LOG.debug( "Updating review count of user : " + toUserId );
        solrSearchService.updateReviewCountOfUserInSolr( toUser );
        if ( fromUser.getStatus() == CommonConstants.STATUS_ACTIVE ) {
            // update from user solr review count
            LOG.debug( "Updating review count of user : " + fromUserId );
            solrSearchService.updateReviewCountOfUserInSolr( fromUser );
        }
        LOG.debug( "Method to move surveys from one user to another user,moveSurveysToAnotherUser() call ended" );
    }


    UserProfile getUserProfileWhereAgentForUser( User user )
    {
        LOG.debug( "Method to find user profile where user is agent, getUserProfileWhereAgentForUser started" );
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
        LOG.debug( "Method to find user profile where user is agent, getUserProfileWhereAgentForUser ended" );
        return agentUserProfile;
    }


    @Transactional
    @Override
    public void importSurveyVOToDBs( SurveyImportVO surveyImportVO, String source ) throws NonFatalException
    {
        LOG.debug( "Method SurveyHandlerImpl.importSurveyVOToDBs started" );
        User user = userManagementService.getUserByUserId( surveyImportVO.getUserId() );
        if ( user == null )
            throw new InvalidInputException( "User with userId : " + surveyImportVO.getUserId() + " was not found" );
        AgentSettings settings = userManagementService.getUserSettings( user.getUserId() );
        if ( settings == null )
            throw new InvalidInputException( "AgentSettings empty for user: " + user.getUserId() );
        //Resolve customerFirstName and customerLastName
        surveyImportVO = resolveCustomerName( surveyImportVO );
        SurveyPreInitiation surveyPreInitiation = importSurveyVOToSurveyPreInitiation( surveyImportVO, user, source );
        SurveyDetails details = importSurveyVOToMongo( surveyImportVO, surveyPreInitiation, user, source );
        String serverBaseUrl = getApplicationBaseUrl();
        String agentProfileLink = serverBaseUrl + CommonConstants.AGENT_PROFILE_FIXED_URL + settings.getProfileUrl();
        socialManagementService.postToSocialMedia( details.getAgentName(), agentProfileLink, details.getCustomerFirstName(),
            details.getCustomerLastName(), details.getAgentId(), details.getScore(), details.get_id(), details.getReview(),
            false, serverBaseUrl, true );
        //Date currentDate = new Date(System.currentTimeMillis());
        //surveyDetailsDao.updateModifiedDateForSurvey( details.get_id(), currentDate );
        LOG.debug( "Method SurveyHandlerImpl.importSurveyVOToDBs finished" );
    }


    SurveyImportVO resolveCustomerName( SurveyImportVO surveyImportVO ) throws InvalidInputException
    {
        String nameArray[] = null;
        String individualName = surveyImportVO.getCustomerFirstName().trim();
        if ( surveyImportVO.getCustomerLastName() != null && !surveyImportVO.getCustomerLastName().isEmpty() )
            individualName += " " + surveyImportVO.getCustomerLastName().trim();
        if ( individualName != null && !individualName.equalsIgnoreCase( "" ) ) {
            nameArray = individualName.split( " " );
        }

        if ( nameArray == null ) {
            throw new InvalidInputException( "Invalid name, please provide a valid name " );
        }

        surveyImportVO.setCustomerFirstName( nameArray[0] );
        String lastName = "";
        if ( nameArray.length > 1 ) {
            for ( int i = 1; i <= nameArray.length - 1; i++ ) {
                lastName += nameArray[i] + " ";
            }
        }
        if ( lastName != null && !lastName.equalsIgnoreCase( "" ) ) {
            lastName = lastName.trim();
            surveyImportVO.setCustomerLastName( lastName );
        } else {
            surveyImportVO.setCustomerLastName( "" );
        }
        return surveyImportVO;
    }


    SurveyPreInitiation importSurveyVOToSurveyPreInitiation( SurveyImportVO surveyImportVO, User user, String source )
        throws InvalidInputException
    {
        LOG.debug( "Method BulkSurveyImporter.importSurveyVOToSurveyPreInitiation started" );
        SurveyPreInitiation survey = new SurveyPreInitiation();
        survey.setSurveySource( source );
        survey.setSurveySourceId( surveyImportVO.getSurveySourceId() );
        survey.setCompanyId( user.getCompany().getCompanyId() );
        survey.setAgentId( user.getUserId() );
        survey.setAgentEmailId( user.getEmailId() );
        survey.setCustomerFirstName( surveyImportVO.getCustomerFirstName() );
        survey.setCustomerLastName( surveyImportVO.getCustomerLastName() );
        survey.setCustomerEmailId( surveyImportVO.getCustomerEmailAddress() );
        Date date = surveyImportVO.getSurveyDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime( date );
        Timestamp engagementTimestamp = new Timestamp( cal.getTimeInMillis() );
        survey.setEngagementClosedTime( engagementTimestamp );
        Timestamp currentTimestamp = new Timestamp( System.currentTimeMillis() );
        survey.setLastReminderTime( currentTimestamp );
        survey.setCreatedOn( currentTimestamp );
        survey.setModifiedOn( currentTimestamp );
        survey.setStatus( CommonConstants.STATUS_SURVEYPREINITIATION_COMPLETE );
        survey.setCity( surveyImportVO.getCity() );
        survey.setState( surveyImportVO.getState() );
        surveyPreInitiationDao.save( survey );
        LOG.debug( "Method BulkSurveyImporter.importSurveyVOToSurveyPreInitiation finished" );
        return survey;
    }


    SurveyDetails importSurveyVOToMongo( SurveyImportVO surveyImportVO, SurveyPreInitiation surveyPreInitiation, User user,
        String source ) throws InvalidInputException, ProfileNotFoundException
    {
        SurveyDetails surveyDetails = new SurveyDetails();
        surveyDetails.setAgentId( user.getUserId() );
        String agentName = user.getFirstName();
        if ( user.getLastName() != null && !user.getLastName().isEmpty() )
            agentName += " " + user.getLastName();
        surveyDetails.setAgentName( agentName );
        Map<String, Long> profile = userManagementService.getPrimaryUserProfileByAgentId( user.getUserId() );
        surveyDetails.setBranchId( profile.get( CommonConstants.BRANCH_ID_COLUMN ) );
        surveyDetails.setCustomerFirstName( surveyImportVO.getCustomerFirstName() );
        String lastName = surveyImportVO.getCustomerLastName();
        if ( lastName != null && !lastName.isEmpty() && !lastName.equalsIgnoreCase( "null" ) )
            surveyDetails.setCustomerLastName( lastName );
        surveyDetails.setCompanyId( user.getCompany().getCompanyId() );
        surveyDetails.setCustomerEmail( surveyImportVO.getCustomerEmailAddress() );
        surveyDetails.setRegionId( profile.get( CommonConstants.REGION_ID_COLUMN ) );
        surveyDetails.setStage( CommonConstants.SURVEY_STAGE_COMPLETE );
        surveyDetails.setReminderCount( 0 );
        surveyDetails.setModifiedOn( new Date( System.currentTimeMillis() ) );
        surveyDetails.setCreatedOn( surveyImportVO.getSurveyDate() );
        surveyDetails.setSurveyResponse( new ArrayList<SurveyResponse>() );
        surveyDetails.setCustRelationWithAgent( null );
        surveyDetails.setReview( surveyImportVO.getReview() );
        surveyDetails.setScore( surveyImportVO.getScore() );
        surveyDetails.setSurveyTransactionDate( surveyImportVO.getSurveyDate() );
        surveyDetails.setSurveyCompletedDate( surveyImportVO.getSurveyDate() );
        surveyDetails.setSurveyUpdatedDate( surveyImportVO.getSurveyDate() );

        surveyDetails.setUrl(
            composeLink( user.getUserId(), surveyImportVO.getCustomerEmailAddress(), surveyImportVO.getCustomerFirstName(),
                surveyImportVO.getCustomerLastName(), surveyPreInitiation.getSurveyPreIntitiationId(), false ) );
        surveyDetails.setEditable( false );
        surveyDetails.setSource( source );
        surveyDetails.setSourceId( surveyImportVO.getSurveySourceId() );
        surveyDetails.setShowSurveyOnUI( true );
        surveyDetails.setCity( surveyImportVO.getCity() );
        surveyDetails.setState( surveyImportVO.getState() );

        surveyDetails.setRetakeSurvey( false );
        surveyDetails.setSurveyPreIntitiationId( surveyPreInitiation.getSurveyPreIntitiationId() );
        surveyDetailsDao.insertSurveyDetails( surveyDetails );
        return surveyDetails;
    }


    @Override
    public void updateZillowSummaryInExistingSurveyDetails( SurveyDetails surveyDetails )
    {
        surveyDetailsDao.updateZillowSummaryInExistingSurveyDetails( surveyDetails );
    }


    @Override
    public void updateZillowSourceIdInExistingSurveyDetails( SurveyDetails surveyDetails )
    {
        surveyDetailsDao.updateZillowSourceIdInExistingSurveyDetails( surveyDetails );
    }


    @Override
    public void updateZillowSurveyUpdatedDateInExistingSurveyDetails( SurveyDetails surveyDetails )
    {
        surveyDetailsDao.updateZillowSurveyUpdatedDateInExistingSurveyDetails( surveyDetails );
    }


    @Override
    @Transactional
    public boolean hasCustomerAlreadySurveyed( long currentAgentId, String customerEmailId )
    {
        int duplicateSurveyInterval = 0;
        User currentAgent = userDao.findById( User.class, currentAgentId );
        if ( currentAgent != null && currentAgent.getCompany() != null ) {
            OrganizationUnitSettings companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(
                currentAgent.getCompany().getCompanyId(), MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
            if ( companySettings != null && companySettings.getSurvey_settings() != null
                && companySettings.getSurvey_settings().getDuplicateSurveyInterval() > 0 ) {
                duplicateSurveyInterval = companySettings.getSurvey_settings().getDuplicateSurveyInterval();
            } else {
                duplicateSurveyInterval = defaultSurveyRetakeInterval;
            }
        }

        // check if incomplete survey exist
        List<SurveyPreInitiation> incompleteSurveyCustomers = null;
        if ( duplicateSurveyInterval > 0 ) {
            incompleteSurveyCustomers = surveyPreInitiationDao.getSurveyByAgentIdAndCustomeEmailForPastNDays( currentAgentId,
                customerEmailId, duplicateSurveyInterval );
        } else {
            incompleteSurveyCustomers = surveyPreInitiationDao.getSurveyByAgentIdAndCustomeEmail( currentAgentId,
                customerEmailId );
        }

        // check if completed survey exist
        SurveyDetails survey;
        if ( duplicateSurveyInterval > 0 ) {
            survey = surveyDetailsDao.getSurveyByAgentIdAndCustomerEmailAndNoOfDays( currentAgentId, customerEmailId, null,
                null, duplicateSurveyInterval );
        } else {
            survey = surveyDetailsDao.getSurveyByAgentIdAndCustomerEmail( currentAgentId, customerEmailId, null, null );
        }
        if ( survey != null || ( incompleteSurveyCustomers != null && incompleteSurveyCustomers.size() > 0 ) ) {
            return true;
        }
        return false;
    }


    @Override
    public String replaceGatewayQuestionText( String questionText, OrganizationUnitSettings agentSettings, User user,
        OrganizationUnitSettings companySettings, SurveyDetails survey, String logoUrl,
        Map<SettingsForApplication, OrganizationUnit> mapPrimaryHierarchy, OrganizationUnitSettings rSettings,
        OrganizationUnitSettings bSettings, Map<String, String> surveyMap ) throws InvalidInputException
    {
        LOG.debug( "Method replaceGateQuestionText started" );
        if ( user == null ) {
            LOG.error( "User cannot be null" );
            throw new InvalidInputException( "User cannot be null" );
        }
        if ( StringUtils.isEmpty( questionText ) ) {
            LOG.error( "Gateway question empty" );
            throw new InvalidInputException( "Gateway question empty" );
        }
        if ( agentSettings == null ) {
            LOG.error( "Agent Settings cannot be null" );
            throw new InvalidInputException( "Agent Settings cannot be null" );
        }
        if ( companySettings == null ) {
            LOG.error( "Company Settings cannot be null" );
            throw new InvalidInputException( "Company Settings cannot be null" );
        }
        if ( survey == null ) {
            LOG.error( "Survey details cannot be null" );
            throw new InvalidInputException( "Survey details cannot be null" );
        }

        DateFormat dateFormat = new SimpleDateFormat( "yyyy/MM/dd" );
        String agentDisclaimer = "";
        String agentLicenses = "";
        String companyDisclaimer = "";

        if ( companySettings != null && companySettings.getDisclaimer() != null )
            companyDisclaimer = companySettings.getDisclaimer();

        if ( agentSettings.getDisclaimer() != null )
            agentDisclaimer = agentSettings.getDisclaimer();

        if ( agentSettings.getLicenses() != null && agentSettings.getLicenses().getAuthorized_in() != null ) {
            agentLicenses = StringUtils.join( agentSettings.getLicenses().getAuthorized_in(), ',' );
        }
        String currentYear = String.valueOf( Calendar.getInstance().get( Calendar.YEAR ) );
        String agentPhone = "";
        if ( agentSettings.getContact_details() != null && agentSettings.getContact_details().getContact_numbers() != null
            && agentSettings.getContact_details().getContact_numbers().getWork() != null ) {
            agentPhone = agentSettings.getContact_details().getContact_numbers().getWork();
        }
        String agentTitle = "";
        if ( agentSettings.getContact_details() != null && agentSettings.getContact_details().getTitle() != null ) {
            agentTitle = agentSettings.getContact_details().getTitle();
        }
        String agentSignature = emailFormatHelper.buildAgentSignature( survey.getAgentName(), agentPhone, agentTitle,
            user.getCompany().getCompany() );

        String retStr = emailFormatHelper.replaceLegendsWithSettings( false, questionText, getApplicationBaseUrl(), logoUrl,
            null, survey.getCustomerFirstName(), survey.getCustomerLastName(), survey.getAgentName(), agentSignature,
            survey.getCustomerEmail(), user.getEmailId(), user.getCompany().getCompany(), dateFormat.format( new Date() ),
            currentYear, "", "", user.getProfileName(), companyDisclaimer, agentDisclaimer, agentLicenses, user, agentSettings,
            bSettings, rSettings, companySettings, mapPrimaryHierarchy, surveyMap );

        LOG.debug( "Method replaceGateQuestionText finished for User : " + user.getUserId() );

        return retStr;
    }


    /**
     * 
     * @param user
     * @param agentSettings
     * @return
     */
    @Override
    public String getLogoUrl( User user, AgentSettings agentSettings )
    {
        if ( user == null ) {
            LOG.error( "User cannot be null" );
            return null;
        }
        LOG.debug( "Method getLogoUrl started for user : " + user.getUserId() );
        Map<String, Long> hierarchyMap = null;
        Map<SettingsForApplication, OrganizationUnit> map = null;
        String logoUrl = null;
        try {
            map = profileManagementService.getPrimaryHierarchyByEntity( CommonConstants.AGENT_ID_COLUMN, user.getUserId() );
            if ( map == null ) {
                LOG.error( "Unable to fetch primary profile for this user " );
                throw new FatalException( "Unable to fetch primary profile this user " + user.getUserId() );
            }
            hierarchyMap = profileManagementService.getPrimaryHierarchyByAgentProfile( agentSettings );

            long companyId = hierarchyMap.get( CommonConstants.COMPANY_ID_COLUMN );
            long regionId = hierarchyMap.get( CommonConstants.REGION_ID_COLUMN );
            long branchId = hierarchyMap.get( CommonConstants.BRANCH_ID_COLUMN );
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
                    LOG.error( e.getMessage() );
                }
                if ( branchSettings != null ) {
                    logoUrl = branchSettings.getLogo();
                }
            } else if ( organizationUnit == OrganizationUnit.AGENT ) {
                logoUrl = agentSettings.getLogo();
            }
        } catch ( InvalidInputException e ) {
            LOG.error( "An InvalidInputException occurred while fetching logo url. Reason : ", e );
        } catch ( ProfileNotFoundException e ) {
            LOG.error( "A ProfileNotFounnException occurred while fetching logo url. Reason : ", e );
        } catch ( InvalidSettingsStateException e ) {
            LOG.error( "An InvalidSettingsStateException occurred while fetching logo url. Reason : ", e );
        }
        LOG.debug( "Method getLogoUrl finished for user : " + user.getUserId() );
        return logoUrl;
    }


    @Override
    public String getLogoUrlWithSettings( User user, AgentSettings agentSettings, OrganizationUnitSettings companySettings,
        OrganizationUnitSettings regionSettings, OrganizationUnitSettings branchSettings,
        Map<SettingsForApplication, OrganizationUnit> map )
    {
        if ( user == null ) {
            LOG.error( "User cannot be null" );
            return null;
        }
        LOG.debug( "Method getLogoUrl started for user : " + user.getUserId() );
        String logoUrl = null;
        if ( map == null ) {
            LOG.error( "Unable to fetch primary profile for this user " );
            throw new FatalException( "Unable to fetch primary profile this user " + user.getUserId() );
        }
        OrganizationUnit organizationUnit = map.get( SettingsForApplication.LOGO );
        if ( organizationUnit == OrganizationUnit.COMPANY && companySettings != null ) {
            logoUrl = companySettings.getLogo();
        } else if ( organizationUnit == OrganizationUnit.REGION && regionSettings != null ) {
            logoUrl = regionSettings.getLogo();
        } else if ( organizationUnit == OrganizationUnit.BRANCH && branchSettings != null ) {
            logoUrl = branchSettings.getLogo();
        } else if ( organizationUnit == OrganizationUnit.AGENT && agentSettings != null ) {
            logoUrl = agentSettings.getLogo();
        }
        LOG.debug( "Method getLogoUrl finished for user : " + user.getUserId() );
        return logoUrl;
    }


    /**
     * 
     * @param unitSettings
     * @param branchSettings
     * @param regionSettings
     * @param companySettings
     * @param surveyAndStage
     */
    @Override
    public void updateSurveyStageForYelp( OrganizationUnitSettings unitSettings, BranchSettings branchSettings,
        OrganizationUnitSettings regionSettings, OrganizationUnitSettings companySettings, Map<String, Object> surveyAndStage )
    {
        // Fetching Yelp Url
        try {
            if ( unitSettings != null && unitSettings.getSocialMediaTokens() != null
                && unitSettings.getSocialMediaTokens().getYelpToken() != null
                && unitSettings.getSocialMediaTokens().getYelpToken().getYelpPageLink() != null ) {
                surveyAndStage.put( "yelpEnabled", true );
                surveyAndStage.put( "yelpLink", unitSettings.getSocialMediaTokens().getYelpToken().getYelpPageLink() );
            } else {
                // Adding Yelp Url of the closest in hierarchy connected with Yelp.
                if ( branchSettings != null && branchSettings.getOrganizationUnitSettings() != null
                    && branchSettings.getOrganizationUnitSettings().getSocialMediaTokens() != null
                    && branchSettings.getOrganizationUnitSettings().getSocialMediaTokens().getYelpToken() != null
                    && branchSettings.getOrganizationUnitSettings().getSocialMediaTokens().getYelpToken()
                        .getYelpPageLink() != null ) {
                    surveyAndStage.put( "yelpEnabled", true );
                    surveyAndStage.put( "yelpLink",
                        branchSettings.getOrganizationUnitSettings().getSocialMediaTokens().getYelpToken().getYelpPageLink() );
                } else if ( regionSettings != null && regionSettings.getSocialMediaTokens() != null
                    && regionSettings.getSocialMediaTokens().getYelpToken() != null
                    && regionSettings.getSocialMediaTokens().getYelpToken().getYelpPageLink() != null ) {
                    surveyAndStage.put( "yelpEnabled", true );
                    surveyAndStage.put( "yelpLink", regionSettings.getSocialMediaTokens().getYelpToken().getYelpPageLink() );
                } else if ( companySettings != null && companySettings.getSocialMediaTokens() != null
                    && companySettings.getSocialMediaTokens().getYelpToken() != null
                    && companySettings.getSocialMediaTokens().getYelpToken().getYelpPageLink() != null ) {
                    surveyAndStage.put( "yelpEnabled", true );
                    surveyAndStage.put( "yelpLink", companySettings.getSocialMediaTokens().getYelpToken().getYelpPageLink() );
                } else
                    surveyAndStage.put( "yelpEnabled", false );
            }
        } catch ( NullPointerException e ) {
            surveyAndStage.put( "yelpEnabled", false );
        }
    }


    @Override
    public void updateSurveyStageForZillow( OrganizationUnitSettings unitSettings, BranchSettings branchSettings,
        OrganizationUnitSettings regionSettings, OrganizationUnitSettings companySettings, Map<String, Object> surveyAndStage )
    {
        // Fetching Zillow Url
        try {
            if ( unitSettings != null && unitSettings.getSocialMediaTokens() != null
                && unitSettings.getSocialMediaTokens().getZillowToken() != null
                && unitSettings.getSocialMediaTokens().getZillowToken().getZillowProfileLink() != null ) {
                surveyAndStage.put( "zillowEnabled", true );
                surveyAndStage.put( "zillowLink", unitSettings.getSocialMediaTokens().getZillowToken().getZillowProfileLink() );
            } else {
                // Adding Zillow Url of the closest in hierarchy connected with Zillow.
                if ( branchSettings != null && branchSettings.getOrganizationUnitSettings() != null
                    && branchSettings.getOrganizationUnitSettings().getSocialMediaTokens() != null
                    && branchSettings.getOrganizationUnitSettings().getSocialMediaTokens().getZillowToken() != null
                    && branchSettings.getOrganizationUnitSettings().getSocialMediaTokens().getZillowToken()
                        .getZillowProfileLink() != null ) {
                    surveyAndStage.put( "zillowEnabled", true );
                    surveyAndStage.put( "zillowLink", branchSettings.getOrganizationUnitSettings().getSocialMediaTokens()
                        .getZillowToken().getZillowProfileLink() );
                } else if ( regionSettings != null && regionSettings.getSocialMediaTokens() != null
                    && regionSettings.getSocialMediaTokens().getZillowToken() != null
                    && regionSettings.getSocialMediaTokens().getZillowToken().getZillowProfileLink() != null ) {
                    surveyAndStage.put( "zillowEnabled", true );
                    surveyAndStage.put( "zillowLink",
                        regionSettings.getSocialMediaTokens().getZillowToken().getZillowProfileLink() );
                } else if ( companySettings != null && companySettings.getSocialMediaTokens() != null
                    && companySettings.getSocialMediaTokens().getZillowToken() != null
                    && companySettings.getSocialMediaTokens().getZillowToken().getZillowProfileLink() != null ) {
                    surveyAndStage.put( "zillowEnabled", true );
                    surveyAndStage.put( "zillowLink",
                        companySettings.getSocialMediaTokens().getZillowToken().getZillowProfileLink() );
                } else
                    surveyAndStage.put( "zillowEnabled", false );
            }
        } catch ( NullPointerException e ) {
            surveyAndStage.put( "zillowEnabled", false );
        }
    }


    @Override
    public void updateSurveyStageForLendingTree( OrganizationUnitSettings unitSettings, BranchSettings branchSettings,
        OrganizationUnitSettings regionSettings, OrganizationUnitSettings companySettings, Map<String, Object> surveyAndStage )
    {
        try {
            if ( unitSettings != null && unitSettings.getSocialMediaTokens() != null
                && unitSettings.getSocialMediaTokens().getLendingTreeToken() != null
                && unitSettings.getSocialMediaTokens().getLendingTreeToken().getLendingTreeProfileLink() != null ) {
                surveyAndStage.put( "lendingtreeEnabled", true );
                surveyAndStage.put( "lendingtreeLink",
                    unitSettings.getSocialMediaTokens().getLendingTreeToken().getLendingTreeProfileLink() );
            } else {
                // Adding LendingTree Url of the closest in hierarchy connected with LendingTree.
                if ( branchSettings != null && branchSettings.getOrganizationUnitSettings() != null
                    && branchSettings.getOrganizationUnitSettings().getSocialMediaTokens() != null
                    && branchSettings.getOrganizationUnitSettings().getSocialMediaTokens().getLendingTreeToken() != null
                    && branchSettings.getOrganizationUnitSettings().getSocialMediaTokens().getLendingTreeToken()
                        .getLendingTreeProfileLink() != null ) {
                    surveyAndStage.put( "lendingtreeEnabled", true );
                    surveyAndStage.put( "lendingtreeLink", branchSettings.getOrganizationUnitSettings().getSocialMediaTokens()
                        .getLendingTreeToken().getLendingTreeProfileLink() );
                } else if ( regionSettings != null && regionSettings.getSocialMediaTokens() != null
                    && regionSettings.getSocialMediaTokens().getLendingTreeToken() != null
                    && regionSettings.getSocialMediaTokens().getLendingTreeToken().getLendingTreeProfileLink() != null ) {
                    surveyAndStage.put( "lendingtreeEnabled", true );
                    surveyAndStage.put( "lendingtreeLink",
                        regionSettings.getSocialMediaTokens().getLendingTreeToken().getLendingTreeProfileLink() );
                } else if ( companySettings != null && companySettings.getSocialMediaTokens() != null
                    && companySettings.getSocialMediaTokens().getLendingTreeToken() != null
                    && companySettings.getSocialMediaTokens().getLendingTreeToken().getLendingTreeProfileLink() != null ) {
                    surveyAndStage.put( "lendingtreeEnabled", true );
                    surveyAndStage.put( "lendingtreeLink",
                        companySettings.getSocialMediaTokens().getLendingTreeToken().getLendingTreeProfileLink() );
                } else
                    surveyAndStage.put( "lendingtreeEnabled", false );
            }
        } catch ( NullPointerException e ) {
            surveyAndStage.put( "lendingtreeEnabled", false );
        }

    }


    @Override
    public void updateSurveyStageForRealtor( OrganizationUnitSettings unitSettings, BranchSettings branchSettings,
        OrganizationUnitSettings regionSettings, OrganizationUnitSettings companySettings, Map<String, Object> surveyAndStage )
    {
        try {
            if ( unitSettings != null && unitSettings.getSocialMediaTokens() != null
                && unitSettings.getSocialMediaTokens().getRealtorToken() != null
                && unitSettings.getSocialMediaTokens().getRealtorToken().getRealtorProfileLink() != null ) {
                surveyAndStage.put( "realtorEnabled", true );

                surveyAndStage.put( "realtorLink",
                    unitSettings.getSocialMediaTokens().getRealtorToken().getRealtorProfileLink() );
            } else {
                // Adding Realtor Url of the closest in hierarchy connected with Realtor.
                if ( branchSettings != null && branchSettings.getOrganizationUnitSettings() != null
                    && branchSettings.getOrganizationUnitSettings().getSocialMediaTokens() != null
                    && branchSettings.getOrganizationUnitSettings().getSocialMediaTokens().getRealtorToken() != null
                    && branchSettings.getOrganizationUnitSettings().getSocialMediaTokens().getRealtorToken()
                        .getRealtorProfileLink() != null ) {
                    surveyAndStage.put( "realtorEnabled", true );
                    surveyAndStage.put( "realtorLink", branchSettings.getOrganizationUnitSettings().getSocialMediaTokens()
                        .getRealtorToken().getRealtorProfileLink() );
                } else if ( regionSettings != null && regionSettings.getSocialMediaTokens() != null
                    && regionSettings.getSocialMediaTokens().getRealtorToken() != null
                    && regionSettings.getSocialMediaTokens().getRealtorToken().getRealtorProfileLink() != null ) {
                    surveyAndStage.put( "realtorEnabled", true );
                    surveyAndStage.put( "realtorLink",
                        regionSettings.getSocialMediaTokens().getRealtorToken().getRealtorProfileLink() );
                } else if ( companySettings != null && companySettings.getSocialMediaTokens() != null
                    && companySettings.getSocialMediaTokens().getRealtorToken() != null
                    && companySettings.getSocialMediaTokens().getRealtorToken().getRealtorProfileLink() != null ) {
                    surveyAndStage.put( "realtorEnabled", true );
                    surveyAndStage.put( "realtorLink",
                        companySettings.getSocialMediaTokens().getRealtorToken().getRealtorProfileLink() );
                } else
                    surveyAndStage.put( "realtorEnabled", false );
            }
        } catch ( NullPointerException e ) {
            surveyAndStage.put( "realtorEnabled", false );
        }

    }


    public void updateSurveyStageForGoogleBusinessToken( OrganizationUnitSettings unitSettings, BranchSettings branchSettings,
        OrganizationUnitSettings regionSettings, OrganizationUnitSettings companySettings, Map<String, Object> surveyAndStage )
    {

        try {
            if ( unitSettings != null && unitSettings.getSocialMediaTokens() != null
                && unitSettings.getSocialMediaTokens().getGoogleBusinessToken() != null
                && unitSettings.getSocialMediaTokens().getGoogleBusinessToken().getGoogleBusinessLink() != null ) {
                surveyAndStage.put( "googleBusinessEnabled", true );
                surveyAndStage.put( "googleBusinessLink",
                    unitSettings.getSocialMediaTokens().getGoogleBusinessToken().getGoogleBusinessLink() );
            } else {
                // Adding Realtor Url of the closest in hierarchy connected with Realtor.
                if ( branchSettings != null && branchSettings.getOrganizationUnitSettings() != null
                    && branchSettings.getOrganizationUnitSettings().getSocialMediaTokens() != null
                    && branchSettings.getOrganizationUnitSettings().getSocialMediaTokens().getGoogleBusinessToken() != null
                    && branchSettings.getOrganizationUnitSettings().getSocialMediaTokens().getGoogleBusinessToken()
                        .getGoogleBusinessLink() != null ) {
                    surveyAndStage.put( "googleBusinessEnabled", true );
                    surveyAndStage.put( "googleBusinessLink", branchSettings.getOrganizationUnitSettings()
                        .getSocialMediaTokens().getGoogleBusinessToken().getGoogleBusinessLink() );
                } else if ( regionSettings != null && regionSettings.getSocialMediaTokens() != null
                    && regionSettings.getSocialMediaTokens().getGoogleBusinessToken() != null
                    && regionSettings.getSocialMediaTokens().getGoogleBusinessToken().getGoogleBusinessLink() != null ) {
                    surveyAndStage.put( "googleBusinessEnabled", true );
                    surveyAndStage.put( "googleBusinessLink",
                        regionSettings.getSocialMediaTokens().getGoogleBusinessToken().getGoogleBusinessLink() );
                } else if ( companySettings != null && companySettings.getSocialMediaTokens() != null
                    && companySettings.getSocialMediaTokens().getGoogleBusinessToken() != null
                    && companySettings.getSocialMediaTokens().getGoogleBusinessToken().getGoogleBusinessLink() != null ) {
                    surveyAndStage.put( "googleBusinessEnabled", true );
                    surveyAndStage.put( "googleBusinessLink",
                        companySettings.getSocialMediaTokens().getGoogleBusinessToken().getGoogleBusinessLink() );
                } else
                    surveyAndStage.put( "googleBusinessEnabled", false );
            }
        } catch ( NullPointerException e ) {
            surveyAndStage.put( "googleBusinessEnabled", false );
        }
    }


    /**
     * 
     * @param surveyPreIntitiationId
     * @return
     */
    @Override
    public SurveyDetails getSurveyBySurveyPreIntitiationId( long surveyPreIntitiationId )
    {
        LOG.debug( "method getSurveyBySurveyPreIntitiationId started for surveyPreIntitiationId : " + surveyPreIntitiationId );
        SurveyDetails survey = surveyDetailsDao.getSurveyBySurveyPreIntitiationId( surveyPreIntitiationId );

        LOG.debug( "method getSurveyBySurveyPreIntitiationId ended for surveyPreIntitiationId : " + surveyPreIntitiationId );
        return survey;
    }


    /**
     * Method to read the csv file and get a list of SurveyImportVO objects
     * @return
     * @throws InvalidInputException
     */
    private List<SurveyImportVO> getSurveyListFromCsv() throws InvalidInputException
    {
        LOG.debug( "BulkSurveyImporter.getSurveyListFromCsv started" );
        InputStream fileStream = null;
        List<SurveyImportVO> surveyList = new ArrayList<>();
        try {
            fileStream = new FileInputStream( thirdPartySurveyImportPath );
            if ( fileStream == null )
                return null;
            XSSFWorkbook workBook = new XSSFWorkbook( fileStream );
            XSSFSheet regionSheet = workBook.getSheetAt( 0 );
            Iterator<Row> rows = regionSheet.rowIterator();
            Iterator<Cell> cells = null;
            XSSFRow row = null;
            XSSFCell cell = null;
            while ( rows.hasNext() ) {
                row = (XSSFRow) rows.next();
                // skip the first 1 row for the header
                if ( row.getRowNum() < 1 ) {
                    continue;
                }
                SurveyImportVO survey = new SurveyImportVO();
                survey.setCustomerEmailAddress( DEFAULT_CUSTOMER_EMAIL_ID_FOR_3RD_PARTY );
                LOG.debug( "Processing row " + row.getRowNum() + " from the file." );
                cells = row.cellIterator();
                while ( cells.hasNext() ) {
                    cell = (XSSFCell) cells.next();
                    if ( cell.getColumnIndex() == USER_EMAIL_ID_INDEX ) {
                        if ( !cell.getStringCellValue().trim().isEmpty() ) {
                            survey.setUserEmailId( cell.getStringCellValue().trim() );
                        }
                    } else if ( cell.getColumnIndex() == CUSTOMER_FIRSTNAME_INDEX
                        && !cell.getStringCellValue().trim().isEmpty() ) {
                        survey.setCustomerFirstName( cell.getStringCellValue().trim() );
                    } else if ( cell.getColumnIndex() == CUSTOMER_LASTNAME_INDEX
                        && !cell.getStringCellValue().trim().isEmpty() ) {
                        survey.setCustomerLastName( cell.getStringCellValue().trim() );
                    } else if ( cell.getColumnIndex() == CUSTOMER_EMAIL_INDEX ) {
                        String emailId = DEFAULT_CUSTOMER_EMAIL_ID_FOR_3RD_PARTY;
                        if ( !cell.getStringCellValue().trim().isEmpty() ) {
                            emailId = cell.getStringCellValue().trim();
                        }
                        //Mask email address if required
                        if ( CommonConstants.YES_STRING.equals( maskEmail ) ) {
                            emailId = utils.maskEmailAddress( emailId );
                        }
                        survey.setCustomerEmailAddress( emailId );
                    } else if ( cell.getColumnIndex() == SURVEY_COMPLETION_INDEX ) {
                        survey.setSurveyDate( cell.getDateCellValue() );
                    } else if ( cell.getColumnIndex() == SCORE_INDEX ) {
                        survey.setScore( cell.getNumericCellValue() );
                    } else if ( cell.getColumnIndex() == SURVEY_SOURCE_ID_INDEX ) {
                        survey.setSurveySourceId( cell.getStringCellValue().trim() );
                    } else if ( cell.getColumnIndex() == COMMENT_INDEX && !cell.getStringCellValue().trim().isEmpty() ) {
                        survey.setReview( cell.getStringCellValue() );
                    } else if ( cell.getColumnIndex() == CITY_INDEX && !cell.getStringCellValue().trim().isEmpty() ) {
                        survey.setCity( cell.getStringCellValue() );
                    } else if ( cell.getColumnIndex() == STATE_INDEX && !cell.getStringCellValue().trim().isEmpty() ) {
                        survey.setState( cell.getStringCellValue() );
                    }
                }
                try {
                    //If object empty, get out of loop;
                    if ( isObjectEmpty( survey ) ) {
                        break;
                    }
                    resolveEmailAddress( survey );
                    validateSurveyRow( survey );
                    surveyList.add( survey );
                } catch ( InvalidInputException e ) {
                    LOG.error( "Error occurred at row : " + row.getRowNum() + ". Reason : " + e.getMessage() );
                }
            }
            fileStream.close();
            //Delete file from location
            File surveyFile = new File( thirdPartySurveyImportPath );
            if ( surveyFile.delete() )
                LOG.debug( "Successfully deleted the third party import file" );
            else
                LOG.error( "Failed to delete third party import file." );
        } catch ( IOException e ) {
            LOG.error( "An IOException occurred while importing. Reason: ", e );
        }
        LOG.debug( "BulkSurveyImporter.getSurveyListFromCsv finished" );
        return surveyList;
    }


    private boolean isObjectEmpty( SurveyImportVO survey )
    {
        if ( ( survey.getCustomerEmailAddress() == null || survey.getCustomerEmailAddress().isEmpty() )
            && ( survey.getUserEmailId() == null || survey.getUserEmailId().isEmpty() ) ) {
            return true;
        }
        return false;
    }


    private void resolveEmailAddress( SurveyImportVO survey ) throws InvalidInputException
    {
        if ( survey.getCustomerEmailAddress() == null || survey.getCustomerEmailAddress().isEmpty() ) {
            throw new InvalidInputException( "customer email address cannot be null" );
        }
        try {
            User user = userManagementService.getUserByEmailAddress( survey.getUserEmailId() );
            survey.setUserId( user.getUserId() );
        } catch ( NoRecordsFetchedException e ) {
            throw new InvalidInputException( "No user found with the email address : " + survey.getUserEmailId() );
        }
    }


    private static void validateSurveyRow( SurveyImportVO survey ) throws InvalidInputException
    {
        if ( survey.getUserId() <= 0 )
            throw new InvalidInputException( "Invalid userId : " + survey.getUserId() );
        if ( survey.getCustomerEmailAddress() == null || survey.getCustomerEmailAddress().isEmpty() )
            throw new InvalidInputException( "Customer Email Address cannot be empty" );
        if ( survey.getCustomerFirstName() == null || survey.getCustomerFirstName().isEmpty() )
            throw new InvalidInputException( "Customer First Name cannot be empty" );
        if ( survey.getReview() == null || survey.getReview().isEmpty() )
            throw new InvalidInputException( "Review cannot be empty" );
        if ( survey.getSurveyDate() == null )
            throw new InvalidInputException( "Survey date cannot be empty" );
        if ( survey.getScore() < 0.0 || survey.getScore() > 5.0 )
            throw new InvalidInputException( "Invalid survey score : " + survey.getScore() );
    }


    @Override
    public void begin3rdPartySurveyImport()
    {
        LOG.debug( "3rd party Survey Importer started" );
        try {
            List<SurveyImportVO> surveyImportVOs = getSurveyListFromCsv();
            if ( surveyImportVOs != null && !surveyImportVOs.isEmpty() ) {
                for ( SurveyImportVO surveyImportVO : surveyImportVOs ) {
                    try {
                        importSurveyVOToDBs( surveyImportVO, CommonConstants.SURVEY_SOURCE_3RD_PARTY );
                    } catch ( Exception e ) {
                        LOG.error( "Error occurred while processing each survey. Reason :", e );
                    }
                }
            }
        } catch ( NonFatalException e ) {
            LOG.error( "An error occurred while uploading the surveys. Reason: ", e );
        }
        LOG.debug( "3rd party Survey Importer finished" );
    }


    /*
     * (non-Javadoc)
     * @see com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler#getSurveysByFilterCriteria(java.lang.String, java.lang.String, java.util.Date, java.util.Date, java.util.List, int, int, long)
     */
    @Override
    @Transactional
    public SurveysAndReviewsVO getSurveysByFilterCriteria( String status, String mood, Long startSurveyID, Date startReviewDate,
        Date startTransactionDate, List<Long> userIds, int startIndex, int count, long companyId )
    {
        LOG.debug( "method getSurveysByStatus started for companyId " + companyId );

        //get mongo survey count
        int mongoSurveyCount = getSurveyCountForCompanyBySurveyStatus( companyId, status, mood, startSurveyID, startReviewDate,
            startTransactionDate, userIds );
        int endIndex = startIndex + count;

        //get start index and batch size for mongo and sql get survey query
        Map<String, Integer> startindexBatchSizeMap = getStartIndexAndBatchForMonogAndSqlQuery( startIndex, endIndex,
            mongoSurveyCount );
        int monogStartIndex = startindexBatchSizeMap.get( "monogStartIndex" );
        int mongoBatch = startindexBatchSizeMap.get( "mongoBatch" );
        int sqlStartIndex = startindexBatchSizeMap.get( "sqlStartIndex" );
        int sqlBatch = startindexBatchSizeMap.get( "sqlBatch" );

        //get survey from mongo
        List<SurveyDetails> surveyDetails = null;
        if ( mongoBatch > 0 )
            surveyDetails = getSurveysForCompanyBySurveyStatus( companyId, status, monogStartIndex, mongoBatch, mood,
                startSurveyID, startReviewDate, startTransactionDate, userIds );
        else
            surveyDetails = new ArrayList<SurveyDetails>();

        //get corresponding pre initiated record from my sql
        Map<SurveyDetails, SurveyPreInitiation> surveyReviewMap = getPreinititatedSurveyForMongoSurveyDetail( surveyDetails );

        //get corresponding users
        getUsersForMongoSurveyDetail( surveyDetails );

        //get pre initiated survey from sql
        List<SurveyPreInitiation> preInitiatedSurveys = null;
        if ( !status.equals( CommonConstants.SURVEY_API_SURVEY_STATUS_COMPLETE ) && sqlBatch > 0 && StringUtils.isEmpty( mood )
            && startReviewDate == null ) {
            Timestamp startEngagementClosedTime = null;
            if ( startTransactionDate != null )
                startEngagementClosedTime = new Timestamp( startTransactionDate.getTime() );
            preInitiatedSurveys = surveyPreInitiationDao.getPreInitiatedSurveyForCompanyByCriteria( sqlStartIndex, sqlBatch,
                userIds, startSurveyID, startEngagementClosedTime, companyId );
        } else {
            preInitiatedSurveys = new ArrayList<SurveyPreInitiation>();
        }


        SurveysAndReviewsVO surveyAndReviews = new SurveysAndReviewsVO();
        surveyAndReviews.setInitiatedSurveys( surveyReviewMap );
        surveyAndReviews.setPreInitiatedSurveys( preInitiatedSurveys );
        LOG.debug( "method getSurveysByStatus ended for companyId " + companyId );
        return surveyAndReviews;
    }


    /**
     * 
     * @param companyId
     * @param status
     * @param mood
     * @param startReviewDate
     * @param startTransactionDate
     * @param userIds
     * @return
     */
    private int getSurveyCountForCompanyBySurveyStatus( long companyId, String status, String mood, Long startSurveyID,
        Date startReviewDate, Date startTransactionDate, List<Long> userIds )
    {
        LOG.debug( "method getSurveyCountForCompanyBySurveyStatus started for companyId %s , status %s ", companyId, status );

        long mongoSurveyCount = surveyDetailsDao.getFilteredSurveyCount( companyId, status, mood, startSurveyID,
            startReviewDate, startTransactionDate, userIds );

        LOG.debug( "method getSurveyCountForCompanyBySurveyStatus ended for companyId %s , status %s ", companyId, status );

        return (int) mongoSurveyCount;
    }


    /**
     * 
     * @param companyId
     * @param status
     * @param start
     * @param batchSize
     * @return
     */
    private List<SurveyDetails> getSurveysForCompanyBySurveyStatus( long companyId, String status, int start, int batchSize,
        String mood, Long startSurveyID, Date startReviewDate, Date startTransactionDate, List<Long> userIds )
    {
        LOG.debug(
            "method getSurveysForCompanyBySurveyStatus started for companyId %s , startIndex %s , batchSize %s , status %s ",
            companyId, start, batchSize, status );
        List<SurveyDetails> surveyDetails = surveyDetailsDao.getFilteredSurveys( start, batchSize, companyId, status, mood,
            startSurveyID, startReviewDate, startTransactionDate, userIds );

        LOG.debug(
            "method getSurveysForCompanyBySurveyStatus ended for companyId %s , startIndex %s , batchSize %s , status %s ",
            companyId, start, batchSize, status );
        return surveyDetails;
    }


    /**
     * 
     * @param surveyDetails
     * @return
     */
    private Map<SurveyDetails, SurveyPreInitiation> getPreinititatedSurveyForMongoSurveyDetail(
        List<SurveyDetails> surveyDetails )
    {
        LOG.debug( "method getPreinititatedSurveyForMongoSurveyDetail started" );
        Map<SurveyDetails, SurveyPreInitiation> surveyReviewMap = new LinkedHashMap<SurveyDetails, SurveyPreInitiation>();
        List<Long> surveyPreinitiationIds = new ArrayList<Long>();

        for ( SurveyDetails surveyDetail : surveyDetails ) {
            surveyPreinitiationIds.add( surveyDetail.getSurveyPreIntitiationId() );
        }

        if ( surveyPreinitiationIds.size() > 0 ) {
            Map<Long, SurveyPreInitiation> surveyPreinitiations = surveyPreInitiationDao
                .getPreInitiatedSurveyForIds( surveyPreinitiationIds );
            for ( SurveyDetails surveyDetail : surveyDetails ) {
                surveyReviewMap.put( surveyDetail, surveyPreinitiations.get( surveyDetail.getSurveyPreIntitiationId() ) );
            }
        }


        LOG.debug( "method getPreinititatedSurveyForMongoSurveyDetail ended" );
        return surveyReviewMap;
    }


    private Map<String, Integer> getStartIndexAndBatchForMonogAndSqlQuery( int startIndex, int endIndex, int mongoSurveyCount )
    {
        LOG.debug( "method getStartIndexAndBatchForMonogAndSqlQuery started " );
        Map<String, Integer> startindexBatchSizeMap = new HashMap<String, Integer>();
        int monogStartIndex = 0;
        int mongoBatch = 0;
        int sqlStartIndex = 0;
        int sqlBatch = 0;

        if ( mongoSurveyCount <= startIndex ) {
            //get data from mysql only
            sqlStartIndex = startIndex - mongoSurveyCount;
            sqlBatch = endIndex - mongoSurveyCount;
        } else if ( mongoSurveyCount > startIndex && mongoSurveyCount < endIndex ) {
            //get data from mongo and my sql
            monogStartIndex = startIndex;
            mongoBatch = mongoSurveyCount - startIndex;
            sqlStartIndex = 0;
            sqlBatch = endIndex - mongoSurveyCount;
        } else {
            //get data from mongo only
            monogStartIndex = startIndex;
            mongoBatch = endIndex - startIndex;
        }

        startindexBatchSizeMap.put( "monogStartIndex", monogStartIndex );
        startindexBatchSizeMap.put( "mongoBatch", mongoBatch );
        startindexBatchSizeMap.put( "sqlStartIndex", sqlStartIndex );
        startindexBatchSizeMap.put( "sqlBatch", sqlBatch );

        LOG.debug( "method getStartIndexAndBatchForMonogAndSqlQuery ended " );
        return startindexBatchSizeMap;
    }


    private void getUsersForMongoSurveyDetail( List<SurveyDetails> surveyDetails )
    {
        LOG.debug( "method getUsersForMongoSurveyDetail started" );
        List<Long> userIds = new ArrayList<Long>();

        for ( SurveyDetails surveyDetail : surveyDetails ) {
            userIds.add( surveyDetail.getAgentId() );
        }

        List<User> users = new ArrayList<User>();
        try {
            users = userDao.getUsersForUserIds( userIds );
        } catch ( InvalidInputException e ) {

        }

        //create map
        Map<Long, String> userIdsAndEmails = new HashMap<Long, String>();
        for ( User user : users ) {
            userIdsAndEmails.put( user.getUserId(), user.getEmailId() );
        }

        int i = 0;
        for ( SurveyDetails surveyDetail : surveyDetails ) {
            surveyDetail.setAgentEmailId( userIdsAndEmails.get( surveyDetail.getAgentId() ) );
            surveyDetails.set( i, surveyDetail );
            i++;
        }

        LOG.debug( "method getUsersForMongoSurveyDetail ended" );

    }


    @Override
    @Transactional
    public void updateSurveyTransactionDateInMongo()
    {
        try {
            int batch = 1000;
            int count = 0;
            List<SurveyDetails> surveys = null;
            do {
                surveys = surveyDetailsDao.getAllSurveys( count, batch );
                LOG.info( "Number of reveiws fetched: " + surveys.size() );
                List<Long> surveyPreInitiationIds = new ArrayList<Long>();
                List<SurveyDetails> surveys1 = new ArrayList<SurveyDetails>();
                List<SurveyDetails> surveys2 = new ArrayList<SurveyDetails>();
                for ( SurveyDetails survey : surveys ) {
                    if ( survey.getSurveyPreIntitiationId() == 0 ) {
                        surveys2.add( survey );
                    } else {
                        surveys1.add( survey );
                        surveyPreInitiationIds.add( survey.getSurveyPreIntitiationId() );
                    }
                }

                LOG.info( "Number of reveiws fetched in surveys1: " + surveys1.size() );
                LOG.info( "Number of reveiws fetched in surveys2: " + surveys2.size() );

                if ( surveys1 != null && surveys1.size() > 0 ) {
                    Map<Long, SurveyPreInitiation> surveyPreInitiations = surveyPreInitiationDao
                        .getPreInitiatedSurveyForIds( surveyPreInitiationIds );

                    for ( SurveyDetails survey : surveys1 ) {
                        if ( surveyPreInitiations.get( survey.getSurveyPreIntitiationId() ) != null ) {
                            Timestamp engagementClosedTime = surveyPreInitiations.get( survey.getSurveyPreIntitiationId() )
                                .getEngagementClosedTime();
                            if ( engagementClosedTime != null ) {
                                survey.setSurveyTransactionDate( engagementClosedTime );
                                surveyDetailsDao.updateTransactionDateInExistingSurveyDetails( survey );
                            }
                        }
                    }
                }


                for ( SurveyDetails survey : surveys2 ) {
                    List<SurveyPreInitiation> spis = surveyPreInitiationDao
                        .getSurveyByAgentIdAndCustomeEmail( survey.getAgentId(), survey.getCustomerEmail() );
                    if ( spis.size() == 1 ) {
                        if ( spis.get( 0 ).getEngagementClosedTime() != null ) {
                            survey.setSurveyTransactionDate( spis.get( 0 ).getEngagementClosedTime() );
                            surveyDetailsDao.updateTransactionDateInExistingSurveyDetails( survey );
                        }
                    }
                }
                count = count + surveys.size();
            } while ( batch == surveys.size() );
            emailServices.sendCustomMail( applicationAdminName, applicationAdminEmail,
                "SurveySourceIdUpdater executed successfully.", "SurveySourceIdUpdater executed successfully.", null );
        } catch ( Exception exception ) {
            String stackTrace = ExceptionUtils.getStackTrace( exception );
            if ( stackTrace != null )
                stackTrace.replaceAll( "\n", "<br>" );
            String errMsg = exception.getMessage();
            try {
                emailServices.sendCustomMail( applicationAdminName, applicationAdminEmail, "SurveySourceIdUpdater failed",
                    stackTrace + errMsg, null );
            } catch ( InvalidInputException e ) {
                LOG.error( "Error in sending error mail for SurveySourceIdUpdater. " + e.getMessage() );
            } catch ( UndeliveredEmailException e ) {
                LOG.error( "Error in sending error mail for SurveySourceIdUpdater. " + e.getMessage() );
            }
        }
    }


    /**
     * 
     */
    @Override
    public Map<String, Date> getMinMaxLastSurveyReminderTime( long systemTime, int reminderInterval )
    {

        LOG.debug( "method getMinMaxLastSurveyReminderTime started" );
        Date minLastReminderTime = new Date( systemTime - maxSurveyReminderInterval * ( 1000 * 60 * 60 * 24 ) );
        Date maxLastReminderTime = new Date( systemTime - reminderInterval * ( 1000 * 60 * 60 * 24 ) );


        Map<String, Date> minMaxLastReminderTime = new HashMap<String, Date>();
        minMaxLastReminderTime.put( "minLastReminderTime", minLastReminderTime );
        minMaxLastReminderTime.put( "maxLastReminderTime", maxLastReminderTime );

        LOG.debug( "method getMinMaxLastSurveyReminderTime ended" );

        return minMaxLastReminderTime;
    }


    /**
     * @throws InvalidInputException 
     * 
     */
    @Override
    @Transactional
    public void moveAllSurveysAlongWithUser( long agentId, long branchId, long regionId, long companyId )
        throws InvalidInputException
    {

        LOG.info( "Method moveAllSurveysAlongWithUser() started for user  " + agentId );
        User user = userManagementService.getUserByUserId( agentId );
        surveyDetailsDao.moveSurveysAlongWithUser( agentId, branchId, regionId, companyId );
        surveyPreInitiationDao.updateCompanyIdForAllRecordsForAgent( user.getEmailId(), companyId );
        LOG.info( "Method moveSurveysAlongWithUser finished." );

    }


    /**
     * @throws InvalidInputException 
     * 
     */
    @Override
    @Transactional
    public void disconnectAllSurveysFromWithUser( long agentId ) throws InvalidInputException
    {

        LOG.info( "Method disconnectAllSurveysFromWithUser() started for user  " + agentId );
        surveyDetailsDao.disconnectSurveysFromWithUser( agentId );
        surveyPreInitiationDao.disconnectSurveysFromAgent( agentId );
        LOG.info( "Method disconnectAllSurveysFromWithUser finished." );

    }


    /**
     * @throws InvalidInputException 
     * 
     */
    @Override
    @Transactional
    public void copyAllSurveysAlongWithUser( long agentId, long branchId, long regionId, long companyId )
        throws InvalidInputException
    {

        LOG.info( "Method copyAllSurveysAlongWithUser() started for user  " + agentId );
        User user = userManagementService.getUserByUserId( agentId );

        Map<Long, Long> updatedsurveyPreInitiationIdMap = new HashMap<Long, Long>();

        //update mysql
        List<SurveyPreInitiation> existingSurveyPreInitiations = surveyPreInitiationDao.findByColumn( SurveyPreInitiation.class,
            CommonConstants.SURVEY_AGENT_EMAIL_ID_COLUMN, user.getLoginName() );
        for ( SurveyPreInitiation surveyPreInitiation : existingSurveyPreInitiations ) {
            //create new survey preinitiation object with updated data
            if ( surveyPreInitiation.getStatus() == CommonConstants.STATUS_SURVEYPREINITIATION_COMPLETE ) {
                SurveyPreInitiation newSurveyPreInitiation = copySurveyPreinitiationObject( surveyPreInitiation );
                newSurveyPreInitiation.setCompanyId( companyId );
                newSurveyPreInitiation = surveyPreInitiationDao.save( newSurveyPreInitiation );
                //add entry in map to update mongo
                updatedsurveyPreInitiationIdMap.put( surveyPreInitiation.getSurveyPreIntitiationId(),
                    newSurveyPreInitiation.getSurveyPreIntitiationId() );
            }

        }

        //update mongo
        List<SurveyDetails> existingSurveyDetails = surveyDetailsDao.getSurveyDetailsForUser( agentId );
        for ( SurveyDetails surveyDetails : existingSurveyDetails ) {
            //create new survey detail object with updated data
            if ( surveyDetails.getStage() == CommonConstants.SURVEY_STAGE_COMPLETE ) {
                SurveyDetails newSurveyDetails = copySurveyDetailObject( surveyDetails );
                newSurveyDetails.setBranchId( branchId );
                newSurveyDetails.setCompanyId( companyId );
                newSurveyDetails.setRegionId( regionId );
                newSurveyDetails.setSurveyPreIntitiationId(
                    updatedsurveyPreInitiationIdMap.get( surveyDetails.getSurveyPreIntitiationId() ) );
                surveyDetailsDao.insertSurveyDetails( newSurveyDetails );
            }
        }

        LOG.info( "Method copyAllSurveysAlongWithUserk finished." );

    }


    private SurveyPreInitiation copySurveyPreinitiationObject( SurveyPreInitiation surveyPreInitiation )
    {
        SurveyPreInitiation newSurveyPreInitiation = new SurveyPreInitiation();
        newSurveyPreInitiation.setAgentEmailId( surveyPreInitiation.getAgentEmailId() );
        newSurveyPreInitiation.setAgentId( 0l );
        newSurveyPreInitiation.setAgentName( surveyPreInitiation.getAgentName() );
        newSurveyPreInitiation.setCity( surveyPreInitiation.getCity() );
        newSurveyPreInitiation.setCompanyId( surveyPreInitiation.getCompanyId() );
        newSurveyPreInitiation.setCreatedOn( surveyPreInitiation.getCreatedOn() );
        newSurveyPreInitiation.setCustomerEmailId( surveyPreInitiation.getCustomerEmailId() );
        newSurveyPreInitiation.setCustomerFirstName( surveyPreInitiation.getCustomerFirstName() );
        newSurveyPreInitiation.setCustomerInteractionDetails( surveyPreInitiation.getCustomerInteractionDetails() );
        newSurveyPreInitiation.setCustomerLastName( surveyPreInitiation.getCustomerLastName() );
        newSurveyPreInitiation.setEngagementClosedTime( surveyPreInitiation.getEngagementClosedTime() );
        newSurveyPreInitiation.setIsSurveyRequestSent( surveyPreInitiation.getIsSurveyRequestSent() );
        newSurveyPreInitiation.setLastReminderTime( surveyPreInitiation.getLastReminderTime() );
        newSurveyPreInitiation.setModifiedOn( surveyPreInitiation.getModifiedOn() );
        newSurveyPreInitiation.setReminderCounts( surveyPreInitiation.getReminderCounts() );
        newSurveyPreInitiation.setState( surveyPreInitiation.getState() );
        newSurveyPreInitiation.setStatus( surveyPreInitiation.getStatus() );
        newSurveyPreInitiation.setSurveySource( surveyPreInitiation.getSurveySource() );
        newSurveyPreInitiation.setSurveySourceId( surveyPreInitiation.getSurveySourceId() );
        newSurveyPreInitiation.setTransactionType( surveyPreInitiation.getTransactionType() );

        return newSurveyPreInitiation;

    }


    /**
     * 
     * @param surveyDetails
     * @return
     */
    private SurveyDetails copySurveyDetailObject( SurveyDetails surveyDetails )
    {

        SurveyDetails newSurveyDetails = new SurveyDetails();
        newSurveyDetails.setAbuseRepByUser( surveyDetails.isAbuseRepByUser() );
        newSurveyDetails.setAbusive( surveyDetails.isAbusive() );
        newSurveyDetails.setAgentEmailId( surveyDetails.getAgentEmailId() );
        newSurveyDetails.setAgentId( 0l );
        newSurveyDetails.setAgentName( surveyDetails.getAgentName() );
        newSurveyDetails.setAgreedToShare( surveyDetails.getAgreedToShare() );
        newSurveyDetails.setBranchId( surveyDetails.getBranchId() );
        newSurveyDetails.setCity( surveyDetails.getCity() );
        newSurveyDetails.setCompanyId( surveyDetails.getCompanyId() );
        newSurveyDetails.setCompleteProfileUrl( surveyDetails.getCompleteProfileUrl() );
        newSurveyDetails.setCreatedOn( surveyDetails.getCreatedOn() );
        newSurveyDetails.setCustomerEmail( surveyDetails.getCustomerEmail() );
        newSurveyDetails.setCustomerFirstName( surveyDetails.getCustomerFirstName() );
        newSurveyDetails.setCustomerLastName( surveyDetails.getCustomerLastName() );
        newSurveyDetails.setCustRelationWithAgent( surveyDetails.getCustRelationWithAgent() );
        newSurveyDetails.setEditable( surveyDetails.getEditable() );
        newSurveyDetails.setLastReminderForIncompleteSurvey( surveyDetails.getLastReminderForIncompleteSurvey() );
        newSurveyDetails.setLastReminderForSocialPost( surveyDetails.getLastReminderForSocialPost() );
        newSurveyDetails.setModifiedOn( surveyDetails.getModifiedOn() );
        newSurveyDetails.setMood( surveyDetails.getMood() );
        newSurveyDetails.setRegionId( surveyDetails.getRegionId() );
        newSurveyDetails.setReminderCount( surveyDetails.getReminderCount() );
        newSurveyDetails.setRetakeSurvey( surveyDetails.isRetakeSurvey() );
        newSurveyDetails.setReview( surveyDetails.getReview() );
        newSurveyDetails.setScore( surveyDetails.getScore() );
        newSurveyDetails.setShowSurveyOnUI( surveyDetails.isShowSurveyOnUI() );
        newSurveyDetails.setSource( surveyDetails.getSource() );
        newSurveyDetails.setSourceId( surveyDetails.getSourceId() );
        newSurveyDetails.setStage( surveyDetails.getStage() );
        newSurveyDetails.setState( surveyDetails.getState() );
        newSurveyDetails.setSummary( surveyDetails.getSummary() );
        newSurveyDetails.setSurveyCompletedDate( surveyDetails.getSurveyCompletedDate() );
        newSurveyDetails.setSurveyGeoLocation( surveyDetails.getSurveyGeoLocation() );
        newSurveyDetails.setSurveyResponse( surveyDetails.getSurveyResponse() );
        newSurveyDetails.setSurveySentDate( surveyDetails.getSurveySentDate() );
        newSurveyDetails.setSurveyTransactionDate( surveyDetails.getSurveyTransactionDate() );
        newSurveyDetails.setSurveyType( surveyDetails.getSurveyType() );
        newSurveyDetails.setSurveyUpdatedDate( surveyDetails.getSurveyUpdatedDate() );
        newSurveyDetails.setUrl( surveyDetails.getUrl() );

        return newSurveyDetails;
    }


    /**
     * 
     * @param surveyPreInitiations
     * @return
     * @throws InvalidInputException 
     */
    // Method to update agentId in SurveyPreInitiation 
    @Override
    @Transactional
    public List<SurveyPreInitiation> validatePreinitiatedRecord( List<SurveyPreInitiation> surveyPreInitiations )
        throws InvalidInputException
    {

        LOG.debug( "Method processPreinitiatedRecord validatePreinitiatedRecord started " );


        for ( SurveyPreInitiation survey : surveyPreInitiations ) {

            User user = null;
            try {
                user = userManagementService.getActiveAgentByEmailAndCompany( survey.getCompanyId(), survey.getAgentEmailId() );
                survey.setAgentId( user.getUserId() );
            } catch ( InvalidInputException | NoRecordsFetchedException e ) {
                LOG.error( "No user found in database for the email id: " + survey.getAgentEmailId() + " and company id : "
                    + survey.getCompanyId() );
                throw new InvalidInputException(
                    "Can not process the record. No service provider found with email address :  " + survey.getAgentEmailId() );
            }

            // check if survey has already been sent to the email id
            int duplicateSurveyInterval = getDuplicateSurveyIntervalForCompany( user.getCompany().getCompanyId() );
            List<SurveyPreInitiation> incompleteSurveyCustomers = null;
            if ( duplicateSurveyInterval > 0 ) {
                incompleteSurveyCustomers = surveyPreInitiationDao.getSurveyByAgentIdAndCustomeEmailForPastNDays(
                    user.getUserId(), survey.getCustomerEmailId(), duplicateSurveyInterval );
            } else {
                incompleteSurveyCustomers = surveyPreInitiationDao.getSurveyByAgentIdAndCustomeEmail( user.getUserId(),
                    survey.getCustomerEmailId() );
            }

            //get valid survey intervals
            Timestamp engagementClosedTime = survey.getEngagementClosedTime();
            Calendar calendar = Calendar.getInstance();
            calendar.add( Calendar.DATE, -validSurveyInterval );
            Date date = calendar.getTime();

            if ( StringUtils.isEmpty( survey.getCustomerEmailId() )
                || !organizationManagementService.validateEmail( survey.getCustomerEmailId() ) ) {
                LOG.warn( "Invalid Customer Email Id " );
                throw new InvalidInputException(
                    "Can not process the record. Invalid Customer email id : " + survey.getCustomerEmailId() + "" );
            } else if ( incompleteSurveyCustomers != null && incompleteSurveyCustomers.size() > 0 ) {
                LOG.warn( "Survey request already sent" );
                throw new InvalidInputException( "Can not process the record. A survey request for customer "
                    + survey.getCustomerFirstName() + " has already received." );
            } else if ( engagementClosedTime.before( date ) ) {
                LOG.debug( "An old record found : " + survey.getSurveyPreIntitiationId() );
                throw new InvalidInputException( "Can not process the record. Request for customer "
                    + survey.getCustomerFirstName() + " is older than " + validSurveyInterval + " days." );
            } else if ( isEmailIsIgnoredEmail( survey.getAgentEmailId(), survey.getCompanyId() ) ) {
                LOG.error( "no agent found with this email id and its an ignored record" );
                throw new InvalidInputException(
                    "Can not process the record. No service provider found with email address :  " + survey.getAgentEmailId() );
            } else if ( survey.getAgentEmailId() == null || survey.getAgentEmailId().isEmpty() ) {
                LOG.error( "Agent email not found , invalid survey " + survey.getSurveyPreIntitiationId() );
                throw new InvalidInputException( "Can not process the record.  service provider email id is missing" );
            } else if ( ( survey.getCustomerFirstName() == null || survey.getCustomerFirstName().isEmpty() )
                && ( survey.getCustomerLastName() == null || survey.getCustomerLastName().isEmpty() ) ) {
                LOG.error(
                    "No Name found for customer, hence this is an invalid survey " + survey.getSurveyPreIntitiationId() );
                throw new InvalidInputException( "Can not process the record. Customer Name is missing" );
            } else if ( survey.getCustomerEmailId() == null || survey.getCustomerEmailId().isEmpty() ) {
                LOG.error( "No customer email id found, invalid survey " + survey.getSurveyPreIntitiationId() );
                throw new InvalidInputException( "Can not process the record. Customer Email id is missing" );
            } else if ( user.getCompany() == null ) {
                LOG.error( "Agent doesnt have an company associated with it " );
                throw new InvalidInputException(
                    "Can not process the record. No service provider found with email address :  " + survey.getAgentEmailId() );
            } else if ( user.getCompany().getCompanyId() != survey.getCompanyId() ) {
                throw new InvalidInputException(
                    "Can not process the record. No service provider found with email address :  " + survey.getAgentEmailId() );
            }

            survey.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
            if ( survey.getStatus() == CommonConstants.STATUS_SURVEYPREINITIATION_DUPLICATE_RECORD )
                survey.setStatus( CommonConstants.STATUS_SURVEYPREINITIATION_DUPLICATE_RECORD );
            else
                survey.setStatus( CommonConstants.SURVEY_STATUS_PRE_INITIATED );
        }

        LOG.debug( "Method processPreinitiatedRecord validatePreinitiatedRecord finished " );
        return surveyPreInitiations;
    }


    /**
     * 
     * @param companyId
     * @return
     */
    private int getDuplicateSurveyIntervalForCompany( long companyId )
    {

        LOG.debug( "Method getDuplicateSurveyIntervalForCompany started for company " + companyId );

        OrganizationUnitSettings companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( companyId,
            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        int duplicateSurveyInterval = 0;
        if ( companySettings != null && companySettings.getSurvey_settings() != null
            && companySettings.getSurvey_settings().getDuplicateSurveyInterval() > 0 ) {
            duplicateSurveyInterval = companySettings.getSurvey_settings().getDuplicateSurveyInterval();
        } else {
            duplicateSurveyInterval = defaultSurveyRetakeInterval;
        }
        LOG.debug( "Method getDuplicateSurveyIntervalForCompany finished for company " + companyId );
        return duplicateSurveyInterval;

    }

}
