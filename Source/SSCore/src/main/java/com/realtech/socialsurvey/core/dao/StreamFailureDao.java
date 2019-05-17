package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.EmailEntity;
import com.realtech.socialsurvey.core.entities.SendGridEventEntity;
import com.realtech.socialsurvey.core.entities.SmsEntity;
import com.realtech.socialsurvey.core.entities.UserEvent;
import com.realtech.socialsurvey.core.entities.ftp.FtpUploadRequest;
import com.realtech.socialsurvey.core.entities.integration.stream.FailedStreamMessage;
import com.realtech.socialsurvey.core.vo.SmsVO;

/**
 * Handles stream failure messages
 * @author nishit
 *
 */
public interface StreamFailureDao
{
    /**
     * Inserts emails that failed to stream
     * @param emailEntity
     * @return
     */
    public boolean insertFailedEmailMessage( EmailEntity emailEntity );


    /**
     * Inserts the failed click events into mongo
     * @param sendGridEventEntity
     * @return
     */
    public boolean insertFailedClickEvent( SendGridEventEntity sendGridEventEntity );


    /**
     * 
     * @return
     */
    public List<EmailEntity> getAllFailedStreamEmailMessages( int start, int batchSize );
    
    /**
     * 
     * @param start
     * @param batchSize
     * @return
     */
    public List<FailedStreamMessage> getAllFailedStreamSms( int start, int batchSize );

    /**
     * 
     * @param id
     */
    public void deleteFailedStreamMsg( String id );


    /**
     * 
     * @param id
     * @param updatedValue
     */
    public void updateRetryFailedForStreamMsg( String id, boolean updatedValue );


    /**
     * 
     * @param failedStreamMessage
     * @return
     */
    public <T> boolean insertFailedStreamMessage( FailedStreamMessage<T> failedStreamMessage );


    /**
     * 
     * @param start
     * @param batchSize
     * @return
     */
    public List<FailedStreamMessage<FtpUploadRequest>> getFtpFailedStreamMessages( int start, int batchSize );


    /**
     * 
     * @param id
     * @return
     */
    public FailedStreamMessage<FtpUploadRequest> getFailedStreamMessage( String id );

	/**
	 * 
	 * @param userEvent
	 * @return
	 */
    public boolean saveFailedUserEvent( UserEvent userEvent );


	/**
	 * @param smsEntity
	 * @return
	 */
	public boolean insertFailedSmsMessage(SmsEntity smsEntity);
	
	/**
	 * @param smsVO
	 * @return
	 */
	public boolean insertFailedSmsOfTopology( SmsVO smsVO );
}
