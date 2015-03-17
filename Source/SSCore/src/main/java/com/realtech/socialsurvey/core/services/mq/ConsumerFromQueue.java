package com.realtech.socialsurvey.core.services.mq;

import com.realtech.socialsurvey.core.entities.EmailEntity;

/**
 * Consumes messages from queue
 *
 */
public interface ConsumerFromQueue {

	/**
	 * Fetches the email entity from the queue
	 * @param topicName topicName can be null in which case the default topic will be used for the service
	 * @return email entity
	 */
	public EmailEntity dequeueEmail(String topicName);
}
