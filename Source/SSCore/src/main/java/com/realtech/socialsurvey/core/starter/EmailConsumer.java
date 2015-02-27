package com.realtech.socialsurvey.core.starter;

import com.realtech.socialsurvey.core.services.mq.impl.KafkaConsumerGroup;

public class EmailConsumer {

	public static void main(String[] args){
		if(args.length != 4){
			System.out.println("Usage: java EmailConsumer <zookeeper broker list> <groupid> <topic> <number of threads>");
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
			System.out.println("Number of threads is not provided properly.");
			System.exit(1);
		}
		System.out.println("Running the consumer group with zookeeper: "+zookeeper+" groupId: "+ groupId+" topic: "+topic+" num of threads: "+iNoOfThreads);
		KafkaConsumerGroup consumerGroup = new KafkaConsumerGroup(zookeeper, groupId, topic);
		consumerGroup.run(iNoOfThreads);
	}
}
