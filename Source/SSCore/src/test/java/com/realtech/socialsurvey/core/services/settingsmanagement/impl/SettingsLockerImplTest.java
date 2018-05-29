package com.realtech.socialsurvey.core.services.settingsmanagement.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.math.BigInteger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.realtech.socialsurvey.TestConstants;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.enums.OrganizationUnit;
import com.realtech.socialsurvey.core.enums.SettingsForApplication;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;


public class SettingsLockerImplTest
{
    @Spy
    @InjectMocks
    private SettingsLockerImpl settingsLockerImpl;

    SettingsForApplication settings;


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
        settings = SettingsForApplication.LOGO;
    }


    @After
    public void tearDown() throws Exception
    {}


    @Test ( expected = InvalidInputException.class)
    public void testLockSettingsValueForCompanyWithNullCompany() throws NonFatalException
    {
        settingsLockerImpl.lockSettingsValueForCompany( null, settings, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void testLockSettingsValueForCompanyWithNullSettingsForApplication() throws NonFatalException
    {
        settingsLockerImpl.lockSettingsValueForCompany( new Company(), null, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void testLockSettingsValueForRegionWithNullRegion() throws NonFatalException
    {
        settingsLockerImpl.lockSettingsValueForRegion( null, settings, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void testLockSettingsValueForRegionWithNullSettingsForApplication() throws NonFatalException
    {
        settingsLockerImpl.lockSettingsValueForRegion( new Region(), null, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void testLockSettingsValueForBranchWithNullBranch() throws NonFatalException
    {
        settingsLockerImpl.lockSettingsValueForBranch( null, settings, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void testLockSettingsValueForBranchWithNullSettingsForApplication() throws NonFatalException
    {
        settingsLockerImpl.lockSettingsValueForBranch( new Branch(), null, false );
    }


    /*@Test
    public void testCheckSettingsLockStatusWithInvalidLockNumber() throws NonFatalException
    {
        assertFalse( "Lock status does not match expected",
            settingsLockerImpl.checkSettingsLockStatus( TestConstants.TEST_INT, OrganizationUnit.AGENT ) );
    }*/


    @Test
    public void testCheckSettingsLockStatusWithValidLockNumberAndNullOrganizationUnit() throws NonFatalException
    {
        assertFalse( "Lock status does not match expected",
            settingsLockerImpl.checkSettingsLockStatus( CommonConstants.SET_BY_NONE, null ) );
    }


    @Test
    public void testCheckSettingsLockStatusWithSetByNoneAsLockNumberAndCompanyOrganizationUnit() throws NonFatalException
    {
        assertFalse( "Lock status does not match expected",
            settingsLockerImpl.checkSettingsLockStatus( CommonConstants.SET_BY_NONE, OrganizationUnit.COMPANY ) );
    }


    @Test
    public void testCheckSettingsLockStatusWithSetByNoneAsLockNumberAndRegionOrganizationUnit() throws NonFatalException
    {
        assertFalse( "Lock status does not match expected",
            settingsLockerImpl.checkSettingsLockStatus( CommonConstants.SET_BY_NONE, OrganizationUnit.REGION ) );
    }


    @Test
    public void testCheckSettingsLockStatusWithSetByNoneAsLockNumberAndBranchOrganizationUnit() throws NonFatalException
    {
        assertFalse( "Lock status does not match expected",
            settingsLockerImpl.checkSettingsLockStatus( CommonConstants.SET_BY_NONE, OrganizationUnit.BRANCH ) );
    }


    @Test
    public void testCheckSettingsLockStatusWithSetByNoneAsLockNumberAndAgentOrganizationUnit() throws NonFatalException
    {
        assertFalse( "Lock status does not match expected",
            settingsLockerImpl.checkSettingsLockStatus( CommonConstants.SET_BY_NONE, OrganizationUnit.BRANCH ) );
    }


    @Test
    public void testCheckSettingsLockStatusWithSetByRegionAsLockNumberAndCompanyOrganizationUnit() throws NonFatalException
    {
        assertFalse( "Lock status does not match expected",
            settingsLockerImpl.checkSettingsLockStatus( CommonConstants.SET_BY_REGION, OrganizationUnit.COMPANY ) );
    }


    @Test
    public void testCheckSettingsLockStatusWithSetByBranchAsLockNumberAndCompanyOrganizationUnit() throws NonFatalException
    {
        assertFalse( "Lock status does not match expected",
            settingsLockerImpl.checkSettingsLockStatus( CommonConstants.SET_BY_BRANCH, OrganizationUnit.COMPANY ) );
    }


    @Test
    public void testCheckSettingsLockStatusWithSetByCompanyAsLockNumberAndRegionOrganizationUnit() throws NonFatalException
    {
        assertFalse( "Lock status does not match expected",
            settingsLockerImpl.checkSettingsLockStatus( CommonConstants.SET_BY_COMPANY, OrganizationUnit.REGION ) );
    }


    @Test
    public void testCheckSettingsLockStatusWithSetByBranchAsLockNumberAndRegionOrganizationUnit() throws NonFatalException
    {
        assertFalse( "Lock status does not match expected",
            settingsLockerImpl.checkSettingsLockStatus( CommonConstants.SET_BY_BRANCH, OrganizationUnit.REGION ) );
    }


    @Test
    public void testCheckSettingsLockStatusWithSetByCompanyAsLockNumberAndBranchOrganizationUnit() throws NonFatalException
    {
        assertFalse( "Lock status does not match expected",
            settingsLockerImpl.checkSettingsLockStatus( CommonConstants.SET_BY_COMPANY, OrganizationUnit.BRANCH ) );
    }


    @Test
    public void testCheckSettingsLockStatusWithSetByRegionAsLockNumberAndBranchOrganizationUnit() throws NonFatalException
    {
        assertFalse( "Lock status does not match expected",
            settingsLockerImpl.checkSettingsLockStatus( CommonConstants.SET_BY_REGION, OrganizationUnit.BRANCH ) );
    }


    @Test ( expected = InvalidSettingsStateException.class)
    public void testGetHighestLockerLevelWithInvalidLockNumber() throws InvalidSettingsStateException
    {
        settingsLockerImpl.getHighestLockerLevel( -1 );
    }


    @Test
    public void testGetHighestLockerLevelWithSetByNoneAsLockNumber() throws InvalidSettingsStateException
    {
        assertNull( settingsLockerImpl.getHighestLockerLevel( CommonConstants.SET_BY_NONE ) );
    }


    @Test
    public void testGetHighestLockerLevelWithSetByCompanyAsLockNumber() throws InvalidSettingsStateException
    {
        assertEquals( "Organization Unit does not match expected", OrganizationUnit.COMPANY,
            settingsLockerImpl.getHighestLockerLevel( CommonConstants.SET_BY_COMPANY ) );
    }


    @Test
    public void testGetHighestLockerLevelWithSetByCompanyNRegionAsLockNumber() throws InvalidSettingsStateException
    {
        assertEquals( "Organization Unit does not match expected", OrganizationUnit.COMPANY,
            settingsLockerImpl.getHighestLockerLevel( CommonConstants.SET_BY_COMPANY_N_REGION ) );
    }


    @Test
    public void testGetHighestLockerLevelWithSetByCompanyNBranchAsLockNumber() throws InvalidSettingsStateException
    {
        assertEquals( "Organization Unit does not match expected", OrganizationUnit.COMPANY,
            settingsLockerImpl.getHighestLockerLevel( CommonConstants.SET_BY_COMPANY_N_BRANCH ) );
    }


    @Test
    public void testGetHighestLockerLevelWithSetByCompanyNRegionNBranchAsLockNumber() throws InvalidSettingsStateException
    {
        assertEquals( "Organization Unit does not match expected", OrganizationUnit.COMPANY,
            settingsLockerImpl.getHighestLockerLevel( CommonConstants.SET_BY_COMPANY_N_REGION_N_BRANCH ) );
    }


    @Test
    public void testGetHighestLockerLevelWithSetByRegionAsLockNumber() throws InvalidSettingsStateException
    {
        assertEquals( "Organization Unit does not match expected", OrganizationUnit.REGION,
            settingsLockerImpl.getHighestLockerLevel( CommonConstants.SET_BY_REGION ) );
    }


    @Test
    public void testGetHighestLockerLevelWithSetByRegionNBranchAsLockNumber() throws InvalidSettingsStateException
    {
        assertEquals( "Organization Unit does not match expected", OrganizationUnit.REGION,
            settingsLockerImpl.getHighestLockerLevel( CommonConstants.SET_BY_REGION_N_BRANCH ) );
    }


    @Test
    public void testGetHighestLockerLevelWithSetByBranchAsLockNumber() throws InvalidSettingsStateException
    {
        assertEquals( "Organization Unit does not match expected", OrganizationUnit.BRANCH,
            settingsLockerImpl.getHighestLockerLevel( CommonConstants.SET_BY_BRANCH ) );
    }
}
