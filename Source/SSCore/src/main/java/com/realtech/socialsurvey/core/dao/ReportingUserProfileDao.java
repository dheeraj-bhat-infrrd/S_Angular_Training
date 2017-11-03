package com.realtech.socialsurvey.core.dao;

import java.util.Set;

import com.realtech.socialsurvey.core.entities.ReportingUserProfile;

public interface ReportingUserProfileDao extends GenericReportingDao<ReportingUserProfile , String>
{

    Set<Long> findUserIdsByRegion( long regionId );

    Set<Long> findUserIdsByBranch( long branchId );

}
