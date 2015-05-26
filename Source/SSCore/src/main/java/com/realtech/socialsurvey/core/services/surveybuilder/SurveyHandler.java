package com.realtech.socialsurvey.core.services.surveybuilder;

import java.util.List;
import java.util.Map;
import org.apache.solr.client.solrj.SolrServerException;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;

public interface SurveyHandler {

	/**
	 * Method to store question and answer format into mongo.
	 * 
	 * @param agentId
	 * @throws InvalidInputException
	 * @throws SolrServerException
	 * @throws NoRecordsFetchedException
	 * @throws SolrException
	 */
	/*
	 * public void storeInitialSurveyDetails(long agentId, long companyId, long regionId, long
	 * branchId, String customerEmail, int reminderCount) throws InvalidInputException,
	 * SolrException, NoRecordsFetchedException, SolrServerException;
	 */
	/*
	 * Method to update answers to all the questions and current stage in MongoDB.
	 * @param agentId
	 * @param customerEmail
	 * @param question
	 * @param answer
	 * @param stage
	 * @throws Exception
	 */
	public void updateCustomerAnswersInSurvey(long agentId, String customerEmail, String question, String questionType, String answer, int stage);

	/*
	 * Method to update customer review and final score on the basis of rating questions in
	 * SURVEY_DETAILS.
	 */
	public void updateGatewayQuestionResponseAndScore(long agentId, String customerEmail, String mood, String review, boolean isAbusive);

	public SurveyDetails storeInitialSurveyDetails(long agentId, String customerEmail, String firstName, String lastName, int reminderCount,
			String custRelationWithAgent, String url) throws SolrException, NoRecordsFetchedException, InvalidInputException;

	public SurveyDetails getSurveyDetails(long agentId, String customerEmail);

	public String getApplicationBaseUrl();

	public void updateSurveyAsClicked(long agentId, String customerEmail);

	public String getSwearWords();

	public void updateReminderCount(long agentId, String customerEmail);

	public Map<String, String> getEmailIdsOfAdminsInHierarchy(long agentId) throws InvalidInputException;

	public List<SurveyPreInitiation> getIncompleteSurveyCustomersEmail(long companyId);

	public void updateReminderCount(List<Long> agents, List<String> customers);

	public List<SurveyDetails> getIncompleteSocialPostSurveys(long companyId);

	public String getMoodsToSendMail();

	public void increaseSurveyCountForAgent(long agentId) throws SolrException;

	public void updateSharedOn(String socialSite, long agentId, String customerEmail);

	public String getGoogleShareUri();
	
	public String getYelpShareUri();

	public String getSurveyUrl(long agentId, String customerEmail, String baseUrl) throws InvalidInputException;

	public void changeStatusOfSurvey(long agentId, String customerEmail, boolean editable);

	public void sendSurveyInvitationMail(String custFirstName, String custLastName, String custEmail, String custRelationWithAgent, User user,
			boolean isAgent, String source) throws InvalidInputException, SolrException, NoRecordsFetchedException, UndeliveredEmailException;

	public void sendSurveyRestartMail(String custFirstName, String custLastName, String custEmail, String custRelationWithAgent, User user, String link)
			throws InvalidInputException, UndeliveredEmailException;

	public SurveyPreInitiation getPreInitiatedSurvey(long agentId, String customerEmail) throws NoRecordsFetchedException;

	public void deleteSurveyPreInitiationDetailsPermanently(SurveyPreInitiation surveyPreInitiation);

	public String composeLink(long userId, String custEmail) throws InvalidInputException;

	public void markSurveyAsStarted(SurveyPreInitiation surveyPreInitiation);

	public void updateReminderCountForSocialPosts(Long agentId, String customerEmail);
}
