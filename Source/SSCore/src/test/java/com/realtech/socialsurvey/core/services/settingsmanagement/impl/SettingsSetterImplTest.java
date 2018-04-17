package com.realtech.socialsurvey.core.services.settingsmanagement.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.SettingsSetterLevel;
import com.realtech.socialsurvey.core.enums.OrganizationUnit;
import com.realtech.socialsurvey.core.enums.SettingsForApplication;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;


public class SettingsSetterImplTest
{
    @Spy
    @InjectMocks
    private SettingsSetterImpl settingsSetterImpl;


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
        settingsSetterImpl = new SettingsSetterImpl();
    }


    @After
    public void tearDown() throws Exception
    {}


    //Tests for setSettingsValueForCompany
    @Test ( expected = InvalidInputException.class)
    public void setSettingsValueForCompanyTestCompanyNull() throws NonFatalException
    {
        settingsSetterImpl.setSettingsValueForCompany( null, SettingsForApplication.ABOUT_ME, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void setSettingsValueForCompanyTestSettingsNull() throws NonFatalException
    {
        settingsSetterImpl.setSettingsValueForCompany( new Company(), null, false );
    }


    //Tests for setSettingsValueForRegion
    @Test ( expected = InvalidInputException.class)
    public void setSettingsValueForRegionTestRegionNull() throws NonFatalException
    {
        settingsSetterImpl.setSettingsValueForRegion( null, SettingsForApplication.ABOUT_ME, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void setSettingsValueForRegionTestSettingsNull() throws NonFatalException
    {
        settingsSetterImpl.setSettingsValueForRegion( new Region(), null, false );
    }


    //Tests for setSettingsValueForBranch
    @Test ( expected = InvalidInputException.class)
    public void setSettingsValueForBranchTestBrancNull() throws NonFatalException
    {
        settingsSetterImpl.setSettingsValueForBranch( null, SettingsForApplication.ABOUT_ME, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void setSettingsValueForBranchTestSettingsNull() throws NonFatalException
    {
        settingsSetterImpl.setSettingsValueForBranch( new Branch(), null, false );
    }


    //Tests for isSettingsValueSet
    @Test
    public void isSettingsValueSetTestCurrentValueLessThanSettings()
    {
        SettingsForApplication settings = SettingsForApplication.LOGO;
        long currentSetValue = 10000000l;
        assertEquals( "Test", false, settingsSetterImpl.isSettingsValueSet( null, currentSetValue, settings ) );
    }


    //Tests for checkSettingsSetStatus
    @Test
    public void testcheckSettingsSetStatusWithInvalidSettingNumber() throws NonFatalException
    {
        assertFalse( "Setting status does not match expected",
            settingsSetterImpl.checkSettingsSetStatus( 0, OrganizationUnit.AGENT ) );
    }


    @Test
    public void testcheckSettingsSetStatusWithValidSettingNumberAndNullOrganizationUnit() throws NonFatalException
    {
        assertFalse( "Setting status does not match expected",
            settingsSetterImpl.checkSettingsSetStatus( CommonConstants.SET_BY_NONE, null ) );
    }


    @Test
    public void testcheckSettingsSetStatusWithNoneSettingNumberAndCompanyOrganizationUnit() throws NonFatalException
    {
        assertFalse( "Setting status does not match expected",
            settingsSetterImpl.checkSettingsSetStatus( CommonConstants.SET_BY_NONE, OrganizationUnit.COMPANY ) );
    }


    @Test
    public void testcheckSettingsSetStatusWithNoneSettingNumberAndRegionOrganizationUnit() throws NonFatalException
    {
        assertFalse( "Setting status does not match expected",
            settingsSetterImpl.checkSettingsSetStatus( CommonConstants.SET_BY_NONE, OrganizationUnit.REGION ) );
    }


    @Test
    public void testcheckSettingsSetStatusWithNoneSettingNumberAndBranchOrganizationUnit() throws NonFatalException
    {
        assertFalse( "Setting status does not match expected",
            settingsSetterImpl.checkSettingsSetStatus( CommonConstants.SET_BY_NONE, OrganizationUnit.BRANCH ) );
    }


    @Test
    public void testcheckSettingsSetStatusWithNoneSettingNumberAndAgentOrganizationUnit() throws NonFatalException
    {
        assertFalse( "Setting status does not match expected",
            settingsSetterImpl.checkSettingsSetStatus( CommonConstants.SET_BY_NONE, OrganizationUnit.BRANCH ) );
    }


    @Test
    public void testcheckSettingsSetStatusWithSetByRegionSettingNumberAndCompanyOrganizationUnit() throws NonFatalException
    {
        assertFalse( "Setting status does not match expected",
            settingsSetterImpl.checkSettingsSetStatus( CommonConstants.SET_BY_REGION, OrganizationUnit.COMPANY ) );
    }


    @Test
    public void testcheckSettingsSetStatusWithSetByBranchSettingNumberAndCompanyOrganizationUnit() throws NonFatalException
    {
        assertFalse( "Setting status does not match expected",
            settingsSetterImpl.checkSettingsSetStatus( CommonConstants.SET_BY_BRANCH, OrganizationUnit.COMPANY ) );
    }


    @Test
    public void testcheckSettingsSetStatusWithSetByCompanySettingNumberAndRegionOrganizationUnit() throws NonFatalException
    {
        assertFalse( "Setting status does not match expected",
            settingsSetterImpl.checkSettingsSetStatus( CommonConstants.SET_BY_COMPANY, OrganizationUnit.REGION ) );
    }


    @Test
    public void testcheckSettingsSetStatusWithSetByBranchSettingNumberAndRegionOrganizationUnit() throws NonFatalException
    {
        assertFalse( "Setting status does not match expected",
            settingsSetterImpl.checkSettingsSetStatus( CommonConstants.SET_BY_BRANCH, OrganizationUnit.REGION ) );
    }


    @Test
    public void testcheckSettingsSetStatusWithSetByCompanySettingNumberAndBranchOrganizationUnit() throws NonFatalException
    {
        assertFalse( "Setting status does not match expected",
            settingsSetterImpl.checkSettingsSetStatus( CommonConstants.SET_BY_COMPANY, OrganizationUnit.BRANCH ) );
    }


    @Test
    public void testcheckSettingsSetStatusWithSetByRegionSettingNumberAndBranchOrganizationUnit() throws NonFatalException
    {
        assertFalse( "Setting status does not match expected",
            settingsSetterImpl.checkSettingsSetStatus( CommonConstants.SET_BY_REGION, OrganizationUnit.BRANCH ) );
    }


    @Test
    public void testcheckSettingsSetStatusWithSetByCompanySettingNumberAndCompanyOrganizationUnit() throws NonFatalException
    {
        assertTrue( "Setting status does not match expected",
            settingsSetterImpl.checkSettingsSetStatus( CommonConstants.SET_BY_COMPANY, OrganizationUnit.COMPANY ) );
    }


    @Test
    public void testcheckSettingsSetStatusWithSetByCompanyNRegionSettingNumberAndCompanyOrganizationUnit()
        throws NonFatalException
    {
        assertTrue( "Setting status does not match expected",
            settingsSetterImpl.checkSettingsSetStatus( CommonConstants.SET_BY_COMPANY_N_REGION, OrganizationUnit.COMPANY ) );
    }


    @Test
    public void testcheckSettingsSetStatusWithSetByCompanyNBranchSettingNumberAndCompanyOrganizationUnit()
        throws NonFatalException
    {
        assertTrue( "Setting status does not match expected",
            settingsSetterImpl.checkSettingsSetStatus( CommonConstants.SET_BY_COMPANY_N_BRANCH, OrganizationUnit.COMPANY ) );
    }


    @Test
    public void testcheckSettingsSetStatusWithSetByCompanyNRegionNBranchSettingNumberAndCompanyOrganizationUnit()
        throws NonFatalException
    {
        assertTrue( "Setting status does not match expected", settingsSetterImpl.checkSettingsSetStatus(
            CommonConstants.SET_BY_COMPANY_N_REGION_N_BRANCH, OrganizationUnit.COMPANY ) );
    }


    @Test
    public void testcheckSettingsSetStatusWithSetByRegionSettingNumberAndRegionOrganizationUnit() throws NonFatalException
    {
        assertTrue( "Setting status does not match expected",
            settingsSetterImpl.checkSettingsSetStatus( CommonConstants.SET_BY_REGION, OrganizationUnit.REGION ) );
    }


    @Test
    public void testcheckSettingsSetStatusWithSetByCompanyNRegionSettingNumberAndRegionOrganizationUnit()
        throws NonFatalException
    {
        assertTrue( "Setting status does not match expected",
            settingsSetterImpl.checkSettingsSetStatus( CommonConstants.SET_BY_COMPANY_N_REGION, OrganizationUnit.REGION ) );
    }


    @Test
    public void testcheckSettingsSetStatusWithSetByRegionNBranchSettingNumberAndRegionOrganizationUnit()
        throws NonFatalException
    {
        assertTrue( "Setting status does not match expected",
            settingsSetterImpl.checkSettingsSetStatus( CommonConstants.SET_BY_REGION_N_BRANCH, OrganizationUnit.REGION ) );
    }


    @Test
    public void testcheckSettingsSetStatusWithSetByCompanyNRegionNBranchSettingNumberAndRegionOrganizationUnit()
        throws NonFatalException
    {
        assertTrue( "Setting status does not match expected", settingsSetterImpl.checkSettingsSetStatus(
            CommonConstants.SET_BY_COMPANY_N_REGION_N_BRANCH, OrganizationUnit.REGION ) );
    }


    @Test
    public void testcheckSettingsSetStatusWithSetByBranchSettingNumberAndBranchOrganizationUnit() throws NonFatalException
    {
        assertTrue( "Setting status does not match expected",
            settingsSetterImpl.checkSettingsSetStatus( CommonConstants.SET_BY_BRANCH, OrganizationUnit.BRANCH ) );
    }


    @Test
    public void testcheckSettingsSetStatusWithSetByCompanyNRegionSettingNumberAndBranchOrganizationUnit()
        throws NonFatalException
    {
        assertTrue( "Setting status does not match expected",
            settingsSetterImpl.checkSettingsSetStatus( CommonConstants.SET_BY_REGION_N_BRANCH, OrganizationUnit.BRANCH ) );
    }


    @Test
    public void testcheckSettingsSetStatusWithSetByCompanyNBranchSettingNumberAndBranchOrganizationUnit()
        throws NonFatalException
    {
        assertTrue( "Setting status does not match expected",
            settingsSetterImpl.checkSettingsSetStatus( CommonConstants.SET_BY_COMPANY_N_BRANCH, OrganizationUnit.BRANCH ) );
    }


    @Test
    public void testcheckSettingsSetStatusWithSetByCompanyNRegionNBranchSettingNumberAndBranchOrganizationUnit()
        throws NonFatalException
    {
        assertTrue( "Setting status does not match expected", settingsSetterImpl.checkSettingsSetStatus(
            CommonConstants.SET_BY_COMPANY_N_REGION_N_BRANCH, OrganizationUnit.BRANCH ) );
    }


    //Tests for getSettingsSetLevel
    @Test
    public void getSettingsSetLevelTestCurrentSetAggregateValueLessThanSettings()
    {
        SettingsSetterLevel level = settingsSetterImpl.getSettingsSetLevel( 10l, SettingsForApplication.ABOUT_ME );
        assertFalse( "Set by Branch", level.isSetByBranch() );
        assertFalse( "Set by Region", level.isSetByRegion() );
        assertFalse( "Set by Company", level.isSetByCompany() );
    }


    @Test
    public void getSettingsSetLevelTestSetByCompanyOnly()
    {
        SettingsSetterLevel level = settingsSetterImpl.getSettingsSetLevel( 100001l, SettingsForApplication.LOGO );
        assertTrue( "Not set by Company", level.isSetByCompany() );
        assertFalse( "Set by Region", level.isSetByRegion() );
        assertFalse( "Set by Branch", level.isSetByBranch() );
    }


    @Test
    public void getSettingsSetLevelTestSetByRegionOnly()
    {
        SettingsSetterLevel level = settingsSetterImpl.getSettingsSetLevel( 100002l, SettingsForApplication.LOGO );
        assertFalse( "set by Company", level.isSetByCompany() );
        assertTrue( "Not set by Region", level.isSetByRegion() );
        assertFalse( "Set by Branch", level.isSetByBranch() );
    }


    @Test
    public void getSettingsSetLEvelTestSetByBranchOnly()
    {
        SettingsSetterLevel level = settingsSetterImpl.getSettingsSetLevel( 100004l, SettingsForApplication.LOGO );
        assertFalse( "Set by Company", level.isSetByCompany() );
        assertFalse( "Set by Region", level.isSetByRegion() );
        assertTrue( "Not set by Branch", level.isSetByBranch() );
    }


    @Test
    public void getSettingsSetLevelTestSetByCompanyNRegion()
    {
        SettingsSetterLevel level = settingsSetterImpl.getSettingsSetLevel( 100003l, SettingsForApplication.LOGO );
        assertTrue( "Not set by Company", level.isSetByCompany() );
        assertTrue( "Not set by Region", level.isSetByRegion() );
        assertFalse( "Set by Branch", level.isSetByBranch() );
    }


    @Test
    public void getSettingsSetLevelTestSetByCompanyNBranch()
    {
        SettingsSetterLevel level = settingsSetterImpl.getSettingsSetLevel( 100005l, SettingsForApplication.LOGO );
        assertTrue( "Not set by Company", level.isSetByCompany() );
        assertFalse( "Set by Region", level.isSetByRegion() );
        assertTrue( "Not set by Branch", level.isSetByBranch() );
    }


    @Test
    public void getSettingsSetLevelTestSetByRegionNBranch()
    {
        SettingsSetterLevel level = settingsSetterImpl.getSettingsSetLevel( 100006l, SettingsForApplication.LOGO );
        assertFalse( "Set by Company", level.isSetByCompany() );
        assertTrue( "Not set by Region", level.isSetByRegion() );
        assertTrue( "Not set by Branch", level.isSetByBranch() );
    }


    @Test
    public void getSettingsSetLevelTestSetByCompanyNRegionNBranch()
    {
        SettingsSetterLevel level = settingsSetterImpl.getSettingsSetLevel( 100007l, SettingsForApplication.LOGO );
        assertTrue( "Not set by Company", level.isSetByCompany() );
        assertTrue( "Not set by Region", level.isSetByRegion() );
        assertTrue( "Not set by Branch", level.isSetByBranch() );
    }


    @Test
    public void getSettingsSetLevelTestSetByNone()
    {
        SettingsSetterLevel level = settingsSetterImpl.getSettingsSetLevel( 100000l, SettingsForApplication.LOGO );
        assertFalse( "Set by Branch", level.isSetByBranch() );
        assertFalse( "Set by Region", level.isSetByRegion() );
        assertFalse( "Set by Company", level.isSetByCompany() );
    }


    //Tests for getLowestSetterLevel
    @Test
    public void getLowestSetterLevelSetByNone() throws InvalidSettingsStateException
    {
        assertEquals( "OrganizationUnit is not null", null, settingsSetterImpl.getLowestSetterLevel( 0 ) );
    }


    @Test ( expected = InvalidSettingsStateException.class)
    public void getLowestSetterLevelInvalidSetUnitValue() throws InvalidSettingsStateException
    {
        settingsSetterImpl.getLowestSetterLevel( -1 );
    }


    @Test
    public void getLowestSetterLevelTestSetByCompanyOnly() throws InvalidSettingsStateException
    {
        assertEquals( "OrganizationUnit is not null", OrganizationUnit.COMPANY, settingsSetterImpl.getLowestSetterLevel( 1 ) );
    }


    @Test
    public void getLowestSetterLevelTestSetByRegionOnly() throws InvalidSettingsStateException
    {
        assertEquals( "OrganizationUnit is not null", OrganizationUnit.REGION, settingsSetterImpl.getLowestSetterLevel( 2 ) );
    }


    @Test
    public void getLowestSetterLevelTestSetByBranchOnly() throws InvalidSettingsStateException
    {
        assertEquals( "OrganizationUnit is not null", OrganizationUnit.BRANCH, settingsSetterImpl.getLowestSetterLevel( 4 ) );
    }


    @Test
    public void getLowestSetterLevelTestSetByCompanyNRegion() throws InvalidSettingsStateException
    {
        assertEquals( "OrganizationUnit is not null", OrganizationUnit.REGION, settingsSetterImpl.getLowestSetterLevel( 3 ) );
    }


    @Test
    public void getLowestSetterLevelTestSetByCompanyNBranch() throws InvalidSettingsStateException
    {
        assertEquals( "OrganizationUnit is not null", OrganizationUnit.BRANCH, settingsSetterImpl.getLowestSetterLevel( 5 ) );
    }


    @Test
    public void getLowestSetterLevelTestSetByRegionNBranch() throws InvalidSettingsStateException
    {
        assertEquals( "OrganizationUnit is not null", OrganizationUnit.BRANCH, settingsSetterImpl.getLowestSetterLevel( 6 ) );
    }


    @Test
    public void getLowestSetterLevelTestSetByCompanyNRegionNBranch() throws InvalidSettingsStateException
    {
        assertEquals( "OrganizationUnit is not null", OrganizationUnit.BRANCH, settingsSetterImpl.getLowestSetterLevel( 7 ) );
    }


    @Test
    public void testGetModifiedSetSettingsValueAlreadyLockedNotSet() throws InvalidSettingsStateException
    {
        SettingsSetterImpl spy = Mockito.spy( settingsSetterImpl );
        Mockito.doReturn( false ).when( spy )
            .isSettingsValueSet( (OrganizationUnit) Mockito.any(), Mockito.anyLong(), (SettingsForApplication) Mockito.any() );
        assertEquals( 0, 0,
            settingsSetterImpl.getModifiedSetSettingsValue( OrganizationUnit.COMPANY, 0, SettingsForApplication.LOGO, false ) );
    }


    @Test
    public void testGetModifiedSetSettingsValueAlreadyLockedSettingSet() throws InvalidSettingsStateException
    {
        SettingsSetterImpl spy = Mockito.spy( settingsSetterImpl );
        Mockito.doReturn( false ).when( spy )
        .isSettingsValueSet( (OrganizationUnit) Mockito.any(), Mockito.anyLong(), (SettingsForApplication) Mockito.any() );
        assertEquals( 1, 1,
            settingsSetterImpl.getModifiedSetSettingsValue( OrganizationUnit.COMPANY, 0, SettingsForApplication.LOGO, true ) );
    }
}
