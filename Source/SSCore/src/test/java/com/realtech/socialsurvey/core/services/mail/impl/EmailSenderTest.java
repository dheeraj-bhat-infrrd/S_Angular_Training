package com.realtech.socialsurvey.core.services.mail.impl;

/**
 * Test class for Email Sender
 */
import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.realtech.socialsurvey.core.entities.SmtpSettings;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;

@PrepareForTest(Session.class)
@RunWith(PowerMockRunner.class)
public class EmailSenderTest {
	
	Session session;
	EmailSenderImpl emailSender;
	@Mock
	Session mockSession;
	@Mock
	Transport mockTransport;
	
	static ApplicationContext CONTEXT;
	
	@BeforeClass
	public static void before(){
		CONTEXT = new ClassPathXmlApplicationContext("sscore-beans.xml");
	}
	
	@AfterClass
	public static void after(){
		CONTEXT = null;
	}
	
	@Before
	public void setUp() throws Exception {
		emailSender = (EmailSenderImpl)CONTEXT.getBean("testEmailSender");
		System.out.println(emailSender.getEmailEntity());
		MailSessionWrapper sessionWrapper = new MailSessionWrapper();
		sessionWrapper.setMailSmtpAuth(SmtpSettings.MAIL_SMTP_AUTH);
		sessionWrapper.setMailSmtpStartTlsEnable(SmtpSettings.MAIL_SMTP_STARTTLS_ENABLE);
		session = sessionWrapper.getMailSession();
		emailSender.setSession(session);
		mockSession = PowerMockito.mock(Session.class);
	}

	@After
	public void tearDown() throws Exception {
		emailSender = null;
		session = null;
	}

	@Test(expected = InvalidInputException.class)
	public void testNullSessionEntity() throws InvalidInputException, UndeliveredEmailException{
		emailSender.setSession(null); // setting the session as null
		emailSender.sendMail();
	}
	
	@Test(expected = InvalidInputException.class)
	public void testNullEmailEntity() throws InvalidInputException, UndeliveredEmailException{
		emailSender.setEmailEntity(null); // setting the test entity as null
		emailSender.sendMail();
	}
	
	@Test(expected = InvalidInputException.class)
	public void testNullSmtpSettings() throws InvalidInputException, UndeliveredEmailException{
		emailSender.setSmtpSettings(null);
		emailSender.sendMail();
	}
	
	@Test(expected = InvalidInputException.class)
	public void testNullSenderEmailId() throws InvalidInputException, UndeliveredEmailException{
		emailSender.getEmailEntity().setSenderEmailId(null);
		emailSender.sendMail();
	}
	
	@Test(expected = InvalidInputException.class)
	public void testNullSenderName() throws InvalidInputException, UndeliveredEmailException{
		emailSender.getEmailEntity().setSenderName(null);
		emailSender.sendMail();
	}
	
	@Test(expected = InvalidInputException.class)
	public void testNullSenderPassword() throws InvalidInputException, UndeliveredEmailException{
		emailSender.getEmailEntity().setSenderPassword(null);
		emailSender.sendMail();
	}
	
	@Test(expected = InvalidInputException.class)
	public void testNullRecipients() throws InvalidInputException, UndeliveredEmailException{
		emailSender.getEmailEntity().setRecipients(null);
		emailSender.sendMail();
	}
	
	@Test(expected = UndeliveredEmailException.class)
	public void testNoSuchProviderForTransport() throws InvalidInputException, UndeliveredEmailException, NoSuchProviderException{
		emailSender.setSession(mockSession);
		// mock behaviour
		Mockito.when(mockSession.getTransport(Matchers.anyString())).thenThrow(new NoSuchProviderException());
		emailSender.sendMail();
	}

	@Test(expected = UndeliveredEmailException.class)
	public void testAuthenticationForTransportConnetion() throws MessagingException, InvalidInputException, UndeliveredEmailException{
		emailSender.setSession(mockSession);
		Mockito.when(mockSession.getTransport(Matchers.anyString())).thenReturn(mockTransport);
		Mockito.doThrow(new AuthenticationFailedException()).when(mockTransport).connect(Matchers.anyString(), Matchers.anyInt(), Matchers.anyString(), Matchers.anyString());;
		emailSender.sendMail();
	}
	
	@Test(expected = UndeliveredEmailException.class)
	public void testMessagingForTransportConnetion() throws MessagingException, InvalidInputException, UndeliveredEmailException{
		emailSender.setSession(mockSession);
		Mockito.when(mockSession.getTransport(Matchers.anyString())).thenReturn(mockTransport);
		Mockito.doThrow(new MessagingException()).when(mockTransport).connect(Matchers.anyString(), Matchers.anyInt(), Matchers.anyString(), Matchers.anyString());;
		emailSender.sendMail();
	}
	
	@Test(expected = UndeliveredEmailException.class)
	public void testIllegalStateForTransportConnetion() throws MessagingException, InvalidInputException, UndeliveredEmailException{
		emailSender.setSession(mockSession);
		Mockito.when(mockSession.getTransport(Matchers.anyString())).thenReturn(mockTransport);
		Mockito.doThrow(new IllegalStateException()).when(mockTransport).connect(Matchers.anyString(), Matchers.anyInt(), Matchers.anyString(), Matchers.anyString());;
		emailSender.sendMail();
	}
	
//	@Test(expected = InvalidInputException.class)
//	public void testInvalidRecipientType() throws MessagingException, InvalidInputException, UndeliveredEmailException{
//		emailSender.getEmailEntity().setRecipientType(5);
//		emailSender.setSession(mockSession);
//		Mockito.when(mockSession.getTransport(Matchers.anyString())).thenReturn(mockTransport);
//		Mockito.doNothing().when(mockTransport).connect(Matchers.anyString(), Matchers.anyInt(), Matchers.anyString(), Matchers.anyString());
//		emailSender.sendMail();
//	}
}
