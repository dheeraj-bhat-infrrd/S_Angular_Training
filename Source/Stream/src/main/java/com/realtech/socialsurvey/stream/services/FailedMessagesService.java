package com.realtech.socialsurvey.stream.services;

import com.realtech.socialsurvey.stream.entities.FailedEmailMessage;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;


/**
 * Created by nishit on 04/01/18.
 */
public interface FailedMessagesService
{    
    List<FailedEmailMessage> getPaginatedFailedEmailMessages(String filter, Pageable pageable);
    
    FailedEmailMessage getFailedEmailMessageById(ObjectId id);
    	
    List<FailedEmailMessage> getFailedEmailMessagesByCompanyId(long companyId, Pageable pageable);
    
    List<FailedEmailMessage> getFailedEmailMessagesByRecipients(List<String> recipients, Pageable pageable);

	void processFailedEmailMessaged(String filter) throws InterruptedException, ExecutionException, TimeoutException;
    
}
