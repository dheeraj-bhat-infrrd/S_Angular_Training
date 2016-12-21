package com.realtech.socialsurvey.core.integration.vendasta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.integration.pos.errorhandlers.VendastaHttpErrorHandler;

import retrofit.RestAdapter;


@Component
public class VendastaApiIntegrationBuilder implements InitializingBean
{
    private static final Logger LOG = LoggerFactory.getLogger( VendastaApiIntegrationBuilder.class );

    private VendastaApiIntegration integrationApi;

    @Value ( "${VENDASTA_RM_API_ENDPOINT}")
    private String apiEndPoint;


    public VendastaApiIntegration getIntegrationApi()
    {
        return integrationApi;
    }


    @Override
    public void afterPropertiesSet() throws Exception
    {
        LOG.info( "Initialising rest builder" );
        RestAdapter apiAdaptor = new RestAdapter.Builder().setLogLevel( RestAdapter.LogLevel.FULL )
            .setErrorHandler( new VendastaHttpErrorHandler() ).setEndpoint( apiEndPoint ).build();
        integrationApi = apiAdaptor.create( VendastaApiIntegration.class );
    }
}
