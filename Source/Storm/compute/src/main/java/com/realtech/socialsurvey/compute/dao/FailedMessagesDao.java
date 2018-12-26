package com.realtech.socialsurvey.compute.dao;

import com.realtech.socialsurvey.compute.entities.*;


/**
 * Failed messages database operations
 * @author nishit
 *
 */
public interface FailedMessagesDao
{
    /** 
     * Inserts a failed email message
     * @param failedEmailMessage
     * @return
     */
    public boolean insertFailedEmailMessages( FailedEmailMessage failedEmailMessage );

    /**
     * Inserts a Failed Social Post
     * @param failedSocialPost
     * @return
     */
    boolean insertFailedSocialPost(FailedSocialPost failedSocialPost );

    /**
     * Inserts failed report request
     * @param failedReportRequest
     * @return
     */
    boolean insertFailedReportRequest(FailedReportRequest failedReportRequest);

    /**
     * Deletes failed email message
     * @param randomUUID
     */
    int deleteFailedEmailMessage(String randomUUID);

    /**
     * Updated failed email message retry count
     * @param randomUUID
     */
    int updatedFailedEmailMessageRetryCount(String randomUUID);

     /**
     * Deletes temporary failed social post
     * @param postId
     * @return
     */
    int deleteFailedSocialPost(String postId);

    /**
     * Updated retryCount of temporary failed social post
     * @param postId
     * @return
     */
    int updateFailedSocialPostRetryCount(String postId);

    /**
    * 
    * @param unsavedEvent
    * @return
    */
   public boolean insertUnsavedUserEvent( UnsavedUserEvent unsavedEvent );

}
