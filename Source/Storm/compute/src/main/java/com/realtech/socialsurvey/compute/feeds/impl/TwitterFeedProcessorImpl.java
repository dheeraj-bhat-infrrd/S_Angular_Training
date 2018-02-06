package com.realtech.socialsurvey.compute.feeds.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.entities.TwitterToken;
import com.realtech.socialsurvey.compute.entities.response.TwitterFeedData;
import com.realtech.socialsurvey.compute.feeds.TwitterFeedProcessor;
import com.realtech.socialsurvey.compute.utils.UrlHelper;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;


public class TwitterFeedProcessorImpl implements TwitterFeedProcessor
{
    private static final Logger LOG = LoggerFactory.getLogger( TwitterFeedProcessorImpl.class );

    private static final int RETRIES_INITIAL = 0;
    private static final int PAGE_SIZE = 200;
    
    private String consumerKey = "01L5xRq0Ynsa2s6z0XbNAhria";
    private String consumerSecret = "TJX32M2OY7TyETtDcUlMF7O06BwVXMpFZRsxJPyeC01UN3rqFM";
    
    long lastFetchedPostId = 0L;

    @Override
    public List<TwitterFeedData> fetchFeed( long companyId, TwitterToken token )
    {
        LOG.info( "Getting tweets with id: {}", companyId );
        // Settings Consumer and Access Tokens
        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer( consumerKey, consumerSecret );
        twitter.setOAuthAccessToken( new AccessToken( token.getTwitterAccessToken(), token.getTwitterAccessTokenSecret() ) );

        // building query to fetch
        List<TwitterFeedData> feedData = new ArrayList<>();
        try {
            int pageNo = 1;
            long maxId = 0L;
            ResponseList<Status> resultList;
            do {
                Paging paging = new Paging( pageNo, PAGE_SIZE );
                
                if ( lastFetchedPostId != 0L ) {
                    paging.setSinceId( lastFetchedPostId );
                }
                
                // Adding max for better results
                if(maxId != 0L){
                    paging.setMaxId( maxId-1 );
                }
                
                resultList = twitter.getUserTimeline( UrlHelper.getTwitterPageIdFromURL( token.getTwitterPageLink()),  paging );

                for ( Status status : resultList ) {

                    status.getRateLimitStatus();
                    TwitterFeedData feed = new TwitterFeedData();
                    feed.setText( status.getText() );
                    feed.setCreatedAt( status.getCreatedAt() );
                    feed.setId( status.getId() );
                    feed.setRetweetCount( status.getRetweetCount() );
                    if ( status.getRateLimitStatus() != null ) {
                        feed.setRemaining( status.getRateLimitStatus().getRemaining() );
                        feed.setLimit( status.getRateLimitStatus().getLimit() );
                        feed.setResetTimeInSeconds( status.getRateLimitStatus().getResetTimeInSeconds() );
                    }

                    feedData.add( feed );
                }
                
                if(!feedData.isEmpty()){
                    maxId = feedData.get( feedData.size() -1 ).getId();
                }

                pageNo++;
            } while ( resultList.size() == PAGE_SIZE );
            
            if(!feedData.isEmpty()){
                lastFetchedPostId = feedData.get( 0 ).getId();
            }
        } catch ( TwitterException e ) {
            LOG.error( "Exception in Twitter feed extration. Reason: " + e.getMessage() );
        }
        return feedData;
    }
}
