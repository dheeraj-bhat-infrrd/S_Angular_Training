package com.realtech.socialsurvey.web.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import org.springframework.web.bind.annotation.RequestParam;
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

	// TODO
	/**
	 * Returns the facebook authorization page
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/facebookauthpage", method = RequestMethod.GET)
	public void getFacebookAuthPage(Model model, HttpServletRequest request, HttpServletResponse response) {
		LOG.info("Method getFacebookAuthPage() called from SocialManagementController");

		try {
			HttpSession session = request.getSession(false);
			if (session == null) {
				LOG.error("Session is null!");
			}
			response.sendRedirect(facebookDialogOauth + "?client_id=" + facebookClientId + "&redirect_uri=" + facebookRedirectUri + "&scope="
					+ facebookScope);
		}
		catch (IOException e) {
			LOG.error("Exception while getting request token. Reason : " + e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/facebookauth", params = "code", method = RequestMethod.GET)
	public void authenticateFacebookAccessCode(Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam("code") String code) {
		LOG.info("Facebook authentication url requested");
		User user = sessionHelper.getCurrentUser();
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
				if (currentUserSettings.getCompanySettings() == null) {
					LOG.error("authenticateLinkedInAccess : agent settings not found in session!");
					throw new NonFatalException("authenticateLinkedInAccess : agent settings not found in session!");
				}
			}

			// Fetching access token
			HttpClient httpclient = HttpClientBuilder.create().build();
			HttpGet httpget = new HttpGet(facebookGraphOauth + "?client_id=" + facebookClientId + "&redirect_uri=" + facebookRedirectUri + "&code="
					+ code + "&client_secret=" + facebookAppSecret);
			String responseBody = httpclient.execute(httpget, new BasicResponseHandler());

			// setting in mongo
			String accessToken = StringUtils.removeEnd(StringUtils.removeStart(responseBody, "access_token="), "&expires=");
			long accessTokenExpiresOn = Long.parseLong(responseBody.substring(responseBody.lastIndexOf("&expires=") + 9));
			userManagementService.setFacebookAccessTokenForUser(user, accessToken, accessTokenExpiresOn, currentUserSettings.getCompanySettings());
		}
		catch (IOException e) {
			LOG.error("Exception while getting access token. Reason : " + e.getMessage(), e);
		}
		catch (NonFatalException e) {
			LOG.error("Exception while getting access token. Reason : " + e.getMessage(), e);
		}
	}
}