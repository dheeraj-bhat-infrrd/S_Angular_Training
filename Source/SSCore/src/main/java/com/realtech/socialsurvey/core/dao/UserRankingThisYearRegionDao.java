package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.UserRankingThisYearRegion;

public interface UserRankingThisYearRegionDao extends GenericReportingDao<UserRankingThisYearRegion, String>{

	int fetchUserRankingRankForThisYearRegion( Long userId, Long regionId, int year );

    long fetchUserRankingCountForThisYearRegion( Long regionId, int year );

    List<UserRankingThisYearRegion> fetchUserRankinReportForThisYearRegion( Long regionId, int year );

    List<UserRankingThisYearRegion> fetchUserRankingWithProfileForThisYearRegion( Long regionId, int year, int startIndex,
        int batchSize );
}
