package com.realtech.socialsurvey.core.integration.zillow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import retrofit.RestAdapter;
import com.realtech.socialsurvey.core.integration.zillow.errorhandlers.ZillowHttpErrorHandler;


@Component
public class ZillowIntergrationApiBuilder implements InitializingBean
{

    private static final Logger LOG = LoggerFactory.getLogger( ZillowIntergrationApiBuilder.class );

    private ZillowIntegrationApi zillowApi;

    @Value ( "${ZILLOW_ENDPOINT}")
    private String zillowEndpoint;


    public ZillowIntegrationApi getZellowIntegrationApi()
    {
        return zillowApi;
    }


    @Override
    public void afterPropertiesSet() throws Exception
    {
        LOG.info( "Initialising rest builder" );
        RestAdapter zellowAdapter = new RestAdapter.Builder().setLogLevel( RestAdapter.LogLevel.FULL )
            .setEndpoint( zillowEndpoint ).setErrorHandler( new ZillowHttpErrorHandler() ).build();
        zillowApi = zellowAdapter.create( ZillowIntegrationApi.class );
    }
}
