package com.realtech.socialsurvey.core.services.stream;

import java.util.List;

import com.realtech.socialsurvey.core.entities.EmailEntity;
import com.realtech.socialsurvey.core.entities.SendGridEventEntity;
import com.realtech.socialsurvey.core.entities.UserEvent;
import com.realtech.socialsurvey.core.entities.ftp.FtpUploadRequest;
import com.realtech.socialsurvey.core.entities.integration.stream.FailedStreamMessage;

/**
 * Handles stream messages
 * @author nishit
 *
 */
public interface StreamMessagesService
{

    /**
     * Saves email entity that failed streaming
     * @param emailEntity
     * @return
     */
    public boolean saveFailedStreamEmailMessages( EmailEntity emailEntity );


    /**
     * Saves click events that failed streaming
     * @param sendGridEventEntity
     * @return
     */
    public boolean saveFailedStreamClickEvent( SendGridEventEntity sendGridEventEntity );


    /**
     * 
     * @return
     */
    public List<EmailEntity> getAllFailedStreamEmailMsgs( int start, int batchSize );


    /**
     * 
     * @param id
     */
    public void deleteFailedStreamMsg( String id );


    /**
     * 
     * @param id
     */
    public void updateRetryFailedForStreamMsg( String id );


    /**
     * 
     * @param failedStreamMessage
     * @return
     */
    public <T> boolean saveFailedStreamMessage( FailedStreamMessage<T> failedStreamMessage );


    /**
     * 
     */
    public void startFailedStreamMessagesRetry();


    /**
     * 
     * @param start
     * @param batchSize
     * @return
     */
    public List<FailedStreamMessage<FtpUploadRequest>> getFtpFailedStreamMessages( int start, int batchSize );


	/**
	 * 
	 * @param userEvent
	 * @return
	 */
    public boolean saveStreamUserEvent( UserEvent userEvent );
    
    
    @SuppressWarnings ( "rawtypes")
    public FailedStreamMessage getFailedStreamMsg( String id );
}
