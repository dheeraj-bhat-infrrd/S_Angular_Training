package com.realtech.socialsurvey.core.services.mail.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.EmailTemplateConstants;
import com.realtech.socialsurvey.core.dao.ForwardMailDetailsDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.SurveyDetailsDao;
import com.realtech.socialsurvey.core.entities.EmailEntity;
import com.realtech.socialsurvey.core.entities.FileContentReplacements;
import com.realtech.socialsurvey.core.entities.ForwardMailDetails;
import com.realtech.socialsurvey.core.entities.MailContent;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.generator.UrlService;
import com.realtech.socialsurvey.core.services.mail.EmailSender;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.mq.ProducerForQueue;
import com.realtech.socialsurvey.core.utils.EmailFormatHelper;


// JIRA: SS-7: By RM02: BOC
/**
 * Implementation file for the email services
 */

@Component
public class EmailServicesImpl implements EmailServices
{

    public static final Logger LOG = LoggerFactory.getLogger( EmailServicesImpl.class );

    @Autowired
    private EmailFormatHelper emailFormatHelper;

    @Autowired
    private ProducerForQueue queueProducer;

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private OrganizationUnitSettingsDao organizationUnitSettingsDao;

    @Autowired
    private SurveyDetailsDao surveyDetailsDao;
    @Value ( "${MAX_PAYMENT_RETRIES}")
    private int maxPaymentRetries;

    @Value ( "${SENDER_EMAIL_DOMAIN}")
    private String defaultEmailDomain;

    @Value ( "${APPLICATION_BASE_URL}")
    private String appBaseUrl;

    @Value ( "${APPLICATION_LOGO_URL}")
    private String appLogoUrl;

    @Value ( "${APPLICATION_ADMIN_EMAIL}")
    private String applicationAdminEmail;

    @Value ( "${APPLICATION_ADMIN_NAME}")
    private String applicationAdminName;

    @Autowired
    private UrlService urlService;

    @Autowired
    private ForwardMailDetailsDao forwardMailDetailsDao;


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
        LOG.info( "Method for sending registration invite mail called with url : " + url + " firstName :" + firstName
            + " and lastName : " + lastName );
        if ( url == null || url.isEmpty() ) {
            LOG.error( "Url is empty or null for sending registration invite mail " );
            throw new InvalidInputException( "Url is empty or null for sending registration invite mail " );
        }
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending registration invite mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending registration invite mail " );
        }
        if ( firstName == null || firstName.isEmpty() ) {
            LOG.error( "Firstname is empty or null for sending registration invite mail " );
            throw new InvalidInputException( "Firstname is empty or null for sending registration invite mail " );
        }

        LOG.info( "Initiating URL Service to shorten the url " + url );
        url = urlService.shortenUrl( url );
        LOG.info( "Finished calling URL Service to shorten the url.Shortened URL : " + url );

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.REGISTRATION_INVITATION_MAIL_SUBJECT;

        // Preparing full name of the recipient
        String fullName = firstName;
        if ( lastName != null && !lastName.isEmpty() ) {
            fullName = firstName + " " + lastName;
        }
        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.REGISTRATION_INVITATION_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, fullName, url, url, url, recipientMailId,
            appBaseUrl, appBaseUrl ) );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, true, false );
        LOG.info( "Successfully sent registration invite mail" );
    }


    @Async
    @Override
    public void sendAgentSurveyReminderMail( String recipientMailId, SurveyPreInitiation survey ) throws InvalidInputException,
        UndeliveredEmailException
    {

        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending registration invite mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending registration invite mail " );
        }

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.AGENT_SURVEY_REMINDER_EMAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.AGENT_SURVEY_REMINDER_EMAIL_BODY );
        String customerName = survey.getCustomerFirstName() + " " + survey.getCustomerLastName();
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, survey.getAgentName(), customerName,
            survey.getCustomerEmailId(), customerName ) );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.info( "Successfully sent registration invite mail" );
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
        LOG.info( "Method to send Email to reset the password link with URL : " + url + "\t and Recipients Mail ID : "
            + recipientMailId );
        if ( url == null || url.isEmpty() ) {
            LOG.error( "URL generated can not be null or empty" );
            throw new InvalidInputException( "URL generated can not be null or empty" );
        }
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipients Email Id can not be null or empty" );
            throw new InvalidInputException( "Recipients Email Id can not be null or empty" );
        }
        if ( name == null || name.isEmpty() ) {
            LOG.error( "Recipients Name can not be null or empty" );
            throw new InvalidInputException( "Recipients Name can not be null or empty" );
        }

        LOG.info( "Initiating URL Service to shorten the url " + url );
        url = urlService.shortenUrl( url );
        LOG.info( "Finished calling URL Service to shorten the url.Shortened URL : " + url );

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.RESET_PASSWORD_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.RESET_PASSWORD_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, name, loginName, url, url, url, appBaseUrl,
            appBaseUrl ) );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.info( "Successfully sent reset password mail" );
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
        LOG.info( "Method to send subscription charge unsuccessful mail to : " + name );
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending unsuccessful subscription charge mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending subscription charge mail " );
        }
        if ( name == null || name.isEmpty() ) {
            LOG.error( "Name is empty or null for sending subscription charge mail " );
            throw new InvalidInputException( "Name is empty or null for sending subscription charge mail " );
        }

        LOG.debug( "Executing sendSubscriptionChargeUnsuccessfulEmail() with parameters : " + recipientMailId + ", " + name
            + ", " + retryDays );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SUBSCRIPTION_UNSUCCESSFUL_MAIL_SUBJECT;

        /**
         * Sequence of the replacement arguments in the list should be same as
         * their sequence of occurrence in the template
         */
        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SUBSCRIPTION_UNSUCCESSFUL_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, name, retryDays, recipientMailId, appBaseUrl,
            appBaseUrl ) );

        LOG.info( "Sending the mail." );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.info( "Mail successfully sent!" );
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
        LOG.info( "Method to send verification mail called for url : " + url + " recipientMailId : " + recipientMailId );
        if ( url == null || url.isEmpty() ) {
            throw new InvalidInputException( "URL generated can not be null or empty" );
        }
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            throw new InvalidInputException( "Recipients Email Id can not be null or empty" );
        }
        if ( recipientName == null || recipientName.isEmpty() ) {
            throw new InvalidInputException( "Recipients Name can not be null or empty" );
        }

        LOG.info( "Initiating URL Service to shorten the url " + url );
        url = urlService.shortenUrl( url );
        LOG.info( "Finished calling URL Service to shorten the url.Shortened URL : " + url );

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.EMAIL_VERIFICATION_MAIL_SUBJECT;

        FileContentReplacements fileContentReplacements = new FileContentReplacements();
        fileContentReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.EMAIL_VERIFICATION_MAIL_BODY );
        fileContentReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, recipientName, url, url, url, appBaseUrl,
            appBaseUrl ) );

        LOG.debug( "Calling email sender to send verification mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, fileContentReplacements, false, false );
        LOG.info( "Successfully sent verification mail" );
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
        String loginName ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.info( "Method to send verification mail called for url: " + url + " recipientMailId: " + recipientMailId );
        if ( url == null || url.isEmpty() ) {
            throw new InvalidInputException( "URL generated can not be null or empty" );
        }
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            throw new InvalidInputException( "Recipients Email Id can not be null or empty" );
        }
        if ( recipientName == null || recipientName.isEmpty() ) {
            throw new InvalidInputException( "Recipients Name can not be null or empty" );
        }

        LOG.info( "Initiating URL Service to shorten the url " + url );
        url = urlService.shortenUrl( url );
        LOG.info( "Finished calling URL Service to shorten the url.Shortened URL : " + url );

        // Fetching mail body
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.VERIFICATION_MAIL_SUBJECT;

        // File content replacements in same order
        FileContentReplacements fileContentReplacements = new FileContentReplacements();
        fileContentReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.VERIFICATION_MAIL_BODY );
        fileContentReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, recipientName, recipientName, url, url, url,
            appBaseUrl, profileName, appBaseUrl, profileName, loginName, appBaseUrl, appBaseUrl ) );

        // sending email
        LOG.debug( "Calling email sender to send verification mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, fileContentReplacements, false, false );
        LOG.info( "Successfully sent verification mail" );
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
        String loginName, boolean holdSendingMail ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.info( "Method to send Email to complete registration link with URL: " + url + "\t and Recipients Mail ID: "
            + recipientMailId );
        if ( url == null || url.isEmpty() ) {
            LOG.error( "URL generated can not be null or empty" );
            throw new InvalidInputException( "URL generated can not be null or empty" );
        }
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipients Email Id can not be null or empty" );
            throw new InvalidInputException( "Recipients Email Id can not be null or empty" );
        }
        if ( name == null || name.isEmpty() ) {
            LOG.error( "Recipients Name can not be null or empty" );
            throw new InvalidInputException( "Recipients Name can not be null or empty" );
        }

        LOG.info( "Initiating URL Service to shorten the url " + url );
        url = urlService.shortenUrl( url );
        LOG.info( "Finished calling URL Service to shorten the url.Shortened URL : " + url );

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.COMPLETE_REGISTRATION_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.COMPLETE_REGISTRATION_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, name, url, url, url, appBaseUrl, profileName,
            appBaseUrl, profileName, loginName, appBaseUrl, appBaseUrl ) );
        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, holdSendingMail );
        LOG.info( "Successfully sent mail for registraion completion." );
    }


    // JIRA SS-42 by RM-05 : EOC

    @Async
    @Override
    public void sendFatalExceptionEmail( String recipientMailId, String stackTrace ) throws InvalidInputException,
        UndeliveredEmailException
    {
        LOG.info( "Sending FatalException email to the admin." );
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending fatal exception mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending fatal exception mail " );
        }

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.FATAL_EXCEPTION_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.FATAL_EXCEPTION_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, stackTrace ) );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.info( "Successfully sent fatal exception mail" );
    }


    @Async
    @Override
    public void sendEmailSendingFailureMail( String recipientMailId, String destinationMailId, String displayName,
        String stackTrace ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.info( "Sending email to the admin on failure of sending mail to customer" );
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sendEmailSendingFailureMail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sendEmailSendingFailureMail " );
        }
        if ( destinationMailId == null || destinationMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sendEmailSendingFailureMail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sendEmailSendingFailureMail " );
        }
        if ( displayName == null || displayName.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sendEmailSendingFailureMail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sendEmailSendingFailureMail " );
        }
        if ( stackTrace == null || stackTrace.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sendEmailSendingFailureMail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sendEmailSendingFailureMail " );
        }

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.EMAIL_SENDING_FAILURE_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.EMAIL_SENDING_FAILURE_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, displayName, destinationMailId, stackTrace ) );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.info( "Successfully sent EmailSendingFailureMail" );
    }


    @Async
    @Override
    public void sendRetryChargeEmail( String recipientMailId, String displayName, String loginName )
        throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending retry charge mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending retry charge mail " );
        }
        if ( displayName == null || displayName.isEmpty() ) {
            LOG.error( "displayName parameter is empty or null for sending retry charge mail " );
            throw new InvalidInputException( "displayName parameter is empty or null for sending retry charge mail " );
        }

        LOG.info( "Sending retry charge email to : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.RETRY_CHARGE_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.RETRY_CHARGE_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, displayName, recipientMailId, appBaseUrl,
            appBaseUrl ) );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.info( "Successfully sent retry charge mail" );
    }


    @Async
    @Override
    public void sendRetryExhaustedEmail( String recipientMailId, String displayName, String loginName )
        throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending retries exhausted mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending retries exhausted mail " );
        }
        if ( displayName == null || displayName.isEmpty() ) {
            LOG.error( "displayName parameter is empty or null for sending retry exhausted mail " );
            throw new InvalidInputException( "displayName parameter is empty or null for sending retry exhausted mail " );
        }

        LOG.info( "Sending retries exhausted email to : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.RETRIES_EXHAUSTED_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.RETRIES_EXHAUSTED_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, displayName, recipientMailId, appBaseUrl,
            appBaseUrl ) );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.info( "Successfully sent retries exhausted mail" );
    }


    @Async
    @Override
    public void sendAccountDisabledMail( String recipientMailId, String displayName, String loginName )
        throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending retries exhausted mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending retries exhausted mail " );
        }
        if ( displayName == null || displayName.isEmpty() ) {
            LOG.error( "displayName parameter is empty or null for sending retry exhausted mail " );
            throw new InvalidInputException( "displayName parameter is empty or null for sending retry exhausted mail " );
        }

        LOG.info( "Sending account disabled email to : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.ACCOUNT_DISABLED_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.ACCOUNT_DISABLED_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, displayName, recipientMailId, appBaseUrl,
            appBaseUrl ) );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.info( "Successfully sent account disabled mail" );
    }


    @Async
    @Override
    public void sendAccountDeletionMail( String recipientMailId, String displayName, String loginName )
        throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending account deletion mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending account deletion mail " );
        }
        if ( displayName == null || displayName.isEmpty() ) {
            LOG.error( "displayName parameter is empty or null for sending account deletion mail " );
            throw new InvalidInputException( "displayName parameter is empty or null for sending account deletion mail " );
        }

        LOG.info( "Sending account deletion email to : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.ACCOUNT_DELETED_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.ACCOUNT_DELETED_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, displayName, recipientMailId ) );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.info( "Successfully sent account disabled mail" );
    }


    @Async
    @Override
    public void sendAccountUpgradeMail( String recipientMailId, String displayName, String loginName )
        throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending account upgrade mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending account upgrade mail " );
        }
        if ( displayName == null || displayName.isEmpty() ) {
            LOG.error( "displayName parameter is empty or null for sending account upgrade mail " );
            throw new InvalidInputException( "displayName parameter is empty or null for sending account upgrade mail " );
        }

        LOG.info( "Sending account upgrade email to : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.ACCOUNT_UPGRADE_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.ACCOUNT_UPGRADE_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, displayName, recipientMailId, appBaseUrl,
            appBaseUrl ) );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.info( "Successfully sent account upgrade mail" );
    }


    @Async
    @Override
    public void sendDefaultSurveyCompletionMail( String recipientMailId, String displayName, String agentName,
        String agentEmail, String agentProfileName, String logoUrl, long agentId ) throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending survey completion mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending survey completion mail " );
        }
        if ( displayName == null || displayName.isEmpty() ) {
            LOG.error( "displayName parameter is empty or null for sending account upgrade mail " );
            throw new InvalidInputException( "displayName parameter is empty or null for sending survey completion mail " );
        }

        LOG.info( "Sending survey completion email to : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId, agentId, agentName );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SURVEY_COMPLETION_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SURVEY_COMPLETION_MAIL_BODY );
        if ( logoUrl == null || logoUrl.equalsIgnoreCase( "" ) ) {
            messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, displayName, agentName, agentName,
                appBaseUrl, agentProfileName, appBaseUrl, agentProfileName, agentName ) );
        } else {
            messageBodyReplacements.setReplacementArgs( Arrays.asList( logoUrl, displayName, agentName, agentName, appBaseUrl,
                agentProfileName, appBaseUrl, agentProfileName, agentName ) );
        }


        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.info( "Successfully sent survey completion mail" );
    }


    @Async
    @Override
    public void sendDefaultSurveyCompletionUnpleasantMail( String recipientMailId, String displayName, String agentName,
        String agentEmail, String companyName, String logoUrl, long agentId ) throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending survey completion unpleasant mail " );
            throw new InvalidInputException(
                "Recipient email Id is empty or null for sending survey completion unpleasant mail " );
        }
        if ( displayName == null || displayName.isEmpty() ) {
            LOG.error( "displayName parameter is empty or null for sending account upgrade mail " );
            throw new InvalidInputException(
                "displayName parameter is empty or null for sending survey completion unpleasant mail " );
        }

        LOG.info( "Sending survey completion email to : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId, agentId, agentName );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SURVEY_COMPLETION_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SURVEY_COMPLETION_UNPLEASANT_MAIL_BODY );
        if ( logoUrl == null || logoUrl.equalsIgnoreCase( "" ) ) {
            messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, displayName, agentName, companyName ) );
        } else {
            messageBodyReplacements
                .setReplacementArgs( Arrays.asList( logoUrl, displayName, agentName, agentName, companyName ) );
        }


        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.info( "Successfully sent survey completion mail" );
    }

    
    @Async
    @Override
    public void sendDefaultSurveyReminderMail( String recipientMailId, String logoUrl, String firstName, String agentName,
        String agentEmailId, String link, String agentPhone, String agentTitle, String companyName )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.info( "Executing the sendDefaultSurveyReminderMail() method" );

        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending survey completion mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending survey completion mail " );
        }
        if ( firstName == null || firstName.isEmpty() ) {
            LOG.error( "firstName parameter is empty or null for sending account upgrade mail " );
            throw new InvalidInputException( "firstName parameter is empty or null for sending survey completion mail " );
        }

        LOG.info( "Initiating URL Service to shorten the url " + link );
        link = urlService.shortenUrl( link );
        LOG.info( "Finished calling URL Service to shorten the url.Shortened URL : " + link );

        LOG.info( "Sending survey reminder email to : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );

        String agentSignature = emailFormatHelper.buildAgentSignature( agentName, agentPhone, agentTitle, companyName );

        FileContentReplacements subjectReplacements = new FileContentReplacements();
        subjectReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SURVEY_REMINDER_MAIL_SUBJECT );
        subjectReplacements.setReplacementArgs( Arrays.asList( agentName ) );

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SURVEY_REMINDER_MAIL_BODY );
        String currentYear = String.valueOf( Calendar.getInstance().get( Calendar.YEAR ) );
        DateFormat dateFormat = new SimpleDateFormat( "yyyy/MM/dd" );
        String fullAddress = "";

        if ( logoUrl == null || logoUrl.equalsIgnoreCase( "" ) ) {
            messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, firstName, link, link, link, "",
                agentSignature, appBaseUrl, appBaseUrl, recipientMailId, companyName, dateFormat.format( new Date() ),
                agentEmailId, companyName, currentYear, fullAddress ) );
        } else {
            messageBodyReplacements.setReplacementArgs( Arrays.asList( logoUrl, firstName, link, link, link, "",
                agentSignature, appBaseUrl, appBaseUrl, recipientMailId, companyName, dateFormat.format( new Date() ),
                agentEmailId, companyName, currentYear, fullAddress ) );
        }

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithSubjectAndBodyReplacements( emailEntity, subjectReplacements, messageBodyReplacements, false, false );
        LOG.info( "Successfully sent survey completion mail" );
    }


    @Async
    @Override
    public void sendSurveyReminderMail( String recipientMailId, String subject, String mailBody, String senderName,
        String senderEmailAddress ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.info( "Executing the sendSurveyReminderMail() method" );

        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending survey completion mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending survey completion mail " );
        }
        if ( subject == null || subject.isEmpty() ) {
            LOG.error( "subject parameter is empty or null for sending social post reminder mail " );
            throw new InvalidInputException( "displayName parameter is empty or null for sending survey completion mail " );
        }

        LOG.info( "Sending survey reminder email to : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        emailEntity.setSenderName( senderName );
        emailEntity.setSenderEmailId( senderEmailAddress );
        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmail( emailEntity, subject, mailBody, false, false );
        LOG.info( "Successfully sent survey completion mail" );
    }


    @Async
    @Override
    public void sendSurveyCompletionMailToAdminsAndAgent( String recipientName, String recipientMailId, String surveyDetail,
        String customerName, String rating , String logoUrl ) throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending survey completion mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending survey completion mail " );
        }
        if ( surveyDetail == null || surveyDetail.isEmpty() ) {
            LOG.error( "syrveyDetail parameter is empty or null for sending account upgrade mail " );
            throw new InvalidInputException( "surveyDetail parameter is empty or null for sending survey completion mail " );
        }

        LOG.info( "Sending survey completion email to : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );

        FileContentReplacements subjectReplacements = new FileContentReplacements();
        subjectReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SURVEY_COMPLETION_ADMINS_MAIL_SUBJECT );
        subjectReplacements.setReplacementArgs( Arrays.asList( rating, customerName ) );

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SURVEY_COMPLETION_ADMINS_MAIL_BODY );
        
        if ( logoUrl == null || logoUrl.isEmpty() ){
            messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, recipientName, recipientMailId, surveyDetail ) );
        }else{
            messageBodyReplacements.setReplacementArgs( Arrays.asList( logoUrl, recipientName, recipientMailId, surveyDetail ) );
        }
        

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithSubjectAndBodyReplacements( emailEntity, subjectReplacements, messageBodyReplacements, false, false );
        LOG.info( "Successfully sent survey completion mail" );
    }


    @Async
    @Override
    public void sendDefaultSocialPostReminderMail( String recipientMailId, String agentPhone, String agentTitle,
        String companyName, String displayName, String agentName, String links, String logoUrl ) throws InvalidInputException,
        UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending survey completion mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending survey completion mail " );
        }
        if ( displayName == null || displayName.isEmpty() ) {
            LOG.error( "displayName parameter is empty or null for sending account upgrade mail " );
            throw new InvalidInputException( "displayName parameter is empty or null for sending survey completion mail " );
        }

        LOG.info( "Sending survey reminder email to : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SOCIALPOST_REMINDER_MAIL_SUBJECT;

        String agentSignature = emailFormatHelper.buildAgentSignature( agentName, agentPhone, agentTitle, companyName );

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SOCIALPOST_REMINDER_MAIL_BODY );
        if ( logoUrl == null || logoUrl.equalsIgnoreCase( "" ) ) {
            messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, displayName, links, "", agentSignature ) );
        } else {
            messageBodyReplacements.setReplacementArgs( Arrays.asList( logoUrl, displayName, links, "", agentSignature ) );
        }

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.info( "Successfully sent survey completion mail" );
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
    public void sendContactUsMail( String recipientEmailId, String displayName, String senderName, String senderEmailId,
        String message ) throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientEmailId == null || recipientEmailId.isEmpty() ) {
            LOG.error( "Recipient email id is null or empty!" );
            throw new InvalidInputException( "Recipient email id is null or empty!" );
        }
        if ( displayName == null || displayName.isEmpty() ) {
            LOG.error( "displayName is null or empty!" );
            throw new InvalidInputException( "displayName is null or empty!" );
        }
        if ( senderName == null || senderName.isEmpty() ) {
            LOG.error( "senderName is null or empty!" );
            throw new InvalidInputException( "senderName is null or empty!" );
        }
        if ( senderEmailId == null || senderEmailId.isEmpty() ) {
            LOG.error( "senderEmailId is null or empty!" );
            throw new InvalidInputException( "senderEmailId is null or empty!" );
        }
        if ( message == null || message.isEmpty() ) {
            LOG.error( "message is null or empty!" );
            throw new InvalidInputException( "message is null or empty!" );
        }

        LOG.info( "Sending contact us email to : " + recipientEmailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientEmailId );

        FileContentReplacements subjectReplacements = new FileContentReplacements();
        subjectReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.CONTACT_US_MAIL_SUBJECT );
        subjectReplacements.setReplacementArgs( Arrays.asList( senderName ) );

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.CONTACT_US_MAIL_BODY );
        messageBodyReplacements
            .setReplacementArgs( Arrays.asList( appLogoUrl, displayName, senderName, senderEmailId, message ) );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithSubjectAndBodyReplacements( emailEntity, subjectReplacements, messageBodyReplacements, false, false );
        LOG.info( "Successfully sent contact us mail" );
    }


    @Async
    @Override
    public void sendDefaultSurveyInvitationMail( String recipientMailId, String logoUrl, String firstName, String agentName,
        String link, String agentEmailId, String agentSignature, String companyName, String surveyInitiatedOn,
        String currentYear, String fullAddress, long agentId ) throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending survey completion mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending survey completion mail " );
        }
        if ( firstName == null || firstName.isEmpty() ) {
            LOG.error( "firstName parameter is empty or null for sending account upgrade mail " );
            throw new InvalidInputException( "firstName parameter is empty or null for sending survey completion mail " );
        }

        LOG.info( "Initiating URL Service to shorten the url " + link );
        link = urlService.shortenUrl( link );
        LOG.info( "Finished calling URL Service to shorten the url.Shortened URL : " + link );

        LOG.info( "Sending survey reminder email to : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId, agentId, agentName );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SURVEY_INVITATION_MAIL_SUBJECT;
        
        FileContentReplacements messageSubjectReplacements = new FileContentReplacements();
        messageSubjectReplacements.setFileName( subjectFileName );
        messageSubjectReplacements.setReplacementArgs( Arrays.asList( agentName) );

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SURVEY_INVITATION_MAIL_BODY );

        if ( logoUrl == null || logoUrl.equalsIgnoreCase( "" ) ) {
            messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, firstName, link, link, link, "",
                agentSignature, recipientMailId, companyName, surveyInitiatedOn, agentEmailId, companyName, currentYear,
                fullAddress ) );
        } else {
            messageBodyReplacements.setReplacementArgs( Arrays.asList( logoUrl, firstName, link, link, link, "",
                agentSignature, recipientMailId, companyName, surveyInitiatedOn, agentEmailId, companyName, currentYear,
                fullAddress ) );
        }


        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithSubjectAndBodyReplacements( emailEntity, messageSubjectReplacements, messageBodyReplacements, false, false );
        LOG.info( "Successfully sent survey invitation mail" );
    }


    @Async
    @Override
    public void sendSurveyInvitationMail( String recipientMailId, String subject, String mailBody, String emailId, String name, long agentId )
        throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending survey completion mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending survey completion mail " );
        }
        if ( subject == null || subject.isEmpty() ) {
            LOG.error( "subject parameter is empty or null for sending social post reminder mail " );
            throw new InvalidInputException( "displayName parameter is empty or null for sending survey completion mail " );
        }

        LOG.info( "Sending survey reminder email to : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId, agentId, name );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmail( emailEntity, subject, mailBody, false, false );
        LOG.info( "Successfully sent survey completion mail" );
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
            LOG.error( "Recipient email Id is empty or null for sending survey completion mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending survey completion mail " );
        }
        if ( displayName == null || displayName.isEmpty() ) {
            LOG.error( "displayName parameter is empty or null for sending account upgrade mail " );
            throw new InvalidInputException( "displayName parameter is empty or null for sending survey completion mail " );
        }

        LOG.info( "Sending account blocking email to : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.ACCOUNT_BLOCKING_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.ACCOUNT_BLOCKING_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, displayName, recipientMailId, appBaseUrl,
            appBaseUrl ) );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.info( "Successfully sent account blocking mail" );
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
            LOG.error( "Recipient email Id is empty or null for sending survey completion mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending survey completion mail " );
        }
        if ( displayName == null || displayName.isEmpty() ) {
            LOG.error( "displayName parameter is empty or null for sending account upgrade mail " );
            throw new InvalidInputException( "displayName parameter is empty or null for sending survey completion mail " );
        }

        LOG.info( "Sending account reactivation email to : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.ACCOUNT_REACTIVATION_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.ACCOUNT_REACTIVATION_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, displayName, recipientMailId, appBaseUrl,
            appBaseUrl ) );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.info( "Successfully sent account blocking mail" );
    }


    @Async
    @Override
    public void sendSubscriptionRevisionMail( String recipientMailId, String name, String oldAmount, String revisedAmount,
        String numOfUsers ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.info( "Sending subscription revision mail to " + recipientMailId + " with name " + name );
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Email id is not sent" );
            throw new InvalidInputException( "Email id is not sent" );
        }
        if ( name == null || name.isEmpty() ) {
            LOG.error( "Name is not sent" );
            throw new InvalidInputException( "Name is not sent" );
        }
        if ( oldAmount == null || oldAmount.isEmpty() ) {
            LOG.error( "oldAmount is not sent" );
            throw new InvalidInputException( "oldAmount is not sent" );
        }
        if ( revisedAmount == null || revisedAmount.isEmpty() ) {
            LOG.error( "revisedAmount is not sent" );
            throw new InvalidInputException( "revisedAmount is not sent" );
        }
        if ( numOfUsers == null || numOfUsers.isEmpty() ) {
            LOG.error( "numOfUsers is not sent" );
            throw new InvalidInputException( "numOfUsers is not sent" );
        }

        LOG.info( "Sending subscription revision email to : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SUBSCRIPTION_PRICE_UPDATED_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SUBSCRIPTION_PRICE_UPDATED_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, name, numOfUsers, oldAmount, revisedAmount,
            recipientMailId, appBaseUrl, appBaseUrl ) );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.info( "Successfully sent subscription revised mail" );
    }


    @Async
    @Override
    public void sendManualRegistrationLink( String recipientId, String firstName, String lastName, String link )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.info( "Sending manual registration link to " + recipientId + " and name " + firstName );
        if ( recipientId == null || recipientId.isEmpty() ) {
            LOG.error( "Recipient id is not present" );
            throw new InvalidInputException( "Recipient id is not present" );
        }
        if ( firstName == null || firstName.isEmpty() ) {
            LOG.error( "firstName is not present" );
            throw new InvalidInputException( "firstName id is not present" );
        }
        if ( link == null || link.isEmpty() ) {
            LOG.error( "link is not present" );
            throw new InvalidInputException( "link id is not present" );
        }

        LOG.info( "Initiating URL Service to shorten the url " + link );
        link = urlService.shortenUrl( link );
        LOG.info( "Finished calling URL Service to shorten the url.Shortened URL : " + link );

        LOG.info( "Sending manual registration email to : " + recipientId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientId );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.MANUAL_REGISTRATION_MAIL_SUBJECT;

        // Preparing full name of the recipient
        String fullName = firstName;
        if ( lastName != null && !lastName.isEmpty() ) {
            fullName = firstName + " " + lastName;
        }
        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.MANUAL_REGISTRATION_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, fullName, fullName, link, link, link ) );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.info( "Successfully sent manual registration mail" );
    }


    @Async
    @Override
    public void sendDefaultSurveyInvitationMailByCustomer( String recipientMailId, String firstName, String agentName,
        String link, String agentEmailId, long agentId ) throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending survey completion mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending survey completion mail " );
        }
        if ( firstName == null || firstName.isEmpty() ) {
            LOG.error( "displayName parameter is empty or null for sending account upgrade mail " );
            throw new InvalidInputException( "displayName parameter is empty or null for sending survey completion mail " );
        }

        LOG.info( "Initiating URL Service to shorten the url " + link );
        link = urlService.shortenUrl( link );
        LOG.info( "Finished calling URL Service to shorten the url.Shortened URL : " + link );

        LOG.info( "Sending survey reminder email to : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId, agentId, agentName );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SURVEY_INVITATION_MAIL_CUSTOMER_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SURVEY_INVITATION_MAIL_CUSTOMER_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, firstName, agentName, link, link, link,
            appBaseUrl, appBaseUrl ) );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.info( "Successfully sent survey invitation mail" );
    }


    @Async
    @Override
    public void sendSurveyInvitationMailByCustomer( String recipientMailId, String subject, String mailBody, String emailId,
        String name, long agentId ) throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending survey completion mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending survey completion mail " );
        }
        if ( subject == null || subject.isEmpty() ) {
            LOG.error( "subject parameter is empty or null for sending social post reminder mail " );
            throw new InvalidInputException( "displayName parameter is empty or null for sending survey completion mail " );
        }

        LOG.info( "Sending survey invitation email to : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId, agentId, name );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmail( emailEntity, subject, mailBody, false, false );
        LOG.info( "Successfully sent survey invitation mail" );
    }


    @Async
    @Override
    public void sendDefaultSurveyRestartMail( String recipientMailId, String logoUrl, String displayName, String agentName,
        String link, String agentEmailId, String agentSignature, long agentId ) throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending survey restart mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending survey restart mail " );
        }
        if ( displayName == null || displayName.isEmpty() ) {
            LOG.error( "displayName parameter is empty or null for sending survey restart mail " );
            throw new InvalidInputException( "displayName parameter is empty or null for sending survey restart mail " );
        }

        LOG.info( "Initiating URL Service to shorten the url " + link );
        link = urlService.shortenUrl( link );
        LOG.info( "Finished calling URL Service to shorten the url.Shortened URL : " + link );

        LOG.info( "Sending survey restart email to : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId, agentId, agentName );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SURVEY_RESTART_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SURVEY_RESTART_MAIL_BODY );

        if ( logoUrl == null || logoUrl.equalsIgnoreCase( "" ) ) {
            messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, displayName, link, link, link, "",
                agentSignature ) );
        } else {
            messageBodyReplacements.setReplacementArgs( Arrays.asList( logoUrl, displayName, link, link, link, "",
                agentSignature ) );
        }

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.info( "Successfully sent survey invitation mail" );
    }


    @Async
    @Override
    public void sendSocialConnectMail( String recipientMailId, String displayName, String loginName, String account )
        throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending survey completion mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending survey completion mail " );
        }
        if ( displayName == null || displayName.isEmpty() ) {
            LOG.error( "displayName parameter is empty or null for sending account upgrade mail " );
            throw new InvalidInputException( "displayName parameter is empty or null for sending survey completion mail " );
        }

        LOG.info( "Sending Social Connect email to : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SOCIAL_CONNECT_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SOCIAL_CONNECT_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, displayName, account, recipientMailId,
            appBaseUrl, appBaseUrl ) );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.info( "Successfully sent social connect mail" );
    }


    @Async
    @Override
    public void sendReportAbuseMail( String recipientMailId, String displayName, String agentName, String customerName,
        String customerEmail, String review, String reason, String reporterName, String reporterEmail )
        throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending survey completion mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending report abuse mail " );
        }
        if ( displayName == null || displayName.isEmpty() ) {
            LOG.error( "displayName parameter is empty or null for sending account upgrade mail " );
            throw new InvalidInputException( "displayName parameter is empty or null for sending report abuse mail " );
        }

        LOG.info( "Sending report abuse email to : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.REPORT_ABUSE_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();

        if ( reporterName != null && reporterEmail != null ) {
            messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
                + EmailTemplateConstants.REPORT_ABUSE_MAIL_WITH_REVIEWER_BODY );
            messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, displayName, agentName, customerName,
                customerEmail, review, reporterName, reporterEmail, reason, appBaseUrl ) );
        } else {
            messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
                + EmailTemplateConstants.REPORT_ABUSE_MAIL_BODY );
            messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, displayName, agentName, customerName,
                customerEmail, review, reason, appBaseUrl ) );
        }

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.info( "Successfully sent social connect mail" );
    }


    @Async
    @Override
    public void sendSurveyReportMail( String recipientMailId, String displayName, String reason ) throws InvalidInputException,
        UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending survey completion mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending survey completion mail " );
        }
        if ( displayName == null || displayName.isEmpty() ) {
            LOG.error( "displayName parameter is empty or null for sending account upgrade mail " );
            throw new InvalidInputException( "displayName parameter is empty or null for sending survey completion mail " );
        }

        LOG.info( "Sending survey completion email to : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SURVEY_REPORT_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SURVEY_REPORT_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, displayName, reason ) );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.info( "Successfully sent survey completion mail" );
    }


    @Override
    public void sendCorruptDataFromCrmNotificationMail( String firstName, String lastName, String recipientMailId,
        Map<String, String> attachmentsDetails ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.info( "Method sendCorruptDataFromCrmNotificationMail() started." );
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending corrupt CRM data notification mail " );
            throw new InvalidInputException(
                "Recipient email Id is empty or null for sending corrupt CRM data notification mail " );
        }

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        emailEntity.setAttachmentDetail( attachmentsDetails );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.CORRUPT_PREINITIATION_RECORD_MAIL_SUBJECT;
        String displayName = firstName + " " + lastName;
        displayName.replaceAll( "null", "" );
        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.CORRUPT_PREINITIATION_RECORD_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, displayName ) );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );

        LOG.info( "Method sendCorruptDataFromCrmNotificationMail() finished." );
    }
    
    
    @Override
    public void sendInvalidEmailsNotificationMail( String firstName, String lastName, String recipientMailId,
        Map<String, String> attachmentsDetails ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.info( "Method sendInvalidEmailsNotificationMail() started." );
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending corrupt CRM data notification mail " );
            throw new InvalidInputException(
                "Recipient email Id is empty or null for sending corrupt CRM data notification mail " );
        }

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        emailEntity.setAttachmentDetail( attachmentsDetails );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.INVALID_EMAILS_MAIL_SUBJECT;
        String displayName = firstName + " " + lastName;
        displayName.replaceAll( "null", "" );
        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.INVALID_EMAILS_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, displayName ) );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );

        LOG.info( "Method sendCorruptDataFromCrmNotificationMail() finished." );
    }


    @Override
    public void sendRecordsNotUploadedCrmNotificationMail( String firstName, String lastName, String recipientMailId,
        Map<String, String> attachmentsDetails ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.info( "Method sendCorruptDataFromCrmNotificationMail() started." );
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending corrupt CRM data notification mail " );
            throw new InvalidInputException(
                "Recipient email Id is empty or null for sending corrupt CRM data notification mail " );
        }

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        emailEntity.setAttachmentDetail( attachmentsDetails );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.RECORDS_NOT_UPLOADED_MAIL_SUBJECT;
        String displayName = firstName + " " + lastName;
        displayName.replaceAll( "null", "" );
        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.RECORDS_NOT_UPLOADED_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, displayName ) );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );

        LOG.info( "Method sendCorruptDataFromCrmNotificationMail() finished." );
    }


    /**
     * 
     */
    @Override
    public void sendHelpMailToAdmin( String senderEmail, String senderName, String displayName, String mailSubject,
        String messageBodyText, String recipientMailId, Map<String, String> attachmentsDetails ) throws InvalidInputException,
        UndeliveredEmailException
    {
        LOG.info( "Method sendHelpMailToAdmin() started." );
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending sending report bug  mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending report bug  mail " );
        }

        LOG.info( "Saving EmailEntity with recipient mail id : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        //set the attachments detail
        emailEntity.setAttachmentDetail( attachmentsDetails );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.HELP_MAIL_TO_SS_ADMIN_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.HELP_MAIL_TO_SS_ADMIN_BODY );

        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, displayName, senderName, senderEmail,
            mailSubject, messageBodyText ) );

        LOG.info( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );

        LOG.info( "Method sendHelpMailToAdmin() finished." );
    }


    @Override
    public void sendZillowCallExceededMailToAdmin( int count ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.info( "Method sendZillowCallExceededMailToAdmin() started" );
        LOG.info( "Saving EmailEntity with recipient mail id : " + applicationAdminEmail );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( applicationAdminEmail );
        String subject = "Zillow API call exceeded for the day";
        String mailBody = "Zillow API call exceeded for the day. Call count : " + count;
        LOG.info( "Calling email sender to send mail" );
        emailSender.sendEmail( emailEntity, subject, mailBody, false, false );
        LOG.info( "Method sendZillowCallExceededMailToAdmin() finished" );
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
        LOG.debug( "Preparing email entity for registration invitation for recipientMailId " + recipients );
        
        EmailEntity emailEntity = new EmailEntity();
        emailEntity.setRecipients( recipients );
        emailEntity.setRecipientType( EmailEntity.RECIPIENT_TYPE_TO );

        LOG.debug( "Prepared email entity for registrationInvite" );
        return emailEntity;
    }


    // creating email entity with senders email id as U<userid>@socialsurvey.me
    private EmailEntity prepareEmailEntityForSendingEmail(String recipientMailId, long userId, String name){
    	LOG.debug("Preparing email entity with recipent "+recipientMailId+" user id "+userId+" and name "+name);
    	List<String> recipients = new ArrayList<String>();
        recipients.add( recipientMailId );

        EmailEntity emailEntity = new EmailEntity();
        emailEntity.setRecipients( recipients );
        emailEntity.setSenderName( name );
        emailEntity.setSenderEmailId( "u" + userId + "@" + defaultEmailDomain );
        emailEntity.setRecipientType( EmailEntity.RECIPIENT_TYPE_TO );

        LOG.debug( "Prepared email entity for sending mail" );
        return emailEntity;
    }

    @Override
    public void sendManualSurveyReminderMail( OrganizationUnitSettings companySettings, User user, String agentName,
        String agentEmailId, String agentPhone, String agentTitle, String companyName, SurveyPreInitiation survey,
        String surveyLink, String logoUrl ) throws InvalidInputException
    {

        LOG.info( "Sending manual survey reminder mail." );
        if ( companySettings != null && companySettings.getMail_content() != null
            && companySettings.getMail_content().getTake_survey_reminder_mail() != null ) {

            MailContent mailContent = companySettings.getMail_content().getTake_survey_reminder_mail();

            String mailBody = emailFormatHelper.replaceEmailBodyWithParams( mailContent.getMail_body(),
                mailContent.getParam_order() );
            String agentSignature = emailFormatHelper.buildAgentSignature( agentName, agentPhone, agentTitle, companyName );
            DateFormat dateFormat = new SimpleDateFormat( "yyyy/MM/dd" );
            String currentYear = String.valueOf( Calendar.getInstance().get( Calendar.YEAR ) );
            String fullAddress = "";

            if ( logoUrl == null || logoUrl.equalsIgnoreCase( "" ) ) {
                mailBody = mailBody.replaceAll( "\\[LogoUrl\\]", appLogoUrl );
            } else {
                mailBody = mailBody.replaceAll( "\\[LogoUrl\\]", logoUrl );
            }
            mailBody = mailBody.replaceAll( "\\[BaseUrl\\]", appBaseUrl );
            mailBody = mailBody.replaceAll( "\\[AgentName\\]", "" );
            mailBody = mailBody.replaceAll( "\\[FirstName\\]", survey.getCustomerFirstName() );
            mailBody = mailBody
                .replaceAll(
                    "\\[Name\\]",
                    emailFormatHelper.getCustomerDisplayNameForEmail( survey.getCustomerFirstName(),
                        survey.getCustomerLastName() ) );
            LOG.info( "Initiating URL Service to shorten the url " + surveyLink );
            try {
                surveyLink = urlService.shortenUrl( surveyLink );
            } catch ( InvalidInputException e ) {
                LOG.error( "InvalidInput Exception while url shortening url. Reason : ", e );
            }
            LOG.info( "Finished calling URL Service to shorten the url.Shortened URL : " + surveyLink );
            mailBody = mailBody.replaceAll( "\\[Link\\]", surveyLink );
            mailBody = mailBody.replaceAll( "\\[AgentSignature\\]", agentSignature );
            mailBody = mailBody.replaceAll( "\\[RecipientEmail\\]", survey.getCustomerEmailId() );
            mailBody = mailBody.replaceAll( "\\[SenderEmail\\]", user.getEmailId() );
            mailBody = mailBody.replaceAll( "\\[CompanyName\\]", companyName );
            mailBody = mailBody.replaceAll( "\\[InitiatedDate\\]", dateFormat.format( new Date() ) );
            mailBody = mailBody.replaceAll( "\\[CurrentYear\\]", currentYear );
            mailBody = mailBody.replaceAll( "\\[FullAddress\\]", fullAddress );
            mailBody = mailBody.replaceAll( "null", "" );
            String mailSubject = CommonConstants.REMINDER_MAIL_SUBJECT + agentName;
            if ( mailContent.getMail_subject() != null && !mailContent.getMail_subject().isEmpty() ) {
                mailSubject = mailContent.getMail_subject();
                mailSubject = mailSubject.replaceAll( "\\[AgentName\\]", agentName );
            }

            try {
                sendSurveyReminderMail( survey.getCustomerEmailId(), mailSubject, mailBody, agentName, user.getEmailId() );
            } catch ( InvalidInputException | UndeliveredEmailException e ) {
                LOG.error( "Exception caught while sending mail to " + survey.getCustomerEmailId() + " .Nested exception is ",
                    e );
            }
        } else {
            try {
                sendDefaultSurveyReminderMail(
                    survey.getCustomerEmailId(),
                    logoUrl,
                    emailFormatHelper.getCustomerDisplayNameForEmail( survey.getCustomerFirstName(),
                        survey.getCustomerLastName() ), agentName, agentEmailId, surveyLink, agentPhone, agentTitle,
                    companyName );
            } catch ( InvalidInputException | UndeliveredEmailException e ) {
                LOG.error( "Exception caught in IncompleteSurveyReminderSender.main while trying to send reminder mail to "
                    + survey.getCustomerFirstName() + " for completion of survey. Nested exception is ", e );
            }
        }

    }


    /**
     * 
     */
    @Override
    public void sendReportBugMailToAdmin( String displayName, String errorMsg, String recipientMailId )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.info( "Method sendReportBugMailToAdmin() started." );
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending sending report bug  mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending report bug  mail " );
        }

        LOG.info( "Saving EmailEntity with recipient mail id : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );

        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.REPORT_BUG_MAIL_TO_ADMIN_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.REPORT_BUG_MAIL_TO_ADMIN_BODY );

        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, displayName, errorMsg ) );

        LOG.info( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );

        LOG.info( "Method sendReportBugMailToAdmin() finished." );
    }
    
    @Override
    public void sendReportBugMailToAdminForExceptionInBatch( String displayName, String batchName , String lastRunTime ,String errorMsg, String exceptionStackTrace , String recipientMailId )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.info( "Method sendReportBugMailToAdminForExceptionInBatch() started." );
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sendReportBugMailToAdminForExceptionInBatch " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sendReportBugMailToAdminForExceptionInBatch " );
        }

        LOG.info( "Saving EmailEntity with recipient mail id : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );

        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.REPORT_EXCEPTION_IN_BATCH_TO_ADMIN_SUBJECT;
        
        FileContentReplacements messageSubjectReplacements = new FileContentReplacements();
        messageSubjectReplacements.setFileName( subjectFileName );
        messageSubjectReplacements.setReplacementArgs( Arrays.asList( batchName ) );
        
        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.REPORT_EXCEPTION_IN_BATCH_TO_ADMIN_BODY );

        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, displayName, batchName , lastRunTime , errorMsg , exceptionStackTrace ) );

        LOG.info( "Calling email sender to send mail" );
        emailSender.sendEmailWithSubjectAndBodyReplacements( emailEntity, messageSubjectReplacements, messageBodyReplacements, false, false );

        LOG.info( "Method sendReportBugMailToAdminForExceptionInBatch() finished." );
    }
    

    @Async
    @Override
    public void sendComplaintHandleMail( String recipientMailId, String customerName, String customerMailId, String mood,
        String rating, String surveyDetail ) throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending survey completion mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending survey complaint handler mail " );
        }

        if ( customerMailId == null || customerMailId.isEmpty() ) {
            LOG.error( "Customer email Id is empty or null " );
            throw new InvalidInputException( "Customer email Id is empty or null " );
        }
        
        //SS-1435: Send survey details too. Check that it is not null.
        if ( surveyDetail == null || surveyDetail.isEmpty() ) {
            LOG.error( "surveyDetail parameter is empty or null for sending account upgrade mail " );
            throw new InvalidInputException( "surveyDetail parameter is empty or null for sending survey completion mail " );
        }

        String[] mailIds = recipientMailId.split( "," );
        List<String> mailIdList = new ArrayList<String>();

        for ( String mailId : mailIds ) {
            mailIdList.add( mailId.trim() );
        }

        LOG.info( "Sending complaint handle email to : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( mailIdList );

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SURVEY_COMPLAINT_HANDLER_MAIL_BODY );
        
        //SS-1435: Send survey details too.
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, customerName, customerName, customerMailId,
            mood, rating, surveyDetail ) );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SURVEY_COMPLAINT_HANDLER_MAIL_SUBJECT, messageBodyReplacements, false, false );
        LOG.info( "Successfully sent survey completion mail" );
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
        String senderEmailAddress, String messageId ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.info( "Executing the sendSurveyReminderMail() method" );
        boolean saveForwardMailDetails = true;

        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null in forwardCustomerReplyMail method " );
            throw new InvalidInputException( "Recipient email Id is empty or null in forwardCustomerReplyMail method " );
        }
        if ( subject == null || subject.isEmpty() ) {
            LOG.error( "Subject is empty or null in forwardCustomerReplyMail method" );
            throw new InvalidInputException( "Subject is empty or null in forwardCustomerReplyMail method" );
        }
        if ( mailBody == null || mailBody.isEmpty() ) {
            LOG.error( "Mail Body is empty or null in forwardCustomerReplyMail method" );
            throw new InvalidInputException( "Mail Body is empty or null in forwardCustomerReplyMail method" );
        }
        if ( senderName == null || senderName.isEmpty() ) {
            LOG.error( "Sender Name is empty or null in forwardCustomerReplyMail method " );
            throw new InvalidInputException( "Sender Name is empty or null in forwardCustomerReplyMail method " );
        }
        if ( senderEmailAddress == null || senderEmailAddress.isEmpty() ) {
            LOG.error( "Sender email Id is empty or null in forwardCustomerReplyMail method " );
            throw new InvalidInputException( "Sender email Id is empty or null in forwardCustomerReplyMail method " );
        }

        if ( messageId == null || messageId.isEmpty() ) {
            LOG.error( "Message Id is empty or null in forwardCustomerReplyMail method " );
            throw new InvalidInputException( "Message Id is empty or null in forwardCustomerReplyMail method " );
        }

        try {
            // Find Forward mail details with messageId
            if ( forwardMailDetailsDao.checkIfForwardMailDetailsExist( senderEmailAddress, recipientMailId, messageId ) ) {
                LOG.info( "This mail has already been sent to the recipient" );
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

        LOG.info( "Forwarding customer reply mail from " + senderEmailAddress + "  to : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        emailEntity.setSenderName( senderName );
        emailEntity.setSenderEmailId( senderEmailAddress );
        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmail( emailEntity, subject, mailBody, true, false );
        LOG.info( "Successfully forwarded customer reply mail from " + senderEmailAddress + " to : " + recipientMailId );
    }
}
// JIRA: SS-7: By RM02: EOC