package com.realtech.socialsurvey.core.starter;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.handler.CSVBulkSurveyInitiatorHandler;

@Component
public class CSVBulkSurveyUploader extends QuartzJobBean {

	public static final Logger LOG = LoggerFactory.getLogger(CSVBulkSurveyUploader.class);

	private CSVBulkSurveyInitiatorHandler bulkSurveyUploadHandler;

	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		LOG.info("Executing CSVBulkSurveyUploader");
		initializeDependencies(jobExecutionContext.getMergedJobDataMap());
		bulkSurveyUploadHandler.startFileUpload();

	}

	private void initializeDependencies(JobDataMap jobMap) {

		bulkSurveyUploadHandler = (CSVBulkSurveyInitiatorHandler) jobMap.get("surveyuploadHandler");
	}
}
