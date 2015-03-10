package com.realtech.socialsurvey.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
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
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthServiceFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserSettings;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.web.common.JspResolver;

/**
 * Controller to manage social media oauth and pull/push posts
 */
@Controller
public class SocialManagementController {

	private static final Logger LOG = LoggerFactory.getLogger(SocialManagementController.class);

	@Autowired
	private UserManagementService userManagementService;

	@Autowired
	private SessionHelper sessionHelper;

	@Autowired
	private LinkedInApiClientFactory linkedInApiClientFactory;

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

	// Facebook
	@Value("${FB_CLIENT_ID}")
	private String facebookClientId;
	@Value("${FB_APP_SECRET}")
	private String facebookAppSecret;
	@Value("${FB_SCOPE}")
	private String facebookScope;
	@Value("${FB_REDIRECT_URI}")
	private String facebookRedirectUri;
	@Value("${FB_DIALOG_OAUTH}")
	private String facebookDialogOauth;
	@Value("${FB_GRAPH_OAUTH}")
	private String facebookGraphOauth;
	@Value("${FB_GRAPH_URI}")
	private String facebookGraphApiUri;

	// Twitter
	@Value("${TWITTER_CONSUMER_KEY}")
	private String twitterConsumerKey;
	@Value("${TWITTER_CONSUMER_SECRET}")
	private String twitterConsumerSecret;

	/**
	 * Returns the social authorization page
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	// TODO
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
				StringBuilder facebookAuthUrl = new StringBuilder(facebookDialogOauth).append("?client_id=").append(facebookClientId);
				facebookAuthUrl.append("&redirect_uri=").append(facebookRedirectUri);
				facebookAuthUrl.append("&scope=").append(facebookScope);

				// Setting authUrl in model
				model.addAttribute(CommonConstants.MESSAGE, CommonConstants.YES);
				model.addAttribute(CommonConstants.SOCIAL_AUTH_URL, facebookAuthUrl.toString());

				LOG.info("Returning the facebook authUrl : " + facebookAuthUrl.toString());
				break;

			// Building twitter authUrl
			case "twitter":
				RequestToken requestToken;
				try {
					requestToken = userManagementService.getTwitterRequestToken();
				}
				catch (Exception e) {
					LOG.error("Exception while getting request token. Reason : " + e.getMessage(), e);
					model.addAttribute("message", e.getMessage());
					return JspResolver.ERROR_PAGE;
				}

				// We will keep the request token in session
				session.setAttribute(CommonConstants.SOCIAL_REQUEST_TOKEN, requestToken);
				model.addAttribute(CommonConstants.MESSAGE, CommonConstants.YES);
				model.addAttribute(CommonConstants.SOCIAL_AUTH_URL, requestToken.getAuthorizationURL());

				LOG.info("Returning the twitter authorizationurl : " + requestToken.getAuthorizationURL());
				break;

			// Building facebook authUrl
			case "linkedin":
				getLinkedInAuthPage(model, request);
				break;

			default:
				LOG.error("Social Network Type invalid in getSocialAuthPage");
		}
		return JspResolver.SOCIAL_AUTH_MESSAGE;
	}

	/**
	 * Returns the linked in authorization page
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/linkedinauthpage", method = RequestMethod.GET)
	public String getLinkedInAuthPage(Model model, HttpServletRequest request) {
		LOG.info("Method getLinkedInAuthPage() called from SocialManagementController");
		HttpSession session = request.getSession(false);
		if (session == null) {
			LOG.error("Session is null!");
		}

		LinkedInRequestToken requestToken;
		try {
			requestToken = userManagementService.getLinkedInRequestToken();
		}
		catch (Exception e) {
			LOG.error("Exception while getting request token. Reason : " + e.getMessage(), e);
			model.addAttribute("message", e.getMessage());
			return JspResolver.ERROR_PAGE;
		}

		// We will keep the request token in session
		session.setAttribute(CommonConstants.LINKEDIN_REQUEST_TOKEN, requestToken);
		model.addAttribute(CommonConstants.MESSAGE, CommonConstants.YES);
		model.addAttribute(CommonConstants.LINKEDIN_AUTH_URL, requestToken.getAuthorizationUrl());

		LOG.info("Returning the authorizationurl : " + requestToken.getAuthorizationUrl());
		return JspResolver.LINKEDIN_MESSAGE;
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
		LOG.info("LinkedIn authentication url requested");
		HttpSession session = request.getSession(false);
		UserSettings currentUserSettings;

		try {
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
				if (currentUserSettings.getAgentSettings() == null) {
					LOG.error("authenticateLinkedInAccess : agent settings not found in session!");
					throw new NonFatalException("authenticateLinkedInAccess : agent settings not found in session!");
				}
			}

			String errorCode = request.getParameter("oauth_problem");
			if (errorCode != null) {
				LOG.error("Error code : " + errorCode);
				model.addAttribute(CommonConstants.ERROR, CommonConstants.YES);
				return JspResolver.LINKEDIN_MESSAGE;
			}

			User user = sessionHelper.getCurrentUser();
			LinkedInOAuthService oauthService = LinkedInOAuthServiceFactory.getInstance().createLinkedInOAuthService(linkedInApiKey,
					linkedInApiSecret);
			String oauthVerifier = request.getParameter("oauth_verifier");
			LOG.debug("LinkedIn oauth verfier : " + oauthVerifier);

			LinkedInRequestToken requestToken = (LinkedInRequestToken) session.getAttribute(CommonConstants.LINKEDIN_REQUEST_TOKEN);
			LinkedInAccessToken accessToken = oauthService.getOAuthAccessToken(requestToken, oauthVerifier);
			userManagementService.setLinkedInAccessTokenForUser(user, accessToken.getToken(), accessToken.getTokenSecret(), currentUserSettings
					.getAgentSettings().values());
		}
		catch (Exception e) {
			session.removeAttribute(CommonConstants.LINKEDIN_REQUEST_TOKEN);
			LOG.error(e.getMessage(), e);
			return JspResolver.LINKEDIN_MESSAGE;
		}
		session.removeAttribute(CommonConstants.LINKEDIN_REQUEST_TOKEN);

		model.addAttribute(CommonConstants.SUCCESS_ATTRIBUTE, CommonConstants.YES);
		LOG.info("Access tokens obtained and added to mongo successfully!");
		return JspResolver.LINKEDIN_MESSAGE;
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

		try {
			User user = sessionHelper.getCurrentUser();
			HttpSession session = request.getSession(false);
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
			String verificationCode = request.getParameter("code");
			StringBuilder facebookAuthUrl = new StringBuilder(facebookGraphOauth).append("?client_id=").append(facebookClientId);
			facebookAuthUrl.append("&redirect_uri=").append(facebookRedirectUri);
			facebookAuthUrl.append("&code=").append(verificationCode);
			facebookAuthUrl.append("&client_secret=").append(facebookAppSecret);

			// fetching access token
			HttpClient httpclient = HttpClientBuilder.create().build();
			HttpGet httpget = new HttpGet(facebookAuthUrl.toString());
			String responseBody = httpclient.execute(httpget, new BasicResponseHandler());
			String accessToken = StringUtils.removeEnd(StringUtils.removeStart(responseBody, "access_token="), "&expires=");
			long accessTokenExpiresOn = Long.parseLong(responseBody.substring(responseBody.lastIndexOf("&expires=") + 9));

			userManagementService.setFacebookAccessTokenForUser(user, accessToken, accessTokenExpiresOn, currentUserSettings.getCompanySettings());
			model.addAttribute(CommonConstants.SUCCESS_ATTRIBUTE, CommonConstants.YES);
		}
		catch (Exception e) {
			LOG.error("Exception while getting access token. Reason : " + e.getMessage(), e);
			return JspResolver.SOCIAL_AUTH_MESSAGE;
		}

		LOG.info("Access tokens obtained and added to mongo successfully!");
		return JspResolver.SOCIAL_AUTH_MESSAGE;
	}

	// TODO
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
				return JspResolver.LINKEDIN_MESSAGE;
			}

			AccessToken accessToken = null;
			while (null == accessToken) {
				ConfigurationBuilder builder = new ConfigurationBuilder();
				builder.setOAuthConsumerKey(twitterConsumerKey);
				builder.setOAuthConsumerSecret(twitterConsumerSecret);
				Configuration configuration = builder.build();
				TwitterFactory factory = new TwitterFactory(configuration);
				Twitter twitter = factory.getInstance();

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
			userManagementService.setTwitterAccessTokenForUser(user, accessToken.getToken(), accessToken.getTokenSecret(),
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
}