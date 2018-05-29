package com.realtech.socialsurvey.core.services.settingsmanagement.impl;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
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

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.SettingsDetails;
import com.realtech.socialsurvey.core.enums.OrganizationUnit;
import com.realtech.socialsurvey.core.enums.SettingsForApplication;
import com.realtech.socialsurvey.core.services.settingsmanagement.SettingsLocker;
import com.realtech.socialsurvey.core.services.settingsmanagement.SettingsSetter;


public class SettingsManagerImplTest
{
    @InjectMocks
    private SettingsManagerImpl settingsManagerImpl;

    @Mock
    private SettingsSetter settingsSetter;

    @Mock
    private SettingsLocker settingsLocker;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {}


    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {}


    @Before
    public void setUp() throws Exception
    {
        settingsManagerImpl = new SettingsManagerImpl();
        MockitoAnnotations.initMocks( this );
    }


    @After
    public void tearDown() throws Exception
    {}


    //Tests for calculateSettingsScore
    /*@Test
    public void calculateSettingsScore()
    {
        List<SettingsDetails> settingsDetailsList = new ArrayList<SettingsDetails>();
        SettingsDetails settingsDetails1 = new SettingsDetails();
        SettingsDetails settingsDetails2 = new SettingsDetails();
        SettingsDetails settingsDetails3 = new SettingsDetails();
        SettingsDetails settingsDetails4 = new SettingsDetails();
        settingsDetails1.setLockSettingsHolder( 0l );
        settingsDetails1.setSetSettingsHolder( BigInteger.valueOf(0) );
        settingsDetailsList.add( settingsDetails1 );
        settingsDetails2.setLockSettingsHolder( 1l );
        settingsDetails2.setSetSettingsHolder( BigInteger.valueOf(1) );
        settingsDetailsList.add( settingsDetails2 );
        settingsDetails3.setLockSettingsHolder( 2l );
        settingsDetails3.setSetSettingsHolder( BigInteger.valueOf(2) );
        settingsDetailsList.add( settingsDetails3 );
        settingsDetails4.setLockSettingsHolder( 4l );
        settingsDetails4.setSetSettingsHolder( BigInteger.valueOf(4) );
        settingsDetailsList.add( settingsDetails4 );
        Map<String, BigInteger> resultMap = settingsManagerImpl.calculateSettingsScore( settingsDetailsList );
        assertEquals( "Result is not set by company, region and branch", (Double) 7.0,
            resultMap.get( CommonConstants.SETTING_SCORE ) );
        assertEquals( "Result is not locked by company, region and branch", (Double) 7.0,
            resultMap.get( CommonConstants.LOCK_SCORE ) );

    }*/


    //Tests for getClosestSettingLevel
    @Test
    public void getClosestSettingLevelTestLockedByCompanySetByCompanyNRegion() throws InvalidSettingsStateException
    {
        Mockito.when( settingsLocker.getHighestLockerLevel( Mockito.eq( 1 ) ) ).thenReturn( OrganizationUnit.COMPANY );
        Map<SettingsForApplication, OrganizationUnit> resultMap = settingsManagerImpl.getClosestSettingLevel( "3", "1" );
        assertEquals( "ClosestSettingLevel incorrect", OrganizationUnit.COMPANY, resultMap.get( SettingsForApplication.LOGO ) );
    }
}
