package com.realtech.socialsurvey.web.controller;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.solr.client.solrj.SolrServerException;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.AbridgedUserProfile;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.BranchFromSearch;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.entities.LockSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.RegionFromSearch;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserAssignment;
import com.realtech.socialsurvey.core.entities.UserFromSearch;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.entities.UserSettings;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.exception.UserAlreadyExistsException;
import com.realtech.socialsurvey.core.services.authentication.AuthenticationService;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
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
	private static final String ROLE_ADMIN = "Admin";
	private static final String ROLE_USER = "User";

	@Autowired
	private MessageUtils messageUtils;

	@Autowired
	private UserManagementService userManagementService;

	@Autowired
	private OrganizationManagementService organizationManagementService;

	@Autowired
	private ProfileManagementService profileManagementService;

	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	private URLGenerator urlGenerator;

	@Autowired
	private SessionHelper sessionHelper;

	@Autowired
	private SolrSearchService solrSearchService;

	private final static int SOLR_BATCH_SIZE = 20;

	// JIRA SS-42 BY RM05 BOC
	/*
	 * Method to show the User Management Page to a user on clicking UserManagement link.
	 */
	@RequestMapping(value = "/showusermangementpage", method = RequestMethod.GET)
	public String initUserManagementPage(Model model, HttpServletRequest request) {
		LOG.info("User Management page started");
		User user = sessionHelper.getCurrentUser();
		HttpSession session = request.getSession(false);

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
			
			long companyId = user.getCompany().getCompanyId();
			try {
				long usersCount = solrSearchService.countUsersByCompany(companyId, 0, SOLR_BATCH_SIZE);
				session.setAttribute("usersCount", usersCount);
			}
			catch (MalformedURLException e) {
				LOG.error("MalformedURLException while fetching users count. Reason : " + e.getMessage(), e);
				throw new NonFatalException("MalformedURLException while fetching users count", e);
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
							String branches = solrSearchService.searchBranches("", admin.getCompany(), null, null, 0, 0);
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
	public String findUsersForCompany(Model model, HttpServletRequest request) {
		LOG.info("Method to fetch user by user, findUserByUserId() started.");
		int startIndex = 0;
		int batchSize = 0;

		try {
			String startIndexStr = request.getParameter("startIndex");
			String batchSizeStr = request.getParameter("batchSize");
			try {
				if (startIndexStr == null || startIndexStr.isEmpty()) {
					LOG.error("Invalid value found in startIndex. It cannot be null or empty.");
					throw new InvalidInputException("Invalid value found in startIndex. It cannot be null or empty.");
				}
				if (batchSizeStr == null || batchSizeStr.isEmpty()) {
					LOG.error("Invalid value found in batchSizeStr. It cannot be null or empty.");
					batchSize = SOLR_BATCH_SIZE;
				}

				startIndex = Integer.parseInt(startIndexStr);
				batchSize = Integer.parseInt(batchSizeStr);
			}
			catch (NumberFormatException e) {
				LOG.error("NumberFormatException while searching for user id. Reason : " + e.getMessage(), e);
				throw new NonFatalException("NumberFormatException while searching for user id", e);
			}

			User admin = sessionHelper.getCurrentUser();
			if (admin == null) {
				LOG.error("No user found in session");
				throw new InvalidInputException("No user found in session", DisplayMessageConstants.NO_USER_IN_SESSION);
			}

			// fetching admin details
			UserFromSearch adminUser;
			try {
				String adminUserDoc = JSONUtil.toJSON(solrSearchService.getUserByUniqueId(admin.getUserId()));
				Type searchedUser = new TypeToken<UserFromSearch>() {}.getType();
				adminUser = new Gson().fromJson(adminUserDoc.toString(), searchedUser);
			}
			catch (SolrServerException e) {
				LOG.error("SolrServerException while searching for user id. Reason : " + e.getMessage(), e);
				throw new NonFatalException("SolrServerException while searching for user id.", e);
			}

			// fetching users from solr
			try {
				String users = solrSearchService.searchUsersByCompany(admin.getCompany().getCompanyId(), startIndex, batchSize);
				Type searchedUsersList = new TypeToken<List<UserFromSearch>>() {}.getType();
				List<UserFromSearch> usersList = new Gson().fromJson(users, searchedUsersList);

				usersList = userManagementService.checkUserCanEdit(admin, adminUser, usersList);

				model.addAttribute("userslist", usersList);
				LOG.debug("Users List: " + usersList.toString());
			}
			catch (MalformedURLException e) {
				LOG.error("MalformedURLException while searching for user id. Reason : " + e.getMessage(), e);
				throw new NonFatalException("MalformedURLException while searching for user id.", DisplayMessageConstants.GENERAL_ERROR, e);
			}
		}
		catch (NonFatalException nonFatalException) {
			LOG.error("NonFatalException while searching for user id. Reason : " + nonFatalException.getStackTrace(), nonFatalException);
			model.addAttribute("message", messageUtils.getDisplayMessage(nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.MESSAGE_HEADER;
		}

		LOG.info("Method to fetch users by company , findUsersForCompany() finished.");
		return JspResolver.USER_LIST_FOR_MANAGEMENT;
	}

	/*
	 * Method to find a user on the basis of email id provided.
	 */
	@ResponseBody
	@RequestMapping(value = "/finduserbyemail", method = RequestMethod.GET)
	public String findUserByEmail(Model model, HttpServletRequest request) {
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

	@RequestMapping(value = "/findusers", method = RequestMethod.GET)
	public String findUsersByEmailIdAndRedirectToPage(Model model, HttpServletRequest request) {
		LOG.info("Finding users and redirecting to search page");

		try {
			String users = findUserByEmail(model, request);

			User admin = sessionHelper.getCurrentUser();
			if (admin == null) {
				throw new InvalidInputException("No user found in session", DisplayMessageConstants.NO_USER_IN_SESSION);
			}

			/**
			 * fetching admin details
			 */
			UserFromSearch adminUser = null;
			try {
				String adminUserDoc = JSONUtil.toJSON(solrSearchService.getUserByUniqueId(admin.getUserId()));
				Type searchedUser = new TypeToken<UserFromSearch>() {}.getType();
				adminUser = new Gson().fromJson(adminUserDoc.toString(), searchedUser);
			}
			catch (SolrServerException e) {
				throw new NonFatalException("SolrServerException while searching for user id.Reason:" + e.getMessage(),
						DisplayMessageConstants.GENERAL_ERROR, e);
			}
			Type searchedUsersList = new TypeToken<List<UserFromSearch>>() {}.getType();
			List<UserFromSearch> usersList = new Gson().fromJson(users, searchedUsersList);
			LOG.debug("Users List in findusers: " + users);

			/**
			 * checking the edit capabilities of user
			 */
			usersList = userManagementService.checkUserCanEdit(admin, adminUser, usersList);

			model.addAttribute("userslist", usersList);
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException in findusers. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.MESSAGE_HEADER;
		}
		return JspResolver.USER_LIST_FOR_MANAGEMENT;
	}

	/*
	 * Method to remove an existing user. Soft delete is done.
	 */
	@ResponseBody
	@RequestMapping(value = "/removeexistinguser", method = RequestMethod.POST)
	public String removeExistingUser(Model model, HttpServletRequest request) {
		LOG.info("Method to deactivate an existing user called.");
		Map<String, String> statusMap = new HashMap<String, String>();
		String message = "";
		long userIdToRemove = 0;

		try {
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
				// update the user count modificaiton notification
				userManagementService.updateUserCountModificationNotification(user.getCompany());
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.REGISTRATION_INVITE_GENERAL_ERROR, e);
			}

			LOG.debug("Removing user {} from solr.", userIdToRemove);
			solrSearchService.removeUserFromSolr(userIdToRemove);

			message = messageUtils.getDisplayMessage(DisplayMessageConstants.USER_DELETE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE).getMessage();
			statusMap.put("status", CommonConstants.SUCCESS_ATTRIBUTE);
		}
		catch (NonFatalException nonFatalException) {
			LOG.error("NonFatalException while removing user. Reason : " + nonFatalException.getMessage(), nonFatalException);
			statusMap.put("status", CommonConstants.ERROR);
			message = messageUtils.getDisplayMessage(nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
		}

		LOG.info("Method to remove an existing user finished.");
		statusMap.put("message", message);
		return new Gson().toJson(statusMap);
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
				throw new InvalidInputException("Invalid user id passed in method assiguserIdnUserToBranch().");
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
			}
			catch (NumberFormatException e) {
				LOG.error("Number format exception while parsing region Id or user id", e);
				throw new NonFatalException("Number format execption while parsing region Id or user id", DisplayMessageConstants.GENERAL_ERROR, e);
			}
			try {
				User assigneeUser = userManagementService.getUserByUserId(userId);
				organizationManagementService.assignRegionToUser(admin, regionId, assigneeUser, true);
			}
			catch (InvalidInputException | NoRecordsFetchedException | SolrException e) {
				LOG.error("Exception while assigning user as region admin.Reason:" + e.getMessage(), e);
				throw new NonFatalException("Exception while assigning user as region admin.Reason:" + e.getMessage(),
						DisplayMessageConstants.GENERAL_ERROR, e);
			}
		}
		// TODO add success message.
		catch (NonFatalException e) {
			LOG.error("Exception occured while assigning region admin. Reason : " + e.getMessage(), e);
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
			Map<String, String> urlParams = null;
			try {
				urlParams = urlGenerator.decryptParameters(encryptedUrlParams);
			}
			catch (InvalidInputException e) {
				LOG.error("Invalid Input exception in decrypting url parameters in showCompleteRegistrationPage(). Reason " + e.getMessage(), e);
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e);
			}

			// fetching details from urlparams
			long companyId;
			try {
				companyId = Long.parseLong(urlParams.get(CommonConstants.COMPANY));
			}
			catch (NumberFormatException e) {
				throw new NonFatalException(e.getMessage(), DisplayMessageConstants.INVALID_REGISTRATION_INVITE, e);
			}

			// checking status of user
			String emailId = urlParams.get(CommonConstants.EMAIL_ID);
			User newUser = userManagementService.getUserByEmailAndCompany(companyId, emailId);
			if (newUser.getStatus() == CommonConstants.STATUS_NOT_VERIFIED) {
				model.addAttribute(CommonConstants.COMPANY, urlParams.get(CommonConstants.COMPANY));
				model.addAttribute(CommonConstants.FIRST_NAME, urlParams.get(CommonConstants.FIRST_NAME));
				model.addAttribute(CommonConstants.EMAIL_ID, emailId);

				String lastName = urlParams.get(CommonConstants.LAST_NAME);
				if (lastName != null && !lastName.isEmpty()) {
					model.addAttribute(CommonConstants.LAST_NAME, urlParams.get(CommonConstants.LAST_NAME));
				}
				LOG.debug("Validation of url completed. Service returning params to be prepopulated in registration page");
			}
			else {
				model.addAttribute("message", "The registration url is no longer valid");
				model.addAttribute("status", DisplayMessageType.ERROR_MESSAGE);
				LOG.debug("The registration url had been used earlier");
				return JspResolver.LOGIN;
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
		User user = null;

		try {
			String firstName = request.getParameter(CommonConstants.FIRST_NAME);
			String lastName = request.getParameter(CommonConstants.LAST_NAME);
			String emailId = request.getParameter(CommonConstants.EMAIL_ID);
			String password = request.getParameter("password");
			String confirmPassword = request.getParameter("confirmPassword");
			String companyIdStr = request.getParameter("companyId");

			// form parameters validation
			validateCompleteRegistrationForm(firstName, lastName, emailId, password, companyIdStr, confirmPassword);

			// Decrypting URL parameters
			Map<String, String> urlParams = new HashMap<>();
			try {
				String encryptedUrlParameters = request.getParameter("q");
				urlParams = urlGenerator.decryptParameters(encryptedUrlParameters);
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e);
			}

			// check if email address entered matches with the one in the encrypted url
			if (!urlParams.get("emailId").equalsIgnoreCase(emailId)) {
				LOG.error("Invalid Input exception. Reason emailId entered does not match with the one to which the mail was sent");
				throw new InvalidInputException("Invalid Input exception", DisplayMessageConstants.INVALID_EMAILID);
			}

			long companyId = 0;
			try {
				companyId = Long.parseLong(companyIdStr);
			}
			catch (NumberFormatException e) {
				throw new InvalidInputException("NumberFormat exception parsing companyId. Reason : " + e.getMessage(),
						DisplayMessageConstants.GENERAL_ERROR, e);
			}
			
			AccountType accountType = null;
			HttpSession session = request.getSession(true);
			try {
				// fetch user object with email Id
				user = authenticationService.getUserWithLoginNameAndCompanyId(emailId, companyId);

				// calling service to update user details on registration
				user = userManagementService.updateUserOnCompleteRegistration(user, emailId, companyId, firstName, lastName, password);
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.USER_NOT_PRESENT, e);
			}

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

			// updating the flags for user profiles
			if (user.getIsAtleastOneUserprofileComplete() == CommonConstants.PROCESS_COMPLETE) {
				// get the user's canonical settings
				LOG.info("Fetching the user's canonical settings and setting it in session");
				sessionHelper.getCanonicalSettings(session);
				sessionHelper.setSettingVariablesInSession(session);
				LOG.debug("Updating user count modification notification");
				userManagementService.updateUserCountModificationNotification(user.getCompany());
			}
			else {
				// TODO: add logic for what happens when no user profile present
			}
			
			// updating session with selected user profile if not set
			LOG.debug("Updating session with selected user profile if not set");
			Map<Long, UserProfile> profileMap = new HashMap<Long, UserProfile>();
			UserProfile selectedProfile = user.getUserProfiles().get(CommonConstants.INITIAL_INDEX);
			for (UserProfile profile : user.getUserProfiles()) {
				if (profile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID) {
					selectedProfile = profile;
					break;
				}
			}
			session.setAttribute(CommonConstants.USER_PROFILE, selectedProfile);
			
			// updating session with aggregated user profiles, if not set
			LOG.debug("Updating session with aggregated user profiles, if not set");
			Map<Long, AbridgedUserProfile> profileAbridgedMap = userManagementService.processedUserProfiles(user, accountType, profileMap);
			if (profileAbridgedMap.size() > 0) {
				session.setAttribute(CommonConstants.USER_PROFILE_LIST, profileAbridgedMap);
				session.setAttribute(CommonConstants.PROFILE_NAME_COLUMN, profileAbridgedMap.get(selectedProfile.getUserProfileId()).getUserProfileName());
			}
			session.setAttribute(CommonConstants.USER_PROFILE_MAP, profileMap);
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while setting new Password. Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.COMPLETE_REGISTRATION;
		}

		LOG.info("Method completeRegistration() to complete registration of user finished.");
		return JspResolver.LANDING;
	}
	
	@RequestMapping(value = "/showlinkedindatacompare")
	public String showLinkedInDataCompare(Model model, HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		
		User user = sessionHelper.getCurrentUser();
		AccountType accountType = (AccountType) session.getAttribute(CommonConstants.ACCOUNT_TYPE_IN_SESSION);
		UserSettings userSettings = (UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);
		UserProfile selectedProfile = (UserProfile) session.getAttribute(CommonConstants.USER_PROFILE);
		
		long branchId = 0;
		long regionId = 0;
		int profilesMaster = 0;
		if (selectedProfile != null) {
			branchId = selectedProfile.getBranchId();
			regionId = selectedProfile.getRegionId();
			profilesMaster = selectedProfile.getProfilesMaster().getProfileId();
		}
		
		// Setting userSettings in session
		OrganizationUnitSettings profileSettings = null;
		try {
			profileSettings = profileManagementService.aggregateUserProfile(user, accountType, userSettings, branchId, regionId, profilesMaster);
		}
		catch (InvalidInputException e) {
			LOG.error("InvalidInputException while fetching profile. Reason :" + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		session.setAttribute(CommonConstants.USER_PROFILE_SETTINGS, profileSettings);

		// Setting parentLock in session
		LockSettings parentLock = null;
		try {
			parentLock = profileManagementService.aggregateParentLockSettings(user, accountType, userSettings, branchId, regionId, profilesMaster);
		}
		catch (InvalidInputException e) {
			LOG.error("InvalidInputException while fetching profile. Reason :" + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		session.setAttribute(CommonConstants.PARENT_LOCK, parentLock);
		
		return JspResolver.LINKEDIN_COMPARE;
	}
	
	@ResponseBody
	@RequestMapping(value = "/fetchuploadedprofileimage", method = RequestMethod.GET)
	public String fetchProfileImage(Model model, HttpServletRequest request) {
		LOG.info("Fetching profile image");
		OrganizationUnitSettings profileSettings = (OrganizationUnitSettings) request.getSession(false).getAttribute(
				CommonConstants.USER_PROFILE_SETTINGS);
		return profileSettings.getProfileImageUrl();
	}

	/**
	 * Method to validate complete registration formF
	 * 
	 * @param firstName
	 * @param lastName
	 * @param emailId
	 * @param password
	 * @param companyIdStr
	 * @param confirmPassword
	 * @throws InvalidInputException
	 */
	private void validateCompleteRegistrationForm(String firstName, String lastName, String emailId, String password, String companyIdStr,
			String confirmPassword) throws InvalidInputException {
		LOG.debug("Method validateCompleteRegistrationForm called");
		if (firstName == null || firstName.isEmpty() || !firstName.trim().matches(CommonConstants.FIRST_NAME_REGEX)) {
			LOG.error("First name invalid");
			throw new InvalidInputException("First name invalid", DisplayMessageConstants.INVALID_FIRSTNAME);
		}
		if (lastName != null && !lastName.isEmpty() && !lastName.matches(CommonConstants.LAST_NAME_REGEX)) {
			LOG.error("Last name invalid");
			throw new InvalidInputException("Last name invalid", DisplayMessageConstants.INVALID_LASTNAME);
		}
		if (emailId == null || emailId.isEmpty() || !emailId.trim().matches(CommonConstants.EMAIL_REGEX)) {
			LOG.error("EmailId not valid");
			throw new InvalidInputException("EmailId not valid", DisplayMessageConstants.INVALID_EMAILID);
		}
		if (password == null || password.isEmpty() || password.length()<CommonConstants.PASSWORD_LENGTH) {
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
		LOG.debug("Method validateCompleteRegistrationForm executed successfully");
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
		User user = sessionHelper.getCurrentUser();

		try {
			String oldPassword = request.getParameter("oldpassword");
			String newPassword = request.getParameter("newpassword");
			String confirmNewPassword = request.getParameter("confirmnewpassword");
			validateChangePasswordFormParameters(oldPassword, newPassword, confirmNewPassword);

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

			model.addAttribute("status", DisplayMessageType.SUCCESS_MESSAGE);
			model.addAttribute("message",
					messageUtils.getDisplayMessage(DisplayMessageConstants.PASSWORD_CHANGE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE));
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while changing password. Reason : " + e.getMessage(), e);
			model.addAttribute("status", DisplayMessageType.ERROR_MESSAGE);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		return JspResolver.CHANGE_PASSWORD;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/finduserassignments", method = RequestMethod.GET)
	public String getUserAssignments(Model model, HttpServletRequest request) {
		LOG.info("Method getUserAssignments() called from UserManagementController");
		HttpSession session = request.getSession();

		try {
			long userId = 0l;
			try {
				userId = Long.parseLong(request.getParameter("userId"));
			}
			catch (NumberFormatException e) {
				throw new InvalidInputException("NumberFormatException while parsing userId.Reason: " + e.getMessage(),
						DisplayMessageConstants.GENERAL_ERROR, e);
			}
			User user = null;
			try {
				user = userManagementService.getUserByUserId(userId);
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException("InvalidInputException while getting user.Reason: " + e.getMessage(),
						DisplayMessageConstants.GENERAL_ERROR, e);
			}

			Map<Long, RegionFromSearch> regions = (Map<Long, RegionFromSearch>) session.getAttribute(CommonConstants.REGIONS_IN_SESSION);
			Map<Long, BranchFromSearch> branches = (Map<Long, BranchFromSearch>) session.getAttribute(CommonConstants.BRANCHES_IN_SESSION);

			List<UserAssignment> userAssignments = new ArrayList<UserAssignment>();
			for (UserProfile userProfile : user.getUserProfiles()) {
				// Check if profile is complete
				if (userProfile.getIsProfileComplete() != CommonConstants.PROCESS_COMPLETE) {
					continue;
				}
				UserAssignment assignment = new UserAssignment();
				assignment.setUserId(user.getUserId());
				assignment.setProfileId(userProfile.getUserProfileId());
				assignment.setStatus(userProfile.getStatus());

				long regionId;
				long branchId;
				RegionFromSearch region = null;
				BranchFromSearch branch = null;
				int profileMaster = userProfile.getProfilesMaster().getProfileId();
				switch (profileMaster) {
					case CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID:
						regionId = userProfile.getRegionId();
						if (regionId != 0l) {
							region = regions.get(regionId);
						}

						// if region is not default
						if (region.getIsDefaultBySystem() != CommonConstants.YES) {
							assignment.setEntityId(regionId);
							assignment.setEntityName(region.getRegionName());
						}
						// if region is default
						else {
							continue;
						}
						assignment.setRole(ROLE_ADMIN);

						break;

					case CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID:
						branchId = userProfile.getBranchId();
						if (branchId != 0l) {
							branch = branches.get(branchId);
						}

						// if branch is not default
						if (branch.getIsDefaultBySystem() != CommonConstants.YES) {
							assignment.setEntityId(branchId);
							assignment.setEntityName(branch.getBranchName());
						}
						// if branch is default
						else {
							continue;
						}
						assignment.setRole(ROLE_ADMIN);

						break;

					case CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID:
						branchId = userProfile.getBranchId();
						if (branchId != 0l) {
							branch = branches.get(branchId);
						}

						// if branch is not default
						if (branch.getIsDefaultBySystem() != CommonConstants.YES) {
							assignment.setEntityId(branchId);
							assignment.setEntityName(branch.getBranchName());
						}
						// if branch is default
						else {
							regionId = userProfile.getRegionId();
							if (regionId != 0l) {
								region = regions.get(regionId);
							}

							// if region is not default
							if (region.getIsDefaultBySystem() != 1) {
								assignment.setEntityId(regionId);
								assignment.setEntityName(region.getRegionName());
							}
							// if region is default
							else {
								assignment.setEntityId(user.getCompany().getCompanyId());
								assignment.setEntityName(user.getCompany().getCompany());
							}
						}
						assignment.setRole(ROLE_USER);
						break;

					default:
				}
				userAssignments.add(assignment);
			}

			// set the request parameters in model
			model.addAttribute("firstName", user.getFirstName());
			model.addAttribute("lastName", user.getLastName());
			model.addAttribute("emailId", user.getEmailId());
			model.addAttribute("userId", user.getUserId());

			// returning in descending order
			Collections.reverse(userAssignments);
			model.addAttribute("profiles", userAssignments);
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while finding user assignments Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.MESSAGE_HEADER;
		}

		LOG.info("Method getUserAssignments() finished from UserManagementController");
		return JspResolver.USER_MANAGEMENT_EDIT_USER_DETAILS;
	}

	@ResponseBody
	@RequestMapping(value = "/updateuserprofile", method = RequestMethod.POST)
	public String updateUserProfile(Model model, HttpServletRequest request) {
		LOG.info("Method updateUserProfile() called from UserManagementController");
		Map<String, String> statusMap = new HashMap<String, String>();
		String message = "";

		try {
			User user = sessionHelper.getCurrentUser();
			long profileId = Long.parseLong(request.getParameter("profileId"));
			int status = Integer.parseInt(request.getParameter("status"));

			userManagementService.updateUserProfile(user, profileId, status);

			message = messageUtils.getDisplayMessage(DisplayMessageConstants.PROFILE_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE).getMessage();
			statusMap.put("status", CommonConstants.SUCCESS_ATTRIBUTE);
		}
		catch (NumberFormatException e) {
			LOG.error("NumberFormatException while parsing profileId. Reason : " + e.getMessage(), e);
			statusMap.put("status", CommonConstants.ERROR);
			message = messageUtils.getDisplayMessage(e.getMessage(), DisplayMessageType.ERROR_MESSAGE).getMessage();
		}
		catch (InvalidInputException e) {
			LOG.error("InvalidInputException while updating profile. Reason : " + e.getMessage(), e);
			statusMap.put("status", CommonConstants.ERROR);
			message = messageUtils.getDisplayMessage(e.getMessage(), DisplayMessageType.ERROR_MESSAGE).getMessage();
		}

		statusMap.put("message", message);
		LOG.info("Method updateUserProfile() finished from UserManagementController");
		return new Gson().toJson(statusMap);
	}

	@ResponseBody
	@RequestMapping(value = "/reinviteuser", method = RequestMethod.GET)
	public String sendInvitationForRegistration(Model model, HttpServletRequest request) {
		LOG.info("Sending invitation to user");
		Map<String, String> statusMap = new HashMap<String, String>();
		String message = "";
		User user = sessionHelper.getCurrentUser();

		try {
			String emailId = request.getParameter("emailId");
			String firstName = request.getParameter("firstName");
			String lastName = request.getParameter("lastName");

			if (emailId == null || emailId.isEmpty()) {
				LOG.warn("Email id is not present to resend invitation");
				throw new InvalidInputException("Invalid email id.", DisplayMessageConstants.INVALID_EMAILID);
			}
			if (firstName == null || firstName.isEmpty()) {
				LOG.warn("First Name is not present to resend invitation");
				throw new InvalidInputException("Invalid first name.", DisplayMessageConstants.INVALID_FIRSTNAME);
			}
			if( lastName == null || lastName.isEmpty()){
				lastName = " ";
			}

			LOG.debug("Sending invitation...");
			userManagementService.sendRegistrationCompletionLink(emailId, firstName, lastName, user.getCompany().getCompanyId());

			message = messageUtils.getDisplayMessage(DisplayMessageConstants.INVITATION_RESEND_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE)
					.getMessage();
			statusMap.put("status", CommonConstants.SUCCESS_ATTRIBUTE);
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while reinviting user. Reason : " + e.getMessage(), e);
			statusMap.put("status", CommonConstants.ERROR);
			message = messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
		}

		LOG.info("Invitation sent to user");
		statusMap.put("message", message);
		return new Gson().toJson(statusMap);
	}
	
	@ResponseBody
	@RequestMapping(value = "/sendverificationmail", method = RequestMethod.GET)
	public String sendVerificationMail(Model model, HttpServletRequest request) {
		LOG.info("Method sendVerificationMail() called from UserManagementController");
		try {
			User user = sessionHelper.getCurrentUser();
			userManagementService.inviteCorporateToRegister(user.getFirstName(), user.getLastName(), user.getEmailId(), true);
		}
		catch (NonFatalException e) {
			LOG.error("InvalidInputException while updating profile. Reason : " + e.getMessage(), e);
		}
		return "success";
	}

	// verify change password parameters
	private void validateChangePasswordFormParameters(String oldPassword, String newPassword, String confirmNewPassword) throws InvalidInputException {
		LOG.debug("Validating change password form paramters");
		if (oldPassword == null || oldPassword.isEmpty() || oldPassword.length()<CommonConstants.PASSWORD_LENGTH) {
			LOG.error("Invalid old password");
			throw new InvalidInputException("Invalid old password", DisplayMessageConstants.INVALID_CURRENT_PASSWORD);
		}
		if (newPassword == null || newPassword.isEmpty() || newPassword.length()<CommonConstants.PASSWORD_LENGTH) {
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
