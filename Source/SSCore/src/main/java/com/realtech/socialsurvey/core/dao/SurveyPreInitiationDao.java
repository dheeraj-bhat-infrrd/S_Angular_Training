package com.realtech.socialsurvey.core.dao;

import java.sql.Timestamp;
import java.util.List;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.integration.EngagementProcessingStatus;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

public interface SurveyPreInitiationDao extends GenericDao<SurveyPreInitiation, Long> {

	/**
	 * Gets the last run time for the source
	 * @param source
	 * @return
	 * @throws InvalidInputException
	 */
	public Timestamp getLastRunTime(String source) throws InvalidInputException;
	
	/**
	 * Gets a list of processed ids
	 * @param source
	 * @param timestamp
	 * @return
	 * @throws InvalidInputException
	 */
	public List<EngagementProcessingStatus> getProcessedIds(String source, Timestamp timestamp) throws InvalidInputException;

}
