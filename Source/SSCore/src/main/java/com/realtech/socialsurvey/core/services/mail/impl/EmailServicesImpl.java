package com.realtech.socialsurvey.core.services.mail.impl;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.EmailTemplateConstants;
import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.dao.BranchDao;
import com.realtech.socialsurvey.core.dao.EmailDao;
import com.realtech.socialsurvey.core.dao.ForwardMailDetailsDao;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.DigestRequestData;
import com.realtech.socialsurvey.core.entities.EmailAttachment;
import com.realtech.socialsurvey.core.entities.EmailEntity;
import com.realtech.socialsurvey.core.entities.EmailObject;
import com.realtech.socialsurvey.core.entities.FileContentReplacements;
import com.realtech.socialsurvey.core.entities.ForwardMailDetails;
import com.realtech.socialsurvey.core.entities.MonthlyDigestAggregate;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.Plan;
import com.realtech.socialsurvey.core.entities.SocialFeedsActionUpdate;
import com.realtech.socialsurvey.core.entities.SocialResponseObject;
import com.realtech.socialsurvey.core.entities.SurveyCsvInfo;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.entities.ftp.FtpSurveyResponse;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.integration.stream.StreamApiConnectException;
import com.realtech.socialsurvey.core.integration.stream.StreamApiException;
import com.realtech.socialsurvey.core.integration.stream.StreamApiIntegrationBuilder;
import com.realtech.socialsurvey.core.services.generator.UrlService;
import com.realtech.socialsurvey.core.services.mail.EmailSender;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.stream.StreamMessagesService;
import com.realtech.socialsurvey.core.utils.EmailFormatHelper;
import com.realtech.socialsurvey.core.utils.FileOperations;


// JIRA: SS-7: By RM02: BOC
/**
 * Implementation file for the email services
 */

@Component
public class EmailServicesImpl implements EmailServices
{

    public static final Logger LOG = LoggerFactory.getLogger( EmailServicesImpl.class );

    private static final DateFormat df = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );

    @Value ( "${SOCIALSURVEYME_SENDER_EMAIL_DOMAIN}")
    private String defaultSendGridMeEmailDomain;

    @Value ( "${SOCIALSURVEYUS_SENDER_EMAIL_DOMAIN}")
    private String defaultSendGridUsEmailDomain;

    @Value ( "${APPLICATION_BASE_URL}")
    private String appBaseUrl;

    @Value ( "${APPLICATION_SUPPORT_EMAIL}")
    private String applicationSupportEmail;

    @Value ( "${APPLICATION_LOGO_URL}")
    private String appLogoUrl;


    @Value ( "${APPLICATION_ADMIN_EMAIL}")
    private String applicationAdminEmail;

    @Value ( "${APPLICATION_ADMIN_NAME}")
    private String applicationAdminName;

    @Value ( "${PARAM_ORDER_TAKE_SURVEY_REMINDER}")
    String paramOrderTakeSurveyReminder;

    @Value ( "${CURRENT_PROFILE}")
    private String currentProfile;


    @Value ( "${SEND_MAIL}")
    private String sendMail;

    @Value ( "${DEFAULT_EMAIL_FROM_ADDRESS}")
    private String defaultFromAddress;

    //FOR DEFAULT SENDGRID ACCOUNT 
    @Value ( "${SENDGRID_SENDER_SOCIALSURVEYME_NAME}")
    private String defaultSendName;

    @Value ( "${QUEUE_MAILS}")
    private boolean queueMails;

    private ForwardMailDetailsDao forwardMailDetailsDao;

    private GenericDao<User, Long> userDao;

    private EmailDao emailDao;

    private EmailFormatHelper emailFormatHelper;

    private EmailSender emailSender;

    private FileOperations fileOperations;

    private OrganizationUnitSettingsDao organizationUnitSettingsDao;

    private Utils utils;

    private UrlService urlService;

    private UserManagementService userManagementService;

    private StreamApiIntegrationBuilder streamApiIntegrationBuilder;

    private StreamMessagesService streamMessagesService;

    private DigestMailHelper digestMailHelper;
    
    private UserProfileDao userProfileDao;

    @javax.annotation.Resource
    @Qualifier ( "branch")
    private BranchDao branchDao;
    
    @Autowired
    public void setBranchDao (@Qualifier ( "branch") BranchDao branchDao)
    {
    	this.branchDao = branchDao;
    }

    @Autowired
    public void setForwardMailDetailsDao( ForwardMailDetailsDao forwardMailDetailsDao )
    {
        this.forwardMailDetailsDao = forwardMailDetailsDao;
    }


    @Autowired
    public void setUserDao( GenericDao<User, Long> userDao )
    {
        this.userDao = userDao;
    }


    @Autowired
    public void setEmailDao( EmailDao emailDao )
    {
        this.emailDao = emailDao;
    }


    @Autowired
    public void setEmailFormatHelper( EmailFormatHelper emailFormatHelper )
    {
        this.emailFormatHelper = emailFormatHelper;
    }


    @Autowired
    public void setEmailSender( EmailSender emailSender )
    {
        this.emailSender = emailSender;
    }


    @Autowired
    public void setFileOperations( FileOperations fileOperations )
    {
        this.fileOperations = fileOperations;
    }


    @Autowired
    public void setOrganizationUnitSettingsDao( OrganizationUnitSettingsDao organizationUnitSettingsDao )
    {
        this.organizationUnitSettingsDao = organizationUnitSettingsDao;
    }


    @Autowired
    public void setUtils( Utils utils )
    {
        this.utils = utils;
    }


    @Autowired
    public void setUrlService( UrlService urlService )
    {
        this.urlService = urlService;
    }


    @Autowired
    public void setUserManagementService( UserManagementService userManagementService )
    {
        this.userManagementService = userManagementService;
    }


    @Autowired
    public void setStreamApiIntegrationBuilder( StreamApiIntegrationBuilder streamApiIntegrationBuilder )
    {
        this.streamApiIntegrationBuilder = streamApiIntegrationBuilder;
    }


    @Autowired
    public void setStreamMessagesService( StreamMessagesService streamMessagesService )
    {
        this.streamMessagesService = streamMessagesService;
    }


    @Autowired
    public void setDigestMailHelper( DigestMailHelper digestMailHelper )
    {
        this.digestMailHelper = digestMailHelper;
    }
    
    @Autowired
    public void setUserProfileDao( UserProfileDao userProfileDao )
    {
        this.userProfileDao = userProfileDao;
    }


    private void sendEmailWithBodyReplacements( EmailEntity emailEntity, String subjectFileName,
        FileContentReplacements messageBodyReplacements, boolean isImmediate, boolean holdSendingMail )
        throws InvalidInputException, UndeliveredEmailException
    {
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, isImmediate, holdSendingMail,
            false );
    }

    private void sendEmailWithBodyReplacements( EmailEntity emailEntity, String subjectFileName,
        FileContentReplacements messageBodyReplacements, boolean isImmediate, boolean holdSendingMail,
        boolean sendMailToSalesLead, String socialPostType ) throws InvalidInputException, UndeliveredEmailException {
        LOG.debug(
            "Method sendEmailWithBodyReplacements called for emailEntity : {} subjectFileName : {} and messageBodyReplacements : {}",
            emailEntity, subjectFileName, messageBodyReplacements );
        // fill in the details if missing
        fillEmailEntity( emailEntity );
        // check if mail needs to be sent
        if ( sendMail.equals( CommonConstants.YES_STRING ) ) {
            if ( subjectFileName == null || subjectFileName.isEmpty() ) {
                throw new InvalidInputException( "Subject file name is null for sending mail" );
            }
            if ( messageBodyReplacements == null ) {
                throw new InvalidInputException( "Email body file name  and replacements are null for sending mail" );
            }

            // Read the subject template to get the subject and set in emailEntity
            LOG.trace( "Reading template to set the mail subject" );
            emailEntity.setSubject( fileOperations.getContentFromFile( subjectFileName ) + "  " + socialPostType + " ]" );

            //Read the mail body template, replace the required contents with arguments provided
            // and set in emailEntity
            LOG.trace( "Reading template to set the mail body" );
            emailEntity.setBody( fileOperations.replaceFileContents( messageBodyReplacements ) );

            // Send the mail
            if ( queueMails ) {
                emailEntity.setHoldSendingMail( holdSendingMail );
                emailEntity.setSendMailToSalesLead( sendMailToSalesLead );
                try {
                    streamApiIntegrationBuilder.getStreamApi().streamEmailMessage( emailEntity );
                } catch ( StreamApiException | StreamApiConnectException e ) {
                    LOG.error( "Could not stream email", e );
                    LOG.info( "Saving message into local db" );
                    saveMessageToStreamLater( emailEntity );
                }

            } else {
                if ( isImmediate ) {
                    emailSender.sendEmailByEmailEntity( emailEntity, sendMailToSalesLead );
                } else {
                    saveEmail( emailEntity, holdSendingMail );
                }
            }

        }

        LOG.debug( "Method sendEmailWithBodyReplacements completed successfully" );
    }

    private void sendEmailWithBodyReplacements( EmailEntity emailEntity, String subjectFileName,
        FileContentReplacements messageBodyReplacements, boolean isImmediate, boolean holdSendingMail,
        boolean sendMailToSalesLead ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug(
            "Method sendEmailWithBodyReplacements called for emailEntity : {} subjectFileName : {} and messageBodyReplacements : {}",
            emailEntity, subjectFileName, messageBodyReplacements );
        // fill in the details if missing
        fillEmailEntity( emailEntity );
        // check if mail needs to be sent
        if ( sendMail.equals( CommonConstants.YES_STRING ) ) {
            if ( subjectFileName == null || subjectFileName.isEmpty() ) {
                throw new InvalidInputException( "Subject file name is null for sending mail" );
            }
            if ( messageBodyReplacements == null ) {
                throw new InvalidInputException( "Email body file name  and replacements are null for sending mail" );
            }


            // Read the subject template to get the subject and set in emailEntity
            LOG.trace( "Reading template to set the mail subject" );
            emailEntity.setSubject( fileOperations.getContentFromFile( subjectFileName ) );

            //Read the mail body template, replace the required contents with arguments provided
            // and set in emailEntity
            LOG.trace( "Reading template to set the mail body" );
            emailEntity.setBody( fileOperations.replaceFileContents( messageBodyReplacements ) );

            // Send the mail
            if ( queueMails ) {
                emailEntity.setHoldSendingMail( holdSendingMail );
                emailEntity.setSendMailToSalesLead( sendMailToSalesLead );
                try {
                    streamApiIntegrationBuilder.getStreamApi().streamEmailMessage( emailEntity );
                } catch ( StreamApiException | StreamApiConnectException e ) {
                    LOG.error( "Could not stream email", e );
                    LOG.info( "Saving message into local db" );
                    saveMessageToStreamLater( emailEntity );
                }

            } else {
                if ( isImmediate ) {
                    emailSender.sendEmailByEmailEntity( emailEntity, sendMailToSalesLead );
                } else {
                    saveEmail( emailEntity, holdSendingMail );
                }
            }

        }

        LOG.debug( "Method sendEmailWithBodyReplacements completed successfully" );
    }


    private boolean saveMessageToStreamLater( EmailEntity emailEntity )
    {
        return streamMessagesService.saveFailedStreamEmailMessages( emailEntity );
    }


    private EmailEntity fillEmailEntity( EmailEntity emailEntity )
    {
        if ( emailEntity.getSenderEmailId() == null || emailEntity.getSenderEmailId().isEmpty() ) {
            LOG.trace( "Setting default from email id" );
            emailEntity.setSenderEmailId( defaultFromAddress );
        }
        if ( emailEntity.getSenderName() == null || emailEntity.getSenderName().isEmpty() ) {
            LOG.trace( "Setting default sender name" );
            emailEntity.setSenderName( defaultSendName );
        }
        return emailEntity;
    }


    private void saveEmail( EmailEntity emailEntity, boolean holdSendingMail )
        throws InvalidInputException, UndeliveredEmailException
    {
        if ( emailEntity.getRecipients() == null || emailEntity.getRecipients().isEmpty() ) {
            throw new InvalidInputException( "No recipients to send mail" );
        }
        if ( emailEntity.getSenderEmailId() == null || emailEntity.getSenderEmailId().isEmpty() ) {
            LOG.debug( "Setting default from email id" );
            emailEntity.setSenderEmailId( defaultFromAddress );
        }
        if ( emailEntity.getSenderName() == null || emailEntity.getSenderName().isEmpty() ) {
            LOG.debug( "Setting default sender name" );
            emailEntity.setSenderName( defaultSendName );
        }
        if ( emailEntity.getBody() == null || emailEntity.getBody().isEmpty() ) {
            throw new InvalidInputException( "Email body is blank." );
        }
        if ( emailEntity.getSubject() == null || emailEntity.getSubject().isEmpty() ) {
            throw new InvalidInputException( "Email subject is blank." );
        }

        LOG.trace( "Sending mail: {}", emailEntity );

        EmailObject emailObject = new EmailObject();
        byte[] emailBinaryObject = null;
        try {
            emailBinaryObject = utils.serializeObject( emailEntity );
        } catch ( IOException ie ) {
            LOG.error( "Exception caught {}", ie.getMessage(), ie );
        }
        if ( holdSendingMail ) {
            emailObject.setHoldSendingMail( CommonConstants.YES );
        } else {
            emailObject.setHoldSendingMail( CommonConstants.NO );
        }
        emailObject.setCreatedOn( new Timestamp( System.currentTimeMillis() ) );
        emailObject.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        emailObject.setCreatedBy( CommonConstants.ADMIN_USER_NAME );
        emailObject.setModifiedBy( CommonConstants.ADMIN_USER_NAME );
        emailObject.setEmailBinaryObject( emailBinaryObject );

        saveEmailInDb( emailObject );
    }


    private void saveEmailInDb( EmailObject emailObject )
    {
        LOG.trace( "Saving Email Object " );
        emailDao.saveEmailObjectInDB( emailObject );

    }


    private void sendEmailWithSubjectAndBodyReplacements( EmailEntity emailEntity, FileContentReplacements subjectReplacements,
        FileContentReplacements messageBodyReplacements, boolean isImmediate, boolean holdSendingMail )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug(
            "Method sendEmailWithSubjectAndBodyReplacements called for emailEntity : {} subjectReplacements : {} and messageBodyReplacements : {}",
            emailEntity, subjectReplacements, messageBodyReplacements );

        // fill in the details if missing
        fillEmailEntity( emailEntity );

        if ( sendMail.equals( CommonConstants.YES_STRING ) ) {
            if ( subjectReplacements == null ) {
                throw new InvalidInputException( "Email subject file name and replacements are null for sending mail" );
            }
            if ( messageBodyReplacements == null ) {
                throw new InvalidInputException( "Email body file name and replacements are null for sending mail" );
            }

            // Read the subject template to get the subject and set in emailEntity
            LOG.trace( "Reading template to set the mail subject" );
            emailEntity.setSubject( fileOperations.replaceFileContents( subjectReplacements ) );

            // Read the mail body template, replace the required contents with arguments provided
            // and set in emailEntity
            LOG.trace( "Reading template to set the mail body" );
            emailEntity.setBody( fileOperations.replaceFileContents( messageBodyReplacements ) );

            // Send the mail
            if ( queueMails ) {
                emailEntity.setHoldSendingMail( holdSendingMail );
                try {
                    streamApiIntegrationBuilder.getStreamApi().streamEmailMessage( emailEntity );
                } catch ( StreamApiException | StreamApiConnectException e ) {
                    LOG.error( "Could not stream email", e );
                    LOG.info( "Saving message into local db" );
                    saveMessageToStreamLater( emailEntity );
                }

            } else {
                if ( isImmediate ) {
                    emailSender.sendEmailByEmailEntity( emailEntity, false );
                } else {
                    saveEmail( emailEntity, holdSendingMail );
                }
            }
            LOG.debug( "Method sendEmailWithSubjectAndBodyReplacements completed successfully" );
        }
    }


    private void sendEmail( EmailEntity emailEntity, String subject, String mailBody, boolean isImmediate,
        boolean holdSendingMail ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "Method sendEmail called for subject : {}", subject );
        if ( sendMail.equals( CommonConstants.YES_STRING ) ) {
            if ( subject == null || subject.isEmpty() ) {
                throw new InvalidInputException( "Subject is null for sending mail" );
            }
            if ( mailBody == null ) {
                throw new InvalidInputException( "Email body is null for sending mail" );
            }

            LOG.trace( "Setting the mail subject and body" );
            emailEntity.setSubject( subject );
            emailEntity.setBody( mailBody );

            // Send the mail
            if ( queueMails ) {
                emailEntity.setHoldSendingMail( holdSendingMail );
                try {
                    streamApiIntegrationBuilder.getStreamApi().streamEmailMessage( emailEntity );
                } catch ( StreamApiException | StreamApiConnectException e ) {
                    LOG.error( "Could not stream email", e );
                    LOG.info( "Saving message into local db" );
                    saveMessageToStreamLater( emailEntity );
                }

            } else {
                if ( isImmediate ) {
                    emailSender.sendEmailByEmailEntity( emailEntity, false );
                } else {
                    saveEmail( emailEntity, holdSendingMail );
                }
            }
        }
        LOG.debug( "Method sendEmail completed successfully" );
    }


    /**
     * Method to send registration invite mail to a single recipient
     *
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    @Async
    @Override
    public void sendRegistrationInviteMail( String url, String recipientMailId, String firstName, String lastName )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "Method for sending registration invite mail called with url : %s firstName : %s and lastName : %s", url,
            firstName, lastName );
        if ( url == null || url.isEmpty() ) {
            LOG.warn( "Url in sendRegistrationInviteMail is empty or null." );
            throw new InvalidInputException( "Url in sendRegistrationInviteMail is empty or null." );
        }
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.warn( "Recipient email Id in sendRegistrationInviteMail is empty or null." );
            throw new InvalidInputException( "Recipient email Id in sendRegistrationInviteMail is empty or null." );
        }
        if ( firstName == null || firstName.isEmpty() ) {
            LOG.warn( "Firstname is in sendRegistrationInviteMail empty or null." );
            throw new InvalidInputException( "Firstname is in sendRegistrationInviteMail empty or null." );
        }

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );

        LOG.trace( "Initiating URL Service to shorten the url: %s", url );
        String shortUrl = urlService.shortenUrl( url, emailEntity.getRandomUUID() );
        LOG.trace( "Finished calling URL Service to shorten the url in sendRegistrationInviteMail. Shortened URL : %s",
            shortUrl );

        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_REGISTRATION_INVITATION_MAIL );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.REGISTRATION_INVITATION_MAIL_SUBJECT;

        // Preparing full name of the recipient
        String fullName = firstName;
        if ( lastName != null && !lastName.isEmpty() ) {
            fullName = firstName + " " + lastName;
        }
        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.REGISTRATION_INVITATION_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs(
            Arrays.asList( appLogoUrl, fullName, shortUrl, shortUrl, shortUrl, recipientMailId, appBaseUrl, appBaseUrl ) );

        LOG.trace( "Sending mail" );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, true, false );
        LOG.debug( "Successfully sent registration invite mail" );
    }


    @Async
    @Override
    public void sendNewRegistrationInviteMail( String url, String recipientMailId, String firstName, String lastName,
        int planId ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "Method sendNewRegistrationInviteMail started for url : %s firstName : %s lastName : %s and planId : %s",
            url, firstName, lastName, planId );
        if ( url == null || url.isEmpty() ) {
            LOG.warn( "Url in sendNewRegistrationInviteMail is empty or null." );
            throw new InvalidInputException( "Url in sendNewRegistrationInviteMail is empty or null." );
        }
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.warn( "Recipient email Id in sendNewRegistrationInviteMail is empty or null." );
            throw new InvalidInputException( "Recipient email Id in sendNewRegistrationInviteMail is empty or null." );
        }
        if ( firstName == null || firstName.isEmpty() ) {
            LOG.warn( "Firstname in sendNewRegistrationInviteMail is empty or null." );
            throw new InvalidInputException( "Firstname in sendNewRegistrationInviteMail is empty or null." );
        }

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );

        LOG.trace( "Initiating URL Service to shorten the url %s", url );
        String shortUrl = urlService.shortenUrl( url, emailEntity.getRandomUUID() );
        LOG.trace( "Finished calling URL Service to shorten the url in sendNewRegistrationInviteMail. Shortened URL : %s",
            shortUrl );

        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_NEW_REGISTRATION_INVITATION_MAIL );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.NEW_REGISTRATION_MAIL_SUBJECT;
        // Preparing full name of the recipient
        String fullName = firstName;
        if ( lastName != null && !lastName.isEmpty() ) {
            fullName = firstName + " " + lastName;
        }
        FileContentReplacements messageBodyReplacements = new FileContentReplacements();

        //When the plan is individual or small business
        if ( planId < Plan.ENTERPRISE.getPlanId() ) {
            messageBodyReplacements.setFileName(
                EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.NEW_REGISTRATION_MAIL_BODY );
        } else {
            messageBodyReplacements.setFileName(
                EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.NEW_REGISTRATION_ENTERPRISE_MAIL_BODY );
        }
        messageBodyReplacements.setReplacementArgs(
            Arrays.asList( appLogoUrl, fullName, shortUrl, shortUrl, shortUrl, recipientMailId, appBaseUrl, appBaseUrl ) );

        LOG.trace( "Sending mail in sendNewRegistrationInviteMail" );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, true, false );
    }


    @Async
    @Override
    public void sendCompanyRegistrationStageMail( String firstName, String lastName, List<String> recipientMailIds,
        String registrationStage, String entityName, String details, boolean isImmediate )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "Method to send registration stage mail started" );
        if ( registrationStage == null || registrationStage.isEmpty() ) {
            throw new InvalidInputException( "Registration stage cannot be empty" );
        }
        if ( entityName == null || entityName.isEmpty() ) {
            throw new InvalidInputException( "Name cannot be null" );
        }

        if ( recipientMailIds == null || recipientMailIds.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null for sending CompanyRegistrationStageMail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending CompanyRegistrationStageMail " );
        }

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailIds );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_COMPANY_REGISTRATION_STAGE_MAIL );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.COMPANY_REGISTRATION_STAGE_MAIL_SUBJECT;

        String modDetails = ( details == null ) ? "" : details;

        String agentName = "";
        if ( firstName != null && !firstName.isEmpty() ) {
            agentName += firstName;
        }
        if ( lastName != null && !lastName.isEmpty() ) {
            agentName += " " + lastName;
        }
        FileContentReplacements messageSubjectReplacements = new FileContentReplacements();
        messageSubjectReplacements.setFileName( subjectFileName );
        messageSubjectReplacements.setReplacementArgs( Arrays.asList( agentName ) );
        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.COMPANY_REGISTRATION_STAGE_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, entityName, registrationStage, modDetails ) );
        sendEmailWithSubjectAndBodyReplacements( emailEntity, messageSubjectReplacements, messageBodyReplacements, isImmediate,
            false );
    }


    @Async
    @Override
    public void sendAgentSurveyReminderMail( String recipientMailId, SurveyPreInitiation survey )
        throws InvalidInputException, UndeliveredEmailException
    {

        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null for sending registration invite mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending registration invite mail " );
        }

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_AGENT_SURVEY_REMINDER_MAIL );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.AGENT_SURVEY_REMINDER_EMAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.AGENT_SURVEY_REMINDER_EMAIL_BODY );
        String customerName = survey.getCustomerFirstName() + " " + survey.getCustomerLastName();
        messageBodyReplacements.setReplacementArgs(
            Arrays.asList( appLogoUrl, survey.getAgentName(), customerName, survey.getCustomerEmailId(), customerName ) );

        LOG.trace( "Sending mail for sendAgentSurveyReminderMail." );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.debug( "Successfully sent registration invite mail" );
    }


    /**
     * Sends a reset password link to the user
     *
     * @param url
     * @param recipientMailId
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    @Async
    @Override
    public void sendResetPasswordEmail( String url, String recipientMailId, String name, String loginName )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "Method to send Email to reset the password link with URL : %s \t and Recipients Mail ID : %s", url,
            recipientMailId );
        if ( url == null || url.isEmpty() ) {
            LOG.warn( "URL in sendResetPasswordEmail can not be null or empty." );
            throw new InvalidInputException( "URL in sendResetPasswordEmail can not be null or empty." );
        }
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.warn( "Recipients Email Id in sendResetPasswordEmail can not be null or empty." );
            throw new InvalidInputException( "Recipients Email Id in sendResetPasswordEmail can not be null or empty." );
        }
        if ( name == null || name.isEmpty() ) {
            LOG.warn( "Recipients name in sendResetPasswordEmail can not be null or empty" );
            throw new InvalidInputException( "Recipients name in sendResetPasswordEmail can not be null or empty" );
        }

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );

        LOG.trace( "Initiating URL Service to shorten the url %s", url );
        String shortUrl = urlService.shortenUrl( url, emailEntity.getRandomUUID() );
        LOG.trace( "Finished calling URL Service to shorten the url in sendResetPasswordEmail. Shortened URL : %s", shortUrl );

        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_RESET_PASSWORD_EMAIL );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.RESET_PASSWORD_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.RESET_PASSWORD_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs(
            Arrays.asList( appLogoUrl, name, loginName, shortUrl, shortUrl, shortUrl, appBaseUrl, appBaseUrl ) );

        LOG.debug( "Sending mail in sendResetPasswordEmail" );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
    }


    @Async
    @Override
    public void sendInvitationToSocialSurveyAdmin( String url, String recipientMailId, String name, String loginName )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "Method to send Email to social survey admin link with URL : {} \t and Recipients Mail ID : {}", url,
            recipientMailId );
        if ( url == null || url.isEmpty() ) {
            LOG.warn( "URL generated can not be null or empty" );
            throw new InvalidInputException( "URL generated can not be null or empty" );
        }
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.warn( "Recipients Email Id can not be null or empty" );
            throw new InvalidInputException( "Recipients Email Id can not be null or empty" );
        }
        if ( name == null || name.isEmpty() ) {
            LOG.warn( "Recipients name can not be null or empty" );
            throw new InvalidInputException( "Recipients name can not be null or empty" );
        }
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );

        LOG.trace( "Initiating URL Service to shorten the url {}", url );
        String shortUrl = urlService.shortenUrl( url, emailEntity.getRandomUUID() );
        LOG.trace( "Finished calling URL Service to shorten the url.Shortened URL : {}", shortUrl );

        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_INVITATION_TO_SOCIALSURVEY_ADMIN_EMAIL );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SS_ADMIN_INVITATION_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SS_ADMIN_INVITATION_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs(
            Arrays.asList( appLogoUrl, name, loginName, shortUrl, shortUrl, shortUrl, appBaseUrl, appBaseUrl ) );

        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, true, false );
        LOG.debug( "Successfully sent invitation to social survey admin " );
    }


    /**
     * Sends a mail to the user when his subscription payment fails.
     *
     * @param recipientMailId
     *            ,name,retryDays
     * @return
     */
    @Async
    @Override
    public void sendSubscriptionChargeUnsuccessfulEmail( String recipientMailId, String name, String retryDays )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "Method to send subscription charge unsuccessful mail to : {}", name );
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null for sending unsuccessful subscription charge mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending subscription charge mail " );
        }
        if ( name == null || name.isEmpty() ) {
            LOG.warn( "Name is empty or null for sending subscription charge mail " );
            throw new InvalidInputException( "Name is empty or null for sending subscription charge mail " );
        }

        LOG.trace( "Executing sendSubscriptionChargeUnsuccessfulEmail() with parameters : {}, {} , {}", recipientMailId, name,
            retryDays );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_SUBSCRIPTION_CHARGE_UNSUCCESSFUL_EMAIL );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SUBSCRIPTION_UNSUCCESSFUL_MAIL_SUBJECT;

        // Sequence of the replacement arguments in the list should be same as
        // their sequence of occurrence in the template
        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SUBSCRIPTION_UNSUCCESSFUL_MAIL_BODY );
        messageBodyReplacements
            .setReplacementArgs( Arrays.asList( appLogoUrl, name, retryDays, recipientMailId, appBaseUrl, appBaseUrl ) );

        LOG.trace( "Sending the mail." );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.debug( "Mail successfully sent!" );
    }


    /**
     * Method to send mail with verification link to verify the account
     *
     * @param url
     * @param recipientMailId
     * @param recipientName
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    @Async
    @Override
    public void sendEmailVerificationMail( String url, String recipientMailId, String recipientName )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "Method to send verification mail called for url : {} recipientMailId : {}", url, recipientMailId );
        if ( url == null || url.isEmpty() ) {
            throw new InvalidInputException( "URL generated can not be null or empty" );
        }
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            throw new InvalidInputException( "Recipients Email Id can not be null or empty" );
        }
        if ( recipientName == null || recipientName.isEmpty() ) {
            throw new InvalidInputException( "Recipients Name can not be null or empty" );
        }
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );

        LOG.trace( "Initiating URL Service to shorten the url {}", url );
        String shortUrl = urlService.shortenUrl( url, emailEntity.getRandomUUID() );
        LOG.trace( "Finished calling URL Service to shorten the url.Shortened URL : {}", shortUrl );

        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_EMAIL_VERIFICATION_MAIL );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.EMAIL_VERIFICATION_MAIL_SUBJECT;

        FileContentReplacements fileContentReplacements = new FileContentReplacements();
        fileContentReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.EMAIL_VERIFICATION_MAIL_BODY );
        fileContentReplacements.setReplacementArgs(
            Arrays.asList( appLogoUrl, recipientName, shortUrl, shortUrl, shortUrl, appBaseUrl, appBaseUrl ) );

        LOG.trace( "Calling email sender to send verification mail" );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, fileContentReplacements, false, false );
        LOG.debug( "Successfully sent verification mail" );
    }


    @Async
    @Override
    public void sendEmailVerificationRequestMailToAdmin( String url, String recipientMailId, String recipientName,
        String emailToVerify, String entityName ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "Method to send verification mail called for url : {} recipientMailId : {}", url, recipientMailId );
        if ( url == null || url.isEmpty() ) {
            throw new InvalidInputException( "URL generated can not be null or empty" );
        }
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            throw new InvalidInputException( "Recipients Email Id can not be null or empty" );
        }
        if ( recipientName == null || recipientName.isEmpty() ) {
            throw new InvalidInputException( "Recipients Name can not be null or empty" );
        }

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );

        LOG.trace( "Initiating URL Service to shorten the url {}", url );
        String shortUrl = urlService.shortenUrl( url, emailEntity.getRandomUUID() );
        LOG.trace( "Finished calling URL Service to shorten the url.Shortened URL : {}", shortUrl );

        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_EMAIL_VERIFICATION_REQUESTMAIL_TO_ADMIN_MAIL );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.EMAIL_VERIFICATION_TO_ADMIN_MAIL_SUBJECT;

        FileContentReplacements fileContentReplacements = new FileContentReplacements();
        fileContentReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.EMAIL_VERIFICATION_TO_ADMIN_MAIL_BODY );
        fileContentReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, recipientName, emailToVerify, entityName,
            shortUrl, shortUrl, shortUrl, appBaseUrl, appBaseUrl ) );

        LOG.trace( "Calling email sender to send verification mail" );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, fileContentReplacements, false, false );
        LOG.debug( "Successfully sent verification mail" );
    }


    @Override
    public void sendEmailVerifiedNotificationMail( String recipientMailId, String recipientName )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "Method sendEmailVerifiedNotificationMail called for emailId : {}", recipientMailId );
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            throw new InvalidInputException( "Recipients Email Id can not be null or empty" );
        }
        if ( recipientName == null || recipientName.isEmpty() ) {
            throw new InvalidInputException( "Recipients Name can not be null or empty" );
        }


        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_EMAIL_VERIFIED_NOTIFICATION_MAIL );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.EMAIL_VERIFIED_NOTIFICATION_MAIL_SUBJECT;

        FileContentReplacements fileContentReplacements = new FileContentReplacements();
        fileContentReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.EMAIL_VERIFIED_NOTIFICATION_MAIL_BODY );
        fileContentReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, recipientName, appBaseUrl, appBaseUrl ) );

        LOG.trace( "Calling email sender to sendEmailVerifiedNotificationMaill" );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, fileContentReplacements, false, false );
        LOG.debug( "Successfully sendEmailVerifiedNotificationMail" );
    }


    @Override
    public void sendEmailVerifiedNotificationMailToAdmin( String recipientMailId, String recipientName, String verifiedEmail,
        String entityName ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "Method sendEmailVerifiedNotificationMailToAdmin called for emailId : {}", recipientMailId );
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            throw new InvalidInputException( "Recipients Email Id can not be null or empty" );
        }
        if ( recipientName == null || recipientName.isEmpty() ) {
            throw new InvalidInputException( "Recipients Name can not be null or empty" );
        }
        if ( verifiedEmail == null || verifiedEmail.isEmpty() ) {
            throw new InvalidInputException( "verified Email can not be null or empty" );
        }


        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_EMAIL_VERIFIED_NOTIFICATION_MAIL_TO_ADMIN_MAIL );
        FileContentReplacements subjectFileContentReplacements = new FileContentReplacements();
        subjectFileContentReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.EMAIL_VERIFIED_NOTIFICATION_MAIL_TO_ADMIN_SUBJECT );
        subjectFileContentReplacements.setReplacementArgs( Arrays.asList( entityName ) );

        FileContentReplacements fileContentReplacements = new FileContentReplacements();
        fileContentReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.EMAIL_VERIFIED_NOTIFICATION_MAIL_TO_ADMIN_BODY );
        fileContentReplacements.setReplacementArgs(
            Arrays.asList( appLogoUrl, recipientName, verifiedEmail, entityName, appBaseUrl, appBaseUrl ) );

        LOG.trace( "Calling email sender to sendEmailVerifiedNotificationMailToAdmin" );
        sendEmailWithSubjectAndBodyReplacements( emailEntity, subjectFileContentReplacements, fileContentReplacements, false,
            false );
        LOG.debug( "Successfully sent EmailVerifiedNotificationMailToAdmin" );
    }


    /**
     * Method to send mail with verification link to verify the account
     *
     * @param url
     * @param recipientMailId
     * @param recipientName
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    @Async
    @Override
    public void sendVerificationMail( String url, String recipientMailId, String recipientName, String profileName,
        String loginName, boolean hiddenSection ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "Method to send verification mail called for url: {} recipientMailId: {}", url, recipientMailId );
        if ( url == null || url.isEmpty() ) {
            throw new InvalidInputException( "URL generated can not be null or empty" );
        }
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            throw new InvalidInputException( "Recipients Email Id can not be null or empty" );
        }
        if ( recipientName == null || recipientName.isEmpty() ) {
            throw new InvalidInputException( "Recipients Name can not be null or empty" );
        }
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );

        LOG.trace( "Initiating URL Service to shorten the url {}", url );
        String shortUrl = urlService.shortenUrl( url, emailEntity.getRandomUUID() );
        LOG.trace( "Finished calling URL Service to shorten the url.Shortened URL : {}", shortUrl );

        // Fetching mail body
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_VERIFICATION_MAIL );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.VERIFICATION_MAIL_SUBJECT;

        // File content replacements in same order
        FileContentReplacements fileContentReplacements = new FileContentReplacements();
        if ( hiddenSection ) {
            fileContentReplacements.setFileName(
                EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.VERIFICATION_MAIL_BODY_CUSTOM );
            fileContentReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, recipientName, recipientName, shortUrl,
                shortUrl, shortUrl, loginName, appBaseUrl, appBaseUrl ) );
        } else {
            fileContentReplacements
                .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.VERIFICATION_MAIL_BODY );
            fileContentReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, recipientName, recipientName, shortUrl,
                shortUrl, shortUrl, appBaseUrl, profileName, appBaseUrl, profileName, loginName, appBaseUrl, appBaseUrl ) );
        }

        // sending email
        LOG.trace( "Calling email sender to send verification mail" );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, fileContentReplacements, false, false );
        LOG.debug( "Successfully sent verification mail" );
    }


    // JIRA SS-42 by RM-05 : BOC
    /**
     * Sends a link to new user to complete registration.
     *
     * @param url
     * @param recipientMailId
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    @Async
    @Override
    public void sendRegistrationCompletionEmail( String url, String recipientMailId, String name, String profileName,
        String loginName, boolean holdSendingMail, boolean hiddenSection )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "Method to send Email to complete registration link with URL: {} \t and Recipients Mail ID: {}", url,
            recipientMailId );
        if ( url == null || url.isEmpty() ) {
            LOG.warn( "URL generated can not be null or empty" );
            throw new InvalidInputException( "URL generated can not be null or empty" );
        }
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.warn( "Recipients Email Id can not be null or empty" );
            throw new InvalidInputException( "Recipients Email Id can not be null or empty" );
        }
        if ( name == null || name.isEmpty() ) {
            LOG.warn( "Recipients Name can not be null or empty" );
            throw new InvalidInputException( "Recipients Name can not be null or empty" );
        }
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );

        LOG.trace( "Initiating URL Service to shorten the url {}", url );
        String shortUrl = urlService.shortenUrl( url, emailEntity.getRandomUUID() );
        LOG.trace( "Finished calling URL Service to shorten the url.Shortened URL : {}", shortUrl );

        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_REGISTRATION_COMPLETION_EMAIL );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.COMPLETE_REGISTRATION_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        if ( hiddenSection ) {
            messageBodyReplacements.setFileName(
                EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.COMPLETE_REGISTRATION_MAIL_BODY_CUSTOM );
            messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, name, shortUrl, shortUrl, shortUrl,
                loginName, applicationSupportEmail, appBaseUrl, appBaseUrl ) );
        } else {
            messageBodyReplacements.setFileName(
                EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.COMPLETE_REGISTRATION_MAIL_BODY );
            messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, name, shortUrl, shortUrl, shortUrl,
                loginName, applicationSupportEmail, appBaseUrl, appBaseUrl ) );
        }
        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, holdSendingMail );
        LOG.debug( "Successfully sent mail for registraion completion." );
    }


    // JIRA SS-42 by RM-05 : EOC

    @Async
    @Override
    public void sendFatalExceptionEmail( String recipientMailId, String stackTrace )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "Sending FatalException email to the admin." );
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null for sending fatal exception mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending fatal exception mail " );
        }

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_FATAL_EXCEPTION_EMAIL );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.FATAL_EXCEPTION_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.FATAL_EXCEPTION_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, currentProfile, stackTrace ) );

        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.debug( "Successfully sent fatal exception mail" );
    }


    @Async
    @Override
    public void sendEmailSendingFailureMail( String recipientMailId, String destinationMailId, String displayName,
        String stackTrace ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "Sending email to the admin on failure of sending mail to customer" );
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null for sendEmailSendingFailureMail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sendEmailSendingFailureMail " );
        }
        if ( destinationMailId == null || destinationMailId.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null for sendEmailSendingFailureMail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sendEmailSendingFailureMail " );
        }
        if ( displayName == null || displayName.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null for sendEmailSendingFailureMail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sendEmailSendingFailureMail " );
        }
        if ( stackTrace == null || stackTrace.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null for sendEmailSendingFailureMail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sendEmailSendingFailureMail " );
        }

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_EMAILSENDING_FAILURE_MAIL );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.EMAIL_SENDING_FAILURE_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.EMAIL_SENDING_FAILURE_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, displayName, destinationMailId, stackTrace ) );

        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.debug( "Successfully sent EmailSendingFailureMail" );
    }


    @Async
    @Override
    public void sendRetryChargeEmail( String recipientMailId, String displayName, String loginName )
        throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null for sending retry charge mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending retry charge mail " );
        }
        if ( displayName == null || displayName.isEmpty() ) {
            LOG.warn( "displayName parameter is empty or null for sending retry charge mail " );
            throw new InvalidInputException( "displayName parameter is empty or null for sending retry charge mail " );
        }

        LOG.debug( "Sending retry charge email to : {}", recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_RETRY_CHARGE_EMAIL );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.RETRY_CHARGE_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.RETRY_CHARGE_MAIL_BODY );
        messageBodyReplacements
            .setReplacementArgs( Arrays.asList( appLogoUrl, displayName, recipientMailId, appBaseUrl, appBaseUrl ) );

        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.debug( "Successfully sent retry charge mail" );
    }


    @Async
    @Override
    public void sendRetryExhaustedEmail( String recipientMailId, String displayName, String loginName )
        throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null for sending retries exhausted mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending retries exhausted mail " );
        }
        if ( displayName == null || displayName.isEmpty() ) {
            LOG.warn( "displayName parameter is empty or null for sending retry exhausted mail " );
            throw new InvalidInputException( "displayName parameter is empty or null for sending retry exhausted mail " );
        }

        LOG.debug( "Sending retries exhausted email to : {}", recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_RETRY_EXHAUSTED_EMAIL );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.RETRIES_EXHAUSTED_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.RETRIES_EXHAUSTED_MAIL_BODY );
        messageBodyReplacements
            .setReplacementArgs( Arrays.asList( appLogoUrl, displayName, recipientMailId, appBaseUrl, appBaseUrl ) );

        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.debug( "Successfully sent retries exhausted mail" );
    }


    @Async
    @Override
    public void sendAccountDisabledMail( String recipientMailId, String displayName, String loginName )
        throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null for sending retries exhausted mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending retries exhausted mail " );
        }
        if ( displayName == null || displayName.isEmpty() ) {
            LOG.warn( "displayName parameter is empty or null for sending retry exhausted mail " );
            throw new InvalidInputException( "displayName parameter is empty or null for sending retry exhausted mail " );
        }

        LOG.debug( "Sending account disabled email to : {}", recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_ACCOUNT_DISABLED_MAIL );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.ACCOUNT_DISABLED_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.ACCOUNT_DISABLED_MAIL_BODY );
        messageBodyReplacements
            .setReplacementArgs( Arrays.asList( appLogoUrl, displayName, recipientMailId, appBaseUrl, appBaseUrl ) );

        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.debug( "Successfully sent account disabled mail" );
    }


    @Async
    @Override
    public void sendAccountDeletionMail( String recipientMailId, String displayName, String loginName )
        throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null for sending account deletion mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending account deletion mail " );
        }
        if ( displayName == null || displayName.isEmpty() ) {
            LOG.warn( "displayName parameter is empty or null for sending account deletion mail " );
            throw new InvalidInputException( "displayName parameter is empty or null for sending account deletion mail " );
        }

        LOG.debug( "Sending account deletion email to : {}", recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_ACCOUNT_DELETION_MAIL );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.ACCOUNT_DELETED_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.ACCOUNT_DELETED_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, displayName, recipientMailId ) );

        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.debug( "Successfully sent account disabled mail" );
    }


    @Async
    @Override
    public void sendAccountUpgradeMail( String recipientMailId, String displayName, String loginName )
        throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null for sending account upgrade mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending account upgrade mail " );
        }
        if ( displayName == null || displayName.isEmpty() ) {
            LOG.warn( "displayName parameter is empty or null for sending account upgrade mail " );
            throw new InvalidInputException( "displayName parameter is empty or null for sending account upgrade mail " );
        }

        LOG.debug( "Sending account upgrade email to : {}", recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_ACCOUNT_UPGRADE_MAIL );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.ACCOUNT_UPGRADE_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.ACCOUNT_UPGRADE_MAIL_BODY );
        messageBodyReplacements
            .setReplacementArgs( Arrays.asList( appLogoUrl, displayName, recipientMailId, appBaseUrl, appBaseUrl ) );

        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.debug( "Successfully sent account upgrade mail" );
    }


    @Async
    @Override
    public void sendSurveyRelatedMail( OrganizationUnitSettings companySettings, User user, String agentName,
        String agentFirstName, String agentPhone, String agentTitle, String surveyLink, String logoUrl,
        String customerFirstName, String customerLastName, String customerEmailId, String emailType, String senderName,
        String senderEmailAddress, String mailSubject, String mailBody, AgentSettings agentSettings, long branchId,
        long regionId, String surveySourceId, long agentId, long companyId, boolean sentFromCompany, String unsubscribedURL )
        throws InvalidInputException, UndeliveredEmailException
    {

        if ( customerEmailId == null || customerEmailId.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null for sending survey completion mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending survey completion mail " );
        }

        Branch branch = branchDao.findById( Branch.class, branchId );
        
        String branchName = (branch.getIsDefaultBySystem() == 0 ) ? branch.getBranch() : "";
        String regionName = (branch.getRegion().getIsDefaultBySystem() == 0) ? branch.getRegion().getRegion() : "";

        String companyName = user.getCompany().getCompany();
        String agentSignature = emailFormatHelper.buildAgentSignature( agentName, agentPhone, agentTitle, companyName );
        DateFormat dateFormat = new SimpleDateFormat( "yyyy/MM/dd" );
        String currentYear = String.valueOf( Calendar.getInstance().get( Calendar.YEAR ) );
        String fullAddress = "";

        LOG.trace( "Initiating URL Service to shorten the url {}", surveyLink );
        String shortSurveyLink = null;
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( customerEmailId, agentId, companyId, sentFromCompany,
            senderName );

        try {
            shortSurveyLink = urlService.shortenUrl( surveyLink, emailEntity.getRandomUUID() );
        } catch ( InvalidInputException e ) {
            LOG.error( "InvalidInput Exception while url shortening url. Reason : ", e );
        }
        LOG.trace( "Finished calling URL Service to shorten the url.Shortened URL : {}", shortSurveyLink );


        //get mail body and content

        String companyDisclaimer = "";
        String agentDisclaimer = "";
        String agentLicenses = "";

        if ( agentSettings.getDisclaimer() != null )
            agentDisclaimer = agentSettings.getDisclaimer();

        if ( agentSettings.getLicenses() != null && agentSettings.getLicenses().getAuthorized_in() != null ) {
            agentLicenses = StringUtils.join( agentSettings.getLicenses().getAuthorized_in(), ',' );
        }
        if ( companySettings.getDisclaimer() != null )
            companyDisclaimer = companySettings.getDisclaimer();

        //replace legends
        mailSubject = emailFormatHelper.replaceLegends( true, mailSubject, appBaseUrl, logoUrl, shortSurveyLink,
            customerFirstName, customerLastName, agentName, agentFirstName, agentSignature, customerEmailId, user.getEmailId(),
            companyName, dateFormat.format( new Date() ), currentYear, fullAddress, "", user.getProfileName(),
            companyDisclaimer, agentDisclaimer, agentLicenses, agentTitle, agentPhone, unsubscribedURL,agentId,branchName,regionName);

        mailBody = emailFormatHelper.replaceLegends( false, mailBody, appBaseUrl, logoUrl, shortSurveyLink, customerFirstName,
            customerLastName, agentName, agentFirstName, agentSignature, customerEmailId, user.getEmailId(), companyName,
            dateFormat.format( new Date() ), currentYear, fullAddress, "", user.getProfileName(), companyDisclaimer,
            agentDisclaimer, agentLicenses, agentTitle, agentPhone, unsubscribedURL,agentId,branchName,regionName);

        //send the email
        if ( mailSubject == null || mailSubject.isEmpty() ) {
            LOG.warn( "subject parameter is empty or null for sending social post reminder mail " );
            throw new InvalidInputException( "displayName parameter is empty or null for sending survey completion mail " );
        }
        emailEntity.setMailType( emailType );
        emailEntity.setRecipientsName( Arrays.asList( customerFirstName + " " + customerLastName ) );
        emailEntity.setBranchName( branch.getBranch() );
        emailEntity.setRegionName( branch.getRegion().getRegion() );
        emailEntity.setCompanyId( companyId );
        emailEntity.setSurveySourceId( surveySourceId );
        emailEntity.setRegionId( regionId );
        emailEntity.setBranchId( branchId );
        emailEntity.setAgentId( agentId );
        emailEntity.setAgentEmailId( senderEmailAddress );
        LOG.trace( "Calling email sender to send mail" );
        sendEmail( emailEntity, mailSubject, mailBody, false, false );
        LOG.debug( "Successfully sent survey completion mail" );

    }


    @Async
    @Override
    public void sendSurveyCompletionMailToAdminsAndAgent( String agentName, String recipientName, String recipientMailId,
        String surveyDetail, String customerName, String rating, String logoUrl, String agentProfileLink,
        String customerDetail, String fbShareUrl , boolean isAddFbShare ) throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null for sending survey completion mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending survey completion mail " );
        }
        if ( surveyDetail == null || surveyDetail.isEmpty() ) {
            LOG.warn( "syrveyDetail parameter is empty or null for sending account upgrade mail " );
            throw new InvalidInputException( "surveyDetail parameter is empty or null for sending survey completion mail " );
        }

        if ( customerDetail == null || customerDetail.isEmpty() ) {
            LOG.warn( "customerDetail parameter is empty or null for sending survey completion mail " );
            throw new InvalidInputException( "customerDetail parameter is empty or null for sending survey completion mail " );
        }

        if( logoUrl == null || logoUrl.isEmpty() )
        	logoUrl = appLogoUrl;
        
        LOG.debug( "Sending survey completion email to : {}", recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_SURVEY_COMPLETION_TO_ADMINS_AND_AGENT_MAIL );

        FileContentReplacements subjectReplacements = new FileContentReplacements();
        subjectReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_COMPLETION_ADMINS_MAIL_SUBJECT );
        subjectReplacements.setReplacementArgs( Arrays.asList( rating, agentName, customerName ) );

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        if(isAddFbShare) {
        		messageBodyReplacements.setFileName(
                    EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_COMPLETION_ADMINS_MAIL_BODY_NEW );
            messageBodyReplacements.setReplacementArgs( Arrays.asList( logoUrl, recipientName, customerName, rating, agentName, fbShareUrl,
                    customerDetail, surveyDetail, agentName, agentProfileLink, agentProfileLink, recipientMailId, recipientMailId,
                    String.valueOf( Calendar.getInstance().get( Calendar.YEAR ) ) ) );
        }else {
        		messageBodyReplacements.setFileName(
                    EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_COMPLETION_ADMINS_MAIL_BODY );
        		messageBodyReplacements.setReplacementArgs( Arrays.asList( logoUrl, recipientName, customerName, rating, agentName,
                        customerDetail, surveyDetail, agentName, agentProfileLink, agentProfileLink, recipientMailId, recipientMailId,
                        String.valueOf( Calendar.getInstance().get( Calendar.YEAR ) ) ) );
        }
        
        
        


        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithSubjectAndBodyReplacements( emailEntity, subjectReplacements, messageBodyReplacements, false, false );
        LOG.debug( "Successfully sent survey completion mail" );
    }


    /**
     * Sends the message from the contact us page as a mail to the respective
     * admin or agent
     *
     * @param recipientEmailId
     * @param displayName
     * @param senderEmailId
     * @param message
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    @Async
    @Override
    public void sendContactUsMail( List<String> recipientEmailIds, String displayName, String senderName, String senderEmailId, String agentName, String agentEmail,
        String message ) throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientEmailIds == null || recipientEmailIds.isEmpty()  ) {
            LOG.warn( "Recipient email id is null or empty!" );
            throw new InvalidInputException( "Recipient email id is null or empty!" );
        }
        if ( displayName == null || displayName.isEmpty() ) {
            LOG.warn( "displayName is null or empty!" );
            throw new InvalidInputException( "displayName is null or empty!" );
        }
        if ( senderName == null || senderName.isEmpty() ) {
            LOG.warn( "senderName is null or empty!" );
            throw new InvalidInputException( "senderName is null or empty!" );
        }
        if ( senderEmailId == null || senderEmailId.isEmpty() ) {
            LOG.warn( "senderEmailId is null or empty!" );
            throw new InvalidInputException( "senderEmailId is null or empty!" );
        }
        if ( message == null || message.isEmpty() ) {
            LOG.warn( "message is null or empty!" );
            throw new InvalidInputException( "message is null or empty!" );
        }

        LOG.debug( "Sending contact us email to : {}", recipientEmailIds );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientEmailIds );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_CONTACT_US_MAIL );

        FileContentReplacements subjectReplacements = new FileContentReplacements();
        subjectReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.CONTACT_US_MAIL_SUBJECT );
        subjectReplacements.setReplacementArgs( Arrays.asList( senderName ) );

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.CONTACT_US_MAIL_BODY );
        messageBodyReplacements
            .setReplacementArgs( Arrays.asList( appLogoUrl, displayName, senderName, senderEmailId, agentName, agentEmail, message ) );

        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithSubjectAndBodyReplacements( emailEntity, subjectReplacements, messageBodyReplacements, false, false );
        LOG.debug( "Successfully sent contact us mail" );
    }


    @Async
    @Override
    public void sendSurveyRelatedMail( String recipientMailId, String subject, String mailBody, String emailId,
        String senderName, long agentId, long companyId, String mailType, boolean sentFromCompany )
        throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null for sending survey completion mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending survey completion mail " );
        }
        if ( subject == null || subject.isEmpty() ) {
            LOG.warn( "subject parameter is empty or null for sending social post reminder mail " );
            throw new InvalidInputException( "displayName parameter is empty or null for sending survey completion mail " );
        }

        LOG.debug( "Sending survey reminder email to : {}", recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId, agentId, companyId, sentFromCompany,
            senderName );
        emailEntity.setCompanyId( companyId );
        emailEntity.setMailType( mailType );

        LOG.trace( "Calling email sender to send mail" );
        sendEmail( emailEntity, subject, mailBody, false, false );
        LOG.debug( "Successfully sent survey completion mail" );
    }


    /**
     * Sends account blocking mail when retries fail
     *
     * @param recipientMailId
     * @param displayName
     * @throws UndeliveredEmailException
     * @throws InvalidInputException
     */
    @Async
    @Override
    public void sendAccountBlockingMail( String recipientMailId, String displayName, String loginName )
        throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null for sending survey completion mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending survey completion mail " );
        }
        if ( displayName == null || displayName.isEmpty() ) {
            LOG.warn( "displayName parameter is empty or null for sending account upgrade mail " );
            throw new InvalidInputException( "displayName parameter is empty or null for sending survey completion mail " );
        }

        LOG.debug( "Sending account blocking email to : {}", recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_ACCOUNT_BLOCKING_MAIL );

        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.ACCOUNT_BLOCKING_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.ACCOUNT_BLOCKING_MAIL_BODY );
        messageBodyReplacements
            .setReplacementArgs( Arrays.asList( appLogoUrl, displayName, recipientMailId, appBaseUrl, appBaseUrl ) );

        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.debug( "Successfully sent account blocking mail" );
    }


    /**
     * Send mail to customer when his account is reactivated
     *
     * @param recipientMailId
     * @param displayName
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    @Async
    @Override
    public void sendAccountReactivationMail( String recipientMailId, String displayName, String loginName )
        throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null for sending survey completion mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending survey completion mail " );
        }
        if ( displayName == null || displayName.isEmpty() ) {
            LOG.warn( "displayName parameter is empty or null for sending account upgrade mail " );
            throw new InvalidInputException( "displayName parameter is empty or null for sending survey completion mail " );
        }

        LOG.debug( "Sending account reactivation email to : {}", recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_ACCOUNT_REACTIVATION_MAIL );

        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.ACCOUNT_REACTIVATION_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.ACCOUNT_REACTIVATION_MAIL_BODY );
        messageBodyReplacements
            .setReplacementArgs( Arrays.asList( appLogoUrl, displayName, recipientMailId, appBaseUrl, appBaseUrl ) );

        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.debug( "Successfully sent account blocking mail" );
    }


    @Async
    @Override
    public void sendSubscriptionRevisionMail( String recipientMailId, String name, String oldAmount, String revisedAmount,
        String numOfUsers ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "Sending subscription revision mail to {} with name {}", recipientMailId, name );
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.warn( "Email id is not sent" );
            throw new InvalidInputException( "Email id is not sent" );
        }
        if ( name == null || name.isEmpty() ) {
            LOG.warn( "Name is not sent" );
            throw new InvalidInputException( "Name is not sent" );
        }
        if ( oldAmount == null || oldAmount.isEmpty() ) {
            LOG.warn( "oldAmount is not sent" );
            throw new InvalidInputException( "oldAmount is not sent" );
        }
        if ( revisedAmount == null || revisedAmount.isEmpty() ) {
            LOG.warn( "revisedAmount is not sent" );
            throw new InvalidInputException( "revisedAmount is not sent" );
        }
        if ( numOfUsers == null || numOfUsers.isEmpty() ) {
            LOG.warn( "numOfUsers is not sent" );
            throw new InvalidInputException( "numOfUsers is not sent" );
        }

        LOG.debug( "Sending subscription revision email to : {}", recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_SUBSCRIPTION_REVISION_MAIL );

        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SUBSCRIPTION_PRICE_UPDATED_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SUBSCRIPTION_PRICE_UPDATED_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs(
            Arrays.asList( appLogoUrl, name, numOfUsers, oldAmount, revisedAmount, recipientMailId, appBaseUrl, appBaseUrl ) );

        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.debug( "Successfully sent subscription revised mail" );
    }


    @Async
    @Override
    public void sendManualRegistrationLink( String recipientId, String firstName, String lastName, String link )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "Sending manual registration link to {} and name {}", recipientId, firstName );
        if ( recipientId == null || recipientId.isEmpty() ) {
            LOG.warn( "Recipient id is not present" );
            throw new InvalidInputException( "Recipient id is not present" );
        }
        if ( firstName == null || firstName.isEmpty() ) {
            LOG.warn( "firstName is not present" );
            throw new InvalidInputException( "firstName id is not present" );
        }
        if ( link == null || link.isEmpty() ) {
            LOG.warn( "link is not present" );
            throw new InvalidInputException( "link id is not present" );
        }
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientId );

        LOG.trace( "Initiating URL Service to shorten the url {}", link );
        String shortlink = urlService.shortenUrl( link, emailEntity.getRandomUUID() );
        LOG.trace( "Finished calling URL Service to shorten the url.Shortened URL : {}", shortlink );

        LOG.trace( "Sending manual registration email to : {}", recipientId );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_MANUAL_REGISTRATION_LINK_MAIL );

        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.MANUAL_REGISTRATION_MAIL_SUBJECT;

        // Preparing full name of the recipient
        String fullName = firstName;
        if ( lastName != null && !lastName.isEmpty() ) {
            fullName = firstName + " " + lastName;
        }
        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.MANUAL_REGISTRATION_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs(
            Arrays.asList( appLogoUrl, fullName, fullName, shortlink, shortlink, shortlink, applicationSupportEmail ) );

        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.debug( "Successfully sent manual registration mail" );
    }


    @Async
    @Override
    public void sendSocialConnectMail( String recipientMailId, String displayName, String loginName, String account )
        throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null for sending survey completion mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending survey completion mail " );
        }
        if ( displayName == null || displayName.isEmpty() ) {
            LOG.warn( "displayName parameter is empty or null for sending account upgrade mail " );
            throw new InvalidInputException( "displayName parameter is empty or null for sending survey completion mail " );
        }

        LOG.debug( "Sending Social Connect email to : {}", recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_SOCIAL_CONNECT_MAIL );

        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SOCIAL_CONNECT_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SOCIAL_CONNECT_MAIL_BODY );
        messageBodyReplacements
            .setReplacementArgs( Arrays.asList( appLogoUrl, displayName, account, recipientMailId, appBaseUrl, appBaseUrl ) );

        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.debug( "Successfully sent social connect mail" );
    }


    @Async
    @Override
    public void sendReportAbuseMail( String recipientMailId, String displayName, String agentName, String customerName,
        String customerEmail, String review, String reason, String reporterName, String reporterEmail )
        throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null for sending survey completion mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending report abuse mail " );
        }
        if ( displayName == null || displayName.isEmpty() ) {
            LOG.warn( "displayName parameter is empty or null for sending account upgrade mail " );
            throw new InvalidInputException( "displayName parameter is empty or null for sending report abuse mail " );
        }

        LOG.debug( "Sending report abuse email to : {}", recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_REPORT_ABUSE_MAIL );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.REPORT_ABUSE_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();

        if ( reporterName != null && reporterEmail != null ) {
            messageBodyReplacements.setFileName(
                EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.REPORT_ABUSE_MAIL_WITH_REVIEWER_BODY );
            messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, displayName, agentName, customerName,
                customerEmail, review, reporterName, reporterEmail, reason, appBaseUrl ) );
        } else {
            messageBodyReplacements
                .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.REPORT_ABUSE_MAIL_BODY );
            messageBodyReplacements.setReplacementArgs(
                Arrays.asList( appLogoUrl, displayName, agentName, customerName, customerEmail, review, reason, appBaseUrl ) );
        }

        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.debug( "Successfully sent social connect mail" );
    }


    @Async
    @Override
    public void sendSurveyReportMail( String recipientMailId, String displayName, String reason )
        throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null for sending survey completion mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending survey completion mail " );
        }
        if ( displayName == null || displayName.isEmpty() ) {
            LOG.warn( "displayName parameter is empty or null for sending account upgrade mail " );
            throw new InvalidInputException( "displayName parameter is empty or null for sending survey completion mail " );
        }

        LOG.debug( "Sending survey completion email to : {}", recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_SURVEY_REPORT_MAIL );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SURVEY_REPORT_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_REPORT_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, displayName, reason ) );

        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.debug( "Successfully sent survey completion mail" );
    }


    @Override
    public void sendCorruptDataFromCrmNotificationMail( String firstName, String lastName, String recipientMailId,
        List<EmailAttachment> attachments ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "Method sendCorruptDataFromCrmNotificationMail() started." );
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null for sending corrupt CRM data notification mail " );
            throw new InvalidInputException(
                "Recipient email Id is empty or null for sending corrupt CRM data notification mail " );
        }

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_CORRUPT_DATA_FROM_CRM_NOTIFICATION_MAIL );
        emailEntity.setAttachments( attachments );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.CORRUPT_PREINITIATION_RECORD_MAIL_SUBJECT;
        String displayName = firstName + " " + lastName;
        displayName.replaceAll( "null", "" );
        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.CORRUPT_PREINITIATION_RECORD_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, displayName ) );

        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );

        LOG.debug( "Method sendCorruptDataFromCrmNotificationMail() finished." );
    }


    @Override
    public void sendInvalidEmailsNotificationMail( String firstName, String lastName, String recipientMailId,
        List<EmailAttachment> attachments ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "Method sendInvalidEmailsNotificationMail() started." );
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null for sending corrupt CRM data notification mail " );
            throw new InvalidInputException(
                "Recipient email Id is empty or null for sending corrupt CRM data notification mail " );
        }

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_INVALID_EMAILS_NOTIFICATION_MAIL );
        emailEntity.setAttachments( attachments );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.INVALID_EMAILS_MAIL_SUBJECT;
        String displayName = firstName + " " + lastName;
        displayName.replaceAll( "null", "" );
        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.INVALID_EMAILS_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, displayName ) );

        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );

        LOG.debug( "Method sendCorruptDataFromCrmNotificationMail() finished." );
    }


    @Override
    public void sendRecordsNotUploadedCrmNotificationMail( String firstName, String lastName, String recipientMailId,
        List<EmailAttachment> attachments ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "Method sendCorruptDataFromCrmNotificationMail() started." );
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null for sending corrupt CRM data notification mail " );
            throw new InvalidInputException(
                "Recipient email Id is empty or null for sending corrupt CRM data notification mail " );
        }

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_RECORDS_NOT_UPLOADED_CRM_NOTIFICATION_MAIL );
        emailEntity.setAttachments( attachments );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.RECORDS_NOT_UPLOADED_MAIL_SUBJECT;
        String displayName = firstName + " " + lastName;
        displayName.replaceAll( "null", "" );
        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.RECORDS_NOT_UPLOADED_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, displayName ) );

        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );

        LOG.debug( "Method sendCorruptDataFromCrmNotificationMail() finished." );
    }


    /**
     *
     */
    @Override
    public void sendHelpMailToAdmin( String senderEmail, String senderName, String displayName, String mailSubject,
        String messageBodyText, String recipientMailId, List<EmailAttachment> attachments )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "Method sendHelpMailToAdmin() started." );
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null for sending sending report bug  mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending report bug  mail " );
        }

        LOG.debug( "Saving EmailEntity with recipient mail id : {}", recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_HELP_MAIL_TO_ADMIN );
        //set the attachments detail
        emailEntity.setAttachments( attachments );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.HELP_MAIL_TO_SS_ADMIN_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.HELP_MAIL_TO_SS_ADMIN_BODY );

        messageBodyReplacements.setReplacementArgs(
            Arrays.asList( appLogoUrl, displayName, senderName, senderEmail, mailSubject, messageBodyText ) );

        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );

        LOG.debug( "Method sendHelpMailToAdmin() finished." );
    }


    @Override
    public void sendZillowCallExceededMailToAdmin( int count ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "Method sendZillowCallExceededMailToAdmin() started" );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( applicationAdminEmail );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_ZILLOW_CALL_EXCEEDED_MAIL_TO_ADMIN );
        String subject = "Zillow API call exceeded for the day";
        String mailBody = "Zillow API call exceeded for the day. Call count : " + count;
        LOG.trace( "Calling email sender to send mail" );
        sendEmail( emailEntity, subject, mailBody, false, false );
        LOG.debug( "Method sendZillowCallExceededMailToAdmin() finished" );
    }


    /**
     * Method to prepare email entity required to send email
     *
     * @param recipientMailId
     * @return
     */
    private EmailEntity prepareEmailEntityForSendingEmail( String recipientMailId )
    {
        List<String> recipients = new ArrayList<String>();
        recipients.add( recipientMailId );

        return prepareEmailEntityForSendingEmail( recipients );
    }


    /**
     * Method to prepare email entity required to send email
     *
     * @param recipients
     * @return
     */
    private EmailEntity prepareEmailEntityForSendingEmail( List<String> recipients )
    {
        LOG.debug( "Preparing email entity for registration invitation for recipientMailId {}", recipients );

        EmailEntity emailEntity = new EmailEntity();
        emailEntity.setRecipients( recipients );
        emailEntity.setRecipientType( EmailEntity.RECIPIENT_TYPE_TO );

        LOG.debug( "Prepared email entity for registrationInvite" );
        return emailEntity;
    }


    // creating email entity with senders email id as U<userid>@socialsurvey.me
    private EmailEntity prepareEmailEntityForSendingEmail( String recipientMailId, long userId, long companyId,
        boolean sentFromCompany, String senderName ) throws InvalidInputException
    {
        LOG.debug( "Preparing email entity with recipent {} user id {} and name {}", recipientMailId, userId, senderName );
        String fromEmailId;
        EmailEntity emailEntity = new EmailEntity();
        OrganizationUnitSettings companySettings = null;

        //set recipients
        List<String> recipients = new ArrayList<String>();
        recipients.add( recipientMailId );
        emailEntity.setRecipients( recipients );

        //set sender name
        emailEntity.setSenderName( senderName );

        //set sender mail
        if ( sentFromCompany ) {
            companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( companyId,
                MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
            if ( companySettings.getEncryptedId() == null ) {
                companySettings.setEncryptedId( userManagementService.generateUserEncryptedId( companyId ) );
                organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings( CommonConstants.ENCRYPTED_ID,
                    companySettings.getEncryptedId(), companySettings,
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
            }
            fromEmailId = "c-" + companySettings.getEncryptedId();
        } else {
            AgentSettings agentSettings = organizationUnitSettingsDao.fetchAgentSettingsById( userId );
            //JIRA SS-700 begin
            if ( agentSettings.getUserEncryptedId() == null ) {
                agentSettings.setUserEncryptedId( userManagementService.generateUserEncryptedId( agentSettings.getIden() ) );
                organizationUnitSettingsDao.updateParticularKeyAgentSettings( CommonConstants.USER_ENCRYPTED_ID,
                    agentSettings.getUserEncryptedId(), agentSettings );
            }
            fromEmailId = "u-" + agentSettings.getUserEncryptedId();

            //get company settings
            companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(
                userDao.findById( User.class, userId ).getCompany().getCompanyId(),
                MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
        }


        //set send email through
        String sendEmailThrough = companySettings.getSendEmailThrough();
        emailEntity.setSendEmailThrough( sendEmailThrough );


        //checking for the right defaultEmailDomain
        String defaultEmailDomain = null;
        if ( sendEmailThrough == null || sendEmailThrough.isEmpty()
            || sendEmailThrough.equals( CommonConstants.SEND_EMAIL_THROUGH_SOCIALSURVEY_ME ) ) {
            defaultEmailDomain = defaultSendGridMeEmailDomain;
        } else if ( sendEmailThrough.equals( CommonConstants.SEND_EMAIL_THROUGH_SOCIALSURVEY_US ) ) {
            defaultEmailDomain = defaultSendGridUsEmailDomain;
        }
        //get full send email address
        String fullFromEmailId = fromEmailId + "@" + defaultEmailDomain;
        emailEntity.setSenderEmailId( fullFromEmailId );

        emailEntity.setRecipientType( EmailEntity.RECIPIENT_TYPE_TO );

        LOG.trace( "Prepared email entity for sending mail" );
        return emailEntity;
    }


    /**
     *
     */
    @Override
    public void sendReportBugMailToAdmin( String displayName, String errorMsg, String recipientMailId )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "Method sendReportBugMailToAdmin() started." );
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null for sending sending report bug  mail. " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending report bug  mail " );
        }

        LOG.debug( "Saving EmailEntity with recipient mail id : {}", recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_REPORT_BUG_MAIL_TO_ADMIN );

        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.REPORT_BUG_MAIL_TO_ADMIN_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.REPORT_BUG_MAIL_TO_ADMIN_BODY );

        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, displayName, errorMsg ) );

        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );

        LOG.debug( "Method sendReportBugMailToAdmin() finished." );
    }


    @Override
    public void sendReportBugMailToAdminForExceptionInBatch( String displayName, String batchName, String lastRunTime,
        String errorMsg, String exceptionStackTrace, String recipientMailId )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "Method sendReportBugMailToAdminForExceptionInBatch() started." );
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null for sendReportBugMailToAdminForExceptionInBatch " );
            throw new InvalidInputException(
                "Recipient email Id is empty or null for sendReportBugMailToAdminForExceptionInBatch " );
        }

        LOG.debug( "Saving EmailEntity with recipient mail id : {}", recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_REPORT_BUG_MAIL_TO_ADMIN_FOR_EXCEPTION_IN_BATCH );

        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.REPORT_EXCEPTION_IN_BATCH_TO_ADMIN_SUBJECT;

        FileContentReplacements messageSubjectReplacements = new FileContentReplacements();
        messageSubjectReplacements.setFileName( subjectFileName );
        messageSubjectReplacements.setReplacementArgs( Arrays.asList( batchName ) );

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.REPORT_EXCEPTION_IN_BATCH_TO_ADMIN_BODY );

        messageBodyReplacements.setReplacementArgs(
            Arrays.asList( appLogoUrl, displayName, batchName, lastRunTime, errorMsg, exceptionStackTrace ) );

        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithSubjectAndBodyReplacements( emailEntity, messageSubjectReplacements, messageBodyReplacements, false,
            false );

        LOG.debug( "Method sendReportBugMailToAdminForExceptionInBatch() finished." );
    }


    @Async
    @Override
    public void sendComplaintHandleMail( String recipientMailId, String customerName, String customerMailId, String agentName,
        String mood, String rating, String surveySourceId, String surveyDetail, String loanId )
        throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null for sending survey completion mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending survey complaint handler mail " );
        }

        if ( customerMailId == null || customerMailId.isEmpty() ) {
            LOG.warn( "Customer email Id is empty or null " );
            throw new InvalidInputException( "Customer email Id is empty or null " );
        }

        if ( agentName == null || agentName.isEmpty() ) {
            LOG.warn( "Agent Name  is empty or null " );
            throw new InvalidInputException( "Agent Name is empty or null " );
        }

        //SS-1435: Send survey details too. Check that it is not null.
        if ( surveyDetail == null || surveyDetail.isEmpty() ) {
            LOG.warn( "surveyDetail parameter is empty or null for sending account upgrade mail " );
            throw new InvalidInputException( "surveyDetail parameter is empty or null for sending survey completion mail " );
        }

        if(loanId == null) loanId = "N/A";

        String[] mailIds = recipientMailId.split( "," );
        List<String> mailIdList = new ArrayList<String>();

        for ( String mailId : mailIds ) {
            mailIdList.add( mailId.trim() );
        }

        LOG.debug( "Sending complaint handle email to : {}", recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( mailIdList );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_COMPLAINT_HANDLE_MAIL );

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_COMPLAINT_HANDLER_MAIL_BODY );

        //SS-1435: Send survey details too.
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, customerName, customerName, customerMailId,
            agentName, mood, rating, surveySourceId == null ? CommonConstants.NOT_AVAILABLE : surveySourceId, surveyDetail ) );

        FileContentReplacements messageSubjectReplacements = new FileContentReplacements();
        messageSubjectReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_COMPLAINT_HANDLER_MAIL_SUBJECT );
        messageSubjectReplacements.setReplacementArgs( Arrays.asList( loanId, customerName ) );

        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithSubjectAndBodyReplacements( emailEntity,messageSubjectReplacements,
            messageBodyReplacements, false, false );
        LOG.debug( "Successfully sent survey completion mail" );
    }


    @Async
    @Override
    public void sendAbusiveNotifyMail( String source, String recipientMailId, String customerName, String customerMailId,
        String agentName, String agentMailId, String mood, String rating, String surveySourceId, String feedBack,
        String surveyMarked ) throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null for sending survey completion mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending survey complaint handler mail " );
        }

        if ( customerMailId == null || customerMailId.isEmpty() ) {
            LOG.warn( "Customer email Id is empty or null " );
            throw new InvalidInputException( "Customer email Id is empty or null " );
        }

        if ( agentName == null || agentName.isEmpty() ) {
            LOG.warn( "Agent Name  is empty or null " );
            throw new InvalidInputException( "Agent Name is empty or null " );
        }

        String[] mailIds = recipientMailId.split( "," );
        List<String> mailIdList = new ArrayList<String>();

        for ( String mailId : mailIds ) {
            mailIdList.add( mailId.trim() );
        }

        LOG.debug( "Sending abusive handle email to : {}", recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( mailIdList );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_ABUSIVE_HANDLE_MAIL );

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_ABUSIVE_HANDLER_MAIL_BODY );

        //SS-1435: Send survey details too.
        messageBodyReplacements.setReplacementArgs(
            Arrays.asList( appLogoUrl, customerName, source, feedBack, rating, source, surveyMarked, agentName, agentMailId,
                customerName, customerMailId, surveySourceId == null ? CommonConstants.NOT_AVAILABLE : surveySourceId ) );

        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithBodyReplacements( emailEntity,
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_ABUSIVE_HANDLER_MAIL_SUBJECT,
            messageBodyReplacements, false, false );
        LOG.debug( "Successfully sent survey abusive mail" );
    }


    /**
     * Method to send complaint handle mail to the admin's for zillow reviews
     * @param recipientMailId
     * @param customerName
     * @param rating
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     * */
    @Async
    @Override
    public void sendZillowReviewComplaintHandleMail( String recipientMailId, String customerName, String rating,
        String reviewUrl ) throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null for sending survey completion mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending survey complaint handler mail " );
        }

        if ( customerName == null || customerName.isEmpty() ) {
            LOG.warn( "Customer name is empty or null " );
            throw new InvalidInputException( "Customer name is empty or null " );
        }

        String[] mailIds = recipientMailId.split( "," );
        List<String> mailIdList = new ArrayList<String>();

        for ( String mailId : mailIds ) {
            mailIdList.add( mailId.trim() );
        }

        LOG.debug( "Sending complaint handle email to : {}", recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( mailIdList );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_ZILLOW_REVIEW_COMPLAINT_HANDLE_MAIL );

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.ZILLOW_REVIEW_COMPLAINT_HANDLER_MAIL_BODY );

        //SS-1435: Send survey details too.
        messageBodyReplacements
            .setReplacementArgs( Arrays.asList( appLogoUrl, customerName, customerName, rating, reviewUrl ) );

        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithBodyReplacements( emailEntity,
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.ZILLOW_REVIEW_COMPLAINT_HANDLER_MAIL_SUBJECT,
            messageBodyReplacements, false, false );
        LOG.debug( "Successfully sent survey completion mail" );
    }


    /**
     * Method to forward customer reply to recipient
     *
     * @param recipientMailId
     * @param subject
     * @param mailBody
     * @param senderName
     * @param senderEmailAddress
     * @param messageId
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    @Async
    @Override
    public void forwardCustomerReplyMail( String recipientMailId, String subject, String mailBody, String senderName,
        String senderEmailAddress, String messageId, String sendUsingDomain )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "Executing the sendSurveyReminderMail() method" );
        boolean saveForwardMailDetails = true;

        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null in forwardCustomerReplyMail method " );
            throw new InvalidInputException( "Recipient email Id is empty or null in forwardCustomerReplyMail method " );
        }
        if ( subject == null || subject.isEmpty() ) {
            LOG.warn( "Subject is empty or null in forwardCustomerReplyMail method" );
            throw new InvalidInputException( "Subject is empty or null in forwardCustomerReplyMail method" );
        }
        if ( mailBody == null || mailBody.isEmpty() ) {
            LOG.warn( "Mail Body is empty or null in forwardCustomerReplyMail method" );
            throw new InvalidInputException( "Mail Body is empty or null in forwardCustomerReplyMail method" );
        }
        if ( senderName == null || senderName.isEmpty() ) {
            LOG.warn( "Sender Name is empty or null in forwardCustomerReplyMail method " );
            throw new InvalidInputException( "Sender Name is empty or null in forwardCustomerReplyMail method " );
        }
        if ( senderEmailAddress == null || senderEmailAddress.isEmpty() ) {
            LOG.warn( "Sender email Id is empty or null in forwardCustomerReplyMail method " );
            throw new InvalidInputException( "Sender email Id is empty or null in forwardCustomerReplyMail method " );
        }

        if ( messageId == null || messageId.isEmpty() ) {
            LOG.warn( "Message Id is empty or null in forwardCustomerReplyMail method " );
            throw new InvalidInputException( "Message Id is empty or null in forwardCustomerReplyMail method " );
        }

        try {
            // Find Forward mail details with messageId
            if ( forwardMailDetailsDao.checkIfForwardMailDetailsExist( senderEmailAddress, recipientMailId, messageId ) ) {
                LOG.debug( "This mail has already been sent to the recipient" );
                return;
            }
        } catch ( UnsupportedOperationException uoe ) {
            LOG.warn( "Exception occurred while checking if Forward Mail Details already exists for message Id : " + messageId
                + ".Reason :", uoe );
            saveForwardMailDetails = false;
        }

        // Save Mail details to prevent further sending to same recipient
        if ( saveForwardMailDetails ) {
            ForwardMailDetails forwardMailDetail = new ForwardMailDetails();
            forwardMailDetail.setSenderMailId( senderEmailAddress );
            forwardMailDetail.setRecipientMailId( recipientMailId );
            forwardMailDetail.setMessageId( messageId );

            forwardMailDetailsDao.insertForwardMailDetails( forwardMailDetail );
        }

        LOG.debug( "Forwarding customer reply mail from {} to : {}", senderEmailAddress, recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_FORWARD_CUSTOMER_REPLY_MAIL );
        emailEntity.setSenderName( senderName );
        emailEntity.setSenderEmailId( senderEmailAddress );

        if ( sendUsingDomain != null && sendUsingDomain.equals( defaultSendGridUsEmailDomain ) ) {
            emailEntity.setSendEmailThrough( CommonConstants.SEND_EMAIL_THROUGH_SOCIALSURVEY_US );
        }

        LOG.trace( "Calling email sender to send mail" );
        sendEmail( emailEntity, subject, mailBody, true, false );
        LOG.debug( "Successfully forwarded customer reply mail from {} to : {}", senderEmailAddress, recipientMailId );
    }


    /**
     * Method to send the billing report in a mail to the social survey admin
     */
    @Override
    public void sendBillingReportMail( String firstName, String lastName, String recipientMailId,
        List<EmailAttachment> attachments ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "Method sendBillingReportMail() started." );
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null for sending billing report mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending billing report mail " );
        }

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_BILLING_REPORT_MAIL );
        emailEntity.setAttachments( attachments );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.BILLING_REPORT_MAIL_SUBJECT;
        String displayName = firstName + " " + lastName;
        displayName.replaceAll( "null", "" );
        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.BILLING_REPORT_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, displayName ) );

        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, true, false );

        LOG.debug( "Method sendBillingReportMail() finished." );
    }


    /**
     *
     */
    @Override
    public void sendCustomMail( String recipientName, String recipientMailId, String subject, String body,
        List<EmailAttachment> attachments ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "Method sendCustomMail() started." );
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null for sending custom mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending custom mail " );
        }

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_CUSTOM_MAIL );
        if ( attachments != null && !attachments.isEmpty() )
            emailEntity.setAttachments( attachments );

        FileContentReplacements messageSubjectReplacements = new FileContentReplacements();
        messageSubjectReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.CUSTOM_MAIL_SUBJECT );
        messageSubjectReplacements.setReplacementArgs( Arrays.asList( subject ) );

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.CUSTOM_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, recipientName, body ) );

        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithSubjectAndBodyReplacements( emailEntity, messageSubjectReplacements, messageBodyReplacements, true,
            false );

        LOG.debug( "Method sendCustomReportMail() finished." );
    }


    /**
     * Method to send the billing report in a mail to the social survey admin
     */
    @Override
    public void sendCustomReportMail( String recipientName, List<String> recipientMailIds, String subject,
        List<EmailAttachment> attachments ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "Method sendCustomReportMail() started." );
        if ( recipientMailIds == null || recipientMailIds.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null for sending billing report mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending billing report mail " );
        }

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailIds );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_CUSTOM_REPORT_MAIL );
        emailEntity.setAttachments( attachments );

        FileContentReplacements messageSubjectReplacements = new FileContentReplacements();
        messageSubjectReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SEND_REPORT_MAIL_SUBJECT );
        messageSubjectReplacements.setReplacementArgs( Arrays.asList( subject ) );

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SEND_REPORT_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, recipientName ) );

        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithSubjectAndBodyReplacements( emailEntity, messageSubjectReplacements, messageBodyReplacements, false,
            false );

        LOG.debug( "Method sendCustomReportMail() finished." );
    }


    /**
     *
     */
    @Override
    public void sendSocialMediaTokenExpiryEmail( String displayName, String recipientMailId, String updateConnectionUrl,
        String appLoginUrl, String socialMediaType ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "Method sendSocialMediaTokenExpiryEmail() started." );
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null for sending sending report bug  mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending report bug  mail " );
        }

        LOG.debug( "Saving EmailEntity with recipient mail id : {}", recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_SOCIAL_MEDIA_TOKEN_EXPIRY_EMAIL );

        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SOCIAL_MEDIA_TOKEN_EXPIRY_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        
        //DONT ADD UPDATE CONNECTION URL FOR FACEBOOK. INSTEAD OF USE SS LOGIN URL.
        if(socialMediaType.equalsIgnoreCase(CommonConstants.FACEBOOK_SOCIAL_SITE)) {
        		messageBodyReplacements.setFileName(
                    EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.FACEBOOK_TOKEN_EXPIRY_EMAIL_BODY );
        		messageBodyReplacements
                .setReplacementArgs( Arrays.asList( appLogoUrl, displayName, socialMediaType, appLoginUrl ) );
        }else {
        		messageBodyReplacements.setFileName(
                    EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SOCIAL_MEDIA_TOKEN_EXPIRY_MAIL_BODY );
        		messageBodyReplacements
                .setReplacementArgs( Arrays.asList( appLogoUrl, displayName, socialMediaType, updateConnectionUrl, appLoginUrl ) );
        }
        

        

        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );

        LOG.debug( "Method sendSocialMediaTokenExpiryEmail() finished." );
    }


    @Async
    @Override
    public void sendPaymentRetriesFailedAlertEmailToAdmin( String recipientMailId, String displayName, String companyName,
        long companyId ) throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null for sending retries exhausted mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending payment faield alert mail " );
        }
        if ( displayName == null || displayName.isEmpty() ) {
            LOG.warn( "displayName parameter is empty or null for sending retry exhausted mail " );
            throw new InvalidInputException( "displayName parameter is empty or null for sending payment faield alert mail " );
        }

        LOG.debug( "Sending payment faield alert email to : {}", recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_PAYMENT_FAILED_ALERT_EMAIL );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.PAYMENT_RETRIES_FAILED_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.PAYMENT_RETRIES_FAILED_MAIL_BODY );
        messageBodyReplacements
            .setReplacementArgs( Arrays.asList( appLogoUrl, displayName, companyName, Long.toString( companyId ) ) );

        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.debug( "Successfully sent payment retry failed alert mail" );
    }


    @Async
    @Override
    public void sendPaymentFailedAlertEmailToAdmin( String recipientMailId, String displayName, String companyName,
        long companyId ) throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending retries  mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending payment faield alert mail " );
        }
        if ( displayName == null || displayName.isEmpty() ) {
            LOG.error( "displayName parameter is empty or null for sending retry  mail " );
            throw new InvalidInputException( "displayName parameter is empty or null for sending payment faield alert mail " );
        }

        LOG.debug( "Sending payment faield alert email to : {}", recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_PAYMENT_FAILED_ALERT_EMAIL_TO_ADMIN );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.PAYMENT_FAILED_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.PAYMENT_FAILED_MAIL_BODY );
        messageBodyReplacements
            .setReplacementArgs( Arrays.asList( appLogoUrl, displayName, companyName, Long.toString( companyId ) ) );

        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.debug( "Successfully sent payment faield alert mail" );
    }


    @Async
    @Override
    public void sendCancelSubscriptionRequestAlertMail( String recipientMailId, String displayName, String companyName )
        throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null for sending retries exhausted mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending payment faield alert mail " );
        }
        if ( displayName == null || displayName.isEmpty() ) {
            LOG.warn( "displayName parameter is empty or null for sending retry exhausted mail " );
            throw new InvalidInputException( "displayName parameter is empty or null for sending payment faield alert mail " );
        }

        LOG.debug( "Sending payment faield alert email to : {}", recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_CANCEL_SUBSCRIPTION_REQUEST_ALERT_MAIL );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.CANCEL_SUBSCRIPTION_REQUEST_ALERT_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.CANCEL_SUBSCRIPTION_REQUEST_ALERT_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, displayName, companyName ) );

        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.debug( "Successfully sent payment faield alert mail" );
    }


    @Async
    @Override
    public void sendWebExceptionEmail( String recipientMailId, String stackTrace )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "Sending WebException email to the admin." );
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null for sending web exception mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending web exception mail " );
        }

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_WEB_EXCEPTION_EMAIL );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.WEB_EXCEPTION_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.FATAL_EXCEPTION_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, currentProfile, stackTrace ) );

        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.debug( "Successfully sent web exception mail" );
    }


    @Async
    @Override
    public void sendNoTransactionAlertMail( List<String> recipientMailIds, String mailBody )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.info( "method sendNoTransactionAlertMail started" );
        if ( recipientMailIds == null || recipientMailIds.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sendNoTransactionAlertMail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sendNoTransactionAlertMail " );
        }
        if ( mailBody == null || mailBody.isEmpty() ) {
            LOG.warn( "mailBody is empty or null for sendNoTransactionAlertMail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sendNoTransactionAlertMail " );
        }


        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailIds );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_NO_TRANSACTION_ALERT_MAIL );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.NO_TRANSACTION_RECEIVED_ALERT_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.NO_TRANSACTION_RECEIVED_ALERT_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, mailBody ) );

        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.debug( "method sendNoTransactionAlertMail ended" );
    }


    @Async
    @Override
    public void sendHighVoulmeUnprocessedTransactionAlertMail( List<String> recipientMailIds, String mailBody )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.info( "method sendHighVoulmeUnprocessedTransactionAlertMail started" );
        if ( recipientMailIds == null || recipientMailIds.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sendHighVoulmeUnprocessedTransactionAlertMail " );
            throw new InvalidInputException(
                "Recipient email Id is empty or null for sendHighVoulmeUnprocessedTransactionAlertMail " );
        }
        if ( mailBody == null || mailBody.isEmpty() ) {
            LOG.error( "mailBody is empty or null for sendHighVoulmeUnprocessedTransactionAlertMail " );
            throw new InvalidInputException(
                "Recipient email Id is empty or null for sendHighVoulmeUnprocessedTransactionAlertMail " );
        }

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailIds );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_HIGH_VOULME_UNPROCESSED_TRANSACTION_ALERT_MAIL );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.HIGH_VOLUME_UNPROCESSED_TRANSACTION_ALERT_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.HIGH_VOLUME_UNPROCESSED_TRANSACTION_ALERT_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, mailBody ) );

        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.debug( "method sendHighVoulmeUnprocessedTransactionAlertMail ended" );
    }


    @Async
    @Override
    public void sendLessVoulmeOfTransactionReceivedAlertMail( List<String> recipientMailIds, String mailBody )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.info( "method sendLessVoulmeOfTransactionReceivedAlertMail started" );
        if ( recipientMailIds == null || recipientMailIds.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sendLessVoulmeOfTransactionReceivedAlertMail " );
            throw new InvalidInputException(
                "Recipient email Id is empty or null for sendLessVoulmeOfTransactionReceivedAlertMail " );
        }
        if ( mailBody == null || mailBody.isEmpty() ) {
            LOG.error( "mailBody is empty or null for sendLessVoulmeOfTransactionReceivedAlertMail " );
            throw new InvalidInputException( "Mail body is empty or null for sendLessVoulmeOfTransactionReceivedAlertMail " );
        }


        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailIds );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_LESS_VOULME_OF_TRANSACTION_RECEIVED_ALERT_MAIL );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.LESS_VOLUME_OF_TRANSACTION_RECEIVED_ALERT_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.LESS_VOLUME_OF_TRANSACTION_RECEIVED_ALERT_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, mailBody ) );

        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.debug( "method sendLessVoulmeOfTransactionReceivedAlertMail ended" );
    }


    @Async
    @Override
    public void sendMonthlyDigestMail( MonthlyDigestAggregate digestAggregate )
        throws InvalidInputException, UndeliveredEmailException
    {

        LOG.info( "method sendMonthlyDigestMail() started" );
        digestMailHelper.validateDigestAggregate( digestAggregate, false );

        String emails = StringUtils.join( digestAggregate.getRecipientMailIds(), "," );
        LOG.debug( "Sending Monthly digest/snapshot email to : {}", emails );


        // prepare Email Entity for all digest recipients 
        for ( String digestRecipient : digestAggregate.getRecipientMailIds() ) {

            if ( StringUtils.isNotEmpty( digestRecipient ) ) {

                EmailEntity emailEntity = prepareEmailEntityForSendingEmail( Arrays.asList( digestRecipient ) );

                FileContentReplacements messageSubjectReplacements = new FileContentReplacements();
                FileContentReplacements messageBodyReplacements = new FileContentReplacements();
                List<String> messageSubjectReplacementsList = new ArrayList<>();
                List<String> messageBodyReplacementsList = new ArrayList<>();

                // setup message and body replacements
                messageSubjectReplacements
                    .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.DIGEST_MAIL_SUBJECT );
                messageSubjectReplacements.setReplacementArgs( messageSubjectReplacementsList );

                messageBodyReplacements
                    .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.DIGEST_MAIL_BODY );
                messageBodyReplacements.setReplacementArgs( messageBodyReplacementsList );


                digestMailHelper.buildDigestMailReplacents( digestAggregate, messageSubjectReplacementsList,
                    messageBodyReplacementsList, digestRecipient, false );

                emailEntity.setMailType( CommonConstants.EMAIL_TYPE_MONTHLY_DIGEST_MAIL );

                LOG.debug( "Calling email sender to send digest mail to {}", digestAggregate );
                sendEmailWithSubjectAndBodyReplacements( emailEntity, messageSubjectReplacements, messageBodyReplacements,
                    false, false );

            } else {
                LOG.debug( "empty digest recipient found in email list." );
            }
        }

        LOG.debug( "Successfully sent Monthly digest/snapshot mail" );

    }


    @Async
    @Override
    public void sendDigestErrorMailForCompany( DigestRequestData digestRequest, String stackTrace )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "sendDigestErrorMailForCompany() started" );
        if ( digestRequest == null ) {
            LOG.warn( "Digest request data is not specified" );
            throw new InvalidInputException( "Digest request data is not specified" );
        } else if ( StringUtils.isEmpty( digestRequest.getProfileLevel() ) ) {
            LOG.warn( "Profile level is not Specified." );
            throw new InvalidInputException( "Profile level is not Specified." );
        } else if ( StringUtils.isEmpty( digestRequest.getEntityName() ) ) {
            LOG.warn( "Entity Name is not Specified." );
            throw new InvalidInputException( "Entity Name is not Specified." );
        } else if ( StringUtils.isEmpty( stackTrace ) ) {
            LOG.warn( "Reason for failure not Specified." );
            throw new InvalidInputException( "Reason for failure not Specified." );
        }

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( applicationAdminEmail );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_DIGEST_ERROR_MAIL_FOR_COMPANY );

        FileContentReplacements messageSubjectReplacements = new FileContentReplacements();
        messageSubjectReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.DIGEST_ERROR_MAIL_SUBJECT );
        messageSubjectReplacements.setReplacementArgs( Arrays.asList( digestRequest.getEntityName() ) );

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.DIGEST_ERROR_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs(
            Arrays.asList( appLogoUrl, digestRequest.getProfileLevel(), digestRequest.getEntityName(), stackTrace ) );

        LOG.trace( "sendDigestErrorMailForCompany() finishing" );
        sendEmailWithSubjectAndBodyReplacements( emailEntity, messageSubjectReplacements, messageBodyReplacements, false,
            false );
    }


    @Async
    @Override
    public void sendFtpProcessingErrorMailForCompany( Set<String> recipients, long companyId, String reason, String stackTrace, boolean isFromBatch, boolean sendOnlyToSocialSurveyAdmin )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "sendFtpProcessingErrorMailForCompany() started" );
        if ( companyId <= 0 ) {
            LOG.warn( "company ID is not specified" );
            throw new InvalidInputException( "company ID is not specified" );
        } else if ( StringUtils.isEmpty( stackTrace ) ) {
            LOG.warn( "error stack trace not Specified" );
            throw new InvalidInputException( "error stack trace not Specified" );
        } else if ( reason == null ) {
            LOG.warn( "Reason for failure not Specified" );
            throw new InvalidInputException( "Reason for failure not Specified" );
        }
        
        List<String> mailRecipients = new ArrayList<>();
        
        if( recipients != null && !recipients.isEmpty() ) {
            mailRecipients.addAll( recipients );
        }
        
        EmailEntity emailEntityForSSAdmin = prepareEmailEntityForSendingEmail( applicationAdminEmail );
        emailEntityForSSAdmin.setMailType( CommonConstants.EMAIL_TYPE_FTP_FILE_UPLOADER );        

        FileContentReplacements messageSubjectReplacements = new FileContentReplacements();
        messageSubjectReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.FTP_BATCH_ERROR_MAIL_SUBJECT );
        messageSubjectReplacements
            .setReplacementArgs( Arrays.asList( isFromBatch ? "batch" : "topology", String.valueOf( companyId ) ) );

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.FTP_BATCH_ERROR_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs(
            Arrays.asList( appLogoUrl, isFromBatch ? "batch" : "topology", String.valueOf( companyId ), reason, stackTrace ) );
        

        LOG.trace( "sendFtpProcessingErrorMailForCompany() finishing" );
        sendEmailWithSubjectAndBodyReplacements( emailEntityForSSAdmin, messageSubjectReplacements, messageBodyReplacements, false,
            false );
        
        if( !mailRecipients.isEmpty() && !sendOnlyToSocialSurveyAdmin ) {
            
            EmailEntity emailEntityForRecipients = prepareEmailEntityForSendingEmail( mailRecipients );
            emailEntityForRecipients.setMailType( CommonConstants.EMAIL_TYPE_FTP_FILE_UPLOADER );
        
            // don't show stack trace for configured mails  
            messageBodyReplacements.setReplacementArgs(
                Arrays.asList( appLogoUrl, isFromBatch ? "batch" : "topology", String.valueOf( companyId ), reason, StringUtils.EMPTY ) );
            
            sendEmailWithSubjectAndBodyReplacements( emailEntityForRecipients, messageSubjectReplacements, messageBodyReplacements, false,
                false );
        }
        
        
    }


    @Override
    public void sendEmailToAdminForUnsuccessfulSurveyCsvUpload( SurveyCsvInfo csvInfo, String errorMessage )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "method sendEmailToAdminForUnsuccessfulSurveyCsvUpload called" );

        if ( csvInfo == null ) {
            LOG.error( "Upload information is missing." );
            throw new InvalidInputException( "Can't send email: Survey csv Upload information is missing." );
        }
        if ( StringUtils.isEmpty( errorMessage ) ) {
            LOG.error( "error message is missing." );
            throw new InvalidInputException( "Can't send email: Survey csv Upload message is missing." );
        }

        LOG.debug( "Sending survey csv upload unsuccessful email to : " + applicationAdminEmail );

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( applicationAdminEmail );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_UNSUCCESSFUL_SURVEY_CSV_UPLOAD_MAIL_TO_ADMIN );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SURVEY_CSV_UPLOAD_UNSUCCESSFUL_ADMIN_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();

        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_CSV_UPLOAD_UNSUCCESSFUL_ADMIN_BODY );

        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl,
            StringUtils.isEmpty( csvInfo.getFileName() ) ? CommonConstants.NOT_AVAILABLE : csvInfo.getFileName(),
            csvInfo.getUploadedDate() == null ? CommonConstants.NOT_AVAILABLE : df.format( csvInfo.getUploadedDate() ),
            StringUtils.isEmpty( csvInfo.get_id() ) ? CommonConstants.NOT_AVAILABLE : csvInfo.get_id(), errorMessage ) );

        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, true, false );
    }


    @Override
    public void sendEmailToUploaderForUnsuccessfulSurveyCsvUpload( SurveyCsvInfo csvInfo, String message )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "method sendEmailToUploaderForUnsuccessfulSurveyCsvUpload called" );

        if ( csvInfo == null ) {
            LOG.error( "Upload information is missing." );
            throw new InvalidInputException( "Can't send email: Survey csv Upload information is missing." );
        }

        if ( StringUtils.isEmpty( message ) ) {
            LOG.error( "error message is missing." );
            throw new InvalidInputException( "Can't send email: Survey csv Upload message is missing." );
        }

        if ( StringUtils.isEmpty( csvInfo.getUploaderEmail() ) ) {
            LOG.error( "Recipient email Id is Missing" );
            throw new InvalidInputException( "Can't send email: Recipient email Id is Missing." );
        }

        LOG.debug( "Sending survey csv upload unsuccessful email to : " + csvInfo.getUploaderEmail() );

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( csvInfo.getUploaderEmail() );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_UNSUCCESSFUL_SURVEY_CSV_UPLOAD_MAIL_TO_UPLOADER );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SURVEY_CSV_UPLOAD_UNSUCCESSFUL_AGENT_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();

        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_CSV_UPLOAD_UNSUCCESSFUL_AGENT_BODY );

        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, csvInfo.getUploaderEmail().split( "@" )[0],
            StringUtils.isEmpty( csvInfo.getFileName() ) ? CommonConstants.NOT_AVAILABLE : csvInfo.getFileName(),
            csvInfo.getUploadedDate() == null ? CommonConstants.NOT_AVAILABLE : df.format( csvInfo.getUploadedDate() ),
            StringUtils.isEmpty( csvInfo.get_id() ) ? CommonConstants.NOT_AVAILABLE : csvInfo.get_id(), message ) );

        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, true, false );

    }


    @Override
    public void sendEmailToUploaderForSuccessfulSurveyCsvUpload( SurveyCsvInfo csvInfo, String results )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "method sendEmailToUploaderForSuccessfulSurveyCsvUpload called" );

        if ( csvInfo == null ) {
            LOG.error( "Upload information is missing." );
            throw new InvalidInputException( "Can't send email: Survey csv Upload information is missing." );
        }

        if ( StringUtils.isEmpty( csvInfo.getUploaderEmail() ) ) {
            LOG.error( "Recipient email Id is Missing" );
            throw new InvalidInputException( "Can't send email: Recipient email Id is Missing." );
        }

        LOG.debug( "Sending survey csv upload unsuccessful email to : " + csvInfo.getUploaderEmail() );

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( csvInfo.getUploaderEmail() );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_SUCCESSFUL_SURVEY_CSV_UPLOAD_MAIL_TO_UPLOADER );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SURVEY_CSV_UPLOAD_SUCCESSFUL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();

        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_CSV_UPLOAD_SUCCESSFUL_BODY );

        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, csvInfo.getUploaderEmail().split( "@" )[0],
            StringUtils.isEmpty( csvInfo.getFileName() ) ? CommonConstants.NOT_AVAILABLE : csvInfo.getFileName(),
            csvInfo.getUploadedDate() == null ? CommonConstants.NOT_AVAILABLE : df.format( csvInfo.getUploadedDate() ),
            results ) );

        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, true, false );
    }


    @Async
	@Override
    public void sendSocialMonitorActionMail( SocialResponseObject socialResponseObject, SocialFeedsActionUpdate socialFeedsActionUpdate,
        String previousStatus, String currentStatus )
        throws InvalidInputException, UndeliveredEmailException
    {
        String recipientMailId = socialResponseObject.getOwnerEmail();
        String recipientName = socialResponseObject.getOwnerName();
        String mailBody = socialFeedsActionUpdate.getText();
        String userName = socialFeedsActionUpdate.getUserName();
        String userEmailId = socialFeedsActionUpdate.getUserEmailId();
        String feedType = socialResponseObject.getType().toString().toLowerCase();
        
        long branchId = socialResponseObject.getBranchId();
        long regionId = socialResponseObject.getRegionId();
        List<UserProfile> recipient = null;
        
        if(branchId != 0 || regionId != 0)
        {
        	if(branchId != 0)
        	{
        		regionId=branchDao.getRegionIdByBranchId(branchId);
        	}
        	recipient=userProfileDao.getImmediateAdminForRegionOrBranch(socialResponseObject.getCompanyId(), regionId, branchId);
        }
        
		LOG.info( "method sendSocialMonitorActionMail started" );
        if ( (recipientMailId == null || recipientMailId.isEmpty()) && (recipient==null || recipient.isEmpty())) {
            LOG.error( "Recipient email Id is empty or null for sendSocialMonitorActionMail " );
            throw new InvalidInputException(
                "Recipient email Id is empty or null for sendSocialMonitorActionMail " );
        }
        if ( mailBody == null || mailBody.isEmpty() ) {
            LOG.error( "mailBody is empty or null for sendSocialMonitorActionMail " );
            throw new InvalidInputException( "Mail body is empty or null for sendSocialMonitorActionMail " );
        }
        
        
        List<UserProfile> admins = userProfileDao.getImmediateAdminForAgent(socialResponseObject.getAgentId(),socialResponseObject.getCompanyId());
        
        // Set
        String senderEmailAddress = "post-"+ socialResponseObject.getId() +"@" + defaultSendGridMeEmailDomain;
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
                + EmailTemplateConstants.SOCIAL_MONITOR_ACTION_MAIL_SUBJECT;
     
        String message = null;
        
        if ( previousStatus != null && currentStatus != null ) {
            message = "Your " + feedType + " post was moved from <b>" + previousStatus + "</b> to <b>" + currentStatus
                + "</b> by " + userName + " [" + userEmailId + "] with message,";
        } else {
            message = "Your " + feedType + " post has a message from " + userName + " [" + userEmailId + "] " + ",";
        }
        String postLinkText= "Here is a link to the post - " + socialResponseObject.getPostLink();
        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SOCIAL_MONITOR_ACTION_MAIL_BODY );
        
        if(recipientMailId != null) {
        	EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        	// Setting default name
        	emailEntity.setSenderName( defaultSendName );
        	emailEntity.setSenderEmailId(senderEmailAddress); 
        	emailEntity.setMailType( CommonConstants.EMAIL_TYPE_SOCIAL_MONITOR_ACTION_MAIL_TO_USER );
        	messageBodyReplacements.setReplacementArgs(
        			Arrays.asList( appLogoUrl, recipientName, message, mailBody, postLinkText ) );
           
        	LOG.trace( "Calling email sender to send mail" );
        	sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false,
        			false, socialResponseObject.getType().toString() );
        }
        
        if(recipient != null)
        {
        	Iterator<UserProfile> iterator = recipient.iterator();
        	while (iterator.hasNext())
        	{
        		EmailEntity emailEntityRecipient;
        		UserProfile res = iterator.next();
        		emailEntityRecipient = prepareEmailEntityForSendingEmail(res.getEmailId());
        		emailEntityRecipient.setSenderEmailId(senderEmailAddress);
        		emailEntityRecipient.setSenderName( defaultSendName );
        		emailEntityRecipient.setMailType( CommonConstants.EMAIL_TYPE_SOCIAL_MONITOR_ACTION_MAIL_TO_ADMIN );
        		messageBodyReplacements.setReplacementArgs(
        				Arrays.asList( appLogoUrl, recipientName, message, mailBody, postLinkText ) );
        		LOG.trace("Calling email sender to send email for owner");
        		sendEmailWithBodyReplacements( emailEntityRecipient, subjectFileName, messageBodyReplacements, false, false,
        				false, socialResponseObject.getType().toString() );
        	}
        }
        
        if(!admins.isEmpty() && admins != null )
        {
        	String adminMessage=null;
    		if ( previousStatus != null && currentStatus != null ) {
    			adminMessage = recipientName+"'s " + feedType + " post was moved from <b>" + previousStatus + "</b> to <b>" + currentStatus
    					+ "</b> by " + userName + " [" + userEmailId + "] with message,";
    		} else {
    			adminMessage = recipientName+"'s " + feedType + " post has a message from " + userName + " [" + userEmailId + "] " + ",";
    		}
    		String postLinkTextAdmin= "Here is a link to the post - " + socialResponseObject.getPostLink();
        	Iterator<UserProfile> iterator = admins.iterator();
        	while (iterator.hasNext())
        	{
        		EmailEntity emailEntityAdmin;
        		UserProfile admin = iterator.next();
        		emailEntityAdmin = prepareEmailEntityForSendingEmail(admin.getEmailId());
        		emailEntityAdmin.setSenderEmailId(senderEmailAddress);
        		emailEntityAdmin.setSenderName( defaultSendName );
        		emailEntityAdmin.setMailType( CommonConstants.EMAIL_TYPE_SOCIAL_MONITOR_ACTION_MAIL_TO_ADMIN );
        		messageBodyReplacements.setReplacementArgs(
        				Arrays.asList( appLogoUrl, "Administrator", adminMessage, mailBody, postLinkTextAdmin ) );
        		LOG.trace("Calling email sender to send email for admin");
        		sendEmailWithBodyReplacements( emailEntityAdmin, subjectFileName, messageBodyReplacements, false, false,
        				false, socialResponseObject.getType().toString() );
        	}    	
        }
        
        LOG.debug( "method sendSocialMonitorActionMail ended" );
	}
    
    @Async
    @Override
    public void sendUserAdditionMail( Set<String> recipients, String addedAdminName, String addedAdminEmailId,
        User addedUser, OrganizationUnitSettings agentSettings ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "method sendUserAdditionMail() called" );

        if ( recipients == null || recipients.isEmpty() ) {
            LOG.warn( "No recipients for user addition mail specified" );
            throw new InvalidInputException( "No recipients for user addition mail specified" );
        } else if ( addedUser == null ) {
            LOG.warn( "User details for added user not specified" );
            throw new InvalidInputException( "User details for added user not specified" );
        } else if ( agentSettings == null ) {
            LOG.warn( "User settings for the added user not specified" );
        }

        if ( LOG.isTraceEnabled() ) {
            LOG.trace( "sending user addition mail for user {}, to {}", addedUser, recipients );
        }

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( new ArrayList<>( recipients ) );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_USER_ADDITION_MAIL );

        FileContentReplacements messageSubjectReplacements = new FileContentReplacements();
        messageSubjectReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.USER_ADDITION_MAIL_SUBJECT );

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.CUSTOM_MAIL_BODY );

        messageBodyReplacements
            .setReplacementArgs( Arrays.asList( appLogoUrl, CommonConstants.ADMIN_RECEPIENT_DISPLAY_NAME, emailFormatHelper
                .buildAgentAdditionOrDeletionMessage( addedAdminName, addedAdminEmailId, addedUser, agentSettings, true ) ) );

        sendEmailWithSubjectAndBodyReplacements( emailEntity, messageSubjectReplacements, messageBodyReplacements, false,
            false );

        LOG.debug( "method sendUserAdditionMail() finished" );
    }

    //@Async
    @Override
    public boolean sendUserDeletionMail( Set<String> recipients, String deletedAdminName, String deletedAdminEmailId,
        User deletedUser, OrganizationUnitSettings agentSettings ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "method sendUserDeletionMail() called" );

        if ( recipients == null || recipients.isEmpty() ) {
            LOG.warn( "No recipients for user deletion mail specified" );
            throw new InvalidInputException( "No recipients for user deletion mail specified" );
        } else if ( deletedUser == null ) {
            LOG.warn( "User details not specified for the user to be deleted" );
            throw new InvalidInputException( "User details not specified for the user to be deleted" );
        } else if ( agentSettings == null ) {
            LOG.warn( "User settings not specified for the user to be deleted" );
        }

        if ( LOG.isTraceEnabled() ) {
            LOG.trace( "sending user deletion mail for user {}, to {}", deletedUser, recipients );
        }

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( new ArrayList<>( recipients ) );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_USER_DELETION_MAIL );

        FileContentReplacements messageSubjectReplacements = new FileContentReplacements();
        messageSubjectReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.USER_DELETION_MAIL_SUBJECT );

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.CUSTOM_MAIL_BODY );

        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, CommonConstants.ADMIN_RECEPIENT_DISPLAY_NAME,
            emailFormatHelper.buildAgentAdditionOrDeletionMessage( deletedAdminName, deletedAdminEmailId, deletedUser,
                agentSettings, false ) ) );

        sendEmailWithSubjectAndBodyReplacements( emailEntity, messageSubjectReplacements, messageBodyReplacements, false,
            false );

        LOG.debug( "method sendUserDeletionMail() finished" );
        return true;
    }

    @Async
    @Override
    public void sendFtpSuccessMail( String companyName, String fileDate, String fileName, FtpSurveyResponse ftpSurveyResponse,
        String ftpMailId, String ftpErrorHtml ) throws InvalidInputException, UndeliveredEmailException
    {
        if ( ftpMailId == null || ftpMailId.isEmpty() ) {
            LOG.warn( "Recipient email Id is empty or null for sending ftp success mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending ftp success mail " );
        }

        String[] mailIds = ftpMailId.split( "," );
        List<String> mailIdList = new ArrayList<>();

        for ( String mailId : mailIds ) {
            mailIdList.add( mailId.trim() );
        }

        LOG.debug( "Sending ftp success email to : {}", ftpMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( mailIdList );
        emailEntity.setMailType( CommonConstants.EMAIL_TYPE_FTP_SUCCESSFULLY_PROCESSED_MAIL );

        FileContentReplacements messageSubjectReplacements = new FileContentReplacements();
        messageSubjectReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.FTP_SUCCESSFULLY_PROCESSED_MAIL_SUBJECT );
        messageSubjectReplacements.setReplacementArgs( Arrays.asList( companyName ) );

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.FTP_SUCCESSFULLY_PROCESSED_MAIL_BODY );

        //SS-1435: Send survey details too.
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, companyName, fileName, fileDate,
            String.valueOf( ftpSurveyResponse.getTotalTransaction() ), String.valueOf( ftpSurveyResponse.getTotalSurveys() ),
            String.valueOf( ftpSurveyResponse.getCustomer1Count() ), String.valueOf( ftpSurveyResponse.getCustomer2Count() ),
            String.valueOf( ftpSurveyResponse.getBuyerCount() ), String.valueOf( ftpSurveyResponse.getSellerCount() ),
            String.valueOf( ftpSurveyResponse.getErrorNum() ), ftpErrorHtml ) );

        LOG.trace( "Calling email sender to send mail" );
        sendEmailWithSubjectAndBodyReplacements( emailEntity, messageSubjectReplacements, messageBodyReplacements, false,
            false );
        LOG.debug( "Successfully sent ftp success mail" );
    }

}

// JIRA: SS-7: By RM02: EOC