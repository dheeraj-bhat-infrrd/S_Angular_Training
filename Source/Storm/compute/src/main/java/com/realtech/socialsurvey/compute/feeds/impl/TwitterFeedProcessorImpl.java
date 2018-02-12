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
import twitter4j.RateLimitStatus;
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

    private String consumerKey = "f6paIc7BaVao0GHwxbhLrac5B";
    private String consumerSecret = "BNc3qvAXhGep18Jm2cSkVDFKKjXunu0yUv1cytseqkJ36M99qj";

    long lastFetchedPostId = 0L;


    @Override
    public List<TwitterFeedData> fetchFeed( long companyId, TwitterToken token )
    {
        LOG.info( "Getting tweets with id: {}", companyId );

        List<TwitterFeedData> feedData = new ArrayList<>();

        if ( token != null ) {
            // Settings Consumer and Access Tokens
            Twitter twitter = new TwitterFactory().getInstance();
            twitter.setOAuthConsumer( consumerKey, consumerSecret );
            twitter
                .setOAuthAccessToken( new AccessToken( token.getTwitterAccessToken(), token.getTwitterAccessTokenSecret() ) );

            try {
                long maxId = 0L;
                ResponseList<Status> resultList;
                do {
                    Paging paging = new Paging();
                    paging.setCount( 5 );

                    if ( lastFetchedPostId != 0L ) {
                        paging.setSinceId( lastFetchedPostId );
                    }

                    // Adding max for better results
                    if ( maxId != 0L ) {
                        paging.setMaxId( maxId - 1 );
                    }

                    String pageId = UrlHelper.getTwitterPageIdFromURL( token.getTwitterPageLink() );
                    resultList = twitter.getUserTimeline( pageId, paging );

                    for ( Status status : resultList ) {
                        feedData.add( createTwitterFeedData( status ) );
                        maxId = status.getId();
                    }

                } while ( resultList.size() == PAGE_SIZE );

            } catch ( TwitterException e ) {
                LOG.error( "Exception in Twitter feed extration. Reason: ", e);
            }
        }

        return feedData;
    }


    /**
     * Method to create TwitterFeedData object
     * @param status
     * @return
     */
    private TwitterFeedData createTwitterFeedData( Status status )
    {
        TwitterFeedData feed = new TwitterFeedData();
        feed.setText( status.getText() );
        feed.setCreatedAt( status.getCreatedAt() );
        feed.setId( status.getId() );
        feed.setRetweetCount( status.getRetweetCount() );
        RateLimitStatus rateLimitStatus = status.getRateLimitStatus();
        if ( rateLimitStatus != null ) {
            feed.setRemaining( status.getRateLimitStatus().getRemaining() );
            feed.setLimit( status.getRateLimitStatus().getLimit() );
            feed.setResetTimeInSeconds( status.getRateLimitStatus().getResetTimeInSeconds() );
        }
        return feed;
    }
    
    public static void main( String[] args )
    {
        TwitterToken token= new TwitterToken();
        token.setTwitterAccessToken( "1011709898-PTKqM3dLWsSHQ5jXYNla816zcbNfPp9b91EjOrP" );
        token.setTwitterAccessTokenSecret( "hxTzYUvgaib15LpE1JCq4fqAGrlcWMxJOs6XAM6WLmDpb" );
        token.setTwitterPageLink( "www.twitter.com/ManiCarpenter" );
        new TwitterFeedProcessorImpl().fetchFeed( 985L, token );
    }
}
