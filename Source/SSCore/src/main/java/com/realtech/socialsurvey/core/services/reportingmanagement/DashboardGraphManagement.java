package com.realtech.socialsurvey.core.services.reportingmanagement;

import java.util.List;

import com.realtech.socialsurvey.core.entities.SurveyStatsReportCompany;

public interface DashboardGraphManagement
{
    
    public List<List<Object>> getSpsStatsGraph(Long entityId , String entityType);

    public List<List<Object>> getCompletionRate( Long entityId , String entityType);

    public List<List<Object>> getAverageReviewRating( Long entityId, String entityType );
    
}
