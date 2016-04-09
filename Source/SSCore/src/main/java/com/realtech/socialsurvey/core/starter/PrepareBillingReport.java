package com.realtech.socialsurvey.core.starter;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.FileUpload;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.DashboardService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.reports.BillingReportsService;
import com.realtech.socialsurvey.core.services.upload.CsvUploadService;


@Component
public class PrepareBillingReport implements Runnable
{
    public static final Logger LOG = LoggerFactory.getLogger( PrepareBillingReport.class );

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private CsvUploadService csvUploadService;   
    
    @Autowired
    private BillingReportsService billingReportsService;

    @Autowired
    private BatchTrackerService batchTrackerService;




    @Override
    public void run()
    {
        LOG.info( "Started method to prepare billing report" );
        // Check if a request for billing report is present in FILE_UPLOAD table
        while ( true ) {
            try {
                List<FileUpload> filesToBeUploadedGenerated = dashboardService.getReportsToBeSent();
                if ( filesToBeUploadedGenerated != null && !( filesToBeUploadedGenerated.isEmpty() ) ) {
                    FileUpload fileUpload = filesToBeUploadedGenerated.get( 0 );
                    //FileName stores the recipient mail ID
                    String recipientMailId = fileUpload.getFileName();

                    try {
                        // update the status to be processing
                        fileUpload.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
                        fileUpload.setStatus( CommonConstants.STATUS_UNDER_PROCESSING );
                        csvUploadService.updateFileUploadRecord( fileUpload );

                        if(fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_BILLING_REPORT){
                         // prepare and send the billing report to admin
                            Map<String, List<Object>> dataToGenerateBillingReport = billingReportsService.generateBillingReportDataForCompanies();
                            billingReportsService.generateBillingReportAndMail( dataToGenerateBillingReport , recipientMailId , null );
                            
                        }else if (fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_COMPANY_USERS_REPORT){
                            Company company = fileUpload.getCompany();
                            if(company != null){
                                Map<Integer, List<Object>> dataToGenerateForCompanyUserReport = dashboardService.downloadCompanyUsersReportData( company.getCompanyId() );
                                dashboardService.generateCompanyReportAndMail( dataToGenerateForCompanyUserReport, recipientMailId , null , company );
                            }
                        } else if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_COMPANY_HIERARCHY_REPORT ) {
                            Company company = fileUpload.getCompany();
                            if ( company != null ) {
                                dashboardService.generateCompanyHierarchyReportAndMail( company.getCompanyId(), recipientMailId, null );
                            }
                        }
                        
                        // update the status to be processed
                        fileUpload.setStatus( CommonConstants.STATUS_INACTIVE );
                        fileUpload.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
                        csvUploadService.updateFileUploadRecord( fileUpload );
                    } catch ( InvalidInputException | UndeliveredEmailException | ProfileNotFoundException e ) {
                        LOG.error( "Error in generating billing report generator " , e );
                        try {
                            String reportType = null;
                            if(fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_BILLING_REPORT){
                                reportType = CommonConstants.BATCH_FILE_UPLOAD_REPORTS_GENERATOR_BILLING_REPORT;
                            }else if(fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_COMPANY_USERS_REPORT){
                                reportType = CommonConstants.BATCH_FILE_UPLOAD_REPORTS_GENERATOR_USERS_REPORT;
                            }else if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_COMPANY_HIERARCHY_REPORT ){
                                reportType = CommonConstants.BATCH_FILE_UPLOAD_REPORTS_GENERATOR_HIERARCHY_REPORT;
                            }
                            String batchName = CommonConstants.BATCH_NAME_FILE_UPLOAD_REPORTS_GENERATOR + " For " + reportType;
                            
                            batchTrackerService.sendMailToAdminRegardingBatchError( batchName,
                                System.currentTimeMillis(), e );
                        } catch ( InvalidInputException | UndeliveredEmailException e1 ) {
                            LOG.error( "error while sende report bug mail to admin " , e1 );
                        } 
                        continue;
                    }
                }
            } catch ( NoRecordsFetchedException e ) {
                LOG.debug( "No files to be uploaded. Sleep for a minute" );
                try {
                    Thread.sleep( 1000 * 60 );
                } catch ( InterruptedException e1 ) {
                    LOG.warn( "Thread interrupted" );
                    break;
                }
            }
        }
    }


}
