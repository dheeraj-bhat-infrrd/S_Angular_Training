package com.realtech.socialsurvey.core.services.social;

import facebook4j.FacebookException;

public interface SocialMediaExceptionHandler
{
    
    void handleFacebookException(FacebookException e);
    
    void handleLinkedinException(FacebookException e);

}
