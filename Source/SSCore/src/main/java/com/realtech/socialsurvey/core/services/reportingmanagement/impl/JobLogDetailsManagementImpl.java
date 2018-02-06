package com.realtech.socialsurvey.core.services.reportingmanagement.impl;

import java.sql.Timestamp;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.dao.JobLogDetailsDao;
import com.realtech.socialsurvey.core.dao.impl.JobLogDetailsDaoImpl;
import com.realtech.socialsurvey.core.entities.JobLogDetails;
import com.realtech.socialsurvey.core.entities.JobLogDetailsResponse;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.impl.testApi;
import com.realtech.socialsurvey.core.services.reportingmanagement.JobLogDetailsManagement;


@Component
public class JobLogDetailsManagementImpl implements JobLogDetailsManagement
{

    @Autowired
    private JobLogDetailsDao jobLogDetailsDao;

    @Autowired
    private Utils utils;

    private static final Logger LOG = LoggerFactory.getLogger( JobLogDetailsDaoImpl.class );


    @Override
    public JobLogDetailsResponse getLastSuccessfulEtlTime() throws InvalidInputException
    {
        LOG.debug( "method to fetch the job-log details, getLastSuccessfulEtlTime() started." );
        JobLogDetailsResponse jobLogDetailsResponse = new JobLogDetailsResponse();
        JobLogDetails lastSuccessfulRun = jobLogDetailsDao.getJobLogDetailsOfLastSuccessfulRun();
        if ( lastSuccessfulRun == null ) {
            jobLogDetailsResponse.setStatus( CommonConstants.STATUS_DUMMY );
        } else {
            jobLogDetailsResponse.setStatus( lastSuccessfulRun.getStatus() );
            jobLogDetailsResponse.setTimestampInEst( utils.convertDateToTimeZone( lastSuccessfulRun.getJobStartTime(), CommonConstants.TIMEZONE_EST ) );

        }

        LOG.debug( "method to fetch the job-log details, getLastSuccessfulEtlTime() finished." );
        return jobLogDetailsResponse;
    }


    @Override
    public boolean getIfEtlIsRunning() throws InvalidInputException
    {
        LOG.debug( "method to fetch the etl run status, getIfEtlIsRunning() started." );
        boolean isRunning = true;
        JobLogDetails jobLogDetails = jobLogDetailsDao.getJobLogDetailsOfLatestRun();
        if(!jobLogDetails.getStatus().equals(CommonConstants.STATUS_RUNNING) && !jobLogDetails.getJobName().equals(CommonConstants.CENTRALIZED_JOB_NAME)) {
        		isRunning = false;
        }

        LOG.debug( "method to fetch the etl run status, getIfEtlIsRunning() finished." );
        return isRunning;
    }

    @Override
    public JobLogDetailsResponse getLastRunForEntity( long entityId , String entityType)throws InvalidInputException
    {
        LOG.debug( "method to fetch the job-log details for entity, getLastRunForEntity() started." );
        JobLogDetailsResponse jobLogDetailsResponse = new JobLogDetailsResponse();
        JobLogDetails jobLogDetails = jobLogDetailsDao.getJobLogDetailsOfLatestRunForEntity(entityId, entityType, CommonConstants.USER_RANKING_JOB_NAME);
        if ( jobLogDetails != null ) {
            jobLogDetailsResponse.setStatus( jobLogDetails.getStatus() );
            jobLogDetailsResponse.setTimestampInEst( utils.convertDateToTimeZone( jobLogDetails.getJobStartTime(), CommonConstants.TIMEZONE_EST ) );
        }
        LOG.debug( "method to fetch the job-log details for entity, getLastRunForEntity() finished." );
        return jobLogDetailsResponse;
    }
    
    //insertJobLog
    @Override
    public long insertJobLog(long entityId , String entityType , String jobName , String status)throws InvalidInputException
    {
        LOG.debug( "method to fetch the job-log details for entity, getLastRunForEntity() started." );
        JobLogDetails jobLogDetails = new JobLogDetails();
        jobLogDetails.setEntityId(entityId);
        jobLogDetails.setEntityType(entityType);
        jobLogDetails.setJobName(jobName);
        jobLogDetails.setStatus(status);
        jobLogDetails.setJobStartTime(new Timestamp(System.currentTimeMillis()));
        jobLogDetails.setJobUuid((UUID.randomUUID()).toString());
        long jobLogId = jobLogDetailsDao.insertJobLog(jobLogDetails);
        testApi test_api = new testApi();
        String[] args = {"sandra"};
        test_api.main(args);
        LOG.info("THE RETURN OF {}"+test_api);
        LOG.debug( "method to fetch the job-log details for entity, getLastRunForEntity() finished." );
        return jobLogId;
    }
}
