package com.realtech.socialsurvey.core.services.generator.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.core.commons.EncryptionService;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.generator.InvalidUrlException;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;

/**
 * @author RM03
 * JIRA Ticket SS-6
 */

public class UrlGeneratorImpl implements URLGenerator {
	
	private static final Logger LOG = LoggerFactory.getLogger(UrlGeneratorImpl.class);
			
	/**
	 * Function that takes Map of key,values and returns cipher text.
	 * AES algorithm used for encryption.
	 * @param params is a Map of key,value pairs.
	 * @return returns String cipher of the key,value pairs in params
	 * @throws InvalidInputException
	 */
	public String generateCipher(Map<String,String> params) throws InvalidInputException {
		
		if(params == null){
			LOG.error("Parameter to generateCipher() in VerificationUrlGenerator is null!");
			throw new InvalidInputException("Parameter to generateCipher() in VerificationUrlGenerator is null!");
		}
		
		String plainText = "";
		
		// The parameters are arranged in format key=value separated by &.		
		for(String key : params.keySet()){
			plainText += key + "=" + params.get(key) + "&";
		}

		// Last extra & is removed.
		plainText = plainText.substring(0, plainText.length()-1);
		
		// Return the url.
		return EncryptionService.encryptAES(plainText,"");
	}
	
		
	/**
	 * Function to generate urls based on the key, value parameters in the registration form
	 * AES algorithm used for encryption.
	 * NOTE : 'q' is the parameter used in the url while encoding the cipher text into url.
	 * @param params is a Map of key,value pairs and baseUrl is the url to which the cipher has to be attached with.
	 * @return returns String that contains url with encoded parameters.
	 * @throws InvalidInputException
	 */
	public String generateUrl(Map<String, String> params,String baseUrl) throws InvalidInputException {
		
		if(params == null){
			LOG.error("First parameter to generateUrl() in VerificationUrlGenerator is null!");
			throw new InvalidInputException("First parameter to generateUrl() in VerificationUrlGenerator is null!");
		}
		
		if(baseUrl == null || baseUrl == ""){
			LOG.error("Second parameter to generateUrl() in VerificationUrlGenerator is null or empty!");
			throw new InvalidInputException("First parameter to generateUrl() in VerificationUrlGenerator is null or empty!");
		}
		
		return baseUrl + "?q=" + generateCipher(params);
	}
	
	
	/**
	 * Function to decrypt the cipher text and extract the key, value parameters of the registration form.
	 * @param cipherText which is the encoded text to be decrypted.
	 * @return a String which is the decoded chipher text.
	 * @throws InvalidInputException
	 */
	public String decryptCipher(String cipherText) throws InvalidInputException{
		
		if( cipherText == null){
			LOG.error("Parameter to decryptCipher() in VerificationUrlGenerator is null!");
			throw new InvalidInputException("Parameter to decryptCipher() in VerificationUrlGenerator is null!");
		}
				
		return EncryptionService.decryptAES(cipherText, "");
		
	}
	
	
	/**
	 * Function that takes parameter cipher text and returns Map of key,value pairs.
	 * @param parameterCipherText which is the cipher text of the parameters.
	 * @return a Map of key,value pairs of the parameters encoded.
	 * @throws InvalidInputException
	 */
	public Map<String,String> decryptParameters(String parameterCipherText) throws InvalidInputException {
		
		if( parameterCipherText == null || parameterCipherText == ""){
			LOG.error("Parameter to decryptParameters() in VerificationUrlGenerator is null or empty!");
			throw new InvalidInputException("Parameter to decryptParameters() in VerificationUrlGenerator is null!");
		}
		
		Map<String,String> params = new HashMap<String,String>();
		
		String plainText = decryptCipher(parameterCipherText);
		String keyValuePairs[] = plainText.split("&");
		
		for( int counter = 0;counter < keyValuePairs.length; counter += 2){
			params.put(keyValuePairs[counter], keyValuePairs[counter+1]);
		}		
		return params;
		
	}
	
	
	/**
	 * Function that takes url with encoded cipher of the parameters and returns Map of key,value pairs.
	 * @param Url which is the url with encoded cipher of the parameters.
	 * @return a Map of key,value pairs of the parameters encoded.
	 * @throws InvalidInputException
	 * @throws InvalidUrlException 
	 */
	public Map<String, String> decryptUrl(String Url) throws InvalidInputException, InvalidUrlException {
		
		if( Url == null || Url == ""){
			LOG.error("Parameter to decryptUrl() in VerificationUrlGenerator is null or empty!");
			throw new InvalidInputException("Parameter to decryptUrl() in VerificationUrlGenerator is null!");
		}
		
		String[] urlSplit = Url.split("q=");
		
		if(urlSplit.length != 2){
			LOG.error("Url parameter sent to decryptUrl() of UrlGeneratorImpl is malformed!");
			throw new InvalidUrlException("Url parameter sent to decryptUrl() of UrlGeneratorImpl is malformed!");			
		}
		
		String parameterCipherText = urlSplit[1];
						
		return decryptParameters(parameterCipherText);
	}
	
	public static void main(String args[]){
		UrlGeneratorImpl v = new UrlGeneratorImpl();
		Map<String,String> params = new HashMap<String,String>();
		params.put("fname", "Karthik");
		params.put("lname", "Srivatsa");
		
		try {
			System.out.println(v.generateUrl(params,"http://www.socialsurvey.com/"));
		} catch (InvalidInputException e) {
			e.printStackTrace();
		}
		try {
			System.out.println(v.decryptUrl("sdfsfas"));
		} catch (InvalidInputException e) {
			e.printStackTrace();
		} catch (InvalidUrlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
}
