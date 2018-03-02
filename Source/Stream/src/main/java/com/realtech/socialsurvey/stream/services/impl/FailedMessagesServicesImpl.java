package com.realtech.socialsurvey.stream.services.impl;

import com.realtech.socialsurvey.stream.common.FailedMessageConstants;
import com.realtech.socialsurvey.stream.entities.FailedEmailMessage;
import com.realtech.socialsurvey.stream.repositories.FailedEmailMessageRepository;
import com.realtech.socialsurvey.stream.services.FailedMessagesService;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by nishit on 04/01/18.
 */
@Service public class FailedMessagesServicesImpl implements FailedMessagesService
{
    private static final Logger LOG = LoggerFactory.getLogger( FailedMessagesServicesImpl.class );

    private static final String MESSAGE_FAILURE_TEMPORARY = "temp";
    private static final String MESSAGE_FAILURE_PERMANENT = "perm";
    private FailedEmailMessageRepository failedEmailMessageRepository;
    
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
	
	   
}
