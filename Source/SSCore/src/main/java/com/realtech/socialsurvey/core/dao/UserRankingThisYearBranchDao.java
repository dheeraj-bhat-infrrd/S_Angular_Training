package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.UserRankingThisYearBranch;

public interface UserRankingThisYearBranchDao extends GenericReportingDao<UserRankingThisYearBranch, String>{
	List<UserRankingThisYearBranch> fetchUserRankingForThisYearBranch(Long branchId, int year , int startIndex, int batchSize );

	int fetchUserRankingRankForThisYearBranch( Long userId, Long branchId, int year );

    long fetchUserRankingCountForThisYearBranch( Long branchId, int year );

    List<UserRankingThisYearBranch> fetchUserRankingReportForThisYearBranch( Long branchId, int year );
}
