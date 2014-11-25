package com.realtech.socialsurvey.web.controller;

import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.realtech.socialsurvey.core.services.authentication.CaptchaValidation;
import com.realtech.socialsurvey.core.services.authentication.impl.ReCaptchaValidationImpl;
import com.realtech.socialsurvey.core.services.registration.RegistrationService;
import com.realtech.socialsurvey.core.services.registration.impl.RegistrationServiceImpl;
import com.realtech.socialsurvey.core.utils.MessageUtils;

/**
 * Test cases for registration controller
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/WEB-INF/sswebapp-config.xml")
public class RegistrationControllerTest {

	@InjectMocks
	private CaptchaValidation captchaValidation = new ReCaptchaValidationImpl();

	@InjectMocks
	private RegistrationService registrationService = new RegistrationServiceImpl();

	@InjectMocks
	private MessageUtils messageUtils = new MessageUtils();

	@Test
	public void testInitRegisterPage() {
		fail("Not yet implemented");
	}

	@Test
	public void testInviteCorporate() {
		fail("Not yet implemented");
	}

}
