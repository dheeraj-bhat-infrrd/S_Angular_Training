package com.realtech.socialsurvey.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.realtech.socialsurvey.web.common.JspResolver;

/**
 * Controller for generic jsp pages
 */
@Controller
public class GenericController {
	
	private static final Logger LOG = LoggerFactory.getLogger(GenericController.class);

	/**
	 * Method to redirect to errorpage
	 */
	@RequestMapping(value = "/errorpage")
	public String errorpage() {
		LOG.warn("Error found.");
		return JspResolver.ERROR_PAGE;
	}
}
