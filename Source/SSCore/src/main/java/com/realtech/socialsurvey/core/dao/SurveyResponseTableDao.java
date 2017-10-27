package com.realtech.socialsurvey.core.dao;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.realtech.socialsurvey.core.entities.SurveyResponseTable;
import com.realtech.socialsurvey.core.entities.SurveyResultsCompanyReport;


/**
 * @author sandra
 *
 */
public interface SurveyResponseTableDao extends GenericReportingDao<SurveyResponseTable, String>{

     /**
     * @param companyId
     * @param startDate
     * @param endDate
     * @return
     */
    public int getMaxResponseForCompanyId( long companyId, Timestamp startDate, Timestamp endDate );

}
