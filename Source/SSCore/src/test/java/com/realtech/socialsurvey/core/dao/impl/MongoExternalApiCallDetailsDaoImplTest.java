package com.realtech.socialsurvey.core.dao.impl;

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


public class MongoExternalApiCallDetailsDaoImplTest
{
    @InjectMocks
    private MongoExternalApiCallDetailsDaoImpl mongoExternalApiCallDetailsDaoImpl;

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
    public void testInsertApiCallDetailsForDetailsNull() throws InvalidInputException
    {
        mongoExternalApiCallDetailsDaoImpl.insertApiCallDetails( null );
    }
}