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
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.organizationmanagement.HierarchyManagementService;
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
	HierarchyManagementService hierarchyManagementService;

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
		User user = (User) session.getAttribute(CommonConstants.USER_IN_SESSION);
		AccountType accountType = (AccountType) session.getAttribute(CommonConstants.ACCOUNT_TYPE_IN_SESSION);
		boolean isRegionAdditionAllowed = false;
		boolean isBranchAdditionAllowed = false;

		try {
			try {
				LOG.debug("Calling service for checking the status of regions already added");
				isRegionAdditionAllowed = !hierarchyManagementService.isMaxRegionAdditionExceeded(user, accountType);
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException("InvalidInputException while checking for max region addition",
						DisplayMessageConstants.GENERAL_ERROR, e);
			}

			try {
				LOG.debug("Calling service for checking the status of branches already added");
				isBranchAdditionAllowed = !hierarchyManagementService.isMaxBranchAdditionExceeded(user, accountType);
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException("InvalidInputException while checking for max region addition",
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
		return JspResolver.BUILD_HIERARCHY;
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
		User user = (User) session.getAttribute(CommonConstants.USER_IN_SESSION);

		try {
			try {
				LOG.debug("Calling service to get the list of branches in company");
				List<Branch> branches = hierarchyManagementService.getAllBranchesForCompany(user.getCompany());
				LOG.debug("Successfully executed service to get the list of branches in company : " + branches);

				// TODO : set the branch in list in either model attribute or send it back as JSON
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
		}
		return JspResolver.MESSAGE_HEADER;
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
		HttpSession session = request.getSession(false);
		User user = (User) session.getAttribute(CommonConstants.USER_IN_SESSION);

		try {
			try {
				LOG.debug("Calling service to get the list of regions in company");
				List<Region> regions = hierarchyManagementService.getAllRegionsForCompany(user.getCompany());
				LOG.debug("Sucessfully executed service to get the list of regions in company : " + regions);

				// TODO : set the region in list in either model attribute or send it back as JSON
			}
			catch (InvalidInputException e) {
				LOG.error("Error occurred while fetching the regions list in method getAllRegionsForCompany");
				throw new InvalidInputException("Error occurred while fetching the region list in method getAllBranchesForCompany",
						DisplayMessageConstants.GENERAL_ERROR, e);
			}
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while fetching all regions. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		LOG.info("Successfully fetched the list of regions");
		return JspResolver.MESSAGE_HEADER;
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
			// Update the region status to inactive
			try {
				regionId = Long.parseLong(request.getParameter("regionId"));
				hierarchyManagementService.deleteRegion(regionId);
			}
			catch (NumberFormatException e) {
				LOG.error("Number format exception occurred while parsing the region id.Reason :" + e.getMessage(), e);
				throw new InvalidInputException("Number format exception occurred while parsing the region id",
						DisplayMessageConstants.GENERAL_ERROR, e);
			}
			catch (InvalidInputException e) {
				LOG.error("Error occurred while deactivating the region");
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
			// update the branch status to inactive
			try {
				branchId = Long.parseLong(request.getParameter("branchId"));
				hierarchyManagementService.deleteBranch(branchId);
			}
			catch (NumberFormatException e) {
				LOG.error("Number format exception occurred while parsing branch id");
				throw new InvalidInputException("Number format exception occurred while parsing branch id", DisplayMessageConstants.GENERAL_ERROR, e);
			}
			catch (InvalidInputException e) {
				LOG.error("Error occurred while deactivating the branch");
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
}
// JIRA SS-37 BY RM02 EOC
