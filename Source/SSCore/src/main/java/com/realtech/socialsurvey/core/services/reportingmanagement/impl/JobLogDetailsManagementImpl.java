package com.realtech.socialsurvey.core.services.reportingmanagement.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.JobLogDetailsDao;
import com.realtech.socialsurvey.core.dao.impl.JobLogDetailsDaoImpl;
import com.realtech.socialsurvey.core.entities.JobLogDetails;
import com.realtech.socialsurvey.core.entities.JobLogDetailsResponse;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.reportingmanagement.JobLogDetailsManagement;


@Component
public class JobLogDetailsManagementImpl implements JobLogDetailsManagement
{

    @Autowired
    private JobLogDetailsDao jobLogDetailsDao;

    private static final Logger LOG = LoggerFactory.getLogger( JobLogDetailsDaoImpl.class );


    @Override
    public JobLogDetailsResponse getLastSuccessfulEtlTime() throws InvalidInputException
    {
        LOG.debug( "method to fetch the job-log details, getLastSuccessfulEtlTime() started." );
        JobLogDetailsResponse jobLogDetailsResponse = new JobLogDetailsResponse();
        JobLogDetails latestJobLogDetails = jobLogDetailsDao.getLatestJobLogDetails();
        JobLogDetails lastSuccessfulRun = latestJobLogDetails;
        if ( latestJobLogDetails != null ) {
            // if latest job hasn't ended, find last successful run
            if ( latestJobLogDetails.getJobEndTime() == null ) {
                lastSuccessfulRun = jobLogDetailsDao.getJobLogDetailsOfLastSuccessfulRun();
                if(lastSuccessfulRun == null){
                    JobLogDetailsResponse.JobLogDetailsTimeAndStatus lastRunTime = jobLogDetailsResponse.new JobLogDetailsTimeAndStatus();
                    lastRunTime.setStatus( CommonConstants.STATUS_DUMMY );
                    jobLogDetailsResponse.setLastRunTime( lastRunTime );
                    return jobLogDetailsResponse;
                }

                if ( latestJobLogDetails.getStatus().matches( "(?i:Error .*)" ) ) {

                    jobLogDetailsResponse.setFailure( true );
                    JobLogDetailsResponse.JobLogDetailsTimeAndStatus failureStatus = jobLogDetailsResponse.new JobLogDetailsTimeAndStatus();
                    failureStatus.setCurrentJob( latestJobLogDetails.getCurrentJobName() );
                    //Timestamp to be set before the other timezones!!
                    failureStatus.setTimestamp( latestJobLogDetails.getJobStartTime() );
                    failureStatus.setEst();
                    failureStatus.setIst();
                    failureStatus.setPst();
                    failureStatus.setStatus( latestJobLogDetails.getStatus() );
                    jobLogDetailsResponse.setFailureStatus( failureStatus );

                } else if ( latestJobLogDetails.getStatus().matches( "(?i:Running .*)" ) ) {

                    jobLogDetailsResponse.setInProgress( true );
                    JobLogDetailsResponse.JobLogDetailsTimeAndStatus progressStatus = jobLogDetailsResponse.new JobLogDetailsTimeAndStatus();
                    progressStatus.setCurrentJob( latestJobLogDetails.getCurrentJobName() );
                    //Timestamp to be set before the other timezones!!
                    progressStatus.setTimestamp( latestJobLogDetails.getJobStartTime() );
                    progressStatus.setEst();
                    progressStatus.setIst();
                    progressStatus.setPst();
                    progressStatus.setStatus( latestJobLogDetails.getStatus() );
                    jobLogDetailsResponse.setProgressStatus( progressStatus );

                }
            } else {
                jobLogDetailsResponse.setSuccessful( true );
            }
            JobLogDetailsResponse.JobLogDetailsTimeAndStatus lastRunTime = jobLogDetailsResponse.new JobLogDetailsTimeAndStatus();
            lastRunTime.setCurrentJob( lastSuccessfulRun.getCurrentJobName() );
            //Timestamp to be set before the other timezones!!
            lastRunTime.setTimestamp( lastSuccessfulRun.getJobStartTime() );
            lastRunTime.setEst();
            lastRunTime.setIst();
            lastRunTime.setPst();
            lastRunTime.setStatus( lastSuccessfulRun.getStatus() );
            jobLogDetailsResponse.setLastRunTime( lastRunTime );
            LOG.debug( "method to fetch the job-log details, getLastSuccessfulEtlTime() finished." );

            return jobLogDetailsResponse;
        }

        return null;
    }

}
