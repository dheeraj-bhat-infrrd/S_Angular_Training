package com.realtech.socialsurvey.core.dao;

import java.util.List;
import java.util.Map;

import com.realtech.socialsurvey.core.entities.SurveyResponseTable;


public interface SurveyResponseTableDao extends GenericReportingDao<SurveyResponseTable, String>{
	
	List<SurveyResponseTable> fetchSurveyResponsesBySurveyDetailsId( String surveyDetailsId );

    Map<String, List<SurveyResponseTable>> geSurveyResponseForCompanyId( long companyId );
}
