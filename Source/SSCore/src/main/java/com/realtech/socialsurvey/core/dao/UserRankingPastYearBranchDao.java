package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.UserRankingPastYearBranch;

public interface UserRankingPastYearBranchDao extends GenericReportingDao<UserRankingPastYearBranch, String>{

    List<UserRankingPastYearBranch> fetchUserRankingForPastYearBranch( Long branchId, int year, int startIndex, int batchSize );

    int fetchUserRankingRankForPastYearBranch( Long userId, Long branchId, int year );

    long fetchUserRankingCountForPastYearBranch( Long branchId, int year );

    List<UserRankingPastYearBranch> fetchUserRankingReportForPastYearBranch( Long branchId, int year );

    List<UserRankingPastYearBranch> fetchUserRankingWithProfileForPastYearBranch( Long branchId, int year, int startIndex,
        int batchSize );
}
