package com.realtech.socialsurvey.web.api.builder;

import com.realtech.socialsurvey.web.api.SSApiIntegration;
import com.realtech.socialsurvey.web.api.errorhandler.SSApiErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import retrofit.RestAdapter;


@Component
public class SSApiIntergrationBuilder implements InitializingBean
{
    private static final Logger LOG = LoggerFactory.getLogger( SSApiIntergrationBuilder.class );

    private SSApiIntegration integrationApi;

    @Value ( "${SSAPI_ENDPOINT}")
    private String apiEndPoint;


    public SSApiIntegration getIntegrationApi()
    {
        return integrationApi;
    }


    @Override
    public void afterPropertiesSet() throws Exception
    {
        LOG.info( "Initialising rest builder" );
        RestAdapter apiAdaptor = new RestAdapter.Builder().setLogLevel( RestAdapter.LogLevel.FULL )
            .setErrorHandler( new SSApiErrorHandler() )
            .setEndpoint( apiEndPoint ).build();
        integrationApi = apiAdaptor.create( SSApiIntegration.class );
    }
}
