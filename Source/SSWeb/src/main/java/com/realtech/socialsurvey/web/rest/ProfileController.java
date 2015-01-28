package com.realtech.socialsurvey.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;

@Controller
@RequestMapping(value = "/profile")
public class ProfileController {

	private static final Logger LOG = LoggerFactory.getLogger(ProfileController.class);

	@ResponseBody
	@RequestMapping(value = "/company/{companyName}")
	public String getCompanyProfile(@PathVariable String companyName) {
		LOG.info("Service to get company profile called");
		try {
			if (companyName == null || companyName.isEmpty()) {
				throw new InvalidInputException("Company name is not specified for getting company profile");
			}
			//TODO implement this
		}
		catch (NonFatalException e) {

		}

		LOG.info("Service to get company profile executed successfully");
		return companyName;

	}

}
