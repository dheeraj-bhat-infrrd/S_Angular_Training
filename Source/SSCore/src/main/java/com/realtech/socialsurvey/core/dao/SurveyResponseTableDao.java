package com.realtech.socialsurvey.core.dao;

import java.sql.Timestamp;

import com.realtech.socialsurvey.core.entities.SurveyResponseTable;


/**
 * @author sandra
 *
 */
public interface SurveyResponseTableDao extends GenericReportingDao<SurveyResponseTable, String>{
    
    
    /**
     * @param entityId
     * @param entityType
     * @param startDate
     * @param endDate
     * @return
     */
    public int getMaxQuestion(long entityId, String entityType, Timestamp startDate, Timestamp endDate);

}
