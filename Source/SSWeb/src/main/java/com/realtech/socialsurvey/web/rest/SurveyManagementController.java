package com.realtech.socialsurvey.web.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.noggit.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.google.gson.Gson;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyQuestionDetails;
import com.realtech.socialsurvey.core.entities.SurveyResponse;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.authentication.CaptchaValidation;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyBuilder;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.common.ErrorCodes;
import com.realtech.socialsurvey.web.common.ErrorResponse;

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

	
	/*
	 * Method to store answer to the current question of the survey.
	 */
	@ResponseBody
	@RequestMapping(value = "/data/storeAnswer")
	public String storeSurveyAnswer(HttpServletRequest request) {
		LOG.info("Method storeSurveyAnswer() started to store response of customer.");
		// TODO store answer provided by customer in mongoDB.
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
	@RequestMapping(value = "/data/storeFeedback")
	public void storeFeedback(HttpServletRequest request) {
		LOG.info("Method storeFeedback() started to store response of customer.");
		// To store final feedback provided by customer in mongoDB.
		try {
			String feedback = request.getParameter("feedback");
			String mood = request.getParameter("mood");
			String customerEmail = request.getParameter("customerEmail");
			long agentId = Long.valueOf(request.getParameter("agentId"));
			boolean isAbusive = Boolean.parseBoolean(request.getParameter("isAbusive"));
			surveyHandler.updateGatewayQuestionResponseAndScore(agentId, customerEmail, mood, feedback, isAbusive);

			// Sending email to the customer telling about successful completion of survey.
			SurveyDetails survey = surveyHandler.getSurveyDetails(agentId, customerEmail);
			try {
				emailServices.queueSurveyCompletionMail(customerEmail, survey.getCustomerName(), survey.getAgentName());
			}
			catch (InvalidInputException e) {
				LOG.error("Exception occurred while trying to send survey completion mail to : " + customerEmail);
				throw e;
			}
		}
		catch (NonFatalException e) {
			LOG.error("Non fatal exception caught in storeFeedback(). Nested exception is ", e);
			return;
		}
		LOG.info("Method storeFeedback() finished to store response of customer.");
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
		Long agentId = Long.parseLong(agentIdStr);
		String agentName = "";
		try {
			agentName = solrSearchService.getUserDisplayNameById(agentId);
		}
		catch (SolrException | NoRecordsFetchedException | SolrServerException e) {
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
				throw new InvalidInputException("Captcha Validation failed!", DisplayMessageConstants.INVALID_CAPTCHA);
			}
			List<SurveyQuestionDetails> surveyQuestionDetails = surveyBuilder.getSurveyByAgenId(agentId);
			try {
				SurveyDetails survey = storeInitialSurveyDetails(agentId, customerEmail, firstName, lastName, 0, custRelationWithAgent);
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

	private SurveyDetails storeInitialSurveyDetails(long agentId, String customerEmail, String firstName, String lastName, int reminderCount,
			String custRelationWithAgent) throws SolrException, NoRecordsFetchedException, InvalidInputException, SolrServerException {
		return surveyHandler.storeInitialSurveyDetails(agentId, customerEmail, firstName, lastName, reminderCount, custRelationWithAgent);
	}

	private String getApplicationBaseUrl() {
		return surveyHandler.getApplicationBaseUrl();
	}
}
// JIRA SS-119 by RM-05 : EOC