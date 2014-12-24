package com.realtech.socialsurvey.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.exception.UserAlreadyExistsException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.common.JspResolver;

// JIRA SS-37 BY RM02 BOC

/**
 * Controller to manage users
 */
@Controller
public class UserManagementController {

	private static final Logger LOG = LoggerFactory.getLogger(UserManagementController.class);

	@Autowired
	private MessageUtils messageUtils;

	@Autowired
	private UserManagementService userManagementService;

	private static final int BATCH_SIZE = 20;

	// JIRA SS-42 BY RM05 BOC

	@RequestMapping(value = "/showusermangementpage", method = RequestMethod.GET)
	public String initUserManagementPage() {
		LOG.info("User Management page started");
		return JspResolver.USER_MANAGEMENT;
	}

	@RequestMapping(value = "/invitenewuser", method = RequestMethod.POST)
	public String inviteNewUser(Model model, HttpServletRequest request) {
		LOG.info("Method to add a new user by existing admin called.");
		try {
			String firstName = request.getParameter(CommonConstants.FIRST_NAME);
			String lastName = request.getParameter(CommonConstants.LAST_NAME);
			String emailId = request.getParameter(CommonConstants.EMAIL_ID);

			HttpSession session = request.getSession(false);
			User user = (User) session.getAttribute(CommonConstants.USER_IN_SESSION);

			try {
				if (userManagementService.isUserAdditionAllowed(user)) {
					userManagementService.inviteUserToRegister(user, firstName, lastName, emailId);
				}
				else {
					throw new InvalidInputException("Limit for maximum users has already reached.", DisplayMessageConstants.MAX_USERS_LIMIT_REACHED);
				}
			}
			catch (InvalidInputException e) {
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.REGISTRATION_INVITE_GENERAL_ERROR, e);
			}
			catch (UndeliveredEmailException e) {
				throw new UndeliveredEmailException(e.getMessage(), DisplayMessageConstants.REGISTRATION_INVITE_GENERAL_ERROR, e);
			}
			catch (NonFatalException e) {
				throw new UserAlreadyExistsException(e.getMessage(), DisplayMessageConstants.EMAILID_ALREADY_TAKEN, e);
			}
		}
		catch (NonFatalException nonFatalException) {
			LOG.error("NonFatalException in while inviting new user. Reason : " + nonFatalException.getMessage(), nonFatalException);
			model.addAttribute("message", messageUtils.getDisplayMessage(nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		model.addAttribute("message",
				messageUtils.getDisplayMessage(DisplayMessageConstants.REGISTRATION_INVITE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE));
		LOG.info("Method to add a new user by existing admin finished.");
		return JspResolver.MESSAGE_HEADER;
	}

	@RequestMapping(value = "/finduserandbranchesbyuserid", method = RequestMethod.POST)
	public String findUserAndAssignedBranchesByUserId(Model model, HttpServletRequest request) {
		LOG.info("Method to fetch user by user, findUserByUserId() started.");
		try {
			String userIdStr = request.getParameter(CommonConstants.USER_ID);
			if (userIdStr == null || userIdStr.isEmpty()) {
				LOG.error("Invalid user id passed in method findUserByUserId().");
				throw new InvalidInputException("Invalid user id passed in method findUserByUserId().");
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
			// userManagementService.get;
			model.addAttribute("searchedUser", user);

			// TODO : add assigned branches to the as model attribute assignedBranches

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

	@RequestMapping(value = "/findusersforcompany", method = RequestMethod.POST)
	public String findUsersForCompany(Model model, HttpServletRequest request) {
		LOG.info("Method to fetch user by user, findUserByUserId() started.");
		try {
			if (model.asMap().get("allUsersList") == null)

				getAllUsersForCompany(model, request);
			else
				getNextListOfUsers(model, request);
		}
		catch (NonFatalException nonFatalException) {
			LOG.error("NonFatalException while searching for user id. Reason : " + nonFatalException.getMessage(), nonFatalException);
			model.addAttribute("message", messageUtils.getDisplayMessage(nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.MESSAGE_HEADER;
		}
		LOG.info("Method to fetch users by company , findUsersForCompany() finished.");
		// return user details page on success
		return JspResolver.MESSAGE_HEADER;
	}

	@RequestMapping(value = "/finduserbyemail", method = RequestMethod.POST)
	public String findUserByEmail(Model model, HttpServletRequest request) {
		LOG.info("Method to find users by email id called.");
		try {

			String emailId = request.getParameter("emailId");
			if (emailId == null || emailId.isEmpty()) {
				LOG.error("Invalid email id passed in method findUserByEmail().");
				throw new InvalidInputException("Invalid email id passed in method findUserByEmail().");
			}

			HttpSession session = request.getSession(false);
			User user = (User) session.getAttribute(CommonConstants.USER_IN_SESSION);
			if (user == null) {
				LOG.error("No user found in current session in deactivateExistingUser().");
				throw new InvalidInputException("No user found in current session in deactivateExistingUser().");
			}

			try {
				user = userManagementService.getUserByEmailId(user, emailId);
				model.addAttribute("searchedUser", user);
			}
			catch (InvalidInputException invalidInputException) {
				throw new InvalidInputException(invalidInputException.getMessage(), invalidInputException);
			}
			catch (NoRecordsFetchedException e) {
				LOG.error("Sorry! No matching email found in our database.", e);
				throw new NoRecordsFetchedException("Sorry! No matching email found in our database.", e);
			}
		}
		// TODO add success message.
		catch (NonFatalException nonFatalException) {
			LOG.error("NonFatalException while searching for user by email id id. Reason : " + nonFatalException.getMessage(), nonFatalException);
			model.addAttribute("message", messageUtils.getDisplayMessage(nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		LOG.info("Method to find users by email id finished.");
		return JspResolver.MESSAGE_HEADER;
	}

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
				throw new InvalidInputException("Invalid user Id found to remove in removeExistingUser().");
			}

			HttpSession session = request.getSession(false);
			User user = (User) session.getAttribute(CommonConstants.USER_IN_SESSION);
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
		}
		catch (NonFatalException nonFatalException) {
			LOG.error("NonFatalException while removing user. Reason : " + nonFatalException.getMessage(), nonFatalException);
			model.addAttribute("message", messageUtils.getDisplayMessage(nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		LOG.info("Method to remove an existing user finished.");
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
			String branch = request.getParameter("branchId");
			String userToAssign = request.getParameter("userId");
			String isAssign = request.getParameter("isAssign");

			if (branch == null || branch.isEmpty()) {
				LOG.error("Null or empty value passed for branch in assignBranchAdmin()");
				throw new InvalidInputException("Null or empty value passed for branch in assignBranchAdmin()");
			}
			if (userToAssign == null || userToAssign.isEmpty()) {
				LOG.error("Null or empty value passed for user id in assignBranchAdmin()");
				throw new InvalidInputException("Null or empty value passed for user id in assignBranchAdmin()");
			}

			long branchId = 0l;
			long userId = 0l;
			try {
				branchId = Long.parseLong(branch);
				userId = Long.parseLong(userToAssign);
				HttpSession session = request.getSession(false);
				User admin = (User) session.getAttribute(CommonConstants.USER_IN_SESSION);
				if (isAssign.equalsIgnoreCase("YES"))
					// Assigns the given user as branch admin
					userManagementService.assignBranchAdmin(admin, branchId, userId);
				else if (isAssign.equalsIgnoreCase("NO"))
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
			HttpSession session;

			try {
				regionId = Long.parseLong(region);
				userId = Long.parseLong(userToAssign);
				session = request.getSession(false);
				User admin = (User) session.getAttribute(CommonConstants.USER_IN_SESSION);
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

			String region = request.getParameter("regionId");
			String userIdToRemove = request.getParameter("userId");

			if (region == null || region.isEmpty()) {
				LOG.error("Null or empty value passed for region id in removeRegionAdmin()");
				throw new InvalidInputException("Null or empty value passed for region id in removeRegionAdmin()");
			}
			if (userIdToRemove == null || userIdToRemove.isEmpty()) {
				LOG.error("Null or empty value passed for user id in assignRegionAdmin()");
				throw new InvalidInputException("Null or empty value passed for user id in removeRegionAdmin()");
			}

			long regionId = 0l;
			long userId = 0l;
			HttpSession session;

			try {
				regionId = Long.parseLong(region);
				userId = Long.parseLong(userIdToRemove);
				session = request.getSession(false);
				User admin = (User) session.getAttribute(CommonConstants.USER_IN_SESSION);

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

	private void getAllUsersForCompany(Model model, HttpServletRequest request) throws NonFatalException {
		LOG.debug("Method getAllUsersForCompany() started to fetch all the users for same company.");
		String userIdStr = request.getParameter(CommonConstants.USER_ID);
		if (userIdStr == null || userIdStr.isEmpty()) {
			LOG.error("Invalid user id passed in method findUserByUserId().");
			throw new InvalidInputException("Invalid user id passed in method findUserByUserId().");
		}
		long userId = 0;
		try {
			userId = Long.parseLong(userIdStr);
		}
		catch (NumberFormatException e) {
			LOG.error("Number format exception while parsing user Id", e);
			throw new NonFatalException("Number format execption while parsing user id", DisplayMessageConstants.GENERAL_ERROR, e);
		}
		List<User> allUsers = userManagementService.getUsersForCompany(userId);
		int maxIndex = BATCH_SIZE;
		if (allUsers.size() <= BATCH_SIZE) {
			maxIndex = allUsers.size();
			model.addAttribute("hasMoreUsers", false);
		}
		else{
			model.addAttribute("hasMoreUsers", true);
		}
		List<User> users = allUsers.subList(CommonConstants.INITIAL_INDEX, maxIndex);
		model.addAttribute("allUsersList", allUsers);
		model.addAttribute("currentIndex", BATCH_SIZE);
		model.addAttribute("usersList", users);
		LOG.debug("Method getAllUsersForCompany() finished to fetch all the users for same company.");
	}

	private void getNextListOfUsers(Model model, HttpServletRequest request) {
		LOG.debug("Method getNextListOfUsers() started to fetch next set of users for same company.");
		Map<String, Object> modelMap = model.asMap();
		int currentIndex = (Integer) modelMap.get("currentIndex");
		@SuppressWarnings("unchecked") List<User> allUsers = (List<User>) modelMap.get("allUsersList");
		int maxIndex = currentIndex + BATCH_SIZE;
		if (allUsers.size() <= maxIndex) {
			maxIndex = allUsers.size();
			model.addAttribute("hasMoreUsers", false);
		}
		else {
			maxIndex = currentIndex + BATCH_SIZE;
			model.addAttribute("hasMoreUsers", true);
		}
		List<User> users = allUsers.subList(currentIndex, maxIndex);
		currentIndex = maxIndex;
		model.addAttribute("usersList", users);
		model.addAttribute("currentIndex", currentIndex);
		LOG.debug("Method getNextListOfUsers() finished to fetch next set of users for same company.");
	}
}
// JIRA SS-37 BY RM02 EOC
