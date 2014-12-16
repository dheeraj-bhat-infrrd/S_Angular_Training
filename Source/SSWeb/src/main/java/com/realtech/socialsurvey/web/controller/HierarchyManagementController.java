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
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
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
			HttpSession session = request.getSession(false);
			User user = (User) session.getAttribute(CommonConstants.USER_IN_SESSION);

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
			HttpSession session = request.getSession(false);
			User user = (User) session.getAttribute(CommonConstants.USER_IN_SESSION);
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

			if (regionName == null || regionName.isEmpty()) {
				throw new InvalidInputException("Region name is invalid while adding region", DisplayMessageConstants.INVALID_REGION_NAME);
			}
			if (regionAddress1 == null || regionAddress2.isEmpty()) {
				throw new InvalidInputException("Region address is invalid while adding region", DisplayMessageConstants.INVALID_REGION_ADDRESS);
			}
			HttpSession session = request.getSession(false);
			User user = (User) session.getAttribute(CommonConstants.USER_IN_SESSION);

			String address = getCompleteAddress(regionAddress1, regionAddress2);
			// TODO store address in database

			LOG.debug("Calling service to add a new region");
			try {
				hierarchyManagementService.addNewRegion(user, regionName);
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

			if (branchName == null || branchName.isEmpty()) {
				throw new InvalidInputException("Branch name is invalid while adding branch", DisplayMessageConstants.INVALID_BRANCH_NAME);
			}
			if (branchAddress1 == null || branchAddress1.isEmpty()) {
				throw new InvalidInputException("Branch address is invalid while adding branch", DisplayMessageConstants.INVALID_BRANCH_ADDRESS);
			}
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
			HttpSession session = request.getSession(false);
			User user = (User) session.getAttribute(CommonConstants.USER_IN_SESSION);

			String address = getCompleteAddress(branchAddress1, branchAddress2);
			// TODO store address in database

			try {
				LOG.debug("Calling service to add a new branch");
				hierarchyManagementService.addNewBranch(user, regionId, branchName);
				LOG.debug("Successfully executed service to add a new branch");
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
}
// JIRA SS-37 BY RM02 EOC
