package com.realtech.socialsurvey.api.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.header.writers.frameoptions.AllowFromStrategy;

public class SSOAllowFromStrategy implements AllowFromStrategy
{
    
    @Value ( "${SSO_SOURCE}")
    private String url;
    
    @Override
    public String getAllowFromValue( HttpServletRequest request )
    {
        return url;
    }

}
