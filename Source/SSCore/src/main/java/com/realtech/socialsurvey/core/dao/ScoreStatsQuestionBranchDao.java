package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.ScoreStatsQuestionBranch;

public interface ScoreStatsQuestionBranchDao extends GenericReportingDao<ScoreStatsQuestionBranch, String>{

    List<ScoreStatsQuestionBranch> fetchScoreStatsQuestionForBranch( Long branchId, int startMonth, int startYear, int endMonth,
        int endYear, List<Long> questionIds );
}
