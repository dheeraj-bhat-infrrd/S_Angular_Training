package com.realtech.socialsurvey.core.starter;

import java.util.List;
import java.util.Map;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.reports.BillingReportsService;


@Component
public class CompaniesBillingReportGenerator extends QuartzJobBean
{

    public static final Logger LOG = LoggerFactory.getLogger( BillingReportGenerator.class );

    private BillingReportsService billingReportsService;

    private UserManagementService userManagementService;
    
    private BatchTrackerService batchTrackerService;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext ) throws JobExecutionException
    {
        LOG.info( "Executing CompaniesBillingReportGenerator" );
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        List<Company> companies = billingReportsService.getCompaniesWithExpiredInvoice();
        for ( Company company : companies ) {
            LOG.debug( "generating billing report for company : " + company.getCompany() );
            try {
                if ( company.getLicenseDetails() != null && company.getLicenseDetails().size() > 0 ) {
                    LicenseDetail licenseDetail = company.getLicenseDetails().get( 0 );
                    Map<String, List<Object>> data = billingReportsService.generateBillingReportDataForACompany( company
                        .getCompanyId() );
                    User companyAdmin = userManagementService.getCompanyAdmin( company.getCompanyId() );
                    String adminName = null;
                    if(licenseDetail.getRecipientMailId() != null && ! licenseDetail.getRecipientMailId().isEmpty()){
                        adminName = companyAdmin.getFirstName();
                    }
                    billingReportsService.generateBillingReportAndMail( data, licenseDetail.getRecipientMailId() , adminName);
                    billingReportsService.updateNextInvoiceBillingDateInLicenceDetail( licenseDetail );
                }
            } catch ( Exception e ) {
                LOG.error( "Error while generating and mailling billing report for company : " + company.getCompany() );
                try {
                    batchTrackerService.sendMailToAdminRegardingBatchError( CommonConstants.COMPANIES_BILLING_REPORT_GENERATOR,
                        System.currentTimeMillis(), e );
                } catch ( InvalidInputException | UndeliveredEmailException e1 ) {
                    LOG.error( "error while sende report bug mail to admin " , e1 );
                } 
                continue;
            
            }
        }

    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        billingReportsService = (BillingReportsService) jobMap.get( "billingReportsService" );
        userManagementService = (UserManagementService) jobMap.get( "userManagementService" );
        batchTrackerService = (BatchTrackerService) jobMap.get( "batchTrackerService" );
    }
}
