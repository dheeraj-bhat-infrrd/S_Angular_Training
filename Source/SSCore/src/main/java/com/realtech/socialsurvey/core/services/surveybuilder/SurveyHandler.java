package com.realtech.socialsurvey.core.services.surveybuilder;

import org.apache.solr.client.solrj.SolrServerException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
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
	public void storeInitialSurveyDetails(long agentId, long companyId, long regionId, long branchId, String customerEmail, int reminderCount)
			throws InvalidInputException, SolrException, NoRecordsFetchedException, SolrServerException;

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
}
