package com.realtech.socialsurvey.core.starter;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;


/**
 * Gets the list of users where number of user have been modified
 *
 */
@Component ( "updatesubscriptionprice")
public class UpdateSubscriptionPriceStarter extends QuartzJobBean
{

    public static final Logger LOG = LoggerFactory.getLogger( UpdateSubscriptionPriceStarter.class );

    @Autowired
    private ReviseSubscriptionPrice reviseSubscription;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext )
    {
        LOG.info( "ExecutingUpdateSubscriptionPriceStarter " );
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        reviseSubscription.updateSubscriptionPriceStarter();
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        reviseSubscription = (ReviseSubscriptionPrice) jobMap.get( "reviseSubscription" );
    }
}
