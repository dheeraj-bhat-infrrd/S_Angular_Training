package com.realtech.socialsurvey.core.starter;

import org.quartz.JobDataMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;

//one time job
public class UpdateSurveyDetails extends QuartzJobBean 
{	
	public static final Logger LOG = LoggerFactory.getLogger( UpdateSurveyDetails.class );

	private UserManagementService userManagementService;
	
	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		
		LOG.info( "Executing UpdateSurveyDetails job started." );
        
		// initialize the dependencies
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        userManagementService.updateSurveyDetails();
        LOG.info( "Executing UpdateSurveyDetails job finished." );
	}
	
	private void initializeDependencies( JobDataMap jobMap )
    {
		userManagementService = (UserManagementService) jobMap.get( "userManagementService" );
    }
}
