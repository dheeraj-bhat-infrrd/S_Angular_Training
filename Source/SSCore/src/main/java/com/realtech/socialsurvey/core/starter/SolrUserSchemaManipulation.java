package com.realtech.socialsurvey.core.starter;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.services.search.SolrSearchService;


public class SolrUserSchemaManipulation extends QuartzJobBean
{

    private SolrSearchService solrSearchService;


    private void initializeDependencies( JobDataMap jobMap )
    {
        solrSearchService = (SolrSearchService) jobMap.get( "solrSearchService" );
    }


    @Override
    protected void executeInternal( JobExecutionContext context ) throws JobExecutionException
    {
        initializeDependencies( context.getMergedJobDataMap() );
        solrSearchService.updateSolrToHideAgentsFromSearchResults();
    }
}
