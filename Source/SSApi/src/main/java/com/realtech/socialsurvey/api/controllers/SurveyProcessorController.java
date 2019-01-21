package com.realtech.socialsurvey.api.controllers;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.realtech.socialsurvey.api.models.response.EntitySurveyStatsVO;
import com.realtech.socialsurvey.api.transformers.EntitySurveyStatsVOTransformer;
import com.realtech.socialsurvey.core.entities.LOSearchEngine;
import com.realtech.socialsurvey.core.entities.SurveyStats;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.searchengine.SearchEngineManagementServices;

import io.swagger.annotations.ApiOperation;
import retrofit.http.Path;
/**
 * 
 * @author rohitpatidar
 *
 */

@RestController
@RequestMapping ("/v1")
public class SurveyProcessorController 
{

    private static final Logger LOGGER = LoggerFactory.getLogger( SurveyProcessorController.class );
    
    @Autowired
    SearchEngineManagementServices searchEngineManagementServices;
    
    @Autowired
    private EntitySurveyStatsVOTransformer entitySurveyStatsVOTransformer;
    
    
    @RequestMapping(value = "{entityType}/{entityId}/surveystats", method = RequestMethod.GET)
	@ApiOperation(value = "Getting survey stats data for entities to process a completed survey.")
    public EntitySurveyStatsVO getSurveyStatsForEntity(@PathVariable("entityType") String entityType , @PathVariable("entityId") long entityId, HttpServletResponse response) 
    {
    		LOGGER.info("Method getSurveyStatsForEntity started for entityType {} and entityId {}", entityType, entityId);
    		try {
    			SurveyStats surveyStats = searchEngineManagementServices.getSurveyStatsByEntityId(entityType, entityId);
    			LOSearchEngine loSearchEngineSettings = searchEngineManagementServices.getLoSearchSettings();
    			
    			EntitySurveyStatsVO entitySurveyStatsVO = entitySurveyStatsVOTransformer.transformDomainObjectToApiResponse(surveyStats, loSearchEngineSettings);
    			return entitySurveyStatsVO;
    		} catch (InvalidInputException e) {
				LOGGER.error("Error in api call getSurveyStatsForEntity for entityType {} and entityId {} " , entityType, entityId, e);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return null;
    		}
    }
    
    
    @RequestMapping(value = "{entityType}/{entityId}/surveystats", method = RequestMethod.POST)
	@ApiOperation(value = "Getting survey stats data for entities to process a completed survey.")
    public boolean updateSurveyStatsForEntity(@PathVariable("entityType") String entityType , @PathVariable("entityId") long entityId, @RequestBody EntitySurveyStatsVO entitySurveyStatsVO, HttpServletResponse response ) 
    {
    		LOGGER.info("Method updateSurveyStatsForEntity started for entityType {} and entityId {}", entityType, entityId);
			try {
				SurveyStats surveyStats = entitySurveyStatsVOTransformer.transformApiRequestToDomainObject(entitySurveyStatsVO);
				searchEngineManagementServices.updateSurveyStatsByEntityId(entityType, entityId, surveyStats);
				LOGGER.info("Method updateSurveyStatsForEntity finished for entityType {} and entityId {}", entityType, entityId);
				response.setStatus(HttpServletResponse.SC_OK);
				return true;
			} catch (InvalidInputException e) {
				LOGGER.error("Error in api call getSurveyStatsForEntity for entityType {} and entityId {} " , entityType, entityId, e);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return false;
	    		}
      		
    }
    
    @RequestMapping(value = "/surveystats/refreshdata", method = RequestMethod.GET)
	@ApiOperation(value = "Getting survey stats data for entities to process a completed survey.")
    public boolean updateSurveyStatsForAllEntities( HttpServletResponse response) 
    {
    		LOGGER.info("Method updateSurveyStatsForAllEntities started");
			try {
				
				LOGGER.info("Method updateSurveyStatsForAllEntities finished");
				response.setStatus(HttpServletResponse.SC_OK);
				return searchEngineManagementServices.updateSurveyStatsForAllEntities();
			} catch (InvalidInputException e) {
				LOGGER.error("Error in api call getSurveyStatsForEntity", e);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return false;
	    		}
      		
    }
    
    
    @RequestMapping(value = "/surveystats/updatecompanyid", method = RequestMethod.GET)
	@ApiOperation(value = "Getting survey stats data for entities to process a completed survey.")
    public boolean updateCompanyIdForAllEntities( HttpServletResponse response) 
    {
    		LOGGER.info("Method updateCompanyIdForAllEntities started");
			try {
				
				LOGGER.info("Method updateCompanyIdForAllEntities finished");
				response.setStatus(HttpServletResponse.SC_OK);
				return searchEngineManagementServices.updateCompanyIdForAllEntities();
			} catch (InvalidInputException e) {
				LOGGER.error("Error in api call updateCompanyIdForAllEntities", e);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return false;
	    		}
      		
    }
    
    @RequestMapping(value = "/surveystats/updatehiddensection", method = RequestMethod.GET)
	@ApiOperation(value = "Getting survey stats data for entities to process a completed survey.")
    public boolean updateHiddenSectionForAgents( HttpServletResponse response) 
    {
    		LOGGER.info("Method updateHiddenSectionForAgents started");
			try {
				
				LOGGER.info("Method updateHiddenSectionForAgents finished");
				response.setStatus(HttpServletResponse.SC_OK);
				return searchEngineManagementServices.updateHiddenSectionForAllAgents();
			} catch (InvalidInputException e) {
				LOGGER.error("Error in api call updateHiddenSectionForAgents", e);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return false;
	    		}
      		
    }

}
