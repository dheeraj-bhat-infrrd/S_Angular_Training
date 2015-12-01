package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import com.realtech.socialsurvey.TestConstants;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

public class SurveyPreInitiationServiceImplTest
{

    @InjectMocks
    private SurveyPreInitiationServiceImpl surveyPreInitiationServiceImpl;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {}


    @AfterClass
    public static void tearDownAfterClass() throws Exception {}


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks( this );
    }


    @After
    public void tearDown() throws Exception {}


    @Test ( expected = InvalidInputException.class)
    public void testGetIncompleteSurveyWithInvalidIden() throws InvalidInputException
    {
        surveyPreInitiationServiceImpl.getIncompleteSurvey( 0, TestConstants.TEST_INT, TestConstants.TEST_INT,
            TestConstants.TEST_INT, TestConstants.TEST_INT, CommonConstants.PROFILE_LEVEL_COMPANY, null, null, false );
    }
}
