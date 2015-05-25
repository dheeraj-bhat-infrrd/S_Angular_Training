package com.realtech.socialsurvey.web.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.google.gson.Gson;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.entities.SocialPost;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.UserProfile;
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
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.VCardVersion;
import ezvcard.parameter.EmailType;
import ezvcard.parameter.TelephoneType;
import ezvcard.property.Kind;

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
				
				// aggregated social profile urls
				SocialMediaTokens agentTokens = profileManagementService.aggregateSocialProfiles(regionProfile, CommonConstants.REGION_ID);
				regionProfile.setSocialMediaTokens(agentTokens);
				
				String json = new Gson().toJson(regionProfile);
				LOG.debug("regionProfile json : " + json);
				response = Response.ok(json).build();
			}
			catch (InvalidInputException | NoRecordsFetchedException e) {
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
				
				// aggregated social profile urls
				SocialMediaTokens agentTokens = profileManagementService.aggregateSocialProfiles(branchProfile, CommonConstants.BRANCH_ID);
				branchProfile.setSocialMediaTokens(agentTokens);
				
				String json = new Gson().toJson(branchProfile);
				LOG.debug("branchProfile json : " + json);
				response = Response.ok(json).build();
			}
			catch (InvalidInputException | NoRecordsFetchedException e) {
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
	 * Service to get the profile of an individual
	 * 
	 * @param companyProfileName
	 * @param individualProfileName
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/individual/{individualProfileName}")
	public Response getIndividualProfile(@PathVariable String individualProfileName) {
		LOG.info("Service to get profile of individual called for individualProfileName : " + individualProfileName);
		Response response = null;
		try {
			if (individualProfileName == null || individualProfileName.isEmpty()) {
				throw new InputValidationException(new ProfileServiceErrorCode(
						CommonConstants.ERROR_CODE_INDIVIDUAL_PROFILE_SERVICE_PRECONDITION_FAILURE, CommonConstants.SERVICE_CODE_INDIVIDUAL_PROFILE,
						"Profile name for individual is invalid"), "individual profile name is null or empty");
			}
			OrganizationUnitSettings individualProfile = null;
			try {
				individualProfile = profileManagementService.getIndividualByProfileName(individualProfileName);
				
				// aggregated social profile urls
				SocialMediaTokens agentTokens = profileManagementService.aggregateSocialProfiles(individualProfile, CommonConstants.AGENT_ID);
				individualProfile.setSocialMediaTokens(agentTokens);
				
				String json = new Gson().toJson(individualProfile);
				LOG.debug("individualProfile json : " + json);
				response = Response.ok(json).build();
			}
			catch (InvalidInputException | NoRecordsFetchedException e) {
				throw new InternalServerException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_INDIVIDUAL_PROFILE_SERVICE_FAILURE,
						CommonConstants.SERVICE_CODE_INDIVIDUAL_PROFILE, "Profile name for individual is invalid"), e.getMessage());
			}
		}
		catch (BaseRestException e) {
			response = getErrorResponse(e);
		}
		LOG.info("Service to get profile of individual finished");
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
			List<AgentSettings> users = null;
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
	 * Service to fetch branches when regionId is provided
	 * 
	 * @param regionId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/region/{regionId}/branches")
	public Response getBranchesByRegionId(@PathVariable long regionId) {
		LOG.info("Service to fetch branches for a region called for regionId :" + regionId);
		Response response = null;
		try {
			if (regionId <= 0l) {
				throw new InputValidationException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_REGION_BRANCHES_FETCH_PRECONDITION_FAILURE,
						CommonConstants.SERVICE_CODE_FETCH_REGION_BRANCHES, "Region id is invalid"),
						"region id is invalid while fetching all branches for the region");
			}
			try {
				List<Branch> branches = organizationManagementService.getBranchesByRegionId(regionId);
				String json = new Gson().toJson(branches);
				LOG.debug("branches json : " + json);
				response = Response.ok(json).build();
			}
			catch (InvalidInputException e) {
				throw new InternalServerException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_REGION_BRANCHES_FETCH_SERVICE_FAILURE,
						CommonConstants.SERVICE_CODE_FETCH_REGION_BRANCHES, "Something went wrong while fetching individuals under region"),
						e.getMessage());
			}
		}
		catch (BaseRestException e) {
			response = getErrorResponse(e);
		}
		return response;
	}

	@ResponseBody
	@RequestMapping(value = "/branch/{branchId}/individuals")
	public Response getIndividualsByBranchId(@PathVariable long branchId) {
		LOG.info("Service to fetch individuals for branch called for branchId:" + branchId);
		Response response = null;
		try {
			if (branchId <= 0l) {
				throw new InputValidationException(new ProfileServiceErrorCode(
						CommonConstants.ERROR_CODE_BRANCH_INDIVIDUALS_FETCH_PRECONDITION_FAILURE,
						CommonConstants.SERVICE_CODE_FETCH_BRANCH_INDIVIDUALS, "Branch id is invalid"),
						"branch id is invalid while fetching all individuals for the branch");
			}
			try {
				List<AgentSettings> individuals = profileManagementService.getIndividualsByBranchId(branchId);
				String json = new Gson().toJson(individuals);
				LOG.debug("individuals json : " + json);
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
		LOG.info("Service to fetch individuals for branch executed successfully");
		return response;
	}

	/**
	 * Service to fetch individuals for the provided region id
	 * 
	 * @param regionId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/region/{regionId}/individuals")
	public Response getIndividualsByRegionId(@PathVariable long regionId) {
		LOG.info("Service to fetch individuals for region called for regionId:" + regionId);
		Response response = null;
		try {
			if (regionId <= 0l) {
				throw new InputValidationException(new ProfileServiceErrorCode(
						CommonConstants.ERROR_CODE_REGION_INDIVIDUALS_FETCH_PRECONDITION_FAILURE,
						CommonConstants.SERVICE_CODE_FETCH_REGION_INDIVIDUALS, "Region id is invalid"),
						"region id is invalid while fetching all individuals for the region");
			}
			try {
				List<AgentSettings> individuals = profileManagementService.getIndividualsByRegionId(regionId);
				String json = new Gson().toJson(individuals);
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
		LOG.info("Service to fetch individuals for region executed successfully");
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
			List<AgentSettings> users = null;
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
			List<AgentSettings> users = null;
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
	 * Service to fetch the reviews within the rating score specified
	 * 
	 * @param companyId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/company/{companyId}/reviews")
	public Response getReviewsForCompany(@PathVariable long companyId, @QueryParam(value = "minScore") Double minScore,
			@QueryParam(value = "maxScore") Double maxScore, @QueryParam(value = "start") Integer start,
			@QueryParam(value = "numRows") Integer numRows, @QueryParam(value = "sortCriteria") String sortCriteria) {
		LOG.info("Service to fetch reviews of company called for companyId:" + companyId + " ,minScore:" + minScore + " and maxscore:" + maxScore);
		Response response = null;
		try {
			if (companyId <= 0l) {
				throw new InputValidationException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_COMPANY_REVIEWS_FETCH_PRECONDITION_FAILURE,
						CommonConstants.SERVICE_CODE_COMPANY_REVIEWS, "Company id for company is invalid"),
						"company id is not valid while fetching all reviews for a company");
			}
			if (minScore == null) {
				minScore = CommonConstants.MIN_RATING_SCORE;
			}
			if (maxScore == null) {
				maxScore = CommonConstants.MAX_RATING_SCORE;
			}
			if (start == null) {
				start = -1;
			}
			if (numRows == null) {
				numRows = -1;
			}
			if(sortCriteria == null){
				sortCriteria = "date";
			}
			try {
				List<SurveyDetails> reviews = profileManagementService.getReviews(companyId, minScore, maxScore, start, numRows,
						CommonConstants.PROFILE_LEVEL_COMPANY, false, null, null, sortCriteria);
				String json = new Gson().toJson(reviews);
				LOG.debug("reviews json : " + json);
				response = Response.ok(json).build();
			}
			catch (InvalidInputException e) {
				throw new InternalServerException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_COMPANY_REVIEWS_FETCH_FAILURE,
						CommonConstants.SERVICE_CODE_COMPANY_REVIEWS, "Something went wrong while fetching reviews for a company"), e.getMessage());
			}
		}
		catch (BaseRestException e) {
			response = getErrorResponse(e);
		}

		LOG.info("Service to fetch reviews of company completed successfully");
		return response;
	}

	/**
	 * Service to fetch reviews for a region
	 * 
	 * @param regionId
	 * @param minScore
	 * @param maxScore
	 * @param start
	 * @param numRows
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/region/{regionId}/reviews")
	public Response getReviewsForRegion(@PathVariable long regionId, @QueryParam(value = "minScore") Double minScore,
			@QueryParam(value = "maxScore") Double maxScore, @QueryParam(value = "start") Integer start,
			@QueryParam(value = "numRows") Integer numRows, @QueryParam(value = "sortCriteria") String sortCriteria) {
		LOG.info("Service to fetch reviews of region called for regionId:" + regionId + " ,minScore:" + minScore + " and maxscore:" + maxScore);
		Response response = null;
		try {
			if (regionId <= 0l) {
				throw new InputValidationException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_REGION_REVIEWS_FETCH_PRECONDITION_FAILURE,
						CommonConstants.SERVICE_CODE_REGION_REVIEWS, "Region id for region is invalid"),
						"region id is not valid while fetching all reviews for a region");
			}
			if (minScore == null) {
				minScore = CommonConstants.MIN_RATING_SCORE;
			}
			if (maxScore == null) {
				maxScore = CommonConstants.MAX_RATING_SCORE;
			}
			if (start == null) {
				start = -1;
			}
			if (numRows == null) {
				numRows = -1;
			}
			if(sortCriteria == null){
				sortCriteria = "date";
			}
			try {
				List<SurveyDetails> reviews = profileManagementService.getReviews(regionId, minScore, maxScore, start, numRows,
						CommonConstants.PROFILE_LEVEL_REGION, false, null, null, sortCriteria);
				String json = new Gson().toJson(reviews);
				LOG.debug("reviews json : " + json);
				response = Response.ok(json).build();
			}
			catch (InvalidInputException e) {
				throw new InternalServerException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_REGION_REVIEWS_FETCH_FAILURE,
						CommonConstants.SERVICE_CODE_REGION_REVIEWS, "Something went wrong while fetching reviews for a region"), e.getMessage());
			}
		}
		catch (BaseRestException e) {
			response = getErrorResponse(e);
		}

		LOG.info("Service to fetch reviews of region completed successfully");
		return response;
	}

	/**
	 * Service to fetch average ratings for company
	 * 
	 * @param companyId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/company/{companyId}/ratings")
	public Response getAverageRatingForCompany(@PathVariable long companyId) {
		LOG.info("Service to get average rating of company called ");
		Response response = null;
		try {
			if (companyId <= 0l) {
				throw new InputValidationException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_AVERAGE_RATING_FETCH_PRECONDITION_FAILURE,
						CommonConstants.SERVICE_CODE_COMPANY_AVERAGE_RATINGS, "Company id for company is invalid"),
						"company id is not valid while fetching average ratings for a company");
			}
			try {
				double averageRating = profileManagementService.getAverageRatings(companyId, CommonConstants.PROFILE_LEVEL_COMPANY, false);
				String json = new Gson().toJson(averageRating);
				LOG.debug("averageRating json : " + json);
				response = Response.ok(json).build();
			}
			catch (InvalidInputException e) {
				throw new InternalServerException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_AVERAGE_RATING_FETCH_FAILURE,
						CommonConstants.SERVICE_CODE_COMPANY_AVERAGE_RATINGS, "Something went wrong while fetching average ratings for company"),
						e.getMessage());
			}
		}
		catch (BaseRestException e) {
			response = getErrorResponse(e);
		}
		LOG.info("Service to get average rating of company executed successfully ");
		return response;
	}

	/**
	 * Service to fetch count of reviews based on ratings specified
	 * 
	 * @param companyId
	 * @param minScore
	 * @param maxScore
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/company/{companyId}/reviewcount")
	public Response getReviewCountForCompany(@PathVariable long companyId, @QueryParam(value = "minScore") Double minScore,
			@QueryParam(value = "maxScore") Double maxScore) {
		LOG.info("Service to fetch the reviews count called for companyId :" + companyId + " ,minScore:" + minScore + " and maxScore:" + maxScore);
		Response response = null;
		try {
			if (companyId <= 0l) {
				throw new InputValidationException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_REVIEWS_COUNT_FETCH_PRECONDITION_FAILURE,
						CommonConstants.SERVICE_CODE_COMPANY_REVIEWS_COUNT, "Company id is invalid"),
						"company id is not valid while fetching reviews count for a company");
			}
			if (minScore == null) {
				minScore = CommonConstants.MIN_RATING_SCORE;
			}
			if (maxScore == null) {
				maxScore = CommonConstants.MAX_RATING_SCORE;
			}
			long reviewsCount = 0;
			try {
				reviewsCount = profileManagementService.getReviewsCount(companyId, minScore, maxScore, CommonConstants.PROFILE_LEVEL_COMPANY, false);
				String json = new Gson().toJson(reviewsCount);
				LOG.debug("reviews count json : " + json);
				response = Response.ok(json).build();
			}
			catch (InvalidInputException e) {
				throw new InternalServerException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_REVIEWS_COUNT_FETCH_FAILURE,
						CommonConstants.SERVICE_CODE_COMPANY_REVIEWS_COUNT, "Error occured while getting reviews count"), e.getMessage());
			}
		}
		catch (BaseRestException e) {
			response = getErrorResponse(e);
		}
		LOG.info("Service to fetch the reviews count executed successfully");
		return response;

	}

	/**
	 * Service to fetch average ratings for region
	 * 
	 * @param regionId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/region/{regionId}/ratings")
	public Response getAverageRatingForRegion(@PathVariable long regionId) {
		LOG.info("Service to get average rating of region called ");
		Response response = null;
		try {
			if (regionId <= 0l) {
				throw new InputValidationException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_AVERAGE_RATING_FETCH_PRECONDITION_FAILURE,
						CommonConstants.SERVICE_CODE_REGION_AVERAGE_RATINGS, "Region id for region is invalid"),
						"region id is not valid while fetching average ratings for a region");
			}
			try {
				double averageRating = profileManagementService.getAverageRatings(regionId, CommonConstants.PROFILE_LEVEL_REGION, false);
				String json = new Gson().toJson(averageRating);
				LOG.debug("averageRating json : " + json);
				response = Response.ok(json).build();
			}
			catch (InvalidInputException e) {
				throw new InternalServerException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_AVERAGE_RATING_FETCH_FAILURE,
						CommonConstants.SERVICE_CODE_REGION_AVERAGE_RATINGS, "Something went wrong while fetching average ratings for region"),
						e.getMessage());
			}
		}
		catch (BaseRestException e) {
			response = getErrorResponse(e);
		}
		LOG.info("Service to get average rating of region executed successfully ");
		return response;
	}

	/**
	 * Service to fetch review count for a region
	 * 
	 * @param regionId
	 * @param minScore
	 * @param maxScore
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/region/{regionId}/reviewcount")
	public Response getReviewCountForRegion(@PathVariable long regionId, @QueryParam(value = "minScore") Double minScore,
			@QueryParam(value = "maxScore") Double maxScore) {
		LOG.info("Service to fetch the reviews count called for regionId :" + regionId + " ,minScore:" + minScore + " and maxScore:" + maxScore);
		Response response = null;
		try {
			if (regionId <= 0l) {
				throw new InputValidationException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_REVIEWS_COUNT_FETCH_PRECONDITION_FAILURE,
						CommonConstants.SERVICE_CODE_REGION_REVIEWS_COUNT, "Region id is invalid"),
						"region id is not valid while fetching reviews count for a region");
			}
			if (minScore == null) {
				minScore = CommonConstants.MIN_RATING_SCORE;
			}
			if (maxScore == null) {
				maxScore = CommonConstants.MAX_RATING_SCORE;
			}
			long reviewsCount = 0;
			try {
				reviewsCount = profileManagementService.getReviewsCount(regionId, minScore, maxScore, CommonConstants.PROFILE_LEVEL_REGION, false);
				String json = new Gson().toJson(reviewsCount);
				LOG.debug("reviews count json : " + json);
				response = Response.ok(json).build();
			}
			catch (InvalidInputException e) {
				throw new InternalServerException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_REVIEWS_COUNT_FETCH_FAILURE,
						CommonConstants.SERVICE_CODE_REGION_REVIEWS_COUNT, "Error occured while getting reviews count"), e.getMessage());
			}
		}
		catch (BaseRestException e) {
			response = getErrorResponse(e);
		}
		LOG.info("Service to fetch the reviews count executed successfully");
		return response;
	}

	/**
	 * Service to fetch average ratings for branch
	 * 
	 * @param branchId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/branch/{branchId}/ratings")
	public Response getAverageRatingForBranch(@PathVariable long branchId) {
		LOG.info("Service to get average rating of branch called ");
		Response response = null;
		try {
			if (branchId <= 0l) {
				throw new InputValidationException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_AVERAGE_RATING_FETCH_PRECONDITION_FAILURE,
						CommonConstants.SERVICE_CODE_BRANCH_AVERAGE_RATINGS, "branch id for branch is invalid"),
						"branch id is not valid while fetching average ratings for a branch");
			}
			try {
				double averageRating = profileManagementService.getAverageRatings(branchId, CommonConstants.PROFILE_LEVEL_BRANCH, false);
				String json = new Gson().toJson(averageRating);
				LOG.debug("averageRating json : " + json);
				response = Response.ok(json).build();
			}
			catch (InvalidInputException e) {
				throw new InternalServerException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_AVERAGE_RATING_FETCH_FAILURE,
						CommonConstants.SERVICE_CODE_BRANCH_AVERAGE_RATINGS, "Something went wrong while fetching average ratings for branch"),
						e.getMessage());
			}
		}
		catch (BaseRestException e) {
			response = getErrorResponse(e);
		}
		LOG.info("Service to get average rating of branch executed successfully ");
		return response;
	}

	/**
	 * Service to fetch review count for a branch
	 * 
	 * @param branchId
	 * @param minScore
	 * @param maxScore
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/branch/{branchId}/reviewcount")
	public Response getReviewCountForBranch(@PathVariable long branchId, @QueryParam(value = "minScore") Double minScore,
			@QueryParam(value = "maxScore") Double maxScore) {
		LOG.info("Service to fetch the reviews count called for branchId :" + branchId + " ,minScore:" + minScore + " and maxScore:" + maxScore);
		Response response = null;
		try {
			if (branchId <= 0l) {
				throw new InputValidationException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_REVIEWS_COUNT_FETCH_PRECONDITION_FAILURE,
						CommonConstants.SERVICE_CODE_BRANCH_REVIEWS_COUNT, "branch id is invalid"),
						"branch id is not valid while fetching reviews count for a branch");
			}
			if (minScore == null) {
				minScore = CommonConstants.MIN_RATING_SCORE;
			}
			if (maxScore == null) {
				maxScore = CommonConstants.MAX_RATING_SCORE;
			}
			long reviewsCount = 0;
			try {
				reviewsCount = profileManagementService.getReviewsCount(branchId, minScore, maxScore, CommonConstants.PROFILE_LEVEL_BRANCH, false);
				String json = new Gson().toJson(reviewsCount);
				LOG.debug("reviews count json : " + json);
				response = Response.ok(json).build();
			}
			catch (InvalidInputException e) {
				throw new InternalServerException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_REVIEWS_COUNT_FETCH_FAILURE,
						CommonConstants.SERVICE_CODE_BRANCH_REVIEWS_COUNT, "Error occured while getting reviews count"), e.getMessage());
			}
		}
		catch (BaseRestException e) {
			response = getErrorResponse(e);
		}
		LOG.info("Service to fetch the reviews count of a branch executed successfully");
		return response;
	}

	/**
	 * Service to fetch reviews for a branch
	 * 
	 * @param branchId
	 * @param minScore
	 * @param maxScore
	 * @param start
	 * @param numRows
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/branch/{branchId}/reviews")
	public Response getReviewsForBranch(@PathVariable long branchId, @QueryParam(value = "minScore") Double minScore,
			@QueryParam(value = "maxScore") Double maxScore, @QueryParam(value = "start") Integer start,
			@QueryParam(value = "numRows") Integer numRows, @QueryParam(value = "sortCriteria") String sortCriteria) {
		LOG.info("Service to fetch reviews of branch called for branchId:" + branchId + " ,minScore:" + minScore + " and maxscore:" + maxScore);
		Response response = null;
		try {
			if (branchId <= 0l) {
				throw new InputValidationException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_BRANCH_REVIEWS_FETCH_PRECONDITION_FAILURE,
						CommonConstants.SERVICE_CODE_BRANCH_REVIEWS, "branch id for branch is invalid"),
						"branch id is not valid while fetching all reviews for a branch");
			}
			if (minScore == null) {
				minScore = CommonConstants.MIN_RATING_SCORE;
			}
			if (maxScore == null) {
				maxScore = CommonConstants.MAX_RATING_SCORE;
			}
			if (start == null) {
				start = -1;
			}
			if (numRows == null) {
				numRows = -1;
			}
			if(sortCriteria == null){
				sortCriteria = "date";
			}
			try {
				List<SurveyDetails> reviews = profileManagementService.getReviews(branchId, minScore, maxScore, start, numRows,
						CommonConstants.PROFILE_LEVEL_BRANCH, false, null, null, sortCriteria);
				String json = new Gson().toJson(reviews);
				LOG.debug("reviews json : " + json);
				response = Response.ok(json).build();
			}
			catch (InvalidInputException e) {
				throw new InternalServerException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_BRANCH_REVIEWS_FETCH_FAILURE,
						CommonConstants.SERVICE_CODE_BRANCH_REVIEWS, "Something went wrong while fetching reviews for a branch"), e.getMessage());
			}
		}
		catch (BaseRestException e) {
			response = getErrorResponse(e);
		}

		LOG.info("Service to fetch reviews of branch completed successfully");
		return response;
	}

	/**
	 * Service to fetch average ratings for agent
	 * 
	 * @param agentId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/individual/{agentId}/ratings")
	public Response getAverageRatingForAgent(@PathVariable long agentId) {
		LOG.info("Service to get average rating of agent called for agentId:" + agentId);
		Response response = null;
		try {
			if (agentId <= 0l) {
				throw new InputValidationException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_AVERAGE_RATING_FETCH_PRECONDITION_FAILURE,
						CommonConstants.SERVICE_CODE_INDIVIDUAL_AVERAGE_RATINGS, "individual id is invalid"),
						"agent id is not valid while fetching average ratings for an agent");
			}
			try {
				double averageRating = profileManagementService.getAverageRatings(agentId, CommonConstants.PROFILE_LEVEL_INDIVIDUAL, false);
				String json = new Gson().toJson(averageRating);
				LOG.debug("averageRating json : " + json);
				response = Response.ok(json).build();
			}
			catch (InvalidInputException e) {
				throw new InternalServerException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_AVERAGE_RATING_FETCH_FAILURE,
						CommonConstants.SERVICE_CODE_BRANCH_AVERAGE_RATINGS, "Something went wrong while fetching average ratings for agent"),
						e.getMessage());
			}
		}
		catch (BaseRestException e) {
			response = getErrorResponse(e);
		}
		LOG.info("Service to get average rating of agent executed successfully ");
		return response;
	}

	/**
	 * Service to fetch review count for an agent
	 * 
	 * @param agentId
	 * @param minScore
	 * @param maxScore
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/individual/{agentId}/reviewcount")
	public Response getReviewCountForAgent(@PathVariable long agentId, @QueryParam(value = "minScore") Double minScore,
			@QueryParam(value = "maxScore") Double maxScore) {
		LOG.info("Service to fetch the reviews count called for agentId :" + agentId + " ,minScore:" + minScore + " and maxScore:" + maxScore);
		Response response = null;
		try {
			if (agentId <= 0l) {
				throw new InputValidationException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_REVIEWS_COUNT_FETCH_PRECONDITION_FAILURE,
						CommonConstants.SERVICE_CODE_INDIVIDUAL_REVIEWS_COUNT, "agent id is invalid"),
						"agent id is not valid while fetching reviews count for a agent");
			}
			if (minScore == null) {
				minScore = CommonConstants.MIN_RATING_SCORE;
			}
			if (maxScore == null) {
				maxScore = CommonConstants.MAX_RATING_SCORE;
			}
			long reviewsCount = 0;
			try {
				reviewsCount = profileManagementService.getReviewsCount(agentId, minScore, maxScore, CommonConstants.PROFILE_LEVEL_INDIVIDUAL, false);
				String json = new Gson().toJson(reviewsCount);
				LOG.debug("reviews count json : " + json);
				response = Response.ok(json).build();
			}
			catch (InvalidInputException e) {
				throw new InternalServerException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_REVIEWS_COUNT_FETCH_FAILURE,
						CommonConstants.SERVICE_CODE_INDIVIDUAL_REVIEWS_COUNT, "Error occured while getting reviews count"), e.getMessage());
			}
		}
		catch (BaseRestException e) {
			response = getErrorResponse(e);
		}
		LOG.info("Service to fetch the reviews count of an agent executed successfully");
		return response;
	}

	/**
	 * Service to fetch reviews for an agent
	 * 
	 * @param agentId
	 * @param minScore
	 * @param maxScore
	 * @param start
	 * @param numRows
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/individual/{agentId}/reviews")
	public Response getReviewsForAgent(@PathVariable long agentId, @QueryParam(value = "minScore") Double minScore,
			@QueryParam(value = "maxScore") Double maxScore, @QueryParam(value = "start") Integer start,
			@QueryParam(value = "numRows") Integer numRows, @QueryParam(value = "sortCriteria") String sortCriteria) {
		LOG.info("Service to fetch reviews of an agent called for agentId:" + agentId + " ,minScore:" + minScore + " and maxscore:" + maxScore);
		Response response = null;
		try {
			if (agentId <= 0l) {
				throw new InputValidationException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_REGION_REVIEWS_FETCH_PRECONDITION_FAILURE,
						CommonConstants.SERVICE_CODE_REGION_REVIEWS, "Agent id is invalid"),
						"agent id is not valid while fetching all reviews for an agent");
			}
			if (minScore == null || sortCriteria.equalsIgnoreCase(CommonConstants.REVIEWS_SORT_CRITERIA_DATE)) {
				minScore = CommonConstants.MIN_RATING_SCORE;
			}
			if (maxScore == null) {
				maxScore = CommonConstants.MAX_RATING_SCORE;
			}
			if (start == null) {
				start = -1;
			}
			if (numRows == null) {
				numRows = -1;
			}
			if(sortCriteria == null){
				sortCriteria = CommonConstants.REVIEWS_SORT_CRITERIA_DATE;
			}
			try {
				List<SurveyDetails> reviews = profileManagementService.getReviews(agentId, minScore, maxScore, start, numRows,
						CommonConstants.PROFILE_LEVEL_INDIVIDUAL, false, null, null, sortCriteria);
				String json = new Gson().toJson(reviews);
				LOG.debug("reviews json : " + json);
				response = Response.ok(json).build();
			}
			catch (InvalidInputException e) {
				throw new InternalServerException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_REGION_REVIEWS_FETCH_FAILURE,
						CommonConstants.SERVICE_CODE_REGION_REVIEWS, "Something went wrong while fetching reviews for a agent"), e.getMessage());
			}
		}
		catch (BaseRestException e) {
			response = getErrorResponse(e);
		}

		LOG.info("Service to fetch reviews of agent completed successfully");
		return response;
	}

	/**
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/individuals/{iden}")
	public Response getProListByProfile(@PathVariable long iden, @QueryParam(value = "profileLevel") String profileLevel,
			@QueryParam(value = "start") Integer start, @QueryParam(value = "numOfRows") Integer numRows) {
		Response response = null;
		try {
			if (iden <= 0l) {
				throw new InputValidationException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_PRO_LIST_FETCH_PRECONDITION_FAILURE,
						CommonConstants.SERVICE_CODE_PRO_LIST_FETCH, "Could not fetch users list. Iden is invalid"),
						"iden is invalid while getting users list for profile");
			}
			if (profileLevel == null) {
				throw new InputValidationException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_PRO_LIST_FETCH_PRECONDITION_FAILURE,
						CommonConstants.SERVICE_CODE_PRO_LIST_FETCH, "Could not fetch users list. Iden is invalid"),
						"iden is invalid while getting users list for profile");
			}
			if (start == null) {
				start = -1;
			}
			if (numRows == null) {
				numRows = -1;
			}
			try {
				SolrDocumentList solrSearchResult = profileManagementService.getProListByProfileLevel(iden, profileLevel, start, numRows);
				String json = new Gson().toJson(solrSearchResult);
				LOG.debug("Pro list json : " + json);
				response = Response.ok(json).build();

			}
			catch (SolrException e) {
				throw new InternalServerException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_PRO_LIST_FETCH_FAILURE,
						CommonConstants.SERVICE_CODE_PRO_LIST_FETCH, "Could not fetch users list."), e.getMessage());
			}
			catch (InvalidInputException e) {
				throw new InternalServerException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_PRO_LIST_FETCH_FAILURE,
						CommonConstants.SERVICE_CODE_PRO_LIST_FETCH, "Could not fetch users list."), e.getMessage());
			}
		}
		catch (BaseRestException e) {
			response = getErrorResponse(e);
		}

		LOG.info("Method getProListByProfile called for iden:" + iden + " and profileLevel:" + profileLevel);
		return response;

	}
	
	/**
	 * Downloads the vcard
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/downloadvcard/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Response downloadVCard(@PathVariable String id, HttpServletResponse response){
		LOG.info("Downloading vcard for profile id: "+id);
		try{
			if(id == null || id.isEmpty()){
				LOG.error("Profile id missing to download vcard");
				throw new InputValidationException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_GENERAL,
						CommonConstants.SERVICE_CODE_GENERAL, "Profile id missing to download vcard"),
						"Expected profile id, but is null or empty");
			}
			OrganizationUnitSettings individualProfile = null;
			try{
				individualProfile = profileManagementService.getIndividualByProfileName(id);
				if(individualProfile != null){
					LOG.debug("Creating Vcard for the profile");
					VCard vCard = new VCard();
					vCard.setKind(Kind.individual());
					// Set the name
					if(individualProfile.getProfileName() != null){
						LOG.debug("Setting profile name as formatted name in the vcard");
						vCard.setFormattedName(individualProfile.getProfileName());
					}
					// set the contact number
					if(individualProfile.getContact_details() != null){
						ContactDetailsSettings contactDetails = individualProfile.getContact_details();
						// set the contact number
						if(contactDetails.getContact_numbers() != null){
							if(contactDetails.getContact_numbers().getWork() != null){
								LOG.debug("Setting work contact number");
								vCard.addTelephoneNumber(contactDetails.getContact_numbers().getWork(), TelephoneType.WORK);
							}
							if(contactDetails.getContact_numbers().getPersonal() != null){
								LOG.debug("Setting cell contact number");
								vCard.addTelephoneNumber(contactDetails.getContact_numbers().getPersonal(), TelephoneType.CELL);
							}
							if(contactDetails.getContact_numbers().getFax() != null){
								LOG.debug("Setting fax number");
								vCard.addTelephoneNumber(contactDetails.getContact_numbers().getFax(), TelephoneType.FAX);
							}
						}
						// setting email addresses
						if(contactDetails.getMail_ids() != null){
							if(contactDetails.getMail_ids().getWork() != null && contactDetails.getMail_ids().getIsWorkEmailVerified()){
								LOG.debug("Adding work email address");
								vCard.addEmail(contactDetails.getMail_ids().getWork(), EmailType.WORK);
							}
							if(contactDetails.getMail_ids().getPersonal() != null && contactDetails.getMail_ids().getIsPersonalEmailVerified()){
								LOG.debug("Adding personla email address");
								vCard.addEmail(contactDetails.getMail_ids().getPersonal(), EmailType.HOME);
							}
						}
						// setting the title
						if(contactDetails.getTitle() != null){
							LOG.debug("Setting title: "+contactDetails.getTitle());
							vCard.addTitle(contactDetails.getTitle());
						}
						
						// validate to version 4
						LOG.warn(vCard.validate(VCardVersion.V4_0).toString());
						
						// send it to the response
						response.setContentType("text/vcf");
						response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", individualProfile.getProfileName()+".vcf"));
						OutputStream responseStream = null;
						try {
							responseStream = response.getOutputStream();
							Ezvcard.write(vCard).go(responseStream);
						}
						catch (IOException e) {
							e.printStackTrace();
						}finally{
							try {
								responseStream.close();
							}
							catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					
				}
			}catch (InvalidInputException | NoRecordsFetchedException e) {
				throw new InternalServerException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_GENERAL,
						CommonConstants.SERVICE_CODE_GENERAL, "Profile name for individual is invalid"), e.getMessage());
			}
		}catch(BaseRestException e){
			return getErrorResponse(e);
		}
		return null;
	}
	
	/**
	 * Service to get the posts of an individual
	 * 
	 * @param individualProfileName
	 * @param start
	 * @param numberOfRows
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/{individualProfileName}/posts")
	public Response getPostsForIndividual(@PathVariable String individualProfileName, @QueryParam(value = "start") Integer start,
			@QueryParam(value = "numRows") Integer numRows) {
		//TODO
		LOG.info("Service to get posts of an individual called for individualProfileName : " + individualProfileName);
		Response response = null;
		try {
			if (individualProfileName == null || individualProfileName.isEmpty()) {
				throw new InputValidationException(new ProfileServiceErrorCode(
						CommonConstants.ERROR_CODE_INDIVIDUAL_POSTS_FETCH_PRECONDITION_FAILURE, CommonConstants.SERVICE_CODE_INDIVIDUAL_POSTS,
						"Profile name for individual is invalid"), "individual profile name is null or empty");
			}
			if (start == null) {
				start = -1;
			}
			if (numRows == null) {
				numRows = -1;
			}
			OrganizationUnitSettings individualProfile = null;
			try {
				individualProfile = profileManagementService.getIndividualByProfileName(individualProfileName);
				
				UserProfile selectedProfile = new UserProfile();
				
				ProfilesMaster profilesMaster = new ProfilesMaster();
				profilesMaster.setProfileId(CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID);
				
				selectedProfile.setProfilesMaster(profilesMaster);
				selectedProfile.setAgentId(individualProfile.getIden());
				
				List<SocialPost> posts = profileManagementService.getSocialPosts(selectedProfile, start, numRows);
				String json = new Gson().toJson(posts);
				LOG.debug("individual posts json : " + json);
				response = Response.ok(json).build();
			}
			catch (InvalidInputException | NoRecordsFetchedException e) {
				throw new InternalServerException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_INDIVIDUAL_POSTS_FETCH_FAILURE,
						CommonConstants.SERVICE_CODE_INDIVIDUAL_POSTS, "Profile name for individual is invalid"), e.getMessage());
			}
		}
		catch (BaseRestException e) {
			response = getErrorResponse(e);
		}
		LOG.info("Service to get posts of individual finished");
		return response;
	}
	
	/**
	 * Service to get the posts of company
	 * 
	 * @param companyName
	 * @param start
	 * @param numberOfRows
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/company/{companyProfileName}/posts")
	public Response getPostsForCompany(@PathVariable String companyProfileName, @QueryParam(value = "start") Integer start,
			@QueryParam(value = "numRows") Integer numRows) {
		//TODO
		LOG.info("Service to get posts of a company called for companyProfileName : " + companyProfileName);
		Response response = null;
		try {
			if (companyProfileName == null || companyProfileName.isEmpty()) {
				throw new InputValidationException(new ProfileServiceErrorCode(
						CommonConstants.ERROR_CODE_COMPANY_POSTS_FETCH_PRECONDITION_FAILURE, CommonConstants.SERVICE_CODE_COMPANY_POSTS,
						"Profile name for company is invalid"), "company profile name is null or empty");
			}
			if (start == null) {
				start = -1;
			}
			if (numRows == null) {
				numRows = -1;
			}

			OrganizationUnitSettings companyProfile = null;
			try {
				companyProfile = profileManagementService.getCompanyProfileByProfileName(companyProfileName);
				UserProfile selectedProfile = new UserProfile();
				
				ProfilesMaster profilesMaster = new ProfilesMaster();
				profilesMaster.setProfileId(CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID);
				
				Company company = new Company();
				company.setCompanyId(companyProfile.getIden());
				
				selectedProfile.setProfilesMaster(profilesMaster);
				selectedProfile.setCompany(company);
				
				List<SocialPost> posts = profileManagementService.getSocialPosts(selectedProfile, start, numRows);
				String json = new Gson().toJson(posts);
				LOG.debug("individual posts json : " + json);
				response = Response.ok(json).build();
			}
			catch (InvalidInputException e) {
				throw new InternalServerException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_COMPANY_POSTS_FETCH_FAILURE,
						CommonConstants.SERVICE_CODE_COMPANY_POSTS, "Profile name for company is invalid"), e.getMessage());
			}
		}
		catch (BaseRestException e) {
			response = getErrorResponse(e);
		}
		LOG.info("Service to get posts of company finished");
		return response;
	}
	
	/**
	 * Service to get the posts of region
	 * 
	 * @param regionProfileName
	 * @param start
	 * @param numberOfRows
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/region/{companyProfileName}/{regionProfileName}/posts")
	public Response getPostsForRegion(@PathVariable String regionProfileName,@PathVariable String companyProfileName, @QueryParam(value = "start") Integer start,
			@QueryParam(value = "numRows") Integer numRows) {
		//TODO
		LOG.info("Service to get posts of a region called for regionProfileName : " + regionProfileName);
		Response response = null;
		try {
			if (regionProfileName == null || regionProfileName.isEmpty()) {
				throw new InputValidationException(new ProfileServiceErrorCode(
						CommonConstants.ERROR_CODE_REGION_POSTS_FETCH_PRECONDITION_FAILURE, CommonConstants.SERVICE_CODE_REGION_POSTS,
						"Profile name for region is invalid"), "region profile name is null or empty");
			}
			if (start == null) {
				start = -1;
			}
			if (numRows == null) {
				numRows = -1;
			}

			OrganizationUnitSettings regionProfile = null;
			try {
				regionProfile = profileManagementService.getRegionByProfileName(companyProfileName, regionProfileName);
				UserProfile selectedProfile = new UserProfile();
				
				ProfilesMaster profilesMaster = new ProfilesMaster();
				profilesMaster.setProfileId(CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID);
				
				selectedProfile.setProfilesMaster(profilesMaster);
				selectedProfile.setRegionId(regionProfile.getIden());
				
				List<SocialPost> posts = profileManagementService.getSocialPosts(selectedProfile, start, numRows);
				String json = new Gson().toJson(posts);
				LOG.debug("individual posts json : " + json);
				response = Response.ok(json).build();
			}
			catch (InvalidInputException e) {
				throw new InternalServerException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_REGION_POSTS_FETCH_FAILURE,
						CommonConstants.SERVICE_CODE_REGION_POSTS, "Profile name for region is invalid"), e.getMessage());
			}
		}
		catch (BaseRestException e) {
			response = getErrorResponse(e);
		}
		LOG.info("Service to get posts of region finished");
		return response;
	}
	
	/**
	 * Service to get the posts of branch
	 * 
	 * @param branchProfileName
	 * @param start
	 * @param numberOfRows
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/branch/{companyProfileName}/{branchProfileName}/posts")
	public Response getPostsForBranch(@PathVariable String branchProfileName, @PathVariable String companyProfileName, @QueryParam(value = "start") Integer start,
			@QueryParam(value = "numRows") Integer numRows) {
		//TODO
		LOG.info("Service to get posts of a branch called for branchProfileName : " + branchProfileName);
		Response response = null;
		try {
			if (branchProfileName == null || branchProfileName.isEmpty()) {
				throw new InputValidationException(new ProfileServiceErrorCode(
						CommonConstants.ERROR_CODE_BRANCH_POSTS_FETCH_PRECONDITION_FAILURE, CommonConstants.SERVICE_CODE_BRANCH_POSTS,
						"Profile name for branch is invalid"), "branch profile name is null or empty");
			}
			if (start == null) {
				start = -1;
			}
			if (numRows == null) {
				numRows = -1;
			}

			OrganizationUnitSettings branchProfile = null;
			try {
				branchProfile = profileManagementService.getBranchByProfileName(companyProfileName, branchProfileName);
				UserProfile selectedProfile = new UserProfile();
				
				ProfilesMaster profilesMaster = new ProfilesMaster();
				profilesMaster.setProfileId(CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID);
				
				selectedProfile.setProfilesMaster(profilesMaster);
				selectedProfile.setBranchId(branchProfile.getIden());
				
				List<SocialPost> posts = profileManagementService.getSocialPosts(selectedProfile, start, numRows);
				String json = new Gson().toJson(posts);
				LOG.debug("individual posts json : " + json);
				response = Response.ok(json).build();
			}
			catch (InvalidInputException e) {
				throw new InternalServerException(new ProfileServiceErrorCode(CommonConstants.ERROR_CODE_BRANCH_POSTS_FETCH_FAILURE,
						CommonConstants.SERVICE_CODE_BRANCH_POSTS, "Profile name for branch is invalid"), e.getMessage());
			}
		}
		catch (BaseRestException e) {
			response = getErrorResponse(e);
		}
		LOG.info("Service to get posts of branch finished");
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
