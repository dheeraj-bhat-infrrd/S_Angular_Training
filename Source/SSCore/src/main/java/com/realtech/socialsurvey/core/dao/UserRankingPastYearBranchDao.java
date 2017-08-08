package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.UserRankingPastYearBranch;

public interface UserRankingPastYearBranchDao extends GenericReportingDao<UserRankingPastYearBranch, String>{
	List<UserRankingPastYearBranch> fetchUserRankingForPastYearBranch(Long branchId, int year);

}
