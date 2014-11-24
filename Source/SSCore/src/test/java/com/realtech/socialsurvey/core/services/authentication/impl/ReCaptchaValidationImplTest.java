//JIRA: SS-12: Captcha Validation implementation test: RM06: BOC
package com.realtech.socialsurvey.core.services.authentication.impl;

import static org.junit.Assert.assertEquals;
import net.tanesha.recaptcha.ReCaptcha;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ReCaptchaValidationImpl.class)
@ContextConfiguration("file:src/main/resources/sscore-beans.xml")
public class ReCaptchaValidationImplTest {

	@Autowired
	ReCaptcha reCaptcha;
	
	ReCaptchaValidationImpl captchaImp;
	
	
	@Before
	public void setUp() throws Exception {}

	@After
	public void tearDown() throws Exception {}

	@Test
	public void testIsCaptchaValid() throws Exception {
		captchaImp = PowerMockito.spy(new ReCaptchaValidationImpl());
		PowerMockito.doReturn(true).when(captchaImp, "validateCaptcha", Matchers.anyString(), Matchers.anyString(), Matchers.anyString());
		assertEquals(true, captchaImp.isCaptchaValid("localhost", "abc", "xyz"));
	}
	
	@Test
	public void testInvalidCaptcha() throws Exception{
		captchaImp = PowerMockito.spy(new ReCaptchaValidationImpl());
		PowerMockito.doReturn(false).when(captchaImp, "validateCaptcha", Matchers.anyString(), Matchers.anyString(), Matchers.anyString());
		assertEquals(false, captchaImp.isCaptchaValid("localhost", "abc", "xyz"));
	}

}
//JIRA: SS-12: Captcha Validation implementation test: RM06: EOC
