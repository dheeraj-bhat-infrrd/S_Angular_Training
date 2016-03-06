package com.realtech.socialsurvey.core.dao.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.AutoPostTrackerDao;
import com.realtech.socialsurvey.core.entities.AutoPostTracker;


@Component ( "autoPostTracker")
public class AutoPostTrackerDaoImpl extends GenericDaoImpl<AutoPostTracker, Integer> implements AutoPostTrackerDao
{

    @Override
    @Transactional
    public boolean checkAutoPostTrackerDetailsExist( String entityColumnName, long entityId, String source, String reviewUrl,
        Timestamp reviewDate )
    {
        Map<String, Object> queries = new HashMap<String, Object>();
        queries.put( CommonConstants.ENTITY_COLUMN_NAME_COLUMN, entityColumnName );
        queries.put( CommonConstants.ZILLOW_ENTITY_ID_COLUMN, entityId );
        queries.put( CommonConstants.REVIEW_SOURCE_COLUMN, source );
        queries.put( CommonConstants.REVIEW_SOURCE_URL_COLUMN, reviewUrl );
        queries.put( CommonConstants.REVIEW_DATE_COLUMN, reviewDate );

        List<AutoPostTracker> autoPostTrackerList = findByKeyValue( AutoPostTracker.class, queries );

        if ( autoPostTrackerList == null || autoPostTrackerList.size() == 0 )
            return false;
        return true;
    }


    @Override
    @Transactional
    public void saveAutoPostTracker( String entityColumnName, long entityId, String source, String sourceLink,
        String reviewUrl, double reviewRating, Timestamp reviewDate )
    {
        if ( !checkAutoPostTrackerDetailsExist( entityColumnName, entityId, source, reviewUrl, reviewDate ) ) {
            AutoPostTracker autoPostTracker = new AutoPostTracker();
            autoPostTracker.setEntityColumnName( entityColumnName );
            autoPostTracker.setEntityId( entityId );
            autoPostTracker.setReviewSource( source );
            autoPostTracker.setReviewSourceLink( sourceLink );
            autoPostTracker.setReviewSourceUrl( reviewUrl );
            autoPostTracker.setReviewRating( reviewRating );
            autoPostTracker.setReviewDate( reviewDate );
            autoPostTracker.setCreatedOn( new Timestamp( System.currentTimeMillis() ) );
            autoPostTracker.setCreatedBy( String.valueOf( entityId ) );
            autoPostTracker.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
            autoPostTracker.setModifiedBy( String.valueOf( entityId ) );

            save( autoPostTracker );
        }
    }
}
