package com.realtech.socialsurvey.core.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.realtech.socialsurvey.core.entities.SocialPostCompanyIdMapping;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


public class MongoSocialPostDaoImplTest
{
    @InjectMocks
    private MongoSocialPostDaoImpl mongoSocialPostDaoImpl;

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
    public void updateCompanyIdForSocialPostsTestEntityTypeNull() throws InvalidInputException
    {
        List<SocialPostCompanyIdMapping> testList = new ArrayList<SocialPostCompanyIdMapping>();
        SocialPostCompanyIdMapping socialPostCompanyIdMapping = new SocialPostCompanyIdMapping();
        testList.add( socialPostCompanyIdMapping );
        mongoSocialPostDaoImpl.updateCompanyIdForSocialPosts( testList );
    }


    @Test ( expected = InvalidInputException.class)
    public void updateCompanyIdForSocialPostsTestEntityTypeEmpty() throws InvalidInputException
    {
        List<SocialPostCompanyIdMapping> testList = new ArrayList<SocialPostCompanyIdMapping>();
        SocialPostCompanyIdMapping socialPostCompanyIdMapping = new SocialPostCompanyIdMapping();
        socialPostCompanyIdMapping.setEntityType( "" );
        testList.add( socialPostCompanyIdMapping );
        mongoSocialPostDaoImpl.updateCompanyIdForSocialPosts( testList );
    }


    @Test ( expected = InvalidInputException.class)
    public void updateCompanyIdForSocialPostsTestEntityTypeInvalid() throws InvalidInputException
    {
        List<SocialPostCompanyIdMapping> testList = new ArrayList<SocialPostCompanyIdMapping>();
        SocialPostCompanyIdMapping socialPostCompanyIdMapping = new SocialPostCompanyIdMapping();
        socialPostCompanyIdMapping.setEntityType( "test" );
        testList.add( socialPostCompanyIdMapping );
        mongoSocialPostDaoImpl.updateCompanyIdForSocialPosts( testList );
    }
}
