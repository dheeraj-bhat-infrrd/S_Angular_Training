package com.realtech.socialsurvey.core.feed.impl;

import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.entities.FacebookToken;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.feed.SocialNetworkDataProcessor;
import facebook4j.Post;


@Component
@Scope ( value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FacebookFeedIngester implements Runnable
{

    private static final Logger LOG = LoggerFactory.getLogger( FacebookFeedIngester.class );

    @Resource
    @Qualifier ( "facebookFeed")
    private SocialNetworkDataProcessor<Post, FacebookToken> processor;

    private FacebookToken token;
    private String collectionName;
    private long iden;


    public void setToken( FacebookToken token )
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
        LOG.info( "Starting the ingestion thread for facebook for " + collectionName + " with iden: " + iden );
        try {
        	LOG.debug("FacebookFeedIngester: "+this.toString());
            processor.preProcess( iden, collectionName, token );
            List<Post> posts = processor.fetchFeed( iden, collectionName, token );
            boolean anyRecordInserted = processor.processFeed( iden, posts, collectionName, token );
            processor.postProcess( iden, collectionName, anyRecordInserted );
        } catch ( NonFatalException e ) {
            LOG.error( "Exception caught while processesing facebook statuses for " + collectionName + " with iden: " + iden, e );
            e.printStackTrace();
        } finally {
            LOG.info( "Done fetching facebook posts for " + collectionName + " with iden: " + iden );
        }
    }
    
    @Override
    public String toString(){
    	return "iden: "+iden+"\t token: "+token.getFacebookAccessTokenToPost()+"\t collection: "+collectionName;
    }
}