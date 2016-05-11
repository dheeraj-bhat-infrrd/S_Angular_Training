package com.realtech.socialsurvey.core.starter;

import java.util.List;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;

public class AgentSettingUserEncryptedIdUpdater extends QuartzJobBean
{
    public static final Logger LOG = LoggerFactory.getLogger( ImageLoader.class );

    private UserManagementService userManagementService;
    private OrganizationManagementService organizationManagementService;

    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext ) throws JobExecutionException
    {
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        
        List<AgentSettings>  agentSettingsList = organizationManagementService.getAllAgentsFromMongo();
        for(AgentSettings agentSettings : agentSettingsList){
            try {
                organizationManagementService.updateUserEncryptedIdOfSetting( agentSettings, userManagementService.generateUserEncryptedId( agentSettings.getIden() ) );
            } catch ( InvalidInputException e ) {
                LOG.error( "Error while updating agent setting : " + agentSettings.getIden());
            }
        }
       
    }  
    
    private void initializeDependencies( JobDataMap jobMap )
    {
        organizationManagementService = (OrganizationManagementService) jobMap.get( "organizationManagementService" );
        userManagementService = (UserManagementService) jobMap.get( "userManagementService" );
    }


}
