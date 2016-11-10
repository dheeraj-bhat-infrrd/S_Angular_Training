package com.realtech.socialsurvey.core.starter;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;


public class SolrAgentSearchCriteriaUpdator extends QuartzJobBean
{
    private SolrUserSearchCriteriaProcessor solrUserSearchCriteriaProcessor;


    private void initializeDependencies( JobDataMap jobMap )
    {
        solrUserSearchCriteriaProcessor = (SolrUserSearchCriteriaProcessor) jobMap.get( "solrUserSearchCriteriaProcessor" );
    }


    @Override
    protected void executeInternal( JobExecutionContext context ) throws JobExecutionException
    {
        initializeDependencies( context.getMergedJobDataMap() );
        solrUserSearchCriteriaProcessor.solrAgentSearchCriteriaUpdator();
    }
}
