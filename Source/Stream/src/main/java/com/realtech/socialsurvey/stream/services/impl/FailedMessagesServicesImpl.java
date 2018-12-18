package com.realtech.socialsurvey.stream.services.impl;

import com.realtech.socialsurvey.stream.common.FailedMessageConstants;
import com.realtech.socialsurvey.stream.entities.FailedEmailMessage;
import com.realtech.socialsurvey.stream.entities.FailedSocialPost;
import com.realtech.socialsurvey.stream.repositories.FailedEmailMessageRepository;
import com.realtech.socialsurvey.stream.services.FailedMessagesService;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * Created by nishit on 04/01/18.
 */
@Service public class FailedMessagesServicesImpl implements FailedMessagesService
{
    private static final Logger LOG = LoggerFactory.getLogger( FailedMessagesServicesImpl.class );

    private static final String MESSAGE_FAILURE_TEMPORARY = "temp";
    private static final String MESSAGE_FAILURE_PERMANENT = "perm";
    private FailedEmailMessageRepository failedEmailMessageRepository;
    private KafkaTemplate<String, String> kafkaEmailMessageTemplate;
    
    
    @Autowired
    @Qualifier ( "emailMessageTemplate")
    public void setKafkaEmailMessageTemplate( KafkaTemplate<String, String> kafkaEmailMessageTemplate )
    {
        this.kafkaEmailMessageTemplate = kafkaEmailMessageTemplate;
    }
    
    @Autowired public void setFailedEmailMessageRepository( FailedEmailMessageRepository failedEmailMessageRepository )
    {
        this.failedEmailMessageRepository = failedEmailMessageRepository;
    }
    
	@Override
	public List<FailedEmailMessage> getPaginatedFailedEmailMessages(String filter, Pageable pageable) {
		LOG.debug("Fetching 10 failed email messages with filter {}", filter);
		List<FailedEmailMessage> failedMessages = null;
		if (filter.equalsIgnoreCase(MESSAGE_FAILURE_TEMPORARY)) {
			LOG.debug("Getting 10 temporary failed messages");
			failedMessages = failedEmailMessageRepository
					.findByMessageTypeAndPermanentFailure(FailedMessageConstants.EMAIL_MESSAGES, false, pageable);
		} else if (filter.equalsIgnoreCase(MESSAGE_FAILURE_PERMANENT)) {
			LOG.debug("Getting 10 permanently failed messages");
			failedMessages = failedEmailMessageRepository
					.findByMessageTypeAndPermanentFailure(FailedMessageConstants.EMAIL_MESSAGES, true, pageable);
		} else {
			LOG.debug("Getting 10 failed messages");
			failedMessages = failedEmailMessageRepository.findByMessageType(FailedMessageConstants.EMAIL_MESSAGES,
					pageable);
		}
		return failedMessages;
	}
	
	@Override
	public FailedEmailMessage getFailedEmailMessageById(ObjectId id) {
		LOG.debug("Fetching failed email message with object id {}", id);
		FailedEmailMessage failedEmailMessage = failedEmailMessageRepository.findById(id);
		if(failedEmailMessage == null) {
        	LOG.debug("The record with id {} is not present", id);
        }
		return failedEmailMessage;
	}

	@Override
	public List<FailedEmailMessage> getFailedEmailMessagesByCompanyId(long companyId, Pageable pageable) {
		LOG.debug("Fetching failed email messages with company id {}", companyId);
		if (companyId <= 0) {
			LOG.debug("Invalid Company Id");
		}
		List<FailedEmailMessage> failedMessages = failedEmailMessageRepository.findByDataCompanyId(companyId, pageable);
		if (failedMessages == null || failedMessages.isEmpty()) {
			LOG.debug("No record found for company id {}", companyId);
		}
		return failedMessages;

	}

	@Override
	public List<FailedEmailMessage> getFailedEmailMessagesByRecipients(List<String> recipients, Pageable pageable) {
		LOG.debug("Fetching failed email messages with  recipients {}", recipients);
		List<FailedEmailMessage> failedMessages = new ArrayList<>();
		List<FailedEmailMessage> failedEmailMessagesToAdd;
		for (String recipient : recipients) {
			failedEmailMessagesToAdd = failedEmailMessageRepository.findByDataRecipients(recipient, pageable);
			if (failedEmailMessagesToAdd == null || failedEmailMessagesToAdd.isEmpty()) {
				LOG.debug("No record found for recipient {}", recipient);
			}
			failedMessages.addAll(failedEmailMessagesToAdd);

		}
		if (failedMessages.isEmpty()) {
			LOG.debug("No record found for recipients {}", recipients);
		} else {
			for (int i = 0; i < failedMessages.size(); i++) {
				for (int j = i + 1; j < failedMessages.size(); j++) {
					if (failedMessages.get(i).getId().equals(failedMessages.get(j).getId())) {
						failedMessages.remove(j);
						j--;
					}
				}
			}
		}
		return failedMessages;
	}
	
	
	@Override
	public void processFailedEmailMessaged(String filter) throws InterruptedException, ExecutionException, TimeoutException 
	{

		LOG.info("processFailedEmailMessaged started with filter {} ", filter);

		int pageNum = 0;
		List<FailedEmailMessage> failedEmailMessages = null;
		Pageable numberOfRecords = null;
		int batchNo = 1;
		do {
			LOG.info("Fetching data for batch {}", batchNo );
			numberOfRecords = new PageRequest(pageNum, 500);
			if (filter.equalsIgnoreCase(MESSAGE_FAILURE_TEMPORARY)) {
				failedEmailMessages = failedEmailMessageRepository.findByMessageTypeAndPermanentFailure(
						FailedMessageConstants.EMAIL_MESSAGES, false, numberOfRecords);
			} else if (filter.equalsIgnoreCase(MESSAGE_FAILURE_PERMANENT)) {
				LOG.debug("Getting 10 permanently failed messages");
				failedEmailMessages = failedEmailMessageRepository.findByMessageTypeAndPermanentFailure(
						FailedMessageConstants.EMAIL_MESSAGES, true, numberOfRecords);
			}
			if(failedEmailMessages != null) {
				LOG.info("Fetch message count {} for batch {}" , failedEmailMessages.size() , batchNo++ );
				for (FailedEmailMessage failedMessage : failedEmailMessages) {
					if (failedMessage.getData() != null) {
						failedMessage.getData().setIsRetried(true);
						failedMessage.setMessageType("EMAIL_MESSAGES_PROCESSED"); 
						LOG.info("Processing message with id {}", failedMessage.getData().getRandomUUID());
						kafkaEmailMessageTemplate.send(new GenericMessage<>(failedMessage.getData())).get(60,
								TimeUnit.SECONDS);
						failedEmailMessageRepository.save(failedMessage);
					}
				}
			}else {
				LOG.info("No more message to process");
			}
			
		} while (failedEmailMessages != null && failedEmailMessages.size() == 500);
		LOG.info( "processFailedEmailMessaged finished with filter {} ", filter );
	}
	
	   
}
