package com.realtech.socialsurvey.core.services.upload;

import org.springframework.web.multipart.MultipartFile;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

/**
 * Holds methods to upload file to the application server
 */
public interface FileUploadService {

	/**
	 * uploads image to server path specified
	 * 
	 * @throws InvalidInputException
	 */
	public String fileUploadHandler(MultipartFile fileLocal, String logoName) throws InvalidInputException;
}
