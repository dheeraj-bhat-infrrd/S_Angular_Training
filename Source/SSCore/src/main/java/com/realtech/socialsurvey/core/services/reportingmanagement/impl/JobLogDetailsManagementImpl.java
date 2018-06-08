package com.realtech.socialsurvey.core.services.reportingmanagement.impl;

import java.sql.Timestamp;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.dao.JobLogDetailsDao;
import com.realtech.socialsurvey.core.dao.impl.JobLogDetailsDaoImpl;
import com.realtech.socialsurvey.core.entities.JobLogDetails;
import com.realtech.socialsurvey.core.entities.JobLogDetailsResponse;
import com.realtech.socialsurvey.core.entities.remoteaccess.RemoteAccessConfig;
import com.realtech.socialsurvey.core.entities.remoteaccess.RemoteAccessResponse;
import com.realtech.socialsurvey.core.enums.remoteaccess.RemoteAccessAuthentication;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.remoteaccess.RemoteAccessException;
import com.realtech.socialsurvey.core.services.reportingmanagement.JobLogDetailsManagement;
import com.realtech.socialsurvey.core.utils.remoteaccess.RemoteAccessUtils;


@DependsOn ( "generic")
@Component
public class JobLogDetailsManagementImpl implements JobLogDetailsManagement
{

    @Autowired
    private JobLogDetailsDao jobLogDetailsDao;

    @Autowired
    private RemoteAccessUtils remoteAccessUtils;

    @Autowired
    private Utils utils;

    @Value ( "${SSH_ETLBOX_USER}")
    private String userName;

    @Value ( "${SSH_ETLBOX_PORT}")
    private int port;

    @Value ( "${PRIVATE_KEY_SYSTEM_PATH}")
    private String sysPath;

    @Value ( "${SSH_ETLBOX_REMOTE_HOST}")
    private String host;

    @Value ( "${SSH_SCRIPT_PATH}")
    private String scriptPath;

    @Value ( "${ALLOW_ETL_RUN_BEFORE_BUFFER}")
    private long bufferTime;

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
            jobLogDetailsResponse.setTimestampInEst( utils.convertDateToTimeZone( lastSuccessfulRun.getJobStartTime().getTime(), CommonConstants.TIMEZONE_EST ) );
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
        if ( !jobLogDetails.getStatus().equals( CommonConstants.STATUS_RUNNING )
            && !jobLogDetails.getJobName().equals( CommonConstants.CENTRALIZED_JOB_NAME ) ) {
            JobLogDetails jobLogDetailsScheduled = jobLogDetailsDao.getLastCentrelisedRun();
            long duration = System.currentTimeMillis() - jobLogDetailsScheduled.getJobStartTime().getTime();
            long scheduleAfter = bufferTime;
            if ( scheduleAfter > duration ) {
                isRunning = false;
            }
            LOG.info( "duration {} , schedule {}", duration, scheduleAfter );
        }

        LOG.debug( "method to fetch the etl run status, getIfEtlIsRunning() finished." );
        return isRunning;
    }


    @Override
    public JobLogDetailsResponse getLastRunForEntity( long entityId, String entityType ) throws InvalidInputException
    {
        LOG.debug( "method to fetch the job-log details for entity, getLastRunForEntity() started." );
        JobLogDetailsResponse jobLogDetailsResponse = new JobLogDetailsResponse();
        JobLogDetails jobLogDetails = jobLogDetailsDao.getJobLogDetailsOfLatestRunForEntity( entityId, entityType,
            CommonConstants.USER_RANKING_JOB_NAME );
        if ( jobLogDetails != null ) {
            jobLogDetailsResponse.setStatus( jobLogDetails.getStatus() );
            jobLogDetailsResponse.setTimestampInEst( utils.convertDateToTimeZone( jobLogDetails.getJobStartTime().getTime(), CommonConstants.TIMEZONE_EST ) );
        }
        LOG.debug( "method to fetch the job-log details for entity, getLastRunForEntity() finished." );
        return jobLogDetailsResponse;
    }


    //insertJobLog
    @Override
    public long insertJobLog( long entityId, String entityType, String jobName, String status ) throws InvalidInputException
    {
        LOG.debug( "method to fetch the job-log details for entity, getLastRunForEntity() started." );
        JobLogDetails jobLogDetails = new JobLogDetails();
        jobLogDetails.setEntityId( entityId );
        jobLogDetails.setEntityType( entityType );
        jobLogDetails.setJobName( jobName );
        jobLogDetails.setStatus( status );
        jobLogDetails.setJobStartTime( new Timestamp( System.currentTimeMillis() ) );
        jobLogDetails.setJobUuid( ( UUID.randomUUID() ).toString() );
        long jobLogId = jobLogDetailsDao.insertJobLog( jobLogDetails );
        LOG.debug( "method to fetch the job-log details for entity, getLastRunForEntity() finished." );
        return jobLogId;
    }


    @Async
    @Override
    public void recalEtl( long companyId, long jobLogId ) throws InvalidInputException
    {
        LOG.info( "method recalEtl() started" );
        try {

            // configure remote access
            RemoteAccessConfig config = new RemoteAccessConfig();

            // connect to ETL box using key file
            config.setPreferredAuthentication( RemoteAccessAuthentication.USING_PUBLIC_KEY );

            // give the path to key file
            config.setKeyPath( sysPath.trim() );

            // Set User and IP of the remote host and SSH port.
            config.setUserName( userName );
            config.setHost( host );
            config.setPort( 22 );

            // command to run the script
            String etlScriptcommand = scriptPath + " " + companyId + " " + jobLogId;

            LOG.debug( "executing command: {} to ETL server as user {} to  host {} and port {}", userName, host, port );
            RemoteAccessResponse response = remoteAccessUtils.executeCommand( config, etlScriptcommand );


            LOG.debug( "Exist status of command executed is {}", response.getStatus() );
            LOG.debug( "output of the executed command : {}", response.getResponse() );

            if ( response.getStatus() > 0 ) {
                LOG.info( "Remote script exec error! {}", response.getStatus() );
                JobLogDetails jobLogDetails = jobLogDetailsDao.findById( JobLogDetails.class, jobLogId );
                jobLogDetails.setStatus( "Remote script exec error " + response.getStatus() );
                jobLogDetailsDao.updateJobLog( jobLogDetails );
            }

        } catch ( RemoteAccessException | InvalidInputException e ) {
            LOG.error( "Exception caught  while trying to trigger user ranking etl {}", e );
            JobLogDetails jobLogDetails = jobLogDetailsDao.findById( JobLogDetails.class, jobLogId );
            jobLogDetails.setStatus( "Exception caught  while trying to trigger user ranking etl : " + e.getMessage() );
            jobLogDetailsDao.updateJobLog( jobLogDetails );

        }
    }
}
