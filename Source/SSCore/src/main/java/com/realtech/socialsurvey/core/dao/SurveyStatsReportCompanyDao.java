package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.SurveyStatsReportCompany;

public interface SurveyStatsReportCompanyDao extends GenericReportingDao<SurveyStatsReportCompany, String>
{

    List<SurveyStatsReportCompany> fetchCompanySurveyStatsById( Long companyId, String startTrxMonth, String endTrxMonth );

}
