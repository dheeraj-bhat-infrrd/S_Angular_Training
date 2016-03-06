package com.realtech.socialsurvey.core.dao;

import java.sql.Timestamp;

import com.realtech.socialsurvey.core.entities.ZillowTempPost;


public interface AutoPostTrackerDao
{

    public boolean checkAutoPostTrackerDetailsExist( String entityColumnName, long entityId, String source, String reviewUrl,
        Timestamp reviewDate );


    public void saveAutoPostTracker( String entityColumnName, long entityId, String source, String sourceLink, String reviewUrl,
        double reviewRating, Timestamp reviewDate );

}
