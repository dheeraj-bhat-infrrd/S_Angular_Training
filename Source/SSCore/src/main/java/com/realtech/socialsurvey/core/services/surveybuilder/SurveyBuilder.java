package com.realtech.socialsurvey.core.services.surveybuilder;

import java.util.List;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.Survey;
import com.realtech.socialsurvey.core.entities.SurveyQuestion;
import com.realtech.socialsurvey.core.entities.SurveyQuestionDetails;
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
	public boolean isSurveyBuildingAllowed(User user, String highestRole) throws InvalidInputException;
	
	/**
	 * Method to create a new Survey into the survey table.
	 * 
	 * @param user
	 * @throws InvalidInputException
	 */
	public void createNewSurvey(User user) throws InvalidInputException;

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
	public void deactivateSurveyCompanyMapping(User user, Survey survey, Company company) throws InvalidInputException, NoRecordsFetchedException;

	/**
	 * Method to update an existing survey by the Corporate Admin.
	 * 
	 * @param user
	 * @throws InvalidInputException
	 */
	public void addQuestionToExistingSurvey(User user, Survey survey, SurveyQuestionDetails surveyQuestions) throws InvalidInputException;

	/**
	 * Method to mark survey to questions mapping as inactive in SURVEY_QUESTIONS_MAPPING.
	 * 
	 * @param user
	 * @throws InvalidInputException
	 */
	public void deactivateQuestionSurveyMapping(User user, SurveyQuestion surveyQuestion) throws InvalidInputException;

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
	 * Method to fetch all the questions that belong to the specified survey. Company is fetched for
	 * user passed which in turn is used to get survey ID.
	 * 
	 * @param user
	 * @throws InvalidInputException
	 */
	public List<SurveyQuestionDetails> getAllActiveQuestionsOfSurvey(User user, Survey survey) throws InvalidInputException;

	/**
	 * Method to fetch list of all Default Surveys
	 * 
	 * @param user
	 * @throws InvalidInputException
	 */
	public List<Survey> getSurveyTemplates() throws InvalidInputException;
	
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
}