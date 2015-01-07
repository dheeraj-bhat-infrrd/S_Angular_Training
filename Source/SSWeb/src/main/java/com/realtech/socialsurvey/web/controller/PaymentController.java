package com.realtech.socialsurvey.web.controller;

// JIRA: SS-15: By RM03

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.payment.Payment;
import com.realtech.socialsurvey.core.services.payment.exception.PaymentException;
import com.realtech.socialsurvey.core.services.registration.RegistrationService;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.common.JspResolver;

/**
 * Handles the payment actions by user
 */
@Controller
public class PaymentController {

	private static final Logger LOG = LoggerFactory.getLogger(PaymentController.class);

	@Autowired
	private Payment gateway;

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private OrganizationManagementService organizationManagementService;

	@Autowired
	private MessageUtils messageUtils;
	
	@Autowired
	private SessionHelper sessionHelper;

	/**
	 * Method used to display the Braintree form to get card details.
	 * 
	 * @param model
	 * @param response
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/payment")
	public String paymentPage(Model model, HttpServletResponse response, HttpServletRequest request) {

		LOG.info("Payment Step 1.");
		model.addAttribute("clienttoken", gateway.getClientToken());
		return JspResolver.PAYMENT;
	}

	/**
	 * Method for a user to subscribe for a plan.
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/subscribe", method = RequestMethod.POST)
	public String subscribeForPlan(Model model, HttpServletRequest request, HttpServletResponse response) {

		LOG.info("Payment Step 2.");
		try {
			boolean status = false;

			String strAccountType = request.getParameter("accounttype");
			// Get the nonce from the request
			String nonce = request.getParameter("payment_method_nonce");

			// Extract the session
			HttpSession session = request.getSession(false);

			// Get the user object from the session and the company object from it
			User user = (User) session.getAttribute(CommonConstants.USER_IN_SESSION);
			Company company = user.getCompany();

			Long accountTypeValue = 0l;
			try {
				accountTypeValue = Long.parseLong(strAccountType);
			}
			catch (NumberFormatException e) {
				throw new InvalidInputException("Error while parsing account type ", DisplayMessageConstants.GENERAL_ERROR, e);
			}

			try {
				status = gateway.subscribe(user, company, accountTypeValue, nonce);
			}
			catch (InvalidInputException e) {
				LOG.error("PaymentController subscribeForPlan() : InvalidInput Exception thrown : " + e.getMessage(), e);
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e);
			}
			catch (PaymentException e){
				LOG.error("PaymentController subscribeForPlan() : Payment Exception thrown : " + e.getMessage(), e);
				throw new PaymentException(e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e);

			}
			if (status) {
				LOG.info("Subscription Successful!");
				AccountType accountType = null;
				try {
					LOG.debug("Calling sevices for adding account type of company");
					accountType = organizationManagementService.addAccountTypeForCompany(user, strAccountType);
					LOG.debug("Successfully executed sevices for adding account type of company.Returning account type : " + accountType);
					
					LOG.debug("Adding account type in session");
					session.setAttribute(CommonConstants.ACCOUNT_TYPE_IN_SESSION, accountType);
					// get the settings
					sessionHelper.getCanonicalSettings(session);
					// set the session variable
					sessionHelper.setSettingVariablesInSession(session);
				}
				catch (InvalidInputException e) {
					throw new InvalidInputException("InvalidInputException in addAccountType. Reason :" + e.getMessage(),
							DisplayMessageConstants.GENERAL_ERROR, e);
				}
				try {
					/**
					 * For each account type, only the company admin's profile completion stage is
					 * updated, all the other profiles created by default need no action so their
					 * profile completion stage is marked completed at the time of insert
					 */
					LOG.debug("Calling sevices for updating profile completion stage");
					registrationService.updateProfileCompletionStage(user, CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID,
							CommonConstants.DASHBOARD_STAGE);
					LOG.debug("Successfully executed sevices for updating profile completion stage");
				}
				catch (InvalidInputException e) {
					LOG.error("InvalidInputException while updating profile completion stage. Reason : " + e.getMessage(), e);
					throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e);
				}
				model.addAttribute("message",
						messageUtils.getDisplayMessage(DisplayMessageConstants.SUBSCRIPTION_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE));
			}
			else {
				LOG.info("Subscription Unsuccessful!");
				model.addAttribute("message",
						messageUtils.getDisplayMessage(DisplayMessageConstants.SUBSCRIPTION_UNSUCCESSFUL, DisplayMessageType.ERROR_MESSAGE));
			}
		}
		catch (NonFatalException e) {
			LOG.error("NonfatalException while adding account type. Reason: " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.PAYMENT;
		}
		return JspResolver.LANDING;
	}
}
