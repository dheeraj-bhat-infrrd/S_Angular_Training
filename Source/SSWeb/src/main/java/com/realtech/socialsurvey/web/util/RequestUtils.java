package com.realtech.socialsurvey.web.util;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RequestUtils {

	private static final Logger LOG = LoggerFactory.getLogger(RequestUtils.class);
	
	/**
	 * Gets the url host name with the scheme
	 * @param request
	 * @return
	 */
	public String getRequestServerName(HttpServletRequest request){
		LOG.info("Getting the server name from request");
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(request.getScheme()).append("://").append(request.getServerName());
		// if port is other than 80 or 443, then append that to the url
		if(request.getServerPort() != 80 && request.getServerPort() != 443){
			urlBuilder.append(":").append(request.getServerPort());
		}
		return urlBuilder.toString();
	}
}
