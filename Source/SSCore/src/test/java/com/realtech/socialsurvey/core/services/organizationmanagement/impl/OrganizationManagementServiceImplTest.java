package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

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
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.entities.AccountsMaster;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.CollectionDotloopProfileMapping;
import com.realtech.socialsurvey.core.entities.Company;
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
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserAssignmentException;
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


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {}


    @AfterClass
    public static void tearDownAfterClass() throws Exception {}


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks( this );
    }


    @After
    public void tearDown() throws Exception {}


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
        Mockito
            .when(
                licenceDetailDao.findByColumn( Mockito.eq( LicenseDetail.class ), Mockito.anyString(),
                    Mockito.any( Company.class ) ) ).thenReturn( null );
        assertEquals( "Return value does not match expected", 0,
            organizationManagementServiceImpl.fetchAccountTypeMasterIdForCompany( new Company() ) );
    }


    @Test
    public void testFetchAccountTypeMasterIdForCompanyWhenLicenseDetailListIsEmpty() throws InvalidInputException,
        SolrException
    {
        Mockito
            .when(
                licenceDetailDao.findByColumn( Mockito.eq( LicenseDetail.class ), Mockito.anyString(),
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
    public void testGetBranchSettingsForUserProfilesWithNullUserProfileList() throws InvalidInputException,
        NoRecordsFetchedException
    {
        organizationManagementServiceImpl.getBranchSettingsForUserProfiles( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetBranchSettingsForUserProfilesWithEmptyUserProfileList() throws InvalidInputException,
        NoRecordsFetchedException
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
        organizationManagementServiceImpl.deleteMailBodyFromSetting( new OrganizationUnitSettings(), TestConstants.TEST_STRING );
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
    public void testAddDisabledAccountWithInvalidCompanyId() throws InvalidInputException, NoRecordsFetchedException,
        PaymentException
    {
        organizationManagementServiceImpl.addDisabledAccount( 0, false );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testAddDisabledAccountWhenLicenseDetailListIsNull() throws InvalidInputException, NoRecordsFetchedException,
        PaymentException
    {
        Mockito.when( companyDao.findById( Mockito.eq( Company.class ), Mockito.anyLong() ) ).thenReturn( new Company() );
        Mockito
            .when(
                licenceDetailDao.findByKeyValue( Mockito.eq( LicenseDetail.class ),
                    Mockito.anyMapOf( String.class, Object.class ) ) ).thenReturn( null );
        organizationManagementServiceImpl.addDisabledAccount( 1, false );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testAddDisabledAccountWhenLicenseDetailListIsEmpty() throws InvalidInputException, NoRecordsFetchedException,
        PaymentException
    {
        Mockito.when( companyDao.findById( Mockito.eq( Company.class ), Mockito.anyLong() ) ).thenReturn( new Company() );
        Mockito
            .when(
                licenceDetailDao.findByKeyValue( Mockito.eq( LicenseDetail.class ),
                    Mockito.anyMapOf( String.class, Object.class ) ) ).thenReturn( new ArrayList<LicenseDetail>() );
        organizationManagementServiceImpl.addDisabledAccount( 1, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDeleteDisabledAccountWithInvalidCompanyId() throws InvalidInputException, NoRecordsFetchedException,
        PaymentException
    {
        organizationManagementServiceImpl.deleteDisabledAccount( 0 );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testDeleteDisabledAccountWhenDisabledAccountsListIsNull() throws InvalidInputException,
        NoRecordsFetchedException, PaymentException
    {
        Mockito.when( companyDao.findById( Mockito.eq( Company.class ), Mockito.anyLong() ) ).thenReturn( new Company() );
        Mockito.when(
            disabledAccountDao.findByKeyValue( Mockito.eq( DisabledAccount.class ),
                Mockito.anyMapOf( String.class, Object.class ) ) ).thenReturn( null );
        organizationManagementServiceImpl.deleteDisabledAccount( 1 );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testDeleteDisabledAccountWhenDisabledAccountsListIsEmpty() throws InvalidInputException,
        NoRecordsFetchedException, PaymentException
    {
        Mockito.when( companyDao.findById( Mockito.eq( Company.class ), Mockito.anyLong() ) ).thenReturn( new Company() );
        Mockito.when(
            disabledAccountDao.findByKeyValue( Mockito.eq( DisabledAccount.class ),
                Mockito.anyMapOf( String.class, Object.class ) ) ).thenReturn( new ArrayList<DisabledAccount>() );
        organizationManagementServiceImpl.deleteDisabledAccount( 1 );
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
    public void testUpgradeToCompanyWithWhenDefaultBranchIsNull() throws InvalidInputException, SolrException,
        NoRecordsFetchedException
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
    public void testUpgradeToEnterpriseWithInvalidAccountMasterId() throws InvalidInputException, SolrException,
        NoRecordsFetchedException
    {
        organizationManagementServiceImpl.upgradeToEnterprise( new Company(), 0 );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testUpgradeToEnterpriseWithWhenDefaultBranchIsNotNullAndDefaultRegionIsNull() throws InvalidInputException,
        SolrException, NoRecordsFetchedException
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
    public void testUpgradeToEnterpriseWhenDefaultBranchIsNullAndAccountMasterIdIsNot3() throws InvalidInputException,
        SolrException, NoRecordsFetchedException
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
    public void testUpgradeAccountWithInvalidAccountMasterId() throws NoRecordsFetchedException, InvalidInputException,
        SolrException
    {
        organizationManagementServiceImpl.upgradeAccount( new Company(), 0 );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testUpgradeAccountWhenLicenseDetailListIsNull() throws NoRecordsFetchedException, InvalidInputException,
        SolrException
    {
        Mockito
            .when(
                licenceDetailDao.findByKeyValue( Mockito.eq( LicenseDetail.class ),
                    Mockito.anyMapOf( String.class, Object.class ) ) ).thenReturn( null );
        organizationManagementServiceImpl.upgradeAccount( new Company(), 1 );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testUpgradeAccountWhenLicenseDetailListIsEmpty() throws NoRecordsFetchedException, InvalidInputException,
        SolrException
    {
        Mockito
            .when(
                licenceDetailDao.findByKeyValue( Mockito.eq( LicenseDetail.class ),
                    Mockito.anyMapOf( String.class, Object.class ) ) ).thenReturn( new ArrayList<LicenseDetail>() );
        organizationManagementServiceImpl.upgradeAccount( new Company(), 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpgradeAccountWithInvalidNewMasterAccountId() throws NoRecordsFetchedException, InvalidInputException,
        SolrException
    {
        LicenseDetail licenseDetail = new LicenseDetail();
        licenseDetail.setAccountsMaster( new AccountsMaster() );
        List<LicenseDetail> licenseDetailList = new ArrayList<LicenseDetail>();
        licenseDetailList.add( licenseDetail );
        Mockito
            .when(
                licenceDetailDao.findByKeyValue( Mockito.eq( LicenseDetail.class ),
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
        Mockito
            .when(
                licenceDetailDao.findByKeyValue( Mockito.eq( LicenseDetail.class ),
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
        Mockito
            .when(
                licenceDetailDao.findByKeyValue( Mockito.eq( LicenseDetail.class ),
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
        Mockito
            .when(
                licenceDetailDao.findByKeyValue( Mockito.eq( LicenseDetail.class ),
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
        Mockito
            .when(
                licenceDetailDao.findByKeyValue( Mockito.eq( LicenseDetail.class ),
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
    public void testGetAllVerticalsMasterWhenVerticalsMastersListIsEmpty() throws InvalidInputException
    {
        organizationManagementServiceImpl.getAllVerticalsMaster();
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetBranchesUnderCompanyWithNullCompanyProfileName() throws InvalidInputException,
        NoRecordsFetchedException, ProfileNotFoundException
    {
        organizationManagementServiceImpl.getBranchesUnderCompany( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetBranchesUnderCompanyWithEmptyCompanyProfileName() throws InvalidInputException,
        NoRecordsFetchedException, ProfileNotFoundException
    {
        organizationManagementServiceImpl.getBranchesUnderCompany( TestConstants.TEST_EMPTY_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetBranchesUnderCompanyWithInvalidCompanyId() throws InvalidInputException, NoRecordsFetchedException,
        ProfileNotFoundException
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
    public void testGetBranchesForRegionWithNullCompanyProfileName() throws InvalidInputException, NoRecordsFetchedException,
        ProfileNotFoundException
    {
        organizationManagementServiceImpl.getBranchesForRegion( null, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetBranchesForRegionWithNEmptyCompanyProfileName() throws InvalidInputException, NoRecordsFetchedException,
        ProfileNotFoundException
    {
        organizationManagementServiceImpl.getBranchesForRegion( TestConstants.TEST_EMPTY_STRING, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetBranchesForRegionWithNullRegionProfileName() throws InvalidInputException, NoRecordsFetchedException,
        ProfileNotFoundException
    {
        organizationManagementServiceImpl.getBranchesForRegion( TestConstants.TEST_STRING, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetBranchesForRegionWithEmptyRegionProfileName() throws InvalidInputException, NoRecordsFetchedException,
        ProfileNotFoundException
    {
        organizationManagementServiceImpl.getBranchesForRegion( TestConstants.TEST_STRING, TestConstants.TEST_EMPTY_STRING );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testGetBranchesForRegionWithNullRegionSettings() throws InvalidInputException, NoRecordsFetchedException,
        ProfileNotFoundException
    {
        Mockito.when( profileManagementService.getRegionByProfileName( Mockito.anyString(), Mockito.anyString() ) ).thenReturn(
            null );
        organizationManagementServiceImpl.getBranchesForRegion( TestConstants.TEST_STRING, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetBranchesByRegionIdWithInvalidRegionId() throws InvalidInputException
    {
        organizationManagementServiceImpl.getBranchesByRegionId( 0 );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testAddNewRegionWithUserWhenAssignedUserIsNull() throws InvalidInputException, SolrException,
        NoRecordsFetchedException, UserAssignmentException
    {
        User user = new User();
        Mockito
            .doReturn( new Region() )
            .when( organizationManagementServiceImpl )
            .addNewRegion( Mockito.any( User.class ), Mockito.anyString(), Mockito.anyInt(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString() );
        Mockito
            .doReturn( new Branch() )
            .when( organizationManagementServiceImpl )
            .addNewBranch( Mockito.any( User.class ), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString() );

        Mockito.when( userDao.findById( Mockito.eq( User.class ), Mockito.anyLong() ) ).thenReturn( null );
        organizationManagementServiceImpl.addNewRegionWithUser( user, TestConstants.TEST_STRING, 0, "Pago Pago", "",
            "United States", "US", "AS", "Pago Pago", "65827", 1, new String[] { TestConstants.TEST_MAIL_ID_STRING }, false,
            true );
    }
}
