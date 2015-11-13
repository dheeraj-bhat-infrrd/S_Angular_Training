package com.realtech.socialsurvey.core.utils;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class EmailFormatHelperTest {

	private EmailFormatHelper emailFormatHelper;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {}

	@Before
	public void setUp() throws Exception {
		emailFormatHelper = new EmailFormatHelper();
	}

	@After
	public void tearDown() throws Exception {}

	@Test
	public void testCustomerDisplayNameForEmailWithFirstAndLastName(){
		assertEquals("test", "Nishit K.", emailFormatHelper.getCustomerDisplayNameForEmail("Nishit", "Kannan"));
	}
	
	@Test
	public void testCustomerDisplayNameForEmailWithFirstName(){
		assertEquals("test", "Nishit", emailFormatHelper.getCustomerDisplayNameForEmail("Nishit", null));
	}

}
