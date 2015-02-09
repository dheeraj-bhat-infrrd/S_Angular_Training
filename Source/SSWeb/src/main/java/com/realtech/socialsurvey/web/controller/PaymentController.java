package com.realtech.socialsurvey.web.controller;

// JIRA: SS-15: By RM03

import java.util.Map;
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
import org.springframework.web.bind.annotation.ResponseBody;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.payment.Payment;
import com.realtech.socialsurvey.core.services.payment.exception.CardUpdateUnsuccessfulException;
import com.realtech.socialsurvey.core.services.payment.exception.CreditCardException;
import com.realtech.socialsurvey.core.services.payment.exception.PaymentException;
import com.realtech.socialsurvey.core.services.payment.exception.SubscriptionUnsuccessfulException;
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
	private UserManagementService userManagementService;

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

		LOG.info("Returning payment page with client token");
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

		LOG.info("Payment controller called for plan subscribal");
		try {
			
			String strAccountType = request.getParameter(CommonConstants.ACCOUNT_TYPE_IN_SESSION);
			// Get the nonce from the request
			String nonce = request.getParameter(CommonConstants.PAYMENT_NONCE);

			// Extract the session
			HttpSession session = request.getSession(false);

			// Get the user object from the session and the company object from it
			User user = sessionHelper.getCurrentUser();
			Company company = user.getCompany();
			AccountType accountType = null;

			if (strAccountType == null || strAccountType.isEmpty()) {
				throw new InvalidInputException("Account type parameter passed is null or empty", DisplayMessageConstants.GENERAL_ERROR);
			}

			int accountTypeValue = 0;
			try {
				accountTypeValue = Integer.parseInt(strAccountType);
			}
			catch (NumberFormatException e) {
				throw new InvalidInputException("Error while parsing account type ", DisplayMessageConstants.GENERAL_ERROR, e);
			}

			try {
				gateway.subscribe(user, company, accountTypeValue, nonce);
			}
			catch (InvalidInputException e) {
				LOG.error("PaymentController subscribeForPlan() : InvalidInput Exception thrown : " + e.getMessage(), e);
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e);
			}
			catch (PaymentException e) {
				LOG.error("PaymentController subscribeForPlan() : Payment Exception thrown : " + e.getMessage(), e);
				throw new PaymentException(e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e);
			}
			catch (CreditCardException e) {
				LOG.error("PaymentController subscribeForPlan() : CreditCardException thrown : " + e.getMessage(),e);
				throw new CreditCardException("PaymentController subscribeForPlan() : CreditCardException thrown : " + e.getMessage(),DisplayMessageConstants.CREDIT_CARD_INVALID,e);
			}
			catch (SubscriptionUnsuccessfulException e) {
				LOG.error("PaymentController subscribeForPlan() : SubscriptionUnsuccessfulException thrown : " + e.getMessage(),e);
				throw new CreditCardException("PaymentController subscribeForPlan() : SubscriptionUnsuccessfulException thrown : " + e.getMessage(),DisplayMessageConstants.SUBSCRIPTION_UNSUCCESSFUL,e);
			}
			
			LOG.info("Subscription Successful!");
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
				userManagementService.updateProfileCompletionStage(user, CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID,
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
		catch (NonFatalException e) {
			LOG.error("NonfatalException while adding account type. Reason: " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.ACCOUNT_TYPE_SELECTION;
		}
		return JspResolver.LANDING;
	}

	@RequestMapping(value = "/paymentchange", method = RequestMethod.GET)
	public Object paymentChangePage(Model model, HttpServletRequest request, HttpServletResponse response) {

		LOG.info("Payment controller called for payment change page");
		// Fetch current user in session
		LOG.debug("Fetching the current user in session");
		User user = sessionHelper.getCurrentUser();
		Map<String, String> currentPaymentDetails = null;

		// Next we fetch the current payment details of user.
		try {
			LOG.debug("Fetching users current card details");
			currentPaymentDetails = gateway.getCurrentPaymentDetails(user.getCompany().getLicenseDetails().get(CommonConstants.INITIAL_INDEX)
					.getSubscriptionId());
		}
		catch (InvalidInputException | NoRecordsFetchedException e) {
			LOG.error("Exception caught : message : " + e.getMessage());
			LOG.info("Setting message div and returning the jsp");
			model.addAttribute("messageFlag", CommonConstants.STATUS_ACTIVE);
			model.addAttribute("messageBody", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.PAYMENT;
		}
		catch (PaymentException e) {
			LOG.error("Exception caught : message : " + e.getMessage());
			LOG.info("Setting message div and returning the jsp");
			model.addAttribute("messageFlag", CommonConstants.STATUS_ACTIVE);
			model.addAttribute("messageBody", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.PAYMENT;
		}

		// Add the parameters to the response
		LOG.debug("Adding attributes");
		model.addAttribute("clienttoken", gateway.getClientToken());
		model.addAllAttributes(currentPaymentDetails);
		model.addAttribute(CommonConstants.PAYMENT_CHANGE_FLAG, CommonConstants.STATUS_ACTIVE);
		LOG.info("Returning payment jsp page");
		return JspResolver.PAYMENT;

	}

	@RequestMapping(value = "/paymentupgrade", method = RequestMethod.POST)
	@ResponseBody
	public String paymentUpgrade(Model model, HttpServletRequest request) {
		LOG.info("Payment controller called to upgrade payment method");
		
		String message = null;
		String paymentNonce = request.getParameter(CommonConstants.PAYMENT_NONCE);
		LOG.info("Payment upgrade called with nonce : " + paymentNonce);

		// Fetching the user from session
		LOG.debug("Fetching user from session");
		User user = sessionHelper.getCurrentUser();
		// Getting the company and the license detail object
		Company company = user.getCompany();
		LicenseDetail licenseDetail = company.getLicenseDetails().get(CommonConstants.INITIAL_INDEX);

		LOG.info("Making API call to update card details");
		try {
			gateway.changePaymentMethod(licenseDetail.getSubscriptionId(), paymentNonce, String.valueOf(company.getCompanyId()));
		}
		catch (InvalidInputException | NoRecordsFetchedException e) {
			LOG.error("Exception has occured : " + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
			return message;
		}
		catch (PaymentException e) {
			LOG.error("Exception has occured : " + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
			return message;
		}
		catch (CreditCardException e) {
			LOG.error("Exception has occured : " + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
			return message;
		}
		catch (CardUpdateUnsuccessfulException e) {
			LOG.error("Exception has occured : " + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.PAYMENT_GATEWAY_EXCEPTION, DisplayMessageType.ERROR_MESSAGE).getMessage();
			return message;
		}

		LOG.info("Payment details change successful! Returning message");
		message = messageUtils.getDisplayMessage(DisplayMessageConstants.CARD_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE).getMessage();

		return message;

	}
}
