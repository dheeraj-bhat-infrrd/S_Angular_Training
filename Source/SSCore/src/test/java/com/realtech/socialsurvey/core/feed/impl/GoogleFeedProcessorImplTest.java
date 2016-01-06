package com.realtech.socialsurvey.core.feed.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.realtech.socialsurvey.core.entities.GooglePlusPost;
import com.realtech.socialsurvey.core.exception.NonFatalException;


public class GoogleFeedProcessorImplTest
{
    @InjectMocks
    private GoogleFeedProcessorImpl googleFeedProcessorImpl;


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


    //Tests for processFeed
    @Test
    public void testProcessFeedPostsEmpty() throws NonFatalException
    {
        assertFalse( "", googleFeedProcessorImpl.processFeed( 0l, new ArrayList<GooglePlusPost>(), null, null ) );
    }


    //Tests for createGooglePlusFeedURL
    @Test
    public void testCreateGooglePlusFeedURL()
    {
        assertEquals(
            "https://www.googleapis.com/plus/v1/people/me/activities/public?access_token=testtoken&fields=nextPageToken,updated,items(id,title,published,url,actor)&maxResults=100",
            googleFeedProcessorImpl.createGooglePlusFeedURL( "testtoken" ) );
    }


    //Tests for convertStringToDate
    @Test ( expected = NonFatalException.class)
    public void testConvertStringToDateForInvalidDateString() throws NonFatalException
    {
        googleFeedProcessorImpl.convertStringToDate( "17-03-1993" );
    }


    @Test
    public void testConvertStringToDateForValidDateString() throws NonFatalException
    {
        Timestamp testTime = new Timestamp( 994228736000l );
        assertEquals( testTime, googleFeedProcessorImpl.convertStringToDate( "2001-07-04T12:08:56.000Z" ) );
    }
}
