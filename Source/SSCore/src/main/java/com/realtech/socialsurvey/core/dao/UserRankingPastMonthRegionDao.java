package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.UserRankingPastMonthRegion;

public interface UserRankingPastMonthRegionDao extends GenericReportingDao<UserRankingPastMonthRegion, String>{

    List<UserRankingPastMonthRegion> fetchUserRankingForPastMonthRegion( Long regionId, int month, int year, int startIndex,
        int batchSize );
    
    int fetchUserRankingRankForPastMonthRegion( Long userId, Long regionId, int year,int month );

    long fetchUserRankingCountForPastMonthRegion( Long regionId, int month, int year );

    List<UserRankingPastMonthRegion> fetchUserRankingReportForPastMonthRegion( Long regionId, int month, int year );

    List<UserRankingPastMonthRegion> fetchUserRankingWithProfileForPastMonthRegion( Long regionId, int month, int year,
        int startIndex, int batchSize );
}
