package com.realtech.socialsurvey.core.services.mail;

import java.util.Map;

import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


/**
 * Services for sending mails via application
 */
public interface EmailServices
{

	/**
	 * Sends registration invitation mail
	 * @param url
	 * @param recipientMailId
	 * @param firstname
	 * @param lastName
	 * @throws InvalidInputException
	 * @throws UndeliveredEmailException
	 */
    public void sendRegistrationInviteMail( String url, String recipientMailId, String firstname, String lastName )
        throws InvalidInputException, UndeliveredEmailException;


    /**
     * Sends reset password link
     * @param url
     * @param recipientMailId
     * @param name
     * @param loginName
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    public void sendResetPasswordEmail( String url, String recipientMailId, String name, String loginName )
        throws InvalidInputException, UndeliveredEmailException;


    // JIRA SS-42 by RM05 : BOC
    /**
     * Sends a link to new user to complete registration.
     * 
     * @param url
     * @param recipientMailId
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    public void sendRegistrationCompletionEmail( String url, String recipientMailId, String name, String profileName,
        String loginName ) throws InvalidInputException, UndeliveredEmailException;


    /**
     * Sends subscription charge unsuccessful email
     * 
     * @param recipientMailId
     * @param name
     * @param retryDays
     * @throws InvalidInputException
     */
    public void sendSubscriptionChargeUnsuccessfulEmail( String recipientMailId, String name, String retryDays )
        throws InvalidInputException, UndeliveredEmailException;


    // JIRA SS-42 by RM05 : EOC

    /**
     * Sends the verification mail
     * 
     * @param url
     * @param recipientMailId
     * @param recipientName
     * @throws InvalidInputException
     */
    public void sendVerificationMail( String url, String recipientMailId, String recipientName, String profileName,
        String loginName ) throws InvalidInputException, UndeliveredEmailException;


    /**
     * Sends the email verification mail
     * 
     * @param url
     * @param recipientMailId
     * @param recipientName
     * @throws InvalidInputException
     */
    public void sendEmailVerificationMail( String url, String recipientMailId, String recipientName )
        throws InvalidInputException, UndeliveredEmailException;


    /**
     * Sends the retry charge email
     * 
     * @param recipientMailId
     * @param displayName
     * @param retries
     * @throws InvalidInputException
     */
    public void sendRetryChargeEmail( String recipientMailId, String displayName, String loginName )
        throws InvalidInputException, UndeliveredEmailException;


    /**
     * Sends the retry exhausted mail
     * 
     * @param recipientMailId
     * @param displayName
     * @throws InvalidInputException
     */
    public void sendRetryExhaustedEmail( String recipientMailId, String displayName, String loginName )
        throws InvalidInputException, UndeliveredEmailException;


    /**
     * Sends the account disabled mail
     * 
     * @param recipientMailId
     * @param displayName
     * @throws InvalidInputException
     */
    public void sendAccountDisabledMail( String recipientMailId, String displayName, String loginName )
        throws InvalidInputException, UndeliveredEmailException;


    /**
     * Sends account upgrade mail
     * @param recipientMailId
     * @param displayName
     * @param loginName
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    public void sendAccountUpgradeMail( String recipientMailId, String displayName, String loginName )
        throws InvalidInputException, UndeliveredEmailException;


    /**
     * Sends the survey complete mail
     * 
     * @param recipientMailId
     * @param displayName
     * @param agentName
     * @throws InvalidInputException
     */
    public void sendDefaultSurveyCompletionMail( String recipientMailId, String displayName, String agentName,
        String agentEmail, String agentProfileName, String logoUrl, long agentId ) throws InvalidInputException, UndeliveredEmailException;


    /**
     * Sends the survey reminder mail
     * 
     * @param recipientMailId
     * @param displayName
     * @param agentName
     * @throws InvalidInputException
     */
    public void sendDefaultSurveyReminderMail( String recipientMailId, String logoUrl, String displayName, String agentName,
        String agentEmailId, String link, String agentPhone, String agentTitle, String companyName )
        throws InvalidInputException, UndeliveredEmailException;


    /**
     * Sends the survey complete admin mail
     * 
     * @param recipientMailId
     * @param displayName
     * @param agentName
     * @throws InvalidInputException
     */
    public void sendSurveyCompletionMailToAdminsAndAgent( String agentName, String recipientMailId, String surveyDetail,
        String customerName, String rating ) throws InvalidInputException, UndeliveredEmailException;


    /**
     * Sends the social post reminder mail
     * 
     * @param recipientMailId
     * @param displayName
     * @param agentName
     * @throws InvalidInputException
     */
    public void sendDefaultSocialPostReminderMail( String recipientMailId, String agentPhone, String agentTitle,
        String companyName, String displayName, String agentName, String links, String logoUrl ) throws InvalidInputException,
        UndeliveredEmailException;


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
    public void sendContactUsMail( String recipientEmailId, String displayName, String senderName, String senderEmailId,
        String message ) throws InvalidInputException, UndeliveredEmailException;


    /**
     * Sends survey invitation mail
     * 
     * @param recipientMailId
     * @param subject
     * @param mailBody
     * @param firstName
     * @param lastName
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    public void sendSurveyInvitationMail( String recipientMailId, String subject, String mailBody, String emailId, String name, long agentId )
        throws InvalidInputException, UndeliveredEmailException;


    /**
     * Sends account blocking mail when retries fail
     * 
     * @param recipientMailId
     * @param displayName
     * @throws UndeliveredEmailException
     * @throws InvalidInputException
     */
    public void sendAccountBlockingMail( String recipientMailId, String displayName, String loginName )
        throws InvalidInputException, UndeliveredEmailException;


    /**
     * Send mail to customer when his account is reactivated
     * 
     * @param recipientMailId
     * @param displayName
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    public void sendAccountReactivationMail( String recipientMailId, String displayName, String loginName )
        throws InvalidInputException, UndeliveredEmailException;


    /**
     * Sends a subscription revision mail to the user
     * 
     * @param recipientMailId
     * @param name
     * @param oldAmount
     * @param revisedAmount
     * @param numOfUsers
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    public void sendSubscriptionRevisionMail( String recipientMailId, String name, String oldAmount, String revisedAmount,
        String numOfUsers ) throws InvalidInputException, UndeliveredEmailException;


    /**
     * Sends manual registration link
     * 
     * @param recipientId
     * @param firstName
     * @param lastName
     * @param link
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    public void sendManualRegistrationLink( String recipientId, String firstName, String lastName, String link )
        throws InvalidInputException, UndeliveredEmailException;


    public void sendSurveyReminderMail( String recipientMailId, String subject, String mailBody, String name, String emailId )
        throws InvalidInputException, UndeliveredEmailException;


    public void sendDefaultSurveyInvitationMail( String recipientMailId, String logoUrl, String displayName, String agentName,
        String link, String agentEmailId, String agentSignature, String companyName, String surveyInitiatedOn,
        String currentYear, String fullAddress, long agentId ) throws InvalidInputException, UndeliveredEmailException;


    public void sendDefaultSurveyInvitationMailByCustomer( String recipientMailId, String displayName, String agentName,
        String link, String agentEmailId, long agentId ) throws InvalidInputException, UndeliveredEmailException;


    public void sendSurveyInvitationMailByCustomer( String recipientMailId, String subject, String mailBody, String emailId,
        String name, long agentId ) throws InvalidInputException, UndeliveredEmailException;


    public void sendFatalExceptionEmail( String recipientMailId, String stackTrace ) throws InvalidInputException,
        UndeliveredEmailException;


    public void sendEmailSendingFailureMail( String recipientMailId, String destinationMailId, String displayName,
        String stackTrace ) throws InvalidInputException, UndeliveredEmailException;


    public void sendDefaultSurveyRestartMail( String recipientMailId, String logoUrl, String displayName, String agentName,
        String link, String agentEmailId, String agentSignature, long agentId ) throws InvalidInputException, UndeliveredEmailException;


    public void sendSocialConnectMail( String recipientMailId, String displayName, String loginName, String account )
        throws InvalidInputException, UndeliveredEmailException;


    public void sendReportAbuseMail( String recipientMailId, String displayName, String agentName, String customerName,
        String customerEmail, String review, String reason, String reporterName, String reporterEmail )
        throws InvalidInputException, UndeliveredEmailException;


    public void sendSurveyReportMail( String recipientMailId, String displayName, String reason ) throws InvalidInputException,
        UndeliveredEmailException;


    public void sendAccountDeletionMail( String recipientMailId, String displayName, String loginName )
        throws InvalidInputException, UndeliveredEmailException;


    public void sendCorruptDataFromCrmNotificationMail( String firstName, String lastName, String recipientEmail,
        Map<String, String> attachmentsDetails ) throws InvalidInputException, UndeliveredEmailException;


    public void sendRecordsNotUploadedCrmNotificationMail( String firstName, String lastName, String recipientEmail,
        Map<String, String> attachmentsDetails ) throws InvalidInputException, UndeliveredEmailException;


    public void sendAgentSurveyReminderMail( String recipientMailId, SurveyPreInitiation survey ) throws InvalidInputException,
        UndeliveredEmailException;


    public void sendHelpMailToAdmin( String senderEmail, String senderName, String displayName, String mailSubject,
        String messageBodyText, String recipientMailId, Map<String, String> attachmentsDetails ) throws InvalidInputException,
        UndeliveredEmailException;


    /**
     * Method to send survey reminder when the resend button is manually clicked
     * 
     * @param user
     * @param agentName
     * @param agentPhone
     * @param agentTitle
     * @param companyName
     * @param survey
     * @param surveyLink
     */
    public void sendManualSurveyReminderMail( OrganizationUnitSettings companySettings, User user, String agentName,
        String agentEmailId, String agentPhone, String agentTitle, String companyName, SurveyPreInitiation survey,
        String surveyLink, String logoUrl );


    void sendZillowCallExceededMailToAdmin( int count ) throws InvalidInputException, UndeliveredEmailException;


    void sendReportBugMailToAdmin( String displayName, String errorMsg, String recipientMailId ) throws InvalidInputException,
        UndeliveredEmailException;


    void sendDefaultSurveyCompletionUnpleasantMail( String recipientMailId, String displayName, String agentName,
        String agentEmail, String companyName, String logoUrl, long agentId ) throws InvalidInputException, UndeliveredEmailException;


    void sendComplaintHandleMail( String recipientMailId, String customerName, String customerMailId, String mood, String rating )
        throws InvalidInputException, UndeliveredEmailException;


    /**
     * @param firstName
     * @param lastName
     * @param recipientMailId
     * @param attachmentsDetails
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    public void sendInvalidEmailsNotificationMail( String firstName, String lastName, String recipientMailId,
        Map<String, String> attachmentsDetails ) throws InvalidInputException, UndeliveredEmailException;
}