package com.realtech.socialsurvey.compute.common;

import java.io.IOException;

import com.realtech.socialsurvey.compute.entities.FileUploadResponse;
import com.realtech.socialsurvey.compute.exception.FileUploadUpdationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.services.api.APIIntergrationException;
import com.realtech.socialsurvey.compute.services.api.SSApiIntegrationService;
import com.realtech.socialsurvey.compute.services.api.SolrApiIntegrationService;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * API builder for retrofit endpoints
 * @author nishit
 *
 */
public class RetrofitApiBuilder
{

    private static final Logger LOG = LoggerFactory.getLogger( RetrofitApiBuilder.class );

    private static final RetrofitApiBuilder API_BUILDER = new RetrofitApiBuilder();

    private SolrApiIntegrationService solrAPIIntergrationService;
    
    private SSApiIntegrationService ssAPIIntergrationService;

    private final String solrApiUrl = LocalPropertyFileHandler.getInstance()
        .getProperty( ComputeConstants.APPLICATION_PROPERTY_FILE, ComputeConstants.SOLR_API_ENDPOINT ).orElse( null );
    
    private final String ssApiUrl = LocalPropertyFileHandler.getInstance()
        .getProperty( ComputeConstants.APPLICATION_PROPERTY_FILE, ComputeConstants.SS_API_ENDPOINT ).orElse( null );


    // Avoid creating instance
    private RetrofitApiBuilder()
    {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        // set basic level logging
        loggingInterceptor.setLevel( Level.BODY );
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor( loggingInterceptor );
        // Create integration service builders
        LOG.info( "Creating SOLR API builder" );
        // construct api gateway url
        Retrofit solrIntegServiceBuilder = new Retrofit.Builder().baseUrl( solrApiUrl )
            .addConverterFactory( GsonConverterFactory.create() ).client( httpClient.build() ).build();
        solrAPIIntergrationService = solrIntegServiceBuilder.create( SolrApiIntegrationService.class );
        
        // api gateway url for ssapi
        Retrofit ssApiIntegServiceBuilder = new Retrofit.Builder().baseUrl( ssApiUrl )
            .addConverterFactory( GsonConverterFactory.create() ).client( httpClient.build() ).build();
        ssAPIIntergrationService = ssApiIntegServiceBuilder.create( SSApiIntegrationService.class);

    }


    public static RetrofitApiBuilder apiBuilderInstance()
    {
        return API_BUILDER;
    }


    public SolrApiIntegrationService getSolrAPIIntergrationService()
    {
        return solrAPIIntergrationService;
    }
    
    public SSApiIntegrationService getSSAPIIntergrationService()
    {
        return ssAPIIntergrationService;
    }


    /**
     * Validates the reponse from api
     * @param response
     */
    public void validateResponse( Response<?> response )
    {
        if ( !response.isSuccessful() ) {
            if ( LOG.isWarnEnabled() ) {
                LOG.warn( "Error found. Response code: {}. Possible reason: {}", response.code(), response.message() );
            }
            try {
                if ( LOG.isWarnEnabled() ) {
                    LOG.warn( "Reason: {}", response.errorBody().string() );
                }
            } catch ( IOException e ) {
                throw new APIIntergrationException( "IOException while sending api response", e );
            }
            throw new APIIntergrationException( response.message() );
        }
    }

    public void validateFileUploadResponse(Response<FileUploadResponse> response) {
        if(response.code() == 500) {
            String errorBody = null;
            try {
                if (LOG.isWarnEnabled()) {
                    errorBody = response.errorBody().string();
                    LOG.warn("Error found. Response code: {}. Possible reason: {}", response.code(), response.errorBody().string());
                }
            }
            catch (IOException ex){
                throw new FileUploadUpdationException(errorBody);
            }
            throw new FileUploadUpdationException(errorBody);
        } else {
            validateResponse(response);
        }
    }


}
