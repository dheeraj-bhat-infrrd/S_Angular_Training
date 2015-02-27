package com.realtech.socialsurvey.core.services.mq.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.realtech.socialsurvey.core.enums.EmailHeader;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.mq.InvalidMessageFormatException;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;

/**
 * Consumes from a Kaafka topic
 *
 */
public class TopicConsumer implements Runnable {
	
	public static final Logger LOG = LoggerFactory.getLogger(TopicConsumer.class);
	
	private static final String HEADER_MARKER = "HEADER^^";
	private static final String ELEMENTS_DELIMITER = "$$";
	
	private KafkaStream<byte[], byte[]> stream;
	
	public TopicConsumer(KafkaStream<byte[], byte[]> stream){
		this.stream = stream;
	}

	@Override
	public void run() {
		// iterate the stream
		ConsumerIterator<byte[], byte[]> itrConsumer = stream.iterator();
		String message = null;
		while(itrConsumer.hasNext()){
			message = new String(itrConsumer.next().message());
			LOG.info("Consuming message:");
			LOG.info(message);
			try{
				String header = fetchHeader(message);
				message = message.substring(HEADER_MARKER.length()+header.length()+2); // 2 is for $$
				delegateProcess(header, message);
			}catch(NonFatalException nfe){
				LOG.error("Could not process message: "+message, nfe);
			}
		}

	}
	
	// fetch the header from the message
	private String fetchHeader(String message) throws NonFatalException{
		LOG.debug("Fetching header from "+message);
		if(message.indexOf(HEADER_MARKER) == -1){
			throw new InvalidMessageFormatException("Invalid format of the message. No header found in the message.");
		}
		String header = message.substring((HEADER_MARKER.length()), message.indexOf(ELEMENTS_DELIMITER));
		LOG.debug("Header: "+header);
		return header;
	}
	
	private void delegateProcess(String header, String message){
		LOG.debug("Delegating message: "+message+" for header: "+header);
		// delegate the mail according to the header
		
	}

}
