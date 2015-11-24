package com.realtech.socialsurvey.core.utils.solr;

import java.sql.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.SocialPostDao;
import com.realtech.socialsurvey.core.dao.SolrImportDao;
import com.realtech.socialsurvey.core.entities.SocialPost;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.batchTracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;


public class SocialPostsDeltaImport
{
    public static final Logger LOG = LoggerFactory.getLogger( SocialPostsDeltaImport.class );


    @Resource
    @Qualifier ( "solrimport")
    private SolrImportDao solrImportDao;

    @Autowired
    private SolrSearchService solrSearchService;

    @Value ( "${SOCIAL_POST_BATCH_SIZE}")
    private int pageSize;

    @Autowired
    private SocialPostDao socialPostDao;

    @Autowired
    private BatchTrackerService batchTrackerService;


    /**
     * Method to fetch social posts from mongodb and index it into Solr
     */
    @Transactional
    public void importSocialPostsIntoSolr( boolean fromBeginning )
    {
        LOG.info( "Started run method of SocialPostsDeltaImport" );
        int pageNo = 1;
        List<SocialPost> socialPosts = null;
        Date lastBuildTime = null;
        boolean errorOccured = false;
        Long nextLastBuild = 0l;
        if ( fromBeginning ) {
            lastBuildTime = new Date( 0l );
        } else {
            try {
                //Long lastBuild = solrSearchService.getLastBuildTimeForSocialPosts().getTime();
                //JIRA SS-1287
                //Get last build time from batch tracker table
                Long lastBuild = batchTrackerService
                    .getLastRunTimeByBatchType( CommonConstants.BATCH_TYPE_SOCIAL_MONITOR_LAST_BUILD );
                lastBuildTime = new Date( lastBuild );
                nextLastBuild = System.currentTimeMillis();
            } catch ( NoRecordsFetchedException e ) {
                LOG.error( "Unable to fetch lastBuildTime from Batch Tracker table. Reason : ", e );
                errorOccured = true;
            }
        }
        do {
            try {

                socialPosts = socialPostDao.fetchSocialPostsPageforSolrIndexing( pageSize * ( pageNo - 1 ), pageSize,
                    lastBuildTime );
                LOG.debug( "Fetched " + socialPosts.size() + " posts." );
            } catch ( NoRecordsFetchedException e ) {
                LOG.error( "NoRecordsFetchedException occurred while fetching social posts" );
            }


            if ( socialPosts == null || socialPosts.isEmpty() ) {
                break;
            }

            LOG.debug( "Adding social posts to Solr" );
            try {
                solrSearchService.addSocialPostsToSolr( socialPosts );
            } catch ( SolrException e ) {
                LOG.error( "SolrException occurred while adding social posts to solr", e );
                errorOccured = true;
            }
            pageNo++;
        } while ( !socialPosts.isEmpty() );
        //Update last build time in batch tracker table
        if ( !( errorOccured ) ) {
            try {
                batchTrackerService.updateModifiedOnColumnByBatchTypeAndTime(
                    CommonConstants.BATCH_TYPE_SOCIAL_MONITOR_LAST_BUILD, nextLastBuild );
            } catch ( NoRecordsFetchedException e ) {
                LOG.error( "Unable to update last build time in batch tracker. Reason : ", e );
            }
        }
        LOG.info( "Finished run method of SocialPostsDeltaImport" );

    }
}
