package com.realtech.socialsurvey.core.dao;

import java.sql.Timestamp;
import java.util.Map;

import com.realtech.socialsurvey.core.entities.SurveyResultsReportBranch;

public interface SurveyResultsReportBranchDao extends GenericReportingDao<SurveyResultsReportBranch, String>{
	
    Map<String, SurveyResultsReportBranch> getSurveyResultForBranchId( long branchId, Timestamp startDate, Timestamp endDate,
        int startIndex, int batchSize );
}
