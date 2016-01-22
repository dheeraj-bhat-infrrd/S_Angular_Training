package com.realtech.socialsurvey.core.dao.impl;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.BasicDBObject;
import com.realtech.socialsurvey.TestConstants;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


public class MongoSurveyDetailsDaoImplTest
{
    @Spy
    @InjectMocks
    private MongoSurveyDetailsDaoImpl mongoSurveyDetailsDaoImpl;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private OrganizationUnitSettingsDao organizationUnitSettingsDao;


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


    @Test
    public void testGetFeedBacksCountWithIncludeZillowReviewsAsTrueAndNotRecommendedTrue()
    {
        Mockito.when( mongoTemplate.count( (Query) Mockito.any(), Mockito.anyString() ) ).thenReturn( 10l );
        long count = mongoSurveyDetailsDaoImpl.getFeedBacksCount( TestConstants.TEST_STRING, 2, 3.5, 3.5, false, true, true, 2 );
        assertEquals( "FeedBack count does not match expected", 10, count );
    }


    @Test
    public void testGetFeedBacksCountWithIncludeZillowReviewsAsTrueAndNotRecommendedFalse()
    {
        Mockito.when( mongoTemplate.count( (Query) Mockito.any(), Mockito.anyString() ) ).thenReturn( 10l );
        long count = mongoSurveyDetailsDaoImpl.getFeedBacksCount( CommonConstants.COMPANY_ID_COLUMN, 2, 3.5, 3.5, false, false,
            true, 2 );
        assertEquals( "FeedBack count does not match expected", 12, count );
    }


    @Test
    public void testGetFeedBacksCountWithIncludeZillowReviewsAsFalseAndNotRecommendedFalse()
    {
        Mockito.when( mongoTemplate.count( (Query) Mockito.any(), Mockito.anyString() ) ).thenReturn( 10l );
        long count = mongoSurveyDetailsDaoImpl.getFeedBacksCount( CommonConstants.COMPANY_ID_COLUMN, 2, 3.5, 3.5, false, false,
            false, 2 );
        assertEquals( "FeedBack count does not match expected", 10, count );
    }


    @Test
    public void testGetFeedBacksCountWithIncludeZillowReviewsAsFalseAndNotRecommendedTrue()
    {
        Mockito.when( mongoTemplate.count( (Query) Mockito.any(), Mockito.anyString() ) ).thenReturn( 2l );
        long count = mongoSurveyDetailsDaoImpl.getFeedBacksCount( CommonConstants.COMPANY_ID_COLUMN, 2, 0, 3.0, false, true,
            false, 2 );
        assertEquals( "FeedBack count does not match expected", 2, count );
    }


    @SuppressWarnings ( "unchecked")
    @Test
    public void testGetRatingForPastNdaysWithIncludeZillowReviewsAsTrueWhenReviewCountIsZero()
    {
        Mockito.when(
            mongoTemplate.aggregate( (TypedAggregation<SurveyDetails>) Mockito.any(), Mockito.anyString(),
                Mockito.eq( SurveyDetails.class ) ) ).thenReturn(
            new AggregationResults<SurveyDetails>( new ArrayList<SurveyDetails>(), new BasicDBObject() ) );
        Mockito.when( mongoTemplate.count( (Query) Mockito.any(), Mockito.anyString() ) ).thenReturn( 0l );
        double average = mongoSurveyDetailsDaoImpl.getRatingForPastNdays( CommonConstants.COMPANY_ID_COLUMN, 2, 90, false,
            false, true, 10, 40 );
        assertEquals( "Average does not match expected", 4, average, 0 );
    }


    @SuppressWarnings ( "unchecked")
    @Test
    public void testGetRatingForPastNdaysWithIncludeZillowReviewsAsTrueWhenResultsIsNull()
    {
        Mockito.when(
            mongoTemplate.aggregate( (TypedAggregation<SurveyDetails>) Mockito.any(), Mockito.anyString(),
                Mockito.eq( SurveyDetails.class ) ) ).thenReturn( null );
        Mockito.when( mongoTemplate.count( (Query) Mockito.any(), Mockito.anyString() ) ).thenReturn( 10l );
        double average = mongoSurveyDetailsDaoImpl.getRatingForPastNdays( CommonConstants.COMPANY_ID_COLUMN, 2, 90, false,
            false, true, 10, 40 );
        assertEquals( "Average does not match expected", 4, average, 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDeleteIncompleteSurveysForAgent() throws InvalidInputException
    {
        mongoSurveyDetailsDaoImpl.deleteIncompleteSurveysForAgent( 0l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateAgentInfoInSurveysWithInvalidAgentId() throws InvalidInputException
    {
        mongoSurveyDetailsDaoImpl.updateAgentInfoInSurveys( 0l, new User(), new UserProfile() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateAgentInfoInSurveysWithNullToUser() throws InvalidInputException
    {
        mongoSurveyDetailsDaoImpl.updateAgentInfoInSurveys( 1l, null, new UserProfile() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateAgentInfoInSurveysWithNullUserProfile() throws InvalidInputException
    {
        mongoSurveyDetailsDaoImpl.updateAgentInfoInSurveys( 1l, new User(), null );
    }
}
