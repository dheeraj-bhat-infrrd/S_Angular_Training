package com.realtech.socialsurvey.core.starter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
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


    @Test ( expected = InvalidInputException.class)
    public void testGetAddressForUserForInvalidCompanyId() throws InvalidInputException
    {
        prepareBillingReport.getAddressForUser( new AgentSettings(), new OrganizationUnitSettings(), new BillingReportData(),
            0, new HashMap<Long, OrganizationUnitSettings>(), new HashMap<Long, OrganizationUnitSettings>() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAddressForUserForCompanySettingsNull() throws InvalidInputException
    {
        prepareBillingReport.getAddressForUser( new AgentSettings(), null, new BillingReportData(), 1,
            new HashMap<Long, OrganizationUnitSettings>(), new HashMap<Long, OrganizationUnitSettings>() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAddressForUserForAgentSettingsNull() throws InvalidInputException
    {
        prepareBillingReport.getAddressForUser( null, new OrganizationUnitSettings(), new BillingReportData(), 1,
            new HashMap<Long, OrganizationUnitSettings>(), new HashMap<Long, OrganizationUnitSettings>() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAddressForUserForReportRowNull() throws InvalidInputException
    {
        prepareBillingReport.getAddressForUser( new AgentSettings(), new OrganizationUnitSettings(), null, 1,
            new HashMap<Long, OrganizationUnitSettings>(), new HashMap<Long, OrganizationUnitSettings>() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAddressForUserForRegionsSettingsNull() throws InvalidInputException
    {
        prepareBillingReport.getAddressForUser( new AgentSettings(), new OrganizationUnitSettings(), new BillingReportData(),
            1, null, new HashMap<Long, OrganizationUnitSettings>() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAddressForUserForBranchesSettingsNull() throws InvalidInputException
    {
        prepareBillingReport.getAddressForUser( new AgentSettings(), new OrganizationUnitSettings(), new BillingReportData(),
            1, new HashMap<Long, OrganizationUnitSettings>(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAddressForUserForContactDetailsNull() throws InvalidInputException
    {
        prepareBillingReport.getAddressForUser( new AgentSettings(), new OrganizationUnitSettings(), new BillingReportData(),
            1, new HashMap<Long, OrganizationUnitSettings>(), new HashMap<Long, OrganizationUnitSettings>() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAddressForUserForStateNull() throws InvalidInputException
    {
        OrganizationUnitSettings companySettings = new OrganizationUnitSettings();
        companySettings.setContact_details( new ContactDetailsSettings() );
        prepareBillingReport.getAddressForUser( new AgentSettings(), companySettings, new BillingReportData(), 1,
            new HashMap<Long, OrganizationUnitSettings>(), new HashMap<Long, OrganizationUnitSettings>() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAddressForUserForStateEmpty() throws InvalidInputException
    {
        OrganizationUnitSettings companySettings = new OrganizationUnitSettings();
        ContactDetailsSettings contactDetails = new ContactDetailsSettings();
        contactDetails.setState( "" );
        companySettings.setContact_details( contactDetails );
        prepareBillingReport.getAddressForUser( new AgentSettings(), companySettings, new BillingReportData(), 1,
            new HashMap<Long, OrganizationUnitSettings>(), new HashMap<Long, OrganizationUnitSettings>() );
    }


    @SuppressWarnings ( "unchecked")
    @Test
    public void testGetAddressForCompanyStateReturned() throws InvalidInputException
    {
        OrganizationUnitSettings companySettings = new OrganizationUnitSettings();
        ContactDetailsSettings contactDetails = new ContactDetailsSettings();
        BillingReportData reportRow = new BillingReportData();
        reportRow.setBranch( CommonConstants.DEFAULT_BRANCH_NAME );
        reportRow.setRegion( CommonConstants.DEFAULT_REGION_NAME );
        contactDetails.setState( "LA" );
        companySettings.setContact_details( contactDetails );
        Mockito.doReturn( "LA" ).when( prepareBillingReport )
            .getRegionAddress( (BillingReportData) Matchers.anyObject(), Matchers.anyString(), Matchers.anyMap() );
        assertEquals( "LA", prepareBillingReport.getAddressForUser( new AgentSettings(), companySettings, reportRow, 1,
            new HashMap<Long, OrganizationUnitSettings>(), new HashMap<Long, OrganizationUnitSettings>() ) );
    }


    @SuppressWarnings ( "unchecked")
    @Test
    public void testGetAddressForBranchSettingInMapForBranchContactDetailNull() throws InvalidInputException
    {
        OrganizationUnitSettings companySettings = new OrganizationUnitSettings();
        ContactDetailsSettings contactDetails = new ContactDetailsSettings();
        Map<Long, OrganizationUnitSettings> branchesSettings = new HashMap<Long, OrganizationUnitSettings>();
        OrganizationUnitSettings branchSettings = new OrganizationUnitSettings();
        BillingReportData reportRow = new BillingReportData();
        contactDetails.setState( "LA" );
        reportRow.setBranch( "Branch1" );
        reportRow.setBranchId( 1l );
        branchSettings.setContact_details( new ContactDetailsSettings() );
        branchesSettings.put( 1l, branchSettings );
        companySettings.setContact_details( contactDetails );
        Mockito.doReturn( "LA" ).when( prepareBillingReport )
            .getRegionAddress( (BillingReportData) Matchers.anyObject(), Matchers.anyString(), Matchers.anyMap() );
        assertEquals( "LA", prepareBillingReport.getAddressForUser( new AgentSettings(), companySettings, reportRow, 1,
            new HashMap<Long, OrganizationUnitSettings>(), branchesSettings ) );
    }


    @SuppressWarnings ( "unchecked")
    @Test
    public void testGetAddressForBranchSettingInMapForBranchContactDetailValid() throws InvalidInputException
    {
        OrganizationUnitSettings companySettings = new OrganizationUnitSettings();
        ContactDetailsSettings contactDetails = new ContactDetailsSettings();
        Map<Long, OrganizationUnitSettings> branchesSettings = new HashMap<Long, OrganizationUnitSettings>();
        OrganizationUnitSettings branchSettings = new OrganizationUnitSettings();
        BillingReportData reportRow = new BillingReportData();
        contactDetails.setState( "LA" );
        reportRow.setBranch( "Branch1" );
        reportRow.setBranchId( 1l );
        companySettings.setContact_details( contactDetails );
        contactDetails.setState( "TN" );
        branchSettings.setContact_details( contactDetails );
        branchesSettings.put( 1l, branchSettings );
        Mockito.doReturn( "LA" ).when( prepareBillingReport )
            .getRegionAddress( (BillingReportData) Matchers.anyObject(), Matchers.anyString(), Matchers.anyMap() );
        assertEquals( ", TN", prepareBillingReport.getAddressForUser( new AgentSettings(), companySettings, reportRow, 1,
            new HashMap<Long, OrganizationUnitSettings>(), branchesSettings ) );
    }


    @SuppressWarnings ( "unchecked")
    @Test ( expected = InvalidInputException.class)
    public void testGetAddressForBranchSettingNotInMapForInvalidBranch() throws InvalidInputException,
        NoRecordsFetchedException
    {
        OrganizationUnitSettings companySettings = new OrganizationUnitSettings();
        ContactDetailsSettings contactDetails = new ContactDetailsSettings();
        Map<Long, OrganizationUnitSettings> branchesSettings = new HashMap<Long, OrganizationUnitSettings>();
        BillingReportData reportRow = new BillingReportData();
        contactDetails.setState( "LA" );
        reportRow.setBranch( "Branch1" );
        reportRow.setBranchId( 1l );
        companySettings.setContact_details( contactDetails );
        Mockito.when( organizationManagementService.getBranchSettingsDefault( Mockito.anyLong() ) ).thenThrow(
            NoRecordsFetchedException.class );
        prepareBillingReport.getAddressForUser( new AgentSettings(), companySettings, reportRow, 1,
            new HashMap<Long, OrganizationUnitSettings>(), branchesSettings );
    }


    @SuppressWarnings ( "unchecked")
    @Test
    public void testGetAddressForBranchSettingInMapForBranchContactDetailsValid() throws InvalidInputException,
        NoRecordsFetchedException
    {
        OrganizationUnitSettings companySettings = new OrganizationUnitSettings();
        ContactDetailsSettings contactDetails = new ContactDetailsSettings();
        Map<Long, OrganizationUnitSettings> branchesSettings = new HashMap<Long, OrganizationUnitSettings>();
        BillingReportData reportRow = new BillingReportData();
        contactDetails.setState( "LA" );
        reportRow.setBranch( "Branch1" );
        reportRow.setBranchId( 1l );
        companySettings.setContact_details( contactDetails );
        OrganizationUnitSettings branchSettings = new OrganizationUnitSettings();
        contactDetails.setState( "KY" );
        branchSettings.setContact_details( contactDetails );
        branchesSettings.put( 1l, branchSettings );
        Mockito.when( organizationManagementService.getBranchSettingsDefault( Mockito.anyLong() ) ).thenThrow(
            NoRecordsFetchedException.class );
        assertEquals( ", KY", prepareBillingReport.getAddressForUser( new AgentSettings(), companySettings, reportRow, 1,
            new HashMap<Long, OrganizationUnitSettings>(), branchesSettings ) );
    }


    @SuppressWarnings ( "unchecked")
    @Test
    public void testGetAddressForBranchSettingInMapForBranchContactDetailsNull() throws InvalidInputException,
        NoRecordsFetchedException
    {
        OrganizationUnitSettings companySettings = new OrganizationUnitSettings();
        ContactDetailsSettings contactDetails = new ContactDetailsSettings();
        Map<Long, OrganizationUnitSettings> branchesSettings = new HashMap<Long, OrganizationUnitSettings>();
        BillingReportData reportRow = new BillingReportData();
        contactDetails.setState( "LA" );
        reportRow.setBranch( "Branch1" );
        reportRow.setBranchId( 1l );
        companySettings.setContact_details( contactDetails );
        OrganizationUnitSettings branchSettings = new OrganizationUnitSettings();
        branchSettings.setContact_details( new ContactDetailsSettings() );
        branchesSettings.put( 1l, branchSettings );
        Mockito.when( organizationManagementService.getBranchSettingsDefault( Mockito.anyLong() ) ).thenThrow(
            NoRecordsFetchedException.class );
        Mockito.doReturn( "TN" ).when( prepareBillingReport )
            .getRegionAddress( (BillingReportData) Matchers.anyObject(), Matchers.anyString(), Matchers.anyMap() );
        assertEquals( "TN", prepareBillingReport.getAddressForUser( new AgentSettings(), companySettings, reportRow, 1,
            new HashMap<Long, OrganizationUnitSettings>(), branchesSettings ) );
    }


    @SuppressWarnings ( "unchecked")
    @Test
    public void testGetAddressForBranchSettingNotInMapForBranchContactDetailsNull() throws InvalidInputException,
        NoRecordsFetchedException
    {
        OrganizationUnitSettings companySettings = new OrganizationUnitSettings();
        ContactDetailsSettings contactDetails = new ContactDetailsSettings();
        BillingReportData reportRow = new BillingReportData();
        contactDetails.setState( "LA" );
        reportRow.setBranch( "Branch1" );
        reportRow.setBranchId( 1l );
        companySettings.setContact_details( contactDetails );
        OrganizationUnitSettings branchSettings = new OrganizationUnitSettings();
        branchSettings.setContact_details( new ContactDetailsSettings() );
        Mockito.when( organizationManagementService.getBranchSettingsDefault( Mockito.anyLong() ) ).thenReturn( branchSettings );
        Mockito.doReturn( "TN" ).when( prepareBillingReport )
            .getRegionAddress( (BillingReportData) Matchers.anyObject(), Matchers.anyString(), Matchers.anyMap() );
        assertEquals( "TN", prepareBillingReport.getAddressForUser( new AgentSettings(), companySettings, reportRow, 1,
            new HashMap<Long, OrganizationUnitSettings>(), new HashMap<Long, OrganizationUnitSettings>() ) );
    }


    @Test
    public void testGetAddressForBranchSettingNotInMapForBranchContactDetailsValid() throws InvalidInputException,
        NoRecordsFetchedException
    {
        OrganizationUnitSettings companySettings = new OrganizationUnitSettings();
        ContactDetailsSettings contactDetails = new ContactDetailsSettings();
        BillingReportData reportRow = new BillingReportData();
        contactDetails.setState( "LA" );
        reportRow.setBranch( "Branch1" );
        reportRow.setBranchId( 1l );
        companySettings.setContact_details( contactDetails );
        OrganizationUnitSettings branchSettings = new OrganizationUnitSettings();
        contactDetails.setState( "KY" );
        branchSettings.setContact_details( contactDetails );
        Mockito.when( organizationManagementService.getBranchSettingsDefault( Mockito.anyLong() ) ).thenReturn( branchSettings );
        assertEquals( ", KY", prepareBillingReport.getAddressForUser( new AgentSettings(), companySettings, reportRow, 1,
            new HashMap<Long, OrganizationUnitSettings>(), new HashMap<Long, OrganizationUnitSettings>() ) );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetRegionAddressForReportRowNull() throws InvalidInputException
    {
        prepareBillingReport.getRegionAddress( null, "XY", new HashMap<Long, OrganizationUnitSettings>() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetRegionAddressForCompanyStateEmpty() throws InvalidInputException
    {
        prepareBillingReport.getRegionAddress( new BillingReportData(), "", new HashMap<Long, OrganizationUnitSettings>() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetRegionAddressForRegionsSettingsNull() throws InvalidInputException
    {
        prepareBillingReport.getRegionAddress( new BillingReportData(), "XY", null );
    }


    @Test
    public void testGetRegionAddressForDefaultRegion() throws InvalidInputException
    {
        BillingReportData reportRow = new BillingReportData();
        reportRow.setRegion( CommonConstants.DEFAULT_REGION_NAME );
        assertEquals( "XY",
            prepareBillingReport.getRegionAddress( reportRow, "XY", new HashMap<Long, OrganizationUnitSettings>() ) );
    }


    @Test
    public void testGetRegionAddressForRegionSettingInMapForContactDetailsNull() throws InvalidInputException
    {
        BillingReportData reportRow = new BillingReportData();
        reportRow.setRegionId( 1l );
        Map<Long, OrganizationUnitSettings> regionsSettings = new HashMap<Long, OrganizationUnitSettings>();
        OrganizationUnitSettings regionSettings = new OrganizationUnitSettings();
        regionSettings.setContact_details( new ContactDetailsSettings() );
        regionsSettings.put( 1l, regionSettings );
        reportRow.setRegion( "RegionX" );
        assertEquals( "XY", prepareBillingReport.getRegionAddress( reportRow, "XY", regionsSettings ) );
    }


    @Test
    public void testGetRegionAddressForRegionSettingInMapForContactDetailsValid() throws InvalidInputException
    {
        BillingReportData reportRow = new BillingReportData();
        reportRow.setRegionId( 1l );
        Map<Long, OrganizationUnitSettings> regionsSettings = new HashMap<Long, OrganizationUnitSettings>();
        OrganizationUnitSettings regionSettings = new OrganizationUnitSettings();
        ContactDetailsSettings contactDetails = new ContactDetailsSettings();
        contactDetails.setState( "TX" );
        regionSettings.setContact_details( contactDetails );
        regionsSettings.put( 1l, regionSettings );
        reportRow.setRegion( "RegionX" );
        assertEquals( ", TX", prepareBillingReport.getRegionAddress( reportRow, "XY", regionsSettings ) );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAddressFromContactDetailsForContactDetailsNull() throws InvalidInputException
    {
        prepareBillingReport.getAddressFromContactDetails( null );
    }
}
