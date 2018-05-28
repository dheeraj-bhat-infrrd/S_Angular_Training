/**
 * 
 */
package com.realtech.socialsurvey.core.starter;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.services.socialmonitor.feed.SocialFeedService;


/**
 * @author manish
 *
 */
public class ArchiveSocialFeedCollectionJob extends QuartzJobBean {
	
	public static final Logger LOG = LoggerFactory.getLogger(ArchiveSocialFeedCollectionJob.class);
	
	private SocialFeedService socialFeedService;

    /* (non-Javadoc)
     * @see org.springframework.scheduling.quartz.QuartzJobBean#executeInternal(org.quartz.JobExecutionContext)
     */
    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext ) throws JobExecutionException
    {
        LOG.info( "Starting archiving old social feeds.");
        socialFeedService.moveDocumentToArchiveCollection();
        LOG.info( "Finished archiving old social feeds.");
    }

    public SocialFeedService getSocialFeedService()
    {
        return socialFeedService;
    }

    public void setSocialFeedService( SocialFeedService socialFeedService )
    {
        this.socialFeedService = socialFeedService;
    }
    
    
}
