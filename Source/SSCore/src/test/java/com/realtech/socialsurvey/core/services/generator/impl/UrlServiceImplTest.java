package com.realtech.socialsurvey.core.services.generator.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


public class UrlServiceImplTest
{
    private UrlServiceImpl urlServiceImpl;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {}


    @AfterClass
    public static void tearDownAfterClass() throws Exception {}


    @Before
    public void setUp() throws Exception {
        urlServiceImpl = new UrlServiceImpl();
    }


    @After
    public void tearDown() throws Exception {}


    @Test ( expected = InvalidInputException.class)
    public void testShortenUrlWithNullURLString() throws InvalidInputException {
        urlServiceImpl.shortenUrl( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testShortenUrlWithEmptyURLString() throws InvalidInputException {
        urlServiceImpl.shortenUrl( "" );
    }


    @Test ( expected = InvalidInputException.class)
    public void testRetrieveCompleteUrlForIDWithNullURLString() throws InvalidInputException {
        urlServiceImpl.retrieveCompleteUrlForID( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testRetrieveCompleteUrlForIDWithEmptyURLString() throws InvalidInputException {
        urlServiceImpl.retrieveCompleteUrlForID( "" );
    }
    
    @Test
    public void testGetUrlTypeManualRegistration() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
    	Method getUrlTypeMethod = UrlServiceImpl.class.getDeclaredMethod("getUrlType", String.class);
    	getUrlTypeMethod.setAccessible(true);
    	String actualUrl = (String)getUrlTypeMethod.invoke(urlServiceImpl, "url/invitetoregister.do");
    	String expected = CommonConstants.MANUAL_REGISTRATION_URL_TYPE;
    	Assert.assertEquals("Manual registration",expected, actualUrl);
    }
}
