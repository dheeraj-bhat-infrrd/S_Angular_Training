package com.realtech.socialsurvey.core.dao;

import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;

public interface UserProfileDao extends GenericDao<UserProfile, Long> {

	public void createUserProfile(User user, Company company, String emailId, long agentId, long branchId, long regionId, int profileMasterId,
			String profileCompletionStage, int isProfileComplete,String createdBy, String modifiedBy);
}
