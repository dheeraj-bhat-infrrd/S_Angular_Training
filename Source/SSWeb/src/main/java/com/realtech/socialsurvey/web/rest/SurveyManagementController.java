package com.realtech.socialsurvey.web.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.noggit.JSONUtil;
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
			String agentIdStr = request.getParameter("agentId");
			long agentId = 0;
			if (agentIdStr == null || agentIdStr.isEmpty()) {
				LOG.error("Null/empty value found for agentId in storeFeedback().");
				throw new InvalidInputException("Null/empty value found for agentId in storeFeedback().");
			}
			try {
				agentId = Long.valueOf(agentIdStr);
			}
			catch (NumberFormatException e) {
				LOG.error("NumberFormatException occurred in storeFeedback() while getting agentId.");
			}
			boolean isAbusive = Boolean.parseBoolean(request.getParameter("isAbusive"));
			surveyHandler.updateGatewayQuestionResponseAndScore(agentId, customerEmail, mood, feedback, isAbusive);

			// TODO Search Engine Optimisation

			if (mood == null || mood.isEmpty()) {
				LOG.error("Null/empty value found for mood in storeFeedback().");
				throw new InvalidInputException("Null/empty value found for mood in storeFeedback().");
			}
			List<String> emailIdsToSendMail = new ArrayList<>();
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
				if (enableKafka.equals(CommonConstants.YES)) {
					emailServices.queueSurveyCompletionMail(customerEmail, survey.getCustomerFirstName() + " " + survey.getCustomerLastName(),
							survey.getAgentName());
				}
				else {
					emailServices.sendSurveyCompletionMail(customerEmail, survey.getCustomerFirstName() + " " + survey.getCustomerLastName(),
							survey.getAgentName());
				}
				if (enableKafka.equals(CommonConstants.YES)) {
					for (String emailId : emailIdsToSendMail) {
						emailServices.queueSurveyCompletionMailToAdmins(emailId, survey.getCustomerFirstName() + " " + survey.getCustomerLastName(),
								survey.getAgentName(), mood);
					}
				}
				else {
					for (String emailId : emailIdsToSendMail) {
						emailServices.sendSurveyCompletionMailToAdmins(emailId, survey.getCustomerFirstName() + " " + survey.getCustomerLastName(),
								survey.getAgentName(), mood);
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
			return "errorpage500";
		}
		Long agentId = 0l;
		try {
			agentId = Long.parseLong(agentIdStr);
		}
		catch (NumberFormatException e) {
			LOG.error("Invalid agent Id passed. Error is : " + e);
			return "errorpage500";
		}
		String agentName = "";
		try {
			agentName = solrSearchService.getUserDisplayNameById(agentId);
		}
		catch (NoRecordsFetchedException | InvalidInputException | SolrServerException e) {
			LOG.error("Error occured while fetching display name of agent. Error is : " + e);
			return "errorpage500";
		}
		model.addAttribute("agentId", agentId);
		model.addAttribute("agentName", agentName);
		LOG.info("Method to start survey initiateSurvey() finished.");
		return "surveyQuestion";
	}

	/*
	 * Method to retrieve survey questions for a survey based upon the company id and agent id.
	 */

	@ResponseBody
	@RequestMapping(value = "/triggersurvey")
	public String triggerSurvey(Model model, HttpServletRequest request) {
		LOG.info("Method to store initial details of customer and agent and to get questions of survey, triggerSurvey() started.");
		Integer stage = null;
		Map<String, Object> surveyAndStage = new HashMap<>();
		try {
			long agentId = 0;
			String customerEmail;
			String firstName;
			String lastName;
			String captchaResponse;
			String challengeField;
			String custRelationWithAgent;
			String url;
			try {
				String user = request.getParameter(CommonConstants.AGENT_ID_COLUMN);
				agentId = Long.parseLong(user);
				customerEmail = request.getParameter(CommonConstants.CUSTOMER_EMAIL_COLUMN);
				firstName = request.getParameter("firstName");
				lastName = request.getParameter("lastName");
				captchaResponse = request.getParameter("captchaResponse");
				challengeField = request.getParameter("recaptcha_challenge_field");
				custRelationWithAgent = request.getParameter("relationship");
				url = getApplicationBaseUrl() + "rest/survey/showsurveypage/" + agentId;
			}
			catch (NumberFormatException e) {
				LOG.error("NumberFormatException caught in triggerSurvey(). Details are " + e);
				throw e;
			}
			if (!captchaValidation.isCaptchaValid(request.getRemoteAddr(), challengeField, captchaResponse)) {
				LOG.error("Captcha Validation failed!");
				throw new InvalidInputException("Captcha Validation failed!", DisplayMessageConstants.INVALID_CAPTCHA);
			}
			List<SurveyQuestionDetails> surveyQuestionDetails = surveyBuilder.getSurveyByAgenId(agentId);
			try {
				SurveyDetails survey = storeInitialSurveyDetails(agentId, customerEmail, firstName, lastName, 0, custRelationWithAgent, url);
				surveyHandler.updateSurveyAsClicked(agentId, customerEmail);
				if (survey != null) {
					stage = survey.getStage();
					for (SurveyQuestionDetails surveyDetails : surveyQuestionDetails) {
						for (SurveyResponse surveyResponse : survey.getSurveyResponse()) {
							if (surveyDetails.getQuestion().trim().equalsIgnoreCase(surveyResponse.getQuestion())) {
								surveyDetails.setCustomerResponse(surveyResponse.getAnswer());
							}
						}
					}
				}
			}
			catch (SolrServerException e) {
				LOG.error("SolrServerException caught in triggerSurvey(). Details are " + e);
				ErrorResponse errorResponse = new ErrorResponse();
				errorResponse.setErrCode(ErrorCodes.REQUEST_FAILED);
				errorResponse.setErrMessage("Agent not found.");
				String errorMessage = JSONUtil.toJSON(errorResponse);
				return errorMessage;
			}

			OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings(userManagementService
					.getUserByUserId(agentId));
			if (companySettings != null) {
				SurveySettings surveySettings = companySettings.getSurvey_settings();
				if (surveySettings != null) {
					surveyAndStage.put("happyText", surveySettings.getHappyText());
					surveyAndStage.put("neutralText", surveySettings.getNeutralText());
					surveyAndStage.put("sadText", surveySettings.getSadText());
					surveyAndStage.put("autopostScore", surveySettings.getAuto_post_score());
					surveyAndStage.put("autopostEnabled", surveySettings.isAutoPostEnabled());
				}
			}
			surveyAndStage.put("stage", stage);
			surveyAndStage.put("survey", surveyQuestionDetails);

		}
		catch (NonFatalException e) {
			LOG.error("Exception caught in getSurvey() method of SurveyManagementController.");
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrCode(ErrorCodes.REQUEST_FAILED);
			errorResponse.setErrMessage("No survey found!");
			String errorMessage = new Gson().toJson(errorResponse);
			return errorMessage;
		}
		LOG.info("Method to store initial details of customer and agent and to get questions of survey, triggerSurvey() started.");
		return new Gson().toJson(surveyAndStage);
	}

	@ResponseBody
	@RequestMapping(value = "/posttosocialnetwork", method = RequestMethod.GET)
	public String postToSocialMedia(HttpServletRequest request) {
		LOG.info("Method to post feedback of customer to various pages of social networking sites started.");
		try {
			String agentName = request.getParameter("agentName");
			String custFirstName = request.getParameter("firstName");
			String custLastName = request.getParameter("lastName");
			String agentIdStr = request.getParameter("agentId");
			String ratingStr = request.getParameter("rating");
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
			String facebookMessage = rating + "-Star Survey Response from " + custFirstName + " " + custLastName + " for " + agentName
					+ " on Social Survey \n";
			String twitterMessage = rating + "-Star Survey Response from " + custFirstName + custLastName + "for " + agentName
					+ " on @SocialSurvey - view at www.social-survey.com/" + agentIdStr;
			try {
				socialManagementService.updateStatusIntoFacebookPage(agentSettings, facebookMessage);
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
		}
		catch (NonFatalException e) {
			LOG.error("Non fatal Exception caught in postToSocialMedia() while trying to post to social networking sites. Nested excption is ", e);
			return e.getMessage();
		}
		LOG.info("Method to post feedback of customer to various pages of social networking sites finished.");
		return "Successfully posted to all the places in hierarchy";
	}

	private SurveyDetails storeInitialSurveyDetails(long agentId, String customerEmail, String firstName, String lastName, int reminderCount,
			String custRelationWithAgent, String url) throws SolrException, NoRecordsFetchedException, InvalidInputException, SolrServerException {
		return surveyHandler.storeInitialSurveyDetails(agentId, customerEmail, firstName, lastName, reminderCount, custRelationWithAgent, url);
	}

	private String getApplicationBaseUrl() {
		return surveyHandler.getApplicationBaseUrl();
	}
}
// JIRA SS-119 by RM-05 : EOC