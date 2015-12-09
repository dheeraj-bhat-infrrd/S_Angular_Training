package com.realtech.socialsurvey.core.starter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.dao.EmailDao;
import com.realtech.socialsurvey.core.entities.EmailEntity;
import com.realtech.socialsurvey.core.entities.EmailObject;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.mail.EmailSender;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;


@Component
@Scope ( value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EmailProcessor implements Runnable
{

    public static final Logger LOG = LoggerFactory.getLogger( EmailProcessor.class );

    @Autowired
    EmailDao emailDao;

    @Value ( "${EMAIL_RETRY_COUNT}")
    private String retryCount;

    @Value ( "${APPLICATION_ADMIN_EMAIL}")
    private String adminEmailId;

    @Value ( "${APPLICATION_ADMIN_NAME}")
    private String adminName;

    @Value ( "${FILE_DIRECTORY_LOCATION}")
    private String fileDirectoryLocation;

    @Autowired
    private Utils utils;

    @Autowired
    private EmailServices emailServices;

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private BatchTrackerService batchTrackerService;


    @Override
    public void run()
    {
        try {
            //update last run start time
            batchTrackerService.getLastRunEndTimeAndUpdateLastStartTimeByBatchType(
                CommonConstants.BATCH_TYPE_EMAIL_READER , CommonConstants.BATCH_NAME_EMAIL_READER );
            Map<EmailEntity, String> errorEmails = new HashMap<EmailEntity, String>();
            while ( true ) {
                List<EmailObject> emailObjectList = emailDao.findAllEmailsToBeSent();
                if ( emailObjectList.isEmpty() ) {
                    try {
                        Thread.sleep( 60000 );
                    } catch ( InterruptedException ie ) {
                        LOG.error( "Exception Caught " + ie.getMessage() );
                    }

                }
                for ( EmailObject emailObject : emailObjectList ) {
                    EmailEntity emailEntity = null;
                    try {
                        emailEntity = (EmailEntity) utils.deserializeObject( emailObject.getEmailBinaryObject() );
                        if ( !emailSender.sendEmailByEmailEntity( emailEntity ) ) {
                            LOG.warn( " Email Sending Failed, Trying again " );
                            errorEmails.put( emailEntity, "unable to send email" );
                        } else {
                            LOG.debug( "Email Sent Successfully " );
                            LOG.debug( "Removing The Email From Database" + emailObject.getId() );
                            emailDao.deleteEmail( emailObject );
                        }
                    } catch ( Exception e ) {
                        LOG.error( "Exception caught " + e.getMessage() );
                        errorEmails.put( emailEntity, e.getMessage() );
                    }

                }
                try {
                    if ( errorEmails.size() > 10 ) {
                        LOG.debug( "Send 10 invalid records at a time " );
                        sendInvalidEmails( errorEmails );
                        LOG.debug( "Clearing old exception emails " );
                        errorEmails.clear();
                    }
                } catch ( Exception ex ) {
                    LOG.error( "Exception while sending invalid mails to admin", ex );
                    throw ex;
                }

              //Update last run end time in batch tracker table
                batchTrackerService.updateLastRunEndTimeByBatchType( CommonConstants.BATCH_TYPE_EMAIL_READER );
            }

        } catch ( Exception e ) {
            LOG.error( "Error in EmailProcessor", e );
            try {
                //update batch tracker with error message
                batchTrackerService.updateErrorForBatchTrackerByBatchType( CommonConstants.BATCH_TYPE_EMAIL_READER,
                    e.getMessage() );
                //send report bug mail to admin
                batchTrackerService.sendMailToAdminRegardingBatchError( CommonConstants.BATCH_NAME_EMAIL_READER,
                    System.currentTimeMillis(), e );
            } catch ( NoRecordsFetchedException | InvalidInputException e1 ) {
                LOG.error( "Error while updating error message in EmailProcessor " );
            } catch ( UndeliveredEmailException e1 ) {
                LOG.error( "Error while sending report excption mail to admin " );
            }
        }
    }


    private void sendInvalidEmails( Map<EmailEntity, String> errorEmails )
    {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet( "Invalid Emails" );
        sheet = fillHeaders( sheet );
        int rownum = 1;
        for ( Map.Entry<EmailEntity, String> entry : errorEmails.entrySet() ) {
            EmailEntity emailEntity = entry.getKey();
            Row row = sheet.createRow( rownum++ );
            row = fillCellsInRow( row, emailEntity, entry.getValue() );
        }
        createExcelForAttachment( workbook );
    }


    private void createExcelForAttachment( HSSFWorkbook workbook )
    {
        String fileName = "invalidEmails_" + System.currentTimeMillis();
        FileOutputStream fileOutput = null;
        InputStream inputStream = null;
        File file = null;
        String filePath = null;
        boolean excelCreated = false;
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
                attachmentsDetails.put( "InvalidEmails.xls", filePath );

                emailServices.sendInvalidEmailsNotificationMail( adminName, "", adminEmailId, attachmentsDetails );

            }
        } catch ( InvalidInputException | UndeliveredEmailException e ) {
            LOG.error( "Exception caught in sendCorruptDataFromCrmNotificationMail() while sending mail to company admin" );
        }
    }


    private Row fillCellsInRow( Row row, EmailEntity entity, String reasonForFailure )
    {
        int cellnum = 0;
        Cell cell2 = row.createCell( cellnum++ );
        cell2.setCellValue( entity.getSubject() );
        Cell cell3 = row.createCell( cellnum++ );
        cell3.setCellValue( entity.getBody() );
        Cell cell4 = row.createCell( cellnum++ );
        cell4.setCellValue( entity.getSenderName() );
        Cell cell5 = row.createCell( cellnum++ );
        cell5.setCellValue( reasonForFailure );
        return row;

    }


    private HSSFSheet fillHeaders( HSSFSheet sheet )
    {
        int cellnum = 0;
        Row row = sheet.createRow( 0 );
        Cell cell2 = row.createCell( cellnum++ );
        cell2.setCellValue( "Email Subject" );
        Cell cell3 = row.createCell( cellnum++ );
        cell3.setCellValue( "Email Body" );
        Cell cell4 = row.createCell( cellnum++ );
        cell4.setCellValue( "Sender Name" );
        Cell cell5 = row.createCell( cellnum++ );
        cell5.setCellValue( "Reason For Failure" );
        return sheet;
    }
}
