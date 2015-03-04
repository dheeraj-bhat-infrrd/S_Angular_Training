package com.realtech.socialsurvey.core.services.generator;
//JIRA: SS-6: By RM03
import java.util.Map;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

// This is the interface for the URL generating classes.
public interface URLGenerator {
	
	
	/**
	 * Function that takes Map of key,values and returns cipher text.
	 * @param params is a Map of key,value pairs.
	 * @return returns String cipher of the key,value pairs in params
	 * @throws InvalidInputException
	 */
	public String generateCipher(Map<String,String> params) throws InvalidInputException;
	
	
	/**
	 * Function to generate urls based on the key, value parameters in the registration form
	 * @param params params is a Map of key,value pairs
	 * @param baseUrl baseUrl is the url to which the cipher has to be attached with
	 * @return String that contains url with encoded parameters
	 * @throws InvalidInputException
	 */
	public String generateUrl(Map<String,String> params,String baseUrl)throws InvalidInputException;
	
	/**
	 * Function to decrypt the cipher text and extract the key, value parameters
	 * @param cipherText which is the encoded text to be decrypted
	 * @return String which is the decoded chipher text
	 * @throws InvalidInputException
	 */
	public String decryptCipher(String cipherText) throws InvalidInputException;
	
	/**
	 * Function that takes parameter cipher text and returns Map of key,value pairs
	 * @param parameterCipherText which is the cipher text of the parameters
	 * @return Map of key,value pairs of the parameters encoded
	 * @throws InvalidInputException
	 */
	public Map<String,String> decryptParameters(String parameterCipherText) throws InvalidInputException;
	
	/**
	 * Function that takes url with encoded cipher of the parameters and returns Map of key,value pairs.
	 * @param Url Url which is the url with encoded cipher of the parameters
	 * @return Map of key,value pairs of the parameters encoded
	 * @throws InvalidInputException
	 * @throws InvalidUrlException
	 */
	public Map<String,String> decryptUrl(String Url) throws InvalidInputException, InvalidUrlException;

}
