package com.realtech.socialsurvey.core.dao.impl;

import java.sql.Timestamp;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.realtech.socialsurvey.core.exception.InvalidInputException;


public class MongoSurveyDetailsDaoImplTest
{
    @InjectMocks
    private MongoSurveyDetailsDaoImpl mongoSurveyDetailsDaoImpl;

    @Mock
    private MongoTemplate mongoTemplate;


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


    @Test ( expected = InvalidInputException.class)
    public void getCompletedSurveyCountTestColumnNull() throws InvalidInputException
    {
        mongoSurveyDetailsDaoImpl.getCompletedSurveyCount( null, 10l, new Timestamp( 0 ), new Timestamp( 0 ), false );
    }


    @Test ( expected = InvalidInputException.class)
    public void getCompletedSurveyCountTestColumnEmpty() throws InvalidInputException
    {
        mongoSurveyDetailsDaoImpl.getCompletedSurveyCount( "", 10l, new Timestamp( 0 ), new Timestamp( 0 ), false );
    }


    @Test ( expected = InvalidInputException.class)
    public void getCompletedSurveyCountTestInvalidColumnValue() throws InvalidInputException
    {
        mongoSurveyDetailsDaoImpl.getCompletedSurveyCount( "test", 0l, new Timestamp( 0 ), new Timestamp( 0 ), false );
    }


    @Test ( expected = InvalidInputException.class)
    public void getCompletedSurveyAggregationCountTestAggregateByNull() throws InvalidInputException
    {
        mongoSurveyDetailsDaoImpl
            .getCompletedSurveyAggregationCount( "test", 10l, new Timestamp( 0 ), new Timestamp( 0 ), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void getCompletedSurveyAggregationCountTestAggregateByEmpty() throws InvalidInputException
    {
        mongoSurveyDetailsDaoImpl.getCompletedSurveyAggregationCount( "test", 10l, new Timestamp( 0 ), new Timestamp( 0 ), "" );
    }


    @Test ( expected = InvalidInputException.class)
    public void getClickedSurveyAggregationCountTestAggregateByNull() throws InvalidInputException
    {
        mongoSurveyDetailsDaoImpl.getClickedSurveyAggregationCount( "test", 10l, new Timestamp( 0 ), new Timestamp( 0 ), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void getClickedSurveyAggregationCountTestAggregateByEmpty() throws InvalidInputException
    {
        mongoSurveyDetailsDaoImpl.getClickedSurveyAggregationCount( "test", 10l, new Timestamp( 0 ), new Timestamp( 0 ), "" );
    }


    @Test ( expected = InvalidInputException.class)
    public void getSocialPostsAggregationCountTestAggregateByNull() throws InvalidInputException
    {
        mongoSurveyDetailsDaoImpl.getSocialPostsAggregationCount( "test", 10l, new Timestamp( 0 ), new Timestamp( 0 ), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void getSocialPostsAggregationCountTestAggregateByEmpty() throws InvalidInputException
    {
        mongoSurveyDetailsDaoImpl.getSocialPostsAggregationCount( "test", 10l, new Timestamp( 0 ), new Timestamp( 0 ), "" );
    }
}
