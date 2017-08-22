package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.ScoreStatsQuestionRegion;

public interface ScoreStatsQuestionRegionDao extends GenericReportingDao<ScoreStatsQuestionRegion, String>{
	
	public List<ScoreStatsQuestionRegion> fetchScoreStatsQuestionForRegion(Long regionId, Long questionId, int startMonth, int endMonth, int year);

	public List<Long> fetchActiveQuestionsForRegion(Long regionId, int startMonth, int endMonth, int year);

}