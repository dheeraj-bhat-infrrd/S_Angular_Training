package com.realtech.socialsurvey.stream.services.impl;

import com.realtech.socialsurvey.stream.common.FailedMessageConstants;
import com.realtech.socialsurvey.stream.entities.FailedEmailMessage;
import com.realtech.socialsurvey.stream.repositories.FailedEmailMessageRepository;
import com.realtech.socialsurvey.stream.services.FailedMessagesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


    @Override public List<FailedEmailMessage> getFailedEmailMessages( String filter )
    {
        LOG.debug( "Fetching failed email messages with filter {}", filter );
        List<FailedEmailMessage> failedMessages = null;
        if ( filter.equalsIgnoreCase( MESSAGE_FAILURE_TEMPORARY ) ) {
            LOG.debug( "Getting temporary failed messages" );
            failedMessages = failedEmailMessageRepository
                .findByMessageTypeAndPermanentFailure( FailedMessageConstants.EMAIL_MESSAGES, false );
        } else if ( filter.equalsIgnoreCase( MESSAGE_FAILURE_PERMANENT ) ) {
            LOG.debug( "Getting permanently failed messages" );
            failedMessages = failedEmailMessageRepository
                .findByMessageTypeAndPermanentFailure( FailedMessageConstants.EMAIL_MESSAGES, true );
        } else {
            LOG.debug( "Getting all failed messages" );
            failedMessages = failedEmailMessageRepository.findByMessageType( FailedMessageConstants.EMAIL_MESSAGES );
        }
        return failedMessages;
    }
}
