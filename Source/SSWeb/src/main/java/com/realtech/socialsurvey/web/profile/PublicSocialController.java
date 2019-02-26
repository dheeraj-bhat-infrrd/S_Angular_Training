package com.realtech.socialsurvey.web.profile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
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
import twitter4j.auth.RequestToken;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.social.SocialManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.web.common.JspResolver;
import com.realtech.socialsurvey.web.util.RequestUtils;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.auth.AccessToken;

/**
 * Controller to manage social media oauth and pull/push posts for end users who are not in session.
 */

@Controller
@RequestMapping(value = "/social")
public class PublicSocialController {

	private static final Logger LOG = LoggerFactory.getLogger(PublicSocialController.class);

	@Value("${FB_REDIRECT_URI_SESSION}")
	private String facebookRedirectUriInSession;
	@Value ( "${FB_REDIRECT_URI}")
	private String facebookRedirectUri;

	@Value("${TWITTER_REDIRECT_URI_SESSION}")
	private String twitterRedirectUriInSession;

	// LinkedIn
	@Value("${LINKED_IN_REST_API_URI}")
	private String linkedInRestApiUri;
	@Value("${LINKED_IN_API_KEY}")
	private String linkedInApiKey;
	@Value("${LINKED_IN_API_SECRET}")
	private String linkedInApiSecret;
	@Value("${LINKED_IN_REDIRECT_URI_SESSION}")
	private String linkedinRedirectUriInSession;
	@Value("${LINKED_IN_AUTH_URI}")
	private String linkedinAuthUri;
	@Value("${LINKED_IN_ACCESS_URI}")
	private String linkedinAccessUri;
	@Value("${LINKED_IN_PROFILE_URI}")
	private String linkedinProfileUri;
	@Value("${LINKED_IN_SCOPE_V2}")
	private String linkedinScope;

	// Google
	@Value("${GOOGLE_API_KEY}")
	private String googleApiKey;
	@Value("${GOOGLE_API_SECRET}")
	private String googleApiSecret;
	@Value("${GOOGLE_REDIRECT_URI_SESSION}")
	private String googleApiRedirectUriInSession;
	@Value("${GOOGLE_API_SCOPE}")
	private String googleApiScope;
	@Value("${GOOGLE_SHARE_URI}")
	private String googleShareUri;
	@Value("${GOOGLE_PROFILE_URI}")
	private String googleProfileUri;

	@Autowired
	private SocialManagementService socialManagementService;
	
	@Autowired
	private RequestUtils requestUtils;

	@Autowired
    private SurveyHandler surveyHandler;

	/**
	 * Returns the social authorization page
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/socialauthinsession", method = RequestMethod.GET)
	public String getSocialAuthPageInSession(Model model, HttpServletRequest request) {
		LOG.info("Method getSocialAuthPageInSession() called from SocialManagementController");
		HttpSession session = request.getSession(false);
		if (session == null) {
			LOG.error("Session is null!");
		}

		// AuthUrl for diff social networks
		String socialNetwork = request.getParameter("social");
		String socialFlow = request.getParameter("flow");

		session.removeAttribute(CommonConstants.SOCIAL_FLOW);

		session.setAttribute("agentName", request.getParameter("agentName"));
		session.setAttribute("firstName", request.getParameter("firstName"));
		session.setAttribute("lastName", request.getParameter("lastName"));
		session.setAttribute("review", request.getParameter("review"));
		session.setAttribute("rating", request.getParameter("rating"));

		String serverBaseUrl = requestUtils.getRequestServerName(request);
		switch (socialNetwork) {

		// Building facebook authUrl
			case "facebook":
				Facebook facebook = socialManagementService.getFacebookInstance(serverBaseUrl, facebookRedirectUri);

				// Setting authUrl in model
				session.setAttribute(CommonConstants.SOCIAL_REQUEST_TOKEN, facebook);
				model.addAttribute(CommonConstants.SOCIAL_AUTH_URL, facebook.getOAuthAuthorizationURL(serverBaseUrl+facebookRedirectUriInSession));
				break;

			// Building twitter authUrl
			case "twitter":
				RequestToken requestToken;
				try {
					Twitter twitter = socialManagementService.getTwitterInstance();
					requestToken = twitter.getOAuthRequestToken(serverBaseUrl+twitterRedirectUriInSession);
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
				linkedInAuth.append("&redirect_uri=").append(requestUtils.getRequestServerName(request)).append(linkedinRedirectUriInSession);
				linkedInAuth.append("&state=").append("SOCIALSURVEY");
				linkedInAuth.append("&scope=").append(linkedinScope);

				model.addAttribute(CommonConstants.SOCIAL_AUTH_URL, linkedInAuth.toString());

				LOG.info("Returning the linkedin authorizationurl : " + linkedInAuth.toString());
				break;

			// Building Google authUrl
			case "google":
				StringBuilder googleAuth = new StringBuilder("https://accounts.google.com/o/oauth2/auth");
				googleAuth.append("?scope=").append(googleApiScope);
				googleAuth.append("&state=").append("security_token");
				googleAuth.append("&response_type=").append("code");
				googleAuth.append("&redirect_uri=").append(serverBaseUrl+googleApiRedirectUriInSession);
				googleAuth.append("&client_id=").append(googleApiKey);
				googleAuth.append("&access_type=").append("offline");

				model.addAttribute(CommonConstants.SOCIAL_AUTH_URL, googleAuth.toString());

				LOG.info("Returning the google authorizationurl : " + googleAuth.toString());
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
		model.addAttribute("restful", CommonConstants.YES);
		return JspResolver.SOCIAL_AUTH_MESSAGE;
	}

	/**
	 * The url that Facebook sends request to with oauth verification code
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/facebookauthinsession", method = RequestMethod.GET)
	public String authenticateFacebookAccess(Model model, HttpServletRequest request) {
		LOG.info("Facebook authentication url requested");
		HttpSession session = request.getSession(false);

		try {
			// On auth error
			String errorCode = request.getParameter("error");
			if (errorCode != null) {
				LOG.error("Error code : " + errorCode);
				model.addAttribute(CommonConstants.ERROR, CommonConstants.YES);
				return JspResolver.SOCIAL_AUTH_MESSAGE;
			}

			// Getting Oauth access token for facebook
			String oauthCode = request.getParameter("code");
			Facebook facebook = (Facebook) session.getAttribute(CommonConstants.SOCIAL_REQUEST_TOKEN);
			facebook4j.auth.AccessToken accessToken = null;
			try {
				accessToken = facebook.getOAuthAccessToken(oauthCode, requestUtils.getRequestServerName(request)+facebookRedirectUriInSession);
			}
			catch (FacebookException e) {
				LOG.error("Error while creating access token for facebook: " + e.getLocalizedMessage(), e);
			}

			// Share On facebook using newly generated token of end user
			facebook.setOAuthAccessToken(new AccessToken(accessToken.getToken(), null));
			String ratingStr = (String) session.getAttribute( "rating" );
			double rating = Double.parseDouble( ratingStr );
			
			String message = surveyHandler.getFormattedSurveyScore( rating ) + "-Star Survey Response from " + session.getAttribute("firstName") + " "
					+ session.getAttribute("lastName") + " for " + session.getAttribute("agentName")
					+ " on SocialSurvey. Below is the feedback :\n " + session.getAttribute("review");
			message = message.replaceAll("null", "");
			facebook.postStatusMessage(message);
		}
		catch (Exception e) {
			session.removeAttribute(CommonConstants.SOCIAL_REQUEST_TOKEN);
			LOG.error("Exception while getting facebook access token. Reason : " + e.getMessage(), e);
			return JspResolver.SOCIAL_AUTH_MESSAGE;
		}

		// Updating attributes
		session.removeAttribute(CommonConstants.SOCIAL_REQUEST_TOKEN);
		model.addAttribute(CommonConstants.SUCCESS_ATTRIBUTE, CommonConstants.YES);
		model.addAttribute("socialNetwork", "facebook");
		LOG.info("Facebook Access tokens obtained and added to mongo successfully!");
		return JspResolver.SOCIAL_AUTH_MESSAGE;
	}

	/**
	 * The url that twitter send request to with the oauth verification code
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/twitterauthinsession", method = RequestMethod.GET)
	public String authenticateTwitterAccess(Model model, HttpServletRequest request) {
		LOG.info("Twitter authentication url requested");
		HttpSession session = request.getSession(false);

		try {
			// On auth error
			String errorCode = request.getParameter("oauth_problem");
			if (errorCode != null) {
				LOG.error("Error code : " + errorCode);
				model.addAttribute(CommonConstants.ERROR, CommonConstants.YES);
				return JspResolver.SOCIAL_AUTH_MESSAGE;
			}

			// Getting Oauth accesstoken for Twitter
			twitter4j.auth.AccessToken accessToken = null;
			Twitter twitter = socialManagementService.getTwitterInstance();
			String oauthVerifier = request.getParameter("oauth_verifier");
			RequestToken requestToken = (RequestToken) session.getAttribute(CommonConstants.SOCIAL_REQUEST_TOKEN);
			try {
				accessToken = twitter.getOAuthAccessToken(requestToken, oauthVerifier);
			}
			catch (TwitterException te) {
				if (TwitterException.UNAUTHORIZED == te.getStatusCode()) {
					LOG.error("Unable to get the access token. Reason: UNAUTHORISED");
				}
				else {
					LOG.error(te.getErrorMessage());
				}
				throw new NonFatalException("Unable to procure twitter access token");
			}
			
			String ratingStr = (String) session.getAttribute( "rating" );
            double rating = Double.parseDouble( ratingStr );
            
			// Tweeting
			String twitterMessage = surveyHandler.getFormattedSurveyScore( rating ) + "-Star Survey Response from " + session.getAttribute("firstName") + " "
					+ session.getAttribute("lastName") + " for " + session.getAttribute("agentName")
					+ " on @SocialSurvey. Below is the feedback :\n " + session.getAttribute("review");
			twitterMessage = twitterMessage.replaceAll("null", "");
			twitter.setOAuthAccessToken(new twitter4j.auth.AccessToken(accessToken.getToken(), accessToken.getTokenSecret()));
			twitter.updateStatus(twitterMessage);
		}
		catch (Exception e) {
			session.removeAttribute(CommonConstants.SOCIAL_REQUEST_TOKEN);
			LOG.error("Exception while getting twitter access token. Reason : " + e.getMessage(), e);
			return JspResolver.SOCIAL_AUTH_MESSAGE;
		}

		// Updating attributes
		session.removeAttribute(CommonConstants.SOCIAL_REQUEST_TOKEN);
		model.addAttribute(CommonConstants.SUCCESS_ATTRIBUTE, CommonConstants.YES);
		model.addAttribute("socialNetwork", "twitter");
		return JspResolver.SOCIAL_AUTH_MESSAGE;
	}

	/**
	 * The url that LinkedIn send request to with the oauth verification code
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/linkedinauthinsession", method = RequestMethod.GET)
	public String authenticateLinkedInAccess(Model model, HttpServletRequest request) {
		LOG.info("Method authenticateLinkedInAccess() called from SocialManagementController");
		HttpSession session = request.getSession(false);

		try {
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
			params.add(new BasicNameValuePair("redirect_uri", requestUtils.getRequestServerName(request)+linkedinRedirectUriInSession));
			params.add(new BasicNameValuePair("client_id", linkedInApiKey));
			params.add(new BasicNameValuePair("client_secret", linkedInApiSecret));

			// fetching access token
			HttpClient httpclient = HttpClientBuilder.create().build();
			HttpPost httpPost = new HttpPost(linkedinAccessUri);
			httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			String accessTokenStr = httpclient.execute(httpPost, new BasicResponseHandler());
			Map<String, Object> map = new Gson().fromJson(accessTokenStr, new TypeToken<Map<String, String>>() {}.getType());
			String accessToken = (String) map.get("access_token");

			String ratingStr = (String) session.getAttribute( "rating" );
			double rating = Double.parseDouble( ratingStr );
            
			// Post on linkedin
			String message = surveyHandler.getFormattedSurveyScore( rating ) + "-Star Survey Response from " + session.getAttribute("firstName") + " "
					+ session.getAttribute("lastName") + " for " + session.getAttribute("agentName")
					+ " on SocialSurvey. Below is the feedback :\n " + session.getAttribute("review");
			message = message.replaceAll("null", "");
			String linkedInPost = new StringBuilder(linkedInRestApiUri).substring(0, linkedInRestApiUri.length() - 1);
			linkedInPost += "/shares?oauth2_access_token=" + accessToken;
			linkedInPost += "&format=json";

			HttpClient client = HttpClientBuilder.create().build();
			HttpPost post = new HttpPost(linkedInPost);

			// add header
			post.setHeader("Content-Type", "application/json");

			StringEntity entity = new StringEntity("{\"comment\": \"" + message + "\",\"visibility\": {\"code\": \"anyone\"}}");

			post.setEntity(entity);
			try {
				client.execute(post);
			}
			catch (RuntimeException e) {
				LOG.error("Runtime exception caught while trying to add an update on linkedin. Nested exception is ", e);
			}
		}
		catch (Exception e) {
			LOG.error("Exception while getting linkedin access token. Reason : " + e.getMessage(), e);
			return JspResolver.SOCIAL_AUTH_MESSAGE;
		}

		// Updating attributes
		model.addAttribute(CommonConstants.SUCCESS_ATTRIBUTE, CommonConstants.YES);
		model.addAttribute("socialNetwork", "linkedin");
		LOG.info("Method authenticateLinkedInAccess() finished from SocialManagementController");
		return JspResolver.SOCIAL_AUTH_MESSAGE;
	}

}
