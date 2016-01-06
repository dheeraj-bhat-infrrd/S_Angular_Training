package com.realtech.socialsurvey.core.starter;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.CompanyDao;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.BillingReportData;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;


public class PrepareBillingReportTest
{
    @Spy
    @InjectMocks
    private PrepareBillingReport prepareBillingReport;

    @Mock
    private CompanyDao companyDao;

    @Mock
    private OrganizationManagementService organizationManagementService;


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


    @Test
    public void testIsUserAnAgentForAgent()
    {
        List<Long> profileMasters = new ArrayList<Long>();
        profileMasters.add( (long) CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID );
        assertTrue( prepareBillingReport.isUserAnAgent( profileMasters ) );
    }


    @Test
    public void testIsUserAnAgentForNotAgent()
    {
        List<Long> profileMasters = new ArrayList<Long>();
        assertFalse( prepareBillingReport.isUserAnAgent( profileMasters ) );
    }


    @Test
    public void testGetAddressFromAgent() throws InvalidInputException, NoRecordsFetchedException
    {
        AgentSettings agentSettings = new AgentSettings();
        ContactDetailsSettings contactDetails = new ContactDetailsSettings();
        contactDetails.setAddress( "test" );
        agentSettings.setContact_details( contactDetails );
        assertEquals( "test", prepareBillingReport.getAddress( agentSettings, new BillingReportData(), "CA", "RA", "BA" ) );
    }


    @Test
    public void testGetAddressFromBranchForNonDefaultBranchAndBranchIdDifferent() throws InvalidInputException,
        NoRecordsFetchedException
    {
        OrganizationUnitSettings branchSettings = new OrganizationUnitSettings();
        ContactDetailsSettings contactDetails = new ContactDetailsSettings();
        contactDetails.setAddress( "test" );
        branchSettings.setContact_details( contactDetails );
        BillingReportData reportRow = new BillingReportData();
        reportRow.setBranch( "TestBranch" );
        reportRow.setBranchId( 1l );
        Mockito.when( organizationManagementService.getBranchSettingsDefault( Mockito.anyLong() ) ).thenReturn( branchSettings );
        assertEquals( "test", prepareBillingReport.getAddress( new AgentSettings(), reportRow, "CA", "RA", "BA" ) );
    }


    @Test
    public void testGetAddressFromBranchForNonDefaultBranchAndpreviousBranchAddressNull() throws InvalidInputException,
        NoRecordsFetchedException
    {
        OrganizationUnitSettings branchSettings = new OrganizationUnitSettings();
        ContactDetailsSettings contactDetails = new ContactDetailsSettings();
        contactDetails.setAddress( "test" );
        branchSettings.setContact_details( contactDetails );
        BillingReportData reportRow = new BillingReportData();
        reportRow.setBranch( "TestBranch" );
        reportRow.setBranchId( 1l );
        Mockito.when( organizationManagementService.getBranchSettingsDefault( Mockito.anyLong() ) ).thenReturn( branchSettings );
        assertEquals( "test", prepareBillingReport.getAddress( new AgentSettings(), reportRow, "CA", "RA", null ) );
    }


    @Test
    public void testGetAddressFromBranchForNonDefaultBranch() throws InvalidInputException, NoRecordsFetchedException
    {
        OrganizationUnitSettings branchSettings = new OrganizationUnitSettings();
        ContactDetailsSettings contactDetails = new ContactDetailsSettings();
        contactDetails.setAddress( "test" );
        branchSettings.setContact_details( contactDetails );
        BillingReportData reportRow = new BillingReportData();
        reportRow.setBranch( "TestBranch" );
        reportRow.setBranchId( 0l );
        Mockito.when( organizationManagementService.getBranchSettingsDefault( Mockito.anyLong() ) ).thenReturn( branchSettings );
        assertEquals( "BA", prepareBillingReport.getAddress( new AgentSettings(), reportRow, "CA", "RA", "BA" ) );
    }


    @Test
    public void testGetAddressFromRegionForNonDefaultBranchAndRegionAndRegionIdDifferent() throws InvalidInputException,
        NoRecordsFetchedException
    {
        OrganizationUnitSettings regionSettings = new OrganizationUnitSettings();
        ContactDetailsSettings contactDetails = new ContactDetailsSettings();
        contactDetails.setAddress( "test" );
        regionSettings.setContact_details( contactDetails );
        BillingReportData reportRow = new BillingReportData();
        reportRow.setBranch( "TestBranch" );
        reportRow.setBranchId( 0l );
        reportRow.setRegion( "TestRegion" );
        reportRow.setRegionId( 1l );
        Mockito.when( organizationManagementService.getBranchSettingsDefault( Mockito.anyLong() ) ).thenReturn(
            new OrganizationUnitSettings() );
        Mockito.when( organizationManagementService.getRegionSettings( Mockito.anyLong() ) ).thenReturn( regionSettings );
        assertEquals( "test", prepareBillingReport.getAddress( new AgentSettings(), reportRow, "CA", "RA", null ) );
    }


    @Test
    public void testGetAddressFromRegionForDefaultBranchAndRegionAndRegionIdDifferent() throws InvalidInputException,
        NoRecordsFetchedException
    {
        OrganizationUnitSettings regionSettings = new OrganizationUnitSettings();
        ContactDetailsSettings contactDetails = new ContactDetailsSettings();
        contactDetails.setAddress( "test" );
        regionSettings.setContact_details( contactDetails );
        BillingReportData reportRow = new BillingReportData();
        reportRow.setBranch( CommonConstants.DEFAULT_BRANCH_NAME );
        reportRow.setBranchId( 0l );
        reportRow.setRegion( "TestRegion" );
        reportRow.setRegionId( 1l );
        Mockito.when( organizationManagementService.getBranchSettingsDefault( Mockito.anyLong() ) ).thenReturn(
            new OrganizationUnitSettings() );
        Mockito.when( organizationManagementService.getRegionSettings( Mockito.anyLong() ) ).thenReturn( regionSettings );
        assertEquals( "test", prepareBillingReport.getAddress( new AgentSettings(), reportRow, "CA", "RA", null ) );
    }


    @Test
    public void testGetAddressFromRegionForNonDefaultBranchAndRegionAndRegionAddressNull() throws InvalidInputException,
        NoRecordsFetchedException
    {
        OrganizationUnitSettings regionSettings = new OrganizationUnitSettings();
        ContactDetailsSettings contactDetails = new ContactDetailsSettings();
        contactDetails.setAddress( "test" );
        regionSettings.setContact_details( contactDetails );
        BillingReportData reportRow = new BillingReportData();
        reportRow.setBranch( "TestBranch" );
        reportRow.setBranchId( 0l );
        reportRow.setRegion( "TestRegion" );
        reportRow.setRegionId( 1l );
        Mockito.when( organizationManagementService.getBranchSettingsDefault( Mockito.anyLong() ) ).thenReturn(
            new OrganizationUnitSettings() );
        Mockito.when( organizationManagementService.getRegionSettings( Mockito.anyLong() ) ).thenReturn( regionSettings );
        assertEquals( "test", prepareBillingReport.getAddress( new AgentSettings(), reportRow, "CA", null, null ) );
    }


    @Test
    public void testGetAddressFromRegionForDefaultBranchAndRegionAndRegionAddressNull() throws InvalidInputException,
        NoRecordsFetchedException
    {
        OrganizationUnitSettings regionSettings = new OrganizationUnitSettings();
        ContactDetailsSettings contactDetails = new ContactDetailsSettings();
        contactDetails.setAddress( "test" );
        regionSettings.setContact_details( contactDetails );
        BillingReportData reportRow = new BillingReportData();
        reportRow.setBranch( CommonConstants.DEFAULT_BRANCH_NAME );
        reportRow.setBranchId( 0l );
        reportRow.setRegion( "TestRegion" );
        reportRow.setRegionId( 1l );
        Mockito.when( organizationManagementService.getBranchSettingsDefault( Mockito.anyLong() ) ).thenReturn(
            new OrganizationUnitSettings() );
        Mockito.when( organizationManagementService.getRegionSettings( Mockito.anyLong() ) ).thenReturn( regionSettings );
        assertEquals( "test", prepareBillingReport.getAddress( new AgentSettings(), reportRow, "CA", null, null ) );
    }


    @Test
    public void testGetAddressFromRegionForDefaultBranchAndRegion() throws InvalidInputException, NoRecordsFetchedException
    {
        OrganizationUnitSettings regionSettings = new OrganizationUnitSettings();
        ContactDetailsSettings contactDetails = new ContactDetailsSettings();
        contactDetails.setAddress( "test" );
        regionSettings.setContact_details( contactDetails );
        BillingReportData reportRow = new BillingReportData();
        reportRow.setBranch( CommonConstants.DEFAULT_BRANCH_NAME );
        reportRow.setBranchId( 0l );
        reportRow.setRegion( "TestRegion" );
        reportRow.setRegionId( 0l );
        Mockito.when( organizationManagementService.getBranchSettingsDefault( Mockito.anyLong() ) ).thenReturn(
            new OrganizationUnitSettings() );
        Mockito.when( organizationManagementService.getRegionSettings( Mockito.anyLong() ) ).thenReturn( regionSettings );
        assertEquals( "RA", prepareBillingReport.getAddress( new AgentSettings(), reportRow, "CA", "RA", null ) );
    }


    @Test
    public void testGetAddressFromCompanyForNonDefaultRegionAndCompanyIdDifferent() throws InvalidInputException,
        NoRecordsFetchedException
    {
        OrganizationUnitSettings companySettings = new OrganizationUnitSettings();
        ContactDetailsSettings contactDetails = new ContactDetailsSettings();
        contactDetails.setAddress( "test" );
        companySettings.setContact_details( contactDetails );
        BillingReportData reportRow = new BillingReportData();
        reportRow.setBranch( CommonConstants.DEFAULT_BRANCH_NAME );
        reportRow.setBranchId( 0l );
        reportRow.setRegion( "TestRegion" );
        reportRow.setRegionId( 0l );
        reportRow.setCompanyId( 1l );
        Mockito.when( organizationManagementService.getBranchSettingsDefault( Mockito.anyLong() ) ).thenReturn(
            new OrganizationUnitSettings() );
        Mockito.when( organizationManagementService.getRegionSettings( Mockito.anyLong() ) ).thenReturn(
            new OrganizationUnitSettings() );
        Mockito.when( organizationManagementService.getCompanySettings( Mockito.anyLong() ) ).thenReturn( companySettings );
        assertEquals( "test", prepareBillingReport.getAddress( new AgentSettings(), reportRow, "CA", null, null ) );
    }


    @Test
    public void testGetAddressFromCompanyForDefaultRegionAndCompanyIdDifferent() throws InvalidInputException,
        NoRecordsFetchedException
    {
        OrganizationUnitSettings companySettings = new OrganizationUnitSettings();
        ContactDetailsSettings contactDetails = new ContactDetailsSettings();
        contactDetails.setAddress( "test" );
        companySettings.setContact_details( contactDetails );
        BillingReportData reportRow = new BillingReportData();
        reportRow.setBranch( CommonConstants.DEFAULT_BRANCH_NAME );
        reportRow.setBranchId( 0l );
        reportRow.setRegion( CommonConstants.DEFAULT_REGION_NAME );
        reportRow.setRegionId( 0l );
        reportRow.setCompanyId( 1l );
        Mockito.when( organizationManagementService.getCompanySettings( Mockito.anyLong() ) ).thenReturn( companySettings );
        assertEquals( "test", prepareBillingReport.getAddress( new AgentSettings(), reportRow, "CA", null, null ) );
    }


    @Test
    public void testGetAddressFromCompanyForDefaultRegionAndCompanyAddrNull() throws InvalidInputException,
        NoRecordsFetchedException
    {
        OrganizationUnitSettings companySettings = new OrganizationUnitSettings();
        ContactDetailsSettings contactDetails = new ContactDetailsSettings();
        contactDetails.setAddress( "test" );
        companySettings.setContact_details( contactDetails );
        BillingReportData reportRow = new BillingReportData();
        reportRow.setBranch( CommonConstants.DEFAULT_BRANCH_NAME );
        reportRow.setBranchId( 0l );
        reportRow.setRegion( CommonConstants.DEFAULT_REGION_NAME );
        reportRow.setRegionId( 0l );
        reportRow.setCompanyId( 0l );
        Mockito.when( organizationManagementService.getCompanySettings( Mockito.anyLong() ) ).thenReturn( companySettings );
        assertEquals( "test", prepareBillingReport.getAddress( new AgentSettings(), reportRow, null, null, null ) );
    }


    @Test
    public void testGetAddressFromCompany() throws InvalidInputException, NoRecordsFetchedException
    {
        OrganizationUnitSettings companySettings = new OrganizationUnitSettings();
        ContactDetailsSettings contactDetails = new ContactDetailsSettings();
        contactDetails.setAddress( "test" );
        companySettings.setContact_details( contactDetails );
        BillingReportData reportRow = new BillingReportData();
        reportRow.setBranch( CommonConstants.DEFAULT_BRANCH_NAME );
        reportRow.setBranchId( 0l );
        reportRow.setRegion( CommonConstants.DEFAULT_REGION_NAME );
        reportRow.setRegionId( 0l );
        reportRow.setCompanyId( 0l );
        Mockito.when( organizationManagementService.getCompanySettings( Mockito.anyLong() ) ).thenReturn( companySettings );
        assertEquals( "CA", prepareBillingReport.getAddress( new AgentSettings(), reportRow, "CA", null, null ) );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAddressFromContactDetails() throws InvalidInputException
    {
        prepareBillingReport.getAddressFromContactDetails( null );
    }
}
