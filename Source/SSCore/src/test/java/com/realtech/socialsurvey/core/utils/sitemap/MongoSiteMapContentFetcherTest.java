package com.realtech.socialsurvey.core.utils.sitemap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.internal.util.reflection.Whitebox;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.ProfileUrlEntity;


public class MongoSiteMapContentFetcherTest
{
    @Spy
    @InjectMocks
    private MongoSiteMapContentFetcher mongoSiteMapContentFetcher;


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


    //Tests for generateLocation
    @Test
    public void generateLocationTestProfileUrlNull()
    {
        Whitebox.setInternalState( mongoSiteMapContentFetcher, "collectionName", "test" );
        Whitebox.setInternalState( mongoSiteMapContentFetcher, "applicationUrl", "test" );
        assertNull( "", mongoSiteMapContentFetcher.generateLocation( null ) );
    }


    @Test
    public void generateLocationTestProfileUrlValid()
    {
        Whitebox.setInternalState( mongoSiteMapContentFetcher, "collectionName",
            MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
        Whitebox.setInternalState( mongoSiteMapContentFetcher, "applicationUrl", "test" );
        assertEquals( "", "testpagestest", mongoSiteMapContentFetcher.generateLocation( "test" ) );
    }


    //Tests for prepareSMEObjects
    @Test
    public void prepareSMEObjectsTestUrlsEmpty()
    {
        Whitebox.setInternalState( mongoSiteMapContentFetcher, "collectionName",
            MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
        Whitebox.setInternalState( mongoSiteMapContentFetcher, "applicationUrl", "test" );
        assertTrue( "", mongoSiteMapContentFetcher.prepareSMEObjects( new ArrayList<ProfileUrlEntity>() ).isEmpty() );
    }


    @Test
    public void prepareSMEObjectsTestUrlsValid()
    {
        Whitebox.setInternalState( mongoSiteMapContentFetcher, "collectionName",
            MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
        Whitebox.setInternalState( mongoSiteMapContentFetcher, "applicationUrl", "test" );
        ProfileUrlEntity url = new ProfileUrlEntity();
        List<ProfileUrlEntity> urls = new ArrayList<ProfileUrlEntity>();
        url.setProfileUrl( "test" );
        urls.add( url );
        Mockito.doReturn( "test" ).when( mongoSiteMapContentFetcher ).generateLocation( Mockito.anyString() );
        assertEquals( "", "test", mongoSiteMapContentFetcher.prepareSMEObjects( urls ).get( CommonConstants.INITIAL_INDEX )
            .getLocation() );
        assertEquals( "", 0.8, mongoSiteMapContentFetcher.prepareSMEObjects( urls ).get( CommonConstants.INITIAL_INDEX )
            .getPriority(), 1 );
    }
}
