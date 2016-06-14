package com.realtech.socialsurvey.web.common;

import com.realtech.socialsurvey.core.entities.LinkedInToken;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class TokenHandler
{
    private static final Logger LOG = LoggerFactory.getLogger( TokenHandler.class );
    public SocialMediaTokens updateLinkedInToken( String accessToken, SocialMediaTokens mediaTokens, String profileLink )
    {
        LOG.debug( "Method updateLinkedInToken() called from SocialManagementController" );
        if ( mediaTokens == null ) {
            LOG.debug( "Media tokens do not exist. Creating them and adding the LinkedIn access token" );
            mediaTokens = new SocialMediaTokens();
            mediaTokens.setLinkedInToken( new LinkedInToken() );
        } else {
            if ( mediaTokens.getLinkedInToken() == null ) {
                LOG.debug( "Updating the existing media tokens for LinkedIn" );
                mediaTokens.setLinkedInToken( new LinkedInToken() );
            }
        }

        mediaTokens.getLinkedInToken().setLinkedInAccessToken( accessToken );
        if ( profileLink != null ) {
            profileLink = profileLink.split( "&" )[0];
            mediaTokens.getLinkedInToken().setLinkedInPageLink( profileLink );
        }
        mediaTokens.getLinkedInToken().setLinkedInAccessTokenCreatedOn( System.currentTimeMillis() );

        LOG.debug( "Method updateLinkedInToken() finished from SocialManagementController" );
        return mediaTokens;
    }
}
