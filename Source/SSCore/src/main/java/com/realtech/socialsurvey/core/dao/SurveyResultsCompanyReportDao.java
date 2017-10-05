package com.realtech.socialsurvey.core.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.realtech.socialsurvey.core.entities.SurveyResultsCompanyReport;

public interface SurveyResultsCompanyReportDao extends GenericReportingDao<SurveyResultsCompanyReport, String>{                            
	
	List<SurveyResultsCompanyReport> fetchSurveyResultsCompanyReportByCompanyId( Long companyId,Timestamp startDate, Timestamp endDate );
}
