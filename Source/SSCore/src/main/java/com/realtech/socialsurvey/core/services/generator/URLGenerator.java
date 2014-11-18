package com.realtech.socialsurvey.core.services.generator;

import java.util.Map;

import com.realtech.socialsurvey.core.exception.InvalidInputException;

// This is the interface for the URL generating classes.
public interface URLGenerator {
	
	public String generateUrl(Map<String,String> params,String baseUrl)throws InvalidInputException;
	
	public Map<String,String> decryptUrl(String Url) throws InvalidInputException, InvalidUrlException;

}
