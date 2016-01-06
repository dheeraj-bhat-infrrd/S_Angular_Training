package com.realtech.socialsurvey.core.utils;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.realtech.socialsurvey.core.exception.InvalidInputException;


public class EmailFormatHelperTest
{

    private EmailFormatHelper emailFormatHelper;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {}


    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {}


    @Before
    public void setUp() throws Exception
    {
        emailFormatHelper = new EmailFormatHelper();
    }


    @After
    public void tearDown() throws Exception
    {}


    @Test
    public void testCustomerDisplayNameForEmailWithFirstAndLastName() throws InvalidInputException
    {
        assertEquals( "test", "Nishit K.", emailFormatHelper.getCustomerDisplayNameForEmail( "Nishit", "Kannan" ) );
    }


    @Test
    public void testCustomerDisplayNameForEmailWithFirstName() throws InvalidInputException
    {
        assertEquals( "test", "Nishit", emailFormatHelper.getCustomerDisplayNameForEmail( "Nishit", null ) );
    }


    @Test ( expected = InvalidInputException.class)
    public void testCustomerDisplayNameForEmailWithNullFirstName() throws InvalidInputException
    {
        emailFormatHelper.getCustomerDisplayNameForEmail( null, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void testCustomerDisplayNameForEmailWithEmptyFirstName() throws InvalidInputException
    {
        emailFormatHelper.getCustomerDisplayNameForEmail( "", "test" );
    }

}
