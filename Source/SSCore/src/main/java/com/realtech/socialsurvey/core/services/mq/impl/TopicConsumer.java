package com.realtech.socialsurvey.core.services.mq.impl;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import com.realtech.socialsurvey.core.enums.EmailHeader;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
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
	private static final String NAME_MARKER = "NAME^^";
	private static final String FIRSTNAME_MARKER = "FIRSTNAME^^";
	private static final String LASTNAME_MARKER = "LASTNAME^^";
	private static final String RETRYDAYS_MARKER = "RETRYDAYS^^";
	private static final String RETRIES_MARKER = "RETRIES^^";
	private static final String AGENTNAME_MARKER = "AGENTNAME^^";
	private static final String ELEMENTS_DELIMITER = "$$";
	
	private KafkaStream<byte[], byte[]> stream;
	private EmailServices emailServices;
	
	public TopicConsumer(KafkaStream<byte[], byte[]> stream, ApplicationContext ctx){
		this.stream = stream;
		emailServices = ctx.getBean(EmailServices.class);
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
					// TODO: handle failed messages
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
	
	private void delegateProcess(String header, String message) throws NonFatalException{
		LOG.debug("Delegating message: "+message+" for header: "+header);
		// delegate the mail according to the header
		if(header.equals(EmailHeader.REGISTRATION.getName())){
			parseRegistrationMailMessage(message);
		}else if(header.equals(EmailHeader.VERFICATION.getName())){
			parseMailMessage(message, EmailHeader.VERFICATION);
		}else if(header.equals(EmailHeader.EMAIL_VERFICATION.getName())){
			parseMailMessage(message, EmailHeader.EMAIL_VERFICATION);
		}else if(header.equals(EmailHeader.RESET_PASSWORD.getName())){
			parseMailMessage(message, EmailHeader.RESET_PASSWORD);
		}else if(header.equals(EmailHeader.SUBSCRIPTION_CHARGE_UNSUCESSFUL)){
			parseSubscriptionChargeUnsucessfulMail(message);
		}else if(header.equals(EmailHeader.REGISTRATION_COMPLETE.getName())){
			parseMailMessage(message, EmailHeader.REGISTRATION_COMPLETE);
		}else if(header.equals(EmailHeader.RETRY_CHARGE.getName())){
			parseRetryChargeMail(message);
		}else if(header.equals(EmailHeader.RETRY_EXHAUSTED.getName())){
			parseMailWithRecipientAndName(message, EmailHeader.RETRY_EXHAUSTED);
		}else if(header.equals(EmailHeader.ACCOUNT_DISABLED.getName())){
			parseMailWithRecipientAndName(message, EmailHeader.ACCOUNT_DISABLED);
		}else if(header.equals(EmailHeader.ACCOUNT_UPGRADE.getName())){
			parseMailWithRecipientAndName(message, EmailHeader.ACCOUNT_UPGRADE);
		}else if(header.equals(EmailHeader.SURVEY_COMPLETION.getName())){
			parseSurveyCompletionMail(message);
		}
	}
	
	private void parseRegistrationMailMessage(String message) throws NonFatalException{
		LOG.debug("Registration mail message: "+message);
		if(message.indexOf(RECIPIENT_MARKER) == -1 || message.indexOf(URL_MARKER) ==-1 || message.indexOf(FIRSTNAME_MARKER) ==-1 || message.indexOf(LASTNAME_MARKER) ==-1){
			throw new InvalidMessageFormatException("Invalid format for registration mail");
		}
		String recipient = message.substring(RECIPIENT_MARKER.length(), message.indexOf(ELEMENTS_DELIMITER));
		LOG.debug("Recipient: "+recipient);
		int messageParsedIndex = RECIPIENT_MARKER.length()+recipient.length()+ELEMENTS_DELIMITER.length(); // holds the index till the message has been parsed.
		String url = message.substring(messageParsedIndex+URL_MARKER.length(), message.indexOf(ELEMENTS_DELIMITER, messageParsedIndex));
		LOG.debug("Url: "+url);
		messageParsedIndex+=URL_MARKER.length()+url.length()+ELEMENTS_DELIMITER.length();
		String firstName = message.substring(messageParsedIndex+NAME_MARKER.length(), message.indexOf(ELEMENTS_DELIMITER, messageParsedIndex));
		LOG.debug("First Name: "+firstName);
		messageParsedIndex+=FIRSTNAME_MARKER.length()+firstName.length()+ELEMENTS_DELIMITER.length();
		String lastName = message.substring(messageParsedIndex+LASTNAME_MARKER.length());
		LOG.debug("Last Name: "+lastName);
		LOG.debug("Sending registration invite mail");
		emailServices.sendRegistrationInviteMail(url, recipient, firstName, lastName);
	}
	
	private void parseMailMessage(String message, EmailHeader header) throws NonFatalException{
		LOG.debug("Mail message: "+message);
		if(message.indexOf(RECIPIENT_MARKER) == -1 || message.indexOf(URL_MARKER) == -1 || message.indexOf(NAME_MARKER) == -1){
			throw new InvalidMessageFormatException("Invalid format for "+header.getName()+" mail");
		}
		String recipient = message.substring(RECIPIENT_MARKER.length(), message.indexOf(ELEMENTS_DELIMITER));
		LOG.debug("Recipient: "+recipient);
		int messageParsedIndex = RECIPIENT_MARKER.length()+recipient.length()+ELEMENTS_DELIMITER.length(); // holds the index till the message has been parsed.
		String url = message.substring(messageParsedIndex+URL_MARKER.length(), message.indexOf(ELEMENTS_DELIMITER, messageParsedIndex));
		LOG.debug("Url: "+url);
		messageParsedIndex+=URL_MARKER.length()+url.length()+ELEMENTS_DELIMITER.length();
		String name = message.substring(messageParsedIndex+NAME_MARKER.length());
		LOG.debug("Name: "+name);
		if (header == EmailHeader.VERFICATION) {
			LOG.debug("Sending verification mail");
			emailServices.sendVerificationMail(url, recipient, name);
		}
		else if (header == EmailHeader.EMAIL_VERFICATION) {
			LOG.debug("Sending verification mail");
			emailServices.sendEmailVerificationMail(url, recipient, name);
		}
		else if (header == EmailHeader.RESET_PASSWORD) {
			LOG.debug("Sending reset password mail");
			emailServices.sendResetPasswordEmail(url, recipient, name);
		}
		else if (header == EmailHeader.REGISTRATION_COMPLETE) {
			LOG.debug("Sending registration complete mail");
			emailServices.sendRegistrationCompletionEmail(url, recipient, name);
		}
	}
	
	private void parseSubscriptionChargeUnsucessfulMail(String message) throws NonFatalException{
		LOG.debug("Subscription charge unsucessful mail message: "+message);
		if(message.indexOf(RECIPIENT_MARKER) == -1 || message.indexOf(NAME_MARKER) ==-1 || message.indexOf(RETRYDAYS_MARKER) ==-1){
			throw new InvalidMessageFormatException("Invalid format for subscription charge unsucessful mail");
		}
		String recipient = message.substring(RECIPIENT_MARKER.length(), message.indexOf(ELEMENTS_DELIMITER));
		LOG.debug("Recipient: "+recipient);
		int messageParsedIndex = RECIPIENT_MARKER.length()+recipient.length()+ELEMENTS_DELIMITER.length(); // holds the index till the message has been parsed.
		String name = message.substring(messageParsedIndex+NAME_MARKER.length(), message.indexOf(ELEMENTS_DELIMITER, messageParsedIndex));
		LOG.debug("Name: "+name);
		messageParsedIndex+=NAME_MARKER.length()+name.length()+ELEMENTS_DELIMITER.length();
		String retryDays = message.substring(messageParsedIndex+RETRYDAYS_MARKER.length());
		LOG.debug("retryDays: "+retryDays);
		LOG.debug("Sending subscription charge unsucessful mail");
		emailServices.sendSubscriptionChargeUnsuccessfulEmail(recipient, name, retryDays);
	}
	
	private void parseRetryChargeMail(String message) throws NonFatalException{
		LOG.debug("Retry charge mail message: "+message);
		if(message.indexOf(RECIPIENT_MARKER) == -1 || message.indexOf(NAME_MARKER) ==-1 || message.indexOf(RETRIES_MARKER) ==-1){
			throw new InvalidMessageFormatException("Invalid format for retry charge mail");
		}
		String recipient = message.substring(RECIPIENT_MARKER.length(), message.indexOf(ELEMENTS_DELIMITER));
		LOG.debug("Recipient: "+recipient);
		int messageParsedIndex = RECIPIENT_MARKER.length()+recipient.length()+ELEMENTS_DELIMITER.length(); // holds the index till the message has been parsed.
		String name = message.substring(messageParsedIndex+NAME_MARKER.length(), message.indexOf(ELEMENTS_DELIMITER, messageParsedIndex));
		LOG.debug("Name: "+name);
		messageParsedIndex+=NAME_MARKER.length()+name.length()+ELEMENTS_DELIMITER.length();
		String retries = message.substring(messageParsedIndex+RETRIES_MARKER.length());
		LOG.debug("Retries: "+retries);
		LOG.debug("Sending retry charge mail");
		emailServices.sendRetryChargeEmail(recipient, name);
	}
	
	private void parseMailWithRecipientAndName(String message, EmailHeader header) throws NonFatalException{
		LOG.debug("Message for "+header.getName()+": "+message);
		if(message.indexOf(RECIPIENT_MARKER) == -1 || message.indexOf(NAME_MARKER) ==-1){
			throw new InvalidMessageFormatException("Invalid message format for "+header.getName());
		}
		String recipient = message.substring(RECIPIENT_MARKER.length(), message.indexOf(ELEMENTS_DELIMITER));
		LOG.debug("Recipient: "+recipient);
		int messageParsedIndex = RECIPIENT_MARKER.length()+recipient.length()+ELEMENTS_DELIMITER.length(); // holds the index till the message has been parsed.
		String name = message.substring(messageParsedIndex+NAME_MARKER.length());
		LOG.debug("Name: "+name);
		if(header == EmailHeader.RETRY_EXHAUSTED){
			LOG.debug("Sending retry exhausted mail");
			emailServices.sendRetryExhaustedEmail(recipient, name);
		}else if(header == EmailHeader.ACCOUNT_DISABLED){
			LOG.debug("Sending account disabled mail");
			emailServices.sendAccountDisabledMail(recipient, name);
		}else if(header == EmailHeader.ACCOUNT_UPGRADE){
			LOG.debug("Sending account upgrade mail");
			emailServices.sendAccountUpgradeMail(recipient, name);
		}
	}
	
	private void parseSurveyCompletionMail(String message) throws NonFatalException{
		LOG.debug("Survey completion mail message: "+message);
		if(message.indexOf(RECIPIENT_MARKER) == -1 || message.indexOf(NAME_MARKER) ==-1 || message.indexOf(AGENTNAME_MARKER) ==-1){
			throw new InvalidMessageFormatException("Invalid format for Survey completion mail");
		}
		String recipient = message.substring(RECIPIENT_MARKER.length(), message.indexOf(ELEMENTS_DELIMITER));
		LOG.debug("Recipient: "+recipient);
		int messageParsedIndex = RECIPIENT_MARKER.length()+recipient.length()+ELEMENTS_DELIMITER.length(); // holds the index till the message has been parsed.
		String name = message.substring(messageParsedIndex+NAME_MARKER.length(), message.indexOf(ELEMENTS_DELIMITER, messageParsedIndex));
		LOG.debug("Name: "+name);
		messageParsedIndex+=NAME_MARKER.length()+name.length()+ELEMENTS_DELIMITER.length();
		String agentName = message.substring(messageParsedIndex+AGENTNAME_MARKER.length());
		LOG.debug("Agent name: "+agentName);
		LOG.debug("Sending account completion mail");
		emailServices.sendSurveyCompletionMail(recipient, name, agentName);
	}
	

}
