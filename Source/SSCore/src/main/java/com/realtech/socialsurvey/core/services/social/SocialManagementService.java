package com.realtech.socialsurvey.core.services.social;

import java.util.List;
import java.util.Map;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.RequestToken;

import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;

import facebook4j.Facebook;
import facebook4j.FacebookException;


/**
 * Interface with methods defined to manage social networks
 */
public interface SocialManagementService
{
    /**
     * Returns the LinkedIn request token for a particular URL
     * 
     * @return
     */
    /*public LinkedInRequestToken getLinkedInRequestToken();
    public LinkedInOAuthService getLinkedInInstance();
    public LinkedInApiClientFactory getLinkedInApiClientFactory();*/

    public RequestToken getTwitterRequestToken( String serverBaseUrl ) throws TwitterException;


    public Twitter getTwitterInstance();


    public Facebook getFacebookInstance( String serverBaseUrl );


    /**
     * Adds the SocialMedia access tokens to mongo
     * 
     * @param user
     * @param accessToken
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     */
    public SocialMediaTokens updateSocialMediaTokens( String collection, OrganizationUnitSettings unitSettings,
        SocialMediaTokens mediaTokens ) throws InvalidInputException;


    public SocialMediaTokens updateAgentSocialMediaTokens( AgentSettings agentSettings, SocialMediaTokens mediaTokens )
        throws InvalidInputException;


    /**
     * Posts the survey on facebook
     * @param agentSettings
     * @param message
     * @param serverBaseUrl
     * @param companyId
     * @return
     * @throws InvalidInputException
     * @throws FacebookException
     */
    public boolean updateStatusIntoFacebookPage( OrganizationUnitSettings agentSettings, String message, String serverBaseUrl,
        long companyId ) throws InvalidInputException, FacebookException;


    /**
     * Post a tweet on behalf of the authorized user
     * @param agentSettings
     * @param message
     * @param companyId
     * @return
     * @throws InvalidInputException
     * @throws TwitterException
     */
    public boolean tweet( OrganizationUnitSettings agentSettings, String message, long companyId )
        throws InvalidInputException, TwitterException;


    public Map<String, List<OrganizationUnitSettings>> getSettingsForBranchesAndRegionsInHierarchy( long agentId )
        throws InvalidInputException;


    public List<OrganizationUnitSettings> getBranchAndRegionSettingsForUser( long userId );


    public boolean updateLinkedin( OrganizationUnitSettings agentSettings, String message, String linkedinProfileUrl,
        String linkedinMessageFeedback ) throws NonFatalException;


    public OrganizationUnitSettings disconnectSocialNetwork( String socialMedia, OrganizationUnitSettings unitSettings,
        String collectionName ) throws InvalidInputException;


    public SocialMediaTokens checkOrAddZillowLastUpdated( SocialMediaTokens mediaTokens ) throws InvalidInputException;


    void resetZillowCallCount();


    void updateZillowCallCount();


    int fetchZillowCallCount();


    public boolean postToSocialMedia( String agentName, String agentProfileLink, String custFirstName, String custLastName,
        long agentId, double rating, String customerEmail, String feedback, boolean isAbusive, String serverBaseUrl,
        boolean onlyPostToSocialSurvey ) throws NonFatalException;


    /**
     * Method to add entry to social connections history
     *
     * @param entityType
     * @param entityId
     * @param mediaTokens
     * @param socialMedia
     * @param action
     * @throws InvalidInputException
     * @throws ProfileNotFoundException
     */
    void updateSocialConnectionsHistory( String entityType, long entityId, SocialMediaTokens mediaTokens, String socialMedia,
        String action ) throws InvalidInputException, ProfileNotFoundException;

}
// JIRA SS-34 BY RM02 BOC
