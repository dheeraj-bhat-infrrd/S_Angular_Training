package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

import static org.junit.Assert.assertEquals;
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
import com.realtech.socialsurvey.TestConstants;
import com.realtech.socialsurvey.core.dao.BranchDao;
import com.realtech.socialsurvey.core.dao.CompanyDao;
import com.realtech.socialsurvey.core.dao.DisabledAccountDao;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.CollectionDotloopProfileMapping;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.DisabledAccount;
import com.realtech.socialsurvey.core.entities.EncompassCrmInfo;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.entities.LoopProfileMapping;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.RetriedTransaction;
import com.realtech.socialsurvey.core.entities.SurveySettings;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.payment.exception.PaymentException;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.utils.EmailFormatHelper;

public class OrganizationManagementServiceImplTest
{

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
}
