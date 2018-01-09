package com.realtech.socialsurvey.stream.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.realtech.socialsurvey.stream.entities.DummyFeed;
import com.realtech.socialsurvey.stream.repositories.DummyFeedRepository;
import com.realtech.socialsurvey.stream.services.DummyFeedService;

@Service
public class DummyFeedServiceImpl implements DummyFeedService
{

    private static final Logger LOG = LoggerFactory.getLogger( DummyFeedServiceImpl.class );
    
    private DummyFeedRepository dummyFeedRepository;
    
    @Autowired
    public void setDummyFeedRepository( DummyFeedRepository dummyFeedRepository )
    {
        this.dummyFeedRepository = dummyFeedRepository;
    }

    @Override
    public DummyFeed insertDummyFeed( DummyFeed feed )
    {
        LOG.info( "Inserting a dummy feed" );
        LOG.debug( "Feed {}", feed );
        return dummyFeedRepository.insert( feed );
    }

}
