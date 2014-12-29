package com.realtech.socialsurvey.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.realtech.socialsurvey.web.common.JspResolver;

/**
 * Controller for generic jsp pages
 */
@Controller
public class GenericController {

	/**
	 * Method to redirect to errorpage
	 */
	@RequestMapping(value = "/errorpage")
	public String errorpage() {
		return JspResolver.ERROR_PAGE;
	}
}
