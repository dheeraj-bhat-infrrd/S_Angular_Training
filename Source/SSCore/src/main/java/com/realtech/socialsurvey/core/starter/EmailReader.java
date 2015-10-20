package com.realtech.socialsurvey.core.starter;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.handler.EmailHandler;


/**
 * Gets the list of users where number of user have been modified
 *
 */
@Component
public class EmailReader extends QuartzJobBean
{

    public static final Logger LOG = LoggerFactory.getLogger( EmailReader.class );

    private EmailHandler handler;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext )
    {
        LOG.info( "ExecutingUpdateSubscriptionPriceStarter " );
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        handler.startEmailProcessing();

    }


    private void initializeDependencies( JobDataMap jobMap )
    {

        handler = (EmailHandler) jobMap.get( "emailHandler" );
    }


}
