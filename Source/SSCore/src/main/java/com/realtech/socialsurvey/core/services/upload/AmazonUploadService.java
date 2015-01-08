package com.realtech.socialsurvey.core.services.upload;

import org.springframework.web.multipart.MultipartFile;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

/**
 * Holds methods to upload file to Amazon S3
 */
public interface AmazonUploadService {

	/**
	 * uploads File to Amazon S3
	 * 
	 * @throws InvalidInputException
	 */
	public String fileUploadHandler(MultipartFile fileLocal, String logoName) throws InvalidInputException;
}
