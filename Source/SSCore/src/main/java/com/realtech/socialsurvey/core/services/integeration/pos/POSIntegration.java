package com.realtech.socialsurvey.core.services.integeration.pos;

import java.sql.Timestamp;
import java.util.List;
import com.realtech.socialsurvey.core.entities.integration.EngagementProcessingStatus;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

/**
 * Methods needed for POS integration
 *
 */
public interface POSIntegration {

	/**
	 * Gets the last run time for the api
	 * @param source
	 * @return
	 * @throws InvalidInputException
	 */
	public Timestamp getLastRunTime(String source) throws InvalidInputException;
	
	/**
	 * Returns a list of processed records
	 * @param source
	 * @param lastRunTime
	 * @return
	 * @throws InvalidInputException
	 */
	public List<EngagementProcessingStatus> getProcessedRecords(String source, Timestamp lastRunTime) throws InvalidInputException;
}
