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
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.Survey;
import com.realtech.socialsurvey.core.entities.SurveyAnswerOptions;
import com.realtech.socialsurvey.core.entities.SurveyCompanyMapping;
import com.realtech.socialsurvey.core.entities.SurveyQuestion;
import com.realtech.socialsurvey.core.entities.SurveyQuestionDetails;
import com.realtech.socialsurvey.core.entities.SurveyQuestionsAnswerOption;
import com.realtech.socialsurvey.core.entities.SurveyQuestionsMapping;
import com.realtech.socialsurvey.core.entities.SurveyTemplate;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.VerticalsMaster;
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

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private GenericDao<VerticalsMaster, Integer> verticalsMasterDao;

	@Autowired
	private OrganizationUnitSettingsDao organizationUnitSettingsDao;


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

	@Override
	@Transactional
	public boolean isSurveyBuildingAllowed(User user) throws InvalidInputException {
		LOG.info("Method isSurveyBuildingAllowed() started for user: " + user);
		if (user == null) {
			throw new InvalidInputException("User is null in isSurveyBuildingAllowed");
		}
		boolean isSurveyBuildingAllowed = false;

		if (user.isCompanyAdmin()) {
			LOG.debug("Checking Survey Building for user role Company Admin");
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
		if (!surveyCompanyMappingList.isEmpty()) {
			SurveyCompanyMapping surveyCompanyMapping = surveyCompanyMappingList.get(CommonConstants.INITIAL_INDEX);
			Survey survey = surveyCompanyMapping.getSurvey();
			return survey;
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
		OrganizationUnitSettings companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(user.getCompany().getCompanyId(), CommonConstants.COMPANY_SETTINGS_COLLECTION);
		String surveyName = " Survey";
		//Fetch the vertical masters from the table for the vertical
		VerticalsMaster verticalsMaster = verticalsMasterDao.findByColumn(VerticalsMaster.class, CommonConstants.VERTICALS_MASTER_NAME_COLUMN, companySettings.getVertical()).get(CommonConstants.INITIAL_INDEX);
		

		// creating new survey and mapping to company
		Survey survey = new Survey();
		survey.setSurveyName(user.getCompany().getCompany() + surveyName);
		survey.setVerticalsMaster(verticalsMaster);
		survey.setStatus(CommonConstants.STATUS_ACTIVE);
		survey.setCreatedBy(String.valueOf(user.getUserId()));
		survey.setModifiedBy(String.valueOf(user.getUserId()));
		survey.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		survey.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		survey = surveyDao.save(survey);
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

	@Override
	@Transactional
	public void deactivateSurveyCompanyMapping(User user) throws InvalidInputException, NoRecordsFetchedException {
		LOG.info("Method deactivateSurveyCompanyMapping() started.");
		if (user == null) {
			LOG.error("Invalid argument passed. User is null in method deactivateSurveyCompanyMapping.");
			throw new InvalidInputException("Invalid argument passed. User is null in method deactivateSurveyCompanyMapping.");
		}
		Survey survey = checkForExistingSurvey(user);

		HashMap<String, Object> queries = new HashMap<>();
		queries.put(CommonConstants.COMPANY_COLUMN, user.getCompany());
		queries.put(CommonConstants.SURVEY_COLUMN, survey);
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);

		List<SurveyCompanyMapping> surveyCompanyMappings = surveyCompanyMappingDao.findByKeyValue(SurveyCompanyMapping.class, queries);
		if (surveyCompanyMappings == null || surveyCompanyMappings.isEmpty()) {
			LOG.error("No Survey Company Mapping records have been found for company id : " + user.getCompany().getCompanyId());
			throw new NoRecordsFetchedException("No SurveyCompany Mappings have been found for company id : " + user.getCompany().getCompanyId());
		}

		SurveyCompanyMapping surveyCompanyMapping = surveyCompanyMappings.get(CommonConstants.INITIAL_INDEX);
		surveyCompanyMapping.setStatus(CommonConstants.STATUS_INACTIVE);
		LOG.debug("Disabling the SurveyCompanyMapping record with id : " + surveyCompanyMapping.getSurveyCompanyMappingId() + "from the database.");

		surveyCompanyMappingDao.update(surveyCompanyMapping);
		LOG.info("Method deactivateSurveyCompanyMapping() finished.");
	}

	@Override
	@Transactional
	public long countActiveQuestionsInSurvey(Survey survey) throws InvalidInputException {
		LOG.info("Method countQuestionsInSurvey() started.");
		if (survey == null) {
			LOG.error("Invalid argument. Null value is passed for survey.");
			throw new InvalidInputException("Invalid argument. Null value is passed for survey.");
		}

		HashMap<String, Object> queries = new HashMap<>();
		queries.put(CommonConstants.SURVEY_COLUMN, survey);
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);

		LOG.info("Method countQuestionsInSurvey() finished.");
		return surveyQuestionsMappingDao.findNumberOfRowsByKeyValue(SurveyQuestionsMapping.class, queries);
	}

	@Override
	@Transactional
	public long countActiveRatingQuestionsInSurvey(User user) throws InvalidInputException {
		LOG.info("Method countActiveRatingQuestionsInSurvey() started.");
		if (user == null) {
			LOG.error("Invalid argument. Null value is passed for user.");
			throw new InvalidInputException("Invalid argument. Null value is passed for user.");
		}
		Survey survey = checkForExistingSurvey(user);
		if (survey == null) {
			survey = createNewSurvey(user);
		}

		HashMap<String, Object> queries = new HashMap<>();
		queries.put(CommonConstants.SURVEY_COLUMN, survey);
		queries.put(CommonConstants.SURVEY_IS_RATING_QUESTION_COLUMN, CommonConstants.QUESTION_RATING_VALUE_TRUE);
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);

		LOG.info("Method countActiveRatingQuestionsInSurvey() finished.");
		return surveyQuestionsMappingDao.findNumberOfRowsByKeyValue(SurveyQuestionsMapping.class, queries);
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
		if (surveyQuestionDetails.getQuestionType().indexOf(CommonConstants.QUESTION_MULTIPLE_CHOICE) != -1
				&& surveyQuestionDetails.getAnswers().size() < 2) {
			LOG.error("Invalid argument passed. Atleast two answers should be given.");
			throw new InvalidInputException("Invalid argument passed. Atleast two answers should be given in method addNewQuestionsAndAnswers.");
		}

		SurveyQuestion surveyQuestion = addNewQuestionsAndAnswers(user, survey, surveyQuestionDetails.getQuestion(),
				surveyQuestionDetails.getQuestionType(), surveyQuestionDetails.getAnswers());
		mapQuestionToSurvey(user, surveyQuestionDetails, surveyQuestion, survey, CommonConstants.STATUS_ACTIVE);
		LOG.info("Method addQuestionToExistingSurvey() finished.");
	}

	// JIRA SS-119 by RM-05
	/*
	 * Method to fetch survey linked to the agent.
	 */
	@Override
	@Transactional
	public List<SurveyQuestionDetails> getSurveyByAgenId(long agentId) throws InvalidInputException {
		LOG.info("Method to get survey for agent id {} called.", agentId);
		User user = userDao.findById(User.class, agentId);
		LOG.info("Method to get survey for agent id {} finished.", agentId);
		List<SurveyQuestionDetails> surveyQuestions = getAllActiveQuestionsOfMappedSurvey(user);
		// TODO Add the default question which will be shown at the end of survey.
		SurveyQuestionDetails surveyQuestionDetails = new SurveyQuestionDetails();
		surveyQuestionDetails.setIsRatingQuestion(CommonConstants.STATUS_ACTIVE);
		surveyQuestionDetails.setQuestion("How was your overall experience with our agent?");
		surveyQuestionDetails.setIsRatingQuestion(CommonConstants.YES);
		surveyQuestionDetails.setQuestionType("sb-master");
		surveyQuestions.add(surveyQuestionDetails);
		return surveyQuestions;
	}

	/**
	 * Method to store questions as well as all the answers for each question.
	 */
	private SurveyQuestion addNewQuestionsAndAnswers(User user, Survey survey, String question, String questionType, List<SurveyAnswerOptions> answers) {
		LOG.debug("Method addNewQuestionsAndAnswers() started.");
		SurveyQuestion surveyQuestion = null;

		if (question != null && !questionType.equals("")) {
			surveyQuestion = addNewQuestion(user, question, questionType);

			// Save answers only if question type is Multiple Choice &
			if (questionType.indexOf(CommonConstants.QUESTION_MULTIPLE_CHOICE) != -1) {
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
		surveyQuestionDao.flush();
		LOG.debug("Method addNewQuestion finished()");
		return surveyQuestion;
	}

	/**
	 * Method to add answers to a question.
	 */
	private void addAnswersToQuestion(User user, SurveyQuestion surveyQuestion, List<SurveyAnswerOptions> answers) {
		LOG.debug("Method addAnswersToQuestion() started to add answers to survey questions");
		SurveyQuestionsAnswerOption surveyQuestionsAnswerOption = null;
		if (answers != null) {
			for (SurveyAnswerOptions answer : answers) {
				surveyQuestionsAnswerOption = new SurveyQuestionsAnswerOption();
				surveyQuestionsAnswerOption.setSurveyQuestion(surveyQuestion);
				surveyQuestionsAnswerOption.setStatus(CommonConstants.STATUS_ACTIVE);
				surveyQuestionsAnswerOption.setCreatedBy(String.valueOf(user.getUserId()));
				surveyQuestionsAnswerOption.setModifiedBy(String.valueOf(user.getUserId()));
				surveyQuestionsAnswerOption.setCreatedOn(new Timestamp(System.currentTimeMillis()));
				surveyQuestionsAnswerOption.setModifiedOn(new Timestamp(System.currentTimeMillis()));
				if (answer != null && !answer.getAnswerText().equals("")) {
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
		surveyQuestionsMappingDao.save(surveyQuestionsMapping);
		surveyQuestionsMappingDao.flush();

		LOG.debug("Method mapQuestionToSurvey() finished.");
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
		if (survey == null) {
			survey = createNewSurvey(user);
		}
		return fetchSurveyQuestions(survey);
	}
	
	@Override
	@Transactional
	public void changeSurveyStatus(User user, int status) throws InvalidInputException {
		LOG.debug("Method changeSurveyStatus() started.");
		if (user == null) {
			LOG.error("Invalid argument passed. user cannot be null.");
			throw new InvalidInputException("Invalid argument passed. User is null in method changeSurveyStatus.");
		}
		Survey survey = checkForExistingSurvey(user);
		if (survey == null) {
			survey = createNewSurvey(user);
		}

		survey.setStatus(status);
		surveyDao.saveOrUpdate(survey);
		surveyDao.flush();
		LOG.debug("Method changeSurveyStatus() finished.");
	}
	
	@Override
	@Transactional
	public List<SurveyTemplate> getSurveyTemplates(User user) throws InvalidInputException {

		//We fetch the vertical for the particular company from company settings
		LOG.debug("Feting the company settings");
		OrganizationUnitSettings companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(user.getCompany().getCompanyId(), CommonConstants.COMPANY_SETTINGS_COLLECTION);
		//Fetch the vertical masters from the table for the vertical
		LOG.debug("Fetching the verticals master record");
		VerticalsMaster verticalsMaster = verticalsMasterDao.findByColumn(VerticalsMaster.class, CommonConstants.VERTICALS_MASTER_NAME_COLUMN, companySettings.getVertical()).get(CommonConstants.INITIAL_INDEX);
				
		HashMap<String, Object> queries = new HashMap<>();
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_SURVEY_TEMPLATE);
		queries.put(CommonConstants.VERTICAL_COLUMN, verticalsMaster);

		List<Survey> surveys = surveyDao.findByKeyValue(Survey.class, queries);
		List<SurveyTemplate> templates = new ArrayList<SurveyTemplate>();
		SurveyTemplate template = null;

		for (Survey survey : surveys) {
			template = new SurveyTemplate();
			template.setSurveyId(survey.getSurveyId());
			template.setSurveyName(survey.getSurveyName());
			template.setQuestions(fetchSurveyQuestions(survey));

			templates.add(template);
		}
	
		return templates;
	}

	/**
	 * Method to fetch Survey Questions.
	 */
	private List<SurveyQuestionDetails> fetchSurveyQuestions(Survey survey) throws InvalidInputException {
		Map<String, Object> queries = new HashMap<String, Object>();
		queries.put(CommonConstants.SURVEY_COLUMN, survey);
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);

		List<SurveyQuestionsMapping> surveyQuestionsMappings = surveyQuestionsMappingDao.findByKeyValueAscending(SurveyQuestionsMapping.class,
				queries, CommonConstants.SURVEY_QUESTION_ORDER_COLUMN);
		if (surveyQuestionsMappings == null || surveyQuestionsMappings.isEmpty()) {
			LOG.error("No question mapped for the survey mapped to provided user.");
			throw new InvalidInputException("No question mapped for the survey mapped to provided user.");
		}

		List<SurveyQuestionDetails> surveyQuestionDetailsList = new ArrayList<>();
		SurveyQuestionDetails surveyQuestionDetails = null;
		List<SurveyAnswerOptions> answerOptionsToQuestion = null;
		SurveyAnswerOptions surveyAnswerOptions = null;

		// For each question
		for (SurveyQuestionsMapping surveyQuestionsMapping : surveyQuestionsMappings) {
			surveyQuestionDetails = new SurveyQuestionDetails();

			surveyQuestionDetails.setQuestionId(surveyQuestionsMapping.getSurveyQuestionsMappingId());
			surveyQuestionDetails.setQuestion(surveyQuestionsMapping.getSurveyQuestion().getSurveyQuestion());
			surveyQuestionDetails.setQuestionType(surveyQuestionsMapping.getSurveyQuestion().getSurveyQuestionsCode());
			surveyQuestionDetails.setQuestionOrder(surveyQuestionsMapping.getQuestionOrder());
			surveyQuestionDetails.setIsRatingQuestion(surveyQuestionsMapping.getIsRatingQuestion());

			// For each answer
			answerOptionsToQuestion = new ArrayList<SurveyAnswerOptions>();
			for (SurveyQuestionsAnswerOption surveyQuestionsAnswerOption : surveyQuestionsMapping.getSurveyQuestion()
					.getSurveyQuestionsAnswerOptions()) {
				surveyAnswerOptions = new SurveyAnswerOptions();

				surveyAnswerOptions.setAnswerId(surveyQuestionsAnswerOption.getSurveyQuestionsAnswerOptionsId());
				surveyAnswerOptions.setAnswerText(surveyQuestionsAnswerOption.getAnswer());
				surveyAnswerOptions.setAnswerOrder(surveyQuestionsAnswerOption.getAnswerOrder());

				answerOptionsToQuestion.add(surveyAnswerOptions);
			}
			surveyQuestionDetails.setAnswers(answerOptionsToQuestion);
			surveyQuestionDetailsList.add(surveyQuestionDetails);
		}
		return surveyQuestionDetailsList;
	}

	@Override
	@Transactional
	public void updateQuestionAndAnswers(User user, long questionId, SurveyQuestionDetails surveyQuestionDetails) throws InvalidInputException {
		LOG.info("Method updateQuestionAndAnswers() started.");
		if (user == null) {
			LOG.error("Invalid argument. Null value is passed for user.");
			throw new InvalidInputException("Invalid argument for user is passed.");
		}
		if (surveyQuestionDetails == null) {
			LOG.error("Invalid argument. Null value is passed for surveyQuestion.");
			throw new InvalidInputException("Invalid argument. Null value is passed for surveyQuestion.");
		}
		if (surveyQuestionDetails.getQuestionType().indexOf(CommonConstants.QUESTION_MULTIPLE_CHOICE) != -1
				&& surveyQuestionDetails.getAnswers().size() < 2) {
			LOG.error("Invalid argument passed. Atleast two answers should be given.");
			throw new InvalidInputException("Invalid argument passed. Atleast two answers should be given in method addNewQuestionsAndAnswers.");
		}

		modifyQuestionAndAnswers(user, questionId, surveyQuestionDetails.getQuestion(), surveyQuestionDetails.getQuestionType(),
				surveyQuestionDetails.getAnswers());
		LOG.info("Method updateQuestionAndAnswers() finished");
	}

	/**
	 * Method to modify question as well as all the answers for each question.
	 */
	private SurveyQuestion modifyQuestionAndAnswers(User user, long questionId, String question, String questionType,
			List<SurveyAnswerOptions> answers) {
		LOG.debug("Method modifyQuestionAndAnswers() started.");
		SurveyQuestion surveyQuestion = null;

		if (question != null && !questionType.equals("")) {
			surveyQuestion = surveyQuestionsMappingDao.findById(SurveyQuestionsMapping.class, questionId).getSurveyQuestion();

			surveyQuestion.setSurveyQuestion(question);
			surveyQuestion.setSurveyQuestionsCode(questionType);
			surveyQuestion.setModifiedBy(String.valueOf(user.getUserId()));
			surveyQuestion.setModifiedOn(new Timestamp(System.currentTimeMillis()));

			// Save answers only if question type is Multiple Choice &
			if (questionType.indexOf(CommonConstants.QUESTION_MULTIPLE_CHOICE) != -1) {
				if (answers != null && !answers.isEmpty() && answers.size() >= 2) {
					modifyAnswersToQuestion(user, surveyQuestion, answers);
				}
			}
			surveyQuestionDao.saveOrUpdate(surveyQuestion);
			surveyQuestionDao.flush();
		}
		LOG.debug("Method modifyQuestionAndAnswers() finished");
		return surveyQuestion;
	}

	/**
	 * Method to modify answers to a question.
	 */
	private void modifyAnswersToQuestion(User user, SurveyQuestion surveyQuestion, List<SurveyAnswerOptions> answers) {
		LOG.debug("Method modifyAnswersToQuestion() started to add answers to survey questions");

		List<SurveyQuestionsAnswerOption> surveyQuestionsAnswerOptionList = surveyQuestion.getSurveyQuestionsAnswerOptions();

		if (answers != null && surveyQuestionsAnswerOptionList != null) {
			for (SurveyQuestionsAnswerOption surveyQuestionsAnswerOption : surveyQuestionsAnswerOptionList) {
				for (SurveyAnswerOptions answer : answers) {

					if (surveyQuestionsAnswerOption.getSurveyQuestionsAnswerOptionsId() != answer.getAnswerId()) {
						continue;
					}
					surveyQuestionsAnswerOption.setModifiedBy(String.valueOf(user.getUserId()));
					surveyQuestionsAnswerOption.setModifiedOn(new Timestamp(System.currentTimeMillis()));

					if (answer != null && !answer.getAnswerText().equals("")) {
						surveyQuestionsAnswerOption.setAnswer(answer.getAnswerText());
						LOG.info("Updating Answer with text: " + answer.getAnswerText());
						surveyQuestionsAnswerOptionDao.saveOrUpdate(surveyQuestionsAnswerOption);
						surveyQuestionsAnswerOptionDao.flush();
					}
				}
			}
		}
		LOG.debug("Method modifyAnswersToQuestion() finished.");
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

		reorderSurveyQuestions(user, surveyQuestionsMapping);

		LOG.info("Method deactivateQuestionSurveyMapping() finished.");
	}

	private void reorderSurveyQuestions(User user, SurveyQuestionsMapping surveyQuestionsMapping) {
		LOG.debug("Method reorderSurveyQuestions() started.");

		Map<String, Object> queries = new HashMap<String, Object>();
		queries.put(CommonConstants.SURVEY_COLUMN, surveyQuestionsMapping.getSurvey());
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
		List<SurveyQuestionsMapping> surveyQuestionsMappings = surveyQuestionsMappingDao.findByKeyValueAscending(SurveyQuestionsMapping.class,
				queries, CommonConstants.SURVEY_QUESTION_ORDER_COLUMN);

		int count = 1;
		for (SurveyQuestionsMapping mapping : surveyQuestionsMappings) {
			mapping.setQuestionOrder(count);
			surveyQuestionsMappingDao.save(mapping);
			surveyQuestionsMappingDao.flush();

			count++;
		}
		LOG.debug("Method reorderSurveyQuestions() finished.");
	}

	@Override
	@Transactional
	public void reorderQuestion(User user, long questionId, String reorderType) throws InvalidInputException {
		LOG.info("Method reorderQuestion() started.");
		if (user == null || reorderType == null || reorderType.equals("")) {
			LOG.error("Invalid argument passed. Either user or reordertype is null in method deactivateQuestionSurveyMapping.");
			throw new InvalidInputException("Invalid argument passed. Either user or reordertype is null in method deactivateQuestionSurveyMapping.");
		}

		String REORDER_UP = "up";
		String REORDER_DOWN = "down";
		SurveyQuestionsMapping surveyQuestionsMapping = surveyQuestionsMappingDao.findById(SurveyQuestionsMapping.class, questionId);
		int order = surveyQuestionsMapping.getQuestionOrder();

		Map<String, Object> queries = new HashMap<String, Object>();
		queries.put(CommonConstants.SURVEY_COLUMN, surveyQuestionsMapping.getSurvey());
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
		List<SurveyQuestionsMapping> surveyQuestionsMappings = surveyQuestionsMappingDao.findByKeyValueAscending(SurveyQuestionsMapping.class,
				queries, CommonConstants.SURVEY_QUESTION_ORDER_COLUMN);

		for (SurveyQuestionsMapping surveyQuestionsMappingNext : surveyQuestionsMappings) {
			// Reorder UP
			if (reorderType.equals(REORDER_UP) && surveyQuestionsMappingNext.getQuestionOrder() == (order - 1)) {
				surveyQuestionsMappingNext.setQuestionOrder(order);
				surveyQuestionsMappingDao.save(surveyQuestionsMappingNext);
				surveyQuestionsMappingDao.flush();

				surveyQuestionsMapping.setQuestionOrder(order - 1);
				surveyQuestionsMappingDao.save(surveyQuestionsMapping);
				surveyQuestionsMappingDao.flush();

				break;
			}
			// Reorder Down
			else if (reorderType.equals(REORDER_DOWN) && surveyQuestionsMappingNext.getQuestionOrder() == (order + 1)) {
				surveyQuestionsMappingNext.setQuestionOrder(order);
				surveyQuestionsMappingDao.save(surveyQuestionsMappingNext);
				surveyQuestionsMappingDao.flush();

				surveyQuestionsMapping.setQuestionOrder(order + 1);
				surveyQuestionsMappingDao.save(surveyQuestionsMapping);
				surveyQuestionsMappingDao.flush();

				break;
			}
		}
		LOG.info("Method reorderQuestion() finished.");
	}

	@Override
	@Transactional
	public Survey cloneSurveyFromTemplate(User user, long templateId, boolean needMappingOfQuestions) throws InvalidInputException, NoRecordsFetchedException {
		LOG.info("Method cloneSurveyFromTemplate() started.");
		if (user == null) {
			LOG.error("Invalid argument passed. User is null in method deactivateSurveyCompanyMapping.");
			throw new InvalidInputException("Invalid argument passed. User is null in method deactivateSurveyCompanyMapping.");
		}

		Survey survey = checkForExistingSurvey(user);
		if (survey != null) {
			deactivateSurveyCompanyMapping(user);
		}
		Survey newSurvey = createNewSurvey(user);

		// fetching template
		Survey surveyTemplate = surveyDao.findById(Survey.class, templateId);
		List<SurveyQuestionDetails> surveyQuestionDetails = fetchSurveyQuestions(surveyTemplate);

		for (SurveyQuestionDetails questionDetails : surveyQuestionDetails) {
			addQuestionToExistingSurvey(user, newSurvey, questionDetails);
		}
		
		LOG.info("Method cloneSurveyFromTemplate() finished.");
		return newSurvey;
	}
	
	/**
	 * Adds a default survey to the company based on the vertical of the company.
	 * @param user
	 * @throws InvalidInputException
	 */
	@Override
	@Transactional
	public void addDefaultSurveyToCompany(User user) throws InvalidInputException {
		if (user == null) {
			LOG.error("addDefaultSurveyToCompany : Invalid company parameter passed.");
			throw new InvalidInputException("addDefaultSurveyToCompany : Invalid company parameter passed.");
		}
		LOG.info("Adding default survey to company for user id : " + user.getUserId());
		
		//We fetch the vertical for the particular company from company settings
		LOG.debug("Feting the company settings");
		OrganizationUnitSettings companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(user.getCompany().getCompanyId(), CommonConstants.COMPANY_SETTINGS_COLLECTION);
		//Fetch the vertical masters from the table for the vertical
		LOG.debug("Fetching the verticals master record");
		VerticalsMaster verticalsMaster = verticalsMasterDao.findByColumn(VerticalsMaster.class, CommonConstants.VERTICALS_MASTER_NAME_COLUMN, companySettings.getVertical()).get(CommonConstants.INITIAL_INDEX);
		
		//Next we get the default survey for a particular vertical
		LOG.debug("Fetching the default survey");
		Map<String, Object> queries = new HashMap<>();
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_SURVEY_TEMPLATE);
		queries.put(CommonConstants.VERTICAL_COLUMN, verticalsMaster);
		Survey defaultSurvey = surveyDao.findByKeyValue(Survey.class, queries).get(CommonConstants.INITIAL_INDEX);
		
		//Now we add the survey to the company
		LOG.debug("Adding aurvey to company");
		addSurveyToCompany(user, defaultSurvey, user.getCompany());
		
		LOG.info("Default survey added to the company");		
	}
	
	/**
	 * Checks if the survey is default and clones it. If not leaves it as it is.
	 * @param user
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 */
	@Transactional
	@Override
	public Map<Integer, Integer> checkIfSurveyIsDefaultAndClone(User user) throws InvalidInputException, NoRecordsFetchedException {
		
		if(user == null ){
			LOG.error("checkIfSurveyIsDefaultAndClone : user parameter is null or invalid");
			throw new InvalidInputException("checkIfSurveyIsDefaultAndClone : user parameter is null or invalid");
		}
		LOG.info(" checkIfSurveyIsDefaultAndClone called");
		Map<Integer, Integer> oldToNewQuestionMap = null;
		//We fetch the current survey of the company
		LOG.debug("Fetching the current survey mapping for the user with id : " + user.getUserId());
		Map<String, Object> queries = new HashMap<>();
		queries.put(CommonConstants.COMPANY_COLUMN, user.getCompany());
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
		SurveyCompanyMapping currentSurveyMapping = surveyCompanyMappingDao.findByKeyValue(SurveyCompanyMapping.class, queries).get(CommonConstants.INITIAL_INDEX);
		Map<Integer, Integer> oldToNewMapping = null;
		//Now we check if it is a default survey
		if( currentSurveyMapping.getSurvey().getStatus() == CommonConstants.STATUS_SURVEY_TEMPLATE ){
			
			//So it is a default survey
			LOG.debug("A default survey is currently mapped to the company with id : " + user.getCompany().getCompanyId());
			
			//We clone the survey for the user
			LOG.debug("Cloning the survey to template with id : " + currentSurveyMapping.getSurvey().getSurveyId());
			Survey newSurvey = cloneSurveyFromTemplate(user, currentSurveyMapping.getSurvey().getSurveyId(),true);
			
			oldToNewMapping = new HashMap<>();
			LOG.debug("Building map of old question ids to new question ids ");
			for( int counter = 0; counter < currentSurveyMapping.getSurvey().getSurveyQuestionsMappings().size();counter++){
				oldToNewMapping.put(currentSurveyMapping.getSurvey().getSurveyQuestionsMappings().get(counter).getSurveyQuestion().getSurveyQuestionsId(), newSurvey.getSurveyQuestionsMappings().get(counter).getSurveyQuestion().getSurveyQuestionsId());
			}			
				LOG.info("returning mapping of old to new questions");
						
			LOG.info("Survey cloned. Now all changes will be added to the users survey");
		}
		else{
			LOG.info("Default survey not found, so no cloning required");
		}
		LOG.debug(" Method checkIfSurveyIsDefaultAndClone completed!");	
		return oldToNewQuestionMap;
	}
}

// JIRA: SS-32: By RM05: EOC