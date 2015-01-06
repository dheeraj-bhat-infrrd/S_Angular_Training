package com.realtech.socialsurvey.batch.jobs;
//JIRA: SS-61: By RM03

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.commons.CommonConstants;

/**
 * Initiates the batch job.
 *
 */
@Component
public class JobRunner {
	
	@Autowired
	private JobLauncher jobLauncher;
	
	@Autowired
	private Job job;
	
	private static final Logger LOG = LoggerFactory.getLogger(JobRunner.class);

	
	public void run(){
		
		try {
			
			LOG.info("Running the jobs from the task scheduler!");
			
			JobParameters param = new JobParametersBuilder().addString(CommonConstants.JOB_PARAMETER_NAME, String.valueOf(System.currentTimeMillis())).toJobParameters();
			
			LOG.info("Starting job execution.");
			jobLauncher.run(job, param);
		}
		catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) 
		{	
			LOG.error("Execution error caught! Message : " + e.getMessage());
			
		}
		
		
	}
	
	

}
