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
import com.realtech.socialsurvey.core.entities.SocialFeedsActionUpdate;
import com.realtech.socialsurvey.core.entities.SocialMonitorFeedData;
import com.realtech.socialsurvey.core.entities.SocialMonitorMacro;
import com.realtech.socialsurvey.core.entities.SocialMonitorResponseData;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.socialmonitor.feed.SocialFeedService;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/v1")
public class SocialMonitorFeedsController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SocialMonitorFeedsController.class);

	private SocialFeedService socialFeedService;

	@Autowired
	public void setSocialFeedService(SocialFeedService socialFeedService) {
		this.socialFeedService = socialFeedService;
	}

	@RequestMapping(value = "/showsocialfeeds", method = RequestMethod.GET)
	@ApiOperation(value = "Get Social posts for Social monitor")
	public ResponseEntity<?> showStreamSocialPosts(long profileId, String profileLevel, int startIndex, int limit,
			String status, boolean flag, @RequestParam List<String> feedtype)
			throws InvalidInputException, SSApiException {
		LOGGER.info("Fetching the list of Social posts for social monitor");
		SocialMonitorResponseData socialMonitorResponseData;
		try {
			socialMonitorResponseData = socialFeedService.getAllSocialPosts(profileId, profileLevel, startIndex, limit,
					status, flag, feedtype);
		} catch (InvalidInputException ie) {
			LOGGER.error("Invalid input exception caught while fetching social feeds", ie);
			throw new SSApiException("Invalid input exception caught while fetching social feeds", ie);
		}

		return new ResponseEntity<>(socialMonitorResponseData, HttpStatus.OK);

	}

	@RequestMapping(value = "/updatesocialfeeds/action", method = RequestMethod.PUT)
	@ApiOperation(value = "Update Social posts for Social monitor for individual/bulk posts")
	public ResponseEntity<?> saveSocialFeedsForAction(@RequestBody SocialFeedsActionUpdate socialFeedsActionUpdate,
			long companyId) throws SSApiException, InvalidInputException {
		LOGGER.info("Updating the action of Social feeds for social monitor");
		try {
			socialFeedService.updateActionForFeeds(socialFeedsActionUpdate, companyId);
		} catch (InvalidInputException ie) {
			LOGGER.error("Invalid input exception caught while updating social feeds", ie);
			throw new SSApiException("Invalid input exception caught while updating social feeds", ie);
		}
		return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
	}

	@RequestMapping(value = "/socialfeedsmacro", method = RequestMethod.GET)
	@ApiOperation(value = "Get macros for a particular entity")
	public ResponseEntity<?> showMacrosForEntity(long companyId) throws InvalidInputException, SSApiException {
		LOGGER.info("Fetching the list of Macros for an entity");
		List<SocialMonitorMacro> socialMonitorMacros = new ArrayList<>();
		try {
			socialMonitorMacros = socialFeedService.getMacros(companyId);
		} catch (InvalidInputException ie) {
			LOGGER.error("Invalid input exception caught while fetching macros", ie);
			throw new SSApiException("Invalid input exception caught while fetching macros", ie);
		}
		return new ResponseEntity<>(socialMonitorMacros, HttpStatus.OK);

	}

	@RequestMapping(value = "/update/socialfeedsmacro", method = RequestMethod.PUT)
	@ApiOperation(value = "Update macros for a particular entity")
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

}
