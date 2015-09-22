package com.realtech.socialsurvey.web.rest;

import java.util.List;
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
import com.realtech.socialsurvey.core.entities.BranchSettings;
import com.realtech.socialsurvey.core.entities.BreadCrumb;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.exception.BaseRestException;
import com.realtech.socialsurvey.core.exception.InputValidationException;
import com.realtech.socialsurvey.core.exception.InternalServerException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.ProfileServiceErrorCode;
import com.realtech.socialsurvey.core.exception.RestErrorResponse;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;

@Controller
@RequestMapping(value = "/breadcrumb")
public class BreadCrumbController {
	private static final Logger LOG = LoggerFactory.getLogger(BreadCrumbController.class);
	@Autowired
	private ProfileManagementService profileManagementService;

	@Autowired
    private OrganizationManagementService organizationManagementService;
	
	@Autowired
	private UserManagementService userManagementService;

	/**
	 * Service to get breadcrumb of  verticalName
	 * 
	 * @param individualProfileName
	 * @return
	 * @throws ProfileNotFoundException
	 * @throws NoRecordsFetchedException
	 */
	@ResponseBody
	@RequestMapping(value = "/{verticalName}")
	public Response getCompanyList(@PathVariable String verticalName) throws ProfileNotFoundException, NoRecordsFetchedException {
		LOG.info("Service to get breadcrumb of  verticalName : " + verticalName);
		Response response = null;
		try {
			if (verticalName == null || verticalName.isEmpty()) {
				throw new InputValidationException(new ProfileServiceErrorCode(
						CommonConstants.ERROR_CODE_INDIVIDUAL_PROFILE_SERVICE_PRECONDITION_FAILURE, CommonConstants.SERVICE_CODE_INDIVIDUAL_PROFILE,
						"verticalName is invalid"), "verticalName  is null or empty");
			}
			List<OrganizationUnitSettings> companyNameList = null;
			try {
				companyNameList = profileManagementService.getCompanyList(verticalName);
				String json = new Gson().toJson(companyNameList);
				LOG.debug("individualProfile breadCrumb json : " + json);
				response = Response.ok(json).build();
			}
			catch (InvalidInputException e) {
				throw new InternalServerException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_INDIVIDUAL_PROFILE_SERVICE_FAILURE,
						CommonConstants.SERVICE_CODE_INDIVIDUAL_PROFILE, "verticalName  is invalid"), e.getMessage());
			}
		}
		catch (BaseRestException e) {
			response = getErrorResponse(e);
		}
		LOG.info("Service to get breadcrumb of verticalName finished");
		return response;

	}

	/**
	 * Service to get the breadcrumb of an individual
	 * 
	 * @param individualProfileName
	 * @return
	 * @throws ProfileNotFoundException
	 * @throws NoRecordsFetchedException
	 */
	@ResponseBody
	@RequestMapping(value = "/individual/{iden}")
	public Response getIndividualBreadCrumb(@PathVariable Long iden) throws ProfileNotFoundException, NoRecordsFetchedException {
		LOG.info("Service to get breadcrumb of  individualProfileName with id : " + iden);
		Response response = null;
		try {
			if (iden < 0) {
				throw new InputValidationException(new ProfileServiceErrorCode(
						CommonConstants.ERROR_CODE_INDIVIDUAL_PROFILE_SERVICE_PRECONDITION_FAILURE, CommonConstants.SERVICE_CODE_INDIVIDUAL_PROFILE,
						"Profile name for individual is invalid"), "individual profile name is null or empty");
			}
			try {
				List<BreadCrumb> userBreadCrumb = profileManagementService.getIndividualsBreadCrumb(iden);
				String json = new Gson().toJson(userBreadCrumb);
				LOG.debug("individualProfile breadCrumb json : " + json);
				response = Response.ok(json).build();
			}
			catch (InvalidInputException e) {
				throw new InternalServerException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_INDIVIDUAL_PROFILE_SERVICE_FAILURE,
						CommonConstants.SERVICE_CODE_INDIVIDUAL_PROFILE, "Profile name for individual is invalid"), e.getMessage());
			}
		}
		catch (BaseRestException e) {
			response = getErrorResponse(e);
		}
		LOG.info("Service to get breadcrumb of individual finished");
		return response;

	}

	/**
	 * Service to get region breadcrumb based on company profile name and region profile name
	 * 
	 * @param companyProfileName
	 * @param regionProfileName
	 * @return
	 * @throws ProfileNotFoundException
	 * @throws NoRecordsFetchedException
	 */

	@ResponseBody
	@RequestMapping(value = "/region/{regionId}")
	public Response getRegionBreadCrumb(@PathVariable Long regionId)
			throws ProfileNotFoundException, NoRecordsFetchedException {
		LOG.info("Service to get breadcrumb of  regionId:" + regionId);
		Response response = null;
		try {

			if (regionId == null) {
				throw new InputValidationException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_REGION_PROFILE_PRECONDITION_FAILURE,
						CommonConstants.SERVICE_CODE_REGION_PROFILE, "Profile name for region is invalid"), "region id is invalid");
			}
			OrganizationUnitSettings regionProfile = null;
			try {
				regionProfile = organizationManagementService.getRegionSettings(regionId);
				if (regionProfile == null) {
					throw new ProfileNotFoundException("No records found ");
				}
				List<BreadCrumb> regionBreadCrumb = profileManagementService.getRegionsBreadCrumb(regionProfile);
				String json = new Gson().toJson(regionBreadCrumb);
				LOG.debug("regionProfile json : " + json);
				response = Response.ok(json).build();
			}
			catch (InvalidInputException e) {
				throw new InternalServerException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_REGION_PROFILE_SERVICE_FAILURE,
						CommonConstants.SERVICE_CODE_REGION_PROFILE, "Error occured while fetching region breadCrumb"), e.getMessage());
			}
		}
		catch (BaseRestException e) {
			response = getErrorResponse(e);
		}
		LOG.info("Service to get breadcrumb of  regionProfileName executed successfully");
		return response;
	}

	/**
	 * Service to get branch breadcrumb based on company profile name and branch profile name
	 * 
	 * @param companyProfileName
	 * @param branchProfileName
	 * @return
	 * @throws ProfileNotFoundException
	 * @throws NoRecordsFetchedException
	 */
	@ResponseBody
	@RequestMapping(value = "/branch/{branchId}")
	public Response getBranchBreadCrumb(@PathVariable Long branchId)
			throws ProfileNotFoundException, NoRecordsFetchedException {
		LOG.info("Service to get branch breadcrumb called for branchId:" + branchId);
		Response response = null;
		try {
			if (branchId == null) {
				throw new InputValidationException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_BRANCH_PROFILE_PRECONDITION_FAILURE,
						CommonConstants.SERVICE_CODE_BRANCH_PROFILE, "Profile name for company is invalid"), "company profile name is null or empty");
			}
			BranchSettings branchProfile = null;
			try {
				branchProfile = organizationManagementService.getBranchSettings(branchId);
				if (branchProfile == null) {
					throw new ProfileNotFoundException("No records found ");
				}

				List<BreadCrumb> branchBreadCrumb = profileManagementService.getBranchsBreadCrumb(branchProfile.getOrganizationUnitSettings());
				String json = new Gson().toJson(branchBreadCrumb);
				LOG.debug("branchProfile json : " + json);
				response = Response.ok(json).build();
			}
			catch (InvalidInputException e) {
				throw new InternalServerException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_BRANCH_PROFILE_SERVICE_FAILURE,
						CommonConstants.SERVICE_CODE_BRANCH_PROFILE, "Error occured while fetching branch profile"), e.getMessage());
			}

		}
		catch (BaseRestException e) {
			response = getErrorResponse(e);
		}
		LOG.info("SService to get branch breadcrumb  executed successfully");
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
