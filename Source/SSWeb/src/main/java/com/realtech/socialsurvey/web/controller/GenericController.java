package com.realtech.socialsurvey.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
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
     * Method for invalidating user before going back to homepage
     * when user clicks on Go back to Homepage button in error page.
     */
    @RequestMapping ( value = "/removeuseronerror")
    public String invalidateuser( HttpServletRequest request )
    {
        LOG.info( "Invalidating user before going back to home page, invalidateuser() called" );
        HttpSession session = request.getSession();
        session.invalidate();
        SecurityContextHolder.clearContext();
        LOG.info( "Invalidated user before going back to home page, invalidateuser() call ended" );
        return JspResolver.INDEX;
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
	
	/**
     * Method to redirect to pageNotFound page
     */
    @RequestMapping(value = "/sspagenotfound")
    public String ssPageNotFound() {
        LOG.warn("Page not found.");
        return JspResolver.SS_PAGE_NOT_FOUND;
    }
}