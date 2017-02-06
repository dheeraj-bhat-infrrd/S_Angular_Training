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

    private ZillowIntegrationAgentApi zillowAgentApi;
    
    private ZillowIntegrationLenderApi zillowLenderApi;

    @Value ( "${ZILLOW_AGENT_API_ENDPOINT}")
    private String zillowAgentApiEndpoint;
    
    
    @Value ( "${ZILLOW_LENDER_API_ENDPOINT}")
    private String zillowLenderApiEndpoint;


    public ZillowIntegrationAgentApi getZillowIntegrationAgentApi()
    {
        return zillowAgentApi;
    }
    
    public ZillowIntegrationLenderApi getZillowIntegrationLenderApi()
    {
        return zillowLenderApi;
    }


    @Override
    public void afterPropertiesSet() throws Exception
    {
        LOG.info( "Initialising rest builder" );
        RestAdapter zellowAgentAdapter = new RestAdapter.Builder().setLogLevel( RestAdapter.LogLevel.FULL )
            .setEndpoint( zillowAgentApiEndpoint ).setErrorHandler( new ZillowHttpErrorHandler() ).build();
        zillowAgentApi = zellowAgentAdapter.create( ZillowIntegrationAgentApi.class );
        
        
        RestAdapter zellowLenderAdapter = new RestAdapter.Builder().setLogLevel( RestAdapter.LogLevel.FULL )
            .setEndpoint( zillowLenderApiEndpoint ).setErrorHandler( new ZillowHttpErrorHandler() ).build();
        zillowLenderApi = zellowLenderAdapter.create( ZillowIntegrationLenderApi.class );
    }
}
