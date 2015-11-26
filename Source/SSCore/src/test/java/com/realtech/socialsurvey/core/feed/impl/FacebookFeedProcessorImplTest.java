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

import com.realtech.socialsurvey.core.exception.NonFatalException;

import facebook4j.Post;


public class FacebookFeedProcessorImplTest
{
    @InjectMocks
    private FacebookFeedProcessorImpl facebookFeedProcessorImpl;


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
    public void testProcessFeedPostsNull() throws NonFatalException
    {
        assertFalse( "", facebookFeedProcessorImpl.processFeed( null, null ) );
    }


    @Test
    public void testProcessFeedPostsEmpty() throws NonFatalException
    {
        assertFalse( "", facebookFeedProcessorImpl.processFeed( new ArrayList<Post>(), null ) );
    }
}
