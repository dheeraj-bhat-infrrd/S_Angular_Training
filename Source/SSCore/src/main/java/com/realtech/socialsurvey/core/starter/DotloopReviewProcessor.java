package com.realtech.socialsurvey.core.starter;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.entities.CompanyDotloopProfileMapping;
import com.realtech.socialsurvey.core.entities.DotLoopCrmInfo;
import com.realtech.socialsurvey.core.entities.DotLoopParticipant;
import com.realtech.socialsurvey.core.entities.DotLoopProfileEntity;
import com.realtech.socialsurvey.core.entities.LoopProfileMapping;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.integration.dotloop.DotloopIntegrationApi;
import com.realtech.socialsurvey.core.integration.dotloop.DotloopIntergrationApiBuilder;
import com.realtech.socialsurvey.core.integration.pos.errorhandlers.DotLoopAccessForbiddenException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;

/**
 * Ingester for social feed
 */
@Component("dotloopreviewprocessor")
public class DotloopReviewProcessor extends QuartzJobBean {

	private static final Logger LOG = LoggerFactory.getLogger(DotloopReviewProcessor.class);

	private DotloopIntergrationApiBuilder dotloopIntegrationApiBuilder;

	private SurveyHandler surveyHandler;

	private OrganizationManagementService organizationManagementService;

	private DotloopIntegrationApi dotloopIntegrationApi;

	private Utils utils;

	private String maskEmail;

	private static final String BUYING_AGENT_ROLE = "Buying Agent";
	private static final String SELLING_AGENT_ROLE = "Selling Agent";
	private static final String LISTING_AGENT_ROLE = "Listing Agent";

	private static final String SELLER_ROLE = "Seller";
	private static final String BUYER_ROLE = "Buyer";

	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) {
		LOG.info("Executing dotloop review processor");
		initializeDependencies(jobExecutionContext.getMergedJobDataMap());
		try {
			List<OrganizationUnitSettings> organizationUnitSettingsList = organizationManagementService.getOrganizationUnitSettingsForCRMSource(
					CommonConstants.CRM_SOURCE_DOTLOOP, CommonConstants.COMPANY_SETTINGS_COLLECTION);
			if (organizationUnitSettingsList != null && !organizationUnitSettingsList.isEmpty()) {
				LOG.info("Looping through crm list of size: " + organizationUnitSettingsList.size());
				for (OrganizationUnitSettings organizationUnitSettings : organizationUnitSettingsList) {
					LOG.info("Getting dotloop records for company id: " + organizationUnitSettings.getId());
					DotLoopCrmInfo dotLoopCrmInfo = (DotLoopCrmInfo) organizationUnitSettings.getCrm_info();
					if (dotLoopCrmInfo.getApi() != null && !dotLoopCrmInfo.getApi().isEmpty()) {
						LOG.debug("API key is " + dotLoopCrmInfo.getApi());
						fetchReviewfromDotloop(dotLoopCrmInfo.getApi(), organizationUnitSettings);
					}
				}
			}
		}
		catch (InvalidInputException | NoRecordsFetchedException e1) {
			LOG.info("Could not get list of dotloop records");
		}
	}

	// Gets list of profiles for a given api key
	private List<DotLoopProfileEntity> getDotLoopProfiles(String authorizationHeader, String apiKey) {
		LOG.debug("Getting dotloop profile list for api key: " + apiKey);
		List<DotLoopProfileEntity> profiles = null;
		if (dotloopIntegrationApi != null) {
			Response dotloopResponse = dotloopIntegrationApi.fetchdotloopProfiles(authorizationHeader);
			String responseString = null;
			if (dotloopResponse != null) {
				responseString = new String(((TypedByteArray) dotloopResponse.getBody()).getBytes());
			}
			if (responseString != null) {
				profiles = new Gson().fromJson(responseString, new TypeToken<List<DotLoopProfileEntity>>() {}.getType());
			}
		}
		LOG.debug("Returning dotloop profile list.");
		return profiles;

	}

	// check if the profile is entered in the system already as inactive
	private boolean isProfilePresentAsInactive(OrganizationUnitSettings unitSettings, DotLoopProfileEntity dotLoopProfile)
			throws InvalidInputException {
		LOG.debug("Checking dotLoopProfile presence in the system as inactive: " + dotLoopProfile.toString());
		boolean isAccountPresentInSystem = false;
		String profileId = String.valueOf(dotLoopProfile.getProfileId());
		CompanyDotloopProfileMapping companyDotloopProfileMapping = organizationManagementService.getCompanyDotloopMappingByCompanyIdAndProfileId(
				unitSettings.getIden(), profileId);
		if (companyDotloopProfileMapping != null) {
			LOG.debug("Profile is already present in the system as inactive.");
			isAccountPresentInSystem = true;
		}
		return isAccountPresentInSystem;
	}

	private void insertCompanyDotloopProfile(DotLoopProfileEntity profileEntity, OrganizationUnitSettings unitSettings) throws InvalidInputException {
		LOG.debug("Inserting into dotloop profile entity");
		CompanyDotloopProfileMapping companyDotloopProfileMapping = new CompanyDotloopProfileMapping();
		companyDotloopProfileMapping.setActive(false);
		companyDotloopProfileMapping.setProfileEmailAddress(profileEntity.getEmailAddress());
		companyDotloopProfileMapping.setProfileName(profileEntity.getName());
		companyDotloopProfileMapping.setProfileId(String.valueOf(profileEntity.getProfileId()));
		companyDotloopProfileMapping.setCompanyId(unitSettings.getIden());
		companyDotloopProfileMapping = organizationManagementService.saveCompanyDotLoopProfileMapping(companyDotloopProfileMapping);
	}

	private void processLoopEntites(List<LoopProfileMapping> loops, String profileId, boolean byPassRecords, String authorizationHeader,
			long organizationUnitId) {
		// if by pass is true, then add all the records in tracker without processing. Otherwise get
		// new records and then add the new record.
		LOG.debug("Processing loops for profile id: " + profileId + " and byPassRecords flag: " + byPassRecords);
		for (LoopProfileMapping loop : loops) {
			// Setting profile id for the loop
			loop.setProfileId(profileId);
			if (!byPassRecords) {
				// check if the record is present in the database then skip the loop. if not, then
				// process it
				LoopProfileMapping loopFromSystem = null;
				try {
					loopFromSystem = organizationManagementService.getLoopByProfileAndLoopId(profileId, loop.getLoopId());
				}
				catch (InvalidInputException e) {
					LOG.error("Could not get loop details from database for loop id " + loop.getLoopId() + " for profile " + profileId, e);
					continue;
				}
				if (loopFromSystem == null) {
					LOG.info("Loop " + loop.getLoopId() + " for profile " + profileId + " is not present. Hence processing.");
					processLoop(loop, authorizationHeader, organizationUnitId);
				}
				else {
					// record is present. process next record
					LOG.info("Loop " + loop.getLoopId() + " for profile " + profileId + " is present. Hence skipping.");
					continue;
				}
			}
			LOG.debug("Insert into tracker.");
			try {
				organizationManagementService.saveLoopsForProfile(loop);
			}
			catch (InvalidInputException e) {
				LOG.warn("Could not insert loop " + loop.getLoopId() + " for profile " + loop.getProfileId());
			}
		}
	}

	// processes the details of the loop
	private void processLoop(LoopProfileMapping loop, String authorizationHeader, long organizationUnitId) {
		LOG.debug("Processing details of loop view id: " + loop.getLoopViewId() + " for profile id: " + loop.getProfileId());
		Response response = null;
		String responseString = null;
		List<DotLoopParticipant> participants = null;
		try {
			response = dotloopIntegrationApi.fetchLoopViewParticipants(authorizationHeader, loop.getProfileId(), loop.getLoopViewId());
			if (response != null) {
				responseString = new String(((TypedByteArray) response.getBody()).getBytes());
				participants = new Gson().fromJson(responseString, new TypeToken<List<DotLoopParticipant>>() {}.getType());
				Map<String, String> customerMapping = null;
				String agentEmailId = null;
				for (DotLoopParticipant participant : participants) {
					if (participant.getRole() != null
							&& (participant.getRole().equalsIgnoreCase(BUYING_AGENT_ROLE)
									|| participant.getRole().equalsIgnoreCase(SELLING_AGENT_ROLE) || participant.getRole().equalsIgnoreCase(
									LISTING_AGENT_ROLE)) && participant.getMemberOfMyTeam() != null
							&& participant.getMemberOfMyTeam().equals(CommonConstants.YES_STRING)) {
						agentEmailId = participant.getEmail();
						if (maskEmail.equals(CommonConstants.YES_STRING)) {
							agentEmailId = utils.maskEmailAddress(agentEmailId);
						}
					}
					if (participant.getRole() != null
							&& (participant.getRole().equalsIgnoreCase(BUYER_ROLE) || participant.getRole().equalsIgnoreCase(SELLER_ROLE))
							&& participant.getMemberOfMyTeam().equalsIgnoreCase(CommonConstants.NO_STRING)) {
						if (participant.getEmail() != null && !participant.getEmail().isEmpty()) {
							if (customerMapping == null) {
								customerMapping = new HashMap<>();
							}
							String customerEmailId = participant.getEmail().trim();
							if (maskEmail.equals(CommonConstants.YES_STRING)) {
								customerEmailId = utils.maskEmailAddress(customerEmailId);
							}
							customerMapping.put(customerEmailId, participant.getName());
						}
					}
				}
				if (customerMapping != null && customerMapping.size() > 0 && agentEmailId != null && !agentEmailId.isEmpty()) {
					SurveyPreInitiation surveyPreInitiation = null;
					for (String customerMappingKey : customerMapping.keySet()) {
						surveyPreInitiation = new SurveyPreInitiation();
						surveyPreInitiation.setCompanyId(organizationUnitId);
						surveyPreInitiation.setCreatedOn(new Timestamp(System.currentTimeMillis()));
						surveyPreInitiation.setAgentId(0);
						surveyPreInitiation.setCustomerEmailId(customerMappingKey);
						surveyPreInitiation.setCustomerFirstName(customerMapping.get(customerMappingKey));
						surveyPreInitiation.setEngagementClosedTime(new Timestamp(System.currentTimeMillis()));
						surveyPreInitiation.setStatus(CommonConstants.STATUS_SURVEYPREINITIATION_NOT_PROCESSED);
						surveyPreInitiation.setSurveySource(CommonConstants.CRM_SOURCE_DOTLOOP);
						try {
							surveyHandler.saveSurveyPreInitiationObject(surveyPreInitiation);
						}
						catch (InvalidInputException e) {
							LOG.error("Unable to insert this record ", e);
						}
					}
				}
				else {
					LOG.warn("Incomplete details from participants call for loop view id");
				}
			}
			else {
				LOG.info("No response fetched for loop details " + loop.getLoopViewId() + " for profile id: " + loop.getProfileId());
			}

		}
		catch (DotLoopAccessForbiddenException e) {
			LOG.error("Could not fetch loop details for " + loop.getLoopViewId() + " for profile id: " + loop.getProfileId());
		}
	}

	/**
	 * Fetches records from dot loop
	 * 
	 * @param apiKey
	 * @param unitSettings
	 */
	public void fetchReviewfromDotloop(String apiKey, OrganizationUnitSettings unitSettings) {
		LOG.debug("Fetching reviews for api key: " + apiKey + " with id: " + unitSettings.getIden());
		String authorizationHeader = CommonConstants.AUTHORIZATION_HEADER + apiKey;
		// get list of profiles
		List<DotLoopProfileEntity> profileList = getDotLoopProfiles(authorizationHeader, apiKey);
		if (profileList != null && !profileList.isEmpty()) {
			LOG.debug("Got " + profileList.size() + " profiles.");
			for (DotLoopProfileEntity profile : profileList) {
				String profileId = String.valueOf(profile.getProfileId());
				try {
					if (profile.isActive() && !isProfilePresentAsInactive(unitSettings, profile)) {
						// check for loop ids with status closed (4)
						Response loopResponse = null;
						int batchNumber = 1;
						String loopResponseString = null;
						// List<LoopProfileMapping> dotloopProfileMappingList = null;
						List<LoopProfileMapping> loopEntities = null;
						boolean byPassRecords = false; // checks if the system has processed the
														// profile ever before.
						try {
							if (organizationManagementService.getLoopsCountByProfile(profileId) > 0) {
								LOG.info("Records for profile id: " + profileId + " is already present");
								byPassRecords = false;
							}
							else {
								LOG.info("Proile id is not processed for profile id: " + profileId
										+ ". Bypassing all records. Just adding into tracker");
								byPassRecords = true;
							}
							do {
								LOG.debug("Gettig batch " + batchNumber + " for closed records for profile " + profileId);
								loopResponse = dotloopIntegrationApi.fetchClosedProfiles(authorizationHeader, profileId, batchNumber);
								if (loopResponse != null) {
									loopResponseString = new String(((TypedByteArray) loopResponse.getBody()).getBytes());
									if (loopResponseString == null || loopResponseString.equals("[]")) {
										// no more records
										LOG.debug("No more loops ids for profile: " + profileId);
										break;
									}
									else {
										LOG.debug("Processing batch: " + batchNumber + " for profile: " + profileId);
										loopEntities = new Gson()
												.fromJson(loopResponseString, new TypeToken<List<LoopProfileMapping>>() {}.getType());
										// process loop entites. If there are no records for the
										// profile id in the tracker
										processLoopEntites(loopEntities, profileId, byPassRecords, authorizationHeader, unitSettings.getIden());
									}
								}
								else {
									// no more records
									LOG.debug("No more loops ids for profile: " + profileId);
									break;
								}
								batchNumber++;
							}
							while (true);
						}
						catch (DotLoopAccessForbiddenException dafe) {
							// insert into tracker table
							LOG.info("Inactive profile. Inserting into Dot loop profile mapping.");
							insertCompanyDotloopProfile(profile, unitSettings);
						}
					}
				}
				catch (JsonSyntaxException | InvalidInputException e) {
					LOG.error("Could not process " + profileId, e);
				}
			}
		}
	}

	private void initializeDependencies(JobDataMap jobMap) {
		dotloopIntegrationApiBuilder = (DotloopIntergrationApiBuilder) jobMap.get("dotloopIntegrationApiBuilder");
		dotloopIntegrationApi = dotloopIntegrationApiBuilder.getDotloopIntegrationApi();
		surveyHandler = (SurveyHandler) jobMap.get("surveyHandler");
		organizationManagementService = (OrganizationManagementService) jobMap.get("organizationManagementService");
		utils = (Utils) jobMap.get("utils");
		maskEmail = (String) jobMap.get("maskEmail");
	}

}