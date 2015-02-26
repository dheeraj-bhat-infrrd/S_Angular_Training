package com.realtech.socialsurvey.core.services.mq;

import com.realtech.socialsurvey.core.enums.EmailHeader;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

/**
 * Produces the messages to be put in queues
 *
 */
public interface ProducerForQueue {

	/**
	 * Queues the mails in the given topic
	 * @param header
	 * @param content
	 */
	public void queueEmail(EmailHeader header, String content) throws InvalidInputException;
}
