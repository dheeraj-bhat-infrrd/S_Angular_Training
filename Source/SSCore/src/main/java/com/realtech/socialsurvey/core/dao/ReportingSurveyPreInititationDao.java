package com.realtech.socialsurvey.core.dao;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import com.realtech.socialsurvey.core.entities.ReportingSurveyPreInititation;

public interface ReportingSurveyPreInititationDao extends GenericReportingDao<ReportingSurveyPreInititation, String>
{

    List<ReportingSurveyPreInititation> getIncompleteSurveyForReporting( Timestamp startDate, Timestamp endDate, int startIndex,
        int batchSize, Set<Long> agentIds, boolean isCompanyAdmin, long companyId );

}
