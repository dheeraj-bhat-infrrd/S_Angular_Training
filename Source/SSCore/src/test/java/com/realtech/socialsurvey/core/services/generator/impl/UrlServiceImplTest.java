package com.realtech.socialsurvey.core.services.generator.impl;

import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.internal.util.reflection.Whitebox;
import com.realtech.socialsurvey.core.dao.UrlDetailsDao;
import com.realtech.socialsurvey.core.entities.UrlDetails;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.utils.EncryptionHelper;


public class UrlServiceImplTest
{
	@Spy
	@InjectMocks
    private UrlServiceImpl urlServiceImpl;
	
	@Mock
	private UrlDetailsDao urlDetailsDao;
	
	@Mock
	private EncryptionHelper encryptionHelper;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {}


    @AfterClass
    public static void tearDownAfterClass() throws Exception {}


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
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
    public void testShortenUrl() throws InvalidInputException{
    	Map<String, String> queryMap = new HashMap<String, String>();
    	Mockito.when(urlDetailsDao.findUrlDetailsByUrl(Matchers.anyString())).thenReturn(null);
    	Mockito.doReturn("abc").when(urlServiceImpl).getUrlType(Matchers.anyString());
    	Mockito.doReturn(queryMap).when(urlServiceImpl).getQueryParamsFromUrl(Matchers.anyString());
    	Mockito.when(urlDetailsDao.insertUrlDetails((UrlDetails)Matchers.anyObject())).thenReturn("pass");
    	Mockito.when(encryptionHelper.encodeBase64(Matchers.anyString())).thenReturn("encryptedurl");
    	Whitebox.setInternalState(urlServiceImpl, "applicationBaseUrl", "http://localhost:8080/");
    	Assert.assertEquals("Shorten Url", urlServiceImpl.shortenUrl("test"), "http://localhost:8080/mail.do?q=encryptedurl");
    }
}
