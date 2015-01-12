package com.realtech.socialsurvey.web.controller;

// JIRA: SS-15: By RM03

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.braintreegateway.WebhookNotification;
import com.realtech.socialsurvey.core.exception.DatabaseException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.payment.Payment;
import com.realtech.socialsurvey.core.utils.MessageUtils;

/**
 * Handles the web hooks and recieves the notifications from Braintree.
 */

@Controller
public class WebHookController {

	@Autowired
	Payment gateway;

	@Autowired
	MessageUtils messageUtils;

	@Autowired
	EmailServices emailServices;

	@Value("${ADMIN_EMAIL_ID}")
	private String recipientMailId;

	private void sendFailureMail(Exception e) {

		LOG.debug("Sending failure mail to recpient : " + recipientMailId);
		String stackTrace = ExceptionUtils.getFullStackTrace(e);
		// replace all dollars in the stack trace with \$
		stackTrace = stackTrace.replace("$", "\\$");

		try {
			emailServices.sendFatalExceptionEmail(recipientMailId, stackTrace);
			LOG.debug("Failure mail sent to admin.");
		}
		catch (InvalidInputException | UndeliveredEmailException e1) {
			LOG.error("CustomItemProcessor : Exception caught when sending Fatal Exception mail. Message : " + e1.getMessage());
		}
	}

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

		WebhookNotification webhookNotification = gateway.getGatewayInstance().webhookNotification()
				.parse(request.getParameter("bt_signature"), request.getParameter("bt_payload"));

		LOG.info("Webhook Received " + webhookNotification.getTimestamp().getTime() + " | Kind: " + webhookNotification.getKind()
				+ " | Subscription: " + webhookNotification.getSubscription().getId());

		try {

			if (webhookNotification.getKind() == WebhookNotification.Kind.SUBSCRIPTION_WENT_PAST_DUE) {
				gateway.updateRetriesForPayment(webhookNotification.getSubscription());
			}

		}
		catch (InvalidInputException e) {
			LOG.error("WebHookController getSubscriptionNotifications() : InvalidInputException thrown : " + e.getMessage());
			sendFailureMail(e);
			return null;
		}
		catch (UndeliveredEmailException e) {
			LOG.error("WebHookController getSubscriptionNotifications() : UndeliveredEmailException thrown : " + e.getMessage());
			sendFailureMail(e);
			return null;
		}
		catch (NoRecordsFetchedException e) {
			LOG.error("WebHookController getSubscriptionNotifications() : NoRecordsFetchedException thrown : " + e.getMessage());
			sendFailureMail(e);
			return null;
		}
		catch (DatabaseException e) {
			LOG.error("WebHookController getSubscriptionNotifications() : NoRecordsFetchedException thrown : " + e.getMessage());
			sendFailureMail(e);
			return null;
		}

		LOG.info("Subscription Notification handled!");
		return new ResponseEntity<String>("Notification recieved!", HttpStatus.OK);

	}

}
