package com.realtech.socialsurvey.core.starter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;


public class CrmDataAgentIdMapper extends QuartzJobBean
{

    public static final Logger LOG = LoggerFactory.getLogger( CrmDataAgentIdMapper.class );

    private SurveyHandler surveyHandler;
    private EmailServices emailServices;
    private UserManagementService userManagementService;
    private String companyAdminEnabled;
    private String adminEmailId;
    private String adminName;
    private String fileDirectoryLocation;
    private BatchTrackerService batchTrackerService;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext ) throws JobExecutionException
    {

        LOG.info( "Executing CrmDataAgentIdMapper" );
        try {
            // initialize the dependencies
            initializeDependencies( jobExecutionContext.getMergedJobDataMap() );

            // update last start time
            batchTrackerService.getLastRunEndTimeAndUpdateLastStartTimeByBatchType(
                CommonConstants.BATCH_TYPE_CRM_DATA_AGENT_ID_MAPPER, CommonConstants.BATCH_NAME_CRM_DATA_AGENT_ID_MAPPER );

            Map<String, Object> corruptRecords = surveyHandler.mapAgentsInSurveyPreInitiation();
            sendCorruptDataFromCrmNotificationMail( corruptRecords );

          //updating last run time for batch in database
            batchTrackerService.updateLastRunEndTimeByBatchType( CommonConstants.BATCH_TYPE_CRM_DATA_AGENT_ID_MAPPER );
            LOG.info( "Completed CrmDataAgentIdMapper" );
        } catch ( Exception e ) {
            LOG.error( "Error in CrmDataAgentIdMapper", e );
            try {
                //update batch tracker with error message
                batchTrackerService.updateErrorForBatchTrackerByBatchType( CommonConstants.BATCH_TYPE_CRM_DATA_AGENT_ID_MAPPER,
                    e.getMessage() );
                //send report bug mail to admin
                batchTrackerService.sendMailToAdminRegardingBatchError( CommonConstants.BATCH_NAME_CRM_DATA_AGENT_ID_MAPPER,
                    System.currentTimeMillis(), e );
            } catch ( NoRecordsFetchedException | InvalidInputException e1 ) {
                LOG.error( "Error while updating error message in CrmDataAgentIdMapper " );
            } catch ( UndeliveredEmailException e1 ) {
                LOG.error( "Error while sending report excption mail to admin " );
            }
        }

    }


    @SuppressWarnings ( "unchecked")
    private void sendCorruptDataFromCrmNotificationMail( Map<String, Object> corruptRecords )
    {
        List<SurveyPreInitiation> unavailableAgents = (List<SurveyPreInitiation>) corruptRecords.get( "unavailableAgents" );
        List<SurveyPreInitiation> invalidAgents = (List<SurveyPreInitiation>) corruptRecords.get( "invalidAgents" );
        List<SurveyPreInitiation> customersWithoutName = (List<SurveyPreInitiation>) corruptRecords
            .get( "customersWithoutName" );
        List<SurveyPreInitiation> customersWithoutEmailId = (List<SurveyPreInitiation>) corruptRecords
            .get( "customersWithoutEmailId" );
        Set<Long> companies = (Set<Long>) corruptRecords.get( "companies" );

        for ( Long companyId : companies ) {
            int rownum = 1;
            int count = 1;
            boolean excelCreated = false;
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet( "Corrupt Records" );
            sheet = fillHeaders( sheet );

            for ( SurveyPreInitiation survey : unavailableAgents ) {

                if ( survey.getCompanyId() == companyId ) {
                    Row row = sheet.createRow( rownum++ );
                    row = fillCellsInRow( row, survey, count++, "Agent Not Available For This Organization " );
                }
            }
            for ( SurveyPreInitiation survey : invalidAgents ) {
                if ( survey.getCompanyId() == companyId ) {
                    Row row = sheet.createRow( rownum++ );
                    row = fillCellsInRow( row, survey, count++, "Agent Does Not Exist" );
                }
            }
            for ( SurveyPreInitiation survey : customersWithoutName ) {
                if ( survey.getCompanyId() == companyId ) {
                    Row row = sheet.createRow( rownum++ );
                    row = fillCellsInRow( row, survey, count++, "Customer Name Is Not Present" );
                }
            }
            for ( SurveyPreInitiation survey : customersWithoutEmailId ) {
                if ( survey.getCompanyId() == companyId ) {
                    Row row = sheet.createRow( rownum++ );
                    row = fillCellsInRow( row, survey, count++, "Customer Email Id Is Not Present" );
                }
            }

            String fileName = companyId + "_" + System.currentTimeMillis();
            FileOutputStream fileOutput = null;
            InputStream inputStream = null;
            File file = null;
            String filePath = null;
            try {
                file = new File( fileDirectoryLocation + File.separator + fileName + ".xls" );
                fileOutput = new FileOutputStream( file );
                file.createNewFile();
                workbook.write( fileOutput );
                filePath = file.getPath();
                excelCreated = true;
            } catch ( FileNotFoundException fe ) {
                LOG.error( "Exception caught " + fe.getMessage() );
                excelCreated = false;
            } catch ( IOException e ) {
                LOG.error( "Exception caught " + e.getMessage() );
                excelCreated = false;
            } finally {
                try {
                    fileOutput.close();
                    if ( inputStream != null ) {
                        inputStream.close();
                    }
                } catch ( IOException e ) {
                    LOG.error( "Exception caught " + e.getMessage() );
                    excelCreated = false;
                }
            }
            try {
                if ( excelCreated ) {
                    Map<String, String> attachmentsDetails = new HashMap<String, String>();
                    attachmentsDetails.put( "CorruptRecords.xls", filePath );

                    if ( companyAdminEnabled == "1" ) {
                        User companyAdmin = userManagementService.getCompanyAdmin( companyId );
                        if ( companyAdmin != null ) {
                            emailServices.sendCorruptDataFromCrmNotificationMail( companyAdmin.getFirstName(),
                                companyAdmin.getLastName(), companyAdmin.getEmailId(), attachmentsDetails );
                        }
                    } else {
                        emailServices.sendCorruptDataFromCrmNotificationMail( adminName, "", adminEmailId, attachmentsDetails );
                    }
                }
            } catch ( InvalidInputException | UndeliveredEmailException e ) {
                LOG.error( "Exception caught in sendCorruptDataFromCrmNotificationMail() while sending mail to company admin" );
            }
        }
    }


    private Row fillCellsInRow( Row row, SurveyPreInitiation survey, int counter, String reasonForFailure )
    {
        int cellnum = 0;
        Cell cell1 = row.createCell( cellnum++ );
        cell1.setCellValue( counter );
        Cell cell2 = row.createCell( cellnum++ );
        cell2.setCellValue( survey.getSurveySource() );
        Cell cell3 = row.createCell( cellnum++ );
        if ( survey.getAgentEmailId() != null ) {
            cell3.setCellValue( survey.getAgentEmailId() );
        } else {
            cell3.setCellValue( "" );
        }
        Cell cell4 = row.createCell( cellnum++ );
        if ( survey.getCustomerFirstName() != null ) {
            cell4.setCellValue( survey.getCustomerFirstName() );
        } else {
            cell4.setCellValue( "" );
        }
        Cell cell5 = row.createCell( cellnum++ );
        if ( survey.getCustomerLastName() != null ) {
            cell5.setCellValue( survey.getCustomerLastName() );
        } else {
            cell5.setCellValue( "" );
        }
        Cell cell6 = row.createCell( cellnum++ );
        if ( survey.getCustomerEmailId() != null ) {
            cell6.setCellValue( survey.getCustomerEmailId() );
        } else {
            cell6.setCellValue( "" );
        }
        Cell cell7 = row.createCell( cellnum++ );
        cell7.setCellValue( reasonForFailure );
        return row;

    }


    public HSSFSheet fillHeaders( HSSFSheet sheet )
    {
        int cellnum = 0;
        Row row = sheet.createRow( 0 );
        Cell cell1 = row.createCell( cellnum++ );
        cell1.setCellValue( "S.No" );
        Cell cell2 = row.createCell( cellnum++ );
        cell2.setCellValue( "Source" );
        Cell cell3 = row.createCell( cellnum++ );
        cell3.setCellValue( "Agent Email Id" );
        Cell cell4 = row.createCell( cellnum++ );
        cell4.setCellValue( "Customer First Name" );
        Cell cell5 = row.createCell( cellnum++ );
        cell5.setCellValue( "Customer Last Name" );
        Cell cell6 = row.createCell( cellnum++ );
        cell6.setCellValue( "Customer Email Id" );
        Cell cell7 = row.createCell( cellnum++ );
        cell7.setCellValue( "Reason For Failure" );
        return sheet;
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        surveyHandler = (SurveyHandler) jobMap.get( "surveyHandler" );
        emailServices = (EmailServices) jobMap.get( "emailServices" );
        userManagementService = (UserManagementService) jobMap.get( "userManagementService" );
        companyAdminEnabled = (String) jobMap.get( "companyAdminEnabled" );
        adminEmailId = (String) jobMap.get( "adminEmailId" );
        adminName = (String) jobMap.get( "adminName" );
        fileDirectoryLocation = (String) jobMap.get( "fileDirectoryLocation" );
        batchTrackerService = (BatchTrackerService) jobMap.get( "batchTrackerService" );
    }
}
