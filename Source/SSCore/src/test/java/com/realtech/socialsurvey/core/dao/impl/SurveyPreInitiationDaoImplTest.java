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


public class SurveyPreInitiationDaoImplTest
{
    @InjectMocks
    private SurveyPreInitiationDaoImpl surveyPreInitiationDaoImpl;

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
    public void getSocialPostsAggregationCountTestAggregateByNull() throws InvalidInputException
    {
        surveyPreInitiationDaoImpl.getIncompletSurveyAggregationCount( 1, 10l, 1, new Timestamp( 0 ), new Timestamp( 0 ), null,
            null );
    }


    @Test ( expected = InvalidInputException.class)
    public void getSocialPostsAggregationCountTestAggregateByEmpty() throws InvalidInputException
    {
        surveyPreInitiationDaoImpl.getIncompletSurveyAggregationCount( 1, 10l, 1, new Timestamp( 0 ), new Timestamp( 0 ), null,
            "" );
    }
}
