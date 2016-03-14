package com.realtech.socialsurvey.core.starter;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.HierarchyUpload;
import com.realtech.socialsurvey.core.entities.UploadStatus;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.upload.HierarchyStructureUploadService;


@Component
public class HierarchyUploadProcessor implements Runnable
{
    public static final Logger LOG = LoggerFactory.getLogger( HierarchyUploadProcessor.class );

    @Autowired
    private HierarchyStructureUploadService hierarchyStructureUploadService;

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
                    List<UploadStatus> initiatedUploads = hierarchyStructureUploadService.findInitiatedHierarchyUploads();
                    for ( UploadStatus uploadStatus : initiatedUploads ) {
                        Company company = uploadStatus.getCompany();
                        User adminUser = hierarchyStructureUploadService.getUser( uploadStatus.getAdminUserId() );
                        try {
                            // Update uploadStatus to 0
                            uploadStatus.setStatus( CommonConstants.HIERARCHY_UPLOAD_PROCESSING );
                            uploadStatus.setMessage( CommonConstants.UPLOAD_MSG_STARTED );
                            hierarchyStructureUploadService.updateUploadStatus( uploadStatus );
                            //Upload hierarchy
                            HierarchyUpload hierarchyUpload = hierarchyStructureUploadService
                                .fetchHierarchyToBeUploaded( company );
                            Map<String, List<String>> errors = hierarchyStructureUploadService.uploadHierarchy(
                                hierarchyUpload, company, adminUser );
                            if ( !errors.isEmpty() ) {
                                //TODO: Send a mail or store somewhere
                            }
                            //Add uploadStatus COMPLETED
                            UploadStatus completedUploadStatus = new UploadStatus();
                            completedUploadStatus.setCompany( company );
                            completedUploadStatus.setStatus( CommonConstants.HIERARCHY_UPLOAD_UPLOAD_COMPLETE );
                            completedUploadStatus.setAdminUserId( adminUser.getUserId() );
                            completedUploadStatus.setMessage( "Hierarchy upload completed successfully." );
                            hierarchyStructureUploadService.addUploadStatusEntry( completedUploadStatus );
                        } catch ( InvalidInputException e ) {
                            //If error occurs, add uploadStatus ERROR and store message
                            UploadStatus errorUploadStatus = new UploadStatus();
                            errorUploadStatus.setCompany( company );
                            errorUploadStatus.setMessage( CommonConstants.UPLOAD_MSG_UPLOAD_ERROR + ". ERROR: "
                                + e.getMessage() );
                            errorUploadStatus.setAdminUserId( adminUser.getUserId() );
                            errorUploadStatus.setStatus( CommonConstants.HIERARCHY_UPLOAD_ERROR );
                            hierarchyStructureUploadService.addUploadStatusEntry( errorUploadStatus );
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
