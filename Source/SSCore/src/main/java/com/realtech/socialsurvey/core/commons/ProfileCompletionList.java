/**
 * 
 */
package com.realtech.socialsurvey.core.commons;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.entities.ProfileStage;
import com.realtech.socialsurvey.core.enums.ProfileStages;

/**
 * @author Kalmeshwar Class contains methods which processes profile stages.
 */
@Component
public class ProfileCompletionList {

	private static final Logger LOG = LoggerFactory.getLogger(ProfileCompletionList.class);

	/**
	 * @return Method returns default profile stages.
	 */
	public List<ProfileStage> getDefaultProfileCompletionList(boolean isAgent) {
		LOG.debug("Method getDefaultProfileCompletionList started");

		List<ProfileStage> profileStages = new ArrayList<ProfileStage>();
		ProfileStage profileStage = null;

		profileStage = new ProfileStage();
		profileStage.setOrder(ProfileStages.LINKEDIN_PRF.getOrder());
		profileStage.setProfileStageKey(ProfileStages.LINKEDIN_PRF.name());
		profileStage.setStatus(CommonConstants.STATUS_ACTIVE);
		profileStages.add(profileStage);

		profileStage = new ProfileStage();
		profileStage.setOrder(ProfileStages.FACEBOOK_PRF.getOrder());
		profileStage.setProfileStageKey(ProfileStages.FACEBOOK_PRF.name());
		profileStage.setStatus(CommonConstants.STATUS_ACTIVE);
		profileStages.add(profileStage);

		profileStage = new ProfileStage();
		profileStage.setOrder(ProfileStages.GOOGLE_PRF.getOrder());
		profileStage.setProfileStageKey(ProfileStages.GOOGLE_PRF.name());
		profileStage.setStatus(CommonConstants.STATUS_ACTIVE);
		profileStages.add(profileStage);

		profileStage = new ProfileStage();
		profileStage.setOrder(ProfileStages.TWITTER_PRF.getOrder());
		profileStage.setProfileStageKey(ProfileStages.TWITTER_PRF.name());
		profileStage.setStatus(CommonConstants.STATUS_ACTIVE);
		profileStages.add(profileStage);
		
		profileStage = new ProfileStage();
        profileStage.setOrder(ProfileStages.ZILLOW_PRF.getOrder());
        profileStage.setProfileStageKey(ProfileStages.ZILLOW_PRF.name());
        profileStage.setStatus(CommonConstants.STATUS_ACTIVE);
        profileStages.add(profileStage);

		profileStage = new ProfileStage();
		profileStage.setOrder(ProfileStages.YELP_PRF.getOrder());
		profileStage.setProfileStageKey(ProfileStages.YELP_PRF.name());
		profileStage.setStatus(CommonConstants.STATUS_ACTIVE);
		profileStages.add(profileStage);

		profileStage = new ProfileStage();
		profileStage.setOrder(ProfileStages.INSTAGRAM_PRF.getOrder());
		profileStage.setProfileStageKey(ProfileStages.INSTAGRAM_PRF.name());
		profileStage.setStatus(CommonConstants.STATUS_ACTIVE);
		profileStages.add(profileStage);

		if (isAgent) {
			profileStage = new ProfileStage();
			profileStage.setOrder(ProfileStages.LICENSE_PRF.getOrder());
			profileStage.setProfileStageKey(ProfileStages.LICENSE_PRF.name());
			profileStage.setStatus(CommonConstants.STATUS_ACTIVE);
			profileStages.add(profileStage);

			profileStage = new ProfileStage();
			profileStage.setOrder(ProfileStages.HOBBIES_PRF.getOrder());
			profileStage.setProfileStageKey(ProfileStages.HOBBIES_PRF.name());
			profileStage.setStatus(CommonConstants.STATUS_ACTIVE);
			profileStages.add(profileStage);

			profileStage = new ProfileStage();
			profileStage.setOrder(ProfileStages.ACHIEVEMENTS_PRF.getOrder());
			profileStage.setProfileStageKey(ProfileStages.ACHIEVEMENTS_PRF.name());
			profileStage.setStatus(CommonConstants.STATUS_ACTIVE);
			profileStages.add(profileStage);
		}
		LOG.debug("Method getDefaultProfileCompletionList ended");

		return profileStages;

	}

	/**
	 * @return Method filters profile stages based on status.
	 */
	public List<ProfileStage> getProfileCompletionList(List<ProfileStage> curProfileStages) {

		LOG.debug("Method getProfileCompletionList started for profile stages : {}", curProfileStages);

		List<ProfileStage> profilestages = new ArrayList<ProfileStage>();
		for (ProfileStage profileStage : curProfileStages) {
			if (profileStage.getStatus() == CommonConstants.STATUS_ACTIVE) {
				if(LOG.isDebugEnabled()){
					LOG.debug("Profile stage {} completed with status {}",profileStage.getProfileStageKey(),profileStage.getStatus());
				}
				profilestages.add(profileStage);
			}
			else {
				if(LOG.isDebugEnabled()){
					LOG.debug("Profile stage {} completed with status {}",profileStage.getProfileStageKey(),profileStage.getStatus());
				}
			}
		}
		LOG.debug("Method getProfileCompletionList ended with filtered profile stages : {}", profilestages);
		return profilestages;
	}
}
