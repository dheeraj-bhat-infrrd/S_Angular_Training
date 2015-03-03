package com.realtech.socialsurvey.core.starter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.realtech.socialsurvey.core.services.mq.impl.KafkaConsumerGroup;

public class EmailConsumer {

	public static final Logger LOG = LoggerFactory.getLogger(EmailConsumer.class);
	
	public static void main(String[] args){
		LOG.info("Starting up the email consumer.");
		LOG.debug("Loading the application context");
		ApplicationContext context = new ClassPathXmlApplicationContext("resources/sscore-beans.xml");
		if(args.length != 4){
			LOG.warn("Usage: java EmailConsumer <zookeeper broker list> <groupid> <topic> <number of threads>");
			System.exit(1);
		}
		// Get the parameters
		String zookeeper = args[0];
		String groupId = args[1];
		String topic = args[2];
		String noOfThreads = args[3];
		int iNoOfThreads = -1;
		try{
			iNoOfThreads = Integer.parseInt(noOfThreads);
		}catch(NumberFormatException nfe){
			LOG.error("Number of threads is not provided properly.");
			System.exit(1);
		}
		LOG.info("Running the consumer group with zookeeper: "+zookeeper+" groupId: "+ groupId+" topic: "+topic+" num of threads: "+iNoOfThreads);
		KafkaConsumerGroup consumerGroup = new KafkaConsumerGroup(zookeeper, groupId, topic);
		consumerGroup.run(iNoOfThreads);
	}
}
