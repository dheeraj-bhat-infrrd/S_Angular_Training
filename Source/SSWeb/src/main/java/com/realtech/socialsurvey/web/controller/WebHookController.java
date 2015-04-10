package com.realtech.socialsurvey.web.controller;

// JIRA: SS-15: By RM03

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.WebhookNotification;
import com.realtech.socialsurvey.core.commons.CoreCommon;
import com.realtech.socialsurvey.core.exception.DatabaseException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.payment.Payment;
import com.realtech.socialsurvey.core.utils.MessageUtils;

/**
 * Handles the web hooks and recieves the notifications from Braintree.
 */

@Controller
public class WebHookController {

	@Autowired
	private Payment gateway;
	
	@Autowired
	private MessageUtils messageUtils;
	
	@Autowired
	private CoreCommon commonServices;

	private static final Logger LOG = LoggerFactory.getLogger(WebHookController.class);

	/**
	 * Webhook for verifying the subscription hook by the GET request from Braintree.
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/webhook/subscription", method = RequestMethod.GET)
	public @ResponseBody
	Object subscribeNotification(HttpServletRequest request, HttpServletResponse response) {

		LOG.info("Notification Recieved!");

		String challenge = gateway.getGatewayInstance().webhookNotification().verify(request.getParameter("bt_challenge"));
		LOG.info("Recieved challenge : " + challenge);
		response.setContentType("text/html");
		return challenge;
	}

	/**
	 * Webhook for accepting subscription notifications from Braintree.
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/webhook/subscription", method = RequestMethod.POST)
	public @ResponseBody
	Object getSubscriptionNotifications(Model model, HttpServletRequest request, HttpServletResponse response) {

		LOG.info("Subscription notification recieved!");
		
		String testingFlag = request.getParameter("testing");
		WebhookNotification webhookNotification = null;
		
		if(testingFlag == null || testingFlag.equals("0")){
			webhookNotification = gateway.getGatewayInstance().webhookNotification()
					.parse(request.getParameter("bt_signature"), request.getParameter("bt_payload"));

			LOG.info("Webhook Received " + webhookNotification.getTimestamp().getTime() + " | Kind: " + webhookNotification.getKind()
					+ " | Subscription: " + webhookNotification.getSubscription().getId());
		}
		else {
			
			String kind = request.getParameter("kind");
			String subscriptionId = request.getParameter("subscription_id");
			
			LOG.info("kind : " + kind);
			LOG.info("subscription id : " + subscriptionId);
			
			if(kind == null || kind.isEmpty()){
				return "kind flag is empty";
			}
			
			if(subscriptionId == null || subscriptionId.isEmpty()){
				return "subscription id is empty";
			}
			
			if(kind.equals(WebhookNotification.Kind.SUBSCRIPTION_WENT_PAST_DUE.toString())){
				HashMap<String, String> sampleNotification = gateway.getGatewayInstance().webhookTesting().sampleNotification(
					    WebhookNotification.Kind.SUBSCRIPTION_WENT_PAST_DUE, subscriptionId
					);

				webhookNotification = gateway.getGatewayInstance().webhookNotification().parse(
					    sampleNotification.get("bt_signature"),
					    sampleNotification.get("bt_payload")
					);
				LOG.info("Webhook Received " + webhookNotification.getTimestamp().getTime() + " | Kind: " + webhookNotification.getKind()
						+ " | Subscription: " + webhookNotification.getSubscription().getId());
			}
			else{
				return "Kind not known!";
			}
			
		}
		

		try {

			if (webhookNotification.getKind() == WebhookNotification.Kind.SUBSCRIPTION_WENT_PAST_DUE) {
				gateway.updateRetriesForPayment(webhookNotification.getSubscription());
			}

		}
		catch (InvalidInputException e) {
			LOG.error("WebHookController getSubscriptionNotifications() : InvalidInputException thrown : " + e.getMessage());
			commonServices.sendFailureMail(e);
			return "WebHookController getSubscriptionNotifications() : InvalidInputException thrown : " + e.getMessage();
		}
		catch (UndeliveredEmailException e) {
			LOG.error("WebHookController getSubscriptionNotifications() : UndeliveredEmailException thrown : " + e.getMessage());
			commonServices.sendFailureMail(e);
			return "WebHookController getSubscriptionNotifications() : UndeliveredEmailException thrown : " + e.getMessage();
		}
		catch (NoRecordsFetchedException e) {
			LOG.error("WebHookController getSubscriptionNotifications() : NoRecordsFetchedException thrown : " + e.getMessage());
			commonServices.sendFailureMail(e);
			return "WebHookController getSubscriptionNotifications() : NoRecordsFetchedException thrown : " + e.getMessage();
		}
		catch (DatabaseException e) {
			LOG.error("WebHookController getSubscriptionNotifications() : NoRecordsFetchedException thrown : " + e.getMessage());
			commonServices.sendFailureMail(e);
			return "WebHookController getSubscriptionNotifications() : NoRecordsFetchedException thrown : " + e.getMessage();
		}

		LOG.info("Subscription Notification handled!");
		return new ResponseEntity<String>("Notification recieved and processed!", HttpStatus.OK);

	}

}
