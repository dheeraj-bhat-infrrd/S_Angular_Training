package com.realtech.socialsurvey.core.starter;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.handler.HierarchyUploadHandler;


public class HierarchyUploader extends QuartzJobBean
{
    public static final Logger LOG = LoggerFactory.getLogger( HierarchyUploader.class );

    //TODO: Make new handler
    private HierarchyUploadHandler uploadHandler;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext )
    {
        LOG.info( "Executing HierarchyUploader" );
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        uploadHandler.startHierarchyUpload();

    }


    private void initializeDependencies( JobDataMap jobMap )
    {

        uploadHandler = (HierarchyUploadHandler) jobMap.get( "hierarchyUploadHandler" );
    }
}
