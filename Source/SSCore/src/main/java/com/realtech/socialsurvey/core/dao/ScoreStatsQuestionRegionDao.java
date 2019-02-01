package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.ScoreStatsQuestionRegion;

public interface ScoreStatsQuestionRegionDao extends GenericReportingDao<ScoreStatsQuestionRegion, String>{

    List<ScoreStatsQuestionRegion> fetchScoreStatsQuestionForRegion( Long regionId, int startMonth, int startYear, int endMonth,
        int endYear, List<Long> questionIds );

}
