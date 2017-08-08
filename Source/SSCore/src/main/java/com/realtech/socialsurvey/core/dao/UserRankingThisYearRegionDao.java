package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.UserRankingThisYearRegion;

public interface UserRankingThisYearRegionDao extends GenericReportingDao<UserRankingThisYearRegion, String>{
	List<UserRankingThisYearRegion> fetchUserRankingForThisYearRegion(Long regionId, int year , int startIndex , int batchSize);

}
