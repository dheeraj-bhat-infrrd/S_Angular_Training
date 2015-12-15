package com.realtech.socialsurvey.core.starter;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.DisabledAccount;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;


public class AccountDeactivator extends QuartzJobBean
{

    public static final Logger LOG = LoggerFactory.getLogger( AccountDeactivator.class );

    private OrganizationManagementService organizationManagementService;
    private SolrSearchService solrSearchService;
    private EmailServices emailServices;
    private BatchTrackerService batchTrackerService;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext )
    {
        LOG.info( "Executing AccountDeactivator" );
        try{
        // initialize the dependencies
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );

        // update last start time
        batchTrackerService.getLastRunEndTimeAndUpdateLastStartTimeByBatchType(
            CommonConstants.BATCH_TYPE_ACCOUNT_DEACTIVATOR, CommonConstants.BATCH_NAME_ACCOUNT_DEACTIVATOR);

        List<DisabledAccount> disabledAccounts = organizationManagementService.disableAccounts( new Date() );
        for ( DisabledAccount account : disabledAccounts ) {
            try {
                sendAccountDisabledNotificationMail( account );
            } catch ( InvalidInputException e ) {
                LOG.error( "Invalid Input Exception caught while sending email to the company admin. Nested exception is ", e );
            }
        }
      //updating last run time for batch in database
        batchTrackerService.updateLastRunEndTimeByBatchType( CommonConstants.BATCH_TYPE_ACCOUNT_DEACTIVATOR );
        LOG.info( "Completed AccountDeactivator" );
        }catch(Exception e){
            LOG.error( "Error in AccountDeactivator", e );
            try {
                //update batch tracker with error message
                batchTrackerService.updateErrorForBatchTrackerByBatchType( CommonConstants.BATCH_TYPE_ACCOUNT_DEACTIVATOR,
                    e.getMessage() );
                //send report bug mail to admin
                batchTrackerService.sendMailToAdminRegardingBatchError( CommonConstants.BATCH_NAME_ACCOUNT_DEACTIVATOR,
                    System.currentTimeMillis(), e );
            } catch ( NoRecordsFetchedException | InvalidInputException e1 ) {
                LOG.error( "Error while updating error message in AccountDeactivator " );
            } catch ( UndeliveredEmailException e1 ) {
                LOG.error( "Error while sending report excption mail to admin " );
            }
        }
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        organizationManagementService = (OrganizationManagementService) jobMap.get( "organizationManagementService" );
        emailServices = (EmailServices) jobMap.get( "emailServices" );
        solrSearchService = (SolrSearchService) jobMap.get( "solrSearchService" );
        batchTrackerService = (BatchTrackerService) jobMap.get( "batchTrackerService" );
    }


    private void sendAccountDisabledNotificationMail( DisabledAccount disabledAccount ) throws InvalidInputException
    {
        // Send email to notify each company admin that the company account will be deactivated after 30 days so that they can take required steps.
        Company company = disabledAccount.getCompany();
        Map<String, String> companyAdmin = new HashMap<String, String>();
        try {
            companyAdmin = solrSearchService.getCompanyAdmin( company.getCompanyId() );
        } catch ( SolrException e1 ) {
            LOG.error( "SolrException caught in sendAccountDisabledNotificationMail() while trying to send mail to the company admin ." );
        }
        try {
            if ( companyAdmin != null && companyAdmin.get( "emailId" ) != null )
                emailServices.sendAccountDisabledMail( companyAdmin.get( "emailId" ), companyAdmin.get( "displayName" ),
                    companyAdmin.get( "loginName" ) );
        } catch ( InvalidInputException | UndeliveredEmailException e ) {
            LOG.error(
                "Exception caught while sending mail to " + companyAdmin.get( "displayName" ) + " .Nested exception is ", e );
        }
    }
}