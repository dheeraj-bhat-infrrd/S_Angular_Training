package com.realtech.socialsurvey.core.starter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.WordUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.MailContent;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.enums.OrganizationUnit;
import com.realtech.socialsurvey.core.enums.SettingsForApplication;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.generator.UrlService;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.settingsmanagement.impl.InvalidSettingsStateException;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.core.utils.EmailFormatHelper;


public class IncompleteSurveyReminderSender extends QuartzJobBean
{

    public static final Logger LOG = LoggerFactory.getLogger( IncompleteSurveyReminderSender.class );

    private SurveyHandler surveyHandler;
    private EmailServices emailServices;
    private UserManagementService userManagementService;
    private OrganizationManagementService organizationManagementService;
    private ProfileManagementService profileManagementService;
    private EmailFormatHelper emailFormatHelper;
    private String applicationBaseUrl;
    private String applicationLogoUrl;
    private BatchTrackerService batchTrackerService;
    private UrlService urlService;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext )
    {
        LOG.info( "Executing IncompleteSurveyReminderSender" );

        // initialize the dependencies
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );

        try {
            //update last run start time
            batchTrackerService.getLastRunEndTimeAndUpdateLastStartTimeByBatchType(
                CommonConstants.BATCH_TYPE_INCOMPLETE_SURVEY_REMINDER_SENDER,
                CommonConstants.BATCH_NAME_INCOMPLETE_SURVEY_REMINDER_SENDER );

            for ( Company company : organizationManagementService.getAllCompanies() ) {
                Map<String, Integer> reminderMap = surveyHandler.getReminderInformationForCompany( company.getCompanyId() );
                int reminderInterval = reminderMap.get( CommonConstants.SURVEY_REMINDER_INTERVAL );
                int reminderCount = reminderMap.get( CommonConstants.SURVEY_REMINDER_COUNT );
                LOG.debug( "Reminder count for company: " + company.getCompanyId() + " is " + reminderCount );
                SimpleDateFormat sdf = new SimpleDateFormat( "dd/MM/yyyy" );
                Date epochReminderDate = null;
                List<SurveyPreInitiation> incompleteSurveyCustomers = surveyHandler.getIncompleteSurveyCustomersEmail( company );
                LOG.debug( "Found " + ( incompleteSurveyCustomers != null ? incompleteSurveyCustomers.size() : 0 )
                    + " surveys sent for company id " + company.getCompanyId() );
                for ( SurveyPreInitiation survey : incompleteSurveyCustomers ) {
                    LOG.debug( "Processing survey pre initiation id: " + survey.getSurveyPreIntitiationId() );

                    LOG.debug( "Survey pre initiation id: " + survey.getSurveyPreIntitiationId() + " within reminder counts" );
                    boolean reminder = false;
                    try {
                        epochReminderDate = sdf.parse( CommonConstants.EPOCH_REMINDER_TIME );
                    } catch ( Exception e ) {
                        LOG.error( "Exception caught " + e.getMessage() );
                        continue;
                    }
                    LOG.debug( "Last reminder time: " + String.valueOf( survey.getLastReminderTime() ) );
                    if ( survey.getLastReminderTime().after( epochReminderDate ) ) {
                        LOG.debug( "Reminder mail for incomplete survey id: " + survey.getSurveyPreIntitiationId() );
                        reminder = true;
                    } else {
                        LOG.debug( "Initial survey request mail for incomplete survey id: "
                            + survey.getSurveyPreIntitiationId() );
                        reminder = false;
                    }
                    long surveyLastRemindedTime = survey.getLastReminderTime().getTime();
                    long currentTime = System.currentTimeMillis();
                    if ( surveyHandler.checkIfTimeIntervalHasExpired( surveyLastRemindedTime, currentTime, reminderInterval ) ) {
                        LOG.debug( "Survey eligible for sending mail with id: " + survey.getSurveyPreIntitiationId() );
                        try {
                            /*
                             * if ( survey.getSurveySource().equalsIgnoreCase(
                             * CommonConstants.CRM_SOURCE_ENCOMPASS ) ) {
                             * sendMailToAgent( survey ); }
                             */
                            if ( reminder ) {
                                if ( survey.getReminderCounts() < reminderCount ) {
                                    sendSurveyReminderEmail( emailServices, organizationManagementService,
                                        userManagementService, survey, company.getCompanyId() );
                                    surveyHandler.markSurveyAsSent( survey );
                                    surveyHandler.updateReminderCount( survey.getSurveyPreIntitiationId(), reminder );
                                } else {
                                    LOG.debug( "This survey " + survey.getSurveyPreIntitiationId()
                                        + " has exceeded the reminder count " );
                                }
                            } else {
                                sendSurveyInitiationEmail( emailServices, organizationManagementService, userManagementService,
                                    survey, company.getCompanyId() );
                                surveyHandler.markSurveyAsSent( survey );
                                surveyHandler.updateReminderCount( survey.getSurveyPreIntitiationId(), reminder );
                            }

                        } catch ( InvalidInputException e ) {
                            LOG.error(
                                "InvalidInputException caught in executeInternal() method of IncompleteSurveyReminderSender. Nested exception is ",
                                e );
                        } catch ( ProfileNotFoundException e ) {
                            LOG.error( "Error while sending incomplete survey mail ", e );
                        }
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


    private void sendMailToAgent( SurveyPreInitiation survey )
    {
        try {
            emailServices.sendAgentSurveyReminderMail( survey.getCustomerEmailId(), survey );
        } catch ( InvalidInputException | UndeliveredEmailException e ) {
            LOG.error( "Exception caught " + e.getMessage() );
        }
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        surveyHandler = (SurveyHandler) jobMap.get( "surveyHandler" );
        emailServices = (EmailServices) jobMap.get( "emailServices" );
        userManagementService = (UserManagementService) jobMap.get( "userManagementService" );
        organizationManagementService = (OrganizationManagementService) jobMap.get( "organizationManagementService" );
        profileManagementService = (ProfileManagementService) jobMap.get( "profileManagementService" );
        emailFormatHelper = (EmailFormatHelper) jobMap.get( "emailFormatHelper" );
        applicationBaseUrl = (String) jobMap.get( "applicationBaseUrl" );
        applicationLogoUrl = (String) jobMap.get( "applicationLogoUrl" );
        batchTrackerService = (BatchTrackerService) jobMap.get( "batchTrackerService" );
        urlService = (UrlService) jobMap.get( "urlService" );
    }


    private void sendSurveyReminderEmail( EmailServices emailServices,
        OrganizationManagementService organizationManagementService, UserManagementService userManagementService,
        SurveyPreInitiation survey, long companyId ) throws InvalidInputException, ProfileNotFoundException
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
        }

        String surveyLink = surveyHandler.composeLink( survey.getAgentId(), survey.getCustomerEmailId(),
            survey.getCustomerFirstName(), survey.getCustomerLastName() );
        try {
            companySettings = organizationManagementService.getCompanySettings( companyId );
        } catch ( InvalidInputException e ) {
            LOG.error( "InvalidInputException occured while trying to fetch company settings." );
        }

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
        long regionId = hierarchyMap.get( CommonConstants.REGION_ID_COLUMN );
        long branchId = hierarchyMap.get( CommonConstants.BRANCH_ID_COLUMN );
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

        // Null check
        if ( companySettings != null && companySettings.getMail_content() != null
            && companySettings.getMail_content().getTake_survey_reminder_mail() != null ) {

            OrganizationUnit organizationUnit = map.get( SettingsForApplication.LOGO );
            //JIRA SS-1363 begin
            /*if ( organizationUnit == OrganizationUnit.COMPANY ) {
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

            MailContent mailContent = companySettings.getMail_content().getTake_survey_reminder_mail();

            String mailBody = emailFormatHelper.replaceEmailBodyWithParams( mailContent.getMail_body(),
                mailContent.getParam_order() );

            if ( logoUrl == null || logoUrl.equalsIgnoreCase( "" ) ) {
                logoUrl = applicationLogoUrl;
            }

            mailBody = emailFormatHelper.replaceLegends( false, mailBody, applicationBaseUrl, logoUrl, surveyLink,
                survey.getCustomerFirstName(), survey.getCustomerLastName(), agentName, agentSignature,
                survey.getCustomerEmailId(), user.getEmailId(), companyName, dateFormat.format( new Date() ), currentYear,
                fullAddress );
            String mailSubject = CommonConstants.REMINDER_MAIL_SUBJECT;
            if ( mailContent.getMail_subject() != null && !mailContent.getMail_subject().isEmpty() ) {
                mailSubject = mailContent.getMail_subject();
            }
            mailSubject = emailFormatHelper.replaceLegends( true, mailSubject, applicationBaseUrl, logoUrl, surveyLink,
                survey.getCustomerFirstName(), survey.getCustomerLastName(), agentName, agentSignature,
                survey.getCustomerEmailId(), user.getEmailId(), companyName, dateFormat.format( new Date() ), currentYear,
                fullAddress );
            try {
                emailServices.sendSurveyReminderMail( survey.getCustomerEmailId(), mailSubject, mailBody, agentName,
                    user.getEmailId() );
            } catch ( InvalidInputException | UndeliveredEmailException e ) {
                LOG.error( "Exception caught while sending mail to " + survey.getCustomerEmailId() + " .Nested exception is ",
                    e );
            }
        } else {
            try {

                emailServices.sendDefaultSurveyReminderMail( survey.getCustomerEmailId(), logoUrl,
                    survey.getCustomerFirstName(), agentName, agentEmailId, surveyLink, agentPhone,
                    agentTitle, companyName );

            } catch ( InvalidInputException | UndeliveredEmailException e ) {
                LOG.error( "Exception caught in IncompleteSurveyReminderSender.main while trying to send reminder mail to "
                    + survey.getCustomerFirstName() + " for completion of survey. Nested exception is ", e );
            }
        }
    }


    private void sendSurveyInitiationEmail( EmailServices emailServices,
        OrganizationManagementService organizationManagementService, UserManagementService userManagementService,
        SurveyPreInitiation survey, long companyId ) throws InvalidInputException, ProfileNotFoundException
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
        }

        String surveyLink = surveyHandler.composeLink( survey.getAgentId(), survey.getCustomerEmailId(),
            survey.getCustomerFirstName(), survey.getCustomerLastName() );
        try {
            companySettings = organizationManagementService.getCompanySettings( companyId );
        } catch ( InvalidInputException e ) {
            LOG.error( "InvalidInputException occured while trying to fetch company settings." );
        }

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
        long regionId = hierarchyMap.get( CommonConstants.REGION_ID_COLUMN );
        long branchId = hierarchyMap.get( CommonConstants.BRANCH_ID_COLUMN );
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

        // Null check
        if ( companySettings != null && companySettings.getMail_content() != null
            && companySettings.getMail_content().getTake_survey_mail() != null ) {

            OrganizationUnit organizationUnit = map.get( SettingsForApplication.LOGO );
            //JIRA SS-1363 begin
            /*if ( organizationUnit == OrganizationUnit.COMPANY ) {
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

            MailContent mailContent = companySettings.getMail_content().getTake_survey_mail();

            String mailBody = emailFormatHelper.replaceEmailBodyWithParams( mailContent.getMail_body(),
                mailContent.getParam_order() );

            if ( logoUrl == null || logoUrl.equalsIgnoreCase( "" ) ) {
                logoUrl = applicationLogoUrl;
            }
            LOG.info( "Initiating URL Service to shorten the url " + surveyLink );
            try {
                surveyLink = urlService.shortenUrl( surveyLink );
            } catch ( InvalidInputException e ) {
                LOG.error( "InvalidInput Exception while url shortening url. Reason : ", e );
            }
            LOG.info( "Finished calling URL Service to shorten the url.Shortened URL : " + surveyLink );

            mailBody = emailFormatHelper.replaceLegends( false, mailBody, applicationBaseUrl, logoUrl, surveyLink,
                survey.getCustomerFirstName(), survey.getCustomerLastName(), agentName, agentSignature,
                survey.getCustomerEmailId(), user.getEmailId(), companyName, dateFormat.format( new Date() ), currentYear,
                fullAddress );
            String mailSubject = CommonConstants.REMINDER_MAIL_SUBJECT;
            if ( mailContent.getMail_subject() != null && !mailContent.getMail_subject().isEmpty() ) {
                mailSubject = mailContent.getMail_subject();
            }
            mailSubject = emailFormatHelper.replaceLegends( true, mailSubject, applicationBaseUrl, logoUrl, surveyLink,
                survey.getCustomerFirstName(), survey.getCustomerLastName(), agentName, agentSignature,
                survey.getCustomerEmailId(), user.getEmailId(), companyName, dateFormat.format( new Date() ), currentYear,
                fullAddress );
            try {
                emailServices.sendSurveyReminderMail( survey.getCustomerEmailId(), mailSubject, mailBody, agentName,
                    user.getEmailId() );
            } catch ( InvalidInputException | UndeliveredEmailException e ) {
                LOG.error( "Exception caught while sending mail to " + survey.getCustomerEmailId() + " .Nested exception is ",
                    e );
            }
        } else {
            try {
                emailServices.sendDefaultSurveyInvitationMail( survey.getCustomerEmailId(), logoUrl,
                    survey.getCustomerFirstName(), user.getFirstName()
                        + ( user.getLastName() != null ? " " + user.getLastName() : "" ), surveyLink, user.getEmailId(),
                    agentSignature, companyName, dateFormat.format( new Date() ), currentYear, fullAddress, user.getUserId() );

            } catch ( InvalidInputException | UndeliveredEmailException e ) {
                LOG.error( "Exception caught in IncompleteSurveyReminderSender.main while trying to send reminder mail to "
                    + survey.getCustomerFirstName() + " for completion of survey. Nested exception is ", e );
            }
        }
    }
}