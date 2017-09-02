package com.realtech.socialsurvey.core.api.builder;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jakewharton.retrofit.Ok3Client;
import com.realtech.socialsurvey.core.api.SSApiBatchIntegration;
import com.realtech.socialsurvey.core.api.errorhandler.SSApiBatchErrorHandler;

import okhttp3.OkHttpClient;
import retrofit.RestAdapter;
import retrofit.client.Client;
import retrofit.client.OkClient;

@Component
public class SSApiBatchIntegrationBuilder implements InitializingBean
{
    private static final Logger LOG = LoggerFactory.getLogger( SSApiBatchIntegrationBuilder.class );

    private SSApiBatchIntegration integrationApi;

    @Value ( "${SSAPI_ENDPOINT}")
    private String apiEndPoint;


    public SSApiBatchIntegration getIntegrationApi()
    {
        return integrationApi;
    }


    @Override
    public void afterPropertiesSet() throws Exception
    {
        LOG.info( "Initialising rest builder" );
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(4, TimeUnit.MINUTES)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build();
        
        
        RestAdapter apiAdaptor = new RestAdapter.Builder().setLogLevel( RestAdapter.LogLevel.FULL )
            .setErrorHandler( new SSApiBatchErrorHandler() )
            .setClient(new Ok3Client(okHttpClient))
            .setEndpoint( apiEndPoint ).build();
        integrationApi = apiAdaptor.create( SSApiBatchIntegration.class );
    }

}
