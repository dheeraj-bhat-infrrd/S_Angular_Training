package com.realtech.socialsurvey.core.commons;

import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.core.entities.SocialMonitorFeedData;


public class SocialMonitorStreamDataComparator implements Comparator<SocialMonitorFeedData>
{

    private static final Logger LOG = LoggerFactory.getLogger( SocialMonitorStreamDataComparator.class );

    
    @Override
    public int compare( SocialMonitorFeedData socialMonitorFeedData1, SocialMonitorFeedData socialMonitorFeedData2 )
    {
        LOG.debug( "Comparing SocialMonitorFeedData" );

        if ( socialMonitorFeedData1.getUpdatedOn() >  socialMonitorFeedData2.getUpdatedOn()) {
            return -1;
        } else if ( socialMonitorFeedData1.getUpdatedOn() < socialMonitorFeedData2.getUpdatedOn() ) {
            return 1;
        } else {
            return 0;
        }
    }
}
