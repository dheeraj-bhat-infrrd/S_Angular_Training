package com.realtech.socialsurvey.compute.feeds.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.entities.TwitterToken;
import com.realtech.socialsurvey.compute.entities.response.TwitterFeedData;
import com.realtech.socialsurvey.compute.feeds.TwitterFeedProcessor;

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

    private String twitterConsumerKey = "01L5xRq0Ynsa2s6z0XbNAhria";

    private String twitterConsumerSecret = "TJX32M2OY7TyETtDcUlMF7O06BwVXMpFZRsxJPyeC01UN3rqFM";

    private String lastFetchedPostId = "";


    @Override
    public List<TwitterFeedData> fetchFeed( long iden, String collection, TwitterToken token )
    {
        LOG.info( "Getting tweets for {} with id: {}", collection, iden );
        // Settings Consumer and Access Tokens
        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer( twitterConsumerKey, twitterConsumerSecret );
        twitter.setOAuthAccessToken( new AccessToken( token.getTwitterAccessToken(), token.getTwitterAccessTokenSecret() ) );

        // building query to fetch
        List<TwitterFeedData> feedData = new ArrayList<>();
        try {
            int pageNo = 1;
            ResponseList<Status> resultList;
            do {
                if ( lastFetchedPostId.equals( "" ) ) {
                    resultList = twitter.getUserTimeline( new Paging( pageNo, PAGE_SIZE ) );
                } else {
                    resultList = twitter
                        .getUserTimeline( new Paging( pageNo, PAGE_SIZE ).sinceId( Long.parseLong( lastFetchedPostId ) ) );
                }

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

                pageNo++;
            } while ( resultList.size() == PAGE_SIZE );
        } catch ( TwitterException e ) {
            LOG.error( "Exception in Twitter feed extration. Reason: " + e.getMessage() );
            if ( lastFetchedPostId.isEmpty() ) {
                lastFetchedPostId = "0";
            }
        }
        return feedData;
    }


    public static void main( String[] args )
    {
        TwitterToken token = new TwitterToken();
        token.setTwitterAccessToken( "1011709898-wkha5NSmR1POjcg1SwWPWm5q4eCPVzKgXhLTq8L" );
        token.setTwitterAccessTokenSecret( "GtmMlb1LMroWJWATmF8NmdxaI27ZF9nelyuN7MYVddS2U" );
        new TwitterFeedProcessorImpl().fetchFeed( 10L, "Social survey", token );
    }
}
