package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.UserRankingThisMonthMain;
import com.realtech.socialsurvey.core.entities.UserRankingThisYearMain;

public interface UserRankingThisMonthMainDao extends GenericReportingDao<UserRankingThisMonthMain, String>{

    int fetchUserRankingRankForThisMonthMain( Long userId, Long companyId, int year );

    long fetchUserRankingCountForThisMonthMain( Long companyId, int year, int month );

    List<UserRankingThisMonthMain> fetchUserRankingReportForThisMonthMain( Long companyId, int month, int year );

    List<UserRankingThisMonthMain> fetchUserRankingWithProfileForThisMonthMain( Long companyId, int month, int year,
        int startIndex, int batchSize );

}
