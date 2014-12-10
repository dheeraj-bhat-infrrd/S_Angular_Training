package com.realtech.socialsurvey.core.services.mail.impl;

/**
 * Test class for Email Sender
 */
import java.util.ArrayList;
import java.util.List;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import com.realtech.socialsurvey.core.commons.InitializeJNDI;
import com.realtech.socialsurvey.core.commons.SpringContextRule;
import com.realtech.socialsurvey.core.entities.EmailEntity;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.mail.EmailSender;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;

@PrepareForTest({ Session.class, EmailSenderImpl.class })
@RunWith(PowerMockRunner.class)
public class EmailSenderTest {
	
	@Rule
	public TestRule springRule = new SpringContextRule(new String[]{"file:src/main/resources/sscore-beans.xml"}, this);
	
	@Autowired
	EmailSender emailSender;
	
	EmailEntity testEmailEntity;

	@Mock
	Session mockSession;
	@Mock
	Transport mockTransport;

	@BeforeClass
	public static void before() throws Exception {
		InitializeJNDI.initializeJNDIforTest();
	}

	@Before
	public void intitialize() throws Exception {
		testEmailEntity = new EmailEntity();
		testEmailEntity.setSenderName("Test");
		testEmailEntity.setSenderEmailId("a@b.com");
		testEmailEntity.setSenderPassword("*******");
		testEmailEntity.setSubject("Test Subject");
		testEmailEntity.setBody("Test body");
		List<String> recipients = new ArrayList<String>();
		recipients.add("x@z.com");
		testEmailEntity.setRecipients(recipients);
		testEmailEntity.setRecipientType(EmailEntity.RECIPIENT_TYPE_TO);
		// mockSession = PowerMockito.mock(Session.class);
	}

	@After
	public void tearDown() throws Exception {}

	@Test(expected = InvalidInputException.class)
	public void testNullEmailEntity() throws InvalidInputException, UndeliveredEmailException {
		emailSender.sendMail(null);
	}

	@Test(expected = InvalidInputException.class)
	public void testNullSenderEmailId() throws InvalidInputException, UndeliveredEmailException {
		emailSender.sendMail(new EmailEntity());
	}

	@Test(expected = InvalidInputException.class)
	public void testNullSenderName() throws InvalidInputException, UndeliveredEmailException {
		EmailEntity emailEntity = new EmailEntity();
		emailEntity.setSenderEmailId("a@b.com");
		emailSender.sendMail(emailEntity);
	}

	//
	// @Test(expected = InvalidInputException.class)
	// public void testNullSenderPassword() throws InvalidInputException, UndeliveredEmailException
	// {
	// emailSender.getEmailEntity().setSenderPassword(null);
	// emailSender.sendMail();
	// }
	//
	// @Test(expected = InvalidInputException.class)
	// public void testNullRecipients() throws InvalidInputException, UndeliveredEmailException {
	// emailSender.getEmailEntity().setRecipients(null);
	// emailSender.sendMail();
	// }
	//
	@Test(expected = UndeliveredEmailException.class)
	public void testNoSuchProviderForTransport() throws Exception {
		// spy the EmailSenderImpl
		emailSender = PowerMockito.spy(emailSender);
		// mock behaviour
		PowerMockito.when(emailSender, "createSession").thenReturn(mockSession);
		Mockito.when(mockSession.getTransport(Matchers.anyString())).thenThrow(new NoSuchProviderException());
		emailSender.sendMail(testEmailEntity);
	}
	//
	// @Test(expected = UndeliveredEmailException.class)
	// public void testAuthenticationForTransportConnetion() throws MessagingException,
	// InvalidInputException, UndeliveredEmailException {
	// // spy the EmailSenderImpl
	// emailSender = PowerMockito.spy(emailSender);
	// Mockito.when(mockSession.getTransport(Matchers.anyString())).thenReturn(mockTransport);
	// Mockito.doThrow(new AuthenticationFailedException()).when(mockTransport)
	// .connect(Matchers.anyString(), Matchers.anyInt(), Matchers.anyString(),
	// Matchers.anyString());;
	// emailSender.sendMail();
	// }
	//
	// @Test(expected = UndeliveredEmailException.class)
	// public void testMessagingForTransportConnetion() throws MessagingException,
	// InvalidInputException, UndeliveredEmailException {
	// emailSender.setSession(mockSession);
	// Mockito.when(mockSession.getTransport(Matchers.anyString())).thenReturn(mockTransport);
	// Mockito.doThrow(new MessagingException()).when(mockTransport)
	// .connect(Matchers.anyString(), Matchers.anyInt(), Matchers.anyString(),
	// Matchers.anyString());;
	// emailSender.sendMail();
	// }
	//
	// @Test(expected = UndeliveredEmailException.class)
	// public void testIllegalStateForTransportConnetion() throws MessagingException,
	// InvalidInputException, UndeliveredEmailException {
	// emailSender.setSession(mockSession);
	// Mockito.when(mockSession.getTransport(Matchers.anyString())).thenReturn(mockTransport);
	// Mockito.doThrow(new IllegalStateException()).when(mockTransport)
	// .connect(Matchers.anyString(), Matchers.anyInt(), Matchers.anyString(),
	// Matchers.anyString());;
	// emailSender.sendMail();
	// }

	// @Test(expected = InvalidInputException.class)
	// public void testInvalidRecipientType() throws MessagingException, InvalidInputException,
	// UndeliveredEmailException{
	// emailSender.getEmailEntity().setRecipientType(5);
	// emailSender.setSession(mockSession);
	// Mockito.when(mockSession.getTransport(Matchers.anyString())).thenReturn(mockTransport);
	// Mockito.doNothing().when(mockTransport).connect(Matchers.anyString(), Matchers.anyInt(),
	// Matchers.anyString(), Matchers.anyString());
	// emailSender.sendMail();
	// }
}
