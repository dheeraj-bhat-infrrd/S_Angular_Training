package com.realtech.socialsurvey.core.services.social;

import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;

import facebook4j.FacebookException;

public interface SocialMediaExceptionHandler
{
    
    void handleFacebookException(FacebookException e , OrganizationUnitSettings settings);
    
    void handleLinkedinException(OrganizationUnitSettings settings);

}
