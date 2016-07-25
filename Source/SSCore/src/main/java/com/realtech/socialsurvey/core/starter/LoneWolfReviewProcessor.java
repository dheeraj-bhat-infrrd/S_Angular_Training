package com.realtech.socialsurvey.core.starter;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.CrmBatchTracker;
import com.realtech.socialsurvey.core.entities.LoneWolfAgentCommission;
import com.realtech.socialsurvey.core.entities.LoneWolfClientContact;
import com.realtech.socialsurvey.core.entities.LoneWolfCrmInfo;
import com.realtech.socialsurvey.core.entities.LoneWolfMember;
import com.realtech.socialsurvey.core.entities.LoneWolfTier;
import com.realtech.socialsurvey.core.entities.LoneWolfTransaction;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.User;
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
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.core.utils.LoneWolfRestUtils;

import retrofit.mime.TypedByteArray;


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
    private LoneWolfRestUtils loneWolfRestUtils;
    private UserManagementService userManagementService;
    private SurveyHandler surveyHandler;
    private Utils utils;
    private int newRecordFoundCount = 0;
    private String applicationAdminEmail;
    private String applicationAdminName;
    private String maskEmail;


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
                    if ( !StringUtils.isEmpty( loneWolfCrmInfo.getApiToken() )
                        && !StringUtils.isEmpty( loneWolfCrmInfo.getClientCode() )
                        && !StringUtils.isEmpty( loneWolfCrmInfo.getSecretKey() ) ) {

                        entityId = organizationUnitSettings.getIden();

                        //make an entry in crm batch tracker and update last run start time
                        crmBatchTrackerService.getLastRunEndTimeAndUpdateLastStartTimeByEntityTypeAndSourceType( entityType,
                            entityId, CommonConstants.CRM_SOURCE_LONEWOLF );

                        try {

                            //Fetch transactions data from lonewolf.
                            List<LoneWolfTransaction> loneWolfTransactions = fetchLoneWolfTransactionsData( loneWolfCrmInfo );

                            //Fetch members data from lonewolf.
                            Map<String, LoneWolfMember> membersByName = fetchLoneWolfMembersData( loneWolfCrmInfo );

                            //Process lone wolf transactions and put it in survey pre initiation table to send surveys
                            processLoneWolfTransactions( loneWolfTransactions, membersByName, collectionName, entityId );

                            //insert crmbatchTrackerHistory with count of Records Fetched
                            crmBatchTracker = crmBatchTrackerService.getCrmBatchTracker( entityType, entityId,
                                CommonConstants.CRM_SOURCE_LONEWOLF );

                            if ( crmBatchTracker != null ) {
                                crmBatchTrackerHistoryService.insertCrmBatchTrackerHistory( newRecordFoundCount,
                                    crmBatchTracker.getCrmBatchTrackerId(), CommonConstants.CRM_SOURCE_LONEWOLF );
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
            LOG.info( "Could not get list of lone wolf records" );
        }
    }


    private void processLoneWolfTransactions( List<LoneWolfTransaction> loneWolfTransactions,
        Map<String, LoneWolfMember> membersByName, String collectionName, long organizationUnitId )
    {
        if ( !loneWolfTransactions.isEmpty() ) {
            for ( LoneWolfTransaction transaction : loneWolfTransactions ) {
                newRecordFoundCount++;
                if ( transaction != null && transaction.getTiers() != null && !transaction.getTiers().isEmpty() ) {
                    for ( LoneWolfTier tier : transaction.getTiers() ) {
                        if ( tier != null && tier.getAgentCommissions() != null && !tier.getAgentCommissions().isEmpty() ) {
                            for ( LoneWolfAgentCommission agentCommission : tier.getAgentCommissions() ) {
                                if ( agentCommission != null && agentCommission.getAgent() != null ) {
                                    LoneWolfMember member = membersByName.get( getKeyForMembersDataMap(
                                        agentCommission.getAgent().getFirstName(), agentCommission.getAgent().getLastName() ) );
                                    if ( transaction.getClientContacts() != null
                                        && !transaction.getClientContacts().isEmpty() ) {
                                        for ( LoneWolfClientContact client : transaction.getClientContacts() ) {
                                            SurveyPreInitiation surveyPreInitiation = new SurveyPreInitiation();
                                            surveyPreInitiation = setCollectionDetails( surveyPreInitiation, collectionName,
                                                organizationUnitId );
                                            surveyPreInitiation.setCreatedOn( new Timestamp( System.currentTimeMillis() ) );
                                            surveyPreInitiation.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
                                            String customerEmailId = null;
                                            if ( client.getEmailAddresses() != null && !client.getEmailAddresses().isEmpty() ) {
                                                customerEmailId = client.getEmailAddresses().get( 0 ).getAddress();
                                                if ( maskEmail.equals( CommonConstants.YES_STRING ) ) {
                                                    customerEmailId = utils.maskEmailAddress( customerEmailId );
                                                }
                                            }
                                            surveyPreInitiation.setCustomerEmailId( customerEmailId );
                                            surveyPreInitiation.setCustomerFirstName( client.getFirstName() );
                                            surveyPreInitiation.setCustomerLastName( client.getLastName() );
                                            surveyPreInitiation.setLastReminderTime( utils.convertEpochDateToTimestamp() );
                                            String agentEmailId = null;
                                            if ( member.getEmailAddresses() != null && !member.getEmailAddresses().isEmpty() ) {
                                                agentEmailId = member.getEmailAddresses().get( 0 ).getAddress();
                                                if ( maskEmail.equals( CommonConstants.YES_STRING ) ) {
                                                    agentEmailId = utils.maskEmailAddress( agentEmailId );
                                                }
                                            }
                                            surveyPreInitiation.setAgentEmailId( agentEmailId );
                                            surveyPreInitiation
                                                .setEngagementClosedTime( new Timestamp( System.currentTimeMillis() ) );
                                            surveyPreInitiation
                                                .setStatus( CommonConstants.STATUS_SURVEYPREINITIATION_NOT_PROCESSED );
                                            surveyPreInitiation.setSurveySource( CommonConstants.CRM_SOURCE_LONEWOLF );
                                            surveyPreInitiation.setSurveySourceId( transaction.getNumber() );
                                            try {
                                                surveyHandler.saveSurveyPreInitiationObject( surveyPreInitiation );
                                            } catch ( InvalidInputException e ) {
                                                LOG.error( "Unable to insert this record ", e );
                                            } catch ( Exception e ) {
                                                LOG.error( "Unable to insert this record ", e );
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    private List<LoneWolfTransaction> fetchLoneWolfTransactionsData( LoneWolfCrmInfo loneWolfCrmInfo )
    {
        //generating authorization header
        String authHeader = loneWolfRestUtils.generateAuthorizationHeaderFor( LoneWolfIntegrationApi.loneWolfTransactionUrl,
            loneWolfCrmInfo.getSecretKey(), loneWolfCrmInfo.getApiToken(), loneWolfCrmInfo.getClientCode() );

        //fetching lone wolf transaction data
        retrofit.client.Response transactionResponse = loneWolfIntegrationApi.fetchClosedTransactions( authHeader,
            loneWolfRestUtils.MD5_EMPTY );

        String responseString = transactionResponse != null
            ? new String( ( (TypedByteArray) transactionResponse.getBody() ).getBytes() ) : null;
        List<LoneWolfTransaction> loneWolfTransactions = responseString != null
            ? new Gson().fromJson( responseString, new TypeToken<List<LoneWolfTransaction>>() {}.getType() ) : null;

        return loneWolfTransactions;
    }


    private Map<String, LoneWolfMember> fetchLoneWolfMembersData( LoneWolfCrmInfo loneWolfCrmInfo )
    {
        //generating authorization header
        String authHeader = loneWolfRestUtils.generateAuthorizationHeaderFor( LoneWolfIntegrationApi.loneWolfMemberUrl,
            loneWolfCrmInfo.getSecretKey(), loneWolfCrmInfo.getApiToken(), loneWolfCrmInfo.getClientCode() );

        //fetching lone wolf members data
        retrofit.client.Response response = loneWolfIntegrationApi.fetchMemberDetails( authHeader,
            loneWolfRestUtils.MD5_EMPTY );

        String responseString = response != null ? new String( ( (TypedByteArray) response.getBody() ).getBytes() ) : null;
        List<LoneWolfMember> members = responseString != null
            ? new Gson().fromJson( responseString, new TypeToken<List<LoneWolfMember>>() {}.getType() ) : null;
        Map<String, LoneWolfMember> membersByName = new HashMap<String, LoneWolfMember>();
        if ( members != null && !members.isEmpty() ) {
            for ( LoneWolfMember member : members ) {
                membersByName.put( getKeyForMembersDataMap( member.getFirstName(), member.getLastName() ), member );
            }
        }
        return membersByName;
    }


    private String getKeyForMembersDataMap( String firstName, String lastName )
    {
        String key = "";
        if ( !StringUtils.isEmpty( firstName ) ) {
            key = key + firstName.trim();
        }
        if ( !StringUtils.isEmpty( lastName ) ) {
            key = key + " " + lastName.trim();
        }
        return key;
    }


    private SurveyPreInitiation setCollectionDetails( SurveyPreInitiation surveyPreInitiation, String collectionName,
        long organizationUnitId )
    {
        LOG.debug( "Inside method setCollectionDetails " );
        if ( collectionName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION ) ) {
            surveyPreInitiation.setCompanyId( organizationUnitId );
            surveyPreInitiation.setAgentId( 0 );
        } else if ( collectionName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION ) ) {
            Company company = organizationManagementService.getPrimaryCompanyByRegion( organizationUnitId );
            if ( company != null ) {
                surveyPreInitiation.setCompanyId( company.getCompanyId() );
            }
            surveyPreInitiation.setRegionCollectionId( organizationUnitId );
            surveyPreInitiation.setAgentId( 0 );
        } else if ( collectionName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION ) ) {
            Region region = organizationManagementService.getPrimaryRegionByBranch( organizationUnitId );
            if ( region != null ) {
                Company company = organizationManagementService.getPrimaryCompanyByRegion( region.getRegionId() );
                if ( company != null ) {
                    surveyPreInitiation.setCompanyId( company.getCompanyId() );
                }
                surveyPreInitiation.setRegionCollectionId( region.getRegionId() );
            }
            surveyPreInitiation.setBranchCollectionId( organizationUnitId );
            surveyPreInitiation.setAgentId( 0 );
        } else if ( collectionName.equals( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION ) ) {
            User user = null;
            try {
                user = userManagementService.getUserObjByUserId( organizationUnitId );
            } catch ( InvalidInputException e ) {
                LOG.error( "Exception caught ", e );
            }
            if ( user != null ) {
                Company company = user.getCompany();
                if ( company != null ) {
                    surveyPreInitiation.setCompanyId( company.getCompanyId() );
                }
                surveyPreInitiation.setAgentId( organizationUnitId );
            }

        }
        surveyPreInitiation.setCollectionName( collectionName );
        return surveyPreInitiation;
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        loneWolfIntegrationApiBuilder = (LoneWolfIntergrationApiBuilder) jobMap.get( "loneWolfIntegrationApiBuilder" );
        loneWolfIntegrationApi = loneWolfIntegrationApiBuilder.getLoneWolfIntegrationApi();
        loneWolfRestUtils = (LoneWolfRestUtils) jobMap.get( "loneWolfRestUtils" );
        batchTrackerService = (BatchTrackerService) jobMap.get( "batchTrackerService" );
        crmBatchTrackerService = (CRMBatchTrackerService) jobMap.get( "crmBatchTrackerService" );
        crmBatchTrackerHistoryService = (CRMBatchTrackerHistoryService) jobMap.get( "crmBatchTrackerHistoryService" );
        emailServices = (EmailServices) jobMap.get( "emailServices" );
        organizationManagementService = (OrganizationManagementService) jobMap.get( "organizationManagementService" );
        userManagementService = (UserManagementService) jobMap.get( "userManagementService" );
        surveyHandler = (SurveyHandler) jobMap.get( "surveyHandler" );
        utils = (Utils) jobMap.get( "utils" );
        maskEmail = (String) jobMap.get( "maskEmail" );
        applicationAdminEmail = (String) jobMap.get( "applicationAdminEmail" );
        applicationAdminName = (String) jobMap.get( "applicationAdminName" );
    }
}
