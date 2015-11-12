package com.realtech.socialsurvey.core.services.generator.impl;
//JIRA: SS-6: By RM03


import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.generator.InvalidUrlException;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.generator.UrlService;
import com.realtech.socialsurvey.core.utils.EncryptionHelper;



/**
 * This class uses the AES algorithm to generate URL from the parameters by encoding them into the URL. 
 * It also has function to decrypt a URL and extract the Map of parameters from them.
 */
@Component
public class UrlGeneratorImpl implements URLGenerator {
	
	private static final Logger LOG = LoggerFactory.getLogger(UrlGeneratorImpl.class);
	
	@Autowired
	private EncryptionHelper encryptionHelper;
			
	/**
	 * Function that takes Map of key,values and returns cipher text.
	 * AES algorithm used for encryption.
	 * @param params is a Map of key,value pairs.
	 * @return returns String cipher of the key,value pairs in params
	 * @throws InvalidInputException
	 */
	@Override
	public String generateCipher(Map<String,String> params) throws InvalidInputException {
		
		if(params == null){
			LOG.error("Parameter to generateCipher() in VerificationUrlGenerator is null!");
			throw new InvalidInputException("Parameter to generateCipher() in VerificationUrlGenerator is null!");
		}
		
		LOG.info("generateCipher(): paramaters : " +params.toString());	
		
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
		
		// Return the url.
		String cipher = encryptionHelper.encryptAES(plainText.toString(),"");
		LOG.info("generateCipher() Output : " + cipher);
		return cipher;
	}
	
		
	/**
	 * Function to generate urls based on the key, value parameters in the registration form
	 * AES algorithm used for encryption.
	 * NOTE : 'q' is the parameter used in the url while encoding the cipher text into url.
	 * @param params is a Map of key,value pairs and baseUrl is the url to which the cipher has to be attached with.
	 * @return returns String that contains url with encoded parameters.
	 * @throws InvalidInputException
	 */
	@Override
	public String generateUrl(Map<String, String> params,String baseUrl) throws InvalidInputException {
		
		if(params == null){
			LOG.error("First parameter to generateUrl() in VerificationUrlGenerator is null!");
			throw new InvalidInputException("First parameter to generateUrl() in VerificationUrlGenerator is null!");
		}
		
		if(baseUrl == null || baseUrl.isEmpty()){
			LOG.error("Second parameter to generateUrl() in VerificationUrlGenerator is null or empty!");
			throw new InvalidInputException("First parameter to generateUrl() in VerificationUrlGenerator is null or empty!");
		}
		
		LOG.info("generateUrl(): parameters: " + params.toString() + "  " + baseUrl);
		
		String url;
		// Check if url already has parameters
		if(baseUrl.contains("?")){
			// If true we add an additional 'q' parameter at the end
			url= baseUrl + "&q=" + generateCipher(params);
			LOG.info("generateUrl() Output : " + url);
			return url;
		}
		else{	
			// If not we put the '?' and add the 'q' parameter
			url = baseUrl + "?q=" + generateCipher(params);
			LOG.info("generateUrl() Output : " + url);
			return url;
		}
	}
	
	
	/**
	 * Function to decrypt the cipher text and extract the key, value parameters of the registration form.
	 * @param cipherText which is the encoded text to be decrypted.
	 * @return a String which is the decoded chipher text.
	 * @throws InvalidInputException
	 */
	@Override
	public String decryptCipher(String cipherText) throws InvalidInputException{
		
		if( cipherText == null || cipherText.isEmpty() ){
			LOG.error("Parameter to decryptCipher() in VerificationUrlGenerator is null or empty!");
			throw new InvalidInputException("Parameter to decryptCipher() in VerificationUrlGenerator is null or empty!");
		}
		
		LOG.info("decryptCipher() : parameters: " + cipherText );
		
		String plainText = encryptionHelper.decryptAES(cipherText, "");
		LOG.info("decryptCipher() Output: " + plainText );
				
		return plainText;
		
	}
	
	/**
	 * Getter for encryptionHelper member variable.
	 * @return
	 */
	public EncryptionHelper getEncryptionHelper() {
		return encryptionHelper;
	}

	/**
	 * Setter for the encryptionHelper member variable.
	 * @param encryptionHelper
	 */
	public void setEncryptionHelper(EncryptionHelper encryptionHelper) {
		this.encryptionHelper = encryptionHelper;
	}


	/**
	 * Function that takes parameter cipher text and returns Map of key,value pairs.
	 * @param parameterCipherText which is the cipher text of the parameters.
	 * @return a Map of key,value pairs of the parameters encoded.
	 * @throws InvalidInputException
	 */
	@Override
	public Map<String, String> decryptParameters(String parameterCipherText) throws InvalidInputException {
		if (parameterCipherText == null || parameterCipherText.isEmpty()) {
			LOG.error("Parameter to decryptParameters() in VerificationUrlGenerator is null or empty!");
			throw new InvalidInputException("Parameter to decryptParameters() in VerificationUrlGenerator is null!");
		}
		LOG.info("decryptParameters() : parameters: " + parameterCipherText);

		Map<String, String> params = new HashMap<String, String>();
		String plainText = decryptCipher(parameterCipherText);
		String keyValuePairs[] = plainText.split("&");

		for (int counter = 0; counter < keyValuePairs.length; counter += 1) {
			String[] keyValuePair = keyValuePairs[counter].split("=");
			boolean isKeyExists = isElementExists(keyValuePair, 0);
			boolean isValueExists = isElementExists(keyValuePair, 1);
			
			if (isKeyExists && isValueExists) {
				params.put(keyValuePair[0], keyValuePair[1]);
			}
			else if (isKeyExists && !isValueExists) {
				params.put(keyValuePair[0], "");
			}
		}
		return params;
	}
	
	@SuppressWarnings("unused")
	private static boolean isElementExists(String[] keyValuePair, int index) {
		try {
			String value = keyValuePair[index];
			return true;
		}
		catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
	}
	
	/**
	 * Function that takes url with encoded cipher of the parameters and returns Map of key,value pairs.
	 * @param url which is the url with encoded cipher of the parameters.
	 * @return a Map of key,value pairs of the parameters encoded.
	 * @throws InvalidInputException
	 * @throws InvalidUrlException 
	 */
	@Override
	public Map<String, String> decryptUrl(String url) throws InvalidInputException, InvalidUrlException {
		
		if( url == null || url.isEmpty() ){
			LOG.error("Parameter to decryptUrl() in VerificationUrlGenerator is null or empty!");
			throw new InvalidInputException("Parameter to decryptUrl() in VerificationUrlGenerator is null!");
		}
		
		LOG.info("decryptUrl() : parameters: " + url );
		
		String[] urlSplit = url.split("q=");
		
		// We check if the url has a q parameter.
		if(urlSplit.length != 2){
			LOG.error("Url parameter sent to decryptUrl() of UrlGeneratorImpl is malformed!");
			throw new InvalidUrlException("Url parameter sent to decryptUrl() of UrlGeneratorImpl is malformed!");			
		}
		
		//In case there are additional parameters we will filter them out and get only the 'q' parameter
		String parameterCipherText = urlSplit[1];
		parameterCipherText = parameterCipherText.split("&")[0];
						
		return decryptParameters(parameterCipherText);
	}
								
}
