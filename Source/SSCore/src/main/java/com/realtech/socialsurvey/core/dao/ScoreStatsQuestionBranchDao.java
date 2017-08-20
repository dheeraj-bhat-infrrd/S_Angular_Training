package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.ScoreStatsQuestionBranch;

public interface ScoreStatsQuestionBranchDao extends GenericReportingDao<ScoreStatsQuestionBranch, String>{
	
	public List<ScoreStatsQuestionBranch> fetchScoreStatsQuestionForBranch(Long branchId, Long questionId, int startMonth, int endMonth, int year);

	public List<Long> fetchActiveQuestionsForBranch(Long branchId, int startMonth, int endMonth, int year);
}
