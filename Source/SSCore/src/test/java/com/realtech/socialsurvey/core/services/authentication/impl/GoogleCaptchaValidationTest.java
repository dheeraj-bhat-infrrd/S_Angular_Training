package com.realtech.socialsurvey.core.services.authentication.impl;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.realtech.socialsurvey.core.exception.InvalidInputException;


public class GoogleCaptchaValidationTest
{
    private GoogleCaptchaValidation googleCaptchaValidation;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {}


    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {}


    @Before
    public void setUp() throws Exception
    {
        googleCaptchaValidation = new GoogleCaptchaValidation();
    }


    @After
    public void tearDown() throws Exception
    {}


    @Test
    public void presentCaptchaQuestionTest()
    {
        assertEquals( "Test", null, googleCaptchaValidation.presentCaptchaQuestion( "test" ) );
    }


    @Test ( expected = InvalidInputException.class)
    public void validateCaptchaTest() throws InvalidInputException
    {
        googleCaptchaValidation.validateCaptcha( "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void isCaptchaValidTestNullRemoteAddress() throws InvalidInputException
    {
        googleCaptchaValidation.isCaptchaValid( null, "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void isCaptchaValidTestEmptyRemoteAddress() throws InvalidInputException
    {
        googleCaptchaValidation.isCaptchaValid( "", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void isCaptchaValidTestNullSecret() throws InvalidInputException
    {
        googleCaptchaValidation.isCaptchaValid( "test", null, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void isCaptchaValidTestEmptySecret() throws InvalidInputException
    {
        googleCaptchaValidation.isCaptchaValid( "test", "", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void isCaptchaValidTestNullResponse() throws InvalidInputException
    {
        googleCaptchaValidation.isCaptchaValid( "test", "test", null );
    }


    @Test ( expected = InvalidInputException.class)
    public void isCaptchaValidTestEmptyResponse() throws InvalidInputException
    {
        googleCaptchaValidation.isCaptchaValid( "test", "test", "" );
    }
}
