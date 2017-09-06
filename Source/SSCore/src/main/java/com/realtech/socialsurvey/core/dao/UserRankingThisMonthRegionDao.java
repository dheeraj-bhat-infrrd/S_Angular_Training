package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.UserRankingThisMonthRegion;

public interface UserRankingThisMonthRegionDao extends GenericReportingDao<UserRankingThisMonthRegion, String>{
	
	int fetchUserRankingRankForThisMonthRegion( Long userId, Long regionId, int year );

    long fetchUserRankingCountForThisMonthRegion( Long regionId, int month , int year);

    List<UserRankingThisMonthRegion> fetchUserRankingReportForThisMonthRegion( Long regionId, int month, int year );

    List<UserRankingThisMonthRegion> fetchUserRankingWithProfileForThisMonthRegion( Long regionId, int month, int year,
        int startIndex, int batchSize );
}
