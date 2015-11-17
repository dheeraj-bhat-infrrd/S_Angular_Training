package com.realtech.socialsurvey.core.services.authentication.impl;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


public class SimpleCaptchaValidationTest
{

    private SimpleCaptchaValidation simpleCaptchaValidation;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {}


    @AfterClass
    public static void tearDownAfterClass() throws Exception {}


    @Before
    public void setUp() throws Exception
    {
        simpleCaptchaValidation = new SimpleCaptchaValidation();
    }


    @After
    public void tearDown() throws Exception {}


    @Test ( expected = InvalidInputException.class)
    public void testIsCaptchaValid() throws InvalidInputException
    {
        simpleCaptchaValidation.isCaptchaValid( "test", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void testValidateCaptchaWithInvalidCaptchaId() throws InvalidInputException
    {
        simpleCaptchaValidation.validateCaptcha( "test", "test" );
    }


    @Test
    public void testValidateCaptchaWithValidCaptchaIdAndWrongAnswer() throws InvalidInputException
    {
        assertFalse( "Answer to captcha should not be valid", simpleCaptchaValidation.validateCaptcha( "5f907657d021e2f708cef94fd7c000b8", "test" ) );
    }
}
