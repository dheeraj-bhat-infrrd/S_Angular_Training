package com.realtech.socialsurvey.web.controller;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.ws.rs.QueryParam;

import org.apache.commons.httpclient.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
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
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.upload.FileUploadService;
import com.realtech.socialsurvey.web.api.SSApiIntegration;
import com.realtech.socialsurvey.web.api.builder.SSApiIntergrationBuilder;
import com.realtech.socialsurvey.web.entities.PersonalProfile;

import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


/**
 * Typically used for new user registration. The controller should not call
 * services directly but should call APIs
 */
@Controller
public class UserWebController
{
    private SSApiIntergrationBuilder apiBuilder;
    private FileUploadService fileUploadService;
    private UserManagementService userManagementService;

    @Value ( "${CDN_PATH}")
    private String amazonEndpoint;

    @Value ( "${AMAZON_IMAGE_BUCKET}")
    private String amazonImageBucket;


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
}
