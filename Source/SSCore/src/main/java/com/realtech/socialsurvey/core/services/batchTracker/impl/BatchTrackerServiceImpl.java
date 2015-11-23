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
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.SurveyDetailsDao;
import com.realtech.socialsurvey.core.entities.BatchTracker;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
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

        List<BatchTracker> batchTrackerList = batchTrackerDao.findByColumn( BatchTracker.class, CommonConstants.BATCH_TYPE_COLUMN, batchType );
        if ( batchTrackerList == null || batchTrackerList.isEmpty() ) {
            throw new NoRecordsFetchedException( "Invalid batch type" );
        }
        BatchTracker batchTracker = batchTrackerList.get( CommonConstants.INITIAL_INDEX );

        LOG.debug( "method getLastRunTimeByBatchType() ended for batch type : " + batchType );
        return batchTracker.getLastRunTime().getTime();

    }


    @Override
    @Transactional
    public void updateModifiedOnColumnByBatchType( String batchType ) throws NoRecordsFetchedException, InvalidInputException
    {
        LOG.debug( "method updateModifiedOnColumnByBatchType() started for batch type : " + batchType );
        if(batchType !=null && ! batchType.isEmpty()){
            throw new InvalidInputException("passed parameter batchtype is incorrect");
        }

        List<BatchTracker> batchTrackerList = batchTrackerDao.findByColumn( BatchTracker.class,
            CommonConstants.BATCH_TYPE_COLUMN, batchType );
        if ( batchTrackerList == null || batchTrackerList.size() <= 0 || batchTrackerList.get( CommonConstants.INITIAL_INDEX ) == null ) {
            throw new NoRecordsFetchedException( "No record Fatched For batch type : " + batchType );
        }

        BatchTracker batchTracker = batchTrackerList.get( CommonConstants.INITIAL_INDEX );
        batchTracker.setLastRunTime( new Timestamp( System.currentTimeMillis() ) );
        batchTracker.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        batchTrackerDao.update( batchTracker );

        LOG.debug( "method updateModifiedOnColumnByBatchType() ended for batch type : " + batchType );
    }


    @Override
    @Transactional
    public List<Long> getUserIdListToBeUpdated( long modifiedOn )
    {
        LOG.debug( "method getReviewCountForAgentsByModifiedOn() started " );
        List<Long> agentsIdList = surveyDetailsDao.getEntityIdListForModifiedReview( CommonConstants.AGENT_ID_COLUMN,
            modifiedOn );
        LOG.debug( "method getReviewCountForAgentsByModifiedOn() ended " );
        return agentsIdList;
    }


    /*
     * (non-Javadoc)
     * @see com.realtech.socialsurvey.core.services.batchTracker.BatchTrackerService#getReviewCountForAgentsByModifiedOn(long)
     */
    @Override
    @Transactional
    public Map<Long, Integer> getReviewCountForAgents( List<Long> agentIdList ) throws ParseException
    {
        LOG.debug( "method getReviewCountForAgents() started " );
        Map<Long, Integer> agentsReviewCount = surveyDetailsDao.getSurveyCountForAgents( agentIdList, false );
        LOG.debug( "method getReviewCountForAgents() ended " );
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
        try {
            solrSearchService.updateCompletedSurveyCountForMultipleUserInSolr( agentsReviewCount );
        } catch ( SolrException e ) {
            LOG.error( "Error while updating review count" );
        }
        LOG.debug( "method updateReviewCountForAgentsInSolr() ended" );
    }

}
