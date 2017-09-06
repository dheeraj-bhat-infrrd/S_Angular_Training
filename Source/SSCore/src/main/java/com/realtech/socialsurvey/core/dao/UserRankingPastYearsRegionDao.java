package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.UserRankingPastYearsRegion;

public interface UserRankingPastYearsRegionDao extends GenericReportingDao<UserRankingPastYearsRegion, String>
{

    List<UserRankingPastYearsRegion> fetchUserRankingForPastYearsRegion( Long regionId, int startIndex, int batchSize );

    int fetchUserRankingRankForPastYearsRegion( Long userId, Long regionId );

    long fetchUserRankingCountForPastYearsRegion( Long regionId );

    List<UserRankingPastYearsRegion> fetchUserRankingWithProfileForPastYearsRegion( Long regionId, int startIndex,
        int batchSize );
}
