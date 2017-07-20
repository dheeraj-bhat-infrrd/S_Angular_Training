package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.SurveyTransactionReport;
import com.realtech.socialsurvey.core.entities.UserAdoptionReport;

public interface SurveyTransactionReportDao extends GenericReportingDao<SurveyTransactionReportDao, String>
{

    List<SurveyTransactionReport> fetchSurveyTransactionByCompanyId( Long companyId, int startYear, int startMonth, int endYear,
        int endMonth );

    List<SurveyTransactionReport> fetchSurveyTransactionByAgentId( Long agentId, int startYear, int startMonth, int endYear,
        int endMonth );

}
