package com.realtech.socialsurvey.compute.feeds.impl;

import com.realtech.socialsurvey.compute.dao.impl.RedisSocialMediaStateDaoImpl;
import com.realtech.socialsurvey.compute.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.compute.entities.TwitterToken;
import com.realtech.socialsurvey.compute.entities.response.TwitterFeedData;
import com.realtech.socialsurvey.compute.feeds.TwitterFeedProcessor;
import com.realtech.socialsurvey.compute.utils.UrlHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.exceptions.JedisConnectionException;
import twitter4j.*;
import twitter4j.auth.AccessToken;

import java.util.ArrayList;
import java.util.List;


/**
 * @author manish
 *
 */
public class TwitterFeedProcessorImpl implements TwitterFeedProcessor
{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger( TwitterFeedProcessorImpl.class );
    private RedisSocialMediaStateDaoImpl redisSocialMediaStateDaoImpl;


    public TwitterFeedProcessorImpl()
    {
        this.redisSocialMediaStateDaoImpl = new RedisSocialMediaStateDaoImpl();
    }

    private static final int PAGE_SIZE = 200;

    // TODO pass it in submit topo commands
    private String consumerKey = "rqhYlHjXPERbAuASOZjNtzyEd";
    private String consumerSecret = "E8K78DEgxexQjlmaVqkddW1oX07ea8eUQBkCQdKlXwaCc3txWS";


    @Override
    public List<TwitterFeedData> fetchFeed( long companyId, SocialMediaTokenResponse mediaToken )
    {
        LOG.info( "Getting tweets with id: {}", companyId );

        List<TwitterFeedData> feedData = new ArrayList<>();

        TwitterToken token = null;
        if ( mediaToken != null ) {
            token = mediaToken.getSocialMediaTokens().getTwitterToken();
        }


        if ( token != null ) {

            String pageId = UrlHelper.getTwitterPageIdFromURL( token.getTwitterPageLink() );
            String lastFetchedKey = mediaToken.getProfileType().toString() + "_" + mediaToken.getIden() + "_" + pageId;

            try {
                String sinceId = redisSocialMediaStateDaoImpl.getLastFetched( lastFetchedKey );

                long lastFetchedPostId = 0L;
                if (StringUtils.isNotEmpty( sinceId ) ) {
                    lastFetchedPostId = Long.parseLong( sinceId );
                } else {
                    // To save preValue of lastFetched
                    sinceId = "0";
                }

                // Settings Consumer and Access Tokens
                Twitter twitter = new TwitterFactory().getInstance();
                twitter.setOAuthConsumer( consumerKey, consumerSecret );
                twitter.setOAuthAccessToken(
                    new AccessToken( token.getTwitterAccessToken(), token.getTwitterAccessTokenSecret() ) );


                long maxId = 0L;
                ResponseList<Status> resultList;


                do {
                    Paging paging = new Paging();
                    paging.setCount( PAGE_SIZE );
                    if ( lastFetchedPostId != 0L ) {
                        paging.setSinceId( lastFetchedPostId );
                    }
                    // Adding max for better results
                    if ( maxId != 0L ) {
                        paging.setMaxId( maxId - 1 );
                    }

                    resultList = twitter.getUserTimeline( pageId, paging );

                    //todo save the twitterSecondsUnitlRest in redis if remainingCount becomes <= 100
                    LOG.info("RateLimit status for page {} is {} and resetTime is {}", pageId, resultList.getRateLimitStatus(),
                            (int)(((long)resultList.getRateLimitStatus().getResetTimeInSeconds() * 1000L - System.currentTimeMillis()) / 1000L));
                    if(resultList.getRateLimitStatus().getRemaining() <= 897) {
                        int secondsUntilReset = (int)(((long)resultList.getRateLimitStatus().getResetTimeInSeconds() * 1000L - System.currentTimeMillis()) / 1000L);
                        redisSocialMediaStateDaoImpl.setTwitterLock(secondsUntilReset,pageId);
                    }

                    for ( Status status : resultList ) {
                        feedData.add( createTwitterFeedData( status ) );
                        maxId = status.getId();
                    }
                } while ( resultList.size() == PAGE_SIZE );

                if(!feedData.isEmpty()){
                    redisSocialMediaStateDaoImpl.saveLastFetched( lastFetchedKey, Long.toString( feedData.get( 0 ).getId() ), sinceId );
                }

            } catch ( TwitterException e ) {
                //todo if the rate limit has been over used by any chance then blacklist the socialtoken
                LOG.error( "Exception in Twitter feed extration. Reason: ", e );
            } catch ( JedisConnectionException e ) {
                LOG.error( "Not able to connect to jedis", e);
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
            LOG.info("Rate Limit = {} and Remaining limit {}", status.getRateLimitStatus().getLimit(), status.getRateLimitStatus().getRemaining());
            feed.setRemaining( status.getRateLimitStatus().getRemaining() );
            feed.setLimit( status.getRateLimitStatus().getLimit() );
            feed.setResetTimeInSeconds( status.getRateLimitStatus().getResetTimeInSeconds() );
        }
        return feed;
    }

    /*public static void main( String[] args )
    {
        TwitterToken token= new TwitterToken();
        token.setTwitterAccessToken( "1011709898-PTKqM3dLWsSHQ5jXYNla816zcbNfPp9b91EjOrP" );
        token.setTwitterAccessTokenSecret( "hxTzYUvgaib15LpE1JCq4fqAGrlcWMxJOs6XAM6WLmDpb" );
        token.setTwitterPageLink( "www.twitter.com/ManiCarpenter" );
        new TwitterFeedProcessorImpl().fetchFeed( 985L, token );
    }*/
}
