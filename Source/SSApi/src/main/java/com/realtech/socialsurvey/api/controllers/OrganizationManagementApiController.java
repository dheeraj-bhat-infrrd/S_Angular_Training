package com.realtech.socialsurvey.api.controllers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping ( "/v1")
public class OrganizationManagementApiController 
{
	
    private static final Logger LOGGER = LoggerFactory.getLogger( OrganizationManagementApiController.class );
    
    @Autowired
    private OrganizationManagementService organizationManagementService;
    
    @RequestMapping ( value = "/updateabusivemail", method = RequestMethod.POST)
    @ApiOperation ( value = "Updating abusive mail settings")
    public String updateAbusiveMail( long entityId , String mailId) throws InvalidInputException, NonFatalException
    {
        LOGGER.info( "Updating abusive mail settings for companyId : {}",entityId );
        organizationManagementService.updateAbusiveMailService(entityId, mailId);
		return mailId;

    }

    @RequestMapping ( value = "/unsetabusivemail", method = RequestMethod.POST)
    @ApiOperation ( value = "Unset abusive mail settings")
    public void unsetAbusiveMail( long entityId ) throws  NonFatalException
    {
        LOGGER.info( "Unset abusive mail settings for companyId : {}",entityId );
        organizationManagementService.unsetAbusiveMailService(entityId);
    }
    
    @RequestMapping ( value = "/unsetcompres", method = RequestMethod.POST)
    @ApiOperation ( value = "Unset Complaint Resolution settings")
    public void unsetCompRes( long entityId ) throws  NonFatalException
    {
        LOGGER.info( "Unset Complaint Resolution settings for companyId : {}",entityId );
        organizationManagementService.unsetComplaintResService(entityId);
    }

}
