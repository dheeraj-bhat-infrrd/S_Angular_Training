package com.realtech.socialsurvey.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.realtech.socialsurvey.web.common.JspResolver;

/*
 * Controller for Social Monitor Web Page 
 */
@Controller
public class SocialMonitorWebController {

    private static final Logger LOG = LoggerFactory.getLogger( SocialMonitorWebController.class );
    
    /*
     * Web API to return JSP name for social monitor web page (Add monitor page)
     */
    @RequestMapping ( value = "/showsocialmonitorpage", method = RequestMethod.GET)
    public String showSocialMonitorPage( Model model, HttpServletRequest request )
    {
        LOG.info( "Social Monitor Add monitors Page Started" );
        
        return JspResolver.SOCIAL_MONITOR_PAGE;
    }
    
    /*
     * Web API to return JSP name for social monitor Stream page
     */
    @RequestMapping ( value = "/showsocialmonitorstreampage", method = RequestMethod.GET)
    public String showSocialMonitorStreamPage( Model model, HttpServletRequest request )
    {
        LOG.info( "Social Monitor Stream Page Started" );
        
        return JspResolver.SOCIAL_MONITOR_STREAM_PAGE;
    }

}
