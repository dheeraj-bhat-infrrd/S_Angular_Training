package com.realtech.socialsurvey.core.services.sms.impl;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.mockito.Mock;

import com.realtech.socialsurvey.core.dao.BranchDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;


public class SmsServicesImplTest {
	
	SmsServicesImpl smsServicesImpl;
	
	@Mock
	OrganizationUnitSettingsDao organizationUnitSettingsDao;
	
	@Mock
	OrganizationUnitSettings companySettings;
	
	@Mock
	BranchDao branchDao;

	 @BeforeClass
	    public static void setUpBeforeClass() throws Exception
	    {}


	    @AfterClass
	    public static void tearDownAfterClass() throws Exception
	    {}


	    @Before
	    public void setUp() throws Exception
	    {
	        smsServicesImpl = new SmsServicesImpl();
	    }


	    @After
	    public void tearDown() throws Exception
	    {}
	    
}
