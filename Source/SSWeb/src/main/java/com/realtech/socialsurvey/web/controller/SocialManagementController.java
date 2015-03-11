package com.realtech.socialsurvey.web.controller;

import java.util.EnumSet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.enumeration.ProfileField;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;
import com.google.code.linkedinapi.schema.Person;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.LinkedInToken;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserSettings;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.social.SocialManagementService;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
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
	private SocialManagementService socialManagementService;

	@Autowired
	private ProfileManagementService profileManagementService;

	// Facebook
	@Value("${FB_REDIRECT_URI}")
	private String facebookRedirectUri;

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
				LinkedInRequestToken linkedInRequestToken;
				try {
					linkedInRequestToken = socialManagementService.getLinkedInRequestToken();
				}
				catch (Exception e) {
					LOG.error("Exception while getting request token. Reason : " + e.getMessage(), e);
					model.addAttribute("message", e.getMessage());
					return JspResolver.ERROR_PAGE;
				}

				// We will keep the request token in session
				session.setAttribute(CommonConstants.SOCIAL_REQUEST_TOKEN, linkedInRequestToken);
				model.addAttribute(CommonConstants.SOCIAL_AUTH_URL, linkedInRequestToken.getAuthorizationUrl());

				LOG.info("Returning the linkedin authorizationurl : " + linkedInRequestToken.getAuthorizationUrl());
				break;

			// Building yelp authUrl
			case "yelp":
				// TODO

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
	public String authenticateFacebookAccessCode(Model model, HttpServletRequest request) {
		LOG.info("Facebook authentication url requested");
		UserSettings currentUserSettings;
		HttpSession session = request.getSession(false);

		try {
			User user = sessionHelper.getCurrentUser();
			if (session == null) {
				LOG.error("authenticateLinkedInAccess : Session object is null!");
				throw new NonFatalException("authenticateLinkedInAccess : Session object is null!");
			}

			if (session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION) == null) {
				LOG.error("authenticateLinkedInAccess : user canonical settings not found in session!");
				throw new NonFatalException("authenticateLinkedInAccess : user canonical settings not found in session!");
			}
			else {
				currentUserSettings = (UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);
				if (currentUserSettings.getCompanySettings() == null) {
					LOG.error("authenticateLinkedInAccess : agent settings not found in session!");
					throw new NonFatalException("authenticateLinkedInAccess : agent settings not found in session!");
				}
			}

			// String errorReason = request.getParameter("error_reason");
			String errorCode = request.getParameter("error");
			if (errorCode != null) {
				LOG.error("Error code : " + errorCode);
				model.addAttribute(CommonConstants.ERROR, CommonConstants.YES);
				return JspResolver.SOCIAL_AUTH_MESSAGE;
			}

			// Building facebook authUrl
			String oauthCode = request.getParameter("code");
			Facebook facebook = (Facebook) session.getAttribute(CommonConstants.SOCIAL_REQUEST_TOKEN);

			facebook4j.auth.AccessToken accessToken = null;
			try {
				accessToken = facebook.getOAuthAccessToken(oauthCode, facebookRedirectUri);
			}
			catch (FacebookException e) {
				LOG.error("Error while creating access token " + e.getLocalizedMessage(), e);
			}

			socialManagementService.setFacebookAccessTokenForUser(user, accessToken.getToken(), accessToken.getExpires(),
					currentUserSettings.getCompanySettings());
			model.addAttribute(CommonConstants.SUCCESS_ATTRIBUTE, CommonConstants.YES);
		}
		catch (Exception e) {
			session.removeAttribute(CommonConstants.SOCIAL_REQUEST_TOKEN);
			LOG.error("Exception while getting access token. Reason : " + e.getMessage(), e);
			return JspResolver.SOCIAL_AUTH_MESSAGE;
		}

		session.removeAttribute(CommonConstants.SOCIAL_REQUEST_TOKEN);
		LOG.info("Access tokens obtained and added to mongo successfully!");
		return JspResolver.SOCIAL_AUTH_MESSAGE;
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
		UserSettings currentUserSettings;

		try {
			User user = sessionHelper.getCurrentUser();
			if (session == null) {
				LOG.error("authenticateTwitterAccess : Session object is null!");
				throw new NonFatalException("authenticateTwitterAccess : Session object is null!");
			}

			if (session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION) == null) {
				LOG.error("authenticateTwitterAccess : user canonical settings not found in session!");
				throw new NonFatalException("authenticateTwitterAccess : user canonical settings not found in session!");
			}
			else {
				currentUserSettings = (UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);
				if (currentUserSettings.getAgentSettings() == null) {
					LOG.error("authenticateTwitterAccess : agent settings not found in session!");
					throw new NonFatalException("authenticateTwitterAccess : agent settings not found in session!");
				}
			}

			String errorCode = request.getParameter("oauth_problem");
			if (errorCode != null) {
				LOG.error("Error code : " + errorCode);
				model.addAttribute(CommonConstants.ERROR, CommonConstants.YES);
				return JspResolver.SOCIAL_AUTH_MESSAGE;
			}

			AccessToken accessToken = null;
			while (null == accessToken) {
				Twitter twitter = socialManagementService.getTwitterInstance();

				String oauthVerifier = request.getParameter("oauth_verifier");
				RequestToken requestToken = (RequestToken) session.getAttribute(CommonConstants.SOCIAL_REQUEST_TOKEN);
				try {
					accessToken = twitter.getOAuthAccessToken(requestToken, oauthVerifier);
				}
				catch (TwitterException te) {
					if (401 == te.getStatusCode()) {
						LOG.info("Unable to get the access token. Reason: UNAUTHORISED");
					}
					else {
						LOG.error(te.getErrorMessage());
					}
				}
			}
			socialManagementService.setTwitterAccessTokenForUser(user, accessToken.getToken(), accessToken.getTokenSecret(),
					currentUserSettings.getCompanySettings());
		}
		catch (Exception e) {
			session.removeAttribute(CommonConstants.SOCIAL_REQUEST_TOKEN);
			LOG.error(e.getMessage(), e);
			return JspResolver.SOCIAL_AUTH_MESSAGE;
		}

		session.removeAttribute(CommonConstants.SOCIAL_REQUEST_TOKEN);
		model.addAttribute(CommonConstants.SUCCESS_ATTRIBUTE, CommonConstants.YES);

		LOG.info("Access tokens obtained and added to mongo successfully!");
		return JspResolver.SOCIAL_AUTH_MESSAGE;
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
		UserSettings userSettings;
		
		try {
			if (session == null) {
				LOG.error("authenticateLinkedInAccess : Session object is null!");
				throw new NonFatalException("authenticateLinkedInAccess : Session object is null!");
			}

			String errorCode = request.getParameter("oauth_problem");
			if (errorCode != null) {
				LOG.error("Error code : " + errorCode);
				model.addAttribute(CommonConstants.ERROR, CommonConstants.YES);
				return JspResolver.SOCIAL_AUTH_MESSAGE;
			}

			// Getting Oauth accesstoken for Linkedin
			LinkedInOAuthService oauthService = socialManagementService.getLinkedInInstance();
			String oauthVerifier = request.getParameter("oauth_verifier");
			LOG.debug("LinkedIn oauth verfier : " + oauthVerifier);
			LinkedInRequestToken requestToken = (LinkedInRequestToken) session.getAttribute(CommonConstants.SOCIAL_REQUEST_TOKEN);
			LinkedInAccessToken accessToken = oauthService.getOAuthAccessToken(requestToken, oauthVerifier);

			User user = sessionHelper.getCurrentUser();
			userSettings = (UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);
			if (userSettings == null) {
				LOG.error("authenticateLinkedInAccess : user canonical settings not found in session!");
				throw new NonFatalException("authenticateLinkedInAccess : user canonical settings not found in session!");
			}

			SocialMediaTokens mediaTokens = null;
			long userId = user.getUserId();
			if (user.isCompanyAdmin()) {
				OrganizationUnitSettings unitSettings = userSettings.getCompanySettings();
				if (unitSettings == null) {
					throw new InvalidInputException("No company settings found in current session");
				}
				mediaTokens = unitSettings.getSocialMediaTokens();
				mediaTokens = updateLinkedInToken(accessToken, mediaTokens);
				mediaTokens = socialManagementService.updateSocialMediaTokens(MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION,
						unitSettings, mediaTokens);
				unitSettings.setSocialMediaTokens(mediaTokens);
				userSettings.setCompanySettings(unitSettings);
			}
			else if (user.isRegionAdmin()) {
				OrganizationUnitSettings regionSettings = userSettings.getRegionSettings().get(userId);
				if (regionSettings == null) {
					throw new InvalidInputException("No Region settings found in current session");
				}
				mediaTokens = regionSettings.getSocialMediaTokens();
				mediaTokens = updateLinkedInToken(accessToken, mediaTokens);
				mediaTokens = socialManagementService.updateSocialMediaTokens(MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION,
						regionSettings, mediaTokens);
				regionSettings.setSocialMediaTokens(mediaTokens);
				userSettings.getRegionSettings().put(userId, regionSettings);
			}
			else if (user.isBranchAdmin()) {
				OrganizationUnitSettings branchSettings = userSettings.getBranchSettings().get(userId);
				if (branchSettings == null) {
					throw new InvalidInputException("No Branch settings found in current session");
				}
				mediaTokens = branchSettings.getSocialMediaTokens();
				mediaTokens = updateLinkedInToken(accessToken, mediaTokens);
				mediaTokens = socialManagementService.updateSocialMediaTokens(MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION,
						branchSettings, mediaTokens);
				branchSettings.setSocialMediaTokens(mediaTokens);
				userSettings.getBranchSettings().put(userId, branchSettings);
			}
			else if (user.isAgent()) {
				AgentSettings agentSettings = userSettings.getAgentSettings().get(userId);
				if (agentSettings == null) {
					throw new InvalidInputException("No Agent settings found in current session");
				}
				mediaTokens = agentSettings.getSocialMediaTokens();
				mediaTokens = updateLinkedInToken(accessToken, mediaTokens);
				mediaTokens = socialManagementService.updateAgentSocialMediaTokens(agentSettings, mediaTokens);
				agentSettings.setSocialMediaTokens(mediaTokens);
				userSettings.getAgentSettings().put(userId, agentSettings);
			}
			else {
				throw new InvalidInputException("Invalid input exception occurred in editing LockSettings.", DisplayMessageConstants.GENERAL_ERROR);
			}
		}
		catch (Exception e) {
			session.removeAttribute(CommonConstants.SOCIAL_REQUEST_TOKEN);
			LOG.error(e.getMessage(), e);
			return JspResolver.SOCIAL_AUTH_MESSAGE;
		}
		
		// Updating attributes
		session.removeAttribute(CommonConstants.SOCIAL_REQUEST_TOKEN);
		session.setAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION, userSettings);
		model.addAttribute(CommonConstants.SUCCESS_ATTRIBUTE, CommonConstants.YES);
		
		LOG.info("Method authenticateLinkedInAccess() finished from SocialManagementController");
		return JspResolver.SOCIAL_AUTH_MESSAGE;
	}

	private SocialMediaTokens updateLinkedInToken(LinkedInAccessToken accessToken, SocialMediaTokens mediaTokens) {
		LOG.debug("Method updateLinkedInToken() called from SocialManagementController");
		if (mediaTokens == null) {
			LOG.debug("Media tokens do not exist. Creating them and adding the LinkedIn access token");
			mediaTokens = new SocialMediaTokens();
			mediaTokens.setLinkedInToken(new LinkedInToken());
		}
		else {
			LOG.debug("Updating the existing media tokens for LinkedIn");
			if (mediaTokens.getLinkedInToken() == null) {
				mediaTokens.setLinkedInToken(new LinkedInToken());
			}
		}
		
		mediaTokens.getLinkedInToken().setLinkedInAccessToken(accessToken.getToken());
		mediaTokens.getLinkedInToken().setLinkedInAccessTokenSecret(accessToken.getTokenSecret());
		mediaTokens.getLinkedInToken().setLinkedInAccessTokenCreatedOn(System.currentTimeMillis());
		
		LOG.debug("Method updateLinkedInToken() finished from SocialManagementController");
		return mediaTokens;
	}
	
	private OrganizationUnitSettings linkedInDataUpdate(String collection, OrganizationUnitSettings unitSettings, LinkedInToken linkedInToken) {
		LOG.info("Method linkedInDataUpdate() called from SocialManagementController");
		LinkedInApiClientFactory factory = socialManagementService.getLinkedInApiClientFactory();
		LinkedInApiClient client = factory.createLinkedInApiClient(linkedInToken.getLinkedInAccessToken(),
				linkedInToken.getLinkedInAccessTokenSecret());

		Person profile = client.getProfileForCurrentUser(EnumSet.of(ProfileField.SUMMARY, ProfileField.PICTURE_URL));
		LOG.info(profile.getSummary());
		if (profile.getSummary() != null || !profile.getSummary().equals("")) {
			try {
				unitSettings.getContact_details().setAbout_me(profile.getSummary());
				profileManagementService.updateContactDetails(collection, unitSettings, unitSettings.getContact_details());
			}
			catch (InvalidInputException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		LOG.info(profile.getPictureUrl());
		if (profile.getPictureUrl() != null || !profile.getPictureUrl().equals("")) {
			try {
				unitSettings.setProfileImageUrl(profile.getPictureUrl());
				profileManagementService.updateProfileImage(collection, unitSettings, profile.getPictureUrl());
			}
			catch (InvalidInputException e) {
				LOG.error(e.getMessage(), e);
			}
		}

		LOG.info("Method linkedInDataUpdate() finished from SocialManagementController");
		return unitSettings;
	}
}