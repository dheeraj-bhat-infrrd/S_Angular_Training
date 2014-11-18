/**
 * 
 */
package com.realtech.socialsurvey.core.services.generator.impl;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.generator.InvalidUrlException;

//JIRA: SS-6: By RM03


public class UrlGeneratorImplTest {	
	
	/**
	 * Tests if InvalidInputException is thrown correctly by generateCipher()
	 * @throws InvalidInputException
	 */
	@Test(expected = InvalidInputException.class)
	public void testInvalidGenerateCipher() throws InvalidInputException {
		UrlGeneratorImpl generator = new UrlGeneratorImpl();
		generator.generateCipher(null);
	}	
	
	/**
	 * Tests if generateCipher returns an non null String
	 * @throws InvalidInputException
	 */
	@Test
	public void testValidGenerateCipher() throws InvalidInputException {
		UrlGeneratorImpl generator = new UrlGeneratorImpl();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("fname", "Karthik");
		params.put("lname", "Srivatsa");
		String url = generator.generateCipher(params);
		assertNotNull(url);
	}
	
	/**
	 * Tests if InvalidInputException is thrown correctly by generateUrl when parameter map is not given.
	 * @throws InvalidInputException
	 */
	@Test(expected = InvalidInputException.class)
	public void testInvalidGenerateUrlWithoutParam() throws InvalidInputException {
		UrlGeneratorImpl generator = new UrlGeneratorImpl();
		generator.generateUrl(null,"http://socialsurvey.com/");
	}	
	
	/**
	 * Tests if InvalidInputException is thrown correctly by generateUrl when base url is not given.
	 * @throws InvalidInputException
	 */
	@Test(expected = InvalidInputException.class)
	public void testInvalidGenerateUrlWithoutBaseUrl() throws InvalidInputException {
		UrlGeneratorImpl generator = new UrlGeneratorImpl();
		Map<String, String> params = new HashMap<String, String>();
		params.put("fname", "Karthik");
		params.put("lname", "Srivatsa");
		generator.generateUrl(params,"");
	}	
	
	/**
	 * Tests if generateUrl returns an non null String
	 * @throws InvalidInputException
	 */
	@Test
	public void testValidGenerateUrl() throws InvalidInputException {
		UrlGeneratorImpl generator = new UrlGeneratorImpl();
		Map<String, String> params = new HashMap<String, String>();
		params.put("fname", "Karthik");
		params.put("lname", "Srivatsa");
		String url = generator.generateUrl(params,"http://socialsurvey.com/");
		assertNotNull(url);
	}
	
	/**
	 * Tests if InvalidInputException is thrown correctly by decryptCipher()
	 * @throws InvalidInputException
	 */
	@Test(expected = InvalidInputException.class)
	public void testInvalidDecryptCipher() throws InvalidInputException {
		UrlGeneratorImpl generator = new UrlGeneratorImpl();
		generator.generateCipher(null);
	}	
	
	/**
	 * Tests if decryptCipher returns an non null String
	 * @throws InvalidInputException
	 */
	@Test
	public void testValidDecryptCipher() throws InvalidInputException {
		UrlGeneratorImpl generator = new UrlGeneratorImpl();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("fname", "Karthik");
		params.put("lname", "Srivatsa");
		String plainText = generator.decryptCipher(generator.generateCipher(params));
		assertNotNull(plainText);
	}
	
	/**
	 * Tests if InvalidInputException is thrown correctly by decryptParameters()
	 * @throws InvalidInputException
	 */
	@Test(expected = InvalidInputException.class)
	public void testInvaliddecryptParameters() throws InvalidInputException {
		UrlGeneratorImpl generator = new UrlGeneratorImpl();
		generator.decryptParameters(null);
	}	
	
	/**
	 * Tests if decryptParameters() returns an non null String
	 * @throws InvalidInputException
	 */
	@Test
	public void testValiddecryptParameters() throws InvalidInputException {
		UrlGeneratorImpl generator = new UrlGeneratorImpl();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("fname", "Karthik");
		params.put("lname", "Srivatsa");
		Map<String,String> decryptedParameters = generator.decryptParameters(generator.generateCipher(params));
		assertNotNull(decryptedParameters);
	}
	
	/**
	 * Tests if InvalidInputException is thrown correctly by decryptUrl()
	 * @throws InvalidInputException
	 * @throws InvalidUrlException 
	 */
	@Test(expected = InvalidInputException.class)
	public void testInvaliddecryptUrl() throws InvalidInputException, InvalidUrlException {
		UrlGeneratorImpl generator = new UrlGeneratorImpl();
		generator.decryptUrl(null);
	}	
	
	/**
	 * Tests if InvalidInputException is thrown for malformed Url by decryptUrl()
	 * @throws InvalidInputException
	 * @throws InvalidUrlException 
	 */
	@Test(expected = InvalidUrlException.class)
	public void testInvalidUrldecryptUrl() throws InvalidInputException, InvalidUrlException {
		UrlGeneratorImpl generator = new UrlGeneratorImpl();
		generator.decryptUrl("saddsfasdfasdf");
	}	
	
	/**
	 * Tests if decryptUrl() returns an non null String
	 * @throws InvalidInputException
	 * @throws InvalidUrlException 
	 */
	@Test
	public void testValiddecryptUrl() throws InvalidInputException, InvalidUrlException {
		UrlGeneratorImpl generator = new UrlGeneratorImpl();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("fname", "Karthik");
		params.put("lname", "Srivatsa");
		Map<String,String> decryptedParameters = generator.decryptUrl(generator.generateUrl(params, "http://www.socialsurvey.com/"));
		assertNotNull(decryptedParameters);
	}
	
	
	
	
	
	
}
