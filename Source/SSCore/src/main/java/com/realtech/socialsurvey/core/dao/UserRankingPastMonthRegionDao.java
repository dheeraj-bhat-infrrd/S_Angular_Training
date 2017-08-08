package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.UserRankingPastMonthRegion;

public interface UserRankingPastMonthRegionDao extends GenericReportingDao<UserRankingPastMonthRegion, String>{
	
	List<UserRankingPastMonthRegion> fetchUserRankingForPastMonthRegion(Long regionId,int month, int year);
}
