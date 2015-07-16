package com.realtech.socialsurvey.web.controller;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.noggit.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.BranchFromSearch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.RegionFromSearch;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserFromSearch;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.admin.AdminAuthenticationService;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.common.JspResolver;

@Controller
public class AdminController {

	private static final Logger LOG = LoggerFactory.getLogger(AdminController.class);

	@Autowired
	private MessageUtils messageUtils;

	@Autowired
	private AdminAuthenticationService adminAuthenticationService;

	@Autowired
	private SolrSearchService solrSearchService;

	@Autowired
	private OrganizationManagementService organizationManagementService;

	@Autowired
	private UserManagementService userManagementService;

	@Autowired
	private SessionHelper sessionHelper;

	@RequestMapping(value = "/admindashboard")
	public String adminDashboard(Model model, HttpServletRequest request) {

		LOG.info("Inside adminDashboard() method in admin controller");

		return JspResolver.ADMIN_DASHBOARD;
	}

	@RequestMapping(value = "/adminhierarchy")
	public String adminHierarchyPage(Model model, HttpServletRequest request) {

		LOG.info("Inside adminHierarchyPage() method in admin controller");

		List<OrganizationUnitSettings> companies = organizationManagementService.getAllCompaniesFromMongo();
		model.addAttribute("companyList", companies);

		return JspResolver.ADMIN_HIERARCHY_VIEW;
	}

	@RequestMapping(value = "/companyhierarchy")
	public String companyHierarchyView(Model model, HttpServletRequest request) {

		LOG.info("Inside companyHierarchyView() method in admin controller");

		String companyIdStr = request.getParameter("companyId");
		List<RegionFromSearch> regions = null;
		List<BranchFromSearch> branches = null;
		List<UserFromSearch> users = null;
		int start = 0;

		HttpSession session = request.getSession();

		try {
			long companyId = 0;
			try {
				companyId = Long.parseLong(companyIdStr);
			}
			catch (NumberFormatException e) {
				throw new NonFatalException("Invalid company id was passed", e);
			}

			Company company = organizationManagementService.getCompanyById(companyId);

			int regionCount = (int) solrSearchService.getRegionsCount("*", company, null);
			String regionsJson = solrSearchService.searchRegions("*", company, null, start, regionCount);
			Type searchedRegionsList = new TypeToken<List<RegionFromSearch>>() {}.getType();
			regions = new Gson().fromJson(regionsJson, searchedRegionsList);

			Map<Long, RegionFromSearch> regionsInSession = null;
			try {
				regionsInSession = organizationManagementService.fetchRegionsMapByCompany(companyId);
			}
			catch (MalformedURLException e) {
				LOG.error("MalformedURLException while fetching regions. Reason : " + e.getMessage(), e);
				throw new NonFatalException("MalformedURLException while fetching regions", e);
			}
			session.setAttribute(CommonConstants.REGIONS_IN_SESSION, regionsInSession);

			try {
				Map<Long, BranchFromSearch> branchesInSession = organizationManagementService.fetchBranchesMapByCompany(companyId);
				session.setAttribute(CommonConstants.BRANCHES_IN_SESSION, branchesInSession);
			}
			catch (MalformedURLException e) {
				LOG.error("MalformedURLException while fetching branches. Reason : " + e.getMessage(), e);
				throw new NonFatalException("MalformedURLException while fetching branches", e);
			}

			try {
				LOG.debug("fetching branches under company from solr");
				branches = organizationManagementService.getBranchesUnderCompanyFromSolr(company, start);

				LOG.debug("fetching users under company from solr");
				users = organizationManagementService.getUsersUnderCompanyFromSolr(company, start);
			}
			catch (NoRecordsFetchedException e) {
				LOG.error("No records found for company branch or region, reason : " + e.getMessage());
				throw new NonFatalException("No defaul branch or region found for company " + company.getCompany(),
						DisplayMessageConstants.COMPANY_NOT_REGISTERD, e);
			}
			model.addAttribute("companyObj", company);

			model.addAttribute("regions", regions);
			model.addAttribute("branches", branches);
			model.addAttribute("individuals", users);

		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while fetching hierarchy view list main page Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.MESSAGE_HEADER;
		}
		return JspResolver.ADMIN_COMPANY_HIERARCHY;
	}

	@RequestMapping(value = "/fetchcompaniesbykey", method = RequestMethod.GET)
	public String fetchCompaniesByKey(Model model, HttpServletRequest request) {

		LOG.info("Inside fetchCompaniesByKey() method");

		String searchKey = request.getParameter("searchKey");

		if (searchKey == null) {
			searchKey = "";
		}

		List<OrganizationUnitSettings> unitSettings = organizationManagementService.getCompaniesByNameFromMongo(searchKey);

		model.addAttribute("companyList", unitSettings);

		return JspResolver.ADMIN_COMPANY_LIST;
	}

	@RequestMapping(value = "/fetchhierarchyviewbranchesforadmin", method = RequestMethod.GET)
	public String fetchHierarchyViewBranchesForRealTechAdmin(Model model, HttpServletRequest request) {
		LOG.info("Method fetchHierarchyViewBranches called in controller");
		String strRegionId = request.getParameter("regionId");
		String companyIdStr = request.getParameter("companyId");
		long regionId = 0l;
		long companyId = 0l;
		List<BranchFromSearch> branches = null;
		int start = 0;
		int rows = -1;
		try {
			try {
				regionId = Long.parseLong(strRegionId);
			}
			catch (NumberFormatException e) {
				throw new InvalidInputException("Error while parsing regionId in fetchHierarchyViewBranches.Reason : " + e.getMessage(),
						DisplayMessageConstants.GENERAL_ERROR, e);
			}
			try {
				companyId = Long.parseLong(companyIdStr);
			}
			catch (NumberFormatException e) {
				throw new InvalidInputException("Error while parsing company in fetchHierarchyViewBranches.Reason : " + e.getMessage(),
						DisplayMessageConstants.GENERAL_ERROR, e);
			}
			int branchCount = (int) solrSearchService.getBranchCountByRegion(regionId);
			String branchesJson = solrSearchService.searchBranchesByRegion(regionId, start, branchCount);
			LOG.debug("Fetched branch .branches json is :" + branchesJson);
			Type searchedBranchesList = new TypeToken<List<BranchFromSearch>>() {}.getType();
			branches = new Gson().fromJson(branchesJson, searchedBranchesList);

			Set<Long> regionIds = new HashSet<Long>();
			regionIds.add(regionId);
			LOG.debug("Fetching users under region:" + regionId);
			List<UserFromSearch> users = organizationManagementService.getUsersUnderRegionFromSolr(regionIds, start, rows);

			User admin = userManagementService.getCompanyAdmin(companyId);
			/**
			 * fetching admin details
			 */
			UserFromSearch adminUser = null;
			try {
				String adminUserDoc = JSONUtil.toJSON(solrSearchService.getUserByUniqueId(admin.getUserId()));
				Type searchedUser = new TypeToken<UserFromSearch>() {}.getType();
				adminUser = new Gson().fromJson(adminUserDoc.toString(), searchedUser);
			}
			catch (SolrException e) {
				LOG.error("SolrException while searching for user id. Reason : " + e.getMessage(), e);
				throw new NonFatalException("SolrException while searching for user id.", DisplayMessageConstants.GENERAL_ERROR, e);
			}

			users = userManagementService.checkUserCanEdit(admin, adminUser, users);

			model.addAttribute("branches", branches);
			model.addAttribute("individuals", users);
			model.addAttribute("regionId", regionId);
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while fetching branches in a region . Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.MESSAGE_HEADER;
		}
		LOG.info("Method fetchHierarchyViewBranches finished in controller. Returning : " + branches);
		return JspResolver.ADMIN_REGION_HIERARCHY;
	}

	@RequestMapping(value = "/fetchbranchusersforadmin", method = RequestMethod.GET)
	public String fetchHierarchyViewUsersForBranchForAdmin(Model model, HttpServletRequest request) {
		LOG.info("Method fetchHierarchyViewUsersForBranch called in Hierarchy management controller");
		String strBranchId = request.getParameter("branchId");
		String strRegionId = request.getParameter("regionId");
		String strCompanyId = request.getParameter("companyId");
		long branchId = 0l;
		long companyId = 01;
		int start = 0;
		try {
			try {
				branchId = Long.parseLong(strBranchId);
			}
			catch (NumberFormatException e) {
				throw new InvalidInputException("Error while parsing branchId in fetchHierarchyViewBranches.Reason : " + e.getMessage(),
						DisplayMessageConstants.GENERAL_ERROR, e);
			}
			try {
				companyId = Long.parseLong(strCompanyId);
			}
			catch (NumberFormatException e) {
				throw new InvalidInputException("Error while parsing companyId in fetchHierarchyViewBranches.Reason : " + e.getMessage(),
						DisplayMessageConstants.GENERAL_ERROR, e);
			}
			int userCount = (int) solrSearchService.getUsersCountByIden(branchId, CommonConstants.BRANCHES_SOLR, false);
			Collection<UserFromSearch> usersResult = solrSearchService.searchUsersByIden(branchId, CommonConstants.BRANCHES_SOLR, false, start,
					userCount);
			String usersJson = new Gson().toJson(usersResult);
			LOG.debug("Solr result returned for users of branch is:" + usersJson);
			/**
			 * convert users to Object
			 */
			Type searchedUsersList = new TypeToken<List<UserFromSearch>>() {}.getType();
			List<UserFromSearch> usersList = new Gson().fromJson(usersJson, searchedUsersList);

			User admin = userManagementService.getCompanyAdmin(companyId);
			/**
			 * fetching admin details
			 */
			UserFromSearch adminUser = null;
			try {
				String adminUserDoc = JSONUtil.toJSON(solrSearchService.getUserByUniqueId(admin.getUserId()));
				Type searchedUser = new TypeToken<UserFromSearch>() {}.getType();
				adminUser = new Gson().fromJson(adminUserDoc.toString(), searchedUser);
			}
			catch (SolrException e) {
				LOG.error("SolrException while searching for user id. Reason : " + e.getMessage(), e);
				throw new NonFatalException("SolrException while searching for user id.", DisplayMessageConstants.GENERAL_ERROR, e);
			}

			usersList = userManagementService.checkUserCanEdit(admin, adminUser, usersList);

			model.addAttribute("users", usersList);
			model.addAttribute("branchId", branchId);
			model.addAttribute("regionId", strRegionId);
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while fetching users in a branch . Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.MESSAGE_HEADER;
		}

		LOG.info("Method fetchHierarchyViewUsersForBranch executed successfully");
		return JspResolver.ADMIN_BRANCH_HIERARCHY;

	}

	@ResponseBody
	@RequestMapping(value = "/loginadminas", method = RequestMethod.GET)
	public String loginAdminAsUser(Model model, HttpServletRequest request) {

		LOG.info("Inside loginAdminAsUser() method in admin controller");

		String columnName = request.getParameter("colName");
		String columnValue = request.getParameter("colValue");

		try {

			if (columnName == null || columnName.isEmpty()) {
				throw new InvalidInputException("Column name passed null/empty");
			}

			if (columnValue == null || columnValue.isEmpty()) {
				throw new InvalidInputException("Column value passed null/empty");
			}

			long id = 01;

			try {
				id = Long.parseLong(columnValue);
			}
			catch (NumberFormatException e) {
				throw new InvalidInputException("Invalid id was passed", e);
			}

			HttpSession session = request.getSession();
			User adminUser = sessionHelper.getCurrentUser();
			session.invalidate();

			User newUser = userManagementService.getUserByUserId(id);

			HttpSession newSession = request.getSession(true);
			newSession.setAttribute(CommonConstants.REALTECH_USER_ID, adminUser.getUserId());

			sessionHelper.loginAdminAs(newUser.getLoginName(), CommonConstants.BYPASS_PWD);

		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException occurred in loginAdminAsUser(), reason : " + e.getMessage());
		}
		return "success";
	}

	@ResponseBody
	@RequestMapping(value = "/switchtoadmin", method = RequestMethod.GET)
	public String switchToAdminUser(Model model, HttpServletRequest request) {

		HttpSession session = request.getSession();
		Long adminUserid = (Long) session.getAttribute(CommonConstants.REALTECH_USER_ID);

		// Logout current user
		session.invalidate();
		SecurityContextHolder.clearContext();

		session = request.getSession(true);

		try {
			User adminUser = userManagementService.getUserObjByUserId(adminUserid);

			if (!adminUser.isSuperAdmin()) {
				throw new InvalidInputException("Admin user in session is not realtech admin");
			}
			sessionHelper.loginAdminAs(adminUser.getLoginName(), CommonConstants.BYPASS_PWD);
		}
		catch (NonFatalException e) {
			LOG.error("Exception occurred in switchToAdminUser() method , reason : " + e.getMessage());
			return "failure";
		}
		return "success";
	}

}
