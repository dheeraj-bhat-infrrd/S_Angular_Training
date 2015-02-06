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
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyBuilder;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
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

	/*
	 * Method to retrieve survey questions for a survey based upon the company id and agent id.
	 */
	@ResponseBody
	@RequestMapping(value = "/data/{agentIdStr}/{customerEmail}")
	public String getSurvey(@PathVariable String agentIdStr, @PathVariable String customerEmail) {
		String survey = "{}";
		try {
			LOG.info("Service to get survey called.");

			if (agentIdStr == null || agentIdStr.isEmpty()) {
				throw new InvalidInputException("Agent name is not specified for getting surveydetails.");
			}
			long agentId = 0;
			try {
				agentId = Long.parseLong(agentIdStr);
			}
			catch (NumberFormatException e) {
				LOG.error("NumberFormat exception caught in getSurvey() method for agentId {}.", agentIdStr);
				throw e;
			}
			List<SurveyQuestionDetails> surveyQuestionDetails = surveyBuilder.getSurveyByAgenId(agentId);
			// surveyHandler.storeInitialSurveyAnswers(surveyDetails);
			survey = new Gson().toJson(surveyQuestionDetails);
		}
		catch (NonFatalException e) {
			LOG.error("Exception caught in getSurvey() method of SurveyManagementController.");
			return "{error:"+e.getMessage()+"}";
		}
		LOG.info("Service to get survey executed successfully");
		return survey;
	}

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
		String answer = request.getParameter("answer");
		String question = request.getParameter("question");
		String questionType = request.getParameter("questionType");
		int stage = Integer.parseInt(request.getParameter("stage"));
		String customerEmail = request.getParameter("customerEmail");
		long agentId = Long.valueOf(request.getParameter("agentId"));
		surveyHandler.updateCustomerAnswersInSurvey(agentId, customerEmail, question, questionType, answer, stage);
		LOG.info("Method storeFeedback() finished to store response of customer.");
	}

	/*
	 * Method to render page for Survey questions.
	 */
	@RequestMapping(value = "/{agentIdStr}/{customerEmailId}")
	public String showSurveyPage(Model model, @PathVariable String agentIdStr, @PathVariable String customerEmailId) {
		model.addAttribute("agentId", agentIdStr);
		model.addAttribute("customerEmailId", customerEmailId);
		return "surveyQuestion";
	}

	/*
	 * Method to store questions and other details into mongo initially.
	 */
	@ResponseBody
	@RequestMapping(value = "/triggersurvey/{customerEmail}/{companyIdStr}/{regionIdStr}/{branchIdStr}/{agentIdStr}/")
	public String triggerSurvey(Model model, @PathVariable String customerEmail, @PathVariable String companyIdStr, @PathVariable String regionIdStr,
			@PathVariable String branchIdStr, @PathVariable String agentIdStr) {
		LOG.info("Method to store initial values for a survey, triggerSurvey() started");
		try {
			long agentId = 0;
			long branchId = 0;
			long regionId = 0;
			long companyId = 0;
			try {
				agentId = Long.parseLong(agentIdStr);
				branchId = Long.parseLong(branchIdStr);
				regionId = Long.parseLong(regionIdStr);
				companyId = Long.parseLong(companyIdStr);
			}
			catch (NumberFormatException e) {
				LOG.error("NumberFormatException caught in triggerSurvey(). Details are " + e);
				throw e;
			}
			try {
				surveyHandler.storeInitialSurveyDetails(agentId, companyId, regionId, branchId, customerEmail, 0);
			}
			catch (SolrServerException e) {
				LOG.error("SolrServerException caught in triggerSurvey(). Details are " + e);
			}
		}
		catch (NonFatalException e) {
			LOG.error("Non Fatal exception caught in triggerSurvey() method of SurveyManagementController.", e);
		}
		LOG.info("Method to store initial values for a survey, triggerSurvey() finished");
		return "Successfully stored! Ab ghar jaiye aur so jaiye.";
	}
}
// JIRA SS-119 by RM-05 : EOC