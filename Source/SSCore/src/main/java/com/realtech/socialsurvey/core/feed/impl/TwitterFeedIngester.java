package com.realtech.socialsurvey.core.feed.impl;

import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import com.realtech.socialsurvey.core.entities.TwitterToken;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.feed.SocialNetworkDataProcessor;


@Component
@Scope ( value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TwitterFeedIngester implements Runnable
{

    private static final Logger LOG = LoggerFactory.getLogger( TwitterFeedIngester.class );

    @Resource
    @Qualifier ( "twitterFeed")
    private SocialNetworkDataProcessor<Status, TwitterToken> processor;

    private TwitterToken token;
    private String collectionName;
    private long iden;


    public void setToken( TwitterToken token )
    {
        this.token = token;
    }


    public void setCollectionName( String collectionName )
    {
        this.collectionName = collectionName;
    }


    public void setIden( long iden )
    {
        this.iden = iden;
    }


    @Override
    public void run()
    {
        LOG.info( "Starting the ingestion thread for twitter for " + collectionName + " with iden: " + iden );
        try {
            processor.preProcess( iden, collectionName, token );
            List<Status> statuses = processor.fetchFeed( iden, collectionName, token );
            boolean anyRecordInserted = processor.processFeed( statuses, collectionName );
            processor.postProcess( iden, collectionName, anyRecordInserted );
        } catch ( NonFatalException e ) {
            LOG.error( "Exception caught while processesing tweets for " + collectionName + " with iden: " + iden, e );
            e.printStackTrace();
        } finally {
            LOG.info( "Done fetching twitter tweets for " + collectionName + " with iden: " + iden );
        }
    }
}