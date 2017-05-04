package com.realtech.socialsurvey.core.starter;

import java.util.Set;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyBuilder;

public class SurveyQuestionUpdater extends QuartzJobBean
{

    public static final Logger LOG = LoggerFactory.getLogger( SurveyTransactionDateUpdater.class );

    
    private SurveyBuilder surveyBuilder;
    
    private OrganizationManagementService organizationManagementService;
    
    private UserManagementService userManagementService; 


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext ) throws JobExecutionException
    {
        LOG.info( "SurveyQuestionUpdater started" );
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        
        Set<Company> companies = organizationManagementService.getAllCompanies();
        for(Company company : companies){
            try {
                User user = userManagementService.getCompanyAdmin( company.getCompanyId() );
                surveyBuilder.checkIfSurveyIsDefaultAndClone( user );
            } catch ( Exception e ) {
                LOG.error( "Error in SurveyQuestionUpdater for company " + company.getCompanyId() , e );
                e.printStackTrace();
            }
        }
        
        LOG.info( "SurveyQuestionUpdater finished" );
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        surveyBuilder = (SurveyBuilder) jobMap.get( "surveyBuilder" );
        organizationManagementService = (OrganizationManagementService) jobMap.get( "organizationManagementService" );
        userManagementService = (UserManagementService) jobMap.get( "userManagementService" );

    }


}