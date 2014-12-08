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
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;

// JIRA SS-37 BY RM02 BOC

/**
 * Controller to manage users
 */
@Controller
public class UserManagementController {

	private static final Logger LOG = LoggerFactory.getLogger(UserManagementController.class);

	@Autowired
	private MessageUtils messageUtils;

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

}
// JIRA SS-37 BY RM02 EOC
