package com.realtech.socialsurvey.core.starter;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.services.organizationmanagement.VendastaManagementService;


public class UsedVendastaTicketRemover extends QuartzJobBean
{
    public static final Logger LOG = LoggerFactory.getLogger( AccountDeactivator.class );

    private VendastaManagementService vendastaManagementService;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext )
    {
        LOG.info( "Executing UsedVendastaTicketRemover" );
        // initialize the dependencies
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        vendastaManagementService.usedVendastaTicketRemover();
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        vendastaManagementService = (VendastaManagementService) jobMap.get( "vendastaManagementService" );
    }

}
