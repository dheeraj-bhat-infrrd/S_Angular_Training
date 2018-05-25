package com.realtech.socialsurvey.stream.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.realtech.socialsurvey.stream.services.AuthenticationService;

@Service
public class AuthenticationServiceImpl implements AuthenticationService
{
    private static final Logger LOG = LoggerFactory.getLogger( AuthenticationServiceImpl.class );


    @Value ( "${api.security.key}")
    private String apiAccessKey;


    @Override
    public boolean isApiAccessKeyValid( String providedApiAccessKey )
    {
        LOG.debug( "Validating api access key" );
        return apiAccessKey.equals( providedApiAccessKey ) ? true : false;
    }

}
