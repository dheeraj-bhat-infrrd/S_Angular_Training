package com.realtech.socialsurvey.core.services.mq.impl;

import java.util.HashMap;
import java.util.Map;
import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailTopicPartitioner implements Partitioner {

	public static final Logger LOG = LoggerFactory.getLogger(EmailTopicPartitioner.class);
	
	private static final Map<String, Integer> HEADER_PARTITION_MAP = new HashMap<String, Integer>();
	
	static{
		// the key should be the same as the values of EmailHeader enum
		HEADER_PARTITION_MAP.put("registration", 1);
		HEADER_PARTITION_MAP.put("reset_password", 2);
		HEADER_PARTITION_MAP.put("registration_complete", 1);
		HEADER_PARTITION_MAP.put("subscription_charge_unsucessful", 3);
		HEADER_PARTITION_MAP.put("verification", 1);
		HEADER_PARTITION_MAP.put("retry_charge", 3);
		HEADER_PARTITION_MAP.put("retry_exhausted", 3);
		HEADER_PARTITION_MAP.put("account_disabled", 2);
		HEADER_PARTITION_MAP.put("account_upgrade", 2);
	}
	
	public EmailTopicPartitioner(VerifiableProperties properties){}
	
	@Override
	public int partition(Object key, int numOfPartitions) {
		LOG.info("Finding the partition id for the email topic");
		String partitionKey = (String)key;
		int keyVal = HEADER_PARTITION_MAP.get(partitionKey);
		return keyVal % numOfPartitions;
	}

}
