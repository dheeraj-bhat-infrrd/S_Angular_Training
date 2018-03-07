package com.realtech.socialsurvey.compute.common;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.entities.Keyword;
import com.realtech.socialsurvey.compute.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.compute.entities.response.SocialResponseObject;
import com.realtech.socialsurvey.compute.entities.response.TwitterFeedData;
import com.realtech.socialsurvey.compute.entities.response.linkedin.LinkedinFeedData;
import com.realtech.socialsurvey.compute.exception.APIIntegrationException;

import retrofit2.Call;
import retrofit2.Response;


/**
 * SS-API Operations
 * @author manish
 *
 */
public class SSAPIOperations
{
    private static final Logger LOG = LoggerFactory.getLogger( SSAPIOperations.class );
    private static SSAPIOperations apiOperations;


    private SSAPIOperations()
    {}


    public static synchronized SSAPIOperations getInstance()
    {
        if ( apiOperations == null ) {
            apiOperations = new SSAPIOperations();
        }
        return apiOperations;
    }


    /**
     * Get keyword for company id
     * @param companyId
     * @return
     */
    public Optional<List<Keyword>> getKeywordsForCompany( long companyId )
    {
        LOG.info( "Executing getKeywordsForCompany method." );
        Call<List<Keyword>> requestCall = RetrofitApiBuilder.apiBuilderInstance().getSSAPIIntergrationService()
            .getKeywordsForCompanyId( companyId );
        try {
            Response<List<Keyword>> response = requestCall.execute();
            RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
            if ( LOG.isTraceEnabled() ) {
                LOG.trace( "getKeywordsForCompany response {}", response.body() );
            }
            return Optional.of( response.body() );
        } catch ( IOException | APIIntegrationException e ) {
            LOG.error( "getKeywordsForCompany IOException/ APIIntegrationException caught", e );
            return Optional.empty();
        }
    }


    /**
     * Get keyword for company id
     * @return
     */
    public Optional<List<SocialMediaTokenResponse>> getMediaTokens()
    {
        LOG.info( "Executing getMediaTokens method." );
        Call<List<SocialMediaTokenResponse>> requestCall = RetrofitApiBuilder.apiBuilderInstance().getSSAPIIntergrationService()
            .getMediaTokens();
        try {
            Response<List<SocialMediaTokenResponse>> response = requestCall.execute();
            RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
            if ( LOG.isTraceEnabled() ) {
                LOG.trace( "getMediaTokens response {}", response.body() );
            }
            return Optional.of( response.body() );
        } catch ( IOException | APIIntegrationException e ) {
            LOG.error( "getMediaTokens IOException/ APIIntegrationException caught", e );
            return Optional.empty();
        }
    }


    /**
     * Save feed api call
     * @param socialPostToMongo
     * @return
     */
    public boolean saveFeedToMongo( SocialResponseObject<?> socialPostToMongo ) throws IOException {
        LOG.info( "Executing saveFeedToMongo method." );
        Call<SocialResponseObject> requestCall = RetrofitApiBuilder.apiBuilderInstance()
            .getSSAPIIntergrationService().saveSocialFeed( socialPostToMongo );
            Response response = requestCall.execute();
            RetrofitApiBuilder.apiBuilderInstance().validateSavePostToMongoResponse( response );
            if ( LOG.isTraceEnabled() ) {
                LOG.trace( "saveFeedToMongo response {}", response.body() );
            }
            return true;
    }

    public Optional<Long> updateSocialPostDuplicateCount( int hash, long comapnyId ) throws IOException {
        LOG.info( "Executing updateSocialPostDuplicateCount method" );
        Call<Long> requestCall = RetrofitApiBuilder.apiBuilderInstance().getSSAPIIntergrationService()
            .updateDuplicateCount( hash, comapnyId );
            Response<Long> response = requestCall.execute();
            RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
            if ( LOG.isTraceEnabled() ) {
                LOG.trace( "updateSocialPostDuplicateCount response {}", response.body() );
            }
            return Optional.of( response.body() );
    }

}