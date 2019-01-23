package com.realtech.socialsurvey.core.starter;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.services.searchengine.SearchEngineManagementServices;

public class RetryFailedSurveyProcessor extends QuartzJobBean {

	public static final Logger LOG = LoggerFactory.getLogger(RetryFailedSurveyProcessor.class);

	private SearchEngineManagementServices searchEngineManagementServices;

	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		LOG.info("Retrying failed survey processors");
		// initialize the dependencies
		initializeDependencies(jobExecutionContext.getMergedJobDataMap());
		searchEngineManagementServices.retryFailedSurveyProcessor();
	}

	private void initializeDependencies(JobDataMap jobMap) {
		searchEngineManagementServices = (SearchEngineManagementServices) jobMap.get("searchEngineManagementServices");
	}
}
