package com.realtech.socialsurvey.core.starter;

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

public class DeactivatedAccountPurger extends QuartzJobBean {

	public static final Logger LOG = LoggerFactory.getLogger(DeactivatedAccountPurger.class);
	
	private OrganizationManagementService organizationManagementService;
	private EmailServices emailServices;
	private String accountPermDeleteSpan;
	private SolrSearchService solrSearchService;
	private BatchTrackerService batchTrackerService;
	
	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) {
		LOG.info("Executing DeactivatedAccountPurger");
		try{
		// initialize the dependencies
		initializeDependencies(jobExecutionContext.getMergedJobDataMap());
		
		// update last start time
        batchTrackerService.getLastRunEndTimeAndUpdateLastStartTimeByBatchType(
            CommonConstants.BATCH_TYPE_DEACTIVATED_ACCOUNT_PURGER, CommonConstants.BATCH_NAME_DEACTIVATED_ACCOUNT_PURGER);
		
		int maxDaysToPurgeAccount = Integer.parseInt(accountPermDeleteSpan);
		List<DisabledAccount> disabledAccounts = organizationManagementService.getAccountsForPurge(maxDaysToPurgeAccount);
		for(DisabledAccount account : disabledAccounts){
			try {
				sendAccountDeletedNotificationMail(account);
				purgeCompany(account.getCompany());
			}
			catch (InvalidInputException e) {
				LOG.error("Invalid Input Exception caught while sending email to the company admin. Nested exception is ", e);
			}
		}
		
		//updating last run time for batch in database
        batchTrackerService.updateLastRunEndTimeByBatchType( CommonConstants.BATCH_TYPE_DEACTIVATED_ACCOUNT_PURGER );
		LOG.info("Completed DeactivatedAccountPurger");
		}catch(Exception e){
            LOG.error( "Error in DeactivatedAccountPurger", e );
            try {
                //update batch tracker with error message
                batchTrackerService.updateErrorForBatchTrackerByBatchType( CommonConstants.BATCH_TYPE_DEACTIVATED_ACCOUNT_PURGER,
                    e.getMessage() );
                //send report bug mail to admin
                batchTrackerService.sendMailToAdminRegardingBatchError( CommonConstants.BATCH_NAME_DEACTIVATED_ACCOUNT_PURGER,
                    System.currentTimeMillis(), e );
            } catch ( NoRecordsFetchedException | InvalidInputException e1 ) {
                LOG.error( "Error while updating error message in DeactivatedAccountPurger " );
            } catch ( UndeliveredEmailException e1 ) {
                LOG.error( "Error while sending report excption mail to admin " );
            }
        }
	}

	private void initializeDependencies(JobDataMap jobMap) {
		organizationManagementService = (OrganizationManagementService) jobMap.get("organizationManagementService");
		emailServices = (EmailServices) jobMap.get("emailServices");
		accountPermDeleteSpan = (String) jobMap.get("accountPermDeleteSpan");
		solrSearchService = (SolrSearchService) jobMap.get("solrSearchService");
		batchTrackerService = (BatchTrackerService) jobMap.get( "batchTrackerService" );
	}

	
	/*
	 * Method to purge all the details of the company
	 */
	private void purgeCompany(Company company){
		LOG.debug("Method to delete all the company details purgeCompany() started.");

		try {
			organizationManagementService.purgeCompany(company);
		}
		catch (InvalidInputException e) {
			LOG.error("InvalidInputException caught in purgeCompany(). Nested exception is ", e);
		}
		catch (SolrException e) {
			LOG.error("SolrException caught in purgeCompany(). Nested exception is ", e);
		}
				
		LOG.debug("Method to delete all the company details purgeCompany() finished.");
	}
	
	private void sendAccountDeletedNotificationMail(DisabledAccount disabledAccount) throws InvalidInputException {
		// Send email to notify each company admin that the company account will be deactivated after 30 days so that they can take required steps.
		Company company = disabledAccount.getCompany();
		Map<String, String> companyAdmin = new HashMap<String, String>();
		try {
			companyAdmin = solrSearchService.getCompanyAdmin(company.getCompanyId());
		}
		catch (SolrException e1) {
			LOG.error("SolrException caught in sendAccountDeletedNotificationMail() while trying to send mail to the company admin .");
		}
		try {
			if(companyAdmin != null && companyAdmin.get("emailId")!=null)
				emailServices.sendAccountDeletionMail(companyAdmin.get("emailId"), companyAdmin.get("displayName"), companyAdmin.get("loginName"));
		}
		catch (InvalidInputException|UndeliveredEmailException e) {
			LOG.error("Exception caught while sending mail to " + companyAdmin.get("displayName") + " .Nested exception is ", e);
		}
	}
}