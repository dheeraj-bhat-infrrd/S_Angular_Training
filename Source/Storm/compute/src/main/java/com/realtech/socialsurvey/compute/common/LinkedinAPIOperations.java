package com.realtech.socialsurvey.compute.common;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.entities.response.linkedin.LinkedinFeedResponse;
import com.realtech.socialsurvey.compute.services.api.APIIntergrationException;

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
    public Optional<LinkedinFeedResponse> fetchFeeds( String lnCompanyId, int start, int count, String eventType,
        String accessToken )
    {
        Call<LinkedinFeedResponse> requestCall = RetrofitApiBuilder.apiBuilderInstance().getLinkedinApiIntegrationService()
            .fetchFeeds( lnCompanyId, start, count, eventType, accessToken );
        try {
            Response<LinkedinFeedResponse> response = requestCall.execute();
            RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
            if ( LOG.isTraceEnabled() ) {
                LOG.trace( "response {}", response.body() );
            }
            return Optional.of( response.body() );
        } catch ( IOException | APIIntergrationException e ) {
            LOG.error( "IOException/ APIIntergrationException caught", e );
            return Optional.empty();
        }
    }
}
