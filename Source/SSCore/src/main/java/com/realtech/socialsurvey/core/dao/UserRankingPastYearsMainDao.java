package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.UserRankingPastYearsMain;

public interface UserRankingPastYearsMainDao extends GenericReportingDao<UserRankingPastYearsMain, String>
{

    List<UserRankingPastYearsMain> fetchUserRankingForPastYearsMain( Long companyId, int startIndex, int batchSize );

    int fetchUserRankingRankForPastYearsMain( Long userId, Long companyId );

    long fetchUserRankingCountForPastYearsMain( Long companyId );

}
