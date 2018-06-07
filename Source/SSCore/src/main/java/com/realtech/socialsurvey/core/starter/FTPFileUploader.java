package com.realtech.socialsurvey.core.starter;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.services.ftpmanagement.FTPManagement;


public class FTPFileUploader extends QuartzJobBean
{

    public static final Logger LOG = LoggerFactory.getLogger( FTPFileUploader.class );

    private FTPManagement ftpManagement;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext ) throws JobExecutionException
    {
        LOG.info( "Executing FTPFileUploader" );
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        ftpManagement.startFTPFileProcessing();
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        ftpManagement = (FTPManagement) jobMap.get( "ftpManagement" );
    }
}
