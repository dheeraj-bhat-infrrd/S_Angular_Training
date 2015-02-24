package com.realtech.socialsurvey.web.controller;

import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.noggit.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.exception.UserAlreadyExistsException;
import com.realtech.socialsurvey.core.services.authentication.AuthenticationService;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.common.ErrorCodes;
import com.realtech.socialsurvey.web.common.ErrorMessages;
import com.realtech.socialsurvey.web.common.ErrorResponse;
import com.realtech.socialsurvey.web.common.JspResolver;

// JIRA SS-37 BY RM02 BOC

/**
 * Controller to manage users
 */
@Controller
public class UserManagementController {

	private static final Logger LOG = LoggerFactory.getLogger(UserManagementController.class);
	private final static int SOLR_BATCH_SIZE = 20;

	@Autowired
	private MessageUtils messageUtils;

	@Autowired
	private UserManagementService userManagementService;

	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	private URLGenerator urlGenerator;

	@Autowired
	private SessionHelper sessionHelper;

	@Autowired
	private SolrSearchService solrSearchService;

	// JIRA SS-42 BY RM05 BOC
	/*
	 * Method to show the User Management Page to a user on clicking UserManagement link.
	 */
	@RequestMapping(value = "/showusermangementpage", method = RequestMethod.GET)
	public String initUserManagementPage(Model model, HttpServletRequest request) {
		LOG.info("User Management page started");
		User user = sessionHelper.getCurrentUser();
		try {
			if (user == null) {
				LOG.error("No user found in session");
				throw new InvalidInputException("No user found in session", DisplayMessageConstants.NO_USER_IN_SESSION);
			}
			if (user.getStatus() != CommonConstants.STATUS_ACTIVE) {
				LOG.error("Inactive or unauthorized users can not access user management page");
				model.addAttribute("message",
						messageUtils.getDisplayMessage(DisplayMessageConstants.USER_MANAGEMENT_NOT_AUTHORIZED, DisplayMessageType.ERROR_MESSAGE));
			}
		}
		catch (NonFatalException nonFatalException) {
			LOG.error("NonFatalException in while inviting new user. Reason : " + nonFatalException.getMessage(), nonFatalException);
			model.addAttribute("message", messageUtils.getDisplayMessage(nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		return JspResolver.USER_MANAGEMENT;
	}

	/*
	 * Method to send invitation to a new user to join.
	 */
	@RequestMapping(value = "/invitenewuser", method = RequestMethod.POST)
	public String inviteNewUser(Model model, HttpServletRequest request) throws NumberFormatException, JSONException {
		LOG.info("Method to add a new user by existing admin called.");
		HttpSession session = request.getSession(false);
		User admin = sessionHelper.getCurrentUser();
		try {
			if (admin == null) {
				LOG.error("No user found in session");
				throw new InvalidInputException("No user found in session", DisplayMessageConstants.NO_USER_IN_SESSION);
			}
			String firstName = request.getParameter(CommonConstants.FIRST_NAME);
			String lastName = request.getParameter(CommonConstants.LAST_NAME);
			String emailId = request.getParameter(CommonConstants.EMAIL_ID);

			// form parameter validations for inviting new user
			if (firstName == null || firstName.isEmpty() || !firstName.matches(CommonConstants.FIRST_NAME_REGEX)) {
				LOG.error("First name invalid");
				throw new InvalidInputException("First name invalid", DisplayMessageConstants.INVALID_FIRSTNAME);
			}
			if (lastName != null && !lastName.isEmpty() && !lastName.matches(CommonConstants.LAST_NAME_REGEX)) {
				LOG.error("Last name invalid");
				throw new InvalidInputException("Last name invalid", DisplayMessageConstants.INVALID_LASTNAME);
			}
			if (emailId == null || emailId.isEmpty() || !emailId.matches(CommonConstants.EMAIL_REGEX)) {
				LOG.error("EmailId not valid");
				throw new InvalidInputException("EmailId not valid", DisplayMessageConstants.INVALID_EMAILID);
			}
			AccountType accountType = (AccountType) session.getAttribute(CommonConstants.ACCOUNT_TYPE_IN_SESSION);
			User user = null;
			try {
				if (userManagementService.isUserAdditionAllowed(admin)) {
					try {
						user = userManagementService.getUserByLoginName(admin, emailId);
						LOG.debug("User already exists in the company with the email id : " + emailId);
						model.addAttribute("existingUserId", user.getUserId());
						throw new UserAlreadyExistsException("User already exists with the email id : " + emailId);
					}
					catch (NoRecordsFetchedException noRecordsFetchedException) {
						LOG.debug("No records exist with the email id passed, inviting the new user");
						user = userManagementService.inviteNewUser(admin, firstName, lastName, emailId);
						LOG.debug("Adding user {} to solr server.", user.getFirstName());
						
						LOG.debug("Adding newly added user {} to mongo", user.getFirstName());
						userManagementService.insertAgentSettings(user);
						LOG.debug("Added newly added user {} to mongo", user.getFirstName());

						LOG.debug("Adding newly added user {} to solr", user.getFirstName());
						solrSearchService.addUserToSolr(user);
						LOG.debug("Added newly added user {} to solr", user.getFirstName());
						
						userManagementService.sendRegistrationCompletionLink(emailId, firstName, lastName, admin.getCompany().getCompanyId());

						// If account type is team assign user to default branch
						if (accountType.getValue() == CommonConstants.ACCOUNTS_MASTER_TEAM) {							
							String branches = solrSearchService.searchBranches("", admin.getCompany(),0,0);
							branches = branches.substring(1, branches.length() - 1);
							JSONObject defaultBranch = new JSONObject(branches);
							// assign new user to default branch in case of team account type
							userManagementService.assignUserToBranch(admin, user.getUserId(),
									Long.parseLong(defaultBranch.get(CommonConstants.BRANCH_ID_SOLR).toString()));
						}
					}
				}
				else {
					throw new InvalidInputException("Limit for maximum users has already reached.", DisplayMessageConstants.MAX_USERS_LIMIT_REACHED);
				}
			}
			catch (InvalidInputException e) {
				LOG.error("NonFatalException in inviteNewUser() while inviting new user. Reason : " + e.getMessage(), e);
				model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
				return JspResolver.MESSAGE_HEADER;
			}
			catch (UndeliveredEmailException e) {
				throw new UndeliveredEmailException(e.getMessage(), DisplayMessageConstants.REGISTRATION_INVITE_GENERAL_ERROR, e);
			}
			catch (UserAlreadyExistsException e) {
				throw new UserAlreadyExistsException(e.getMessage(), DisplayMessageConstants.EMAILID_ALREADY_TAKEN, e);
			}
			model.addAttribute("userId", user.getUserId());
			model.addAttribute("message",
					messageUtils.getDisplayMessage(DisplayMessageConstants.REGISTRATION_INVITE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE));
		}
		catch (NonFatalException nonFatalException) {
			LOG.error("NonFatalException in while inviting new user. Reason : " + nonFatalException.getMessage(), nonFatalException);
			model.addAttribute("message", messageUtils.getDisplayMessage(nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		LOG.info("Method to add a new user by existing admin finished.");
		return JspResolver.USER_ID_ON_INVITE;
	}

	/*
	 * Method to fetch list of branches a user is assigned to.
	 */
	@RequestMapping(value = "/finduserandbranchesbyuserid", method = RequestMethod.POST)
	public String findUserAndAssignedBranchesByUserId(Model model, HttpServletRequest request) {
		LOG.info("Method to fetch user by user, findUserByUserId() started.");
		try {
			String userIdStr = request.getParameter(CommonConstants.USER_ID);
			HttpSession session = request.getSession(false);
			User admin = sessionHelper.getCurrentUser();
			if (admin == null) {
				LOG.error("No user found in session");
				throw new InvalidInputException("No user found in session", DisplayMessageConstants.NO_USER_IN_SESSION);
			}
			AccountType accountType = (AccountType) session.getAttribute(CommonConstants.ACCOUNT_TYPE_IN_SESSION);
			int accountTypeVal = accountType.getValue();
			model.addAttribute("accounttypeval", accountTypeVal);
			if (userIdStr == null) {
				LOG.error("Invalid user id passed in method findUserByUserId().");
				throw new InvalidInputException("Invalid user id passed in method findUserByUserId().");
			}
			else if (userIdStr.isEmpty()) {
				return JspResolver.USER_DETAILS;
			}
			long userId = 0;
			try {
				userId = Long.parseLong(userIdStr);
			}
			catch (NumberFormatException e) {
				LOG.error("Number format exception while parsing user Id", e);
				throw new NonFatalException("Number format execption while parsing user id", DisplayMessageConstants.GENERAL_ERROR, e);
			}
			User user = userManagementService.getUserByUserId(userId);
			try {
				List<Branch> branches = userManagementService.getBranchesAssignedToUser(user);
				// Adding assigned branches to the model attribute as assignedBranches
				model.addAttribute("assignedBranches", branches);
			}
			catch (NoRecordsFetchedException e) {
				LOG.trace("No branch attched with the user " + userId);
			}
			model.addAttribute("searchedUser", user);

		}
		catch (NonFatalException nonFatalException) {
			LOG.error("NonFatalException while searching for user id. Reason : " + nonFatalException.getMessage(), nonFatalException);
			model.addAttribute("message", messageUtils.getDisplayMessage(nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.MESSAGE_HEADER;
		}
		LOG.info("Method to fetch user by user id , findUserByUserId() finished.");
		// return user details page on success
		return JspResolver.USER_DETAILS;
	}

	/*
	 * Method to fetch list of all the users who belong to the same as that of current user. Current
	 * user is company admin who can assign different roles to other users.
	 */
	@RequestMapping(value = "/findusersforcompany", method = RequestMethod.GET)
	@ResponseBody
	public String findUsersForCompany(Model model, HttpServletRequest request) {
		LOG.info("Method to fetch user by user, findUserByUserId() started.");
		String startIndexStr = request.getParameter("startIndex");
		String batchSizeStr = request.getParameter("batchSize");
		String users = "";
		int startIndex = 0;
		int batchSize = 0;
		try {
			User admin = sessionHelper.getCurrentUser();
			if (admin == null) {
				LOG.error("No user found in session");
				throw new InvalidInputException("No user found in session", DisplayMessageConstants.NO_USER_IN_SESSION);
			}
			if (startIndexStr == null || startIndexStr.isEmpty()) {
				LOG.error("Invalid value found in startIndex. It cannot be null or empty.");
				throw new InvalidInputException("Invalid value found in startIndex. It cannot be null or empty.");
			}
			if (batchSizeStr == null || batchSizeStr.isEmpty()) {
				LOG.error("Invalid value found in batchSizeStr. It cannot be null or empty.");
				batchSize = SOLR_BATCH_SIZE;
			}
			try {
				startIndex = Integer.parseInt(startIndexStr);
				batchSize = Integer.parseInt(batchSizeStr);
			}
			catch (NumberFormatException e) {
				LOG.error("NumberFormatException while searching for user id. Reason : " + e.getMessage(), e);
				throw new NonFatalException("NumberFormatException while searching for user id", e);
			}
			try {
				users = solrSearchService.searchUsersByCompany(admin.getCompany().getCompanyId(), startIndex, batchSize);
			}
			catch (MalformedURLException e) {
				LOG.error("MalformedURLException while searching for user id. Reason : " + e.getMessage(), e);
				throw new NonFatalException("MalformedURLException while searching for user id.", e);
			}
		}
		catch (NonFatalException nonFatalException) {
			LOG.error("NonFatalException while searching for user id. Reason : " + nonFatalException.getStackTrace(), nonFatalException);
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrCode(ErrorCodes.REQUEST_FAILED);
			errorResponse.setErrMessage(ErrorMessages.REQUEST_FAILED);
			String errorMessage = JSONUtil.toJSON(errorResponse);
			return errorMessage;
		}
		LOG.info("Method to fetch users by company , findUsersForCompany() finished.");
		// return user details page on success
		return users;
	}

	/*
	 * Method to find a user on the basis of email id provided.
	 */
	@RequestMapping(value = "/finduserbyemail", method = RequestMethod.GET)
	public @ResponseBody
	String findUserByEmail(Model model, HttpServletRequest request) {
		LOG.info("Method to find users by email id called.");
		String users = "";
		try {
			String searchKey = request.getParameter("searchKey");
			if (searchKey == null) {
				LOG.error("Invalid search key passed in method findUserByEmail().");
				throw new InvalidInputException("Invalid searchKey passed in method findUserByEmail().");
			}

			User user = sessionHelper.getCurrentUser();
			if (user == null) {
				LOG.error("No user found in current session in findUserByEmail().");
				throw new InvalidInputException("No user found in current session in findUserByEmail().");
			}

			try {
				users = solrSearchService.searchUsersByLoginNameOrName(searchKey, user.getCompany().getCompanyId());
			}
			catch (InvalidInputException invalidInputException) {
				throw new InvalidInputException(invalidInputException.getMessage(), invalidInputException);
			}
			catch (MalformedURLException e) {
				LOG.error("Error occured while searching for email id in findUserByEmail(). Reason is ", e);
				throw new NonFatalException("Error occured while searching for email id in findUserByEmail(). Reason is ", e);
			}
		}
		catch (NonFatalException nonFatalException) {
			LOG.error("NonFatalException while searching for user by email id id. Reason : " + nonFatalException.getMessage(), nonFatalException);
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrCode(ErrorCodes.REQUEST_FAILED);
			errorResponse.setErrMessage(ErrorMessages.REQUEST_FAILED);
			return JSONUtil.toJSON(errorResponse);
		}
		LOG.info("Method to find users by email id finished.");
		return users;
	}

	/*
	 * Method to remove an existing user. Soft delete is done.
	 */
	@RequestMapping(value = "/removeexistinguser", method = RequestMethod.POST)
	public String removeExistingUser(Model model, HttpServletRequest request) {
		LOG.info("Method to deactivate an existing user called.");
		try {

			long userIdToRemove = 0;
			try {
				userIdToRemove = Long.parseLong(request.getParameter("userIdToRemove"));
			}
			catch (NumberFormatException e) {
				LOG.error("Number format exception while parsing user Id", e);
				throw new NonFatalException("Number format execption while parsing user id", DisplayMessageConstants.GENERAL_ERROR, e);
			}
			if (userIdToRemove < 0) {
				LOG.error("Invalid user Id found to remove in removeExistingUser().");
				throw new InvalidInputException("Invalid user Id found to remove in removeExistingUser().",
						DisplayMessageConstants.NO_USER_IN_SESSION);
			}

			User user = sessionHelper.getCurrentUser();
			if (user == null) {
				LOG.error("No user found in current session in removeExistingUser().");
				throw new InvalidInputException("No user found in current session in removeExistingUser().");
			}
			try {
				userManagementService.removeExistingUser(user, userIdToRemove);
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.REGISTRATION_INVITE_GENERAL_ERROR, e);
			}
			LOG.debug("Removing user {} from solr.", userIdToRemove);
			solrSearchService.removeUserFromSolr(userIdToRemove);
			model.addAttribute("message",
					messageUtils.getDisplayMessage(DisplayMessageConstants.USER_DELETE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE));
		}
		catch (NonFatalException nonFatalException) {
			LOG.error("NonFatalException while removing user. Reason : " + nonFatalException.getMessage(), nonFatalException);
			model.addAttribute("message", messageUtils.getDisplayMessage(nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		LOG.info("Method to remove an existing user finished.");
		return JspResolver.MESSAGE_HEADER;
	}

	/*
	 * Method to assign a user to a branch.
	 */
	@RequestMapping(value = "/assignusertobranch", method = RequestMethod.POST)
	public String assignUserToBranch(Model model, HttpServletRequest request) {
		User admin = sessionHelper.getCurrentUser();
		String userIdStr = request.getParameter(CommonConstants.USER_ID);
		String branchIdStr = request.getParameter("branchId");
		try {
			if (admin == null) {
				LOG.error("No user found in session");
				throw new InvalidInputException("No user found in session", DisplayMessageConstants.NO_USER_IN_SESSION);
			}
			if (userIdStr == null || userIdStr.isEmpty()) {
				LOG.error("Invalid user id passed in method assignUserToBranch().");
				throw new InvalidInputException("Invalid user id passed in method assignUserToBranch().");
			}
			if (branchIdStr == null || branchIdStr.isEmpty()) {
				LOG.error("Invalid branch id passed in method assignUserToBranch().");
				throw new InvalidInputException("Invalid branch id passed in method assignUserToBranch().");
			}

			LOG.info("Method to assign user to branch is called for user " + userIdStr);
			long userId = 0;
			long branchId = 0;
			try {
				userId = Long.parseLong(userIdStr);
				branchId = Long.parseLong(branchIdStr);
			}
			catch (NumberFormatException e) {
				LOG.error("Number format exception while parsing user Id or branch id", e);
				throw new NonFatalException("Number format execption while parsing user id or branch id", DisplayMessageConstants.GENERAL_ERROR, e);
			}
			try {
				userManagementService.assignUserToBranch(admin, userId, branchId);
				model.addAttribute("message",
						messageUtils.getDisplayMessage(DisplayMessageConstants.BRANCH_ASSIGN_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE));
			}
			catch (InvalidInputException e) {
				model.addAttribute("message",
						messageUtils.getDisplayMessage(DisplayMessageConstants.BRANCH_ASSIGNING_NOT_AUTHORIZED, DisplayMessageType.ERROR_MESSAGE));
				return JspResolver.MESSAGE_HEADER;
			}
		}
		catch (NonFatalException e) {
			LOG.error("Exception occured while assigning user to a branch. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}

		LOG.info("Method to assign user to branch is finished for user " + userIdStr);
		return JspResolver.MESSAGE_HEADER;
	}

	@RequestMapping(value = "/unassignuserfrombranch", method = RequestMethod.POST)
	public String unassignUserFromBranch(Model model, HttpServletRequest request) {
		User admin = sessionHelper.getCurrentUser();
		String userIdStr = request.getParameter(CommonConstants.USER_ID);
		String branchIdStr = request.getParameter("branchId");
		try {
			if (admin == null) {
				LOG.error("No user found in session");
				throw new InvalidInputException("No user found in session", DisplayMessageConstants.NO_USER_IN_SESSION);
			}
			if (userIdStr == null || userIdStr.isEmpty()) {
				LOG.error("Invalid user id passed in method unAssignUserFromBranch().");
				throw new InvalidInputException("Invalid user id passed in method unAssignUserFromBranch().");
			}
			if (branchIdStr == null || branchIdStr.isEmpty()) {
				LOG.error("Invalid branch id passed in method assignUserToBranch().");
				throw new InvalidInputException("Invalid branch id passed in method unAssignUserFromBranch().");
			}

			LOG.info("Method to unassign user to branch is called for user " + userIdStr);
			long userId = 0;
			long branchId = 0;
			try {
				userId = Long.parseLong(userIdStr);
				branchId = Long.parseLong(branchIdStr);
			}
			catch (NumberFormatException e) {
				LOG.error("Number format exception while parsing user Id or branch id", e);
				throw new NonFatalException("Number format execption while parsing user id or branch id", DisplayMessageConstants.GENERAL_ERROR, e);
			}

			userManagementService.unassignUserFromBranch(admin, userId, branchId);
			model.addAttribute("message",
					messageUtils.getDisplayMessage(DisplayMessageConstants.BRANCH_UNASSIGN_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE));
		}
		catch (NonFatalException e) {
			LOG.error("Exception occured while unassigning user from a branch. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		LOG.info("Method to unassign user from branch is finished for user " + userIdStr);
		return JspResolver.MESSAGE_HEADER;
	}

	/**
	 * Method to assign a user as branch admin
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/assignorunassignbranchadmin", method = RequestMethod.POST)
	public String assignOrUnassignBranchAdmin(Model model, HttpServletRequest request) {
		LOG.info("Method to assign or unassign branch admin called");
		try {
			User admin = sessionHelper.getCurrentUser();
			if (admin == null) {
				LOG.error("No user found in session");
				throw new InvalidInputException("No user found in session", DisplayMessageConstants.NO_USER_IN_SESSION);
			}
			String branch = request.getParameter("branchId");
			String userToAssign = request.getParameter("userId");
			String isAssign = request.getParameter("isAssign");
			if (branch == null || branch.isEmpty()) {
				LOG.error("Null or empty value passed for branch in assignOrUnassignBranchAdmin()");
				throw new InvalidInputException("Null or empty value passed for branch in assignOrUnassignBranchAdmin()");
			}
			if (userToAssign == null || userToAssign.isEmpty()) {
				LOG.error("Null or empty value passed for user id in assignOrUnassignBranchAdmin()");
				throw new InvalidInputException("Null or empty value passed for user id in assignOrUnassignBranchAdmin()");
			}
			if (isAssign == null || isAssign.isEmpty()) {
				LOG.error("Null or empty value passed for check field isAssign in assignOrUnassignBranchAdmin()");
				throw new InvalidInputException("Null or empty value passed for user id in assignOrUnassignBranchAdmin()");
			}

			long branchId = 0l;
			long userId = 0l;
			try {
				branchId = Long.parseLong(branch);
				userId = Long.parseLong(userToAssign);
				if (isAssign.equalsIgnoreCase(CommonConstants.IS_ASSIGN_ADMIN))
					// Assigns the given user as branch admin
					userManagementService.assignBranchAdmin(admin, branchId, userId);
				else if (isAssign.equalsIgnoreCase(CommonConstants.IS_UNASSIGN_ADMIN))
					// Unassigns the given user as branch admin
					userManagementService.unassignBranchAdmin(admin, branchId, userId);
			}
			catch (NumberFormatException e) {
				LOG.error("Number format exception while parsing branch Id or user id", e);
				throw new NonFatalException("Number format execption while parsing branch Id or user id", DisplayMessageConstants.GENERAL_ERROR, e);
			}
		}
		// TODO add success message.
		catch (NonFatalException nonFatalException) {
			LOG.error("NonFatalException while trying to assign or unassign a user to branch. Reason : " + nonFatalException.getMessage(),
					nonFatalException);
			model.addAttribute("message", messageUtils.getDisplayMessage(nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		LOG.info("Successfully completed method to assign or unassign branch admin");
		return JspResolver.MESSAGE_HEADER;
	}

	// JIRA SS-42 BY RM05 EOC
	/**
	 * Method to assign a user as region admin.
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/assignregionadmin", method = RequestMethod.POST)
	public String assignRegionAdmin(Model model, HttpServletRequest request) {
		LOG.info("Method to assign region admin called");

		try {
			User admin = sessionHelper.getCurrentUser();
			if (admin == null) {
				LOG.error("No user found in session");
				throw new InvalidInputException("No user found in session", DisplayMessageConstants.NO_USER_IN_SESSION);
			}
			String region = request.getParameter("regionId");
			String userToAssign = request.getParameter("userId");

			if (region == null || region.isEmpty()) {
				LOG.error("Null or empty value passed for region id in assignRegionAdmin()");
				throw new InvalidInputException("Null or empty value passed for region id in assignRegionAdmin()");
			}
			if (userToAssign == null || userToAssign.isEmpty()) {
				LOG.error("Null or empty value passed for user id in assignRegionAdmin()");
				throw new InvalidInputException("Null or empty value passed for user id in assignRegionAdmin()");
			}

			long regionId = 0l;
			long userId = 0l;

			try {
				regionId = Long.parseLong(region);
				userId = Long.parseLong(userToAssign);
				// Assigns the given user as region admin
				userManagementService.assignRegionAdmin(admin, regionId, userId);
			}
			catch (NumberFormatException e) {
				LOG.error("Number format exception while parsing region Id or user id", e);
				throw new NonFatalException("Number format execption while parsing region Id or user id", DisplayMessageConstants.GENERAL_ERROR, e);
			}
		}
		// TODO add success message.
		catch (NonFatalException e) {
			LOG.error("Exception occured while assigning branch admin. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		LOG.info("Successfully completed method to assign region admin");
		return JspResolver.MESSAGE_HEADER;
	}

	/**
	 * Method to remove a region admin.
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/unassignregionadmin", method = RequestMethod.POST)
	public String unassignRegionAdmin(Model model, HttpServletRequest request) {

		LOG.info("Method to remove region admin called");

		try {
			User admin = sessionHelper.getCurrentUser();
			if (admin == null) {
				LOG.error("No user found in session");
				throw new InvalidInputException("No user found in session", DisplayMessageConstants.NO_USER_IN_SESSION);
			}
			String region = request.getParameter("regionId");
			String userIdToRemove = request.getParameter("userId");

			if (region == null || region.isEmpty()) {
				LOG.error("Null or empty value passed for region id in removeRegionAdmin()");
				throw new InvalidInputException("Null or empty value passed for region id in removeRegionAdmin()");
			}
			if (userIdToRemove == null || userIdToRemove.isEmpty()) {
				LOG.error("Null or empty value passed for user id in unassignRegionAdmin()");
				throw new InvalidInputException("Null or empty value passed for user id in unassignRegionAdmin()");
			}

			long regionId = 0l;
			long userId = 0l;

			try {
				regionId = Long.parseLong(region);
				userId = Long.parseLong(userIdToRemove);

				// Remove the given user from branch admin.
				userManagementService.unassignRegionAdmin(admin, regionId, userId);
			}
			catch (NumberFormatException e) {
				LOG.error("Number format exception while parsing region Id or user id", e);
				throw new NonFatalException("Number format execption while parsing region Id", DisplayMessageConstants.GENERAL_ERROR, e);
			}
		}
		// TODO add success message.
		catch (NonFatalException e) {
			LOG.error("Exception occured while assigning region admin.Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		LOG.info("Successfully completed method to remove region admin");
		return JspResolver.MESSAGE_HEADER;
	}

	/**
	 * Method to activate or deactivate a user.
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/updateuser", method = RequestMethod.POST)
	public String updateUser(Model model, HttpServletRequest request) {

		LOG.info("Method to activate or deactivate a user, activateOrDecativateUser() called.");
		try {
			User admin = sessionHelper.getCurrentUser();
			if (admin == null) {
				LOG.error("No user found in session");
				throw new InvalidInputException("No user found in session", DisplayMessageConstants.NO_USER_IN_SESSION);
			}
			String isAssign = request.getParameter("isAssign");
			if (isAssign == null || isAssign.isEmpty()) {
				LOG.error("Null or empty value passed for check field isAssign in assignOrUnassignBranchAdmin()");
				throw new InvalidInputException("Null or empty value passed for user id in assignOrUnassignBranchAdmin()");
			}
			long userIdToUpdate = 0;
			try {
				userIdToUpdate = Long.parseLong(request.getParameter("userIdToUpdate"));
			}
			catch (NumberFormatException e) {
				LOG.error("Number format exception while parsing user Id", e);
				throw new NonFatalException("Number format execption while parsing user id", DisplayMessageConstants.GENERAL_ERROR, e);
			}
			if (userIdToUpdate < 0) {
				LOG.error("Invalid user Id found to update in updateUser().");
				throw new InvalidInputException("Invalid user Id found to update in updateUser().");
			}

			try {
				if (isAssign.equalsIgnoreCase("YES"))
					// Set the given user as active.
					userManagementService.updateUser(admin, userIdToUpdate, true);
				else if (isAssign.equalsIgnoreCase("NO"))
					// Set the given user as inactive.
					userManagementService.updateUser(admin, userIdToUpdate, false);
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e);
			}
			model.addAttribute("message",
					messageUtils.getDisplayMessage(DisplayMessageConstants.USER_STATUS_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE));
		}
		catch (NonFatalException nonFatalException) {
			LOG.error("NonFatalException while removing user. Reason : " + nonFatalException.getMessage(), nonFatalException);
			model.addAttribute("message", messageUtils.getDisplayMessage(nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		LOG.info("Method to activate or deactivate a user, updateUser() finished.");
		return JspResolver.MESSAGE_HEADER;
	}

	/**
	 * Method to show registration completion page to the user. User can update first name, last
	 * name here. User must update the password for completion of registration.
	 * 
	 * @param model
	 * @param request
	 * @return
	 * @throws InvalidInputException
	 */
	@RequestMapping(value = "/showcompleteregistrationpage", method = RequestMethod.GET)
	public String showCompleteRegistrationPage(@RequestParam("q") String encryptedUrlParams, Model model) {

		LOG.info("Method showCompleteRegistrationPage() to complete registration of user started.");

		try {
			try {
				Map<String, String> urlParams = urlGenerator.decryptParameters(encryptedUrlParams);
				model.addAttribute(CommonConstants.COMPANY, urlParams.get(CommonConstants.COMPANY));
				model.addAttribute(CommonConstants.EMAIL_ID, urlParams.get(CommonConstants.EMAIL_ID));
				model.addAttribute(CommonConstants.FIRST_NAME, urlParams.get(CommonConstants.FIRST_NAME));
				model.addAttribute(CommonConstants.LAST_NAME, urlParams.get(CommonConstants.LAST_NAME));
			}
			catch (InvalidInputException e) {
				LOG.error("Invalid Input exception in decrypting url parameters in showCompleteRegistrationPage(). Reason " + e.getMessage(), e);
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e);
			}
			LOG.info("Method showCompleteRegistrationPage() to complete registration of user finished.");
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException in showCompleteRegistrationPage(). Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.MESSAGE_HEADER;
		}
		return JspResolver.COMPLETE_REGISTRATION;
	}

	/**
	 * Method to complete registration of the user.
	 * 
	 * @param model
	 * @param request
	 * @return
	 * @throws InvalidInputException
	 */
	@RequestMapping(value = "/completeregistration", method = RequestMethod.POST)
	public String completeRegistration(Model model, HttpServletRequest request) {
		LOG.info("Method completeRegistration() to complete registration of user started.");

		try {
			String firstName = request.getParameter(CommonConstants.FIRST_NAME);
			String lastName = request.getParameter(CommonConstants.LAST_NAME);
			String emailId = request.getParameter(CommonConstants.EMAIL_ID);
			String password = request.getParameter("password");
			String confirmPassword = request.getParameter("confirmPassword");
			String encryptedUrlParameters = request.getParameter("q");
			String companyIdStr = request.getParameter("companyId");
			Map<String, String> urlParams = new HashMap<>();
			User user = null;

			// form parameters validation
			if (firstName == null || firstName.isEmpty() || !firstName.matches(CommonConstants.FIRST_NAME_REGEX)) {
				LOG.error("First name invalid");
				throw new InvalidInputException("First name invalid", DisplayMessageConstants.INVALID_FIRSTNAME);
			}
			if (lastName != null && !lastName.isEmpty() && !lastName.matches(CommonConstants.LAST_NAME_REGEX)) {
				LOG.error("Last name invalid");
				throw new InvalidInputException("Last name invalid", DisplayMessageConstants.INVALID_LASTNAME);
			}
			if (emailId == null || emailId.isEmpty() || !emailId.matches(CommonConstants.EMAIL_REGEX)) {
				LOG.error("EmailId not valid");
				throw new InvalidInputException("EmailId not valid", DisplayMessageConstants.INVALID_EMAILID);
			}
			if (password == null || password.isEmpty() || !password.matches(CommonConstants.PASSWORD_REG_EX)) {
				LOG.error("Password passed was invalid");
				throw new InvalidInputException("Password passed was invalid", DisplayMessageConstants.INVALID_PASSWORD);
			}
			if (companyIdStr == null || companyIdStr.isEmpty()) {
				LOG.error("Company Id passed was null or empty");
				throw new InvalidInputException("Company Id passed was null or empty", DisplayMessageConstants.INVALID_COMPANY_NAME);
			}

			if (confirmPassword == null || confirmPassword.isEmpty()) {
				LOG.error("Confirm password passed was null or empty");
				throw new InvalidInputException("Confirm password passed was null or empty", DisplayMessageConstants.INVALID_PASSWORD);
			}

			// check if password and confirm password field match
			if (!password.equals(confirmPassword)) {
				LOG.error("Password and confirm password fields do not match");
				throw new InvalidInputException("Password and confirm password fields do not match", DisplayMessageConstants.PASSWORDS_MISMATCH);
			}
			// Decrypting URL parameters
			try {
				urlParams = urlGenerator.decryptParameters(encryptedUrlParameters);
			}
			catch (InvalidInputException e) {
				LOG.error("Invalid Input exception in decrypting Url. Reason " + e.getMessage(), e);
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e);
			}

			// check if email ID entered matches with the one in the encrypted url
			if (!urlParams.get("emailId").equals(emailId)) {
				LOG.error("Invalid Input exception. Reason emailId entered does not match with the one to which the mail was sent");
				throw new InvalidInputException("Invalid Input exception", DisplayMessageConstants.INVALID_EMAILID);
			}
			long companyId = 0;
			try {
				companyId = Long.parseLong(companyIdStr);
			}
			catch (NumberFormatException exception) {
				LOG.error("NumberFormat exception. Reason : " + exception.getStackTrace());
				throw new InvalidInputException("NumberFormat exception. Reason : " + exception.getStackTrace());
			}

			try {
				// fetch user object with email Id
				user = authenticationService.getUserWithLoginNameAndCompanyId(emailId, companyId);
				user.setFirstName(firstName);
				user.setLastName(lastName);
				user.setStatus(CommonConstants.STATUS_ACTIVE);
				user.setModifiedBy(String.valueOf(user.getUserId()));
				user.setModifiedOn(new Timestamp(System.currentTimeMillis()));
			}
			catch (InvalidInputException e) {
				LOG.error("Invalid Input exception in fetching user object. Reason " + e.getMessage(), e);
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.USER_NOT_PRESENT, e);
			}
			try {
				// change user's password
				authenticationService.changePassword(user, password);
			}
			catch (InvalidInputException e) {
				LOG.error("Invalid Input exception in changing the user's password. Reason " + e.getMessage(), e);
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e);
			}
			AccountType accountType = null;
			HttpSession session = request.getSession(true);

			LOG.debug("Adding newly added user {} to mongo", user.getFirstName());
			userManagementService.insertAgentSettings(user);
			LOG.debug("Added newly added user {} to mongo", user.getFirstName());

			LOG.debug("Adding newly registered user to principal session");
			sessionHelper.loginOnRegistration(emailId, password);
			LOG.debug("Successfully added registered user to principal session");

			List<LicenseDetail> licenseDetails = user.getCompany().getLicenseDetails();
			if (licenseDetails != null && !licenseDetails.isEmpty()) {
				LicenseDetail licenseDetail = licenseDetails.get(0);
				accountType = AccountType.getAccountType(licenseDetail.getAccountsMaster().getAccountsMasterId());
				LOG.debug("Adding account type in session");
				session.setAttribute(CommonConstants.ACCOUNT_TYPE_IN_SESSION, accountType);
			}
			else {
				LOG.debug("License details not found for the user's company");
			}
			if (user.getIsAtleastOneUserprofileComplete() == CommonConstants.PROCESS_COMPLETE) {

				// get the user's canonical settings
				LOG.info("Fetching the user's canonical settings and setting it in session");
				sessionHelper.getCanonicalSettings(session);
				sessionHelper.setSettingVariablesInSession(session);
			}
			else {
				// TODO: add logic for what happens when no user profile present
			}
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while setting new Password. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.COMPLETE_REGISTRATION;
		}
		LOG.info("Method completeRegistration() to complete registration of user finished.");
		return JspResolver.LANDING;
	}

	@RequestMapping(value = "/showchangepasswordpage")
	public String showChangePasswordPage() {
		return JspResolver.CHANGE_PASSWORD;
	}

	// JIRA SS-77 BY RM07 BOC
	/**
	 * Method to change password
	 */
	@RequestMapping(value = "/changepassword", method = RequestMethod.POST)
	public String changePassword(Model model, HttpServletRequest request) {
		LOG.info("change the password");

		String oldPassword = request.getParameter("oldpassword");
		String newPassword = request.getParameter("newpassword");
		String confirmNewPassword = request.getParameter("confirmnewpassword");

		try {
			validateChangePasswordFormParameters(oldPassword, newPassword, confirmNewPassword);

			// get user in session
			User user = sessionHelper.getCurrentUser();

			// check if old password entered matches with the one in the encrypted
			try {
				LOG.debug("Calling authentication service to validate user while changing password");
				authenticationService.validateUser(user, oldPassword);
				LOG.debug("Successfully executed authentication service to validate user");
			}
			catch (InvalidInputException e) {
				LOG.error("Invalid Input exception in validating User. Reason " + e.getMessage(), e);
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.INVALID_PASSWORD, e);

			}
			// change user's password
			authenticationService.changePassword(user, newPassword);

			LOG.info("change user password executed successfully");
			model.addAttribute("message",
					messageUtils.getDisplayMessage(DisplayMessageConstants.PASSWORD_CHANGE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE));

		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while changing password. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));

		}
		return JspResolver.CHANGE_PASSWORD;
	}

	// verify change password parameters
	private void validateChangePasswordFormParameters(String oldPassword, String newPassword, String confirmNewPassword) throws InvalidInputException {
		LOG.debug("Validating change password form paramters");
		if (oldPassword == null || oldPassword.isEmpty() || !oldPassword.matches(CommonConstants.PASSWORD_REG_EX)) {
			LOG.error("Invalid old password");
			throw new InvalidInputException("Invalid old password", DisplayMessageConstants.INVALID_CURRENT_PASSWORD);
		}
		if (newPassword == null || newPassword.isEmpty() || !newPassword.matches(CommonConstants.PASSWORD_REG_EX)) {
			LOG.error("Invalid new password");
			throw new InvalidInputException("Invalid new password", DisplayMessageConstants.INVALID_NEW_PASSWORD);
		}
		if (confirmNewPassword == null || confirmNewPassword.isEmpty()) {
			LOG.error("Confirm Password can not be null or empty");
			throw new InvalidInputException("Confirm Password can not be null or empty", DisplayMessageConstants.INVALID_CONFIRM_NEW_PASSWORD);
		}

		// check if new password and confirm new password field match
		if (!newPassword.equals(confirmNewPassword)) {
			LOG.error("Password and confirm password fields do not match");
			throw new InvalidInputException("Password and confirm password fields do not match", DisplayMessageConstants.PASSWORDS_MISMATCH);
		}
		LOG.debug("change password form parameters validated successfully");
	}
}
// JIRA SS-77 BY RM07 EOC
// JIRA SS-37 BY RM02 EOC
