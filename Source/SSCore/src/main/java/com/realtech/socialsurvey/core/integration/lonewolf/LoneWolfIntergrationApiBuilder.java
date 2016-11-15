package com.realtech.socialsurvey.core.integration.lonewolf;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.integration.pos.errorhandlers.LoneWolfHttpErrorHandler;
import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;


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
        
        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(120, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(120, TimeUnit.SECONDS);
        
        LOG.info( "Initialising rest builder" );
        RestAdapter loneWolfAdapter = new RestAdapter.Builder().setLogLevel( RestAdapter.LogLevel.FULL )
            .setClient(new OkClient(okHttpClient)).setEndpoint( loneWolfEndpoint ).setErrorHandler( new LoneWolfHttpErrorHandler() ).build();
        loneWolfIntegrationApi = loneWolfAdapter.create( LoneWolfIntegrationApi.class );
    }
}
