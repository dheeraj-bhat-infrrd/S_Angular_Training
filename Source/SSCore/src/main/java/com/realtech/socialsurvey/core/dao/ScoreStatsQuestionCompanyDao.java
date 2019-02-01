package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.ScoreStatsQuestionCompany;

public interface ScoreStatsQuestionCompanyDao extends GenericReportingDao<ScoreStatsQuestionCompany, String>{
	
    List<ScoreStatsQuestionCompany> fetchScoreStatsQuestionForCompany( Long companyId, int startMonth, int startYear,
        int endMonth, int endYear, List<Long> questionIds );
}
