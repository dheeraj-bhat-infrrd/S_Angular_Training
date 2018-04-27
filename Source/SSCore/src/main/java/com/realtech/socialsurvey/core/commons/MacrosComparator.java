package com.realtech.socialsurvey.core.commons;


import java.util.Comparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.core.entities.SocialMonitorMacro;

public class MacrosComparator implements Comparator<SocialMonitorMacro>
{

    private static final Logger LOG = LoggerFactory.getLogger( MacrosComparator.class );


    @Override
    public int compare( SocialMonitorMacro socialMonitorMacro1, SocialMonitorMacro socialMonitorMacro2 )
    {
        LOG.debug( "Comparing SocialMonitorMacro" );

        if ( socialMonitorMacro1.getLastUsedTime() > socialMonitorMacro2.getLastUsedTime() ) {
            return -1;
        } else if ( socialMonitorMacro1.getLastUsedTime() < socialMonitorMacro2.getLastUsedTime() ) {
            return 1;
        } else {
            return 0;
        }
    }
}

