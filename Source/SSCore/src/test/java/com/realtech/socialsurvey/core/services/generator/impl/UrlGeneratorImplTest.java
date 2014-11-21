/**
 * 
 */
package com.realtech.socialsurvey.core.services.generator.impl;

import static org.junit.Assert.*;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.generator.InvalidUrlException;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;

//JIRA: SS-6: By RM03

/**
 * This the Junit test module for UrlGeneratorImpl in the generator package.
 */
public class UrlGeneratorImplTest {	
	
	URLGenerator generator;
	Map<String, String> params;
	static ApplicationContext context;
	
	/**
	 * This method is called before the tests are run. 
	 * Here we initialize the variables required for the tests.
	 */
	@Before
	public void initialize(){
		
		generator = (URLGenerator) context.getBean("urlGenerator");
		params = new HashMap<String, String>();
		params.put("fname", "Karthik");
		params.put("lname", "Srivatsa");
		
	}
	
	@BeforeClass
	public static void before(){
		context = new ClassPathXmlApplicationContext("sscore-all-test-config.xml");
	}
	
	@AfterClass
	public static void after(){
		context = null;
	}
	
	
	/**
	 * Tests if InvalidInputException is thrown correctly by generateCipher()
	 * @throws InvalidInputException
	 */
	@Test(expected = InvalidInputException.class)
	public void testInvalidGenerateCipher() throws InvalidInputException {
		generator.generateCipher(null);
	}	
	
	/**
	 * Tests if generateCipher returns an non null String
	 * @throws InvalidInputException
	 */
	@Test
	public void testValidGenerateCipher() throws InvalidInputException {
		String url = generator.generateCipher(params);
		assertEquals(url,"8d0f69ecdc877159750932044ffb68762c7e1c869bc2aab6fd7b7dc9c8df673d");
	}
	
	/**
	 * Tests if InvalidInputException is thrown correctly by generateUrl when parameter map is not given.
	 * @throws InvalidInputException
	 */
	@Test(expected = InvalidInputException.class)
	public void testInvalidGenerateUrlWithoutParam() throws InvalidInputException {
		generator.generateUrl(null,"http://socialsurvey.com/");
	}	
	
	/**
	 * Tests if InvalidInputException is thrown correctly by generateUrl when base url is not given.
	 * @throws InvalidInputException
	 */
	@Test(expected = InvalidInputException.class)
	public void testInvalidGenerateUrlWithoutBaseUrl() throws InvalidInputException {
		generator.generateUrl(params,"");
	}	
	
	/**
	 * Tests if generateUrl returns a valid url.
	 * @throws InvalidInputException
	 */
	@Test
	public void testValidGenerateUrl() throws InvalidInputException {
		String url = generator.generateUrl(params,"http://www.socialsurvey.com/");
		assertEquals(url,"http://www.socialsurvey.com/?q=8d0f69ecdc877159750932044ffb68762c7e1c869bc2aab6fd7b7dc9c8df673d");
	}
	
	/**
	 * Tests if generateUrl returns valid url when baseUrl has additional parameters in it.
	 * @throws InvalidInputException
	 */
	@Test
	public void testGenerateUrlWithParameters() throws InvalidInputException {
		
		String baseUrl = "http://www.socialsurvey.com/?token=12345";
		String url = generator.generateUrl(params,baseUrl);
		assertEquals(url,"http://www.socialsurvey.com/?token=12345&q=8d0f69ecdc877159750932044ffb68762c7e1c869bc2aab6fd7b7dc9c8df673d");
	}
	
	/**
	 * Tests if InvalidInputException is thrown correctly by decryptCipher()
	 * @throws InvalidInputException
	 */
	@Test(expected = InvalidInputException.class)
	public void testInvalidDecryptCipher() throws InvalidInputException {
		generator.generateCipher(null);
	}	
	
	/**
	 * Tests if decryptCipher returns an non null String
	 * @throws InvalidInputException
	 */
	@Test
	public void testValidDecryptCipher() throws InvalidInputException {
		String plainText = generator.decryptCipher(generator.generateCipher(params));
		assertEquals(plainText,"lname=Srivatsa&fname=Karthik");
	}
	
	/**
	 * Tests if InvalidInputException is thrown correctly by decryptParameters()
	 * @throws InvalidInputException
	 */
	@Test(expected = InvalidInputException.class)
	public void testInvaliddecryptParameters() throws InvalidInputException {
		generator.decryptParameters(null);
	}	
	
	/**
	 * Tests if decryptParameters() returns an non null Map
	 * @throws InvalidInputException
	 */
	@Test
	public void testValidDecryptParameters() throws InvalidInputException {
		Map<String,String> decryptedParameters = generator.decryptParameters(generator.generateCipher(params));
		assertEquals(decryptedParameters,params);
	}
	
	/**
	 * Tests if InvalidInputException is thrown correctly by decryptUrl()
	 * @throws InvalidInputException
	 * @throws InvalidUrlException 
	 */
	@Test(expected = InvalidInputException.class)
	public void testInvaliddecryptUrl() throws InvalidInputException, InvalidUrlException {
		generator.decryptUrl(null);
	}	
	
	/**
	 * Tests if InvalidInputException is thrown for malformed Url by decryptUrl()
	 * @throws InvalidInputException
	 * @throws InvalidUrlException 
	 */
	@Test(expected = InvalidUrlException.class)
	public void testInvalidUrldecryptUrl() throws InvalidInputException, InvalidUrlException {
		generator.decryptUrl("saddsfasdfasdf");
	}	
	
	/**
	 * Tests if decryptUrl() returns an non null Map of parameters
	 * @throws InvalidInputException
	 * @throws InvalidUrlException 
	 */
	@Test
	public void testValidDecryptUrl() throws InvalidInputException, InvalidUrlException {
		Map<String,String> decryptedParameters = generator.decryptUrl(generator.generateUrl(params, "http://www.socialsurvey.com/"));
		assertEquals(decryptedParameters,params);
	}
	
	/**
	 * Tests if decryptUrl() returns an non null Map of parameters when additional parameters exist in the url.
	 * @throws InvalidInputException
	 * @throws InvalidUrlException 
	 */
	@Test
	public void testDecryptUrlWithParameters() throws InvalidInputException, InvalidUrlException {
		Map<String,String> decryptedParameters = generator.decryptUrl("http://www.socialsurvey.com/?token=12345&q=8d0f69ecdc877159750932044ffb68762c7e1c869bc2aab6fd7b7dc9c8df673d&id=1123");
		assertEquals(decryptedParameters,params);
	}
			
}
