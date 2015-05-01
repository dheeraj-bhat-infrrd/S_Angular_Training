package com.realtech.socialsurvey.web.handler;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import com.realtech.socialsurvey.web.common.JspResolver;

public class UserLoginHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	private static final Logger LOG = LoggerFactory.getLogger(UserLoginHandler.class);

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authUser) throws IOException,
			ServletException {
		LOG.info("Inside onAuthenticationSuccess controller");
		String isDirectRegistration = request.getParameter("j_isDirectRegistration");
		LOG.debug("isDirectRegistration in UserLoginHandler is "+isDirectRegistration);
		HttpSession session = request.getSession(false);
		String redirectTo = null;

		if (session != null) {
			redirectTo = (String) request.getSession().getAttribute("url_prior_login");
			LOG.info("Session Check: " + redirectTo);
		}

		if (redirectTo != null && !redirectTo.contains(JspResolver.LOGIN)) {
			response.sendRedirect(redirectTo);
			LOG.info("Url Check: " + redirectTo);
		}
		else {
			redirectTo = "./"+JspResolver.USER_LOGIN+".do";
			if(isDirectRegistration.equalsIgnoreCase("false")){
				redirectTo = redirectTo+"?bm=I";
			}
			response.sendRedirect(redirectTo);
			LOG.info("Final Check: " + redirectTo);
		}
	}
}