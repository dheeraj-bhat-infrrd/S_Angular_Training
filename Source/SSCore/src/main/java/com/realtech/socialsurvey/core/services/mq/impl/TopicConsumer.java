package com.realtech.socialsurvey.core.services.mq.impl;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.realtech.socialsurvey.core.enums.EmailHeader;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.mq.InvalidMessageFormatException;

/**
 * Consumes from a Kafka topic
 *
 */
public class TopicConsumer implements Runnable {
	
	public static final Logger LOG = LoggerFactory.getLogger(TopicConsumer.class);
	
	private static final String HEADER_MARKER = "HEADER^^";
	private static final String RECIPIENT_MARKER = "RECIPIENT^^";
	private static final String URL_MARKER = "URL^^";
	private static final String ELEMENTS_DELIMITER = "$$";
	
	private KafkaStream<byte[], byte[]> stream;
	
	public TopicConsumer(){}
	
	public TopicConsumer(KafkaStream<byte[], byte[]> stream){
		this.stream = stream;
	}
	
	public void setStream(KafkaStream<byte[], byte[]> stream){
		this.stream = stream;
	}

	@Override
	public void run() {
		// iterate the stream
		if(stream != null){
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
		}else{
			LOG.warn("No stream provided for the topic");
		}

	}
	
	// fetch the header from the message
	private String fetchHeader(String message) throws NonFatalException{
		LOG.debug("Fetching header from "+message);
		if(message.indexOf(HEADER_MARKER) == -1){
			throw new InvalidMessageFormatException("Invalid format of the message. No header found in the message.");
		}
		String header = message.substring(HEADER_MARKER.length(), message.indexOf(ELEMENTS_DELIMITER));
		LOG.debug("Header: "+header);
		return header;
	}
	
	private void delegateProcess(String header, String message) throws InvalidMessageFormatException{
		LOG.debug("Delegating message: "+message+" for header: "+header);
		// delegate the mail according to the header
		if(header.equals(EmailHeader.REGISTRATION.getName())){
			parseRegistrationMailMessage(message);
		}
	}
	
	private void parseRegistrationMailMessage(String message) throws InvalidMessageFormatException{
		if(message.indexOf(RECIPIENT_MARKER) == -1){
			throw new InvalidMessageFormatException("Invalid format. Recipient is not present in the message for registration mail");
		}
		String recipient = message.substring(RECIPIENT_MARKER.length(), message.indexOf(ELEMENTS_DELIMITER));
		LOG.debug("Recipient: "+recipient);
		int messageParsedIndex = RECIPIENT_MARKER.length()+recipient.length()+ELEMENTS_DELIMITER.length(); // holds the index till the message has been parsed.
		String url = message.substring(messageParsedIndex, message.indexOf(ELEMENTS_DELIMITER, messageParsedIndex));
		LOG.debug("Url: "+url);
		messageParsedIndex+=URL_MARKER.length()+url.length()+ELEMENTS_DELIMITER.length();
		
		
	}

}
