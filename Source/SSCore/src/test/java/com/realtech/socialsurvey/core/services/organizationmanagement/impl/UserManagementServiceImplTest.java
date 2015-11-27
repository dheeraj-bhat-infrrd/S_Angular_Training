package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.RemovedUser;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.UserAlreadyExistsException;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;

public class UserManagementServiceImplTest
{

    @InjectMocks
    private UserManagementServiceImpl userManagementServiceImpl;
    
    @Mock
    private URLGenerator urlGenerator;
    
    @Mock
    private UserProfileDao userProfileDao;
    
    @Mock
    private GenericDao<RemovedUser, Long> removedUserDao;
    
    @Mock
    private UserDao userDao;
    
    @Mock
    private GenericDao<Company, Long> companyDao;

    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {}


    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {}


    @Before
    public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks( this );
    }


    @After
    public void tearDown() throws Exception
    {}
    
    
    @Test ( expected = InvalidInputException.class)
    public void testGetProfilesMasterByIdForInvalidProfileId() throws InvalidInputException
    {
        userManagementServiceImpl.getProfilesMasterById( 0 );
    }
    
    /*@Test
    public void testCheckIfTheLinkHasExpiredForNullUrlParameter() throws InvalidInputException
    {
        Mockito.when( urlGenerator.decryptParameters( Mockito.anyString() ) ).thenReturn( null );
        assertEquals( "Test", false, userManagementServiceImpl.checkIfTheLinkHasExpired( "test" ) );
    }
    */
    
    @Test ( expected = InvalidInputException.class)
    public void testUpdateProfileCompletionStageForNullUser() throws InvalidInputException
    {
        userManagementServiceImpl.updateProfileCompletionStage( null, 2, "test" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testUpdateProfileCompletionStageForInvalidProfileMasterId() throws InvalidInputException
    {
        userManagementServiceImpl.updateProfileCompletionStage( new User(), 0 , "test" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testUpdateProfileCompletionStageForNullProfileCompletionStage() throws InvalidInputException
    {
        userManagementServiceImpl.updateProfileCompletionStage( new User(), 1 , null );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testUpdateProfileCompletionStageForEmptyProfileCompletionStage() throws InvalidInputException
    {
        userManagementServiceImpl.updateProfileCompletionStage( new User(), 1 , "" );
    }
    
    
    @Test ( expected = InvalidInputException.class)
    public void testVerifyAccountForNullUrlParam() throws InvalidInputException, SolrException
    {
        Mockito.when( urlGenerator.decryptParameters( Mockito.anyString() ) ).thenReturn( null );
        userManagementServiceImpl.verifyAccount( "test" );
    }
    

    @Test ( expected = InvalidInputException.class)
    public void testVerifyAccountForEmptyUrlParam() throws InvalidInputException, SolrException
    {
        Mockito.when( urlGenerator.decryptParameters( Mockito.anyString() ) ).thenReturn( new HashMap<String, String>() );
        userManagementServiceImpl.verifyAccount( "test" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testVerifyAccountForUrlParamWithoutUserKey() throws InvalidInputException, SolrException
    {
        Map<String, String> urlparamMap = new HashMap<String, String>();
        urlparamMap.put( "test", "test2" );
        Mockito.when( urlGenerator.decryptParameters( Mockito.anyString() ) ).thenReturn( urlparamMap );
        userManagementServiceImpl.verifyAccount( "test" );
    }
    
    
    @Test ( expected = InvalidInputException.class)
    public void testinviteUserToRegisterForNullFirstName() throws InvalidInputException, SolrException, UserAlreadyExistsException, UndeliveredEmailException
    {
        userManagementServiceImpl.inviteUserToRegister( new User(), null, "test", "test2", false );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testinviteUserToRegisterForNullEmail() throws InvalidInputException, SolrException, UserAlreadyExistsException, UndeliveredEmailException
    {
        userManagementServiceImpl.inviteUserToRegister( new User(), "test" , "test2", null , false );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testInviteNewUserForNullFirstName() throws InvalidInputException, SolrException, UserAlreadyExistsException, UndeliveredEmailException
    {
        userManagementServiceImpl.inviteNewUser( new User(), null, "test", "test1" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testInviteNewUserForNullEmail() throws InvalidInputException, SolrException, UserAlreadyExistsException, UndeliveredEmailException
    {
        userManagementServiceImpl.inviteNewUser( new User(), "test", "test1", null );
    }
    
    
    @Test ( expected = InvalidInputException.class)
    public void testRemoveExistingUserForNullAdmin() throws InvalidInputException
    {
        userManagementServiceImpl.removeExistingUser( null, 2 );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testRemoveExistingUserForInvalidUserId() throws InvalidInputException
    {
        userManagementServiceImpl.removeExistingUser( null, 0l );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testRemoveExistingUserForNoUserFound() throws InvalidInputException
    {
        Mockito.when( userDao.findById( Mockito.eq( User.class ), Mockito.anyLong() )).thenReturn( null );
        userManagementServiceImpl.removeExistingUser( new User(), 2l );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testGetUserByLoginNameForNullAdmin() throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementServiceImpl.getUserByLoginName( null, "test" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testGetUserByLoginNameForNullLoginName() throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementServiceImpl.getUserByLoginName( new User(), null );
    }
    
    @Test ( expected = NoRecordsFetchedException.class)
    public void testGetUserByLoginNameForNullUserList() throws InvalidInputException, NoRecordsFetchedException
    {
        Mockito.when( userDao.findByKeyValue( Mockito.eq( User.class ), Mockito.anyMap() )).thenReturn( null );
        userManagementServiceImpl.getUserByLoginName( new User(), "test" );
    }
    
    @Test ( expected = NoRecordsFetchedException.class)
    public void testGetUserByLoginNameForEmptyUserList() throws InvalidInputException, NoRecordsFetchedException
    {
        Mockito.when( userDao.findByKeyValue( Mockito.eq( User.class ), Mockito.anyMap() )).thenReturn( new ArrayList<User>() );
        userManagementServiceImpl.getUserByLoginName( new User(), "test" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void getUserByEmailAndCompanyForNullEmailId() throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementServiceImpl.getUserByEmailAndCompany( 1l, null );
    }
    
    @Test ( expected = NoRecordsFetchedException.class)
    public void testGetUserByLoginNameForNullCompany() throws InvalidInputException, NoRecordsFetchedException
    {
        Mockito.when( companyDao.findById( Mockito.eq( Company.class ), Mockito.anyLong() )).thenReturn( null );
        userManagementServiceImpl.getUserByEmailAndCompany( 1l, "test" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testGetUserByEmailForNullEmailId() throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementServiceImpl.getUserByEmail( null );
    }
    
    @Test ( expected = NoRecordsFetchedException.class)
    public void testGetUserByEmailForNoUserFound() throws InvalidInputException, NoRecordsFetchedException
    {
        Mockito.when( userDao.findByKeyValue( Mockito.eq( User.class ), Mockito.anyMap() )).thenReturn( new ArrayList<User>() );
        userManagementServiceImpl.getUserByEmail( "test" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testGetUserBySimilarEmailIdForNullAdmin() throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementServiceImpl.getUsersBySimilarEmailId( null , "test" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testGetUserBySimilarEmailIdForNullEmailId() throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementServiceImpl.getUsersBySimilarEmailId( new User(),  null );
    }
        
    @Test ( expected = InvalidInputException.class)
    public void testSsUserAdditionAllowedForNullUser() throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementServiceImpl.isUserAdditionAllowed( null );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testSsUserAdditionAllowedForEmptyUser() throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementServiceImpl.isUserAdditionAllowed( new User() );
    }
}
