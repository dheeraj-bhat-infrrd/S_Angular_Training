package com.realtech.socialsurvey.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.realtech.socialsurvey.core.exception.FatalException;

/**
 * Global error handler
 *
 */

@ControllerAdvice
public class GlobalErrorController {

	private static final Logger LOG = LoggerFactory.getLogger(GlobalErrorController.class);
	
	/**
	 * Returns 500 ISE in case of fatal exception
	 * @param fe
	 */
	@ExceptionHandler(value=FatalException.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Fatal Exception")
	public void handleFatalException(FatalException fe){
		LOG.error("=====> FATAL ERROR: "+fe.getMessage(), fe);
	}
}
