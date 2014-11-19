
package com.realtech.socialsurvey.core.utils;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.realtech.socialsurvey.core.exception.InvalidInputException;

//JIRA: SS-6: By RM03

/**
 * This the Junit test module for EncryptionHelper in the utils package.
 */
public class EncryptionHelperTest {
	
	EncryptionHelper encryptor;
	Map<String, String> params;
	String paramText;
	
	/**
	 * This method is called before the tests are run. 
	 * Here we initialize the variables required for the tests.
	 */
	@Before
	public void initialize(){
		
		encryptor = new EncryptionHelper();
		params = new HashMap<String, String>();
		params.put("fname", "Karthik");
		params.put("lname", "Srivatsa");
		
		StringBuilder plainText = new StringBuilder();
		// The parameters are arranged in format key=value separated by &.		
		for(String key : params.keySet()){
			plainText.append(key);
			plainText.append("=");
			plainText.append(params.get(key));
			plainText.append("&");
		}
		// Last extra & is removed.
		plainText.deleteCharAt(plainText.length()-1);
		paramText = plainText.toString();
		
	}

	/**
	 * Testing the function getNullSafeString() for valid output.
	 */
	@Test
	public void testValidGetNullSafeString() {		
		assertEquals("Raremile",encryptor.getNullSafeString("Raremile"));
		assertEquals("",encryptor.getNullSafeString(null));		
	}

	/**
	 * Testing the hexArrayToByteString() for exceptions.
	 * @throws InvalidInputException
	 */
	@Test(expected = InvalidInputException.class)
	public void testInvalidHexStringToByteArray() throws InvalidInputException{
		encryptor.hexStringToByteArray(null);		
	}

	/**
	 * Testing the hexArrayToByteString() for expected output.
	 * @throws InvalidInputException
	 */
	@Test
	public void testValidHexStringToByteArray() throws InvalidInputException{
		byte[] output = encryptor.hexStringToByteArray("8d0f69ecdc877159750932044ffb68762c7e1c869bc2aab6fd7b7dc9c8df673d");
		assertEquals("8d0f69ecdc877159750932044ffb68762c7e1c869bc2aab6fd7b7dc9c8df673d",encryptor.byteArrayToHexString(output) );		
	}

	/**
	 * Testing the function byteArrayToHexString() for exceptions.
	 * @throws InvalidInputException
	 */
	@Test(expected = InvalidInputException.class)
	public void testInvalidByteArrayToHexString() throws InvalidInputException{
		encryptor.byteArrayToHexString(null);
	}

	/**
	 * Testing function byteArrayToHexString() for expected output.
	 * @throws InvalidInputException
	 */
	@Test
	public void testValidByteArrayToHexString() throws InvalidInputException{
		
		byte[] testInput = encryptor.hexStringToByteArray("8d0f69ecdc877159750932044ffb68762c7e1c869bc2aab6fd7b7dc9c8df673d");
		assertEquals(encryptor.byteArrayToHexString(testInput),"8d0f69ecdc877159750932044ffb68762c7e1c869bc2aab6fd7b7dc9c8df673d");
				
	}

	/**
	 * Testing encryptAES() function for exception when plain text is null.
	 * @throws InvalidInputException
	 */
	@Test(expected = InvalidInputException.class)
	public void testInvalidEncryptAESWithoutPlainText() throws InvalidInputException{
		encryptor.encryptAES(null, "");
	}
	
	/**
	 * Testing encryptAES() function for exception when key is null.
	 * @throws InvalidInputException
	 */
	@Test(expected = InvalidInputException.class)
	public void testInvalidEncryptAESWithoutKey() throws InvalidInputException{
		encryptor.encryptAES("raremile", null);
	}
	
	/**
	 * Testing encryptAES() function for valid output.
	 * @throws InvalidInputException
	 */
	@Test
	public void testValidEncryptAES() throws InvalidInputException{
		String cipher = encryptor.encryptAES(paramText, "");
		assertEquals(cipher,"8d0f69ecdc877159750932044ffb68762c7e1c869bc2aab6fd7b7dc9c8df673d");
	}

	/**
	 * Testing decryptAES() function for exception when cipher text is null.
	 * @throws InvalidInputException
	 */
	@Test(expected = InvalidInputException.class)
	public void testInvalidDecryptAESWithoutPlainText() throws InvalidInputException{
		encryptor.decryptAES(null, "");
	}
	
	/**
	 * Testing decryptAES() function for exception when key is null.
	 * @throws InvalidInputException
	 */
	@Test(expected = InvalidInputException.class)
	public void testInvalidDecryptAESWithoutKey() throws InvalidInputException{
		encryptor.decryptAES("raremile", null);
	}

	/**
	 * Testing the decryptAES() function for valid output.
	 * @throws InvalidInputException
	 */
	@Test
	public void testValidDecryptAES() throws InvalidInputException{
		String plainText = encryptor.decryptAES("8d0f69ecdc877159750932044ffb68762c7e1c869bc2aab6fd7b7dc9c8df673d", "");
		assertEquals(paramText,plainText);
	}

}
