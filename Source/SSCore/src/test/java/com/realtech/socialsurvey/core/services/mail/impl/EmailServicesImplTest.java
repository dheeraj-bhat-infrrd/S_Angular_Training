package com.realtech.socialsurvey.core.services.mail.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;

// JIRA: SS-7: By RM02: BOC

/**
 * Test cases for EmailServices
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/main/resources/sscore-beans.xml")
public class EmailServicesImplTest {

	@Autowired
	EmailServices emailServices;

	@Test
	public void testSendRegistrationInviteMail() throws InvalidInputException, UndeliveredEmailException {
		emailServices.sendRegistrationInviteMail("http://localhost:8080/socialsurvey", "bhumika@raremile.com", "bhumika", "mishra");
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
