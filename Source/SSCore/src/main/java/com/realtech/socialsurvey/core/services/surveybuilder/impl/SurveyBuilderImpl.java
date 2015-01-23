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
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyBuilder;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;

// JIRA: SS-32: By RM05: BOC
/**
 * This class is responsible for survey building and modifying
 */
@Component
public class SurveyBuilderImpl implements SurveyBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(SurveyBuilderImpl.class);
	private static final String CA_ROLE = "1";
	private static final String MULTIPLE_CHOICE = "mult";

	@Autowired
	private GenericDao<Survey, Long> surveyDao;

	@Autowired
	private GenericDao<SurveyQuestion, Long> surveyQuestionDao;

	@Autowired
	private GenericDao<SurveyQuestionsAnswerOption, Long> surveyQuestionsAnswerOptionDao;

	@Autowired
	private GenericDao<SurveyQuestionsMapping, Long> surveyQuestionsMappingDao;

	@Autowired
	private GenericDao<SurveyCompanyMapping, Long> surveyCompanyMappingDao;

	@Override
	@Transactional
	public boolean isSurveyBuildingAllowed(User user, String highestRole) throws InvalidInputException {
		LOG.info("Method isSurveyBuildingAllowed() started for user: " + user);
		if (user == null) {
			throw new InvalidInputException("User is null in isSurveyBuildingAllowed");
		}
		if (highestRole == null) {
			throw new InvalidInputException("Account type is null in isSurveyBuildingAllowed");
		}
		boolean isSurveyBuildingAllowed = false;
		
		if (highestRole.equals(CA_ROLE)) {
			LOG.debug("Checking Survey Building for user role CA_ROLE");
			isSurveyBuildingAllowed = true;
		}
		LOG.info("Returning from isSurveyBuildingAllowed for user : " + user.getUserId() + " isSurveyBuildingAllowed is :" + isSurveyBuildingAllowed);
		return isSurveyBuildingAllowed;
	}
	
	@Override
	@Transactional
	public Survey checkForExistingSurvey(User user) throws InvalidInputException {
		LOG.info("Method checkForExistingSurvey() started.");
		if (user == null) {
			LOG.error("Invalid argument. Null value is passed for user.");
			throw new InvalidInputException("Invalid argument. Null value is passed for user.", DisplayMessageConstants.GENERAL_ERROR);
		}

		HashMap<String, Object> queries = new HashMap<>();
		queries.put(CommonConstants.COMPANY_COLUMN, user.getCompany());
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
		
		// Checking for existing survey
		List<SurveyCompanyMapping> surveyCompanyMappingList = surveyCompanyMappingDao.findByKeyValue(SurveyCompanyMapping.class, queries);
		
		LOG.info("Method checkForExistingSurvey() finished.");
		if(!surveyCompanyMappingList.isEmpty()) {
			SurveyCompanyMapping surveyCompanyMapping = surveyCompanyMappingList.get(CommonConstants.INITIAL_INDEX);
			return surveyCompanyMapping.getSurvey();
		}
		return null;
	}
	
	@Override
	@Transactional
	public Survey createNewSurvey(User user) throws InvalidInputException {
		LOG.info("Method createNewSurvey() started.");
		if (user == null) {
			LOG.error("Invalid argument. Null value is passed for user.");
			throw new InvalidInputException("Invalid argument. Null value is passed for user.", DisplayMessageConstants.GENERAL_ERROR);
		}

		// creating new survey and mapping to company
		Survey survey = addSurvey(user, "Survey Test");
		mapSurveyToCompany(user, survey, user.getCompany());
		LOG.info("Method createNewSurvey() finished.");
		return survey;
	}

	@Override
	@Transactional
	public void addSurveyToCompany(User user, Survey survey, Company company) throws InvalidInputException {
		LOG.info("Method addSurveyToCompany() started.");
		mapSurveyToCompany(user, survey, company);
		LOG.info("Method addSurveyToCompany() finished.");
	}

	@Override
	@Transactional
	public void deactivateSurveyCompanyMapping(User user, Survey survey, Company company) throws InvalidInputException, NoRecordsFetchedException {
		LOG.info("Method deactivateSurveyCompanyMapping() started.");
		if (user == null) {
			LOG.error("Invalid argument passed. User is null in method deactivateSurveyCompanyMapping.");
			throw new InvalidInputException("Invalid argument passed. User is null in method deactivateSurveyCompanyMapping.");
		}
		HashMap<String, Object> queries = new HashMap<>();
		queries.put(CommonConstants.COMPANY_COLUMN, company);
		queries.put(CommonConstants.SURVEY_COLUMN, survey);
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);

		List<SurveyCompanyMapping> surveyCompanyMappings = surveyCompanyMappingDao.findByKeyValue(SurveyCompanyMapping.class, queries);
		if (surveyCompanyMappings == null || surveyCompanyMappings.isEmpty()) {
			LOG.error("No Survey Company Mapping records have been found for company id : " + company.getCompanyId());
			throw new NoRecordsFetchedException("No SurveyCompany Mappings have been found for company id : " + company.getCompanyId());
		}

		SurveyCompanyMapping surveyCompanyMapping = surveyCompanyMappings.get(CommonConstants.INITIAL_INDEX);
		surveyCompanyMapping.setStatus(CommonConstants.STATUS_INACTIVE);
		surveyCompanyMappingDao.update(surveyCompanyMapping);
		LOG.debug("Disabling the SurveyCompanyMapping record with id : " + surveyCompanyMapping.getSurveyCompanyMappingId() + "from the database.");

		// Perform soft delete of the record in the database
		LOG.info("Method deactivateSurveyCompanyMapping() finished.");
	}

	@Override
	@Transactional
	public int countActiveQuestionsInSurvey(Survey survey) throws InvalidInputException {
		LOG.info("Method countQuestionsInSurvey() started.");
		if (survey == null) {
			LOG.error("Invalid argument. Null value is passed for survey.");
			throw new InvalidInputException("Invalid argument. Null value is passed for survey.");
		}

		HashMap<String, Object> queries = new HashMap<>();
		queries.put(CommonConstants.SURVEY_COLUMN, survey);
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);

		// Checking for existing survey
		List<SurveyQuestionsMapping> surveyQuestionsMappingsList = surveyQuestionsMappingDao.findByKeyValue(SurveyQuestionsMapping.class, queries);

		LOG.info("Method countQuestionsInSurvey() finished.");
		return surveyQuestionsMappingsList.size();
	}

	
	@Override
	@Transactional
	public void addQuestionToExistingSurvey(User user, Survey survey, SurveyQuestionDetails surveyQuestionDetails) throws InvalidInputException {
		LOG.info("Method addQuestionToExistingSurvey() started.");
		if (user == null) {
			LOG.error("Invalid argument. Null value is passed for user.");
			throw new InvalidInputException("Invalid argument for user is passed.");
		}
		if (surveyQuestionDetails == null) {
			LOG.error("Invalid argument. Null value is passed for surveyQuestion.");
			throw new InvalidInputException("Invalid argument. Null value is passed for surveyQuestion.");
		}
		if (survey == null) {
			LOG.error("Invalid argument. Null value is passed for survey.");
			throw new InvalidInputException("Invalid argument. Null value is passed for survey.");
		}
		if (surveyQuestionDetails.getQuestionType().indexOf(MULTIPLE_CHOICE) != -1 && surveyQuestionDetails.getAnswers().size() < 2) {
			LOG.error("Invalid argument passed. Atleast two answers should be given.");
			throw new InvalidInputException("Invalid argument passed. Atleast two answers should be given in method addNewQuestionsAndAnswers.");
		}
		
		SurveyQuestion surveyQuestion = addNewQuestionsAndAnswers(user, survey, surveyQuestionDetails.getQuestion(),
				surveyQuestionDetails.getQuestionType(), surveyQuestionDetails.getAnswers());
		mapQuestionToSurvey(user, surveyQuestionDetails, surveyQuestion, survey, CommonConstants.STATUS_ACTIVE);
		LOG.info("Method addQuestionToExistingSurvey() finished.");
	}

	@Override
	@Transactional
	public void deactivateQuestionSurveyMapping(User user, long surveyQuestionId) throws InvalidInputException {
		LOG.info("Method deactivateQuestionSurveyMapping() started.");
		if (user == null) {
			LOG.error("Invalid argument passed. Either user or surveyQuestion is null in method deactivateQuestionSurveyMapping.");
			throw new InvalidInputException(
					"Invalid argument passed. Either user or surveyQuestion is null in method deactivateQuestionSurveyMapping.");
		}
		SurveyQuestionsMapping surveyQuestionsMapping = surveyQuestionsMappingDao.findById(SurveyQuestionsMapping.class, surveyQuestionId);
		surveyQuestionsMapping.setStatus(CommonConstants.STATUS_INACTIVE);
		
		surveyQuestionsMappingDao.save(surveyQuestionsMapping);
		surveyQuestionsMappingDao.flush();
		LOG.info("Method deactivateQuestionSurveyMapping() finished.");
	}

	@Override
	@Transactional
	public List<SurveyQuestionDetails> getAllActiveQuestionsOfMappedSurvey(User user) throws InvalidInputException {
		if (user == null) {
			LOG.error("Invalid argument passed. User cannot be null.");
			throw new InvalidInputException("Invalid argument passed. User is null in method getAllActiveQuestionsOfMappedSurvey.");
		}

		Company company = user.getCompany();
		if (company == null) {
			LOG.error("No company found for given survey.");
			throw new InvalidInputException("No company found for given survey.");
		}

		Survey survey = checkForExistingSurvey(user);
		if(survey == null) {
			survey = createNewSurvey(user);
		}
		return fetchSurveyQuestions(survey);
	}

	@Override
	@Transactional
	public List<SurveyQuestionDetails> getAllActiveQuestionsOfSurvey(User user, Survey survey) throws InvalidInputException {
		if (user == null) {
			LOG.error("Invalid argument passed. User cannot be null.");
			throw new InvalidInputException("Invalid argument passed. User is null in method getAllActiveQuestionsOfSurvey.");
		}
		if (survey == null) {
			LOG.error("Invalid argument passed. Survey cannot be null.");
			throw new InvalidInputException("Invalid argument passed. Survey is null in method getAllActiveQuestionsOfSurvey.");
		}
		return fetchSurveyQuestions(survey);
	}

	@Override
	@Transactional
	public List<Survey> getSurveyTemplates() throws InvalidInputException {
		HashMap<String, Object> queries = new HashMap<>();
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_SURVEY_TEMPLATE);
		
		List<Survey> surveys = surveyDao.findByKeyValue(Survey.class, queries);
		return surveys;
	}
	
	@Override
	@Transactional
	public Survey getSurvey(long surveyId) throws InvalidInputException {
		Survey survey = surveyDao.findById(Survey.class, surveyId);
		return survey;
	}
	
	@Override
	@Transactional
	public SurveyQuestion getSurveyQuestion(long surveyQuestionId) throws InvalidInputException {
		SurveyQuestion surveyQuestion = surveyQuestionDao.findById(SurveyQuestion.class, surveyQuestionId);
		return surveyQuestion;
	}

	/**
	 * Method to change Question order
	 */
	private void changeQuestionOrder(User user, SurveyQuestion surveyQuestion) {
		LOG.debug("Method changeQuestionOrder() called");
		
		// Get corresponding mapping
		
		// get whether move up or down
		
		// fetch the other record
		
		// update both the questions
		
		LOG.debug("Method changeQuestionOrder() finished");
	}
	
	/**
	 * Method to create new survey in database.
	 */
	private Survey addSurvey(User user, String surveyName) {
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
	 * Method to store questions as well as all the answers for each question.
	 */
	private SurveyQuestion addNewQuestionsAndAnswers(User user, Survey survey, String question, String questionType, List<SurveyAnswer> answers) {
		LOG.debug("Method addNewQuestionsAndAnswers() started.");
		SurveyQuestion surveyQuestion = null;

		if (question != null && !questionType.equals("")) {
			surveyQuestion = addNewQuestion(user, question, questionType);

			// Save answers only if question type is Multiple Choice & 
			if (questionType.indexOf(MULTIPLE_CHOICE) != -1) {
				if (answers != null && !answers.isEmpty() && answers.size() >= 2) {
					addAnswersToQuestion(user, surveyQuestion, answers);
				}
			}
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
	private void mapQuestionToSurvey(User user, SurveyQuestionDetails surveyQuestionDetails, SurveyQuestion surveyQuestion, Survey survey, int status) {
		LOG.debug("Method mapQuestionToSurvey() started to map questions with survey.");
		SurveyQuestionsMapping surveyQuestionsMapping = new SurveyQuestionsMapping();
		surveyQuestionsMapping.setIsRatingQuestion(surveyQuestionDetails.getIsRatingQuestion());
		surveyQuestionsMapping.setQuestionOrder(surveyQuestionDetails.getQuestionOrder());
		surveyQuestionsMapping.setSurveyQuestion(surveyQuestion);
		surveyQuestionsMapping.setStatus(status);
		surveyQuestionsMapping.setSurvey(survey);
		surveyQuestionsMapping.setCreatedBy(String.valueOf(user.getUserId()));
		surveyQuestionsMapping.setModifiedBy(String.valueOf(user.getUserId()));
		surveyQuestionsMapping.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		surveyQuestionsMapping.setModifiedOn(new Timestamp(System.currentTimeMillis()));

		LOG.debug("Saving mapping of survey to question.");
		surveyQuestionsMappingDao.saveOrUpdate(surveyQuestionsMapping);
		LOG.debug("Method mapQuestionToSurvey() finished.");
	}
	
	/**
	 * Creates a new entry for new survey company mapping into database.
	 */
	private void mapSurveyToCompany(User user, Survey survey, Company company) throws InvalidInputException {
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

		SurveyCompanyMapping mapping = new SurveyCompanyMapping();
		mapping.setSurvey(survey);
		mapping.setCompany(company);
		mapping.setStatus(CommonConstants.STATUS_ACTIVE);
		mapping.setCreatedBy(String.valueOf(user.getUserId()));
		mapping.setModifiedBy(String.valueOf(user.getUserId()));
		mapping.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		mapping.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		mapping = surveyCompanyMappingDao.saveOrUpdate(mapping);
	}
	
	/**
	 * Method to fetch Survey Questions.
	 */
	private List<SurveyQuestionDetails> fetchSurveyQuestions(Survey survey) throws InvalidInputException {
		Map<String, Object> queries = new HashMap<String, Object>();
		queries.put(CommonConstants.SURVEY_COLUMN, survey);
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
		
		List<SurveyQuestionsMapping> surveyQuestionsMappings = surveyQuestionsMappingDao.findByKeyValue(SurveyQuestionsMapping.class, queries);
		if (surveyQuestionsMappings == null || surveyQuestionsMappings.isEmpty()) {
			LOG.error("No question mapped for the survey mapped to provided user.");
			throw new InvalidInputException("No question mapped for the survey mapped to provided user.");
		}

		List<SurveyQuestionDetails> surveyQuestionDetailsList = new ArrayList<>();
		SurveyQuestionDetails surveyQuestionDetails = null;
		List<SurveyAnswer> answerOptionsToQuestion = null;
		SurveyAnswer surveyAnswer = null;

		// For each question
		for (SurveyQuestionsMapping surveyQuestionsMapping : surveyQuestionsMappings) {
			surveyQuestionDetails = new SurveyQuestionDetails();

			surveyQuestionDetails.setQuestionId(surveyQuestionsMapping.getSurveyQuestionsMappingId());
			surveyQuestionDetails.setQuestion(surveyQuestionsMapping.getSurveyQuestion().getSurveyQuestion());
			surveyQuestionDetails.setQuestionType(surveyQuestionsMapping.getSurveyQuestion().getSurveyQuestionsCode());
			surveyQuestionDetails.setQuestionOrder(surveyQuestionsMapping.getQuestionOrder());
			surveyQuestionDetails.setIsRatingQuestion(surveyQuestionsMapping.getIsRatingQuestion());

			// For each answer
			answerOptionsToQuestion = new ArrayList<SurveyAnswer>();
			for (SurveyQuestionsAnswerOption surveyQuestionsAnswerOption : surveyQuestionsMapping.getSurveyQuestion().getSurveyQuestionsAnswerOptions()) {
				surveyAnswer = new SurveyAnswer();
				
				surveyAnswer.setAnswerId(surveyQuestionsAnswerOption.getSurveyQuestionsAnswerOptionsId());
				surveyAnswer.setAnswerText(surveyQuestionsAnswerOption.getAnswer());
				surveyAnswer.setAnswerOrder(surveyQuestionsAnswerOption.getAnswerOrder());
				
				answerOptionsToQuestion.add(surveyAnswer);
			}
			surveyQuestionDetails.setAnswers(answerOptionsToQuestion);
			
			surveyQuestionDetailsList.add(surveyQuestionDetails);
		}
		return surveyQuestionDetailsList;
	}
}
// JIRA: SS-32: By RM05: EOC