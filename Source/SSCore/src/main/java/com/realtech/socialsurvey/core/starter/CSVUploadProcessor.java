package com.realtech.socialsurvey.core.starter;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.FileUpload;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.upload.CsvUploadService;


@Component
public class CSVUploadProcessor implements Runnable
{

    public static final Logger LOG = LoggerFactory.getLogger( CSVUploadProcessor.class );

    @Autowired
    private CsvUploadService csvUploadService;

    @Autowired
    private BatchTrackerService batchTrackerService;


    @Override
    public void run()
    {


        try {
            LOG.info( "Checking if any hierarchy needs to be uploaded" );
            //update last run start time
            batchTrackerService.getLastRunEndTimeAndUpdateLastStartTimeByBatchType(
                CommonConstants.BATCH_TYPE_CSV_HIERARCHY_UPLOAD_PROCESSOR,
                CommonConstants.BATCH_NAME_CSV_HIERARCHY_UPLOAD_PROCESSOR );
            while ( true ) {
                LOG.debug( "Checking for any new file upload" );
                // check if there are any files to be uploaded
                try {
                    List<FileUpload> filesToBeUploaded = csvUploadService.getFilesToBeUploaded();
                    for ( FileUpload fileUpload : filesToBeUploaded ) {
                        try {
                            // update the status to be processing
                            fileUpload.setStatus( CommonConstants.STATUS_UNDER_PROCESSING );
                            csvUploadService.updateFileUploadRecord( fileUpload );
                            // parse the csv
                            csvUploadService.parseAndUploadTempCsv( fileUpload );
                            // update the status to be processed
                            fileUpload.setStatus( CommonConstants.STATUS_INACTIVE );
                            csvUploadService.updateFileUploadRecord( fileUpload );
                        } catch ( InvalidInputException e ) {
                            LOG.debug( "Error updating the status" );
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
            //updating last run time for batch in database
            batchTrackerService.updateLastRunEndTimeByBatchType( CommonConstants.BATCH_TYPE_CSV_HIERARCHY_UPLOAD_PROCESSOR );
        } catch ( Exception e ) {
            LOG.error( "Error in CSVUploadProcessor", e );
            try {
                //update batch tracker with error message
                batchTrackerService.updateErrorForBatchTrackerByBatchType(
                    CommonConstants.BATCH_TYPE_CSV_HIERARCHY_UPLOAD_PROCESSOR, e.getMessage() );
                //send report bug mail to admin
                batchTrackerService.sendMailToAdminRegardingBatchError(
                    CommonConstants.BATCH_NAME_CSV_HIERARCHY_UPLOAD_PROCESSOR, System.currentTimeMillis(), e );
            } catch ( NoRecordsFetchedException | InvalidInputException e1 ) {
                LOG.error( "Error while updating error message in CSVUploadProcessor " );
            } catch ( UndeliveredEmailException e1 ) {
                LOG.error( "Error while sending report excption mail to admin " );
            }
        }
    }

}
