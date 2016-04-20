package com.realtech.socialsurvey.core.services.crmbatchtrackerhistory;

import com.realtech.socialsurvey.core.exception.InvalidInputException;

/**
 * 
 * @author Dipak
 *
 */
public interface CRMBatchTrackerHistoryService {

	public void insertCrmBatchTrackerHistory(int countOfRecordsFound,int crmBatchTrackerId)throws InvalidInputException;
}
