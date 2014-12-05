package com.realtech.socialsurvey.web.controller;

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
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.hierarchymanagement.HierarchyManagementService;
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

		LOG.info("Successfully completed method to showBuildHierarchyPage");
		return null;
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
		// get current session
		HttpSession session = request.getSession(false);
		// get current user in session
		User user = (User) session.getAttribute(CommonConstants.USER_IN_SESSION);

		// get the list of branches in company
		try {
			try {
				hierarchyManagementService.getAllBranchesForCompany(user.getCompany());

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
		// get current session
		HttpSession session = request.getSession(false);
		// get current user in session
		User user = (User) session.getAttribute(CommonConstants.USER_IN_SESSION);

		// get the list of regions in company
		try {
			try {
				hierarchyManagementService.getAllRegionsForCompany(user.getCompany());

				// TODO : set the region in list in either model attribute or send it back as JSON
			}
			catch (InvalidInputException e) {
				LOG.error("Error occurred while fetching the branch list in method getAllRegionsForCompany");
				throw new InvalidInputException("Error occurred while fetching the region list in method getAllBranchesForCompany",
						DisplayMessageConstants.GENERAL_ERROR, e);
			}
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while fetching all regions. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		LOG.info("Successfully deactivated the list of regions");
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
	public String decatvieRegion(Model model, HttpServletRequest request) {
		LOG.info("Deactive region");
		Long regionId = Long.parseLong(request.getParameter("regionId"));
		try {
			// Update the region status to inactive
			try {
				hierarchyManagementService.deleteRegion(regionId);
			}
			catch (InvalidInputException e) {
				LOG.error("Error occurred while deactivating the region");
				throw new InvalidInputException("Error occurred while deactivating the region", DisplayMessageConstants.GENERAL_ERROR, e);
			}
			LOG.info("Successfully deactived the branch " + regionId);
			model.addAttribute("message",
					messageUtils.getDisplayMessage(DisplayMessageConstants.REGION_DELETE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE));
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while sending the reset password link. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		return JspResolver.MESSAGE_HEADER;
	}

	/**
	 * Deactivates a region status
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/deactivatebranch", method = RequestMethod.POST)
	public String decatvieBranch(Model model, HttpServletRequest request) {
		LOG.info("Deactive region");
		Long branchId = Long.parseLong(request.getParameter("branchId"));
		try {
			// update the branch status to inactive
			try {
				hierarchyManagementService.deleteBranch(branchId);
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
