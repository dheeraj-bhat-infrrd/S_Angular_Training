package com.realtech.socialsurvey.core.starter;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.handler.CSVUploadHandler;

/**
 * Uploads the hierarchy
 */
@Component
public class CSVHierarchyUploader extends QuartzJobBean {

	public static final Logger LOG = LoggerFactory.getLogger(CSVHierarchyUploader.class);

	private CSVUploadHandler uploadHandler;

	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) {
		LOG.info("Executing CSVHierarchyUploader");
		initializeDependencies(jobExecutionContext.getMergedJobDataMap());
		uploadHandler.startFileUpload();

	}

	private void initializeDependencies(JobDataMap jobMap) {

		uploadHandler = (CSVUploadHandler) jobMap.get("uploadHandler");
	}
}
