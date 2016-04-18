package com.realtech.socialsurvey.core.services.crmbatchtracker.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.entities.CrmBatchTracker;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.crmbatchtracker.CRMBatchTrackerService;


/**
 * 
 * @author rohit
 *
 */
public class CRMBatchTrackerServiceImpl implements CRMBatchTrackerService
{

    public static final Logger LOG = LoggerFactory.getLogger( CRMBatchTrackerServiceImpl.class );

    @Autowired
    private GenericDao<CrmBatchTracker, Long> crmBatchTrackerDao;


    @Override
    @Transactional
    public long getLastRunEndTimeAndUpdateLastStartTimeByEntityTypeAndSourceType( String entityType, long entityId,
        String source ) throws InvalidInputException
    {
        LOG.debug( "method getLastRunEndTimeAndUpdateLastStartTimeByEntityTypeAndSourceType started" );
        if ( entityType == null || entityType.isEmpty() ) {
            throw new InvalidInputException( "passed parameter entityType is null or empty" );
        }
        if ( source == null || source.isEmpty() ) {
            throw new InvalidInputException( "passed parameter source is null or empty" );
        }
        if ( entityId <= 0l ) {
            throw new InvalidInputException( "passed parameter entityId is incorrect" );
        }


        Map<String, Object> queries = new HashMap<String, Object>();
        queries.put( CommonConstants.SOURCE_COLUMN, source );
        queries.put( entityType, entityId );
        List<CrmBatchTracker> crmBatchTrackerList = crmBatchTrackerDao.findByKeyValue( CrmBatchTracker.class, queries );

        CrmBatchTracker crmBatchTracker;
        long lastEndTime;
        long currentTime = System.currentTimeMillis();
        if ( crmBatchTrackerList == null || crmBatchTrackerList.isEmpty() ) {
            LOG.debug( "No entry found in crm batch tracker for entity type : " + entityType + " with id : " + entityId );
            //create new crm batch tracker for the record
            crmBatchTracker = new CrmBatchTracker();
            if ( entityType.equalsIgnoreCase( CommonConstants.COMPANY_ID_COLUMN ) ) {
                crmBatchTracker.setCompanyId( entityId );
            } else if ( entityType.equalsIgnoreCase( CommonConstants.REGION_ID_COLUMN ) ) {
                crmBatchTracker.setRegionId( entityId );
            } else if ( entityType.equalsIgnoreCase( CommonConstants.BRANCH_ID_COLUMN ) ) {
                crmBatchTracker.setBranchId( entityId );
            } else if ( entityType.equalsIgnoreCase( CommonConstants.AGENT_ID_COLUMN ) ) {
                crmBatchTracker.setAgentId( entityId );
            }
            crmBatchTracker.setSource( source );
            crmBatchTracker.setLastRunEndDate( new Timestamp( CommonConstants.EPOCH_TIME_IN_MILLIS ) );
            crmBatchTracker.setCreatedOn( new Timestamp( currentTime ) );
            crmBatchTracker.setRecentRecordFetchedDate( new Timestamp(CommonConstants.EPOCH_TIME_IN_MILLIS ) );
            lastEndTime = CommonConstants.EPOCH_TIME_IN_MILLIS;
        } else {
            crmBatchTracker = crmBatchTrackerList.get( CommonConstants.INITIAL_INDEX );
            lastEndTime = crmBatchTracker.getLastRunEndDate().getTime();
        }


        crmBatchTracker.setLastRunStartDate( new Timestamp( currentTime ) );
        crmBatchTracker.setModifiedOn( new Timestamp( currentTime ) );
        crmBatchTrackerDao.saveOrUpdate( crmBatchTracker );
        LOG.debug( "method getLastRunEndTimeAndUpdateLastStartTimeByEntityTypeAndSourceType ended" );
        return lastEndTime;
    }

    @Override
    @Transactional
    public void updateErrorForBatchTrackerByEntityTypeAndSourceType( String entityType, long entityId, String source,
        String error ) throws NoRecordsFetchedException, InvalidInputException
    {

        LOG.debug( "method updateErrorForBatchTrackerByEntityTypeAndSourceType started" );
        if ( entityType == null || entityType.isEmpty() ) {
            throw new InvalidInputException( "passed parameter entityType is null or empty" );
        }
        if ( source == null || source.isEmpty() ) {
            throw new InvalidInputException( "passed parameter source is null or empty" );
        }
        if ( entityId <= 0l ) {
            throw new InvalidInputException( "passed parameter entityId is incorrect" );
        }

        Map<String, Object> queries = new HashMap<String, Object>();
        queries.put( CommonConstants.SOURCE_COLUMN, source );
        queries.put( entityType, entityId );
        List<CrmBatchTracker> crmBatchTrackerList = crmBatchTrackerDao.findByKeyValue( CrmBatchTracker.class, queries );
        if ( crmBatchTrackerList == null || crmBatchTrackerList.size() <= 0
            || crmBatchTrackerList.get( CommonConstants.INITIAL_INDEX ) == null ) {
            throw new NoRecordsFetchedException( "No record Fatched For entity type : " + entityType + " with entity id : "
                + entityId );
        }

        CrmBatchTracker crmBatchTracker = crmBatchTrackerList.get( CommonConstants.INITIAL_INDEX );
        crmBatchTracker.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        crmBatchTracker.setError( "Error : " + error );
        crmBatchTrackerDao.update( crmBatchTracker );
        LOG.debug( "method updateErrorForBatchTrackerByEntityTypeAndSourceType ended" );
    }


    @Override
    @Transactional
    public void updateLastRunEndTimeByEntityTypeAndSourceType( String entityType, long entityId, String source,int lastRunRecordFetchedCount )
        throws NoRecordsFetchedException, InvalidInputException
    {
        LOG.debug( "method updateLastRunEndTimeByEntityTypeAndSourceType started" );
        if ( entityType == null || entityType.isEmpty() ) {
            throw new InvalidInputException( "passed parameter entityType is null or empty" );
        }
        if ( source == null || source.isEmpty() ) {
            throw new InvalidInputException( "passed parameter source is null or empty" );
        }
        if ( entityId <= 0l ) {
            throw new InvalidInputException( "passed parameter entityId is incorrect" );
        }

        Map<String, Object> queries = new HashMap<String, Object>();
        queries.put( CommonConstants.SOURCE_COLUMN, source );
        queries.put( entityType, entityId );
        List<CrmBatchTracker> crmBatchTrackerList = crmBatchTrackerDao.findByKeyValue( CrmBatchTracker.class, queries );
        if ( crmBatchTrackerList == null || crmBatchTrackerList.size() <= 0
            || crmBatchTrackerList.get( CommonConstants.INITIAL_INDEX ) == null ) {
            throw new NoRecordsFetchedException( "No record Fatched For entity type : " + entityType + " with entity id : "
                + entityId );
        }
        CrmBatchTracker crmBatchTracker = crmBatchTrackerList.get( CommonConstants.INITIAL_INDEX );
        crmBatchTracker.setLastRunEndDate( new Timestamp( System.currentTimeMillis() ) );
        crmBatchTracker.setLastRunRecordFetchedCount(lastRunRecordFetchedCount);
        crmBatchTracker.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        crmBatchTracker.setError( null );
        crmBatchTrackerDao.update( crmBatchTracker );
        LOG.debug( "method updateLastRunEndTimeByEntityTypeAndSourceType ended" );

    }

	@Override
	@Transactional
	public CrmBatchTracker getCrmBatchTracker(String entityType, long entityId,
			String source)throws InvalidInputException,NoRecordsFetchedException {
		// TODO Auto-generated method stub
		
		LOG.debug( "method getCrmBatchTracker started" );
        if ( entityType == null || entityType.isEmpty() ) {
            throw new InvalidInputException( "passed parameter entityType is null or empty" );
        }
        if ( source == null || source.isEmpty() ) {
            throw new InvalidInputException( "passed parameter source is null or empty" );
        }
        if ( entityId <= 0l ) {
            throw new InvalidInputException( "passed parameter entityId is incorrect" );
        }
        Map<String, Object> queries = new HashMap<String, Object>();
        queries.put( CommonConstants.SOURCE_COLUMN, source );
        queries.put( entityType, entityId );
        List<CrmBatchTracker> crmBatchTrackerList = crmBatchTrackerDao.findByKeyValue( CrmBatchTracker.class, queries );
        if ( crmBatchTrackerList == null || crmBatchTrackerList.size() <= 0
            || crmBatchTrackerList.get( CommonConstants.INITIAL_INDEX ) == null ) {
            throw new NoRecordsFetchedException( "No record Fatched For entity type : " + entityType + " with entity id : "
                + entityId );
        }
        CrmBatchTracker crmBatchTracker = crmBatchTrackerList.get( CommonConstants.INITIAL_INDEX );
        LOG.debug( "method getCrmBatchTracker ended" );
		return crmBatchTracker;
	}
}
