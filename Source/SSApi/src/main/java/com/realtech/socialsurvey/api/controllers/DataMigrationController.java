/**
 * 
 */
package com.realtech.socialsurvey.api.controllers;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.migration.DataMigrationService;
import io.swagger.annotations.ApiOperation;

/**
 * @author Subhrajit This is the API controller data migration activity.
 *
 */
@RestController
@RequestMapping("/v1")
public class DataMigrationController {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataMigrationController.class);

	@Autowired
	private DataMigrationService dataMigrationService;

	/**
	 * Method for data migration of MCQ to NPS question in mongo.
	 * 
	 * @param companyId
	 * @param question
	 * @return
	 * @throws InvalidInputException
	 * @throws IOException
	 */
	@RequestMapping(value = "/migrate/{companyId}/surveyresponse", method = RequestMethod.POST)
	@ApiOperation(value = "Migrate the survey response data for the question text and companyId.")
	public void migrateMCQtoNPSMongo(@PathVariable long companyId, Integer questionId,
			String question) throws InvalidInputException, IOException {

		LOGGER.info("API call to migrate MCQ answers to NPS answer started.");
		if (companyId > 0 && question != null && questionId > 0) {
			dataMigrationService.migrateMCQtoNPSMongo(companyId, questionId, question);
		} else {
			LOGGER.warn("Invalid company id or question text.");
			throw new InvalidInputException("Invalid company id or question text.");
		}
	}

}
