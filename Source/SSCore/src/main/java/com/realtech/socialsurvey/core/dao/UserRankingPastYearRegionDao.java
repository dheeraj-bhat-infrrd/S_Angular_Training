package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.dao.GenericReportingDao;
import com.realtech.socialsurvey.core.entities.UserRankingPastYearRegion;

public interface UserRankingPastYearRegionDao extends GenericReportingDao<UserRankingPastYearRegion, String>{

    List<UserRankingPastYearRegion> fetchUserRankingForPastYearRegion( Long regionId, int year, int startIndex, int batchSize );
    
    int fetchUserRankingRankForPastYearRegion( Long userId, Long regionId, int year );

    long fetchUserRankingCountForPastYearRegion( Long regionId, int year );

    List<UserRankingPastYearRegion> fetchUserRankingReportForPastYearRegion( Long regionId, int year );

    List<UserRankingPastYearRegion> fetchUserRankingWithProfileForPastYearRegion( Long regionId, int year, int startIndex,
        int batchSize );
}
