package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.SurveyTransactionReport;
import com.realtech.socialsurvey.core.entities.UserAdoptionReport;

public interface SurveyTransactionReportDao extends GenericReportingDao<SurveyTransactionReportDao, String>
{

    List<SurveyTransactionReport> fetchSurveyTransactionById( Long entityId, String entityType, int startYear, int startMonth,
        int endYear, int endMonth );

}
