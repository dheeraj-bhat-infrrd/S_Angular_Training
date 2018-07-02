package com.realtech.socialsurvey.compute.common;

import com.realtech.socialsurvey.compute.entities.response.FacebookResponse;
import com.realtech.socialsurvey.compute.entities.response.InstagramMedia;
import com.realtech.socialsurvey.compute.entities.response.InstagramResponse;
import com.realtech.socialsurvey.compute.exception.APIIntegrationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;


/**
 * Facebook-API Operations
 * @author manish
 *
 */
public class FacebookAPIOperations
{
    private static final Logger LOG = LoggerFactory.getLogger( FacebookAPIOperations.class );

    private static FacebookAPIOperations apiOperations;
    
    private static final String FIELDS ="story,message,created_time,updated_time,full_picture,picture,message_tags,from,permalink_url,"
        + "application,likes.summary(true),comments.summary(true)";
    private static final String LIMIT = "100";

    private static final String IG_FIELDS = "ig_id,caption,media_url,media_type,timestamp,username,permalink,like_count,comments_count";
    public static final String IG_LIMIT = "50";

    private static final String IG_INITIAL_FIELDS = "connected_instagram_account{media.limit(50){ig_id,caption,media_url,media_type,"
        + "timestamp,username,permalink,like_count,comments_count}}";

    private FacebookAPIOperations()
    {}


    public static synchronized FacebookAPIOperations getInstance()
    {
        if ( apiOperations == null ) {
            apiOperations = new FacebookAPIOperations();
        }
        return apiOperations;
    }
    
    /**
     * Fetch feeds for page id with accessToken
     * @param pageId - Facebook page id
     * @param accessToken 
     * @param since - Unix timestamp
     * @param after - after cursor
     * @param before - before cursor
     * @return
     */
    public Response<FacebookResponse> fetchFeeds( String pageId , String accessToken, String since, String until, String pagingToken)
    {
        Call<FacebookResponse> requestCall = RetrofitApiBuilder.apiBuilderInstance()
            .getFacebookAPIIntergrationService().fetchFeeds( pageId, accessToken, since, until, LIMIT, pagingToken, FIELDS );
        
        try {
            Response<FacebookResponse> response = requestCall.execute();
            RetrofitApiBuilder.apiBuilderInstance().validateFacebookResponse( response );

            if ( LOG.isTraceEnabled() ) {
                LOG.trace( "response {}", response.body() );
                LOG.trace( "headers {}", response.headers() );
            }
            return response;
        } catch ( IOException | APIIntegrationException e ) {
            LOG.error( "IOException/ APIIntegrationException caught", e );
            return null;
        }
    }

    /**
     * Fetch instagram feeds using the cursor
     * @param igAccountId
     * @param accessToken
     * @param after
     * @return
     */
    public Response<InstagramMedia> fetchMedia(String igAccountId, String accessToken , String after) {
        Call<InstagramMedia> requestCall = RetrofitApiBuilder.apiBuilderInstance().getFacebookAPIIntergrationService()
                .fetchIgFeeds(igAccountId, accessToken, IG_FIELDS, IG_LIMIT, after);
        try {
            Response<InstagramMedia> response = requestCall.execute();
            RetrofitApiBuilder.apiBuilderInstance().validateFacebookResponse( response );

            if ( LOG.isTraceEnabled() ) {
                LOG.trace( "response {}", response.body() );
                LOG.trace( "headers {}", response.headers() );
            }
            return response;
        } catch ( IOException | APIIntegrationException e ) {
            LOG.error( "IOException/ APIIntegrationException caught", e );
            return null;
        }
    }

    /**
     * Fetch the first batch of instagram feeds
     * @param pageId
     * @param accessToken
     * @return
     */
    public Response<InstagramResponse> fetchMedia(String pageId, String accessToken) {
        Call<InstagramResponse> requestCall = RetrofitApiBuilder.apiBuilderInstance().getFacebookAPIIntergrationService()
                .fetchIgFeeds( pageId, accessToken, IG_INITIAL_FIELDS );
        try {
            Response<InstagramResponse> response = requestCall.execute();
            RetrofitApiBuilder.apiBuilderInstance().validateFacebookResponse( response );

            if ( LOG.isTraceEnabled() ) {
                LOG.trace( "response {}", response.body() );
                LOG.trace( "headers {}", response.headers() );
            }
            return response ;
        } catch ( IOException | APIIntegrationException e ) {
            LOG.error( "IOException/ APIIntegrationException caught", e );
            return null;
        }
    }
}
