package com.realtech.socialsurvey.core.handler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.starter.CSVBulkSurveyInitiatorProcessor;

/**
 * Handles the thread pool for csv upload for bulk survey
 *
 */
@Component
public class CSVBulkSurveyInitiatorHandler {

	public static final Logger LOG = LoggerFactory.getLogger(CSVBulkSurveyInitiatorHandler.class);

	private ExecutorService executor;

	@Autowired
	private CSVBulkSurveyInitiatorProcessor csvSurveyUploadProcessor;

	public void startFileUpload() {
		executor = Executors.newFixedThreadPool(1);
		executor.execute(csvSurveyUploadProcessor);
	}
}
