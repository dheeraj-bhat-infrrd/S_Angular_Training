package com.realtech.socialsurvey.core.services.mail;

import java.util.List;
import java.util.Set;

import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.DigestRequestData;
import com.realtech.socialsurvey.core.entities.EmailAttachment;
import com.realtech.socialsurvey.core.entities.MonthlyDigestAggregate;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SocialFeedsActionUpdate;
import com.realtech.socialsurvey.core.entities.SocialResponseObject;
import com.realtech.socialsurvey.core.entities.SurveyCsvInfo;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.ftp.FtpSurveyResponse;
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
        String loginName, boolean holdSendingMail, boolean hiddenSection )
        throws InvalidInputException, UndeliveredEmailException;


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
        String loginName, boolean hiddenSection ) throws InvalidInputException, UndeliveredEmailException;


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
     * Sends all survey related emails
     * @param companySettings
     * @param user
     * @param agentName
     * @param agentPhone
     * @param agentTitle
     * @param surveyLink
     * @param logoUrl
     * @param customerFirstName
     * @param customerLastName
     * @param customerEmailId
     * @param emailType
     * @param senderName
     * @param senderEmailAddress
     * @param mailSubject
     * @param mailBody
     * @param agentSettings
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    void sendSurveyRelatedMail(OrganizationUnitSettings companySettings, User user, String agentName, String agentFirstName, String agentPhone, String agentTitle,
                               String surveyLink, String logoUrl, String customerFirstName, String customerLastName, String customerEmailId,
                               String emailType, String senderName, String senderEmailAddress, String mailSubject, String mailBody,
                               AgentSettings agentSettings, long branchId, long regionId, String surveySourceId, long agentId,
                               long companyId, boolean sentFromCompany, String unsubscribedURL) throws InvalidInputException, UndeliveredEmailException;

    /**
     * Sends the survey complete admin mail
     * @param agentName
     * @param recipientMailId
     * @param customerDetail TODO
     * @param displayName
     * 
     * @throws InvalidInputException
     */
    public void sendSurveyCompletionMailToAdminsAndAgent( String agentName, String recipientName, String recipientMailId,
        String surveyDetail, String customerName, String rating, String logoUrl, String agentProfileLink,
        String customerDetail, String propertyAddress, String fbShareUrl, boolean isAddFbShare ) throws InvalidInputException, UndeliveredEmailException;


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
    public void sendContactUsMail( List<String> recipientEmailIds, String displayName, String senderName, String senderEmailId, String agentName, String agentEmail,
        String message ) throws InvalidInputException, UndeliveredEmailException;


    /**
     * Sends survey invitation mail
     * @param recipientMailId
     * @param subject
     * @param mailBody
     * @param emailId
     * @param name
     * @param agentId
     * @param companyId
     * @param mailType
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    public void sendSurveyRelatedMail(String recipientMailId, String subject, String mailBody, String emailId, String senderName,
                                      long agentId, long companyId, String mailType, boolean sentFromCompany ) throws InvalidInputException, UndeliveredEmailException;


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

    public void sendFatalExceptionEmail( String recipientMailId, String stackTrace )
        throws InvalidInputException, UndeliveredEmailException;


    public void sendEmailSendingFailureMail( String recipientMailId, String destinationMailId, String displayName,
        String stackTrace ) throws InvalidInputException, UndeliveredEmailException;

    public void sendSocialConnectMail( String recipientMailId, String displayName, String loginName, String account )
        throws InvalidInputException, UndeliveredEmailException;


    public void sendReportAbuseMail( String recipientMailId, String displayName, String agentName, String customerName,
        String customerEmail, String review, String reason, String reporterName, String reporterEmail )
        throws InvalidInputException, UndeliveredEmailException;


    public void sendSurveyReportMail( String recipientMailId, String displayName, String reason )
        throws InvalidInputException, UndeliveredEmailException;


    public void sendAccountDeletionMail( String recipientMailId, String displayName, String loginName )
        throws InvalidInputException, UndeliveredEmailException;


    public void sendCorruptDataFromCrmNotificationMail( String firstName, String lastName, String recipientEmail,
        List<EmailAttachment> attachments ) throws InvalidInputException, UndeliveredEmailException;


    public void sendRecordsNotUploadedCrmNotificationMail( String firstName, String lastName, String recipientEmail,
        List<EmailAttachment> attachments ) throws InvalidInputException, UndeliveredEmailException;


    public void sendAgentSurveyReminderMail( String recipientMailId, SurveyPreInitiation survey )
        throws InvalidInputException, UndeliveredEmailException;


    public void sendHelpMailToAdmin( String senderEmail, String senderName, String displayName, String mailSubject,
        String messageBodyText, String recipientMailId, List<EmailAttachment> attachments )
        throws InvalidInputException, UndeliveredEmailException;


    void sendZillowCallExceededMailToAdmin( int count ) throws InvalidInputException, UndeliveredEmailException;


    void sendReportBugMailToAdmin( String displayName, String errorMsg, String recipientMailId )
        throws InvalidInputException, UndeliveredEmailException;

    //SS-1435: Send survey details too
    void sendComplaintHandleMail( String recipientMailId, String customerName, String customerMailId, String agentName,
        String mood, String rating, String surveySourceId, String surveyDetail )
        throws InvalidInputException, UndeliveredEmailException;


    /**
     * @param firstName
     * @param lastName
     * @param recipientMailId
     * @param attachments
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    public void sendInvalidEmailsNotificationMail( String firstName, String lastName, String recipientMailId,
        List<EmailAttachment> attachments) throws InvalidInputException, UndeliveredEmailException;


    void forwardCustomerReplyMail( String recipientMailId, String subject, String mailBody, String senderName,
        String senderEmailAddress, String headers, String sendUsingDomain )
        throws InvalidInputException, UndeliveredEmailException;


    void sendReportBugMailToAdminForExceptionInBatch( String displayName, String batchName, String lastRunTime, String errorMsg,
        String exceptionStackTrace, String recipientMailId ) throws InvalidInputException, UndeliveredEmailException;


    void sendBillingReportMail( String firstName, String lastName, String recipientMailId,
        List<EmailAttachment> attachments) throws InvalidInputException, UndeliveredEmailException;


    void sendInvitationToSocialSurveyAdmin( String url, String recipientMailId, String name, String loginName )
        throws InvalidInputException, UndeliveredEmailException;


    void sendCustomReportMail( String recipientName, List<String> recipientMailIds, String subject,
        List<EmailAttachment> attachments) throws InvalidInputException, UndeliveredEmailException;


    public void sendZillowReviewComplaintHandleMail( String recipientMailId, String customerName, String rating,
        String reviewUrl ) throws InvalidInputException, UndeliveredEmailException;


    /**
     *
     * @param url
     * @param recipientMailId
     * @param firstName
     * @param lastName
     * @param planId
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    public void sendNewRegistrationInviteMail( String url, String recipientMailId, String firstName, String lastName,
        int planId ) throws InvalidInputException, UndeliveredEmailException;


    public void sendCompanyRegistrationStageMail( String firstName, String lastName, List<String> recipientMailIds,
        String registrationStage, String name, String details, boolean isImmediate )
        throws InvalidInputException, UndeliveredEmailException;


    /**
     * 
     * @param recipientName
     * @param recipientMailIds
     * @param subject
     * @param body
     * @param attachments
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    public void sendCustomMail( String recipientName, String recipientMailId, String subject, String body,
        List<EmailAttachment> attachments) throws InvalidInputException, UndeliveredEmailException;


    /**
     * 
     * @param url
     * @param recipientMailId
     * @param recipientName
     * @param emailToVerify
     * @param entityType
     * @param entityName
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    void sendEmailVerificationRequestMailToAdmin( String url, String recipientMailId, String recipientName,
        String emailToVerify, String entityName ) throws InvalidInputException, UndeliveredEmailException;


    /**
     * 
     * @param recipientMailId
     * @param recipientName
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    void sendEmailVerifiedNotificationMail( String recipientMailId, String recipientName )
        throws InvalidInputException, UndeliveredEmailException;


    /**
     * 
     * @param recipientMailId
     * @param recipientName
     * @param verifiedEmail
     * @param entityName
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    void sendEmailVerifiedNotificationMailToAdmin( String recipientMailId, String recipientName, String verifiedEmail,
        String entityName ) throws InvalidInputException, UndeliveredEmailException;


    void sendSocialMediaTokenExpiryEmail( String displayName, String recipientMailId, String updateConnectionUrl,
        String appLoginUrl, String socialMediaType ) throws InvalidInputException, UndeliveredEmailException;


    void sendPaymentRetriesFailedAlertEmailToAdmin( String recipientMailId, String displayName, String companyName, long companyId  )
        throws InvalidInputException, UndeliveredEmailException;


    void sendCancelSubscriptionRequestAlertMail( String recipientMailId, String displayName, String companyName )
        throws InvalidInputException, UndeliveredEmailException;


    void sendPaymentFailedAlertEmailToAdmin( String recipientMailId, String displayName, String companyName,  long companyId  )
        throws InvalidInputException, UndeliveredEmailException;
    /**
     * Send mail caught from web app
     * @param recipientMailId
     * @param stackTrace
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    void sendWebExceptionEmail( String recipientMailId, String stackTrace )
        throws InvalidInputException, UndeliveredEmailException;


    void sendMonthlyDigestMail( MonthlyDigestAggregate digestAggregate )
        throws InvalidInputException, UndeliveredEmailException;


    void sendNoTransactionAlertMail( List<String> recipientMailIds, String mailBody )
        throws InvalidInputException, UndeliveredEmailException;


    void sendHighVoulmeUnprocessedTransactionAlertMail( List<String> recipientMailIds, String mailBody )
        throws InvalidInputException, UndeliveredEmailException;


    void sendLessVoulmeOfTransactionReceivedAlertMail( List<String> recipientMailIds, String mailBody ) throws InvalidInputException,
        UndeliveredEmailException;


    void sendDigestErrorMailForCompany( DigestRequestData digestRequest, String stackTrace )
        throws InvalidInputException, UndeliveredEmailException;
    
    public void sendEmailToAdminForUnsuccessfulSurveyCsvUpload( SurveyCsvInfo csvInfo, String errorMessage ) throws InvalidInputException, UndeliveredEmailException;


    public void sendEmailToUploaderForUnsuccessfulSurveyCsvUpload( SurveyCsvInfo csvInfo, String message )
        throws InvalidInputException, UndeliveredEmailException;


    public void sendEmailToUploaderForSuccessfulSurveyCsvUpload( SurveyCsvInfo csvInfo, String results ) throws InvalidInputException, UndeliveredEmailException;


    public void sendSocialMonitorActionMail(SocialResponseObject socialResponseObject, SocialFeedsActionUpdate socialFeedsActionUpdate,
        String previousStatus, String currentStatus )
        throws InvalidInputException, UndeliveredEmailException;


    public void sendAbusiveNotifyMail(String source,String recipientMailId, String customerName, String customerMailId, String agentName,String agentMailId,
			String mood, String rating, String surveySourceId, String feedBack ,String surveyMarked )
			throws InvalidInputException, UndeliveredEmailException;

    
    public boolean sendUserAdditionMail( Set<String> recipients, String addedAdminName, String addedAdminEmailId, User addedUser,
        OrganizationUnitSettings agentSettings ) throws InvalidInputException, UndeliveredEmailException;


    public boolean sendUserDeletionMail( Set<String> recipients, String deletedAdminName, String deletedAdminEmailId, User deletedUser,
        OrganizationUnitSettings agentSettings ) throws InvalidInputException, UndeliveredEmailException;


    public void sendFtpProcessingErrorMailForCompany( Set<String> recipients, long companyId, String reason, String stackTrace, boolean isFromBatch, boolean sendOnlyToSocialSurveyAdmin )
        throws InvalidInputException, UndeliveredEmailException;


    /**
     * @param CompanyName
     * @param fileDate
     * @param fileName
     * @param ftpSurveyResponse
     * @param agentMailId
     * @param recipientMailId
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    public void sendFtpSuccessMail( String CompanyName, String fileDate, String fileName, FtpSurveyResponse ftpSurveyResponse,
        String agentMailId, String recipientMailId ) throws InvalidInputException, UndeliveredEmailException;




}