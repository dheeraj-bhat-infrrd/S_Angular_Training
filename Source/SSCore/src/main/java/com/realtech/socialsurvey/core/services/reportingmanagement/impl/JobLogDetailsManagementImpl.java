package com.realtech.socialsurvey.core.services.reportingmanagement.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Properties;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.Utils;
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
        	JobLogDetails jobLogDetailsScheduled = jobLogDetailsDao.getLastCentrelisedRun();
        	long duration = System.currentTimeMillis() - jobLogDetailsScheduled.getJobStartTime().getTime();
        	long scheduleAfter = CommonConstants.BUFFER;
        	if(scheduleAfter > duration) {
        		isRunning = false;
        	}
        	LOG.info("duration {} , schedule {}",duration,scheduleAfter);
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
        recalEtl(entityId, jobLogId);
        LOG.info("THE RETURN OF SSH{}");
        LOG.debug( "method to fetch the job-log details for entity, getLastRunForEntity() finished." );
        return jobLogId;
    }
    
    @Override 
    public void recalEtl(long companyId , long jobLogId) {
    	 JSch jsch = new JSch();

         Session session;
         try {
         
             // Open a Session to remote SSH server and Connect.
             jsch.addIdentity("/home/ec2-user/.ssh/sndy");
             // Set User and IP of the remote host and SSH port.
             session = jsch.getSession("ec2-user", "52.34.21.141", 22);
             // When we do SSH to a remote host for the 1st time or if key at the remote host 
             // changes, we will be prompted to confirm the authenticity of remote host. 
             // This check feature is controlled by StrictHostKeyChecking ssh parameter. 
             // By default StrictHostKeyChecking  is set to yes as a security measure.
             Properties config = new Properties();
             config.put("StrictHostKeyChecking", "no");
             session.setConfig(config);
             session.connect();

             // create the execution channel over the session
             ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
             // Set the command to execute on the channel and execute the command
             channelExec.setCommand("./test/test.sh " + companyId);
             channelExec.connect();

             // Get an InputStream from this channel and read messages, generated 
             // by the executing command, from the remote side.
             InputStream in = channelExec.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(in));
             String line;
             while ((line = reader.readLine()) != null) {
                 LOG.info(line);
             }

             // Command execution completed here.

             // Retrieve the exit status of the executed command
             int exitStatus = channelExec.getExitStatus();
             if (exitStatus > 0) {
            	 LOG.info("Remote script exec error! " + exitStatus);
             }
             //Disconnect the Session
             session.disconnect();
         } catch (JSchException e) {
             e.printStackTrace();
         } catch (IOException e) {
             e.printStackTrace();
         }

    }
}
