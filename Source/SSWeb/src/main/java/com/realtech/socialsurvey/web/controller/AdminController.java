package com.realtech.socialsurvey.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.realtech.socialsurvey.core.services.admin.AdminAuthenticationService;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.common.JspResolver;

@Controller
public class AdminController {
	
	private static final Logger LOG = LoggerFactory.getLogger(AdminController.class);
	
	@Autowired
	private MessageUtils messageUtils;
	
	@Autowired
	private AdminAuthenticationService adminAuthenticationService;
	
	@RequestMapping(value = "/admindashboard")
	public String adminDashboard(Model model, HttpServletRequest request, HttpServletResponse response) {
		
		LOG.info("Inside adminDashboard() method in admin controller");
		
		return JspResolver.ADMIN_DASHBOARD;
	}
}
