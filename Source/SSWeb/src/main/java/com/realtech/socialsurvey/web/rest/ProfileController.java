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
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.BaseRestException;
import com.realtech.socialsurvey.core.exception.CompanyProfilePreconditionFailureErrorCode;
import com.realtech.socialsurvey.core.exception.InputValidationException;
import com.realtech.socialsurvey.core.exception.InternalServerException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.ProfileServiceErrorCode;
import com.realtech.socialsurvey.core.exception.RestErrorResponse;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;

/**
 * JIRA:SS-117 by RM02 Class with rest services for fetching various profiles
 */
@Controller
@RequestMapping(value = "/profile")
public class ProfileController {

	private static final Logger LOG = LoggerFactory.getLogger(ProfileController.class);

	@Autowired
	private OrganizationManagementService organizationManagementService;

	@Autowired
	private ProfileManagementService profileManagementService;

	/**
	 * Service to get company details along with all regions based on profile name
	 * 
	 * @param profileName
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/{profileName}")
	public Response getCompanyProfile(@PathVariable String profileName) {
		LOG.info("Service to get company profile called for profileName :" + profileName);
		Response response = null;
		try {
			if (profileName == null || profileName.isEmpty()) {
				throw new InputValidationException(new CompanyProfilePreconditionFailureErrorCode(
						"Company profile name is not specified for getting company profile"),
						"Company profile name is not specified for getting company profile");
			}
			OrganizationUnitSettings companyProfile = null;
			try {
				companyProfile = profileManagementService.getCompanyProfileByProfileName(profileName);
				String json = new Gson().toJson(companyProfile);
				LOG.debug("companyProfile json : " + json);
				response = Response.ok(json).build();
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
	 * Service to get region profile based on company profile name and region profile name
	 * 
	 * @param companyProfileName
	 * @param regionProfileName
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/{companyProfileName}/region/{regionProfileName}")
	public Response getRegionProfile(@PathVariable String companyProfileName, @PathVariable String regionProfileName) {
		LOG.info("Service to get region profile called for regionProfileName:" + regionProfileName);
		Response response = null;
		try {
			if (companyProfileName == null || companyProfileName.isEmpty()) {
				throw new InputValidationException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_REGION_PROFILE_PRECONDITION_FAILURE,
						CommonConstants.SERVICE_CODE_REGION_PROFILE, "Profile name for company is invalid"), "company profile name is null or empty");
			}
			if (regionProfileName == null || regionProfileName.isEmpty()) {
				throw new InputValidationException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_REGION_PROFILE_PRECONDITION_FAILURE,
						CommonConstants.SERVICE_CODE_REGION_PROFILE, "Profile name for region is invalid"), "region profile name is null or empty");
			}
			OrganizationUnitSettings regionProfile = null;
			try {
				regionProfile = profileManagementService.getRegionByProfileName(companyProfileName, regionProfileName);
				String json = new Gson().toJson(regionProfile);
				LOG.debug("regionProfile json : " + json);
				response = Response.ok(json).build();
			}
			catch (InvalidInputException e) {
				throw new InternalServerException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_REGION_PROFILE_SERVICE_FAILURE,
						CommonConstants.SERVICE_CODE_REGION_PROFILE, "Error occured while fetching region profile"), e.getMessage());
			}
		}
		catch (BaseRestException e) {
			response = getErrorResponse(e);
		}
		LOG.info("Service to get region profile executed successfully");
		return response;
	}

	/**
	 * Service to get branch based on company profile name and branch profile name
	 * 
	 * @param companyProfileName
	 * @param branchProfileName
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/{companyProfileName}/branch/{branchProfileName}")
	public Response getBranchProfile(@PathVariable String companyProfileName, @PathVariable String branchProfileName) {
		LOG.info("Service to get branch profile called for regionProfileName:" + branchProfileName);
		Response response = null;
		try {
			if (companyProfileName == null || companyProfileName.isEmpty()) {
				throw new InputValidationException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_BRANCH_PROFILE_PRECONDITION_FAILURE,
						CommonConstants.SERVICE_CODE_BRANCH_PROFILE, "Profile name for company is invalid"), "company profile name is null or empty");
			}
			if (branchProfileName == null || branchProfileName.isEmpty()) {
				throw new InputValidationException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_BRANCH_PROFILE_PRECONDITION_FAILURE,
						CommonConstants.SERVICE_CODE_BRANCH_PROFILE, "Profile name for branch is invalid"), "branch profile name is null or empty");
			}
			OrganizationUnitSettings branchProfile = null;
			try {
				branchProfile = profileManagementService.getBranchByProfileName(companyProfileName, branchProfileName);
				String json = new Gson().toJson(branchProfile);
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
		LOG.info("Service to get branch profile executed successfully");
		return response;
	}

	/**
	 * Service to fetch all regions for company whose profile name is specified
	 * 
	 * @param companyProfileName
	 * @return
	 * @throws InvalidInputException
	 */
	@ResponseBody
	@RequestMapping(value = "/{companyProfileName}/regions")
	public Response getRegionsForCompany(@PathVariable String companyProfileName) throws InvalidInputException {
		LOG.info("Service to get regions for company called for companyProfileName:" + companyProfileName);
		Response response = null;
		try {
			if (companyProfileName == null || companyProfileName.isEmpty()) {
				throw new InputValidationException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_REGION_FETCH_PRECONDITION_FAILURE,
						CommonConstants.SERVICE_CODE_FETCH_ALL_REGIONS, "Profile name for company is invalid"),
						"company profile name is null or empty while fetching all regions for a company");
			}
			List<Region> regions = null;
			try {
				regions = organizationManagementService.getRegionsForCompany(companyProfileName);
				String json = new Gson().toJson(regions);
				LOG.debug("regions json : " + json);
				response = Response.ok(json).build();
			}
			catch (InvalidInputException e) {
				throw new InternalServerException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_REGION_FETCH_SERVICE_FAILURE,
						CommonConstants.SERVICE_CODE_FETCH_ALL_REGIONS, "Error occured while fetching regions under a company"), e.getMessage());
			}
		}
		catch (BaseRestException e) {
			response = getErrorResponse(e);
		}
		LOG.info("Service to get regions for company excecuted successfully");
		return response;
	}

	/**
	 * Service to fetch all branches linked directly to a company
	 * 
	 * @param companyProfileName
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/{companyProfileName}/branches")
	public Response getBranchesForCompany(@PathVariable String companyProfileName) {
		LOG.info("Service to get all branches of company called");
		Response response = null;
		try {
			if (companyProfileName == null || companyProfileName.isEmpty()) {
				throw new InputValidationException(new ProfileServiceErrorCode(
						CommonConstants.ERROR_CODE_COMPANY_BRANCHES_FETCH_PRECONDITION_FAILURE, CommonConstants.SERVICE_CODE_FETCH_COMPANY_BRANCHES,
						"Profile name for company is invalid"),
						"company profile name is null or empty while fetching all branches directly under a company");
			}
			LOG.debug("Calling services to fetch all branches linked directly to company:" + companyProfileName);
			List<Branch> branches = null;
			try {
				branches = organizationManagementService.getBranchesUnderCompany(companyProfileName);
				String json = new Gson().toJson(branches);
				LOG.debug("regions json : " + json);
				response = Response.ok(json).build();
			}
			catch (InvalidInputException | NoRecordsFetchedException e) {
				throw new InternalServerException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_COMPANY_BRANCHES_FETCH_SERVICE_FAILURE,
						CommonConstants.SERVICE_CODE_FETCH_COMPANY_BRANCHES, "Something went wrong while fetching branches under company"),
						e.getMessage());
			}

		}
		catch (BaseRestException e) {
			response = getErrorResponse(e);
		}

		LOG.info("Service to get all branches of company executed successfully");
		return response;
	}

	/**
	 * Service to fetch all individuals linked directly to a company
	 * 
	 * @param companyProfileName
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/{companyProfileName}/individuals")
	public Response getIndividualsForCompany(@PathVariable String companyProfileName) {
		LOG.info("Service to get all individuals of company called");
		Response response = null;
		try {
			if (companyProfileName == null || companyProfileName.isEmpty()) {
				throw new InputValidationException(new ProfileServiceErrorCode(
						CommonConstants.ERROR_CODE_COMPANY_INDIVIDUALS_FETCH_PRECONDITION_FAILURE,
						CommonConstants.SERVICE_CODE_FETCH_COMPANY_INDIVIDUALS, "Profile name for company is invalid"),
						"company profile name is null or empty while fetching all individuals for a company");
			}
			List<User> users = null;
			try {
				users = profileManagementService.getIndividualsForCompany(companyProfileName);
				String json = new Gson().toJson(users);
				LOG.debug("individuals json : " + json);
				response = Response.ok(json).build();
			}
			catch (InvalidInputException | NoRecordsFetchedException e) {
				throw new InputValidationException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_COMPANY_INDIVIDUALS_FETCH_SERVICE_FAILURE,
						CommonConstants.SERVICE_CODE_FETCH_COMPANY_INDIVIDUALS, "Error occurred while fetching individuals for company"),
						e.getMessage());
			}
		}
		catch (BaseRestException e) {
			response = getErrorResponse(e);
		}

		LOG.info("Service to get all individuals of company excecuted successfully");
		return response;
	}

	/**
	 * Service to fetch all the branches inside a region of company
	 * 
	 * @param companyProfileName
	 * @param regionProfileName
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/{companyProfileName}/region/{regionProfileName}/branches")
	public Response getBranchesForRegion(@PathVariable String companyProfileName, @PathVariable String regionProfileName) {
		LOG.info("Service to fetch all the branches inside a region of company called");
		Response response = null;
		try {
			if (companyProfileName == null || companyProfileName.isEmpty()) {
				throw new InputValidationException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_REGION_BRANCHES_FETCH_PRECONDITION_FAILURE,
						CommonConstants.SERVICE_CODE_FETCH_REGION_BRANCHES, "Profile name for company is invalid"),
						"company profile name is null or empty while fetching all branches for the region");
			}
			if (regionProfileName == null || regionProfileName.isEmpty()) {
				throw new InputValidationException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_REGION_BRANCHES_FETCH_PRECONDITION_FAILURE,
						CommonConstants.SERVICE_CODE_FETCH_REGION_BRANCHES, "Profile name for region is invalid"),
						"region profile name is null or empty while fetching all branches for the region");
			}
			List<Branch> branches = null;
			try {
				branches = organizationManagementService.getBranchesForRegion(companyProfileName, regionProfileName);
				String json = new Gson().toJson(branches);
				LOG.debug("branches json : " + json);
				response = Response.ok(json).build();
			}
			catch (InvalidInputException | NoRecordsFetchedException e) {
				throw new InternalServerException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_REGION_BRANCHES_FETCH_SERVICE_FAILURE,
						CommonConstants.SERVICE_CODE_FETCH_REGION_BRANCHES, "Something went wrong while fetching branches under region"),
						e.getMessage());
			}
		}
		catch (BaseRestException e) {
			response = getErrorResponse(e);
		}

		LOG.info("Service to fetch all the branches inside a region of company executed successfully");
		return response;
	}

	/**
	 * Service to get all individuals directly linked to the specified region
	 * 
	 * @param companyProfileName
	 * @param regionProfileName
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/{companyProfileName}/region/{regionProfileName}/individuals")
	public Response getIndividualsForRegion(@PathVariable String companyProfileName, @PathVariable String regionProfileName) {
		LOG.info("Service to get all individuals directly linked to the specified region called");
		Response response = null;
		try {
			if (companyProfileName == null || companyProfileName.isEmpty()) {
				throw new InputValidationException(new ProfileServiceErrorCode(
						CommonConstants.ERROR_CODE_REGION_INDIVIDUALS_FETCH_PRECONDITION_FAILURE,
						CommonConstants.SERVICE_CODE_FETCH_REGION_INDIVIDUALS, "Profile name for company is invalid"),
						"company profile name is null or empty while fetching all individuals for the region");
			}
			if (regionProfileName == null || regionProfileName.isEmpty()) {
				throw new InputValidationException(new ProfileServiceErrorCode(
						CommonConstants.ERROR_CODE_REGION_INDIVIDUALS_FETCH_PRECONDITION_FAILURE,
						CommonConstants.SERVICE_CODE_FETCH_REGION_INDIVIDUALS, "Profile name for company is invalid"),
						"region profile name is null or empty while fetching all individuals for the region");
			}
			List<User> users = null;
			try {
				users = profileManagementService.getIndividualsForRegion(companyProfileName, regionProfileName);
				String json = new Gson().toJson(users);
				LOG.debug("individuals json : " + json);
				response = Response.ok(json).build();
			}
			catch (InvalidInputException | NoRecordsFetchedException e) {
				throw new InternalServerException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_REGION_INDIVIDUALS_FETCH_SERVICE_FAILURE,
						CommonConstants.SERVICE_CODE_FETCH_REGION_INDIVIDUALS, "Something went wrong while fetching individuals under region"),
						e.getMessage());
			}
		}
		catch (BaseRestException e) {
			response = getErrorResponse(e);
		}
		LOG.info("Service to get all individuals directly linked to the specified region executed successfully");
		return response;
	}

	/**
	 * Service to get the list of users under the branch specified
	 * 
	 * @param companyProfileName
	 * @param branchProfileName
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/{companyProfileName}/branch/{branchProfileName}/individuals")
	public Response getIndividualsForBranch(@PathVariable String companyProfileName, @PathVariable String branchProfileName) {
		LOG.info("Servie to get all individuals in a branch called");
		Response response = null;
		try {
			if (companyProfileName == null || companyProfileName.isEmpty()) {
				throw new InputValidationException(new ProfileServiceErrorCode(
						CommonConstants.ERROR_CODE_BRANCH_INDIVIDUALS_FETCH_PRECONDITION_FAILURE,
						CommonConstants.SERVICE_CODE_FETCH_BRANCH_INDIVIDUALS, "Profile name for company is invalid"),
						"company profile name is null or empty while fetching all individuals for a branch");
			}
			if (branchProfileName == null || branchProfileName.isEmpty()) {
				throw new InputValidationException(new ProfileServiceErrorCode(
						CommonConstants.ERROR_CODE_BRANCH_INDIVIDUALS_FETCH_PRECONDITION_FAILURE,
						CommonConstants.SERVICE_CODE_FETCH_BRANCH_INDIVIDUALS, "Profile name for branch is invalid"),
						"branch profile name is null or empty while fetching all individuals for a branch");
			}
			List<User> users = null;
			try {
				users = profileManagementService.getIndividualsForBranch(companyProfileName, branchProfileName);
				String json = new Gson().toJson(users);
				LOG.debug("individuals under branch json : " + json);
				response = Response.ok(json).build();
			}
			catch (InvalidInputException e) {
				throw new InternalServerException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_BRANCH_INDIVIDUALS_FETCH_SERVICE_FAILURE,
						CommonConstants.SERVICE_CODE_FETCH_BRANCH_INDIVIDUALS, "Something went wrong while fetching individuals under branch"),
						e.getMessage());
			}
		}
		catch (BaseRestException e) {
			response = getErrorResponse(e);
		}

		LOG.info("Service to get all individuals in a branch executed successfully");
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
