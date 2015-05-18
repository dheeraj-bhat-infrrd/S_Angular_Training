package com.realtech.socialsurvey.core.starter;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.feed.impl.SocialFeedIngestionKickStarter;

/**
 * Ingester for social feed
 */
@Component("socialfeedingester")
public class SocialFeedIngester extends QuartzJobBean{

	private static final Logger LOG = LoggerFactory.getLogger(SocialFeedIngester.class);

	private SocialFeedIngestionKickStarter ingestor;

	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) {
		LOG.info("Executing SocialFeedIngester");
		initializeDependencies(jobExecutionContext.getMergedJobDataMap());
		ingestor.startFeedIngestion();
	}
	
	private void initializeDependencies(JobDataMap jobMap) {
		ingestor = (SocialFeedIngestionKickStarter) jobMap.get("ingestor");
	}
}