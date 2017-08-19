package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.ScoreStatsQuestionCompany;

public interface ScoreStatsQuestionCompanyDao extends GenericReportingDao<ScoreStatsQuestionCompany, String>{

	public List<ScoreStatsQuestionCompany> fetchScoreStatsQuestionForCompany(Long companyId, int startMonth, int endMonth, int year);

}
