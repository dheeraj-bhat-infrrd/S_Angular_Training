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

import twitter4j.Status;

import com.realtech.socialsurvey.core.exception.NonFatalException;


public class TwitterFeedProcessorImplTest
{
    @InjectMocks
    private TwitterFeedProcessorImpl twitterFeedProcessorImpl;


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
    public void testProcessFeedPostsEmpty() throws NonFatalException
    {
        assertFalse( "", twitterFeedProcessorImpl.processFeed( 0l, new ArrayList<Status>(), null, null ) );
    }
}
