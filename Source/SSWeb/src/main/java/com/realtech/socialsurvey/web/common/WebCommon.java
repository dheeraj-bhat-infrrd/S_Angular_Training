package com.realtech.socialsurvey.web.common;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;

@Component
public class WebCommon {
	
	@Autowired
	private EmailServices emailServices;

	@Value("${ADMIN_EMAIL_ID}")
	private String recipientMailId;
	
	private static final Logger LOG = LoggerFactory.getLogger(WebCommon.class);

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

}
