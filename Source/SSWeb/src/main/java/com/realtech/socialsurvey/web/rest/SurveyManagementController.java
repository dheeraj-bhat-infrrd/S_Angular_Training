package com.realtech.socialsurvey.web.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import twitter4j.TwitterException;
import com.google.gson.Gson;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyQuestionDetails;
import com.realtech.socialsurvey.core.entities.SurveyResponse;
import com.realtech.socialsurvey.core.entities.SurveySettings;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.authentication.CaptchaValidation;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.social.SocialManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyBuilder;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.common.ErrorCodes;
import com.realtech.socialsurvey.web.common.ErrorResponse;
import com.realtech.socialsurvey.web.common.JspResolver;
import facebook4j.FacebookException;

// JIRA SS-119 by RM-05 : BOC
@Controller
@RequestMapping(value = "/survey")
public class SurveyManagementController {

	private static final Logger LOG = LoggerFactory.getLogger(SurveyManagementController.class);

	@Autowired
	private SurveyBuilder surveyBuilder;

	@Autowired
	private SurveyHandler surveyHandler;

	@Autowired
	private MessageUtils messageUtils;

	@Autowired
	private URLGenerator urlGenerator;

	@Autowired
	private SolrSearchService solrSearchService;

	@Autowired
	private CaptchaValidation captchaValidation;

	@Autowired
	private EmailServices emailServices;

	@Autowired
	private SocialManagementService socialManagementService;

	@Autowired
	private OrganizationManagementService organizationManagementService;

	@Autowired
	private UserManagementService userManagementService;

	@Value("${ENABLE_KAFKA}")
	private String enableKafka;

	@Value("${YELP_REDIRECT_URI}")
	private String yelpRedirectUri;

	/*
	 * Method to store answer to the current question of the survey.
	 */
	@ResponseBody
	@RequestMapping(value = "/data/storeAnswer")
	public String storeSurveyAnswer(HttpServletRequest request) {
		LOG.info("Method storeSurveyAnswer() started to store response of customer.");
		String answer = request.getParameter("answer");
		String question = request.getParameter("question");
		String questionType = request.getParameter("questionType");
		int stage = Integer.parseInt(request.getParameter("stage"));
		String customerEmail = request.getParameter("customerEmail");
		long agentId = Long.valueOf(request.getParameter("agentId"));
		surveyHandler.updateCustomerAnswersInSurvey(agentId, customerEmail, question, questionType, answer, stage);
		LOG.info("Method storeSurveyAnswer() finished to store response of customer.");
		return surveyHandler.getSwearWords();
	}

	/*
	 * Method to store final feedback of the survey from customer.
	 */
	@ResponseBody
	@RequestMapping(value = "/data/storeFeedback")
	public String storeFeedback(HttpServletRequest request) {
		LOG.info("Method storeFeedback() started to store response of customer.");
		
		// To store final feedback provided by customer in mongoDB.
		try {
			String feedback = request.getParameter("feedback");
			String mood = request.getParameter("mood");
			String customerEmail = request.getParameter("customerEmail");
			
			long agentId = 0;
			try {
				String agentIdStr = request.getParameter("agentId");
				if (agentIdStr == null || agentIdStr.isEmpty()) {
					LOG.error("Null/empty value found for agentId in storeFeedback().");
					throw new InvalidInputException("Null/empty value found for agentId in storeFeedback().");
				}
				agentId = Long.valueOf(agentIdStr);
			}
			catch (NumberFormatException e) {
				LOG.error("NumberFormatException occurred in storeFeedback() while getting agentId.");
			}
			
			boolean isAbusive = Boolean.parseBoolean(request.getParameter("isAbusive"));
			surveyHandler.updateGatewayQuestionResponseAndScore(agentId, customerEmail, mood, feedback, isAbusive);
			surveyHandler.increaseSurveyCountForAgent(agentId);

			// TODO Search Engine Optimisation
			if (mood == null || mood.isEmpty()) {
				LOG.error("Null/empty value found for mood in storeFeedback().");
				throw new InvalidInputException("Null/empty value found for mood in storeFeedback().");
			}
			Set<String> emailIdsToSendMail = new HashSet<>();
			SolrDocument solrDocument = null;
			
			try {
				solrDocument = solrSearchService.getUserByUniqueId(agentId);
			}
			catch (SolrServerException e) {
				LOG.error("SolrServerException occurred in storeFeedback() while fetching email id of agent. NEsted exception is ", e);
			}
			
			if (solrDocument != null && !solrDocument.isEmpty()) {
				emailIdsToSendMail.add(solrDocument.get(CommonConstants.USER_EMAIL_ID_SOLR).toString());
			}
			
			String moodsToSendMail = surveyHandler.getMoodsToSendMail();
			if (!moodsToSendMail.isEmpty() && moodsToSendMail != null) {
				List<String> moods = new ArrayList<>(Arrays.asList(moodsToSendMail.split(",")));
				if (moods.contains(mood)) {
					emailIdsToSendMail.addAll(surveyHandler.getEmailIdsOfAdminsInHierarchy(agentId));
				}
			}
			
			// Sending email to the customer telling about successful completion of survey.
			SurveyDetails survey = surveyHandler.getSurveyDetails(agentId, customerEmail);
			try {
				String customerName = survey.getCustomerFirstName();
				if (survey.getCustomerLastName() != null && !survey.getCustomerLastName().isEmpty()) {
					customerName = survey.getCustomerFirstName() + " " + survey.getCustomerLastName();
				}
				
				if (enableKafka.equals(CommonConstants.YES)) {
					emailServices.queueSurveyCompletionMail(customerEmail, customerName, survey.getAgentName());
				}
				else {
					emailServices.sendSurveyCompletionMail(customerEmail, customerName, survey.getAgentName());
				}
				
				// Generate the text as in mail
				String surveyDetail = generateSurveyTextForMail(customerName, mood, survey);
				
				if (enableKafka.equals(CommonConstants.YES)) {
					for (String emailId : emailIdsToSendMail) {
						emailServices.queueSurveyCompletionMailToAdmins(emailId, surveyDetail);
					}
				}
				else {
					for (String emailId : emailIdsToSendMail) {
						emailServices.sendSurveyCompletionMailToAdmins(emailId, surveyDetail);
					}
				}
			}
			catch (InvalidInputException e) {
				LOG.error("Exception occurred while trying to send survey completion mail to : " + customerEmail);
				throw e;
			}
		}
		catch (NonFatalException e) {
			LOG.error("Non fatal exception caught in storeFeedback(). Nested exception is ", e);
			return e.getMessage();
		}
		LOG.info("Method storeFeedback() finished to store response of customer.");
		return "Survey stored successfully";
	}
	
	private String generateSurveyTextForMail(String customerName, String mood, SurveyDetails survey) {
		StringBuilder surveyDetail = new StringBuilder(customerName).append(" Complete Survey Response for ").append(survey.getAgentName());
		surveyDetail.append("\n").append("Date Sent: ").append(survey.getCreatedOn().toString());
		surveyDetail.append("\n").append("Date Completed: ").append(survey.getModifiedOn().toString());
		surveyDetail.append("\n").append("Average Score: ").append(String.valueOf(survey.getScore()));
		surveyDetail.append("\n");
		int count = 1;
		for (SurveyResponse response : survey.getSurveyResponse()) {
			surveyDetail.append("\n").append("Question " + count + ": ").append(response.getQuestion());
			surveyDetail.append("\n").append("Response to Q" + count + ": ").append(response.getAnswer());
			count ++;
		}
		surveyDetail.append("\n");
		surveyDetail.append("\n").append("Customer Comments: ").append(survey.getReview());
		surveyDetail.append("\n").append("Customer Mood: ").append(mood);
		if (survey.getSharedOn() != null && !survey.getSharedOn().isEmpty()) {
			surveyDetail.append("\n").append("Share Checkbox: ").append("Yes");
		}
		else {
			surveyDetail.append("\n").append("Share Checkbox: ").append("No");
		}
		surveyDetail.append("\n").append("Shared on: ").append(StringUtils.join(survey.getSharedOn(), ", "));
		
		return surveyDetail.toString();
	}

	@ResponseBody
	@RequestMapping(value = "/redirecttodetailspage")
	public String showDetailsPage(Model model, HttpServletRequest request) {
		LOG.info("Method to redirect to survey page started.");
		String agentId = request.getParameter("userId");
		LOG.info("Method to redirect to survey page started.");
		return getApplicationBaseUrl() + "rest/survey/showsurveypage/" + agentId;
	}

	@RequestMapping(value = "/showsurveypage/{agentIdStr}")
	public String initiateSurvey(Model model, @PathVariable String agentIdStr) {
		LOG.info("Method to start survey initiateSurvey() started.");
		if (agentIdStr == null || agentIdStr.isEmpty()) {
			LOG.error("Invalid agentId passed. Agent Id can not be null or empty.");
			return JspResolver.ERROR_PAGE;
		}
		Long agentId = 0l;
		try {
			agentId = Long.parseLong(agentIdStr);
		}
		catch (NumberFormatException e) {
			LOG.error("Invalid agent Id passed. Error is : " + e);
			return JspResolver.ERROR_PAGE;
		}
		String agentName = "";
		String agentEmail = "";
		try {
			SolrDocument user = solrSearchService.getUserByUniqueId(agentId);
			if(user!=null){
				agentName = user.get(CommonConstants.USER_DISPLAY_NAME_SOLR).toString();
				agentEmail = user.get(CommonConstants.USER_EMAIL_ID_SOLR).toString();
			}
		}
		catch (InvalidInputException | SolrServerException e) {
			LOG.error("Error occured while fetching details of agent. Error is : " + e);
			return JspResolver.ERROR_PAGE;
		}
		model.addAttribute("agentId", agentId);
		model.addAttribute("agentName", agentName);
		model.addAttribute("agentEmail", agentEmail);
		LOG.info("Method to start survey initiateSurvey() finished.");
		return JspResolver.SHOW_SURVEY_QUESTIONS;
	}

	@RequestMapping(value = "/showsurveypageforurl")
	public String initiateSurveyWithUrl(Model model, HttpServletRequest request) {
		LOG.info("Method to start survey initiateSurveyWithUrl() started.");
		String param = request.getParameter("q");
		model.addAttribute("q", param);
		LOG.info("Method to start survey initiateSurveyWithUrl() finished.");
		return JspResolver.SHOW_SURVEY_QUESTIONS;
	}

	/*
	 * Method to retrieve survey questions for a survey based upon the company id and agent id.
	 */

	@ResponseBody
	@RequestMapping(value = "/triggersurvey")
	public String triggerSurvey(Model model, HttpServletRequest request) {
		LOG.info("Method to store initial details of customer and agent and to get questions of survey, triggerSurvey() started.");
		try {
			long agentId = 0;
			String customerEmail;
			String firstName;
			String lastName;
			String captchaResponse;
			String challengeField;
			String custRelationWithAgent;
			try {
				String user = request.getParameter(CommonConstants.AGENT_ID_COLUMN);
				agentId = Long.parseLong(user);
				customerEmail = request.getParameter(CommonConstants.CUSTOMER_EMAIL_COLUMN);
				firstName = request.getParameter("firstName");
				lastName = request.getParameter("lastName");
				captchaResponse = request.getParameter("captchaResponse");
				challengeField = request.getParameter("recaptcha_challenge_field");
				custRelationWithAgent = request.getParameter("relationship");
			}
			catch (NumberFormatException e) {
				LOG.error("NumberFormatException caught in triggerSurvey(). Details are " + e);
				throw e;
			}
			if (!captchaValidation.isCaptchaValid(request.getRemoteAddr(), challengeField, captchaResponse)) {
				LOG.error("Captcha Validation failed!");
				// throw new InvalidInputException("Captcha Validation failed!",
				// DisplayMessageConstants.INVALID_CAPTCHA);
				String errorMsg = messageUtils.getDisplayMessage(DisplayMessageConstants.INVALID_CAPTCHA, DisplayMessageType.ERROR_MESSAGE)
						.getMessage();
				throw new InvalidInputException(errorMsg, DisplayMessageConstants.INVALID_CAPTCHA);

			}
			User user = userManagementService.getUserByUserId(agentId);
			surveyHandler.sendSurveyInvitationMail(firstName, lastName, customerEmail, custRelationWithAgent, user, false);
		}
		catch (NonFatalException e) {
			LOG.error("Exception caught in getSurvey() method of SurveyManagementController.");
			/*ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrCode(ErrorCodes.REQUEST_FAILED);
			errorResponse.setErrMessage(e.getMessage());
			String errorMessage = new Gson().toJson(errorResponse);*/
			return "Something went wrong while sending survey link. Please try again later.";
		}
		LOG.info("Method to store initial details of customer and agent and to get questions of survey, triggerSurvey() started.");
		return "Link to take survey has been sent on your email id successfully.";
	}

	@ResponseBody
	@RequestMapping(value = "/triggersurveywithurl", method = RequestMethod.GET)
	public String triggerSurveyWithUrl(HttpServletRequest request) {
		LOG.info("Method to start survey when URL is given, triggerSurveyWithUrl() started.");
		Map<String, Object> surveyAndStage = new HashMap<>();
		try {
			String url = request.getParameter("q");
			Map<String, String> urlParams = urlGenerator.decryptParameters(url);
			if (urlParams != null) {
				long agentId = Long.parseLong(urlParams.get(CommonConstants.AGENT_ID_COLUMN));
				surveyAndStage = getSurvey(agentId, urlParams.get(CommonConstants.CUSTOMER_EMAIL_COLUMN), null, null, 0, null, url);
			}
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException caught in triggerSurveyWithUrl().");
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrCode(ErrorCodes.REQUEST_FAILED);
			errorResponse.setErrMessage(e.getMessage());
			String errorMessage = new Gson().toJson(errorResponse);
			return errorMessage;
		}
		LOG.info("Method to start survey when URL is given, triggerSurveyWithUrl() finished.");
		return new Gson().toJson(surveyAndStage);
	}

	@ResponseBody
	@RequestMapping(value = "/posttosocialnetwork", method = RequestMethod.GET)
	public String postToSocialMedia(HttpServletRequest request) {
		LOG.info("Method to post feedback of customer to various pages of social networking sites started.");
		try {
			String agentName = request.getParameter("agentName");
			String agentProfileLink = request.getParameter("agentProfileLink");
			String custFirstName = request.getParameter("firstName");
			String custLastName = request.getParameter("lastName");
			String agentIdStr = request.getParameter("agentId");
			String ratingStr = request.getParameter("rating");
			String customerEmail = request.getParameter("customerEmail");
			long agentId = 0;
			double rating = 0;
			try {
				agentId = Long.parseLong(agentIdStr);
				rating = Double.parseDouble(ratingStr);
			}
			catch (NumberFormatException|NullPointerException e) {
				LOG.error("Number format/Null Pointer exception caught in postToSocialMedia() while trying to convert agent Id. Nested exception is ", e);
				return e.getMessage();
			}
			rating = Math.round(rating * 100) / 100;
			List<OrganizationUnitSettings> settings = socialManagementService.getSettingsForBranchesAndRegionsInHierarchy(agentId);
			AgentSettings agentSettings = userManagementService.getUserSettings(agentId);
			String facebookMessage = rating + "-Star Survey Response from " + custFirstName + " " + custLastName + " for " + agentName
					+ " on Social Survey - view at " + getApplicationBaseUrl() + CommonConstants.AGENT_PROFILE_FIXED_URL + agentProfileLink;
			
			String twitterMessage = rating + "-Star Survey Response from " + custFirstName + " " + custLastName + " for " + agentName
					+ " on @SocialSurvey - view at " + getApplicationBaseUrl() + CommonConstants.AGENT_PROFILE_FIXED_URL + agentProfileLink;
			
			String linkedinMessage = rating + "-Star Survey Response from " + custFirstName + " " + custLastName + " for " + agentName
					+ " on SocialSurvey - view at " + getApplicationBaseUrl() + CommonConstants.AGENT_PROFILE_FIXED_URL + agentProfileLink;
			try {
				socialManagementService.updateStatusIntoFacebookPage(agentSettings, facebookMessage);
				List<String> socialSites = new ArrayList<>();
				socialSites.add(CommonConstants.FACEBOOK_SOCIAL_SITE);
				socialSites.add(CommonConstants.TWITTER_SOCIAL_SITE);
				socialSites.add(CommonConstants.LINKEDIN_SOCIAL_SITE);
				surveyHandler.updateSharedOn(socialSites, agentId, customerEmail);
			}
			catch (FacebookException e) {
				LOG.error("FacebookException caught in postToSocialMedia() while trying to post to facebook. Nested excption is ", e);
			}
			for (OrganizationUnitSettings setting : settings) {
				try {
					socialManagementService.updateStatusIntoFacebookPage(setting, facebookMessage);
				}
				catch (FacebookException e) {
					LOG.error("FacebookException caught in postToSocialMedia() while trying to post to facebook. Nested excption is ", e);
				}
			}
			try {
				socialManagementService.tweet(agentSettings, twitterMessage);
			}
			catch (TwitterException e) {
				LOG.error("TwitterException caught in postToSocialMedia() while trying to post to twitter. Nested excption is ", e);
			}
			for (OrganizationUnitSettings setting : settings) {
				try {
					socialManagementService.tweet(setting, twitterMessage);
				}
				catch (TwitterException e) {
					LOG.error("TwitterException caught in postToSocialMedia() while trying to post to twitter. Nested excption is ", e);
				}
			}

			socialManagementService.updateLinkedin(agentSettings, linkedinMessage);
			for (OrganizationUnitSettings setting : settings) {
				socialManagementService.updateLinkedin(setting, linkedinMessage);
			}
		}
		catch (NonFatalException e) {
			LOG.error("Non fatal Exception caught in postToSocialMedia() while trying to post to social networking sites. Nested excption is ", e);
			return e.getMessage();
		}
		LOG.info("Method to post feedback of customer to various pages of social networking sites finished.");
		return "Successfully posted to all the places in hierarchy";
	}

	@ResponseBody
	@RequestMapping(value = "/posttofacebook", method = RequestMethod.GET)
	public String postToFacebook(HttpServletRequest request) {
		LOG.info("Method to post feedback of customer to facebook started.");
		try {
			String urlParam = request.getParameter("q");
			Map<String, String> facebookDetails = urlGenerator.decryptParameters(urlParam);
			String agentName = facebookDetails.get("agentName");
			String custFirstName = facebookDetails.get("firstName");
			String agentProfileLink = facebookDetails.get("agentProfileLink");
			String custLastName = facebookDetails.get("lastName");
			String agentIdStr = facebookDetails.get("agentId");
			String ratingStr = facebookDetails.get("rating");
			String customerEmail = facebookDetails.get("customerEmail");
			long agentId = 0;
			double rating = 0;
			try {
				agentId = Long.parseLong(agentIdStr);
				rating = Double.parseDouble(ratingStr);
			}
			catch (NumberFormatException e) {
				LOG.error("Number format exception caught in postToSocialMedia() while trying to convert agent Id. Nested exception is ", e);
				return e.getMessage();
			}
			List<OrganizationUnitSettings> settings = socialManagementService.getSettingsForBranchesAndRegionsInHierarchy(agentId);
			AgentSettings agentSettings = userManagementService.getUserSettings(agentId);
			rating = Math.round(rating * 100) / 100;
			String facebookMessage = rating + "-Star Survey Response from " + custFirstName + " " + custLastName + " for " + agentName
					+ " on Social Survey - view at " + getApplicationBaseUrl() + CommonConstants.AGENT_PROFILE_FIXED_URL + agentProfileLink;
			try {
				socialManagementService.updateStatusIntoFacebookPage(agentSettings, facebookMessage);
				List<String> socialSites = new ArrayList<>();
				socialSites.add(CommonConstants.FACEBOOK_SOCIAL_SITE);
				surveyHandler.updateSharedOn(socialSites, agentId, customerEmail);
			}
			catch (FacebookException e) {
				LOG.error("FacebookException caught in postToSocialMedia() while trying to post to facebook. Nested excption is ", e);
			}
			for (OrganizationUnitSettings setting : settings) {
				try {
					socialManagementService.updateStatusIntoFacebookPage(setting, facebookMessage);
				}
				catch (FacebookException e) {
					LOG.error("FacebookException caught in postToSocialMedia() while trying to post to facebook. Nested excption is ", e);
				}
			}
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException caught in postToFacebook(). Nested exception is ", e);
		}
		LOG.info("Method to post feedback of customer to facebook finished.");
		return "";
	}

	@ResponseBody
	@RequestMapping(value = "/posttotwitter", method = RequestMethod.GET)
	public String postToTwitter(HttpServletRequest request) {
		LOG.info("Method to post feedback of customer to twitter started.");
		try {
			String urlParam = request.getParameter("q");
			Map<String, String> twitterDetails = urlGenerator.decryptParameters(urlParam);
			String agentName = twitterDetails.get("agentName");
			String agentProfileLink = twitterDetails.get("agentProfileLink");
			String custFirstName = twitterDetails.get("firstName");
			String custLastName = twitterDetails.get("lastName");
			String agentIdStr = twitterDetails.get("agentId");
			String ratingStr = twitterDetails.get("rating");
			String customerEmail = twitterDetails.get("customerEmail");
			long agentId = 0;
			double rating = 0;
			try {
				agentId = Long.parseLong(agentIdStr);
				rating = Double.parseDouble(ratingStr);
			}
			catch (NumberFormatException e) {
				LOG.error("Number format exception caught in postToTwitter() while trying to convert agent Id. Nested exception is ", e);
				return e.getMessage();
			}
			List<OrganizationUnitSettings> settings = socialManagementService.getSettingsForBranchesAndRegionsInHierarchy(agentId);
			AgentSettings agentSettings = userManagementService.getUserSettings(agentId);
			rating = Math.round(rating * 100) / 100;
			String twitterMessage = rating + "-Star Survey Response from " + custFirstName + custLastName + " for " + agentName
					+ " on @SocialSurvey - view at " + getApplicationBaseUrl() + CommonConstants.AGENT_PROFILE_FIXED_URL + agentProfileLink;
			try {
				socialManagementService.tweet(agentSettings, twitterMessage);
				List<String> socialSites = new ArrayList<>();
				socialSites.add(CommonConstants.TWITTER_SOCIAL_SITE);
				surveyHandler.updateSharedOn(socialSites, agentId, customerEmail);
			}
			catch (TwitterException e) {
				LOG.error("TwitterException caught in postToTwitter() while trying to post to twitter. Nested excption is ", e);
			}
			for (OrganizationUnitSettings setting : settings) {
				try {
					socialManagementService.tweet(setting, twitterMessage);
				}
				catch (TwitterException e) {
					LOG.error("TwitterException caught in postToTwitter() while trying to post to twitter. Nested excption is ", e);
				}
			}
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException caught in postToTwitter(). Nested exception is ", e);
		}
		LOG.info("Method to post feedback of customer to twitter finished.");
		return "";
	}

	@ResponseBody
	@RequestMapping(value = "/posttolinkedin", method = RequestMethod.GET)
	public String postToLinkedin(HttpServletRequest request) {
		LOG.info("Method to post feedback of customer to linkedin started.");
		try {
			String urlParam = request.getParameter("q");
			Map<String, String> linkedinDetails = urlGenerator.decryptParameters(urlParam);
			String agentName = linkedinDetails.get("agentName");
			String custFirstName = linkedinDetails.get("firstName");
			String custLastName = linkedinDetails.get("lastName");
			String agentIdStr = linkedinDetails.get("agentId");
			String ratingStr = linkedinDetails.get("rating");
			String customerEmail = linkedinDetails.get("customerEmail");
			String agentProfileLink = linkedinDetails.get("agentProfileLink");
			long agentId = 0;
			double rating = 0;
			try {
				agentId = Long.parseLong(agentIdStr);
				rating = Double.parseDouble(ratingStr);
			}
			catch (NumberFormatException e) {
				LOG.error("Number format exception caught in postToLinkedin() while trying to convert agent Id. Nested exception is ", e);
				return e.getMessage();
			}
			List<OrganizationUnitSettings> settings = socialManagementService.getSettingsForBranchesAndRegionsInHierarchy(agentId);
			AgentSettings agentSettings = userManagementService.getUserSettings(agentId);
			rating = Math.round(rating * 100) / 100;
			String message = rating + "-Star Survey Response from " + custFirstName + custLastName + " for " + agentName
					+ " on SocialSurvey - view at " + getApplicationBaseUrl() + CommonConstants.AGENT_PROFILE_FIXED_URL + agentProfileLink;
			socialManagementService.updateLinkedin(agentSettings, message);
			for (OrganizationUnitSettings setting : settings) {
				socialManagementService.updateLinkedin(setting, message);
			}
			List<String> socialSites = new ArrayList<>();
			socialSites.add(CommonConstants.TWITTER_SOCIAL_SITE);
			surveyHandler.updateSharedOn(socialSites, agentId, customerEmail);
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException caught in postToTwitter(). Nested exception is ", e);
		}
		LOG.info("Method to post feedback of customer to twitter finished.");
		return "";
	}

	@ResponseBody
	@RequestMapping(value = "/displaypiclocationofagent", method = RequestMethod.GET)
	public String getDisplayPicLocationOfAgent(HttpServletRequest request) {
		LOG.info("Method to get location of agent's image started.");
		String picLocation = "";
		String agentIdStr = request.getParameter("agentId");
		long agentId = 0;

		if (agentIdStr == null || agentIdStr.isEmpty()) {
			LOG.error("Null value found for agent Id in request param.");
			return "Null value found for agent Id in request param.";
		}

		try {
			agentId = Long.parseLong(agentIdStr);
		}
		catch (NumberFormatException e) {
			LOG.error("NumberFormatException caught in getDisplayPicLocationOfAgent() while getting agent id");
			return e.getMessage();
		}

		try {
			AgentSettings agentSettings = userManagementService.getUserSettings(agentId);
			if (agentSettings != null) {
				if (agentSettings.getProfileImageUrl() != null && !agentSettings.getProfileImageUrl().isEmpty()) {
					picLocation = agentSettings.getProfileImageUrl();
				}
			}
		}
		catch (InvalidInputException e) {
			LOG.error("InvalidInputException caught in getDisplayPicLocationOfAgent() while fetching image for agent.");
			return e.getMessage();
		}

		LOG.info("Method to get location of agent's image finished.");
		return picLocation;
	}

	@ResponseBody
	@RequestMapping(value = "/getyelplinkrest", method = RequestMethod.GET)
	public String getYelpLinkRest(HttpServletRequest request) {
		LOG.info("Method to get Yelp details, getYelpLink() started.");
		Map<String, String> yelpUrl = new HashMap<String, String>();
		try {
			String agentIdStr = request.getParameter("agentId");
			if (agentIdStr == null || agentIdStr.isEmpty()) {
				throw new InvalidInputException("InvalidInputException caught in getYelpLinkRest(). Agent Id cannot be null or empty.");
			}

			long agentId = 0;
			try {
				agentId = Long.parseLong(agentIdStr);
			}
			catch (NumberFormatException e) {
				LOG.error("NumberFormatException caught while trying to convert agentId in getYelpLink(). Nested exception is ", e);
				throw e;
			}

			OrganizationUnitSettings settings = userManagementService.getUserSettings(agentId);

			if (settings.getSocialMediaTokens() == null || settings.getSocialMediaTokens().getYelpToken() == null) {

			}
			else {
				yelpUrl.put("host", yelpRedirectUri);
				yelpUrl.put("relativePath", settings.getSocialMediaTokens().getYelpToken().getYelpPageLink());
			}
		}
		catch (NonFatalException e) {
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
	@RequestMapping(value = "/getgooglepluslinkrest", method = RequestMethod.GET)
	public String getGooglePlusLinkRest(HttpServletRequest request) {
		LOG.info("Method to get Google details, getGooglePlusLink() started.");
		Map<String, String> googleUrl = new HashMap<String, String>();
		try {
			String agentIdStr = request.getParameter("agentId");
			if (agentIdStr == null || agentIdStr.isEmpty()) {
				throw new InvalidInputException("InvalidInputException caught in getYelpLinkRest(). Agent Id cannot be null or empty.");
			}

			long agentId = 0;
			try {
				agentId = Long.parseLong(agentIdStr);
			}
			catch (NumberFormatException e) {
				LOG.error("NumberFormatException caught while trying to convert agentId in getYelpLink(). Nested exception is ", e);
				throw e;
			}

			OrganizationUnitSettings settings = userManagementService.getUserSettings(agentId);

			if (settings.getProfileUrl() != null) {
				googleUrl.put("host", surveyHandler.getGoogleShareUri());
				googleUrl.put("profileServer", surveyHandler.getApplicationBaseUrl() + "pages");
				googleUrl.put("relativePath", settings.getProfileUrl());
			}
		}
		catch (NonFatalException e) {
			LOG.error("Exception occured in getGooglePlusLink() while trying to post into Google.");
			ErrorResponse response = new ErrorResponse();
			response.setErrCode("Error while trying to post on Google.");
			response.setErrMessage(e.getMessage());
			return new Gson().toJson(response);
		}
		LOG.info("Method to get Google details, getGooglePlusLink() finished.");
		return new Gson().toJson(googleUrl);
	}

	@ResponseBody
	@RequestMapping(value = "/updatesharedon", method = RequestMethod.GET)
	public String updateSharedOn(HttpServletRequest request) {
		LOG.info("Method to get update shared on social sites details, updateSharedOn() started.");

		try {
			String agentIdStr = request.getParameter("agentId");
			String customerEmail = request.getParameter("customerEmail");
			String socialSite = request.getParameter("socialSite");
			if (agentIdStr == null || agentIdStr.isEmpty()) {
				throw new InvalidInputException("InvalidInputException caught in getYelpLinkRest(). Agent Id cannot be null or empty.");
			}

			long agentId = 0;
			try {
				agentId = Long.parseLong(agentIdStr);
			}
			catch (NumberFormatException e) {
				LOG.error("NumberFormatException caught while trying to convert agentId in getYelpLink(). Nested exception is ", e);
				throw e;
			}
			List<String> socialSites = new ArrayList<>();
			socialSites.add(socialSite);
			surveyHandler.updateSharedOn(socialSites, agentId, customerEmail);

		}
		catch (NonFatalException e) {
			LOG.error("Exception occured in updateSharedOn() while trying to post into Google.");
			ErrorResponse response = new ErrorResponse();
			response.setErrCode("");
			response.setErrMessage(e.getMessage());
			return new Gson().toJson(response);
		}
		LOG.info("Method to get Google details, updateSharedOn() finished.");
		return new Gson().toJson("Success");
	}

	@ResponseBody
	@RequestMapping(value = "/makesurveyeditable")
	public void makeSurveyEditable(HttpServletRequest request) {
		String agentIdStr = request.getParameter("agentId");
		String customerEmail = request.getParameter("customerEmail");
		try {
			if (agentIdStr == null || agentIdStr.isEmpty()) {
				throw new InvalidInputException("Invalid value (Null/Empty) found for agentId.");
			}
			long agentId = Long.parseLong(agentIdStr);
			surveyHandler.changeStatusOfSurvey(agentId, customerEmail, true);
		}
		catch (NonFatalException e) {
			LOG.error("NonfatalException caught in makeSurveyEditable(). Nested exception is ", e);
		}
	}

	@RequestMapping(value = "/notfound")
	public String showNotFoundPage(HttpServletRequest request) {
		return JspResolver.NOT_FOUND_PAGE;
	}	
	
	private SurveyDetails storeInitialSurveyDetails(long agentId, String customerEmail, String firstName, String lastName, int reminderCount,
			String custRelationWithAgent, String url) throws SolrException, NoRecordsFetchedException, InvalidInputException {
		return surveyHandler.storeInitialSurveyDetails(agentId, customerEmail, firstName, lastName, reminderCount, custRelationWithAgent, url);
	}

	private String getApplicationBaseUrl() {
		return surveyHandler.getApplicationBaseUrl();
	}

	private Map<String, Object> getSurvey(long agentId, String customerEmail, String firstName, String lastName, int reminderCount,
			String custRelationWithAgent, String url) throws InvalidInputException, SolrException, NoRecordsFetchedException {
		Integer stage = null;
		Map<String, Object> surveyAndStage = new HashMap<>();
		List<SurveyQuestionDetails> surveyQuestionDetails = surveyBuilder.getSurveyByAgenId(agentId);
		boolean editable = false;
		try {
			SurveyDetails survey = storeInitialSurveyDetails(agentId, customerEmail, firstName, lastName, reminderCount, custRelationWithAgent, url);
			surveyHandler.updateSurveyAsClicked(agentId, customerEmail);
			if (survey != null) {
				stage = survey.getStage();
				editable = survey.getEditable();
				surveyAndStage.put("agentName", survey.getAgentName());
				surveyAndStage.put("customerEmail", customerEmail);
				surveyAndStage.put("customerFirstName", survey.getCustomerFirstName());
				surveyAndStage.put("customerLastName", survey.getCustomerLastName());
				for (SurveyQuestionDetails surveyDetails : surveyQuestionDetails) {
					for (SurveyResponse surveyResponse : survey.getSurveyResponse()) {
						if (surveyDetails.getQuestion().trim().equalsIgnoreCase(surveyResponse.getQuestion())) {
							surveyDetails.setCustomerResponse(surveyResponse.getAnswer());
						}
					}
				}
			}
		}
		catch (SolrException e) {
			LOG.error("SolrException caught in triggerSurvey(). Details are " + e);
			throw e;
		}

		OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings(userManagementService.getUserByUserId(agentId));
		if (companySettings != null) {
			SurveySettings surveySettings = companySettings.getSurvey_settings();
			if (surveySettings != null) {
				surveyAndStage.put("happyText", surveySettings.getHappyText());
				surveyAndStage.put("neutralText", surveySettings.getNeutralText());
				surveyAndStage.put("sadText", surveySettings.getSadText());
				surveyAndStage.put("autopostScore", surveySettings.getShow_survey_above_score());
				surveyAndStage.put("autopostEnabled", surveySettings.isAutoPostEnabled());
			}
		}

		AgentSettings agentSettings = userManagementService.getUserSettings(agentId);
		try {
			agentSettings.getSocialMediaTokens().getYelpToken().getYelpPageLink();
			surveyAndStage.put("yelpEnabled", true);
		}
		catch (NullPointerException e) {
			surveyAndStage.put("yelpEnabled", false);
		}
		try {
			agentSettings.getSocialMediaTokens().getGoogleToken().getProfileLink();
			surveyAndStage.put("googleEnabled", true);
		}
		catch (NullPointerException e) {
			surveyAndStage.put("googleEnabled", false);
		}
		surveyAndStage.put("agentProfileLink", agentSettings.getProfileUrl());
		surveyAndStage.put("stage", stage);
		surveyAndStage.put("survey", surveyQuestionDetails);
		surveyAndStage.put("agentId", agentId);
		surveyAndStage.put("editable", editable);
		return surveyAndStage;
	}

}
// JIRA SS-119 by RM-05 : EOC