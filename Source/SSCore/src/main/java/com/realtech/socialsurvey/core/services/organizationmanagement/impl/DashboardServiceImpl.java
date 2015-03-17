package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.SurveyDetailsDao;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserSettings;
import com.realtech.socialsurvey.core.services.organizationmanagement.DashboardService;

// JIRA SS-137 BY RM05:BOC
/**
 * Class with methods defined to show dash board of user.
 */

@Component
public class DashboardServiceImpl implements DashboardService, InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(DashboardServiceImpl.class);
	private static Map<String, Integer> weightageColumns;

	@Autowired
	private SurveyDetailsDao surveyDetailsDao;

	@Autowired
	private OrganizationUnitSettingsDao organizationUnitSettingsDao;

	@Override
	public long getAllSurveyCountForPastNdays(String columnName, long columnValue, int numberOfDays) {
		return surveyDetailsDao.getSentSurveyCount(columnName, columnValue, numberOfDays);
	}

	@Override
	public long getCompletedSurveyCountForPastNdays(String columnName, long columnValue, int numberOfDays) {
		return surveyDetailsDao.getCompletedSurveyCount(columnName, columnValue, numberOfDays);
	}

	@Override
	public long getClickedSurveyCountForPastNdays(String columnName, long columnValue, int numberOfDays) {
		return surveyDetailsDao.getClickedSurveyCount(columnName, columnValue, numberOfDays);
	}

	@Override
	public long getSocialPostsForPastNdays(String columnName, long columnValue, int numberOfDays) {
		return surveyDetailsDao.getSocialPostsCount(columnName, columnValue, numberOfDays);
	}

	@Override
	public double getSurveyScore(String columnName, long columnValue, int numberOfDays) {
		return surveyDetailsDao.getRatingForPastNdays(columnName, columnValue, numberOfDays);
	}

	@Override
	public int getProfileCompletionPercentage(User user, String columnName, long columnValue, UserSettings userSettings) {
		LOG.info("Method to calculate profile completion percentage started.");
		int totalWeight = 0;
		double currentWeight = 0;
		OrganizationUnitSettings organizationUnitSettings = new OrganizationUnitSettings();
		switch(columnName){
			case CommonConstants.COMPANY_ID_COLUMN :
				organizationUnitSettings = userSettings.getCompanySettings();
				break;
			case CommonConstants.REGION_ID_COLUMN :
				organizationUnitSettings = userSettings.getRegionSettings().get(columnValue);
				break;
			case CommonConstants.BRANCH_ID_COLUMN :
				organizationUnitSettings = userSettings.getBranchSettings().get(columnValue);
				break;
			case CommonConstants.AGENT_ID_COLUMN : 
				organizationUnitSettings = userSettings.getAgentSettings().get(columnValue);
				break;
			default :
				LOG.error("Invalid value passed for columnName. It should be either of companyId/regionId/branchId/agentId.");
		}
		if(weightageColumns.containsKey("email")){
			totalWeight+=weightageColumns.get("email");
			if(organizationUnitSettings.getContact_details()!=null && organizationUnitSettings.getContact_details().getMail_ids()!=null)
				currentWeight+=weightageColumns.get("email");
		}
		if(weightageColumns.containsKey("about_me")){
			totalWeight+=weightageColumns.get("about_me");
			if(organizationUnitSettings.getContact_details()!=null && organizationUnitSettings.getContact_details().getAbout_me()!=null)
				currentWeight+=weightageColumns.get("about_me");
		}
		if(weightageColumns.containsKey("contact_number")){
			totalWeight+=weightageColumns.get("contact_number");
			if(organizationUnitSettings.getContact_details()!=null && organizationUnitSettings.getContact_details().getContact_numbers()!=null)
				currentWeight+=weightageColumns.get("contact_number");
		}
		if(weightageColumns.containsKey("profile_image")){
			totalWeight+=weightageColumns.get("profile_image");
			if(organizationUnitSettings.getProfileImageUrl()!=null)
				currentWeight+=weightageColumns.get("profile_image");
		}
		if(weightageColumns.containsKey("title")){
			totalWeight+=weightageColumns.get("title");
			if(organizationUnitSettings.getContact_details().getTitle()!=null)
				currentWeight+=weightageColumns.get("title");
		}
		LOG.info("Method to calculate profile completion percentage finished.");
		try{
			return (int) Math.round(currentWeight*100/totalWeight);
		}catch(ArithmeticException e){
			LOG.error("Exception caught in getProfileCompletionPercentage(). Nested exception is ",e);
			return 0;
		}
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		weightageColumns = new HashMap<>();
		weightageColumns.put("email", 1);
		weightageColumns.put("about_me", 1);
		weightageColumns.put("title", 1);
		weightageColumns.put("profile_image", 1);
		weightageColumns.put("contact_number", 1);
	}

	/*
	 * Method to calculate number of badges based upon surveyScore, count of surveys sent and profile completeness.
	 */
	@Override
	public int getBadges(int surveyScore, int surveyCount, int socialPosts, int profileCompleteness) {
		LOG.info("Method to calculate number of badges started.");
		int badges = 0;
		double normalizedSurveyScore = surveyScore*25/CommonConstants.MAX_SURVEY_SCORE;
		double normalizedProfileCompleteness = profileCompleteness*25/100;
		if(surveyCount>CommonConstants.MAX_SENT_SURVEY_COUNT)
			surveyCount = CommonConstants.MAX_SENT_SURVEY_COUNT;
		double normalizedSurveyCount = surveyCount*25/CommonConstants.MAX_SENT_SURVEY_COUNT;
		if(socialPosts>CommonConstants.MAX_SOCIAL_POSTS)
			socialPosts = CommonConstants.MAX_SOCIAL_POSTS;
		double normalizedSocialPosts = socialPosts*25/CommonConstants.MAX_SOCIAL_POSTS;
		int overallPercentage = (int) Math.round(normalizedSurveyScore+normalizedProfileCompleteness+normalizedSurveyCount+normalizedSocialPosts);
		if(overallPercentage<34)
			badges = 1;
		else if(overallPercentage<67)
			badges = 2;
		else
			badges = 3;
		LOG.info("Method to calculate number of badges finished.");
		return badges;
	}
}
// JIRA SS-137 BY RM05:EOC
