package com.realtech.socialsurvey.core.api.builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.api.SSApiBatchIntegration;
import com.realtech.socialsurvey.core.api.errorhandler.SSApiBatchErrorHandler;

import retrofit.RestAdapter;

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
        RestAdapter apiAdaptor = new RestAdapter.Builder().setLogLevel( RestAdapter.LogLevel.FULL )
            .setErrorHandler( new SSApiBatchErrorHandler() )
            .setEndpoint( apiEndPoint ).build();
        integrationApi = apiAdaptor.create( SSApiBatchIntegration.class );
    }

}
