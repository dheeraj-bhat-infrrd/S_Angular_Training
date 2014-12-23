package com.realtech.socialsurvey.jobs;


import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.processor.CustomItemProcessor;

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
			
			String dateParam = new Date().toString();
			JobParameters param = new JobParametersBuilder().addString("date", dateParam).toJobParameters();
			
			LOG.info("Starting job execution.");
			JobExecution jobExecution = jobLauncher.run(job, param);
		}
		catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) 
		{	
			LOG.error("Execution error caught! Message : " + e.getMessage());
			
		}
		
		
	}
	
	

}
