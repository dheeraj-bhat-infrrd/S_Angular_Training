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

import com.braintreegateway.WebhookNotification;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.CoreCommon;
import com.realtech.socialsurvey.core.exception.DatabaseException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.payment.Payment;
import com.realtech.socialsurvey.core.services.payment.exception.PaymentException;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
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
	
	@Autowired
    private EmailServices emailServices;
	

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

		if (testingFlag == null || testingFlag.equals("0")) {
			webhookNotification = gateway.getGatewayInstance().webhookNotification()
					.parse(request.getParameter("bt_signature"), request.getParameter("bt_payload"));
			
			LOG.info("Webhook Notification: "+webhookNotification.toString());

			LOG.info("Webhook Received " + webhookNotification.getTimestamp().getTime() + " | Kind: " + webhookNotification.getKind()
					+ " | Subscription: " + webhookNotification.getSubscription().getId());
		}
		else {

			String kind = request.getParameter("kind");
			String subscriptionId = request.getParameter("subscription_id");

			LOG.info("kind : " + kind);
			LOG.info("subscription id : " + subscriptionId);

			if (kind == null || kind.isEmpty()) {
				return "kind flag is empty";
			}

			if (subscriptionId == null || subscriptionId.isEmpty()) {
				return "subscription id is empty";
			}
			
			HashMap<String, String> sampleNotification = null;

			if (kind.equals(WebhookNotification.Kind.SUBSCRIPTION_WENT_PAST_DUE.toString())) {
				sampleNotification = gateway.getGatewayInstance().webhookTesting()
						.sampleNotification(WebhookNotification.Kind.SUBSCRIPTION_WENT_PAST_DUE, subscriptionId);
			}
			else if (kind.equals(WebhookNotification.Kind.SUBSCRIPTION_CHARGED_UNSUCCESSFULLY.toString())) {
				sampleNotification = gateway.getGatewayInstance().webhookTesting()
						.sampleNotification(WebhookNotification.Kind.SUBSCRIPTION_CHARGED_UNSUCCESSFULLY, subscriptionId);
			}
			else if (kind.equals(WebhookNotification.Kind.SUBSCRIPTION_CHARGED_SUCCESSFULLY.toString())) {
				sampleNotification = gateway.getGatewayInstance().webhookTesting()
						.sampleNotification(WebhookNotification.Kind.SUBSCRIPTION_CHARGED_SUCCESSFULLY, subscriptionId);
			}else if (kind.equals(WebhookNotification.Kind.SUBSCRIPTION_CANCELED.toString())){
			 	
			}else if (kind.equals(WebhookNotification.Kind.SUBSCRIPTION_EXPIRED.toString())){
				// TODO: Implement
				
			}else if (kind.equals(WebhookNotification.Kind.SUBSCRIPTION_TRIAL_ENDED.toString())){
				// TODO: Implement
				
			}else if (kind.equals(WebhookNotification.Kind.SUBSCRIPTION_WENT_ACTIVE.toString())){
				// TODO: Implement
				
			}else if (kind.equals(WebhookNotification.Kind.DISPUTE_OPENED.toString())){
				// TODO: Implement
				
			}else if (kind.equals(WebhookNotification.Kind.DISPUTE_WON.toString())){
				// TODO: Implement
				
			}else if (kind.equals(WebhookNotification.Kind.DISPUTE_LOST.toString())){
				// TODO: Implement
				
			}else {
				return "Kind not known!";
			}
			webhookNotification = gateway.getGatewayInstance().webhookNotification()
					.parse(sampleNotification.get("bt_signature"), sampleNotification.get("bt_payload"));
			LOG.info("Webhook Received " + webhookNotification.getTimestamp().getTime() + " | Kind: " + webhookNotification.getKind()
					+ " | Subscription: " + webhookNotification.getSubscription().getId());

		}
		
		
		try {
			if (webhookNotification.getKind() == WebhookNotification.Kind.SUBSCRIPTION_WENT_PAST_DUE) {
				// gateway.changeLicenseToPastDue(webhookNotification.getSubscription());
				gateway.intimateUser(webhookNotification.getSubscription(), CommonConstants.SUBSCRIPTION_WENT_PAST_DUE);
			}
			else if (webhookNotification.getKind() == WebhookNotification.Kind.SUBSCRIPTION_CHARGED_UNSUCCESSFULLY) {
				// gateway.incrementRetriesAndSendMail(webhookNotification.getSubscription());
				gateway.intimateUser(webhookNotification.getSubscription(), CommonConstants.SUBSCRIPTION_CHARGED_UNSUCCESSFULLY);
			}
			else if (webhookNotification.getKind() == WebhookNotification.Kind.SUBSCRIPTION_CHARGED_SUCCESSFULLY) {
				// gateway.checkIfCompanyIsDisabledOrSubscriptionIsPastDueAndEnableIt(webhookNotification.getSubscription());
				gateway.intimateUser(webhookNotification.getSubscription(), CommonConstants.SUBSCRIPTION_CHARGED_SUCCESSFULLY);
			}
			else if(webhookNotification.getKind() == WebhookNotification.Kind.SUBSCRIPTION_CANCELED){
			    gateway.intimateUser(webhookNotification.getSubscription(), CommonConstants.SUBSCRIPTION_CANCELED);
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
			LOG.error("WebHookController getSubscriptionNotifications() : DatabaseException thrown : " + e.getMessage());
			commonServices.sendFailureMail(e);
			return "WebHookController getSubscriptionNotifications() : DatabaseException thrown : " + e.getMessage();
		} catch ( PaymentException e ) {
		    LOG.error("WebHookController getSubscriptionNotifications() : PaymentException thrown : " + e.getMessage());
            commonServices.sendFailureMail(e);
            return "WebHookController getSubscriptionNotifications() : PaymentException thrown : " + e.getMessage();
        } catch ( SolrException e ) {
            LOG.error("WebHookController getSubscriptionNotifications() : SolrException thrown : " + e.getMessage());
            commonServices.sendFailureMail(e);
            return "WebHookController getSubscriptionNotifications() : SolrException thrown : " + e.getMessage();
        }

		LOG.info("Subscription Notification handled!");
		return new ResponseEntity<String>("Notification recieved and processed!", HttpStatus.OK);

	}

}
