package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
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
import com.realtech.socialsurvey.core.dao.BranchDao;
import com.realtech.socialsurvey.core.dao.CompanyDao;
import com.realtech.socialsurvey.core.dao.DisabledAccountDao;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.RegionDao;
import com.realtech.socialsurvey.core.dao.SurveyDetailsDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.entities.AccountsMaster;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.CollectionDotloopProfileMapping;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.DisabledAccount;
import com.realtech.socialsurvey.core.entities.EncompassCrmInfo;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.entities.LoopProfileMapping;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.RetriedTransaction;
import com.realtech.socialsurvey.core.entities.SurveySettings;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserAssignmentException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.payment.exception.PaymentException;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.utils.EmailFormatHelper;


public class OrganizationManagementServiceImplTest
{
    @Spy
    @InjectMocks
    private OrganizationManagementServiceImpl organizationManagementServiceImpl;

    @Mock
    private GenericDao<LicenseDetail, Long> licenceDetailDao;

    @Mock
    private GenericDao<RetriedTransaction, Long> retriedTransactionDao;

    @Mock
    private GenericDao<CollectionDotloopProfileMapping, Long> collectionDotloopProfileMappingDao;

    @Mock
    private GenericDao<LoopProfileMapping, Long> loopProfileMappingDao;

    @Mock
    private BranchDao branchDao;

    @Mock
    private OrganizationUnitSettingsDao organizationUnitSettingsDao;

    @Mock
    private EmailFormatHelper emailFormatHelper;

    @Mock
    private CompanyDao companyDao;

    @Mock
    private DisabledAccountDao disabledAccountDao;

    @Mock
    private RegionDao regionDao;

    @Mock
    private SolrSearchService solrSearchService;

    @Mock
    private ProfileManagementService profileManagementService;

    @Mock
    private UserDao userDao;

    @Mock
    private UserManagementService userManagementService;

    @Mock
    private UserProfileDao userProfileDao;
    
    @Mock
    private SurveyDetailsDao surveyDetailsDao;


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
    public void testAddAccountTypeForCompanyWithNullStrAccountType() throws InvalidInputException, SolrException
    {
        organizationManagementServiceImpl.addAccountTypeForCompany( new User(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddAccountTypeForCompanyWithEmptyStrAccountType() throws InvalidInputException, SolrException
    {
        organizationManagementServiceImpl.addAccountTypeForCompany( new User(), TestConstants.TEST_EMPTY_STRING );
    }


    @Test
    public void testFetchAccountTypeMasterIdForCompanyWhenLicenseDetailListIsNull() throws InvalidInputException, SolrException
    {
        Mockito.when( licenceDetailDao.findByColumn( Mockito.eq( LicenseDetail.class ), Mockito.anyString(),
            Mockito.any( Company.class ) ) ).thenReturn( null );
        assertEquals( "Return value does not match expected", 0,
            organizationManagementServiceImpl.fetchAccountTypeMasterIdForCompany( new Company() ) );
    }


    @Test
    public void testFetchAccountTypeMasterIdForCompanyWhenLicenseDetailListIsEmpty() throws InvalidInputException, SolrException
    {
        Mockito.when( licenceDetailDao.findByColumn( Mockito.eq( LicenseDetail.class ), Mockito.anyString(),
            Mockito.any( Company.class ) ) ).thenReturn( new ArrayList<LicenseDetail>() );
        assertEquals( "Return value does not match expected", 0,
            organizationManagementServiceImpl.fetchAccountTypeMasterIdForCompany( new Company() ) );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetCompanySettingsWithNullUser() throws InvalidInputException
    {
        organizationManagementServiceImpl.getCompanySettings( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetCompanySettingsWithUserHavingCompanyAsNull() throws InvalidInputException
    {
        organizationManagementServiceImpl.getCompanySettings( new User() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetRegionSettingsForUserProfilesWithNullUserProfileList() throws InvalidInputException
    {
        organizationManagementServiceImpl.getRegionSettingsForUserProfiles( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetRegionSettingsForUserProfilesWithEmptyUserProfileList() throws InvalidInputException
    {
        organizationManagementServiceImpl.getRegionSettingsForUserProfiles( new ArrayList<UserProfile>() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetBranchSettingsForUserProfilesWithNullUserProfileList()
        throws InvalidInputException, NoRecordsFetchedException
    {
        organizationManagementServiceImpl.getBranchSettingsForUserProfiles( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetBranchSettingsForUserProfilesWithEmptyUserProfileList()
        throws InvalidInputException, NoRecordsFetchedException
    {
        organizationManagementServiceImpl.getBranchSettingsForUserProfiles( new ArrayList<UserProfile>() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetRegionSettingsWithInvalidRegionId() throws InvalidInputException
    {
        organizationManagementServiceImpl.getRegionSettings( 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetBranchSettingsWithInvalidBranchId() throws InvalidInputException, NoRecordsFetchedException
    {
        organizationManagementServiceImpl.getBranchSettings( 0 );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testGetBranchSettingsWhenBranchIsNull() throws InvalidInputException, NoRecordsFetchedException
    {
        Mockito.when( organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( Mockito.anyLong(), Mockito.anyString() ) )
            .thenReturn( new OrganizationUnitSettings() );
        Mockito.when( branchDao.findById( Mockito.eq( Branch.class ), Mockito.anyLong() ) ).thenReturn( null );
        organizationManagementServiceImpl.getBranchSettings( 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetBranchSettingsDefaultWithInvalidBranchId() throws InvalidInputException, NoRecordsFetchedException
    {
        organizationManagementServiceImpl.getBranchSettingsDefault( 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAgentSettingsWithInvalidAgentId() throws InvalidInputException, NoRecordsFetchedException
    {
        organizationManagementServiceImpl.getAgentSettings( 0 );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testGetAgentSettingsWhenAgentSettingsIsNull() throws InvalidInputException, NoRecordsFetchedException
    {
        Mockito.when( organizationUnitSettingsDao.fetchAgentSettingsById( Mockito.anyLong() ) ).thenReturn( null );
        organizationManagementServiceImpl.getAgentSettings( 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateCRMDetailsWithNullCompanySettings() throws InvalidInputException
    {
        organizationManagementServiceImpl.updateCRMDetails( null, new EncompassCrmInfo(), TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateCRMDetailsWithNullCrmInfo() throws InvalidInputException
    {
        organizationManagementServiceImpl.updateCRMDetails( new OrganizationUnitSettings(), null, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateCRMDetailsForAnyUnitSettingsWithNullUnitSettings() throws InvalidInputException
    {
        organizationManagementServiceImpl.updateCRMDetailsForAnyUnitSettings( null, TestConstants.TEST_STRING,
            new EncompassCrmInfo(), TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateCRMDetailsForAnyUnitSettingsWithNullCrmInfo() throws InvalidInputException
    {
        organizationManagementServiceImpl.updateCRMDetailsForAnyUnitSettings( new OrganizationUnitSettings(),
            TestConstants.TEST_STRING, null, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateSurveySettingsWithNullCompanySettings() throws InvalidInputException
    {
        organizationManagementServiceImpl.updateSurveySettings( null, new SurveySettings() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateScoreForSurveyWithNullUnitSettings() throws InvalidInputException
    {
        organizationManagementServiceImpl.updateScoreForSurvey( TestConstants.TEST_STRING, null, new SurveySettings() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateLocationEnabledWithCompanySettings() throws InvalidInputException
    {
        organizationManagementServiceImpl.updateLocationEnabled( null, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateAccountDisabledWithNullCompanySettings() throws InvalidInputException
    {
        organizationManagementServiceImpl.updateAccountDisabled( null, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateSurveyParticipationMailBodyWithNullCompanySettings() throws InvalidInputException
    {
        organizationManagementServiceImpl.updateSurveyParticipationMailBody( null, TestConstants.TEST_STRING,
            TestConstants.TEST_STRING, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateSurveyParticipationMailBodyWithNullMailSubjectAndBody() throws InvalidInputException
    {
        organizationManagementServiceImpl.updateSurveyParticipationMailBody( new OrganizationUnitSettings(), null, null,
            TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateSurveyParticipationMailBodyWithNullMailCategory() throws InvalidInputException
    {
        organizationManagementServiceImpl.updateSurveyParticipationMailBody( new OrganizationUnitSettings(),
            TestConstants.TEST_STRING, TestConstants.TEST_STRING, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateSurveyParticipationMailBodyWithInvalidMailCategory() throws InvalidInputException
    {
        Mockito.when(
            emailFormatHelper.replaceEmailBodyParamsWithDefaultValue( Mockito.anyString(), Mockito.anyListOf( String.class ) ) )
            .thenReturn( TestConstants.TEST_STRING );
        organizationManagementServiceImpl.updateSurveyParticipationMailBody( new OrganizationUnitSettings(),
            TestConstants.TEST_STRING, TestConstants.TEST_STRING, TestConstants.TEST_EMPTY_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDeleteMailBodyFromSettingWithNullCompanySettings() throws NonFatalException
    {
        organizationManagementServiceImpl.deleteMailBodyFromSetting( null, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDeleteMailBodyFromSettingWithNullMailCategory() throws NonFatalException
    {
        organizationManagementServiceImpl.deleteMailBodyFromSetting( new OrganizationUnitSettings(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDeleteMailBodyFromSettingWithInvalidMailCategory() throws NonFatalException
    {
        organizationManagementServiceImpl.deleteMailBodyFromSetting( new OrganizationUnitSettings(),
            TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testRevertSurveyParticipationMailBodyWithNullCompanySettings() throws NonFatalException
    {
        organizationManagementServiceImpl.revertSurveyParticipationMailBody( null, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testRevertSurveyParticipationMailBodyWithNullMailCategory() throws NonFatalException
    {
        organizationManagementServiceImpl.revertSurveyParticipationMailBody( new OrganizationUnitSettings(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testRevertSurveyParticipationMailBodyWithInvalidMailCategory() throws NonFatalException
    {
        organizationManagementServiceImpl.revertSurveyParticipationMailBody( new OrganizationUnitSettings(),
            TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddDisabledAccountWithInvalidCompanyId()
        throws InvalidInputException, NoRecordsFetchedException, PaymentException
    {
        organizationManagementServiceImpl.addDisabledAccount( 0, false, 1 );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testAddDisabledAccountWhenLicenseDetailListIsNull()
        throws InvalidInputException, NoRecordsFetchedException, PaymentException
    {
        Mockito.when( companyDao.findById( Mockito.eq( Company.class ), Mockito.anyLong() ) ).thenReturn( new Company() );
        Mockito.when( licenceDetailDao.findByKeyValue( Mockito.eq( LicenseDetail.class ),
            Mockito.anyMapOf( String.class, Object.class ) ) ).thenReturn( null );
        organizationManagementServiceImpl.addDisabledAccount( 1, false, 1 );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testAddDisabledAccountWhenLicenseDetailListIsEmpty()
        throws InvalidInputException, NoRecordsFetchedException, PaymentException
    {
        Mockito.when( companyDao.findById( Mockito.eq( Company.class ), Mockito.anyLong() ) ).thenReturn( new Company() );
        Mockito.when( licenceDetailDao.findByKeyValue( Mockito.eq( LicenseDetail.class ),
            Mockito.anyMapOf( String.class, Object.class ) ) ).thenReturn( new ArrayList<LicenseDetail>() );
        organizationManagementServiceImpl.addDisabledAccount( 1, false, 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDeleteDisabledAccountWithInvalidCompanyId()
        throws InvalidInputException, NoRecordsFetchedException, PaymentException
    {
        organizationManagementServiceImpl.inactiveDisabledAccount( 0 );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testDeleteDisabledAccountWhenDisabledAccountsListIsNull()
        throws InvalidInputException, NoRecordsFetchedException, PaymentException
    {
        Mockito.when( companyDao.findById( Mockito.eq( Company.class ), Mockito.anyLong() ) ).thenReturn( new Company() );
        Mockito.when( disabledAccountDao.findByKeyValue( Mockito.eq( DisabledAccount.class ),
            Mockito.anyMapOf( String.class, Object.class ) ) ).thenReturn( null );
        organizationManagementServiceImpl.inactiveDisabledAccount( 1 );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testDeleteDisabledAccountWhenDisabledAccountsListIsEmpty()
        throws InvalidInputException, NoRecordsFetchedException, PaymentException
    {
        Mockito.when( companyDao.findById( Mockito.eq( Company.class ), Mockito.anyLong() ) ).thenReturn( new Company() );
        Mockito.when( disabledAccountDao.findByKeyValue( Mockito.eq( DisabledAccount.class ),
            Mockito.anyMapOf( String.class, Object.class ) ) ).thenReturn( new ArrayList<DisabledAccount>() );
        organizationManagementServiceImpl.inactiveDisabledAccount( 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpgradeDefaultRegionWithNullRegion() throws InvalidInputException
    {
        organizationManagementServiceImpl.upgradeDefaultRegion( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpgradeDefaultBranchWithNullBranch() throws InvalidInputException
    {
        organizationManagementServiceImpl.upgradeDefaultBranch( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testFetchDefaultRegionWithNullCompany() throws InvalidInputException
    {
        organizationManagementServiceImpl.fetchDefaultRegion( null );
    }


    @Test
    public void testFetchDefaultRegionWhenRegionListIsNull() throws InvalidInputException
    {
        Mockito.when( regionDao.findByKeyValue( Mockito.eq( Region.class ), Mockito.anyMapOf( String.class, Object.class ) ) )
            .thenReturn( null );
        assertNull( "Region list is not as expected", organizationManagementServiceImpl.fetchDefaultRegion( new Company() ) );
    }


    @Test
    public void testFetchDefaultRegionWhenRegionListIsEmpty() throws InvalidInputException
    {
        Mockito.when( regionDao.findByKeyValue( Mockito.eq( Region.class ), Mockito.anyMapOf( String.class, Object.class ) ) )
            .thenReturn( new ArrayList<Region>() );
        assertNull( "Region list is not as expected", organizationManagementServiceImpl.fetchDefaultRegion( new Company() ) );
    }


    @Test
    public void testFetchDefaultRegionWhenRegionListIsMoreThanOne() throws InvalidInputException
    {
        List<Region> regionList = new ArrayList<Region>();
        regionList.add( new Region() );
        regionList.add( new Region() );
        Mockito.when( regionDao.findByKeyValue( Mockito.eq( Region.class ), Mockito.anyMapOf( String.class, Object.class ) ) )
            .thenReturn( regionList );
        assertNull( "Region list is not as expected", organizationManagementServiceImpl.fetchDefaultRegion( new Company() ) );
    }


    @Test
    public void testFetchDefaultRegionWhenRegionListIsDoesntContainDefaultBySystem() throws InvalidInputException
    {
        Region region = new Region();
        region.setIsDefaultBySystem( 0 );
        List<Region> regionList = new ArrayList<Region>();
        regionList.add( region );
        Mockito.when( regionDao.findByKeyValue( Mockito.eq( Region.class ), Mockito.anyMapOf( String.class, Object.class ) ) )
            .thenReturn( regionList );
        assertNull( "Region list is not as expected", organizationManagementServiceImpl.fetchDefaultRegion( new Company() ) );
    }


    @Test ( expected = InvalidInputException.class)
    public void testFetchDefaultBranchWithNullCompany() throws InvalidInputException
    {
        organizationManagementServiceImpl.fetchDefaultBranch( null );
    }


    @Test
    public void testFetchDefaultBranchWhenBranchListIsNull() throws InvalidInputException
    {
        Mockito.when( branchDao.findByKeyValue( Mockito.eq( Branch.class ), Mockito.anyMapOf( String.class, Object.class ) ) )
            .thenReturn( null );
        assertNull( "Branch list is not as expected", organizationManagementServiceImpl.fetchDefaultBranch( new Company() ) );
    }


    @Test
    public void testFetchDefaultBranchWhenBranchListIsEmpty() throws InvalidInputException
    {
        Mockito.when( branchDao.findByKeyValue( Mockito.eq( Branch.class ), Mockito.anyMapOf( String.class, Object.class ) ) )
            .thenReturn( new ArrayList<Branch>() );
        assertNull( "Branch list is not as expected", organizationManagementServiceImpl.fetchDefaultBranch( new Company() ) );
    }


    @Test
    public void testFetchDefaultBranchWhenBranchListIsMoreThanOne() throws InvalidInputException
    {
        List<Branch> branchList = new ArrayList<Branch>();
        branchList.add( new Branch() );
        branchList.add( new Branch() );
        Mockito.when( branchDao.findByKeyValue( Mockito.eq( Branch.class ), Mockito.anyMapOf( String.class, Object.class ) ) )
            .thenReturn( branchList );
        assertNull( "Branch list is not as expected", organizationManagementServiceImpl.fetchDefaultBranch( new Company() ) );
    }


    @Test
    public void testFetchDefaultBranchWhenBranchListIsDoesntContainDefaultBySystem() throws InvalidInputException
    {
        Branch branch = new Branch();
        branch.setIsDefaultBySystem( 0 );
        List<Branch> branchList = new ArrayList<Branch>();
        branchList.add( branch );
        Mockito.when( branchDao.findByKeyValue( Mockito.eq( Branch.class ), Mockito.anyMapOf( String.class, Object.class ) ) )
            .thenReturn( branchList );
        assertNull( "Branch list is not as expected", organizationManagementServiceImpl.fetchDefaultBranch( new Company() ) );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpgradeToCompanyWithNullCompany() throws InvalidInputException, SolrException, NoRecordsFetchedException
    {
        organizationManagementServiceImpl.upgradeToCompany( null );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testUpgradeToCompanyWithWhenDefaultBranchIsNull()
        throws InvalidInputException, SolrException, NoRecordsFetchedException
    {
        Mockito.doReturn( null ).when( organizationManagementServiceImpl ).fetchDefaultBranch( Matchers.any( Company.class ) );
        organizationManagementServiceImpl.upgradeToCompany( new Company() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpgradeToEnterpriseWithNullCompany() throws InvalidInputException, SolrException, NoRecordsFetchedException
    {
        organizationManagementServiceImpl.upgradeToEnterprise( null, 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpgradeToEnterpriseWithInvalidAccountMasterId()
        throws InvalidInputException, SolrException, NoRecordsFetchedException
    {
        organizationManagementServiceImpl.upgradeToEnterprise( new Company(), 0 );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testUpgradeToEnterpriseWithWhenDefaultBranchIsNotNullAndDefaultRegionIsNull()
        throws InvalidInputException, SolrException, NoRecordsFetchedException
    {
        Mockito.doReturn( new Branch() ).when( organizationManagementServiceImpl )
            .fetchDefaultBranch( Matchers.any( Company.class ) );
        Mockito.doReturn( new Branch() ).when( organizationManagementServiceImpl )
            .upgradeDefaultBranch( Matchers.any( Branch.class ) );
        Mockito.doNothing().when( organizationManagementServiceImpl ).insertBranchSettings( Matchers.any( Branch.class ) );
        Mockito.doNothing().when( solrSearchService ).addOrUpdateBranchToSolr( Mockito.any( Branch.class ) );
        Mockito.doReturn( null ).when( organizationManagementServiceImpl ).fetchDefaultRegion( Matchers.any( Company.class ) );
        organizationManagementServiceImpl.upgradeToEnterprise( new Company(), 1 );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testUpgradeToEnterpriseWhenDefaultBranchIsNullAndAccountMasterIdIsNot3()
        throws InvalidInputException, SolrException, NoRecordsFetchedException
    {
        Mockito.doReturn( null ).when( organizationManagementServiceImpl ).fetchDefaultBranch( Matchers.any( Company.class ) );
        organizationManagementServiceImpl.upgradeToEnterprise( new Company(), 1 );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testUpgradeToEnterpriseWhenDefaultBranchIsNullAndAccountMasterIdIs3ButDefaultRegionIsNull()
        throws InvalidInputException, SolrException, NoRecordsFetchedException
    {
        Mockito.doReturn( null ).when( organizationManagementServiceImpl ).fetchDefaultBranch( Matchers.any( Company.class ) );
        Mockito.doReturn( null ).when( organizationManagementServiceImpl ).fetchDefaultRegion( Matchers.any( Company.class ) );
        organizationManagementServiceImpl.upgradeToEnterprise( new Company(), 3 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpgradeAccountWithNullCompany() throws NoRecordsFetchedException, InvalidInputException, SolrException
    {
        organizationManagementServiceImpl.upgradeAccount( null, 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpgradeAccountWithInvalidAccountMasterId()
        throws NoRecordsFetchedException, InvalidInputException, SolrException
    {
        organizationManagementServiceImpl.upgradeAccount( new Company(), 0 );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testUpgradeAccountWhenLicenseDetailListIsNull()
        throws NoRecordsFetchedException, InvalidInputException, SolrException
    {
        Mockito.when( licenceDetailDao.findByKeyValue( Mockito.eq( LicenseDetail.class ),
            Mockito.anyMapOf( String.class, Object.class ) ) ).thenReturn( null );
        organizationManagementServiceImpl.upgradeAccount( new Company(), 1 );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testUpgradeAccountWhenLicenseDetailListIsEmpty()
        throws NoRecordsFetchedException, InvalidInputException, SolrException
    {
        Mockito.when( licenceDetailDao.findByKeyValue( Mockito.eq( LicenseDetail.class ),
            Mockito.anyMapOf( String.class, Object.class ) ) ).thenReturn( new ArrayList<LicenseDetail>() );
        organizationManagementServiceImpl.upgradeAccount( new Company(), 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpgradeAccountWithInvalidNewMasterAccountId()
        throws NoRecordsFetchedException, InvalidInputException, SolrException
    {
        LicenseDetail licenseDetail = new LicenseDetail();
        licenseDetail.setAccountsMaster( new AccountsMaster() );
        List<LicenseDetail> licenseDetailList = new ArrayList<LicenseDetail>();
        licenseDetailList.add( licenseDetail );
        Mockito.when( licenceDetailDao.findByKeyValue( Mockito.eq( LicenseDetail.class ),
            Mockito.anyMapOf( String.class, Object.class ) ) ).thenReturn( licenseDetailList );
        organizationManagementServiceImpl.upgradeAccount( new Company(), 5 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpgradeAccountWithNewAccountMasterIdAsTeamWhenLicenseDetailContainsInvalidCurrentMasterId()
        throws NoRecordsFetchedException, InvalidInputException, SolrException
    {
        AccountsMaster accountsMaster = new AccountsMaster();
        accountsMaster.setAccountsMasterId( 0 );
        LicenseDetail licenseDetail = new LicenseDetail();
        licenseDetail.setAccountsMaster( accountsMaster );
        List<LicenseDetail> licenseDetailList = new ArrayList<LicenseDetail>();
        licenseDetailList.add( licenseDetail );
        Mockito.when( licenceDetailDao.findByKeyValue( Mockito.eq( LicenseDetail.class ),
            Mockito.anyMapOf( String.class, Object.class ) ) ).thenReturn( licenseDetailList );
        organizationManagementServiceImpl.upgradeAccount( new Company(), CommonConstants.ACCOUNTS_MASTER_TEAM );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpgradeAccountWithNewAccountMasterIdAsCompanyWhenLicenseDetailContainsInvalidCurrentMasterId()
        throws NoRecordsFetchedException, InvalidInputException, SolrException
    {
        AccountsMaster accountsMaster = new AccountsMaster();
        accountsMaster.setAccountsMasterId( 0 );
        LicenseDetail licenseDetail = new LicenseDetail();
        licenseDetail.setAccountsMaster( accountsMaster );
        List<LicenseDetail> licenseDetailList = new ArrayList<LicenseDetail>();
        licenseDetailList.add( licenseDetail );
        Mockito.when( licenceDetailDao.findByKeyValue( Mockito.eq( LicenseDetail.class ),
            Mockito.anyMapOf( String.class, Object.class ) ) ).thenReturn( licenseDetailList );
        organizationManagementServiceImpl.upgradeAccount( new Company(), CommonConstants.ACCOUNTS_MASTER_COMPANY );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpgradeAccountWithNewAccountMasterIdAsEnterpriseWhenLicenseDetailContainsInvalidCurrentMasterId()
        throws NoRecordsFetchedException, InvalidInputException, SolrException
    {
        AccountsMaster accountsMaster = new AccountsMaster();
        accountsMaster.setAccountsMasterId( 0 );
        LicenseDetail licenseDetail = new LicenseDetail();
        licenseDetail.setAccountsMaster( accountsMaster );
        List<LicenseDetail> licenseDetailList = new ArrayList<LicenseDetail>();
        licenseDetailList.add( licenseDetail );
        Mockito.when( licenceDetailDao.findByKeyValue( Mockito.eq( LicenseDetail.class ),
            Mockito.anyMapOf( String.class, Object.class ) ) ).thenReturn( licenseDetailList );
        organizationManagementServiceImpl.upgradeAccount( new Company(), CommonConstants.ACCOUNTS_MASTER_ENTERPRISE );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testUpgradeAccountWithNewAccountMasterIdAsTeamWhenLicenseDetailContainsValidCurrentMasterId()
        throws NoRecordsFetchedException, InvalidInputException, SolrException
    {
        AccountsMaster accountsMaster = new AccountsMaster();
        accountsMaster.setAccountsMasterId( 1 );
        LicenseDetail licenseDetail = new LicenseDetail();
        licenseDetail.setAccountsMaster( accountsMaster );
        List<LicenseDetail> licenseDetailList = new ArrayList<LicenseDetail>();
        licenseDetailList.add( licenseDetail );
        Mockito.when( licenceDetailDao.findByKeyValue( Mockito.eq( LicenseDetail.class ),
            Mockito.anyMapOf( String.class, Object.class ) ) ).thenReturn( licenseDetailList );
        Mockito.doReturn( null ).when( organizationManagementServiceImpl ).fetchDefaultBranch( Mockito.any( Company.class ) );
        organizationManagementServiceImpl.upgradeAccount( new Company(), CommonConstants.ACCOUNTS_MASTER_TEAM );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetRegionsForCompanyWithInvalidCompanyId() throws InvalidInputException, ProfileNotFoundException
    {
        organizationManagementServiceImpl.getRegionsForCompany( TestConstants.TEST_INT );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetBranchesBySearchKey() throws InvalidInputException, ProfileNotFoundException, SolrException
    {
        organizationManagementServiceImpl.getRegionsBySearchKey( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetRegionsBySearchKey() throws InvalidInputException, ProfileNotFoundException, SolrException
    {
        organizationManagementServiceImpl.getBranchesBySearchKey( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetUsersBySearchKey() throws InvalidInputException, ProfileNotFoundException, SolrException
    {
        organizationManagementServiceImpl.getUsersBySearchKey( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAllVerticalsMasterWhenVerticalsMastersListIsEmpty() throws InvalidInputException
    {
        organizationManagementServiceImpl.getAllVerticalsMaster();
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetBranchesUnderCompanyWithNullCompanyProfileName()
        throws InvalidInputException, NoRecordsFetchedException, ProfileNotFoundException
    {
        organizationManagementServiceImpl.getBranchesUnderCompany( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetBranchesUnderCompanyWithEmptyCompanyProfileName()
        throws InvalidInputException, NoRecordsFetchedException, ProfileNotFoundException
    {
        organizationManagementServiceImpl.getBranchesUnderCompany( TestConstants.TEST_EMPTY_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetBranchesUnderCompanyWithInvalidCompanyId()
        throws InvalidInputException, NoRecordsFetchedException, ProfileNotFoundException
    {
        organizationManagementServiceImpl.getBranchesUnderCompany( TestConstants.TEST_INT );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testGetDefaultRegionForCompanyWhenRegionListIsNull() throws InvalidInputException, NoRecordsFetchedException
    {
        Mockito.when( regionDao.findByKeyValue( Mockito.eq( Region.class ), Mockito.anyMapOf( String.class, Object.class ) ) )
            .thenReturn( null );
        organizationManagementServiceImpl.getDefaultRegionForCompany( new Company() );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testGetDefaultRegionForCompanyWhenRegionListIsEmpty() throws InvalidInputException, NoRecordsFetchedException
    {
        Mockito.when( regionDao.findByKeyValue( Mockito.eq( Region.class ), Mockito.anyMapOf( String.class, Object.class ) ) )
            .thenReturn( new ArrayList<Region>() );
        organizationManagementServiceImpl.getDefaultRegionForCompany( new Company() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetDefaultBranchForRegionWithInvalidRegionId() throws InvalidInputException, NoRecordsFetchedException
    {
        organizationManagementServiceImpl.getDefaultBranchForRegion( 0 );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testGetDefaultBranchForRegionWhenBranchesListIsNull() throws InvalidInputException, NoRecordsFetchedException
    {
        Mockito.when( branchDao.findByKeyValue( Mockito.eq( Branch.class ), Mockito.anyMapOf( String.class, Object.class ) ) )
            .thenReturn( null );
        organizationManagementServiceImpl.getDefaultBranchForRegion( 1 );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testGetDefaultBranchForRegionWhenBranchesListIsEmpty() throws InvalidInputException, NoRecordsFetchedException
    {
        Mockito.when( branchDao.findByKeyValue( Mockito.eq( Branch.class ), Mockito.anyMapOf( String.class, Object.class ) ) )
            .thenReturn( new ArrayList<Branch>() );
        organizationManagementServiceImpl.getDefaultBranchForRegion( 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetBranchesForRegionWithNullCompanyProfileName()
        throws InvalidInputException, NoRecordsFetchedException, ProfileNotFoundException
    {
        organizationManagementServiceImpl.getBranchesForRegion( null, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetBranchesForRegionWithNEmptyCompanyProfileName()
        throws InvalidInputException, NoRecordsFetchedException, ProfileNotFoundException
    {
        organizationManagementServiceImpl.getBranchesForRegion( TestConstants.TEST_EMPTY_STRING, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetBranchesForRegionWithNullRegionProfileName()
        throws InvalidInputException, NoRecordsFetchedException, ProfileNotFoundException
    {
        organizationManagementServiceImpl.getBranchesForRegion( TestConstants.TEST_STRING, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetBranchesForRegionWithEmptyRegionProfileName()
        throws InvalidInputException, NoRecordsFetchedException, ProfileNotFoundException
    {
        organizationManagementServiceImpl.getBranchesForRegion( TestConstants.TEST_STRING, TestConstants.TEST_EMPTY_STRING );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testGetBranchesForRegionWithNullRegionSettings()
        throws InvalidInputException, NoRecordsFetchedException, ProfileNotFoundException
    {
        Mockito.when( profileManagementService.getRegionByProfileName( Mockito.anyString(), Mockito.anyString() ) )
            .thenReturn( null );
        organizationManagementServiceImpl.getBranchesForRegion( TestConstants.TEST_STRING, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetBranchesByRegionIdWithInvalidRegionId() throws InvalidInputException
    {
        organizationManagementServiceImpl.getBranchesByRegionId( 0 );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testAddNewRegionWithUserWhenAssignedUserIsNull()
        throws InvalidInputException, SolrException, NoRecordsFetchedException, UserAssignmentException
    {
        User user = new User();
        Mockito.doReturn( new Region() ).when( organizationManagementServiceImpl ).addNewRegion( Mockito.any( User.class ),
            Mockito.anyString(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
            Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString() );
        Mockito.doReturn( new Branch() ).when( organizationManagementServiceImpl ).addNewBranch( Mockito.any( User.class ),
            Mockito.anyLong(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
            Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString() );

        Mockito.when( userDao.findById( Mockito.eq( User.class ), Mockito.anyLong() ) ).thenReturn( null );
        organizationManagementServiceImpl.addNewRegionWithUser( user, TestConstants.TEST_STRING, 0, "Pago Pago", "",
            "United States", "US", "AS", "Pago Pago", "65827", 1, new String[] { TestConstants.TEST_MAIL_ID_STRING }, false,
            true, true );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAssignRegionToUserWithNullAdminUser() throws InvalidInputException, NoRecordsFetchedException, SolrException
    {
        organizationManagementServiceImpl.assignRegionToUser( null, 1, new User(), false );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAssignRegionToUserWithInvalidRegionId()
        throws InvalidInputException, NoRecordsFetchedException, SolrException
    {
        organizationManagementServiceImpl.assignRegionToUser( new User(), 0, new User(), false );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAssignRegionToUserWithNullAssigneeUser()
        throws InvalidInputException, NoRecordsFetchedException, SolrException
    {
        organizationManagementServiceImpl.assignRegionToUser( new User(), 1, null, false );
    }


    //Unable to mock messageUtils. (It's a static class)
/*    @Test ( expected = InvalidInputException.class)
    public void testAssignRegionToUserWhenSameUserProfileExist()
        throws InvalidInputException, NoRecordsFetchedException, SolrException
    {
        UserProfile userProfile = new UserProfile();
        userProfile.setRegionId( 1 );
        userProfile.setBranchId( 1 );
        userProfile.setProfilesMaster( new ProfilesMaster() );
        userProfile.setStatus( CommonConstants.STATUS_ACTIVE );
        User assigneeUser = new User();
        assigneeUser.setUserProfiles( Arrays.asList( new UserProfile[] { userProfile } ) );

        Mockito.doReturn( new Branch() ).when( organizationManagementServiceImpl )
            .getDefaultBranchForRegion( Mockito.anyLong() );
        Mockito.doReturn( userProfile ).when( userManagementService ).createUserProfile( Mockito.any( User.class ),
            Mockito.any( Company.class ), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong(),
            Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyString(),
            Mockito.anyString() );

        organizationManagementServiceImpl.assignRegionToUser( new User(), 1, assigneeUser, false );
    }*/


    @Test ( expected = NoRecordsFetchedException.class)
    public void testAddNewBranchWithUserWhenAssigneeUserIsNull()
        throws InvalidInputException, SolrException, NoRecordsFetchedException, UserAssignmentException
    {
        Mockito.doReturn( new Branch() ).when( organizationManagementServiceImpl ).addNewBranch( Mockito.any( User.class ),
            Mockito.anyLong(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
            Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString() );
        Mockito.when( userDao.findById( Mockito.eq( User.class ), Mockito.anyLong() ) ).thenReturn( null );
        organizationManagementServiceImpl.addNewBranchWithUser( new User(), TestConstants.TEST_STRING, 1, 0, "Pago Pago", "",
            "United States", "US", "AS", "Pago Pago", "65287", 1, TestConstants.TEST_EMPTY_ARRAY, false, true, true );

    }


    @Test ( expected = InvalidInputException.class)
    public void testAssignBranchToUserWithNullAdminUser() throws InvalidInputException, NoRecordsFetchedException, SolrException
    {
        organizationManagementServiceImpl.assignBranchToUser( null, 1, 1, new User(), false );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAssignBranchToUserWithInvalidBranchId()
        throws InvalidInputException, NoRecordsFetchedException, SolrException
    {
        organizationManagementServiceImpl.assignBranchToUser( new User(), 0, 1, new User(), false );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAssignBranchToUserWithInvalidRegionId()
        throws InvalidInputException, NoRecordsFetchedException, SolrException
    {
        organizationManagementServiceImpl.assignBranchToUser( new User(), 1, 0, new User(), false );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAssignBranchToUserWithNullAssigneeUser()
        throws InvalidInputException, NoRecordsFetchedException, SolrException
    {
        organizationManagementServiceImpl.assignBranchToUser( new User(), 1, 1, null, false );
    }

    //Unable to mock messageUtils. (It's a static class)
/*    @Test ( expected = InvalidInputException.class)
    public void testAssignBranchToUserWhenSameUserProfileExist()
        throws InvalidInputException, NoRecordsFetchedException, SolrException
    {
        UserProfile userProfile = new UserProfile();
        userProfile.setRegionId( 1 );
        userProfile.setBranchId( 1 );
        userProfile.setProfilesMaster( new ProfilesMaster() );
        userProfile.setStatus( CommonConstants.STATUS_ACTIVE );
        User assigneeUser = new User();
        assigneeUser.setUserProfiles( Arrays.asList( new UserProfile[] { userProfile } ) );

        Mockito.doReturn( userProfile ).when( userManagementService ).createUserProfile( Mockito.any( User.class ),
            Mockito.any( Company.class ), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong(),
            Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyString(),
            Mockito.anyString() );

        organizationManagementServiceImpl.assignBranchToUser( new User(), 1, 1, assigneeUser, false );
    }*/


    @Test ( expected = NoRecordsFetchedException.class)
    public void testAddIndividualWhenAssigneeUserIsNull()
        throws InvalidInputException, NoRecordsFetchedException, SolrException, UserAssignmentException
    {
        Mockito.when( userDao.findById( Mockito.eq( User.class ), Mockito.anyLong() ) ).thenReturn( null );
        organizationManagementServiceImpl.addIndividual( new User(), 1, 1, 1, TestConstants.TEST_EMPTY_ARRAY, false, true,
            true, true, false, null, null );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testAddIndividualWhenDefaultRegionIsNull()
        throws InvalidInputException, NoRecordsFetchedException, SolrException, UserAssignmentException
    {
        Mockito.doReturn( null ).when( organizationManagementServiceImpl )
            .getDefaultRegionForCompany( Mockito.any( Company.class ) );
        Mockito.when( userDao.findById( Mockito.eq( User.class ), Mockito.anyLong() ) ).thenReturn( new User() );
        organizationManagementServiceImpl.addIndividual( new User(), 1, 0, 0, TestConstants.TEST_EMPTY_ARRAY, false, true,
            true, true, false, null, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAllBranchesForCompanyWithNullCompany() throws InvalidInputException
    {
        organizationManagementServiceImpl.getAllBranchesForCompany( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAllBranchesForCompanyWithProjectionsWithNullCompany() throws InvalidInputException
    {
        organizationManagementServiceImpl.getAllBranchesForCompanyWithProjections( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAllRegionsForCompanyWithNullCompany() throws InvalidInputException
    {
        organizationManagementServiceImpl.getAllRegionsForCompany( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAllRegionsForCompanyWithProjectionsWithNullCompany() throws InvalidInputException
    {
        organizationManagementServiceImpl.getAllRegionsForCompanyWithProjections( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateBranchStatusWithNullUser() throws InvalidInputException, SolrException
    {
        organizationManagementServiceImpl.updateBranchStatus( null, 1, 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateBranchStatusWithInvalidBranchId() throws InvalidInputException, SolrException
    {
        organizationManagementServiceImpl.updateBranchStatus( new User(), 0, 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateBranchStatusWhenBranchIsNull() throws InvalidInputException, SolrException
    {
        Mockito.when( branchDao.findById( Mockito.eq( Branch.class ), Mockito.anyLong() ) ).thenReturn( null );
        organizationManagementServiceImpl.updateBranchStatus( new User(), 1, 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateRegionStatusWithNullUser() throws InvalidInputException, SolrException
    {
        organizationManagementServiceImpl.updateRegionStatus( null, 1, 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateRegionStatusWithInvalidRegionId() throws InvalidInputException, SolrException
    {
        organizationManagementServiceImpl.updateRegionStatus( new User(), 0, 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateRegionStatusWhenRegionIsNull() throws InvalidInputException, SolrException
    {
        Mockito.when( regionDao.findById( Mockito.eq( Region.class ), Mockito.anyLong() ) ).thenReturn( null );
        organizationManagementServiceImpl.updateRegionStatus( new User(), 1, 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAllBranchesInRegionWithInvalidRegionId() throws InvalidInputException, SolrException
    {
        organizationManagementServiceImpl.getAllBranchesInRegion( 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAllBranchesInRegionWhenRegionIsNull() throws InvalidInputException, SolrException
    {
        Mockito.when( regionDao.findById( Mockito.eq( Region.class ), Mockito.anyLong() ) ).thenReturn( null );
        organizationManagementServiceImpl.getAllBranchesInRegion( 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAllBranchesInRegionWithProjectionsWithInvalidRegionId() throws InvalidInputException, SolrException
    {
        organizationManagementServiceImpl.getAllBranchesInRegionWithProjections( 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAllBranchesInRegionWithProjectionsWhenRegionIsNull() throws InvalidInputException, SolrException
    {
        Mockito.when( regionDao.findById( Mockito.eq( Region.class ), Mockito.anyLong() ) ).thenReturn( null );
        organizationManagementServiceImpl.getAllBranchesInRegionWithProjections( 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetCountBranchesInRegionWithInvalidRegionId() throws InvalidInputException, SolrException
    {
        organizationManagementServiceImpl.getCountBranchesInRegion( 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetCountBranchesInRegionWhenRegionIsNull() throws InvalidInputException, SolrException
    {
        Mockito.when( regionDao.findById( Mockito.eq( Region.class ), Mockito.anyLong() ) ).thenReturn( null );
        organizationManagementServiceImpl.getCountBranchesInRegion( 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAllUserProfilesInBranchWithInvalidBranchId() throws InvalidInputException
    {
        organizationManagementServiceImpl.getAllUserProfilesInBranch( 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetCountUsersInBranchWithInvalidBranchId() throws InvalidInputException
    {
        organizationManagementServiceImpl.getCountUsersInBranch( 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testIsBranchAdditionAllowedWithNullUser() throws InvalidInputException
    {
        organizationManagementServiceImpl.isBranchAdditionAllowed( null, AccountType.FREE );
    }


    @Test ( expected = InvalidInputException.class)
    public void testIsBranchAdditionAllowedWithNullAccountType() throws InvalidInputException
    {
        organizationManagementServiceImpl.isBranchAdditionAllowed( new User(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testIsBranchAdditionAllowedWithInvalidAccountType() throws InvalidInputException
    {
        organizationManagementServiceImpl.isBranchAdditionAllowed( new User(), AccountType.FREE );
    }


    @Test
    public void testIsBranchAdditionAllowedWhenUserIsBranchAdmin() throws InvalidInputException
    {
        User branchAdmin = new User();
        branchAdmin.setBranchAdmin( true );
        assertFalse( "Branch addition value is not as expected",
            organizationManagementServiceImpl.isBranchAdditionAllowed( branchAdmin, AccountType.COMPANY ) );
    }


    @Test
    public void testIsBranchAdditionAllowedWhenUserIsRegionAdmin() throws InvalidInputException
    {
        User regionAdmin = new User();
        regionAdmin.setRegionAdmin( true );
        assertTrue( "Branch addition value is not as expected",
            organizationManagementServiceImpl.isBranchAdditionAllowed( regionAdmin, AccountType.COMPANY ) );
    }


    @Test
    public void testIsBranchAdditionAllowedWhenUserIsCompanyAdmin() throws InvalidInputException
    {
        User companyAdmin = new User();
        companyAdmin.setCompanyAdmin( true );
        assertTrue( "Branch addition value is not as expected",
            organizationManagementServiceImpl.isBranchAdditionAllowed( companyAdmin, AccountType.COMPANY ) );
    }


    @Test
    public void testIsBranchAdditionAllowedWhenUserIsAgent() throws InvalidInputException
    {
        User agent = new User();
        agent.setAgent( true );
        assertFalse( "Branch addition value is not as expected",
            organizationManagementServiceImpl.isBranchAdditionAllowed( agent, AccountType.COMPANY ) );
    }


    @Test
    public void testIsBranchAdditionAllowedWithAccountTypeTeam() throws InvalidInputException
    {
        assertFalse( "Branch addition value is not as expected",
            organizationManagementServiceImpl.isBranchAdditionAllowed( new User(), AccountType.TEAM ) );
    }


    @Test
    public void testIsBranchAdditionAllowedWithAccountTypeIndividual() throws InvalidInputException
    {
        assertFalse( "Branch addition value is not as expected",
            organizationManagementServiceImpl.isBranchAdditionAllowed( new User(), AccountType.INDIVIDUAL ) );
    }


    @Test ( expected = InvalidInputException.class)
    public void testIsRegionAdditionAllowedWithNullUser() throws InvalidInputException
    {
        organizationManagementServiceImpl.isRegionAdditionAllowed( null, AccountType.FREE );
    }


    @Test ( expected = InvalidInputException.class)
    public void testIsRegionAdditionAllowedWithNullAccountType() throws InvalidInputException
    {
        organizationManagementServiceImpl.isRegionAdditionAllowed( new User(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testIsRegionAdditionAllowedWithInvalidAccountType() throws InvalidInputException
    {
        organizationManagementServiceImpl.isRegionAdditionAllowed( new User(), AccountType.FREE );
    }


    @Test
    public void testIsRegionAdditionAllowedAllowedWhenUserIsBranchAdmin() throws InvalidInputException
    {
        User branchAdmin = new User();
        branchAdmin.setBranchAdmin( true );
        assertFalse( "Region addition value is not as expected",
            organizationManagementServiceImpl.isRegionAdditionAllowed( branchAdmin, AccountType.ENTERPRISE ) );
    }


    @Test
    public void testIsRegionAdditionAllowedAllowedWhenUserIsRegionAdmin() throws InvalidInputException
    {
        User regionAdmin = new User();
        regionAdmin.setRegionAdmin( true );
        assertFalse( "Region addition value is not as expected",
            organizationManagementServiceImpl.isRegionAdditionAllowed( regionAdmin, AccountType.ENTERPRISE ) );
    }


    @Test
    public void testIsRegionAdditionAllowedWhenUserIsCompanyAdmin() throws InvalidInputException
    {
        User companyAdmin = new User();
        companyAdmin.setCompanyAdmin( true );
        assertTrue( "Region addition value is not as expected",
            organizationManagementServiceImpl.isRegionAdditionAllowed( companyAdmin, AccountType.ENTERPRISE ) );
    }


    @Test
    public void testIsRegionAdditionAllowedWhenUserIsAgent() throws InvalidInputException
    {
        User agent = new User();
        agent.setAgent( true );
        assertFalse( "Region addition value is not as expected",
            organizationManagementServiceImpl.isRegionAdditionAllowed( agent, AccountType.ENTERPRISE ) );
    }


    @Test
    public void testIsRegionAdditionAllowedWithAccountTypeTeam() throws InvalidInputException
    {
        assertFalse( "Region addition value is not as expected",
            organizationManagementServiceImpl.isRegionAdditionAllowed( new User(), AccountType.TEAM ) );
    }


    @Test
    public void testIsRegionAdditionAllowedWithAccountTypeIndividual() throws InvalidInputException
    {
        assertFalse( "Region addition value is not as expected",
            organizationManagementServiceImpl.isRegionAdditionAllowed( new User(), AccountType.INDIVIDUAL ) );
    }


    @Test
    public void testIsRegionAdditionAllowedWithAccountTypeCompany() throws InvalidInputException
    {
        assertFalse( "Region addition value is not as expected",
            organizationManagementServiceImpl.isRegionAdditionAllowed( new User(), AccountType.COMPANY ) );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddNewBranchWithNullUser() throws InvalidInputException, SolrException
    {
        organizationManagementServiceImpl.addNewBranch( null, 1, 0, TestConstants.TEST_STRING, "Pago Pago",
            TestConstants.TEST_EMPTY_STRING, "United States", "US", "AS", "Pago Pago", "65287" );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddNewBranchWithNullBranchName() throws InvalidInputException, SolrException
    {
        organizationManagementServiceImpl.addNewBranch( new User(), 1, 0, null, "Pago Pago", TestConstants.TEST_EMPTY_STRING,
            "United States", "US", "AS", "Pago Pago", "65287" );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddNewBranchWithEmptyBranchName() throws InvalidInputException, SolrException
    {
        organizationManagementServiceImpl.addNewBranch( new User(), 1, 0, TestConstants.TEST_EMPTY_STRING, "Pago Pago",
            TestConstants.TEST_EMPTY_STRING, "United States", "US", "AS", "Pago Pago", "65287" );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddNewBranchWhenRegionIsNull() throws InvalidInputException, SolrException
    {
        Mockito.when( regionDao.findById( Mockito.eq( Region.class ), Mockito.anyLong() ) ).thenReturn( null );
        organizationManagementServiceImpl.addNewBranch( new User(), 1, 0, TestConstants.TEST_STRING, "Pago Pago",
            TestConstants.TEST_EMPTY_STRING, "United States", "US", "AS", "Pago Pago", "65287" );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGenerateAndSetBranchProfileNameAndUrlWithNullBranch() throws InvalidInputException
    {
        organizationManagementServiceImpl.generateAndSetBranchProfileNameAndUrl( null, new OrganizationUnitSettings() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGenerateAndSetBranchProfileNameAndUrlWhenBranchNameIsNull() throws InvalidInputException
    {
        organizationManagementServiceImpl.generateAndSetBranchProfileNameAndUrl( new Branch(), new OrganizationUnitSettings() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGenerateAndSetBranchProfileNameAndUrlWhenBranchNameIsEmpty() throws InvalidInputException
    {
        Branch branch = new Branch();
        branch.setBranch( TestConstants.TEST_EMPTY_STRING );
        organizationManagementServiceImpl.generateAndSetBranchProfileNameAndUrl( branch, new OrganizationUnitSettings() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddNewRegionWithNullUser() throws InvalidInputException, SolrException
    {
        organizationManagementServiceImpl.addNewRegion( null, TestConstants.TEST_STRING, 0, "Pago Pago", "", "United States",
            "US", "AS", "Pago Pago", "65287" );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddNewRegionWithNullRegionName() throws InvalidInputException, SolrException
    {
        organizationManagementServiceImpl.addNewRegion( new User(), null, 0, "Pago Pago", "", "United States", "US", "AS",
            "Pago Pago", "65287" );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddNewRegionWithEmptyRegionName() throws InvalidInputException, SolrException
    {
        organizationManagementServiceImpl.addNewRegion( new User(), TestConstants.TEST_EMPTY_STRING, 0, "Pago Pago", "",
            "United States", "US", "AS", "Pago Pago", "65287" );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGenerateAndSetRegionProfileNameAndUrlWithNullRegion() throws InvalidInputException
    {
        organizationManagementServiceImpl.generateAndSetRegionProfileNameAndUrl( null, new OrganizationUnitSettings() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGenerateAndSetRegionProfileNameAndUrlWithNullRegionName() throws InvalidInputException
    {
        organizationManagementServiceImpl.generateAndSetRegionProfileNameAndUrl( new Region(), new OrganizationUnitSettings() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGenerateAndSetRegionProfileNameAndUrlWithEmptyRegionName() throws InvalidInputException
    {
        Region region = new Region();
        region.setRegion( TestConstants.TEST_EMPTY_STRING );
        organizationManagementServiceImpl.generateAndSetRegionProfileNameAndUrl( region, new OrganizationUnitSettings() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetRegionIdsForUserWithInvalidProfileMasterId() throws InvalidInputException, NoRecordsFetchedException
    {
        organizationManagementServiceImpl.getRegionIdsForUser( new User(), 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetRegionIdsForUserWithNullUser() throws InvalidInputException, NoRecordsFetchedException
    {
        organizationManagementServiceImpl.getRegionIdsForUser( null, 1 );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testGetRegionIdsForUserWhenUserProfilesIsNull() throws InvalidInputException, NoRecordsFetchedException
    {
        organizationManagementServiceImpl.getRegionIdsForUser( new User(), 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetBranchIdsForUserWithInvalidProfileMasterId() throws InvalidInputException, NoRecordsFetchedException
    {
        organizationManagementServiceImpl.getBranchIdsForUser( new User(), 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetBranchIdsForUserWithNullUser() throws InvalidInputException, NoRecordsFetchedException
    {
        organizationManagementServiceImpl.getBranchIdsForUser( null, 1 );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testGetBranchIdsForUserWhenUserProfilesIsNull() throws InvalidInputException, NoRecordsFetchedException
    {
        organizationManagementServiceImpl.getBranchIdsForUser( new User(), 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetBranchesUnderCompanyFromSolrWithNullCompany()
        throws InvalidInputException, NoRecordsFetchedException, SolrException
    {
        organizationManagementServiceImpl.getBranchesUnderCompanyFromSolr( null, 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetUsersUnderCompanyFromSolrWithNullCompany()
        throws InvalidInputException, NoRecordsFetchedException, SolrException
    {
        organizationManagementServiceImpl.getUsersUnderCompanyFromSolr( null, 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAllUsersUnderCompanyFromSolrWithNullCompany()
        throws InvalidInputException, NoRecordsFetchedException, SolrException
    {
        organizationManagementServiceImpl.getAllUsersUnderCompanyFromSolr( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetUsersUnderRegionFromSolrWithNullRegionIdsList()
        throws InvalidInputException, NoRecordsFetchedException, SolrException
    {
        organizationManagementServiceImpl.getUsersUnderRegionFromSolr( null, 1, 10 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetUsersUnderRegionFromSolrWithEmptyRegionIdsList()
        throws InvalidInputException, NoRecordsFetchedException, SolrException
    {
        organizationManagementServiceImpl.getUsersUnderRegionFromSolr( new HashSet<Long>(), 1, 10 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateRegionWithNullUser()
        throws InvalidInputException, SolrException, NoRecordsFetchedException, UserAssignmentException
    {
        organizationManagementServiceImpl.updateRegion( null, 1, TestConstants.TEST_STRING, "Pago Pago",
            TestConstants.TEST_EMPTY_STRING, "United States", "US", "AS", "Pago Pago", "65287", 1,
            TestConstants.TEST_EMPTY_ARRAY, false, true, true );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateRegionWithNullRegionName()
        throws InvalidInputException, SolrException, NoRecordsFetchedException, UserAssignmentException
    {
        organizationManagementServiceImpl.updateRegion( new User(), 1, null, "Pago Pago", TestConstants.TEST_EMPTY_STRING,
            "United States", "US", "AS", "Pago Pago", "65287", 1, TestConstants.TEST_EMPTY_ARRAY, false, true, true );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateRegionWithEmptyRegionName()
        throws InvalidInputException, SolrException, NoRecordsFetchedException, UserAssignmentException
    {
        organizationManagementServiceImpl.updateRegion( new User(), 1, TestConstants.TEST_EMPTY_STRING, "Pago Pago",
            TestConstants.TEST_EMPTY_STRING, "United States", "US", "AS", "Pago Pago", "65287", 1,
            TestConstants.TEST_EMPTY_ARRAY, false, true, true );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateRegionWithInvalidRegionId()
        throws InvalidInputException, SolrException, NoRecordsFetchedException, UserAssignmentException
    {
        organizationManagementServiceImpl.updateRegion( new User(), 0, TestConstants.TEST_STRING, "Pago Pago",
            TestConstants.TEST_EMPTY_STRING, "United States", "US", "AS", "Pago Pago", "65287", 1,
            TestConstants.TEST_EMPTY_ARRAY, false, true, true );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testUpdateRegionWhenRegionIsNull()
        throws InvalidInputException, SolrException, NoRecordsFetchedException, UserAssignmentException
    {
        Mockito.when( regionDao.findById( Mockito.eq( Region.class ), Mockito.anyLong() ) ).thenReturn( null );
        organizationManagementServiceImpl.updateRegion( new User(), 1, TestConstants.TEST_STRING, "Pago Pago",
            TestConstants.TEST_EMPTY_STRING, "United States", "US", "AS", "Pago Pago", "65287", 1,
            TestConstants.TEST_EMPTY_ARRAY, false, true, true );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testUpdateRegionWhenAssigneeUserIsNull()
        throws InvalidInputException, SolrException, NoRecordsFetchedException, UserAssignmentException
    {
        Mockito.when( regionDao.findById( Mockito.eq( Region.class ), Mockito.anyLong() ) ).thenReturn( new Region() );
        Mockito.when( userDao.findById( Mockito.eq( User.class ), Mockito.anyLong() ) ).thenReturn( null );
        organizationManagementServiceImpl.updateRegion( new User(), 1, TestConstants.TEST_STRING, "Pago Pago",
            TestConstants.TEST_EMPTY_STRING, "United States", "US", "AS", "Pago Pago", "65287", 1,
            TestConstants.TEST_EMPTY_ARRAY, false, true, true );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateBranchWithNullUser() throws InvalidInputException, SolrException
    {
        organizationManagementServiceImpl.updateBranch( 1, 1, TestConstants.TEST_STRING, "Pago Pago",
            TestConstants.TEST_EMPTY_STRING, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateBranchWithNullBranchName() throws InvalidInputException, SolrException
    {
        organizationManagementServiceImpl.updateBranch( 1, 1, null, "Pago Pago", TestConstants.TEST_EMPTY_STRING, new User() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateBranchWithEmptyBranchName() throws InvalidInputException, SolrException
    {
        organizationManagementServiceImpl.updateBranch( 1, 1, TestConstants.TEST_EMPTY_STRING, "Pago Pago",
            TestConstants.TEST_EMPTY_STRING, new User() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateBranchWithNullBranchAddress1() throws InvalidInputException, SolrException
    {
        organizationManagementServiceImpl.updateBranch( 1, 1, TestConstants.TEST_STRING, null, TestConstants.TEST_EMPTY_STRING,
            new User() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateBranchWithEmptyBranchAddress1() throws InvalidInputException, SolrException
    {
        organizationManagementServiceImpl.updateBranch( 1, 1, TestConstants.TEST_STRING, TestConstants.TEST_EMPTY_STRING,
            TestConstants.TEST_EMPTY_STRING, new User() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateBranchWithInvalidBranchId() throws InvalidInputException, SolrException
    {
        organizationManagementServiceImpl.updateBranch( 0, 1, TestConstants.TEST_STRING, TestConstants.TEST_STRING,
            TestConstants.TEST_EMPTY_STRING, new User() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateBranchWhenBranchIsNull() throws InvalidInputException, SolrException
    {
        Mockito.when( branchDao.findById( Mockito.eq( Branch.class ), Mockito.anyLong() ) ).thenReturn( null );
        organizationManagementServiceImpl.updateBranch( 1, 1, TestConstants.TEST_STRING, TestConstants.TEST_STRING,
            TestConstants.TEST_EMPTY_STRING, new User() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateBranchWhenRegionIsNull() throws InvalidInputException, SolrException
    {
        Branch branch = new Branch();
        branch.setRegion( new Region() );
        Mockito.when( branchDao.findById( Mockito.eq( Branch.class ), Mockito.anyLong() ) ).thenReturn( branch );
        organizationManagementServiceImpl.updateBranch( 1, 1, TestConstants.TEST_STRING, TestConstants.TEST_STRING,
            TestConstants.TEST_EMPTY_STRING, new User() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testupdateBranchWithNullUser()
        throws InvalidInputException, SolrException, NoRecordsFetchedException, UserAssignmentException
    {
        organizationManagementServiceImpl.updateBranch( null, 1, 1, TestConstants.TEST_STRING, "Pago Pago",
            TestConstants.TEST_EMPTY_STRING, "United States", "US", "AS", "Pago Pago", "65287", 1,
            TestConstants.TEST_EMPTY_ARRAY, false, true, true );
    }


    @Test ( expected = InvalidInputException.class)
    public void testupdateBranchWithNullBranchName()
        throws InvalidInputException, SolrException, NoRecordsFetchedException, UserAssignmentException
    {
        organizationManagementServiceImpl.updateBranch( new User(), 1, 1, null, "Pago Pago", TestConstants.TEST_EMPTY_STRING,
            "United States", "US", "AS", "Pago Pago", "65287", 1, TestConstants.TEST_EMPTY_ARRAY, false, true, true );
    }


    @Test ( expected = InvalidInputException.class)
    public void testupdateBranchWithEmptyBranchName()
        throws InvalidInputException, SolrException, NoRecordsFetchedException, UserAssignmentException
    {
        organizationManagementServiceImpl.updateBranch( new User(), 1, 1, TestConstants.TEST_EMPTY_STRING, "Pago Pago",
            TestConstants.TEST_EMPTY_STRING, "United States", "US", "AS", "Pago Pago", "65287", 1,
            TestConstants.TEST_EMPTY_ARRAY, false, true, true );
    }


    @Test ( expected = InvalidInputException.class)
    public void testupdateBranchWithNullBranchAddress1()
        throws InvalidInputException, SolrException, NoRecordsFetchedException, UserAssignmentException
    {
        organizationManagementServiceImpl.updateBranch( new User(), 1, 1, TestConstants.TEST_STRING, null,
            TestConstants.TEST_EMPTY_STRING, "United States", "US", "AS", "Pago Pago", "65287", 1,
            TestConstants.TEST_EMPTY_ARRAY, false, true, true );
    }


    @Test ( expected = InvalidInputException.class)
    public void testupdateBranchWithEmptyBranchAddress1()
        throws InvalidInputException, SolrException, NoRecordsFetchedException, UserAssignmentException
    {
        organizationManagementServiceImpl.updateBranch( new User(), 1, 1, TestConstants.TEST_STRING,
            TestConstants.TEST_EMPTY_STRING, TestConstants.TEST_EMPTY_STRING, "United States", "US", "AS", "Pago Pago", "65287",
            1, TestConstants.TEST_EMPTY_ARRAY, false, true, true );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testupdateBranchWhenBranchIsNull()
        throws InvalidInputException, SolrException, NoRecordsFetchedException, UserAssignmentException
    {
        Mockito.when( branchDao.findById( Mockito.eq( Branch.class ), Mockito.anyLong() ) ).thenReturn( null );
        organizationManagementServiceImpl.updateBranch( new User(), 1, 1, TestConstants.TEST_STRING, "Pago Pago",
            TestConstants.TEST_EMPTY_STRING, "United States", "US", "AS", "Pago Pago", "65287", 1,
            TestConstants.TEST_EMPTY_ARRAY, false, true, true );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testupdateBranchWhenRegionIsNull()
        throws InvalidInputException, SolrException, NoRecordsFetchedException, UserAssignmentException
    {
        Branch branch = new Branch();
        branch.setRegion( new Region() );
        Mockito.when( branchDao.findById( Mockito.eq( Branch.class ), Mockito.anyLong() ) ).thenReturn( branch );
        Mockito.doReturn( new Region() ).when( organizationManagementServiceImpl )
            .getDefaultRegionForCompany( Mockito.any( Company.class ) );
        Mockito.when( regionDao.findById( Mockito.eq( Region.class ), Mockito.anyLong() ) ).thenReturn( null );
        organizationManagementServiceImpl.updateBranch( new User(), 1, 1, TestConstants.TEST_STRING, "Pago Pago",
            TestConstants.TEST_EMPTY_STRING, "United States", "US", "AS", "Pago Pago", "65287", 1,
            TestConstants.TEST_EMPTY_ARRAY, false, true, true );
    }


    @SuppressWarnings ( "unchecked")
    @Test ( expected = NoRecordsFetchedException.class)
    public void testupdateBranchWhenAssigneeUserIsNull()
        throws InvalidInputException, SolrException, NoRecordsFetchedException, UserAssignmentException
    {
        Branch branch = new Branch();
        branch.setRegion( new Region() );
        Mockito.when( branchDao.findById( Mockito.eq( Branch.class ), Mockito.anyLong() ) ).thenReturn( branch );
        Mockito.doReturn( new Region() ).when( organizationManagementServiceImpl )
            .getDefaultRegionForCompany( Mockito.any( Company.class ) );
        Mockito.when( regionDao.findById( Mockito.eq( Region.class ), Mockito.anyLong() ) ).thenReturn( new Region() );
        Mockito.doNothing().when( userProfileDao ).updateRegionIdForBranch( Mockito.anyLong(), Mockito.anyLong() );
        Mockito.when( solrSearchService.findUsersInBranch( Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt() ) )
            .thenReturn( null );
        Mockito.doNothing().when(surveyDetailsDao).updateRegionIdForAllSurveysOfBranch( Mockito.anyLong(), Mockito.anyLong() );;
        Mockito.doReturn( null ).when( organizationManagementServiceImpl ).updateRegionIdForUsers(
            (SolrDocumentList) Matchers.any(), Matchers.anyLong(), Matchers.anyLong(), Matchers.anyLong() );
        Mockito.doNothing().when( solrSearchService ).updateRegionsForMultipleUsers( Mockito.anyMap() );
        Mockito.doNothing().when( branchDao ).update( Mockito.any( Branch.class ) );
        Mockito.doNothing().when( organizationUnitSettingsDao ).updateKeyOrganizationUnitSettingsByCriteria(
            Mockito.anyString(), Mockito.any(), Mockito.anyString(), Mockito.any(), Mockito.anyString() );
        Mockito.doNothing().when( solrSearchService ).addOrUpdateBranchToSolr( Mockito.any( Branch.class ) );
        Mockito.when( userDao.findById( Mockito.eq( User.class ), Mockito.anyLong() ) ).thenReturn( null );
        organizationManagementServiceImpl.updateBranch( new User(), 1, 1, TestConstants.TEST_STRING, "Pago Pago",
            TestConstants.TEST_EMPTY_STRING, "United States", "US", "AS", "Pago Pago", "65287", 1,
            TestConstants.TEST_EMPTY_ARRAY, false, true, true );
    }


    /*from last*/
    @Test ( expected = InvalidInputException.class)
    public void testPurgeCompanyWithNullCompany() throws InvalidInputException, SolrException
    {
        organizationManagementServiceImpl.purgeCompany( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetLoopsCountByProfileWithNullProfileId() throws InvalidInputException
    {
        organizationManagementServiceImpl.getLoopsCountByProfile( null, TestConstants.TEST_STRING, 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetLoopsCountByProfileWithEmptyProfileId() throws InvalidInputException
    {
        organizationManagementServiceImpl.getLoopsCountByProfile( TestConstants.TEST_EMPTY_STRING, TestConstants.TEST_STRING,
            1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSaveLoopsForProfileWithNullLoopProfileMapping() throws InvalidInputException
    {
        organizationManagementServiceImpl.saveLoopsForProfile( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetLoopByProfileAndLoopIdWithNullProfileId() throws InvalidInputException
    {
        organizationManagementServiceImpl.getLoopByProfileAndLoopId( null, TestConstants.TEST_STRING, TestConstants.TEST_STRING,
            1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetLoopByProfileAndLoopIdWithEmptyProfileId() throws InvalidInputException
    {
        organizationManagementServiceImpl.getLoopByProfileAndLoopId( TestConstants.TEST_EMPTY_STRING, TestConstants.TEST_STRING,
            TestConstants.TEST_STRING, 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetLoopByProfileAndLoopIdWithNullLoopId() throws InvalidInputException
    {
        organizationManagementServiceImpl.getLoopByProfileAndLoopId( TestConstants.TEST_STRING, null, TestConstants.TEST_STRING,
            1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetLoopByProfileAndLoopIdWithEmptyLoopId() throws InvalidInputException
    {
        organizationManagementServiceImpl.getLoopByProfileAndLoopId( TestConstants.TEST_STRING, TestConstants.TEST_EMPTY_STRING,
            TestConstants.TEST_STRING, 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetCollectionDotloopMappingByCollectionIdAndProfileIdWithInvalidOrganizationUnitId()
        throws InvalidInputException
    {
        organizationManagementServiceImpl.getCollectionDotloopMappingByCollectionIdAndProfileId( TestConstants.TEST_STRING, 0,
            TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetCollectionDotloopMappingByCollectionIdAndProfileIdWithNullProfileId() throws InvalidInputException
    {
        organizationManagementServiceImpl.getCollectionDotloopMappingByCollectionIdAndProfileId( TestConstants.TEST_STRING, 1,
            null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetCollectionDotloopMappingByCollectionIdAndProfileIdWithEmptyProfileId() throws InvalidInputException
    {
        organizationManagementServiceImpl.getCollectionDotloopMappingByCollectionIdAndProfileId( TestConstants.TEST_STRING, 1,
            TestConstants.TEST_EMPTY_STRING );
    }


    @Test
    public void testGetCollectionDotloopMappingByCollectionIdAndProfileIdWhenCollectionDotloopProfileMappingListIsNull()
        throws InvalidInputException
    {
        Mockito.when( collectionDotloopProfileMappingDao.findByKeyValue( Mockito.eq( CollectionDotloopProfileMapping.class ),
            Mockito.anyMapOf( String.class, Object.class ) ) ).thenReturn( null );
        assertNull( "CollectionDotloopProfileMappingList is not as expected", organizationManagementServiceImpl
            .getCollectionDotloopMappingByCollectionIdAndProfileId( TestConstants.TEST_STRING, 1, TestConstants.TEST_STRING ) );
    }


    @Test
    public void testGetCollectionDotloopMappingByCollectionIdAndProfileIdWhenCollectionDotloopProfileMappingListIsEmpty()
        throws InvalidInputException
    {
        Mockito
            .when( collectionDotloopProfileMappingDao.findByKeyValue( Mockito.eq( CollectionDotloopProfileMapping.class ),
                Mockito.anyMapOf( String.class, Object.class ) ) )
            .thenReturn( new ArrayList<CollectionDotloopProfileMapping>() );
        assertNull( "CollectionDotloopProfileMappingList is not as expected", organizationManagementServiceImpl
            .getCollectionDotloopMappingByCollectionIdAndProfileId( TestConstants.TEST_STRING, 1, TestConstants.TEST_STRING ) );
    }


    @Test ( expected = InvalidInputException.class)
    public void testsaveCollectionDotLoopProfileMappingWithNullCollectionDotloopProfileMapping() throws InvalidInputException
    {
        organizationManagementServiceImpl.saveCollectionDotLoopProfileMapping( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateCollectionDotLoopProfileMappingWithNullCollectionDotloopProfileMapping() throws InvalidInputException
    {
        organizationManagementServiceImpl.updateCollectionDotLoopProfileMapping( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetCollectionDotloopMappingByProfileIdWithNullProfileId() throws InvalidInputException
    {
        organizationManagementServiceImpl.getCollectionDotloopMappingByProfileId( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetCollectionDotloopMappingByProfileIdWithEmptyProfileId() throws InvalidInputException
    {
        organizationManagementServiceImpl.getCollectionDotloopMappingByProfileId( TestConstants.TEST_EMPTY_STRING );
    }


    @Test
    public void testGetCollectionDotloopMappingByProfileIdWhenCollectionDotloopProfileMappingListIsEmpty()
        throws InvalidInputException
    {
        Mockito.when( collectionDotloopProfileMappingDao.findByColumn( Mockito.eq( CollectionDotloopProfileMapping.class ),
            Mockito.anyString(), Mockito.anyString() ) ).thenReturn( new ArrayList<CollectionDotloopProfileMapping>() );
        assertNull( "CollectionDotloopProfileMappingList is not as expected",
            organizationManagementServiceImpl.getCollectionDotloopMappingByProfileId( TestConstants.TEST_STRING ) );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetRegionsForRegionIdsWithNullRegionIdsSet() throws InvalidInputException
    {
        organizationManagementServiceImpl.getRegionsForRegionIds( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetRegionsForRegionIdsWithEmptyRegionIdsSet() throws InvalidInputException
    {
        organizationManagementServiceImpl.getRegionsForRegionIds( new HashSet<Long>() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetBranchesForBranchIdsWithNullBranchIdsSet() throws InvalidInputException
    {
        organizationManagementServiceImpl.getBranchesForBranchIds( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetBranchesForBranchIdsWithEmptyBranchIdsSet() throws InvalidInputException
    {
        organizationManagementServiceImpl.getBranchesForBranchIds( new HashSet<Long>() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateProfileUrlForDeletedEntityWithNullEntityType() throws InvalidInputException
    {
        organizationManagementServiceImpl.updateProfileUrlAndStatusForDeletedEntity( null, 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateProfileUrlForDeletedEntityWithEmptyEntityType() throws InvalidInputException
    {
        organizationManagementServiceImpl.updateProfileUrlAndStatusForDeletedEntity( TestConstants.TEST_EMPTY_STRING, 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateProfileUrlForDeletedEntityWithInvalidEntityType() throws InvalidInputException
    {
        organizationManagementServiceImpl.updateProfileUrlAndStatusForDeletedEntity( TestConstants.TEST_STRING, 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateProfileUrlForDeletedEntityWithInvalidEntityId() throws InvalidInputException
    {
        organizationManagementServiceImpl.updateProfileUrlAndStatusForDeletedEntity( TestConstants.TEST_STRING, 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateProfileUrlForDeletedEntityWhenUnitSettingsIsNull() throws InvalidInputException
    {
        Mockito.when( userManagementService.getUserSettings( Mockito.anyLong() ) ).thenReturn( null );
        organizationManagementServiceImpl.updateProfileUrlAndStatusForDeletedEntity( CommonConstants.AGENT_ID_COLUMN, 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateProfileUrlForDeletedEntityWhenContactDetailsIsNull() throws InvalidInputException
    {
        Mockito.when( userManagementService.getUserSettings( Mockito.anyLong() ) ).thenReturn( new AgentSettings() );
        organizationManagementServiceImpl.updateProfileUrlAndStatusForDeletedEntity( CommonConstants.AGENT_ID_COLUMN, 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateProfileUrlForDeletedEntityWhenContactDetailsHasNullAsName() throws InvalidInputException
    {
        AgentSettings agentSettings = new AgentSettings();
        agentSettings.setContact_details( new ContactDetailsSettings() );
        Mockito.when( userManagementService.getUserSettings( Mockito.anyLong() ) ).thenReturn( agentSettings );
        organizationManagementServiceImpl.updateProfileUrlAndStatusForDeletedEntity( CommonConstants.AGENT_ID_COLUMN, 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateProfileUrlForDeletedEntityWhenContactDetailsHasEmptyName() throws InvalidInputException
    {
        ContactDetailsSettings contactDetailsSettings = new ContactDetailsSettings();
        contactDetailsSettings.setName( TestConstants.TEST_EMPTY_STRING );
        AgentSettings agentSettings = new AgentSettings();
        agentSettings.setContact_details( contactDetailsSettings );
        Mockito.when( userManagementService.getUserSettings( Mockito.anyLong() ) ).thenReturn( agentSettings );
        organizationManagementServiceImpl.updateProfileUrlAndStatusForDeletedEntity( CommonConstants.AGENT_ID_COLUMN, 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateProfileUrlForDeletedEntityWhenProfileUrlIsNull() throws InvalidInputException
    {
        ContactDetailsSettings contactDetailsSettings = new ContactDetailsSettings();
        contactDetailsSettings.setName( TestConstants.TEST_STRING );
        AgentSettings agentSettings = new AgentSettings();
        agentSettings.setContact_details( contactDetailsSettings );
        Mockito.when( userManagementService.getUserSettings( Mockito.anyLong() ) ).thenReturn( agentSettings );
        organizationManagementServiceImpl.updateProfileUrlAndStatusForDeletedEntity( CommonConstants.AGENT_ID_COLUMN, 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateProfileUrlForDeletedEntityWhenProfileUrlIsEmpty() throws InvalidInputException
    {
        ContactDetailsSettings contactDetailsSettings = new ContactDetailsSettings();
        contactDetailsSettings.setName( TestConstants.TEST_STRING );
        AgentSettings agentSettings = new AgentSettings();
        agentSettings.setContact_details( contactDetailsSettings );
        agentSettings.setProfileUrl( TestConstants.TEST_EMPTY_STRING );
        Mockito.when( userManagementService.getUserSettings( Mockito.anyLong() ) ).thenReturn( agentSettings );
        organizationManagementServiceImpl.updateProfileUrlAndStatusForDeletedEntity( CommonConstants.AGENT_ID_COLUMN, 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testgetAllRegionsUnderCompanyConnectedToZillowWithInvalidCompanyId() throws InvalidInputException
    {
        organizationManagementServiceImpl.getAllRegionsUnderCompanyConnectedToZillow( 0, 1, 10 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAllBranchesUnderProfileTypeConnectedToZillowWithNullProfileLevel() throws InvalidInputException
    {
        organizationManagementServiceImpl.getAllBranchesUnderProfileTypeConnectedToZillow( null, 1, 1, 10 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAllBranchesUnderProfileTypeConnectedToZillowWithEmptyProfileLevel() throws InvalidInputException
    {
        organizationManagementServiceImpl.getAllBranchesUnderProfileTypeConnectedToZillow( TestConstants.TEST_EMPTY_STRING, 1,
            1, 10 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAllBranchesUnderProfileTypeConnectedToZillowWithInvalidProfileLevel() throws InvalidInputException
    {
        organizationManagementServiceImpl.getAllBranchesUnderProfileTypeConnectedToZillow( TestConstants.TEST_STRING, 1, 1,
            10 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAllBranchesUnderProfileTypeConnectedToZillowWithInvalidIden() throws InvalidInputException
    {
        organizationManagementServiceImpl.getAllBranchesUnderProfileTypeConnectedToZillow( TestConstants.TEST_STRING, 0, 1,
            10 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAllUsersUnderProfileTypeConnectedToZillowWithNullProfileLevel() throws InvalidInputException
    {
        organizationManagementServiceImpl.getAllUsersUnderProfileTypeConnectedToZillow( null, 1, 1, 10 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAllUsersUnderProfileTypeConnectedToZillowWithEmptyProfileLevel() throws InvalidInputException
    {
        organizationManagementServiceImpl.getAllUsersUnderProfileTypeConnectedToZillow( TestConstants.TEST_EMPTY_STRING, 1, 1,
            10 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAllUsersUnderProfileTypeConnectedToZillowWithInvalidProfileLevel() throws InvalidInputException
    {
        organizationManagementServiceImpl.getAllUsersUnderProfileTypeConnectedToZillow( TestConstants.TEST_STRING, 1, 1, 10 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAllUsersUnderProfileTypeConnectedToZillowWithInvalidIden() throws InvalidInputException
    {
        organizationManagementServiceImpl.getAllUsersUnderProfileTypeConnectedToZillow( TestConstants.TEST_STRING, 0, 1, 10 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGenerateProfileNameForCompanyWithNullCompanyName() throws InvalidInputException
    {
        organizationManagementServiceImpl.generateProfileNameForCompany( null, 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGenerateProfileNameForCompanyWithEmptyCompanyName() throws InvalidInputException
    {
        organizationManagementServiceImpl.generateProfileNameForCompany( TestConstants.TEST_EMPTY_STRING, 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateProfileUrlForDeletedEntityTypeNull() throws InvalidInputException
    {
        organizationManagementServiceImpl.updateProfileUrlAndStatusForDeletedEntity( null, 1l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateProfileUrlForDeletedEntityTypeEmpty() throws InvalidInputException
    {
        organizationManagementServiceImpl.updateProfileUrlAndStatusForDeletedEntity( "", 1l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateProfileUrlForDeletedEntityIdInvalid() throws InvalidInputException
    {
        organizationManagementServiceImpl.updateProfileUrlAndStatusForDeletedEntity( "agentId", 0l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateProfileUrlForDeletedEntityUserSettingNull() throws InvalidInputException
    {
        Mockito.when( userManagementService.getUserSettings( Mockito.anyLong() ) ).thenReturn( null );
        organizationManagementServiceImpl.updateProfileUrlAndStatusForDeletedEntity( "agentId", 1l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateProfileUrlForDeletedEntityBranchSettingNull() throws InvalidInputException, NoRecordsFetchedException
    {
        Mockito.doReturn( null ).when( organizationManagementServiceImpl ).getBranchSettingsDefault( Matchers.anyLong() );
        organizationManagementServiceImpl.updateProfileUrlAndStatusForDeletedEntity( "branchId", 1l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateProfileUrlForDeletedEntityRegionSettingNull() throws InvalidInputException
    {
        Mockito.doReturn( null ).when( organizationManagementServiceImpl ).getRegionSettings( Matchers.anyLong() );
        organizationManagementServiceImpl.updateProfileUrlAndStatusForDeletedEntity( "regionId", 1l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateProfileUrlForDeletedEntityInvalidEntityType() throws InvalidInputException
    {
        Mockito.doReturn( null ).when( organizationManagementServiceImpl ).getRegionSettings( Matchers.anyLong() );
        organizationManagementServiceImpl.updateProfileUrlAndStatusForDeletedEntity( "test", 1l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateProfileUrlForDeletedEntityContactDetailsNull() throws InvalidInputException
    {
        AgentSettings agent = new AgentSettings();
        Mockito.when( userManagementService.getUserSettings( Mockito.anyLong() ) ).thenReturn( agent );
        organizationManagementServiceImpl.updateProfileUrlAndStatusForDeletedEntity( "agentId", 1l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateProfileUrlForDeletedEntityNameNull() throws InvalidInputException
    {
        AgentSettings agent = new AgentSettings();
        agent.setContact_details( new ContactDetailsSettings() );
        Mockito.when( userManagementService.getUserSettings( Mockito.anyLong() ) ).thenReturn( agent );
        organizationManagementServiceImpl.updateProfileUrlAndStatusForDeletedEntity( "agentId", 1l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateProfileUrlForDeletedEntityNameEmpty() throws InvalidInputException
    {
        AgentSettings agent = new AgentSettings();
        ContactDetailsSettings contactDetails = new ContactDetailsSettings();
        contactDetails.setName( "" );
        agent.setContact_details( contactDetails );
        Mockito.when( userManagementService.getUserSettings( Mockito.anyLong() ) ).thenReturn( agent );
        organizationManagementServiceImpl.updateProfileUrlAndStatusForDeletedEntity( "agentId", 1l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateProfileUrlForDeletedEntityExistingProfileUrlNull() throws InvalidInputException
    {
        AgentSettings agent = new AgentSettings();
        ContactDetailsSettings contactDetails = new ContactDetailsSettings();
        contactDetails.setName( "test" );
        agent.setContact_details( contactDetails );
        Mockito.when( userManagementService.getUserSettings( Mockito.anyLong() ) ).thenReturn( agent );
        organizationManagementServiceImpl.updateProfileUrlAndStatusForDeletedEntity( "agentId", 1l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateProfileUrlForDeletedEntityExistingProfileUrlEmpty() throws InvalidInputException
    {
        AgentSettings agent = new AgentSettings();
        ContactDetailsSettings contactDetails = new ContactDetailsSettings();
        contactDetails.setName( "test" );
        agent.setProfileUrl( "" );
        agent.setContact_details( contactDetails );
        Mockito.when( userManagementService.getUserSettings( Mockito.anyLong() ) ).thenReturn( agent );
        organizationManagementServiceImpl.updateProfileUrlAndStatusForDeletedEntity( "agentId", 1l );
    }


    //Tests for UpdateRegionIdForUsers

    @Test ( expected = InvalidInputException.class)
    public void testUpdateRegionIdForUsersForUserListEmpty() throws InvalidInputException
    {
        organizationManagementServiceImpl.updateRegionIdForUsers( null, 1l, 1l, 1l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateRegionIdForUsersFornewRegionIdInvalid() throws InvalidInputException
    {
        organizationManagementServiceImpl.updateRegionIdForUsers( new SolrDocumentList(), 0l, 1l, 1l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateRegionIdForUsersForoldRegionIdInvalid() throws InvalidInputException
    {
        organizationManagementServiceImpl.updateRegionIdForUsers( new SolrDocumentList(), 1l, 0l, 1l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateRegionIdForUsersForcurBranchIdInvalid() throws InvalidInputException
    {
        organizationManagementServiceImpl.updateRegionIdForUsers( new SolrDocumentList(), 1l, 1l, 0l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateRegionIdForUsersForBranchInvalid() throws InvalidInputException
    {
        SolrDocumentList userList = new SolrDocumentList();
        SolrDocument user = new SolrDocument();
        List<Long> branches = new ArrayList<Long>();
        branches.add( 1l );
        user.put( CommonConstants.BRANCHES_SOLR, branches );
        userList.add( user );
        Mockito.when( userManagementService.getBranchById( Mockito.anyLong() ) ).thenReturn( null );
        organizationManagementServiceImpl.updateRegionIdForUsers( userList, 1l, 1l, 1l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateRegionIdForUsersForRegionInvalid() throws InvalidInputException
    {
        SolrDocumentList userList = new SolrDocumentList();
        SolrDocument user = new SolrDocument();
        List<Long> branches = new ArrayList<Long>();
        branches.add( 1l );
        user.put( CommonConstants.BRANCHES_SOLR, branches );
        userList.add( user );
        Mockito.when( userManagementService.getBranchById( Mockito.anyLong() ) ).thenReturn( new Branch() );
        organizationManagementServiceImpl.updateRegionIdForUsers( userList, 1l, 1l, 1l );
    }


    @Test
    public void testValidateUserAssignmentWithNullAdminUser()
    {
        assertFalse( "User Assignment Validation result is not as expected",
            organizationManagementServiceImpl.validateUserAssignment( null, null, new HashMap<String, List<User>>() ) );
    }


    @Test
    public void testValidateUserAssignmentWithNullAssigneeUser()
    {
        assertFalse( "User Assignment Validation result is not as expected",
            organizationManagementServiceImpl.validateUserAssignment( new User(), null, new HashMap<String, List<User>>() ) );
    }


    @Test
    public void testValidateUserAssignmentWhenAdminUserAndAssigneeUserBelongToSameCompany()
    {
        User adminUser = new User();
        User assigneeUser = new User();
        Company company = new Company();
        adminUser.setCompany( company );
        assigneeUser.setCompany( company );
        Map<String, List<User>> userMap = new HashMap<String, List<User>>();
        assertTrue( "User assignment validation result is not as expected",
            organizationManagementServiceImpl.validateUserAssignment( adminUser, assigneeUser, userMap ) );
        assertTrue( "Invalid user assignment found", userMap.size() == 0 );
    }


    @Test
    public void testValidateUserAssignmentWhenAdminUserAndAssigneeUserBelongToDifferentCompany()
    {
        User adminUser = new User();
        User assigneeUser = new User();
        Company company1 = new Company();
        Company company2 = new Company();
        company1.setCompanyId( 1 );
        company2.setCompanyId( 2 );
        adminUser.setCompany( company1 );
        assigneeUser.setCompany( company2 );
        assigneeUser.setEmailId( TestConstants.TEST_MAIL_ID_STRING );
        Map<String, List<User>> userMap = new HashMap<String, List<User>>();
        assertFalse( "User assignment validation result is not as expected",
            organizationManagementServiceImpl.validateUserAssignment( adminUser, assigneeUser, userMap ) );
        assertTrue( "Invalid user assignment not found",
            userMap.size() != 0 && !userMap.get( CommonConstants.INVALID_USERS_ASSIGN_LIST ).isEmpty() );
        assertEquals( "Invalid user assignment email id is not same as assignee user email id passed as argument",
            userMap.get( CommonConstants.INVALID_USERS_ASSIGN_LIST ).get( 0 ).getEmailId(), assigneeUser.getEmailId() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetRegionIdsUnderCompanyWithInvalidCompanyId() throws InvalidInputException
    {
        organizationManagementServiceImpl.getRegionIdsUnderCompany( 0, 0, 50 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetBranchIdsUnderCompanyWithInvalidCompanyId() throws InvalidInputException
    {
        organizationManagementServiceImpl.getBranchIdsUnderCompany( 0, 0, 50 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAgentIdsUnderCompanyWithInvalidCompanyId() throws InvalidInputException
    {
        organizationManagementServiceImpl.getAgentIdsUnderCompany( 0, 0, 50 );
    }
}
