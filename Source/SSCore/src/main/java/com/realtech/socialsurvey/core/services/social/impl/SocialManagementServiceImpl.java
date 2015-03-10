package com.realtech.socialsurvey.core.services.social.impl;

import java.util.Collection;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthServiceFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.FacebookToken;
import com.realtech.socialsurvey.core.entities.LinkedInToken;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.entities.TwitterToken;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.social.SocialManagementService;

/**
 * JIRA:SS-34 BY RM02 Implementation for User management services
 */
@DependsOn("generic")
@Component
public class SocialManagementServiceImpl implements SocialManagementService, InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(SocialManagementServiceImpl.class);

	@Autowired
	private OrganizationUnitSettingsDao organizationUnitSettingsDao;

	// LinkedIn
	@Value("${LINKED_IN_API_KEY}")
	private String linkedInApiKey;
	@Value("${LINKED_IN_API_SECRET}")
	private String linkedInApiSecret;
	@Value("${LINKED_IN_OAUTH_TOKEN}")
	private String linkedInOauthToken;
	@Value("${LINKED_IN_OAUTH_SECRET}")
	private String linkedInOauthSecret;
	@Value("${LINKED_IN_REDIRECT_URI}")
	private String linkedinRedirectUri;
	
	// Twitter
	@Value("${TWITTER_CONSUMER_KEY}")
	private String twitterConsumerKey;
	@Value("${TWITTER_CONSUMER_SECRET}")
	private String twitterConsumerSecret;
	@Value("${TWITTER_REDIRECT_URI}")
	private String twitterRedirectUri;

	/**
	 * Returns the LinkedIn request token for a particular URL
	 * 
	 * @return
	 */
	@Override
	public LinkedInRequestToken getLinkedInRequestToken() {
		LinkedInOAuthService oauthService = getLinkedInInstance();
		LinkedInRequestToken requestToken = oauthService.getOAuthRequestToken(linkedinRedirectUri);
		return requestToken;
	}

	@Override
	public LinkedInOAuthService getLinkedInInstance() {
		return LinkedInOAuthServiceFactory.getInstance().createLinkedInOAuthService(linkedInApiKey, linkedInApiSecret);
	}
	
	/**
	 * Returns the Twitter request token for a particular URL
	 * 
	 * @return
	 * @throws TwitterException 
	 */
	@Override
	public RequestToken getTwitterRequestToken() throws TwitterException {
		Twitter twitter = getTwitterInstance();
		RequestToken requestToken = twitter.getOAuthRequestToken(twitterRedirectUri);
		return requestToken;
	}

	@Override
	public Twitter getTwitterInstance() {
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthConsumerKey(twitterConsumerKey);
		builder.setOAuthConsumerSecret(twitterConsumerSecret);
		Configuration configuration = builder.build();
		TwitterFactory factory = new TwitterFactory(configuration);
		Twitter twitter = factory.getInstance();
		return twitter;
	}

	/**
	 * Adds the LinkedIn access tokens to the agent's settings in mongo
	 * 
	 * @param user
	 * @param accessToken
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 */
	@Override
	public void setLinkedInAccessTokenForUser(User user, String accessToken, String accessTokenSecret, Collection<AgentSettings> agentSettings)
			throws InvalidInputException, NoRecordsFetchedException {
		if (user == null) {
			LOG.error("setLinkedInAccessTokenForUser : user parameter is null!");
			throw new InvalidInputException("setLinkedInAccessTokenForUser : user parameter is null!");
		}
		if (accessToken == null || accessToken.isEmpty()) {
			LOG.error("setLinkedInAccessTokenForUser : accessToken parameter is null!");
			throw new InvalidInputException("setLinkedInAccessTokenForUser : accessToken parameter is null!");
		}

		LOG.info("Adding the LinkedIn access tokens to agent settings in mongo for user id : " + user.getUserId());
		Iterator<AgentSettings> settingsIterator = agentSettings.iterator();
		while (settingsIterator.hasNext()) {
			AgentSettings agentSetting = settingsIterator.next();
			LOG.debug("Setting the access token for settings with id : " + agentSetting.getId());
			SocialMediaTokens mediaTokens = agentSetting.getSocialMediaTokens();
			// Check if media tokens exist. If not, create them.
			if (mediaTokens == null) {
				LOG.debug("Media tokens do not exist. Creating them and adding the LinkedIn access token");
				mediaTokens = new SocialMediaTokens();
				mediaTokens.setLinkedInToken(new LinkedInToken());
				mediaTokens.getLinkedInToken().setLinkedInAccessToken(accessToken);
				mediaTokens.getLinkedInToken().setLinkedInAccessTokenSecret(accessTokenSecret);
				mediaTokens.getLinkedInToken().setLinkedInAccessTokenCreatedOn(System.currentTimeMillis());
			}
			else {
				LOG.debug("Updating the existing media tokens for LinkedIn");
				if (mediaTokens.getLinkedInToken() == null) {
					mediaTokens.setLinkedInToken(new LinkedInToken());
				}
				mediaTokens.getLinkedInToken().setLinkedInAccessToken(accessToken);
				mediaTokens.getLinkedInToken().setLinkedInAccessTokenSecret(accessTokenSecret);
				mediaTokens.getLinkedInToken().setLinkedInAccessTokenCreatedOn(System.currentTimeMillis());
			}
			LOG.debug("Updating the mongo collection with new LinkedIn access tokens for settings with id : " + agentSetting.getId());
			organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(CommonConstants.SOCIAL_MEDIA_TOKEN_MONGO_KEY, mediaTokens,
					agentSetting, CommonConstants.AGENT_SETTINGS_COLLECTION);
		}

		LOG.info("Agent settings successfully updated with LinkedIn access token");
	}
	
	/**
	 * Adds the Facebook access tokens to the agent's settings in mongo
	 * 
	 * @param user
	 * @param accessToken
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 */
	@Override
	public void setFacebookAccessTokenForUser(User user, String accessToken, long accessTokenExpiresOn, OrganizationUnitSettings companySettings)
			throws InvalidInputException, NoRecordsFetchedException {
		if (user == null) {
			LOG.error("setFacebookAccessTokenForUser : user parameter is null!");
			throw new InvalidInputException("setFacebookAccessTokenForUser : user parameter is null!");
		}
		if (accessToken == null || accessToken.isEmpty()) {
			LOG.error("setFacebookAccessTokenForUser : accessToken parameter is null!");
			throw new InvalidInputException("setFacebookAccessTokenForUser : accessToken parameter is null!");
		}

		LOG.info("Adding the facebook access tokens to agent settings in mongo for user id : " + user.getUserId());
		SocialMediaTokens mediaTokens = companySettings.getSocialMediaTokens();

		// Check if media tokens exist. If not, create them.
		if (mediaTokens == null) {
			LOG.debug("Media tokens do not exist. Creating them and adding the Facebook access token");
			mediaTokens = new SocialMediaTokens();
			mediaTokens.setFacebookToken(new FacebookToken());
			mediaTokens.getFacebookToken().setFacebookAccessToken(accessToken);
			mediaTokens.getFacebookToken().setFacebookAccessTokenCreatedOn(System.currentTimeMillis());
			mediaTokens.getFacebookToken().setFacebookAccessTokenExpiresOn(accessTokenExpiresOn);
		}
		else {
			LOG.debug("Updating the existing media tokens for Facebook");
			if (mediaTokens.getFacebookToken() == null) {
				mediaTokens.setFacebookToken(new FacebookToken());
			}
			mediaTokens.getFacebookToken().setFacebookAccessToken(accessToken);
			mediaTokens.getFacebookToken().setFacebookAccessTokenCreatedOn(System.currentTimeMillis());
			mediaTokens.getFacebookToken().setFacebookAccessTokenExpiresOn(accessTokenExpiresOn);
		}
		LOG.info("Updating the mongo collection with new Facebook access tokens for settings with id : " + companySettings.getId());
		organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(CommonConstants.SOCIAL_MEDIA_TOKEN_MONGO_KEY, mediaTokens,
				companySettings, MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION);

		LOG.info("Agent settings successfully updated with Facebook access token");
	}

	/**
	 * Adds the Twitter access tokens to the agent's settings in mongo
	 * 
	 * @param user
	 * @param accessToken
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 */
	@Override
	public void setTwitterAccessTokenForUser(User user, String accessToken, String accessTokenSecret, OrganizationUnitSettings companySettings)
			throws InvalidInputException, NoRecordsFetchedException {
		if (user == null) {
			LOG.error("setTwitterAccessTokenForUser : user parameter is null!");
			throw new InvalidInputException("setTwitterAccessTokenForUser : user parameter is null!");
		}
		if (accessToken == null || accessToken.isEmpty()) {
			LOG.error("setTwitterAccessTokenForUser : accessToken parameter is null!");
			throw new InvalidInputException("setTwitterAccessTokenForUser : accessToken parameter is null!");
		}

		LOG.info("Adding the twitter access tokens to agent settings in mongo for user id : " + user.getUserId());
		SocialMediaTokens mediaTokens = companySettings.getSocialMediaTokens();

		// Check if media tokens exist. If not, create them.
		if (mediaTokens == null) {
			LOG.debug("Media tokens do not exist. Creating them and adding the Twitter access token");
			mediaTokens = new SocialMediaTokens();
			mediaTokens.setTwitterToken(new TwitterToken());
			mediaTokens.getTwitterToken().setTwitterAccessToken(accessToken);
			mediaTokens.getTwitterToken().setTwitterAccessTokenSecret(accessTokenSecret);
			mediaTokens.getTwitterToken().setTwitterAccessTokenCreatedOn(System.currentTimeMillis());
		}
		else {
			LOG.debug("Updating the existing media tokens for Twitter");
			if (mediaTokens.getTwitterToken() == null) {
				mediaTokens.setTwitterToken(new TwitterToken());
			}
			mediaTokens.getTwitterToken().setTwitterAccessToken(accessToken);
			mediaTokens.getTwitterToken().setTwitterAccessTokenSecret(accessTokenSecret);
			mediaTokens.getTwitterToken().setTwitterAccessTokenCreatedOn(System.currentTimeMillis());
		}
		LOG.info("Updating the mongo collection with new Twitter access tokens for settings with id : " + companySettings.getId());
		organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(CommonConstants.SOCIAL_MEDIA_TOKEN_MONGO_KEY, mediaTokens,
				companySettings, MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION);

		LOG.info("Agent settings successfully updated with Facebook access token");
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
	}
}