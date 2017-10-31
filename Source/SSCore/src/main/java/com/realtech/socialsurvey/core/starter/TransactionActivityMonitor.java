package com.realtech.socialsurvey.core.starter;

import java.util.List;
import java.util.Map;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.CompanyActiveUsersStats;
import com.realtech.socialsurvey.core.entities.CompanySurveyStatusStats;
import com.realtech.socialsurvey.core.services.activitymanager.ActivityManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;

public class TransactionActivityMonitor extends QuartzJobBean
{
    
    public static final Logger LOG = LoggerFactory.getLogger( CrmDataAgentIdMapper.class );
    
    private ActivityManagementService activityManagementService;
    private OrganizationManagementService organizationManagementService;
    
    private void initializeDependencies( JobDataMap jobMap )
    {
        activityManagementService = (ActivityManagementService) jobMap.get( "activityManagementService" );
        organizationManagementService = (OrganizationManagementService) jobMap.get( "organizationManagementService" );
    }
    
    
    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext ) throws JobExecutionException
    {
        // initialize the dependencies
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        LOG.info( "Executing CrmDataAgentIdMapper" );
        
        //for less incoming transactions
        List<Company> allCompanies =  organizationManagementService.getAllActiveEnterpriseCompanies();
        List<Company> companiesWithNoTransactions = activityManagementService.getCompaniesWithNoTransactionInPastNDays( allCompanies , 3 );
        activityManagementService.sendNoTransactionAlertMailForCompanies( companiesWithNoTransactions , 3 );
        
        //for less invitation mails
        List<CompanySurveyStatusStats> companySurveyStatusStatsList  =  activityManagementService.getSurveyStatusStatsForPastDay();
        List<Long> companyIdsForLessSurveyAlerts = activityManagementService.validateSurveyStatsForCompanies( companySurveyStatusStatsList );
        activityManagementService.sendHighNotProcessedTransactionAlertMailForCompanies(companyIdsForLessSurveyAlerts, allCompanies);
        
        //for less invitations in past month
        Map<Long, Long> companySurveyStatsCountsMap  = activityManagementService.getSurveyStatusStatsForPastOneMonth();
        List<CompanyActiveUsersStats> companyActiveUserCounts  = activityManagementService.getCompanyActiveUserCountForPastDay();
        activityManagementService.validateAndSentLessSurveysAlert(  companyActiveUserCounts,  companySurveyStatsCountsMap);
    }

}
