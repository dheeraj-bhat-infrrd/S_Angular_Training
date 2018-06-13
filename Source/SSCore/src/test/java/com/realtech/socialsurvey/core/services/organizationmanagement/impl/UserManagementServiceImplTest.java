package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.realtech.socialsurvey.TestConstants;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.CompanyDao;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.dao.UserEmailMappingDao;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.entities.AccountsMaster;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.entities.RemovedUser;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserEmailMapping;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.UserAlreadyExistsException;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;


public class UserManagementServiceImplTest
{

    @Spy
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
    private CompanyDao companyDao;

    @Mock
    private OrganizationUnitSettingsDao organizationUnitSettingsDao;

    @Mock
    private UserEmailMappingDao userEmailMappingDao;

    @Mock
    private GenericDao<LicenseDetail, Long> licenseDetailsDao;
    
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
        userManagementServiceImpl.updateProfileCompletionStage( new User(), 0, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateProfileCompletionStageForNullProfileCompletionStage() throws InvalidInputException
    {
        userManagementServiceImpl.updateProfileCompletionStage( new User(), 1, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateProfileCompletionStageForEmptyProfileCompletionStage() throws InvalidInputException
    {
        userManagementServiceImpl.updateProfileCompletionStage( new User(), 1, "" );
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
    public void testinviteUserToRegisterForNullFirstName() throws InvalidInputException, SolrException,
        UserAlreadyExistsException, UndeliveredEmailException, NoRecordsFetchedException
    {
        userManagementServiceImpl.inviteUserToRegister( new User(), null, "test", "test2", false, true, false, true );
    }


    @Test ( expected = InvalidInputException.class)
    public void testinviteUserToRegisterForNullEmail() throws InvalidInputException, SolrException, UserAlreadyExistsException,
        UndeliveredEmailException, NoRecordsFetchedException
    {
        userManagementServiceImpl.inviteUserToRegister( new User(), "test", "test2", null, false, true, false, true );
    }


    @Test ( expected = InvalidInputException.class)
    public void testInviteNewUserForNullFirstName() throws InvalidInputException, SolrException, UserAlreadyExistsException,
        UndeliveredEmailException
    {
        userManagementServiceImpl.inviteNewUser( new User(), null, "test", "test1" );
    }


    @Test ( expected = InvalidInputException.class)
    public void testInviteNewUserForNullEmail() throws InvalidInputException, SolrException, UserAlreadyExistsException,
        UndeliveredEmailException
    {
        userManagementServiceImpl.inviteNewUser( new User(), "test", "test1", null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testRemoveExistingUserForNullAdmin() throws InvalidInputException
    {
        userManagementServiceImpl.removeExistingUser( null, 2, CommonConstants.STATUS_INACTIVE  );
    }


    @Test ( expected = InvalidInputException.class)
    public void testRemoveExistingUserForInvalidUserId() throws InvalidInputException
    {
        userManagementServiceImpl.removeExistingUser( null, 0l, CommonConstants.STATUS_INACTIVE  );
    }


    @Test ( expected = InvalidInputException.class)
    public void testRemoveExistingUserForNoUserFound() throws InvalidInputException
    {
        Mockito.when( userDao.findById( Mockito.eq( User.class ), Mockito.anyLong() ) ).thenReturn( null );
        userManagementServiceImpl.removeExistingUser( new User(), 2l, CommonConstants.STATUS_INACTIVE  );
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


    @SuppressWarnings ( "unchecked")
    @Test ( expected = NoRecordsFetchedException.class)
    public void testGetUserByLoginNameForNullUserList() throws InvalidInputException, NoRecordsFetchedException
    {
        Mockito.when( userDao.findByKeyValue( Mockito.eq( User.class ), Mockito.anyMap() ) ).thenReturn( null );
        userManagementServiceImpl.getUserByLoginName( new User(), "test" );
    }


    @SuppressWarnings ( "unchecked")
    @Test ( expected = NoRecordsFetchedException.class)
    public void testGetUserByLoginNameForEmptyUserList() throws InvalidInputException, NoRecordsFetchedException
    {
        Mockito.when( userDao.findByKeyValue( Mockito.eq( User.class ), Mockito.anyMap() ) ).thenReturn( new ArrayList<User>() );
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
        Mockito.when( companyDao.findById( Mockito.eq( Company.class ), Mockito.anyLong() ) ).thenReturn( null );
        userManagementServiceImpl.getUserByEmailAndCompany( 1l, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetUserByEmailForNullEmailId() throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementServiceImpl.getUserByEmail( null );
    }


    @SuppressWarnings ( "unchecked")
    @Test ( expected = NoRecordsFetchedException.class)
    public void testGetUserByEmailForNoUserFound() throws InvalidInputException, NoRecordsFetchedException
    {
        Mockito.when( userDao.findByKeyValue( Mockito.eq( User.class ), Mockito.anyMap() ) ).thenReturn( new ArrayList<User>() );
        userManagementServiceImpl.getUserByEmail( "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetUserBySimilarEmailIdForNullAdmin() throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementServiceImpl.getUsersBySimilarEmailId( null, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetUserBySimilarEmailIdForNullEmailId() throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementServiceImpl.getUsersBySimilarEmailId( new User(), null );
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


    @Test ( expected = NoRecordsFetchedException.class)
    public void testSsUserAdditionAllowedForNullLicenceDetail() throws InvalidInputException, NoRecordsFetchedException
    {
        Mockito.doReturn( null ).when( licenseDetailsDao )
        .findByColumn( Mockito.eq( LicenseDetail.class ), Mockito.anyString(), Mockito.any( Company.class ) );
        Company company = new Company();
        User user = new User();
        user.setCompany( company );
        userManagementServiceImpl.isUserAdditionAllowed( user );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testSsUserAdditionAllowedForEmptyLicenceDetail() throws InvalidInputException, NoRecordsFetchedException
    {
        Mockito.doReturn( new ArrayList<LicenseDetail>() ).when( licenseDetailsDao )
        .findByColumn( Mockito.eq( LicenseDetail.class ), Mockito.anyString(), Mockito.any( Company.class ) );
        Company company = new Company();
        User user = new User();
        user.setCompany( company );
        userManagementServiceImpl.isUserAdditionAllowed( user );
    }


    @Test
    public void testSsUserAdditionAllowedForMaxUserAllowedAsNoLimit() throws InvalidInputException, NoRecordsFetchedException
    {
        List<LicenseDetail> licenceDetailList = new ArrayList<LicenseDetail>();
        LicenseDetail licenseDetail = new LicenseDetail();
        AccountsMaster accountsMaster = new AccountsMaster();

        accountsMaster.setMaxUsersAllowed( -1 );
        licenseDetail.setAccountsMaster( accountsMaster );
        licenceDetailList.add( licenseDetail );

        Mockito
            .when(
                licenseDetailsDao.findByColumn( Mockito.eq( LicenseDetail.class ), Mockito.anyString(),
                    Mockito.any( Company.class ) ) ).thenReturn( licenceDetailList );

        Company company = new Company();
        User user = new User();
        user.setCompany( company );
        assertEquals( "test", true, userManagementServiceImpl.isUserAdditionAllowed( user ) );
    }


    @Test
    public void testSsUserAdditionAllowedForMaxUserAllowedISMoreThanNoOfUsers() throws InvalidInputException,
        NoRecordsFetchedException
    {
        List<LicenseDetail> licenceDetailList = new ArrayList<LicenseDetail>();
        LicenseDetail licenseDetail = new LicenseDetail();
        AccountsMaster accountsMaster = new AccountsMaster();

        accountsMaster.setMaxUsersAllowed( 3 );
        licenseDetail.setAccountsMaster( accountsMaster );
        licenceDetailList.add( licenseDetail );

        Mockito.doReturn( licenceDetailList ).when( licenseDetailsDao )
        .findByColumn( Mockito.eq( LicenseDetail.class ), Mockito.anyString(), Mockito.any( Company.class ) );
        
        Mockito.when( userDao.getUsersCountForCompany( (Company) Mockito.anyObject() ) ).thenReturn( 2l );

        Company company = new Company();
        User user = new User();
        user.setCompany( company );
        assertEquals( "test", true, userManagementServiceImpl.isUserAdditionAllowed( user ) );
    }


    @Test
    public void testSsUserAdditionAllowedForMaxUserAllowedISLessThanNoOfUsers() throws InvalidInputException,
        NoRecordsFetchedException
    {
        List<LicenseDetail> licenceDetailList = new ArrayList<LicenseDetail>();
        LicenseDetail licenseDetail = new LicenseDetail();
        AccountsMaster accountsMaster = new AccountsMaster();

        accountsMaster.setMaxUsersAllowed( 2 );
        licenseDetail.setAccountsMaster( accountsMaster );
        licenceDetailList.add( licenseDetail );

        Mockito.doReturn( licenceDetailList ).when( licenseDetailsDao )
        .findByColumn( Mockito.eq( LicenseDetail.class ), Mockito.anyString(), Mockito.any( Company.class ) );
        
        Mockito.when( userDao.getUsersCountForCompany( (Company) Mockito.anyObject() ) ).thenReturn( 3l );

        Company company = new Company();
        User user = new User();
        user.setCompany( company );
        assertEquals( "test", false, userManagementServiceImpl.isUserAdditionAllowed( user ) );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUnassignBranchAdminForNullAdmin() throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementServiceImpl.unassignBranchAdmin( null, 2l, 2l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUnassignBranchAdminForInvalidBranchId() throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementServiceImpl.unassignBranchAdmin( new User(), 0l, 2l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUnassignBranchAdminForInvalidUserId() throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementServiceImpl.unassignBranchAdmin( new User(), 1l, 0l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUnassignBranchAdminForNoUserFound() throws InvalidInputException, NoRecordsFetchedException
    {
        Mockito.when( userDao.findById( Mockito.eq( User.class ), Mockito.anyLong() ) ).thenReturn( null );
        userManagementServiceImpl.unassignBranchAdmin( new User(), 1l, 1l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUnassignRegionAdminForNullAdmin() throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementServiceImpl.unassignRegionAdmin( null, 2l, 2l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUnassignRegionAdminForInvalidBranchId() throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementServiceImpl.unassignRegionAdmin( new User(), 0l, 2l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUnassignRegionAdminForInvalidUserId() throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementServiceImpl.unassignRegionAdmin( new User(), 1l, 0l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUnassignRegionAdminForNoUserFound() throws InvalidInputException, NoRecordsFetchedException
    {
        Mockito.when( userDao.findById( Mockito.eq( User.class ), Mockito.anyLong() ) ).thenReturn( null );
        userManagementServiceImpl.unassignRegionAdmin( new User(), 1l, 1l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetUserByUserIdForNoUserFound() throws InvalidInputException, NoRecordsFetchedException
    {
        Mockito.when( userDao.findById( Mockito.eq( User.class ), Mockito.anyLong() ) ).thenReturn( null );
        userManagementServiceImpl.getUserByUserId( 1l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetUserByUserIdForNoUserSettingsFound() throws InvalidInputException, NoRecordsFetchedException
    {
        Mockito.when( userDao.findById( Mockito.eq( User.class ), Mockito.anyLong() ) ).thenReturn( new User() );
        Mockito.when( organizationUnitSettingsDao.fetchAgentSettingsById( Mockito.anyLong() ) ).thenReturn( null );
        userManagementServiceImpl.getUserByUserId( 1l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetUserObjByUserIdForNoUserFound() throws InvalidInputException, NoRecordsFetchedException
    {
        Mockito.when( userDao.findById( Mockito.eq( User.class ), Mockito.anyLong() ) ).thenReturn( null );
        userManagementServiceImpl.getUserObjByUserId( 0l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetUserByProfileIdForNoUserProfileFound() throws InvalidInputException, NoRecordsFetchedException
    {
        Mockito.when( userProfileDao.findById( Mockito.eq( UserProfile.class ), Mockito.anyLong() ) ).thenReturn( null );
        userManagementServiceImpl.getUserByProfileId( 0l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetMultipleUsersByUserId() throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementServiceImpl.getMultipleUsersByUserId( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetBranchesAssignedToUserForNullUser() throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementServiceImpl.getBranchesAssignedToUser( null );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testGetBranchesAssignedToUserForNoBranchFound() throws InvalidInputException, NoRecordsFetchedException
    {
        Mockito.when( userProfileDao.getBranchIdsForUser( (User) Mockito.anyObject() ) ).thenReturn( null );
        userManagementServiceImpl.getBranchesAssignedToUser( new User() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetUsersForCompanyForNullUser() throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementServiceImpl.getUsersForCompany( null );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testGetUsersForCompanyForNoUserFound() throws InvalidInputException, NoRecordsFetchedException
    {
        Mockito.when( userProfileDao.getBranchIdsForUser( (User) Mockito.anyObject() ) ).thenReturn( null );
        User user = new User();
        user.setCompany( new Company() );
        userManagementServiceImpl.getUsersForCompany( user );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAssignBranchAdminForNullAdmin() throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementServiceImpl.assignBranchAdmin( null, 2l, 2l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAssignBranchAdminForInvalidBranchId() throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementServiceImpl.assignBranchAdmin( new User(), 0l, 2l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAssignBranchAdminForInvalidUserId() throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementServiceImpl.assignBranchAdmin( new User(), 2l, 0l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAssignBranchAdminForNoUserFound() throws InvalidInputException, NoRecordsFetchedException
    {
        Mockito.when( userDao.findById( Mockito.eq( User.class ), Mockito.anyLong() ) ).thenReturn( null );
        userManagementServiceImpl.assignBranchAdmin( new User(), 2l, 2l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAssignUserToBranchForNullAdmin() throws InvalidInputException, NoRecordsFetchedException, SolrException
    {
        userManagementServiceImpl.assignUserToBranch( null, 1l, 1l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUnassignUserFromBranchNullAdmin() throws InvalidInputException, NoRecordsFetchedException, SolrException
    {
        userManagementServiceImpl.unassignUserFromBranch( null, 1l, 1l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUnassignUserFromBranchForNoUserFound() throws InvalidInputException, NoRecordsFetchedException,
        SolrException
    {
        Mockito.when( userDao.findById( Mockito.eq( User.class ), Mockito.anyLong() ) ).thenReturn( null );
        userManagementServiceImpl.assignUserToBranch( new User(), 1l, 1l );
    }


    @SuppressWarnings ( "unchecked")
    @Test ( expected = InvalidInputException.class)
    public void testUnassignUserFromBranchForNoUserProfileFound() throws InvalidInputException, NoRecordsFetchedException,
        SolrException
    {
        Mockito.when( userDao.findById( Mockito.eq( User.class ), Mockito.anyLong() ) ).thenReturn( new User() );
        Mockito.when( userProfileDao.findByKeyValue( Mockito.eq( UserProfile.class ), Mockito.anyMap() ) ).thenReturn( null );
        userManagementServiceImpl.assignUserToBranch( new User(), 1l, 1l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateUserForNullAdmin() throws InvalidInputException, NoRecordsFetchedException, SolrException
    {
        userManagementServiceImpl.updateUser( null, 1l, true );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateUserForNoUserFound() throws InvalidInputException, NoRecordsFetchedException, SolrException
    {
        Mockito.when( userDao.findById( Mockito.eq( User.class ), Mockito.anyLong() ) ).thenReturn( null );
        userManagementServiceImpl.updateUser( new User(), 1l, true );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateUserProfileForNullAdmin() throws InvalidInputException, NoRecordsFetchedException, SolrException
    {
        userManagementServiceImpl.updateUserProfile( null, 1l, 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateUserProfileForNoUserProfileFound() throws InvalidInputException, NoRecordsFetchedException,
        SolrException
    {
        Mockito.when( userProfileDao.findById( Mockito.eq( UserProfile.class ), Mockito.anyLong() ) ).thenReturn( null );
        userManagementServiceImpl.updateUserProfile( new User(), 1l, 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testremoveUserProfileForNoUserProfileFound() throws InvalidInputException, NoRecordsFetchedException,
        SolrException
    {
        Mockito.when( userProfileDao.findById( Mockito.eq( UserProfile.class ), Mockito.anyLong() ) ).thenReturn( null );
        userManagementServiceImpl.removeUserProfile( 1l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testupdateUserInSolrForNullUser() throws InvalidInputException, NoRecordsFetchedException, SolrException
    {
        userManagementServiceImpl.updateUserInSolr( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateUserProfilesStatusForNullAdmin() throws InvalidInputException, NoRecordsFetchedException,
        SolrException
    {
        userManagementServiceImpl.updateUserProfilesStatus( null, 1l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateUserProfilesStatusForNoUserProfileFound() throws InvalidInputException, NoRecordsFetchedException,
        SolrException
    {
        Mockito.when( userProfileDao.findById( Mockito.eq( UserProfile.class ), Mockito.anyLong() ) ).thenReturn( null );
        userManagementServiceImpl.updateUserProfilesStatus( new User(), 1l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdatePrimaryProfileOfUserForNullUser() throws InvalidInputException, NoRecordsFetchedException,
        SolrException
    {
        userManagementServiceImpl.updatePrimaryProfileOfUser( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSendRegistrationCompletionLinkForNullEmailId() throws InvalidInputException, NoRecordsFetchedException,
        SolrException, UndeliveredEmailException
    {
        userManagementServiceImpl.sendRegistrationCompletionLink( null, "test", "test", 2l, "test", "test", false );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSendRegistrationCompletionLinkForInvalidEmailId() throws InvalidInputException, NoRecordsFetchedException,
        SolrException, UndeliveredEmailException
    {
        userManagementServiceImpl.sendRegistrationCompletionLink( "", "test", "test", 2l, "test", "test", false );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSendRegistrationCompletionLinkForNullProfileName() throws InvalidInputException, NoRecordsFetchedException,
        SolrException, UndeliveredEmailException
    {
        userManagementServiceImpl.sendRegistrationCompletionLink( "test", "test", "test", 2l, null, "test", false );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSendRegistrationCompletionLinkForInvalidProfileName() throws InvalidInputException,
        NoRecordsFetchedException, SolrException, UndeliveredEmailException
    {
        userManagementServiceImpl.sendRegistrationCompletionLink( null, "test", "test", 2l, "", "test", false );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAllUserProfilesForUserForNullUser() throws InvalidInputException
    {
        userManagementServiceImpl.getAllUserProfilesForUser( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSendVerificationLinkForNullUser() throws InvalidInputException, UndeliveredEmailException
    {
        userManagementServiceImpl.sendVerificationLink( null );
    }


    @SuppressWarnings ( "unchecked")
    @Test ( expected = InvalidInputException.class)
    public void testSendVerificationLinkForGenerateUrlError() throws InvalidInputException, UndeliveredEmailException
    {
        Mockito.when( urlGenerator.generateUrl( Mockito.anyMap(), Mockito.anyString() ) ).thenThrow(
            new InvalidInputException() );
        userManagementServiceImpl.sendVerificationLink( new User() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUserExistsForNullUserName() throws InvalidInputException, UndeliveredEmailException
    {
        userManagementServiceImpl.userExists( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUserExistsForEmptyUserName() throws InvalidInputException, UndeliveredEmailException
    {
        userManagementServiceImpl.userExists( "" );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetCanonicalUserSettingsForNullUser() throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementServiceImpl.getCanonicalUserSettings( null, AccountType.COMPANY );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetCanonicalUserSettingsForNullAccountType() throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementServiceImpl.getCanonicalUserSettings( new User(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetUserSettingsForInvalidAgentId() throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementServiceImpl.getUserSettings( 0l );
    }

    @Test ( expected = InvalidInputException.class)
    public void testFetchAgentContactDetailByEncryptedIdForNull() throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementServiceImpl.fetchAgentContactDetailByEncryptedId( null );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testFetchAgentContactDetailByEncryptedIdForEmpty() throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementServiceImpl.fetchAgentContactDetailByEncryptedId( "" );
    }
    
    
    @Test ( expected = InvalidInputException.class)
    public void testAssignUserToCompanyForNullAdmin() throws InvalidInputException, NoRecordsFetchedException, SolrException
    {
        userManagementServiceImpl.assignUserToCompany( null, 1l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAssignUserToCompanyForInvalidUserId() throws InvalidInputException, NoRecordsFetchedException,
        SolrException
    {
        userManagementServiceImpl.assignUserToCompany( new User(), 0l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAssignUserToRegionForNullAdmin() throws InvalidInputException, NoRecordsFetchedException, SolrException
    {
        userManagementServiceImpl.assignUserToRegion( null, 0l, 0l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAssignUserToRegionForInvalidUserId() throws InvalidInputException, NoRecordsFetchedException, SolrException
    {
        userManagementServiceImpl.assignUserToRegion( new User(), 0l, 1l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAssignUserToRegionForInvalidRegionId() throws InvalidInputException, NoRecordsFetchedException,
        SolrException
    {
        userManagementServiceImpl.assignUserToRegion( new User(), 1l, 10l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testInsertAgentSettingsForNullUser() throws InvalidInputException, NoRecordsFetchedException, SolrException
    {
        userManagementServiceImpl.insertAgentSettings( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGenerateIndividualProfileNameForInvalidUserId() throws InvalidInputException, NoRecordsFetchedException,
        SolrException
    {
        userManagementServiceImpl.generateIndividualProfileName( 0l, "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGenerateIndividualProfileNameForNullEmailId() throws InvalidInputException, NoRecordsFetchedException,
        SolrException
    {
        userManagementServiceImpl.generateIndividualProfileName( 1l, "test", null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGenerateIndividualProfileNameForEmptyEmailId() throws InvalidInputException, NoRecordsFetchedException,
        SolrException
    {
        userManagementServiceImpl.generateIndividualProfileName( 1l, "test", "" );
    }


    @Test ( expected = InvalidInputException.class)
    public void testupdateProfileUrlInRegionSettingsForNullSettings() throws InvalidInputException, NoRecordsFetchedException,
        SolrException
    {
        userManagementServiceImpl.updateProfileUrlInRegionSettings( "test", "test", null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testupdateProfileUrlInAgentSettingsForNullSettings() throws InvalidInputException, NoRecordsFetchedException,
        SolrException
    {
        userManagementServiceImpl.updateProfileUrlInAgentSettings( "test", "test", null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testupdateProfileUrlInCompanySettingsForNullSettings() throws InvalidInputException, NoRecordsFetchedException,
        SolrException
    {
        userManagementServiceImpl.updateProfileUrlInCompanySettings( "test", "test", null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testupdateProfileUrlInBranchSettingsForNullSettings() throws InvalidInputException, NoRecordsFetchedException,
        SolrException
    {
        userManagementServiceImpl.updateProfileUrlInBranchSettings( "test", "test", null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateUserOnCompleteRegistrationForNullUser() throws InvalidInputException, NoRecordsFetchedException,
        SolrException
    {
        userManagementServiceImpl.updateUserOnCompleteRegistration( null, "test", 01, "test", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateUserCountModificationNotificationForNull() throws InvalidInputException, NoRecordsFetchedException,
        SolrException
    {
        userManagementServiceImpl.updateUserCountModificationNotification( null );
    }



    @Test ( expected = InvalidInputException.class)
    public void testGetUsersByUserIdsForNullList() throws InvalidInputException, NoRecordsFetchedException, SolrException
    {
        userManagementServiceImpl.getUsersByUserIds( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetUsersByUserIdsForEmptyList() throws InvalidInputException, NoRecordsFetchedException, SolrException
    {
        userManagementServiceImpl.getUsersByUserIds( new HashSet<Long>() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetActiveUserByEmailAndCompanyForNullEmail() throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementServiceImpl.getActiveUserByEmailAndCompany( 1l, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetActiveUserByEmailAndCompanyForEmptyEmail() throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementServiceImpl.getActiveUserByEmailAndCompany( 1l, "" );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testGetActiveUserByEmailAndCompanyForNoCompanyFound() throws InvalidInputException, NoRecordsFetchedException
    {
        Mockito.when( companyDao.findById( Mockito.eq( Company.class ), Mockito.anyLong() ) ).thenReturn( null );
        userManagementServiceImpl.getActiveUserByEmailAndCompany( 1l, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAgentUserProfileForUserIdWithInvalidUserId() throws InvalidInputException
    {
        userManagementServiceImpl.getAgentUserProfileForUserId( 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAgentUserProfileForUserIdWhenUserDoesNotExist() throws InvalidInputException
    {
        Mockito.when( userDao.findById( Mockito.eq( User.class ), Mockito.anyLong() ) ).thenReturn( null );
        userManagementServiceImpl.getAgentUserProfileForUserId( 2 );
    }


    //Tests for RestoreDeletedUser
    @Test ( expected = InvalidInputException.class)
    public void testRestoreDeletedUserForInvalidUserId() throws InvalidInputException, SolrException
    {
        userManagementServiceImpl.restoreDeletedUser( 0l, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void testRestoreDeletedUserForUserDoesntExist() throws InvalidInputException, SolrException
    {
        Mockito.doReturn( null ).when( userManagementServiceImpl ).getUserByUserId( Matchers.anyLong() );
        userManagementServiceImpl.restoreDeletedUser( 50l, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void testRestoreDeletedUserForUserAlreadyActive() throws InvalidInputException, SolrException
    {
        User user = new User();
        user.setStatus( CommonConstants.STATUS_ACTIVE );
        Mockito.doReturn( user ).when( userManagementServiceImpl ).getUserByUserId( Matchers.anyLong() );
        userManagementServiceImpl.restoreDeletedUser( 50l, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void testRestoreDeletedUserForUserWithSameEmailExists() throws InvalidInputException, SolrException, NoRecordsFetchedException
    {
        User user = new User();
        String email = "abc@xyz.com";
        user.setEmailId( email );
        user.setUserId( 50l );
        user.setStatus( CommonConstants.STATUS_INACTIVE );
        Mockito.doReturn( user ).when( userManagementServiceImpl ).getUserByUserId( Matchers.anyLong() );

        List<User> userList = new ArrayList<User>();
        User user2 = new User();

        user2.setUserId( 2l );
        user2.setEmailId( email );
        userList.add( user2 );
        Mockito.doReturn( user2 ).when( userManagementServiceImpl ).getUserByEmailAddress( email );
        userManagementServiceImpl.restoreDeletedUser( 50l, false );
    }


    //Tests for SearchUsersInCompanyByMultipleCriteria
    @Test ( expected = InvalidInputException.class)
    public void testSearchUsersInCompanyByMultipleCriteriaForQueriesNull() throws InvalidInputException,
        NoRecordsFetchedException
    {
        userManagementServiceImpl.searchUsersInCompanyByMultipleCriteria( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSearchUsersInCompanyByMultipleCriteriaForQueriesEmpty() throws InvalidInputException,
        NoRecordsFetchedException
    {
        userManagementServiceImpl.searchUsersInCompanyByMultipleCriteria( new HashMap<String, Object>() );
    }


    @SuppressWarnings ( "unchecked")
    @Test ( expected = NoRecordsFetchedException.class)
    public void testSearchUsersInCompanyByMultipleCriteriaForUsersNull() throws InvalidInputException,
        NoRecordsFetchedException
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put( "test", "test" );
        Mockito.when( userDao.findByKeyValueAscending( Mockito.eq( User.class ), Mockito.anyMap(), Mockito.anyString() ) )
            .thenReturn( null );
        userManagementServiceImpl.searchUsersInCompanyByMultipleCriteria( map );
    }


    @SuppressWarnings ( "unchecked")
    @Test ( expected = NoRecordsFetchedException.class)
    public void testSearchUsersInCompanyByMultipleCriteriaForUsersEmpty() throws InvalidInputException,
        NoRecordsFetchedException
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put( "test", "test" );
        Mockito.when( userDao.findByKeyValueAscending( Mockito.eq( User.class ), Mockito.anyMap(), Mockito.anyString() ) )
            .thenReturn( new ArrayList<User>() );
        userManagementServiceImpl.searchUsersInCompanyByMultipleCriteria( map );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetUserByEmailAddressForEmailIdNull() throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementServiceImpl.getUserByEmailAddress( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetUserByEmailAddressForEmailIdEmpty() throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementServiceImpl.getUserByEmailAddress( "" );
    }


    @SuppressWarnings ( "unchecked")
    @Test ( expected = NoRecordsFetchedException.class)
    public void testGetUserByEmailAddressForMappingsNullActiveUserNull() throws InvalidInputException,
        NoRecordsFetchedException
    {
        Mockito.when( userEmailMappingDao.findByKeyValue( Mockito.eq( UserEmailMapping.class ), Mockito.anyMap() ) )
            .thenReturn( null );
        Mockito.when( userDao.getActiveUser( Mockito.anyString() ) ).thenThrow( NoRecordsFetchedException.class );
        userManagementServiceImpl.getUserByEmailAddress( TestConstants.TEST_MAIL_ID_STRING );
    }
    
    
    @Test ( expected = InvalidInputException.class)
    public void testDeleteSSAdminForNullAdmin() throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementServiceImpl.deleteSSAdmin( null , 0l );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testDeleteSSAdminForNoUserFound() throws InvalidInputException, NoRecordsFetchedException
    {
        Mockito.when( userDao.findById( Mockito.eq( User.class ), Mockito.anyLong() ) ).thenReturn( null );
        userManagementServiceImpl.deleteSSAdmin( new User() , 0l );
    }
    
    
    @Test ( expected = InvalidInputException.class)
    public void testsaveEmailUserMappingForNullEmail() throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementServiceImpl.saveEmailUserMapping( null, 1l );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testsaveEmailUserMappingForEmptyEmail() throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementServiceImpl.saveEmailUserMapping( "", 1l );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testsaveEmailUserMappingForInvalidUserId() throws InvalidInputException, NoRecordsFetchedException
    {
        Mockito.when( userDao.findById( Mockito.eq( User.class ), Mockito.anyLong() ) ).thenReturn( null );
        userManagementServiceImpl.saveEmailUserMapping( "test", 1l );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testsaveIgnoredEmailCompanyMappingForNullEmail() throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementServiceImpl.saveIgnoredEmailCompanyMapping( null, 1l );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testsaveIgnoredEmailCompanyMappingForEmptyEmail() throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementServiceImpl.saveIgnoredEmailCompanyMapping( "", 1l );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testsaveIgnoredEmailCompanyMappingForInvalidCompanyId() throws InvalidInputException, NoRecordsFetchedException
    {
        Mockito.when( companyDao.findById( Mockito.eq( Company.class ), Mockito.anyLong() ) ).thenReturn( null );
        userManagementServiceImpl.saveIgnoredEmailCompanyMapping( "test", 1l );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testgetUsersAndEmailMappingForCompanyForInvalidCompanyId() throws InvalidInputException, NoRecordsFetchedException
    {
        userManagementServiceImpl.getUsersAndEmailMappingForCompany( 0, 1, 10, -1 );
    }
}
