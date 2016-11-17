package com.realtech.socialsurvey.core.integration.lonewolf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.integration.pos.errorhandlers.LoneWolfHttpErrorHandler;

import retrofit.RestAdapter;


@Component
public class LoneWolfIntergrationApiBuilder implements InitializingBean
{

    private static final Logger LOG = LoggerFactory.getLogger( LoneWolfIntergrationApiBuilder.class );

    private LoneWolfIntegrationApi loneWolfIntegrationApi;

    @Value ( "${LONEWOLF_ENDPOINT}")
    private String loneWolfEndpoint;


    public String getLoneWolfEndpoint()
    {
        return loneWolfEndpoint;
    }


    public void setLoneWolfEndpoint( String loneWolfEndpoint )
    {
        this.loneWolfEndpoint = loneWolfEndpoint;
    }


    public LoneWolfIntegrationApi getLoneWolfIntegrationApi()
    {
        return loneWolfIntegrationApi;
    }


    @Override
    public void afterPropertiesSet() throws Exception
    {


        LOG.info( "Initialising rest builder" );
        RestAdapter loneWolfAdapter = new RestAdapter.Builder().setLogLevel( RestAdapter.LogLevel.FULL )
            .setEndpoint( loneWolfEndpoint ).setErrorHandler( new LoneWolfHttpErrorHandler() ).build();
        loneWolfIntegrationApi = loneWolfAdapter.create( LoneWolfIntegrationApi.class );
    }
}
