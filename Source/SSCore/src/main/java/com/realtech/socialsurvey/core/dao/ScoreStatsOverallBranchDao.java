package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.ScoreStatsOverallBranch;

public interface ScoreStatsOverallBranchDao extends GenericReportingDao<ScoreStatsOverallBranch, String>{

	List<ScoreStatsOverallBranch> fetchScoreStatsOverallForBranch(Long branchId,int startMonth, int endMonth,int year);

}
