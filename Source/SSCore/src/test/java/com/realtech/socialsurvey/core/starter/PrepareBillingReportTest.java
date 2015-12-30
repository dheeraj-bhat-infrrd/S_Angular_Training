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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.CompanyDao;
import com.realtech.socialsurvey.core.entities.BillingReportData;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
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
