package com.realtech.socialsurvey.core.starter;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.realtech.socialsurvey.core.entities.LoopDetails;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.CollectionDotloopProfileMapping;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.CrmBatchTracker;
import com.realtech.socialsurvey.core.entities.DotLoopCrmInfo;
import com.realtech.socialsurvey.core.entities.DotLoopParticipant;
import com.realtech.socialsurvey.core.entities.DotLoopProfileEntity;
import com.realtech.socialsurvey.core.entities.LoopProfileMapping;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.integration.dotloop.DotloopIntegrationApi;
import com.realtech.socialsurvey.core.integration.dotloop.DotloopIntergrationApiBuilder;
import com.realtech.socialsurvey.core.integration.pos.errorhandlers.DotLoopAccessForbiddenException;
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.crmbatchtracker.CRMBatchTrackerService;
import com.realtech.socialsurvey.core.services.crmbatchtrackerhistory.CRMBatchTrackerHistoryService;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;

import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


/**
 * Ingester for dot loop
 */
@Component ( "dotloopreviewprocessor")
public class DotloopReviewProcessor extends QuartzJobBean
{


    private static final Logger LOG = LoggerFactory.getLogger( DotloopReviewProcessor.class );

    private DotloopIntergrationApiBuilder dotloopIntegrationApiBuilder;

    private SurveyHandler surveyHandler;

    private OrganizationManagementService organizationManagementService;

    private DotloopIntegrationApi dotloopIntegrationApi;

    private UserManagementService userManagementService;

    private Utils utils;

    private String maskEmail;

    private String applicationAdminEmail;

    private String applicationAdminName;

    private BatchTrackerService batchTrackerService;

    private CRMBatchTrackerService crmBatchTrackerService;

    private CRMBatchTrackerHistoryService crmBatchTrackerHistoryService;

    private EmailServices emailServices;

    private static final String BUYING_AGENT_ROLE = "Buying Agent";
    private static final String SELLING_AGENT_ROLE = "Selling Agent";
    private static final String LISTING_AGENT_ROLE = "Listing Agent";

    private static final String SELLER_ROLE = "Seller";
    private static final String BUYER_ROLE = "Buyer";

    private static final String SOLD_STATUS = "Sold";

    private boolean newLoopFound = false;
    private boolean newRecordFound = false;
    private int newRecordFoundCount = 0;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext )
    {
        try {
            LOG.info( "Executing dotloop review processor" );
            initializeDependencies( jobExecutionContext.getMergedJobDataMap() );

            // update last start time
            batchTrackerService.getLastRunEndTimeAndUpdateLastStartTimeByBatchType(
                CommonConstants.BATCH_TYPE_DOT_LOOP_REVIEW_PROCESSOR, CommonConstants.BATCH_NAME_DOT_LOOP_REVIEW_PROCESSOR );

            startDotloopFeedProcessing( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
            startDotloopFeedProcessing( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
            startDotloopFeedProcessing( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );
            startDotloopFeedProcessing( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
            //updating last run time for batch in database
            batchTrackerService.updateLastRunEndTimeByBatchType( CommonConstants.BATCH_TYPE_DOT_LOOP_REVIEW_PROCESSOR );
        } catch ( Exception e ) {
            LOG.error( "Error in dotloop review processor", e );
            try {
                //update batch tracker with error message
                batchTrackerService.updateErrorForBatchTrackerByBatchType( CommonConstants.BATCH_TYPE_DOT_LOOP_REVIEW_PROCESSOR,
                    e.getMessage() );
                //send report bug mail to admin
                batchTrackerService.sendMailToAdminRegardingBatchError( CommonConstants.BATCH_NAME_DOT_LOOP_REVIEW_PROCESSOR,
                    System.currentTimeMillis(), e );
            } catch ( NoRecordsFetchedException | InvalidInputException e1 ) {
                LOG.error( "Error while updating error message in dotloop review processor " );
            } catch ( UndeliveredEmailException e1 ) {
                LOG.error( "Error while sending report excption mail to admin " );
            }
        }
    }


    public void startDotloopFeedProcessing( String collectionName )
    {
        LOG.debug( "Inside method startDotloopFeedProcessing " );

        try {
            String entityType = null; // to maintain entry in crm batch tracker
            CrmBatchTracker crmBatchTracker = null;
            //get entity type and id
            if ( collectionName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION ) ) {
                entityType = CommonConstants.COMPANY_ID_COLUMN;
            } else if ( collectionName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION ) ) {
                entityType = CommonConstants.REGION_ID_COLUMN;
            } else if ( collectionName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION ) ) {
                entityType = CommonConstants.BRANCH_ID_COLUMN;
            } else if ( collectionName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION ) ) {
                entityType = CommonConstants.AGENT_ID_COLUMN;
            }
            long entityId;
            List<OrganizationUnitSettings> organizationUnitSettingsList = organizationManagementService
                .getOrganizationUnitSettingsForCRMSource( CommonConstants.CRM_SOURCE_DOTLOOP, collectionName );
            if ( organizationUnitSettingsList != null && !organizationUnitSettingsList.isEmpty() ) {
                LOG.info( "Looping through crm list of size: " + organizationUnitSettingsList.size() );
                for ( OrganizationUnitSettings organizationUnitSettings : organizationUnitSettingsList ) {
                    LOG.info( "Getting dotloop records for company id: " + organizationUnitSettings.getId() );
                    DotLoopCrmInfo dotLoopCrmInfo = (DotLoopCrmInfo) organizationUnitSettings.getCrm_info();
                    if ( dotLoopCrmInfo.getApi() != null && !dotLoopCrmInfo.getApi().isEmpty() ) {
                        entityId = organizationUnitSettings.getIden();
                        //make an entry in crm batch tracker and update last run start time
                        crmBatchTrackerService.getRecentRecordFetchedAndUpdateLastStartTimeByEntityTypeAndSourceType( entityType,
                            entityId, CommonConstants.CRM_SOURCE_DOTLOOP );
                        LOG.debug( "API key is " + dotLoopCrmInfo.getApi() );
                        try {
                            fetchReviewfromDotloop( dotLoopCrmInfo, collectionName, organizationUnitSettings );
                            if ( !dotLoopCrmInfo.isRecordsBeenFetched() ) {
                                LOG.debug( "This was the first fetch hence updating recordsFetched to true " );
                                dotLoopCrmInfo.setRecordsBeenFetched( true );
                                updateDotLoopCrmInfo( collectionName, organizationUnitSettings, dotLoopCrmInfo );
                            }
                            //insert crmbatchTrackerHistory with count of Records Fetched
                            crmBatchTracker = crmBatchTrackerService.getCrmBatchTracker( entityType, entityId,
                                CommonConstants.CRM_SOURCE_DOTLOOP );
                            if ( crmBatchTracker != null )
                                crmBatchTrackerHistoryService.insertCrmBatchTrackerHistory( newRecordFoundCount,
                                    crmBatchTracker.getCrmBatchTrackerId(), CommonConstants.CRM_SOURCE_DOTLOOP );
                            // update  last run end time and count of new records found in crm batch tracker
                            crmBatchTrackerService.updateLastRunEndTimeByEntityTypeAndSourceType( entityType, entityId,
                                CommonConstants.CRM_SOURCE_DOTLOOP, newRecordFoundCount );

                        } catch ( Exception e ) {
                            LOG.error( "Exception caught for collection " + collectionName + "having iden as "
                                + organizationUnitSettings.getIden(), e );
                            // update  error message in crm batch tracker
                            crmBatchTrackerService.updateErrorForBatchTrackerByEntityTypeAndSourceType( entityType, entityId,
                                CommonConstants.CRM_SOURCE_DOTLOOP, e.getMessage() );
                            try {
                                LOG.info( "Building error message for the auto post failure" );
                                String errorMsg = "Error while processing dotloop feed for collection " + collectionName
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


    // Gets list of profiles for a given api key
    private List<DotLoopProfileEntity> getDotLoopProfiles( String authorizationHeader, String apiKey )
    {
        LOG.debug( "Getting dotloop profile list for api key: " + apiKey );
        List<DotLoopProfileEntity> profiles = null;
        if ( dotloopIntegrationApi != null ) {
            Response dotloopResponse = dotloopIntegrationApi.fetchdotloopProfiles( authorizationHeader );
            String responseString = null;
            if ( dotloopResponse != null ) {
                responseString = new String( ( (TypedByteArray) dotloopResponse.getBody() ).getBytes() );
            }
            if ( responseString != null ) {
                profiles = new Gson().fromJson( responseString, new TypeToken<List<DotLoopProfileEntity>>() {}.getType() );
            }
        }
        LOG.debug( "Returning dotloop profile list." );
        return profiles;

    }


    // check if the profile is entered in the system already as inactive
    private boolean isProfilePresentAsInactive( String collectionName, OrganizationUnitSettings unitSettings,
        DotLoopProfileEntity dotLoopProfile ) throws InvalidInputException
    {
        LOG.debug( "Checking dotLoopProfile presence in the system as inactive: " + dotLoopProfile.toString() );
        boolean isAccountPresentInSystem = false;
        String profileId = String.valueOf( dotLoopProfile.getProfileId() );
        CollectionDotloopProfileMapping collectionDotloopProfileMapping = organizationManagementService
            .getCollectionDotloopMappingByCollectionIdAndProfileId( collectionName, unitSettings.getIden(), profileId );
        if ( collectionDotloopProfileMapping != null ) {
            LOG.debug( "Profile is already present in the system as inactive." );
            isAccountPresentInSystem = true;
        }
        return isAccountPresentInSystem;
    }


    private void insertCompanyDotloopProfile( String collectionName, DotLoopProfileEntity profileEntity,
        OrganizationUnitSettings unitSettings ) throws InvalidInputException
    {
        LOG.debug( "Inserting into dotloop profile entity" );
        CollectionDotloopProfileMapping collectionDotloopProfileMapping = new CollectionDotloopProfileMapping();
        collectionDotloopProfileMapping.setActive( false );
        collectionDotloopProfileMapping.setProfileEmailAddress( profileEntity.getEmailAddress() );
        collectionDotloopProfileMapping.setProfileName( profileEntity.getName() );
        collectionDotloopProfileMapping.setProfileId( String.valueOf( profileEntity.getProfileId() ) );
        if ( collectionName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION ) ) {
            collectionDotloopProfileMapping.setCompanyId( unitSettings.getIden() );
        } else if ( collectionName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION ) ) {
            collectionDotloopProfileMapping.setRegionId( unitSettings.getIden() );
        } else if ( collectionName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION ) ) {
            collectionDotloopProfileMapping.setBranchId( unitSettings.getIden() );
        } else if ( collectionName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION ) ) {
            collectionDotloopProfileMapping.setAgentId( unitSettings.getIden() );
        }
        collectionDotloopProfileMapping.setCreatedOn( new Timestamp( System.currentTimeMillis() ) );
        collectionDotloopProfileMapping.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        collectionDotloopProfileMapping.setCreatedBy( CommonConstants.ADMIN_USER_NAME );
        collectionDotloopProfileMapping.setModifiedBy( CommonConstants.ADMIN_USER_NAME );
        collectionDotloopProfileMapping = organizationManagementService
            .saveCollectionDotLoopProfileMapping( collectionDotloopProfileMapping );
    }


    private void processLoopEntites( String collectionName, List<LoopProfileMapping> loops, String profileId,
        boolean byPassRecords, String authorizationHeader, long organizationUnitId )
    {
        // if by pass is true, then add all the records in tracker without processing. Otherwise get
        // new records and then add the new record.
        LOG.debug( "Processing loops for profile id: " + profileId + " and byPassRecords flag: " + byPassRecords );
        for ( LoopProfileMapping loop : loops ) {
            try {
                // Setting profile id for the loop
                loop.setProfileId( profileId );
                // if status is not sold, stop the process and send a mail to the admin
                if ( !loop.getLoopStatus().equalsIgnoreCase( SOLD_STATUS ) ) {
                    LOG.warn( "Found a loop status which is not sold." );
                    throw new FatalException( "Found a loop status which is not sold. Details: Loop view id: "
                        + loop.getLoopViewId() + " for profile id: " + profileId + ". Collection: " + collectionName + " ID: "
                        + organizationUnitId );

                }
                // check if the record is present in the database then skip the loop. if not, then
                // process it
                LoopProfileMapping loopFromSystem = null;
                try {
                    loopFromSystem = organizationManagementService.getLoopByProfileAndLoopId( profileId, loop.getLoopId(),
                        collectionName, organizationUnitId );
                } catch ( InvalidInputException e ) {
                    LOG.error( "Could not get loop details from database for loop id " + loop.getLoopId() + " for profile "
                        + profileId, e );
                    continue;
                }
                if ( loopFromSystem == null ) {
                    LOG.info( "Loop " + loop.getLoopId() + " for profile " + profileId + " is not present. Hence processing." );
                    if ( !byPassRecords ) {
                        processLoop( collectionName, loop, authorizationHeader, organizationUnitId );
                    }
                    //update the flag
                    newLoopFound = true;
                    LOG.debug( "Insert into tracker." );
                    try {
                        loop = setHierarchyInformationInLoop( loop, collectionName, organizationUnitId );
                        loop.setCreatedOn( new Timestamp( System.currentTimeMillis() ) );
                        organizationManagementService.saveLoopsForProfile( loop );
                    } catch ( InvalidInputException e ) {
                        LOG.warn( "Could not insert loop " + loop.getLoopId() + " for profile " + loop.getProfileId() );
                    }
                } else {
                    // record is present. process next record
                    LOG.info( "Loop " + loop.getLoopId() + " for profile " + profileId + " is present. Hence skipping." );
                    continue;
                }
            } catch ( FatalException e ) {
                try {
                    emailServices.sendFatalExceptionEmail( applicationAdminEmail, e.getMessage() );
                } catch ( InvalidInputException | UndeliveredEmailException e1 ) {
                    LOG.error( "Error while sending mail to admin ", e );
                }
            }
        }
    }


    private LoopProfileMapping setHierarchyInformationInLoop( LoopProfileMapping loop, String collectionName,
        long organizationUnitId )
    {
        LOG.debug( "Inside method  setHierarchyInformationInLoop for loop " + loop.getProfileId() );
        if ( collectionName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION ) ) {
            loop.setCompanyId( organizationUnitId );
        } else if ( collectionName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION ) ) {
            loop.setRegionId( organizationUnitId );
        } else if ( collectionName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION ) ) {
            loop.setBranchId( organizationUnitId );
        } else if ( collectionName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION ) ) {
            loop.setAgentId( organizationUnitId );
        }
        loop.setCollectionName( collectionName );
        return loop;
    }


    // processes the details of the loop
    private void processLoop( String collectionName, LoopProfileMapping loop, String authorizationHeader,
        long organizationUnitId )
    {
        LOG.debug( "Processing details of loop view id: " + loop.getLoopViewId() + " for profile id: " + loop.getProfileId() );
        Response response = null;
        String responseString = null;
        List<DotLoopParticipant> participants = null;
	    Map<String, String> cityStateMap = null;
	    String city = null;
	    String state = null;
        try {
	        cityStateMap = getCityAndStateForLoop( loop, authorizationHeader );
	        if ( cityStateMap != null && !cityStateMap.isEmpty() ) {
		        if ( cityStateMap.containsKey( CommonConstants.CITY ) )
			        city = cityStateMap.get( CommonConstants.CITY );
		        if ( cityStateMap.containsKey( CommonConstants.STATE ) )
			        state = cityStateMap.get( CommonConstants.STATE );
	        }
            response = dotloopIntegrationApi.fetchLoopViewParticipants( authorizationHeader, loop.getProfileId(),
                loop.getLoopViewId() );
            if ( response != null ) {
                responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
                participants = new Gson().fromJson( responseString, new TypeToken<List<DotLoopParticipant>>() {}.getType() );
                //JIRA SS-493 begin
                List<String> buyingAgentEmailIdList = null;
                List<String> sellingAgentEmailIdList = null;
                Map<String, String> buyersMap = null;
                Map<String, String> sellersMap = null;

                for ( DotLoopParticipant participant : participants ) {
                    String agentEmailId = null;
                    //If buying agent
                    if ( participant.getRole() != null && participant.getRole().equalsIgnoreCase( BUYING_AGENT_ROLE )
                        && participant.getMemberOfMyTeam() != null
                        && participant.getMemberOfMyTeam().equalsIgnoreCase( CommonConstants.YES_STRING ) ) {
                        agentEmailId = participant.getEmail();
                        if ( agentEmailId != null && !agentEmailId.isEmpty() ) {
                            if ( maskEmail.equals( CommonConstants.YES_STRING ) ) {
                                agentEmailId = utils.maskEmailAddress( agentEmailId );
                            }
                            if ( buyingAgentEmailIdList == null )
                                buyingAgentEmailIdList = new ArrayList<>();
                            buyingAgentEmailIdList.add( agentEmailId );
                        }
                    }

                    //If selling agent
                    if ( participant.getRole() != null
                        && ( participant.getRole().equalsIgnoreCase( SELLING_AGENT_ROLE )
                            || participant.getRole().equalsIgnoreCase( LISTING_AGENT_ROLE ) )
                        && participant.getMemberOfMyTeam() != null
                        && participant.getMemberOfMyTeam().equalsIgnoreCase( CommonConstants.YES_STRING ) ) {
                        agentEmailId = participant.getEmail();
                        if ( agentEmailId != null && !agentEmailId.isEmpty() ) {
                            if ( maskEmail.equals( CommonConstants.YES_STRING ) ) {
                                agentEmailId = utils.maskEmailAddress( agentEmailId );
                            }
                            if ( sellingAgentEmailIdList == null )
                                sellingAgentEmailIdList = new ArrayList<>();
                            sellingAgentEmailIdList.add( agentEmailId );
                        }
                    }

                    //If buyer
                    if ( participant.getRole() != null && participant.getRole().equalsIgnoreCase( BUYER_ROLE )
                        && participant.getMemberOfMyTeam() != null
                        && participant.getMemberOfMyTeam().equalsIgnoreCase( CommonConstants.NO_STRING ) ) {
                        if ( participant.getEmail() != null && !participant.getEmail().isEmpty() ) {
                            if ( buyersMap == null ) {
                                buyersMap = new HashMap<>();
                            }
                            String customerEmailId = participant.getEmail().trim();
                            if ( maskEmail.equals( CommonConstants.YES_STRING ) ) {
                                customerEmailId = utils.maskEmailAddress( customerEmailId );
                            }
                            buyersMap.put( customerEmailId, participant.getName() );
                        }
                    }

                    //If seller
                    if ( participant.getRole() != null && participant.getRole().equalsIgnoreCase( SELLER_ROLE )
                        && participant.getMemberOfMyTeam() != null
                        && participant.getMemberOfMyTeam().equalsIgnoreCase( CommonConstants.NO_STRING ) ) {
                        if ( participant.getEmail() != null && !participant.getEmail().isEmpty() ) {
                            if ( sellersMap == null ) {
                                sellersMap = new HashMap<>();
                            }
                            String customerEmailId = participant.getEmail().trim();
                            if ( maskEmail.equals( CommonConstants.YES_STRING ) ) {
                                customerEmailId = utils.maskEmailAddress( customerEmailId );
                            }
                            sellersMap.put( customerEmailId, participant.getName() );
                        }
                    }
                }

                //Send surveys from buying agent to buyers
                if ( buyersMap != null && buyersMap.size() > 0 && buyingAgentEmailIdList != null
                    && !buyingAgentEmailIdList.isEmpty() ) {
                    for ( String buyingAgentEmailId : buyingAgentEmailIdList ) {
                        for ( String buyerEmailId : buyersMap.keySet() ) {
                            SurveyPreInitiation surveyPreInitiation = new SurveyPreInitiation();
                            surveyPreInitiation = setCollectionDetails( surveyPreInitiation, collectionName,
                                organizationUnitId );
                            surveyPreInitiation.setCreatedOn( new Timestamp( System.currentTimeMillis() ) );
                            surveyPreInitiation.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
                            surveyPreInitiation.setCustomerEmailId( buyerEmailId );
                            surveyPreInitiation.setCustomerFirstName( buyersMap.get( buyerEmailId ) );
                            surveyPreInitiation.setCustomerLastName( null );
                            surveyPreInitiation.setLastReminderTime( utils.convertEpochDateToTimestamp() );
                            surveyPreInitiation.setAgentEmailId( buyingAgentEmailId );
                            
                            surveyPreInitiation.setStatus( CommonConstants.STATUS_SURVEYPREINITIATION_NOT_PROCESSED );
                            surveyPreInitiation.setSurveySource( CommonConstants.CRM_SOURCE_DOTLOOP );
	                        surveyPreInitiation.setCity( city );
	                        surveyPreInitiation.setState( state );
                            // adding the loop view id in the source id for back tracking
                            surveyPreInitiation.setSurveySourceId( String.valueOf( loop.getLoopViewId() ) );
                            try {
                                surveyPreInitiation.setEngagementClosedTime( getFormattedTime(loop.getLastUpdated()) );
                                surveyHandler.saveSurveyPreInitiationObject( surveyPreInitiation );
                                //update the flag
                                newRecordFound = true;
                                newRecordFoundCount++;
                            } catch ( InvalidInputException | ParseException e ) {
                                LOG.error( "Unable to insert this record ", e );
                            }
                        }
                    }
                } else {
                    LOG.warn( "Incomplete details from participants call for loop view id" );
                }


                //Send surveys from selling agents to sellers
                if ( sellersMap != null && sellersMap.size() > 0 && sellingAgentEmailIdList != null
                    && !sellingAgentEmailIdList.isEmpty() ) {
                    for ( String sellingAgentEmailId : sellingAgentEmailIdList ) {
                        for ( String sellerEmailId : sellersMap.keySet() ) {
                            SurveyPreInitiation surveyPreInitiation = new SurveyPreInitiation();
                            surveyPreInitiation = setCollectionDetails( surveyPreInitiation, collectionName,
                                organizationUnitId );
                            surveyPreInitiation.setCreatedOn( new Timestamp( System.currentTimeMillis() ) );
                            surveyPreInitiation.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
                            surveyPreInitiation.setCustomerEmailId( sellerEmailId );
                            surveyPreInitiation.setCustomerFirstName( sellersMap.get( sellerEmailId ) );
                            surveyPreInitiation.setCustomerLastName( null );
                            surveyPreInitiation.setLastReminderTime( utils.convertEpochDateToTimestamp() );
                            surveyPreInitiation.setAgentEmailId( sellingAgentEmailId );
                            surveyPreInitiation.setStatus( CommonConstants.STATUS_SURVEYPREINITIATION_NOT_PROCESSED );
                            surveyPreInitiation.setSurveySource( CommonConstants.CRM_SOURCE_DOTLOOP );
	                        surveyPreInitiation.setCity( city );
	                        surveyPreInitiation.setState( state );
                            // adding the loop view id in the source id for back tracking
                            surveyPreInitiation.setSurveySourceId( String.valueOf( loop.getLoopViewId() ) );
                            try {
                                surveyPreInitiation.setEngagementClosedTime( getFormattedTime(loop.getLastUpdated()) );
                                surveyHandler.saveSurveyPreInitiationObject( surveyPreInitiation );
                                //update the flag
                                newRecordFound = true;
                                newRecordFoundCount++;
                            } catch ( InvalidInputException | ParseException e ) {
                                LOG.error( "Unable to insert this record ", e );
                            }
                        }
                    }
                } else {
                    LOG.warn( "Incomplete details from participants call for loop view id" );
                }
                //JIRA SS-493 end
            } else {
                LOG.info( "No response fetched for loop details " + loop.getLoopViewId() + " for profile id: "
                    + loop.getProfileId() );
            }

        } catch ( DotLoopAccessForbiddenException e ) {
            LOG.error( "Could not fetch loop details for " + loop.getLoopViewId() + " for profile id: " + loop.getProfileId() );
        }
    }


	private Timestamp getFormattedTime( String dateVal ) throws ParseException
    {
	    String timeZoneDesc =  dateVal.substring( dateVal.lastIndexOf( '-' ) );
        dateVal = dateVal.replace( 'T', ' ' );
        dateVal = dateVal.substring( 0, dateVal.lastIndexOf( '-' ) );
        
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        Calendar cal = Calendar.getInstance();
        cal.setTime( sdf.parse( dateVal ) );
        cal.setTimeZone( TimeZone.getTimeZone( "GMT"+ timeZoneDesc ) );
        
        Timestamp ts = new Timestamp( cal.getTimeInMillis() );
        return ts;
    }


    /**
	 * Method to get city and state values for loop
	 * @param loop
	 * @param authorizationHeader
	 * @return
	 */
	private Map<String, String> getCityAndStateForLoop( LoopProfileMapping loop, String authorizationHeader )
	{
		LOG.info( "Method getCityAndStateForLoop() started" );
		Map<String, String> cityStateMap = null;
		String state = null;
		String city = null;
		String responseString = null;
		LoopDetails details = null;
		try {
			Response response = dotloopIntegrationApi
				.fetchLoopProfileDetail( authorizationHeader, loop.getProfileId(), loop.getLoopViewId() );
			if ( response != null ) {
				responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
				details = new Gson().fromJson( responseString, new TypeToken<LoopDetails>()
				{
				}.getType() );
				if ( details != null ) {
					Map<String, Map<String, String>> sections = details.getSections();
					if ( sections != null && !sections.isEmpty() && sections
						.containsKey( CommonConstants.KEY_DOTLOOP_PROPERTY_ADDRESS ) ) {
						Map<String, String> propertyAddress = sections.get( CommonConstants.KEY_DOTLOOP_PROPERTY_ADDRESS );
						if ( propertyAddress != null && !propertyAddress.isEmpty() ) {
							if ( propertyAddress.containsKey( CommonConstants.KEY_DOTLOOP_STATE ) )
								state = propertyAddress.get( CommonConstants.KEY_DOTLOOP_STATE );
							if ( propertyAddress.containsKey( CommonConstants.CITY ) )
								city = propertyAddress.get( CommonConstants.CITY );
						}
					}
				}
			}
		} catch ( DotLoopAccessForbiddenException e ) {
			LOG.error( "Could not fetch loop details for " + loop.getLoopViewId() + " for profile id: " + loop.getProfileId() );
		}
		if ( city != null || state != null ) {
			cityStateMap = new HashMap<>();
			if ( city != null )
				cityStateMap.put( CommonConstants.CITY, city );
			if ( state != null )
				cityStateMap.put( CommonConstants.STATE, state );
		}
		LOG.info( "Method getCityAndStateForLoop() finished" );
		return cityStateMap;
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


    private void updateDotLoopCrmInfo( String collectionName, OrganizationUnitSettings unitSettings,
        DotLoopCrmInfo dotLoopCrmInfo )
    {
        LOG.debug( "Updating dotloop crm for  collection " + collectionName + "having id as " + unitSettings.getIden() );
        try {
            organizationManagementService.updateCRMDetailsForAnyUnitSettings( unitSettings, collectionName, dotLoopCrmInfo,
                "com.realtech.socialsurvey.core.entities.DotLoopCrmInfo" );
        } catch ( InvalidInputException e ) {
            LOG.error( "Unable to update dotloop ", e );
        }
    }


    /**
     * Fetches records from dot loop
     *
     * @param dotloopCrmInfo
     * @param unitSettings
     */
    public void fetchReviewfromDotloop( DotLoopCrmInfo dotloopCrmInfo, String collectionName,
        OrganizationUnitSettings unitSettings )
    {
        String apiKeysStr = dotloopCrmInfo.getApi();
        List<String> apiKeyList = Arrays.asList( apiKeysStr.split( CommonConstants.COMMA_SEPERATOR_PATTERN ) );
        for ( String apiKey : apiKeyList ) {
            apiKey = apiKey.trim();
            LOG.debug( "Fetching reviews for api key: " + apiKey + " with id: " + unitSettings.getIden() );
            String authorizationHeader = CommonConstants.AUTHORIZATION_HEADER + apiKey;
            //re initialize the flag;
            newLoopFound = false;
            newRecordFound = false;
            newRecordFoundCount = 0;
            // get list of profiles
            List<DotLoopProfileEntity> profileList = getDotLoopProfiles( authorizationHeader, apiKey );
            if ( profileList != null && !profileList.isEmpty() ) {
                LOG.debug( "Got " + profileList.size() + " profiles." );
                for ( DotLoopProfileEntity profile : profileList ) {

                    String profileId = String.valueOf( profile.getProfileId() );
                    try {
                        if ( profile.isActive() ) {
                            // check for loop ids with status closed (4)
                            Response loopResponse = null;
                            int batchNumber = 1;
                            String loopResponseString = null;
                            // List<LoopProfileMapping> dotloopProfileMappingList = null;
                            List<LoopProfileMapping> loopEntities = null;
                            boolean byPassRecords = false; // checks if the system has processed the
                            // profile ever before.
                            try {

                                if ( dotloopCrmInfo.isRecordsBeenFetched() && organizationManagementService
                                    .getLoopsCountByProfile( profileId, collectionName, unitSettings.getIden() ) > 0 ) {
                                    LOG.info( "Records for profile id: " + profileId + " is already present" );
                                    byPassRecords = false;
                                } else {
                                    LOG.info( "Proile id is not processed for profile id: " + profileId
                                        + ". Bypassing all records. Just adding into tracker" );
                                    byPassRecords = true;
                                }
                                do {
                                    LOG.debug( "Gettig batch " + batchNumber + " for closed records for profile " + profileId );
                                    loopResponse = dotloopIntegrationApi.fetchClosedProfiles( authorizationHeader, profileId,
                                        batchNumber );
                                    if ( loopResponse != null ) {
                                        loopResponseString = new String(
                                            ( (TypedByteArray) loopResponse.getBody() ).getBytes() );
                                        if ( loopResponseString == null || loopResponseString.equals( "[]" ) ) {
                                            // no more records
                                            LOG.debug( "No more loops ids for profile: " + profileId );
                                            break;
                                        } else {
                                            LOG.debug( "Processing batch: " + batchNumber + " for profile: " + profileId );
                                            loopEntities = new Gson().fromJson( loopResponseString,
                                                new TypeToken<List<LoopProfileMapping>>() {}.getType() );
                                            // process loop entites. If there are no records for the
                                            // profile id in the tracker
                                            processLoopEntites( collectionName, loopEntities, profileId, byPassRecords,
                                                authorizationHeader, unitSettings.getIden() );
                                        }
                                    } else {
                                        // no more records
                                        LOG.debug( "No more loops ids for profile: " + profileId );
                                        break;
                                    }
                                    batchNumber++;
                                } while ( true );
                            } catch ( DotLoopAccessForbiddenException dafe ) {
                                // insert into tracker table
                                LOG.info( "Inactive profile. Inserting into Dot loop profile mapping." );
                                insertCompanyDotloopProfile( collectionName, profile, unitSettings );
                            }
                        }
                    } catch ( JsonSyntaxException | InvalidInputException e ) {
                        LOG.error( "Could not process " + profileId, e );
                    }
                }
            }

            //send report to admin if no new record is fetched
            if ( !newLoopFound && !newRecordFound ) {
                String subject = "No record fetched from Dotloop for " + collectionName + " id : " + unitSettings.getIden();
                String body = "";
                if ( !newLoopFound )
                    body += "No new loop found for the entity <br/>";
                if ( !newRecordFound )
                    body += "No new record found for the entity <br/>";
                try {
                    emailServices.sendCustomMail( applicationAdminName, applicationAdminEmail, subject, body, null );
                } catch ( InvalidInputException e ) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch ( UndeliveredEmailException e ) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        dotloopIntegrationApiBuilder = (DotloopIntergrationApiBuilder) jobMap.get( "dotloopIntegrationApiBuilder" );
        dotloopIntegrationApi = dotloopIntegrationApiBuilder.getDotloopIntegrationApi();
        surveyHandler = (SurveyHandler) jobMap.get( "surveyHandler" );
        organizationManagementService = (OrganizationManagementService) jobMap.get( "organizationManagementService" );
        userManagementService = (UserManagementService) jobMap.get( "userManagementService" );
        utils = (Utils) jobMap.get( "utils" );
        maskEmail = (String) jobMap.get( "maskEmail" );
        applicationAdminEmail = (String) jobMap.get( "applicationAdminEmail" );
        applicationAdminName = (String) jobMap.get( "applicationAdminName" );
        emailServices = (EmailServices) jobMap.get( "emailServices" );
        batchTrackerService = (BatchTrackerService) jobMap.get( "batchTrackerService" );
        crmBatchTrackerService = (CRMBatchTrackerService) jobMap.get( "crmBatchTrackerService" );
        crmBatchTrackerHistoryService = (CRMBatchTrackerHistoryService) jobMap.get( "crmBatchTrackerHistoryService" );
    }

}