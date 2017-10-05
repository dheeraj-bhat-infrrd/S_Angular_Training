package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.ScoreStatsOverallUser;

public interface ScoreStatsOverallUserDao extends GenericReportingDao<ScoreStatsOverallUser, String>{
	
    List<ScoreStatsOverallUser> fetchScoreStatsOverallForUser( Long userId, int startMonth, int startYear, int endMonth,
        int endYear );

}
