package com.realtech.socialsurvey.web.controller;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.QueryParam;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.realtech.socialsurvey.core.vo.ManageTeamBulkRequest;

import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.DatabaseException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.admin.AdminAuthenticationService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.upload.FileUploadService;
import com.realtech.socialsurvey.web.api.SSApiIntegration;
import com.realtech.socialsurvey.web.api.builder.SSApiIntergrationBuilder;
import com.realtech.socialsurvey.web.entities.PersonalProfile;

import retrofit.client.Response;
import retrofit.http.Part;
import retrofit.mime.TypedByteArray;


/**
 * Typically used for new user registration. The controller should not call
 * services directly but should call APIs
 */
@Controller
public class UserWebController
{
    private static final Logger LOG = LoggerFactory.getLogger( UserWebController.class );

    @Autowired
    private SSApiIntergrationBuilder apiBuilder;
    private FileUploadService fileUploadService;
    private UserManagementService userManagementService;
    private AdminAuthenticationService adminAuthenticationService;

    @Value ( "${CDN_PATH}")
    private String amazonEndpoint;

    @Value ( "${AMAZON_IMAGE_BUCKET}")
    private String amazonImageBucket;
    
    @Value("${SOCIAL_MONITOR_AUTH_HEADER}")
    private String authHeader;
    
    @Autowired
    private SessionHelper sessionHelper;

    @Autowired
    public UserWebController( SSApiIntergrationBuilder apiBuilder, FileUploadService fileUploadService,
        UserManagementService userManagementService )
    {
        this.apiBuilder = apiBuilder;
        this.fileUploadService = fileUploadService;
        this.userManagementService = userManagementService;
    }


    @RequestMapping ( value = "/registeraccount/getuserprofile", method = RequestMethod.GET)
    @ResponseBody
    public String getUserProfile( @QueryParam ( "userId") String userId )
    {
        String responseString = null;
        SSApiIntegration api = apiBuilder.getIntegrationApi();
        Response response = api.getUserProfile( userId );
        responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        return responseString;
    }


    @RequestMapping ( value = "/registeraccount/updateuserprofile", method = RequestMethod.PUT)
    @ResponseBody
    public String updateUserProfile( @QueryParam ( "userId") String userId, @QueryParam ( "stage") String stage,
        @RequestBody PersonalProfile personalProfile )
    {
        String responseString = null;
        SSApiIntegration api = apiBuilder.getIntegrationApi();
        Response response = api.updateUserProfile( personalProfile, userId );
        responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        if ( response.getStatus() == HttpStatus.SC_OK ) {
            response = api.updateUserProfileStage( userId, stage );
            responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        }
        return responseString;
    }


    @RequestMapping ( value = "/registeraccount/getuserstage", method = RequestMethod.GET)
    @ResponseBody
    public String getUserStage( @QueryParam ( "userId") String userId )
    {
        String responseString = null;
        SSApiIntegration api = apiBuilder.getIntegrationApi();
        Response response = api.getUserStage( userId );
        responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        return responseString;
    }


    @RequestMapping ( value = "/registeraccount/updateuserstage", method = RequestMethod.PUT)
    @ResponseBody
    public String getUpdateUserStage( @QueryParam ( "userId") String userId, @QueryParam ( "stage") String stage )
    {
        String responseString = null;
        SSApiIntegration api = apiBuilder.getIntegrationApi();
        Response response = api.updateUserProfileStage( userId, stage );
        responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        return responseString;
    }


    @RequestMapping ( value = "/registeraccount/uploaduserprofilelogo", method = RequestMethod.POST)
    @ResponseBody
    public String uploadUserProfileLogo( @QueryParam ( "userId") String userId, MultipartHttpServletRequest request )
        throws InvalidInputException, IllegalStateException, IOException
    {
        String responseString = null;
        SSApiIntegration api = apiBuilder.getIntegrationApi();
        Iterator<String> itr = request.getFileNames();
        while ( itr.hasNext() ) {
            String uploadedFile = itr.next();
            MultipartFile file = request.getFile( uploadedFile );
            File fileLocal = new File( CommonConstants.IMAGE_NAME );
            file.transferTo( fileLocal );
            String profileImageUrl = fileUploadService.uploadProfileImageFile( fileLocal, file.getOriginalFilename(), false );
            profileImageUrl = amazonEndpoint + CommonConstants.FILE_SEPARATOR + amazonImageBucket
                + CommonConstants.FILE_SEPARATOR + profileImageUrl;
            Response response = api.updateUserProfileImage( userId, profileImageUrl );
            responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        }
        return responseString;
    }


    @RequestMapping ( value = "/registeraccount/removeuserprofilelogo", method = RequestMethod.PUT)
    @ResponseBody
    public String removeUserProfileLogo( @QueryParam ( "userId") String userId )
    {
        String responseString = null;
        SSApiIntegration api = apiBuilder.getIntegrationApi();
        Response response = api.removeUserProfileImage( userId );
        responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        return responseString;
    }


    @RequestMapping ( value = "/registeraccount/validatewebadress", method = RequestMethod.POST)
    @ResponseBody
    public String validateWebAddress( @RequestBody String webAddress )
    {
        String responseString = null;
        SSApiIntegration api = apiBuilder.getIntegrationApi();
        Response response = api.validateWebAddress( webAddress );
        responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        return responseString;
    }


    @RequestMapping ( value = "/registeraccount/savePassword", method = RequestMethod.PUT)
    @ResponseBody
    public String savePassword( @QueryParam ( "userId") String userId, @RequestBody String password )
    {
        String responseString = null;
        SSApiIntegration api = apiBuilder.getIntegrationApi();
        Response response = api.savePassword( userId, password );
        responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        return responseString;
    }


    @RequestMapping ( value = "/registeraccount/isregistrationpasswordset", method = RequestMethod.GET)
    @ResponseBody
    public boolean isRegistrationPasswordSet( @QueryParam ( "userId") String userId )
        throws InvalidInputException, JsonProcessingException
    {
        User user = userManagementService.getUserByUserId( Long.parseLong( userId ) );
        return user.getLoginPassword() != null ? true : false;
    }


    @RequestMapping ( value = "/registeraccount/setregistrationpassword")
    public String setRegistrationPassword( @RequestParam ( "q") String encryptedUrlParams, RedirectAttributes attributes )
        throws InvalidInputException, JsonProcessingException
    {
        Map<String, String> urlParams = userManagementService.validateRegistrationUrl( encryptedUrlParams );
        long userId = Long.parseLong( urlParams.get( CommonConstants.USER_ID ) );
        User user = userManagementService.getUserByUserId( userId );
        if ( user.getLoginPassword() == null ) {
            attributes.addFlashAttribute( "userId", Long.parseLong( urlParams.get( CommonConstants.USER_ID ) ) );
            attributes.addFlashAttribute( "companyId", Long.parseLong( urlParams.get( CommonConstants.COMPANY_ID ) ) );
            attributes.addFlashAttribute( "firstName", urlParams.get( CommonConstants.FIRST_NAME ) );
            attributes.addFlashAttribute( "lastName", urlParams.get( CommonConstants.LAST_NAME ) );
            attributes.addFlashAttribute( "setPassword", true );
            return "redirect:/accountsignupredirect.do?PlanId=" + urlParams.get( CommonConstants.PLAN_ID );
        } else {
            return "redirect:/newlogin.do";
        }
    }

    @ResponseBody
    @PostMapping ( value = "/users/reinvite")
    public String reInviteUsers(@RequestBody List<String> emailIds ){
        final String authorizationHeader = CommonConstants.BASIC + authHeader;
        try {
            return new Gson().toJson( apiBuilder.getIntegrationApi().
                reInviteUsers(emailIds, authorizationHeader) );
        }catch ( Exception ex ){
            LOG.error( "Exception while reinviting the users {}", ex );
            return ex.getMessage();
        }
    }

    @ResponseBody
    @PostMapping ( value = "/users/delete" )
    public String deleteUsers(HttpServletRequest request, @RequestBody List<Long> userIds){
        final String authorizationHeader = CommonConstants.BASIC + authHeader;
        
        ManageTeamBulkRequest manageTeamBulkRequest = new ManageTeamBulkRequest();
        try {
            manageTeamBulkRequest.setUserIds( userIds );

            //get the ID of the user who is currently logged in
            manageTeamBulkRequest.setAdminId(  sessionHelper.getCurrentUser().getUserId() );
            
            return new Gson().toJson( apiBuilder.getIntegrationApi().
                deleteUsers(manageTeamBulkRequest, authorizationHeader) );
        }catch ( Exception ex ){
            LOG.error( "Exception while deleting the users {}", ex );
            return ex.getMessage();
        }
    }

    @ResponseBody
    @PostMapping ( value = "/users/assigntobranch" )
    public String assignUsersToBranch(HttpServletRequest request, @RequestBody ManageTeamBulkRequest manageTeamBulkRequest){
        final String authorizationHeader = CommonConstants.BASIC + authHeader;
        try {
            manageTeamBulkRequest.setAdminId( sessionHelper.getCurrentUser().getUserId() );
            
            return new Gson().toJson( apiBuilder.getIntegrationApi().
                assignUsersToBranch(manageTeamBulkRequest, authorizationHeader) );
        }catch ( Exception ex ){
            LOG.error( "Exception while assigning the users to branch {}", ex );
            return ex.getMessage();
        }
    }

    @ResponseBody
    @PostMapping ( value = "/users/assigntoregion" )
    public String assignUsersToRegion(HttpServletRequest request, @RequestBody ManageTeamBulkRequest manageTeamBulkRequest){
        final String authorizationHeader = CommonConstants.BASIC + authHeader;
        try {
            manageTeamBulkRequest.setAdminId( sessionHelper.getCurrentUser().getUserId() );
            
            return new Gson().toJson( apiBuilder.getIntegrationApi().
                assignUsersToRegion(manageTeamBulkRequest, authorizationHeader) );
        }catch ( Exception ex ){
            LOG.error( "Exception while assigning users to region {}", ex );
            return ex.getMessage();
        }
    }

    @ResponseBody
    @PostMapping ( value = "/users/assignassocialmonitoradmin" )
    public String assignUsersAsSocialMonitorAdmin(HttpServletRequest request, @RequestBody List<Long> userIds){
        final String authorizationHeader = CommonConstants.BASIC + authHeader;
        ManageTeamBulkRequest manageTeamBulkRequest = new ManageTeamBulkRequest();
        try {
            manageTeamBulkRequest.setUserIds( userIds );
            
            manageTeamBulkRequest.setAdminId( sessionHelper.getCurrentUser().getUserId() );
            
            return new Gson().toJson( apiBuilder.getIntegrationApi().
                assignUsersAsSocialMonitorAdmin(manageTeamBulkRequest, authorizationHeader) );
        }catch ( Exception ex ){
            LOG.error( "Exception while assigning the users as social monitor admin {}", ex );
            return ex.getMessage();
        }
    }

    @ResponseBody
    @PostMapping ( value = "/users/autopostscore" )
    public String updateAutoPostScore(HttpServletRequest request, @RequestBody ManageTeamBulkRequest manageTeamBulkRequest){
        final String authorizationHeader = CommonConstants.BASIC + authHeader;
        try {
            manageTeamBulkRequest.setAdminId( sessionHelper.getCurrentUser().getUserId() );
            
            return new Gson().toJson( apiBuilder.getIntegrationApi().
                updateAutoPostScore(manageTeamBulkRequest, authorizationHeader) );
        }catch ( Exception ex ){
            LOG.error( "Exception while updating the autopostscore for users {}", ex );
            return ex.getMessage();
        }
    }
    
    @ResponseBody
    @PostMapping ( value = "/users/uploadlogo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE )
    public String bulkUpdateLogoForAgents(@Part("logo") MultipartFile file, @RequestParam("userIds") String userIds,
        @RequestParam ( "logoFileName") String logoFileName)
    {
        LOG.info( "Method to update logo started for {} with the logoFileName {}", userIds, logoFileName );
        List<Long> userId =   new Gson().fromJson(userIds, new TypeToken<List<Long>>(){}.getType());
        try {
            return new Gson().toJson( userManagementService.bulkUpdateLogoForAgents( userId, logoFileName, file) );
        } catch ( InvalidInputException e ) {
            LOG.error( "Exception while updating the logo for users {}", e );
            return e.getMessage();
        } catch ( DatabaseException exception ) {
            LOG.error( "Exception while updating the logo for users {}", exception );
            return exception.getMessage();
        }

    }

    @ResponseBody
    @PostMapping ( value = "/users/uploadprofilepic")
    public String bulkUploadProfilePicToAgents(@RequestBody ManageTeamBulkRequest manageTeamBulkRequest)
    {
        LOG.info(" Method to update profile pic for agents: {} started ",manageTeamBulkRequest.getUserIds());
        String authorizationHeader = CommonConstants.BASIC + authHeader;
        try {
            return new Gson().toJson( apiBuilder.
                getIntegrationApi().bulkUploadProfilePicToAgents( manageTeamBulkRequest, authorizationHeader) );
        } catch ( Exception ex) {
            LOG.error( "Exception while updating the profile pic for users {}", ex );
            return ex.getMessage();
        }

    }

    @ResponseBody
    @GetMapping ( value = "/users/active")
    public String getAllActiveUsersForCompany( HttpServletRequest request )
    {
        LOG.info( " Method to fetch all active users started " );
        String authorizationHeader = CommonConstants.BASIC + authHeader;

        User user = sessionHelper.getCurrentUser();
        long companyId = user.getCompany().getCompanyId();
        
        String entityType = (String) request.getSession( false ).getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        if(user.isCompanyAdmin()) {
            entityType = CommonConstants.COMPANY_ID_COLUMN;
        }else if(user.isRegionAdmin()) {
            entityType = CommonConstants.REGION_ID_COLUMN;
        }else if(user.isBranchAdmin()) {
            entityType = CommonConstants.BRANCH_ID_COLUMN;
        }
        try {

            String sortingOrder = request.getParameter( CommonConstants.SORT_ORDER );
            if(sortingOrder == null || sortingOrder.isEmpty()) {
                sortingOrder = "ASC";
            }

            String searchKey = request.getParameter( "searchKey" );
            if(searchKey == null || searchKey.isEmpty()) {
                return new Gson().toJson( apiBuilder.
                    getIntegrationApi().getAllActiveUsersInHierarchy(companyId,entityType,user.getUserId(),authorizationHeader) );
            } else {
                return new Gson().toJson( apiBuilder.getIntegrationApi().getAllActiveUsersInHierarchy( searchKey, companyId,
                    user.getUserId(), sortingOrder, entityType, authorizationHeader ) );
            }

        } catch ( Exception ex) {
            LOG.error( "Exception while getting all active users for the company {}", ex );
            return ex.getMessage();
        }

    }

    @ResponseBody
    @GetMapping ( value = "/users/unverified")
    public String getAllUnverifiedUsers( HttpServletRequest request )
    {
        LOG.info( "Method to fetch all unverified users started" );

        String authorizationHeader = CommonConstants.BASIC + authHeader;

        User user = sessionHelper.getCurrentUser();
        long companyId = user.getCompany().getCompanyId();
        
        String entityType = (String) request.getSession( false ).getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        if(user.isCompanyAdmin()) {
            entityType = CommonConstants.COMPANY_ID_COLUMN;
        }else if(user.isRegionAdmin()) {
            entityType = CommonConstants.REGION_ID_COLUMN;
        }else if(user.isBranchAdmin()) {
            entityType = CommonConstants.BRANCH_ID_COLUMN;
        }
        try{
            String sortingOrder = request.getParameter( CommonConstants.SORT_ORDER );
            if(sortingOrder == null || sortingOrder.isEmpty()) {
                sortingOrder = "ASC";
            }

            String searchKey = request.getParameter( "searchKey" );
            if(searchKey == null || searchKey.isEmpty()) {
                return new Gson().toJson( apiBuilder.
                    getIntegrationApi().getAllUnverifiedUsersInHierarchy(companyId,entityType,user.getUserId(),authorizationHeader) );
            } else {
                return new Gson().toJson( apiBuilder.getIntegrationApi().getAllUnverifiedUsersInHierarchy( searchKey, companyId,
                    user.getUserId(), sortingOrder, entityType, authorizationHeader ) );
            }
        } catch ( Exception ex) {
            LOG.error( "Exception while getting all unverified users for the company {}",  ex );
            return ex.getMessage();
        }
    }
    
    @ResponseBody
    @GetMapping ( value = "/users/verified")
    public String getAllVerifiedUsers( HttpServletRequest request )
    {
        LOG.info( "Method to fetch all verified users started" );

        String authorizationHeader = CommonConstants.BASIC + authHeader;

        User user = sessionHelper.getCurrentUser();
        long companyId = user.getCompany().getCompanyId();
        
        String entityType = (String) request.getSession( false ).getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        if(user.isCompanyAdmin()) {
            entityType = CommonConstants.COMPANY_ID_COLUMN;
        }else if(user.isRegionAdmin()) {
            entityType = CommonConstants.REGION_ID_COLUMN;
        }else if(user.isBranchAdmin()) {
            entityType = CommonConstants.BRANCH_ID_COLUMN;
        }
        try{
        String sortingOrder = request.getParameter( CommonConstants.SORT_ORDER );
        if(sortingOrder == null || sortingOrder.isEmpty()) {
            sortingOrder = "ASC";
        }
        
        String searchKey = request.getParameter( "searchKey" );
        if(searchKey == null || searchKey.isEmpty()) {
            return new Gson().toJson( apiBuilder.
                getIntegrationApi().getAllVerifiedUsersInHierarchy(companyId,entityType,user.getUserId(),authorizationHeader) );
        } else {
            return new Gson().toJson( apiBuilder.getIntegrationApi().getAllVerifiedUsersInHierarchy( searchKey, companyId,
                user.getUserId(), sortingOrder,entityType, authorizationHeader ) );
        }
        } catch (Exception ex) {
            LOG.error("Exception while getting all verified users for the company {}", ex );
            return ex.getMessage();
        }
    }
}

