package com.realtech.socialsurvey.api.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.realtech.socialsurvey.core.entities.JobLogDetailsResponse;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.reportingmanagement.JobLogDetailsManagement;
import io.swagger.annotations.ApiOperation;


@RestController
@RequestMapping ( "/v1")
public class JobLogDetailsReportingController
{

    private static final Logger LOGGER = LoggerFactory.getLogger( JobLogDetailsReportingController.class );

    @Autowired
    private JobLogDetailsManagement jobLogDetailsManagement;


    @RequestMapping ( value = "/lastsuccessfuletltime", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch Job Log Details for last successful run")
    public String getLastSuccessfulEtlTimeApi() throws InvalidInputException
    {
        LOGGER.info( "Fetching Job Log Details for last successful run" );
        String json = null;
        JobLogDetailsResponse jobLogDetailsResponse = jobLogDetailsManagement.getLastSuccessfulEtlTime();
        json = new Gson().toJson( jobLogDetailsResponse );
        return json;

    }

}
