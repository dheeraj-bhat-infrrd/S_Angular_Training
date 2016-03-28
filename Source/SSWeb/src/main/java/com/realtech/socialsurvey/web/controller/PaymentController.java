package com.realtech.socialsurvey.web.controller;

// JIRA: SS-15: By RM03

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
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
	
	@Autowired
	private EmailServices emailServices;
	
    @Value ( "${APPLICATION_SUPPORT_EMAIL}")
    private String supportMail;

	/**
	 * Method used to display the Braintree form to get card details.
	 * 
	 * @param model
	 * @param response
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/paymentpage")
	public String paymentPage(Model model, HttpServletResponse response, HttpServletRequest request) {
		LOG.info("Returning payment page with client token");
		
		LOG.debug("Getting the account type from the request");
		String strAccountType = request.getParameter("accounttype");

		try{			
			if (strAccountType == null || strAccountType.isEmpty()) {
				LOG.error("Account type passed is null or empty while returning the payment page");
				throw new InvalidInputException("Account type passed is null or empty while returning the payment page");
			}
		}
		catch (NonFatalException e) {
			LOG.error("NonfatalException while returning the payment page. Reason: " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.MESSAGE_HEADER;
		}		
		
		LOG.debug("Adding the model attributes");
		model.addAttribute("accounttype", strAccountType);
		model.addAttribute(CommonConstants.PAID_PLAN_UPGRADE_FLAG, CommonConstants.YES);
		model.addAttribute("clienttoken", gateway.getClientToken());
		
		LOG.info("Returning the payment page");
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
	public String subscribeForPlan(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {

		LOG.info("Payment controller called for plan subscribal");
		String skipPayment = request.getParameter("skipPayment");
		try {
			String strAccountType = request.getParameter(CommonConstants.ACCOUNT_TYPE_IN_SESSION);
			// Get the nonce from the request
			String nonce = request.getParameter(CommonConstants.PAYMENT_NONCE);

			// Get the user object from the session and the company object from it
			User user = sessionHelper.getCurrentUser();

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
				gateway.subscribe(user, accountTypeValue, nonce);
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
				LOG.error("PaymentController subscribeForPlan() : CreditCardException thrown : " + e.getMessage(), e);
				throw new CreditCardException("PaymentController subscribeForPlan() : CreditCardException thrown : " + e.getMessage(),
						DisplayMessageConstants.CREDIT_CARD_INVALID, e);
			}
			catch (SubscriptionUnsuccessfulException e) {
				LOG.error("PaymentController subscribeForPlan() : SubscriptionUnsuccessfulException thrown : " + e.getMessage(), e);
				if (e.getErrorCode().equals(DisplayMessageConstants.BANK_REJECTED)) {
					throw new SubscriptionUnsuccessfulException("PaymentController subscribeForPlan() : SubscriptionUnsuccessfulException thrown : "
							+ e.getMessage(), DisplayMessageConstants.BANK_REJECTED, e);
				}
				throw new SubscriptionUnsuccessfulException("PaymentController subscribeForPlan() : SubscriptionUnsuccessfulException thrown : "
						+ e.getMessage(), DisplayMessageConstants.SUBSCRIPTION_UNSUCCESSFUL, e);
			}
			LOG.info("Subscription Successful!");
			
			// Now we update the stage after payment is done and before the setting up the account.
			try {
				/**
				 * For each account type, only the company admin's profile completion stage is
				 * updated, all the other profiles created by default need no action so their
				 * profile completion stage is marked completed at the time of insert
				 */
				LOG.debug("Calling sevices for updating profile completion stage");
				userManagementService.updateProfileCompletionStage(user, CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID,
						CommonConstants.PRE_PROCESSING_BEFORE_LOGIN_STAGE);
				LOG.debug("Successfully executed sevices for updating profile completion stage");
			}
			catch (InvalidInputException e) {
				LOG.error("InvalidInputException while updating profile completion stage. Reason : " + e.getMessage(), e);
				throw new InvalidInputException(e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e);
			}
		     
            //TODO: specify details
            String details = "First Name : " + user.getFirstName() + "<br/>" +
                "Last Name : " + user.getLastName() + "<br/>" + 
                "Email Address : "  + user.getEmailId() + "<br/>" +               
                "Company Name : " + user.getCompany().getCompany() + "<br/>" + 
                "Account Type : " + strAccountType;
            try {
                emailServices.sendCompanyRegistrationStageMail( supportMail,
                    CommonConstants.COMPANY_REGISTRATION_STAGE_COMPLETE, user.getCompany().getCompany(), details );
            } catch ( InvalidInputException e ) {
                e.printStackTrace();
            } catch ( UndeliveredEmailException e ) {
                e.printStackTrace();
            }
		}
		catch (NonFatalException e) {
			LOG.error("NonfatalException while adding account type. Reason: " + e.getMessage(), e);
			redirectAttributes.addFlashAttribute("skipPayment", skipPayment);
			redirectAttributes.addFlashAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return "redirect:/" + JspResolver.ACCOUNT_TYPE_SELECTION_PAGE + ".do";
		}
		
		return "redirect:./" + CommonConstants.PRE_PROCESSING_BEFORE_LOGIN_STAGE;
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
			if(e.getErrorCode().equals(DisplayMessageConstants.BANK_REJECTED)){
				message = messageUtils.getDisplayMessage(DisplayMessageConstants.BANK_REJECTED, DisplayMessageType.ERROR_MESSAGE).getMessage();
			}
			else {
				message = messageUtils.getDisplayMessage(DisplayMessageConstants.PAYMENT_GATEWAY_EXCEPTION, DisplayMessageType.ERROR_MESSAGE).getMessage();
			}
			return message;
		}

		LOG.info("Payment details change successful! Returning message");
		message = messageUtils.getDisplayMessage(DisplayMessageConstants.CARD_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE).getMessage();

		return message;

	}
	
//	private String makeJsonMessage(int status, String message) {
//
//		JSONObject jsonMessage = new JSONObject();
//		LOG.debug("Building json response");
//		try {
//			jsonMessage.put("success", status);
//			jsonMessage.put("message", message);
//		}
//		catch (JSONException e) {
//			LOG.error("Exception occured while building json response : " + e.getMessage(), e);
//		}
//
//		LOG.info("Returning json response : " + jsonMessage.toString());
//		return jsonMessage.toString();
//	}
	
//	@RequestMapping(value = "/upgradecardfordisabledaccount", method = RequestMethod.POST)
//	@ResponseBody
//	public String upgradeCardForDisabledAccount(Model model, HttpServletRequest request) {
//		LOG.info("Payment controller called to upgrade payment method");
//		
//		String message = null;
//		String paymentNonce = request.getParameter(CommonConstants.PAYMENT_NONCE);
//		LOG.info("Payment upgrade called with nonce : " + paymentNonce);
//
//		// Fetching the user from session
//		LOG.debug("Fetching user from session");
//		User user = sessionHelper.getCurrentUser();
//		// Getting the company and the license detail object
//		Company company = user.getCompany();
//		LicenseDetail licenseDetail = company.getLicenseDetails().get(CommonConstants.INITIAL_INDEX);
//
//		LOG.info("Making API call to update card details");
//		try {
//			gateway.changePaymentMethod(licenseDetail.getSubscriptionId(), paymentNonce, String.valueOf(company.getCompanyId()));
//			organizationManagementService.changeCompanyStatusToPaymentProcessing(company);
//		}
//		catch (InvalidInputException | NoRecordsFetchedException e) {
//			LOG.error("Exception has occured : " + e.getMessage(), e);
//			message = messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
//			return makeJsonMessage(CommonConstants.STATUS_INACTIVE, message);
//		}
//		catch (PaymentException e) {
//			LOG.error("Exception has occured : " + e.getMessage(), e);
//			message = messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
//			return makeJsonMessage(CommonConstants.STATUS_INACTIVE, message);
//		}
//		catch (CreditCardException e) {
//			LOG.error("Exception has occured : " + e.getMessage(), e);
//			message = messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
//			return makeJsonMessage(CommonConstants.STATUS_INACTIVE, message);
//		}
//		catch (CardUpdateUnsuccessfulException e) {
//			LOG.error("Exception has occured : " + e.getMessage(), e);
//			message = messageUtils.getDisplayMessage(DisplayMessageConstants.PAYMENT_GATEWAY_EXCEPTION, DisplayMessageType.ERROR_MESSAGE).getMessage();
//			return makeJsonMessage(CommonConstants.STATUS_INACTIVE, message);
//		}
//		
//		LOG.info("Payment details change successful! Returning message");
//		message = messageUtils.getDisplayMessage(DisplayMessageConstants.RETRY_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE).getMessage();
//
//		return makeJsonMessage(CommonConstants.STATUS_ACTIVE, message);
//	}
	
//	@RequestMapping(value="/disabledaccountretry",method=RequestMethod.GET)
//	@ResponseBody
//	public String retryChargeForDisabledAccount(HttpServletRequest request){
//		LOG.info("retryChargeForDisabledAccount called to retry payment");
//		String message = null;
//		try {
//			
//			User user = sessionHelper.getCurrentUser();
//			if(user == null){
//				LOG.error("User not found in the session!");
//				throw new InvalidInputException("User not found in the session!");
//			}
//			
//			Company company = user.getCompany();
//			if(company == null){
//				LOG.error("company not found in the session!");
//				throw new InvalidInputException("company not found in the session!");
//			}
//			
//			if(company.getLicenseDetails() == null || company.getLicenseDetails().isEmpty()){
//				LOG.error("license details not found for company id : " + company.getCompanyId());
//				throw new InvalidInputException("license details not found for company id : " + company.getCompanyId());
//			}
//			gateway.retryChargeForSubscription(company.getLicenseDetails().get(CommonConstants.INITIAL_INDEX));
//			organizationManagementService.changeCompanyStatusToPaymentProcessing(company);
//			message = messageUtils.getDisplayMessage(DisplayMessageConstants.RETRY_SUCCESSFUL, DisplayMessageType.ERROR_MESSAGE).getMessage();
//		}
//		catch (InvalidInputException | NoRecordsFetchedException | PaymentRetryUnsuccessfulException e){
//			LOG.error("Exception has occured : " + e.getMessage(), e);
//			message = messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
//			return makeJsonMessage(CommonConstants.STATUS_INACTIVE, message);
//		}		
//		
//		return makeJsonMessage(CommonConstants.STATUS_ACTIVE, message);
//	}
}
