package com.realtech.socialsurvey.core.commons;

import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;

public class CoreCommonTest {

	@InjectMocks
	private CoreCommon coreCommon;
	
	@Mock
	GenericDao<User, Long> userDao;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@After
	public void tearDown() throws Exception {}

	/*
	@Test
	public void test() {
		fail("Not yet implemented");
	}
	*/
	
	@Test(expected=InvalidInputException.class)
	public void testGetCorporateAdminWithNullCompany() throws InvalidInputException, NoRecordsFetchedException{
		coreCommon.getCorporateAdmin(null);
	}
	
	@Test(expected=NoRecordsFetchedException.class)
	public void testGetCorporateAdminWhenNullUserListFound() throws InvalidInputException, NoRecordsFetchedException{
		Mockito.when(userDao.findByKeyValue(Mockito.eq(User.class), Mockito.anyMap())).thenReturn(null);
		coreCommon.getCorporateAdmin(new Company());
	}
	
	@Test(expected=NoRecordsFetchedException.class)
	public void testGetCorporateAdminWhenEmptyUserListFound() throws InvalidInputException, NoRecordsFetchedException{
		Mockito.when(userDao.findByKeyValue(Mockito.eq(User.class), Mockito.anyMap())).thenReturn(new ArrayList<User>());
		coreCommon.getCorporateAdmin(new Company());
	}

}
