package com.realtech.socialsurvey.compute.common;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.entities.response.linkedin.LinkedinFeedResponse;
import com.realtech.socialsurvey.compute.exception.APIIntegrationException;

import retrofit2.Call;
import retrofit2.Response;


/**
 * Facebook-API Operations
 * @author manish
 *
 */
public class LinkedinAPIOperations
{
    private static final Logger LOG = LoggerFactory.getLogger( LinkedinAPIOperations.class );
    private static final int LIMIT = 100;
    private static LinkedinAPIOperations apiOperations;


    private LinkedinAPIOperations()
    {}


    public static synchronized LinkedinAPIOperations getInstance()
    {
        if ( apiOperations == null ) {
            apiOperations = new LinkedinAPIOperations();
        }
        return apiOperations;
    }


    /**
     * Fetch feeds for page id with accessToken
     * @param pageId
     * @param accessToken
     * @return
     */
    public LinkedinFeedResponse fetchFeeds( String lnCompanyId, String accessToken, int start, String eventType )
    {
        Call<LinkedinFeedResponse> requestCall = RetrofitApiBuilder.apiBuilderInstance().getLinkedinApiIntegrationService()
            .fetchFeeds( lnCompanyId, start, LIMIT, eventType, accessToken );
        try {
            Response<LinkedinFeedResponse> response = requestCall.execute();
            RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
            if ( LOG.isTraceEnabled() ) {
                LOG.trace( "response {}", response.body() );
            }
            return response.body();
        } catch ( IOException | APIIntegrationException e ) {
            LOG.error( "IOException/ APIIntegrationException caught", e );
            return null;
        }
    }
}
