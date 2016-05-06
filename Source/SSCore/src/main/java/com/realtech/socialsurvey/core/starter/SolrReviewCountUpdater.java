package com.realtech.socialsurvey.core.starter;

import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;


/**
 * 
 * @author rohit
 *
 */


public class SolrReviewCountUpdater extends QuartzJobBean
{

    public static final Logger LOG = LoggerFactory.getLogger( SolrReviewCountUpdater.class );

    private SolrSearchService solrSearchService;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext ) throws JobExecutionException
    {
        LOG.info( "executing SolrReviewCountUpdater" );
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        solrSearchService.solrReviewCountUpdater();
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        solrSearchService = (SolrSearchService) jobMap.get( "solrSearchService" );
    }

}
