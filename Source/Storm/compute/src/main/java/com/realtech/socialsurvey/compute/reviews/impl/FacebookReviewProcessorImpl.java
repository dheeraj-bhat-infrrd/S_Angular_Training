package com.realtech.socialsurvey.compute.reviews.impl;

import com.realtech.socialsurvey.compute.common.ComputeConstants;
import com.realtech.socialsurvey.compute.common.FacebookAPIOperations;
import com.realtech.socialsurvey.compute.common.SSAPIOperations;
import com.realtech.socialsurvey.compute.dao.RedisSocialMediaStateDao;
import com.realtech.socialsurvey.compute.dao.impl.RedisSocialMediaStateDaoImpl;
import com.realtech.socialsurvey.compute.entities.FBReviewLastFetched;
import com.realtech.socialsurvey.compute.entities.FacebookTokenForSM;
import com.realtech.socialsurvey.compute.entities.FacebookXUsageHeader;
import com.realtech.socialsurvey.compute.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.compute.entities.response.FacebookReviewData;
import com.realtech.socialsurvey.compute.entities.response.FacebookReviewResponse;
import com.realtech.socialsurvey.compute.enums.ProfileType;
import com.realtech.socialsurvey.compute.enums.SurveySource;
import com.realtech.socialsurvey.compute.exception.FacebookFeedException;
import com.realtech.socialsurvey.compute.reviews.FacebookReviewProcessor;
import com.realtech.socialsurvey.compute.utils.ConversionUtils;
import com.realtech.socialsurvey.compute.utils.UrlHelper;
import okhttp3.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.exceptions.JedisConnectionException;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author Lavanya
 */

public class FacebookReviewProcessorImpl implements FacebookReviewProcessor
{
    private static final Logger LOG = LoggerFactory.getLogger( FacebookReviewProcessorImpl.class );

    private static final int USAGE_PERCENT_80 = 80;

    private static final int TOKEN_BLOCK_TIME = 3600;

    private static final int PAGE_BLOCK_TIME = 86400;

    private static final String X_PAGE_USAGE = "X-Page-Usage";

    private static final String X_APP_USAGE = "X-App-Usage";

    private RedisSocialMediaStateDao redisSocialMediaStateDao;

    public FacebookReviewProcessorImpl()
    {
        this.redisSocialMediaStateDao = new RedisSocialMediaStateDaoImpl();
    }


    @Override public List<FacebookReviewData> fetchReviews( SocialMediaTokenResponse mediaToken )
    {
        List<FacebookReviewData> reviews = new ArrayList<>(  );

        FacebookTokenForSM fbToken = mediaToken.getSocialMediaTokens().getFacebookToken();

        try{
            String pageId = UrlHelper.getFacebookPageIdFromURL( fbToken.getFacebookPageLink() );
            String lastFetchedKey = "FbReview_" + mediaToken.getProfileType().toString() + "_" + mediaToken.getIden() + "_" + pageId;

            //String lastFetchedTime = redisSocialMediaStateDao.getLastFetched( lastFetchedKey );
            long lastFetchedTime  ;
                /*if lastFetchedId = null, ie. we are fetching the reviews for the first time.
                 So fetch all the reviews from the page*/
            if( mediaToken.getSocialMediaLastFetched() == null ||
                mediaToken.getSocialMediaLastFetched().getFbReviewLastFetched() == null ) {
                lastFetchedTime = 0;
            } else
                lastFetchedTime = mediaToken.getSocialMediaLastFetched().getFbReviewLastFetched().getCurrent();

            //do while until ders no paging returned or until lastFetchedTime
            FacebookReviewResponse response;
            String after = "";
            do {
                response = fetchReviews( pageId, fbToken.getFacebookAccessToken(), after, mediaToken.getIden(), mediaToken.getProfileType() );
                if ( response != null && response.getData()!= null && !response.getData().isEmpty() ) {
                    //get the data and check if the last record created time is greater than the last fetch time
                    //yes then add all the records to the list
                    //no => get only the records whose created_time > lastfetchedTime and break
                    List<FacebookReviewData> data = response.getData();
                    if ( lastFetchedTime  < data.get( data.size() - 1 ).getCreatedTime() )
                        reviews.addAll( data );
                    else {
                        long finalLastFetchedTime = lastFetchedTime;
                        reviews.addAll( data.stream().filter( x -> x.getCreatedTime() >  finalLastFetchedTime )
                            .collect( Collectors.toList() ) );
                        break;
                    }

                    if( response.getPaging() != null && response.getPaging().getCursors() != null)
                        after = response.getPaging().getCursors().getAfter();
                }
                else break;
            }while ( response.getPaging() != null && response.getPaging().getCursors() != null
                && response.getPaging().getCursors().getAfter() != null );

            if(!reviews.isEmpty()){
                //update the fbReviewLastFetched of the corresponding hierarchy setting
                SSAPIOperations.getInstance().updateSocialMediaLastFetched(mediaToken.getIden(),
                    reviews.get( 0 ).getCreatedTime() , lastFetchedTime,
                    mediaToken.getProfileType().getValue(), SurveySource.FACEBOOK.getValue());

                /*redisSocialMediaStateDao.saveLastFetched( lastFetchedKey,
                    Long.toString( reviews.get( 0 ).getCreatedTime() ), lastFetchedTime );*/
            }

        } catch ( JedisConnectionException e ) {
            LOG.error( "Not able to connect to redis", e );
        }
        return reviews;
    }


    private FacebookReviewResponse fetchReviews( String pageId, String pageAccessToken, String after, long iden,
        ProfileType profileType )
    {
        try {
            Response<FacebookReviewResponse> response = FacebookAPIOperations.getInstance()
                .fetchReviews( pageId, pageAccessToken, after );
            if ( response != null ) {
                checkRateLimiting( response.headers(), pageId, pageAccessToken );
                return response.body();
            }
        }catch ( FacebookFeedException ffe){
            handleError(pageId, ffe, iden, profileType);
        }
        return null;
    }


    private void handleError( String pageId, FacebookFeedException e, long iden, ProfileType profileType ) {
        LOG.error( "Error while fetching fb reviews with companyId {} and pageId {} and exception {}", iden, pageId, e );
        if(e.getFacebookErrorCode() == 4){
            // Application level
            redisSocialMediaStateDao.setFacebookLockForApplication(TOKEN_BLOCK_TIME );
        } else if(e.getFacebookErrorCode() == 17){
            // Account level
            redisSocialMediaStateDao.setFacebookLockForPage( pageId, PAGE_BLOCK_TIME );
        } else if(e.getFacebookErrorCode() == 32){
            // Page level rate limit
            redisSocialMediaStateDao.setFacebookLockForPage( pageId, PAGE_BLOCK_TIME );
        } else if( e.getFacebookErrorCode() == 210 || e.getFacebookErrorCode() == 1 ){
            //trying to fetch reviews from user
            LOG.error(e.getMessage());
        } else if(e.getFacebookErrorCode() == 190) {
            SSAPIOperations.getInstance().updateTokenExpiryAlert(iden, ComputeConstants.FACEBOOK_TOKEN_EXPIRY_FIELD,
                true, profileType.getValue());
        }

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
