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


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext ) throws JobExecutionException
    {
        LOG.info( "Executing CompaniesBillingReportGenerator" );
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        billingReportsService.companiesBillingReportGenerator();
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        billingReportsService = (BillingReportsService) jobMap.get( "billingReportsService" );
    }
}
