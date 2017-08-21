package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.ScoreStatsOverallCompany;

public interface ScoreStatsOverallCompanyDao extends GenericReportingDao<ScoreStatsOverallCompany, String>{
	
	List<ScoreStatsOverallCompany> fetchScoreStatsOverallForCompany(Long companyId,int startMonth, int endMonth,int year);

}
