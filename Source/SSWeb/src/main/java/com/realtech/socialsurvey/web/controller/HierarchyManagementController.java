package com.realtech.socialsurvey.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

// JIRA SS-37 BY RM02 BOC
/**
 * Controller to manage hierarchy
 */
@Controller
public class HierarchyManagementController {

	private static final Logger LOG = LoggerFactory
			.getLogger(HierarchyManagementController.class);

	/**
	 * Method to call services for showing up the build hierarchy page
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/showbuildhierarchypage", method = RequestMethod.GET)
	public String showBuildHierarchyPage(Model model, HttpServletRequest request) {
		LOG.info("Method showBuildHierarchyPage called");

		LOG.info("Successfully completed method to showBuildHierarchyPage");
		return null;
	}

}
// JIRA SS-37 BY RM02 EOC
