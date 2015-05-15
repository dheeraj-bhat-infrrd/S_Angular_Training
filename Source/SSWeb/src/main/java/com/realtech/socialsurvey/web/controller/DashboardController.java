package com.realtech.socialsurvey.web.controller;

// JIRA SS-137 : by RM-05 : BOC
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.solr.common.SolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.gson.Gson;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.AbridgedUserProfile;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyRecipient;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.entities.UserSettings;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.organizationmanagement.DashboardService;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.common.JspResolver;

@Controller
public class DashboardController {

	private static final Logger LOG = LoggerFactory.getLogger(DashboardController.class);

	@Autowired
	private SessionHelper sessionHelper;

	@Autowired
	private MessageUtils messageUtils;

	@Autowired
	private DashboardService dashboardService;

	@Autowired
	private ProfileManagementService profileManagementService;

	@Autowired
	private UserManagementService userManagementService;

	@Autowired
	private OrganizationManagementService organizationManagementService;

	@Autowired
	private SolrSearchService solrSearchService;

	@Autowired
	private EmailServices emailServices;

	@Autowired
	private SurveyHandler surveyHandler;

	@Autowired
	private URLGenerator urlGenerator;

	@Value("${ENABLE_KAFKA}")
	private String enableKafka;

	private final String EXCEL_FORMAT = "application/vnd.ms-excel";
	private final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";
	private final String EXCEL_FILE_EXTENSION = ".xlsx";

	/*
	 * Method to initiate dashboard
	 */
	@RequestMapping(value = "/dashboard")
	public String initDashboardPage(Model model, HttpServletRequest request) {
		LOG.info("Dashboard Page started");
		HttpSession session = request.getSession(false);
		User user = sessionHelper.getCurrentUser();

		try {
			// updating session with selected user profile if not set
			sessionHelper.updateProcessedUserProfiles(session, user);
			
			UserProfile selectedProfile = (UserProfile) session.getAttribute(CommonConstants.USER_PROFILE);
			int profileMasterId = selectedProfile.getProfilesMaster().getProfileId();

			model.addAttribute("userId", user.getUserId());
			model.addAttribute("emailId", user.getEmailId());
			model.addAttribute("profileId", selectedProfile.getUserProfileId());
			model.addAttribute("profileMasterId", profileMasterId);
			
			if (profileMasterId == CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID) {
				model.addAttribute("columnName", CommonConstants.COMPANY_ID_COLUMN);
				model.addAttribute("columnValue", user.getCompany().getCompanyId());
			}
			else if (profileMasterId == CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID) {
				model.addAttribute("columnName", CommonConstants.REGION_ID_COLUMN);
				model.addAttribute("columnValue", selectedProfile.getRegionId());
			}
			else if (profileMasterId == CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID) {
				model.addAttribute("columnName", CommonConstants.BRANCH_ID_COLUMN);
				model.addAttribute("columnValue", selectedProfile.getBranchId());
			}
			else if (profileMasterId == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID) {
				model.addAttribute("columnName", CommonConstants.AGENT_ID_COLUMN);
				model.addAttribute("columnValue", selectedProfile.getAgentId());
			}
		}
		catch (InvalidInputException e) {
			LOG.error("InvalidInputException caught in initDashboardPage while setting details about user. Nested exception is ", e);
			model.addAttribute("message", "InvalidInputException caught in initDashboardPage while setting details about user. Nested exception is "
					+ e.getMessage());
			return JspResolver.ERROR_PAGE;
		}
		catch (SolrException e) {
			LOG.error("SolrException caught in initDashboardPage while setting details about user. Nested exception is ", e);
			model.addAttribute("message",
					"SolrException caught in initDashboardPage while setting details about user. Nested exception is " + e.getMessage());
			return JspResolver.ERROR_PAGE;
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while logging in. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.LOGIN;
		}
		return JspResolver.DASHBOARD;
	}

	/*
	 * Method to get profile details for displaying
	 */
	@RequestMapping(value = "/profiledetails")
	public String getProfileDetails(Model model, HttpServletRequest request) {
		LOG.info("Method to get profile of company/region/branch/agent getProfileDetails() started");
		User user = sessionHelper.getCurrentUser();
		UserSettings userSettings = (UserSettings) request.getSession(false).getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);

		// settings profile details
		String columnName = request.getParameter("columnName");
		long columnValue = 0;
		if (columnName.equalsIgnoreCase(CommonConstants.COMPANY_ID_COLUMN)) {
			columnValue = user.getCompany().getCompanyId();
			model.addAttribute("name", user.getCompany().getCompany());
			model.addAttribute("title", getTitle(request, columnName, columnValue, user));
		}
		else if (columnName.equalsIgnoreCase(CommonConstants.REGION_ID_COLUMN)) {
			try {
				columnValue = Long.parseLong(request.getParameter("columnValue"));
			}
			catch (NumberFormatException e) {
				LOG.error("NumberFormatException caught in getProfileDetails() while converting columnValue for regionId/branchId/agentId.");
				throw e;
			}

			model.addAttribute("name", userSettings.getRegionSettings().get(columnValue).getContact_details().getName());
			model.addAttribute("title", getTitle(request, columnName, columnValue, user));
			model.addAttribute("company", user.getCompany().getCompany());
		}
		else if (columnName.equalsIgnoreCase(CommonConstants.BRANCH_ID_COLUMN)) {
			try {
				columnValue = Long.parseLong(request.getParameter("columnValue"));
			}
			catch (NumberFormatException e) {
				LOG.error("NumberFormatException caught in getProfileDetails() while converting columnValue for regionId/branchId/agentId.");
				throw e;
			}

			model.addAttribute("name", userSettings.getBranchSettings().get(columnValue).getContact_details().getName());
			model.addAttribute("title", getTitle(request, columnName, columnValue, user));
			model.addAttribute("company", user.getCompany().getCompany());
		}
		else if (columnName.equalsIgnoreCase(CommonConstants.AGENT_ID_COLUMN)) {
			columnValue = user.getUserId();
			model.addAttribute("name", user.getFirstName() + " " + (user.getLastName() != null ? user.getLastName() : ""));
			model.addAttribute("title", getTitle(request, columnName, columnValue, user));
			model.addAttribute("company", user.getCompany().getCompany());
		}

		// calculating details for circles
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

		double surveyScore = (double) Math.round(dashboardService.getSurveyScore(columnName, columnValue, numberOfDays)*1000.0)/1000.0;
		int sentSurveyCount = (int) dashboardService.getAllSurveyCountForPastNdays(columnName, columnValue, numberOfDays);
		int socialPostsCount = (int) dashboardService.getSocialPostsForPastNdays(columnName, columnValue, numberOfDays);
		int profileCompleteness = dashboardService.getProfileCompletionPercentage(user, columnName, columnValue, userSettings);

		model.addAttribute("socialScore", surveyScore);
		if(sentSurveyCount>999)
			model.addAttribute("surveyCount", "1K+");
		else
			model.addAttribute("surveyCount", sentSurveyCount);
		if(socialPostsCount>999)
			model.addAttribute("socialPosts", "1K+");
		else
			model.addAttribute("socialPosts", socialPostsCount);
		model.addAttribute("profileCompleteness", profileCompleteness);
		model.addAttribute("badges", dashboardService.getBadges(surveyScore, sentSurveyCount, socialPostsCount, profileCompleteness));

		LOG.info("Method to get profile of company/region/branch/agent getProfileDetails() finished");
		return JspResolver.DASHBOARD_PROFILEDETAIL;
	}

	/*
	 * Method to get survey details for showing details
	 */
	@RequestMapping(value = "/surveycount")
	public String getSurveyCount(Model model, HttpServletRequest request) {
		LOG.info("Method to get count of all, completed and clicked surveys, getSurveyCount() started");
		User user = sessionHelper.getCurrentUser();

		String columnName = request.getParameter("columnName");
		long columnValue = 0;
		try {
			String columnValueStr = request.getParameter("columnValue");
			columnValue = Long.parseLong(columnValueStr);
		}
		catch (NumberFormatException e) {
			LOG.error("NumberFormatException caught in getSurveyCount() while converting columnValue for regionId/branchId/agentId.");
			throw e;
		}

		if (columnName.equalsIgnoreCase(CommonConstants.COMPANY_ID_COLUMN)) {
			columnValue = user.getCompany().getCompanyId();
		}
		else if (columnName.equalsIgnoreCase(CommonConstants.AGENT_ID_COLUMN) && columnValue == 0) {
			columnValue = user.getUserId();
		}

		int numberOfDays = -1;
		try {
			if (request.getParameter("numberOfDays") != null) {
				numberOfDays = Integer.parseInt(request.getParameter("numberOfDays"));
			}
		}
		catch (NumberFormatException e) {
			LOG.error("NumberFormatException caught in getSurveyCount() while converting numberOfDays.");
			throw e;
		}

		model.addAttribute("allSurveySent", dashboardService.getAllSurveyCountForPastNdays(columnName, columnValue, numberOfDays));
		model.addAttribute("completedSurvey", dashboardService.getCompletedSurveyCountForPastNdays(columnName, columnValue, numberOfDays));
		model.addAttribute("clickedSurvey", dashboardService.getClickedSurveyCountForPastNdays(columnName, columnValue, numberOfDays));
		model.addAttribute("socialPosts", dashboardService.getSocialPostsForPastNdays(columnName, columnValue, numberOfDays));

		LOG.info("Method to get count of all, completed and clicked surveys, getSurveyCount() finished");
		return JspResolver.DASHBOARD_SURVEYSTATUS;
	}

	/*
	 * Method to get survey details for generating graph.
	 */
	@ResponseBody
	@RequestMapping(value = "/surveydetailsforgraph")
	public String getSurveyDetailsForGraph(Model model, HttpServletRequest request) {
		LOG.info("Method to get survey details for generating graph, getGraphDetailsForWeek() started.");
		User user = sessionHelper.getCurrentUser();

		try {
			String columnName = request.getParameter("columnName");
			String reportType = request.getParameter("reportType");
			if (columnName == null || columnName.isEmpty()) {
				LOG.error("Null/Empty value found for field columnName.");
				throw new NonFatalException("Null/Empty value found for field columnName.");
			}

			long columnValue = 0;
			try {
				String columnValueStr = request.getParameter("columnValue");
				columnValue = Long.parseLong(columnValueStr);
			}
			catch (NumberFormatException e) {
				LOG.error("NumberFormatException caught in getSurveyCountForCompany() while converting columnValue for regionId/branchId/agentId.");
				throw e;
			}

			if (columnName.equalsIgnoreCase(CommonConstants.COMPANY_ID_COLUMN)) {
				columnValue = user.getCompany().getCompanyId();
			}
			else if (columnName.equalsIgnoreCase(CommonConstants.AGENT_ID_COLUMN) && columnValue == 0) {
				columnValue = user.getUserId();
			}
			LOG.info("Method to get details for generating graph, getGraphDetailsForWeek() finished.");

			try {
				return new Gson().toJson(dashboardService.getSurveyDetailsForGraph(columnName, columnValue, reportType));
			}
			catch (ParseException e) {
				LOG.error("Parse Exception occurred in getSurveyDetailsForGraph(). Nested exception is ", e);
				return e.getMessage();
			}
		}
		catch (NonFatalException e) {
			LOG.error("Non fatal EXception caught in getSurveyDetailsForGraph() while getting details of surveys for graph. Nested exception is ", e);
			return e.getMessage();
		}
	}

	/*
	 * Method to fetch reviews for showing on dash board based upon start index and batch size.
	 */
	@RequestMapping(value = "/fetchdashboardreviews")
	public String getReviews(Model model, HttpServletRequest request) {
		LOG.info("Method to get reviews of company, region, branch, agent getReviews() started.");
		User user = sessionHelper.getCurrentUser();
		List<SurveyDetails> surveyDetails = new ArrayList<>();

		try {
			String startIndexStr = request.getParameter("startIndex");
			String batchSizeStr = request.getParameter("batchSize");
			int startIndex = Integer.parseInt(startIndexStr);
			int batchSize = Integer.parseInt(batchSizeStr);

			String columnName = request.getParameter("columnName");
			if (columnName == null || columnName.isEmpty()) {
				LOG.error("Invalid value (null/empty) passed for profile level.");
				throw new InvalidInputException("Invalid value (null/empty) passed for profile level.");
			}
			String profileLevel = getProfileLevel(columnName);

			long iden = 0;
			if (profileLevel.equals(CommonConstants.PROFILE_LEVEL_COMPANY)) {
				iden = user.getCompany().getCompanyId();
			}
			else if (profileLevel.equals(CommonConstants.PROFILE_LEVEL_INDIVIDUAL)) {
				iden = user.getUserId();
			}
			else {
				String columnValue = request.getParameter("columnValue");
				if (columnValue != null && !columnValue.isEmpty()) {
					try {
						iden = Long.parseLong(columnValue);
					}
					catch (NumberFormatException e) {
						LOG.error("NumberFormatException caught while parsing columnValue in getReviews(). Nested exception is ", e);
						throw e;
					}
				}
			}

			try {
				surveyDetails = profileManagementService.getReviews(iden, -1, -1, startIndex, batchSize, profileLevel, true, null, null, "date");
			}
			catch (InvalidInputException e) {
				LOG.error("InvalidInputException caught in getReviews() while fetching reviews. Nested exception is ", e);
				throw e;
			}
			model.addAttribute("reviews", surveyDetails);
		}
		catch (NonFatalException e) {
			LOG.error("Non fatal exception caught in getReviews() while fetching reviews. Nested exception is ", e);
			model.addAttribute("message", e.getMessage());
		}

		LOG.info("Method to get reviews of company, region, branch, agent getReviews() finished.");
		return JspResolver.DASHBOARD_REVIEWS;
	}

	/*
	 * Method to fetch count of all the reviews in SURVEY_DETAILS collection. It returns dta on the
	 * basis of columnName and columnValue which can be either of
	 * companyId/RegionId/BranchId/AgentId.
	 */
	@ResponseBody
	@RequestMapping(value = "/fetchdashboardreviewCount")
	public String getReviewCount(Model model, HttpServletRequest request) {
		LOG.info("Method to get reviews count getReviewCount() started.");
		User user = sessionHelper.getCurrentUser();
		long reviewCount = 0;

		try {
			String columnName = request.getParameter("columnName");
			if (columnName == null || columnName.isEmpty()) {
				LOG.error("Invalid value (null/empty) passed for profile level.");
				throw new InvalidInputException("Invalid value (null/empty) passed for profile level.");
			}
			String profileLevel = getProfileLevel(columnName);

			long iden = 0;
			if (profileLevel.equals(CommonConstants.PROFILE_LEVEL_COMPANY)) {
				iden = user.getCompany().getCompanyId();
			}
			else if (profileLevel.equals(CommonConstants.PROFILE_LEVEL_INDIVIDUAL)) {
				iden = user.getUserId();
			}
			else {
				String columnValue = request.getParameter("columnValue");
				if (columnValue == null || columnValue.isEmpty()) {
					LOG.error("Null or empty value passed for Region/BranchId. Please pass valid value.");
					throw new InvalidInputException("Null or empty value passed for Region/BranchId. Please pass valid value.");
				}

				try {
					iden = Long.parseLong(columnValue);
				}
				catch (NumberFormatException e) {
					LOG.error("NumberFormatException caught while parsing columnValue in getReviews(). Nested exception is ", e);
					throw e;
				}
			}

			// Calling service method to count number of reviews stored in database.
			reviewCount = profileManagementService.getReviewsCount(iden, -1, -1, profileLevel, true);
		}
		catch (NonFatalException e) {
			LOG.error("Non fatal exception caught in getReviewCount() while fetching reviews count. Nested exception is ", e);
			return new Gson().toJson(e.getMessage());
		}
		LOG.info("Method to get reviews count getReviewCount() finished.");
		return String.valueOf(reviewCount);
	}

	/*
	 * Method to fetch name which has to be displayed in Review section of dash board.
	 */
	@ResponseBody
	@RequestMapping(value = "/fetchName")
	public String getName(Model model, HttpServletRequest request) {
		LOG.info("Method to get name to display in review section getName() started.");
		User user = sessionHelper.getCurrentUser();
		String name = "";

		try {
			String columnName = request.getParameter("columnName");
			if (columnName == null || columnName.isEmpty()) {
				LOG.error("Invalid value (null/empty) passed for profile level.");
				throw new InvalidInputException("Invalid value (null/empty) passed for profile level.");
			}

			long id = 0;
			if (columnName.equals(CommonConstants.COMPANY_ID_COLUMN)) {
				return new Gson().toJson(user.getCompany().getCompany());
			}
			else if (columnName.equals(CommonConstants.AGENT_ID_COLUMN)) {
				return new Gson().toJson(user.getFirstName() + " " + user.getLastName());
			}
			else {
				String columnValue = request.getParameter("columnValue");
				if (columnValue != null && !columnValue.isEmpty()) {
					try {
						id = Long.parseLong(columnValue);
					}
					catch (NumberFormatException e) {
						LOG.error("NumberFormatException caught while parsing columnValue in getReviews(). Nested exception is ", e);
						throw e;
					}
				}

				if (columnName.equalsIgnoreCase(CommonConstants.BRANCH_ID_COLUMN))
					name = solrSearchService.searchBranchNameById(id);
				else if (columnName.equalsIgnoreCase(CommonConstants.REGION_ID_COLUMN))
					name = solrSearchService.searchRegionById(id);
			}
		}
		catch (NonFatalException e) {
			LOG.error("Non fatal exception caught in getReviewCount() while fetching reviews count. Nested exception is ", e);
			return new Gson().toJson(e.getMessage());
		}

		LOG.info("Method to get name to display in review section getName() finished.");
		return name;
	}

	/*
	 * Method to fetch incomplete survey data.
	 */
	@RequestMapping(value = "/fetchdashboardincompletesurvey")
	public String getIncompleteSurvey(Model model, HttpServletRequest request) {
		LOG.info("Method to get reviews of company, region, branch, agent getReviews() started.");
		List<SurveyDetails> surveyDetails;
		User user = sessionHelper.getCurrentUser();

		try {
			surveyDetails = fetchIncompleteSurveys(request, user);
			model.addAttribute("incompleteSurveys", surveyDetails);
		}
		catch (NonFatalException e) {
			LOG.error("Non fatal exception caught in getReviews() while fetching reviews. Nested exception is ", e);
			model.addAttribute("message", e.getMessage());
		}

		LOG.info("Method to get reviews of company, region, branch, agent getReviews() finished.");
		return JspResolver.DASHBOARD_INCOMPLETESURVEYS;
	}

	/*
	 * Method to get count of all the incomplete surveys.
	 */
	@ResponseBody
	@RequestMapping(value = "/fetchdashboardincompletesurveycount")
	public String getIncompleteSurveyCount(Model model, HttpServletRequest request) {
		LOG.info("Method to get reviews of company, region, branch, agent getReviews() started.");
		List<SurveyDetails> surveyDetails;
		User user = sessionHelper.getCurrentUser();

		try {
			surveyDetails = fetchIncompleteSurveys(request, user);
		}
		catch (NonFatalException e) {
			LOG.error("Non fatal exception caught in getReviews() while fetching reviews. Nested exception is ", e);
			return e.getMessage();
		}

		LOG.info("Method to get reviews of company, region, branch, agent getReviews() finished.");
		return String.valueOf(surveyDetails.size());
	}

	@RequestMapping(value = "/redirecttosurveyrequestpage")
	public String redirectToSurveyRequestPage(Model model, HttpServletRequest request) {
		User agent = sessionHelper.getCurrentUser();
		model.addAttribute("agentId", agent.getUserId());
		model.addAttribute("agentName", agent.getFirstName() + agent.getLastName());
		model.addAttribute("agentEmail", agent.getEmailId());
		return JspResolver.SURVEY_REQUEST;
	}

	/*
	 * Fetches incomplete surveys based upon the criteria. Criteria can be startIndex and/or
	 * batchSize.
	 */
	private List<SurveyDetails> fetchIncompleteSurveys(HttpServletRequest request, User user) throws InvalidInputException {
		LOG.debug("Method fetchIncompleteSurveys() started");
		List<SurveyDetails> surveyDetails;
		String startIndexStr = request.getParameter("startIndex");
		String batchSizeStr = request.getParameter("batchSize");
		int startIndex = Integer.parseInt(startIndexStr);
		int batchSize = Integer.parseInt(batchSizeStr);

		String columnName = request.getParameter("columnName");
		if (columnName == null || columnName.isEmpty()) {
			LOG.error("Invalid value (null/empty) passed for profile level.");
			throw new InvalidInputException("Invalid value (null/empty) passed for profile level.");
		}
		String profileLevel = getProfileLevel(columnName);

		long iden = 0;
		if (profileLevel.equals(CommonConstants.PROFILE_LEVEL_COMPANY)) {
			iden = user.getCompany().getCompanyId();
		}
		else if (profileLevel.equals(CommonConstants.PROFILE_LEVEL_INDIVIDUAL)) {
			iden = user.getUserId();
		}
		else {
			String columnValue = request.getParameter("columnValue");
			if (columnValue != null && !columnValue.isEmpty()) {
				try {
					iden = Long.parseLong(columnValue);
				}
				catch (NumberFormatException e) {
					LOG.error("NumberFormatException caught while parsing columnValue in getReviews(). Nested exception is ", e);
					throw e;
				}
			}
		}

		try {
			surveyDetails = profileManagementService.getIncompleteSurvey(iden, 0, 0, startIndex, batchSize, profileLevel, null, null);
		}
		catch (InvalidInputException e) {
			LOG.error("InvalidInputException caught in getReviews() while fetching reviews. Nested exception is ", e);
			throw e;
		}
		LOG.debug("Method fetchIncompleteSurveys() finished");
		return surveyDetails;
	}

	@RequestMapping(value = "/findregionbranchorindividual")
	public String getRegionBranchOrAgent(Model model, HttpServletRequest request) {
		LOG.info("Method to get list of regions, branches, agents getRegionBranchOrAgent() started.");
		User user = sessionHelper.getCurrentUser();
		long regionOrBranchId = 0;
		List<SolrDocument> result = null;

		try {
			String searchColumn = request.getParameter("searchColumn");
			if (searchColumn == null || searchColumn.isEmpty()) {
				LOG.error("Invalid value (null/empty) passed for search criteria.");
				throw new InvalidInputException("Invalid value (null/empty) passed for search criteria.");
			}
			model.addAttribute("searchColumn", searchColumn);

			String columnName = request.getParameter("columnName");
			if (columnName == null || columnName.isEmpty()) {
				LOG.error("Invalid value (null/empty) passed for profile level.");
				throw new InvalidInputException("Invalid value (null/empty) passed for profile level.");
			}

			String columnValueStr = request.getParameter("columnValue");
			if (columnValueStr == null || columnValueStr.isEmpty()) {
				LOG.error("Invalid value (null/empty) passed for Region/branch Id.");
				throw new InvalidInputException("Invalid value (null/empty) passed for Region/branch Id.");
			}

			String searchKey = request.getParameter("searchKey");
			if (searchKey == null) {
				searchKey = "";
			}

			if (columnName.equalsIgnoreCase(CommonConstants.COMPANY_ID_COLUMN)) {
				try {
					result = solrSearchService.searchBranchRegionOrAgentByName(searchColumn, searchKey, columnName, user.getCompany().getCompanyId());
				}
				catch (InvalidInputException e) {
					LOG.error("InvalidInputException caught in getRegionBranchOrAgent() while fetching details. Nested exception is ", e);
					throw e;
				}
			}
			else if (columnName.equalsIgnoreCase(CommonConstants.REGION_ID_COLUMN)) {
				try {
					regionOrBranchId = Long.parseLong(columnValueStr);
					result = solrSearchService.searchBranchRegionOrAgentByName(searchColumn, searchKey, columnName, regionOrBranchId);
				}
				catch (NumberFormatException e) {
					LOG.error("NumberFormatException caught in getRegionBranchOrAgent() while fetching details. Nested exception is ", e);
					throw e;
				}
				catch (InvalidInputException e) {
					LOG.error("InvalidInputException caught in getRegionBranchOrAgent() while fetching details. Nested exception is ", e);
					throw e;
				}
			}
			else if (columnName.equalsIgnoreCase(CommonConstants.BRANCH_ID_COLUMN)) {
				try {
					result = solrSearchService.searchBranchRegionOrAgentByName(searchColumn, searchKey, columnName, regionOrBranchId);
				}
				catch (InvalidInputException e) {
					LOG.error("InvalidInputException caught in getRegionBranchOrAgent() while fetching details. Nested exception is ", e);
					throw e;
				}
			}
			model.addAttribute("results", result);
		}
		catch (NonFatalException e) {
			LOG.error("Non fatal exception caught in getReviews() while fetching reviews. Nested exception is ", e);
			model.addAttribute("message", e.getMessage());
		}
		LOG.info("Method to get list of regions, branches, agents getRegionBranchOrAgent() finished.");
		return JspResolver.DASHBOARD_SEARCHRESULTS;
	}

	/*
	 * Method to send a reminder email to the customer if not completed survey already.
	 */
	@ResponseBody
	@RequestMapping(value = "/sendsurveyremindermail")
	public String sendReminderMailForSurvey(Model model, HttpServletRequest request) {
		LOG.info("Method to send email to remind customer for survey sendReminderMailForSurvey() started.");

		try {
			String customerName = request.getParameter("customerName");
			String customerEmail = request.getParameter("customerEmail");
			String agentName = request.getParameter("agentName");

			if (customerName == null || customerName.isEmpty()) {
				LOG.error("Invalid value (null/empty) passed for customerName.");
				throw new InvalidInputException("Invalid value (null/empty) passed for customerName.");
			}
			if (customerEmail == null || customerEmail.isEmpty()) {
				LOG.error("Invalid value (null/empty) passed for customerEmail.");
				throw new InvalidInputException("Invalid value (null/empty) passed for customerEmail.");
			}

			long agentId = 0;
			try {
				String agentIdStr = request.getParameter("agentId");
				if (agentIdStr == null || agentIdStr.isEmpty()) {
					LOG.error("Invalid value (null/empty) passed for agentIdStr.");
					throw new InvalidInputException("Invalid value (null/empty) passed for agentIdStr.");
				}
				agentId = Long.parseLong(agentIdStr);
			}
			catch (NumberFormatException e) {
				LOG.error("NumberFormatException caught while parsing agentId in sendReminderMailForSurvey(). Nested exception is ", e);
				throw e;
			}
			
			String surveyLink = "";
			SurveyDetails survey = surveyHandler.getSurveyDetails(agentId, customerEmail);
			if (survey != null) {
				surveyLink = survey.getUrl();
			}

			try {
				AgentSettings agentSettings = userManagementService.getUserSettings(agentId);
				String agentTitle = "";
				if (agentSettings.getContact_details() != null && agentSettings.getContact_details().getTitle() != null) {
					agentTitle = agentSettings.getContact_details().getTitle();
				}
				
				String agentPhone = "";
				if (agentSettings.getContact_details() != null && agentSettings.getContact_details().getContact_numbers() != null && 
						agentSettings.getContact_details().getContact_numbers().getWork() != null) {
					agentPhone = agentSettings.getContact_details().getContact_numbers().getWork();
				}
				
				User user = userManagementService.getUserByUserId(agentId);
				String companyName = user.getCompany().getCompany();

				if (enableKafka.equals(CommonConstants.YES)) {
					emailServices.queueSurveyReminderMail(customerEmail, customerName, agentName, surveyLink, agentPhone, agentTitle, companyName);
				}
				else {
					emailServices.sendDefaultSurveyReminderMail(customerEmail, customerName, agentName, surveyLink, agentPhone, agentTitle,
							companyName);
				}
			}
			catch (InvalidInputException e) {
				LOG.error("Exception occurred while trying to send survey reminder mail to : " + customerEmail);
				throw e;
			}

			// Increasing value of reminder count by 1.
			surveyHandler.updateReminderCount(agentId, customerEmail);
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException caught in sendReminderMailForSurvey() while sending mail. Nested exception is ", e);
		}

		LOG.info("Method to send email to remind customer for survey sendReminderMailForSurvey() finished.");
		return new Gson().toJson("success");
	}

	/*
	 * Method to download file containing completed surveys
	 */
	@RequestMapping(value = "/downloaddashboardcompletesurvey")
	public void getCompleteSurveyFile(Model model, HttpServletRequest request, HttpServletResponse response) {
		LOG.info("Method to get file containg completed surveys list getCompleteSurveyFile() started.");
		List<SurveyDetails> surveyDetails = new ArrayList<>();
		try {
			String columnName = request.getParameter("columnName");
			String startDateStr = request.getParameter("startDate");
			String endDateStr = request.getParameter("endDate");
			
			Date startDate = null;
			if (startDateStr != null && !startDateStr.isEmpty()) {
				try {
					startDate = new SimpleDateFormat(CommonConstants.DATE_FORMAT).parse(startDateStr);
				}
				catch (ParseException e) {
					LOG.error("ParseException caught in getCompleteSurveyFile() while parsing startDate. Nested exception is ", e);
				}
			}
			
			Date endDate = Calendar.getInstance().getTime();
			if (endDateStr != null && !endDateStr.isEmpty()) {
				try {
					endDate = new SimpleDateFormat(CommonConstants.DATE_FORMAT).parse(endDateStr);
				}
				catch (ParseException e) {
					LOG.error("ParseException caught in getCompleteSurveyFile() while parsing startDate. Nested exception is ", e);
				}
			}
			
			if (columnName == null || columnName.isEmpty()) {
				LOG.error("Invalid value (null/empty) passed for profile level.");
				throw new InvalidInputException("Invalid value (null/empty) passed for profile level.");
			}
			
			String profileLevel = getProfileLevel(columnName);
			long iden = 0;

			User user = sessionHelper.getCurrentUser();
			if (profileLevel.equals(CommonConstants.PROFILE_LEVEL_COMPANY)) {
				iden = user.getCompany().getCompanyId();
			}
			else if (profileLevel.equals(CommonConstants.PROFILE_LEVEL_INDIVIDUAL)) {
				iden = user.getUserId();
			}
			else {
				String columnValue = request.getParameter("columnValue");
				if (columnValue != null && !columnValue.isEmpty()) {
					try {
						iden = Long.parseLong(columnValue);
					}
					catch (NumberFormatException e) {
						LOG.error("NumberFormatException caught while parsing columnValue in getCompleteSurveyFile(). Nested exception is ", e);
						throw e;
					}
				}
			}
			
			try {
				surveyDetails = profileManagementService.getReviews(iden, -1, -1, -1, -1, profileLevel, true, startDate, endDate, "date");
				String fileLocation = "Completed_Survey_" + profileLevel + "_" + iden + EXCEL_FILE_EXTENSION;
				XSSFWorkbook workbook = dashboardService.downloadCompleteSurveyData(surveyDetails, fileLocation);
				response.setContentType(EXCEL_FORMAT);
				String headerKey = CONTENT_DISPOSITION_HEADER;
				String headerValue = String.format("attachment; filename=\"%s\"", new File(fileLocation).getName());
				response.setHeader(headerKey, headerValue);
				
				// write into file
				OutputStream responseStream = null;
				try {
					responseStream = response.getOutputStream();
					workbook.write(responseStream);
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				finally {
					try {
						responseStream.close();
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
				response.flushBuffer();
			}
			catch (InvalidInputException e) {
				LOG.error("InvalidInputException caught in getCompleteSurveyFile() while fetching completed surveys file. Nested exception is ", e);
				throw e;
			}
			catch (IOException e) {
				LOG.error("IOException caught in getCompleteSurveyFile() while fetching completed surveys file. Nested exception is ", e);
			}
		}
		catch (NonFatalException e) {
			LOG.error("Non fatal exception caught in getCompleteSurveyFile() while fetching completed surveys file. Nested exception is ", e);
		}
		LOG.info("Method to get file containg completed surveys list getCompleteSurveyFile() finished.");
	}

	/*
	 * Method to download file containing incomplete surveys
	 */
	@RequestMapping(value = "/downloaddashboardincompletesurvey")
	public void getIncompleteSurveyFile(Model model, HttpServletRequest request, HttpServletResponse response) {
		LOG.info("Method to get file containg incomplete surveys list getIncompleteSurveyFile() started.");
		List<SurveyDetails> surveyDetails = new ArrayList<>();
		try {
			String columnName = request.getParameter("columnName");
			if (columnName == null || columnName.isEmpty()) {
				LOG.error("Invalid value (null/empty) passed for profile level.");
				throw new InvalidInputException("Invalid value (null/empty) passed for profile level.");
			}

			String startDateStr = request.getParameter("startDate");
			String endDateStr = request.getParameter("endDate");

			Date startDate = null;
			Date endDate = Calendar.getInstance().getTime();
			if (startDateStr != null) {
				try {
					startDate = new SimpleDateFormat(CommonConstants.DATE_FORMAT).parse(startDateStr);
				}
				catch (ParseException e) {
					LOG.error("ParseException caught in getCompleteSurveyFile() while parsing startDate. Nested exception is ", e);
				}
			}
			if (endDateStr != null) {
				try {
					endDate = new SimpleDateFormat(CommonConstants.DATE_FORMAT).parse(endDateStr);
				}
				catch (ParseException e) {
					LOG.error("ParseException caught in getCompleteSurveyFile() while parsing startDate. Nested exception is ", e);
				}
			}

			String profileLevel = getProfileLevel(columnName);
			long iden = 0;

			User user = sessionHelper.getCurrentUser();
			if (profileLevel.equals(CommonConstants.PROFILE_LEVEL_COMPANY)) {
				iden = user.getCompany().getCompanyId();
			}
			else if (profileLevel.equals(CommonConstants.PROFILE_LEVEL_INDIVIDUAL)) {
				iden = user.getUserId();
			}
			else {
				String columnValue = request.getParameter("columnValue");
				if (columnValue != null && !columnValue.isEmpty()) {
					try {
						iden = Long.parseLong(columnValue);
					}
					catch (NumberFormatException e) {
						LOG.error("NumberFormatExcept;ion caught while parsing columnValue in getIncompleteSurveyFile(). Nested exception is ", e);
						throw e;
					}
				}
			}
			try {
				surveyDetails = profileManagementService.getIncompleteSurvey(iden, 0, 0, -1, -1, profileLevel, startDate, endDate);
				String fileName = "Incomplete_Survey_" + profileLevel + "_" + iden + ".xlsx";
				XSSFWorkbook workbook = dashboardService.downloadIncompleteSurveyData(surveyDetails, fileName);
				response.setContentType(EXCEL_FORMAT);
				String headerKey = CONTENT_DISPOSITION_HEADER;
				String headerValue = String.format("attachment; filename=\"%s\"", new File(fileName).getName());
				response.setHeader(headerKey, headerValue);
				// write into file
				OutputStream responseStream = null;
				try {
					responseStream = response.getOutputStream();
					workbook.write(responseStream);
				}
				catch (IOException e) {
					LOG.error("IOException caught in getIncompleteSurveyFile(). Nested exception is ", e);
				}
				finally {
					try {
						responseStream.close();
					}
					catch (IOException e) {
						LOG.error("IOException caught in getIncompleteSurveyFile(). Nested exception is ", e);
					}
				}
				response.flushBuffer();
			}
			catch (InvalidInputException e) {
				LOG.error("InvalidInputException caught in getIncompleteSurveyFile() while fetching incomplete reviews file. Nested exception is ", e);
				throw e;
			}
			catch (IOException e) {
				LOG.error("IOException caught in getIncompleteSurveyFile() while fetching incomplete reviews file. Nested exception is ", e);
			}
		}
		catch (NonFatalException e) {
			LOG.error("Non fatal exception caught in getReviews() while fetching incomplete reviews file. Nested exception is ", e);
		}
		LOG.info("Method to get file containg incomplete surveys list getIncompleteSurveyFile() finished.");
	}

	/*
	 * Method to 1. Store initial details of customer. 2. Send Invitation mail to the customer to
	 * take survey.
	 */
	@ResponseBody
	@RequestMapping(value = "/sendsurveyinvite")
	public String sendSurveyInvitation(HttpServletRequest request) {
		LOG.info("Method sendSurveyInvitation() called from DashboardController.");
		String custFirstName = request.getParameter("firstName");
		String custLastName = request.getParameter("lastName");
		String custEmail = request.getParameter("email");
		String custRelationWithAgent = request.getParameter("relation");
		User user = sessionHelper.getCurrentUser();

		try {
			surveyHandler.sendSurveyInvitationMail(custFirstName, custLastName, custEmail, custRelationWithAgent, user, true);
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException caught in sendSurveyInvitation(). Nested exception is ", e);
		}

		LOG.info("Method sendSurveyInvitation() finished from DashboardController.");
		return "Success";
	}

	@ResponseBody
	@RequestMapping(value = "/sendmultiplesurveyinvites", method = RequestMethod.POST)
	public String sendMultipleSurveyInvitations(HttpServletRequest request) {
		LOG.info("Method sendMultipleSurveyInvitations() called from DashboardController.");
		User user = sessionHelper.getCurrentUser();
		List<SurveyRecipient> surveyRecipients = null;

		try {
			String payload = request.getParameter("receiversList");
			try {
				if (payload == null) {
					throw new InvalidInputException("SurveyRecipients passed was null or empty");
				}
				surveyRecipients = new ObjectMapper().readValue(payload,
						TypeFactory.defaultInstance().constructCollectionType(List.class, SurveyRecipient.class));
			}
			catch (IOException ioException) {
				throw new NonFatalException("Error occurred while parsing the Json.", DisplayMessageConstants.GENERAL_ERROR, ioException);
			}

			// sending mails on traversing the list
			if (!surveyRecipients.isEmpty()) {
				for (SurveyRecipient recipient : surveyRecipients) {
					surveyHandler.sendSurveyInvitationMail(recipient.getFirstname(), recipient.getLastname(), recipient.getEmailId(), null, user,
							true);
				}
			}
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException caught in sendMultipleSurveyInvitations(). Nested exception is ", e);
		}

		LOG.info("Method sendMultipleSurveyInvitations() finished from DashboardController.");
		return "Success";
	}

	// Method to return title for logged in user.
	// It returns title for various fields e.g. company, region, branch and agent.
	private String getTitle(HttpServletRequest request, String field, long value, User user) {
		LOG.debug("Method to find title for " + field + " started.");
		String title = "";
		try {
			UserSettings userSettings = (UserSettings) request.getSession(false).getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);
			switch (field) {
				case CommonConstants.COMPANY_ID_COLUMN:
					title = userSettings.getCompanySettings().getContact_details().getTitle();
					break;
				case CommonConstants.REGION_ID_COLUMN:
					OrganizationUnitSettings regionSettings = userSettings.getRegionSettings().get(value);
					title = regionSettings.getContact_details().getTitle();
					break;
				case CommonConstants.BRANCH_ID_COLUMN:
					OrganizationUnitSettings branchSettings = userSettings.getBranchSettings().get(value);
					title = branchSettings.getContact_details().getTitle();
					break;
				case CommonConstants.AGENT_ID_COLUMN:
					for (UserProfile userProfile : user.getUserProfiles()) {
						if (userProfile.getBranchId() == value) {
							AgentSettings agentSettings = userSettings.getAgentSettings();
							if (agentSettings != null)
								title = agentSettings.getContact_details().getTitle();
						}
					}
					break;
				default:
					LOG.error("Invalid value " + field + " passed for field. It should be either of " + CommonConstants.COMPANY_ID_COLUMN + "/"
							+ CommonConstants.REGION_ID_COLUMN + "/" + CommonConstants.BRANCH_ID_COLUMN + "/" + CommonConstants.AGENT_ID_COLUMN);
			}
		}
		catch (NullPointerException e) {
			LOG.error("Null Pointer exception caught in getProfileDetails() while fetching designation of agent. Nested exception is ", e);
		}
		LOG.debug("Method to find title for " + field + " finished.");
		return title;
	}

	private String getProfileLevel(String columnName) {
		LOG.debug("Method to return profile level based upon column to be queried started.");
		String profileLevel = "";
		switch (columnName) {
			case CommonConstants.COMPANY_ID_COLUMN:
				profileLevel = CommonConstants.PROFILE_LEVEL_COMPANY;
				break;
			case CommonConstants.REGION_ID_COLUMN:
				profileLevel = CommonConstants.PROFILE_LEVEL_REGION;
				break;
			case CommonConstants.BRANCH_ID_COLUMN:
				profileLevel = CommonConstants.PROFILE_LEVEL_BRANCH;
				break;
			case CommonConstants.AGENT_ID_COLUMN:
				profileLevel = CommonConstants.PROFILE_LEVEL_INDIVIDUAL;
				break;
		}
		LOG.debug("Method to return profile level based upon column to be quried finished.");
		return profileLevel;
	}

	@ResponseBody
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/updatecurrentprofile")
	public String updateSelectedProfile(Model model, HttpServletRequest request) {
		LOG.info("Method updateSelectedProfile() started.");

		HttpSession session = request.getSession(false);
		User user = sessionHelper.getCurrentUser();

		AccountType accountType = (AccountType) session.getAttribute(CommonConstants.ACCOUNT_TYPE_IN_SESSION);
		Map<Long, UserProfile> profileMap = (Map<Long, UserProfile>) session.getAttribute(CommonConstants.USER_PROFILE_MAP);
		Map<Long, AbridgedUserProfile> profileAbridgedMap = (Map<Long, AbridgedUserProfile>) session.getAttribute(CommonConstants.USER_PROFILE_LIST);
		String profileIdStr = request.getParameter("profileId");

		UserProfile selectedProfile = userManagementService.updateSelectedProfile(user, accountType, profileMap, profileIdStr);

		session.setAttribute(CommonConstants.USER_PROFILE, selectedProfile);
		session.setAttribute(CommonConstants.PROFILE_NAME_COLUMN, profileAbridgedMap.get(selectedProfile.getUserProfileId()).getUserProfileName());

		LOG.info("Method updateSelectedProfile() finished.");
		return CommonConstants.SUCCESS_ATTRIBUTE;
	}
	
	@ResponseBody
	@RequestMapping(value = "/restartsurvey")
	public void restartSurvey(HttpServletRequest request) {
		String agentIdStr = request.getParameter("agentId");
		String customerEmail = request.getParameter("customerEmail");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		try {
			if (agentIdStr == null || agentIdStr.isEmpty()) {
				throw new InvalidInputException("Invalid value (Null/Empty) found for agentId.");
			}
			long agentId = Long.parseLong(agentIdStr);
			surveyHandler.changeStatusOfSurvey(agentId, customerEmail, true);
			SurveyDetails survey = surveyHandler.getSurveyDetails(agentId, customerEmail);
			User user = sessionHelper.getCurrentUser();
			surveyHandler.sendSurveyRestartMail(firstName, lastName, customerEmail, survey.getCustRelationWithAgent(), user, survey.getUrl());
		}
		catch (NonFatalException e) {
			LOG.error("NonfatalException caught in makeSurveyEditable(). Nested exception is ", e);
		}
	}
}
// JIRA SS-137 : by RM-05 : EOC