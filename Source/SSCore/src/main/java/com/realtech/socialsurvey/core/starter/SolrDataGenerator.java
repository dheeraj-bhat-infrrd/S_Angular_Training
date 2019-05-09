package com.realtech.socialsurvey.core.starter;

import com.realtech.socialsurvey.core.handler.SolrDataImporterHandler;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;


@Component
public class SolrDataGenerator extends QuartzJobBean
{
    public static final Logger LOG = LoggerFactory.getLogger( SolrDataGenerator.class );

    private SolrDataImporterHandler solrDataImporterHandler;

    @Override protected void executeInternal( JobExecutionContext jobExecutionContext ) throws JobExecutionException
    {
        LOG.info( "Executing SolrDataGenerator" );
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        solrDataImporterHandler.importData();
    }

    private void initializeDependencies( JobDataMap jobMap )
    {
        solrDataImporterHandler = (SolrDataImporterHandler) jobMap.get( "solrDataImporterHandler" );
    }
}
