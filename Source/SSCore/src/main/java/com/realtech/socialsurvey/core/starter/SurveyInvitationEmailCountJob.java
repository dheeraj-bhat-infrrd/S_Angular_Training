/**
 * 
 */
package com.realtech.socialsurvey.core.starter;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.entities.ReportRequest;
import com.realtech.socialsurvey.core.integration.stream.StreamApiIntegrationBuilder;

/**
 * @author Subhrajit
 *
 */
public class SurveyInvitationEmailCountJob extends QuartzJobBean {
	
	
	public static final Logger LOG = LoggerFactory.getLogger(SurveyInvitationEmailCountJob.class);
	
	private ReportRequest reportRequest = new ReportRequest();
	private StreamApiIntegrationBuilder streamApiIntegrationBuilder;
	private String timeFrame;

	/* (non-Javadoc)
	 * @see org.springframework.scheduling.quartz.QuartzJobBean#executeInternal(org.quartz.JobExecutionContext)
	 */
	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
		reportRequest.transform(timeFrame);
        streamApiIntegrationBuilder.getStreamApi().triggerBatch( reportRequest );

	}
	
	private void initializeDependencies( JobDataMap jobMap )
    {
		streamApiIntegrationBuilder = (StreamApiIntegrationBuilder) jobMap.get( "streamApiIntegrationBuilder" );
		timeFrame = (String) jobMap.get("timeFrame");
    }

}
