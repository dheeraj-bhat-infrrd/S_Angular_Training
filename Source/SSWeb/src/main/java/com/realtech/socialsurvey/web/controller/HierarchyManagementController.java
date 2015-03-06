package com.realtech.socialsurvey.web.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.google.gson.Gson;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.BranchSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.organizationmanagement.HierarchyManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.common.JspResolver;

// JIRA SS-37 BY RM02 BOC
/**
 * Controller to manage hierarchy
 */
@Controller
public class HierarchyManagementController {

	private static final Logger LOG = LoggerFactory.getLogger(HierarchyManagementController.class);

	@Autowired
	private MessageUtils messageUtils;
	@Autowired
	private HierarchyManagementService hierarchyManagementService;
	@Autowired
	private OrganizationManagementService organizationManagementService;
	@Autowired
	private SolrSearchService solrSearchService;
	@Autowired
	private OrganizationUnitSettingsDao organizationUnitSettingsDao;
	@Autowired
	private SessionHelper sessionHelper;

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
		HttpSession session = request.getSession(false);
		User user = sessionHelper.getCurrentUser();
		AccountType accountType = (AccountType) session.getAttribute(CommonConstants.ACCOUNT_TYPE_IN_SESSION);
		boolean isRegionAdditionAllowed = false;
		boolean isBranchAdditionAllowed = false;

		try {
			try {
				LOG.debug("Calling service for checking the status of regions already added");
				isRegionAdditionAllowed = hierarchyManagementService.isRegionAdditionAllowed(user, accountType);
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException("InvalidInputException while checking for max region addition. Reason : " + e.getMessage(),
						DisplayMessageConstants.GENERAL_ERROR, e);
			}

			try {
				LOG.debug("Calling service for checking the status of branches already added");
				isBranchAdditionAllowed = hierarchyManagementService.isBranchAdditionAllowed(user, accountType);
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException("InvalidInputException while checking for max region addition. Reason : " + e.getMessage(),
						DisplayMessageConstants.GENERAL_ERROR, e);
			}

			model.addAttribute("isRegionAdditionAllowed", isRegionAdditionAllowed);
			model.addAttribute("isBranchAdditionAllowed", isBranchAdditionAllowed);
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException in showBuildHierarchyPage. Reason:" + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.MESSAGE_HEADER;
		}

		LOG.info("Successfully completed method to showBuildHierarchyPage");
		return JspResolver.HIERARCHY_MANAGEMENT;
	}

	/**
	 * Fetch the list of branches for the company
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/fetchallbranches", method = RequestMethod.GET)
	public String fetchAllBranches(Model model, HttpServletRequest request) {

		LOG.info("Fetching all the branches for current user");
		HttpSession session = request.getSession(false);
		User user = sessionHelper.getCurrentUser();
		AccountType accountType = (AccountType) session.getAttribute(CommonConstants.ACCOUNT_TYPE_IN_SESSION);
		String jspToReturn = null;

		try {
			try {
				LOG.debug("Calling service to get the list of branches in company");
				List<Branch> branches = hierarchyManagementService.getAllBranchesForCompany(user.getCompany());
				LOG.debug("Successfully executed service to get the list of branches in company : " + branches);

				model.addAttribute("branches", branches);
				/**
				 * UI for enterprise branches and regions is different hence deciding which jsp to
				 * return
				 */
				if (accountType == AccountType.ENTERPRISE) {
					jspToReturn = JspResolver.EXISTING_ENTERPRISE_BRANCHES;
				}
				else {
					jspToReturn = JspResolver.EXISTING_BRANCHES;
				}
			}
			catch (InvalidInputException e) {
				LOG.error("Error occurred while fetching the branch list in method getAllBranchesForCompany");
				throw new InvalidInputException("Error occurred while fetching the branch list in method getAllBranchesForCompany",
						DisplayMessageConstants.GENERAL_ERROR, e);
			}
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while fetching all branches. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.MESSAGE_HEADER;
		}
		return jspToReturn;
	}

	/**
	 * Fetch the list of regions for the company
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/fetchallregions", method = RequestMethod.GET)
	public String fetchAllRegions(Model model, HttpServletRequest request) {

		LOG.info("Fetching all the regions for current user");
		User user = sessionHelper.getCurrentUser();

		try {
			try {
				LOG.debug("Calling service to get the list of regions in company");
				List<Region> regions = hierarchyManagementService.getAllRegionsForCompany(user.getCompany());
				LOG.debug("Sucessfully executed service to get the list of regions in company : " + regions);

				model.addAttribute("regions", regions);
			}
			catch (InvalidInputException e) {
				LOG.error("Error occurred while fetching the regions list in method getAllRegionsForCompany");
				throw new InvalidInputException("Error occurred while fetching the region list in method getAllRegionsForCompany",
						DisplayMessageConstants.GENERAL_ERROR, e);
			}
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while fetching all regions. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.MESSAGE_HEADER;
		}
		LOG.info("Successfully fetched the list of regions");
		return JspResolver.EXISTING_ENTERPRISE_REGIONS;
	}

	/**
	 * Method to fetch all regions for selector
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/fetchregionsselector", method = RequestMethod.GET)
	public String fetchRegionsSelector(Model model, HttpServletRequest request) {
		LOG.info("Method fetchRegionsSelector called in HierarchyManagementController ");
		User user = sessionHelper.getCurrentUser();

		try {
			try {
				LOG.debug("Calling service to get the list of regions in company");
				List<Region> regions = hierarchyManagementService.getAllRegionsForCompany(user.getCompany());
				LOG.debug("Sucessfully executed service to get the list of regions in company : " + regions);

				model.addAttribute("regions", regions);

			}
			catch (InvalidInputException e) {
				LOG.error("Error occurred while fetching the regions list in method fetchRegionsSelector");
				throw new InvalidInputException("Error occurred while fetching the region list in method fetchRegionsSelector",
						DisplayMessageConstants.GENERAL_ERROR, e);
			}
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while fetchRegionsSelector. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.MESSAGE_HEADER;
		}
		LOG.info("Successfully fetched the list of regions");
		return JspResolver.REGIONS_AUTOCOMPLETE;
	}

	/**
	 * Deactivates a region status
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/deactivateregion", method = RequestMethod.POST)
	public String deactivateRegion(Model model, HttpServletRequest request) {
		LOG.info("Deactivating region");

		try {
			long regionId = 0l;
			User user = sessionHelper.getCurrentUser();

			try {
				regionId = Long.parseLong(request.getParameter("regionId"));
				LOG.debug("Calling service to deactivate region");
				hierarchyManagementService.updateRegionStatus(user, regionId, CommonConstants.STATUS_INACTIVE);
				LOG.debug("Successfully executed service to deactivate region");
			}
			catch (NumberFormatException e) {
				LOG.error("Number format exception occurred while parsing the region id.Reason :" + e.getMessage(), e);
				throw new InvalidInputException("Number format exception occurred while parsing the region id",
						DisplayMessageConstants.GENERAL_ERROR, e);
			}
			catch (InvalidInputException e) {
				LOG.error("Error occurred while deactivating the region", e);
				throw new InvalidInputException("Error occurred while deactivating the region", DisplayMessageConstants.GENERAL_ERROR, e);
			}
			LOG.info("Successfully deactivated the region " + regionId);
			model.addAttribute("message",
					messageUtils.getDisplayMessage(DisplayMessageConstants.REGION_DELETE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE));
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while deactivating the region. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		return JspResolver.MESSAGE_HEADER;
	}

	/**
	 * Check for associated branches for the region
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/checkbranchesinregion", method = RequestMethod.POST)
	public String checkBranchesInRegion(Model model, HttpServletRequest request) {
		LOG.info("Fetching all the branches for current region");
		String messageToReturn = null;

		try {
			long regionId = 0l;
			try {
				regionId = Long.parseLong(request.getParameter("regionId"));
				LOG.debug("Calling service to get the count of branches in region");
				long branchCount = hierarchyManagementService.getCountBranchesInRegion(regionId);
				LOG.debug("Successfully executed service to get the count of branches in region : " + branchCount);

				if (branchCount > 0l) {
					model.addAttribute("message", messageUtils.getDisplayMessage("BRANCH_MAPPING_EXISTS", DisplayMessageType.ERROR_MESSAGE));
					messageToReturn = JspResolver.MESSAGE_HEADER;
				}
				else {
					model.addAttribute("message", messageUtils.getDisplayMessage("REGION_CAN_DELETE", DisplayMessageType.SUCCESS_MESSAGE));
					messageToReturn = JspResolver.MESSAGE_HEADER;
				}
			}
			catch (InvalidInputException e) {
				LOG.error("Error occurred while fetching the branch count in method checkBranchesInRegion");
				throw new InvalidInputException("Error occurred while fetching the branch count in method checkBranchesInRegion",
						DisplayMessageConstants.GENERAL_ERROR, e);
			}
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while fetching branch count for region. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			messageToReturn = JspResolver.MESSAGE_HEADER;
		}
		return messageToReturn;
	}

	/**
	 * Deactivates a branch status
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/deactivatebranch", method = RequestMethod.POST)
	public String deactivateBranch(Model model, HttpServletRequest request) {
		LOG.info("Deactivating branch");
		try {
			long branchId = 0l;
			User user = sessionHelper.getCurrentUser();
			try {
				branchId = Long.parseLong(request.getParameter("branchId"));

				LOG.debug("Calling service to deactivate branch");
				hierarchyManagementService.updateBranchStatus(user, branchId, CommonConstants.STATUS_INACTIVE);
				LOG.debug("Successfully executed service to deactivate branch");
			}
			catch (NumberFormatException e) {
				LOG.error("Number format exception occurred while parsing branch id", e);
				throw new InvalidInputException("Number format exception occurred while parsing branch id", DisplayMessageConstants.GENERAL_ERROR, e);
			}
			catch (InvalidInputException e) {
				LOG.error("Error occurred while deactivating the branch", e);
				throw new InvalidInputException("Error occurred while deactivating the branch", DisplayMessageConstants.GENERAL_ERROR, e);
			}
			LOG.info("Successfully deactived the branch " + branchId);
			model.addAttribute("message",
					messageUtils.getDisplayMessage(DisplayMessageConstants.BRANCH_DELETE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE));
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while deactivating a branch. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		return JspResolver.MESSAGE_HEADER;
	}

	/**
	 * Check for associated user profiles for the branch
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/checkusersinbranch", method = RequestMethod.POST)
	public String checkUsersInBranch(Model model, HttpServletRequest request) {
		LOG.info("Fetching count of users for current branch");
		String messageToReturn = null;

		try {
			long branchId = 0l;
			try {
				branchId = Long.parseLong(request.getParameter("branchId"));
				LOG.debug("Calling service to get the count of users in branch");
				long usersCount = hierarchyManagementService.getCountUsersInBranch(branchId);
				LOG.debug("Successfully executed service to get the count of users in branch : " + usersCount);

				if (usersCount > 0l) {
					model.addAttribute("message", messageUtils.getDisplayMessage("USER_MAPPING_EXISTS", DisplayMessageType.ERROR_MESSAGE));
					messageToReturn = JspResolver.MESSAGE_HEADER;
				}
				else {
					model.addAttribute("message", messageUtils.getDisplayMessage("BRANCH_CAN_DELETE", DisplayMessageType.SUCCESS_MESSAGE));
					messageToReturn = JspResolver.MESSAGE_HEADER;
				}
			}
			catch (InvalidInputException e) {
				LOG.error("Error occurred while fetching the users count in method checkUsersInBranch");
				throw new InvalidInputException("Error occurred while fetching the users count in method checkUsersInBranch",
						DisplayMessageConstants.GENERAL_ERROR, e);
			}
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while fetching users count for branch. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			messageToReturn = JspResolver.MESSAGE_HEADER;
		}
		return messageToReturn;
	}

	/**
	 * Method to add a new region
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "addregion", method = RequestMethod.POST)
	public String addRegion(Model model, HttpServletRequest request) {
		LOG.info("Method to add a region called in controller");
		try {
			String regionName = request.getParameter("regionName");
			String regionAddress1 = request.getParameter("regionAddress1");
			String regionAddress2 = request.getParameter("regionAddress2");

			validateRegionForm(regionName, regionAddress1);

			User user = sessionHelper.getCurrentUser();
			String address = getCompleteAddress(regionAddress1, regionAddress2);
			LOG.info("Address " + address + " is yet to be stored");

			LOG.debug("Calling service to add a new region");
			try {
				hierarchyManagementService.addNewRegion(user, regionName, CommonConstants.NO, regionAddress1, regionAddress2);

				model.addAttribute("message",
						messageUtils.getDisplayMessage(DisplayMessageConstants.REGION_ADDTION_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE));
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException("InvalidInputException occured while adding new region.REason : " + e.getMessage(),
						DisplayMessageConstants.GENERAL_ERROR, e);
			}

		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while adding a branch. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		LOG.info("Successfully comppleted method to add a region in controller");
		return JspResolver.MESSAGE_HEADER;
	}

	/**
	 * Method to add a new branch
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "addbranch", method = RequestMethod.POST)
	public String addBranch(Model model, HttpServletRequest request) {
		LOG.info("Method to add a branch called in controller");
		try {
			String branchName = request.getParameter("branchName");
			String branchAddress1 = request.getParameter("branchAddress1");
			String branchAddress2 = request.getParameter("branchAddress2");
			String strRegionId = request.getParameter("regionId");

			validateBranchForm(branchName, branchAddress1);

			long regionId = 0l;
			try {
				/**
				 * parse the regionId if a region is selected for the branch
				 */
				if (strRegionId != null && !strRegionId.isEmpty()) {
					regionId = Long.parseLong(strRegionId);
				}
			}
			catch (NumberFormatException e) {
				throw new InvalidInputException("NumberFormatException while parsing regionId", DisplayMessageConstants.GENERAL_ERROR, e);
			}

			User user = sessionHelper.getCurrentUser();
			String address = getCompleteAddress(branchAddress1, branchAddress2);
			LOG.info("Address " + address + " is yet to be stored");

			try {
				LOG.debug("Calling service to add a new branch");
				hierarchyManagementService.addNewBranch(user, regionId, CommonConstants.NO, branchName, branchAddress1, branchAddress2);
				LOG.debug("Successfully executed service to add a new branch");

				model.addAttribute("message",
						messageUtils.getDisplayMessage(DisplayMessageConstants.BRANCH_ADDITION_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE));
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException("InvalidInputException occured while adding new branch.REason : " + e.getMessage(),
						DisplayMessageConstants.GENERAL_ERROR, e);
			}
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while adding a branch. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		LOG.info("Successfully comppleted controller to add a branch");
		return JspResolver.MESSAGE_HEADER;

	}

	/**
	 * Method to update a branch
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/updatebranch", method = RequestMethod.POST)
	public String updateBranch(Model model, HttpServletRequest request) {
		LOG.info("Method updateBranch called in HierarchyManagementController");
		String strBranchId = request.getParameter("branchId");
		String branchName = request.getParameter("branchName");
		String strRegionId = request.getParameter("regionId");
		String branchAddress1 = request.getParameter("branchAddress1");
		String branchAddress2 = request.getParameter("branchAddress2");
		try {
			validateBranchForm(branchName, branchAddress1);
			long regionId = 0l;
			try {
				/**
				 * parse the regionId if a region is selected for the branch
				 */
				if (strRegionId != null && !strRegionId.isEmpty()) {
					regionId = Long.parseLong(strRegionId);
				}
			}
			catch (NumberFormatException e) {
				throw new InvalidInputException("Error while parsing regionId in update branch.Reason : " + e.getMessage(),
						DisplayMessageConstants.INVALID_REGION_SELECTED, e);
			}

			long branchId = 0l;
			try {
				branchId = Long.parseLong(strBranchId);
			}
			catch (NumberFormatException e) {
				throw new InvalidInputException("Error while parsing branchId in update branch.Reason : " + e.getMessage(),
						DisplayMessageConstants.GENERAL_ERROR, e);
			}

			User user = sessionHelper.getCurrentUser();
			String address = getCompleteAddress(branchAddress1, branchAddress2);
			LOG.info("Address " + address + " is yet to be stored");

			try {
				LOG.debug("Calling service to update branch with Id : " + branchId);
				hierarchyManagementService.updateBranch(branchId, regionId, branchName, branchAddress1, branchAddress2, user);
				LOG.debug("Successfully executed service to update a branch");

				model.addAttribute("message",
						messageUtils.getDisplayMessage(DisplayMessageConstants.BRANCH_UPDATION_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE));
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException("InvalidInputException occured while updating branch.Reason : " + e.getMessage(),
						DisplayMessageConstants.GENERAL_ERROR, e);
			}
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while updating branch. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		LOG.info("Method to update branch completed successfully");
		return JspResolver.MESSAGE_HEADER;
	}

	/**
	 * Method to update a region
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/updateregion", method = RequestMethod.POST)
	public String updateRegion(Model model, HttpServletRequest request) {
		LOG.info("Method updateRegion called in HierarchyManagementController");
		String regionName = request.getParameter("regionName");
		String strRegionId = request.getParameter("regionId");
		String regionAddress1 = request.getParameter("regionAddress1");
		String regionAddress2 = request.getParameter("regionAddress2");
		try {
			validateRegionForm(regionName, regionAddress1);
			long regionId = 0l;
			try {
				regionId = Long.parseLong(strRegionId);
			}
			catch (NumberFormatException e) {
				throw new InvalidInputException("Error while parsing regionId in update region.Reason : " + e.getMessage(),
						DisplayMessageConstants.GENERAL_ERROR, e);
			}

			User user = sessionHelper.getCurrentUser();
			String address = getCompleteAddress(regionAddress1, regionAddress2);
			LOG.info("Address " + address + " is yet to be stored");

			try {
				LOG.debug("Calling service to update region with Id : " + regionId);
				hierarchyManagementService.updateRegion(regionId, regionName, regionAddress1, regionAddress2, user);
				LOG.debug("Successfully executed service to update a region");

				model.addAttribute("message",
						messageUtils.getDisplayMessage(DisplayMessageConstants.REGION_UPDATION_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE));
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException("InvalidInputException occured while updating region.Reason : " + e.getMessage(),
						DisplayMessageConstants.GENERAL_ERROR, e);
			}
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while updating region. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		LOG.info("Method to update region completed successfully");
		return JspResolver.MESSAGE_HEADER;
	}

	/**
	 * Method to fetch regions from solr for a given pattern, if no pattern is provided fetches all
	 * the regions for logged in user's company
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/searchregions", method = RequestMethod.GET)
	public String searchRegions(Model model, HttpServletRequest request) {
		LOG.info("Method to search region called in controller");
		String regionPattern = request.getParameter("regionPattern");
		User user = sessionHelper.getCurrentUser();
		String searchRegionJson = null;
		String strStart = request.getParameter("start");
		String strRows = request.getParameter("rows");
		int start = 0;
		int rows = -1;
		try {
			if (regionPattern == null || regionPattern.isEmpty()) {
				regionPattern = "*";
			}
			/**
			 * if start index is present in request, parse and use it else use the default start
			 * index
			 */
			if (strStart != null && !strStart.isEmpty()) {
				try {
					start = Integer.parseInt(strStart);
				}
				catch (NumberFormatException e) {
					LOG.error("Number format exception while parsing start index value" + strStart + ".Reason :" + e.getMessage(), e);
				}
			}
			/**
			 * if number of rows is present in request, parse and use it else fetch default number
			 * of rows
			 */
			if (strRows != null && !strRows.isEmpty()) {
				try {
					rows = Integer.parseInt(strRows);
				}
				catch (NumberFormatException e) {
					LOG.error("Number format exception while parsing rows value" + strRows + ".Reason :" + e.getMessage(), e);
				}
			}

			try {
				LOG.debug("Calling solr search service to get the regions");
				searchRegionJson = solrSearchService.searchRegions(regionPattern, user.getCompany(), start, rows + 1);
				LOG.debug("Calling solr search service to get the regions");
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e);
			}

		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while searching regions. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}

		LOG.info("Method to search region completed successfully. Returning json : " + searchRegionJson);
		return searchRegionJson;
	}

	/**
	 * Method to fetch branches from solr for a given pattern, if no pattern is provided fetches all
	 * the branches for logged in user's company
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/searchbranches", method = RequestMethod.GET)
	public String searchBranches(Model model, HttpServletRequest request) {
		LOG.info("Method to search branches called in controller");
		String branchPattern = request.getParameter("branchPattern");
		User user = sessionHelper.getCurrentUser();
		String searchBranchJson = null;
		String strStart = request.getParameter("start");
		String strRows = request.getParameter("rows");
		int start = 0;
		int rows = -1;
		try {
			if (branchPattern == null || branchPattern.isEmpty()) {
				branchPattern = "*";
			}
			/**
			 * if start index is present in request, parse and use it else use the default start
			 * index
			 */
			if (strStart != null && !strStart.isEmpty()) {
				try {
					start = Integer.parseInt(strStart);
				}
				catch (NumberFormatException e) {
					LOG.error("Number format exception while parsing start index value" + strStart + ".Reason :" + e.getMessage(), e);
				}
			}
			/**
			 * if number of rows is present in request, parse and use it else fetch default number
			 * of rows
			 */
			if (strRows != null && !strRows.isEmpty()) {
				try {
					rows = Integer.parseInt(strRows);
				}
				catch (NumberFormatException e) {
					LOG.error("Number format exception while parsing rows value" + strRows + ".Reason :" + e.getMessage(), e);
				}
			}
			try {
				LOG.debug("Calling solr search service to get the branches");
				searchBranchJson = solrSearchService.searchBranches(branchPattern, user.getCompany(), start, rows + 1);
				LOG.debug("Calling solr search service to get the branches");
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e);
			}
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while searching branches. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}

		LOG.info("Method to search branches completed successfully.");
		return searchBranchJson;
	}

	/**
	 * Method to fetch a region details based on id
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/fetchregiontoupdate", method = RequestMethod.GET)
	public String fetchRegionToUpdate(Model model, HttpServletRequest request) {
		LOG.info("Method fetchRegionToUpdate called in controller");
		String strRegionId = request.getParameter("regionId");
		long regionId = 0l;
		String regionToUpdateJson = null;
		try {
			try {
				regionId = Long.parseLong(strRegionId);
			}
			catch (NumberFormatException e) {
				throw new InvalidInputException("Error while parsing regionId in fetchRegionToUpdate.Reason : " + e.getMessage(),
						DisplayMessageConstants.GENERAL_ERROR, e);
			}
			LOG.debug("Calling service to fetch region settings");
			OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings(regionId);
			regionToUpdateJson = new Gson().toJson(regionSettings);
			LOG.debug("Fetched region .regionToUpdateJson is :" + regionToUpdateJson);
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while fetching Region To Update. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}

		LOG.info("Method fetchRegionToUpdate finished in controller");
		return regionToUpdateJson;

	}

	/**
	 * Method to fetch branch along with settings for update
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/fetchbranchtoupdate", method = RequestMethod.GET)
	public String fetchBranchToUpdate(Model model, HttpServletRequest request) {
		LOG.info("Method fetchBranchToUpdate called in controller");
		String strBranchId = request.getParameter("branchId");
		long branchId = 0l;
		String branchToUpdateJson = null;
		try {
			try {
				branchId = Long.parseLong(strBranchId);
			}
			catch (NumberFormatException e) {
				throw new InvalidInputException("Error while parsing branchId in fetchBranchToUpdate.Reason : " + e.getMessage(),
						DisplayMessageConstants.GENERAL_ERROR, e);
			}
			LOG.debug("Calling service to fetch branch settings");
			BranchSettings branchSettings = organizationManagementService.getBranchSettings(branchId);
			branchToUpdateJson = new Gson().toJson(branchSettings);
			LOG.debug("Fetched branch .branchToUpdateJson is :" + branchToUpdateJson);
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while fetching branch To Update. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		LOG.info("Method fetchBranchToUpdate finished in controller. Returning : " + branchToUpdateJson);
		return branchToUpdateJson;

	}

	/**
	 * Method to get complete address from address lines
	 * 
	 * @param address1
	 * @param address2
	 * @return
	 */
	private String getCompleteAddress(String address1, String address2) {
		LOG.debug("Getting complete address for address1 : " + address1 + " and address2 : " + address2);
		String address = address1;
		/**
		 * if address line 2 is present, append it to address1 else the complete address is address1
		 */
		if (address1 != null && !address1.isEmpty() && address2 != null && !address2.isEmpty()) {
			address = address1 + " " + address2;
		}
		LOG.debug("Returning complete address" + address);
		return address;
	}

	/**
	 * Method to validate branch addition/updation form
	 * 
	 * @param branchName
	 * @param branchAddress1
	 * @throws InvalidInputException
	 */
	private void validateBranchForm(String branchName, String branchAddress1) throws InvalidInputException {
		LOG.debug("Validating branch add/update form");
		if (branchName == null || branchName.isEmpty()) {
			throw new InvalidInputException("Branch name is invalid while updating branch", DisplayMessageConstants.INVALID_BRANCH_NAME);
		}
		if (branchAddress1 == null || branchAddress1.isEmpty()) {
			throw new InvalidInputException("Branch address is invalid while adding branch", DisplayMessageConstants.INVALID_BRANCH_ADDRESS);
		}
		LOG.debug("Successsfully validated branch add/update form");
	}

	/**
	 * Method to validate add/update region form
	 * 
	 * @param regionName
	 * @param regionAddress1
	 * @throws InvalidInputException
	 */
	private void validateRegionForm(String regionName, String regionAddress1) throws InvalidInputException {
		LOG.debug("Validating region add/update form");
		if (regionName == null || regionName.isEmpty()) {
			throw new InvalidInputException("Region name is invalid while adding region", DisplayMessageConstants.INVALID_REGION_NAME);
		}
		if (regionAddress1 == null || regionAddress1.isEmpty()) {
			throw new InvalidInputException("Region address is invalid while adding region", DisplayMessageConstants.INVALID_REGION_ADDRESS);
		}
		LOG.debug("Validating region add/update form");
	}
}
// JIRA SS-37 BY RM02 EOC
