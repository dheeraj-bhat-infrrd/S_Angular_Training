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
import com.realtech.socialsurvey.core.services.reportingmanagement.ReportingDashboardManagement;
import com.realtech.socialsurvey.core.services.reports.BillingReportsService;
import com.realtech.socialsurvey.core.services.upload.FileUploadService;


@Component
public class PrepareBillingReport implements Runnable
{
    public static final Logger LOG = LoggerFactory.getLogger( PrepareBillingReport.class );

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private BillingReportsService billingReportsService;

    @Autowired
    private BatchTrackerService batchTrackerService;
    
    @Autowired
    private ReportingDashboardManagement reportingDashboardManagement;

    @Autowired
    private FileUploadService fileUploadService;


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
                    //Stored Filename in S3
                    String locationInS3 = null;

                    try {
                        // update the status to be processing
                        fileUpload.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
                        fileUpload.setStatus( CommonConstants.STATUS_UNDER_PROCESSING );
                        fileUploadService.updateFileUploadRecord( fileUpload );

                        if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_BILLING_REPORT ) {
                            // prepare and send the billing report to admin
                            Map<Integer, List<Object>> dataToGenerateBillingReport = billingReportsService
                                .generateBillingReportDataForCompanies();
                            billingReportsService.generateBillingReportAndMail( dataToGenerateBillingReport, recipientMailId,
                                null );

                        } else if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_COMPANY_USERS_REPORT ) {
                            Company company = fileUpload.getCompany();
                            if ( company != null ) {
                                Map<Integer, List<Object>> dataToGenerateForCompanyUserReport = dashboardService
                                    .downloadCompanyUsersReportData( company.getCompanyId() );
                                dashboardService.generateCompanyReportAndMail( dataToGenerateForCompanyUserReport,
                                    recipientMailId, null, company );
                            }
                        } else if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_COMPANY_HIERARCHY_REPORT ) {
                            Company company = fileUpload.getCompany();
                            if ( company != null ) {
                                dashboardService.generateCompanyHierarchyReportAndMail( company.getCompanyId(), recipientMailId,
                                    null );
                            }
                        } else if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_COMPANY_REGISTRATION_REPORT ) {
                            dashboardService.generateCompanyRegistrationReportAndMail( fileUpload.getStartDate(),
                                fileUpload.getEndDate(), recipientMailId, null );
                        } else if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_SURVEY_DATA_REPORT ) {
                            dashboardService.generateSurveyDataReportAndMail( fileUpload.getStartDate(),
                                fileUpload.getEndDate(), fileUpload.getProfileLevel(), fileUpload.getProfileValue(),
                                fileUpload.getAdminUserId(), fileUpload.getCompany().getCompanyId(), recipientMailId, null );
                        } else if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_USER_RANKING_REPORT ) {
                            dashboardService.generateUserRankingReportAndMail( fileUpload.getStartDate(),
                                fileUpload.getEndDate(), fileUpload.getProfileLevel(), fileUpload.getProfileValue(),
                                fileUpload.getAdminUserId(), fileUpload.getCompany().getCompanyId(), recipientMailId, null );
                        } else if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_SOCIAL_MONITOR_REPORT ) {
                            dashboardService.generateSocialMonitorReportAndMail( fileUpload.getStartDate(),
                                fileUpload.getEndDate(), fileUpload.getProfileLevel(), fileUpload.getProfileValue(),
                                fileUpload.getAdminUserId(), fileUpload.getCompany().getCompanyId(), recipientMailId, null );
                        } else if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_INCOMPLETE_SURVEY_REPORT ) {
                            dashboardService.generateIncompleteSurveyReportAndMail( fileUpload.getStartDate(),
                                fileUpload.getEndDate(), fileUpload.getProfileLevel(), fileUpload.getProfileValue(),
                                fileUpload.getAdminUserId(), fileUpload.getCompany().getCompanyId(), recipientMailId, null );
                        } else if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_USER_ADOPTION_REPORT ) {
                            dashboardService.generateUserAdoptionReportAndMail( fileUpload.getStartDate(),
                                fileUpload.getEndDate(), fileUpload.getProfileLevel(), fileUpload.getProfileValue(),
                                fileUpload.getAdminUserId(), fileUpload.getCompany().getCompanyId(), recipientMailId, null );
                        } else if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_STATS_REPORT ){
                            locationInS3 = reportingDashboardManagement.generateSurveyStatsForReporting( fileUpload.getProfileValue(), fileUpload.getProfileLevel(),
                                fileUpload.getAdminUserId() );
                        } else if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_VERIFIED_USERS_REPORT ){
                            locationInS3 = reportingDashboardManagement.generateUserAdoptionForReporting( fileUpload.getProfileValue(), fileUpload.getProfileLevel(),
                                fileUpload.getAdminUserId() );
                        } else if (fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_COMPANY_USERS_REPORT){
                            locationInS3 = reportingDashboardManagement.generateCompanyUserForReporting( fileUpload.getProfileValue(), fileUpload.getProfileLevel(),
                                fileUpload.getAdminUserId() );
                        } else if (fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_COMPANY_DETAILS_REPORT){
                        	locationInS3 = reportingDashboardManagement.generateCompanyDetailsReport( fileUpload.getProfileValue(), fileUpload.getProfileLevel());
                         
                        }else if (fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_TRANSACTION_REPORT){
                             locationInS3 = reportingDashboardManagement.generateSurveyTransactionForReporting( fileUpload.getProfileValue(), fileUpload.getProfileLevel(),
                                 fileUpload.getAdminUserId(),fileUpload.getStartDate() );

                        }else if (fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_USER_RANKING_MONTHLY_REPORT){
                            //change this to have user ranking report 
                            int type = 2;
                            locationInS3 = reportingDashboardManagement.generateUserRankingForReporting( fileUpload.getProfileValue(), fileUpload.getProfileLevel(),
                                fileUpload.getAdminUserId(),fileUpload.getStartDate() , type);

                       }else if (fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_USER_RANKING_YEARLY_REPORT){
                           //change this to have user ranking report 
                           int type = 1;
                           locationInS3 = reportingDashboardManagement.generateUserRankingForReporting( fileUpload.getProfileValue(), fileUpload.getProfileLevel(),
                               fileUpload.getAdminUserId(),fileUpload.getStartDate() , type);

                      } else if (fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_RESULTS_REPORT){
                      	locationInS3 = reportingDashboardManagement.generateSurveyResultsReport( fileUpload.getProfileValue(), fileUpload.getProfileLevel(),
                                fileUpload.getAdminUserId(),fileUpload.getStartDate(),fileUpload.getEndDate());
                     
                      } else if(fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_INCOMPLETE_SURVEY_REPORT){
                          locationInS3 = reportingDashboardManagement.generateIncompleteSurveyResultsReport( fileUpload.getProfileValue(), fileUpload.getProfileLevel(),
                              fileUpload.getAdminUserId(),fileUpload.getStartDate(),fileUpload.getEndDate());
                      } else if(fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_NPS_WEEK_REPORT){
                          int type = 1;
                          locationInS3 = reportingDashboardManagement.generateNpsReportForWeekOrMonth( fileUpload.getProfileValue(), fileUpload.getProfileLevel(), fileUpload.getStartDate(), type);
                      } else if(fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_NPS_MONTH_REPORT){
                          int type = 2;
                          locationInS3 = reportingDashboardManagement.generateNpsReportForWeekOrMonth( fileUpload.getProfileValue(), fileUpload.getProfileLevel(),
                              fileUpload.getStartDate(), type);
                      } else if (fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_BRANCH_RANKING_MONTHLY_REPORT){
                          locationInS3 = reportingDashboardManagement.generateBranchRankingReportMonth( fileUpload.getProfileValue(), fileUpload.getProfileLevel(),
                              fileUpload.getAdminUserId(),fileUpload.getStartDate());

                     } else if (fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_BRANCH_RANKING_YEARLY_REPORT){
                         locationInS3 = reportingDashboardManagement.generateBranchRankingReportYear( fileUpload.getProfileValue(), fileUpload.getProfileLevel(),
                             fileUpload.getAdminUserId(),fileUpload.getStartDate());

                    } 

                        // update the status to be processed
                        fileUpload.setStatus( CommonConstants.STATUS_DONE );
                        fileUpload.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
                        if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_STATS_REPORT
                            || fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_VERIFIED_USERS_REPORT
                            || fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_COMPANY_USERS_REPORT
                            || fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_RESULTS_REPORT
                            || fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_TRANSACTION_REPORT
                            || fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_USER_RANKING_MONTHLY_REPORT
                            || fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_USER_RANKING_YEARLY_REPORT
                            || fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_INCOMPLETE_SURVEY_REPORT
                            || fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_COMPANY_DETAILS_REPORT
                            || fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_NPS_WEEK_REPORT
                            || fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_NPS_MONTH_REPORT
                            || fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_BRANCH_RANKING_MONTHLY_REPORT
                            || fileUpload
                                .getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_BRANCH_RANKING_YEARLY_REPORT ) {
                            if ( locationInS3 != null && locationInS3 != "" ) {
                                fileUpload.setFileName( locationInS3 );
                            } else {
                                throw new Exception( "Generating report file and/or uploading to S3 failed." );
                            }
                        }
                        fileUploadService.updateFileUploadRecord( fileUpload );
                    } catch ( Exception e ) {
                        LOG.error( "Error in generating billing report generator ", e );
                        
                        try {
                            // update the status to be processed
                            fileUpload.setStatus( CommonConstants.STATUS_FAIL );
                            fileUpload.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
                            fileUploadService.updateFileUploadRecord( fileUpload );
                            String reportType = null;
                            if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_BILLING_REPORT ) {
                                reportType = CommonConstants.BATCH_FILE_UPLOAD_REPORTS_GENERATOR_BILLING_REPORT;
                            } else if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_COMPANY_USERS_REPORT ) {
                                reportType = CommonConstants.BATCH_FILE_UPLOAD_REPORTS_GENERATOR_USERS_REPORT;
                            } else if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_COMPANY_HIERARCHY_REPORT ) {
                                reportType = CommonConstants.BATCH_FILE_UPLOAD_REPORTS_GENERATOR_HIERARCHY_REPORT;
                            } else if ( fileUpload
                                .getUploadType() == CommonConstants.FILE_UPLOAD_COMPANY_REGISTRATION_REPORT ) {
                                reportType = CommonConstants.BATCH_FILE_UPLOAD_REPORTS_GENERATOR_REGISTRATION_REPORT;
                            } else if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_SURVEY_DATA_REPORT ) {
                                reportType = CommonConstants.BATCH_FILE_UPLOAD_REPORTS_GENERATOR_SURVEY_DATA_REPORT;
                            } else if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_USER_RANKING_REPORT ) {
                                reportType = CommonConstants.BATCH_FILE_UPLOAD_REPORTS_GENERATOR_USER_RANKING_REPORT;
                            } else if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_SOCIAL_MONITOR_REPORT ) {
                                reportType = CommonConstants.BATCH_FILE_UPLOAD_REPORTS_GENERATOR_SOCIAL_MONITOR_REPORT;
                            } else if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_INCOMPLETE_SURVEY_REPORT ) {
                                reportType = CommonConstants.BATCH_FILE_UPLOAD_REPORTS_GENERATOR_INCOMPLETE_SURVEY_REPORT;
                            } else if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_USER_ADOPTION_REPORT ) {
                                reportType = CommonConstants.BATCH_FILE_UPLOAD_REPORTS_GENERATOR_USER_ADOPTION_REPORT;
                            } else if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_STATS_REPORT ){
                                reportType = CommonConstants.BATCH_FILE_UPLOAD_REPORTS_GENERATOR_REPORTING_SURVEY_STATS_REPORT;
                            } else if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_VERIFIED_USERS_REPORT ){
                                reportType = CommonConstants.BATCH_FILE_UPLOAD_REPORTS_GENERATOR_REPORTING_VERIFIED_USERS_REPORT;
                            } else if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_COMPANY_USERS_REPORT ){
                                reportType = CommonConstants.BATCH_FILE_UPLOAD_REPORTS_GENERATOR_REPORTING_COMPANY_USER_REPORT;
                            }else if(fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_RESULTS_REPORT){
                                reportType = CommonConstants.BATCH_FILE_UPLOAD_REPORTS_GENERATOR_REPORTING_SURVEY_RESULTS_COMPANY_REPORT;
                            }else if(fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_USER_RANKING_MONTHLY_REPORT){
                                reportType = CommonConstants.BATCH_FILE_UPLOAD_REPORTS_GENERATOR_REPORTING_USER_RANKING_MONTHLY_REPORT;
                            }else if(fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_USER_RANKING_YEARLY_REPORT){
                                reportType = CommonConstants.BATCH_FILE_UPLOAD_REPORTS_GENERATOR_REPORTING_USER_RANKING_YEARLY_REPORT;
                            }else if(fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_NPS_WEEK_REPORT){
                                reportType = CommonConstants.BATCH_FILE_UPLOAD_REPORTS_GENERATOR_REPORTING_NPS_WEEK_REPORT;
                            }else if(fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_NPS_MONTH_REPORT){
                                reportType = CommonConstants.BATCH_FILE_UPLOAD_REPORTS_GENERATOR_REPORTING_NPS_MONTH_REPORT;
                            }else if(fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_BRANCH_RANKING_MONTHLY_REPORT){
                                reportType = CommonConstants.BATCH_FILE_UPLOAD_REPORTS_GENERATOR_REPORTING_BRANCH_RANKING_MONTHLY_REPORT;
                            }else if(fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_BRANCH_RANKING_YEARLY_REPORT){
                                reportType = CommonConstants.BATCH_FILE_UPLOAD_REPORTS_GENERATOR_REPORTING_BRANCH_RANKING_YEARLY_REPORT;
                            }
                            
                            String batchName = CommonConstants.BATCH_NAME_FILE_UPLOAD_REPORTS_GENERATOR + " For " + reportType;
                            batchTrackerService.sendMailToAdminRegardingBatchError( batchName, System.currentTimeMillis(), e );
                            
                        } catch ( InvalidInputException | UndeliveredEmailException e1 ) {
                            LOG.error( "error while sende report bug mail to admin ", e1 );
                        } catch ( Exception exception ) {
                            LOG.error( "General Error in setting fail and sending mail , in generating billing report generator ", exception );
                            break;
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
                }  catch ( Exception exception ) {
                    LOG.error( "General Error in the first catch block when no record is fetched in generating billing report generator ", exception );
                    break;
                }
            }  catch ( Exception exception ) {
                LOG.error( "General Error in the first try block in generating billing report generator ", exception );
                break;
            }
        }
    }
}
