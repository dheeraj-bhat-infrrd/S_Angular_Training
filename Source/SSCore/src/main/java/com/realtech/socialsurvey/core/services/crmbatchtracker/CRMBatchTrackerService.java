package com.realtech.socialsurvey.core.services.crmbatchtracker;

/**
 * 
 * @author rohit
 *
 */
public interface CRMBatchTrackerService
{
    public long getLastRunEndTimeAndUpdateLastStartTimeByEntityTypeAndSourceType( String entutyType  , long entityId , String source);

}
