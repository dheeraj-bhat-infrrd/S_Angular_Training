package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.ScoreStatsQuestionUser;

public interface ScoreStatsQuestionUserDao extends GenericReportingDao<ScoreStatsQuestionUser, String>{
	
	public List<ScoreStatsQuestionUser> fetchScoreStatsQuestionForUser(Long userId, Long questionId, int startMonth, int endMonth, int year);
	
	public List<Long> fetchActiveQuestionsForUser(Long userId, int startMonth, int endMonth, int year);
}