package com.realtech.socialsurvey.core.services.stream.impl;

import com.realtech.socialsurvey.core.entities.SendGridEventEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.realtech.socialsurvey.core.dao.StreamFailureDao;
import com.realtech.socialsurvey.core.entities.EmailEntity;
import com.realtech.socialsurvey.core.services.stream.StreamMessagesService;

/**
 * Implementation for streaming messages
 * @author nishit
 *
 */
@Service
public class StreamMessagesServiceImpl implements StreamMessagesService
{
    
    private static final Logger LOG = LoggerFactory.getLogger( StreamMessagesServiceImpl.class );
    
    private StreamFailureDao streamFailureDao;
    
    @Autowired
    public void setStreamFailureDao( StreamFailureDao streamFailureDao )
    {
        this.streamFailureDao = streamFailureDao;
    }



    @Override
    public boolean saveFailedStreamEmailMessages( EmailEntity emailEntity )
    {
        LOG.debug( "Saving failed email message" );
        return streamFailureDao.insertFailedEmailMessage( emailEntity );
    }



    @Override
    public boolean saveFailedStreamClickEvent(SendGridEventEntity sendGridEventEntity)
    {
        LOG.debug( "Saving failed click event" );
        return streamFailureDao.insertFailedClickEvent( sendGridEventEntity );
    }

}
