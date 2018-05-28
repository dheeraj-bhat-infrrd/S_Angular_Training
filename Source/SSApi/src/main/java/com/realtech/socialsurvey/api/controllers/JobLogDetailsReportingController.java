package com.realtech.socialsurvey.api.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.realtech.socialsurvey.core.commons.CommonConstants;
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
    
    @RequestMapping ( value = "/lastsuccessfuletltime/isetlrunning", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch Job Log Details to check etl run")
    public String isEtlRunning() throws InvalidInputException
    {
        LOGGER.info( "Fetching Job Log Details to check etl run" );
        String json = null;
        Boolean isRunning = jobLogDetailsManagement.getIfEtlIsRunning();
        json = new Gson().toJson( isRunning );
        return json;
    }
    
    @RequestMapping ( value = "/lastsuccessfuletltime/{entityId}/{entityType}", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch Job Log Details to check last run for entity")
    public String lastRunForEntity(@PathVariable ( "entityId") long entityId  , @PathVariable ( "entityType") String entityType ) throws InvalidInputException
    {
        LOGGER.info( "Fetching Job Log Details to check last etl run for entity" );
        String json = null;
        JobLogDetailsResponse jobLogDetailsResponse = jobLogDetailsManagement.getLastRunForEntity(entityId, entityType);
        json = new Gson().toJson( jobLogDetailsResponse );
        return json;
    }
    
    @RequestMapping ( value = "/lastsuccessfuletltime/recal/{entityId}/{entityType}", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch Job Log Details to check last run for entity")
    public String recalUserRanking(@PathVariable ( "entityId") long entityId  , @PathVariable ( "entityType") String entityType ) throws InvalidInputException
    {
        LOGGER.info( "insert to Job Log Details and recal user ranking" );
        //CHECK IF ETL IS RUNNING
        long jobLogId = -1;
        if(!jobLogDetailsManagement.getIfEtlIsRunning()) {
        	 jobLogId = jobLogDetailsManagement.insertJobLog(entityId, entityType, CommonConstants.USER_RANKING_JOB_NAME, CommonConstants.STATUS_RUNNING);
        	 LOGGER.info("Calling method to trigger user ranking etl.");
        	 jobLogDetailsManagement.recalEtl(entityId, jobLogId);
        }else {
        		LOGGER.warn("Found One running job. Can't Trigger user ranking ETL.");
        }
        return new Gson().toJson( jobLogId );
    }
    
    

}
