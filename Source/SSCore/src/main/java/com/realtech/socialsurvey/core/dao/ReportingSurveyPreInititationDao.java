package com.realtech.socialsurvey.core.dao;

import java.sql.Timestamp;
import java.util.List;

import com.realtech.socialsurvey.core.entities.ReportingSurveyPreInititation;

public interface ReportingSurveyPreInititationDao extends GenericReportingDao<ReportingSurveyPreInititation, String>
{

    /**
     * This method returns the list of survey pre-initiation data 
     * for the incomplete survey results report.
     * @param entityType
     * @param companyId
     * @param startDate
     * @param endDate
     * @param startIndex
     * @param batchSize
     * @return
     */
    List<ReportingSurveyPreInititation> getIncompleteSurveyForReporting( String entityType, long companyId, Timestamp startDate, 
        Timestamp endDate, int startIndex, int batchSize);

}
