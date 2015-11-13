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
		assertEquals("Generated Region profile does not match expected", "/region/company/region-name", utils.generateRegionProfileUrl("company", "region-name"));
	}
	
	@Test
	public void testGenerateBranchProfileUrl(){
		assertEquals("Generated Branch profile does not match expected", "/office/company/office-name", utils.generateBranchProfileUrl("company", "office-name"));
	}
	
	@Test
	public void testGenerateCompanyProfileUrl(){
		assertEquals("Generated Company profile does not match expected", "/company/company", utils.generateCompanyProfileUrl("company"));
	}

	@Test
    public void testGenerateAgentProfileUrl(){
	    assertEquals("Generated Agent profile does not match expected", "/rijil-krishnan", utils.generateAgentProfileUrl("rijil-krishnan"));
    }

    @Test
    public void testPrepareProfileNameWithInputHavingSpaces(){
        assertEquals("Generated Profile Name does not match expected", "rijil-krishnan", utils.prepareProfileName("rijil krishnan"));
    }

    @Test
    public void testPrepareProfileNameWithInputHavingHyphen(){
        assertEquals("Generated Profile Name does not match expected", "rijil-krishnan", utils.prepareProfileName("rijil-krishnan"));
    }

    @Test
    public void testAppendIdenToProfileName(){
        assertEquals("Generated Profile Name with iden does not match expected", "rijil-krishnan-2", utils.appendIdenToProfileName("rijil-krishnan", 2));
    }
	
	@Test
	public void testMaskEmailAddress(){
		Whitebox.setInternalState(utils, "maskingPrefix", "test");
		Whitebox.setInternalState(utils, "maskingSuffix", "@abc.com");
		assertEquals("Masked email address does not match expected", "test+my+example.com@abc.com", utils.maskEmailAddress("my@example.com"));
	}
}
