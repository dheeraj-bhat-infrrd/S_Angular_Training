package com.realtech.socialsurvey.core.services.mq.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import org.springframework.context.ApplicationContext;

/**
 * High level consumer for Kafka topics
 *
 */
public class KafkaConsumerGroup {

	private final ConsumerConnector consumerConnector;
	private final String topic;
	private ExecutorService executorService;
	private ApplicationContext ctx;
	
	private static ConsumerConfig createConsumerConfig(String zookeeper, String groupId){
		Properties properties = new Properties();
		properties.put("zookeeper.connect", zookeeper);
		properties.put("group.id", groupId);
		properties.put("zookeeper.session.timeout.ms", "4000");
		properties.put("zookeeper.sync.time.ms", "200");
		properties.put("auto.commit.interval.ms", "1000");
		return new ConsumerConfig(properties);
	}
	
	public KafkaConsumerGroup(String zookeeper, String groupId, String topic, ApplicationContext ctx){
		consumerConnector = Consumer.createJavaConsumerConnector(createConsumerConfig(zookeeper, groupId));
		this.topic = topic;
		this.ctx = ctx;
	}
	
	public void run(int numOfThreads){
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, numOfThreads);
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumerConnector.createMessageStreams(topicCountMap);
		List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
		executorService = Executors.newFixedThreadPool(numOfThreads);
		for(final KafkaStream<byte[], byte[]> stream :  streams){
			executorService.submit(new TopicConsumer(stream, ctx));
		}
	}
	
	public void shutdown(){
		if(consumerConnector != null){
			consumerConnector.shutdown();
		}
		if(executorService != null){
			executorService.shutdown();
		}
	}
}
