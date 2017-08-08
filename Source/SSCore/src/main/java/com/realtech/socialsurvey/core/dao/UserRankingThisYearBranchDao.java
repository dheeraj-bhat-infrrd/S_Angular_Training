package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.UserRankingThisYearBranch;

public interface UserRankingThisYearBranchDao extends GenericReportingDao<UserRankingThisYearBranch, String>{
	List<UserRankingThisYearBranch> fetchUserRankingForThisYearBranch(Long branchId, int year);
}
