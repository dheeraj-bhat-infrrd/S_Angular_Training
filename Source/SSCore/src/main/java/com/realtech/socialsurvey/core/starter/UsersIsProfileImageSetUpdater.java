package com.realtech.socialsurvey.core.starter;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.utils.solr.SetIsProfileImageSetFieldForUsers;

public class UsersIsProfileImageSetUpdater extends QuartzJobBean
{
    public static final Logger LOG = LoggerFactory.getLogger( UsersIsProfileImageSetUpdater.class );
    
    private SetIsProfileImageSetFieldForUsers setIsProfileImageSetFieldForUsers;
        
    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext )
    {
        LOG.info( "Executing UsersIsProfileImageSetUpdater" );
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        setIsProfileImageSetFieldForUsers.setIsProfileImageSetField();
        LOG.info( "Finished executing UsersIsProfileImageSetUpdater" );
    }

    private void initializeDependencies( JobDataMap jobMap )
    {
        setIsProfileImageSetFieldForUsers = (SetIsProfileImageSetFieldForUsers) jobMap.get( "setIsProfileImageSetFieldForUsers" );
    }
}
