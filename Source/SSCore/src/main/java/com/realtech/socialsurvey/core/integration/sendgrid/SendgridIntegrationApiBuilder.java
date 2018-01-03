package com.realtech.socialsurvey.core.integration.sendgrid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import retrofit.RestAdapter;

import com.realtech.socialsurvey.core.integration.zillow.errorhandlers.ZillowHttpErrorHandler;

@Component
public class SendgridIntegrationApiBuilder implements InitializingBean
{

    private static final Logger LOG = LoggerFactory.getLogger( SendgridIntegrationApiBuilder.class );

    private SendgridIntegrationApi sendgridIntegrationApi;
    

    @Value ( "${SENDGRID_API_ENDPOINT}")
    private String sendgridApiEndpoint;
    
    


    public SendgridIntegrationApi getSendgridIntegrationApi()
    {
        return sendgridIntegrationApi;
    }
    

    @Override
    public void afterPropertiesSet() throws Exception
    {
        LOG.info( "Initialising rest builder" );
        RestAdapter sendgridAdapter = new RestAdapter.Builder().setLogLevel( RestAdapter.LogLevel.FULL )
            .setEndpoint( sendgridApiEndpoint ).setErrorHandler( new ZillowHttpErrorHandler() ).build();
        sendgridIntegrationApi = sendgridAdapter.create( SendgridIntegrationApi.class );
       
    }


}
