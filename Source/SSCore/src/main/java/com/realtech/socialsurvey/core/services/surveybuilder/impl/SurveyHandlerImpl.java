package com.realtech.socialsurvey.core.services.surveybuilder.impl;

import java.util.ArrayList;
import java.util.Date;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.SurveyDetailsDao;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyResponse;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;

// JIRA SS-119 by RM-05:BOC
@Component
public class SurveyHandlerImpl implements SurveyHandler {

	private static final Logger LOG = LoggerFactory.getLogger(SurveyHandlerImpl.class);

	@Autowired
	private SolrSearchService solrSearchService;

	@Autowired
	private SurveyDetailsDao surveyDetailsDao;

	/**
	 * Method to store question and answer format into mongo.
	 * 
	 * @param agentId
	 * @throws Exception
	 */
	@Override
	public void storeInitialSurveyDetails(long agentId, long companyId, long regionId, long branchId, String customerEmail, int reminderCount)
			throws SolrException, NoRecordsFetchedException, SolrServerException {
		String agentName;
		LOG.info("Method to store initial details of survey, storeInitialSurveyAnswers() started.");
		try {
			agentName = solrSearchService.getUserDisplayNameById(agentId);
		}
		catch (SolrException | NoRecordsFetchedException | SolrServerException e) {
			LOG.error("Exception caught in storeInitialSurveyAnswers () while fetching user's name from Solr.", e);
			throw e;
		}
		SurveyDetails surveyDetails = new SurveyDetails();
		surveyDetails.setAgentId(agentId);
		surveyDetails.setAgentName(agentName);
		surveyDetails.setBranchId(branchId);
		surveyDetails.setCompanyId(companyId);
		surveyDetails.setCustomerEmail(customerEmail);
		surveyDetails.setRegionId(regionId);
		surveyDetails.setStage(CommonConstants.INITIAL_INDEX);
		surveyDetails.setReminderCount(reminderCount);
		surveyDetails.setUpdatedOn(new Date());
		surveyDetails.setSurveyResponse(new ArrayList<SurveyResponse>());
		surveyDetailsDao.updateEmailForExistingFeedback(agentId, customerEmail);
		surveyDetailsDao.insertSurveyDetails(surveyDetails);
		LOG.info("Method to store initial details of survey, storeInitialSurveyAnswers() finished.");
	}

	/*
	 * Method to update answers to all the questions and current stage in MongoDB.
	 * @param agentId
	 * @param customerEmail
	 * @param question
	 * @param answer
	 * @param stage
	 * @throws Exception
	 */
	@Override
	public void updateCustomerAnswersInSurvey(long agentId, String customerEmail, String question, String questionType, String answer, int stage) {
		LOG.info("Method to update answers provided by customer in SURVEY_DETAILS, updateCustomerAnswersInSurvey() started.");
		SurveyResponse surveyResponse = new SurveyResponse();
		surveyResponse.setAnswer(answer);
		surveyResponse.setQuestion(question);
		surveyResponse.setQuestionType(questionType);
		surveyDetailsDao.updateCustomerResponse(agentId, customerEmail, surveyResponse, stage);
		LOG.info("Method to update answers provided by customer in SURVEY_DETAILS, updateCustomerAnswersInSurvey() finished.");
	}
	
	/*
	 * Method to update customer review and final score on the basis of rating questions in SURVEY_DETAILS.
	 */
	@Override
	public void updateGatewayQuestionResponseAndScore(long agentId, String customerEmail, String mood, String review){
		LOG.info("Method to update customer review and final score on the basis of rating questions in SURVEY_DETAILS, updateCustomerAnswersInSurvey() started.");
		surveyDetailsDao.updateGatewayAnswer(agentId, customerEmail, mood, review);
		surveyDetailsDao.updateFinalScore(agentId, customerEmail);
		LOG.info("Method to update customer review and final score on the basis of rating questions in SURVEY_DETAILS, updateCustomerAnswersInSurvey() finished.");
	}
}
// JIRA SS-119 by RM-05:EOC
