package com.realtech.socialsurvey.core.integration.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import retrofit.RestAdapter;


/**
 * Stream api builder
 * @author nishit
 *
 */
@Component
public class StreamApiIntegrationBuilder implements InitializingBean
{

    private static final Logger LOG = LoggerFactory.getLogger( StreamApiIntegrationBuilder.class );

    @Value ( "${STREAM_API_ENDPOINT}")
    private String streamApiEndpoint;

    private StreamApi streamApi;


    public StreamApi getStreamApi()
    {
        return streamApi;
    }


    @Override
    public void afterPropertiesSet() throws Exception
    {
        LOG.info( "Initializing StreamApiIntegrationBuilder" );
        RestAdapter streamAdapter = new RestAdapter.Builder().setLogLevel( RestAdapter.LogLevel.BASIC )
            .setEndpoint( streamApiEndpoint ).setErrorHandler( new StreamApiErrorHandler() ).build();
        streamApi = streamAdapter.create( StreamApi.class );
    }

}
