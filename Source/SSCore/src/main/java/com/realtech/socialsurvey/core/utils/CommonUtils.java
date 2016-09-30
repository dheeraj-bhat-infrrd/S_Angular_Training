package com.realtech.socialsurvey.core.utils;

import org.springframework.stereotype.Component;

@Component
public class CommonUtils
{

    public String getAgentNameForHiddenAgentCompany(String firstName , String lastName){
        
        String agentName =  firstName;
        if ( lastName != null && ! lastName.isEmpty() ) {
            agentName = firstName + " " + lastName.substring( 0 , 1 );
        }
        
        return agentName;
    }
}
