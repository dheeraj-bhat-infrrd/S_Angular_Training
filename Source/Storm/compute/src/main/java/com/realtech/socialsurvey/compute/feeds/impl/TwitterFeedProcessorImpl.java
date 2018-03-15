package com.realtech.socialsurvey.compute.feeds.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.dao.impl.RedisSocialMediaStateDaoImpl;
import com.realtech.socialsurvey.compute.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.compute.entities.TwitterTokenForSM;
import com.realtech.socialsurvey.compute.entities.response.TwitterFeedData;
import com.realtech.socialsurvey.compute.feeds.TwitterFeedProcessor;
import com.realtech.socialsurvey.compute.utils.UrlHelper;

import redis.clients.jedis.exceptions.JedisConnectionException;
import twitter4j.Paging;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;


/**
 * @author manish
 *
 */
public class TwitterFeedProcessorImpl implements TwitterFeedProcessor
{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger( TwitterFeedProcessorImpl.class );
    private RedisSocialMediaStateDaoImpl redisSocialMediaStateDao;


    public TwitterFeedProcessorImpl()
    {
        this.redisSocialMediaStateDao = new RedisSocialMediaStateDaoImpl();
    }

    private static final int PAGE_SIZE = 200;
    public static final int TWITTER_MIN_LIMIT = 100;

    @Override
    public List<TwitterFeedData> fetchFeed(long companyId, SocialMediaTokenResponse mediaToken,
                                           String twitterConsumerKey, String twitterConsumerSecret)
    {
        LOG.info( "Getting tweets with id: {}", companyId );

        List<TwitterFeedData> feedData = new ArrayList<>();

        TwitterTokenForSM token = mediaToken.getSocialMediaTokens().getTwitterToken();

        String pageId = UrlHelper.getTwitterPageIdFromURL( token.getTwitterPageLink() );
        String lastFetchedKey = mediaToken.getProfileType().toString() + "_" + mediaToken.getIden() + "_" + pageId;

        try {
                String sinceId = redisSocialMediaStateDao.getLastFetched( lastFetchedKey );

                long lastFetchedPostId = 0L;
                if (StringUtils.isNotEmpty( sinceId ) ) {
                    lastFetchedPostId = Long.parseLong( sinceId );
                } else {
                    // To save preValue of lastFetched
                    sinceId = "0";
                }

            // Settings Consumer and Access Tokens
            Twitter twitter = new TwitterFactory().getInstance();
            twitter.setOAuthConsumer( twitterConsumerKey, twitterConsumerSecret );
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

                //save the twitterSecondsUnitlRest in redis if remainingCount becomes <= 100
                if(LOG.isDebugEnabled()){
                    LOG.debug("RateLimit status for page {} is {} ", pageId, resultList.getRateLimitStatus());
                }
                
                if(resultList.getRateLimitStatus().getRemaining() <= TWITTER_MIN_LIMIT) {
                    int secondsUntilReset = (int)(((long)resultList.getRateLimitStatus().getResetTimeInSeconds() * 1000L - System.currentTimeMillis()) / 1000L);
                    redisSocialMediaStateDao.setTwitterLock(secondsUntilReset,pageId);
                }

                for ( Status status : resultList ) {
                    feedData.add(createTwitterFeedData(status));
                    maxId = status.getId();
                }
            } while ( resultList.size() == PAGE_SIZE );

            if(!feedData.isEmpty()){
                redisSocialMediaStateDao.saveLastFetched( lastFetchedKey, Long.toString( feedData.get( 0 ).getId() ), sinceId );
            }

        } catch ( TwitterException e ) {
            // if the rate limit has been over used by any chance then blacklist the socialtoken
            LOG.error( "Exception in Twitter feed extraction. Reason: ", e );
            if( e.getErrorCode() == 88 ) {
                redisSocialMediaStateDao.setTwitterLock(900,pageId);
            }
        } catch ( JedisConnectionException e ) {
            LOG.error( "Not able to connect to redis", e);
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
        feed.setPictures(Arrays.stream(status.getMediaEntities()).map(x -> x.getMediaURL()).collect(Collectors.toList()));
        RateLimitStatus rateLimitStatus = status.getRateLimitStatus();
        if ( rateLimitStatus != null ) {
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
