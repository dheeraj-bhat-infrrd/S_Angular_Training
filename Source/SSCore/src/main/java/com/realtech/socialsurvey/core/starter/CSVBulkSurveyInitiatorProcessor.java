package com.realtech.socialsurvey.core.starter;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.FileUpload;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.upload.BulkSurveyFileUpload;

@Component
public class CSVBulkSurveyInitiatorProcessor implements Runnable {

	public static final Logger LOG = LoggerFactory.getLogger(CSVBulkSurveyInitiatorProcessor.class);

	@Autowired
	private BulkSurveyFileUpload bulkSurveyFileUpload;

	@Override
	public void run() {
		LOG.info("Checking if any bulk survey needs to be uploaded");
		while(true){
			LOG.debug("Checking for any new file upload");
			// check if there are any files to be uploaded
			try {
				List<FileUpload> filesToBeUploaded = bulkSurveyFileUpload.getSurveyUploadFiles();
				for(FileUpload fileUpload : filesToBeUploaded){
					try {
						// update the status to be processing
						fileUpload.setStatus(CommonConstants.STATUS_UNDER_PROCESSING);
						bulkSurveyFileUpload.updateFileUploadRecord(fileUpload);
						// parse the csv
						bulkSurveyFileUpload.uploadBulkSurveyFile(fileUpload);
						// update the status to be processed
						fileUpload.setStatus(CommonConstants.STATUS_INACTIVE);
						bulkSurveyFileUpload.updateFileUploadRecord(fileUpload);
					}
					catch (InvalidInputException e) {
						LOG.debug("Error updating the status");
						continue;
					} catch (ProfileNotFoundException e) {
						LOG.error("error while upload bulk survey " , e);
					}
				}
			}
			catch (NoRecordsFetchedException e) {
				LOG.debug("No files to be uploaded for survey. Sleep for a minute");
				try {
					Thread.sleep(1000 * 60);
				}
				catch (InterruptedException e1) {
					LOG.warn("Thread interrupted");
					break;
				}
			}
			
		}
	}

}
