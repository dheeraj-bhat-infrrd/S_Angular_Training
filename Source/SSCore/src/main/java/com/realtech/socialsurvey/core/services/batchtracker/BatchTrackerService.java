package com.realtech.socialsurvey.core.services.batchtracker;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;


public interface BatchTrackerService
{
    public long getLastRunEndTimeAndUpdateLastStartTimeByBatchType( String batchType );


    public Map<Long, Integer> getReviewCountForAgents( List<Long> agentIdList ) throws ParseException;


    public void updateLastRunEndTimeByBatchType( String batchType ) throws NoRecordsFetchedException, InvalidInputException;


    public List<Long> getUserIdListToBeUpdated( long modifiedOn );


    public void updateErrorForBatchTrackerByBatchType( String batchType, String error ) throws NoRecordsFetchedException,
        InvalidInputException;


    public void sendMailToAdminREgardingBatchError( String  batchName, long lastRunTime ,  Exception e ) throws InvalidInputException, UndeliveredEmailException;


    public long getLastRunEndTimeByBatchType( String batchType ) throws NoRecordsFetchedException;


}
