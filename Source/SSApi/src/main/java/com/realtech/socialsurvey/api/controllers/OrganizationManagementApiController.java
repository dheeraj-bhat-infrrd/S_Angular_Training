package com.realtech.socialsurvey.api.controllers;


import java.sql.Timestamp;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


import com.realtech.socialsurvey.api.models.request.NotificationRequest;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.*;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;

import com.realtech.socialsurvey.core.vo.*;
import com.realtech.socialsurvey.core.vo.NotesVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.api.exceptions.SSApiException;
import com.realtech.socialsurvey.api.models.request.FailedStormMessage;
import com.realtech.socialsurvey.api.utils.RestUtils;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.ftp.FtpSurveyResponse;
import com.realtech.socialsurvey.core.entities.ftp.FtpUploadRequest;
import com.realtech.socialsurvey.core.entities.integration.stream.FailedStreamMessage;
import com.realtech.socialsurvey.core.exception.AuthorizationException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.admin.AdminAuthenticationService;
import com.realtech.socialsurvey.core.services.ftpmanagement.FTPManagement;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.social.SocialManagementService;
import com.realtech.socialsurvey.core.services.stream.StreamMessagesService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;

import io.swagger.annotations.ApiOperation;



@RestController
@RequestMapping ( "/v1")
public class OrganizationManagementApiController
{

    private static final Logger LOGGER = LoggerFactory.getLogger( OrganizationManagementApiController.class );

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Autowired
    private StreamMessagesService streamMessagesService;
    
    @Autowired
    private FTPManagement ftpManagement;

    @Autowired
    private UserManagementService userManagementService;
    
    @Autowired
    private AdminAuthenticationService adminAuthenticationService;
    
    @Autowired
    private RestUtils restUtils;
    
    @Autowired
    private SurveyHandler surveyHandler;

    @Autowired
    private SocialManagementService socialManagementService;
    
    @RequestMapping ( value = "/updateabusivemail", method = RequestMethod.POST)
    @ApiOperation ( value = "Updating abusive mail settings")
    public String updateAbusiveMail( long entityId, String mailId ) throws InvalidInputException, NonFatalException
    {
        LOGGER.info( "Updating abusive mail settings for companyId : {}", entityId );
        organizationManagementService.updateAbusiveMailService( entityId, mailId );
        return mailId;

    }


    @RequestMapping ( value = "/unsetabusivemail", method = RequestMethod.POST)
    @ApiOperation ( value = "Unset abusive mail settings")
    public void unsetAbusiveMail( long entityId ) throws NonFatalException
    {
        LOGGER.info( "Unset abusive mail settings for companyId : {}", entityId );
        organizationManagementService.unsetAbusiveMailService( entityId );
    }


    @RequestMapping ( value = "/unsetcompres", method = RequestMethod.POST)
    @ApiOperation ( value = "Unset Complaint Resolution settings")
    public void unsetCompRes( long entityId ) throws NonFatalException
    {
        LOGGER.info( "Unset Complaint Resolution settings for companyId : {}", entityId );
        organizationManagementService.unsetComplaintResService( entityId );
    }
    
    @RequestMapping ( value = "/flag/agents/profile", method = RequestMethod.POST)
    @ApiOperation ( value = "Api to disable or enable agent edit in profile")
    public  ResponseEntity<?> flagAgentProfiles( @Valid @RequestBody AgentDisableApiEntity agentDisableApiEntity,  HttpServletRequest request ) throws  NonFatalException
    {
        LOGGER.info( "Api to disable or enable agents edit in profile started" );
        long companyId = 0;
        String authorizationHeader = request.getHeader( CommonConstants.SURVEY_API_REQUEST_PARAMETER_AUTHORIZATION );
        //authorize request
        try {
            companyId = adminAuthenticationService.validateAuthHeader( authorizationHeader );
            LOGGER.info( "Api to disable or enable agent edit in profile for agents, isAgentProfileDisabled : {}",agentDisableApiEntity.isAgentProfileDisabled() );

        } catch ( AuthorizationException e ) {
            return restUtils.getRestResponseEntity( HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", null, null, request, companyId );
        }
        organizationManagementService.updateAgentsProfileDisable( agentDisableApiEntity.getAgentIds(), agentDisableApiEntity.isAgentProfileDisabled() );
        return restUtils.getRestResponseEntity( HttpStatus.OK, "Api to disable or enable agent edit in profile Successfully processed", null, null, request,
            companyId );
    }
    
    @RequestMapping ( value = "/sethasregisteredforsummit", method = RequestMethod.POST)
    @ApiOperation ( value = "Set hasRegisteredForSummit")
    public ResponseEntity<?> setHasRegisteredForSummit(long companyId, boolean hasRegisteredForSummit) throws SSApiException
    {
        try {
            organizationManagementService.setHasRegisteredForSummit(companyId, hasRegisteredForSummit);
            LOGGER.info( "setHasregisteredforsummit completed successfully" );
            return new ResponseEntity<>( "Successfully updated", HttpStatus.OK );
                
        } catch ( NonFatalException e ) {
            throw new SSApiException( e.getMessage(), e.getErrorCode() );
        }
    }
    
    @RequestMapping ( value = "/setshowsummitpopup", method = RequestMethod.POST)
    @ApiOperation ( value = "Set IsShowSummitPopup")
    public ResponseEntity<?> setShowSummitPopup(long entityId, String entityType, boolean isShowSummitPopup) throws SSApiException
    {
        try {
            organizationManagementService.setShowSummitPopup(entityId, entityType, isShowSummitPopup);
            LOGGER.info( "setShowSummitPopup completed successfully" );
            return new ResponseEntity<>( "Successfully updated", HttpStatus.OK );
                
        } catch ( NonFatalException e ) {
            throw new SSApiException( e.getMessage(), e.getErrorCode() );
        }
    }
    
    
    @RequestMapping ( value = "/encompass/{companyId}/version/update", method = RequestMethod.POST)
    @ApiOperation ( value = "update the version of encompass for a company")
    public ResponseEntity<?> updateEncompassVersion( @PathVariable long companyId, String version ) throws NonFatalException
    {
        LOGGER.info( "Updating the encompass version for companyId : {}", companyId );
        try {
            organizationManagementService.updateEncompassVersion( companyId, version );
            return new ResponseEntity<>( HttpStatus.OK );
        } catch ( NonFatalException error ) {
            throw new SSApiException( "Unable to update encompass version, " + error.getMessage(), error );
        }
    }
    
    @RequestMapping ( value = "/unsetwebadd", method = RequestMethod.POST)
    @ApiOperation ( value = "Unset web address in profile")
    public void unsetWebAdd( long entityId ,String entityType) throws  NonFatalException
    {
        LOGGER.info( "Unset web address in profile for entityId : {} , entityType : {}",entityId,entityType );
        organizationManagementService.unsetWebAddressInProfile( entityId, entityType );
    }


    /**
     * 
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/userprofileflags", method = RequestMethod.GET)
    public ResponseEntity<?> getProfileFlags( long userId )
    {
        LOGGER.info( "Method getProfileFlags() started." );
        Map<String, String> response = new HashMap<>();

        response.put( "success", "false" );
        if ( userId > 0 ) {
            try {
                List<UserProfile> profiles = userManagementService.getUserProfiles( userId );
                if ( profiles != null ) {
                    response.put( "success", "true" );
                    response.put( "isBranchAdmin", "false" );
                    response.put( "isRegionAdmin", "false" );
                    response.put( "isAgent", "false" );
                    for ( UserProfile profile : profiles ) {
                        if ( profile.getStatus() == CommonConstants.STATUS_ACTIVE ) {
                            if ( profile.getProfilesMaster()
                                .getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) {
                                response.put( "isAgent", "true" );
                            } else if ( profile.getProfilesMaster()
                                .getProfileId() == CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID ) {
                                response.put( "isBranchAdmin", "true" );
                            } else if ( profile.getProfilesMaster()
                                .getProfileId() == CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID ) {
                                response.put( "isRegionAdmin", "true" );
                            }
                        }
                    }
                } else {
                    response.put( "reason", "No user profiles found" );
                }

            } catch ( InvalidInputException error ) {
                LOGGER.warn( "Unable to parse user ID, ", error );
                response.put( "reason", error.getMessage() );
            }
        } else {
            response.put( "reason", "Invalid user ID" );
        }

        LOGGER.info( "Method getProfileFlags() finished." );
        return new ResponseEntity<>( response, HttpStatus.OK );
    }
    
    @RequestMapping ( value = "/flag/agentprofiles", method = RequestMethod.POST)
    @ApiOperation ( value = "Api to disable or enable agent edit in profile")
    public  ResponseEntity<?> flagAgentProfile( long companyId , boolean isAgentProfileDisabled ,  HttpServletRequest request ) throws  NonFatalException
    {
        LOGGER.info( "Api to disable or enable agent edit in profile started for companyId:{}",companyId );
        String authorizationHeader = request.getHeader( CommonConstants.SURVEY_API_REQUEST_PARAMETER_AUTHORIZATION );
        //authorize request
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
            LOGGER.info( "Api to disable or enable agent edit in profile for companyId : {} , isAgentProfileDisabled : {}",companyId,isAgentProfileDisabled );

        } catch ( AuthorizationException e ) {
            return restUtils.getRestResponseEntity( HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", null, null, request, companyId );
        }
        organizationManagementService.updateAgentProfileDisable( companyId, isAgentProfileDisabled );
        return restUtils.getRestResponseEntity( HttpStatus.OK, "Api to disable or enable agent edit in profile Successfully processed", null, null, request,
            companyId );
    }
    
    
    @RequestMapping ( value = "/setftpcrm/{companyId}", method = RequestMethod.POST)
    @ApiOperation ( value = "Set ftp crm info")
    public void setFtpCrm( @PathVariable ( "companyId") Long companyId, @RequestBody TransactionSourceFtp transactionSourceFtp )
        throws InvalidInputException
    {
        LOGGER.info( "Set ftp crm info for companyId : {}", companyId );
        organizationManagementService.setFtpInfo( companyId, transactionSourceFtp );

    }


    @RequestMapping ( value = "/crm/ftp/info/company", method = RequestMethod.GET)
    @ApiOperation ( value = "get ftp crm info")
    public String getFtpCrmInfo( String status, int startIndex, int batchSize )
    {
        LOGGER.info( "Getting ftp crm info for company" );
        return new Gson().toJson( ftpManagement.getFtpConnections( status, startIndex, batchSize, true ) );
    }


    //    @RequestMapping ( value = "/getftpcrm/{companyId}/{ftpId}", method = RequestMethod.POST)
    //    @ApiOperation ( value = "Set ftp crm info")
    //    public TransactionSourceFtp setFtpCrm( @PathVariable ( "companyId") long companyId, @PathVariable ( "ftpId") long ftpId )
    //    {
    //        LOGGER.info( "Set ftp crm info for companyId:{}", companyId );
    //        return organizationUnitSettings.fetchFileHeaderMapper( companyId, ftpId );
    //    }


    @RequestMapping ( value = "/crm/ftp/stream/failed", method = RequestMethod.GET)
    @ApiOperation ( value = "get failed ftp stream request")
    public String getFailedFtpStreamMessages( int startIndex, int batchSize )
    {
        LOGGER.info( "geting failed ftp stream request" );
        return new Gson().toJson( streamMessagesService.getFtpFailedStreamMessages( startIndex, batchSize ),
            new TypeToken<List<FailedStreamMessage<FtpUploadRequest>>>() {}.getType() );
    }


    @RequestMapping ( value = "/crm/ftp/stream/failed", method = RequestMethod.POST)
    @ApiOperation ( value = "save failed ftp stream request")
    public String saveFailedFtpStreamMessage( @RequestBody FailedStreamMessage<FtpUploadRequest> failedStreamMessage )
        throws InvalidInputException
    {
        if ( failedStreamMessage == null ) {
            LOGGER.warn( "Invalid failed stream message" );
            throw new InvalidInputException( "Invalid failed stream message" );
        }

        LOGGER.info( "saving failed ftp stream request : {}", failedStreamMessage );
        streamMessagesService.saveFailedStreamMessage( failedStreamMessage );
        return failedStreamMessage.getId();
    }


    @RequestMapping ( value = "/crm/ftp/stream/failed", method = RequestMethod.DELETE)
    @ApiOperation ( value = "delete failed ftp stream request")
    public void deleteFailedFtpStreamMessage( String failedStreamMessageId ) throws InvalidInputException
    {
        if ( StringUtils.isEmpty( failedStreamMessageId ) ) {
            LOGGER.warn( "Invalid failed stream message ID" );
            throw new InvalidInputException( "Invalid failed stream message ID" );
        }

        LOGGER.info( "deleting failed ftp stream request with ID: {}", failedStreamMessageId );
        streamMessagesService.deleteFailedStreamMsg( failedStreamMessageId );
    }


    @RequestMapping ( value = "/getftpcrm/{companyId}/{ftpId}", method = RequestMethod.GET)
    @ApiOperation ( value = "get ftp crm info")
    public TransactionSourceFtp getFtpCrm( @PathVariable ( "companyId") long companyId, @PathVariable ( "ftpId") long ftpId )
    {
        LOGGER.info( "get ftp crm info for companyId : {}", companyId );
        return organizationManagementService.fetchFtpInfo( companyId, ftpId );
    }


    @RequestMapping ( value = "/crm/ftp/storm/failed", method = RequestMethod.POST)
    @ApiOperation ( value = "process failed ftp storm message")
    public ResponseEntity<String> processFailedFtpStormMessage( @RequestBody FailedStormMessage failedStormMessage )
        throws SSApiException
    {
        if ( failedStormMessage == null ) {
            LOGGER.warn( "Invalid failed storm message" );
            throw new SSApiException( "Invalid failed storm message" );
        }

        LOGGER.info( "processing failed ftp storm request : {}", failedStormMessage );

        try {
            String messageId = ftpManagement.processFailedStormMessage( failedStormMessage.getFtpUploadRequest(),
                failedStormMessage.getReasonForFailure(), failedStormMessage.isSendOnlyToSocialSurveyAdmin() );
            return new ResponseEntity<>( messageId, HttpStatus.OK );
        } catch ( InvalidInputException | UndeliveredEmailException e ) {
            throw new SSApiException( e.getMessage(), e );
        }
    }


    @RequestMapping ( value = "/crm/ftp/stream/failed/{id}/update", method = RequestMethod.POST)
    @ApiOperation ( value = "process failed ftp storm message")
    public ResponseEntity<String> updateFailedFtpStreamMessage( @PathVariable ( "id") String id )
        throws SSApiException
    {
        LOGGER.info( "updating failed ftp stream message with id : {}", id );

        try {
            ftpManagement.updateRetryFailedForFailedFtpRequest( id );
            return new ResponseEntity<>( HttpStatus.OK );
        } catch ( InvalidInputException e ) {
            throw new SSApiException( e.getMessage(), e );
        }
    }    

    @RequestMapping ( value = "/crm/ftp/complete/mail", method = RequestMethod.POST)
    @ApiOperation ( value = "process send ftp success mail")
    public ResponseEntity<String> sendCompletionMail( long companyId , long ftpId , String s3FileLocation ,@RequestBody FtpSurveyResponse ftpSurveyResponse ) throws  InvalidInputException, UndeliveredEmailException
    {
        LOGGER.info( "Sending Ftp completion mail for companyId:{} , ftpId:{}" );
        organizationManagementService.sendCompletionMailService( companyId, ftpId, s3FileLocation, ftpSurveyResponse );
        return new ResponseEntity<>( "Sucess", HttpStatus.OK );
    }
    
  
    
    @RequestMapping ( value = "/crm/{ftpId}/configmail", method = RequestMethod.POST)
    @ApiOperation ( value = "process set up ftp success mail")
    public ResponseEntity<String> configureFtpEmail( long companyId ,@PathVariable ( "ftpId") long ftpId , String email) throws NonFatalException
    {
        LOGGER.info( "Configuring ftp email companyId:{} , ftpId:{} , email : {}",companyId,ftpId,email );
        organizationManagementService.updateFtpMailService(companyId,ftpId,email);
        return new ResponseEntity<>( "Success", HttpStatus.OK );
    }
    
    @RequestMapping ( value = "/checkIfSurveyIsOld", method = RequestMethod.GET)
    @ApiOperation ( value = "get ftp crm info")
    public ResponseEntity<?> checkIfSurveyIsOld(  HttpServletRequest request )
        throws SSApiException
    {
        LOGGER.info( "SurveyApiController.checkIfSurveyIsOld started" );        
       String customerEmailId = request.getParameter("customerEmailId");
        //authorize request
        SurveyPreInitiation surveyPreInitiation = surveyHandler.getPreInitiatedSurveyByCustomer(customerEmailId);
        long DAY_IN_MS = 1000 * 60 * 60 * 24;
		Date CRITERIA_DATE = new Date(System.currentTimeMillis() - (30 * DAY_IN_MS));
		if(surveyPreInitiation != null && surveyPreInitiation.getCreatedOn().before(new Timestamp(CRITERIA_DATE.getTime())) ) {
			return new ResponseEntity<>( "true", HttpStatus.OK );
		}else {
			return new ResponseEntity<>( "false", HttpStatus.OK );
		}
        
        
    }
    
    @RequestMapping ( value = "/hierarchy/company/{identifier}/profilenames", method = RequestMethod.GET)
    @ApiOperation ( value = "get profile names of a heirarchy")
    public Map<String, Map<String, String>> getProfileNamesForHeirarchy( @PathVariable ( "identifier") long iden ) throws SSApiException
    {
        LOGGER.info( "Method getProfileNamesForHeirarchy() started to get profile names from ss-api." );
        try {
            return organizationManagementService.getProfileNameMapForCompany( iden );
        } catch ( Exception e ) {
            LOGGER.error( "could not get profile names", e );
            throw new SSApiException( "could not get profile names", e );
        }
    }
    
    @RequestMapping ( value = "/mismatched/emailId/{companyId}", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch mismatched survey for emailId")
    public ResponseEntity<?> fetchMismatchedSurveyForEmail( @PathVariable long companyId, String transactionEmail , int startIndex, int batchSize, long count,
        @RequestHeader ( "authorizationHeader") String authorizationHeader) throws  NonFatalException
    {
        LOGGER.info( "Fetch mismatched survey for emailId for companyId : {} , transactionEmail : {}",companyId,transactionEmail );
       
        //authorize request
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
        } catch ( AuthorizationException e ) {
        	return new ResponseEntity<>( "AUTHORIZATION FAILED", HttpStatus.UNAUTHORIZED );
        }
        SurveyPreInitiationList surveyPreInitiationList = socialManagementService.getUnmatchedPreInitiatedSurveysForEmail( companyId, transactionEmail, startIndex, batchSize, count );
        return new ResponseEntity<>( surveyPreInitiationList, HttpStatus.OK );
    }


    /**
     * Api to save the notification into mongo
     * Calls {@link OrganizationManagementService#saveNotification(long, String, long, String)}
     * @param notificationRequest
     * @return either true or false representing success/failure
     */
    @RequestMapping(value = "/notification", method = RequestMethod.POST)
    public boolean saveNotification( @RequestBody NotificationRequest notificationRequest ) {
        LOGGER.info( "Method to save error details to crmInfo has started" );
        boolean success = true ;

        long companyId = notificationRequest.getCompanyId();
        String message = notificationRequest.getMessage();
        String type = notificationRequest.getType();
        long receivedOn = notificationRequest.getReceivedOn();

        try {
            if ( companyId <= 0l || StringUtils.isEmpty( message ) || receivedOn <= 0 ) {
                throw new InvalidInputException( "Invalid details provided comapanyId = " + companyId + " ,error = " + message +
                    "errorOccuredOn = " + receivedOn);
            }
            organizationManagementService.saveNotification(companyId, message, receivedOn, type);

        }catch ( Exception e ) {
            LOGGER.error( "An exception occured while saving CRM error details "+ e.getMessage());

            success = false;
        }
        return success;
    }

    /**
     * Api disbable notification ie, changes {@link com.realtech.socialsurvey.core.entities.Notification#isDisabled} to true
     * Calls {@link OrganizationManagementService#disableNotification(long)}}
     * @param companyId
     * @return
     */
    @RequestMapping(value = "/disablenotification/{companyId}", method = RequestMethod.PUT)
    public ResponseEntity<Boolean> disableNotification(@PathVariable long companyId ) throws SSApiException
    {
        LOGGER.info( "Method to disbale notification started" );

        try {
            if ( companyId <= 0l ) {
                throw new InvalidInputException( "Invalid details provided comapanyId = " + companyId);
            }
            organizationManagementService.disableNotification(companyId);
            return new ResponseEntity<>(true, HttpStatus.CREATED );

        }catch ( Exception e ) {
            throw new SSApiException( e.getMessage() );
        }
    }

    @RequestMapping(value = "/enableincompletesurveydeletetoggle/{companyId}/{isIncompleteSurveyDeleteEnabled}", method = RequestMethod.POST)
    public ResponseEntity<Boolean> enableIncompleteSurveyDeleteToggle (@PathVariable long companyId, @PathVariable boolean isIncompleteSurveyDeleteEnabled) throws SSApiException
    {
    	LOGGER.info( "Method to enable incomplete survey delete toggle started");
    	try {
    		if ( companyId <= 0l ) {
                throw new InvalidInputException( "Invalid comapanyId = " + companyId);
            }
    		return new ResponseEntity<>(organizationManagementService.enableIncompleteSurveyDeleteToggle( companyId, isIncompleteSurveyDeleteEnabled),HttpStatus.OK);
    	}catch ( Exception e ) {
            throw new SSApiException( e.getMessage());
        }
    }
    
    @RequestMapping( value = "/branch/{id}", method = RequestMethod.GET)
    @ApiOperation( value = "Gets the branch details")
    public ResponseEntity<?> getBranchDetails(@PathVariable long id, @RequestHeader ( "authorizationHeader") String authorizationHeader)
        throws SSApiException
    {
        LOGGER.info( " Method to fetch branch details with id {} started ", id );
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
            return new ResponseEntity<>( organizationManagementService.getBranchDetails( id ), HttpStatus.OK );
        } catch ( AuthorizationException e ) {
            return new ResponseEntity<>( "AUTHORIZATION FAILED", HttpStatus.UNAUTHORIZED );
        } catch ( NoRecordsFetchedException | InvalidInputException e ) {
            throw new SSApiException( e.getMessage() );
        }
    }

    @RequestMapping( value = "/organizationsettings/{placeId}/idens", method = RequestMethod.GET)
    public ResponseEntity<List<OrganizationUnitIds>> getDetailsFromPlaceId(@PathVariable String placeId) {
        List<OrganizationUnitIds> ouIds=null;
        try {
            ouIds = organizationManagementService.getDetailsFromPlaceId(placeId);
        } catch ( InvalidInputException | ProfileNotFoundException e ) {
            LOGGER.error( "Could not fetch oranization ids for place id.",e );
        }
        return new ResponseEntity<List<OrganizationUnitIds>>( ouIds, HttpStatus.OK );
    }

    @RequestMapping ( value = "/updateSocialMediaLastFetched/profile/{profile}/iden/{iden}/socialMedia/{socialMedia}/current/{current}/previous/{previous}",
        method = RequestMethod.PUT)
    @ApiOperation ( value = "Updates the given field of socialMediaToken with the value")
    public ResponseEntity<?> updateSocialMediaLastFetched( HttpServletRequest request,
        @RequestHeader ( "authorizationHeader") String authorizationHeader, @PathVariable("profile") String profile,
        @PathVariable("iden") long iden, @PathVariable("socialMedia") String socialMedia,
        @PathVariable("current") long current, @PathVariable("previous") long previous)
        throws SSApiException
    {
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
            LOGGER.info( "SocialMonitorController.updateSocialMediaLastFetched started" );
            boolean updateStatus = organizationManagementService.updateSocialMediaLastFetched( profile, iden, socialMedia, current, previous );
            LOGGER.info( "SocialMonitorController.updateSocialMediaLastFetched completed successfully" );
            return new ResponseEntity<>( updateStatus, HttpStatus.OK );
        } catch ( AuthorizationException authoriztionFailure ) {
            return new ResponseEntity<>( "AUTHORIZATION FAILED", HttpStatus.UNAUTHORIZED );
        }
        catch ( InvalidInputException e ) {
            throw new SSApiException( e.getMessage(), e );
        }

    }

    @RequestMapping ( value = "/resetSocialMediaLastFetched/profile/{profile}/iden/{iden}/socialMedia/{socialMedia}",
        method = RequestMethod.PUT)
    @ApiOperation ( value = "Updates the given field of socialMediaToken with the value")
    public ResponseEntity<?> resetSocialMediaLastFetched( HttpServletRequest request,
        @RequestHeader ( "authorizationHeader") String authorizationHeader, @PathVariable("profile") String profile,
        @PathVariable("iden") long iden, @PathVariable("socialMedia") String socialMedia )
        throws SSApiException
    {
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
            LOGGER.info( "SocialMonitorController.resetSocialMediaLastFetched started" );
            boolean updateStatus = organizationManagementService.resetSocialMediaLastFetched( profile, iden, socialMedia );
            LOGGER.info( "SocialMonitorController.resetSocialMediaLastFetched completed successfully" );
            return new ResponseEntity<>( updateStatus, HttpStatus.OK );
        } catch ( AuthorizationException authoriztionFailure ) {
            return new ResponseEntity<>( "AUTHORIZATION FAILED", HttpStatus.UNAUTHORIZED );
        }
        catch ( InvalidInputException e ) {
            throw new SSApiException( e.getMessage(), e );
        }

    }

    /**
     * Api for saving alert emails to {@link EncompassCrmInfo} which is a part of
     * {@link OrganizationUnitSettings}
     * Calls {@link OrganizationManagementService#updateCompanySettings(long, String, Object)} }}
     * @param companyId
     * @return
     */
    @RequestMapping(value = "/updateencompassalertemailids", method = RequestMethod.POST)
    public ResponseEntity<?> updateEncompassAlertEmailIds(@RequestBody EncompassAlertMailsVO encompassAlertMailsVO ) throws SSApiException
    {
        LOGGER.info( "Method to update alert emails started" );
        try {
            
            LOGGER.info( "encompassAlertMailsVO = {}", encompassAlertMailsVO.toString() );
            long companyId = encompassAlertMailsVO.getCompanyId();
            String alertEmailIds = encompassAlertMailsVO.getAlertMails();
            
            if ( companyId <= 0l && StringUtils.isEmpty( alertEmailIds ) ) {
                throw new InvalidInputException( "Invalid details provided comapanyId = " + companyId + " alertEmailIds : " + alertEmailIds );
            }

            List<String> alertEmails = new ArrayList<>(  );

            if( !StringUtils.isEmpty( alertEmailIds ) && !alertEmailIds.contains( "," )
                && organizationManagementService.validateEmail( alertEmailIds ))
                alertEmails.add( alertEmailIds );
            else if( !StringUtils.isEmpty( alertEmailIds ) && alertEmailIds.contains( "," ) ){
                String mailIds[] = alertEmailIds.split( "," );

                if ( mailIds.length == 0 )
                    throw new InvalidInputException( "Alert Emails - " + alertEmailIds+ " entered to input is empty",
                        DisplayMessageConstants.GENERAL_ERROR );

                for ( String mailID : mailIds ) {
                    if ( !alertEmails.contains( mailID.trim().toLowerCase() ) ) {
                        if ( !organizationManagementService.validateEmail( mailID.trim() ) )
                            throw new InvalidInputException( "Alert EMail id - " + mailID + " entered amongst the mail ids to input is invalid",
                                DisplayMessageConstants.GENERAL_ERROR );
                        else {
                            alertEmails.add( mailID.trim().toLowerCase() );
                        }
                    }
                }
            }

            organizationManagementService.updateCompanySettings(companyId, MongoOrganizationUnitSettingDaoImpl.KEY_ALERT_EMAIL, alertEmails);
            return new ResponseEntity<>( alertEmails, HttpStatus.ACCEPTED );

        }catch ( Exception e ) {
            throw new SSApiException( e.getMessage(), e );
        }
    }


    @RequestMapping ( value = "/linkedinprofileurl", method = RequestMethod.POST)
    @ApiOperation ( value = "Save the linkedIn profile url fileds in socialMediaToken")
    public ResponseEntity<?> saveLinkedInProfileUrl( String entityType, long entityId, String linkedInProfileUrl )
        throws SSApiException
    {
        LOGGER.info( "Method to save linkedin profile url args: {},{} and {}",entityType, entityId, linkedInProfileUrl);
        try {
            String linkedInProfileurl = organizationManagementService.saveLinkedInProfileUrl( entityType, entityId,
                linkedInProfileUrl );
            return new ResponseEntity<>( linkedInProfileurl, HttpStatus.CREATED );
        } catch ( NonFatalException e ) {
            LOGGER.error( "An exception occured while saving linkedInProfileurl error details {} ", e.getMessage() );
            throw new SSApiException( e.getMessage(), e.getErrorCode() );
        }
    }
    /**
     * Api for fetching relevant company statistics required to show on admin dashboard
     * Calls {@link OrganizationManagementService#fetchCompanyStatistics(long)}} to get the
     * statistics from reporting DB
     * @param authorizationHeader
     * @param companyId
     * @return
     * @throws SSApiException
     */
    @GetMapping(value = "/fetchcompanystatistics/{companyId}")
    public ResponseEntity<?> fetchCompanyStatistics(@RequestHeader ( "authorizationHeader") String authorizationHeader,
        @PathVariable("companyId") long companyId )
    {
        try {
            LOGGER.debug("Authorization Header : {}", authorizationHeader);
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
            
            //get all the necessary data from reporting db
            CompanyStatistics companyStatistics = organizationManagementService.fetchCompanyStatistics( companyId );

            return new ResponseEntity<>( companyStatistics, HttpStatus.OK );
        }
        catch ( AuthorizationException authException){
            return new ResponseEntity<>( CommonConstants.AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        } catch ( NoRecordsFetchedException noRecordsFetchedException ){
            return new ResponseEntity<>( noRecordsFetchedException.getMessage(), HttpStatus.BAD_REQUEST );
        }
        catch ( Exception ex ){
            LOGGER.error("Exception while fetching company statistics for company {} with exception {}", companyId, ex);
            return new ResponseEntity<>( ex.getMessage(),  HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }
    
    @PutMapping(value = "/updatecustomerinformation/{companyId}")
    public ResponseEntity<?> updateCustomerInformation( @RequestHeader ( "authorizationHeader") String authorizationHeader,
        @PathVariable("companyId") long companyId, @RequestParam("key") String key,
        @RequestParam(value = "value", required = false) Object value,
        @RequestParam("modifiedBy") long modifiedBy )
    {
        LOGGER.debug( "Method updateCustomerInformation() started." );
        try {
            
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
            String message = organizationManagementService.updateCustomerInformation( companyId, key, value, modifiedBy );

            LOGGER.debug( "Method updateCustomerInformation() finished." );

            return new ResponseEntity<>( message, HttpStatus.OK );
        }
        catch ( AuthorizationException authException ) {
            
            LOGGER.error("Exception while authenticating put request ", companyId, authException);
            return new ResponseEntity<>( CommonConstants.AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        }
        catch ( Exception ex ) {
            
            LOGGER.error("Exception while fetching company statistics for company {} with exception {}", companyId, ex);
            return new ResponseEntity<>( ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }

    /**
     * Api for fetching customer success information required to show on admin dashboard
     * calls {@link OrganizationManagementService#fetchCustomerSuccessInformation(long)} to get the
     * data from mongo
     * @param authorizationHeader
     * @param companyId
     * @return
     * @throws SSApiException
     */
    @GetMapping(value = "/fetchcustomersuccessinfo/{companyId}")
    public ResponseEntity<?> fetchCustomerSuccessInfo(@RequestHeader ( "authorizationHeader") String authorizationHeader,
        @PathVariable("companyId") long companyId )
    {
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );

            CustomerSuccessInformation customerSuccessInfo = organizationManagementService.fetchCustomerSuccessInformation( companyId );

            return new ResponseEntity<>( customerSuccessInfo, HttpStatus.OK );
        } catch ( AuthorizationException authException ) {
            return new ResponseEntity<>( CommonConstants.AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        } catch ( NoRecordsFetchedException noRecordsFetchedException ) {
            return new ResponseEntity<>( noRecordsFetchedException.getMessage(), HttpStatus.BAD_REQUEST );
        } catch ( Exception e ) {
            LOGGER.error( "Exception while fetching customer success information for company {} with exception {}", companyId, e );
            return new ResponseEntity<>( e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }


    /**
     * API for fetching all the SS Admins. Calls {@link OrganizationManagementService#getSocialSurveyAdmins()}
     * @param authorizationHeader
     * @return
     * @throws SSApiException
     */
    @GetMapping(value = "/getsocialsurveyadmins")
    public ResponseEntity<?> getSocialSurveyAdmins(@RequestHeader ( "authorizationHeader") String authorizationHeader )
    {
        LOGGER.debug( "Method getSocialSurveyAdmins() started." );
        try {
            
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
            
            return new ResponseEntity<>( organizationManagementService.getSocialSurveyAdmins(), HttpStatus.OK );
        }
        catch ( AuthorizationException authException) {
            
            return new ResponseEntity<>( CommonConstants.AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        }
        catch ( Exception ex ) {
            
            return new ResponseEntity<>( ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }


    /**
     * Fetches the ss admin notes for the given companyId.
     * @param authorizationHeader
     * @param companyId
     * @param startIndex represents from which record the notes needs to be fetched
     * @param limit represents the the max records
     * @return
     */
    @GetMapping(value = "/fetchnotes/{companyId}/startIndex/{startIndex}/limit/{limit}")
    public ResponseEntity<?> getSSAdminNotes(@RequestHeader("authorizationHeader") String authorizationHeader,
        @PathVariable("companyId") long companyId, @PathVariable("startIndex") long startIndex, @PathVariable("limit") long limit  )
        throws SSApiException
    {
        LOGGER.debug( "Method to fetch ss admin notes for companyId {} started", companyId );
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
            return new ResponseEntity<>( organizationManagementService.fetchNotes(companyId, startIndex, limit), HttpStatus.OK );
        } catch ( AuthorizationException e ) {
            return new ResponseEntity<>( CommonConstants.AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        } catch ( InvalidInputException e ) {
            LOGGER.error( "Invalid details provided for fetching ssadmin notes ", e.getMessage() );
            return new ResponseEntity<>( e.getMessage(), HttpStatus.BAD_REQUEST );
        }catch ( Exception ex ) {
            throw new SSApiException( ex.getMessage() );
        }
    }

    @PostMapping(value = "/updateNotes")
    public ResponseEntity<?> updateNotes(@RequestHeader("authorizationHeader")String authorizationHeader,@RequestBody NotesVo notes)
        throws SSApiException
    {
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );

            organizationManagementService.updateSSAdminNotes( notes );
            return null;

        } catch ( AuthorizationException e ) {
            return new ResponseEntity<>( CommonConstants.AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        } catch ( NonFatalException e ) {
            LOGGER.error( "UpdatingNotes for companyId {} failed with exception {}", notes.getCompanyId(), e.getMessage() );
            throw new SSApiException( e.getMessage() );
        }
    }
}
