package com.realtech.socialsurvey.core.services.surveybuilder.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.Survey;
import com.realtech.socialsurvey.core.entities.SurveyAnswer;
import com.realtech.socialsurvey.core.entities.SurveyCompanyMapping;
import com.realtech.socialsurvey.core.entities.SurveyQuestion;
import com.realtech.socialsurvey.core.entities.SurveyQuestionDetails;
import com.realtech.socialsurvey.core.entities.SurveyQuestionsAnswerOption;
import com.realtech.socialsurvey.core.entities.SurveyQuestionsMapping;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyBuilder;

// JIRA: SS-32: By RM05: BOC
/**
 * This class is responsible for creating a new survey and modifying a pre-existing survey.
 */
@Component
public class SurveyBuilderImpl implements SurveyBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(SurveyBuilderImpl.class);

	@Autowired
	private GenericDao<Survey, Long> surveyDao;

	@Autowired
	private GenericDao<SurveyQuestion, Long> surveyQuestionDao;

	@Autowired
	private GenericDao<SurveyQuestionsAnswerOption, Long> surveyQuestionsAnswerOptionDao;

	@Autowired
	private GenericDao<SurveyQuestionsMapping, Long> surveyQuestionsMappingDao;

	@Autowired
	private GenericDao<Company, Long> companyDao;

	@Autowired
	private GenericDao<SurveyCompanyMapping, Long> surveyCompanyMappingDao;

	/**
	 * Method to create a new Survey into the survey table.
	 * 
	 * @throws InvalidInputException
	 */
	@Transactional
	@Override
	public void createNewSurvey(User user, List<SurveyQuestionDetails> surveyQuestions, String surveyName) throws InvalidInputException {
		LOG.info("Method createNewSurvey() started.");
		if (surveyName == null || surveyName.equals("")) {
			LOG.error("Invalid argument. Null value is passed for surveyName.");
			throw new InvalidInputException("Invalid argument. Null value is passed for surveyName.");
		}
		if (user == null) {
			LOG.error("Invalid argument. Null value is passed for user.");
			throw new InvalidInputException("Invalid argument. Null value is passed for user.");
		}
		if (surveyQuestions == null || surveyQuestions.isEmpty()) {
			LOG.error("Invalid argument. Null value is passed for surveyQuestions.");
			throw new InvalidInputException("Invalid argument. Null value is passed for surveyQuestions.");
		}

		Company company = user.getCompany();
		Survey survey = addSurvey(surveyName, company, user);
		mapSurveyToCompany(survey, company, user);
		if (surveyQuestions != null) {
			for (SurveyQuestionDetails surveyQuestionDetails : surveyQuestions) {
				SurveyQuestion surveyQuestion = addNewQuestionsAndAnswers(user, survey, surveyQuestionDetails.getQuestion(),
						surveyQuestionDetails.getQuestionType(), surveyQuestionDetails.getAnswers());
				mapQuestionToSurvey(user, surveyQuestionDetails, surveyQuestion, survey);
			}
		}
		LOG.info("Method createNewSurvey() finished.");
	}


	/**
	 * Method to create a new survey company mapping into database.
	 * 
	 * @throws InvalidInputException
	 */
	@Transactional
	@Override
	public void addSurveyToCompany(Survey survey, Company company, User user) throws InvalidInputException {
		LOG.info("Method addSurveyToCompany() started.");
		if (survey == null) {
			LOG.error("Invalid argument. Null value is passed for survey.");
			throw new InvalidInputException("Invalid argument. Null value is passed for survey.");
		}
		if (company == null) {
			LOG.error("Invalid argument. Null value is passed for company.");
			throw new InvalidInputException("Invalid argument. Null value is passed for company.");
		}
		if (user == null) {
			LOG.error("Invalid argument. Null value is passed for user.");
			throw new InvalidInputException("Invalid argument. Null value is passed for user.");
		}
		
		mapSurveyToCompany(survey, company, user);
		LOG.info("Method addSurveyToCompany() finished.");
	}

	
	/**
	 * Method to update an existing survey by the Corporate Admin.
	 * 
	 * @throws InvalidInputException
	 */
	@Override
	@Transactional
	public void addQuestionsToExistingSurvey(User user, Survey survey, List<SurveyQuestionDetails> surveyQuestions) throws InvalidInputException {
		LOG.info("Method addQuestionsToExistingSurvey() started.");
		if (user == null) {
			LOG.error("Invalid argument. Null value is passed for user.");
			throw new InvalidInputException("Invalid argument for user is passed.");
		}
		if (surveyQuestions == null || surveyQuestions.isEmpty()) {
			LOG.error("Invalid argument. Null value is passed for surveyQuestions.");
			throw new InvalidInputException("Invalid argument. Null value is passed for surveyQuestions.");
		}
		if (survey == null) {
			LOG.error("Invalid argument. Null value is passed for survey.");
			throw new InvalidInputException("Invalid argument. Null value is passed for survey.");
		}

		for (SurveyQuestionDetails surveyQuestionDetails : surveyQuestions) {
			SurveyQuestion surveyQuestion = addNewQuestionsAndAnswers(user, survey, surveyQuestionDetails.getQuestion(),
					surveyQuestionDetails.getQuestionType(), surveyQuestionDetails.getAnswers());
			mapQuestionToSurvey(user, surveyQuestionDetails, surveyQuestion, survey);
		}

		LOG.info("Method addQuestionsToExistingSurvey() finished.");
	}

	/**
	 * Method to mark survey to questions mapping as inactive in SURVEY_QUESTIONS_MAPPING.
	 * 
	 * @throws InvalidInputException
	 */
	@Override
	@Transactional
	public void deactivateExistingSurveyMappings(User user, SurveyQuestion surveyQuestion) throws InvalidInputException {
		LOG.info("Method deactivateExistingSurveyMappings() started.");
		if (user == null || surveyQuestion == null) {
			LOG.error("Invalid argument passed. Either user or surveyQuestion is null in method deactivateExistingSurveyMappings.");
			throw new InvalidInputException(
					"Invalid argument passed. Either user or surveyQuestion is null in method deactivateExistingSurveyMappings.");
		}
		List<SurveyQuestionsMapping> surveyQuestionsMappings = surveyQuestionsMappingDao.findByColumn(SurveyQuestionsMapping.class,
				CommonConstants.SURVEY_QUESTION_COLUMN, surveyQuestion);
		for (SurveyQuestionsMapping surveyQuestionsMapping : surveyQuestionsMappings) {
			surveyQuestionsMapping.setStatus(CommonConstants.STATUS_INACTIVE);
			surveyQuestionsMappingDao.save(surveyQuestionsMapping);
			surveyQuestionsMappingDao.flush();
		}
		LOG.info("Method deactivateExistingSurveyMappings() finished.");
	}

	/**
	 * Method to fetch all the questions that belong to the specified survey. Company is fetched for
	 * user passed which in turn is used to get survey ID. Assumption : Only 1 survey is linked to a
	 * company.
	 * 
	 * @throws InvalidInputException
	 */
	@Override
	@Transactional
	public List<SurveyQuestionDetails> getAllActiveQuestionsOfSurvey(User user) throws InvalidInputException {
		if (user == null) {
			LOG.error("Invalid argument passed. Either user cannot be  null.");
			throw new InvalidInputException("Invalid argument passed. User is null in method getAllActiveQuestionsOfSurvey.");
		}

		Company company = user.getCompany();
		if (company == null) {
			LOG.error("No company found for given survey.");
			throw new InvalidInputException("No company found for given survey.");
		}
		List<Survey> surveys = surveyDao.findByColumn(Survey.class, CommonConstants.SURVEY_COMPANY_COLUMN, company);
		if (surveys == null || surveys.isEmpty()) {
			LOG.error("No survey found for given user.");
			throw new InvalidInputException("No survey found for given user.");
		}

		Survey survey = surveys.get(0);
		Map<String, Object> criteriaQueries = new HashMap<>();
		criteriaQueries.put(CommonConstants.SURVEY_COLUMN, survey);
		criteriaQueries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
		List<SurveyQuestionsMapping> surveyQuestionsMappings = surveyQuestionsMappingDao
				.findByKeyValue(SurveyQuestionsMapping.class, criteriaQueries);
		if (surveyQuestionsMappings == null || surveyQuestionsMappings.isEmpty()) {
			LOG.error("No question mapped for the survey mapped to provided user.");
			throw new InvalidInputException("No question mapped for the survey mapped to provided user.");
		}

		List<SurveyQuestionDetails> surveyQuestionDetailsList = new ArrayList<>();
		SurveyQuestionDetails surveyQuestionDetails = null;
		List<SurveyAnswer> answerOptionsToQuestion = null;
		SurveyAnswer surveyAnswer = null;

		for (SurveyQuestionsMapping surveyQuestionsMapping : surveyQuestionsMappings) {
			surveyQuestionDetails = new SurveyQuestionDetails();

			surveyQuestionDetails.setQuestion(surveyQuestionsMapping.getSurveyQuestion().getSurveyQuestion());
			surveyQuestionDetails.setQuestionType(surveyQuestionsMapping.getSurveyQuestion().getSurveyQuestionsCode());
			surveyQuestionDetails.setQuestionOrder(surveyQuestionsMapping.getQuestionOrder());
			surveyQuestionDetails.setIsRatingQuestion(surveyQuestionsMapping.getIsRatingQuestion());

			answerOptionsToQuestion = new ArrayList<>();

			for (SurveyQuestionsAnswerOption surveyQuestionsAnswerOption : surveyQuestionsMapping.getSurveyQuestion()
					.getSurveyQuestionsAnswerOptions()) {
				surveyAnswer = new SurveyAnswer();
				surveyAnswer.setAnswerText(surveyQuestionsAnswerOption.getAnswer());
				surveyAnswer.setAnswerOrder(surveyQuestionsAnswerOption.getAnswerOrder());
				answerOptionsToQuestion.add(surveyAnswer);
			}

			surveyQuestionDetailsList.add(surveyQuestionDetails);
		}
		return surveyQuestionDetailsList;
	}

	/**
	 * Creates a new entry for new survey into database.
	 */
	private Survey addSurvey(String surveyName, Company company, User user) {
		LOG.debug("Method addSurvey() called to add a new survey.");
		Survey survey = new Survey();
		survey.setSurveyName(surveyName);
		survey.setStatus(CommonConstants.STATUS_ACTIVE);
		survey.setCreatedBy(String.valueOf(user.getUserId()));
		survey.setModifiedBy(String.valueOf(user.getUserId()));
		survey.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		survey.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		survey = surveyDao.save(survey);
		LOG.debug("Method addSurvey() finished.");
		return survey;
	}

	/**
	 * Creates a new survey company mapping into database.
	 */
	private SurveyCompanyMapping mapSurveyToCompany(Survey survey, Company company, User user) {
		LOG.debug("Method addSurveyCompanyMapping() called to add a new surveyCompanyMapping.");
		SurveyCompanyMapping mapping = new SurveyCompanyMapping();
		mapping.setSurvey(survey);
		mapping.setCompany(company);
		mapping.setStatus(CommonConstants.STATUS_ACTIVE);
		mapping.setCreatedBy(String.valueOf(user.getUserId()));
		mapping.setModifiedBy(String.valueOf(user.getUserId()));
		mapping.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		mapping.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		mapping = surveyCompanyMappingDao.save(mapping);
		LOG.debug("Method addSurveyCompanyMapping() finished.");
		return mapping;
	}

	/**
	 * Method to store questions as well as all the answers for each question.
	 */
	private SurveyQuestion addNewQuestionsAndAnswers(User user, Survey survey, String question, String questionType, List<SurveyAnswer> answers) {
		LOG.debug("Method addNewQuestionsAndAnswers() started.");
		SurveyQuestion surveyQuestion = null;
		if (question != null && answers != null) {
			surveyQuestion = addNewQuestion(user, question, questionType);
			addAnswersToQuestion(user, surveyQuestion, answers);
		}
		LOG.debug("Method addNewQuestionsAndAnswers() finished");
		return surveyQuestion;
	}

	/**
	 * Method to insert new question into SURVEY_QUESTION table.
	 */
	private SurveyQuestion addNewQuestion(User user, String question, String questionType) {
		LOG.debug("Method addNewQuestion started()");
		SurveyQuestion surveyQuestion = new SurveyQuestion();
		surveyQuestion.setSurveyQuestion(question);
		surveyQuestion.setSurveyQuestionsCode(questionType);
		surveyQuestion.setCreatedBy(String.valueOf(user.getUserId()));
		surveyQuestion.setModifiedBy(String.valueOf(user.getUserId()));
		surveyQuestion.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		surveyQuestion.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		surveyQuestion = surveyQuestionDao.save(surveyQuestion);
		LOG.debug("Method addNewQuestion finished()");
		return surveyQuestion;
	}

	/**
	 * Method to add answers to a question.
	 */
	private void addAnswersToQuestion(User user, SurveyQuestion surveyQuestion, List<SurveyAnswer> answers) {
		LOG.debug("Method addAnswersToQuestion() started to add answers to survey questions");
		SurveyQuestionsAnswerOption surveyQuestionsAnswerOption = null;
		if (answers != null) {
			for (SurveyAnswer answer : answers) {
				surveyQuestionsAnswerOption = new SurveyQuestionsAnswerOption();
				surveyQuestionsAnswerOption.setSurveyQuestion(surveyQuestion);
				surveyQuestionsAnswerOption.setStatus(CommonConstants.STATUS_ACTIVE);
				surveyQuestionsAnswerOption.setCreatedBy(String.valueOf(user.getUserId()));
				surveyQuestionsAnswerOption.setModifiedBy(String.valueOf(user.getUserId()));
				surveyQuestionsAnswerOption.setCreatedOn(new Timestamp(System.currentTimeMillis()));
				surveyQuestionsAnswerOption.setModifiedOn(new Timestamp(System.currentTimeMillis()));
				if (answer != null) {
					surveyQuestionsAnswerOption.setAnswer(answer.getAnswerText());
					surveyQuestionsAnswerOption.setAnswerOrder(answer.getAnswerOrder());
					surveyQuestionsAnswerOptionDao.save(surveyQuestionsAnswerOption);
					surveyQuestionsAnswerOptionDao.flush();
				}
			}
		}
		LOG.debug("Method addAnswersToQuestion() finished.");
	}

	/**
	 * Creates a new entry for new survey question mapping into database.
	 */
	private void mapQuestionToSurvey(User user, SurveyQuestionDetails surveyQuestionDetails, SurveyQuestion surveyQuestion, Survey survey) {
		LOG.debug("Method mapQuestionToSurvey() started to map questions with survey.");
		SurveyQuestionsMapping surveyQuestionsMapping = new SurveyQuestionsMapping();
		surveyQuestionsMapping.setIsRatingQuestion(surveyQuestionDetails.getIsRatingQuestion());
		surveyQuestionsMapping.setQuestionOrder(surveyQuestionDetails.getQuestionOrder());
		surveyQuestionsMapping.setSurveyQuestion(surveyQuestion);
		surveyQuestionsMapping.setStatus(CommonConstants.STATUS_ACTIVE);
		surveyQuestionsMapping.setSurvey(survey);
		surveyQuestionsMapping.setCreatedBy(String.valueOf(user.getUserId()));
		surveyQuestionsMapping.setModifiedBy(String.valueOf(user.getUserId()));
		surveyQuestionsMapping.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		surveyQuestionsMapping.setModifiedOn(new Timestamp(System.currentTimeMillis()));

		LOG.debug("Saving mapping of survey to question.");
		surveyQuestionsMappingDao.save(surveyQuestionsMapping);
		LOG.debug("Method mapQuestionToSurvey() finished.");
	}
}
// JIRA: SS-32: By RM05: EOC
