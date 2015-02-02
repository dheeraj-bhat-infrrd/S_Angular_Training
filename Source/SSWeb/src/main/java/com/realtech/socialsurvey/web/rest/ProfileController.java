package com.realtech.socialsurvey.web.rest;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.google.gson.Gson;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.CompanyProfile;
import com.realtech.socialsurvey.core.entities.GenericResponse;
import com.realtech.socialsurvey.core.exception.BaseRestException;
import com.realtech.socialsurvey.core.exception.CompanyProfilePreconditionFailureErrorCode;
import com.realtech.socialsurvey.core.exception.InputValidationException;
import com.realtech.socialsurvey.core.exception.InternalServerException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.ProfileServiceErrorCode;
import com.realtech.socialsurvey.core.exception.RestErrorResponse;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;

@Controller
@RequestMapping(value = "/profile")
public class ProfileController {

	private static final Logger LOG = LoggerFactory.getLogger(ProfileController.class);

	@Autowired
	private OrganizationManagementService organizationManagementService;

	@ResponseBody
	@Produces(MediaType.APPLICATION_JSON)
	@RequestMapping(value = "/company/{profileName}")
	public Response getCompanyProfile(@PathVariable String profileName) {
		LOG.info("Service to get company profile called");
		GenericResponse genericResponse = new GenericResponse();
		Response response = null;
		try {
			if (profileName == null || profileName.isEmpty()) {
				throw new InputValidationException(new CompanyProfilePreconditionFailureErrorCode(
						"Company profile name is not specified for getting company profile"),
						"Company profile name is not specified for getting company profile");
			}
			CompanyProfile companyProfile = null;
			try {
				companyProfile = organizationManagementService.getCompanyProfileByProfileName(profileName);
				genericResponse.setResult(companyProfile);
				response = Response.ok(new Gson().toJson(genericResponse)).build();
			}
			catch (InvalidInputException e) {
				throw new InternalServerException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_COMPANY_PROFILE_SERVICE_FAILURE,
						CommonConstants.SERVICE_CODE_COMPANY_PROFILE, "Error occured while fetching company profile"), e.getMessage());
			}
		}
		catch (BaseRestException e) {
			response = getErrorResponse(e);
		}
		LOG.info("Service to get company profile executed successfully");
		return response;
	}

	/**
	 * Method to get the error response object from base rest exception
	 * 
	 * @param ex
	 * @return
	 */
	private Response getErrorResponse(BaseRestException ex) {
		LOG.debug("Resolve Error Response");
		Status httpStatus = resolveHttpStatus(ex);
		RestErrorResponse errorResponse = ex.transformException(httpStatus.getStatusCode());
		Response response = Response.status(httpStatus).entity(new Gson().toJson(errorResponse)).build();
		return response;
	}

	/**
	 * Method to get the http status based on the exception type
	 * 
	 * @param ex
	 * @return
	 */
	private Status resolveHttpStatus(BaseRestException ex) {
		LOG.debug("Resolving http status");
		Status httpStatus = Status.INTERNAL_SERVER_ERROR;
		if (ex instanceof InputValidationException) {
			httpStatus = Status.UNAUTHORIZED;
		}
		else if (ex instanceof InternalServerException) {
			httpStatus = Status.INTERNAL_SERVER_ERROR;
		}
		LOG.debug("Resolved http status to " + httpStatus.getStatusCode());
		return httpStatus;
	}
}
