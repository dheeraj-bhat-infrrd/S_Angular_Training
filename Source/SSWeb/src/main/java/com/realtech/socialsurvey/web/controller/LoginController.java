package com.realtech.socialsurvey.web.controller;

// JIRA SS-21 : by RM-06 : BOC

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.authentication.AuthenticationService;
import com.realtech.socialsurvey.web.common.JspResolver;

@Controller
public class LoginController {

	private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	private AuthenticationService authenticationService;

	@RequestMapping(value = "/login")
	public String initLoginPage() {
		LOG.info("Login Page started");
		return JspResolver.LOGIN;
	}

	@RequestMapping(value = "/forgotPassword")
	public String initForgotPassword() {
		LOG.info("Forgot Password Page started");
		return JspResolver.FORGOT_PASSWORD;
	}

	@RequestMapping(value = "/resetPassword")
	public String initResetPassword() {
		LOG.info("Forgot Password Page started");
		return JspResolver.RESET_PASSWORD;
	}

	@RequestMapping(value = "/userLogin", method = RequestMethod.POST)
	public String login(Model model, HttpServletRequest request) {
		
		String userId = request.getParameter("userId");
		String password = request.getParameter("password");
		LOG.info("User login with user Id :" + userId);
		
		HttpSession session = request.getSession(true);

		User user = null;

		try {
			user = authenticationService.validateUser(userId, password);
			session.setAttribute("user", user);
		}
		catch (InvalidInputException e) {
			e.printStackTrace();
		}

		return JspResolver.MESSAGE_HEADER;
	}

}

// JIRA SS-21 : by RM-06 : EOC