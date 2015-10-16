package com.realtech.socialsurvey.core.services.social;

import java.util.concurrent.Future;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;


public interface SocialAsyncService
{

    public Future<OrganizationUnitSettings> linkedInDataUpdateAsync( String collection, OrganizationUnitSettings unitSettings,
        SocialMediaTokens mediaToken );


    public OrganizationUnitSettings linkedInDataUpdate( String collection, OrganizationUnitSettings unitSettings,
        SocialMediaTokens mediaToken );


    public OrganizationUnitSettings updateLinkedInProfileImage( String collection, OrganizationUnitSettings unitSettings );
}