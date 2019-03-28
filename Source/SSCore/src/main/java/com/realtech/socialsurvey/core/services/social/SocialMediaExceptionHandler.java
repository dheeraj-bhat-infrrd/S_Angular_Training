package com.realtech.socialsurvey.core.services.social;

import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;

import facebook4j.FacebookException;

public interface SocialMediaExceptionHandler
{
    
    void handleFacebookException(FacebookException e , OrganizationUnitSettings settings , String collectionName);
    
    void handleLinkedinException(OrganizationUnitSettings settings , String collectionName);

    String generateAndSendSocialMedialTokenExpiryMail( OrganizationUnitSettings settings , String collectionName ,  String socialMediaType);

    public void handleLinkedinV2Exception( OrganizationUnitSettings settings, String collectionName );

}
