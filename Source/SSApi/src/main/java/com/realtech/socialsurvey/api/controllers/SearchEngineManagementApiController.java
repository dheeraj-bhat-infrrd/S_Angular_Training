package com.realtech.socialsurvey.api.controllers;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.realtech.socialsurvey.api.transformers.AdvancedSearchTabVOTransformer;
import com.realtech.socialsurvey.api.transformers.SearchEngineVOTransformer;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.LOSearchEngine;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.searchengine.SearchEngineManagementServices;
import com.realtech.socialsurvey.core.vo.AdvancedSearchVO;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/v1")
public class SearchEngineManagementApiController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SearchEngineManagementApiController.class);
	
	@Autowired
	private SearchEngineManagementServices searchEngineService;
	
	@Autowired
	private SearchEngineVOTransformer searchEngineVOTransformer;
	
	@Autowired
	private AdvancedSearchTabVOTransformer advancedSearchTabVOTransformer;
	
	@Autowired 
	private OrganizationManagementService organizationManagementService;
	
	private String sucessStringForResponse = "Sucess";
	
	 //create an api to update addresses from higher to lower hierarchies
    @RequestMapping ( value = "/heirarchy/address/update/{companyId}", method = RequestMethod.POST)
    @ApiOperation ( value = "update the lower hierarchies of a company with address")
    public ResponseEntity<String>  updateHierarchyAddress(@PathVariable long companyId){
        LOGGER.info( "Updating the lower hierarchies of a company with address for companyId:{}",companyId );
        searchEngineService.updateHierarchyAddressForCompany( companyId );
        return new ResponseEntity<>( sucessStringForResponse, HttpStatus.OK );
    }
    
    //create api to update address for agent
    @RequestMapping ( value = "/lower/hierarchy/{entityType}/{entityId}", method = RequestMethod.POST)
    @ApiOperation ( value = "update the lower hierarchies of given entity with address")
    public ResponseEntity<String>  updateHierarchyAddress(@PathVariable String entityType, @PathVariable long entityId, @Valid @RequestBody ContactDetailsSettings contactDetails){
        LOGGER.info( "Updating the lower hierarchies for entityTYpe:{} , entityId:{}",entityType,entityId );
        try {
        	searchEngineService.updateAddressForAgents(entityType, entityId, contactDetails);
            return new ResponseEntity<>( sucessStringForResponse, HttpStatus.OK );
        } catch (InvalidInputException e) {
			return new ResponseEntity<>("Invalid input for entity type", HttpStatus.BAD_REQUEST);
		}
    }
    
    //create api to update address for agent
    @RequestMapping ( value = "/add/address", method = RequestMethod.POST)
    @ApiOperation ( value = "insert address while adding individual")
    public ResponseEntity<String>  updateAddressForAgentWhileAddingIndividual(long userId, long regionId, long branchId){
        LOGGER.info( "Updating the address while adding individual under regionId:{} , branchId:{}",regionId,branchId );
        searchEngineService.updateAddressForAgentWhileAddingIndividual(userId,regionId,branchId);
        return new ResponseEntity<>( sucessStringForResponse, HttpStatus.OK );
    }
    

    //create api to update address for agent
    @RequestMapping ( value = "/individual/address", method = RequestMethod.POST)
    @ApiOperation ( value = "update the agent address for id")
    public ResponseEntity<String>  updateAddressForAgentId( long userId){
        LOGGER.info( "Updating Address for agentId : {} ",userId);
        searchEngineService.updateAddressForAgentId(userId);
        return new ResponseEntity<>( sucessStringForResponse, HttpStatus.OK );
    }
    
    //create api to update address for agent
    @RequestMapping ( value = "/primary/change/address", method = RequestMethod.POST)
    @ApiOperation ( value = "update the agent address for primary change")
    public ResponseEntity<String>  updateAddressForAgentWhilePrimaryChange(long userId){
        LOGGER.info( "Updating Address for agent when primary changes");
        searchEngineService.updateAddressForAgentWhilePrimaryChange(userId);
        return new ResponseEntity<>( sucessStringForResponse, HttpStatus.OK );
    }
    
    //one time job
    //update company ,region, branch whose location's haven't been updated
    @RequestMapping ( value = "/missed/users", method = RequestMethod.POST)
    @ApiOperation ( value = "update the agents with their own address")
    public ResponseEntity<String>  updatelocForUsers(){
        LOGGER.info( "update user's (company admin , region admin ,branch admin and users) who have no latlng updates");
        searchEngineService.updatelocForUsersWithLatLngNotUpdated();
        return new ResponseEntity<>( sucessStringForResponse, HttpStatus.OK );
    }
    
    //one time job
	@RequestMapping(value = "/locating/{entityType}", method = RequestMethod.GET)
	@ApiOperation(value = "update the agents with their own address")
	public ResponseEntity<?> getloc(double longitude, double latitude, double distanceInmiles, @PathVariable String entityType) {
		LOGGER.info("Getting agent's near given lat,lng");
		// convert to api response object
		try {
			return new ResponseEntity<>(searchEngineVOTransformer
					.transformDomainObjectToApiResponse(searchEngineService.nearestToLoc(longitude, latitude, distanceInmiles, entityType)), HttpStatus.OK);
		} catch (InvalidInputException e) {
			return new ResponseEntity<>("Invalid input for entity type", HttpStatus.BAD_REQUEST);
		}
		
	}
	
	@RequestMapping(value = "/applosetting", method = RequestMethod.GET)
	@ApiOperation(value = "get application settings")
	public ResponseEntity<?> getApplicationLOSetttings() {
		LOGGER.info("Getting Application settings");
		// convert to api response object
		LOSearchEngine loSearchEngine =searchEngineService.getLoSearchSettings();
		try {
			return new ResponseEntity<>( advancedSearchTabVOTransformer.transformDomainObjectToApiResponse(loSearchEngine, organizationManagementService.getAllVerticalsMaster()) , HttpStatus.OK);
		} catch (InvalidInputException e) {
			return new ResponseEntity<>("Empty vertical List", HttpStatus.BAD_REQUEST);
		}
	}
	
	// one time job
	@RequestMapping(value = "/searchresults", method = RequestMethod.POST)
	@ApiOperation(value = "get Search Results for the given criteria")
	public ResponseEntity<?> getSearchResults(@Valid @RequestBody AdvancedSearchVO advancedSearchVO) {
		LOGGER.info("get Search Results for the given criteria");
		// convert to api response object
		return new ResponseEntity<>(
				searchEngineVOTransformer.transformDomainObjectToApiResponse(
						searchEngineService.getSearchResults(advancedSearchVO),
						searchEngineService.getCollectionFromProfile(advancedSearchVO.getProfileFilter())),
				HttpStatus.OK);
	}
	
	// one time job
	@RequestMapping(value = "/nearme", method = RequestMethod.GET)
	@ApiOperation(value = "get Suggestion for city and county")
	public ResponseEntity<?> getNearMe(String searchString,int startIndex, int batchSize, boolean onlyUsFilter) {
		LOGGER.info("get Suggestion for city and county");
		// convert to api response object
		return new ResponseEntity<>(
				searchEngineService.getSuggestionForNearMe(searchString,startIndex,batchSize,onlyUsFilter), HttpStatus.OK);
	}
	
	// one time job
	@RequestMapping(value = "/searchresults/count", method = RequestMethod.POST)
	@ApiOperation(value = "get Search Results for the given criteria")
	public ResponseEntity<?> getSearchResultsCount(@Valid @RequestBody AdvancedSearchVO advancedSearchVO) {
		LOGGER.info("get Search Results Count for the given criteria");
		// convert to api response object
		return new ResponseEntity<>(searchEngineService.getSearchResultsCount(advancedSearchVO), HttpStatus.OK);
	}
	
}
