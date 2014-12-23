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

	// JIRA SS-42 BY RM05 BOC

	@RequestMapping(value = "/invitenewuser", method = RequestMethod.POST)
	public String inviteNewUser(Model model, HttpServletRequest request) throws InvalidInputException, UndeliveredEmailException,
			UserAlreadyExistsException {
		LOG.info("Method to add a new user by existing admin called.");

		String firstName = request.getParameter(CommonConstants.FIRST_NAME);
		String lastName = request.getParameter(CommonConstants.LAST_NAME);
		String emailId = request.getParameter(CommonConstants.EMAIL_ID);

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(CommonConstants.USER_IN_SESSION);

		try {
			if (userManagementService.isUserAdditionAllowed(user)) {
				userManagementService.inviteUserToRegister(user, firstName, lastName, emailId);
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
		LOG.info("Method to add a new user by existing admin finished.");
		return JspResolver.MESSAGE_HEADER;
	}

	@RequestMapping(value = "/finduserbyemail", method = RequestMethod.POST)
	public String findUserByUserId(Model model, HttpServletRequest request) throws InvalidInputException {
		LOG.info("Method to fetch user by user, findUserByUserId() started.");
		String userIdStr = request.getParameter(CommonConstants.USER_ID);
		if (userIdStr == null || userIdStr.isEmpty()) {
			LOG.error("Invalid user id passed in method findUserByUserId().");
			throw new InvalidInputException("Invalid user id passed in method findUserByUserId().");
		}
		long userId = Long.parseLong(userIdStr);
		User user = userManagementService.getUserByUserId(userId);
		model.addAttribute("user",user);
		LOG.info("Method to fetch user by user, findUserByUserId() finished.");
		return JspResolver.MESSAGE_HEADER;
	}

	@RequestMapping(value = "/finduserbyemail", method = RequestMethod.POST)
	public String findUserByEmail(Model model, HttpServletRequest request) throws InvalidInputException {
		LOG.info("Method to find users by email id called.");

		String emailId = request.getParameter("emailId");
		if (emailId == null || emailId.isEmpty()) {
			LOG.error("Invalid email id passed in method findUserByEmail().");
			throw new InvalidInputException("Invalid email id passed in method findUserByEmail().");
		}

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(CommonConstants.USER_IN_SESSION);
		if (user == null) {
			LOG.error("No user found in current session in deactivateExistingUser().");
			throw new InvalidInputException("No user found in current session in deactivateExistingUser().");
		}

		try {
			user = userManagementService.getUserByEmailId(user, emailId);
			model.addAttribute("user", user);
		}
		catch (InvalidInputException invalidInputException) {
			throw new InvalidInputException(invalidInputException.getMessage(), invalidInputException);
		}
		catch (NoRecordsFetchedException e) {
			LOG.error("Sorry! No matching email found in our database.", e);

		}
		LOG.info("Method to find users by email id finished.");
		return JspResolver.MESSAGE_HEADER;
	}

	@RequestMapping(value = "/removeexistinguser", method = RequestMethod.POST)
	public String removeExistingUser(Model model, HttpServletRequest request) throws InvalidInputException, UndeliveredEmailException,
			UserAlreadyExistsException {
		LOG.info("Method to deactivate an existing user called.");

		long userIdToRemove = Long.parseLong(request.getParameter("userIdToRemove"));

		if (userIdToRemove < 0) {
			LOG.error("Invalid user Id found to remove in removeExistingUser().");
			throw new InvalidInputException("Invalid user Id found to remove in removeExistingUser().");
		}

		HttpSession session = request.getSession();
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
		LOG.info("Method to remove an existing user finished.");
		return JspResolver.MESSAGE_HEADER;
	}

	// JIRA SS-42 BY RM05 EOC

	/**
	 * Method to assign a user as branch admin
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/assignbranchadmin", method = RequestMethod.POST)
	public String assignBranchAdmin(Model model, HttpServletRequest request) {
		LOG.info("Method to assign branch admin called");
		try {

			String branch = request.getParameter("branchId");
			String userToAssign = request.getParameter("userId");

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
				// Assigns the given user as branch admin
				userManagementService.assignBranchAdmin(admin, branchId, userId);
			}
			catch (NumberFormatException e) {
				LOG.error("Number format exception while parsing branch Id or user id", e);
				throw new NonFatalException("Number format execption while parsing branch Id or user id", DisplayMessageConstants.GENERAL_ERROR, e);
			}
		}
		catch (NonFatalException e) {
			LOG.error("Exception occured while assigning branch admin.Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		LOG.info("Successfully completed method to assign branch admin");
		return JspResolver.MESSAGE_HEADER;
	}

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
		catch (NonFatalException e) {
			LOG.error("Exception occured while assigning branch admin.Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		LOG.info("Successfully completed method to assign region admin");
		return JspResolver.MESSAGE_HEADER;
	}

	/**
	 * Method to remove a branch admin.
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/unassignbranchadmin", method = RequestMethod.POST)
	public String unassignBranchAdmin(Model model, HttpServletRequest request) {
		LOG.info("Method to remove branch admin called");
		try {

			String branch = request.getParameter("branchId");
			String userIdToRemove = request.getParameter("userId");

			if (branch == null || branch.isEmpty()) {
				LOG.error("Null or empty value passed for branch in assignBranchAdmin()");
				throw new InvalidInputException("Null or empty value passed for branch in assignBranchAdmin()");
			}
			if (userIdToRemove == null || userIdToRemove.isEmpty()) {
				LOG.error("Null or empty value passed for user id in assignBranchAdmin()");
				throw new InvalidInputException("Null or empty value passed for user id in assignBranchAdmin()");
			}

			long branchId = 0l;
			long userId = 0l;
			HttpSession session;

			try {
				branchId = Long.parseLong(branch);
				userId = Long.parseLong(userIdToRemove);
				session = request.getSession(false);
				User admin = (User) session.getAttribute(CommonConstants.USER_IN_SESSION);
				// Remove the given user from branch admin.
				userManagementService.unassignBranchAdmin(admin, branchId, userId);
			}
			catch (NumberFormatException e) {
				LOG.error("Number format exception while parsing branch Id or user id", e);
				throw new NonFatalException("Number format execption while parsing branch Id", DisplayMessageConstants.GENERAL_ERROR, e);
			}
		}
		catch (NonFatalException e) {
			LOG.error("Exception occured while assigning branch admin.Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		LOG.info("Successfully completed method to remove branch admin");
		return JspResolver.MESSAGE_HEADER;
	}

	/**
	 * Method to remove a region admin.
	 * 
	 * @param model
	 * @param request
	 * @return
	 * @throws InvalidInputException
	 */
	@RequestMapping(value = "/unassignregionadmin", method = RequestMethod.POST)
	public String unassignRegionAdmin(Model model, HttpServletRequest request) throws InvalidInputException {

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
		catch (NonFatalException e) {
			LOG.error("Exception occured while assigning region admin.Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		LOG.info("Successfully completed method to remove region admin");
		return JspResolver.MESSAGE_HEADER;
	}
}
// JIRA SS-37 BY RM02 EOC
