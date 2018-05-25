package com.realtech.socialsurvey.compute.services;

import com.realtech.socialsurvey.compute.entities.EmailMessage;
import com.realtech.socialsurvey.compute.entities.ReportRequest;
import com.realtech.socialsurvey.compute.entities.SocialPost;
import com.realtech.socialsurvey.compute.entities.UserEvent;

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
     * Insert failed social post
     * @param post
     * @param thrw
     */
    public void insertPermanentlyFailedSocialPost( SocialPost post, Throwable thrw );

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

    /**
     * Deletes failed email message
     * @param emailMessage
     */
    int deleteFailedEmailMessage(String emailMessage);

    int updateFailedEmailMessageRetryCount(String randomUUID);

    /**
     * 
     * @param userEvent
     * @param willRetry
     * @param retryCount
     * @param wasRetrySuccessful
     * @param isPermanentlyFailed
     * @param thrw
     * @return
     */
    public boolean insertUnsavedUserEvent( UserEvent userEvent, boolean willRetry, int retryCount, boolean wasRetrySuccessful,
        boolean isPermanentlyFailed, Throwable thrw );
}
