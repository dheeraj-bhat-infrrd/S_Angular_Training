package com.realtech.socialsurvey.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.UserSessionInvalidateException;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.common.JspResolver;

/**
 * Global error handler
 */

@ControllerAdvice
public class GlobalErrorController {

	private static final Logger LOG = LoggerFactory.getLogger(GlobalErrorController.class);

	@Autowired
	private MessageUtils messageUtils;

	/**
	 * Returns 500 ISE in case of fatal exception
	 * 
	 * @param fe
	 */
	@ExceptionHandler(value = FatalException.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Fatal Exception")
	public void handleFatalException(FatalException fe) {
		LOG.error("=====> FATAL ERROR: " + fe.getMessage(), fe);
	}

	@ExceptionHandler(value = UserSessionInvalidateException.class)
	public String handleUserInvalidateSession(UserSessionInvalidateException ex, Model model) {
		model.addAttribute("message",
				messageUtils.getDisplayMessage(DisplayMessageConstants.INVALID_USER_CREDENTIALS, DisplayMessageType.ERROR_MESSAGE));
		return JspResolver.LOGIN;
	}
}
