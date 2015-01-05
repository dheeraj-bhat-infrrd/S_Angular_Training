package com.realtech.socialsurvey.core.services.upload;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

/**
 * Holds methods to upload image to the application
 */
public interface ImageUploadService {

	/**
	 * uploads image to server path specified
	 * 
	 * @throws IOException
	 */
	public String imageUploadHandler(MultipartFile fileLocal, String logoName) throws InvalidInputException;
}
