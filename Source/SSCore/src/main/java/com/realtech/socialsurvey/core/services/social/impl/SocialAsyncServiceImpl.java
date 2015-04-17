package com.realtech.socialsurvey.core.services.social.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import com.google.gson.Gson;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.CompanyPositions;
import com.realtech.socialsurvey.core.entities.LinkedInProfileData;
import com.realtech.socialsurvey.core.entities.LinkedInToken;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.PositionValues;
import com.realtech.socialsurvey.core.entities.SkillValues;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.social.SocialAsyncService;

@Component
public class SocialAsyncServiceImpl implements SocialAsyncService {

	private static final Logger LOG = LoggerFactory.getLogger(SocialAsyncServiceImpl.class);

	@Autowired
	private ProfileManagementService profileManagementService;
	
	@Autowired
	private SolrSearchService solrSearchService;

	@Value("${LINKED_IN_REST_API_URI}")
	private String linkedInRestApiUri;

	@Async
	@Override
	public Future<OrganizationUnitSettings> linkedInDataUpdate(String collection, OrganizationUnitSettings unitSettings, LinkedInToken linkedInToken) {
		LOG.info("Method linkedInDataUpdate() called from SocialAsyncServiceImpl");

		StringBuilder linkedInFetch = new StringBuilder(linkedInRestApiUri)
				.append("(id,first-name,last-name,headline,picture-url,industry,summary,specialties,location,picture-urls::(original),positions:(id,title,summary,start-date,end-date,is-current,company:(id,name,type,size,industry,ticker)),associations,interests,skills:(id,skill:(name)))");
		linkedInFetch.append("?oauth2_access_token=").append(linkedInToken.getLinkedInAccessToken());
		linkedInFetch.append("&format=json");
		LOG.debug("URL to be posted to linked in: " + linkedInFetch.toString());
		LinkedInProfileData linkedInProfileData = null;
		try {
			HttpClient httpclient = HttpClientBuilder.create().build();
			HttpGet httpget = new HttpGet(linkedInFetch.toString());
			String responseBody = httpclient.execute(httpget, new BasicResponseHandler());
			LOG.debug("Response from linkedin: " + responseBody);
			linkedInProfileData = new Gson().fromJson(responseBody, LinkedInProfileData.class);
		}
		catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		if (linkedInProfileData != null) {
			LOG.debug("Adding linkedin data into collection");
			try {
				unitSettings.setLinkedInProfileData(linkedInProfileData);
				profileManagementService.updateLinkedInProfileData(collection, unitSettings, linkedInProfileData);
			}
			catch (InvalidInputException e) {
				LOG.error("Error while updating linkedin profile data", e);
			}
		}

		// update the details if its not present in the user settings already
		// check to see if contact details were modified
		boolean isContactDetailsUpdated = false;
		if (unitSettings.getContact_details().getAbout_me() == null || unitSettings.getContact_details().getAbout_me().isEmpty()) {
			LOG.debug("About me is empty. Filling with linkedin data");
			if (linkedInProfileData.getSummary() != null && !linkedInProfileData.getSummary().isEmpty()) {
				unitSettings.getContact_details().setAbout_me(linkedInProfileData.getSummary());
				isContactDetailsUpdated = true;
				// profileManagementService.updateContactDetails(collection, unitSettings,
				// unitSettings.getContact_details());
			}
		}
		if (unitSettings.getContact_details().getName() == null || unitSettings.getContact_details().getName().isEmpty()) {
			LOG.debug("Name is empty. Filling with linkedin data");
			unitSettings.getContact_details().setFirstName(linkedInProfileData.getFirstName());
			if (linkedInProfileData.getLastName() != null && !linkedInProfileData.getLastName().isEmpty()) {
				unitSettings.getContact_details().setLastName(linkedInProfileData.getLastName());
			}
			unitSettings.getContact_details().setName(
					linkedInProfileData.getFirstName() + (linkedInProfileData.getLastName() != null ? " " + linkedInProfileData.getLastName() : ""));
			isContactDetailsUpdated = true;
		}

		if (unitSettings.getContact_details().getTitle() == null || unitSettings.getContact_details().getTitle().isEmpty()) {
			LOG.debug("Title is empty. Filling with linkedin data");
			if (linkedInProfileData.getHeadline() != null && !linkedInProfileData.getHeadline().isEmpty()) {
				unitSettings.getContact_details().setTitle(linkedInProfileData.getHeadline());
				isContactDetailsUpdated = true;
			}
		}

		if (unitSettings.getContact_details().getLocation() == null || unitSettings.getContact_details().getLocation().isEmpty()) {
			LOG.debug("Location is empty. Filling with linkedin data");
			if (linkedInProfileData.getLocation() != null) {
				unitSettings.getContact_details().setLocation(linkedInProfileData.getLocation().getName());
				isContactDetailsUpdated = true;
			}
		}

		if (unitSettings.getContact_details().getIndustry() == null || unitSettings.getContact_details().getIndustry().isEmpty()) {
			LOG.debug("Industry is empty. Filling with linkedin data");
			if (linkedInProfileData.getIndustry() != null && !linkedInProfileData.getIndustry().isEmpty()) {
				unitSettings.getContact_details().setIndustry(linkedInProfileData.getIndustry());
				isContactDetailsUpdated = true;
			}
		}

		if (isContactDetailsUpdated) {
			LOG.debug("Contact details were updated. Updating the same in database");
			try {
				profileManagementService.updateContactDetails(collection, unitSettings, unitSettings.getContact_details());
			}
			catch (InvalidInputException e) {
				LOG.error(e.getMessage(), e);
			}
		}

		if (unitSettings.getProfileImageUrl() == null || unitSettings.getProfileImageUrl().isEmpty()) {
			try {
				if(linkedInProfileData.getPictureUrls() != null && linkedInProfileData.getPictureUrls().getValues() != null && !linkedInProfileData.getPictureUrls().getValues().isEmpty()){
					unitSettings.setProfileImageUrl(linkedInProfileData.getPictureUrls().getValues().get(0));
					profileManagementService.updateProfileImage(collection, unitSettings, unitSettings.getProfileImageUrl());
				}
			}
			catch (InvalidInputException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		
		if(unitSettings instanceof AgentSettings){
			AgentSettings agentSettings = (AgentSettings)unitSettings;
			if(agentSettings.getExpertise() == null){
				LOG.debug("Expertise is not present. Filling the data from linkedin");
				if(linkedInProfileData.getSkills() != null && linkedInProfileData.getSkills().getValues() != null && !linkedInProfileData.getSkills().getValues().isEmpty()){
					List<String> expertiseList = new ArrayList<String>();
					for(SkillValues skillValue: linkedInProfileData.getSkills().getValues()){
						expertiseList.add(skillValue.getSkill().getName());
					}
					agentSettings.setExpertise(expertiseList);
					if(agentSettings.getExpertise() != null && agentSettings.getExpertise().size() > 0){
						try {
							profileManagementService.updateAgentExpertise(agentSettings, agentSettings.getExpertise());
						}
						catch (InvalidInputException e) {
							LOG.error(e.getMessage(), e);
						}
					}
				}
			}
			
			if(agentSettings.getPositions() == null || agentSettings.getPositions().isEmpty()){
				LOG.debug("Positions is not present. Filling the data from linkedin");
				if(linkedInProfileData.getPositions() != null && linkedInProfileData.getPositions().getValues() != null && !linkedInProfileData.getPositions().getValues().isEmpty()){
					List<CompanyPositions> companyPositionsList = new ArrayList<CompanyPositions>();
					CompanyPositions companyPositions = null;
					for(PositionValues positionValue: linkedInProfileData.getPositions().getValues()){
						companyPositions = new CompanyPositions();
						companyPositions.setName(positionValue.getCompany().getName());
						companyPositions.setStartTime(positionValue.getStartDate().getMonth()+"-"+positionValue.getStartDate().getYear());
						companyPositions.setIsCurrent(positionValue.isCurrent());
						companyPositions.setTitle(positionValue.getTitle());
						if(!positionValue.isCurrent()){
							companyPositions.setEndTime(positionValue.getEndDate().getMonth()+"-"+positionValue.getEndDate().getYear());
						}
						companyPositionsList.add(companyPositions);
					}
					if(companyPositionsList.size() > 0){
						agentSettings.setPositions(companyPositionsList);
						try {
							profileManagementService.updateAgentCompanyPositions(agentSettings, companyPositionsList);
						}
						catch (InvalidInputException e) {
							LOG.error(e.getMessage(), e);
						}
					}
				}
			}
			// finally update details in solr
			try {
				LOG.debug("Updating details in solr");
				solrSearchService.editUserInSolr(agentSettings.getIden(), CommonConstants.ABOUT_ME_SOLR, agentSettings.getContact_details().getAbout_me());
				solrSearchService.editUserInSolr(agentSettings.getIden(), CommonConstants.PROFILE_IMAGE_URL_SOLR, agentSettings.getProfileImageUrl());
			}
			catch (SolrException e) {
				LOG.error("Could not update details in solr",e);
			}
		}

		LOG.info("Method linkedInDataUpdate() finished from SocialAsyncServiceImpl");
		return new AsyncResult<OrganizationUnitSettings>(unitSettings);
	}
}