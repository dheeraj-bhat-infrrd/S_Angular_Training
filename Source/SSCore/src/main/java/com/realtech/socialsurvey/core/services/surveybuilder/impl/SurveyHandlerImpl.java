package com.realtech.socialsurvey.core.services.surveybuilder.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.google.gson.Gson;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.SurveyDetailsDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyResponse;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;

// JIRA SS-119 by RM-05:BOC
@Component
public class SurveyHandlerImpl implements SurveyHandler, InitializingBean {

	private static final Logger LOG = LoggerFactory
			.getLogger(SurveyHandlerImpl.class);

	private static String SWEAR_WORDS;

	@Autowired
	private SolrSearchService solrSearchService;

	@Autowired
	private SurveyDetailsDao surveyDetailsDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private URLGenerator urlGenerator;

	@Value("${APPLICATION_BASE_URL}")
	private String applicationBaseUrl;

	/**
	 * Method to store question and answer format into mongo.
	 * 
	 * @param agentId
	 * @throws InvalidInputException
	 * @throws Exception
	 */
	@Override
	@Transactional
	public SurveyDetails storeInitialSurveyDetails(long agentId,
			String customerEmail, String firstName, String lastName,
			int reminderCount, String custRelationWithAgent)
			throws SolrException, NoRecordsFetchedException,
			SolrServerException, InvalidInputException {

		LOG.info("Method to store initial details of survey, storeInitialSurveyAnswers() started.");

		String agentName;
		long branchId = 0;
		long companyId = 0;
		long regionId = 0;

		User user = userDao.findById(User.class, agentId);
		companyId = user.getCompany().getCompanyId();
		agentName = user.getFirstName() + " " + user.getLastName();
		for (UserProfile userProfile : user.getUserProfiles()) {
			if (userProfile.getAgentId() == agentId) {
				branchId = userProfile.getBranchId();
				regionId = userProfile.getRegionId();
			}
		}

		SurveyDetails surveyDetails = new SurveyDetails();
		surveyDetails.setAgentId(agentId);
		surveyDetails.setAgentName(agentName);
		surveyDetails.setBranchId(branchId);
		surveyDetails.setCustomerName(firstName + " " + lastName);
		surveyDetails.setCompanyId(companyId);
		surveyDetails.setCustomerEmail(customerEmail);
		surveyDetails.setRegionId(regionId);
		surveyDetails.setStage(CommonConstants.INITIAL_INDEX);
		surveyDetails.setReminderCount(reminderCount);
		surveyDetails.setUpdatedOn(new Date());
		surveyDetails.setSurveyResponse(new ArrayList<SurveyResponse>());
		surveyDetails.setCustRelationWithAgent(custRelationWithAgent);
		SurveyDetails survey = surveyDetailsDao
				.getSurveyByAgentIdAndCustomerEmail(agentId, customerEmail);
		LOG.info("Method to store initial details of survey, storeInitialSurveyAnswers() finished.");
		if (survey == null) {
			surveyDetailsDao.insertSurveyDetails(surveyDetails);
			return null;
		} else {
			return survey;
		}
	}

	/*
	 * Method to update answers to all the questions and current stage in
	 * MongoDB.
	 * 
	 * @param agentId
	 * 
	 * @param customerEmail
	 * 
	 * @param question
	 * 
	 * @param answer
	 * 
	 * @param stage
	 * 
	 * @throws Exception
	 */
	@Override
	public void updateCustomerAnswersInSurvey(long agentId,
			String customerEmail, String question, String questionType,
			String answer, int stage) {
		LOG.info("Method to update answers provided by customer in SURVEY_DETAILS, updateCustomerAnswersInSurvey() started.");
		SurveyResponse surveyResponse = new SurveyResponse();
		surveyResponse.setAnswer(answer);
		surveyResponse.setQuestion(question);
		surveyResponse.setQuestionType(questionType);
		surveyDetailsDao.updateCustomerResponse(agentId, customerEmail,
				surveyResponse, stage);
		LOG.info("Method to update answers provided by customer in SURVEY_DETAILS, updateCustomerAnswersInSurvey() finished.");
	}

	/*
	 * Method to update customer review and final score on the basis of rating
	 * questions in SURVEY_DETAILS.
	 */
	@Override
	public void updateGatewayQuestionResponseAndScore(long agentId,
			String customerEmail, String mood, String review, boolean isAbusive) {
		LOG.info("Method to update customer review and final score on the basis of rating questions in SURVEY_DETAILS, updateCustomerAnswersInSurvey() started.");
		surveyDetailsDao.updateGatewayAnswer(agentId, customerEmail, mood,
				review, isAbusive);
		surveyDetailsDao.updateFinalScore(agentId, customerEmail);
		LOG.info("Method to update customer review and final score on the basis of rating questions in SURVEY_DETAILS, updateCustomerAnswersInSurvey() finished.");
	}

	public SurveyDetails getSurveyDetails(long agentId, String customerEmail) {
		LOG.info("Method getSurveyDetails() to return survey details by agent id and customer email started.");
		SurveyDetails surveyDetails;
		surveyDetails = surveyDetailsDao.getSurveyByAgentIdAndCustomerEmail(
				agentId, customerEmail);
		LOG.info("Method getSurveyDetails() to return survey details by agent id and customer email finished.");
		return surveyDetails;
	}

	@Override
	public String getApplicationBaseUrl() {
		return applicationBaseUrl;
	}

	@Override
	public String getSwearWords() {
		return SWEAR_WORDS;
	}

	/*
	 * Loads string of swear words from file containing all the swear words.
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		Resource resource = new ClassPathResource("swear-words");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					resource.getInputStream()));
			String text;
			List<String> swearWordsLst = new ArrayList<>();
			text = br.readLine();
			while (text != null) {
				swearWordsLst.add(text.trim());
				text = br.readLine();
			}
			SWEAR_WORDS = new Gson().toJson(swearWordsLst);
		} catch (IOException e) {
			LOG.error(
					"Error parsing list of swear words. Nested exception is ",
					e);
			throw e;
		}
	}

	/*
	 * Method to update a survey as clicked when user triggers the survey and
	 * page of the first question starts loading.
	 */
	public void updateSurveyAsClicked(long agentId, String customerEmail) {
		LOG.info("Method updateSurveyAsClicked() to mark the survey as clicked, started");
		surveyDetailsDao.updateSurveyAsClicked(agentId, customerEmail);
		LOG.info("Method updateSurveyAsClicked() to mark the survey as clicked, finished");
	}
}
// JIRA SS-119 by RM-05:EOC
