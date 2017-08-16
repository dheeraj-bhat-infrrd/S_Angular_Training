package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.UserRankingThisMonthBranch;

public interface UserRankingThisMonthBranchDao extends GenericReportingDao<UserRankingThisMonthBranch, String>{

    List<UserRankingThisMonthBranch> fetchUserRankingForThisMonthBranch( Long branchId, int month, int year, int startIndex,
        int batchSize );

    int fetchUserRankingRankForThisMonthBranch( Long userId, Long branchId, int year );

    long fetchUserRankingCountForThisMonthBranch( Long branchId,  int month,int year );
}
