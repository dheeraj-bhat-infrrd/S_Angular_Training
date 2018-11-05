package com.realtech.socialsurvey.core.dao;

import java.sql.Timestamp;
import java.util.Map;

import com.realtech.socialsurvey.core.entities.SurveyResultsCompanyReport;

public interface SurveyResultsCompanyReportDao extends GenericReportingDao<SurveyResultsCompanyReport, String>{                            
	
    Map<String, SurveyResultsCompanyReport> getSurveyResultForEntityTypeId( String entityType, Long entityId, Timestamp startDate,
        Timestamp endDate, int startIndex, int batchSize );
}
