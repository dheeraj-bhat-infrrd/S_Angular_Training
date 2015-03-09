package com.realtech.socialsurvey.web.controller;

// JIRA SS-137 : by RM-05 : BOC

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.google.gson.Gson;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.services.organizationmanagement.DashboardService;

@Controller
public class DashboardController {

	private static final Logger LOG = LoggerFactory.getLogger(DashboardController.class);

	@Autowired
	private SessionHelper sessionHelper;

	@Autowired
	private DashboardService dashboardService;

	@ResponseBody
	@RequestMapping(value = "/surveycount")
	public String getSurveyCount(Model model, HttpServletRequest request) {
		LOG.info("Method to get count of all, completed and clicked surveys, getSurveyCount() started.");
		Map<String, Object> surveyCount = new HashMap<String, Object>();
		// try {
		User user = sessionHelper.getCurrentUser();
		String columnName = request.getParameter("columnName");
		long columnValue = 0;
		if (columnName.equalsIgnoreCase(CommonConstants.COMPANY_ID_COLUMN)) {
			columnValue = user.getCompany().getCompanyId();
		}
		else {
			try {
				columnValue = Long.parseLong(request.getParameter("columnValue"));
			}
			catch (NumberFormatException e) {
				LOG.error("NumberFormatException caught in getSurveyCountForCompany() while converting columnValue for regionId/branchId/agentId.");
				throw e;
			}
		}
		int numberOfDays = -1;
		try {
			if (request.getParameter("numberOfDays") != null) {
				numberOfDays = Integer.parseInt(request.getParameter("numberOfDays"));
			}
		}
		catch (NumberFormatException e) {
			LOG.error("NumberFormatException caught in getSurveyCountForCompany() while converting numberOfDays.");
			throw e;
		}
		long allSurveyCount = dashboardService.getAllSurveyCountForPastNdays(columnName, columnValue, numberOfDays);
		surveyCount.put("allSurveySent", allSurveyCount);
		surveyCount.put("completedSurvey", dashboardService.getCompletedSurveyCountForPastNdays(columnName, columnValue, numberOfDays));
		surveyCount.put("clickedSurvey", dashboardService.getClickedSurveyCountForPastNdays(columnName, columnValue, numberOfDays));
		surveyCount.put("socialPosts", dashboardService.getSocialPostsForPastNdays(columnName, columnValue, numberOfDays));

		/*
		 * } catch (NonFatalException e) { }
		 */

		LOG.info("Method to get count of surveys sent in entire company, getSurveyCountForCompany() finished.");
		return new Gson().toJson(surveyCount);
	}

	@ResponseBody
	@RequestMapping(value = "/profiledetails")
	public String getProfileDetails(Model model, HttpServletRequest request) {
		LOG.info("Method to get profile of company, region, branch, agent getProfileDetails() started.");
		Map<String, Object> profileDetails = new HashMap<>();
		User user = sessionHelper.getCurrentUser();
		String columnName = request.getParameter("columnName");
		long columnValue = 0;
		if (columnName.equalsIgnoreCase(CommonConstants.COMPANY_ID_COLUMN)) {
			columnValue = user.getCompany().getCompanyId();
		}
		else {
			try {
				columnValue = Long.parseLong(request.getParameter("columnValue"));
			}
			catch (NumberFormatException e) {
				LOG.error("NumberFormatException caught in getProfileDetails() while converting columnValue for regionId/branchId/agentId.");
				throw e;
			}
		}
		int numberOfDays = 30;
		try {
			if (request.getParameter("numberOfDays") != null) {
				numberOfDays = Integer.parseInt(request.getParameter("numberOfDays"));
			}
		}
		catch (NumberFormatException e) {
			LOG.error("NumberFormatException caught in getProfileDetails() while converting numberOfDays.");
			throw e;
		}
		profileDetails.put("socialScore", (int) dashboardService.getSocialScore(columnName, columnValue, numberOfDays));
		profileDetails.put("surveyCount", dashboardService.getAllSurveyCountForPastNdays(columnName, columnValue, numberOfDays));
		profileDetails.put("socialPosts", dashboardService.getSocialPostsForPastNdays(columnName, columnValue, numberOfDays));
		LOG.info("Method to get profile of company, region, branch, agent getProfileDetails() finished.");
		return new Gson().toJson(profileDetails);
	}
}
// JIRA SS-137 : by RM-05 : EOC