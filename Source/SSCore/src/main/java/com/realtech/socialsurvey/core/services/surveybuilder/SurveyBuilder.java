package com.realtech.socialsurvey.core.services.surveybuilder;

import java.util.List;
import java.util.Map;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.Survey;
import com.realtech.socialsurvey.core.entities.SurveyQuestion;
import com.realtech.socialsurvey.core.entities.SurveyQuestionDetails;
import com.realtech.socialsurvey.core.entities.SurveyTemplate;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;

public interface SurveyBuilder {

	/**
	 * Method to check if survey building is allowed for the logged in user
	 * 
	 * @param user
	 * @param highestRole
	 * @return
	 * @throws InvalidInputException
	 */
	public boolean isSurveyBuildingAllowed(User user) throws InvalidInputException;

	/**
	 * Method to check and return for active Survey
	 * 
	 * @param user
	 * @throws InvalidInputException
	 */
	public Survey checkForExistingSurvey(User user) throws InvalidInputException;

	/**
	 * Method to create a new Survey into the survey table.
	 * 
	 * @param user
	 * @throws InvalidInputException
	 */
	public Survey createNewSurvey(User user) throws InvalidInputException;

	/**
	 * Method to create a new survey company mapping into database.
	 * 
	 * @param user
	 * @throws InvalidInputException
	 */
	public void addSurveyToCompany(User user, Survey survey, Company company) throws InvalidInputException;

	/**
	 * Method to mark survey to company mapping as inactive in SURVEY_COMPANY_MAPPING.
	 * 
	 * @param user
	 * @throws InvalidInputException
	 */
	public void deactivateSurveyCompanyMapping(User user) throws InvalidInputException, NoRecordsFetchedException;

	/**
	 * Method to get count of survey questions
	 * 
	 * @param survey
	 * @throws InvalidInputException
	 */
	public long countActiveQuestionsInSurvey(Survey survey) throws InvalidInputException;

	/**
	 * Method to get count of survey rating questions
	 * 
	 * @param survey
	 * @throws InvalidInputException
	 */
	public long countActiveRatingQuestionsInSurvey(User user) throws InvalidInputException;

	/**
	 * Method to update an existing survey by the Corporate Admin.
	 * 
	 * @param user
	 * @throws InvalidInputException
	 */
	public long addQuestionToExistingSurvey(User user, Survey survey, SurveyQuestionDetails surveyQuestions) throws InvalidInputException;

	/**
	 * Method to mark survey to questions mapping as inactive in SURVEY_QUESTIONS_MAPPING.
	 * 
	 * @param user
	 * @throws InvalidInputException
	 */
	public void deactivateQuestionSurveyMapping(User user, long surveyQuestionId) throws InvalidInputException;

	/**
	 * Method to fetch all the questions that belong to the specified survey. Company is fetched for
	 * user passed which in turn is used to get survey ID. Assumption : Only 1 survey is linked to a
	 * company.
	 * 
	 * @param user
	 * @throws InvalidInputException
	 */
	public List<SurveyQuestionDetails> getAllActiveQuestionsOfMappedSurvey(User user) throws InvalidInputException;

	/**
	 * Method to mark survey as active/inactive in SURVEY
	 * 
	 * @param user
	 * @param status
	 * @throws InvalidInputException
	 */
	public void changeSurveyStatus(User user, int status) throws InvalidInputException;

	/**
	 * Method to fetch list of all Default Surveys
	 * 
	 * @param
	 * @throws InvalidInputException
	 */
	public List<SurveyTemplate> getSurveyTemplates(User user) throws InvalidInputException;

	/**
	 * Method to fetch Survey from SurveyId
	 * 
	 * @param surveyId
	 * @throws InvalidInputException
	 */
	public Survey getSurvey(long surveyId) throws InvalidInputException;

	/**
	 * Method to fetch SurveyQuestion from SurveyQuestionId
	 * 
	 * @param surveyQuestionId
	 * @throws InvalidInputException
	 */
	public SurveyQuestion getSurveyQuestion(long surveyQuestionId) throws InvalidInputException;
	public SurveyQuestion getSurveyQuestionFromMapping(long surveyQuestionMappingId) throws InvalidInputException;

	/**
	 * Method to update Survey Question and answers
	 * 
	 * @param questionId
	 * @throws InvalidInputException
	 */
	public void updateQuestionAndAnswers(User user, long questionId, SurveyQuestionDetails surveyQuestionDetails) throws InvalidInputException;

	/**
	 * Method to reorder Survey Questions
	 * 
	 * @param questionId
	 * @throws InvalidInputException
	 */
	public void reorderQuestion(User user, long questionId, String reorderType) throws InvalidInputException;

	/**
	 * Method to clone Survey from template
	 * 
	 * @param questionId
	 * @return 
	 * @throws InvalidInputException
	 */
	public Survey cloneSurveyFromTemplate(User user, long templateId) throws InvalidInputException, NoRecordsFetchedException;

	/**
	 * Method to fetch Survey Questions.
	 * 
	 * @param agentId
	 * @throws InvalidInputException
	 */
	public List<SurveyQuestionDetails> getSurveyByAgenId(long agentId) throws InvalidInputException;
	
	public List<SurveyQuestionDetails> getSurveyByAgent(User user) throws InvalidInputException;
	
	/**
	 * Adds a default survey to the company based on the vertical of the company.
	 * @param user
	 * @throws InvalidInputException
	 */
	public void addDefaultSurveyToCompany(User user) throws InvalidInputException;
	
	/**
	 * Checks if the survey is default and clones it. If not leaves it as it is.
	 * @param user
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 */
	public Map<Long, Long> checkIfSurveyIsDefaultAndClone(User user) throws InvalidInputException, NoRecordsFetchedException;
}