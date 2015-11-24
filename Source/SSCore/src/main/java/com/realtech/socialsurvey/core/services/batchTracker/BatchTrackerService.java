package com.realtech.socialsurvey.core.services.batchTracker;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;


public interface BatchTrackerService
{
    public long getLastRunTimeByBatchType( String batchType ) throws NoRecordsFetchedException;


    public Map<Long, Integer> getReviewCountForAgents( List<Long> agentIdList ) throws ParseException;


    public void updateModifiedOnColumnByBatchType( String batchType ) throws NoRecordsFetchedException;


    public void updateReviewCountForAgentsInSolr( Map<Long, Integer> agentsReviewCount );


    public List<Long> getUserIdListToBeUpdated( long modifiedOn );


    void updateModifiedOnColumnByBatchTypeAndTime( String batchType, long time ) throws NoRecordsFetchedException;

}
