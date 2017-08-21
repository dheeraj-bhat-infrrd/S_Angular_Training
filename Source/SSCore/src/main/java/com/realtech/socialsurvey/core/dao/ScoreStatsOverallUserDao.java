package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.ScoreStatsOverallUser;

public interface ScoreStatsOverallUserDao extends GenericReportingDao<ScoreStatsOverallUser, String>{
	
	public List<ScoreStatsOverallUser> fetchScoreStatsOverallForUser(Long userId, int startMonth, int endMonth, int year);

}
