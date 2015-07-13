package com.realtech.socialsurvey.core.services.social;

import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.RequestToken;

import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;

import facebook4j.Facebook;
import facebook4j.FacebookException;

/**
 * Interface with methods defined to manage social networks
 */
public interface SocialManagementService {
	/**
	 * Returns the LinkedIn request token for a particular URL
	 * 
	 * @return
	 */
	/*public LinkedInRequestToken getLinkedInRequestToken();
	public LinkedInOAuthService getLinkedInInstance();
	public LinkedInApiClientFactory getLinkedInApiClientFactory();*/

	public RequestToken getTwitterRequestToken(String serverBaseUrl) throws TwitterException;
	public Twitter getTwitterInstance();

	public Facebook getFacebookInstance(String serverBaseUrl);

	/**
	 * Adds the SocialMedia access tokens to mongo
	 * 
	 * @param user
	 * @param accessToken
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 */
	public SocialMediaTokens updateSocialMediaTokens(String collection, OrganizationUnitSettings unitSettings, SocialMediaTokens mediaTokens)
			throws InvalidInputException;

	public SocialMediaTokens updateAgentSocialMediaTokens(AgentSettings agentSettings, SocialMediaTokens mediaTokens)
			throws InvalidInputException;
	
	public boolean updateStatusIntoFacebookPage(OrganizationUnitSettings agentSettings, String message, String serverBaseUrl) throws InvalidInputException,
			FacebookException;

	public boolean tweet(OrganizationUnitSettings agentSettings, String message) throws InvalidInputException, TwitterException;
	
	public List<OrganizationUnitSettings> getSettingsForBranchesAndRegionsInHierarchy(long agentId) throws InvalidInputException;
	
	public List<OrganizationUnitSettings> getBranchAndRegionSettingsForUser(long userId);
	
	public boolean updateLinkedin(OrganizationUnitSettings agentSettings, String message, String linkedinProfileUrl, String linkedinMessageFeedback) throws NonFatalException;
	
	public OrganizationUnitSettings disconnectSocialNetwork(String socialMedia, OrganizationUnitSettings unitSettings, String collectionName) throws InvalidInputException;

}
// JIRA SS-34 BY RM02 BOC
