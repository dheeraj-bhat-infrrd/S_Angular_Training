package com.realtech.socialsurvey.web.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;

public class UserLogoutHandler implements LogoutHandler {

	private static final Logger LOG = LoggerFactory.getLogger(UserLogoutHandler.class);

	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication user) {
		LOG.info("Inside logout method of MyLogoutHandler");

		// Invalidate session in browser
		request.getSession(false).invalidate();
		SecurityContextHolder.clearContext();
	}
}