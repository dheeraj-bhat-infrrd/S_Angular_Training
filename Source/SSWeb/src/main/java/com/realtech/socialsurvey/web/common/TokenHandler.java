package com.realtech.socialsurvey.web.common;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.entities.LinkedInToken;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;


/**
 * @author manish
 *
 */
@Component
public class TokenHandler
{
    private static final Logger LOG = LoggerFactory.getLogger( TokenHandler.class );
    
    /**
     * Method to update LinkedIn token in SocialMediaTokens 
     * @param accessToken - accessToken
     * @param mediaTokens - Unit setting mediaTokens
     * @param profileLink - LinkedIn user profile link
     * @param expiresIn - token expiry time.
     * @param version - LinkedIn API version
     * @return
     */
    public SocialMediaTokens updateLinkedInToken( String accessToken, SocialMediaTokens mediaTokens, String profileLink,
        long expiresIn, String version, String linkedinId )
    {
        LOG.info( "Method updateLinkedInToken() called for API version {}", version );
        if ( mediaTokens == null ) {
            LOG.debug( "Media tokens do not exist. Creating them and adding the LinkedIn access token" );
            mediaTokens = new SocialMediaTokens();
        }

        LinkedInToken linkedInToken = mediaTokens.getLinkedInV2Token();

        if ( linkedInToken == null ) {
            linkedInToken = new LinkedInToken();
        }

        linkedInToken.setLinkedInId( linkedinId );
        linkedInToken.setVersion( version );
        linkedInToken.setLinkedInAccessToken( accessToken );
        linkedInToken.setLinkedInAccessTokenExpiresIn( expiresIn );
        linkedInToken.setLinkedInAccessTokenCreatedOn( System.currentTimeMillis() );
        //update expiry email alert detail
        linkedInToken.setTokenExpiryAlertSent( false );
        linkedInToken.setTokenExpiryAlertEmail( null );
        linkedInToken.setTokenExpiryAlertTime( null );
        if ( profileLink != null ) {
            profileLink = profileLink.split( "&" )[0];
            linkedInToken.setLinkedInPageLink( profileLink );
        }

        if ( mediaTokens.getLinkedInToken() != null ) {
            linkedInToken.setLinkedInPageLink( mediaTokens.getLinkedInToken().getLinkedInPageLink() );
        }
        
        if ( StringUtils.isNotEmpty( mediaTokens.getLinkedInProfileUrl()) ) {
            linkedInToken.setLinkedInPageLink( mediaTokens.getLinkedInProfileUrl() );
        }
        
        mediaTokens.setLinkedInV2Token( linkedInToken );
        LOG.info( "Method updateLinkedInToken() finished for API version {}", version );
        return mediaTokens;
    }
}

