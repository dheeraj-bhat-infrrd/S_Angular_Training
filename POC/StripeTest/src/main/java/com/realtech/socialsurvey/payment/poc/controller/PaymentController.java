/**
 * 
 */
package com.realtech.socialsurvey.payment.poc.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.realtech.socialsurvey.payment.poc.constants.PaymentConstants;
import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Customer;
import com.stripe.model.Subscription;

/**
 * @author Sameer
 * 
 */
@Controller
@RequestMapping("/payment")
public class PaymentController {

	/**
	 * This method initiates the payment process by landing on payment details
	 * page.
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/initiate", method = RequestMethod.GET)
	public String initiatePayment(ModelMap model) {

		model.addAttribute(PaymentConstants.PUBLISH_KEY_STR,
				PaymentConstants.PUBLISH_KEY_VALUE);
		return PaymentConstants.PAYMENT_PAGE;

	}

	/**
	 * This method is used to create the customer and subscription for recurring
	 * billing.
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/pay", method = RequestMethod.POST)
	public String doPayment(ModelMap model, HttpServletRequest request) {
		String page = PaymentConstants.SUCCESS_PAGE;
		try {
			// stripe token contains the customer card details in encrypted
			// form.
			String token = request.getParameter(PaymentConstants.STRIPE_TOKEN);

			model.addAttribute(PaymentConstants.MESSAGE,
					PaymentConstants.SUCCESS);
			model.addAttribute(PaymentConstants.STRIPE_TOKEN, token);

			// The stripe api key determines the stripe account we are using.
			Stripe.apiKey = PaymentConstants.STRIPE_API_KEY;

			// creating a customer in stripe account.
			Map<String, Object> customerParams = new HashMap<String, Object>();
			customerParams.put(PaymentConstants.DESCRIPTION,
					"Customer for sameer.vaidya@raremile.com");
			customerParams.put(PaymentConstants.CARD, token); // obtained with
																// Stripe.js
			customerParams.put(PaymentConstants.EMAIL,
					request.getParameter(PaymentConstants.EMAIL));

			Customer customer = Customer.create(customerParams);

			// Creating a subscription for enabling recurring billing.
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(PaymentConstants.PLAN, PaymentConstants.PLAN_NAME);
			Subscription subscription = customer.createSubscription(params);

			model.addAttribute(PaymentConstants.SUB_ID, subscription.getId());

		} catch (AuthenticationException e) {
			e.printStackTrace();
			model.addAttribute(PaymentConstants.FAILURE_MSG,
					"Failure !!!! Stripe was not able to authenticate the customer details");
			page = PaymentConstants.FAILURE_PAGE;
		} catch (InvalidRequestException e) {
			e.printStackTrace();
			model.addAttribute(
					PaymentConstants.FAILURE_MSG,
					"Failure !!!! The initial setup is not configured for the customer .. Ex: No plan found, card was not found ");
			page = PaymentConstants.FAILURE_PAGE;
		} catch (APIConnectionException e) {
			e.printStackTrace();
			model.addAttribute(PaymentConstants.FAILURE_MSG,
					"Failure !!!! Stripe was not able to connect to apis");
			page = PaymentConstants.FAILURE_PAGE;
		} catch (CardException e) {
			e.printStackTrace();
			model.addAttribute(PaymentConstants.FAILURE_MSG,
					"Failure !!!! The card details are invalid");
			page = PaymentConstants.FAILURE_PAGE;
		} catch (APIException e) {
			e.printStackTrace();
			model.addAttribute(PaymentConstants.FAILURE_MSG,
					"Failure !!!! The stripe api did not return the response as expected.");
			page = PaymentConstants.FAILURE_PAGE;
		}
		return page;
	}
}
