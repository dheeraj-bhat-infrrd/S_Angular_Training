package com.realtech.socialsurvey.core.feed.impl;

import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.entities.GooglePlusPost;
import com.realtech.socialsurvey.core.entities.GoogleToken;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.feed.SocialNetworkDataProcessor;


@Component
@Scope ( value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GoogleFeedIngester implements Runnable
{

    private static final Logger LOG = LoggerFactory.getLogger( GoogleFeedIngester.class );

    @Resource
    @Qualifier ( "googleFeed")
    private SocialNetworkDataProcessor<GooglePlusPost, GoogleToken> processor;

    private GoogleToken token;
    private String collectionName;
    private long iden;


    public void setToken( GoogleToken token )
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
        LOG.info( "Starting the ingestion thread for google for " + collectionName + " with iden: " + iden );
        try {
            processor.preProcess( iden, collectionName, token );
            List<GooglePlusPost> statuses = processor.fetchFeed( iden, collectionName, token );
            boolean anyRecordInserted = processor.processFeed( iden, statuses, collectionName, token );
            processor.postProcess( iden, collectionName, anyRecordInserted );
        } catch ( NonFatalException e ) {
            LOG.error( "Exception caught while processesing google for " + collectionName + " with iden: " + iden, e );
            e.printStackTrace();
        } finally {
            LOG.info( "Done fetching google plus posts for " + collectionName + " with iden: " + iden );
        }
    }
}