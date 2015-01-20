package com.realtech.socialsurvey.core.services.mail.impl;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.springframework.beans.factory.annotation.Autowired;
import com.realtech.socialsurvey.core.commons.InitializeJNDI;
import com.realtech.socialsurvey.core.commons.SpringContextRule;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;

// JIRA: SS-7: By RM02: BOC

/**
 * Test cases for EmailServices
 */
public class EmailServicesImplTest {

	@Rule
	public TestRule springRule = new SpringContextRule(new String[]{"file:src/main/resources/sscore-beans.xml"}, this);
	
	@Autowired
	EmailServices emailServices;

	@BeforeClass
	public static void setUp() throws Exception{
		InitializeJNDI.initializeJNDIforTest();
	}
	
	@Test
	public void testSendRegistrationInviteMail() throws InvalidInputException, UndeliveredEmailException {
		emailServices.sendRegistrationInviteMail("http://localhost:8080/socialsurvey", "nishit@raremile.com", "nishit", "kannan");
	}

	@Test(expected = InvalidInputException.class)
	public void testInvalidUrlInput() throws InvalidInputException, UndeliveredEmailException {
		emailServices.sendRegistrationInviteMail(null, "bhumika@raremile.com", "bhumika", "mishra");
	}

	@Test(expected = InvalidInputException.class)
	public void testInvalidRecipientId() throws InvalidInputException, UndeliveredEmailException {
		emailServices.sendRegistrationInviteMail("http://localhost:8080/socialsurvey", "", "Bhumika", "Mishra");
	}

	@Test(expected = InvalidInputException.class)
	public void testInvalidFirstName() throws InvalidInputException, UndeliveredEmailException {
		emailServices.sendRegistrationInviteMail("http://localhost:8080/socialsurvey", "bhumika@raremile.com", "", "");
	}

}
//JIRA: SS-7: By RM02: EOC
