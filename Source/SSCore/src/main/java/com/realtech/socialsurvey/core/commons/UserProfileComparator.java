package com.realtech.socialsurvey.core.commons;

import java.util.Comparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.realtech.socialsurvey.core.entities.UserProfile;

/**
 * Compares User Profiles based on their profile master id. The highest profile will be on the top
 */
public class UserProfileComparator implements Comparator<UserProfile> {

	private static final Logger LOG = LoggerFactory.getLogger(UserProfileComparator.class);

	@Override
	public int compare(UserProfile userProfile1, UserProfile userProfile2) {
		LOG.debug("Comparing user profiles");
		// higher the order, lower the profile id
		if (userProfile1.getProfilesMaster().getProfileId() > userProfile2.getProfilesMaster().getProfileId()) {
			return 1;
		}
		else if (userProfile1.getProfilesMaster().getProfileId() < userProfile2.getProfilesMaster().getProfileId()) {
			return -1;
		}
		else {
			return 0;
		}
	}

}
