package com.realtech.socialsurvey.core.services.crmbatchtracker.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.entities.BatchTracker;
import com.realtech.socialsurvey.core.entities.CrmBatchTracker;
import com.realtech.socialsurvey.core.services.crmbatchtracker.CRMBatchTrackerService;

/**
 * 
 * @author rohit
 *
 */
public class CRMBatchTrackerServiceImpl implements CRMBatchTrackerService
{
    
    
    @Autowired
    private GenericDao<CrmBatchTracker, Long> crmBatchTrackerDao;

    @Override
    @Transactional
    public long getLastRunEndTimeAndUpdateLastStartTimeByEntityTypeAndSourceType( String entityType  , long entityId , String source){
        
        Map<String, Object> queries = new HashMap<String, Object>();
        queries.put( CommonConstants.SOURCE_COLUMN, source );
        queries.put( entityType, entityId );
        List<CrmBatchTracker> crmBatchTrackerList = crmBatchTrackerDao.findByKeyValue( CrmBatchTracker.class, queries );
        
        CrmBatchTracker crmBatchTracker;
        long lastEndTime;
        long currentTime = System.currentTimeMillis();
        if ( crmBatchTrackerList == null || crmBatchTrackerList.isEmpty() ) {
            //create new batch tracker for the record
            crmBatchTracker = new CrmBatchTracker();
            crmBatchTracker.setCompanyId( entityId );
            crmBatchTracker.setSource( source );
            crmBatchTracker.setRecentRecordFetchedEndDate( new Timestamp( CommonConstants.EPOCH_TIME_IN_MILLIS ) );
            crmBatchTracker.setCreatedOn( new Timestamp( currentTime ) );
            lastEndTime = CommonConstants.EPOCH_TIME_IN_MILLIS;
        } else {
            crmBatchTracker = crmBatchTrackerList.get( CommonConstants.INITIAL_INDEX );
            lastEndTime = crmBatchTracker.getRecentRecordFetchedEndDate().getTime();
        }


        crmBatchTracker.setRecentRecordFetchedStartDate( new Timestamp( currentTime ) );
        crmBatchTracker.setModifiedOn( new Timestamp( currentTime ) );
        crmBatchTrackerDao.saveOrUpdate( crmBatchTracker );
        
        return lastEndTime;
    }
}
