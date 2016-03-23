package com.realtech.socialsurvey.core.dao.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.ExternalSurveyTrackerDao;
import com.realtech.socialsurvey.core.entities.ExternalSurveyTracker;


@Component ( "externalSurveyTracker")
public class ExternalSurveyTrackerDaoImpl extends GenericDaoImpl<ExternalSurveyTracker, Integer> implements ExternalSurveyTrackerDao
{

    @Override
    @Transactional
    public ExternalSurveyTracker checkExternalSurveyTrackerDetailsExist( String entityColumnName, long entityId, String source, String reviewUrl,
        Timestamp reviewDate )
    {
        Map<String, Object> queries = new HashMap<String, Object>();
        queries.put( CommonConstants.ENTITY_COLUMN_NAME_COLUMN, entityColumnName );
        queries.put( CommonConstants.ZILLOW_ENTITY_ID_COLUMN, entityId );
        queries.put( CommonConstants.REVIEW_SOURCE_COLUMN, source );
        queries.put( CommonConstants.REVIEW_SOURCE_URL_COLUMN, reviewUrl );
        queries.put( CommonConstants.REVIEW_DATE_COLUMN, reviewDate );

        List<ExternalSurveyTracker> externalSurveyTrackerList = findByKeyValue( ExternalSurveyTracker.class, queries );

        if ( externalSurveyTrackerList == null || externalSurveyTrackerList.size() == 0 )
            return null;
        return externalSurveyTrackerList.get( 0 );
    }


    @Override
    @Transactional
    public void saveExternalSurveyTracker( String entityColumnName, long entityId, String source, String sourceLink,
        String reviewUrl, double reviewRating, int autoPostStatus, int complaintResolutionStatus,Timestamp reviewDate, String postedOn )
    {
        if ( checkExternalSurveyTrackerDetailsExist( entityColumnName, entityId, source, reviewUrl, reviewDate ) == null ) {
            ExternalSurveyTracker externalSurveyTracker = new ExternalSurveyTracker();
            externalSurveyTracker.setEntityColumnName( entityColumnName );
            externalSurveyTracker.setEntityId( entityId );
            externalSurveyTracker.setReviewSource( source );
            externalSurveyTracker.setReviewSourceLink( sourceLink );
            externalSurveyTracker.setReviewSourceUrl( reviewUrl );
            externalSurveyTracker.setReviewRating( reviewRating );
            externalSurveyTracker.setAutoPostStatus( autoPostStatus );
            externalSurveyTracker.setComplaintResolutionStatus( complaintResolutionStatus );
            externalSurveyTracker.setReviewDate( reviewDate );
            externalSurveyTracker.setPostedOn( postedOn );
            externalSurveyTracker.setCreatedOn( new Timestamp( System.currentTimeMillis() ) );
            externalSurveyTracker.setCreatedBy( String.valueOf( entityId ) );
            externalSurveyTracker.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
            externalSurveyTracker.setModifiedBy( String.valueOf( entityId ) );

            save( externalSurveyTracker );
        }
    }
}
