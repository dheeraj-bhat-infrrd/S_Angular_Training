package com.realtech.socialsurvey.core.dao;

import java.sql.Timestamp;

import com.realtech.socialsurvey.core.entities.ExternalSurveyTracker;


public interface ExternalSurveyTrackerDao
{

    public ExternalSurveyTracker checkExternalSurveyTrackerDetailsExist( String entityColumnName, long entityId, String source, String reviewUrl,
        Timestamp reviewDate );


    public void saveExternalSurveyTracker( String entityColumnName, long entityId, String source, String sourceLink, String reviewUrl,
        double reviewRating, int autoPostStatus, int complaintResolutionStatus, Timestamp reviewDate );

}
