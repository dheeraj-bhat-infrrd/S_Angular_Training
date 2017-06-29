package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.SurveyStatsReportCompany;

public interface SurveyStatsReportCompanyDao extends GenericReportingDao<SurveyStatsReportCompany, String>
{
    public List<SurveyStatsReportCompany> fetchCompanySurveyStatsById( Long companyid );

}
