package com.realtech.socialsurvey.core.integration.dotloop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import retrofit.RestAdapter;
import com.realtech.socialsurvey.core.integration.pos.errorhandlers.DotLoopHttpErrorHandler;


@Component
public class DotloopIntergrationApiBuilder implements InitializingBean
{

    private static final Logger LOG = LoggerFactory.getLogger( DotloopIntergrationApiBuilder.class );

    private DotloopIntegrationApi dotloopIntegrationApi;

    @Value ( "${DOTLOOP_ENDPOINT}")
    private String dotloopEndpoint;


    public DotloopIntegrationApi getDotloopIntegrationApi()
    {
        return dotloopIntegrationApi;
    }


    @Override
    public void afterPropertiesSet() throws Exception
    {
        LOG.info( "Initialising rest builder" );
        RestAdapter dotloopAdapter = new RestAdapter.Builder().setLogLevel( RestAdapter.LogLevel.FULL )
            .setEndpoint( dotloopEndpoint ).setErrorHandler(new DotLoopHttpErrorHandler()).build();
        dotloopIntegrationApi = dotloopAdapter.create( DotloopIntegrationApi.class );
    }
}
