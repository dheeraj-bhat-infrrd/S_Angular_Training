package com.realtech.socialsurvey.core.dao;

import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;

public interface UserProfileDao extends GenericDao<UserProfile, Integer> {
	
	public void createUserProfile(User user, Company company, String emailId, int agentId, int branchId, int regionId, int profileMasterId);
}
