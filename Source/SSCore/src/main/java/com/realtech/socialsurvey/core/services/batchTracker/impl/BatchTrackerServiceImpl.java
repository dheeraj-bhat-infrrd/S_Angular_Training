package com.realtech.socialsurvey.core.services.batchTracker.impl;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.BatchTrackerDao;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.SurveyDetailsDao;
import com.realtech.socialsurvey.core.entities.BatchTracker;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.batchTracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;


@Component
public class BatchTrackerServiceImpl implements BatchTrackerService
{
    public static final Logger LOG = LoggerFactory.getLogger( BatchTrackerServiceImpl.class );

    @Autowired
    private GenericDao<BatchTracker, Long> batchTrackerDao;

    @Autowired
    private SurveyDetailsDao surveyDetailsDao;

    @Autowired
    private SolrSearchService solrSearchService;


    /*
     * (non-Javadoc)
     * @see com.realtech.socialsurvey.core.services.batchTracker.BatchTrackerService#getLastRunTimeByBatchType(java.lang.String)
     */
    @Override
    @Transactional
    public long getLastRunTimeByBatchType( String batchType ) throws NoRecordsFetchedException
    {
        LOG.debug( "method getLastRunTimeByBatchType() started for batch type : " + batchType );
        List<BatchTracker> batchTrackerList = batchTrackerDao.findByColumn( BatchTracker.class,
            CommonConstants.BATCH_TYPE_COLUMN, batchType );
        if ( batchTrackerList.size() <= 0 || batchTrackerList.get( CommonConstants.INITIAL_INDEX ) == null ) {
            throw new NoRecordsFetchedException( "No record Fatched For batch type : " + batchType );
        }

        BatchTracker batchTracker = batchTrackerList.get( CommonConstants.INITIAL_INDEX );

        LOG.debug( "method getLastRunTimeByBatchType() ended for batch type : " + batchType );
        return batchTracker.getLastRunTime().getTime();

    }


    @Override
    @Transactional
    public void updateModifiedOnColumnByBatchType( String batchType ) throws NoRecordsFetchedException
    {
        LOG.debug( "method updateModifiedOnColumnByBatchType() started for batch type : " + batchType );

        List<BatchTracker> batchTrackerList = batchTrackerDao.findByColumn( BatchTracker.class,
            CommonConstants.BATCH_TYPE_COLUMN, batchType );
        if ( batchTrackerList.size() <= 0 || batchTrackerList.get( CommonConstants.INITIAL_INDEX ) == null ) {
            throw new NoRecordsFetchedException( "No record Fatched For batch type : " + batchType );
        }

        BatchTracker batchTracker = batchTrackerList.get( CommonConstants.INITIAL_INDEX );
        batchTracker.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        batchTrackerDao.update( batchTracker );

        LOG.debug( "method updateModifiedOnColumnByBatchType() ended for batch type : " + batchType );
    }


    /*
     * (non-Javadoc)
     * @see com.realtech.socialsurvey.core.services.batchTracker.BatchTrackerService#getReviewCountForAgentsByModifiedOn(long)
     */
    @Override
    @Transactional
    public Map<Long, Integer> getReviewCountForAgentsByModifiedOn( long modifiedOn ) throws ParseException
    {
        LOG.debug( "method getReviewCountForAgentsByModifiedOn() started " );
        Map<Long, Integer> agentsReviewCount = surveyDetailsDao.getSurveyCountInATimePeriod( CommonConstants.AGENT_ID_COLUMN,
            modifiedOn, 0, 5, false );
        LOG.debug( "method getReviewCountForAgentsByModifiedOn() ended " );
        return agentsReviewCount;
    }


    /*
     * (non-Javadoc)
     * @see com.realtech.socialsurvey.core.services.batchTracker.BatchTrackerService#updateReviewCountForAgentsInSolr(java.util.Map)
     */
    @Override
    @Transactional
    public void updateReviewCountForAgentsInSolr( Map<Long, Integer> agentsReviewCount )
    {
        LOG.debug( "method updateReviewCountForAgentsInSolr() started" );
        for ( Map.Entry<Long, Integer> entry : agentsReviewCount.entrySet() ) {
            long agentId = entry.getKey();
            int incrementCount = entry.getValue();
            try {
                solrSearchService.updateCompletedSurveyCountForUserInSolr( agentId, incrementCount );
            } catch ( SolrException e ) {
                LOG.error( "Error while updating review count for agent with id : " + agentId );
            }
        }
        LOG.debug( "method updateReviewCountForAgentsInSolr() ended" );
    }


}
