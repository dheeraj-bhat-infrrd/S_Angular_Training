package com.realtech.socialsurvey.core.feed.impl;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import com.realtech.socialsurvey.core.exception.NoContextFoundException;


public class SocialFeedExecutorsTest
{
    @InjectMocks
    private SocialFeedExecutors socialFeedExecutors;


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


    @Test ( expected = NoContextFoundException.class)
    public void testAddFacebookProcessorToPoolContextNull()
    {
        Whitebox.setInternalState( socialFeedExecutors, "context", null );
        socialFeedExecutors.addFacebookProcessorToPool( null, null );
    }


    @Test ( expected = NoContextFoundException.class)
    public void testAddGoogleProcessorToPoolContextNull()
    {
        Whitebox.setInternalState( socialFeedExecutors, "context", null );
        socialFeedExecutors.addGoogleProcessorToPool( null, null );
    }


    @Test ( expected = NoContextFoundException.class)
    public void testAddTwitterProcessorToPoolContextNull()
    {
        Whitebox.setInternalState( socialFeedExecutors, "context", null );
        socialFeedExecutors.addTwitterProcessorToPool( null, null );
    }
}
