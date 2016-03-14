package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

public class ZillowUpdateServiceImplTest
{

    @Spy
    @InjectMocks
    private ZillowUpdateServiceImpl zillowUpdateServiceImpl;

    @Mock
    private ProfileManagementServiceImpl profileManagementServiceImpl;

  
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {}


    @AfterClass
    public static void tearDownAfterClass() throws Exception {}


    @Before
    public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks( this );
    }


    @After
    public void tearDown() throws Exception {}


    @Test ( expected = InvalidInputException.class)
    public void testPushZillowReviewsWithNullReviewsMap() throws InvalidInputException
    {
        zillowUpdateServiceImpl.pushZillowReviews( null, null, null, 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testPushZillowReviewsWithEmptyReviewsMap() throws InvalidInputException
    {
        zillowUpdateServiceImpl.pushZillowReviews( new ArrayList<HashMap<String, Object>>(), null, null, 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testPushZillowReviewsWithNullCollectionName() throws InvalidInputException
    {
        List<HashMap<String, Object>> reviews = new ArrayList<HashMap<String, Object>>();
        reviews.add( new HashMap<String, Object>() );
        zillowUpdateServiceImpl.pushZillowReviews( reviews, null, null, 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testPushZillowReviewsWithEmptyCollectionName() throws InvalidInputException
    {
        List<HashMap<String, Object>> reviews = new ArrayList<HashMap<String, Object>>();
        reviews.add( new HashMap<String, Object>() );
        zillowUpdateServiceImpl.pushZillowReviews( reviews, "", null, 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testPushZillowReviewsWithNullProfileSettings() throws InvalidInputException
    {
        List<HashMap<String, Object>> reviews = new ArrayList<HashMap<String, Object>>();
        reviews.add( new HashMap<String, Object>() );
        zillowUpdateServiceImpl.pushZillowReviews( reviews, "test", null, 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testPushZillowReviewsWithInvalidCompanyId() throws InvalidInputException
    {
        List<HashMap<String, Object>> reviews = new ArrayList<HashMap<String, Object>>();
        reviews.add( new HashMap<String, Object>() );
        zillowUpdateServiceImpl.pushZillowReviews( reviews, "test", new OrganizationUnitSettings(), 0 );
    }
}
