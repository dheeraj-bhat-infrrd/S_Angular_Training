package com.realtech.socialsurvey.web.controller;
//JIRA: SS-15: By RM03

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.braintreegateway.WebhookNotification;
import com.realtech.socialsurvey.core.services.payment.Payment;

/**
 * Handles the web hooks and recieves the notifications from Braintree.
 *
 */

@Controller
public class WebHookController {
	
	@Autowired
	Payment gateway;
	
	private static final Logger LOG = LoggerFactory.getLogger(WebHookController.class);
	
	/**
	 * Webhook for verifying the subscription hook by the GET request from Braintree.
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/webhook/subscription",method=RequestMethod.GET)
	public @ResponseBody Object subscribeNotification(HttpServletRequest request,HttpServletResponse response){
		
		LOG.info("Notification Recieved!");
		gateway.initialise();
				
		String challenge = gateway.initialise().webhookNotification().verify(request.getParameter("bt_challenge"));
		LOG.info("Recieved challenge : " + challenge);
		response.setContentType("text/html");		
		return challenge;
	}
	
	/**
	 * Webhook for accepting subscription notifications from Braintree.
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/webhook/subscription",method=RequestMethod.POST)
	public String getSubscriptionNotifications(HttpServletRequest request,HttpServletResponse response){
		
		gateway.initialise();
		WebhookNotification webhookNotification = gateway.initialise().webhookNotification().parse(
			      request.getParameter("bt_signature"),
			      request.getParameter("bt_payload")
				);
		LOG.info("Webhook Received " + webhookNotification.getTimestamp().getTime() + " | Kind: " + webhookNotification.getKind() + " | Subscription: " + webhookNotification.getSubscription().getId());
		response.setStatus(200);
		return("");
		
	}

}
