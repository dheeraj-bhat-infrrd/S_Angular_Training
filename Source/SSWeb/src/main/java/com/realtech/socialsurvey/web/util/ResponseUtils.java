package com.realtech.socialsurvey.web.util;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author manish
 *
 */
@Component
public class ResponseUtils
{
    private static final Logger LOG = LoggerFactory.getLogger(ResponseUtils.class);

    
    private static final String EXPIRES = "Expires";
    private static final String PRAGMA = "Pragma";
    private static final String NO_CACHE = "no-cache";
    private static final String NO_CACHE_NO_STORE_MUST_REVALIDATE = "no-cache, no-store, must-revalidate";
    private static final String CACHE_CONTROL = "Cache-Control";
    
    /**
     * Method to set Cache-Control: no-store, must-revalidate 
     *                              Expires: 0
     * @param response
     */
    public void setNoCacheControlHeaders(HttpServletResponse response) {
        response.setHeader(CACHE_CONTROL, NO_CACHE_NO_STORE_MUST_REVALIDATE);
        response.setHeader(PRAGMA, NO_CACHE);
        response.setDateHeader(EXPIRES, 0);
    }
    
}
