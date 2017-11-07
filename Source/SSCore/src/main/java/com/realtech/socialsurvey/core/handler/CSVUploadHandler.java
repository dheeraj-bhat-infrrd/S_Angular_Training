package com.realtech.socialsurvey.core.handler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.starter.CSVUploadProcessor;

@Component
public class CSVUploadHandler {

	public static final Logger LOG = LoggerFactory.getLogger(CSVUploadHandler.class);

	private ExecutorService executor;

	@Autowired
	private CSVUploadProcessor csvUploadProcessor;

	public void startFileUpload() {
		executor = Executors.newFixedThreadPool(1);
		executor.execute(csvUploadProcessor);
	}
}
