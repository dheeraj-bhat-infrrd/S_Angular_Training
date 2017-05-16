/**
 * 
 */
package com.reporting.arc.utils;

import com.sendgrid.SendGrid;
import com.sendgrid.SendGrid.Email;
import com.sendgrid.SendGridException;

/**
 * @author E7440
 *
 */
public class EmailSendingUtil {
	
	public static void sendBatchExceptionMail(String exceptionMessage){
		String username = PropertyReader.getValueForKey("SENDGRID.SENDER.SOCIALSURVEYME.USERNAME");
		String password = PropertyReader.getValueForKey("SENDGRID.SENDER.SOCIALSURVEYME.PASSWORD");
		String fromAddress = PropertyReader.getValueForKey("SENDGRID.DEFAULT.FROM.ADDRESS");
		String fromName = PropertyReader.getValueForKey("SENDGRID.DEFAULT.FROM.NAME");
		String subject = PropertyReader.getValueForKey("SENDGRID.EMAIL.SUBJECT");
		String body = PropertyReader.getValueForKey("SENDGRID.EMAIL.BODY");
		String toAddressString = PropertyReader.getValueForKey("SENDGRID.DEFAULT.TO.ADDRESS");
		String[] tos = toAddressString.split(",");
		SendGrid sendGrid = new SendGrid(username, password);
		
		
		Email email = new Email();
		email.setFrom(fromAddress);
		email.setFromName(fromName);
		email.setSubject(subject);
		email.setTo(tos);
		email.setText(body+" : "+exceptionMessage);
		
		try {
			sendGrid.send(email);
		} catch (SendGridException e) {
			e.printStackTrace();
		}
	}
}