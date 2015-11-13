package com.realtech.socialsurvey.core.commons;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.realtech.socialsurvey.core.entities.SocialPost;


public class SocialPostsComparatorTest
{
    private SocialPostsComparator comparator;
    private SocialPost postA;
    private SocialPost postB;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {}


    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {}


    @Before
    public void setUp() throws Exception
    {
        comparator = new SocialPostsComparator();
        postA = new SocialPost();
        postB = new SocialPost();
    }


    @After
    public void tearDown() throws Exception
    {}


    @Test
    public void testComparingWithNewerFirstPost()
    {
        postA.setTimeInMillis( 1000l );
        postB.setTimeInMillis( 500l );
        assertEquals( "Test", -1, comparator.compare( postA, postB ) );
    }


    @Test
    public void testComparingWithNewerSecondPost()
    {
        postA.setTimeInMillis( 500l );
        postB.setTimeInMillis( 1000l );
        assertEquals( "Test", 1, comparator.compare( postA, postB ) );
    }


    @Test
    public void testComparingWithEqualSocialPosts()
    {
        postA.setTimeInMillis( 1000l );
        postB.setTimeInMillis( 1000l );
        assertEquals( "Test", 0, comparator.compare( postA, postB ) );
    }
}
