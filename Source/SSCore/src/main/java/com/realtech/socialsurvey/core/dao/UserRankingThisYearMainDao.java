package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.UserRankingThisYearMain;

public interface UserRankingThisYearMainDao extends GenericReportingDao<UserRankingThisYearMain, String>{
	List<UserRankingThisYearMain> fetchUserRankingForThisYearMain(Long companyId, int year , int startIndex, int batchSize );

}
