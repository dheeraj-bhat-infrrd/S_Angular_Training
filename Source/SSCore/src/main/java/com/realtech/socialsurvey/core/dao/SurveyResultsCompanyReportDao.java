package com.realtech.socialsurvey.core.dao;

import java.util.Date;
import java.util.List;

import com.realtech.socialsurvey.core.entities.SurveyResultsCompanyReport;

public interface SurveyResultsCompanyReportDao extends GenericReportingDao<SurveyResultsCompanyReport, String>{                            
	
	List<SurveyResultsCompanyReport> fetchSurveyResultsCompanyReportByCompanyId( Long companyId,Date startDate, Date endDate );
}
