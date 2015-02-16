package com.realtech.socialsurvey.web.rest;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.google.gson.Gson;
import com.realtech.socialsurvey.core.entities.SurveyQuestionDetails;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyBuilder;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;

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

	/*
	 * Method to store answer to the current question of the survey.
	 */
	@RequestMapping(value = "/data/storeAnswer")
	public void storeSurveyAnswer(HttpServletRequest request) {
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
	}
	
	/*
	 * Method to store final feedback of the survey from customer.
	 */
	@RequestMapping(value = "/data/storeFeedback")
	public void storeFeedback(HttpServletRequest request) {
		LOG.info("Method storeFeedback() started to store response of customer.");
		// TODO store answer provided by customer in mongoDB.
		String feedback = request.getParameter("feedback");
		String mood = request.getParameter("mood");
		String customerEmail = request.getParameter("customerEmail");
		long agentId = Long.valueOf(request.getParameter("agentId"));
		surveyHandler.updateGatewayQuestionResponseAndScore(agentId, customerEmail, mood, feedback);
		LOG.info("Method storeFeedback() finished to store response of customer.");
	}
	
	@ResponseBody
	@RequestMapping(value="/redirecttodetailspage")
	public String showDetailsPage(Model model, HttpServletRequest request){
		LOG.info("Method to redirect to survey page started.");
		String agentId = request.getParameter("userId");
		LOG.info("Method to redirect to survey page started.");
		return getApplicationBaseUrl()+"rest/survey/showsurveypage/"+agentId;
	}
	
	@RequestMapping(value="/showsurveypage/{agentId}")
	public String initiateSurvey(Model model, @PathVariable String agentId){
		LOG.info("Method to start survey initiateSurvey() started.");
		model.addAttribute("agentId", agentId);
		LOG.info("Method to start survey initiateSurvey() finished.");
		return "surveyQuestion";
	}
	
	/*
	 * Method to retrieve survey questions for a survey based upon the company id and agent id.
	 */
	
	@ResponseBody
	@RequestMapping(value = "/triggersurvey")
	public String triggerSurvey(HttpServletRequest request){
		LOG.info("Method to store initial details of customer and agent and to get questions of survey, triggerSurvey() started.");
		String survey=null;
		try {
			long agentId = 0;
			String customerEmail;
			String firstName;
			String lastName;
			try {
				String user = request.getParameter("agentId");
				agentId = Long.parseLong(user);
				customerEmail = request.getParameter("customerEmail");
				firstName = request.getParameter("firstName");
				lastName = request.getParameter("lastName");
			}
			catch (NumberFormatException e) {
				LOG.error("NumberFormatException caught in triggerSurvey(). Details are " + e);
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e);
			}
			try {
				storeInitialSurveyDetails(agentId, customerEmail, firstName, lastName, 0);
			}
			catch (SolrServerException e) {
				LOG.error("SolrServerException caught in triggerSurvey(). Details are " + e);
			}
			List<SurveyQuestionDetails> surveyQuestionDetails = surveyBuilder.getSurveyByAgenId(agentId);
			// surveyHandler.storeInitialSurveyAnswers(surveyDetails);
			survey = new Gson().toJson(surveyQuestionDetails);
		}
		catch (NonFatalException e) {
			LOG.error("Exception caught in getSurvey() method of SurveyManagementController.");
			return "{error:" + e.getMessage() + "}";
		}
		LOG.info("Method to store initial details of customer and agent and to get questions of survey, triggerSurvey() started.");
		return survey;
	}
		
	private String storeInitialSurveyDetails(long agentId, String customerEmail, String firstName, String lastName, int reminderCount) throws SolrException, NoRecordsFetchedException, InvalidInputException, SolrServerException{
		return surveyHandler.storeInitialSurveyDetails(agentId, customerEmail, firstName, lastName, reminderCount);
	}
	
	private String getApplicationBaseUrl(){
		return surveyHandler.getApplicationBaseUrl();
	}
}
// JIRA SS-119 by RM-05 : EOC