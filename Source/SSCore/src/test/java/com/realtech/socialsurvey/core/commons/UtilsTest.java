package com.realtech.socialsurvey.core.commons;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

public class UtilsTest {

	private Utils utils;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {}

	@Before
	public void setUp() throws Exception {
		utils = new Utils();
	}

	@After
	public void tearDown() throws Exception {}

	@Test
	public void testGenerateRegionProfileUrl(){
		assertEquals("Proper test", "/region/company/region-name", utils.generateRegionProfileUrl("company", "region-name"));
	}
	
	@Test
	public void testGenerateBranchProfileUrl(){
		assertEquals("Proper test", "/office/company/office-name", utils.generateBranchProfileUrl("company", "office-name"));
	}
	
	@Test
	public void testGenerateCompanyProfileUrl(){
		assertEquals("Proper test", "/company/company", utils.generateCompanyProfileUrl("company"));
	}
	
	@Test
	public void testMaskEmailAddress(){
		Whitebox.setInternalState(utils, "maskingPrefix", "test");
		Whitebox.setInternalState(utils, "maskingSuffix", "@abc.com");
		assertEquals("Proper Test", "test+my+example.com@abc.com", utils.maskEmailAddress("my@example.com"));
	}
}
