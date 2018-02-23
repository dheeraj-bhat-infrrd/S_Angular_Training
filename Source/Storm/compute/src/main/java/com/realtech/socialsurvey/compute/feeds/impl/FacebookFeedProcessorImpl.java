package com.realtech.socialsurvey.compute.feeds.impl;

import com.realtech.socialsurvey.compute.common.FacebookAPIOperations;
import com.realtech.socialsurvey.compute.dao.impl.RedisSocialMediaStateDaoImpl;
import com.realtech.socialsurvey.compute.entities.FacebookToken;
import com.realtech.socialsurvey.compute.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.compute.entities.response.FacebookFeedData;
import com.realtech.socialsurvey.compute.entities.response.FacebookResponse;
import com.realtech.socialsurvey.compute.feeds.FacebookFeedProcessor;
import com.realtech.socialsurvey.compute.utils.UrlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;


/**
 * @author manish
 *
 */
public class FacebookFeedProcessorImpl implements FacebookFeedProcessor
{
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger( FacebookFeedProcessorImpl.class );

    private RedisSocialMediaStateDaoImpl redisSocialMediaStateDaoImpl;


    public FacebookFeedProcessorImpl()
    {
        this.redisSocialMediaStateDaoImpl = new RedisSocialMediaStateDaoImpl();
    }

    long lastFetchedPostId = 0L;
    long lastFetchedSince = 0L;


    /* (non-Javadoc)
     * @see com.realtech.socialsurvey.compute.feeds.FacebookFeedProcessor#fetchFeeds(com.realtech.socialsurvey.compute.entities.FacebookToken)
     */
    public List<FacebookFeedData> fetchFeeds( long companyId, SocialMediaTokenResponse mediaToken )
    {
        LOG.info( "Getting feeds with id: {}", companyId );

        List<FacebookFeedData> feeds = new ArrayList<>();

        FacebookToken token = null;
        if ( mediaToken.getSocialMediaTokens() != null ) {
            token = mediaToken.getSocialMediaTokens().getFacebookToken();
        }

        if ( token != null ) {

            try {

                String pageId = UrlHelper.getFacebookPageIdFromURL( token.getFacebookPageLink() );
                String lastFetchedKey = mediaToken.getProfileType().toString() + "_" + mediaToken.getIden() + "_" + pageId;
                String since = redisSocialMediaStateDaoImpl.getLastFetched( lastFetchedKey );
                String until = null;
                if ( since == null || since.isEmpty() ) {
                    Calendar cal = Calendar.getInstance();
                    until = String.valueOf( cal.getTimeInMillis() / 1000 );
                    cal.add( Calendar.DAY_OF_YEAR, -10 );
                    since = String.valueOf( cal.getTimeInMillis() / 1000 );
                }

                FacebookResponse result = FacebookAPIOperations.getInstance().fetchFeeds( pageId,
                    token.getFacebookAccessToken(), since, until, "" );

                if ( result != null ) {
                    feeds.addAll( result.getData() );
                    while ( result != null && result.getPaging() != null && result.getPaging().getNext() != null ) {
                        Map<String, String> queryMap = UrlHelper.getQueryParamsFromUrl( result.getPaging().getNext() );

                        result = FacebookAPIOperations.getInstance().fetchFeeds( pageId, token.getFacebookAccessToken(),
                            queryMap.get( "since" ), queryMap.get( "until" ), queryMap.get( "__paging_token" ) );

                        if ( result != null ) {
                            feeds.addAll( result.getData() );
                        }
                    }
                    if(!feeds.isEmpty()){
                        redisSocialMediaStateDaoImpl.saveLastFetched( lastFetchedKey,
                            Long.toString( feeds.get( 0 ).getCreatedTime() ), since );
                    }
                }
            } catch ( JedisConnectionException e ) {
                LOG.error( "Not able to connect to jedis", e);
            }
        }

        return feeds;
    }
}
