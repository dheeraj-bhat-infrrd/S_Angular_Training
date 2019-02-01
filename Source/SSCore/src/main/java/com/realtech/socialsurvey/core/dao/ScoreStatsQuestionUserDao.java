package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.ScoreStatsQuestionUser;

public interface ScoreStatsQuestionUserDao extends GenericReportingDao<ScoreStatsQuestionUser, String>{

    List<ScoreStatsQuestionUser> fetchScoreStatsQuestionForUser( Long userId, int startMonth, int startYear, int endMonth,
        int endYear, List<Long> questionIds );
}
