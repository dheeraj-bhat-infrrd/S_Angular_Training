package com.realtech.socialsurvey.core.feed.impl;

import static org.junit.Assert.assertFalse;

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
        assertFalse( "", googleFeedProcessorImpl.processFeed( new ArrayList<GooglePlusPost>(), null ) );
    }
}
