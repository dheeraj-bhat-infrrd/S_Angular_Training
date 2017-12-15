package com.realtech.socialsurvey.core.services.mail.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.EmailTemplateConstants;
import com.realtech.socialsurvey.core.dao.ForwardMailDetailsDao;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.EmailEntity;
import com.realtech.socialsurvey.core.entities.FileContentReplacements;
import com.realtech.socialsurvey.core.entities.ForwardMailDetails;
import com.realtech.socialsurvey.core.entities.MailContent;
import com.realtech.socialsurvey.core.entities.MonthlyDigestAggregate;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.Plan;
import com.realtech.socialsurvey.core.entities.SurveyCsvInfo;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.generator.UrlService;
import com.realtech.socialsurvey.core.services.mail.EmailSender;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
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

    @Autowired
    private EmailFormatHelper emailFormatHelper;

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private FileOperations fileOperations;

    @Autowired
    private OrganizationUnitSettingsDao organizationUnitSettingsDao;
    
    @Value ( "${SOCIALSURVEYME_SENDER_EMAIL_DOMAIN}")
    private String defaultSendGridMeEmailDomain;
    
    @Value ("${SOCIALSURVEYUS_SENDER_EMAIL_DOMAIN}")
    private String defaultSendGridUsEmailDomain;

    @Value ( "${APPLICATION_BASE_URL}")
    private String appBaseUrl;

    @Value ( "${APPLICATION_SUPPORT_EMAIL}")
    private String applicationSupportEmail;

    @Value ( "${APPLICATION_LOGO_URL}")
    private String appLogoUrl;

    @Value ( "${APPLICATION_NEW_LOGO_URL}")
    private String appNewLogoUrl;

    @Value ( "${APPLICATION_ADMIN_EMAIL}")
    private String applicationAdminEmail;

    @Value ( "${APPLICATION_ADMIN_NAME}")
    private String applicationAdminName;

    @Value ( "${PARAM_ORDER_TAKE_SURVEY_REMINDER}")
    String paramOrderTakeSurveyReminder;
    
    @Value ( "${CURRENT_PROFILE}")
    private String currentProfile;

    @Value ( "${APPLICATION_WORD_PRESS_SITE_URL}")
    private String applicationWordPressSite;

    @Autowired
    private UrlService urlService;

    @Autowired
    private ForwardMailDetailsDao forwardMailDetailsDao;

    @Autowired
    private UserManagementService userManagementService;
    
    @Autowired
    private GenericDao<User, Long> userDao;


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
        LOG.info( "Method for sending registration invite mail called with url : %s firstName : %s and lastName : %s", url,  firstName, lastName);
        if ( url == null || url.isEmpty() ) {
            LOG.error( "Url in sendRegistrationInviteMail is empty or null." );
            throw new InvalidInputException( "Url in sendRegistrationInviteMail is empty or null." );
        }
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id in sendRegistrationInviteMail is empty or null." );
            throw new InvalidInputException( "Recipient email Id in sendRegistrationInviteMail is empty or null." );
        }
        if ( firstName == null || firstName.isEmpty() ) {
            LOG.error( "Firstname is in sendRegistrationInviteMail empty or null." );
            throw new InvalidInputException( "Firstname is in sendRegistrationInviteMail empty or null." );
        }

        LOG.debug( "Initiating URL Service to shorten the url: %s", url );
        String shortUrl = urlService.shortenUrl( url );
        LOG.debug( "Finished calling URL Service to shorten the url in sendRegistrationInviteMail. Shortened URL : %s", shortUrl );

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
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

        LOG.debug( "Sending mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, true, false );
        LOG.info( "Successfully sent registration invite mail" );
    }


    @Async
    @Override
    public void sendNewRegistrationInviteMail( String url, String recipientMailId, String firstName, String lastName,
        int planId ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.info( "Method sendNewRegistrationInviteMail started for url : %s firstName : %s lastName : %s and planId : %s", url, firstName, lastName, planId );
        if ( url == null || url.isEmpty() ) {
            LOG.error( "Url in sendNewRegistrationInviteMail is empty or null." );
            throw new InvalidInputException( "Url in sendNewRegistrationInviteMail is empty or null." );
        }
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id in sendNewRegistrationInviteMail is empty or null." );
            throw new InvalidInputException( "Recipient email Id in sendNewRegistrationInviteMail is empty or null." );
        }
        if ( firstName == null || firstName.isEmpty() ) {
            LOG.error( "Firstname in sendNewRegistrationInviteMail is empty or null." );
            throw new InvalidInputException( "Firstname in sendNewRegistrationInviteMail is empty or null." );
        }

        LOG.debug( "Initiating URL Service to shorten the url %s", url );
        String shortUrl = urlService.shortenUrl( url );
        LOG.debug( "Finished calling URL Service to shorten the url in sendNewRegistrationInviteMail. Shortened URL : %s", shortUrl );

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
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

        LOG.debug( "Sending mail in sendNewRegistrationInviteMail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, true, false );
    }


    @Async
    @Override
    public void sendCompanyRegistrationStageMail( String firstName, String lastName, List<String> recipientMailIds,
        String registrationStage, String entityName, String details, boolean isImmediate )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.info( "Method to send registration stage mail started" );
        if ( registrationStage == null || registrationStage.isEmpty() ) {
            throw new InvalidInputException( "Registration stage cannot be empty" );
        }
        if ( entityName == null || entityName.isEmpty() ) {
            throw new InvalidInputException( "Name cannot be null" );
        }

        if ( recipientMailIds == null || recipientMailIds.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending CompanyRegistrationStageMail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending CompanyRegistrationStageMail " );
        }

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailIds );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.COMPANY_REGISTRATION_STAGE_MAIL_SUBJECT;

        String modDetails = (details == null) ? "" : details;
        
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
        emailSender.sendEmailWithSubjectAndBodyReplacements( emailEntity, messageSubjectReplacements, messageBodyReplacements,
            isImmediate, false );
    }


    @Async
    @Override
    public void sendAgentSurveyReminderMail( String recipientMailId, SurveyPreInitiation survey )
        throws InvalidInputException, UndeliveredEmailException
    {

        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending registration invite mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending registration invite mail " );
        }

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.AGENT_SURVEY_REMINDER_EMAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.AGENT_SURVEY_REMINDER_EMAIL_BODY );
        String customerName = survey.getCustomerFirstName() + " " + survey.getCustomerLastName();
        messageBodyReplacements.setReplacementArgs(
            Arrays.asList( appLogoUrl, survey.getAgentName(), customerName, survey.getCustomerEmailId(), customerName ) );

        LOG.debug( "Sending mail for sendAgentSurveyReminderMail." );
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
        LOG.info( "Method to send Email to reset the password link with URL : %s \t and Recipients Mail ID : %s", url, recipientMailId );
        if ( url == null || url.isEmpty() ) {
            LOG.error( "URL in sendResetPasswordEmail can not be null or empty." );
            throw new InvalidInputException( "URL in sendResetPasswordEmail can not be null or empty." );
        }
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipients Email Id in sendResetPasswordEmail can not be null or empty." );
            throw new InvalidInputException( "Recipients Email Id in sendResetPasswordEmail can not be null or empty." );
        }
        if ( name == null || name.isEmpty() ) {
            LOG.error( "Recipients name in sendResetPasswordEmail can not be null or empty" );
            throw new InvalidInputException( "Recipients name in sendResetPasswordEmail can not be null or empty" );
        }

        LOG.debug( "Initiating URL Service to shorten the url %s", url );
        String shortUrl = urlService.shortenUrl( url );
        LOG.debug( "Finished calling URL Service to shorten the url in sendResetPasswordEmail. Shortened URL : %s", shortUrl );

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.RESET_PASSWORD_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.RESET_PASSWORD_MAIL_BODY );
        messageBodyReplacements
            .setReplacementArgs( Arrays.asList( appLogoUrl, name, loginName, shortUrl, shortUrl, shortUrl, appBaseUrl, appBaseUrl ) );

        LOG.debug( "Sending mail in sendResetPasswordEmail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
    }


    @Async
    @Override
    public void sendInvitationToSocialSurveyAdmin( String url, String recipientMailId, String name, String loginName )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.info( "Method to send Email to social survey admin link with URL : " + url + "\t and Recipients Mail ID : "
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
            LOG.error( "Recipients name can not be null or empty" );
            throw new InvalidInputException( "Recipients name can not be null or empty" );
        }

        LOG.info( "Initiating URL Service to shorten the url " + url );
        url = urlService.shortenUrl( url );
        LOG.info( "Finished calling URL Service to shorten the url.Shortened URL : " + url );

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SS_ADMIN_INVITATION_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SS_ADMIN_INVITATION_MAIL_BODY );
        messageBodyReplacements
            .setReplacementArgs( Arrays.asList( appLogoUrl, name, loginName, url, url, url, appBaseUrl, appBaseUrl ) );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, true, false );
        LOG.info( "Successfully sent invitation to social survey admin " );
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
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SUBSCRIPTION_UNSUCCESSFUL_MAIL_BODY );
        messageBodyReplacements
            .setReplacementArgs( Arrays.asList( appLogoUrl, name, retryDays, recipientMailId, appBaseUrl, appBaseUrl ) );

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
        fileContentReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.EMAIL_VERIFICATION_MAIL_BODY );
        fileContentReplacements
            .setReplacementArgs( Arrays.asList( appLogoUrl, recipientName, url, url, url, appBaseUrl, appBaseUrl ) );

        LOG.debug( "Calling email sender to send verification mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, fileContentReplacements, false, false );
        LOG.info( "Successfully sent verification mail" );
    }


    @Async
    @Override
    public void sendEmailVerificationRequestMailToAdmin( String url, String recipientMailId, String recipientName,
        String emailToVerify, String entityName ) throws InvalidInputException, UndeliveredEmailException
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
            + EmailTemplateConstants.EMAIL_VERIFICATION_TO_ADMIN_MAIL_SUBJECT;

        FileContentReplacements fileContentReplacements = new FileContentReplacements();
        fileContentReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.EMAIL_VERIFICATION_TO_ADMIN_MAIL_BODY );
        fileContentReplacements.setReplacementArgs(
            Arrays.asList( appLogoUrl, recipientName, emailToVerify, entityName, url, url, url, appBaseUrl, appBaseUrl ) );

        LOG.debug( "Calling email sender to send verification mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, fileContentReplacements, false, false );
        LOG.info( "Successfully sent verification mail" );
    }


    @Override
    public void sendEmailVerifiedNotificationMail( String recipientMailId, String recipientName )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.info( "Method sendEmailVerifiedNotificationMail called for emailId : " + recipientMailId );
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            throw new InvalidInputException( "Recipients Email Id can not be null or empty" );
        }
        if ( recipientName == null || recipientName.isEmpty() ) {
            throw new InvalidInputException( "Recipients Name can not be null or empty" );
        }


        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.EMAIL_VERIFIED_NOTIFICATION_MAIL_SUBJECT;

        FileContentReplacements fileContentReplacements = new FileContentReplacements();
        fileContentReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.EMAIL_VERIFIED_NOTIFICATION_MAIL_BODY );
        fileContentReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, recipientName, appBaseUrl, appBaseUrl ) );

        LOG.debug( "Calling email sender to sendEmailVerifiedNotificationMaill" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, fileContentReplacements, false, false );
        LOG.info( "Successfully sendEmailVerifiedNotificationMail" );
    }


    @Override
    public void sendEmailVerifiedNotificationMailToAdmin( String recipientMailId, String recipientName, String verifiedEmail,
        String entityName ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.info( "Method sendEmailVerifiedNotificationMailToAdmin called for emailId : " + recipientMailId );
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

        FileContentReplacements subjectFileContentReplacements = new FileContentReplacements();
        subjectFileContentReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.EMAIL_VERIFIED_NOTIFICATION_MAIL_TO_ADMIN_SUBJECT );
        subjectFileContentReplacements.setReplacementArgs( Arrays.asList( entityName ) );

        FileContentReplacements fileContentReplacements = new FileContentReplacements();
        fileContentReplacements.setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.EMAIL_VERIFIED_NOTIFICATION_MAIL_TO_ADMIN_BODY );
        fileContentReplacements.setReplacementArgs(
            Arrays.asList( appLogoUrl, recipientName, verifiedEmail, entityName, appBaseUrl, appBaseUrl ) );

        LOG.debug( "Calling email sender to sendEmailVerifiedNotificationMailToAdmin" );
        emailSender.sendEmailWithSubjectAndBodyReplacements( emailEntity, subjectFileContentReplacements,
            fileContentReplacements, false, false );
        LOG.info( "Successfully sent EmailVerifiedNotificationMailToAdmin" );
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
        if ( hiddenSection ) {
            fileContentReplacements.setFileName(
                EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.VERIFICATION_MAIL_BODY_CUSTOM );
            fileContentReplacements.setReplacementArgs(
                Arrays.asList( appLogoUrl, recipientName, recipientName, url, url, url, loginName, appBaseUrl, appBaseUrl ) );
        } else {
            fileContentReplacements
                .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.VERIFICATION_MAIL_BODY );
            fileContentReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, recipientName, recipientName, url, url, url,
                appBaseUrl, profileName, appBaseUrl, profileName, loginName, appBaseUrl, appBaseUrl ) );
        }

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
        String loginName, boolean holdSendingMail, boolean hiddenSection )
        throws InvalidInputException, UndeliveredEmailException
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
        if ( hiddenSection ) {
            messageBodyReplacements.setFileName(
                EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.COMPLETE_REGISTRATION_MAIL_BODY_CUSTOM );
            messageBodyReplacements.setReplacementArgs(
                Arrays.asList( appLogoUrl, name, url, url, url, loginName, applicationSupportEmail, appBaseUrl, appBaseUrl ) );
        } else {
            messageBodyReplacements.setFileName(
                EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.COMPLETE_REGISTRATION_MAIL_BODY );
            messageBodyReplacements.setReplacementArgs(
                Arrays.asList( appLogoUrl, name, url, url, url, loginName, applicationSupportEmail, appBaseUrl, appBaseUrl ) );
        }
        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false,
            holdSendingMail );
        LOG.info( "Successfully sent mail for registraion completion." );
    }


    // JIRA SS-42 by RM-05 : EOC

    @Async
    @Override
    public void sendFatalExceptionEmail( String recipientMailId, String stackTrace )
        throws InvalidInputException, UndeliveredEmailException
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
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.FATAL_EXCEPTION_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, currentProfile, stackTrace ) );

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
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.EMAIL_SENDING_FAILURE_MAIL_BODY );
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
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.RETRY_CHARGE_MAIL_BODY );
        messageBodyReplacements
            .setReplacementArgs( Arrays.asList( appLogoUrl, displayName, recipientMailId, appBaseUrl, appBaseUrl ) );

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
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.RETRIES_EXHAUSTED_MAIL_BODY );
        messageBodyReplacements
            .setReplacementArgs( Arrays.asList( appLogoUrl, displayName, recipientMailId, appBaseUrl, appBaseUrl ) );

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
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.ACCOUNT_DISABLED_MAIL_BODY );
        messageBodyReplacements
            .setReplacementArgs( Arrays.asList( appLogoUrl, displayName, recipientMailId, appBaseUrl, appBaseUrl ) );

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
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.ACCOUNT_DELETED_MAIL_BODY );
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
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.ACCOUNT_UPGRADE_MAIL_BODY );
        messageBodyReplacements
            .setReplacementArgs( Arrays.asList( appLogoUrl, displayName, recipientMailId, appBaseUrl, appBaseUrl ) );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.info( "Successfully sent account upgrade mail" );
    }


    @Async
    @Override
    public void sendDefaultSurveyCompletionMail( String recipientMailId, String firstName, String agentName, String agentEmail,
        String agentProfileName, String logoUrl, long agentId ) throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending survey completion mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending survey completion mail " );
        }
        if ( firstName == null || firstName.isEmpty() ) {
            LOG.error( "firstName parameter is empty or null for sending account upgrade mail " );
            throw new InvalidInputException( "firstName parameter is empty or null for sending survey completion mail " );
        }

        LOG.info( "Sending survey completion email to : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId, agentId, agentName );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SURVEY_COMPLETION_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_COMPLETION_MAIL_BODY );
        if ( logoUrl == null || logoUrl.equalsIgnoreCase( "" ) ) {
            messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, WordUtils.capitalize( firstName ), agentName,
                agentName, appBaseUrl, agentProfileName, appBaseUrl, agentProfileName, agentName ) );
        } else {
            messageBodyReplacements.setReplacementArgs( Arrays.asList( logoUrl, WordUtils.capitalize( firstName ), agentName,
                agentName, appBaseUrl, agentProfileName, appBaseUrl, agentProfileName, agentName ) );
        }


        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.info( "Successfully sent survey completion mail" );
    }


    @Async
    @Override
    public void sendDefaultSurveyCompletionUnpleasantMail( String recipientMailId, String firstName, String agentName,
        String agentEmail, String companyName, String logoUrl, long agentId )
        throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending survey completion unpleasant mail " );
            throw new InvalidInputException(
                "Recipient email Id is empty or null for sending survey completion unpleasant mail " );
        }
        if ( firstName == null || firstName.isEmpty() ) {
            LOG.error( "firstName parameter is empty or null for sending account upgrade mail " );
            throw new InvalidInputException(
                "firstName parameter is empty or null for sending survey completion unpleasant mail " );
        }

        LOG.info( "Sending survey completion email to : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId, agentId, agentName );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SURVEY_COMPLETION_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_COMPLETION_UNPLEASANT_MAIL_BODY );
        if ( logoUrl == null || logoUrl.equalsIgnoreCase( "" ) ) {
            messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, firstName, agentName, companyName ) );
        } else {
            messageBodyReplacements
                .setReplacementArgs( Arrays.asList( logoUrl, firstName, agentName, agentName, companyName ) );
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
        subjectReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_REMINDER_MAIL_SUBJECT );
        subjectReplacements.setReplacementArgs( Arrays.asList( agentName ) );

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_REMINDER_MAIL_BODY );
        String currentYear = String.valueOf( Calendar.getInstance().get( Calendar.YEAR ) );
        DateFormat dateFormat = new SimpleDateFormat( "yyyy/MM/dd" );
        String fullAddress = "";

        if ( logoUrl == null || logoUrl.equalsIgnoreCase( "" ) ) {
            messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, WordUtils.capitalize( firstName ), link,
                link, link, "", agentSignature, appBaseUrl, appBaseUrl, recipientMailId, companyName,
                dateFormat.format( new Date() ), agentEmailId, companyName, currentYear, fullAddress ) );
        } else {
            messageBodyReplacements.setReplacementArgs( Arrays.asList( logoUrl, WordUtils.capitalize( firstName ), link, link,
                link, "", agentSignature, appBaseUrl, appBaseUrl, recipientMailId, companyName, dateFormat.format( new Date() ),
                agentEmailId, companyName, currentYear, fullAddress ) );
        }

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithSubjectAndBodyReplacements( emailEntity, subjectReplacements, messageBodyReplacements, false,
            false );
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
    public void sendSurveyCompletionMailToAdminsAndAgent( String agentName, String recipientName, String recipientMailId,
        String surveyDetail, String customerName, String rating, String logoUrl, String agentProfileLink, String customerDetail )
        throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending survey completion mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending survey completion mail " );
        }
        if ( surveyDetail == null || surveyDetail.isEmpty() ) {
            LOG.error( "syrveyDetail parameter is empty or null for sending account upgrade mail " );
            throw new InvalidInputException( "surveyDetail parameter is empty or null for sending survey completion mail " );
        }
        
        if ( customerDetail == null || customerDetail.isEmpty() ) {
            LOG.error( "customerDetail parameter is empty or null for sending survey completion mail " );
            throw new InvalidInputException( "customerDetail parameter is empty or null for sending survey completion mail " );
        }

        LOG.info( "Sending survey completion email to : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );

        FileContentReplacements subjectReplacements = new FileContentReplacements();
        subjectReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_COMPLETION_ADMINS_MAIL_SUBJECT );
        subjectReplacements.setReplacementArgs( Arrays.asList( rating, customerName, agentName ) );

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_COMPLETION_ADMINS_MAIL_BODY );

        if ( logoUrl == null || logoUrl.isEmpty() ) {
            messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, recipientName, customerName, rating,
                agentName, customerDetail, surveyDetail, agentName, agentProfileLink, agentProfileLink, recipientMailId, recipientMailId,
                String.valueOf( Calendar.getInstance().get( Calendar.YEAR ) ) ) );
        } else {
            messageBodyReplacements.setReplacementArgs( Arrays.asList( logoUrl, recipientName, customerName, rating, agentName,
                customerDetail, surveyDetail, agentName, agentProfileLink, agentProfileLink, recipientMailId, recipientMailId,
                String.valueOf( Calendar.getInstance().get( Calendar.YEAR ) ) ) );
        }


        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithSubjectAndBodyReplacements( emailEntity, subjectReplacements, messageBodyReplacements, false,
            false );
        LOG.info( "Successfully sent survey completion mail" );
    }


    @Async
    @Override
    public void sendDefaultSocialPostReminderMail( String recipientMailId, String agentPhone, String agentTitle,
        String companyName, String firstName, String agentName, String links, String logoUrl )
        throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending survey completion mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending survey completion mail " );
        }
        if ( firstName == null || firstName.isEmpty() ) {
            LOG.error( "firstName parameter is empty or null for sending account upgrade mail " );
            throw new InvalidInputException( "firstName parameter is empty or null for sending survey completion mail " );
        }

        LOG.info( "Sending survey reminder email to : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SOCIALPOST_REMINDER_MAIL_SUBJECT;

        String agentSignature = emailFormatHelper.buildAgentSignature( agentName, agentPhone, agentTitle, companyName );

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SOCIALPOST_REMINDER_MAIL_BODY );
        if ( logoUrl == null || logoUrl.equalsIgnoreCase( "" ) ) {
            messageBodyReplacements.setReplacementArgs(
                Arrays.asList( appLogoUrl, WordUtils.capitalize( firstName ), links, "", agentSignature ) );
        } else {
            messageBodyReplacements
                .setReplacementArgs( Arrays.asList( logoUrl, WordUtils.capitalize( firstName ), links, "", agentSignature ) );
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
        subjectReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.CONTACT_US_MAIL_SUBJECT );
        subjectReplacements.setReplacementArgs( Arrays.asList( senderName ) );

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.CONTACT_US_MAIL_BODY );
        messageBodyReplacements
            .setReplacementArgs( Arrays.asList( appLogoUrl, displayName, senderName, senderEmailId, message ) );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithSubjectAndBodyReplacements( emailEntity, subjectReplacements, messageBodyReplacements, false,
            false );
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
        messageSubjectReplacements.setReplacementArgs( Arrays.asList( agentName ) );

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_INVITATION_MAIL_BODY );

        if ( logoUrl == null || logoUrl.equalsIgnoreCase( "" ) ) {
            messageBodyReplacements.setReplacementArgs(
                Arrays.asList( appLogoUrl, WordUtils.capitalize( firstName ), link, link, link, "", agentSignature,
                    recipientMailId, companyName, surveyInitiatedOn, agentEmailId, companyName, currentYear, fullAddress ) );
        } else {
            messageBodyReplacements.setReplacementArgs(
                Arrays.asList( logoUrl, WordUtils.capitalize( firstName ), link, link, link, "", agentSignature,
                    recipientMailId, companyName, surveyInitiatedOn, agentEmailId, companyName, currentYear, fullAddress ) );
        }


        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithSubjectAndBodyReplacements( emailEntity, messageSubjectReplacements, messageBodyReplacements,
            false, false );
        LOG.info( "Successfully sent survey invitation mail" );
    }


    @Async
    @Override
    public void sendSurveyInvitationMail( String recipientMailId, String subject, String mailBody, String emailId, String name,
        long agentId ) throws InvalidInputException, UndeliveredEmailException
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
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.ACCOUNT_BLOCKING_MAIL_BODY );
        messageBodyReplacements
            .setReplacementArgs( Arrays.asList( appLogoUrl, displayName, recipientMailId, appBaseUrl, appBaseUrl ) );

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
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.ACCOUNT_REACTIVATION_MAIL_BODY );
        messageBodyReplacements
            .setReplacementArgs( Arrays.asList( appLogoUrl, displayName, recipientMailId, appBaseUrl, appBaseUrl ) );

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
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SUBSCRIPTION_PRICE_UPDATED_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs(
            Arrays.asList( appLogoUrl, name, numOfUsers, oldAmount, revisedAmount, recipientMailId, appBaseUrl, appBaseUrl ) );

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
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.MANUAL_REGISTRATION_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, fullName, fullName, link, link, link, applicationSupportEmail ) );

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
            LOG.error( "firstName parameter is empty or null for sending account upgrade mail " );
            throw new InvalidInputException( "firstName parameter is empty or null for sending survey completion mail " );
        }

        LOG.info( "Initiating URL Service to shorten the url " + link );
        link = urlService.shortenUrl( link );
        LOG.info( "Finished calling URL Service to shorten the url.Shortened URL : " + link );

        LOG.info( "Sending survey reminder email to : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId, agentId, agentName );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SURVEY_INVITATION_MAIL_CUSTOMER_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_INVITATION_MAIL_CUSTOMER_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, WordUtils.capitalize( firstName ), agentName,
            link, link, link, appBaseUrl, appBaseUrl ) );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.info( "Successfully sent survey invitation mail" );
    }


    @Async
    @Override
    public void sendSurveyInvitationMailByCustomer( String recipientMailId, String subject, String mailBody, String emailId,
        String firstName, long agentId ) throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending survey invitation mail by customer" );
            throw new InvalidInputException(
                "Recipient email Id is empty or null for sending survey invitation mail by customer" );
        }
        if ( subject == null || subject.isEmpty() ) {
            LOG.error( "subject parameter is empty or null for sending survey invitation mail by customer" );
            throw new InvalidInputException(
                "subject parameter is empty or null for sending survey invitation mail by customer" );
        }

        LOG.info( "Sending survey invitation email to : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId, agentId, firstName );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmail( emailEntity, subject, mailBody, false, false );
        LOG.info( "Successfully sent survey invitation mail" );
    }


    @Async
    @Override
    public void sendDefaultSurveyRestartMail( String recipientMailId, String logoUrl, String firstName, String agentName,
        String link, String agentEmailId, String agentSignature, long agentId )
        throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending survey restart mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending survey restart mail " );
        }
        if ( firstName == null || firstName.isEmpty() ) {
            LOG.error( "firstName parameter is empty or null for sending survey restart mail " );
            throw new InvalidInputException( "firstName parameter is empty or null for sending survey restart mail " );
        }

        LOG.info( "Initiating URL Service to shorten the url " + link );
        link = urlService.shortenUrl( link );
        LOG.info( "Finished calling URL Service to shorten the url.Shortened URL : " + link );

        LOG.info( "Sending survey restart email to : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId, agentId, agentName );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SURVEY_RESTART_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_RESTART_MAIL_BODY );

        if ( logoUrl == null || logoUrl.equalsIgnoreCase( "" ) ) {
            messageBodyReplacements.setReplacementArgs(
                Arrays.asList( appLogoUrl, WordUtils.capitalize( firstName ), link, link, link, "", agentSignature ) );
        } else {
            messageBodyReplacements.setReplacementArgs(
                Arrays.asList( logoUrl, WordUtils.capitalize( firstName ), link, link, link, "", agentSignature ) );
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
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SOCIAL_CONNECT_MAIL_BODY );
        messageBodyReplacements
            .setReplacementArgs( Arrays.asList( appLogoUrl, displayName, account, recipientMailId, appBaseUrl, appBaseUrl ) );

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

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.info( "Successfully sent social connect mail" );
    }


    @Async
    @Override
    public void sendSurveyReportMail( String recipientMailId, String displayName, String reason )
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

        LOG.info( "Sending survey completion email to : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SURVEY_REPORT_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_REPORT_MAIL_BODY );
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
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.CORRUPT_PREINITIATION_RECORD_MAIL_BODY );
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
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.INVALID_EMAILS_MAIL_BODY );
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
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.RECORDS_NOT_UPLOADED_MAIL_BODY );
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
        String messageBodyText, String recipientMailId, Map<String, String> attachmentsDetails )
        throws InvalidInputException, UndeliveredEmailException
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
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.HELP_MAIL_TO_SS_ADMIN_BODY );

        messageBodyReplacements.setReplacementArgs(
            Arrays.asList( appLogoUrl, displayName, senderName, senderEmail, mailSubject, messageBodyText ) );

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
        LOG.debug( "Preparing email entity for registration invitation for recipientMailId {}", recipients );

        EmailEntity emailEntity = new EmailEntity();
        emailEntity.setRecipients( recipients );
        emailEntity.setRecipientType( EmailEntity.RECIPIENT_TYPE_TO );

        LOG.debug( "Prepared email entity for registrationInvite" );
        return emailEntity;
    }


    // creating email entity with senders email id as U<userid>@socialsurvey.me
    private EmailEntity prepareEmailEntityForSendingEmail( String recipientMailId, long userId, String name )
        throws InvalidInputException
    {
        LOG.debug( "Preparing email entity with recipent " + recipientMailId + " user id " + userId + " and name " + name );
        List<String> recipients = new ArrayList<String>();
        recipients.add( recipientMailId );

        EmailEntity emailEntity = new EmailEntity();
        emailEntity.setRecipients( recipients );
        emailEntity.setSenderName( name );

        AgentSettings agentSettings = organizationUnitSettingsDao.fetchAgentSettingsById( userId );
        //JIRA SS-700 begin
        if ( agentSettings.getUserEncryptedId() == null ) {
            agentSettings.setUserEncryptedId( userManagementService.generateUserEncryptedId( agentSettings.getIden() ) );
            organizationUnitSettingsDao.updateParticularKeyAgentSettings( CommonConstants.USER_ENCRYPTED_ID,
                agentSettings.getUserEncryptedId(), agentSettings );
        }
        //JIRA SS-700 end
        //JIRA SS-60 //pass stored encrypted id in mongo for the user

        //JIRA SS-975
        //get companyId
        User user = userDao.findById( User.class,  agentSettings.getIden());
        long companyId = user.getCompany().getCompanyId();
        //get company settings
        OrganizationUnitSettings companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(
            companyId, MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
        //get sendemailthrough
        String sendEmailThrough=companySettings.getSendEmailThrough();
        
        emailEntity.setSendEmailThrough( sendEmailThrough );
        
        //JIRA SS-975 end
        
        //checking for the right defaultEmailDomain
        String defaultEmailDomain = null ;
        if( sendEmailThrough == null || sendEmailThrough.isEmpty() || sendEmailThrough.equals( CommonConstants.SEND_EMAIL_THROUGH_SOCIALSURVEY_ME ) ){
            defaultEmailDomain = defaultSendGridMeEmailDomain ;
        }else if( sendEmailThrough.equals( CommonConstants.SEND_EMAIL_THROUGH_SOCIALSURVEY_US  )){
            defaultEmailDomain = defaultSendGridUsEmailDomain ;
        }
        emailEntity.setSenderEmailId( "u-" + agentSettings.getUserEncryptedId() + "@" + defaultEmailDomain );
        
        emailEntity.setRecipientType( EmailEntity.RECIPIENT_TYPE_TO );

        LOG.debug( "Prepared email entity for sending mail" );
        return emailEntity;
    }


    @Override
    public void sendManualSurveyReminderMail( OrganizationUnitSettings companySettings, User user, String agentName,
        String agentEmailId, String agentPhone, String agentTitle, String companyName, SurveyPreInitiation survey,
        String surveyLink, String logoUrl, String agentDisclaimer, String agentLicenses ) throws InvalidInputException
    {
        LOG.info( "Sending manual survey reminder mail." );

        String agentSignature = emailFormatHelper.buildAgentSignature( agentName, agentPhone, agentTitle, companyName );
        DateFormat dateFormat = new SimpleDateFormat( "yyyy/MM/dd" );
        String currentYear = String.valueOf( Calendar.getInstance().get( Calendar.YEAR ) );
        String fullAddress = "";

        if ( logoUrl == null || logoUrl.equalsIgnoreCase( "" ) ) {
            logoUrl = appLogoUrl;
        }
        LOG.info( "Initiating URL Service to shorten the url " + surveyLink );
        try {
            surveyLink = urlService.shortenUrl( surveyLink );
        } catch ( InvalidInputException e ) {
            LOG.error( "InvalidInput Exception while url shortening url. Reason : ", e );
        }
        LOG.info( "Finished calling URL Service to shorten the url.Shortened URL : " + surveyLink );


        //get mail body and content
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
        String companyDisclaimer = "";
        if ( companySettings.getDisclaimer() != null )
            companyDisclaimer = companySettings.getDisclaimer();

        //replace legends
        mailSubject = emailFormatHelper.replaceLegends( true, mailSubject, appBaseUrl, logoUrl, surveyLink,
            survey.getCustomerFirstName(), survey.getCustomerLastName(), agentName, agentSignature, survey.getCustomerEmailId(),
            user.getEmailId(), companyName, dateFormat.format( new Date() ), currentYear, fullAddress, "",
            user.getProfileName(), companyDisclaimer, agentDisclaimer, agentLicenses );

        mailBody = emailFormatHelper.replaceLegends( false, mailBody, appBaseUrl, logoUrl, surveyLink,
            survey.getCustomerFirstName(), survey.getCustomerLastName(), agentName, agentSignature, survey.getCustomerEmailId(),
            user.getEmailId(), companyName, dateFormat.format( new Date() ), currentYear, fullAddress, "",
            user.getProfileName(), companyDisclaimer, agentDisclaimer, agentLicenses );
        //JIRA SS-473 end
        //send mail
        try {
            sendSurveyReminderMail( survey.getCustomerEmailId(), mailSubject, mailBody, agentName, user.getEmailId() );
        } catch ( InvalidInputException | UndeliveredEmailException e ) {
            LOG.error( "Exception caught while sending mail to " + survey.getCustomerEmailId() + " .Nested exception is ", e );
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
            LOG.warn( "Recipient email Id is empty or null for sending sending report bug  mail. " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending report bug  mail " );
        }

        LOG.debug( "Saving EmailEntity with recipient mail id : {}", recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );

        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.REPORT_BUG_MAIL_TO_ADMIN_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.REPORT_BUG_MAIL_TO_ADMIN_BODY );

        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, displayName, errorMsg ) );

        LOG.debug( "Calling email sender to send mail." );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );

        LOG.info( "Method sendReportBugMailToAdmin() finished." );
    }


    @Override
    public void sendReportBugMailToAdminForExceptionInBatch( String displayName, String batchName, String lastRunTime,
        String errorMsg, String exceptionStackTrace, String recipientMailId )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.info( "Method sendReportBugMailToAdminForExceptionInBatch() started." );
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sendReportBugMailToAdminForExceptionInBatch " );
            throw new InvalidInputException(
                "Recipient email Id is empty or null for sendReportBugMailToAdminForExceptionInBatch " );
        }

        LOG.info( "Saving EmailEntity with recipient mail id : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );

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

        LOG.info( "Calling email sender to send mail" );
        emailSender.sendEmailWithSubjectAndBodyReplacements( emailEntity, messageSubjectReplacements, messageBodyReplacements,
            false, false );

        LOG.info( "Method sendReportBugMailToAdminForExceptionInBatch() finished." );
    }


    @Async
    @Override
    public void sendComplaintHandleMail( String recipientMailId, String customerName, String customerMailId, String agentName, String mood,
        String rating, String surveySourceId, String surveyDetail ) throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending survey completion mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending survey complaint handler mail " );
        }

        if ( customerMailId == null || customerMailId.isEmpty() ) {
            LOG.error( "Customer email Id is empty or null " );
            throw new InvalidInputException( "Customer email Id is empty or null " );
        }
        
        if ( agentName == null || agentName.isEmpty() ) {
            LOG.error( "Agent Name  is empty or null " );
            throw new InvalidInputException( "Agent Name is empty or null " );
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
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_COMPLAINT_HANDLER_MAIL_BODY );

        //SS-1435: Send survey details too.
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, customerName, customerName, customerMailId, agentName, mood,
            rating, surveySourceId == null ? CommonConstants.NOT_AVAILABLE : surveySourceId, surveyDetail ) );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity,
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_COMPLAINT_HANDLER_MAIL_SUBJECT,
            messageBodyReplacements, false, false );
        LOG.info( "Successfully sent survey completion mail" );
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
            LOG.error( "Recipient email Id is empty or null for sending survey completion mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending survey complaint handler mail " );
        }

        if ( customerName == null || customerName.isEmpty() ) {
            LOG.error( "Customer name is empty or null " );
            throw new InvalidInputException( "Customer name is empty or null " );
        }

        String[] mailIds = recipientMailId.split( "," );
        List<String> mailIdList = new ArrayList<String>();

        for ( String mailId : mailIds ) {
            mailIdList.add( mailId.trim() );
        }

        LOG.info( "Sending complaint handle email to : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( mailIdList );

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.ZILLOW_REVIEW_COMPLAINT_HANDLER_MAIL_BODY );

        //SS-1435: Send survey details too.
        messageBodyReplacements
            .setReplacementArgs( Arrays.asList( appLogoUrl, customerName, customerName, rating, reviewUrl ) );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity,
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.ZILLOW_REVIEW_COMPLAINT_HANDLER_MAIL_SUBJECT,
            messageBodyReplacements, false, false );
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
        String senderEmailAddress, String messageId, String sendUsingDomain ) throws InvalidInputException, UndeliveredEmailException
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
        
        if(sendUsingDomain != null && sendUsingDomain.equals(defaultSendGridUsEmailDomain)){
            emailEntity.setSendEmailThrough(CommonConstants.SEND_EMAIL_THROUGH_SOCIALSURVEY_US);
        }
        
        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmail( emailEntity, subject, mailBody, true, false );
        LOG.info( "Successfully forwarded customer reply mail from " + senderEmailAddress + " to : " + recipientMailId );
    }


    /**
     * Method to send the billing report in a mail to the social survey admin
     */
    @Override
    public void sendBillingReportMail( String firstName, String lastName, String recipientMailId,
        Map<String, String> attachmentsDetails ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.info( "Method sendBillingReportMail() started." );
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending billing report mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending billing report mail " );
        }

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        emailEntity.setAttachmentDetail( attachmentsDetails );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.BILLING_REPORT_MAIL_SUBJECT;
        String displayName = firstName + " " + lastName;
        displayName.replaceAll( "null", "" );
        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.BILLING_REPORT_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, displayName ) );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, true, false );

        LOG.info( "Method sendBillingReportMail() finished." );
    }


    /**
     *
     */
    @Override
    public void sendCustomMail( String recipientName, String recipientMailId, String subject, String body,
        Map<String, String> attachmentsDetails ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.info( "Method sendCustomMail() started." );
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending custom mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending custom mail " );
        }

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        if ( attachmentsDetails != null && !attachmentsDetails.isEmpty() )
            emailEntity.setAttachmentDetail( attachmentsDetails );

        FileContentReplacements messageSubjectReplacements = new FileContentReplacements();
        messageSubjectReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.CUSTOM_MAIL_SUBJECT );
        messageSubjectReplacements.setReplacementArgs( Arrays.asList( subject ) );

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.CUSTOM_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, recipientName, body ) );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithSubjectAndBodyReplacements( emailEntity, messageSubjectReplacements, messageBodyReplacements,
            false, false );

        LOG.info( "Method sendCustomReportMail() finished." );
    }


    /**
     * Method to send the billing report in a mail to the social survey admin
     */
    @Override
    public void sendCustomReportMail( String recipientName, List<String> recipientMailIds, String subject,
        Map<String, String> attachmentsDetails ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.info( "Method sendCustomReportMail() started." );
        if ( recipientMailIds == null || recipientMailIds.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending billing report mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending billing report mail " );
        }

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailIds );
        emailEntity.setAttachmentDetail( attachmentsDetails );

        FileContentReplacements messageSubjectReplacements = new FileContentReplacements();
        messageSubjectReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SEND_REPORT_MAIL_SUBJECT );
        messageSubjectReplacements.setReplacementArgs( Arrays.asList( subject ) );

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SEND_REPORT_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, recipientName ) );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithSubjectAndBodyReplacements( emailEntity, messageSubjectReplacements, messageBodyReplacements,
            false, false );

        LOG.info( "Method sendCustomReportMail() finished." );
    }


    /**
     *
     */
    @Override
    public void sendSocialMediaTokenExpiryEmail( String displayName, String recipientMailId, String updateConnectionUrl,
        String appLoginUrl, String socialMediaType ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.info( "Method sendSocialMediaTokenExpiryEmail() started." );
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending sending report bug  mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending report bug  mail " );
        }

        LOG.info( "Saving EmailEntity with recipient mail id : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );

        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SOCIAL_MEDIA_TOKEN_EXPIRY_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SOCIAL_MEDIA_TOKEN_EXPIRY_MAIL_BODY );

        messageBodyReplacements
            .setReplacementArgs( Arrays.asList( appLogoUrl, displayName, socialMediaType, updateConnectionUrl, appLoginUrl ) );

        LOG.info( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );

        LOG.info( "Method sendSocialMediaTokenExpiryEmail() finished." );
    }
    
    
    @Async
    @Override
    public void sendPaymentFailedAlertEmail( String recipientMailId, String displayName, String companyName )
        throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending retries exhausted mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending payment faield alert mail " );
        }
        if ( displayName == null || displayName.isEmpty() ) {
            LOG.error( "displayName parameter is empty or null for sending retry exhausted mail " );
            throw new InvalidInputException( "displayName parameter is empty or null for sending payment faield alert mail " );
        }

        LOG.info( "Sending payment faield alert email to : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.PAYMENT_RETRIES_FAILED_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.PAYMENT_RETRIES_FAILED_MAIL_BODY );
        messageBodyReplacements
            .setReplacementArgs( Arrays.asList( appLogoUrl, displayName, companyName ) );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.info( "Successfully sent payment faield alert mail" );
    }
    
    
    
    @Async
    @Override
    public void sendCancelSubscriptionRequestAlertMail( String recipientMailId, String displayName, String companyName )
        throws InvalidInputException, UndeliveredEmailException
    {
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending retries exhausted mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending payment faield alert mail " );
        }
        if ( displayName == null || displayName.isEmpty() ) {
            LOG.error( "displayName parameter is empty or null for sending retry exhausted mail " );
            throw new InvalidInputException( "displayName parameter is empty or null for sending payment faield alert mail " );
        }

        LOG.info( "Sending payment faield alert email to : " + recipientMailId );
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.CANCEL_SUBSCRIPTION_REQUEST_ALERT_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.CANCEL_SUBSCRIPTION_REQUEST_ALERT_MAIL_BODY );
        messageBodyReplacements
            .setReplacementArgs( Arrays.asList( appLogoUrl, displayName, companyName ) );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.info( "Successfully sent payment faield alert mail" );
    }
    
    @Async
    @Override
    public void sendWebExceptionEmail( String recipientMailId, String stackTrace )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.info( "Sending WebException email to the admin." );
        if ( recipientMailId == null || recipientMailId.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sending web exception mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sending web exception mail " );
        }

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailId );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.WEB_EXCEPTION_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.FATAL_EXCEPTION_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, currentProfile, stackTrace ) );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.info( "Successfully sent web exception mail" );
    }

    @Async
    @Override
    public void sendNoTransactionAlertMail( List<String> recipientMailIds, String mailBody ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.info( "method sendNoTransactionAlertMail started" );
        if ( recipientMailIds == null || recipientMailIds.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sendNoTransactionAlertMail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sendNoTransactionAlertMail " );
        }
        if ( mailBody == null || mailBody.isEmpty() ) {
            LOG.error( "mailBody is empty or null for sendNoTransactionAlertMail " );
            throw new InvalidInputException( "mailBody is empty or null for sendNoTransactionAlertMail " );
        }
        

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailIds );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.NO_TRANSACTION_RECEIVED_ALERT_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.NO_TRANSACTION_RECEIVED_ALERT_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, mailBody ) );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.info( "method sendNoTransactionAlertMail ended" );    
    }
    
    @Async
    @Override
    public void sendHighVoulmeUnprocessedTransactionAlertMail( List<String> recipientMailIds, String mailBody ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.info( "method sendHighVoulmeUnprocessedTransactionAlertMail started" );
        if ( recipientMailIds == null || recipientMailIds.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sendHighVoulmeUnprocessedTransactionAlertMail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sendHighVoulmeUnprocessedTransactionAlertMail " );
        }
        if ( mailBody == null || mailBody.isEmpty() ) {
            LOG.error( "mailBody is empty or null for sendHighVoulmeUnprocessedTransactionAlertMail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sendHighVoulmeUnprocessedTransactionAlertMail " );
        }
        
        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailIds );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.HIGH_VOLUME_UNPROCESSED_TRANSACTION_ALERT_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.HIGH_VOLUME_UNPROCESSED_TRANSACTION_ALERT_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, mailBody ) );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.info( "method sendHighVoulmeUnprocessedTransactionAlertMail ended" );    
      }

    
    @Async
    @Override
    public void sendLessVoulmeOfTransactionReceivedAlertMail( List<String> recipientMailIds, String mailBody ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.info( "method sendLessVoulmeOfTransactionReceivedAlertMail started" );
        if ( recipientMailIds == null || recipientMailIds.isEmpty() ) {
            LOG.error( "Recipient email Id is empty or null for sendLessVoulmeOfTransactionReceivedAlertMail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for sendLessVoulmeOfTransactionReceivedAlertMail " );
        }
        if ( mailBody == null || mailBody.isEmpty() ) {
            LOG.error( "mailBody is empty or null for sendLessVoulmeOfTransactionReceivedAlertMail " );
            throw new InvalidInputException( "Mail body is empty or null for sendLessVoulmeOfTransactionReceivedAlertMail " );
        }
        

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( recipientMailIds );
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.LESS_VOLUME_OF_TRANSACTION_RECEIVED_ALERT_MAIL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.LESS_VOLUME_OF_TRANSACTION_RECEIVED_ALERT_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, mailBody ) );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, false, false );
        LOG.info( "method sendLessVoulmeOfTransactionReceivedAlertMail ended" );    
      }

    @Async
    @Override
    public void sendMonthlyDigestMail( MonthlyDigestAggregate digestAggregate )
        throws InvalidInputException, UndeliveredEmailException
    {
        if ( digestAggregate == null ) {
            LOG.error( "sendMonthlyDigestMail(): Digest Aggregate object is null" );
            throw new InvalidInputException( "Data for the monthly digest/snapshot mail is missing." );
        }
        if( digestAggregate.getCompanyId() < 1 ){
            LOG.error( "Company ID must be greater that one for Monthly digest/snapshot mail " );
            throw new InvalidInputException( "Company ID cannot be less than one for Monthly digest/snapshot mail." );
        }
        if ( StringUtils.isEmpty( digestAggregate.getRecipientMailId() ) ) {
            LOG.error( "Recipient email Id is empty or null for Monthly digest/snapshot mail " );
            throw new InvalidInputException( "Recipient email Id is empty or null for Monthly digest/snapshot mail." );
        }

        if ( digestAggregate.getDigestList() == null || digestAggregate.getDigestList().size() != 3 ) {
            LOG.error( "Digest data for three months required." );
            throw new InvalidInputException( "Digest data for three months required." );
        }

        if ( StringUtils.isEmpty( digestAggregate.getCompanyName() ) ) {
            LOG.error( "Company name for the digest not specified." );
            throw new InvalidInputException( "Company name for the digest not specified." );
        }

        if ( StringUtils.isEmpty( digestAggregate.getMonthUnderConcern() ) ) {
            LOG.error( "Month for the digest not specified." );
            throw new InvalidInputException( "Month for the digest not specified." );
        }

        if ( StringUtils.isEmpty( digestAggregate.getYearUnderConcern() ) ) {
            LOG.error( "Year for the digest not specified." );
            throw new InvalidInputException( "Year for the digest not specified." );
        }

        LOG.debug( "Sending Monthly digest/snapshot email to : " + digestAggregate.getRecipientMailId() );

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( digestAggregate.getRecipientMailId() );
        String monthYearForDisplay = StringUtils.capitalize( digestAggregate.getMonthUnderConcern() ) + " "
            + digestAggregate.getYearUnderConcern();
        FileContentReplacements messageSubjectReplacements = new FileContentReplacements();
        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        List<String> messageBodyReplacementsList = new ArrayList<>();


        // user ranking display toggle
        messageBodyReplacementsList.add( StringUtils.isEmpty( digestAggregate.getUserRankingHtmlRows() ) ? "display:none;" : "" );
        
        messageBodyReplacementsList.add( applicationWordPressSite );
        messageBodyReplacementsList.add( appNewLogoUrl );
        messageBodyReplacementsList.add( digestAggregate.getCompanyName() );
        messageBodyReplacementsList.add( monthYearForDisplay );

        // adding average rating score data
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 2 ).getAverageScoreRatingIcon() ) );
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 2 ).getAverageScoreRating() ) );
        messageBodyReplacementsList.add( StringUtils.defaultString( digestAggregate.getDigestList().get( 2 ).getUserCount() ) );
        messageBodyReplacementsList
            .add( StringUtils.upperCase( StringUtils.defaultString( digestAggregate.getDigestList().get( 2 ).getMonth() ) ) );

        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 1 ).getAverageScoreRatingIcon() ) );
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 1 ).getAverageScoreRating() ) );
        messageBodyReplacementsList.add( StringUtils.defaultString( digestAggregate.getDigestList().get( 1 ).getUserCount() ) );
        messageBodyReplacementsList
            .add( StringUtils.upperCase( StringUtils.defaultString( digestAggregate.getDigestList().get( 1 ).getMonth() ) ) );

        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 0 ).getAverageScoreRatingIcon() ) );
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 0 ).getAverageScoreRating() ) );
        messageBodyReplacementsList.add( StringUtils.defaultString( digestAggregate.getDigestList().get( 0 ).getUserCount() ) );
        messageBodyReplacementsList
            .add( StringUtils.upperCase( StringUtils.defaultString( digestAggregate.getDigestList().get( 0 ).getMonth() ) ) );

        messageBodyReplacementsList.add( StringUtils.defaultString( digestAggregate.getAvgRatingTxt() ) );


        // adding survey completion rate data
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 2 ).getSurveyCompletionRateIcon() ) );
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 2 ).getSurveyCompletionRate() ) );
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 2 ).getCompletedTransactions() ) );
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 2 ).getTotalTransactions() ) );
        messageBodyReplacementsList
            .add( StringUtils.upperCase( StringUtils.defaultString( digestAggregate.getDigestList().get( 2 ).getMonth() ) ) );

        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 1 ).getSurveyCompletionRateIcon() ) );
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 1 ).getSurveyCompletionRate() ) );
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 1 ).getCompletedTransactions() ) );
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 1 ).getTotalTransactions() ) );
        messageBodyReplacementsList
            .add( StringUtils.upperCase( StringUtils.defaultString( digestAggregate.getDigestList().get( 1 ).getMonth() ) ) );

        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 0 ).getSurveyCompletionRateIcon() ) );
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 0 ).getSurveyCompletionRate() ) );
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 0 ).getCompletedTransactions() ) );
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 0 ).getTotalTransactions() ) );
        messageBodyReplacementsList
            .add( StringUtils.upperCase( StringUtils.defaultString( digestAggregate.getDigestList().get( 0 ).getMonth() ) ) );

        messageBodyReplacementsList.add( StringUtils.defaultString( digestAggregate.getSurveyPercentageTxt() ) );


        // adding satisfaction rating data
        messageBodyReplacementsList.add( StringUtils.defaultString( digestAggregate.getDigestList().get( 2 ).getSpsIcon() ) );
        messageBodyReplacementsList.add( StringUtils.defaultString( digestAggregate.getDigestList().get( 2 ).getSps() ) );
        messageBodyReplacementsList.add( StringUtils.defaultString( digestAggregate.getDigestList().get( 2 ).getPromoters() ) );
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 2 ).getDetractors() ) );
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 2 ).getTotalCompletedReviews() ) );
        messageBodyReplacementsList
            .add( StringUtils.upperCase( StringUtils.defaultString( digestAggregate.getDigestList().get( 2 ).getMonth() ) ) );

        messageBodyReplacementsList.add( StringUtils.defaultString( digestAggregate.getDigestList().get( 1 ).getSpsIcon() ) );
        messageBodyReplacementsList.add( StringUtils.defaultString( digestAggregate.getDigestList().get( 1 ).getSps() ) );
        messageBodyReplacementsList.add( StringUtils.defaultString( digestAggregate.getDigestList().get( 1 ).getPromoters() ) );
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 1 ).getDetractors() ) );
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 1 ).getTotalCompletedReviews() ) );
        messageBodyReplacementsList
            .add( StringUtils.upperCase( StringUtils.defaultString( digestAggregate.getDigestList().get( 1 ).getMonth() ) ) );

        messageBodyReplacementsList.add( StringUtils.defaultString( digestAggregate.getDigestList().get( 0 ).getSpsIcon() ) );
        messageBodyReplacementsList.add( StringUtils.defaultString( digestAggregate.getDigestList().get( 0 ).getSps() ) );
        messageBodyReplacementsList.add( StringUtils.defaultString( digestAggregate.getDigestList().get( 0 ).getPromoters() ) );
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 0 ).getDetractors() ) );
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 0 ).getTotalCompletedReviews() ) );
        messageBodyReplacementsList
            .add( StringUtils.upperCase( StringUtils.defaultString( digestAggregate.getDigestList().get( 0 ).getMonth() ) ) );

        messageBodyReplacementsList.add( StringUtils.defaultString( digestAggregate.getStatisfactionRatingTxt() ) );


        // top ten ranked users HTML
        messageBodyReplacementsList.add( StringUtils.defaultString( digestAggregate.getUserRankingHtmlRows() ) );

        // email meta-data
        messageBodyReplacementsList.add( StringUtils.defaultString( digestAggregate.getRecipientMailId() ) );
        messageBodyReplacementsList.add( StringUtils.defaultString( digestAggregate.getRecipientMailId() ) );


        messageSubjectReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.DIGEST_MAIL_SUBJECT );
        messageSubjectReplacements.setReplacementArgs( Arrays.asList( monthYearForDisplay ) );

        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.DIGEST_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( messageBodyReplacementsList );

        LOG.debug( "Calling email sender to send mail" );
        emailSender.sendEmailWithSubjectAndBodyReplacements( emailEntity, messageSubjectReplacements, messageBodyReplacements,
            false, false );
        LOG.debug( "Successfully sent Monthly digest/snapshot mail" );

    }
    
    @Async
    @Override
    public void sendDigestErrorMailForCompany( String companyName, String stackTrace )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "sendDigestErrorMailForCompany() started" );
        if ( StringUtils.isEmpty( companyName ) ) {
            LOG.error( "Company Name is not Specified." );
            throw new InvalidInputException( "Company Name is not Specified." );
        } else if ( StringUtils.isEmpty( stackTrace ) ) {
            LOG.error( "Reason for failure not Specified." );
            throw new InvalidInputException( "Reason for failure not Specified." );
        }

        EmailEntity emailEntity = prepareEmailEntityForSendingEmail( applicationAdminEmail );

        FileContentReplacements messageSubjectReplacements = new FileContentReplacements();
        messageSubjectReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.DIGEST_ERROR_MAIL_SUBJECT );
        messageSubjectReplacements.setReplacementArgs( Arrays.asList( companyName ) );

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();
        messageBodyReplacements
            .setFileName( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.DIGEST_ERROR_MAIL_BODY );
        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, companyName, stackTrace ) );

        LOG.debug( "sendDigestErrorMailForCompany() finishing" );
        emailSender.sendEmailWithSubjectAndBodyReplacements( emailEntity, messageSubjectReplacements, messageBodyReplacements,
            false, false );
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
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SURVEY_CSV_UPLOAD_UNSUCCESSFUL_ADMIN_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();

        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_CSV_UPLOAD_UNSUCCESSFUL_ADMIN_BODY );

        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl,
            StringUtils.isEmpty( csvInfo.getFileName() ) ? CommonConstants.NOT_AVAILABLE : csvInfo.getFileName(),
            csvInfo.getUploadedDate() == null ? CommonConstants.NOT_AVAILABLE : df.format( csvInfo.getUploadedDate() ),
            StringUtils.isEmpty( csvInfo.get_id() ) ? CommonConstants.NOT_AVAILABLE : csvInfo.get_id(), errorMessage ) );

        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, true, false );
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
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SURVEY_CSV_UPLOAD_UNSUCCESSFUL_AGENT_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();

        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_CSV_UPLOAD_UNSUCCESSFUL_AGENT_BODY );

        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, csvInfo.getUploaderEmail().split( "@" )[0],
            StringUtils.isEmpty( csvInfo.getFileName() ) ? CommonConstants.NOT_AVAILABLE : csvInfo.getFileName(),
            csvInfo.getUploadedDate() == null ? CommonConstants.NOT_AVAILABLE : df.format( csvInfo.getUploadedDate() ),
            StringUtils.isEmpty( csvInfo.get_id() ) ? CommonConstants.NOT_AVAILABLE : csvInfo.get_id(), message ) );

        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, true, false );

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
        String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
            + EmailTemplateConstants.SURVEY_CSV_UPLOAD_SUCCESSFUL_SUBJECT;

        FileContentReplacements messageBodyReplacements = new FileContentReplacements();

        messageBodyReplacements.setFileName(
            EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_CSV_UPLOAD_SUCCESSFUL_BODY );

        messageBodyReplacements.setReplacementArgs( Arrays.asList( appLogoUrl, csvInfo.getUploaderEmail().split( "@" )[0],
            StringUtils.isEmpty( csvInfo.getFileName() ) ? CommonConstants.NOT_AVAILABLE : csvInfo.getFileName(),
            csvInfo.getUploadedDate() == null ? CommonConstants.NOT_AVAILABLE : df.format( csvInfo.getUploadedDate() ),
            results ) );

        emailSender.sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, true, false );
    }

}

// JIRA: SS-7: By RM02: EOC