package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.UserRankingPastYearMain;

public interface UserRankingPastYearMainDao extends GenericReportingDao<UserRankingPastYearMain, String>{
	
	public List<UserRankingPastYearMain> fetchUserRankingForPastYearMain(Long companyId, int year);
}
