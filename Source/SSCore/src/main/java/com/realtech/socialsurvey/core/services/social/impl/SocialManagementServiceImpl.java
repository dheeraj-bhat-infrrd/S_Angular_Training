package com.realtech.socialsurvey.core.services.social.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.social.SocialManagementService;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.auth.AccessToken;

/**
 * JIRA:SS-34 BY RM02 Implementation for User management services
 */
@DependsOn("generic")
@Component
public class SocialManagementServiceImpl implements SocialManagementService, InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(SocialManagementServiceImpl.class);

	@Autowired
	private OrganizationUnitSettingsDao organizationUnitSettingsDao;

	@Autowired
	private UserManagementService userManagementService;

	@Autowired
	private UserProfileDao userProfileDao;

	// Facebook
	@Value("${FB_CLIENT_ID}")
	private String facebookClientId;
	@Value("${FB_CLIENT_SECRET}")
	private String facebookAppSecret;
	@Value("${FB_SCOPE}")
	private String facebookScope;
	@Value("${FB_REDIRECT_URI}")
	private String facebookRedirectUri;

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
	/*
	 * @Override public LinkedInRequestToken getLinkedInRequestToken() { LinkedInOAuthService
	 * oauthService = getLinkedInInstance(); LinkedInRequestToken requestToken =
	 * oauthService.getOAuthRequestToken(linkedinRedirectUri); return requestToken; }
	 * @Override public LinkedInOAuthService getLinkedInInstance() { return
	 * LinkedInOAuthServiceFactory.getInstance().createLinkedInOAuthService(linkedInApiKey,
	 * linkedInApiSecret); } // LinkedIn data update public LinkedInApiClientFactory
	 * getLinkedInApiClientFactory() { return LinkedInApiClientFactory.newInstance(linkedInApiKey,
	 * linkedInApiSecret); }
	 */

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

		return new TwitterFactory(configuration).getInstance();
	}

	/**
	 * Returns the Facebook request token for a particular URL
	 * 
	 * @return
	 * @throws TwitterException
	 */
	@Override
	public Facebook getFacebookInstance() {
		facebook4j.conf.ConfigurationBuilder confBuilder = new facebook4j.conf.ConfigurationBuilder();
		confBuilder.setOAuthAppId(facebookClientId);
		confBuilder.setOAuthAppSecret(facebookAppSecret);
		confBuilder.setOAuthCallbackURL(facebookRedirectUri);
		confBuilder.setOAuthPermissions(facebookScope);
		facebook4j.conf.Configuration configuration = confBuilder.build();

		return new FacebookFactory(configuration).getInstance();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
	}

	// Social Media Tokens update
	@Override
	public SocialMediaTokens updateSocialMediaTokens(String collection, OrganizationUnitSettings unitSettings, SocialMediaTokens mediaTokens)
			throws InvalidInputException {
		if (mediaTokens == null) {
			throw new InvalidInputException("Social Tokens passed can not be null");
		}
		LOG.info("Updating Social Tokens information");
		organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(MongoOrganizationUnitSettingDaoImpl.KEY_SOCIAL_MEDIA_TOKENS,
				mediaTokens, unitSettings, collection);
		LOG.info("Social Tokens updated successfully");
		return mediaTokens;
	}

	@Override
	public SocialMediaTokens updateAgentSocialMediaTokens(AgentSettings agentSettings, SocialMediaTokens mediaTokens) throws InvalidInputException {
		if (mediaTokens == null) {
			throw new InvalidInputException("Social Tokens passed can not be null");
		}
		LOG.info("Updating Social Tokens information");
		organizationUnitSettingsDao.updateParticularKeyAgentSettings(MongoOrganizationUnitSettingDaoImpl.KEY_SOCIAL_MEDIA_TOKENS, mediaTokens,
				agentSettings);
		LOG.info("Social Tokens updated successfully");
		return mediaTokens;
	}

	@Override
	public void updateStatusIntoFacebookPage(OrganizationUnitSettings agentSettings, String message) throws InvalidInputException, FacebookException {
		if (agentSettings == null) {
			throw new InvalidInputException("AgentSettings can not be null");
		}
		LOG.info("Updating Social Tokens information");
		Facebook facebook = getFacebookInstance();
		facebook.setOAuthAccessToken(new AccessToken(agentSettings.getSocialMediaTokens().getFacebookToken().getFacebookAccessToken(), null));
		facebook.postStatusMessage(message);
		LOG.info("Social Tokens updated successfully");
	}

	@Override
	public void tweet(OrganizationUnitSettings agentSettings, String message) throws InvalidInputException, TwitterException {
		if (agentSettings == null) {
			throw new InvalidInputException("AgentSettings can not be null");
		}
		LOG.info("Updating Social Tokens information");
		Twitter twitter = getTwitterInstance();
		twitter.setOAuthAccessToken(new twitter4j.auth.AccessToken(agentSettings.getSocialMediaTokens().getTwitterToken().getTwitterAccessToken(),
				agentSettings.getSocialMediaTokens().getTwitterToken().getTwitterAccessTokenSecret()));
		twitter.updateStatus("Status");
		LOG.info("Social Tokens updated successfully");
	}

	@Override
	public List<OrganizationUnitSettings> getSettingsForBranchesAndRegionsInHierarchy(long agentId) throws InvalidInputException {
		LOG.info("Method to get settings of branches and regions current agent belongs to, getSettingsForBranchesAndRegionsInHierarchy() started.");
		List<OrganizationUnitSettings> settings = new ArrayList<>();
		Set<Long> branchIds = new HashSet<>();
		Set<Long> regionIds = new HashSet<>();
		Map<String, Object> queries = new HashMap<>();
		queries.put(CommonConstants.USER_ID, agentId);
		queries.put(CommonConstants.PROFILE_MASTER_COLUMN,
				userManagementService.getProfilesMasterById(CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID));
		List<UserProfile> userProfiles = userProfileDao.findByKeyValue(UserProfile.class, queries);
		for (UserProfile userProfile : userProfiles) {
			branchIds.add(userProfile.getBranchId());
			regionIds.add(userProfile.getRegionId());
		}

		for (Long branchId : branchIds) {
			settings.add(organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(branchId, CommonConstants.BRANCH_SETTINGS_COLLECTION));
		}

		for (Long regionId : regionIds) {
			settings.add(organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(regionId, CommonConstants.REGION_SETTINGS_COLLECTION));
		}

		LOG.info("Method to get settings of branches and regions current agent belongs to, getSettingsForBranchesAndRegionsInHierarchy() finished.");
		return settings;
	}
}