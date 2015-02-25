package com.realtech.socialsurvey.core.services.mq;

import java.util.List;
import com.realtech.socialsurvey.core.enums.EmailHeader;

/**
 * Produces the messages to be put in queues
 *
 */
public interface ProducerForQueue {

	/**
	 * Queues the mails in the given topic
	 * @param header
	 * @param recipientMailIds
	 * @param content
	 */
	public void queueEmail(EmailHeader header, List<String> recipientMailIds, String content);
}
