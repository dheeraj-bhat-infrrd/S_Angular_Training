package com.realtech.socialsurvey.api.controllers;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.realtech.socialsurvey.api.exceptions.SSApiException;
import com.realtech.socialsurvey.core.entities.SegmentsVO;
import com.realtech.socialsurvey.core.entities.SocialFeedsActionUpdate;
import com.realtech.socialsurvey.core.entities.SocialMonitorMacro;
import com.realtech.socialsurvey.core.entities.SocialMonitorResponseData;
import com.realtech.socialsurvey.core.entities.SocialMonitorUsersVO;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.socialmonitor.feed.SocialFeedService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;




@RestController
@RequestMapping("/v1")
@Api ( value = "Social monitor APIs", description = "APIs for Social Monitor Feeds")
public class SocialMonitorFeedsController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SocialMonitorFeedsController.class);

	private SocialFeedService socialFeedService;

	@Autowired
	public void setSocialFeedService(SocialFeedService socialFeedService) {
		this.socialFeedService = socialFeedService;
	}
	

    @RequestMapping ( value = "/showsocialfeeds", method = RequestMethod.GET)
    @ApiOperation ( value = "Get Social posts for Social monitor", response = SocialMonitorResponseData.class)
    @ApiResponses ( value = { @ApiResponse ( code = 200, message = "Successfully fetched the list of social feeds") })
    public ResponseEntity<?> showStreamSocialPosts( int startIndex, int limit,
        @RequestParam ( value = "status", required = false) String status,
        @RequestParam ( value = "flag", required = false) boolean flag, @RequestParam List<String> feedtype,
        @RequestParam ( value = "companyId", required = false) Long companyId,
        @RequestParam ( value = "regionIds", required = false) List<Long> regionIds,
        @RequestParam ( value = "branchIds", required = false) List<Long> branchIds,
        @RequestParam ( value = "agentIds", required = false) List<Long> agentIds,
        @RequestParam ( value = "searchText", required = false) String searchText, boolean isCompanySet ) throws InvalidInputException, SSApiException
    {
		LOGGER.info("Fetching the list of Social posts for social monitor");
		SocialMonitorResponseData socialMonitorResponseData;
		try {
			socialMonitorResponseData = socialFeedService.getAllSocialPosts(startIndex, limit, status, flag, feedtype,
					companyId, regionIds, branchIds, agentIds, searchText, isCompanySet);
		} catch (InvalidInputException ie) {
			LOGGER.error("Invalid input exception caught while fetching social feeds", ie);
			throw new SSApiException("Invalid input exception caught while fetching social feeds", ie);
		}

		return new ResponseEntity<>(socialMonitorResponseData, HttpStatus.OK);

	}

	@RequestMapping(value = "/updatesocialfeeds/action", method = RequestMethod.PUT)
	@ApiOperation(value = "Update Social posts for Social monitor for individual/bulk posts", response = String.class)
	@ApiResponses(value = { @ApiResponse ( code = 200, message = "Successfully updated the action on a post")})
	public ResponseEntity<?> saveSocialFeedsForAction(@RequestBody SocialFeedsActionUpdate socialFeedsActionUpdate,
			@RequestParam(value = "companyId", required = false) Long companyId)
			throws SSApiException, InvalidInputException {
		LOGGER.info("Updating the action of Social feeds for social monitor");
		try {
			socialFeedService.updateActionForFeeds(socialFeedsActionUpdate, companyId);
		} catch (InvalidInputException ie) {
			LOGGER.error("Invalid input exception caught while updating social feeds", ie);
			throw new SSApiException("Invalid input exception caught while updating social feeds", ie);
		}
		return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
	}


    @RequestMapping ( value = "/socialfeedsmacro/company/{companyId}", method = RequestMethod.GET)
    @ApiOperation ( value = "Get macros for a particular entity", response = SocialMonitorMacro.class, responseContainer = "List")
    @ApiResponses ( value = { @ApiResponse ( code = 200, message = "Successfully fetched the list of macros") })
    public ResponseEntity<?> showMacrosForEntity( @PathVariable long companyId,
        @RequestParam ( value = "searchMacros", required = false) String searchMacros )
        throws InvalidInputException, SSApiException
    {
		LOGGER.info("Fetching the list of Macros for an entity");
		List<SocialMonitorMacro> socialMonitorMacros = new ArrayList<>();
		try {
			socialMonitorMacros = socialFeedService.getMacros(companyId, searchMacros);
		} catch (InvalidInputException ie) {
			LOGGER.error("Invalid input exception caught while fetching macros", ie);
			throw new SSApiException("Invalid input exception caught while fetching macros", ie);
		}
		return new ResponseEntity<>(socialMonitorMacros, HttpStatus.OK);

	}

	@RequestMapping(value = "/update/socialfeedsmacro", method = RequestMethod.POST)
	@ApiOperation(value = "Update macros for a particular entity", response = String.class)
	@ApiResponses(value = { @ApiResponse ( code = 200, message = "Successfully addded the macro")})
	public ResponseEntity<?> updateMacrosForEntity(@RequestBody SocialMonitorMacro socialMonitorMacro, long companyId)
			throws InvalidInputException, SSApiException {
		LOGGER.info("Updating the list of Macros for an entity");
		try {
			socialFeedService.updateMacrosForFeeds(socialMonitorMacro, companyId);
		} catch (InvalidInputException ie) {
			LOGGER.error("Invalid input exception caught while updating macros", ie);
			throw new SSApiException("Invalid input exception caught while updating macros", ie);
		}
		return new ResponseEntity<>("SUCCESS", HttpStatus.OK);

	}

	@RequestMapping(value = "/socialfeedsmacro/company/{companyId}/macro/{macroId}", method = RequestMethod.GET)
	@ApiOperation(value = "Get macro by macroId", response = SocialMonitorMacro.class)
	@ApiResponses(value = { @ApiResponse ( code = 200, message = "Successfully fetched the macro")})
	public ResponseEntity<?> getMacroById(@PathVariable Long companyId, @PathVariable String macroId)
			throws InvalidInputException, SSApiException {
		LOGGER.info("Fetching the Macro by Id");
		SocialMonitorMacro socialMonitorMacro;
		try {
			socialMonitorMacro = socialFeedService.getMacroById(macroId, companyId);
		} catch (InvalidInputException ie) {
			LOGGER.error("Invalid input exception caught while fetching macro", ie);
			throw new SSApiException("Invalid input exception caught while fetching macro", ie);
		}
		return new ResponseEntity<>(socialMonitorMacro, HttpStatus.OK);
	}

	@RequestMapping(value = "/segments/company/{companyId}", method = RequestMethod.GET)
	@ApiOperation(value = "Get regions and branches for a company", response = SegmentsVO.class)
	@ApiResponses(value = { @ApiResponse ( code = 200, message = "Successfully fetched the segments")})
	public ResponseEntity<?> getSegmentsByCompanyId(@PathVariable Long companyId)
			throws InvalidInputException, SSApiException {
		LOGGER.info("Fetching regions and branches for a company");
		SegmentsVO segmentsVO;
		try {
			segmentsVO = socialFeedService.getSegmentsByCompanyId(companyId);
		} catch (InvalidInputException ie) {
			LOGGER.error("Invalid input exception caught while fetching branches and regions", ie);
			throw new SSApiException("Invalid input exception caught while fetching branches and regions", ie);
		}
		return new ResponseEntity<>(segmentsVO, HttpStatus.OK);

	}

	@RequestMapping(value = "/users/company/{companyId}", method = RequestMethod.GET)
	@ApiOperation(value = "Get users for a company", response = SocialMonitorUsersVO.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse ( code = 200, message = "Successfully fetched the users")})
	public ResponseEntity<?> getUsersByCompanyId(@PathVariable Long companyId)
			throws InvalidInputException, SSApiException, ProfileNotFoundException {
		LOGGER.info("Fetching users for a company");
		List<SocialMonitorUsersVO> list = null;
		try {
			list = socialFeedService.getUsersByCompanyId(companyId);
		} catch (InvalidInputException ie) {
			LOGGER.error("Invalid input exception caught while fetching users", ie);
			throw new SSApiException("Invalid input exception caught while fetching users", ie);
		}
		return new ResponseEntity<>(list, HttpStatus.OK);

	}

}
