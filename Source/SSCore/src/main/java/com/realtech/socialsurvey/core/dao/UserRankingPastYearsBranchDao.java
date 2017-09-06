package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.UserRankingPastYearsBranch;

public interface UserRankingPastYearsBranchDao extends GenericReportingDao<UserRankingPastYearsBranch, String>
{

    int fetchUserRankingRankForPastYearsBranch( Long userId, Long branchId );

    long fetchUserRankingCountForPastYearsBranch( Long branchId );

    List<UserRankingPastYearsBranch> fetchUserRankingWithProfileForPastYearsBranch( Long branchId, int startIndex,
        int batchSize );
}
