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
import com.realtech.socialsurvey.core.services.authentication.AuthenticationService;
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
	private AuthenticationService authenticationService;

	/**
	 * Method to assign a user as branch admin
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unused")
	@RequestMapping(value = "/assignbranchadmin", method = RequestMethod.POST)
	public String assignBranchAdmin(Model model, HttpServletRequest request) {
		LOG.info("Method to assign branch admin called");
		try {
			long branchId = 0l;
			long userId = 0l;
			try {
				branchId = Long.parseLong(request.getParameter("branchId"));
				userId = Long.parseLong(request.getParameter("userId"));
				HttpSession session = request.getSession(false);
				User user = (User) session.getAttribute(CommonConstants.USER_IN_SESSION);
				// TODO call service to assign branch admin

			}
			catch (NumberFormatException e) {
				LOG.error("Number format exception while parsing branch Id", e);
				throw new NonFatalException("Number format execption while parsing branch Id", DisplayMessageConstants.GENERAL_ERROR, e);
			}
		}
		catch (NonFatalException e) {
			LOG.error("Exception occured while assigning branch admin.Reason : " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		LOG.info("Successfully completed method to assign branch admin");
		return null;
	}

	/**
	 * Method to assign a user as region admin
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/assignregionadmin", method = RequestMethod.POST)
	public String assignRegionAdmin(Model model, HttpServletRequest request) {
		LOG.info("Method to assign region admin called");
		// TODO call services to assign region admin
		LOG.info("Successfully completed method to assign region admin");
		return null;
	}

	// JIRA SS-37 BY RM02 EOC

	

	// JIRA SS-77 BY RM07 BOC

	/**
	 * Method to change password
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/showchangepasswordpage")
	public String showChangePasswordPage(){
		return JspResolver.CHANGE_PASSWORD;
	}

	
	
	@RequestMapping(value = "/changepassword", method=RequestMethod.POST)
	public String changePassword(Model model, HttpServletRequest request) {
		LOG.info("change the password");

		String oldPassword = request.getParameter("oldpassword");
		String newPassword = request.getParameter("newpassword");
		String confirmNewPassword = request.getParameter("confirmnewpassword");

		try {
			validateChangePasswordFormParameters(oldPassword, newPassword, confirmNewPassword);

			// get user in session
			HttpSession session = request.getSession(false);
			User user = (User) session.getAttribute(CommonConstants.USER_IN_SESSION);

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

			try {
				// change user's password
				authenticationService.changePassword(user, newPassword);
			}
			catch (InvalidInputException e) {
				LOG.error("Invalid Input exception in changing the user's password. Reason " + e.getMessage(), e);
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e);
			}
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
// JIRA SS-77 BY RM07 EOD