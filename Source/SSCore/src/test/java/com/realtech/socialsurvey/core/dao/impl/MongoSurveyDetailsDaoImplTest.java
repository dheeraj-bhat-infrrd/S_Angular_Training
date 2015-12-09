package com.realtech.socialsurvey.core.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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
import org.springframework.data.mongodb.core.query.Query;
import com.mongodb.BasicDBObject;
import com.realtech.socialsurvey.TestConstants;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;


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
    public void testGetOrganizationUnitSettingsByColumnNameAndIdWithNullColumnName()
    {
        assertNull( mongoSurveyDetailsDaoImpl.getOrganizationUnitSettingsByColumnNameAndId( null, 1 ) );
    }


    @Test
    public void testGetOrganizationUnitSettingsByColumnNameAndIdWithEmptyColumnName()
    {
        assertNull( mongoSurveyDetailsDaoImpl.getOrganizationUnitSettingsByColumnNameAndId( TestConstants.TEST_EMPTY_STRING, 1 ) );
    }


    @Test
    public void testGetOrganizationUnitSettingsByColumnNameAndIdWithInvalidId()
    {
        assertNull( mongoSurveyDetailsDaoImpl.getOrganizationUnitSettingsByColumnNameAndId( TestConstants.TEST_STRING, 0 ) );
    }


    @Test
    public void testGetOrganizationUnitSettingsByColumnNameAndIdWithInvalidColumnName()
    {
        assertNull( mongoSurveyDetailsDaoImpl.getOrganizationUnitSettingsByColumnNameAndId( TestConstants.TEST_STRING, 1 ) );
    }


    @Test
    public void testGetZillowReviewCountBasedOnColumnNameAndIdAndIdWheOrganiztionUnitSettingsIsNull()
    {
        Mockito.doReturn( null ).when( mongoSurveyDetailsDaoImpl )
            .getOrganizationUnitSettingsByColumnNameAndId( TestConstants.TEST_STRING, 1 );
        assertEquals( "Zillow review count does not match expected", 0,
            mongoSurveyDetailsDaoImpl.getZillowReviewCountBasedOnColumnNameAndId( TestConstants.TEST_STRING, 1 ) );
    }


    @Test
    public void testGetZillowReviewAverageBasedOnColumnNameAndIdAndIdWheOrganiztionUnitSettingsIsNull()
    {
        Mockito.doReturn( null ).when( mongoSurveyDetailsDaoImpl )
            .getOrganizationUnitSettingsByColumnNameAndId( TestConstants.TEST_STRING, 1 );
        assertEquals( "Zillow review average does not match expected", 0,
            mongoSurveyDetailsDaoImpl.getZillowReviewAverageBasedOnColumnNameAndId( TestConstants.TEST_STRING, 1 ) );
    }


    @Test
    public void testGetFeedBacksCountWithIncludeZillowReviewsAsTrueAndNotRecommendedTrue()
    {
        Mockito.when( mongoTemplate.count( (Query) Mockito.any(), Mockito.anyString() ) ).thenReturn( 10l );
        long count = mongoSurveyDetailsDaoImpl.getFeedBacksCount( TestConstants.TEST_STRING, 2, 3.5, 3.5, false, true, true );
        assertEquals( "FeedBack count does not match expected", 10, count );
    }


    @Test
    public void testGetFeedBacksCountWithIncludeZillowReviewsAsTrueAndNotRecommendedFalse()
    {
        OrganizationUnitSettings organizationUnitSettings = new OrganizationUnitSettings();
        organizationUnitSettings.setZillowReviewCount( 2 );
        Mockito.when( organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( Mockito.anyLong(), Mockito.anyString() ) )
            .thenReturn( organizationUnitSettings );
        Mockito.when( mongoTemplate.count( (Query) Mockito.any(), Mockito.anyString() ) ).thenReturn( 10l );
        Mockito.doReturn( organizationUnitSettings ).when( mongoSurveyDetailsDaoImpl )
            .getOrganizationUnitSettingsByColumnNameAndId( CommonConstants.COMPANY_ID_COLUMN, 1 );
        Mockito.doReturn( 2l ).when( mongoSurveyDetailsDaoImpl )
            .getZillowReviewCountBasedOnColumnNameAndId( CommonConstants.COMPANY_ID_COLUMN, 1 );
        long count = mongoSurveyDetailsDaoImpl.getFeedBacksCount( CommonConstants.COMPANY_ID_COLUMN, 2, 3.5, 3.5, false, false,
            true );
        assertEquals( "FeedBack count does not match expected", 12, count );
    }


    @Test
    public void testGetFeedBacksCountWithIncludeZillowReviewsAsFalseAndNotRecommendedFalse()
    {
        Mockito.when( mongoTemplate.count( (Query) Mockito.any(), Mockito.anyString() ) ).thenReturn( 10l );
        long count = mongoSurveyDetailsDaoImpl.getFeedBacksCount( CommonConstants.COMPANY_ID_COLUMN, 2, 3.5, 3.5, false, false,
            false );
        assertEquals( "FeedBack count does not match expected", 10, count );
    }


    @Test
    public void testGetFeedBacksCountWithIncludeZillowReviewsAsFalseAndNotRecommendedTrue()
    {
        Mockito.when( mongoTemplate.count( (Query) Mockito.any(), Mockito.anyString() ) ).thenReturn( 2l );
        long count = mongoSurveyDetailsDaoImpl.getFeedBacksCount( CommonConstants.COMPANY_ID_COLUMN, 2, 0, 3.0, false, true,
            false );
        assertEquals( "FeedBack count does not match expected", 2, count );
    }


    @Test
    public void testGetRatingForPastNdaysWithIncludeZillowReviewsAsTrueWhenReviewCountIsZero()
    {
        OrganizationUnitSettings organizationUnitSettings = new OrganizationUnitSettings();
        organizationUnitSettings.setZillowReviewCount( 10 );
        organizationUnitSettings.setZillowReviewAverage( 4 );
        Mockito.when(
            mongoTemplate.aggregate( (TypedAggregation<SurveyDetails>) Mockito.any(), Mockito.anyString(),
                Mockito.eq( SurveyDetails.class ) ) ).thenReturn(
            new AggregationResults<SurveyDetails>( new ArrayList<SurveyDetails>(), new BasicDBObject() ) );
        Mockito.when( mongoTemplate.count( (Query) Mockito.any(), Mockito.anyString() ) ).thenReturn( 0l );
        Mockito.when( organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( Mockito.anyLong(), Mockito.anyString() ) )
            .thenReturn( organizationUnitSettings );
        Mockito.doReturn( organizationUnitSettings ).when( mongoSurveyDetailsDaoImpl )
            .getOrganizationUnitSettingsByColumnNameAndId( CommonConstants.COMPANY_ID_COLUMN, 1 );
        Mockito.doReturn( 10l ).when( mongoSurveyDetailsDaoImpl )
            .getZillowReviewCountBasedOnColumnNameAndId( CommonConstants.COMPANY_ID_COLUMN, 1 );
        Mockito.doReturn( 4l ).when( mongoSurveyDetailsDaoImpl )
            .getZillowReviewAverageBasedOnColumnNameAndId( CommonConstants.COMPANY_ID_COLUMN, 1 );
        double average = mongoSurveyDetailsDaoImpl.getRatingForPastNdays( CommonConstants.COMPANY_ID_COLUMN, 2, 90, false,
            false, true );
        assertEquals( "Average does not match expected", 4, average, 0 );
    }


    @Test
    public void testGetRatingForPastNdaysWithIncludeZillowReviewsAsTrueWhenResultsIsNull()
    {
        OrganizationUnitSettings organizationUnitSettings = new OrganizationUnitSettings();
        organizationUnitSettings.setZillowReviewCount( 10 );
        organizationUnitSettings.setZillowReviewAverage( 4 );
        Mockito.when(
            mongoTemplate.aggregate( (TypedAggregation<SurveyDetails>) Mockito.any(), Mockito.anyString(),
                Mockito.eq( SurveyDetails.class ) ) ).thenReturn( null );
        Mockito.when( mongoTemplate.count( (Query) Mockito.any(), Mockito.anyString() ) ).thenReturn( 10l );
        Mockito.when( organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( Mockito.anyLong(), Mockito.anyString() ) )
            .thenReturn( organizationUnitSettings );
        Mockito.doReturn( organizationUnitSettings ).when( mongoSurveyDetailsDaoImpl )
            .getOrganizationUnitSettingsByColumnNameAndId( CommonConstants.COMPANY_ID_COLUMN, 1 );
        Mockito.doReturn( 10l ).when( mongoSurveyDetailsDaoImpl )
            .getZillowReviewCountBasedOnColumnNameAndId( CommonConstants.COMPANY_ID_COLUMN, 1 );
        Mockito.doReturn( 40l ).when( mongoSurveyDetailsDaoImpl )
            .getZillowReviewAverageBasedOnColumnNameAndId( CommonConstants.COMPANY_ID_COLUMN, 1 );
        double average = mongoSurveyDetailsDaoImpl.getRatingForPastNdays( CommonConstants.COMPANY_ID_COLUMN, 2, 90, false,
            false, true );
        assertEquals( "Average does not match expected", 4, average, 0 );
    }
}
