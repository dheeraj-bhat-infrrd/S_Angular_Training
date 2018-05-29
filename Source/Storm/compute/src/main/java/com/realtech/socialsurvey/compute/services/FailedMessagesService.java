package com.realtech.socialsurvey.compute.services;

import com.realtech.socialsurvey.compute.entities.EmailMessage;
import com.realtech.socialsurvey.compute.entities.ReportRequest;
import com.realtech.socialsurvey.compute.entities.response.SocialResponseObject;

/**
 * Operations on failed messages while processing
 * @author nishit
 *
 */
public interface FailedMessagesService
{

    /**
     * Inserts a permanently failed message. These message are not supposed to be retried
     * @param emailMessage
     * @param thrw
     */
    public void insertPermanentlyFailedEmailMessage(EmailMessage emailMessage, Throwable thrw);
    
    /**
     * Inserts a temporary failed message
     * @param emailMessage
     */
    public void insertTemporaryFailedEmailMessage(EmailMessage emailMessage);

    /**
     * Inserts failed report request. These request will not be retried
     * @param reportRequest
     * @param thrw
     */
    public void insertPermanentlyFailedReportRequest(ReportRequest reportRequest, Throwable thrw);

    /**
     * Inserts a temporary report request
     * @param reportRequest
     */
    void insertTemporaryFailedReportRequest(ReportRequest reportRequest);

    /*Inserts permanent failed socialPost*/
    void insertPermanentlyFailedSocialPost( SocialResponseObject<?> post, Throwable thrw );

    /* Inserts temporary failed socialPost */
    void insertTemporaryFailedSocialPost(SocialResponseObject<?> post);
    /**
     * Deletes failed email message
     * @param emailMessage
     */
    int deleteFailedEmailMessage(String emailMessage);

    int updateFailedEmailMessageRetryCount(String randomUUID);

    int deleteFailedSocialPost(String postId);

    int updateFailedSocialPostRetryCount(String postId);
}
