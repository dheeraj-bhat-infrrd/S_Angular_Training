package com.realtech.socialsurvey.compute.feeds.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.ComputeConstants;
import com.realtech.socialsurvey.compute.common.LocalPropertyFileHandler;
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

    private static final int PAGE_SIZE = 20;
    public static final int TWITTER_MIN_LIMIT = 100;

    private static final int TWITTER_FIRST_RETRIEVAL_COUNT = NumberUtils.toInt( LocalPropertyFileHandler.getInstance()
        .getProperty( ComputeConstants.APPLICATION_PROPERTY_FILE, ComputeConstants.TWITTER_FIRST_RETRIEVAL_COUNT )
        .orElse( "20" ) );

    @Override
    public List<TwitterFeedData> fetchFeed( long companyId, SocialMediaTokenResponse mediaToken, String twitterConsumerKey,
        String twitterConsumerSecret )
    {
        LOG.info( "Getting tweets with id: {}", companyId);

        List<TwitterFeedData> feedData = new ArrayList<>();

        TwitterTokenForSM token = mediaToken.getSocialMediaTokens().getTwitterToken();

        String pageId = UrlHelper.getTwitterPageIdFromURL( token.getTwitterPageLink() );
        String lastFetchedKey = mediaToken.getProfileType().toString() + "_" + mediaToken.getIden() + "_" + pageId;

        try {

            long lastFetchedPostId = NumberUtils.toLong( redisSocialMediaStateDao.getLastFetched( lastFetchedKey ) );

            // Settings Consumer and Access Tokens
            Twitter twitter = new TwitterFactory().getInstance();
            twitter.setOAuthConsumer( twitterConsumerKey, twitterConsumerSecret );
            twitter
                .setOAuthAccessToken( new AccessToken( token.getTwitterAccessToken(), token.getTwitterAccessTokenSecret() ) );

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
                if ( LOG.isDebugEnabled() ) {
                    LOG.debug( "RateLimit status for page {} is {} ", pageId, resultList.getRateLimitStatus() );
                }

                if ( resultList.getRateLimitStatus().getRemaining() <= TWITTER_MIN_LIMIT ) {
                    int secondsUntilReset = (int) ( ( (long) resultList.getRateLimitStatus().getResetTimeInSeconds() * 1000L
                        - System.currentTimeMillis() ) / 1000L );
                    redisSocialMediaStateDao.setTwitterLock( secondsUntilReset, pageId );
                }

                
                for ( Status status : resultList ) {
                    feedData.add( createTwitterFeedData( status ) );
                    maxId = status.getId();
                }

                if ( lastFetchedPostId == 0L && feedData.size() >= TWITTER_FIRST_RETRIEVAL_COUNT ) {
                    LOG.debug( "Inside fetchFeed method returning {} records", feedData.size() );
                    break;
                }
            } while ( resultList.size() == PAGE_SIZE );

            if ( !feedData.isEmpty() ) {
                redisSocialMediaStateDao.saveLastFetched( lastFetchedKey, Long.toString( feedData.get( 0 ).getId() ), Long.toString( lastFetchedPostId ) );
            }

        } catch ( TwitterException e ) {
            LOG.error( "Exception in Twitter feed extraction while fetching data for {}, Reason: ",pageId, e );
            // if the rate limit has been over used by any chance then blacklist the socialtoken
            if ( e.getErrorCode() == 88 ) {
                LOG.warn( "Rate limit exceeded. Error code {}", e.getErrorCode() );
                redisSocialMediaStateDao.setTwitterLock( 900, pageId );
            } else if(e.getErrorCode() == 32) {
                LOG.warn( "There was an issue with the authentication data for the request. Error code {}", e.getErrorCode() );
                redisSocialMediaStateDao.setTwitterLock( 3600, pageId );
            }
        } catch ( JedisConnectionException e ) {
            LOG.error( "Not able to connect to redis", e );
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
        feed.setCreatedAt( status.getCreatedAt().getTime() );
        
        feed.setId( status.getId() );

        if(status.isRetweeted()){
            feed.setFavoriteCount( status.getRetweetedStatus().getFavoriteCount() );
        } else {
            feed.setFavoriteCount( status.getFavoriteCount() );
        }
        
        feed.setRetweetCount( status.getRetweetCount() );
        feed.setPictures(
            Arrays.stream( status.getMediaEntities() ).map( x -> x.getMediaURL() ).collect( Collectors.toList() ) );
        feed.setUserName( status.getUser().getScreenName() );
        feed.setRetweetCount( status.getRetweetCount() );

        feed.setSource(getTwitterFeedSource(status.getSource()));
        RateLimitStatus rateLimitStatus = status.getRateLimitStatus();
        if ( rateLimitStatus != null ) {
            feed.setRemaining( status.getRateLimitStatus().getRemaining() );
            feed.setLimit( status.getRateLimitStatus().getLimit() );
            feed.setResetTimeInSeconds( status.getRateLimitStatus().getResetTimeInSeconds() );
        }
        return feed;
    }

    private String getTwitterFeedSource(String rawSource) 
    {
    		String source = null;		
    		if(StringUtils.isNotEmpty(rawSource) && StringUtils.contains(rawSource, ">")  && StringUtils.contains(rawSource, "</a>") ) {
    			source = rawSource.substring(rawSource.indexOf(">") + 1, rawSource.indexOf("</a>"));
    		}
    		return source;
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
