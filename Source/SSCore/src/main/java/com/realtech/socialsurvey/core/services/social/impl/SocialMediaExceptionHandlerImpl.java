package com.realtech.socialsurvey.core.services.social.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.core.services.social.SocialMediaExceptionHandler;

import facebook4j.FacebookException;

public class SocialMediaExceptionHandlerImpl implements SocialMediaExceptionHandler
{
    
    Logger LOG = LoggerFactory.getLogger( SocialMediaExceptionHandlerImpl.class );

    @Override
    public void handleFacebookException( FacebookException e )
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void handleLinkedinException( FacebookException e )
    {
        // TODO Auto-generated method stub
        
    }
    
    

}
