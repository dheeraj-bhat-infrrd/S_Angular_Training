package com.realtech.socialsurvey.core.starter;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.HierarchyUploadDao;
import com.realtech.socialsurvey.core.entities.ParsedHierarchyUpload;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.upload.HierarchyUploadService;


@Component
public class HierarchyUploadProcessor implements Runnable
{
    public static final Logger LOG = LoggerFactory.getLogger( HierarchyUploadProcessor.class );

    @Autowired
    private HierarchyUploadService hierarchyUploadService;

    @Autowired
    private HierarchyUploadDao hierarchyUploadDao;

    @Autowired
    private BatchTrackerService batchTrackerService;


    @Override
    public void run()
    {
        try {

            LOG.info( "Checking if any company hierarchy needs to be uploaded" );
            batchTrackerService.getLastRunEndTimeAndUpdateLastStartTimeByBatchType(
                CommonConstants.BATCH_TYPE_HIERARCHY_UPLOAD_PROCESSOR, CommonConstants.BATCH_NAME_HIERARCHY_UPLOAD_PROCESSOR );
            while ( true ) {
                try {
                    List<ParsedHierarchyUpload> initiatedUploads = hierarchyUploadService.findInitiatedHierarchyUploads();
                    for ( ParsedHierarchyUpload upload : initiatedUploads ) {
                        try {

                            //Upload hierarchy
                            hierarchyUploadService.processHierarchyUploadXlsx( upload );

                        } catch ( Exception error ) {
                            LOG.error( "Error while processing upload for company with ID: " + upload.getCompanyId(), error );

                            upload.setHasGeneralErrors( true );
                            // set the error message 
                            if ( upload.getGeneralErrors() == null ) {
                                upload.setGeneralErrors( new ArrayList<String>() );
                            }
                            upload.getGeneralErrors().add( "Error while processing upload for company with ID: "
                                + upload.getCompanyId() + ", Reason: " + error.getMessage() );

                            upload.setStatus( CommonConstants.HIERARCHY_UPLOAD_STATUS_UNSCHEDULED_ABORT );
                            hierarchyUploadDao.reinsertParsedHierarchyUpload( upload );
                            continue;
                        }
                    }
                } catch ( NoRecordsFetchedException e ) {
                    LOG.debug( "No initiated upload entries. Sleep for a minute" );
                    try {
                        Thread.sleep( 1000 * 60 );
                    } catch ( InterruptedException e1 ) {
                        LOG.warn( "Thread interrupted" );
                        break;
                    }
                }
            }
            //updating last run time for batch in database
            batchTrackerService.updateLastRunEndTimeByBatchType( CommonConstants.BATCH_TYPE_HIERARCHY_UPLOAD_PROCESSOR );
        } catch ( Exception e ) {
            LOG.error( "Error in HierarchyUploadProcessor", e );
            try {
                //update batch tracker with error message
                batchTrackerService.updateErrorForBatchTrackerByBatchType(
                    CommonConstants.BATCH_TYPE_HIERARCHY_UPLOAD_PROCESSOR, e.getMessage() );
                //send report bug mail to admin
                batchTrackerService.sendMailToAdminRegardingBatchError( CommonConstants.BATCH_NAME_HIERARCHY_UPLOAD_PROCESSOR,
                    System.currentTimeMillis(), e );
            } catch ( NoRecordsFetchedException | InvalidInputException e1 ) {
                LOG.error( "Error while updating error message in HierarchyUploadProcessor " );
            } catch ( UndeliveredEmailException e1 ) {
                LOG.error( "Error while sending report excption mail to admin " );
            }
        }
    }
}
