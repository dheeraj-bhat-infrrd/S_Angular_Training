package com.realtech.socialsurvey.core.services.social;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.RequestToken;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import facebook4j.Facebook;

/**
 * Interface with methods defined to manage social networks
 */
public interface SocialManagementService {
	/**
	 * Returns the LinkedIn request token for a particular URL
	 * 
	 * @return
	 */
	public LinkedInRequestToken getLinkedInRequestToken();
	public LinkedInOAuthService getLinkedInInstance();
	public LinkedInApiClientFactory getLinkedInApiClientFactory();

	public RequestToken getTwitterRequestToken() throws TwitterException;
	public Twitter getTwitterInstance();

	public Facebook getFacebookInstance();

	/**
	 * Adds the Facebook access tokens to the agent's settings in mongo
	 * 
	 * @param user
	 * @param accessToken
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 */
	public void setFacebookAccessTokenForUser(User user, String accessToken, long accessTokenExpiresOn, OrganizationUnitSettings companySettings)
			throws InvalidInputException, NoRecordsFetchedException;

	public void setTwitterAccessTokenForUser(User user, String accessToken, String accessTokenSecret, OrganizationUnitSettings companySettings)
			throws InvalidInputException, NoRecordsFetchedException;

	/*public void setLinkedInAccessTokenForUser(User user, String accessToken, String accessTokenSecret, Collection<AgentSettings> agentSettings)
	throws InvalidInputException, NoRecordsFetchedException;*/

	public SocialMediaTokens updateSocialMediaTokens(String collection, OrganizationUnitSettings unitSettings, SocialMediaTokens mediaTokens)
			throws InvalidInputException;

	public SocialMediaTokens updateAgentSocialMediaTokens(AgentSettings agentSettings, SocialMediaTokens mediaTokens)
			throws InvalidInputException;
}
// JIRA SS-34 BY RM02 BOC
