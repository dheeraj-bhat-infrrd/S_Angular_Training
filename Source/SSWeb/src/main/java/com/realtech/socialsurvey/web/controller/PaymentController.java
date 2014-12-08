package com.realtech.socialsurvey.web.controller;
//JIRA: SS-15: By RM03

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
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.payment.Payment;
import com.realtech.socialsurvey.core.services.registration.RegistrationService;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.common.JspResolver;

/**
 * Handles the payment actions by user
 *
 */
@Controller
public class PaymentController {
	
	private static final Logger LOG = LoggerFactory.getLogger(PaymentController.class);
	
	@Autowired
	private Payment gateway;
	
	@Autowired
	private RegistrationService registrationService;
	
	@Autowired
	private MessageUtils messageUtils;	
	
	/**
	 * Method used to display the Braintree form to get card details.
	 * @param model
	 * @param response
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/payment")
	public String paymentPage(Model model,HttpServletResponse response,HttpServletRequest request){
		
		LOG.info("Payment Step 1.");
		gateway.initialise();
		model.addAttribute("clienttoken", gateway.getClientToken());
		return JspResolver.PAYMENT;
	}
	
	/**
	 * Method for a user to subscribe for a plan.
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/subscribe", method= RequestMethod.POST)
	public String subscribeForPlan(Model model, HttpServletRequest request,HttpServletResponse response){
		
		LOG.info("Payment Step 2.");
		
		boolean status=false;		
		gateway.initialise();
		
		//Extract the session
		HttpSession session = request.getSession(false);
				
		//Get the user object from the session and the company object from it
		User user = (User) session.getAttribute(CommonConstants.USER_IN_SESSION);
		Company company = user.getCompany();
		
		//Get the planId from the session
		int planId = Integer.parseInt(request.getParameter("accounttype"));
		
		//Get the nonce from the request
		String nonce = request.getParameter("payment_method_nonce");
		
		try{
			status = gateway.subscribe(user,company, planId, nonce);
		}
		catch(InvalidInputException e){
			LOG.error("PaymentController subscribeForPlan() : InvalidInput Exception thrown : " + messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.MESSAGE_HEADER;
			
		}
		catch (NonFatalException e) {
			LOG.error("PaymentController subscribeForPlan() : NonFatalException : " + messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.MESSAGE_HEADER;
		}
		
		if(status){
			LOG.info("Subscription Successful!");
			try {
				registrationService.updateProfileCompletionStage(user, CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID, CommonConstants.LOGIN_STAGE);
			}
			catch (InvalidInputException invalidInputException) {
				LOG.error("PaymentController subscribeForPlan() : NonFatalException : " + messageUtils.getDisplayMessage(invalidInputException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
				model.addAttribute("message", messageUtils.getDisplayMessage(invalidInputException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
				return JspResolver.MESSAGE_HEADER;
			}
			model.addAttribute("message",messageUtils.getDisplayMessage(DisplayMessageConstants.SUBSCRIPTION_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE));
			return JspResolver.LOGIN;
		}
		else{
			LOG.info("Subscription Unsuccessful!");
			model.addAttribute("message",messageUtils.getDisplayMessage(DisplayMessageConstants.SUBSCRIPTION_UNSUCCESSFUL, DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.MESSAGE_HEADER;
		}
		
	}

}
