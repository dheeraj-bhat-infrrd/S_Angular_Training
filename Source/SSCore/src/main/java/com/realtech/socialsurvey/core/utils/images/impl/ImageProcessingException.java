package com.realtech.socialsurvey.core.utils.images.impl;

import com.realtech.socialsurvey.core.exception.NonFatalException;

/**
 * Exception class to handle errors during image processing.
 *
 */
public class ImageProcessingException extends NonFatalException {

	/**
	 * default serial version id
	 */
	private static final long serialVersionUID = 2015020702364453334L;

	public ImageProcessingException() {
		super();
	}

	public ImageProcessingException(String message) {
		super(message);
	}

	public ImageProcessingException(String message, Throwable thrw) {
		super(message, thrw);
	}
	
	public ImageProcessingException(String message, String errorCode) {
		super(message,errorCode);
	}

	public ImageProcessingException(String message, String errorCode, Throwable thrw) {
		super(message, errorCode, thrw);
	}
}
