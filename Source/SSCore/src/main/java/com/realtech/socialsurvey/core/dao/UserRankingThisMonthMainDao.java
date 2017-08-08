package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.UserRankingThisMonthMain;
import com.realtech.socialsurvey.core.entities.UserRankingThisYearMain;

public interface UserRankingThisMonthMainDao extends GenericReportingDao<UserRankingThisMonthMain, String>{
	List<UserRankingThisMonthMain> fetchUserRankingForThisMonthMain(Long companyId, int month, int year);
}
