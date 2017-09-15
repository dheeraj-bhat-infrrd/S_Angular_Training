package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.UserRankingThisYearMain;

public interface UserRankingThisYearMainDao extends GenericReportingDao<UserRankingThisYearMain, String>{

    int fetchUserRankingRankForThisYearMain( Long userId, Long companyId, int year );

    long fetchUserRankingCountForThisYearMain( Long companyId, int year );

    List<UserRankingThisYearMain> fetchUserRankingReportForThisYearMain( Long companyId, int year );

    List<UserRankingThisYearMain> fetchUserRankingWithProfileForThisYearMain( Long companyId, int year, int startIndex,
        int batchSize );

}
