package com.realtech.socialsurvey.compute.common;

import com.realtech.socialsurvey.compute.entities.FileUploadResponse;
import com.realtech.socialsurvey.compute.entities.response.FacebookErrorResponse;
import com.realtech.socialsurvey.compute.exception.APIIntegrationException;
import com.realtech.socialsurvey.compute.exception.FacebookFeedException;
import com.realtech.socialsurvey.compute.exception.FileUploadUpdationException;
import com.realtech.socialsurvey.compute.exception.MongoSaveException;
import com.realtech.socialsurvey.compute.services.api.FacebookApiIntegrationService;
import com.realtech.socialsurvey.compute.services.api.LinkedinApiIntegrationService;
import com.realtech.socialsurvey.compute.services.api.SSApiIntegrationService;
import com.realtech.socialsurvey.compute.services.api.SolrApiIntegrationService;
import com.realtech.socialsurvey.compute.utils.ConversionUtils;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


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
    
    private FacebookApiIntegrationService facebookAPIIntergrationService;
    
    private LinkedinApiIntegrationService linkedinApiIntegrationService;
    
    private SSApiIntegrationService ssAPIIntergrationServiceWithIncreasedTimeout;
    private SolrApiIntegrationService solrAPIIntergrationServiceWithIncreasedTimeout;

    private final String solrApiUrl = LocalPropertyFileHandler.getInstance()
        .getProperty( ComputeConstants.APPLICATION_PROPERTY_FILE, ComputeConstants.SOLR_API_ENDPOINT ).orElse( null );
    
    private final String ssApiUrl = LocalPropertyFileHandler.getInstance()
        .getProperty( ComputeConstants.APPLICATION_PROPERTY_FILE, ComputeConstants.SS_API_ENDPOINT ).orElse( null );

    private final String facebookApiUrl = LocalPropertyFileHandler.getInstance()
        .getProperty( ComputeConstants.APPLICATION_PROPERTY_FILE, ComputeConstants.FACEBOOK_API_ENDPOINT ).orElse( null );

    private final String linkedinApiUrl = LocalPropertyFileHandler.getInstance()
        .getProperty( ComputeConstants.APPLICATION_PROPERTY_FILE, ComputeConstants.LINKED_IN_REST_API_URI ).orElse( null );

    // Avoid creating instance
    private RetrofitApiBuilder()
    {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        // set basic level logging
        loggingInterceptor.setLevel( Level.BASIC );
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        OkHttpClient.Builder httpClientWithIncreasedTimeout = new OkHttpClient.Builder()
        		.connectTimeout(180000, TimeUnit.MILLISECONDS)
        		.readTimeout(180000, TimeUnit.MILLISECONDS)
        		.writeTimeout(180000, TimeUnit.MILLISECONDS);
        httpClient.addInterceptor( loggingInterceptor );
        httpClientWithIncreasedTimeout.addInterceptor(loggingInterceptor);
        // Create integration service builders
        LOG.info( "Creating API builder" );
        // construct api gateway url
        Retrofit solrIntegServiceBuilder = new Retrofit.Builder().baseUrl( solrApiUrl )
            .addConverterFactory( GsonConverterFactory.create() ).client( httpClient.build() ).build();
        solrAPIIntergrationService = solrIntegServiceBuilder.create( SolrApiIntegrationService.class );
        
        // api gateway url for ssapi
        Retrofit ssApiIntegServiceBuilder = new Retrofit.Builder().baseUrl( ssApiUrl )
            .addConverterFactory( GsonConverterFactory.create() ).client( httpClient.build() ).build();
        ssAPIIntergrationService = ssApiIntegServiceBuilder.create( SSApiIntegrationService.class);
        
     // api gateway url for Facebook
        Retrofit facebookApiIntegServiceBuilder = new Retrofit.Builder().baseUrl( facebookApiUrl )
            .addConverterFactory( GsonConverterFactory.create() ).client( httpClient.build() ).build();
        facebookAPIIntergrationService = facebookApiIntegServiceBuilder.create( FacebookApiIntegrationService.class);
        
     // api gateway url for Linked
        Retrofit linkedinApiIntegServiceBuilder = new Retrofit.Builder().baseUrl( linkedinApiUrl )
            .addConverterFactory( GsonConverterFactory.create() ).client( httpClient.build() ).build();
        linkedinApiIntegrationService = linkedinApiIntegServiceBuilder.create( LinkedinApiIntegrationService.class);
        // api gateway url for ss api reporting
        Retrofit reportingSSApiIntegServiceBuilder = new Retrofit.Builder().baseUrl( ssApiUrl )
                .addConverterFactory( GsonConverterFactory.create() ).client( httpClientWithIncreasedTimeout.build() ).build();
        ssAPIIntergrationServiceWithIncreasedTimeout  = reportingSSApiIntegServiceBuilder.create( SSApiIntegrationService.class);
        
        // api gateway for solr with increased timeout.
        Retrofit solrIntegServiceBuilderWithIncreasedTimeout = new Retrofit.Builder().baseUrl( solrApiUrl )
                .addConverterFactory( GsonConverterFactory.create() ).client( httpClientWithIncreasedTimeout.build() ).build();
        solrAPIIntergrationServiceWithIncreasedTimeout = solrIntegServiceBuilderWithIncreasedTimeout
        		.create( SolrApiIntegrationService.class );
        

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
    
    public FacebookApiIntegrationService getFacebookAPIIntergrationService()
    {
        return facebookAPIIntergrationService;
    }
    
    public LinkedinApiIntegrationService getLinkedinApiIntegrationService(){
        return linkedinApiIntegrationService;
    }

    public SSApiIntegrationService getSSAPIIntergrationServiceWithIncreasedTimeOut() {
		return ssAPIIntergrationServiceWithIncreasedTimeout;
	}

	public SolrApiIntegrationService getSolrAPIIntergrationServiceWithIncreasedTimeout() {
		return solrAPIIntergrationServiceWithIncreasedTimeout;
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
                throw new APIIntegrationException( "IOException while sending api response", e );
            }
            throw new APIIntegrationException( response.message() );
        }
    }
    
    /**
     * Validates the reponse from api
     * @param response
     */
    public void validateFacebookResponse( Response<?> response )
    {
        if ( !response.isSuccessful() ) {
            if ( LOG.isWarnEnabled() ) {
                LOG.warn( "Error found. Response code: {}. Possible reason: {}", response.code(), response.message() );
            }
            try {
                String errorBody = response.errorBody().string();
                if ( LOG.isWarnEnabled() ) {
                    LOG.warn( "Reason: {}", errorBody );
                }
                
                FacebookErrorResponse errorResponse = ConversionUtils.deserialize( errorBody, FacebookErrorResponse.class );
                throw new FacebookFeedException( errorResponse.getError().getCode(), errorResponse.getError().getMessage() );
            } catch ( IOException e ) {
                throw new APIIntegrationException( "IOException while sending api response", e );
            }
        }
    }

    public void validateSavePostToMongoResponse(Response<?> response) {
        try {
            if(!response.isSuccessful()) {
                String errorBody = response.errorBody().string();
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Error found. Response code: {}. Possible reason: {}", response.code(), response.message());
                }
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Reason: {}", errorBody);
                }
                throw new MongoSaveException(errorBody);
            }
        } catch (IOException e) {
            throw new APIIntegrationException("IOException while sending api response", e);
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
