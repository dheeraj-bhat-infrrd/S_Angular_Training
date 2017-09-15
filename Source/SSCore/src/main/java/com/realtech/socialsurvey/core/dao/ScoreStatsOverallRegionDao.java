package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.ScoreStatsOverallRegion;

public interface ScoreStatsOverallRegionDao extends GenericReportingDao<ScoreStatsOverallRegion, String>{

    List<ScoreStatsOverallRegion> fetchScoreStatsOverallForRegion( Long regionId, int startMonth, int startYear, int endMonth,
        int endYear );
	
}
