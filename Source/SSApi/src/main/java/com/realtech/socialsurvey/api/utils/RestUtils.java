package com.realtech.socialsurvey.api.utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * 
 * @author rohit
 *
 */
@Component
public class RestUtils {

	
	public ResponseEntity<Map<String , Object>> getRestResponseEntity(HttpStatus httpStatus , String responseMsg, 
	    String dataKey , Object dataObject , HttpServletRequest request , long companyId ){
		
		Map<String,Object> responseMap = new LinkedHashMap<String, Object>();
		
		Map<String , Object> msgEntity = new HashMap<String , Object>();
		msgEntity.put("code", httpStatus.value());
		msgEntity.put("message", responseMsg);
		responseMap.put("msg", msgEntity);
		
		if(! StringUtils.isBlank(dataKey) ){
			Map<String , Object> dateEntity = new HashMap<String , Object>();
			dateEntity.put(dataKey, dataObject);
			responseMap.put("data", dateEntity);
		}
		
		ResponseEntity<Map<String , Object>> responseEntity = new ResponseEntity<Map<String,Object>>(responseMap , httpStatus);
        request.setAttribute( "output" , responseEntity );
        request.setAttribute( "companyId" , companyId );
		return responseEntity;
	}
}
