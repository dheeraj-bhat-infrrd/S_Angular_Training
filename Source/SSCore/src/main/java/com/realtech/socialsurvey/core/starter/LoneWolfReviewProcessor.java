package com.realtech.socialsurvey.core.starter;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.CrmBatchTracker;
import com.realtech.socialsurvey.core.entities.LoneWolfCrmInfo;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.integration.lonewolf.LoneWolfIntegrationApi;
import com.realtech.socialsurvey.core.integration.lonewolf.LoneWolfIntergrationApiBuilder;
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.crmbatchtracker.CRMBatchTrackerService;
import com.realtech.socialsurvey.core.services.crmbatchtrackerhistory.CRMBatchTrackerHistoryService;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;


@Component ( "lonewolfreviewprocessor")
public class LoneWolfReviewProcessor extends QuartzJobBean
{
    private static final Logger LOG = LoggerFactory.getLogger( LoneWolfReviewProcessor.class );

    private LoneWolfIntergrationApiBuilder loneWolfIntegrationApiBuilder;

    private LoneWolfIntegrationApi loneWolfIntegrationApi;

    private BatchTrackerService batchTrackerService;

    private CRMBatchTrackerService crmBatchTrackerService;

    private CRMBatchTrackerHistoryService crmBatchTrackerHistoryService;

    private EmailServices emailServices;

    private OrganizationManagementService organizationManagementService;

    private int newRecordFoundCount = 0;

    private String applicationAdminEmail;

    private String applicationAdminName;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext ) throws JobExecutionException
    {
        try {
            LOG.info( "Executing lonewolf review processor" );

            initializeDependencies( jobExecutionContext.getMergedJobDataMap() );

            // update last start time
            batchTrackerService.getLastRunEndTimeAndUpdateLastStartTimeByBatchType(
                CommonConstants.BATCH_TYPE_LONE_WOLF_REVIEW_PROCESSOR, CommonConstants.BATCH_NAME_LONE_WOLF_REVIEW_PROCESSOR );

            startLoneWolfFeedProcessing( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
            startLoneWolfFeedProcessing( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
            startLoneWolfFeedProcessing( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );
            startLoneWolfFeedProcessing( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );

            //updating last run time for batch in database
            batchTrackerService.updateLastRunEndTimeByBatchType( CommonConstants.BATCH_TYPE_LONE_WOLF_REVIEW_PROCESSOR );
        } catch ( Exception e ) {
            LOG.error( "Error in lonewolf review processor", e );
            try {
                //update batch tracker with error message
                batchTrackerService.updateErrorForBatchTrackerByBatchType(
                    CommonConstants.BATCH_TYPE_LONE_WOLF_REVIEW_PROCESSOR, e.getMessage() );

                //send report bug mail to admin
                batchTrackerService.sendMailToAdminRegardingBatchError( CommonConstants.BATCH_NAME_LONE_WOLF_REVIEW_PROCESSOR,
                    System.currentTimeMillis(), e );
            } catch ( NoRecordsFetchedException | InvalidInputException e1 ) {
                LOG.error( "Error while updating error message in lonewolf review processor " );
            } catch ( UndeliveredEmailException e1 ) {
                LOG.error( "Error while sending report execption mail to admin " );
            }
        }
    }


    private void startLoneWolfFeedProcessing( String collectionName )
    {
        LOG.debug( "Inside method startLoneWolfFeedProcessing " );

        try {
            CrmBatchTracker crmBatchTracker = null;

            // to maintain entry in crm batch tracker, get entity type and id
            String entityType = null;
            long entityId;
            if ( collectionName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION ) ) {
                entityType = CommonConstants.COMPANY_ID_COLUMN;
            } else if ( collectionName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION ) ) {
                entityType = CommonConstants.REGION_ID_COLUMN;
            } else if ( collectionName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION ) ) {
                entityType = CommonConstants.BRANCH_ID_COLUMN;
            } else if ( collectionName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION ) ) {
                entityType = CommonConstants.AGENT_ID_COLUMN;
            }

            List<OrganizationUnitSettings> organizationUnitSettingsList = organizationManagementService
                .getOrganizationUnitSettingsForCRMSource( CommonConstants.CRM_SOURCE_LONEWOLF, collectionName );
            if ( organizationUnitSettingsList != null && !organizationUnitSettingsList.isEmpty() ) {

                LOG.info( "Looping through crm list of size: " + organizationUnitSettingsList.size() );
                for ( OrganizationUnitSettings organizationUnitSettings : organizationUnitSettingsList ) {

                    LOG.info( "Getting lonewolf records for company id: " + organizationUnitSettings.getId() );
                    LoneWolfCrmInfo loneWolfCrmInfo = (LoneWolfCrmInfo) organizationUnitSettings.getCrm_info();
                    if ( loneWolfCrmInfo.getApi() != null && !loneWolfCrmInfo.getApi().isEmpty() ) {
                        entityId = organizationUnitSettings.getIden();

                        //make an entry in crm batch tracker and update last run start time
                        crmBatchTrackerService.getLastRunEndTimeAndUpdateLastStartTimeByEntityTypeAndSourceType( entityType,
                            entityId, CommonConstants.CRM_SOURCE_LONEWOLF );

                        try {

                            //TODO Fetch transactions data from lonewolf and process it.

                            //insert crmbatchTrackerHistory with count of Records Fetched
                            crmBatchTracker = crmBatchTrackerService.getCrmBatchTracker( entityType, entityId,
                                CommonConstants.CRM_SOURCE_LONEWOLF );

                            if ( crmBatchTracker != null ) {
                                crmBatchTrackerHistoryService.insertCrmBatchTrackerHistory( newRecordFoundCount,
                                    crmBatchTracker.getCrmBatchTrackerId() );
                            }

                            // update  last run end time and count of new records found in crm batch tracker
                            crmBatchTrackerService.updateLastRunEndTimeByEntityTypeAndSourceType( entityType, entityId,
                                CommonConstants.CRM_SOURCE_LONEWOLF, newRecordFoundCount );

                        } catch ( Exception e ) {
                            LOG.error( "Exception caught for collection " + collectionName + "having iden as "
                                + organizationUnitSettings.getIden(), e );

                            // update  error message in crm batch tracker
                            crmBatchTrackerService.updateErrorForBatchTrackerByEntityTypeAndSourceType( entityType, entityId,
                                CommonConstants.CRM_SOURCE_LONEWOLF, e.getMessage() );
                            try {
                                LOG.info( "Building error message for the auto post failure" );
                                String errorMsg = "Error while processing lonewolf feed for collection " + collectionName
                                    + ", profile name " + organizationUnitSettings.getProfileName() + " with iden "
                                    + organizationUnitSettings.getIden() + "at time " + new Date( System.currentTimeMillis() )
                                    + " <br>";
                                errorMsg += "<br>" + e.getMessage() + "<br><br>";
                                errorMsg += "<br>StackTrace : <br>"
                                    + ExceptionUtils.getStackTrace( e ).replaceAll( "\n", "<br>" ) + "<br>";
                                LOG.info( "Sending bug mail to admin" );
                                emailServices.sendReportBugMailToAdmin( applicationAdminName, errorMsg, applicationAdminEmail );
                                LOG.info( "Sent bug mail to admin for the auto post failure" );
                            } catch ( UndeliveredEmailException ude ) {
                                LOG.error( "error while sending report bug mail to admin ", ude );
                            } catch ( InvalidInputException iie ) {
                                LOG.error( "error while sending report bug mail to admin ", iie );
                            }
                        }
                    }
                }
            }
        } catch ( InvalidInputException | NoRecordsFetchedException e1 ) {
            LOG.info( "Could not get list of dotloop records" );
        }

    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        loneWolfIntegrationApiBuilder = (LoneWolfIntergrationApiBuilder) jobMap.get( "loneWolfIntegrationApiBuilder" );
        loneWolfIntegrationApi = loneWolfIntegrationApiBuilder.getLoneWolfIntegrationApi();
        batchTrackerService = (BatchTrackerService) jobMap.get( "batchTrackerService" );
        crmBatchTrackerService = (CRMBatchTrackerService) jobMap.get( "crmBatchTrackerService" );
        crmBatchTrackerHistoryService = (CRMBatchTrackerHistoryService) jobMap.get( "crmBatchTrackerHistoryService" );
        emailServices = (EmailServices) jobMap.get( "emailServices" );
        organizationManagementService = (OrganizationManagementService) jobMap.get( "organizationManagementService" );
        applicationAdminEmail = (String) jobMap.get( "applicationAdminEmail" );
        applicationAdminName = (String) jobMap.get( "applicationAdminName" );
    }

}
