package com.realtech.socialsurvey.compute.common;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.entities.FeedIngestionEntity;
import com.realtech.socialsurvey.compute.entities.Keyword;
import com.realtech.socialsurvey.compute.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.compute.entities.response.FacebookFeedData;
import com.realtech.socialsurvey.compute.entities.response.SocialResponseObject;
import com.realtech.socialsurvey.compute.entities.response.TwitterFeedData;
import com.realtech.socialsurvey.compute.entities.response.linkedin.LinkedinFeedData;
import com.realtech.socialsurvey.compute.services.api.APIIntergrationException;

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
        } catch ( IOException | APIIntergrationException e ) {
            LOG.error( "getKeywordsForCompany IOException/ APIIntergrationException caught", e );
            return Optional.empty();
        }
    }


    /**
     * Get keyword for company id
     * @param companyId
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
        } catch ( IOException | APIIntergrationException e ) {
            LOG.error( "getMediaTokens IOException/ APIIntergrationException caught", e );
            return Optional.empty();
        }
    }


    /**
     * Save feed api call
     * @param companyId
     * @return
     */
    public boolean saveFeedToMongo( SocialResponseObject<FacebookFeedData> socialPostToMongo )
    {
        LOG.info( "Executing saveFeedToMongo method." );
        Call<SocialResponseObject<FacebookFeedData>> requestCall = RetrofitApiBuilder.apiBuilderInstance()
            .getSSAPIIntergrationService().saveSocialFeed( socialPostToMongo );
        try {
            Response<SocialResponseObject<FacebookFeedData>> response = requestCall.execute();
            RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
            if ( LOG.isTraceEnabled() ) {
                LOG.trace( "saveFeedToMongo response {}", response.body() );
            }
            return true;
        } catch ( IOException | APIIntergrationException e ) {
            LOG.error( "saveFeedToMongo IOException/ APIIntergrationException caught", e );
            return false;
        }
    }


    public boolean saveTwitterFeedToMongo( SocialResponseObject<TwitterFeedData> socialPostToMongo )
    {
        LOG.info( "Executing saveFeedToMongo method." );
        Call<SocialResponseObject<TwitterFeedData>> requestCall = RetrofitApiBuilder.apiBuilderInstance()
            .getSSAPIIntergrationService().saveTwitterFeed( socialPostToMongo );
        try {
            Response<SocialResponseObject<TwitterFeedData>> response = requestCall.execute();
            RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
            if ( LOG.isTraceEnabled() ) {
                LOG.trace( "saveTwitterFeedToMongo response {}", response.body() );
            }
            return true;
        } catch ( IOException | APIIntergrationException e ) {
            LOG.error( "saveTwitterFeedToMongo IOException/ APIIntergrationException caught", e );
            return false;
        }
    }


    public boolean saveLinkedinFeedToMongo( SocialResponseObject<LinkedinFeedData> socialPostToMongo )
    {
        LOG.info( "Executing saveFeedToMongo method." );
        Call<SocialResponseObject<LinkedinFeedData>> requestCall = RetrofitApiBuilder.apiBuilderInstance()
            .getSSAPIIntergrationService().saveLinkedinFeed( socialPostToMongo );
        try {
            Response<SocialResponseObject<LinkedinFeedData>> response = requestCall.execute();
            RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
            if ( LOG.isTraceEnabled() ) {
                LOG.trace( "saveLinkedinFeedToMongo response {}", response.body() );
            }
            return true;
        } catch ( IOException | APIIntergrationException e ) {
            LOG.error( "saveTwitterFeedToMongo IOException/ APIIntergrationException caught", e );
            return false;
        }
    }


    public Optional<Long> getSocialPostDuplicateCount( int hash, long comapnyId )
    {
        LOG.info( "Executing getSocialPostDuplicateCount method" );
        Call<Long> requestCall = RetrofitApiBuilder.apiBuilderInstance().getSSAPIIntergrationService().getDuplicateCount( hash,
            comapnyId );
        try {
            Response<Long> response = requestCall.execute();
            RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
            if ( LOG.isTraceEnabled() ) {
                LOG.trace( "getSocialPostDuplicateCount response {}", response.body() );
            }
            return Optional.of( response.body() );
        } catch ( APIIntergrationException | IOException e ) {
            LOG.error( "getSocialPostDuplicateCount IOException/ APIIntergrationException caught ", e );
            return Optional.empty();
        }
    }


    public Optional<Long> updateSocialPostDuplicateCount( int hash, long comapnyId, long duplicateCount )
    {
        LOG.info( "Executing updateSocialPostDuplicateCount method" );
        Call<Long> requestCall = RetrofitApiBuilder.apiBuilderInstance().getSSAPIIntergrationService()
            .updateDuplicateCount( hash, comapnyId, duplicateCount );
        try {
            Response<Long> response = requestCall.execute();
            RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
            if ( LOG.isTraceEnabled() ) {
                LOG.trace( "updateSocialPostDuplicateCount response {}", response.body() );
            }
            return Optional.of( response.body() );
        } catch ( APIIntergrationException | IOException e ) {
            LOG.error( "updateSocialPostDuplicateCount IOException/ APIIntergrationException caught ", e );
            return Optional.empty();
        }
    }
}