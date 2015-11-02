package com.realtech.socialsurvey.core.starter;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.utils.solr.SocialPostsFullImport;


/**
 * Quartz job to import social posts from mongodb into Solr
 *
 */
public class SolrSocialPostsImport extends QuartzJobBean
{
    public static final Logger LOG = LoggerFactory.getLogger( SolrSocialPostsImport.class );
    private SocialPostsFullImport socialPostsFullImport;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext )
    {
        LOG.info( "Executing SolrSocialPostsImport" );
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        socialPostsFullImport.importSocialPostsIntoSolr();

        LOG.info( "Finished the SolrSocialPostsImport" );
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        socialPostsFullImport = (SocialPostsFullImport) jobMap.get( "socialPostsFullImport" );
    }
}
