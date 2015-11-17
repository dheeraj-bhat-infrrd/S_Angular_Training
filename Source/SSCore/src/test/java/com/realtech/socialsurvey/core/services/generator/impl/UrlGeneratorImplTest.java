package com.realtech.socialsurvey.core.services.generator.impl;

import java.util.HashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.generator.InvalidUrlException;


public class UrlGeneratorImplTest
{
    private UrlGeneratorImpl urlGeneratorImpl;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {}


    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {}


    @Before
    public void setUp() throws Exception
    {
        urlGeneratorImpl = new UrlGeneratorImpl();
    }


    @After
    public void tearDown() throws Exception
    {}


    @Test ( expected = InvalidInputException.class)
    public void generateCipherTestParamsNull() throws InvalidInputException
    {
        urlGeneratorImpl.generateCipher( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void generateUrlTestParamsNull() throws InvalidInputException
    {
        urlGeneratorImpl.generateUrl( null, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void generateUrlTestBaseUrlNull() throws InvalidInputException
    {
        urlGeneratorImpl.generateUrl( new HashMap<String, String>(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void generateUrlTestBaseUrlEmpty() throws InvalidInputException
    {
        urlGeneratorImpl.generateUrl( new HashMap<String, String>(), "" );
    }


    @Test ( expected = InvalidInputException.class)
    public void decryptCipherTestCipherNull() throws InvalidInputException
    {
        urlGeneratorImpl.decryptCipher( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void decryptCipherTestCipherEmpty() throws InvalidInputException
    {
        urlGeneratorImpl.decryptCipher( "" );
    }


    @Test ( expected = InvalidInputException.class)
    public void decryptParametersTestParameterNull() throws InvalidInputException
    {
        urlGeneratorImpl.decryptParameters( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void decryptParametersTestParameterEmpty() throws InvalidInputException
    {
        urlGeneratorImpl.decryptParameters( "" );
    }


    @Test ( expected = InvalidInputException.class)
    public void decryptUrlTestUrlNull() throws InvalidInputException, InvalidUrlException
    {
        urlGeneratorImpl.decryptUrl( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void decryptUrlTestUrlEmpty() throws InvalidInputException, InvalidUrlException
    {
        urlGeneratorImpl.decryptUrl( "" );
    }


    @Test ( expected = InvalidUrlException.class)
    public void decryptUrlTestUrlInvalid() throws InvalidInputException, InvalidUrlException
    {
        urlGeneratorImpl.decryptUrl( "test" );
    }
}
