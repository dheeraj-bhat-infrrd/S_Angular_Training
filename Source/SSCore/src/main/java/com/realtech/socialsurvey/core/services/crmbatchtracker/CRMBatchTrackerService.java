package com.realtech.socialsurvey.core.services.crmbatchtracker;

import com.realtech.socialsurvey.core.entities.CrmBatchTracker;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;

/**
 * 
 * @author rohit
 *
 */
public interface CRMBatchTrackerService
{
    public long getLastRunEndTimeAndUpdateLastStartTimeByEntityTypeAndSourceType( String entutyType  , long entityId , String source) throws InvalidInputException;

    void updateErrorForBatchTrackerByEntityTypeAndSourceType( String entityType, long entityId, String source, String error )
        throws NoRecordsFetchedException, InvalidInputException;

    void updateLastRunEndTimeByEntityTypeAndSourceType( String entityType, long entityId, String source,int lastRunRecordFetchedCount )
        throws NoRecordsFetchedException, InvalidInputException;
    
    public CrmBatchTracker getCrmBatchTracker(String entityType, long entityId, String source)throws InvalidInputException;

}
