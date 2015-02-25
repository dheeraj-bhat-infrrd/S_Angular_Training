package com.realtech.socialsurvey.core.services.mq.impl;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import com.realtech.socialsurvey.core.enums.EmailHeader;
import com.realtech.socialsurvey.core.services.mq.ProducerForQueue;

/**
 * Queues into Kafka.
 *
 */
public class KafkaProducerImpl implements ProducerForQueue, InitializingBean {
	
	public static final Logger LOG = LoggerFactory.getLogger(KafkaProducerImpl.class);
	
	private static final String DEFAULT_EMAIL_TOPIC = "email_topic";

	@Override
	public void queueEmail(EmailHeader header, List<String> recipientMailIds, String content) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		LOG.debug(arg0);
		
	}
	
}
