package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.UserRankingPastMonthMain;

public interface UserRankingPastMonthMainDao extends GenericReportingDao<UserRankingPastMonthMain, String>{
	public List<UserRankingPastMonthMain> fetchUserRankingForPastMonthMain(Long companyId, int month, int year , int startIndex , int batchSize);

    int fetchUserRankingRankForPastMonthMain( Long userId, Long companyId, int year, int month );

    long fetchUserRankingCountForPastMonthMain( Long companyId, int year, int month );

    List<UserRankingPastMonthMain> fetchUserRankingrReportForPastMonthMain( Long companyId, int month, int year );

    List<UserRankingPastMonthMain> fetchUserRankingWithProfileForPastMonthMain( Long companyId, int month, int year,
        int startIndex, int batchSize );

}
