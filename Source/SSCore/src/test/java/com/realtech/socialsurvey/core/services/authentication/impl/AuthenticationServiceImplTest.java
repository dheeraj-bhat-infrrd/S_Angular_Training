package com.realtech.socialsurvey.core.services.authentication.impl;

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
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;


public class AuthenticationServiceImplTest
{
    
    @InjectMocks
    private AuthenticationServiceImpl authenticationServiceImpl;

    @Mock
    UserDao userDao;
    
    @Mock
    GenericDao<Company, Long> companyDao;
    
    @Mock
    UserManagementService userManagementService;
    
    @Mock
    GenericDao<UserProfile, Integer> userProfileDao;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {}

    @AfterClass
    public static void tearDownAfterClass() throws Exception {}


    @Before
    public void setUp() throws Exception {
//        authenticationServiceImpl = new AuthenticationServiceImpl();
        MockitoAnnotations.initMocks( this );
    }


    @After
    public void tearDown() throws Exception {}


    @Test ( expected = InvalidInputException.class)
    public void testVerifyRegisteredUserWhenNullUserListFound() throws InvalidInputException {
        Mockito.when(userDao.findByColumn(Mockito.eq(User.class), Mockito.eq( CommonConstants.LOGIN_NAME ),Mockito.anyString())).thenReturn( null );
        authenticationServiceImpl.verifyRegisteredUser( "test@testmail.com" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testVerifyRegisteredUserWhenEmptyUserListFound() throws InvalidInputException {
        Mockito.when(userDao.findByColumn(Mockito.eq(User.class), Mockito.eq( CommonConstants.LOGIN_NAME ),Mockito.anyString())).thenReturn( new ArrayList<User>() );
        authenticationServiceImpl.verifyRegisteredUser( "test@testmail.com" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testGetUserWithLoginNameAndCompanyIdWhenNullUserListFound() throws InvalidInputException {
        Mockito.when( companyDao.findById( Mockito.eq( Company.class ), Mockito.anyLong() ) ).thenReturn( new Company() );
        Mockito.when( userDao.findByKeyValue( Mockito.eq( User.class ), Mockito.anyMap() ) ).thenReturn( null );
        authenticationServiceImpl.getUserWithLoginNameAndCompanyId( "test", 0 );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testGetUserWithLoginNameAndCompanyIdWhenEmptyUserListFound() throws InvalidInputException {
        Mockito.when( companyDao.findById( Mockito.eq( Company.class ), Mockito.anyLong() ) ).thenReturn( new Company() );
        Mockito.when( userDao.findByKeyValue( Mockito.eq( User.class ), Mockito.anyMap() ) ).thenReturn( new ArrayList<User>() );
        authenticationServiceImpl.getUserWithLoginNameAndCompanyId( "test", 0 );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testGetCompanyAdminProfileForUserWhenNullUserProfileListFound() throws InvalidInputException {
        Mockito.when( userManagementService.getProfilesMasterById( Mockito.anyInt() ) ).thenReturn( new ProfilesMaster() );
        Mockito.when( userProfileDao.findByKeyValue( Mockito.eq( UserProfile.class ), Mockito.anyMap() ) ).thenReturn( null );
        authenticationServiceImpl.getUserWithLoginNameAndCompanyId( "test", 0 );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testGetCompanyAdminProfileForUserWhenEmptyUserProfileListFound() throws InvalidInputException {
        Mockito.when( userManagementService.getProfilesMasterById( Mockito.anyInt() ) ).thenReturn( new ProfilesMaster() );
        Mockito.when( userProfileDao.findByKeyValue( Mockito.eq( UserProfile.class ), Mockito.anyMap() ) ).thenReturn( null );
        authenticationServiceImpl.getUserWithLoginNameAndCompanyId( "test", 0 );
    }
}
