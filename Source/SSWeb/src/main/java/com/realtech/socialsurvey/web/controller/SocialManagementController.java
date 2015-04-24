package com.realtech.socialsurvey.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.FacebookToken;
import com.realtech.socialsurvey.core.entities.LinkedInToken;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.entities.SocialProfileToken;
import com.realtech.socialsurvey.core.entities.TwitterToken;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.entities.UserSettings;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.social.SocialAsyncService;
import com.realtech.socialsurvey.core.services.social.SocialManagementService;
import com.realtech.socialsurvey.core.services.social.api.Google2Api;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.web.common.ErrorResponse;
import com.realtech.socialsurvey.web.common.JspResolver;
import facebook4j.Facebook;
import facebook4j.FacebookException;

/**
 * Controller to manage social media oauth and pull/push posts
 */
@Controller
public class SocialManagementController {

	private static final Logger LOG = LoggerFactory.getLogger(SocialManagementController.class);

	@Autowired
	private SessionHelper sessionHelper;

	@Autowired
	private SocialAsyncService socialAsyncService;

	@Autowired
	private SocialManagementService socialManagementService;

	@Autowired
	private UserManagementService userManagementService;

	// Facebook
	@Value("${FB_REDIRECT_URI}")
	private String facebookRedirectUri;

	// LinkedIn
	@Value("${LINKED_IN_REST_API_URI}")
	private String linkedInRestApiUri;
	@Value("${LINKED_IN_API_KEY}")
	private String linkedInApiKey;
	@Value("${LINKED_IN_API_SECRET}")
	private String linkedInApiSecret;
	@Value("${LINKED_IN_REDIRECT_URI}")
	private String linkedinRedirectUri;
	@Value("${LINKED_IN_AUTH_URI}")
	private String linkedinAuthUri;
	@Value("${LINKED_IN_ACCESS_URI}")
	private String linkedinAccessUri;

	// Google
	@Value("${GOOGLE_API_KEY}")
	private String googleApiKey;
	@Value("${GOOGLE_API_SECRET}")
	private String googleApiSecret;
	@Value("${GOOGLE_REDIRECT_URI}")
	private String googleApiRedirectUri;
	@Value("${GOOGLE_API_SCOPE}")
	private String googleApiScope;
	@Value("${GOOGLE_SHARE_URI}")
	private String googleShareUri;

	// Yelp
	@Value("${YELP_REDIRECT_URI}")
	private String yelpRedirectUri;
	
	/**
	 * Returns the social authorization page
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/socialauth", method = RequestMethod.GET)
	public String getSocialAuthPage(Model model, HttpServletRequest request) {
		LOG.info("Method getSocialAuthPage() called from SocialManagementController");
		HttpSession session = request.getSession(false);
		if (session == null) {
			LOG.error("Session is null!");
		}

		// AuthUrl for diff social networks
		String socialNetwork = request.getParameter("social");
		String socialFlow = request.getParameter("flow");
		
		session.removeAttribute(CommonConstants.SOCIAL_FLOW);
		switch (socialNetwork) {

			// Building facebook authUrl
			case "facebook":
				Facebook facebook = socialManagementService.getFacebookInstance();

				// Setting authUrl in model
				session.setAttribute(CommonConstants.SOCIAL_REQUEST_TOKEN, facebook);
				model.addAttribute(CommonConstants.SOCIAL_AUTH_URL, facebook.getOAuthAuthorizationURL(facebookRedirectUri));

				LOG.info("Returning the facebook authUrl : " + facebook.getOAuthAuthorizationURL(facebookRedirectUri));
				break;

			// Building twitter authUrl
			case "twitter":
				RequestToken requestToken;
				try {
					requestToken = socialManagementService.getTwitterRequestToken();
				}
				catch (Exception e) {
					LOG.error("Exception while getting request token. Reason : " + e.getMessage(), e);
					model.addAttribute("message", e.getMessage());
					return JspResolver.ERROR_PAGE;
				}

				// We will keep the request token in session
				session.setAttribute(CommonConstants.SOCIAL_REQUEST_TOKEN, requestToken);
				model.addAttribute(CommonConstants.SOCIAL_AUTH_URL, requestToken.getAuthorizationURL());

				LOG.info("Returning the twitter authorizationurl : " + requestToken.getAuthorizationURL());
				break;

			// Building linkedin authUrl
			case "linkedin":
				if (socialFlow != null && !socialFlow.isEmpty()) {
					session.setAttribute(CommonConstants.SOCIAL_FLOW, socialFlow);
				}

				StringBuilder linkedInAuth = new StringBuilder(linkedinAuthUri).append("?response_type=").append("code");
				linkedInAuth.append("&client_id=").append(linkedInApiKey);
				linkedInAuth.append("&redirect_uri=").append(linkedinRedirectUri);
				linkedInAuth.append("&state=").append("SOCIALSURVEY");

				model.addAttribute(CommonConstants.SOCIAL_AUTH_URL, linkedInAuth.toString());

				LOG.info("Returning the linkedin authorizationurl : " + linkedInAuth.toString());
				break;

			// Building Google authUrl
			case "google":
				OAuthService service = new ServiceBuilder().provider(Google2Api.class).apiKey(googleApiKey).apiSecret(googleApiSecret)
						.callback(googleApiRedirectUri).scope(googleApiScope).build();

				String redirectURL = service.getAuthorizationUrl(null);
				model.addAttribute(CommonConstants.SOCIAL_AUTH_URL, redirectURL);

				LOG.info("Returning the google authorizationurl : " + redirectURL);
				break;

			// TODO Building Yelp authUrl
			case "yelp":
				break;

			// TODO Building RSS authUrl
			case "rss":
				break;

			default:
				LOG.error("Social Network Type invalid in getSocialAuthPage");
		}

		model.addAttribute(CommonConstants.MESSAGE, CommonConstants.YES);
		return JspResolver.SOCIAL_AUTH_MESSAGE;
	}

	/**
	 * The url that Facebook send request to with the oauth verification code
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/facebookauth", method = RequestMethod.GET)
	public String authenticateFacebookAccess(Model model, HttpServletRequest request) {
		LOG.info("Facebook authentication url requested");
		HttpSession session = request.getSession(false);

		try {
			UserSettings userSettings = (UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);
			UserProfile selectedProfile = (UserProfile) session.getAttribute(CommonConstants.USER_PROFILE);
			if (userSettings == null || selectedProfile == null) {
				LOG.error("authenticateFacebookAccess : userSettings not found in session!");
				throw new NonFatalException("authenticateFacebookAccess : userSettings not found in session!");
			}

			// On auth error
			String errorCode = request.getParameter("error");
			if (errorCode != null) {
				LOG.error("Error code : " + errorCode);
				model.addAttribute(CommonConstants.ERROR, CommonConstants.YES);
				return JspResolver.SOCIAL_AUTH_MESSAGE;
			}

			// Getting Oauth accesstoken for facebook
			String oauthCode = request.getParameter("code");
			Facebook facebook = (Facebook) session.getAttribute(CommonConstants.SOCIAL_REQUEST_TOKEN);

			facebook4j.auth.AccessToken accessToken = null;
			try {
				accessToken = facebook.getOAuthAccessToken(oauthCode, facebookRedirectUri);
			}
			catch (FacebookException e) {
				LOG.error("Error while creating access token for facebook: " + e.getLocalizedMessage(), e);
			}

			// Storing token
			SocialMediaTokens mediaTokens;
			int profilesMaster = selectedProfile.getProfilesMaster().getProfileId();
			if (profilesMaster == CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID) {
				OrganizationUnitSettings companySettings = userSettings.getCompanySettings();
				if (companySettings == null) {
					throw new InvalidInputException("No company settings found in current session");
				}
				mediaTokens = companySettings.getSocialMediaTokens();
				mediaTokens = updateFacebookToken(accessToken, mediaTokens);
				mediaTokens = socialManagementService.updateSocialMediaTokens(MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION,
						companySettings, mediaTokens);
				companySettings.setSocialMediaTokens(mediaTokens);
				userSettings.setCompanySettings(companySettings);
			}
			else if (profilesMaster == CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID) {
				long regionId = selectedProfile.getRegionId();
				OrganizationUnitSettings regionSettings = userSettings.getRegionSettings().get(regionId);
				if (regionSettings == null) {
					throw new InvalidInputException("No Region settings found in current session");
				}
				mediaTokens = regionSettings.getSocialMediaTokens();
				mediaTokens = updateFacebookToken(accessToken, mediaTokens);
				mediaTokens = socialManagementService.updateSocialMediaTokens(MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION,
						regionSettings, mediaTokens);
				regionSettings.setSocialMediaTokens(mediaTokens);
				userSettings.getRegionSettings().put(regionId, regionSettings);
			}
			else if (profilesMaster == CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID) {
				long branchId = selectedProfile.getBranchId();
				OrganizationUnitSettings branchSettings = userSettings.getBranchSettings().get(branchId);
				if (branchSettings == null) {
					throw new InvalidInputException("No Branch settings found in current session");
				}
				mediaTokens = branchSettings.getSocialMediaTokens();
				mediaTokens = updateFacebookToken(accessToken, mediaTokens);
				mediaTokens = socialManagementService.updateSocialMediaTokens(MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION,
						branchSettings, mediaTokens);
				branchSettings.setSocialMediaTokens(mediaTokens);
				userSettings.getBranchSettings().put(branchId, branchSettings);
			}
			else if (profilesMaster == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID) {
				AgentSettings agentSettings = userSettings.getAgentSettings();
				if (agentSettings == null) {
					throw new InvalidInputException("No Agent settings found in current session");
				}

				mediaTokens = agentSettings.getSocialMediaTokens();
				mediaTokens = updateFacebookToken(accessToken, mediaTokens);
				mediaTokens = socialManagementService.updateAgentSocialMediaTokens(agentSettings, mediaTokens);
				agentSettings.setSocialMediaTokens(mediaTokens);
				userSettings.setAgentSettings(agentSettings);
			}
			else {
				throw new InvalidInputException("Invalid input exception occurred while creating access token for facebook", DisplayMessageConstants.GENERAL_ERROR);
			}
		}
		catch (Exception e) {
			session.removeAttribute(CommonConstants.SOCIAL_REQUEST_TOKEN);
			LOG.error("Exception while getting facebook access token. Reason : " + e.getMessage(), e);
			return JspResolver.SOCIAL_AUTH_MESSAGE;
		}

		// Updating attributes
		session.removeAttribute(CommonConstants.SOCIAL_REQUEST_TOKEN);
		model.addAttribute(CommonConstants.SUCCESS_ATTRIBUTE, CommonConstants.YES);

		LOG.info("Facebook Access tokens obtained and added to mongo successfully!");
		return JspResolver.SOCIAL_AUTH_MESSAGE;
	}

	private SocialMediaTokens updateFacebookToken(facebook4j.auth.AccessToken accessToken, SocialMediaTokens mediaTokens) {
		LOG.debug("Method updateFacebookToken() called from SocialManagementController");
		if (mediaTokens == null) {
			LOG.debug("Media tokens do not exist. Creating them and adding the facebook access token");
			mediaTokens = new SocialMediaTokens();
			mediaTokens.setFacebookToken(new FacebookToken());
		}
		else {
			LOG.debug("Updating the existing media tokens for facebook");
			if (mediaTokens.getFacebookToken() == null) {
				mediaTokens.setFacebookToken(new FacebookToken());
			}
		}

		mediaTokens.getFacebookToken().setFacebookAccessToken(accessToken.getToken());
		mediaTokens.getFacebookToken().setFacebookAccessTokenCreatedOn(System.currentTimeMillis());
		mediaTokens.getFacebookToken().setFacebookAccessTokenExpiresOn(accessToken.getExpires());

		LOG.debug("Method updateFacebookToken() finished from SocialManagementController");
		return mediaTokens;
	}

	/**
	 * The url that twitter send request to with the oauth verification code
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/twitterauth", method = RequestMethod.GET)
	public String authenticateTwitterAccess(Model model, HttpServletRequest request) {
		LOG.info("Twitter authentication url requested");
		HttpSession session = request.getSession(false);

		try {
			UserSettings userSettings = (UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);
			UserProfile selectedProfile = (UserProfile) session.getAttribute(CommonConstants.USER_PROFILE);
			if (userSettings == null || selectedProfile == null) {
				LOG.error("authenticateTwitterAccess : userSettings not found in session!");
				throw new NonFatalException("authenticateTwitterAccess : userSettings not found in session!");
			}

			// On auth error
			String errorCode = request.getParameter("oauth_problem");
			if (errorCode != null) {
				LOG.error("Error code : " + errorCode);
				model.addAttribute(CommonConstants.ERROR, CommonConstants.YES);
				return JspResolver.SOCIAL_AUTH_MESSAGE;
			}

			// Getting Oauth accesstoken for Twitter
			AccessToken accessToken = null;
			while (null == accessToken) {
				Twitter twitter = socialManagementService.getTwitterInstance();

				String oauthVerifier = request.getParameter("oauth_verifier");
				RequestToken requestToken = (RequestToken) session.getAttribute(CommonConstants.SOCIAL_REQUEST_TOKEN);
				try {
					accessToken = twitter.getOAuthAccessToken(requestToken, oauthVerifier);
				}
				catch (TwitterException te) {
					if (TwitterException.UNAUTHORIZED == te.getStatusCode()) {
						LOG.info("Unable to get the access token. Reason: UNAUTHORISED");
					}
					else {
						LOG.error(te.getErrorMessage());
					}
				}
			}

			// Storing token
			SocialMediaTokens mediaTokens;
			int profilesMaster = selectedProfile.getProfilesMaster().getProfileId();
			if (profilesMaster == CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID) {
				OrganizationUnitSettings companySettings = userSettings.getCompanySettings();
				if (companySettings == null) {
					throw new InvalidInputException("No company settings found in current session");
				}
				mediaTokens = companySettings.getSocialMediaTokens();
				mediaTokens = updateTwitterToken(accessToken, mediaTokens);
				mediaTokens = socialManagementService.updateSocialMediaTokens(MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION,
						companySettings, mediaTokens);
				companySettings.setSocialMediaTokens(mediaTokens);
				userSettings.setCompanySettings(companySettings);
			}
			else if (profilesMaster == CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID) {
				long regionId = selectedProfile.getRegionId();
				OrganizationUnitSettings regionSettings = userSettings.getRegionSettings().get(regionId);
				if (regionSettings == null) {
					throw new InvalidInputException("No Region settings found in current session");
				}
				mediaTokens = regionSettings.getSocialMediaTokens();
				mediaTokens = updateTwitterToken(accessToken, mediaTokens);
				mediaTokens = socialManagementService.updateSocialMediaTokens(MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION,
						regionSettings, mediaTokens);
				regionSettings.setSocialMediaTokens(mediaTokens);
				userSettings.getRegionSettings().put(regionId, regionSettings);
			}
			else if (profilesMaster == CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID) {
				long branchId = selectedProfile.getBranchId();
				OrganizationUnitSettings branchSettings = userSettings.getBranchSettings().get(branchId);
				if (branchSettings == null) {
					throw new InvalidInputException("No Branch settings found in current session");
				}
				mediaTokens = branchSettings.getSocialMediaTokens();
				mediaTokens = updateTwitterToken(accessToken, mediaTokens);
				mediaTokens = socialManagementService.updateSocialMediaTokens(MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION,
						branchSettings, mediaTokens);
				branchSettings.setSocialMediaTokens(mediaTokens);
				userSettings.getBranchSettings().put(branchId, branchSettings);
			}
			else if (profilesMaster == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID) {
				AgentSettings agentSettings = userSettings.getAgentSettings();
				if (agentSettings == null) {
					throw new InvalidInputException("No Agent settings found in current session");
				}

				mediaTokens = agentSettings.getSocialMediaTokens();
				mediaTokens = updateTwitterToken(accessToken, mediaTokens);
				mediaTokens = socialManagementService.updateAgentSocialMediaTokens(agentSettings, mediaTokens);
				agentSettings.setSocialMediaTokens(mediaTokens);
				userSettings.setAgentSettings(agentSettings);
			}
			else {
				throw new InvalidInputException("Invalid input exception occurred while creating access token for twitter", DisplayMessageConstants.GENERAL_ERROR);
			}
		}
		catch (Exception e) {
			session.removeAttribute(CommonConstants.SOCIAL_REQUEST_TOKEN);
			LOG.error("Exception while getting twitter access token. Reason : " + e.getMessage(), e);
			return JspResolver.SOCIAL_AUTH_MESSAGE;
		}

		// Updating attributes
		session.removeAttribute(CommonConstants.SOCIAL_REQUEST_TOKEN);
		model.addAttribute(CommonConstants.SUCCESS_ATTRIBUTE, CommonConstants.YES);

		LOG.info("Twitter Access tokens obtained and added to mongo successfully!");
		return JspResolver.SOCIAL_AUTH_MESSAGE;
	}

	private SocialMediaTokens updateTwitterToken(AccessToken accessToken, SocialMediaTokens mediaTokens) {
		LOG.debug("Method updateTwitterToken() called from SocialManagementController");
		if (mediaTokens == null) {
			LOG.debug("Media tokens do not exist. Creating them and adding the twitter access token");
			mediaTokens = new SocialMediaTokens();
			mediaTokens.setTwitterToken(new TwitterToken());
		}
		else {
			LOG.debug("Updating the existing media tokens for twitter");
			if (mediaTokens.getTwitterToken() == null) {
				mediaTokens.setTwitterToken(new TwitterToken());
			}
		}

		mediaTokens.getTwitterToken().setTwitterAccessToken(accessToken.getToken());
		mediaTokens.getTwitterToken().setTwitterAccessTokenSecret(accessToken.getTokenSecret());
		mediaTokens.getTwitterToken().setTwitterAccessTokenCreatedOn(System.currentTimeMillis());

		LOG.debug("Method updateTwitterToken() finished from SocialManagementController");
		return mediaTokens;
	}

	/**
	 * The url that LinkedIn send request to with the oauth verification code
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/linkedinauth", method = RequestMethod.GET)
	public String authenticateLinkedInAccess(Model model, HttpServletRequest request) {
		LOG.info("Method authenticateLinkedInAccess() called from SocialManagementController");
		HttpSession session = request.getSession(false);

		try {
			UserSettings userSettings = (UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);
			UserProfile selectedProfile = (UserProfile) session.getAttribute(CommonConstants.USER_PROFILE);
			if (userSettings == null || selectedProfile == null) {
				LOG.error("authenticateLinkedInAccess : userSettings not found in session!");
				throw new NonFatalException("authenticateLinkedInAccess : userSettings not found in session!");
			}

			// On auth error
			String errorCode = request.getParameter("error");
			if (errorCode != null) {
				LOG.error("Error code : " + errorCode);
				model.addAttribute(CommonConstants.ERROR, CommonConstants.YES);
				return JspResolver.SOCIAL_AUTH_MESSAGE;
			}

			// Getting Oauth accesstoken for Linkedin
			String oauthCode = request.getParameter("code");
			List<NameValuePair> params = new ArrayList<NameValuePair>(2);
			params.add(new BasicNameValuePair("grant_type", "authorization_code"));
			params.add(new BasicNameValuePair("code", oauthCode));
			params.add(new BasicNameValuePair("redirect_uri", linkedinRedirectUri));
			params.add(new BasicNameValuePair("client_id", linkedInApiKey));
			params.add(new BasicNameValuePair("client_secret", linkedInApiSecret));

			HttpClient httpclient = HttpClientBuilder.create().build();
			HttpPost httpPost = new HttpPost(linkedinAccessUri);
			httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

			// Fetching oauth token from json response
			String accessTokenStr = httpclient.execute(httpPost, new BasicResponseHandler());
			Map<String, Object> map = new Gson().fromJson(accessTokenStr, new TypeToken<Map<String, String>>() {}.getType());
			String accessToken = (String) map.get("access_token");

			SocialMediaTokens mediaTokens;
			int profilesMaster = selectedProfile.getProfilesMaster().getProfileId();
			if (profilesMaster == CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID) {
				OrganizationUnitSettings companySettings = userSettings.getCompanySettings();
				if (companySettings == null) {
					throw new InvalidInputException("No company settings found in current session");
				}
				mediaTokens = companySettings.getSocialMediaTokens();
				mediaTokens = updateLinkedInToken(accessToken, mediaTokens);
				mediaTokens = socialManagementService.updateSocialMediaTokens(MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION,
						companySettings, mediaTokens);
				companySettings.setSocialMediaTokens(mediaTokens);
				userSettings.setCompanySettings(companySettings);
			}
			else if (profilesMaster == CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID) {
				long regionId = selectedProfile.getRegionId();
				OrganizationUnitSettings regionSettings = userSettings.getRegionSettings().get(regionId);
				if (regionSettings == null) {
					throw new InvalidInputException("No Region settings found in current session");
				}
				mediaTokens = regionSettings.getSocialMediaTokens();
				mediaTokens = updateLinkedInToken(accessToken, mediaTokens);
				mediaTokens = socialManagementService.updateSocialMediaTokens(MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION,
						regionSettings, mediaTokens);
				regionSettings.setSocialMediaTokens(mediaTokens);
				userSettings.getRegionSettings().put(regionId, regionSettings);
			}
			else if (profilesMaster == CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID) {
				long branchId = selectedProfile.getBranchId();
				OrganizationUnitSettings branchSettings = userSettings.getBranchSettings().get(branchId);
				if (branchSettings == null) {
					throw new InvalidInputException("No Branch settings found in current session");
				}
				mediaTokens = branchSettings.getSocialMediaTokens();
				mediaTokens = updateLinkedInToken(accessToken, mediaTokens);
				mediaTokens = socialManagementService.updateSocialMediaTokens(MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION,
						branchSettings, mediaTokens);
				branchSettings.setSocialMediaTokens(mediaTokens);
				userSettings.getBranchSettings().put(branchId, branchSettings);
			}
			else if (profilesMaster == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID) {
				AgentSettings agentSettings = userSettings.getAgentSettings();
				if (agentSettings == null) {
					throw new InvalidInputException("No Agent settings found in current session");
				}

				mediaTokens = agentSettings.getSocialMediaTokens();
				mediaTokens = updateLinkedInToken(accessToken, mediaTokens);
				mediaTokens = socialManagementService.updateAgentSocialMediaTokens(agentSettings, mediaTokens);
				agentSettings.setSocialMediaTokens(mediaTokens);
				userSettings.setAgentSettings(agentSettings);

				// starting async service for data update from linkedin
				socialAsyncService.linkedInDataUpdate(MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings,
						mediaTokens.getLinkedInToken());
			}
			else {
				throw new InvalidInputException("Invalid input exception occurred while creating access token for linkedin", DisplayMessageConstants.GENERAL_ERROR);
			}
		}
		catch (Exception e) {
			LOG.error("Exception while getting linkedin access token. Reason : " + e.getMessage(), e);
			return JspResolver.SOCIAL_AUTH_MESSAGE;
		}

		// Updating attributes
		model.addAttribute(CommonConstants.SUCCESS_ATTRIBUTE, CommonConstants.YES);

		LOG.info("Method authenticateLinkedInAccess() finished from SocialManagementController");
		return JspResolver.SOCIAL_AUTH_MESSAGE;
	}

	private SocialMediaTokens updateLinkedInToken(String accessToken, SocialMediaTokens mediaTokens) {
		LOG.debug("Method updateLinkedInToken() called from SocialManagementController");
		if (mediaTokens == null) {
			LOG.debug("Media tokens do not exist. Creating them and adding the LinkedIn access token");
			mediaTokens = new SocialMediaTokens();
			mediaTokens.setLinkedInToken(new LinkedInToken());
		}
		else {
			if (mediaTokens.getLinkedInToken() == null) {
				LOG.debug("Updating the existing media tokens for LinkedIn");
				mediaTokens.setLinkedInToken(new LinkedInToken());
			}
		}

		mediaTokens.getLinkedInToken().setLinkedInAccessToken(accessToken);
		mediaTokens.getLinkedInToken().setLinkedInAccessTokenCreatedOn(System.currentTimeMillis());

		LOG.debug("Method updateLinkedInToken() finished from SocialManagementController");
		return mediaTokens;
	}

	/**
	 * The url that Google send request to with the oauth verification code
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/googleauth", method = RequestMethod.GET)
	public String authenticateGoogleAccess(Model model, HttpServletRequest request) {
		LOG.info("Method authenticateGoogleAccess() called from SocialManagementController");
		HttpSession session = request.getSession(false);

		try {
			UserSettings userSettings = (UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);
			UserProfile selectedProfile = (UserProfile) session.getAttribute(CommonConstants.USER_PROFILE);
			if (userSettings == null || selectedProfile == null) {
				LOG.error("authenticateGoogleAccess : userSettings not found in session!");
				throw new NonFatalException("authenticateGoogleAccess : userSettings not found in session!");
			}

			// On auth error
			String errorCode = request.getParameter("error");
			if (errorCode != null) {
				LOG.error("Error code : " + errorCode);
				model.addAttribute(CommonConstants.ERROR, CommonConstants.YES);
				return JspResolver.SOCIAL_AUTH_MESSAGE;
			}

			// Getting Oauth accesstoken for Google+
			String OAuthCode = request.getParameter("code");
			OAuthService service = new ServiceBuilder().provider(Google2Api.class).apiKey(googleApiKey).apiSecret(googleApiSecret)
					.callback(googleApiRedirectUri).scope(googleApiScope).build();
			Token accessToken = service.getAccessToken(null, new Verifier(OAuthCode));
			LOG.info("Token: " + accessToken.getToken());

			// Storing access token
			SocialMediaTokens mediaTokens;
			int profilesMaster = selectedProfile.getProfilesMaster().getProfileId();
			if (profilesMaster == CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID) {
				OrganizationUnitSettings companySettings = userSettings.getCompanySettings();
				if (companySettings == null) {
					throw new InvalidInputException("No company settings found in current session");
				}
				mediaTokens = companySettings.getSocialMediaTokens();
				mediaTokens = updateGoogleToken(accessToken, mediaTokens);
				mediaTokens = socialManagementService.updateSocialMediaTokens(MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION,
						companySettings, mediaTokens);
				companySettings.setSocialMediaTokens(mediaTokens);
				userSettings.setCompanySettings(companySettings);
			}
			else if (profilesMaster == CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID) {
				long regionId = selectedProfile.getRegionId();
				OrganizationUnitSettings regionSettings = userSettings.getRegionSettings().get(regionId);
				if (regionSettings == null) {
					throw new InvalidInputException("No Region settings found in current session");
				}
				mediaTokens = regionSettings.getSocialMediaTokens();
				mediaTokens = updateGoogleToken(accessToken, mediaTokens);
				mediaTokens = socialManagementService.updateSocialMediaTokens(MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION,
						regionSettings, mediaTokens);
				regionSettings.setSocialMediaTokens(mediaTokens);
				userSettings.getRegionSettings().put(regionId, regionSettings);
			}
			else if (profilesMaster == CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID) {
				long branchId = selectedProfile.getBranchId();
				OrganizationUnitSettings branchSettings = userSettings.getBranchSettings().get(branchId);
				if (branchSettings == null) {
					throw new InvalidInputException("No Branch settings found in current session");
				}
				mediaTokens = branchSettings.getSocialMediaTokens();
				mediaTokens = updateGoogleToken(accessToken, mediaTokens);
				mediaTokens = socialManagementService.updateSocialMediaTokens(MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION,
						branchSettings, mediaTokens);
				branchSettings.setSocialMediaTokens(mediaTokens);
				userSettings.getBranchSettings().put(branchId, branchSettings);
			}
			else if (profilesMaster == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID) {
				AgentSettings agentSettings = userSettings.getAgentSettings();
				if (agentSettings == null) {
					throw new InvalidInputException("No Agent settings found in current session");
				}

				mediaTokens = agentSettings.getSocialMediaTokens();
				mediaTokens = updateGoogleToken(accessToken, mediaTokens);
				mediaTokens = socialManagementService.updateAgentSocialMediaTokens(agentSettings, mediaTokens);
				agentSettings.setSocialMediaTokens(mediaTokens);
				userSettings.setAgentSettings(agentSettings);
			}
			else {
				throw new InvalidInputException("Invalid input exception occurred while creating access token for google", DisplayMessageConstants.GENERAL_ERROR);
			}
		}
		catch (Exception e) {
			session.removeAttribute(CommonConstants.SOCIAL_REQUEST_TOKEN);
			LOG.error("Exception while getting google access token. Reason : " + e.getMessage(), e);
			return JspResolver.SOCIAL_AUTH_MESSAGE;
		}

		// Updating attributes
		session.removeAttribute(CommonConstants.SOCIAL_REQUEST_TOKEN);
		model.addAttribute(CommonConstants.SUCCESS_ATTRIBUTE, CommonConstants.YES);

		LOG.info("Method authenticateGoogleAccess() finished from SocialManagementController");
		return JspResolver.SOCIAL_AUTH_MESSAGE;
	}

	private SocialMediaTokens updateGoogleToken(Token accessToken, SocialMediaTokens mediaTokens) {
		LOG.debug("Method updateGoogleToken() called from SocialManagementController");
		if (mediaTokens == null) {
			LOG.debug("Media tokens do not exist. Creating them and adding the Google access token");
			mediaTokens = new SocialMediaTokens();
			mediaTokens.setGoogleToken(new SocialProfileToken());
		}
		else {
			LOG.debug("Updating the existing media tokens for google plus");
			if (mediaTokens.getGoogleToken() == null) {
				mediaTokens.setGoogleToken(new SocialProfileToken());
			}
		}
		mediaTokens.getGoogleToken().setAccessToken(accessToken.getToken());
		mediaTokens.getGoogleToken().setAccessTokenSecret(accessToken.getSecret());
		mediaTokens.getGoogleToken().setAccessTokenCreatedOn(System.currentTimeMillis());

		LOG.debug("Method updateGoogleToken() finished from SocialManagementController");
		return mediaTokens;
	}
	
	@ResponseBody
	@RequestMapping(value = "/postonfacebook", method = RequestMethod.GET)
	public String postToFacebook(HttpServletRequest request) {
		LOG.info("Method to post feedback of customer to facebook started.");
		String agentName = request.getParameter("agentName");
		String custFirstName = request.getParameter("firstName");
		String custLastName = request.getParameter("lastName");
		String review = request.getParameter("review");
		
		double rating = 0;
		try {
			String ratingStr = request.getParameter("score");
			rating = Double.parseDouble(ratingStr);
		}
		catch (NumberFormatException e) {
			LOG.error("Number format exception caught in postToFacebook() while trying to convert agent Id. Nested exception is ", e);
			return e.getMessage();
		}

		User user = sessionHelper.getCurrentUser();
		List<OrganizationUnitSettings> settings = socialManagementService.getBranchAndRegionSettingsForUser(user.getUserId());
		rating = Math.round(rating * 100) / 100;
		String facebookMessage = rating + "-Star Survey Response from " + custFirstName + " " + custLastName + " for " + agentName
				+ " on Social Survey \n" + review;
		facebookMessage = facebookMessage.replaceAll("null", "");
		
		for (OrganizationUnitSettings setting : settings) {
			try {
				if (setting != null)
					socialManagementService.updateStatusIntoFacebookPage(setting, facebookMessage);
			}
			catch (FacebookException | InvalidInputException e) {
				LOG.error("FacebookException/InvalidInputException caught in postToFacebook() while trying to post to facebook. Nested excption is ", e);
			}
		}
		
		LOG.info("Method to post feedback of customer to facebook finished.");
		return "Successfully posted to all the your facebook profiles";
	}

	@ResponseBody
	@RequestMapping(value = "/postontwitter", method = RequestMethod.GET)
	public String postToTwitter(HttpServletRequest request) {
		LOG.info("Method to post feedback of customer on twitter started.");
		try {
			String agentName = request.getParameter("agentName");
			String custFirstName = request.getParameter("firstName");
			String custLastName = request.getParameter("lastName");
			String agentIdStr = request.getParameter("agentId");
			
			double rating = 0;
			try {
				String ratingStr = request.getParameter("score");
				rating = Double.parseDouble(ratingStr);
			}
			catch (NumberFormatException e) {
				LOG.error("Number format exception caught in postToTwitter() while trying to convert agent Id. Nested exception is ", e);
				return e.getMessage();
			}

			User user = sessionHelper.getCurrentUser();
			List<OrganizationUnitSettings> settings = socialManagementService.getBranchAndRegionSettingsForUser(user.getUserId());
			rating = Math.round(rating * 100) / 100;
			String twitterMessage = rating + "-Star Survey Response from " + custFirstName + custLastName + " for " + agentName
					+ " on @SocialSurvey - view at www.social-survey.com/" + agentIdStr;
			twitterMessage = twitterMessage.replaceAll("null", "");

			for (OrganizationUnitSettings setting : settings) {
				try {
					if (setting != null)
						socialManagementService.tweet(setting, twitterMessage);
				}
				catch (TwitterException e) {
					LOG.error("TwitterException caught in postToTwitter() while trying to post to twitter. Nested excption is ", e);
					throw new NonFatalException(
							"TwitterException caught in postToTwitter() while trying to post to twitter in postToTwitter(). Nested exception is ", e);
				}
			}

		}
		catch (NonFatalException e) {
			LOG.error("Non fatal Exception caught in postToTwitter() while trying to post to social networking sites. Nested excption is ", e);
			return e.getMessage();
		}
		
		LOG.info("Method to post feedback of customer to various pages of twitter finished.");
		return "Successfully posted to all the your twitter profiles";
	}

	@ResponseBody
	@RequestMapping(value = "/postonlinkedin", method = RequestMethod.GET)
	public String postToLinkedin(HttpServletRequest request) {
		LOG.info("Method to post feedback of customer on twitter started.");
		String agentName = request.getParameter("agentName");
		String custFirstName = request.getParameter("firstName");
		String custLastName = request.getParameter("lastName");
		String agentIdStr = request.getParameter("agentId");
		
		double rating = 0;
		try {
			String ratingStr = request.getParameter("score");
			rating = Double.parseDouble(ratingStr);
		}
		catch (NumberFormatException e) {
			LOG.error("Number format exception caught in postToLinkedin() while trying to convert agent Id. Nested exception is ", e);
			return e.getMessage();
		}

		User user = sessionHelper.getCurrentUser();
		List<OrganizationUnitSettings> settings = socialManagementService.getBranchAndRegionSettingsForUser(user.getUserId());
		rating = Math.round(rating * 100) / 100;
		String message = rating + "-Star Survey Response from " + custFirstName + custLastName + " for " + agentName
				+ " on SocialSurvey - view at www.social-survey.com/" + agentIdStr;
		message = message.replaceAll("null", "");
		
		for (OrganizationUnitSettings setting : settings) {
			try {
				if (setting != null)
					socialManagementService.updateLinkedin(setting, message);
			}
			catch (NonFatalException e) {
				LOG.error("NonFatalException caught in postToLinkedin() while trying to post to twitter. Nested excption is ", e);
			}
		}

		LOG.info("Method to post feedback of customer to various pages of twitter finished.");
		return "Successfully posted to all the your twitter profiles";
	}

	@ResponseBody
	@RequestMapping(value = "/getyelplink", method = RequestMethod.GET)
	public String getYelpLink(HttpServletRequest request) {
		LOG.info("Method to get Yelp details, getYelpLink() started.");
		Map<String, String> yelpUrl = new HashMap<String, String>();

		try {
			sessionHelper.getCanonicalSettings(request.getSession(false));
			OrganizationUnitSettings settings = (OrganizationUnitSettings) request.getSession(false).getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);

			if (settings.getSocialMediaTokens() != null && settings.getSocialMediaTokens().getYelpToken() != null){
				yelpUrl.put("host", yelpRedirectUri);
				yelpUrl.put("relativePath", settings.getSocialMediaTokens().getYelpToken().getYelpPageLink());
			}
		}
		catch (InvalidInputException | NoRecordsFetchedException e) {
			LOG.error("Exception occured in getYelpLink() while trying to post into Yelp.");
			ErrorResponse response = new ErrorResponse();
			response.setErrCode("Error while trying to post on Yelp.");
			response.setErrMessage(e.getMessage());
			return new Gson().toJson(response);
		}
		
		LOG.info("Method to get Yelp details, getYelpLink() finished.");
		return new Gson().toJson(yelpUrl);
	}
	
	@ResponseBody
	@RequestMapping(value = "/getgooglepluslink", method = RequestMethod.GET)
	public String getGooglePlusLink(HttpServletRequest request) {
		LOG.info("Method to get Google details, getGooglePlusLink() started.");
		Map<String, String> googleUrl = new HashMap<String, String>();

		try {
			sessionHelper.getCanonicalSettings(request.getSession(false));
			OrganizationUnitSettings settings = (OrganizationUnitSettings) request.getSession(false).getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);

			if (settings.getSocialMediaTokens() != null && settings.getSocialMediaTokens().getGoogleToken() != null){
				googleUrl.put("host", googleShareUri);
				googleUrl.put("relativePath", settings.getSocialMediaTokens().getGoogleToken().getProfileLink());
			}
		}
		catch (InvalidInputException | NoRecordsFetchedException e) {
			LOG.error("Exception occured in getGooglePlusLink() while trying to post into Google.");
			ErrorResponse response = new ErrorResponse();
			response.setErrCode("Error while trying to post on Google.");
			response.setErrMessage(e.getMessage());
			return new Gson().toJson(response);
		}
		
		LOG.info("Method to get Google details, getGooglePlusLink() finished.");
		return new Gson().toJson(googleUrl);
	}
}