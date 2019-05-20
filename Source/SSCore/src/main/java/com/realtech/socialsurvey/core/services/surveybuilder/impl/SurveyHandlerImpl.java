package com.realtech.socialsurvey.core.services.surveybuilder.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
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
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import com.mongodb.BulkWriteError;
import com.realtech.socialsurvey.core.entities.*;
import com.realtech.socialsurvey.core.vo.BulkWriteErrorVO;
import com.realtech.socialsurvey.core.vo.SmsSurveyReminderResponseVO;

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
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.BulkOperationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.EmailTemplateConstants;
import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.dao.BranchDao;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.RegionDao;
import com.realtech.socialsurvey.core.dao.SurveyCsvUploadDao;
import com.realtech.socialsurvey.core.dao.SurveyDetailsDao;
import com.realtech.socialsurvey.core.dao.SurveyPreInitiationDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.enums.OrganizationUnit;
import com.realtech.socialsurvey.core.enums.SettingsForApplication;
import com.realtech.socialsurvey.core.enums.SurveyErrorCode;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.factories.ApplicationSettingsInstanceProvider;
import com.realtech.socialsurvey.core.integration.stream.StreamApiIntegrationBuilder;
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.contact.ContactUnsubscribeService;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.EmailUnsubscribeService;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.DeleteDataTrackerService;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.settingsmanagement.impl.InvalidSettingsStateException;
import com.realtech.socialsurvey.core.services.sms.SmsServices;
import com.realtech.socialsurvey.core.services.social.SocialManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.core.services.upload.FileUploadService;
import com.realtech.socialsurvey.core.utils.CsvUtils;
import com.realtech.socialsurvey.core.utils.EmailFormatHelper;
import com.realtech.socialsurvey.core.utils.FileOperations;
import com.realtech.socialsurvey.core.vo.SurveyDetailsVO;
import com.realtech.socialsurvey.core.vo.SurveysAndReviewsVO;


// JIRA SS-119 by RM-05:BOC
@Component
public class SurveyHandlerImpl implements SurveyHandler, InitializingBean
{

    private static final Logger LOG = LoggerFactory.getLogger( SurveyHandlerImpl.class );

    private static String SWEAR_WORDS;
    
    private static final String DATE_FORMATE_YYYYMMDD = "yyyy/MM/dd";

    @Autowired
    private SolrSearchService solrSearchService;

    @Autowired
    private SurveyDetailsDao surveyDetailsDao;

    @Autowired
    private SurveyCsvUploadDao surveyCsvUploadDao;

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
    private BatchTrackerService batchTrackerService;

    @Autowired
    private EmailServices emailServices;
    
    @Autowired
    private SmsServices smsServices;
    
    @Autowired
    private ApplicationSettingsInstanceProvider applicationSettingsInstanceProvider;
    
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

    @Value ( "${ZILLOW_REVIEW_POST_URL}")
    private String zillowReviewwPostUrl;
    

    @Value ( "${COUNTRY_DIALIN_CODES}")
    private String countryDialInCodes;
    
    private List<String> dialInCodes;
    
    @Autowired
    private Utils utils;

    @Autowired
    private SocialManagementService socialManagementService;

    @Autowired
    private GenericDao<CompanyIgnoredEmailMapping, Long> companyIgnoredEmailMappingDao;

    @Autowired
    private CsvUtils csvUtils;

    @Autowired
    private FileUploadService fileUploadService;
    
    @Autowired
    private EmailUnsubscribeService unsubscribeService;
    
    @Autowired
    private ContactUnsubscribeService contactUnsubscribeService;
    
    @Autowired
	private StreamApiIntegrationBuilder streamApiIntergrationBuilder;
    
    @javax.annotation.Resource
    @Qualifier ( "branch")
    private BranchDao branchDao;
    
    @Autowired
    private RegionDao regionDao;

    @Autowired
    private GenericDao<SurveyPreInitiationTemp, Long> surveyPreinitiationTempDao;

    @Autowired
    private DeleteDataTrackerService deleteDataTrackerService;

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

    private static final int STATUS_UPLOADER_EMAIL_INVALID = 2;
    private static final int STATUS_CSV_UPLOAD_INCOMPLETE = 3;
    private static final int STATUS_CSV_UPLOAD_COMPLETE = 4;

    private static final int SURVEY_CSV_CUSTOMER_FIRST_NAME_INDEX_FOR_AGENT = 0;
    private static final int SURVEY_CSV_CUSTOMER_FIRST_NAME_INDEX_FOR_ADMIN = 1;

    private static final int SURVEY_CSV_CUSTOMER_LAST_NAME_INDEX_FOR_AGENT = 1;
    private static final int SURVEY_CSV_CUSTOMER_LAST_NAME_INDEX_FOR_ADMIN = 2;

    private static final int SURVEY_CSV_CUSTOMER_EMAIL_INDEX_FOR_AGENT = 2;
    private static final int SURVEY_CSV_CUSTOMER_EMAIL_NAME_INDEX_FOR_ADMIN = 3;

    private static final int SURVEY_CSV_AGENT_EMAIL_INDEX_FOR_ADMIN = 0;
    
    private static final int SURVEY_CSV_CUSTOMER_CONTACT_NUMBER_INDEX_FOR_AGENT = 3;
    private static final int SURVEY_CSV_CUSTOMER_CONTACT_NUMBER_INDEX_FOR_ADMIN = 4;
    
    private static final String AGENT_EMAIL = "Agent Email";
    private static final String CUSTOMER_FIRST_NAME = "Customer First Name";
    private static final String CUSTOMER_LAST_NAME = "Customer Last Name";
    private static final String CUSTOMER_EMAIL = "Customer Email";
    private static final String CUSTOMER_CONTACT_NUMBER = "Customer Contact Number";
    
    private static final List<String> AGENT_HEADER = Arrays.asList( CUSTOMER_FIRST_NAME, CUSTOMER_LAST_NAME, CUSTOMER_EMAIL, CUSTOMER_CONTACT_NUMBER );
    private static final List<String> ADMIN_HEADER = Arrays.asList( AGENT_EMAIL, CUSTOMER_FIRST_NAME, CUSTOMER_LAST_NAME, CUSTOMER_EMAIL, CUSTOMER_CONTACT_NUMBER );
    
    private  static final String SMS_FAILED_OUT_OFF_WINDOW_TIME_MSG = "SMS send failed: We can only send text messages to your customers between %s and %s %s Time. Please try again during those hours.";
    private  static final String SMS_FAILED_LIMIT_REACHED_MSG = "SMS send failed: You have reached your limit for sending text reminders to this customer.";

    private  static final String SMS_FAILED_ONLY_ONE_REMINDER_MSG = "SMS send failed: You can only send 1 text message to your customer every 24 hours. Please try again later.";

    

    @PostConstruct
    private void fetchCountryDialInCodeList() {
    	if( dialInCodes == null ) {
    		
    		dialInCodes = new ArrayList<>();
    	}
    	dialInCodes = Arrays.asList( countryDialInCodes.split( "," ) );
    }

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
            if ( userProfile.getStatus() == CommonConstants.STATUS_ACTIVE && userProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) {
                //get primary profile if there are  multiple active agent profile for user
            		if(userProfile.getIsPrimary() == CommonConstants.YES || branchId == 0 ) {
                		branchId = userProfile.getBranchId();
                    regionId = userProfile.getRegionId();
                }
            		
            }
        }
        Branch branch = userManagementService.getBranchById(branchId);
        Region region = userManagementService.getRegionById(regionId);

        SurveyDetails surveyDetails = new SurveyDetails();
        surveyDetails.setAgentId( surveyPreInitiation.getAgentId() );
        surveyDetails.setAgentName( agentName );
        surveyDetails.setAgentEmailId( surveyPreInitiation.getAgentEmailId() );
        surveyDetails.setBranchId( branchId );
        String branchName = "";
        if(branch != null && branch.getIsDefaultBySystem() == 0) {
            branchName = branch.getBranch();
        }
        surveyDetails.setBranchName(branchName);
        
        surveyDetails.setCustomerFirstName( surveyPreInitiation.getCustomerFirstName() );
        String lastName = surveyPreInitiation.getCustomerLastName();
        if ( lastName != null && !lastName.isEmpty() && !lastName.equalsIgnoreCase( "null" ) )
            surveyDetails.setCustomerLastName( lastName );
        surveyDetails.setCompanyId( companyId );
        surveyDetails.setCustomerEmail( surveyPreInitiation.getCustomerEmailId() );
        surveyDetails.setRegionId( regionId );
        
        String regionName = "";
        if(region!=null && region.getIsDefaultBySystem() == 0) {
            regionName = region.getRegion();
        }
        surveyDetails.setRegionName(regionName);
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

        surveyDetails.setNpsScore( -1 );
        surveyDetails.setReview( null );
        surveyDetails.setRetakeSurvey( retakeSurvey );
        surveyDetails.setSurveyPreIntitiationId( surveyPreInitiation.getSurveyPreIntitiationId() );
        surveyDetails.setSurveyTransactionDate( surveyPreInitiation.getEngagementClosedTime() );
        surveyDetails.setState( surveyPreInitiation.getState() );
        surveyDetails.setCity( surveyPreInitiation.getCity() );
        
        //adding participantType and surveySentDate to mongo
        surveyDetails.setParticipantType(surveyPreInitiation.getParticipantType());
        surveyDetails.setSurveySentDate(surveyPreInitiation.getCreatedOn());

        SurveyDetails survey = null;
        //if survey request is old get survey by agent id and customer email
        if ( isOldRecord ) {
            survey = surveyDetailsDao.getSurveyByAgentIdAndCustomerEmail( surveyPreInitiation.getAgentId(),
                surveyPreInitiation.getCustomerEmailId(), surveyPreInitiation.getCustomerLastName(), lastName );
        } else {
            survey = surveyDetailsDao.getSurveyBySurveyPreIntitiationId( surveyPreInitiation.getSurveyPreIntitiationId() );
        }
        
        if(surveyPreInitiation.getPropertyAddress() != null) {
            surveyDetails.setPropertyAddress( surveyPreInitiation.getPropertyAddress() );
        }

        if(surveyPreInitiation.getLoanProcessorEmail() != null) {
            surveyDetails.setLoanProcessorEmail( surveyPreInitiation.getLoanProcessorEmail() );
        }
        
        if(surveyPreInitiation.getLoanProcessorName() != null) {
            surveyDetails.setLoanProcessorName( surveyPreInitiation.getLoanProcessorName() );
        }
        
        if(surveyPreInitiation.getCustomFieldOne() != null) {
            surveyDetails.setCustomFieldOne( surveyPreInitiation.getCustomFieldOne() );
        }
        if(surveyPreInitiation.getCustomFieldTwo() != null) {
            surveyDetails.setCustomFieldTwo( surveyPreInitiation.getCustomFieldTwo() );
        }
        if(surveyPreInitiation.getCustomFieldThree() != null) {
            surveyDetails.setCustomFieldThree( surveyPreInitiation.getCustomFieldThree() );
        }
        if(surveyPreInitiation.getCustomFieldFour() != null) {
            surveyDetails.setCustomFieldFour( surveyPreInitiation.getCustomFieldFour() );
        }
        if(surveyPreInitiation.getCustomFieldFive() != null) {
            surveyDetails.setCustomFieldFive( surveyPreInitiation.getCustomFieldFive() );
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
            if(survey.isOpenRetakeSurveyRequest()) {
                survey.setRetakeSurvey( retakeSurvey );
                survey.setNpsScore( -1 );
            		survey.setReview( null );
            		survey.setStage( CommonConstants.INITIAL_INDEX );
            }
            	survey.setOpenRetakeSurveyRequest(false);
            surveyDetails.setModifiedOn( new Date( System.currentTimeMillis() ) );
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



    /**
     * @param surveyId
     * @param question
     * @param questionType
     * @param answer
     * @param stage
     * @param isUserRankingQuestion
     * @param isNpsQuestion
     */
    @Override
    public void updateCustomerAnswersInSurvey( String surveyId, String question, String questionType, String answer, int stage,
        boolean isUserRankingQuestion, boolean isNpsQuestion, int questionId, boolean considerForScore )
    {
        LOG.info(
            "Method to update answers provided by customer in SURVEY_DETAILS, updateCustomerAnswersInSurvey() started." );
        SurveyResponse surveyResponse = new SurveyResponse();
        surveyResponse.setAnswer( answer );
        surveyResponse.setQuestion( question );
        surveyResponse.setQuestionType( questionType );
        surveyResponse.setIsUserRankingQuestion( isUserRankingQuestion );
        surveyResponse.setIsNpsQuestion( isNpsQuestion );
        surveyResponse.setQuestionId( questionId );
        surveyResponse.setConsiderForScore( considerForScore );
        surveyDetailsDao.updateCustomerResponse( surveyId, surveyResponse, stage );
        LOG.info(
            "Method to update answers provided by customer in SURVEY_DETAILS, updateCustomerAnswersInSurvey() finished." );
    }


    /*
     * Method to update customer review and final score on the basis of rating questions in
     * SURVEY_DETAILS.
     */
    @Override
    public double updateGatewayQuestionResponseAndScore( String surveyId, String mood, String review, boolean isAbusive,
        String agreedToShare, String profImageUrl )
    {
        LOG.info(
            "Method to update customer review and final score on the basis of rating questions in SURVEY_DETAILS, updateCustomerAnswersInSurvey() started." );
        //surveyDetailsDao.updateFinalScore( surveyId );
        //modulerising update final score 
        //fetch survey response
        List<SurveyResponse> surveyResponse = surveyDetailsDao.getSurveyRatingResponse(surveyId);
        //calculate score 
        double score = calScore(surveyResponse);
        //get nps
        double npsScore = getNpsScore(surveyResponse);
        surveyDetailsDao.updateGatewayAnswer( surveyId, mood, review, isAbusive, agreedToShare, score, npsScore, profImageUrl );
        LOG.info(
            "Method to update customer review and final score on the basis of rating questions in SURVEY_DETAILS, updateCustomerAnswersInSurvey() finished." );
        return score;
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
    public void updateSurveyAsAbusive( String surveymongoId, String reporterEmail, String reporterName, String reportReason )
    {
        LOG.debug( "Method updateSurveyAsAbusive() to mark the survey as abusive, started" );
        surveyDetailsDao.updateSurveyAsAbusive( surveymongoId, reporterEmail, reporterName, reportReason );
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

    /*
     * Method to increase reminder count by 1. This method is called every time a reminder sms is
     * sent to the customer.
     */
    @Override
    @Transactional
    public SurveyPreInitiation updateReminderCountSms( long surveyPreInitiationId, boolean autoReminder, boolean isNotDuplicate )
    {
    	LOG.debug( "Method to increase reminder count by 1, updateReminderCountSms() started." );
    	SurveyPreInitiation survey = surveyPreInitiationDao.findById( SurveyPreInitiation.class, surveyPreInitiationId );
    	if ( survey != null ) {
    		Timestamp timestamp = new Timestamp( System.currentTimeMillis() );
    		if(isNotDuplicate) {
	    		survey.setModifiedOn( timestamp );
	    		survey.setLastSmsReminderTime( timestamp );
	    		survey.setReminderCountsSms( survey.getReminderCountsSms() + 1 );
	    		if ( autoReminder ) {
	    			survey.setIsAutoSmsReminderSent(1);
	    		}
	    	}
    		else {
    			survey.setLastSmsReminderTime( timestamp );
	    		survey.setReminderCountsSms( survey.getReminderCountsSms() + 1 );
    		}
    		surveyPreInitiationDao.merge( survey );
    		LOG.debug( "Method to increase reminder count by 1, updateReminderCountSms() finished." );
    	}
        return survey;
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
            surveyPreInitiation.setIsSurveyRequestSent( CommonConstants.IS_SURVEY_REQUEST_SENT_TRUE );
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
        LOG.info("Method to get swear words is implemented");
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
     * Method to get list of all the administrator emailIds, an agent comes under.
     */
    @Transactional
    @Override
    public Map<String, Map<String, String>> getEmailIdsOfAdminsInHierarchy( long agentId ) throws InvalidInputException
    {
        Map<String, Map<String, String>> adminsHierarchyMap = new HashMap<>();
        Map<String, List<UserProfile>> adminMap = new HashMap<>();
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
            adminMap.put( CommonConstants.BRANCH_ID_COLUMN, userProfileDao.findByKeyValue( UserProfile.class, queries ) );
            queries.clear();
            queries.put( CommonConstants.REGION_ID_COLUMN, agentProfile.getRegionId() );
            queries.put( CommonConstants.PROFILE_MASTER_COLUMN,
                userManagementService.getProfilesMasterById( CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID ) );
            queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
            adminMap.put( CommonConstants.REGION_ID_COLUMN, userProfileDao.findByKeyValue( UserProfile.class, queries ) );
            queries.clear();
            if ( agentProfile.getCompany() != null ) {
                queries.put( CommonConstants.COMPANY_COLUMN, agentProfile.getCompany() );
                queries.put( CommonConstants.PROFILE_MASTER_COLUMN,
                    userManagementService.getProfilesMasterById( CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID ) );
                queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
                adminMap.put( CommonConstants.COMPANY_ID_COLUMN, userProfileDao.findByKeyValue( UserProfile.class, queries ) );
            }
        }

        adminsHierarchyMap.put( CommonConstants.BRANCH_ID_COLUMN,
            processAdminListForAHierarchy( adminMap.get( CommonConstants.BRANCH_ID_COLUMN ) ) );
        adminsHierarchyMap.put( CommonConstants.REGION_ID_COLUMN,
            processAdminListForAHierarchy( adminMap.get( CommonConstants.REGION_ID_COLUMN ) ) );
        adminsHierarchyMap.put( CommonConstants.COMPANY_ID_COLUMN,
            processAdminListForAHierarchy( adminMap.get( CommonConstants.COMPANY_ID_COLUMN ) ) );

        return adminsHierarchyMap;
    }


    private Map<String, String> processAdminListForAHierarchy( List<UserProfile> admins )
    {

        if ( admins == null || admins.isEmpty() ) {
            return null;
        } else {
            Map<String, String> adminHierarchyMap = new HashMap<>();
            for ( UserProfile admin : admins ) {
                String name = admin.getUser().getFirstName();
                if ( admin.getUser().getLastName() != null )
                    name += " " + admin.getUser().getLastName();
                adminHierarchyMap.put( admin.getEmailId(), name );
            }
            return adminHierarchyMap;
        }
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

        LOG.info( "Criteria to getIncompleteSurveyForReminderEmail is  {} {} {} {}", companyCriteria.toString(), statusCriteria.toString(), 
            minLastReminderCriteria.toString(), maxLastReminderCriteria );

        incompleteSurveyCustomers = surveyPreInitiationDao.findByCriteria( SurveyPreInitiation.class, companyCriteria,
            statusCriteria, minLastReminderCriteria, maxLastReminderCriteria, reminderCountCriteria );
        LOG.debug( "method getIncompleteSurveyForReminderEmail finished." );
        return incompleteSurveyCustomers;
    }
    
    /*
     * Method to get list of customers' contact numbers who have not completed survey yet. It checks if
     * max number of reminder sms have been sent. It also checks if required number of days have
     * been passed since the last sms was sent.
     */
    @Override
    @Transactional
    public List<SurveyPreInitiation> getIncompleteSurveyForSmsReminder( long companyId, Date minLastReminderDate,
        Date maxLastReminderDate, int maxReminderCount )
    {
        LOG.debug( "method getIncompleteSurveyForSmsReminder started." );

        Criterion companyCriteria = Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId );
        Criterion statusCriteria = Restrictions.in( CommonConstants.STATUS_COLUMN,
            Arrays.asList( CommonConstants.STATUS_SURVEYPREINITIATION_PROCESSED, CommonConstants.SURVEY_STATUS_INITIATED ) );

        Criterion minLastReminderCriteria = Restrictions.gt( CommonConstants.CREATED_ON, minLastReminderDate );
        Criterion maxLastReminderCriteria = Restrictions.lt( CommonConstants.CREATED_ON, maxLastReminderDate );

        Criterion lastReminderTimeNull = Restrictions.isNull( CommonConstants.LAST_SMS_REMINDER_TIME );

        Criterion lastReminderTimeAndCriteria = Restrictions.and(minLastReminderCriteria, maxLastReminderCriteria, lastReminderTimeNull);

        Criterion reminderCountCriteria = Restrictions.eq( "reminderCountsSms", 0 );

        Criterion contactNumberCriteria = Restrictions.isNotNull("customerContactNumber");

        Criterion autoReminderCriteria = Restrictions.eq( "isAutoSmsReminderSent", 0 );

        LOG.info( "Criteria to getIncompleteSurveyForSmsReminder is {} {} {} {} {} {}", companyCriteria, statusCriteria, 
        		lastReminderTimeAndCriteria, reminderCountCriteria, contactNumberCriteria, autoReminderCriteria );

        List<SurveyPreInitiation> incompleteSurveyCustomers = surveyPreInitiationDao.findByCriteria( SurveyPreInitiation.class, companyCriteria,
            statusCriteria, lastReminderTimeAndCriteria, reminderCountCriteria, contactNumberCriteria, autoReminderCriteria );
        LOG.debug( "method getIncompleteSurveyForSmsReminder finished." );
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

        LOG.info( "Criteria to getSurveyListToSendInvitationMail is  {} {} {}", companyCriteria.toString(), 
            statusCriteria.toString(), lastReminderCriteria.toString() );

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
                //if social post reminder is disabled then return  empty list for whole company
                if( surveySettings.getIsSocialPostReminderDisabled()) {
                		return incompleteSocialPostCustomers;
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
    public void markSurveyAsRetake( String surveyId, boolean editable, String requestSource )
    {
        LOG.debug( "Method to update status of survey in SurveyDetails collection, changeStatusOfSurvey() started." );
        SurveyDetails surveyDetails = surveyDetailsDao.getSurveyBySurveyMongoId( surveyId );
        
        RetakeSurveyHistory retakeSurveyHistory = new RetakeSurveyHistory();
        retakeSurveyHistory.setCompleteProfileUrl( surveyDetails.getCompleteProfileUrl() );
        retakeSurveyHistory.setMood( surveyDetails.getMood() );
        retakeSurveyHistory.setReview( surveyDetails.getReview() );
        retakeSurveyHistory.setScore( surveyDetails.getScore() );
        retakeSurveyHistory.setSocialMediaPostDetails( surveyDetails.getSocialMediaPostDetails() );
        retakeSurveyHistory.setSummary( surveyDetails.getSummary() );
        retakeSurveyHistory.setSurveyResponse( surveyDetails.getSurveyResponse() );
        retakeSurveyHistory.setUrl( surveyDetails.getUrl() );
        retakeSurveyHistory.setRetakeRequestDate( new Date ( System.currentTimeMillis() ) );
        retakeSurveyHistory.setRequestSource(requestSource);
        List<RetakeSurveyHistory> retakeSurveyHistories =  surveyDetails.getRetakeSurveyHistory();
        if(retakeSurveyHistories == null)
            retakeSurveyHistories = new ArrayList<RetakeSurveyHistory>();
        retakeSurveyHistories.add( retakeSurveyHistory );
        surveyDetails.setRetakeSurveyHistory( retakeSurveyHistories );
        surveyDetails.setOpenRetakeSurveyRequest(true);
        surveyDetails.setNoOfRetake( surveyDetails.getNoOfRetake() + 1 );
        surveyDetails.setLastRetakeRequestDate( new Date ( System.currentTimeMillis() ) );
        
        surveyDetails.setModifiedOn(new Date(System.currentTimeMillis()));
        surveyDetailsDao.updateSurveyDetailsForRetake( surveyDetails );
        LOG.debug( "Method to update status of survey in SurveyDetails collection, changeStatusOfSurvey() finished." );
    }


    /*
     * Method to store initial details of customer to initiate survey and send a mail with link of
     * survey.
     */
    @Override
    @Transactional
    public void storeSPIandSendSurveyInvitationMail( String custFirstName, String custLastName, String custEmail,
        String custRelationWithAgent, User user, boolean isAgent, String source, String custContactNumber ) throws InvalidInputException, SolrException,
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
            custRelationWithAgent, source, custContactNumber );

        //prepare and send email
        if(surveyPreInitiation.getIsSurveyRequestSent() == CommonConstants.IS_SURVEY_REQUEST_SENT_TRUE) {
            prepareAndSendInvitationMail( surveyPreInitiation );
        }        

        LOG.debug( "Method storeSPIandSendSurveyInvitationMail() finished ." );
    }


    @Override
    public void sendSurveyReminderEmail( SurveyPreInitiation survey ) throws InvalidInputException, ProfileNotFoundException
    {
        // Send email to complete survey to each customer.
        OrganizationUnitSettings companySettings = null;
        String agentName = "";
        String agentFirstName = "";
        User user = null;
        Map<String, Long> hierarchyMap = null;
        Map<SettingsForApplication, OrganizationUnit> map = null;
        String logoUrl = null;
        user = userManagementService.getUserByUserId( survey.getAgentId() );

        if ( user != null ) {
            agentName = user.getFirstName();
            agentFirstName = user.getFirstName();
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

        //For Company with hidden agents
        String senderName;
        if ( companySettings.isSendEmailFromCompany() ) {
            senderName = companyName;
        } else {
            senderName = agentName;
        }
        
        String unsubscribedURL = buildUnsubscribedUrl( survey.getAgentId(), survey.getCustomerEmailId(), survey.getCompanyId() );

        //send mail
        try {
            emailServices.sendSurveyRelatedMail(companySettings, user, agentName, agentFirstName, agentPhone, agentTitle,
                    surveyLink, logoUrl, survey.getCustomerFirstName(),
                    survey.getCustomerLastName(), survey.getCustomerEmailId(), CommonConstants.EMAIL_TYPE_SURVEY_REMINDER_MAIL,
                    senderName, user.getEmailId(), mailSubject, mailBody, agentSettings, branchId, regionId,
                    survey.getSurveySourceId(), survey.getAgentId(), companyId, companySettings.isSendEmailFromCompany()
                    ,unsubscribedURL);
        } catch ( InvalidInputException | UndeliveredEmailException e ) {
            LOG.error( "Exception caught while sending mail to " + survey.getCustomerEmailId() + " .Nested exception is ", e );
        }
    }
    
    private String buildUnsubscribedUrl(long agentId, String customerEmailId, long companyId) throws InvalidInputException {
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put( CommonConstants.AGENT_ID_COLUMN, Long.toString( agentId) );
        urlParams.put( CommonConstants.CUSTOMER_EMAIL_COLUMN, customerEmailId );
        urlParams.put( CommonConstants.COMPANY_ID_COLUMN, Long.toString( companyId) );
        return urlGenerator.generateUrl( urlParams, getApplicationBaseUrl()+ CommonConstants.UNSUBSCRIBE_URL);
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
        String agentFirstName = user.getFirstName();
        if ( user.getLastName() != null && !user.getLastName().isEmpty() ) {
            agentName = user.getFirstName() + " " + user.getLastName();
        }

        // TODO add address for mail footer
        //String fullAddress = "";

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
        SurveyPreInitiation surveyPreInitiation = null;
        if ( survey != null ) {
             surveyPreInitiation = getPreInitiatedSurvey( user.getUserId(), custEmail, custFirstName,
                custLastName );
            if ( surveyPreInitiation != null ) {
                markSurveyAsStarted( surveyPreInitiation );
            } else
                preInitiateSurvey( user, custEmail, custFirstName, custLastName, 0, custRelationWithAgent, surveySource, null );
        } else {
            //TODO survey will be null, Need to handle this
            /*SurveyPreInitiation surveyPreInitiation = getPreInitiatedSurvey( survey.getSurveyPreIntitiationId() );
            if ( surveyPreInitiation != null ) {
                markSurveyAsStarted( surveyPreInitiation );
            } else
                preInitiateSurvey( user, custEmail, custFirstName, custLastName, 0, custRelationWithAgent, surveySource );*/
        }

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

        //For Company with hidden agents
        String senderName;
        if ( companySettings.isSendEmailFromCompany() ) {
            senderName = companyName;
        } else {
            senderName = agentName;
        }
        
        String unsubscribedURL = buildUnsubscribedUrl( user.getUserId(), custEmail, user.getCompany().getCompanyId() );

        //send mail
        try {
            emailServices.sendSurveyRelatedMail(companySettings, user, agentName, agentFirstName, agentPhone, agentTitle,
                    surveyUrl, logoUrl, custFirstName,
                    custLastName, custEmail, CommonConstants.EMAIL_TYPE_SURVEY_RESTART_MAIL,
                    senderName, user.getEmailId(), mailSubject, mailBody, agentSettings, branchId, regionId,
                    surveyPreInitiation.getSurveySourceId(), survey.getAgentId(), companyId, companySettings.isSendEmailFromCompany(),
                    unsubscribedURL);
        } catch ( InvalidInputException | UndeliveredEmailException e ) {
            LOG.error( "Exception caught while sending mail to " + custEmail + ". Nested exception is ", e );
        }
        LOG.debug( "sendSurveyRestartMail() finished." );
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

        String agentFirstName = user.getFirstName();
        String agentName = user.getFirstName();
        if ( user.getLastName() != null && !user.getLastName().isEmpty() ) {
            agentName = user.getFirstName() + " " + user.getLastName();
        }
        String agentSignature = emailFormatHelper.buildAgentSignature( agentName, agentPhone, agentTitle, companyName );
        String currentYear = String.valueOf( Calendar.getInstance().get( Calendar.YEAR ) );
        DateFormat dateFormat = new SimpleDateFormat( DATE_FORMATE_YYYYMMDD );
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
            LOG.error( "Error: ", e );
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
                LOG.error( "Error: ", e );
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
        
        String unsubscribedUrl = buildUnsubscribedUrl( user.getUserId(), custEmail, user.getCompany().getCompanyId() );
        Branch branch = branchDao.findById( Branch.class, branchId );
        String branchName = "";
        if(branch.getIsDefaultBySystem() == 0) {
            branchName = branch.getBranch();
        }
        Region region = regionDao.findById( Region.class, regionId );
        String regionName = "";
        if(region.getIsDefaultBySystem() == 0) {
            regionName = region.getRegion();
        }
        //replace the legends
        mailSubject = emailFormatHelper.replaceLegends( true, mailSubject, applicationBaseUrl, logoUrl, null, custFirstName,
            custLastName, agentName, agentFirstName, agentSignature, custEmail, user.getEmailId(), companyName, dateFormat.format( new Date() ),
            currentYear, fullAddress, "", user.getProfileName(), companyDisclaimer, agentDisclaimer, agentLicenses, agentTitle, agentPhone,
            unsubscribedUrl,user.getUserId(), branchName, regionName );


        mailBody = emailFormatHelper.replaceLegends( false, mailBody, applicationBaseUrl, logoUrl, null, custFirstName,
            custLastName, agentName ,agentFirstName, agentSignature, custEmail, user.getEmailId(), companyName, dateFormat.format( new Date() ),
            currentYear, fullAddress, "", user.getProfileName(), companyDisclaimer, agentDisclaimer, agentLicenses, agentTitle, agentPhone, unsubscribedUrl,
            user.getUserId(), branchName, regionName );

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
            emailServices.sendSurveyRelatedMail( custEmail, mailSubject, mailBody, user.getEmailId(), senderName,
                user.getUserId(), companyId, CommonConstants.EMAIL_TYPE_SURVEY_COMPLETION_MAIL,  companySettings.isSendEmailFromCompany()  );
        } catch ( InvalidInputException | UndeliveredEmailException e ) {
            LOG.error( "Exception caught while sending mail to " + custEmail + ". Nested exception is ", e );
        }
        LOG.info( "sendSurveyCompletionMail() finished." );
    }


    @Override
    public void sendSurveyCompletionUnpleasantMail( String custEmail, String custFirstName, String custLastName, User user )
        throws InvalidInputException, UndeliveredEmailException, ProfileNotFoundException
    {
        LOG.info( "Method sendSurveyCompletionUnpleasantMail() started." );
        Map<SettingsForApplication, OrganizationUnit> map = null;
        String logoUrl = null;
        AgentSettings agentSettings = userManagementService.getUserSettings( user.getUserId() );
        
        if ( agentSettings == null ) {
            LOG.warn( "agentSettings is null" );
            throw new InvalidInputException( "No agent settings found for userId " + user.getUserId());
        }
        
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

        String agentFirstName = user.getFirstName();
        String agentName = user.getFirstName();
        if ( user.getLastName() != null && !user.getLastName().isEmpty() ) {
            agentName = user.getFirstName() + " " + user.getLastName();
        }
        String agentSignature = emailFormatHelper.buildAgentSignature( agentName, agentPhone, agentTitle, companyName );
        String currentYear = String.valueOf( Calendar.getInstance().get( Calendar.YEAR ) );
        DateFormat dateFormat = new SimpleDateFormat( DATE_FORMATE_YYYYMMDD );
        

        Map<String, Long> hierarchyMap = profileManagementService.getPrimaryHierarchyByAgentProfile( agentSettings );

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
            LOG.error( "Error: ", e );
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
                LOG.error( "Error: ", e );
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

        if ( companySettings == null ) {
            LOG.warn( "companySettings is null" );
            throw new InvalidInputException( "No companySettings found for userId " + user.getUserId());
        }
        
        MailContent surveyCompletionUnpleasant = null;
        
        if(companySettings != null && companySettings.getMail_content() != null) {
            surveyCompletionUnpleasant = companySettings.getMail_content().getSurvey_completion_unpleasant_mail();
        }
        
        if ( surveyCompletionUnpleasant == null || StringUtils.isBlank( surveyCompletionUnpleasant.getMail_body())) {
            surveyCompletionUnpleasant = new MailContent();
            FileContentReplacements replacements = new FileContentReplacements();
            List<String> paramOrder = Arrays.asList( paramOrderSurveyCompletionUnpleasantMail.split( "," ) ) ;
            replacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
                + EmailTemplateConstants.SURVEY_COMPLETION_UNPLEASANT_MAIL_BODY );
            String emailBody = fileOperations.replaceFileContents( replacements );
            surveyCompletionUnpleasant.setMail_body( emailBody );
            surveyCompletionUnpleasant.setParam_order( paramOrder );
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

        String unsubscribedUrl = buildUnsubscribedUrl( user.getUserId(), custEmail, user.getCompany().getCompanyId() );
        Branch branch = branchDao.findById( Branch.class, branchId );
        String branchName = "";
        if ( branch.getIsDefaultBySystem() == 0 ) {
            branchName = branch.getBranch();
        }
        Region region = regionDao.findById( Region.class, regionId );
        String regionName = "";
        if ( region.getIsDefaultBySystem() == 0 ) {
            regionName = region.getRegion();
        }
        
        // TODO add address for mail footer
        String fullAddress = "";
        
        mailBody = emailFormatHelper.replaceLegends( false, mailBody, applicationBaseUrl, logoUrl, null, custFirstName,
            custLastName, agentName, agentFirstName, agentSignature, custEmail, user.getEmailId(), companyName,
            dateFormat.format( new Date() ), currentYear, fullAddress, "", user.getProfileName(), companyDisclaimer,
            agentDisclaimer, agentLicenses, agentTitle, agentPhone, unsubscribedUrl, user.getUserId(), branchName, regionName );

        String mailSubject = surveyCompletionUnpleasant.getMail_subject();
        if ( mailSubject == null || mailSubject.isEmpty() ) {
            mailSubject = CommonConstants.SURVEY_COMPLETION_UNPLEASANT_MAIL_SUBJECT;
        }

        mailSubject = emailFormatHelper.replaceLegends( true, mailSubject, applicationBaseUrl, logoUrl, null, custFirstName,
            custLastName, agentName, agentFirstName, agentSignature, custEmail, user.getEmailId(), companyName,
            dateFormat.format( new Date() ), currentYear, fullAddress, "", user.getProfileName(), companyDisclaimer,
            agentDisclaimer, agentLicenses, agentTitle, agentPhone, unsubscribedUrl, user.getUserId(), branchName, regionName );
        //JIRA SS-473 end

        //For Company with hidden agents
        String senderName;
        if ( companySettings.isSendEmailFromCompany() ) {
            senderName = companyName;
        } else {
            senderName = agentName;
        }
        try {
            emailServices.sendSurveyRelatedMail( custEmail, mailSubject, mailBody, user.getEmailId(), senderName,
                user.getUserId(), companyId, CommonConstants.EMAIL_TYPE_SURVEY_COMPLETION_UNPLEASANT_MAIL,
                companySettings.isSendEmailFromCompany() );
        } catch ( InvalidInputException | UndeliveredEmailException e ) {
            LOG.error( "Exception caught while sending mail to " + custEmail + ". Nested exception is ", e );
        }
        LOG.info( "Method sendSurveyCompletionUnpleasantMail() finished." );
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

        String agentFirstName = user.getFirstName();
        String agentName = user.getFirstName();
        if ( user.getLastName() != null && !user.getLastName().isEmpty() ) {
            agentName = user.getFirstName() + " " + user.getLastName();
        }
        String agentSignature = emailFormatHelper.buildAgentSignature( agentName, agentPhone, agentTitle, companyName );
        String currentYear = String.valueOf( Calendar.getInstance().get( Calendar.YEAR ) );
        DateFormat dateFormat = new SimpleDateFormat( DATE_FORMATE_YYYYMMDD );
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
        String unsubscribedUrl = buildUnsubscribedUrl( user.getUserId(), custEmail, user.getCompany().getCompanyId() );
        Branch branch = branchDao.findById( Branch.class, branchId );
        String branchName = "";
        if(branch.getIsDefaultBySystem() == 0) {
            branchName = branch.getBranch();
        }
        Region region = regionDao.findById( Region.class, regionId );
        String regionName = "";
        if(region.getIsDefaultBySystem() == 0) {
            regionName = region.getRegion();
        }
        //replace legends
        mailSubject = emailFormatHelper.replaceLegends( true, mailSubject, applicationBaseUrl, logoUrl, "", custFirstName,
            custLastName, agentName, agentFirstName, agentSignature, custEmail, user.getEmailId(), companyName, dateFormat.format( new Date() ),
            currentYear, fullAddress, links, user.getProfileName(), companyDisclaimer, agentDisclaimer, agentLicenses, agentTitle, agentPhone,
            unsubscribedUrl, user.getUserId(), branchName, regionName);

        mailBody = emailFormatHelper.replaceLegends( false, mailBody, applicationBaseUrl, logoUrl, "", custFirstName,
            custLastName, agentName, agentFirstName, agentSignature, custEmail, user.getEmailId(), companyName, dateFormat.format( new Date() ),
            currentYear, fullAddress, links, user.getProfileName(), companyDisclaimer, agentDisclaimer, agentLicenses, agentTitle, agentPhone,
            unsubscribedUrl,user.getUserId(), branchName, regionName);
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
            emailServices.sendSurveyRelatedMail( custEmail, mailSubject, mailBody, user.getEmailId(), senderName,
                user.getUserId(), companyId, CommonConstants.EMAIL_TYPE_SURVEY_SOCIALPOST_REMINDER_MAIL,  companySettings.isSendEmailFromCompany()  );
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


    @Override
    @Transactional
    public SurveyPreInitiation getPreInitiatedSurveyByCustomer( String customerEmailId )
    {
        LOG.debug( "Method getPreInitiatedSurveyByCustomer() started for id " + customerEmailId );
        Criterion criteria = Restrictions.eq( "customerEmailId", customerEmailId );
        List<SurveyPreInitiation> surveyPreInitiation = surveyPreInitiationDao.findByCriteria( SurveyPreInitiation.class,
        		criteria );
        LOG.debug( "Method getPreInitiatedSurveyByCustomer() finished for id " + customerEmailId );
        if(surveyPreInitiation != null && !surveyPreInitiation.isEmpty())
        		return surveyPreInitiation.get(0);
        else
        		return null;
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
                        incompleteSurveyCustomers = surveyPreInitiationDao.getValidSurveyByAgentIdAndCustomeEmailForPastNDays(
                            user.getUserId(), survey.getCustomerEmailId(), duplicateSurveyInterval );
                    } else {
                        incompleteSurveyCustomers = surveyPreInitiationDao.getValidSurveyByAgentIdAndCustomeEmail( user.getUserId(),
                            survey.getCustomerEmailId() );
                    }
                    if ( incompleteSurveyCustomers != null && incompleteSurveyCustomers.size() > 0 ) {
                        LOG.warn( "Survey request already sent" );
                        status = CommonConstants.STATUS_SURVEYPREINITIATION_DUPLICATE_RECORD;
                        errorCode = SurveyErrorCode.DUPLICATE_RECORD.name();
                        survey.setStatus( status );
                        //update source in old nor verified records
                        updateSurveySourcesInOldRecords(incompleteSurveyCustomers , survey );
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
            } else if(!Utils.validateCustomerEmail( survey.getCustomerEmailId() )){
                LOG.error( "Invalid customer email id found, invalid survey " + survey.getSurveyPreIntitiationId() );
                status = CommonConstants.STATUS_SURVEYPREINITIATION_CORRUPT_RECORD;
                errorCode = SurveyErrorCode.CORRUPT_RECORD_CUSTOMER_EMAIL_ID_INVALID.name();
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
            } else if (unsubscribeService.isUnsubscribed( survey.getCustomerEmailId(), survey.getCompanyId() )) {
                status = CommonConstants.STATUS_SURVEYPREINITIATION_UNSUBSCRIBED;
                errorCode = SurveyErrorCode.UNSUBSCRIBED_CUSTOMER_EMAIL.name();
            }

            //check is customer last name is null or customer first name have more then one words
            if( survey.getCustomerFirstName().indexOf(" ") > 0 ) {
            		if( survey.getCustomerFirstName().length() >= survey.getCustomerFirstName().indexOf(" ") + 2 ) {
            			String newFirstName = survey.getCustomerFirstName().substring(0, survey.getCustomerFirstName().indexOf(" ") );
            			String newLastName = survey.getCustomerFirstName().substring( survey.getCustomerFirstName().indexOf(" ") + 1 , survey.getCustomerFirstName().length());
            			survey.setCustomerFirstName(newFirstName);
            			//append if there is already last name 
            			if(StringUtils.isEmpty(survey.getCustomerLastName()))
            				survey.setCustomerLastName(newLastName);
            			else
            				survey.setCustomerLastName(newLastName + " " + survey.getCustomerLastName());
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
            if(companySettings.isAllowPartnerSurvey()){
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
        String agentFirstName = "";
        User user = null;
        Map<String, Long> hierarchyMap = null;
        Map<SettingsForApplication, OrganizationUnit> map = null;
        String logoUrl = null;
        user = userManagementService.getUserByUserId( survey.getAgentId() );

        if ( user != null ) {
            agentName = user.getFirstName();
            agentFirstName = user.getFirstName();
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

        //get mail subject and body
        String mailBody = "";
        String mailSubject = "";
        if ( companySettings != null && companySettings.getMail_content() != null
            && companySettings.getMail_content().getTake_survey_mail() != null ) {

            MailContent mailContent = companySettings.getMail_content().getTake_survey_mail();

            mailBody = emailFormatHelper.replaceEmailBodyWithParams( mailContent.getMail_body(), mailContent.getParam_order() );

            mailSubject = CommonConstants.SURVEY_MAIL_SUBJECT + agentName;
            if ( mailContent.getMail_subject() != null && !mailContent.getMail_subject().isEmpty() ) {
                mailSubject = mailContent.getMail_subject();
            }

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

        //For Company with hidden agents
        String senderName;
        if ( companySettings.isSendEmailFromCompany() ) {
            senderName = companyName;
        } else {
            senderName = agentName;
        }
        
        String unsubscribedURL = buildUnsubscribedUrl( survey.getAgentId(), survey.getCustomerEmailId(), survey.getCompanyId() );

        //send the mail
        try {
            /*emailServices.sendSurveyRelatedMail( survey.getCustomerEmailId(), mailSubject, mailBody, user.getEmailId(),
                senderName, user.getUserId(), companyId );*/
            emailServices.sendSurveyRelatedMail(companySettings, user, agentName, agentFirstName, agentPhone, agentTitle,
                    surveyLink, logoUrl, survey.getCustomerFirstName(),
                    survey.getCustomerLastName(), survey.getCustomerEmailId(), CommonConstants.EMAIL_TYPE_SURVEY_INVITATION_MAIL,
                    senderName,  user.getEmailId(), mailSubject, mailBody, agentSettings, branchId, regionId,
                    survey.getSurveySourceId(), survey.getAgentId(), companyId, companySettings.isSendEmailFromCompany(), unsubscribedURL);
        } catch ( InvalidInputException | UndeliveredEmailException e ) {
            LOG.error( "Exception caught while sending mail to " + survey.getCustomerEmailId() + " .Nested exception is ", e );
        }
    }


    // Method to store details of a customer in mysql at the time of sending invite.
    @Override
    public SurveyPreInitiation preInitiateSurvey( User user, String custEmail, String custFirstName, String custLastName, int i,
        String custRelationWithAgent, String source, String custContactNumber )
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
        if(unsubscribeService.isUnsubscribed( custEmail, user.getCompany().getCompanyId() )) {
            surveyPreInitiation.setStatus( CommonConstants.STATUS_SURVEYPREINITIATION_UNSUBSCRIBED );
            surveyPreInitiation.setIsSurveyRequestSent( CommonConstants.IS_SURVEY_REQUEST_SENT_FALSE );
        } else {
            surveyPreInitiation.setStatus( CommonConstants.SURVEY_STATUS_PRE_INITIATED );
            surveyPreInitiation.setIsSurveyRequestSent( CommonConstants.IS_SURVEY_REQUEST_SENT_TRUE );
        }
        surveyPreInitiation.setSurveySource( source );
        if( custContactNumber != null && !custContactNumber.isEmpty() ) {
        	
        	surveyPreInitiation.setCustomerContactNumber( custContactNumber );
        	surveyPreInitiation.setReminderCountsSms( 0 );
        }
        surveyPreInitiation = surveyPreInitiationDao.save( surveyPreInitiation );

        LOG.debug( "Method preInitiateSurvey() finished." );
        return surveyPreInitiation;
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
    
    public void checkIfContactNumberSurveyedForAgent(long agentId, String recipientContactNumber ) throws InvalidInputException, DuplicateContactSurveyRequestException {
        
        if ( agentId <= 0l ) {
            LOG.warn( "AgentId should be non zero value" );
            throw new InvalidInputException( "Agent id is invalid" );
        }
        
        if (StringUtils.isNotEmpty( recipientContactNumber ) && hasCustomerAlreadySurveyedForContactNumber( agentId, recipientContactNumber)) {
            throw new DuplicateContactSurveyRequestException( "Survey request already sent" );
        }
    }


    @Override
    @Transactional
    public void initiateSurveyRequest( long agentId, String recipientEmailId, String recipientFirstname,
        String recipientLastname, String source, String recipientContactNumber )
        throws DuplicateSurveyRequestException, InvalidInputException, SelfSurveyInitiationException, SolrException,
        NoRecordsFetchedException, UndeliveredEmailException, ProfileNotFoundException
    {
        LOG.debug( "Sending survey request for agent id: {} recipientEmailId: {} recipientFirstname: {} recipientLastname: {}", agentId, recipientEmailId, recipientFirstname, recipientLastname );
        if ( agentId <= 0l ) {
            LOG.warn( "Agentid should be non zero value" );
            throw new InvalidInputException( "Agent id is invalid" );
        }
        if ( recipientEmailId == null || recipientEmailId.isEmpty()
            || !Utils.validateCustomerEmail( recipientEmailId ) ) {
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
            incompleteSurveyCustomers = surveyPreInitiationDao.getValidSurveyByAgentIdAndCustomeEmailForPastNDays( agentId,
                recipientEmailId, duplicateSurveyInterval );
        } else {
            incompleteSurveyCustomers = surveyPreInitiationDao.getValidSurveyByAgentIdAndCustomeEmail( agentId, recipientEmailId );
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
            source, recipientContactNumber );
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
    public void deleteExistingSurveysByEntity( String entityType, long entityId, String source ) throws InvalidInputException
    {
        LOG.debug( "Method deleteExistingSurveysByEntity() started" );
        if ( entityType == null || entityType.isEmpty() ) {
            throw new InvalidInputException( "Entity Type is invalid" );
        }
        if ( entityId <= 0 ) {
            throw new InvalidInputException( "Entity ID is invalid" );
        }
        List<SurveyDetails> documentsToBeDeleted = surveyDetailsDao.fetchSurveyForParticularHierarchyAndSource( entityId,
            entityType, source );
        surveyDetailsDao.removeExistingZillowSurveysByEntity( entityType, entityId, source );
        deleteDataTrackerService.writeToDeleteTrackerForSurveyDetails(documentsToBeDeleted);
        LOG.debug( "Method deleteExistingSurveysByEntity() finished" );
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
    
    public void updateSurveyAsAbusiveNotify( String surveyId )
    {

        LOG.debug( "Method updateSurveyAsAbusiveNotify() to mark a survey as under resolution started, started" );
        surveyDetailsDao.updateSurveyAsAbusiveNotify(surveyId);
        LOG.debug( "Method updateSurveyAsAbusiveNotify() to mark a survey as under resolution started, ended" );
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
                        user = userManagementService.getActiveAgentByEmailAndCompany( companyId, agentEmailId );
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
                            CommonConstants.SURVEY_SOURCE_BULK_UPLOAD, null );
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
        ratingFormat.setMaximumFractionDigits( 2 );
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


    @Override
    @Transactional
    public void moveSurveyBetweenUsers( long surveyPreinitiationId, long toUserId )
        throws InvalidInputException, NoRecordsFetchedException, SolrException
    {
        if ( surveyPreinitiationId <= 0 ) {
            LOG.error( "Invalid from user id passed as parameter" );
            throw new InvalidInputException( "Invalid from user id passed as parameter" );
        }
        if ( toUserId <= 0 ) {
            LOG.error( "Invalid to user id passed as parameter" );
            throw new InvalidInputException( "Invalid to user id passed as parameter" );
        }
        try {
            User toUser = userManagementService.getUserByUserId( toUserId );
            UserProfile toUserProfile = getUserProfileWhereAgentForUser( toUser );
            long fromUserId = getAgentIdFromSurveyPreInitiation( surveyPreinitiationId );
            // replace agent id in Survey Pre Initiation
            surveyPreInitiationDao.updateAgentInfoOfPreInitiatedSurvey( surveyPreinitiationId, toUser );
            /// replace agent id in Details
            surveyDetailsDao.updateAgentInfoInSurveyBySPI( surveyPreinitiationId, toUser, toUserProfile );

            LOG.debug( "Updating review count of user :{} ", toUserId );
            solrSearchService.updateReviewCountOfUserInSolr( toUser );
            if ( fromUserId != 0 ) {
                LOG.debug( "Updating review count of user :{} ", fromUserId );
                User fromUser = userManagementService.getUserByUserId( fromUserId );
                solrSearchService.updateReviewCountOfUserInSolr( fromUser );
            }
        } catch ( InvalidInputException e ) {
            throw new NoRecordsFetchedException( "Either user or survey could not be found" );
        }

    }


    private long getAgentIdFromSurveyPreInitiation( long surveyPreinitiationId )
    {
        SurveyDetails survey = surveyDetailsDao.getsurveyFromSurveyPreinitiationId( surveyPreinitiationId );
        if ( survey != null ) {
            return survey.getAgentId();
        }
        return 0;
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
            false, serverBaseUrl, true, false );
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
        if ( user.getEmailId() != null && !user.getEmailId().isEmpty() )
            surveyDetails.setAgentEmailId( user.getEmailId() );
        Map<String, Long> profile = userManagementService.getPrimaryUserProfileByAgentId( user.getUserId() );
        Branch branch = userManagementService.getBranchById( profile.get( CommonConstants.BRANCH_ID_COLUMN ) );
        Region region = userManagementService.getRegionById( profile.get( CommonConstants.REGION_ID_COLUMN ) );
        surveyDetails.setBranchId( profile.get( CommonConstants.BRANCH_ID_COLUMN ) );
        surveyDetails.setBranchName(branch.getBranch());
        surveyDetails.setCustomerFirstName( surveyImportVO.getCustomerFirstName() );
        String lastName = surveyImportVO.getCustomerLastName();
        if ( lastName != null && !lastName.isEmpty() && !lastName.equalsIgnoreCase( "null" ) )
            surveyDetails.setCustomerLastName( lastName );
        surveyDetails.setCompanyId( user.getCompany().getCompanyId() );
        surveyDetails.setCustomerEmail( surveyImportVO.getCustomerEmailAddress() );
        surveyDetails.setRegionId( profile.get( CommonConstants.REGION_ID_COLUMN ) );
        surveyDetails.setRegionName(region.getRegion());
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
            incompleteSurveyCustomers = surveyPreInitiationDao.getValidSurveyByAgentIdAndCustomeEmailForPastNDays( currentAgentId,
                customerEmailId, duplicateSurveyInterval );
        } else {
            incompleteSurveyCustomers = surveyPreInitiationDao.getValidSurveyByAgentIdAndCustomeEmail( currentAgentId,
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
    public boolean hasCustomerAlreadySurveyedForContactNumber( long currentAgentId, String customerContactNumber )
    {
        

        StringBuilder builder = new StringBuilder( customerContactNumber );
        if( builder.charAt( 0 ) == '+' ) {

            builder.delete(0, 1);
        }
        
        String countryDialInCode = "1";
        String contactNumber = builder.substring( builder.length() - 10 );
        if( builder.length() > 10 ) {
            countryDialInCode = builder.substring( 0,  builder.length() - 10 );
            if( !dialInCodes.contains( countryDialInCode ) && !countryDialInCode.equals( "0" ) ) {
                LOG.warn("Customer contact number {} is invalid", customerContactNumber );
            } else {
                if( countryDialInCode.equals( "0" ) ) {
                    
                    countryDialInCode = "1";
                }
            }   
        }
        
        User currentAgent = userDao.findById( User.class, currentAgentId );
        int duplicateSurveyInterval = 120;
        if ( currentAgent != null && currentAgent.getCompany() != null ) {
            duplicateSurveyInterval = getDuplicateSurveyIntervalForCompany( currentAgent.getCompany().getCompanyId() );
        }
        
        return checkIfContactNumberAlreadyExists( countryDialInCode, contactNumber, currentAgentId,  duplicateSurveyInterval);
               
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

        DateFormat dateFormat = new SimpleDateFormat( DATE_FORMATE_YYYYMMDD );
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
            null, survey.getCustomerFirstName(), survey.getCustomerLastName(), survey.getAgentName(), user.getFirstName(), agentSignature,
            survey.getCustomerEmail(), user.getEmailId(), user.getCompany().getCompany(), dateFormat.format( new Date() ),
            currentYear, "", "", user.getProfileName(), companyDisclaimer, agentDisclaimer, agentLicenses, agentTitle, agentPhone, user, agentSettings,
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
        OrganizationUnitSettings regionSettings, OrganizationUnitSettings companySettings, Map<String, Object> surveyAndStage, SurveyDetails surveyDetails  )
    {
        // Fetching Zillow Url
        try {
        	
        	String zillowScreenName = "";
            if ( unitSettings != null && unitSettings.getSocialMediaTokens() != null
                && unitSettings.getSocialMediaTokens().getZillowToken() != null
                && unitSettings.getSocialMediaTokens().getZillowToken().getZillowProfileLink() != null ) {
                surveyAndStage.put( "zillowEnabled", true );
                zillowScreenName = unitSettings.getSocialMediaTokens().getZillowToken().getZillowScreenName();
            } else {
                // Adding Zillow Url of the closest in hierarchy connected with Zillow.
                if ( branchSettings != null && branchSettings.getOrganizationUnitSettings() != null
                    && branchSettings.getOrganizationUnitSettings().getSocialMediaTokens() != null
                    && branchSettings.getOrganizationUnitSettings().getSocialMediaTokens().getZillowToken() != null
                    && branchSettings.getOrganizationUnitSettings().getSocialMediaTokens().getZillowToken()
                        .getZillowProfileLink() != null ) {
                    surveyAndStage.put( "zillowEnabled", true );
                    zillowScreenName = unitSettings.getSocialMediaTokens().getZillowToken().getZillowScreenName();
                } else if ( regionSettings != null && regionSettings.getSocialMediaTokens() != null
                    && regionSettings.getSocialMediaTokens().getZillowToken() != null
                    && regionSettings.getSocialMediaTokens().getZillowToken().getZillowProfileLink() != null ) {
                		surveyAndStage.put( "zillowEnabled", true );
                			zillowScreenName = unitSettings.getSocialMediaTokens().getZillowToken().getZillowScreenName();
                } else if ( companySettings != null && companySettings.getSocialMediaTokens() != null
                    && companySettings.getSocialMediaTokens().getZillowToken() != null
                    && companySettings.getSocialMediaTokens().getZillowToken().getZillowProfileLink() != null ) {
                    surveyAndStage.put( "zillowEnabled", true );
                    zillowScreenName = unitSettings.getSocialMediaTokens().getZillowToken().getZillowScreenName();
                } else
                    surveyAndStage.put( "zillowEnabled", false );
            }
            
            //put link in map
            String zillowPostUrl = zillowReviewwPostUrl.replaceAll( "\\[screenName\\]", "" + zillowScreenName );
            
            SimpleDateFormat sdf = new SimpleDateFormat(CommonConstants.ZILLOW_SHARE_DATE_FORMAT);
            sdf.setTimeZone(TimeZone.getTimeZone("PST"));
            zillowPostUrl = zillowPostUrl.replaceAll( "\\[dateOfService\\]", "" + sdf.format(surveyDetails.getSurveyTransactionDate()) );
            surveyAndStage.put( "zillowLink", zillowPostUrl );
            
            //check if adding review text is enabled for zillow review share
			if (companySettings.getZillowShareConfig() != null) {

				surveyAndStage.put("isAutoFillReviewContentForZillowPost", companySettings.getZillowShareConfig().isAutoFillReviewContent());

				if (!StringUtils.isEmpty(companySettings.getZillowShareConfig().getSubjectContent()))
					surveyAndStage.put("subjectContentForZillowPost", companySettings.getZillowShareConfig().getSubjectContent());
				
				if (!StringUtils.isEmpty(companySettings.getZillowShareConfig().getReviewFooterContent()))
					surveyAndStage.put("reviewFooterContentForZillowPost", companySettings.getZillowShareConfig().getReviewFooterContent());
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
        } catch ( FileNotFoundException e ) {
            LOG.error( "The third party import file was not found" );
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
    public SurveysAndReviewsVO getSurveysByFilterCriteria( String mood, Long startSurveyID, Date startReviewDate,
        Date startTransactionDate, List<Long> userIds, boolean isRetaken, int startIndex, int count, long companyId )
    {
        LOG.debug( "method getSurveysByStatus started for companyId " + companyId );

        //get mongo survey count
        int mongoSurveyCount = getMongoSurveyCountForCompanyBySurveyStatus( companyId, mood, startSurveyID, startReviewDate,
            startTransactionDate, userIds, isRetaken );
        int endIndex = startIndex + count;

        //get start index and batch size for mongo and sql get survey query
        Map<String, Integer> startindexBatchSizeMap = getStartIndexAndBatchForMonogAndSqlQuery( startIndex, endIndex,
            mongoSurveyCount );
        int monogStartIndex = startindexBatchSizeMap.get( "monogStartIndex" );
        int mongoBatch = startindexBatchSizeMap.get( "mongoBatch" );
        //int sqlStartIndex = startindexBatchSizeMap.get( "sqlStartIndex" );
        //int sqlBatch = startindexBatchSizeMap.get( "sqlBatch" );

        //get survey from mongo
        List<SurveyDetails> surveyDetails = null;
        if ( mongoBatch > 0 )
            surveyDetails = getSurveysForCompanyBySurveyStatus( companyId, monogStartIndex, mongoBatch, mood,
                startSurveyID, startReviewDate, startTransactionDate, userIds, isRetaken );
        else
            surveyDetails = new ArrayList<SurveyDetails>();

        //get corresponding pre initiated record from my sql
        Map<SurveyDetails, SurveyPreInitiation> surveyReviewMap = getPreinititatedSurveyForMongoSurveyDetail( surveyDetails );

        //get corresponding users
        getUsersForMongoSurveyDetail( surveyDetails );

        //get pre initiated survey from sql
        /*List<SurveyPreInitiation> preInitiatedSurveys = null;
        if ( !status.equals( CommonConstants.SURVEY_API_SURVEY_STATUS_COMPLETE ) && sqlBatch > 0 && StringUtils.isEmpty( mood )
            && startReviewDate == null ) {
            Timestamp startEngagementClosedTime = null;
            if ( startTransactionDate != null )
                startEngagementClosedTime = new Timestamp( startTransactionDate.getTime() );
            preInitiatedSurveys = surveyPreInitiationDao.getPreInitiatedSurveyForCompanyByCriteria( sqlStartIndex, sqlBatch,
                userIds, startSurveyID, startEngagementClosedTime, companyId );
        } else {
            preInitiatedSurveys = new ArrayList<SurveyPreInitiation>();
        }*/


        SurveysAndReviewsVO surveyAndReviews = new SurveysAndReviewsVO();
        surveyAndReviews.setInitiatedSurveys( surveyReviewMap );
        //surveyAndReviews.setPreInitiatedSurveys( preInitiatedSurveys );
        LOG.debug( "method getSurveysByStatus ended for companyId " + companyId );
        return surveyAndReviews;
    }
    
    /**
     * 
     * @param startSurveyID
     * @param startTransactionDate
     * @param userIds
     * @param startIndex
     * @param count
     * @param companyId
     * @return
     */
    @Override
    @Transactional
    public SurveysAndReviewsVO getIncompelteSurveysByFilterCriteria( Long startSurveyID, Date startTransactionDate, List<Long> userIds, int startIndex, int count, long companyId )
    {
      //get pre initiated survey from sql
        List<SurveyPreInitiation> preInitiatedSurveys = new ArrayList<SurveyPreInitiation>();;
        if ( count > 0 ) {
            Timestamp startEngagementClosedTime = null;
            if ( startTransactionDate != null )
                startEngagementClosedTime = new Timestamp( startTransactionDate.getTime() );
            preInitiatedSurveys = surveyPreInitiationDao.getPreInitiatedSurveyForCompanyByCriteria( startIndex, count,
                userIds, startSurveyID, startEngagementClosedTime, companyId );
        } 
        
        SurveysAndReviewsVO surveyAndReviews = new SurveysAndReviewsVO();
        surveyAndReviews.setPreInitiatedSurveys( preInitiatedSurveys );
        return surveyAndReviews;
    }

    
    @Override
    @Transactional
    public Integer getSurveysCountByFilterCriteria( String mood, Long startSurveyID, Date startReviewDate,
        Date startTransactionDate, List<Long> userIds, boolean isRetaken, long companyId )
    {
        return getMongoSurveyCountForCompanyBySurveyStatus( companyId, mood, startSurveyID, startReviewDate,
            startTransactionDate, userIds, isRetaken );
        
    }

    /**
     * 
     */
    @Override
    @Transactional
    public Float getSurveysAvgScoreByFilterCriteria( String mood, Long startSurveyID, Date startReviewDate,
        Date startTransactionDate, List<Long> userIds, boolean isRetaken, long companyId )
    {
        return   surveyDetailsDao.getFilteredSurveyAvgScore( companyId, mood, startSurveyID,
            startReviewDate, startTransactionDate, userIds, isRetaken );
        
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
    private int getMongoSurveyCountForCompanyBySurveyStatus( long companyId, String mood, Long startSurveyID,
        Date startReviewDate, Date startTransactionDate, List<Long> userIds , boolean isRetaken )
    {
        LOG.debug( "method getSurveyCountForCompanyBySurveyStatus started for companyId %s ", companyId );

        long mongoSurveyCount = surveyDetailsDao.getFilteredSurveyCount( companyId, mood, startSurveyID,
            startReviewDate, startTransactionDate, userIds, isRetaken );

        LOG.debug( "method getSurveyCountForCompanyBySurveyStatus ended for companyId %s ", companyId );

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
    private List<SurveyDetails> getSurveysForCompanyBySurveyStatus( long companyId, int start, int batchSize,
        String mood, Long startSurveyID, Date startReviewDate, Date startTransactionDate, List<Long> userIds , boolean isRetaken )
    {
        LOG.debug(
            "method getSurveysForCompanyBySurveyStatus started for companyId %s , startIndex %s , batchSize %s ",
            companyId, start, batchSize );
        List<SurveyDetails> surveyDetails = surveyDetailsDao.getFilteredSurveys( start, batchSize, companyId, mood,
            startSurveyID, startReviewDate, startTransactionDate, userIds, isRetaken );

        LOG.debug(
            "method getSurveysForCompanyBySurveyStatus ended for companyId %s , startIndex %s , batchSize %s  ",
            companyId, start, batchSize );
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
                        .getValidSurveyByAgentIdAndCustomeEmail( survey.getAgentId(), survey.getCustomerEmail() );
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
        newSurveyDetails.setBranchName( surveyDetails.getBranchName() );
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
        newSurveyDetails.setRegionName( surveyDetails.getRegionName() );
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
    public List<SurveyPreInitiation> validatePreinitiatedRecord( List<SurveyPreInitiation> surveyPreInitiations , long companyId )
        throws InvalidInputException
    {

        LOG.debug( "Method processPreinitiatedRecord validatePreinitiatedRecord started " );

        int duplicateSurveyInterval = getDuplicateSurveyIntervalForCompany(companyId);

        //fetch company settingd companysettings
        final OrganizationUnitSettings companySettings = organizationUnitSettingsDao
            .fetchOrganizationUnitSettingsById( companyId, MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        for ( SurveyPreInitiation survey : surveyPreInitiations ) {
            // validate, verify, cross-verify and setup the survey pre-initiation object
            validateAndProcessSurveyPreInitiation( survey , duplicateSurveyInterval, companySettings.isAllowPartnerSurvey());

        }

        LOG.debug( "Method processPreinitiatedRecord validatePreinitiatedRecord finished " );
        return surveyPreInitiations;
    }



    @Override
    public boolean isFileAlreadyUploaded( String fileName, String uploaderEmail )
    {
        LOG.debug( "method isFileAlreadyUploaded called" );
        return surveyCsvUploadDao.doesFileUploadExist( fileName, uploaderEmail );
    }
    


    /**
     * @param survey
     * @param allowPartnerSurveyForCompany
     * @throws InvalidInputException
     */
    @Override
    public void validateAndProcessSurveyPreInitiation( SurveyPreInitiation survey, int duplicateSurveyInterval,
        boolean allowPartnerSurveyForCompany ) throws InvalidInputException
    {
    	boolean isUnsubscribed = false;
    	boolean isPartnerSurveyAllowed = true;
    	boolean isIgnored = false;
        // null and syntax checks
        checkForSyntaxInSurveyPreInitiationData( survey );

        // obtain user object from MySQL
        User user = null;
        try {
        		user = obtainUserObjectFromSurveyPreInitiation( survey );
        }catch(InvalidInputException e) {
        		LOG.warn("No user found for the email id {}" , survey.getAgentEmailId() );
        }

        // check for ignored agent email mapping
        if ( isEmailIsIgnoredEmail( survey.getAgentEmailId(), survey.getCompanyId() ) ) {
            LOG.error( "no agent found with this email id and its an ignored record" );
            /*throw new InvalidInputException(
                "Can not process the record. The Service provider email id is set to be ignored" );*/
            survey.setStatus(CommonConstants.STATUS_SURVEYPREINITIATION_IGNORED_RECORD);
            isIgnored = true;
        }
        
        if(unsubscribeService.isUnsubscribed( survey.getCustomerEmailId(), survey.getCompanyId() )) {
            LOG.debug( "Customer has unsubscribed emails either from social survey or from this company." );
            survey.setStatus( CommonConstants.STATUS_SURVEYPREINITIATION_UNSUBSCRIBED );
            isUnsubscribed=true;
        }


        // check if the engagement time was within the valid survey interval
        performSurveyIntervalCheck( survey );

		if (user != null) {
			// Cross check identifiers obtained from different sources
			crossCheckAndVerifySurveyPreInitiationDataUsingDifferentSources(survey, user);

			// check if survey has already been sent to the given email id within the time
			// of discourse
			checkForAlreadyExistingSurvey(survey, user, duplicateSurveyInterval);

			// set the agent ID
			survey.setAgentId(user.getUserId());

			//check if partnerSurvey is allowed if participant type is buyeragent or selleragent
            final AgentSettings agentSettings = organizationUnitSettingsDao.fetchAgentSettingsById( survey.getAgentId() );
            if ( survey.getParticipantType() == CommonConstants.SURVEY_PARTICIPANT_TYPE_BUYER_AGENT
                || survey.getParticipantType() == CommonConstants.SURVEY_PARTICIPANT_TYPE_SELLER_AGENT ) {
                if ( !allowPartnerSurveyForCompany || !agentSettings.isAllowPartnerSurvey() ) {
                    survey.setStatus( CommonConstants.STATUS_SURVEYPREINITIATION_SURVEY_NOT_ALLOWED );
                    isPartnerSurveyAllowed = false;
                }
            }

			// set up survey pre-initiation object for further processing
			if (StringUtils.isEmpty(survey.getAgentName())) {
				survey.setAgentName(user.getFirstName() + user.getLastName() == null ? "" : " " + user.getLastName());
			}

			//if borrower email is equal to coborrower email , we set the status of the survey to duplicate
			//but status should change from STATUS_SURVEYPREINITIATION_NOT_PROCESSED to SURVEY_STATUS_PRE_INITIATED
			//and not from STATUS_SURVEYPREINITIATION_DUPLICATE_RECORD to SURVEY_STATUS_PRE_INITIATED
			if(survey.getStatus() != CommonConstants.STATUS_SURVEYPREINITIATION_DUPLICATE_RECORD && !isUnsubscribed && isPartnerSurveyAllowed && !isIgnored) {
			  //update status
                survey.setStatus(CommonConstants.SURVEY_STATUS_PRE_INITIATED);  
			}
		}//The status shouldn't be marked as mismatched if it's a duplicate 
		else if(survey.getStatus() != CommonConstants.STATUS_SURVEYPREINITIATION_DUPLICATE_RECORD){
			// user is not present so mark record as mismatch
			survey.setStatus(CommonConstants.STATUS_SURVEYPREINITIATION_MISMATCH_RECORD);
		}
		
        
    }


    /**
     * @param survey
     * @throws InvalidInputException
     */
    private void checkForSyntaxInSurveyPreInitiationData( SurveyPreInitiation survey ) throws InvalidInputException
    {

        // Null checks 

        if ( StringUtils.isEmpty( survey.getAgentEmailId() ) ) {
            LOG.error( "Agent email not found , invalid survey " + survey.getSurveyPreIntitiationId() );
            throw new InvalidInputException( "Can not process the record. Service provider email id is missing" );
        }

        if ( StringUtils.isEmpty( survey.getCustomerFirstName() ) ) {
            LOG.error(
                "No first name found for customer, hence this is an invalid survey " + survey.getSurveyPreIntitiationId() );
            throw new InvalidInputException( "Can not process the record. Customer First Name is missing" );
        }

        if ( StringUtils.isEmpty( survey.getCustomerEmailId() ) ) {
            LOG.error( "No customer email id found, invalid survey " + survey.getSurveyPreIntitiationId() );
            throw new InvalidInputException( "Can not process the record. Customer Email id is missing" );
        }


        // email syntax checks
        if ( !organizationManagementService.validateEmail( survey.getAgentEmailId() ) ) {
            LOG.error( "Invalid Agent Email Id " );
            throw new InvalidInputException(
                "Can not process the record. Invalid Service provider email id : " + survey.getAgentEmailId() + "" );
        }

        if ( !Utils.validateCustomerEmail( survey.getCustomerEmailId() ) ) {
            LOG.error( "Invalid Customer Email Id " );
            throw new InvalidInputException(
                "Can not process the record. Invalid Customer email id : " + survey.getCustomerEmailId() + "" );
        }

    }


    /**
     * @param survey
     * @return
     * @throws InvalidInputException
     */
    private User obtainUserObjectFromSurveyPreInitiation( SurveyPreInitiation survey ) throws InvalidInputException
    {
        try {

            return userManagementService.getActiveAgentByEmailAndCompany( survey.getCompanyId(), survey.getAgentEmailId() );

        } catch ( InvalidInputException | NoRecordsFetchedException e ) {
            LOG.error( "No user found in database for the email id: " + survey.getAgentEmailId() + " and company id : "
                + survey.getCompanyId() );
            throw new InvalidInputException(
                "Can not process the record. No service provider found with email address :  " + survey.getAgentEmailId() );
        }
    }


    /**
     * @param survey
     * @param user
     * @throws InvalidInputException
     */
    private void crossCheckAndVerifySurveyPreInitiationDataUsingDifferentSources( SurveyPreInitiation survey, User user )
        throws InvalidInputException
    {
        if ( user.getCompany() == null ) {
            LOG.error( "Agent doesnt have an company associated with it " );
            throw new InvalidInputException(
                "Can not process the record. No service provider found with email address :  " + survey.getAgentEmailId() );
        }


    }


    /**
     * @param survey
     * @throws InvalidInputException
     */
    private void performSurveyIntervalCheck( SurveyPreInitiation survey ) throws InvalidInputException
    {
        //get valid survey intervals
        Calendar calendar = Calendar.getInstance();
        calendar.add( Calendar.DATE, -validSurveyInterval );
        Date date = calendar.getTime();

        if ( survey.getEngagementClosedTime() == null ) {
            LOG.error( "No transaction date found " + survey.getSurveyPreIntitiationId() );
            throw new InvalidInputException( "Can not process the record. No transaction date found." );
        } else if ( survey.getEngagementClosedTime().before( date ) ) {
            LOG.error( "An old record found : " + survey.getSurveyPreIntitiationId() );
            throw new InvalidInputException( "Can not process the record. Request for customer " + survey.getCustomerFirstName()
                + " is older than " + validSurveyInterval + " days." );
        }
    }


    /**
     * @param survey
     * @param user
     * @throws InvalidInputException
     */
    private void checkForAlreadyExistingSurvey( SurveyPreInitiation survey, User user, int duplicateSurveyInterval ) throws InvalidInputException
    {
        List<SurveyPreInitiation> incompleteSurveyCustomers = null;

        // get incomplete survey depending on the survey re-take interval
        if ( duplicateSurveyInterval > 0 ) {
            incompleteSurveyCustomers = surveyPreInitiationDao.getValidSurveyByAgentIdAndCustomeEmailForPastNDays( user.getUserId(),
                survey.getCustomerEmailId(), duplicateSurveyInterval );
        } else {
            incompleteSurveyCustomers = surveyPreInitiationDao.getValidSurveyByAgentIdAndCustomeEmail( user.getUserId(),
                survey.getCustomerEmailId() );
        }

     // checking the status for unsubscribed before setting it to duplicate.
        if ( incompleteSurveyCustomers != null && incompleteSurveyCustomers.size() > 0 && survey.getStatus() != CommonConstants.STATUS_SURVEYPREINITIATION_UNSUBSCRIBED) 
        	survey.setStatus(CommonConstants.STATUS_SURVEYPREINITIATION_DUPLICATE_RECORD);

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
    
    
    /**
     * method to build survey completion threshold Map
     * @param survey
     * @return Map
     * @throws InvalidInputException 
     * @throws NoRecordsFetchedException 
     */
    @Override
    public Map<String, Double> buildSurveyCompletionThresholdMap( SurveyDetails survey )
        throws InvalidInputException, NoRecordsFetchedException
    {
        if ( survey == null || StringUtils.isEmpty( survey.get_id() ) ) {
            throw new InvalidInputException(
                "method checkAndSendSurveyCompletedMailToAdminsAndAgent(): Survey details are not specifed." );
        }

        LOG.debug( "checking agent notification threshold for survey with ID: {}", survey.get_id() );
        OrganizationUnitSettings companySettings = null;
        OrganizationUnitSettings regionSettings = null;
        OrganizationUnitSettings branchSettings = null;
        OrganizationUnitSettings agentSettings = null;

        double surveyCompletionThresholdForAgent = 0.0d;
        double surveyCompletionThresholdForBranch = 0.0d;
        double surveyCompletionThresholdForRegion = 0.0d;
        double surveyCompletionThresholdForCompany = 0.0d;

        Map<String, Double> surveyCompletionThresholdMap = new HashMap<>();


        if ( survey.getAgentId() > 0 ) {
            agentSettings = organizationManagementService.getAgentSettings( survey.getAgentId() );
        }
        if ( survey.getBranchId() != 0 ) {
            // WARNING: doesn't contain region data for the branch, use "getBranchSettings()" if needed
            branchSettings = organizationManagementService.getBranchSettingsDefault( survey.getBranchId() );
        }
        if ( survey.getRegionId() != 0 ) {
            regionSettings = organizationManagementService.getRegionSettings( survey.getRegionId() );
        }
        if ( survey.getCompanyId() != 0 ) {
            companySettings = organizationManagementService.getCompanySettings( survey.getCompanyId() );
        }


        if ( agentSettings != null && agentSettings.getSurvey_settings() != null ) {
            surveyCompletionThresholdForAgent = agentSettings.getSurvey_settings().getSurveyCompletedMailThreshold();
        }
        if ( branchSettings != null && branchSettings.getSurvey_settings() != null ) {
            surveyCompletionThresholdForBranch = branchSettings.getSurvey_settings().getSurveyCompletedMailThreshold();
        }
        if ( regionSettings != null && regionSettings.getSurvey_settings() != null ) {
            surveyCompletionThresholdForRegion = regionSettings.getSurvey_settings().getSurveyCompletedMailThreshold();
        }
        if ( companySettings != null && companySettings.getSurvey_settings() != null ) {
            surveyCompletionThresholdForCompany = companySettings.getSurvey_settings().getSurveyCompletedMailThreshold();
        }

        surveyCompletionThresholdMap.put( CommonConstants.AGENT_ID_COLUMN, surveyCompletionThresholdForAgent );
        surveyCompletionThresholdMap.put( CommonConstants.BRANCH_ID_COLUMN, surveyCompletionThresholdForBranch );
        surveyCompletionThresholdMap.put( CommonConstants.REGION_ID_COLUMN, surveyCompletionThresholdForRegion );
        surveyCompletionThresholdMap.put( CommonConstants.COMPANY_ID_COLUMN, surveyCompletionThresholdForCompany );

        LOG.debug( "method checkAndSendSurveyCompletedMailToAdminsAndAgent() finished for survey with Id: {}",
            survey.get_id() );
        return surveyCompletionThresholdMap;
    }


    @Override
    public Map<String, String> buildPreferredAdminEmailListForSurvey( SurveyDetails survey, double companyThreshold,
        double regionThreshold, double branchThreshold ) throws InvalidInputException
    {
        if ( survey == null || StringUtils.isEmpty( survey.get_id() ) ) {
            throw new InvalidInputException(
                "method buildPreferredAdminEmailListForSurvey(): Survey details are not specifed." );
        }

        LOG.debug( "loading email IDs for survey with ID: {}", survey.get_id() );
        Map<String, String> emailMap = new HashMap<>();

        // get the email Map for every hierarchy
        Map<String, Map<String, String>> adminMap = getEmailIdsOfAdminsInHierarchy( survey.getAgentId() );

        //  process branch administrators
        if ( branchThreshold <= survey.getScore() && adminMap.get( CommonConstants.BRANCH_ID_COLUMN ) != null ) {
            emailMap.putAll( adminMap.get( CommonConstants.BRANCH_ID_COLUMN ) );
        }

        //  process region administrators
        if ( regionThreshold <= survey.getScore() && adminMap.get( CommonConstants.REGION_ID_COLUMN ) != null ) {
            emailMap.putAll( adminMap.get( CommonConstants.REGION_ID_COLUMN ) );
        }

        //  process company administrators
        if ( companyThreshold <= survey.getScore() && adminMap.get( CommonConstants.COMPANY_ID_COLUMN ) != null ) {
            emailMap.putAll( adminMap.get( CommonConstants.COMPANY_ID_COLUMN ) );
        }

        LOG.debug( "method buildPreferredAdminEmailListForSurvey() finished for survey with ID: {}", survey.get_id() );
        return emailMap;
    }

    @Override
    public boolean createEntryForSurveyUploadWithCsv( String hierarchyType, MultipartFile tempFile, String fileName,
        long hierarchyId, User user, String uploaderEmail ) throws NonFatalException, IOException
    {
        LOG.debug( "createEntryForSurveyUploadWithCsv started for user with Id: {}", user.getUserId() );
        String stamp = "";

        if ( hierarchyId <= 0 ) {
            throw new InvalidInputException( "Please provide a valid hierarchy Identifier." );
        }

        if ( Arrays.asList( CommonConstants.REGION_ID, CommonConstants.COMPANY_ID, CommonConstants.BRANCH_ID )
            .contains( StringUtils.defaultString( hierarchyType ) ) ) {
            stamp = "SURVEY_CSV_UPLOAD_ADMIN_";
        } else if ( CommonConstants.AGENT_ID.equals( StringUtils.defaultString( hierarchyType ) ) ) {
            stamp = "SURVEY_CSV_UPLOAD_AGENT_";
        } else {
            throw new InvalidInputException( "Please provide a valid hierarchy type." );
        }

        // Set the new filename
        String savedFileName = stamp + user.getUserId() + "_" + new Date( System.currentTimeMillis() ).toString() + ".csv";

        File convFile = new File( URLEncoder.encode( fileName, "UTF-8" ) );
        tempFile.transferTo( convFile );
        
        String fileUrl = fileUploadService.uploadFileAtSurveyCsvBucket( convFile, savedFileName );

        SurveyCsvInfo csvInfo = new SurveyCsvInfo();
        csvInfo.setFileName( fileName );
        csvInfo.setFileUrl( fileUrl );
        csvInfo.setHierarchyType( hierarchyType );
        csvInfo.setHierarchyId( hierarchyId );
        csvInfo.setUploadedDate( new Date() );
        csvInfo.setInitiatedUserId( user.getUserId() );
        csvInfo.setCompanyId( user.getCompany().getCompanyId() );
        csvInfo.setUploaderEmail( uploaderEmail );
        csvInfo.setStatus( CommonConstants.STATUS_ACTIVE );

        surveyCsvUploadDao.createEntryForSurveyCsvUpload( csvInfo );
        LOG.debug( "createEntryForSurveyUploadWithCsv completed for user with Id: {}", user.getUserId() );
        return true;
    }


    @Override
    public void processActiveSurveyCsvUploads()
    {
        LOG.debug( "method processActiveSurveyCsvUploads started" );

        try {

            // update last start time
            batchTrackerService.getLastRunEndTimeAndUpdateLastStartTimeByBatchType(
                CommonConstants.BATCH_TYPE_ACCOUNT_DEACTIVATOR, CommonConstants.BATCH_NAME_SURVEY_CSV_UPLOAD_PROCESSOR );

            // get the list of active CSV uploads
            List<SurveyCsvInfo> surveyCsvUploads = surveyCsvUploadDao.getActiveSurveyCsvUploads();

            // check if there are active uploads pending, if not, terminate the method 
            if ( surveyCsvUploads == null || surveyCsvUploads.size() == 0 ) {
                LOG.debug( "method processActiveSurveyCsvUploads terminated due to non-existent upload requests." );
                return;
            }

            for ( SurveyCsvInfo csvInfo : surveyCsvUploads ) {


                //check for valid uploaded email and e-mail social survey administrator if invalid.
                if ( StringUtils.isEmpty( csvInfo.getUploaderEmail() )
                    || !organizationManagementService.validateEmail( csvInfo.getUploaderEmail() ) ) {

                    LOG.error( "Uploader Email is Invalid." );

                    // set the status for the upload
                    surveyCsvUploadDao.updateStatusForSurveyCsvUpload( csvInfo.get_id(), STATUS_UPLOADER_EMAIL_INVALID );

                    // send mail to administrator indicating that uploaded email is not present or valid.
                    emailServices.sendEmailToAdminForUnsuccessfulSurveyCsvUpload( csvInfo,
                        "Can't process CSV file, reason: Uploader Email is Invalid." );
                    continue;
                }


                try {

                    // perform null and other necessary checks
                    performPreliminaryChecks( csvInfo );

                    // process the CSV file for the active upload if generic errors aren't encountered
                    Map<Integer, List<String>> csvData = csvUtils.readFromCsv( csvInfo.getFileUrl() );

                    // each row in the CSV file is to be processed and results are to be mailed. 
                    List<String> results = processSurveyCsvData( csvInfo, csvData );

                    // email the results of CSV upload process to the concerned party
                    emailServices.sendEmailToUploaderForSuccessfulSurveyCsvUpload( csvInfo, htmlReady( results ) );

                    // update the database with latest details
                    csvInfo.setStatus( STATUS_CSV_UPLOAD_COMPLETE );
                    csvInfo.setCsvUploadCompletedDate( new Date() );
                    surveyCsvUploadDao.updateSurveyCsvUpload( csvInfo );


                } catch ( InvalidInputException | IOException expectedGenericException ) {

                    LOG.error( "Generic exception encountered for csv upload with ID: " + csvInfo.get_id() );

                    // set the status for the upload
                    surveyCsvUploadDao.updateStatusForSurveyCsvUpload( csvInfo.get_id(), STATUS_CSV_UPLOAD_INCOMPLETE );

                    // send unsuccessful mail to the uploaded email
                    emailServices.sendEmailToUploaderForUnsuccessfulSurveyCsvUpload( csvInfo,
                        expectedGenericException.getMessage() );
                }
            }

            //updating last run time for batch in database
            batchTrackerService.updateLastRunEndTimeByBatchType( CommonConstants.BATCH_TYPE_SURVEY_CSV_UPLOAD_PROCESSOR );
            LOG.debug( "method processActiveSurveyCsvUploads finished" );

        } catch ( Exception unforseenError ) {
            try {
                LOG.error( "Error in processActiveSurveyCsvUploads", unforseenError );
                //update batch tracker with error message
                batchTrackerService.updateErrorForBatchTrackerByBatchType(
                    CommonConstants.BATCH_TYPE_SURVEY_CSV_UPLOAD_PROCESSOR, unforseenError.getMessage() );
                //send report bug mail to admin
                batchTrackerService.sendMailToAdminRegardingBatchError( CommonConstants.BATCH_NAME_SURVEY_CSV_UPLOAD_PROCESSOR,
                    System.currentTimeMillis(), unforseenError );
            } catch ( NoRecordsFetchedException | InvalidInputException unableToUpdateBatchTracker ) {
                LOG.error( "Error while updating error message in processActiveSurveyCsvUploads " );
            } catch ( UndeliveredEmailException unableToSendEmail ) {
                LOG.error( "Error while sending report excption mail to admin " );
            }
        }
    }


    /**
     * @param csvInfo
     * @throws InvalidInputException
     */
    private void performPreliminaryChecks( SurveyCsvInfo csvInfo ) throws InvalidInputException
    {
        // null checks
        if ( StringUtils.isEmpty( csvInfo.getHierarchyType() )
            || !Arrays.asList( CommonConstants.COMPANY_ID, CommonConstants.REGION_ID, CommonConstants.BRANCH_ID,
                CommonConstants.AGENT_ID ).contains( csvInfo.getHierarchyType() )
            || csvInfo.getHierarchyId() <= 0 || csvInfo.getCompanyId() <= 0 ) {
            LOG.error( "Hierarchy details are missing." );
            throw new InvalidInputException( "Can't process CSV file, reason: Hierarchy details are missing or Invalid" );
        }

        if ( StringUtils.isEmpty( csvInfo.getFileUrl() ) ) {
            LOG.error( "CSV file details are missing." );
            throw new InvalidInputException( "Can't process CSV file, reason: CSV file details are missing." );
        }

    }


    /**
     * method to process each row from CSV file and start survey process for verified rows
     * @param csvInfo
     * @param csvData
     * @return Map<Integer, List<String>>
     */
    private List<String> processSurveyCsvData( SurveyCsvInfo csvInfo, Map<Integer, List<String>> csvData )
        throws InvalidInputException
    {
        LOG.debug( "Method processSurveyCsvData called for upload with Id:" + csvInfo.get_id() );

        List<String> results = null;
        User agent = null;
        Map<String, Set<String>> processedSurveyEmails = null;
        boolean isFromAgentPopup = CommonConstants.AGENT_ID.equals( csvInfo.getHierarchyType() );

        if ( csvData != null && csvData.size() > 0 ) {

            // initialize required data sets
            processedSurveyEmails = new HashMap<>();
            results = new ArrayList<>();

            if ( isFromAgentPopup ) {
                try {
                    agent = userManagementService.getUserByUserId( csvInfo.getHierarchyId() );
                } catch ( InvalidInputException userNotFound ) {
                    LOG.error( "No agent found for the given upload." );
                    throw new InvalidInputException( "No agent found for the given upload." );
                }
            }

            if( validateHeader( csvData.get( 1 ), isFromAgentPopup ? AGENT_HEADER : ADMIN_HEADER ) ){
                LOG.debug( "Header validated." );
                csvData.remove( 1 );
            } else {
                LOG.error( "Invalid header" );
                throw new InvalidInputException( "Invalid Header." );
            }

            // iterate over each row of CSV,validate and process the corresponding Survey Pre-Initiation Object
            for ( Entry<Integer, List<String>> entry : csvData.entrySet() ) {

                // customer details
                String customerFirstName = "";
                String customerLastName = "";
                String customerEmail = "";
                String customerContactNumber = "";

                // agent Email
                String agentEmail = "";

                try {

                    // parse required values
                    try {
                        if ( isFromAgentPopup ) {

                            agentEmail = agent.getEmailId();

                            customerFirstName = entry.getValue().get( SURVEY_CSV_CUSTOMER_FIRST_NAME_INDEX_FOR_AGENT );
                            customerLastName = entry.getValue().get( SURVEY_CSV_CUSTOMER_LAST_NAME_INDEX_FOR_AGENT );
                            customerEmail = entry.getValue().get( SURVEY_CSV_CUSTOMER_EMAIL_INDEX_FOR_AGENT );
                            if( entry.getValue().size() > 3 ) {
                            	customerContactNumber = entry.getValue().get( SURVEY_CSV_CUSTOMER_CONTACT_NUMBER_INDEX_FOR_AGENT );
                            }

                        } else {

                            agentEmail = entry.getValue().get( SURVEY_CSV_AGENT_EMAIL_INDEX_FOR_ADMIN );
                            customerFirstName = entry.getValue().get( SURVEY_CSV_CUSTOMER_FIRST_NAME_INDEX_FOR_ADMIN );
                            customerLastName = entry.getValue().get( SURVEY_CSV_CUSTOMER_LAST_NAME_INDEX_FOR_ADMIN );
                            customerEmail = entry.getValue().get( SURVEY_CSV_CUSTOMER_EMAIL_NAME_INDEX_FOR_ADMIN );
                            if( entry.getValue().size() > 4 ) {
                            	customerContactNumber = entry.getValue().get( SURVEY_CSV_CUSTOMER_CONTACT_NUMBER_INDEX_FOR_ADMIN );
                            }

                        }
                    } catch ( NullPointerException | IndexOutOfBoundsException unableToParseRow ) {
                        LOG.error( "Record under concern does not have the required data." );
                        throw new InvalidInputException(
                            "The Record under concern does not point to the required data in order." );
                    }


                    // check if the agent customer pair is already processed
                    if ( processedSurveyEmails.containsKey( agentEmail )
                        && processedSurveyEmails.get( agentEmail ).contains( customerEmail ) ) {
                        LOG.error( "Record under concern is a duplicate of agent-customer pair processed before in the list." );
                        throw new InvalidInputException(
                            "The Record under concern is a duplicate of agent-customer pair processed before in the list." );
                    }


                    // construct survey pre-initiation object from the extracted row
                    SurveyPreInitiation survey = buildSurveyPreInitiationFromCsvRecord( csvInfo.getCompanyId(), agentEmail,
                        customerFirstName, customerLastName, customerEmail, customerContactNumber, csvInfo.getUploadedDate() );


                    // perform all the necessary checks for the SPI object 
                    int duplicateSurveyInterval = getDuplicateSurveyIntervalForCompany(csvInfo.getCompanyId());
                    final OrganizationUnitSettings companySettings = organizationUnitSettingsDao
                        .fetchOrganizationUnitSettingsById( csvInfo.getCompanyId(),
                            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
                    validateAndProcessSurveyPreInitiation( survey , duplicateSurveyInterval, companySettings.isAllowPartnerSurvey() );


                    // depending on the hierarchy at which the file was uploaded, start the survey process
                    if ( isFromAgentPopup
                        || isConformingToGivenHierarchy( survey, csvInfo.getHierarchyId(), csvInfo.getHierarchyType() ) ) {
                        saveSurveyPreInitiationObject( survey );

                        // add the success result to the result map
                        results.add( "row " + entry.getKey() + ": OK, Record processed successfully" );

                        // add to the list of processed records
                        if ( processedSurveyEmails.containsKey( agentEmail ) ) {
                            processedSurveyEmails.get( agentEmail ).add( customerEmail );
                        } else {
                            Set<String> customerSet = new HashSet<>(); 
                            customerSet.add( customerEmail );
                            processedSurveyEmails.put( agentEmail, customerSet );
                        }

                    } else {
                        LOG.error( "record under concern has the agent who does not belong to the uploaded hierarchy" );
                        throw new InvalidInputException(
                            "The Record under concern has the agent who does not belong to the uploaded hierarchy." );
                    }


                } catch ( InvalidInputException rowException ) {
                    LOG.error( "Can't process the row further." );

                    // add the failed result to the result map
                    results.add( "row " + entry.getKey() + ": Failed, " + rowException.getMessage() );
                }
            }
        }
        return results;
    }


    private boolean validateHeader( List<String> header, List<String> standardHeader ) throws InvalidInputException
    {
        LOG.debug( "validateHeader() started." );
        if( header == null || header.size() < standardHeader.size() ){
            return false;
        }
        
        try{
            int counter;
            for( counter = 0; counter < standardHeader.size(); counter++ ){
                if( !StringUtils.equalsIgnoreCase( standardHeader.get( counter ), header.get( counter ) ) ){
                    return false;
                }
            }
        } catch( IndexOutOfBoundsException headerError ){
            LOG.error( "Header does not have enough values." );
            throw new InvalidInputException( "Header does not have enough values." );
        }
        
        
        LOG.debug( "validateHeader() finished." );
        return true;
    }


    /**
     * @param companyId
     * @param agentEmail
     * @param customerFirstName
     * @param customerLastName
     * @param customerEmail
     * @param uploadedDate
     * @return
     */
    private SurveyPreInitiation buildSurveyPreInitiationFromCsvRecord( long companyId, String agentEmail,
        String customerFirstName, String customerLastName, String customerEmail, String customerContactNumber, Date uploadedDate )
    {
        LOG.debug( "method buildSurveyPreInitiationFromCsvRecord called" );
        SurveyPreInitiation surveyPreInitiation = new SurveyPreInitiation();
        surveyPreInitiation.setAgentEmailId( agentEmail );
        surveyPreInitiation.setCompanyId( companyId );

        surveyPreInitiation.setCustomerEmailId( customerEmail );
        surveyPreInitiation.setCustomerFirstName( customerFirstName );
        surveyPreInitiation.setCustomerLastName( customerLastName );
        
        if( !StringUtils.isEmpty( customerContactNumber ) ) {
        	
        	surveyPreInitiation.setCustomerContactNumber( filterContactNumber( customerContactNumber ) );
        }

        surveyPreInitiation.setSurveySource( "CSV_UPLOAD" );
        surveyPreInitiation.setEngagementClosedTime( new Timestamp( uploadedDate.getTime() ) );

        surveyPreInitiation.setCreatedOn( new Timestamp( System.currentTimeMillis() ) );
        surveyPreInitiation.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        surveyPreInitiation.setLastReminderTime( utils.convertEpochDateToTimestamp() );
        surveyPreInitiation.setStatus( CommonConstants.STATUS_SURVEYPREINITIATION_NOT_PROCESSED );
        return surveyPreInitiation;

    }
    
    private String filterContactNumber( String customerContactNumber ) {
		
    	boolean flag = false;
		StringBuilder builder = new StringBuilder( customerContactNumber );

		if( builder.charAt( 0 ) == '+' ) {

			builder.delete(0, 1);
			flag= true;
		}
		builder.replace(0, builder.length(), builder.toString().replaceAll("[^\\d]", ""));
		
		if( flag && builder.length() > 0 ) {
			builder.insert(0, '+');
		}
		return builder.substring( 0, Math.min( builder.length(), 30 ) );
	}


    /**
     * @param survey
     * @param hierarchyId
     * @param hierarchyType
     * @return
     * @throws InvalidInputException 
     */
    private boolean isConformingToGivenHierarchy( SurveyPreInitiation survey, long hierarchyId, String hierarchyType ) throws InvalidInputException
    {
        LOG.debug( "method isConformingToGivenHierarchy called" );
        Map<String, Object> queries = new HashMap<>();

        // add the user Id
        queries.put( CommonConstants.USER_COLUMN, userManagementService.getUserByUserId( survey.getAgentId() ) );


        if ( CommonConstants.COMPANY_ID.equals( hierarchyType ) ) {
            // if an agent profile exists under any region or branch then the survey is qualified.
            queries.put( CommonConstants.COMPANY_COLUMN, userManagementService.getCompanyById( hierarchyId ) );
        } else {
            queries.put( hierarchyType, hierarchyId );
        }

        // check if the user is an agent under the hierarchy
        try {
            queries.put( CommonConstants.PROFILE_MASTER_COLUMN,
                userManagementService.getProfilesMasterById( CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) );
        } catch ( InvalidInputException error ) {
            LOG.error( "Unable to determine profiile master ID" );
            return false;
        }

        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
        List<UserProfile> agentProfiles = userProfileDao.findByKeyValue( UserProfile.class, queries );

        if ( agentProfiles != null && agentProfiles.size() > 0 ) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * @param results
     * @return
     */
    private String htmlReady( List<String> results )
    {
        StringBuilder resultString = null;
        if ( results != null && results.size() > 0 ) {
            resultString = new StringBuilder( "<br/>" );
            for ( String rowResult : results ) {
                resultString.append( rowResult ).append( "<br/>" );
            }
        } else {
            resultString = new StringBuilder( "No records found." );
        }
        return resultString.toString();
    }
    
    
    private void updateSurveySourcesInOldRecords(List<SurveyPreInitiation> existingSurveyPreinititionList , SurveyPreInitiation latestSurveyPreInitiation) 
	{

		LOG.info("method updateSurveySourcesInOldRecords started");
		// check if new survey is from verified source
		if (!CommonConstants.CRM_UNVERIFIED_SOURCES.contains(latestSurveyPreInitiation.getSurveySource())) {
			// check if any old survey for same customer is from non verified source
			for (SurveyPreInitiation currentSurveyPreinitition : existingSurveyPreinititionList) {
				// if it is from non verified source than update the source and source id
				if (CommonConstants.CRM_UNVERIFIED_SOURCES.contains(currentSurveyPreinitition.getSurveySource())) {
					currentSurveyPreinitition.setSurveySourceId(latestSurveyPreInitiation.getSurveySourceId());
					currentSurveyPreinitition.setSurveySource(latestSurveyPreInitiation.getSurveySource());
					surveyPreInitiationDao.update(currentSurveyPreinitition);

					// update mongo survey detail as well
					SurveyDetails surveyDetails = surveyDetailsDao
							.getSurveyBySurveyPreIntitiationId(currentSurveyPreinitition.getSurveyPreIntitiationId());
					if (surveyDetails != null) {
						surveyDetails.setSource(latestSurveyPreInitiation.getSurveySource());
						surveyDetails.setSourceId(latestSurveyPreInitiation.getSurveySourceId());
						surveyDetailsDao.updateSourceDetailInExistingSurveyDetails(surveyDetails);
					}
				}
			}

		}

	}
    
    @Override 
    public String[] fetchSwearWords(String entityType, long entityId) throws InvalidInputException {
        String[] returnSwear = organizationUnitSettingsDao.fetchSavedSwearWords( entityType, entityId ).getSwearWords();
        LOG.debug( "returnSwear:{}",returnSwear );
        return returnSwear;
    }
    
    @Override 
    public void updateSwearWords( String entityType, long entityId, String[] swearWords ) throws InvalidInputException {
        organizationUnitSettingsDao.updateSwearWords( entityType, entityId , swearWords);
    }

    @Override
    public List<BulkWriteErrorVO> saveOrUpdateReviews( List<SurveyDetailsVO> surveyDetails )
        throws InvalidInputException, ParseException
    {
        List<SurveyDetails> existingReviews = new ArrayList<>(  );
        List<SurveyDetails> newReviews = new ArrayList<>(  );
        List<BulkWriteErrorVO> errors = new ArrayList<>( );

        //first  check if the survey is already existing in survey details
        //yes => update the survey
        //else => insert it
        for(SurveyDetailsVO surveyDetail: surveyDetails){
            //survey exists then update the review
            SurveyDetails existingReview = checkIfReviewExists(surveyDetail);
            if( existingReview != null ){
                existingReview.setReview( surveyDetail.getReview() );
                existingReview.setSurveyUpdatedDate( new Date(surveyDetail.getSurveyUpdatedDate()) );
                existingReview.setScore( surveyDetail.getScore() );
                existingReview.setFbRecommendationType( surveyDetail.getFbRecommendationType() );
                existingReviews.add( existingReview );
            }
            else
                newReviews.add( convertSurveyVOToEntity( surveyDetail ) );
        }

        //insert the new reviews
        if(newReviews != null && !newReviews.isEmpty()){
            try{
                surveyDetailsDao.insertSurveyDetails( newReviews );
            } catch (  BulkOperationException bulkWriteException ) {
                List<BulkWriteError> bulkWriteErrors = bulkWriteException.getErrors();
                for(BulkWriteError error: bulkWriteErrors){
                    errors.add( new BulkWriteErrorVO( error.getIndex(), error.getCode(), error.getMessage() ) );
                }
            }
        }

        //update the reviews
        if(existingReviews != null && existingReviews.size() > 0) {
            if(updateReviews(existingReviews) == existingReviews.size()){
                LOG.info( "Review have been successfully updated" );
            } else{
                LOG.warn( "Something went wrong while bulk updating the reviews" );
            }
        }

         return errors;
    }
    
    private int updateReviews( List<SurveyDetails> existingReviews )
    {
        return surveyDetailsDao.bulkUpdateReviews(existingReviews);
    }


    private SurveyDetails checkIfReviewExists( SurveyDetailsVO surveyDetail ) throws InvalidInputException
    {
        LOG.info( "checking if survey already exist in database with review id : {}", surveyDetail.getSourceId() );
        Map<String, Object> queries = new HashMap<String, Object>();
        switch ( surveyDetail.getProfileType() ){
            case MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION : {
                queries.put( CommonConstants.COMPANY_ID_COLUMN, surveyDetail.getCompanyId() );
                break;
            }
            case MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION: {
                queries.put( CommonConstants.REGION_ID_COLUMN, surveyDetail.getCompanyId() );
                break;
            }
            case MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION: {
                queries.put( CommonConstants.BRANCH_ID_COLUMN, surveyDetail.getCompanyId() );
                break;
            }
            case MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION: {
                queries.put( CommonConstants.AGENT_ID_COLUMN, surveyDetail.getCompanyId() );
                break;
            }
            default : throw new InvalidInputException( "Ivalid ProfileType {}", surveyDetail.getProfileType() );
        }
        queries.put( CommonConstants.SURVEY_SOURCE_ID_COLUMN, surveyDetail.getSourceId());
        queries.put(  CommonConstants.SURVEY_SOURCE_COLUMN, surveyDetail.getSource() );
        SurveyDetails existingSurveyDetails = surveyDetailsDao.getReviewByQueryMap( queries );
        return existingSurveyDetails;
    }

    private SurveyDetails convertSurveyVOToEntity(SurveyDetailsVO surveyVO) throws ParseException {
        SurveyDetails surveyDetails = new SurveyDetails();
        surveyDetails.setAgentId( surveyVO.getAgentId() );
        surveyDetails.setCompanyId( surveyVO.getCompanyId() );
        surveyDetails.setRegionId( surveyVO.getRegionId() );
        surveyDetails.setBranchId( surveyVO.getBranchId() );
        surveyDetails.setCompleteProfileUrl( surveyVO.getCompleteProfileUrl() );
        surveyDetails.setAgentName( surveyVO.getAgentName() );
        surveyDetails.setCustomerFirstName( surveyVO.getCustomerFirstName() );
        surveyDetails.setCustomerLastName( surveyVO.getCustomerLastName() );
        surveyDetails.setSource( surveyVO.getSource() );
        surveyDetails.setSourceId( surveyVO.getSourceId() );
        surveyDetails.setReview( surveyVO.getReview() );
        surveyDetails.setSummary( surveyVO.getSummary() );
        surveyDetails.setScore( surveyVO.getScore() );
        surveyDetails.setSurveyTransactionDate( new Date(surveyVO.getSurveyTransactionDate()) );
        surveyDetails.setStage( surveyVO.getStage() );
        surveyDetails.setAgreedToShare( surveyVO.getAgreedToShare() );
        surveyDetails.setSurveySentDate( new Date(surveyVO.getSurveySentDate()) );
        surveyDetails.setSurveyCompletedDate( new Date(surveyVO.getSurveyCompletedDate()) );
        surveyDetails.setCreatedOn( new Date(surveyVO.getCreatedOn()) );
        surveyDetails.setModifiedOn( new Date(surveyVO.getModifiedOn()) );
        surveyDetails.setSurveyUpdatedDate( new Date(surveyVO.getSurveyUpdatedDate()) );
        surveyDetails.setShowSurveyOnUI( surveyVO.getShowSurveyOnUI() );
        surveyDetails.setFbRecommendationType( surveyVO.getFbRecommendationType() );
        return surveyDetails;
    }

    public double calScore(List<SurveyResponse> surveyResponse) {
    	 double noOfResponse = 0;
         double answer = 0;
    	 for ( SurveyResponse response : surveyResponse ) {
             if ( response.getQuestionType().equals( CommonConstants.QUESTION_TYPE_SCALE  )
                 || response.getQuestionType().equals( CommonConstants.QUESTION_TYPE_SMILE )
                 || response.getQuestionType().equals( CommonConstants.QUESTION_TYPE_STAR )
                 || response.getQuestionType().equals( CommonConstants.QUESTION_TYPE_0TO10)
                 && ( response.getAnswer() != null && !response.getAnswer().isEmpty() )) {
                 //check if question type is 0to10 and divide answer by 2
                 if(response.getQuestionType().equals( CommonConstants.QUESTION_TYPE_0TO10)){
                     if(response.isConsiderForScore()){
                         int npsAnswer = Integer.parseInt( response.getAnswer() );
                         answer += (double) npsAnswer/2;
                         noOfResponse++;
                     }
                 }else{
                     answer += Integer.parseInt( response.getAnswer() );
                     noOfResponse++;
                 }
             }
         }
    	 if(noOfResponse != 0) {
    		 return Math.round( answer / noOfResponse * 1000.0 ) / 1000.0;
    	 } else return -1;
    }
    
    @Override
	public double getNpsScore(List<SurveyResponse> surveyResponse) {
		for (SurveyResponse response : surveyResponse) {
			if (response.getQuestionType().equals(CommonConstants.QUESTION_TYPE_0TO10)
					&& (response.getAnswer() != null && !response.getAnswer().isEmpty())
					&& response.getIsNpsQuestion()) {
				// check if isNpsQuestion and set npsScore
				return Integer.parseInt(response.getAnswer());
			}
		}
		return -1;
	}

    
    /**
     * 
     */
    @Override
	public void streamSurveyProcessRequest(SurveyDetails surveyDetails)
    {
    		LOG.info("Method streamSurveyProcessRequest started for survey with id {} " , surveyDetails.get_id());
		SurveyProcessData surveyProcessData = new SurveyProcessData();
		surveyProcessData.setSurveyId(surveyDetails.getSurveyPreIntitiationId());
		surveyProcessData.setAgentId(surveyDetails.getAgentId());
		surveyProcessData.setBranchId(surveyDetails.getBranchId());
		surveyProcessData.setRegionId(surveyDetails.getRegionId());
		surveyProcessData.setCompanyId(surveyDetails.getCompanyId());
		surveyProcessData.setCustomerEmail(surveyDetails.getCustomerEmail());
		surveyProcessData.setCustomerFirstName(surveyDetails.getCustomerFirstName());
		surveyProcessData.setCustomerLastName(surveyDetails.getCustomerLastName());
		surveyProcessData.setMood(surveyDetails.getMood());
		surveyProcessData.setNpsScore(surveyDetails.getNpsScore());
		surveyProcessData.setReview(surveyDetails.getReview());
		surveyProcessData.setScore(surveyDetails.getScore());

		streamApiIntergrationBuilder.getStreamApi().sendsurveyProcessRequest(surveyProcessData);
		LOG.info("Method streamSurveyProcessRequest finished for survey with id {} " , surveyDetails.get_id());

	}
    
    @Override
    @Transactional
    public SurveyPreInitiation saveSurveyPreInitiationTempObject( SurveyPreInitiation surveyPreInitiation )
        throws InvalidInputException
    {
        if ( surveyPreInitiation == null ) {
            LOG.debug( "SurveyPreInitiation object passed null for insert" );
            throw new InvalidInputException( "SurveyPreInitiation object passed null for insert" );
        }
        LOG.debug( "Inside method saveSurveyPreInitiationObject " );
        SurveyPreInitiationTemp surveyPreInitiationTemp = new SurveyPreInitiationTemp();
        surveyPreInitiationTemp = surveyPreinitiationTempDao.save( convertToSpTemp(surveyPreInitiation) );
        //sending the survey id in response 
        surveyPreInitiation.setSurveyPreIntitiationId(surveyPreInitiationTemp.getSurveyPreIntitiationId());
        return surveyPreInitiation;
    }
    
    private SurveyPreInitiationTemp convertToSpTemp(SurveyPreInitiation surveyPreInitiation ) {
    	SurveyPreInitiationTemp surveyPreInitiationTemp = new SurveyPreInitiationTemp();
    	surveyPreInitiationTemp.setAgentEmailId(surveyPreInitiation.getAgentEmailId());
    	surveyPreInitiationTemp.setAgentId(surveyPreInitiation.getAgentId());
    	surveyPreInitiationTemp.setAgentName(surveyPreInitiation.getAgentName());
    	surveyPreInitiationTemp.setBranchCollectionId(surveyPreInitiation.getBranchCollectionId());
    	surveyPreInitiationTemp.setCity(surveyPreInitiation.getCity());
    	surveyPreInitiationTemp.setCollectionName(surveyPreInitiation.getCollectionName());
    	surveyPreInitiationTemp.setCompanyId(surveyPreInitiation.getCompanyId());
    	surveyPreInitiationTemp.setCreatedOn(surveyPreInitiation.getCreatedOn());
    	surveyPreInitiationTemp.setCustomerEmailId(surveyPreInitiation.getCustomerEmailId());
    	surveyPreInitiationTemp.setCustomerFirstName(surveyPreInitiation.getCustomerFirstName());
    	surveyPreInitiationTemp.setCustomerInteractionDetails(surveyPreInitiation.getCustomerInteractionDetails());
    	surveyPreInitiationTemp.setCustomerLastName(surveyPreInitiation.getCustomerLastName());
    	surveyPreInitiationTemp.setEngagementClosedTime(surveyPreInitiation.getEngagementClosedTime());
    	surveyPreInitiationTemp.setErrorCode(surveyPreInitiation.getErrorCode());
    	surveyPreInitiationTemp.setErrorCodeDescription(surveyPreInitiation.getErrorCodeDescription());
    	surveyPreInitiationTemp.setIsSurveyRequestSent(surveyPreInitiation.getIsSurveyRequestSent());
    	surveyPreInitiationTemp.setLastReminderTime(surveyPreInitiation.getLastReminderTime());
    	surveyPreInitiationTemp.setLoanProcessorEmail(surveyPreInitiation.getLoanProcessorEmail());
    	surveyPreInitiationTemp.setLoanProcessorName(surveyPreInitiation.getLoanProcessorName());
    	surveyPreInitiationTemp.setModifiedOn(surveyPreInitiation.getModifiedOn());
    	surveyPreInitiationTemp.setParticipantType(surveyPreInitiation.getParticipantType());
    	surveyPreInitiationTemp.setPropertyAddress(surveyPreInitiation.getPropertyAddress());
    	surveyPreInitiationTemp.setRegionCollectionId(surveyPreInitiation.getRegionCollectionId());
    	surveyPreInitiationTemp.setReminderCounts(surveyPreInitiation.getReminderCounts());
    	surveyPreInitiationTemp.setState(surveyPreInitiation.getState());
    	surveyPreInitiationTemp.setStatus(surveyPreInitiation.getStatus());
    	surveyPreInitiationTemp.setSurveyPreIntitiationId(surveyPreInitiation.getSurveyPreIntitiationId());
    	surveyPreInitiationTemp.setSurveySource(surveyPreInitiation.getSurveySource());
    	surveyPreInitiationTemp.setSurveySourceId(surveyPreInitiation.getSurveySourceId());
    	surveyPreInitiationTemp.setTransactionType(surveyPreInitiation.getTransactionType());
    	surveyPreInitiationTemp.setUser(surveyPreInitiation.getUser());
    	surveyPreInitiationTemp.setCustomFieldFive(surveyPreInitiation.getCustomFieldFive());
    	surveyPreInitiationTemp.setCustomFieldFour(surveyPreInitiation.getCustomFieldFour());
    	surveyPreInitiationTemp.setCustomFieldThree(surveyPreInitiation.getCustomFieldThree());
    	surveyPreInitiationTemp.setCustomFieldTwo(surveyPreInitiation.getCustomFieldTwo());
    	surveyPreInitiationTemp.setCustomFieldOne(surveyPreInitiation.getCustomFieldOne());
    	return surveyPreInitiationTemp;
    }
    

    @Override
    public boolean sendSms( SurveyPreInitiation survey, String agentFirstName,  boolean saveToStreamLater ) throws InvalidInputException
    {

        String surveyLink = composeLink( survey.getAgentId(), survey.getCustomerEmailId(), survey.getCustomerFirstName(),
            survey.getCustomerLastName(), survey.getSurveyPreIntitiationId(), false );

        SmsEntity smsEntity = new SmsEntity();
        smsEntity.setRecipientName( survey.getCustomerFirstName()
            + ( StringUtils.isEmpty( survey.getCustomerLastName() ) ? "" : " " + survey.getCustomerLastName() ) );
        smsEntity.setRecipientContactNumber( survey.getCustomerContactNumber() );
        smsEntity.setAgentId( survey.getAgentId() );
        smsEntity.setCompanyId( survey.getCompanyId() );
        smsEntity.setSpiId( survey.getSurveyPreIntitiationId() );

        ApplicationSettings applicationSettings = applicationSettingsInstanceProvider.getApplicationSettings();

        String configuredSmsText = applicationSettings.getDefaultSmsReminderText();

        return smsServices.sendSmsReminder( configuredSmsText, surveyLink, survey.getCustomerFirstName(), agentFirstName, smsEntity, saveToStreamLater );
    }
    
    @Override
    public long checkIfContactNumberAlreadySurveyed( String countryDialInCode, String contactNumber,
    		long agentId, Timestamp createOn, OrganizationUnitSettings companySettings ) {
    	List<String> contactList = new ArrayList<>();
    	
    	contactList.add( "+" + countryDialInCode + contactNumber );
    	contactList.add( countryDialInCode + contactNumber );
    	if( "1".equals( countryDialInCode ) ) {
    		
    		contactList.add( "+0" + contactNumber );
    		contactList.add( "0" + contactNumber );
    		contactList.add( contactNumber );
    	}

    	int duplicateSurveyInterval = 0;
    	if ( companySettings != null && companySettings.getSurvey_settings() != null
    			&& companySettings.getSurvey_settings().getDuplicateSurveyInterval() > 0 ) {
    		duplicateSurveyInterval = companySettings.getSurvey_settings().getDuplicateSurveyInterval();
    	} else {
    		duplicateSurveyInterval = defaultSurveyRetakeInterval;
    	}
    	
    	return surveyPreInitiationDao.getSurveyCountByAgentIdAndCustomeContactNumberForPastNDays(agentId, contactList,
    			createOn, duplicateSurveyInterval);
    }
    
    @Override
    public boolean checkIfContactNumberAlreadyExists( String countryDialInCode, String contactNumber,
            long agentId, int duplicateSurveyInterval ) {
        List<String> contactList = new ArrayList<>();
        
        contactList.add( "+" + countryDialInCode + contactNumber );
        contactList.add( countryDialInCode + contactNumber );
        if( "1".equals( countryDialInCode ) ) {
            contactList.add( "+0" + contactNumber );
            contactList.add( "0" + contactNumber );
            contactList.add( contactNumber );
        }
        
        long count = surveyPreInitiationDao.getSurveyPreInitiationCount(agentId, contactList, duplicateSurveyInterval);
        return count > 0 ? true : false; 
    }
    
    @Override
    public boolean checkIfContactNumberAlreadySurveyed(String countryDialInCode, String contactNumber,
        long agentId, OrganizationUnitSettings companySettings) {

		  List<String> contactList = new ArrayList<>();

		  contactList.add( "+" + countryDialInCode + contactNumber ); contactList.add(
		  countryDialInCode + contactNumber ); if( "1".equals( countryDialInCode ) ) {

		  contactList.add( "+0" + contactNumber ); contactList.add( "0" + contactNumber
		  ); contactList.add( contactNumber ); }

        int duplicateSurveyInterval = 0;
        if ( companySettings != null && companySettings.getSurvey_settings() != null
                && companySettings.getSurvey_settings().getDuplicateSurveyInterval() > 0 ) {
            duplicateSurveyInterval = companySettings.getSurvey_settings().getDuplicateSurveyInterval();
        } else {
            duplicateSurveyInterval = defaultSurveyRetakeInterval;
        }
        long count = surveyPreInitiationDao.getSurveyPreInitiationCountAlreadySent(agentId, contactList, duplicateSurveyInterval);
        return count > 0 ? true : false; 
    }

    @Override
    public List<SmsSurveyReminderResponseVO> sendMultipleIncompleteSurveyReminder(long companyId,  String[] surveysSelectedArray) throws NonFatalException
    {
        OrganizationUnitSettings companySettings = userManagementService.getCompanySettingForSmsReminder( companyId );

        if ( companySettings == null ){
            LOG.warn("No company settings found");
            throw new InvalidInputException( "No company settings found" );
        }
        
        if(companySettings.getSurvey_settings() == null) {
            LOG.warn("No Survey settings found");
            throw new InvalidInputException( "No Survey settings found" );
        }
        
        if( !companySettings.getSurvey_settings().isSmsSurveyReminderEnabled()) {
            LOG.warn("Company is not allowed to send SMS reminder");
            throw new InvalidInputException( "Company is not configured for sms survey reminder" );
        }

        SMSTimeWindow smsTimeWindow = companySettings.getSurvey_settings().getSmsTimeWindow();

        if( smsTimeWindow == null ) {
            LOG.warn("SmsTimeWindow not found, using default time from application properties.");
            smsTimeWindow = applicationSettingsInstanceProvider.getApplicationSettings().getSmsTimeWindow();
        }

        LOG.info( "Found smsTimeWindow startTime : {}, endTime: {}, timeZone: {}", smsTimeWindow.getStartTime(),
            smsTimeWindow.getEndTime(), smsTimeWindow.getTimeZone() );

        List<SmsSurveyReminderResponseVO> smsSurveyReminderResponses = new ArrayList<>();
        SmsSurveyReminderResponseVO smsSurveyReminderResponse = new SmsSurveyReminderResponseVO();

        //converting time into utc and comparing them to server utc time.
        if(isSMSWindowTimeValid( smsTimeWindow)) {
            int maxReminderCount = 0;
            if( companySettings.getSurvey_settings().getMaxNumberOfSmsSurveyReminders() > 0 ) {
                maxReminderCount = companySettings.getSurvey_settings().getMaxNumberOfSmsSurveyReminders();
            }
            else {
                maxReminderCount = applicationSettingsInstanceProvider.getApplicationSettings().getDefaultSmsSurveyReminderCount();
            }
    
        	// Send sms to complete survey to each customer.
        	LOG.info("Method to sendMultipleIncompleteSurveyReminder started ");

        	for ( String incompleteSurveyIdStr : surveysSelectedArray ) {
    			try {
                    smsSurveyReminderResponse.setResponseType( "ERROR" );
                    smsSurveyReminderResponses.add( smsSurveyReminderResponse );
    				long surveyPreInitiationId;
    				try {
    					surveyPreInitiationId = Integer.parseInt( incompleteSurveyIdStr );
    				} catch ( NumberFormatException e ) {
    					LOG.error("Invalid surveyPreInitiationIdStr passed",e);
    					throw new InvalidInputException( "Invalid surveyPreInitiationIdStr passed", e );
    				}
    				smsSurveyReminderResponse.setSurveyPreInitiationId( surveyPreInitiationId );
    				SurveyPreInitiation survey = getPreInitiatedSurveyById( surveyPreInitiationId );
    				
    				if ( survey == null ) {
                        LOG.warn("Invalid surveyPreInitiationIdStr passed");
                        throw new InvalidInputException( "Invalid surveyPreInitiationIdStr passed" );
                    }
    				
    				//check if max survey reminder has been reached
    		        if ( survey.getReminderCountsSms() >= maxReminderCount ) {
    		            smsSurveyReminderResponse.setMessage(SMS_FAILED_LIMIT_REACHED_MSG);
    		            continue;
    		        }
    				
    				OrganizationUnitSettings agentSettings = userManagementService.getAgentSettingForSmsReminder( survey.getAgentId() );
    				
    				if ( StringUtils.isEmpty( survey.getCustomerContactNumber() ) ) {
    					LOG.warn("Customer contact number not found");
    					smsSurveyReminderResponse.setMessage("Cannot send the reminder to " + survey.getCustomerFirstName()+ " " + survey.getCustomerLastName() + 
    	                    ". Customer contact number not found. ");
    					continue;
    				}
    				
    				if( survey.getCustomerContactNumber().length() < 10 ) {
    					
    					LOG.warn("Customer contact number is invalid");
    					smsSurveyReminderResponse.setMessage("SMS send failed: Customer contact number " + survey.getCustomerContactNumber() + " is invalid. ");
    					continue;
    				}
    				
    				StringBuilder builder = new StringBuilder( survey.getCustomerContactNumber() );
    				if( builder.charAt( 0 ) == '+' ) {
    
    					builder.delete(0, 1);
    				}
    				
    				String countryDialInCode = "1";
            		String contactNumber = builder.substring( builder.length() - 10 );
            		if( builder.length() > 10 ) {
            			
            			countryDialInCode = builder.substring( 0,  builder.length() - 10 );
            			if( !dialInCodes.contains( countryDialInCode ) && !countryDialInCode.equals( "0" ) ) {
            				LOG.warn("Customer contact number is invalid");
            				smsSurveyReminderResponse.setMessage( "SMS send failed: Customer contact number " + survey.getCustomerContactNumber() + " is invalid. ");
        					continue;
            			}
            			
            			if( countryDialInCode.equals( "0" ) ) {
            				
            				countryDialInCode = "1";
            			}
            		}
    
    				Calendar yesterDayDate = Calendar.getInstance();
    				yesterDayDate.add( Calendar.DATE, -1 );
                    if ( survey.getLastSmsReminderTime() != null && survey.getLastSmsReminderTime().after( yesterDayDate.getTime() ) ) {
    				    smsSurveyReminderResponse.setMessage(SMS_FAILED_ONLY_ONE_REMINDER_MSG);
    					continue;
    				}
    				
    				if( this.checkIfContactNumberAlreadySurveyed( countryDialInCode, contactNumber, survey.getAgentId(), survey.getCreatedOn(), companySettings ) != 0 ) {
    				    smsSurveyReminderResponse.setMessage( "SMS send failed: Contact number is associated with other survey for same agent.");
    					continue;
    				}
    				
    				survey.setCustomerContactNumber( "+" + countryDialInCode + contactNumber );
    				
    				//Check if customer contact number is unsubscribed.
                    if ( contactUnsubscribeService.isUnsubscribed( survey.getCompanyId(), survey.getCustomerContactNumber()) ) {
                        LOG.debug( "Customer has unsubscribed his number {} either for the company {} or for social survey.",
                            survey.getCustomerEmailId(), survey.getCompanyId() );
                        smsSurveyReminderResponse.setMessage( "Cannot send the reminder to " + survey.getCustomerContactNumber()
    					+ ". The customer has unsubscribed this contact number for social survey. ");
                        continue;
                    }
                    
                    String agentFirstName = agentSettings.getContact_details().getName();
    
                    boolean isSuccess = sendSms(survey, agentFirstName, false);
                    
                    if(!isSuccess) {
                        LOG.warn( "Stream api might be down or throwing error." );
                        smsSurveyReminderResponse.setMessage( "SMS send failed: could not process request right now, try again after some time.");
                        continue;
                    }
                    
    				// Increasing value of reminder count for sms by 1.
                    SurveyPreInitiation surveyPreInitiationUpdated = updateReminderCountSms( survey.getSurveyPreIntitiationId(), false, true );
                    smsSurveyReminderResponse.setMessage( "Reminder sms sent successfully to " + survey.getCustomerContactNumber());
    				smsSurveyReminderResponse.setResponseType( "SUCCESS" );
    				smsSurveyReminderResponse.setCustomerName( surveyPreInitiationUpdated.getCustomerFirstName() +" "+ surveyPreInitiationUpdated.getCustomerLastName());
    				smsSurveyReminderResponse.setReminderCountsSms(surveyPreInitiationUpdated.getReminderCountsSms());
    				smsSurveyReminderResponse.setModifiedOn( surveyPreInitiationUpdated.getModifiedOn());
    			} catch ( NumberFormatException e ) {
    				LOG.error("Number format exception occured while parsing incomplete survey id : "+incompleteSurveyIdStr,e);
    				throw new NonFatalException(
    						"Number format exception occured while parsing incomplete survey id : " + incompleteSurveyIdStr, e );
    			}
    		}
        	LOG.info("Method to sendMultipleIncompleteSurveyReminder finished");
        	return smsSurveyReminderResponses;
        }
        else {
            for(String surveyPreInitiationId : surveysSelectedArray) {
                smsSurveyReminderResponse.setSurveyPreInitiationId( Long.parseLong( surveyPreInitiationId ));
                smsSurveyReminderResponse.setMessage(String.format( SMS_FAILED_OUT_OFF_WINDOW_TIME_MSG, smsTimeWindow.getStartTime(), smsTimeWindow.getEndTime(), smsTimeWindow.getTimeZone()));
                smsSurveyReminderResponses.add( smsSurveyReminderResponse );
            }
            return smsSurveyReminderResponses;
        }
    }


    @Override
    public boolean isSMSWindowTimeValid( SMSTimeWindow smsTimeWindow )
    {
        try {
            if(smsTimeWindow == null) {
                LOG.warn( "smsTimeWindow cann't be null" );
                return false;
            }
            
            DateTimeZone timeZone = DateTimeZone.forID( smsTimeWindow.getTimeZone() );
            
            DateTime currentLocalDateTime = DateTime.now( timeZone );
            
            // Get the system based on timezone specified for company
            LOG.info( "Current time is {} inside method isSMSWindowTimeValid and SMSTimeWindow is {}",  currentLocalDateTime, smsTimeWindow);

            int currentHours = currentLocalDateTime.getHourOfDay();
            int currentMins = currentLocalDateTime.getMinuteOfHour();

            DateFormat dateFormat = new SimpleDateFormat( CommonConstants.HOURS_MINS_TIME_FORMATE );
            Calendar calendar = Calendar.getInstance();

            Date startDate = dateFormat.parse( smsTimeWindow.getStartTime() );

            calendar.setTime( startDate );
            int startHours = calendar.get( Calendar.HOUR_OF_DAY );
            int startMins = calendar.get( Calendar.MINUTE );

            Date endDate = dateFormat.parse( smsTimeWindow.getEndTime() );
            calendar.setTime( endDate );
            int endHours = calendar.get( Calendar.HOUR_OF_DAY );
            int endMins = calendar.get( Calendar.MINUTE );

            if ( startHours > endHours || ( startHours == endHours && startMins > endMins ) ) {
                int tempEndHours = 23;
                int tempEndMins = 59;
                int tempStartHours = 0;
                int tempStartMins = 0;

                // First window from start time to 23:59
                boolean inFirstRange = isTimeInRange( currentHours, currentMins, startHours, startMins, tempEndHours,
                    tempEndMins );

                // Second window from 00:00 to end time
                boolean inSecondRange = isTimeInRange( currentHours, currentMins, tempStartHours, tempStartMins, endHours,
                    endMins );

                // Valid if it is in first and second range.
                return inFirstRange && inSecondRange;
            } else {
                // from start time to end time
                return isTimeInRange( currentHours, currentMins, startHours, startMins, endHours, endMins );
            }
        } catch ( ParseException e ) {
            LOG.error( "Found ParseException in method isSMSWindowTimeValid while converting current date into UTC", e );
        }
        return false;
    }
    

    private boolean isTimeInRange( int currentHours, int currentMins, int startHours, int startMins, int endHours, int endMins )
    {
        if ( ( ( currentHours < startHours || currentHours > endHours )
            || ( currentHours == startHours && currentMins < startMins )
            || ( currentHours == endHours && currentMins > endMins ) ) ) {
            return false;
        } else {
            return true;
        }
    }
    
    @Override
    public ReviewReplyVO createOrUpdateReplyToReview( String surveyId, String replyText, String replyByName, String replyById, String replyId, String entityType )
        throws InvalidInputException
    {
        ReviewReply reviewReply = new ReviewReply();
        boolean isUpdateReply = false;
        int profileMasterId = 0;
        SurveyDetails surveyDetails = getSurveyDetails(surveyId);
        
        OrganizationUnitSettings organizationUnitSettings = organizationUnitSettingsDao
            .fetchOrganizationUnitSettingsById( surveyDetails.getCompanyId(), MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
        String surveyUrl = organizationUnitSettings.getCompleteProfileUrl()+"/"+surveyId;
        
        if(replyId != null && !replyId.isEmpty()){
            
            //If we have a replyId, that means we're doing an update reply
            
            isUpdateReply = true;
            
            List<ReviewReply> allReviewReplies = surveyDetails.getReviewReply();
            
            //Iterate over all replies to get the reply we want to update
            for(ReviewReply tempReply : allReviewReplies)
            {
                if ( ( tempReply.getReplyId() != null ) && ( tempReply.getReplyId().equals( replyId ) ) )
                {
                    reviewReply = tempReply;
                    
                    //Update the reply with the new replyText
                    reviewReply.setReplyText( replyText );
                    break;
                }
            }
        }
        else{
            if ( entityType.equalsIgnoreCase( CommonConstants.COMPANY_ID ) ) {
                profileMasterId = CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID;
            } else if ( entityType.equalsIgnoreCase( CommonConstants.REGION_ID ) ) {
                profileMasterId = CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID;
            } else if ( entityType.equalsIgnoreCase( CommonConstants.BRANCH_ID ) ) {
                profileMasterId = CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID;
            } else if ( entityType.equalsIgnoreCase( CommonConstants.AGENT_ID ) ) {
                profileMasterId = CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID;
            } else {
                throw new InvalidInputException( "Invalid Collection Type" );
            }
            
            //If we don't have a replyId, we're doing a create reply
            
            reviewReply.setReplyId( UUID.randomUUID().toString() );
            reviewReply.setReplyText( replyText );
            reviewReply.setCreatedOn( new Date() );
            reviewReply.setReplyByName( replyByName );
            reviewReply.setReplyById( replyById );
            reviewReply.setProfileMasterId( profileMasterId );
               
        }
        
        //Setting modified_on is common for both flows
        reviewReply.setModifiedOn( new Date() );

        surveyDetailsDao.upsertReviewReply( reviewReply, surveyId );
        
        //transform it to the VO to return as a response 
        ReviewReplyVO reviewReplyVO = ReviewReplyVO.transformToVO( reviewReply );
        reviewReplyVO.setSurveyId( surveyId );
        
        //notify the customer that he's got a reply to his review
        String customerName = surveyDetails.getCustomerFirstName();
        if ( surveyDetails.getCustomerLastName() != null && !surveyDetails.getCustomerLastName().isEmpty() ) {
            customerName = surveyDetails.getCustomerFirstName() + " " + surveyDetails.getCustomerLastName();
        }
        String agentName = surveyDetails.getAgentName();
        
        String customerEmail = surveyDetails.getCustomerEmail();
        
        if(isUpdateReply){
            emailServices.sendMailForEditReplyOnReview(replyText, surveyUrl, agentName, replyByName , customerName,  customerEmail );
        }else {
            emailServices.sendMailForCreateReplyOnReview(replyText, surveyUrl, agentName, replyByName , customerName,  customerEmail );
        }

        LOG.debug( "method saveReplyToReviews() ended" );
  
        return reviewReplyVO;
    }


    @Override
    public void deleteReviewReply( String replyId, String surveyId ) throws InvalidInputException
    {
        surveyDetailsDao.deleteReviewReply( replyId, surveyId );
        SurveyDetails surveyDetails = getSurveyDetails(surveyId);
        
        OrganizationUnitSettings organizationUnitSettings = organizationUnitSettingsDao
            .fetchOrganizationUnitSettingsById( surveyDetails.getCompanyId(), MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
        String surveyUrl = organizationUnitSettings.getCompleteProfileUrl()+"/"+surveyId;
        
        String customerName = surveyDetails.getCustomerFirstName();
        if ( surveyDetails.getCustomerLastName() != null && !surveyDetails.getCustomerLastName().isEmpty() ) {
            customerName = surveyDetails.getCustomerFirstName() + " " + surveyDetails.getCustomerLastName();
        }
        String agentName = surveyDetails.getAgentName();
        
        String customerEmail = surveyDetails.getCustomerEmail();
        
        emailServices.sendMailForDeleteReplyOnReview(surveyUrl, agentName, customerName,  customerEmail);
    }

    @Override
    public boolean isCompanyAllowedForAutoReminder( long companyId ) {

    	OrganizationUnitSettings companySettings;
		try {
			companySettings = userManagementService.getCompanySettingForSmsReminder( companyId );

			if ( companySettings == null || companySettings.getSurvey_settings() == null){
	            LOG.warn("No company settings found for companyId: {} ", companyId);
	            return false;
	        }
			return companySettings.getSurvey_settings().isSmsSurveyReminderEnabled();

		} catch (InvalidInputException e) {
			LOG.info( "Sms survey reminder setting is not enable for companyId {} ", companyId );
			return false;
		}
    }
    
}