/**
 * 
 */
package com.realtech.socialsurvey.core.services.generator.impl;

import static org.junit.Assert.assertEquals;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.generator.InvalidUrlException;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;

//JIRA: SS-6: By RM03

/**
 * This the Junit test module for UrlGeneratorImpl in the generator package.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/main/resources/sscore-beans.xml")
public class UrlGeneratorImplTest {	
	
	@Autowired
	URLGenerator urlGenerator;
	
	Map<String, String> params;
	
	/**
	 * This method is called before the tests are run. 
	 * Here we initialize the variables required for the tests.
	 */
	@Before
	public void initialize(){
		
		params = new HashMap<String, String>();
		params.put("fname", "Karthik");
		params.put("lname", "Srivatsa");
		
	}
	
	
	/**
	 * Tests if InvalidInputException is thrown correctly by generateCipher()
	 * @throws InvalidInputException
	 */
	@Test(expected = InvalidInputException.class)
	public void testInvalidGenerateCipher() throws InvalidInputException {
		urlGenerator.generateCipher(null);
	}	
	
	/**
	 * Tests if generateCipher returns an non null String
	 * @throws InvalidInputException
	 */
	@Test
	public void testValidGenerateCipher() throws InvalidInputException {
		String url = urlGenerator.generateCipher(params);
		assertEquals(url,"8d0f69ecdc877159750932044ffb68762c7e1c869bc2aab6fd7b7dc9c8df673d");
	}
	
	/**
	 * Tests if InvalidInputException is thrown correctly by generateUrl when parameter map is not given.
	 * @throws InvalidInputException
	 */
	@Test(expected = InvalidInputException.class)
	public void testInvalidGenerateUrlWithoutParam() throws InvalidInputException {
		urlGenerator.generateUrl(null,"http://socialsurvey.com/");
	}	
	
	/**
	 * Tests if InvalidInputException is thrown correctly by generateUrl when base url is not given.
	 * @throws InvalidInputException
	 */
	@Test(expected = InvalidInputException.class)
	public void testInvalidGenerateUrlWithoutBaseUrl() throws InvalidInputException {
		urlGenerator.generateUrl(params,"");
	}	
	
	/**
	 * Tests if generateUrl returns a valid url.
	 * @throws InvalidInputException
	 */
	@Test
	public void testValidGenerateUrl() throws InvalidInputException {
		String url = urlGenerator.generateUrl(params,"http://www.socialsurvey.com/");
		assertEquals(url,"http://www.socialsurvey.com/?q=8d0f69ecdc877159750932044ffb68762c7e1c869bc2aab6fd7b7dc9c8df673d");
	}
	
	/**
	 * Tests if generateUrl returns valid url when baseUrl has additional parameters in it.
	 * @throws InvalidInputException
	 */
	@Test
	public void testGenerateUrlWithParameters() throws InvalidInputException {
		
		String baseUrl = "http://www.socialsurvey.com/?token=12345";
		String url = urlGenerator.generateUrl(params,baseUrl);
		assertEquals(url,"http://www.socialsurvey.com/?token=12345&q=8d0f69ecdc877159750932044ffb68762c7e1c869bc2aab6fd7b7dc9c8df673d");
	}
	
	/**
	 * Tests if InvalidInputException is thrown correctly by decryptCipher()
	 * @throws InvalidInputException
	 */
	@Test(expected = InvalidInputException.class)
	public void testInvalidDecryptCipher() throws InvalidInputException {
		urlGenerator.generateCipher(null);
	}	
	
	/**
	 * Tests if decryptCipher returns an non null String
	 * @throws InvalidInputException
	 */
	@Test
	public void testValidDecryptCipher() throws InvalidInputException {
		String plainText = urlGenerator.decryptCipher(urlGenerator.generateCipher(params));
		assertEquals(plainText,"lname=Srivatsa&fname=Karthik");
	}
	
	/**
	 * Tests if InvalidInputException is thrown correctly by decryptParameters()
	 * @throws InvalidInputException
	 */
	@Test(expected = InvalidInputException.class)
	public void testInvaliddecryptParameters() throws InvalidInputException {
		urlGenerator.decryptParameters(null);
	}	
	
	/**
	 * Tests if decryptParameters() returns an non null Map
	 * @throws InvalidInputException
	 */
	@Test
	public void testValidDecryptParameters() throws InvalidInputException {
		Map<String,String> decryptedParameters = urlGenerator.decryptParameters(urlGenerator.generateCipher(params));
		assertEquals(decryptedParameters,params);
	}
	
	/**
	 * Tests if InvalidInputException is thrown correctly by decryptUrl()
	 * @throws InvalidInputException
	 * @throws InvalidUrlException 
	 */
	@Test(expected = InvalidInputException.class)
	public void testInvaliddecryptUrl() throws InvalidInputException, InvalidUrlException {
		urlGenerator.decryptUrl(null);
	}	
	
	/**
	 * Tests if InvalidInputException is thrown for malformed Url by decryptUrl()
	 * @throws InvalidInputException
	 * @throws InvalidUrlException 
	 */
	@Test(expected = InvalidUrlException.class)
	public void testInvalidUrldecryptUrl() throws InvalidInputException, InvalidUrlException {
		urlGenerator.decryptUrl("saddsfasdfasdf");
	}	
	
	/**
	 * Tests if decryptUrl() returns an non null Map of parameters
	 * @throws InvalidInputException
	 * @throws InvalidUrlException 
	 */
	@Test
	public void testValidDecryptUrl() throws InvalidInputException, InvalidUrlException {
		Map<String,String> decryptedParameters = urlGenerator.decryptUrl(urlGenerator.generateUrl(params, "http://www.socialsurvey.com/"));
		assertEquals(decryptedParameters,params);
	}
	
	/**
	 * Tests if decryptUrl() returns an non null Map of parameters when additional parameters exist in the url.
	 * @throws InvalidInputException
	 * @throws InvalidUrlException 
	 */
	@Test
	public void testDecryptUrlWithParameters() throws InvalidInputException, InvalidUrlException {
		Map<String,String> decryptedParameters = urlGenerator.decryptUrl("http://www.socialsurvey.com/?token=12345&q=8d0f69ecdc877159750932044ffb68762c7e1c869bc2aab6fd7b7dc9c8df673d&id=1123");
		assertEquals(decryptedParameters,params);
	}
			
}
