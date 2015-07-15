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
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.MailContent;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.SurveyResponse;
import com.realtech.socialsurvey.core.entities.SurveySettings;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
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

        SurveyDetails survey = surveyDetailsDao.getSurveyByAgentIdAndCustomerEmail( agentId, customerEmail );
        LOG.info( "Method to store initial details of survey, storeInitialSurveyAnswers() finished." );

        if ( survey == null ) {
            surveyDetailsDao.insertSurveyDetails( surveyDetails );
            return null;
        } else {
            return survey;
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
    public SurveyDetails getSurveyDetails( long agentId, String customerEmail )
    {
        LOG.info( "Method getSurveyDetails() to return survey details by agent id and customer email started." );
        SurveyDetails surveyDetails;
        surveyDetails = surveyDetailsDao.getSurveyByAgentIdAndCustomerEmail( agentId, customerEmail );
        LOG.info( "Method getSurveyDetails() to return survey details by agent id and customer email finished." );
        return surveyDetails;
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
    public void updateReminderCount( long agentId, String customerEmail )
    {
        LOG.info( "Method to increase reminder count by 1, updateReminderCount() started." );
        Map<String, Object> queries = new HashMap<>();
        queries.put( "agentId", agentId );
        queries.put( "customerEmailId", customerEmail );
        List<SurveyPreInitiation> surveys = surveyPreInitiationDao.findByKeyValue( SurveyPreInitiation.class, queries );
        if ( surveys != null && !surveys.isEmpty() ) {
            SurveyPreInitiation survey = surveys.get( CommonConstants.INITIAL_INDEX );
            survey.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
            survey.setReminderCounts( survey.getReminderCounts() + 1 );
            surveyPreInitiationDao.merge( survey );
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
        }
        for ( UserProfile admin : admins ) {
            String name = admin.getUser().getFirstName();
            if ( admin.getUser().getLastName() != null )
                name += " " + admin.getUser().getLastName();
            emailIdsOfAdmins.put( admin.getEmailId(), name );
        }
        return emailIdsOfAdmins;
    }


    /*
     * Method to get list of customers' email ids who have not completed survey yet. It checks if
     * max number of reminder mails have been sent. It also checks if required number of days have
     * been passed since the last mail was sent.
     */
    @Override
    @Transactional
    public List<SurveyPreInitiation> getIncompleteSurveyCustomersEmail( long companyId )
    {
        LOG.info( "started." );
        int reminderInterval = 0;
        int maxReminders = 0;
        List<SurveyPreInitiation> incompleteSurveyCustomers = new ArrayList<>();
        OrganizationUnitSettings organizationUnitSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(
            companyId, MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
        // Fetching surveyReminderInterval and max number of reminders for a company.
        if ( organizationUnitSettings != null ) {
            SurveySettings surveySettings = organizationUnitSettings.getSurvey_settings();
            if ( surveySettings != null ) {
                if ( !surveySettings.getIsReminderDisabled() && surveySettings.getSurvey_reminder_interval_in_days() > 0 ) {
                    reminderInterval = surveySettings.getSurvey_reminder_interval_in_days();
                    maxReminders = surveySettings.getMax_number_of_survey_reminders();
                }
            }
        }
        // Setting default values for Max number of survey reminders and survey reminder interval. 
        if ( maxReminders == 0 ) {
            maxReminders = maxSurveyReminders;
        }
        if ( reminderInterval == 0 ) {
            reminderInterval = surveyReminderInterval;
        }
        //		incompleteSurveyCustomers = surveyDetailsDao.getIncompleteSurveyCustomers(companyId, surveyReminderInterval, maxReminders);
        /*  incompleteSurveyCustomers = surveyPreInitiationDao.getIncompleteSurveyForReminder( companyId, reminderInterval,
              maxReminders );*/
        LOG.debug( "Now fetching survey which are already processed " );
        incompleteSurveyCustomers = surveyPreInitiationDao.findByColumn( SurveyPreInitiation.class,
            CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_SURVEYPREINITIATION_PROCESSED );
        // TODO do above code using mysql...should be simple
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
    public void changeStatusOfSurvey( long agentId, String customerEmail, boolean editable )
    {
        LOG.info( "Method to update status of survey in SurveyDetails collection, changeStatusOfSurvey() started." );
        surveyDetailsDao.changeStatusOfSurvey( agentId, customerEmail, editable );
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
        NoRecordsFetchedException, UndeliveredEmailException
    {
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

        String link = composeLink( user.getUserId(), custEmail );
        preInitiateSurvey( user, custEmail, custFirstName, custLastName, 0, custRelationWithAgent, source );
        //		storeInitialSurveyDetails(user.getUserId(), custEmail, custFirstName, custLastName, 0, custRelationWithAgent, link);

        //		if (isAgent)
        sendInvitationMailByAgent( user, custFirstName, custLastName, custEmail, link );
        //		else
        //			sendInvitationMailByCustomer(user, custFirstName, custLastName, custEmail, link);
        LOG.debug( "Method sendSurveyInvitationMail() finished from DashboardController." );
    }


    /*
     * Method to send email to customer by agent for restarting an already completed survey.
     */
    @Override
    public void sendSurveyRestartMail( String custFirstName, String custLastName, String custEmail,
        String custRelationWithAgent, User user, String surveyUrl ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.info( "sendSurveyRestartMail() started." );
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
        String agentSignature = emailFormatHelper.buildAgentSignature( agentPhone, agentTitle, companyName );
        String currentYear = String.valueOf( Calendar.getInstance().get( Calendar.YEAR ) );
        DateFormat dateFormat = new SimpleDateFormat( "yyyy/MM/dd" );
        // TODO add address for mail footer
        String fullAddress = "";

        OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user.getCompany()
            .getCompanyId() );
        if ( companySettings != null && companySettings.getMail_content() != null
            && companySettings.getMail_content().getRestart_survey_mail() != null ) {

            MailContent restartSurvey = companySettings.getMail_content().getRestart_survey_mail();
            String mailBody = emailFormatHelper.replaceEmailBodyWithParams( restartSurvey.getMail_body(),
                restartSurvey.getParam_order() );

            mailBody = mailBody.replaceAll( "\\[BaseUrl\\]", applicationBaseUrl );
            mailBody = mailBody.replaceAll( "\\[LogoUrl\\]", appLogoUrl );
            mailBody = mailBody.replaceAll( "\\[Link\\]", surveyUrl );
            mailBody = mailBody.replaceAll( "\\[Name\\]", custFirstName + " " + custLastName );
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
            emailServices.sendDefaultSurveyInvitationMail( custEmail, custFirstName + " " + custLastName, user.getFirstName()
                + ( user.getLastName() != null ? " " + user.getLastName() : "" ), surveyUrl, user.getEmailId(), agentSignature,
                companyName, dateFormat.format( new Date() ), currentYear, fullAddress );
        }
        LOG.info( "sendSurveyRestartMail() finished." );
    }


    // Method to fetch initial survey details from MySQL based upn agent id and customer email.
    @Override
    @Transactional
    public SurveyPreInitiation getPreInitiatedSurvey( long agentId, String customerEmail ) throws NoRecordsFetchedException
    {
        LOG.info( "Method getSurveyByAgentIdAndCutomerEmail() started. " );
        Map<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.AGENT_ID_COLUMN, agentId );
        queries.put( "customerEmailId", customerEmail );
        List<SurveyPreInitiation> surveyPreInitiations = surveyPreInitiationDao.findByKeyValue( SurveyPreInitiation.class,
            queries );
        LOG.info( "Method getSurveyByAgentIdAndCutomerEmail() finished. " );
        if ( surveyPreInitiations != null && !surveyPreInitiations.isEmpty() ) {
            return surveyPreInitiations.get( CommonConstants.INITIAL_INDEX );
        }
        return null;
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
    public String composeLink( long userId, String custEmail ) throws InvalidInputException
    {
        LOG.debug( "Method composeLink() started" );
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put( CommonConstants.AGENT_ID_COLUMN, userId + "" );
        urlParams.put( CommonConstants.CUSTOMER_EMAIL_COLUMN, custEmail );
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
                        survey.setAgentId( user.getUserId() );
                        surveyPreInitiationDao.update( survey );
                        if ( user.getCreatedOn().after( survey.getEngagementClosedTime() ) ) {
                            status = CommonConstants.STATUS_SURVEYPREINITIATION_OLD_RECORD;
                        }
                    }
                }
            }
            if ( survey.getAgentEmailId() == null || survey.getAgentEmailId().isEmpty() ) {
                LOG.error( "Agent email not found , invalid survey " + survey.getSurveyPreIntitiationId() );
                unavailableAgents.add( survey );
                companies.add( survey.getCompanyId() );
            } else if ( survey.getCustomerFirstName() == null || survey.getCustomerFirstName().isEmpty() ) {
                if ( survey.getCustomerLastName() == null || survey.getCustomerLastName().isEmpty() ) {
                    LOG.error( "No Name found for customer, hence this is an invalid survey "
                        + survey.getSurveyPreIntitiationId() );
                    customersWithoutName.add( survey );
                }

            } else if ( survey.getCustomerLastName() == null || survey.getCustomerLastName().isEmpty() ) {
                if ( survey.getCustomerFirstName() == null || survey.getCustomerFirstName().isEmpty() ) {
                    LOG.error( "No Name found for customer, hence this is an invalid survey "
                        + survey.getSurveyPreIntitiationId() );
                    customersWithoutName.add( survey );
                    companies.add( survey.getCompanyId() );
                }
            } else if ( survey.getCustomerEmailId() == null || survey.getCustomerEmailId().isEmpty() ) {
                LOG.error( "No customer email id found, invalid survey " + survey.getSurveyPreIntitiationId() );
                customersWithoutEmailId.add( survey );
                companies.add( survey.getCompanyId() );
            } else if ( user == null ) {
                LOG.error( "no agent found with this email id" );
                invalidAgents.add( survey );
                companies.add( survey.getCompanyId() );
            } else if ( user.getCompany() == null ) {
                LOG.error( "Agent doesnt have an company associated with it " );
                invalidAgents.add( survey );
                companies.add( survey.getCompanyId() );
            } else if ( user.getCompany().getCompanyId() != survey.getCompanyId() ) {
                unavailableAgents.add( survey );
                companies.add( survey.getCompanyId() );
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


    /*
     * Method to send email by agent to initiate survey.
     */
    private void sendInvitationMailByAgent( User user, String custFirstName, String custLastName, String custEmail,
        String surveyUrl ) throws InvalidInputException, UndeliveredEmailException
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

        String agentSignature = emailFormatHelper.buildAgentSignature( agentPhone, agentTitle, companyName );
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
            mailBody = mailBody.replaceAll( "\\[LogoUrl\\]", appLogoUrl );
            mailBody = mailBody.replaceAll( "\\[Link\\]", surveyUrl );
            mailBody = mailBody.replaceAll( "\\[Name\\]", custFirstName + " " + custLastName );
            mailBody = mailBody.replaceAll( "\\[AgentName\\]", agentName );
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
            emailServices.sendDefaultSurveyInvitationMail( custEmail, custFirstName + " " + custLastName, user.getFirstName()
                + ( user.getLastName() != null ? " " + user.getLastName() : "" ), surveyUrl, user.getEmailId(), agentSignature,
                companyName, dateFormat.format( new Date() ), currentYear, fullAddress );
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
            mailBody = mailBody.replaceAll( "\\[AgentName\\]", user.getFirstName() + " " + user.getLastName() );
            mailBody = mailBody.replaceAll( "\\[Name\\]", custFirstName + " " + custLastName );
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
            emailServices.sendDefaultSurveyInvitationMailByCustomer( custEmail, custFirstName + " " + custLastName,
                user.getFirstName() + ( user.getLastName() != null ? " " + user.getLastName() : "" ), link, user.getEmailId() );
        }
        LOG.debug( "sendInvitationMailByCustomer() finished." );
    }


    // Method to store details of a customer in mysql at the time of  sending invite.
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
}
// JIRA SS-119 by RM-05:EOC