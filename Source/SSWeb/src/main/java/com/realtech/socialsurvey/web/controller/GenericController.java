package com.realtech.socialsurvey.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.realtech.socialsurvey.web.common.JspResolver;

/**
 * Controller for generic jsp pages
 */
@Controller
public class GenericController {

	private static final Logger LOG = LoggerFactory.getLogger(GenericController.class);

	@Value("${GOOGLE_ANALYTICS_ID}")
	private String googleAnalyticsTrackingId;

	@Value("${GOOGLE_MAP_API_KEY}")
	private String googleMapAPIKey;

	/**
	 * Method to redirect to errorpage
	 */
	@RequestMapping(value = "/errorpage")
	public String errorpage() {
		LOG.warn("Error found.");
		return JspResolver.ERROR_PAGE;
	}

	/**
	 * Method to return GA tracking id
	 */
	@ResponseBody
	@RequestMapping(value = "/fetchgatrackingid")
	public String fetchGoogleAnalyticsTrackingId() {
		LOG.info("Method fetchGoogleAnalyticsTrackingId() called from GenericController");
		return googleAnalyticsTrackingId;
	}

	/**
	 * Method to return google maps API Key
	 */
	@ResponseBody
	@RequestMapping(value = "/fetchgooglemapapikey")
	public String fetchGoogleMapAPIKey() {
		LOG.info("Method fetchGoogleMapAPIKey() called from GenericController");
		return googleMapAPIKey;
	}
}