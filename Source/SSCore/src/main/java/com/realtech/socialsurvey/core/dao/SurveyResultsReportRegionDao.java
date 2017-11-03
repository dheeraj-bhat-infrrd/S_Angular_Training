package com.realtech.socialsurvey.core.dao;

import java.sql.Timestamp;
import java.util.Map;

import com.realtech.socialsurvey.core.entities.SurveyResultsReportRegion;

public interface SurveyResultsReportRegionDao extends GenericReportingDao<SurveyResultsReportRegion, String> {
	
	Map<String, SurveyResultsReportRegion> getSurveyResultForRegionId( long regionId, Timestamp startDate, Timestamp endDate,
	        int startIndex, int batchSize );

}
