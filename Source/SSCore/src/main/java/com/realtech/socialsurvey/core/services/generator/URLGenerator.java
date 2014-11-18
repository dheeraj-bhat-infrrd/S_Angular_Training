package com.realtech.socialsurvey.core.services.generator;
//JIRA: SS-6: By RM03
import java.util.Map;

import com.realtech.socialsurvey.core.exception.InvalidInputException;

// This is the interface for the URL generating classes.
public interface URLGenerator {
	
	public String generateCipher(Map<String,String> params) throws InvalidInputException;
	
	public String generateUrl(Map<String,String> params,String baseUrl)throws InvalidInputException;
	
	public String decryptCipher(String cipherText) throws InvalidInputException;
	
	public Map<String,String> decryptParameters(String parameterCipherText) throws InvalidInputException;
	
	public Map<String,String> decryptUrl(String Url) throws InvalidInputException, InvalidUrlException;

}
