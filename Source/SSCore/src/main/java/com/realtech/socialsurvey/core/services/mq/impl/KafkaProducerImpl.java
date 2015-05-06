package com.realtech.socialsurvey.core.services.mq.impl;

import java.util.Properties;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.realtech.socialsurvey.core.enums.EmailHeader;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.mq.ProducerForQueue;

/**
 * Queues into Kafka.
 *
 */
@Service
public class KafkaProducerImpl implements ProducerForQueue, InitializingBean, DisposableBean {
	
	public static final Logger LOG = LoggerFactory.getLogger(KafkaProducerImpl.class);
	
	private static final String DEFAULT_EMAIL_TOPIC = "email_topic";
	private static ProducerConfig EMAIL_PRODUCER_CONFIG = null;
	private static Producer<String, String> EMAIL_PRODUCER = null;
	
	@Value("${BROKER_LIST}")
	private String brokerList; 
	
	@Value("${EMAIL_TOPIC_BROKER_SERIALIZER_CLASS}")
	private String emailSerializerClass; 
	
	@Value("${EMAIL_TOPIC_PARTITIONER_CLASS}")
	private String emailPartitionerClass;
	
	@Value("${EMAIL_TOPIC_REQUEST_REQUIRED_ACKS}")
	private String emailRequestRequiredAcks;

	@Override
	public void queueEmail(EmailHeader header, String content) throws InvalidInputException{
		LOG.info("Queueing mail to Kafka: header: "+header+"\t content: "+content);
		if(header == null){
			LOG.warn("Header is null to queue the mail");
			throw new InvalidInputException("Header is null");
		}
		if(content == null || content.isEmpty()){
			LOG.warn("No content to queue the mail");
			throw new InvalidInputException("Content is not to queue the mail");
		}
		// topics will be partitioned by header name
		StringBuilder finalContent = new StringBuilder();
		finalContent.append("HEADER^^").append(header.getName()).append("$$").append(content);
		KeyedMessage<String, String> data = new KeyedMessage<String, String>(DEFAULT_EMAIL_TOPIC, header.getName(), finalContent.toString());
		EMAIL_PRODUCER.send(data);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		LOG.info("Setting producers for topics");
		LOG.debug("Setting producer config for eamil topic");
		Properties props = new Properties();
		props.put("metadata.broker.list", brokerList);
		props.put("serializer.class", emailSerializerClass);
		props.put("partitioner.class", emailPartitionerClass);
		props.put("request.required.acks", emailRequestRequiredAcks);
		EMAIL_PRODUCER_CONFIG = new ProducerConfig(props);
		EMAIL_PRODUCER = new Producer<String, String>(EMAIL_PRODUCER_CONFIG);
		LOG.debug("Setting producer config for eamil topic completed");
		LOG.info("Setting producers for topics completed");
	}

	@Override
	public void destroy() throws Exception {
		LOG.info("Closing the producers");
		if(EMAIL_PRODUCER != null){
			EMAIL_PRODUCER.close();
		}
	}
}