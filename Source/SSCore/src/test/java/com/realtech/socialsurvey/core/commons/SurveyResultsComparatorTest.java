package com.realtech.socialsurvey.core.commons;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.realtech.socialsurvey.core.entities.SurveyDetails;


public class SurveyResultsComparatorTest
{
    private SurveyResultsComparator comparator;
    private SurveyDetails surveyA;
    private SurveyDetails surveyB;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {}


    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {}


    @Before
    public void setUp() throws Exception
    {
        comparator = new SurveyResultsComparator();
        surveyA = new SurveyDetails();
        surveyB = new SurveyDetails();
    }


    @After
    public void tearDown() throws Exception
    {}


    @Test
    public void testComparingWithNewerFirstSurvey()
    {
        surveyA.setCreatedOn( new Date( 1000l ) );
        surveyB.setCreatedOn( new Date( 500l ) );
        assertEquals( "Test", -1, comparator.compare( surveyA, surveyB ) );
    }


    @Test
    public void testComparingWithNewerSecondSurvey()
    {
        surveyA.setCreatedOn( new Date( 500l ) );
        surveyB.setCreatedOn( new Date( 1000l ) );
        assertEquals( "Test", 1, comparator.compare( surveyA, surveyB ) );
    }


    @Test
    public void testComparingWithEqualSurveyCreationTimes()
    {
        surveyA.setCreatedOn( new Date( 1000l ) );
        surveyB.setCreatedOn( new Date( 1000l ) );
        assertEquals( "Test", 0, comparator.compare( surveyA, surveyB ) );
    }
}
