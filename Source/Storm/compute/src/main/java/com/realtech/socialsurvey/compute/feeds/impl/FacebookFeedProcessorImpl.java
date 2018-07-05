package com.realtech.socialsurvey.compute.feeds.impl;

import com.realtech.socialsurvey.compute.common.ComputeConstants;
import com.realtech.socialsurvey.compute.common.FacebookAPIOperations;
import com.realtech.socialsurvey.compute.common.SSAPIOperations;
import com.realtech.socialsurvey.compute.dao.RedisSocialMediaStateDao;
import com.realtech.socialsurvey.compute.dao.impl.RedisSocialMediaStateDaoImpl;
import com.realtech.socialsurvey.compute.entities.FacebookTokenForSM;
import com.realtech.socialsurvey.compute.entities.FacebookXUsageHeader;
import com.realtech.socialsurvey.compute.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.compute.entities.response.FacebookFeedData;
import com.realtech.socialsurvey.compute.entities.response.FacebookResponse;
import com.realtech.socialsurvey.compute.enums.ProfileType;
import com.realtech.socialsurvey.compute.exception.FacebookFeedException;
import com.realtech.socialsurvey.compute.feeds.FacebookFeedProcessor;
import com.realtech.socialsurvey.compute.utils.ConversionUtils;
import com.realtech.socialsurvey.compute.utils.UrlHelper;
import okhttp3.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.exceptions.JedisConnectionException;
import retrofit2.Response;

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
    private static final int USAGE_PERCENT_80 = 80;

    private static final int TOKEN_BLOCK_TIME = 3600;

    private static final int PAGE_BLOCK_TIME = 86400;

    private static final String X_PAGE_USAGE = "X-Page-Usage";

    private static final String X_APP_USAGE = "X-App-Usage";

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger( FacebookFeedProcessorImpl.class );

    private RedisSocialMediaStateDao redisSocialMediaStateDao;


    public FacebookFeedProcessorImpl()
    {
        this.redisSocialMediaStateDao = new RedisSocialMediaStateDaoImpl();
    }


    /* (non-Javadoc)
     * @see com.realtech.socialsurvey.compute.feeds.FacebookFeedProcessor#fetchFeeds(com.realtech.socialsurvey.compute.entities.FacebookToken)
     */
    public List<FacebookFeedData> fetchFeeds( long companyId, SocialMediaTokenResponse mediaToken )
    {
        LOG.info( "Getting feeds with id: {}", companyId );

        List<FacebookFeedData> feeds = new ArrayList<>();

        FacebookTokenForSM token = null;
        if ( mediaToken.getSocialMediaTokens() != null ) {
            token = mediaToken.getSocialMediaTokens().getFacebookToken();
        }

        if ( token != null ) {

            try {

                String pageId = UrlHelper.getFacebookPageIdFromURL( token.getFacebookPageLink() );
                String lastFetchedKey = mediaToken.getProfileType().toString() + "_" + mediaToken.getIden() + "_" + pageId;

                String since = redisSocialMediaStateDao.getLastFetched( lastFetchedKey );
                String until = null;
                if ( since == null || since.isEmpty() ) {
                    Calendar cal = Calendar.getInstance();
                    until = String.valueOf( cal.getTimeInMillis() / 1000 );
                    cal.add( Calendar.DAY_OF_YEAR, -5 );
                    since = String.valueOf( cal.getTimeInMillis() / 1000 );
                }

                FacebookResponse result = fetchFeeds( pageId, token.getFacebookAccessTokenToPost(), since, until, "",
                    mediaToken.getIden(), mediaToken.getProfileType());

                if ( result != null ) {
                    feeds.addAll( result.getData() );
                    while ( result != null && result.getPaging() != null && result.getPaging().getNext() != null ) {
                        Map<String, String> queryMap = UrlHelper.getQueryParamsFromUrl( result.getPaging().getNext() );

                        result = fetchFeeds( pageId, token.getFacebookAccessTokenToPost(), queryMap.get( "since" ),
                            queryMap.get( "until" ), queryMap.get( "__paging_token" ), mediaToken.getIden(), mediaToken.getProfileType() );

                        if ( result != null ) {
                            feeds.addAll( result.getData() );
                        }
                    }
                    if ( !feeds.isEmpty() ) {
                        redisSocialMediaStateDao.saveLastFetched( lastFetchedKey,
                            Long.toString( feeds.get( 0 ).getCreatedTime() ), since );
                    }
                }
            } catch ( JedisConnectionException e ) {
                LOG.error( "Not able to connect to jedis", e );
            }
        }

        return feeds;
    }


    /**
     * @param pageId
     * @param accessToken
     * @param since
     * @param until
     * @param pagingToken
     * @param iden
     * @param profileType
     * @return
     */
    private FacebookResponse fetchFeeds( String pageId, String accessToken, String since, String until, String pagingToken,
        long iden, ProfileType profileType )
    {
        try {
            Response<FacebookResponse> response = FacebookAPIOperations.getInstance().fetchFeeds( pageId, accessToken, since, until,
                pagingToken );
            if ( response != null ) {
                checkRateLimiting( response.headers(), pageId, accessToken );
                return response.body();
            }
        } catch (FacebookFeedException e) {
            if(e.getFacebookErrorCode() == 4){
                // Application level
                redisSocialMediaStateDao.setFacebookLockForApplication(TOKEN_BLOCK_TIME );
            } else if(e.getFacebookErrorCode() == 17){
                // Account level
                redisSocialMediaStateDao.setFacebookLockForPage( pageId, PAGE_BLOCK_TIME );
            } else if(e.getFacebookErrorCode() == 32){
                // Page level rate limit
                redisSocialMediaStateDao.setFacebookLockForPage( pageId, PAGE_BLOCK_TIME );
            }
            else if(e.getFacebookErrorCode() == 190){
                //update mongo expiryalertsent to true
                SSAPIOperations.getInstance().updateTokenExpiryAlert(iden, ComputeConstants.FACEBOOK_TOKEN_EXPIRY_FIELD,
                    true, profileType.getValue());
            }
        }

        return null;
    }


    private void checkRateLimiting( Headers headers, String pageId, String accessToken )
    {
        String xAppUsageHeaderStr = headers.get( X_APP_USAGE );
        String xPageUsageHeaderStr = headers.get( X_PAGE_USAGE );

        if ( xPageUsageHeaderStr != null ) {
            FacebookXUsageHeader xPageUsageHeader = ConversionUtils.deserialize( xPageUsageHeaderStr,
                FacebookXUsageHeader.class );
            LOG.debug( "Response contains X-Page-Usage header, {}", xPageUsageHeader );
            if ( xPageUsageHeader.getCallCount() >= USAGE_PERCENT_80 || xPageUsageHeader.getTotalCputime() >= USAGE_PERCENT_80
                || xPageUsageHeader.getTotalTime() >= USAGE_PERCENT_80 ) {
                redisSocialMediaStateDao.setFacebookLockForPage( pageId, PAGE_BLOCK_TIME );
            }
        }

        if ( xAppUsageHeaderStr != null ) {
            FacebookXUsageHeader xAppUsageHeader = ConversionUtils.deserialize( xAppUsageHeaderStr,
                FacebookXUsageHeader.class );
            LOG.debug( "Response contains X-App-Usage header, {}", xAppUsageHeader );
            if ( xAppUsageHeader.getCallCount() >= USAGE_PERCENT_80 || xAppUsageHeader.getTotalCputime() >= USAGE_PERCENT_80
                || xAppUsageHeader.getTotalTime() >= USAGE_PERCENT_80 ) {
                redisSocialMediaStateDao.setFacebookLockForToken( accessToken, TOKEN_BLOCK_TIME );
            }
        }
    }
}
