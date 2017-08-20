package com.realtech.socialsurvey.api.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.realtech.socialsurvey.api.exceptions.SSApiException;
import com.realtech.socialsurvey.api.utils.RestUtils;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping ( "/")
public class BaseController {
	
	@Autowired
    private RestUtils restUtils;
	
	@RequestMapping ( value = "/**")
    @ApiOperation ( value = "No handler found")
    public ResponseEntity<?> noResourceFound( HttpServletRequest request )
        throws SSApiException
    {
		long companyId = 0;
		return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST, "Invalid request", null, null,
                request, companyId );
    }
}
