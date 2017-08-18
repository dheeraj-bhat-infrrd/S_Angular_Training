package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.UserRankingPastMonthBranch;

public interface UserRankingPastMonthBranchDao extends GenericReportingDao<UserRankingPastMonthBranch, String>{	

    List<UserRankingPastMonthBranch> fetchUserRankingForPastMonthBranch( Long branchId, int month, int year, int startIndex,
        int batchSize );
    
    int fetchUserRankingRankForPastMonthBranch( Long userId, Long branchId, int year ,int month);

    long fetchUserRankingCountForPastMonthBranch( Long branchId, int month , int year);
}