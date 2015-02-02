package com.realtech.socialsurvey.web.security;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.ELRequestMatcher;
import org.springframework.security.web.util.RequestMatcher;

@SuppressWarnings("deprecation")
public class AjaxAwareLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

	private static final Logger LOG = LoggerFactory.getLogger(AjaxAwareLoginUrlAuthenticationEntryPoint.class);
	private static final RequestMatcher requestMatcher = new ELRequestMatcher("hasHeader('X-Requested-With','XMLHttpRequest')");

	public AjaxAwareLoginUrlAuthenticationEntryPoint() {
		super();
	}

	public AjaxAwareLoginUrlAuthenticationEntryPoint(String loginFormUrl) {
		super(loginFormUrl);
	}

	@Override
	public void commence(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException authException)
			throws IOException, ServletException {

		if (requestMatcher.matches(request)) {
			LOG.info("Redirecting to login.do");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
		}
		else {
			super.commence(request, response, authException);
		}
	}
}