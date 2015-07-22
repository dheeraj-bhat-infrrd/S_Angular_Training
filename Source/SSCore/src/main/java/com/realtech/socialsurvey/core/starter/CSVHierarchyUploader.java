package com.realtech.socialsurvey.core.starter;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.handler.EmailHandler;
import com.realtech.socialsurvey.core.services.upload.CsvUploadService;

/**
 * Uploads the hierarchy
 *
 */
@Component
public class CSVHierarchyUploader extends QuartzJobBean{

	public static final Logger LOG = LoggerFactory.getLogger( CSVHierarchyUploader.class );
	
	private CsvUploadService uploadService;
	
	@Override
    protected void executeInternal( JobExecutionContext jobExecutionContext )
    {
        LOG.info( "ExecutingUpdateSubscriptionPriceStarter " );
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        handler.startEmailProcessing();

    }


    private void initializeDependencies( JobDataMap jobMap )
    {

    	uploadService = (EmailHandler) jobMap.get( "uploadService" );
    }
}
