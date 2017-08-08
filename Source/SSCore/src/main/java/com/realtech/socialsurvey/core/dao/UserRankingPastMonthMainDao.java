package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.UserRankingPastMonthMain;

public interface UserRankingPastMonthMainDao extends GenericReportingDao<UserRankingPastMonthMain, String>{
	public List<UserRankingPastMonthMain> fetchUserRankingForPastMonthMain(Long companyId, int month, int year);
}
