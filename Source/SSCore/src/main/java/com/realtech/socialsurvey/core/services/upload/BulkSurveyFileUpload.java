package com.realtech.socialsurvey.core.services.upload;

import java.util.List;
import com.realtech.socialsurvey.core.entities.FileUpload;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;

public interface BulkSurveyFileUpload {
	
	/**
	 * Uploads file for send bulk survey request
	 * @param fileUpload
	 * @throws InvalidInputException
	 */
	public void uploadBulkSurveyFile(FileUpload fileUpload)  throws InvalidInputException;

	/**
	 * Gets a list of survey upload files
	 * @return
	 * @throws NoRecordsFetchedException
	 */
	public List<FileUpload> getSurveyUploadFiles() throws NoRecordsFetchedException;
	
	/**
	 * Updates the file upload status
	 * @param fileUpload
	 * @throws InvalidInputException
	 */
	public void updateFileUploadRecord(FileUpload fileUpload) throws InvalidInputException;
}
