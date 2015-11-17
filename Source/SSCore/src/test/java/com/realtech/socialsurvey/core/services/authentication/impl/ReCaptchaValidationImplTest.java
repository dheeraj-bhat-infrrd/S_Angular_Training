package com.realtech.socialsurvey.core.services.authentication.impl;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.realtech.socialsurvey.core.exception.InvalidInputException;


public class ReCaptchaValidationImplTest
{
    private ReCaptchaValidationImpl reCaptchaValidationImpl;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {}


    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {}


    @Before
    public void setUp() throws Exception
    {
        reCaptchaValidationImpl = new ReCaptchaValidationImpl();
    }


    @After
    public void tearDown() throws Exception
    {}


    @Test
    public void presentCaptchaQuestionTest()
    {
        assertEquals( "Test", null, reCaptchaValidationImpl.presentCaptchaQuestion( "test" ) );
    }


    @Test ( expected = InvalidInputException.class)
    public void validateCaptchaTest() throws InvalidInputException
    {
        reCaptchaValidationImpl.validateCaptcha( "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void isCaptchaValidTestNullRemoteAddress() throws InvalidInputException
    {
        reCaptchaValidationImpl.isCaptchaValid( null, "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void isCaptchaValidTestEmptyRemoteAddress() throws InvalidInputException
    {
        reCaptchaValidationImpl.isCaptchaValid( "", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void isCaptchaValidTestNullChallenge() throws InvalidInputException
    {
        reCaptchaValidationImpl.isCaptchaValid( "test", null, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void isCaptchaValidTestEmptyChallenge() throws InvalidInputException
    {
        reCaptchaValidationImpl.isCaptchaValid( "test", "", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void isCaptchaValidTestNullResponse() throws InvalidInputException
    {
        reCaptchaValidationImpl.isCaptchaValid( "test", "test", null );
    }


    @Test ( expected = InvalidInputException.class)
    public void isCaptchaValidTestEmptyResponse() throws InvalidInputException
    {
        reCaptchaValidationImpl.isCaptchaValid( "test", "test", "" );
    }
}
