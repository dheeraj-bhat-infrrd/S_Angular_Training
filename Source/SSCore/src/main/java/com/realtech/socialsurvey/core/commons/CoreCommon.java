package com.realtech.socialsurvey.core.commons;
// RM03
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;

/**
 * Contains methods used in all projects.
 *
 */
@Component
public class CoreCommon {
	
	@Value("${ADMIN_EMAIL_ID}")
	private String recipientMailId;
	
	@Autowired
	private EmailServices emailServices;
	
	private static final Logger LOG = LoggerFactory.getLogger(CoreCommon.class);

	/**
	 * Sends a failure mail with exception details to the admin.
	 * @param e
	 */
	public void sendFailureMail(Exception e) {

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
	
	/**
	 * Send the mail to admin when an email sending fails with the email id and the name to whom
	 * the email had to be sent to.
	 * @param destinationMailId
	 * @param displayName
	 * @param e
	 */
	public void sendEmailSendingFailureMail(String destinationMailId,String displayName,Exception e) {

		LOG.debug("Sending failure mail to recpient : " + recipientMailId);
		String stackTrace = ExceptionUtils.getFullStackTrace(e);
		// replace all dollars in the stack trace with \$
		stackTrace = stackTrace.replace("$", "\\$");

		try {
			emailServices.sendEmailSendingFailureMail(recipientMailId, destinationMailId, displayName, stackTrace);;
			LOG.debug("Failure mail sent to admin.");
		}
		catch (InvalidInputException | UndeliveredEmailException e1) {
			LOG.error("CustomItemProcessor : sendEmailSendingFailureMail : Exception caught when sending Exception mail. Message : " + e1.getMessage());
		}
	}

}
