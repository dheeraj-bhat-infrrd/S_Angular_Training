package com.realtech.socialsurvey.core.services.reportingmanagement.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
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
         	String pemString = "-----BEGIN RSA PRIVATE KEY-----"+ System.getProperty("line.separator")+
         	"MIIEowIBAAKCAQEAsE/8tlMDRiHNGo6wgqMpD/4KwiC95bD6ziyRgx9qA2nBemuGygUuVaKVCvKm"+
         	"yjlvctaTZikoBlWJiUdArTgqQK7m3u72rLbUvqvP0VZK2dAGfuhO7kPbeuVJMx9FmvxTsO35dDzR"+
         	"Kn3YPi0waTcJhVyhAAX3QBWU4KImW5QSFp8q9WH7LQkPHa4luoG1eh8zkQncUX8g7P7vmfPlBOSl"+
         	"XAcli4uJWN68x8kuRtmz49XryKdMw/yxn7MIfrUPQnC+7PyHHTURypC9nmg2RFFWNck9HTfGcU/u"+
         	"NZsfFht7cBllp24XZKQBrdOAEKDx5sdo1DvSqH1atbtQ9OkccgPwSQIDAQABAoIBAQCTN5apnpMD"+
         	"QkICRNa5ngcxZUwSYEhvu+EPukMeG2LnyqxUzefUU5MmygRtCcOe42pdlzGoggD7mPyodahZeqY9"+
         	"ME1yJFIwUNE/DDMQdl81k+94k/0kEeD3su/2EcyK5tnyyCIcnpOASWgAMrdTgzMd+gQtAs464kpX"+
         	"bs1cdjzMSV5kNu+NxLAnBIhZ4Q/hwCKxoQxDsFHIEU+aJc5jUS6Xp95CAJQgdpcNh5m0l7IDDwbI"+
         	"nrNHr8HR/L9YFhuNB8yJWzUakU3GweSBY2s2SZzqU7dAOJ/NBhnNgKqmbUzdMubs1WJht+j5uk8Y"+
         	"TqXVjvemVAXQ5lkYlT37oEzWw5AhAoGBAP7cdiM3ZDbIrp1ipPzN/Q25SoAOEbdRrsZBINFMCGNn"+
         	"Tf4KBD5DcaXCu/1FBLKVRPVtlda7/B5APADEMAhI6EWGuoBDWzC3Mprt3T9toZISM4ln4moROZD0"+
         	"4wYoCKYU5368g6AGUu2KkMgIlSFw3au0cu+so5AxGt3JdPMlpYiNAoGBALEZrESfDKnxx6ZdrN1W"+
         	"FSi5wvHD/Z/78znh2f0omw2tjA6Ux67uHdpcmwJ8fz3eA3Kg/anV8zn26gEqdkO60f+q4Prul6AC"+
         	"1ic9fNyAdEz8WbrDalZ2uI2lWEcnnZUhtd0mm7Bv7I9OjlLuzcfjobBf7JvQmG1q7rtqaVo+9Y2t"+
         	"AoGAIrkku12ToMcsyzKiafMitBj3poTEMybE2iwLbZifV/O+M4tn0pfbxwrGFBawMiiWMZ4RmSUZ"+
         	"j4GO60fPh4Fva/GUPV9v8C41jdg0may1I7KpJOJrfaoFHtPOjosrynz0oVbL1CSobcbAPRC2SM0Y"+
         	"1okKU89ApaebEnadIXeSmw0CgYAX7GTl/CpExsF2is7wM1G2Z6ma6SPYubs4hXbiCNsxNNFnotd6"+
         	"nPXxanMSGelCXTxP/sllaN5rZRxlPewt6A24IMg1fYeD+P4e3OKCEuQjBi/E1+MEfRMfJ0fOjN5v"+
         	"p9ceYbnEUso87ZqyP0oTB3/vCHY48iu1FC3dqVlD9J9NHQKBgGD1DsBILJuN2L0nzI4NTadweE6M"+
         	"vz3S660enZVRe6DtI3NILJQRUSYZafO0UhoAkthOx+BNEIM+9J0rKfHCkcKrBjolcOVRKvY2VmSS"+
         	"zYfUs4bNKDY+1CTdg2poxfmSyS/IuslDjieI2AahExPiqmDQiXxkvvrX81yyjuGi6kZb"+
         	"-----END RSA PRIVATE KEY-----";
         	//add identity
         	jsch.addIdentity("Raremile.pem", pemString.getBytes(),null,null);
             // Open a Session to remote SSH server and Connect.
             // Set User and IP of the remote host and SSH port.
             session = jsch.getSession("ec2-user", "52.11.11.200", 22);
             // When we do SSH to a remote host for the 1st time or if key at the remote host 
             // changes, we will be prompted to confirm the authenticity of remote host. 
             // This check feature is controlled by StrictHostKeyChecking ssh parameter. 
             // By default StrictHostKeyChecking  is set to yes as a security measure.
             session.setConfig("StrictHostKeyChecking", "no");
             //Set password
             //session.setPassword("testPassword");
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
