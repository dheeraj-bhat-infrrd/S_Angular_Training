package com.realtech.socialsurvey.core.dao;

import com.realtech.socialsurvey.core.entities.RemovedUser;

/*
 * Interface for UserProfileDao to perform various operations on UserProfile.
 */
public interface RemovedUserDao extends GenericDao<RemovedUser, Long> {

	public void deleteRemovedUsersByCompany(long companyId);
}