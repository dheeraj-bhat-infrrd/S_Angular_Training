package com.realtech.socialsurvey.web.controller;
import com.realtech.socialsurvey.web.common.JspResolver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.LinkedinUserProfileResponse;
import com.realtech.socialsurvey.core.entities.RegistrationStage;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.payment.Payment;
import com.realtech.socialsurvey.core.services.social.SocialAsyncService;
import com.realtech.socialsurvey.core.services.social.SocialManagementService;
import com.realtech.socialsurvey.core.services.upload.FileUploadService;
import com.realtech.socialsurvey.web.api.SSApiIntegration;
import com.realtech.socialsurvey.web.api.builder.SSApiIntergrationBuilder;
import com.realtech.socialsurvey.web.api.entities.AccountRegistrationAPIRequest;
import com.realtech.socialsurvey.web.api.entities.CaptchaAPIRequest;
import com.realtech.socialsurvey.web.api.exception.SSAPIException;
import com.realtech.socialsurvey.web.common.TokenHandler;
import com.realtech.socialsurvey.web.entities.AuthError;
import com.realtech.socialsurvey.web.entities.CompanyProfile;
import com.realtech.socialsurvey.web.entities.PersonalProfile;
import com.realtech.socialsurvey.web.ui.entities.AccountRegistration;
import com.realtech.socialsurvey.web.util.RequestUtils;

import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


/**
 * Typically used for account registration. The controller should not call
 * services directly but should call APIs
 */
@Controller
public class AccountController
{
    private static final Logger LOG = LoggerFactory.getLogger( AccountController.class );

    @Autowired
    private SSApiIntergrationBuilder apiBuilder;

    @Autowired
    private RequestUtils requestUtils;

    @Autowired
    private TokenHandler tokenHandler;

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private SocialAsyncService socialAsyncService;

    @Autowired
    private SocialManagementService socialManagementService;

    @Autowired
    private Payment gateway;

    @Autowired
    private FileUploadService fileUploadService;

    // LinkedIn
    @Value ( "${LINKED_IN_API_KEY}")
    private String linkedInApiKey;

    @Value ( "${LINKED_IN_API_SECRET}")
    private String linkedInApiSecret;

    @Value ( "${LINKED_IN_REDIRECT_URI_STATELESS}")
    private String linkedinRedirectUri;

    @Value ( "${LINKED_IN_AUTH_URI}")
    private String linkedinAuthUri;

    @Value ( "${LINKED_IN_SCOPE}")
    private String linkedinScope;

    @Value ( "${LINKED_IN_ACCESS_URI}")
    private String linkedinAccessUri;

    @Value ( "${LINKED_IN_PROFILE_URI}")
    private String linkedinProfileUri;

    @Value ( "${AMAZON_LOGO_BUCKET}")
    private String amazonLogoBucket;

    @Value ( "${CDN_PATH}")
    private String amazonEndpoint;

    @Value ( "${AMAZON_IMAGE_BUCKET}")
    private String amazonImageBucket;

    private static final String AGENT_UNIT = "agent";
    private static final String BRANCH_UNIT = "branch";
    private static final String REGION_UNIT = "region";
    private static final String COMPANY_UNIT = "company";


    @RequestMapping ( value = "/registeraccount/initiateregistration", method = RequestMethod.POST)
    @ResponseBody
    public String initateAccountRegistration( @RequestBody AccountRegistration account, HttpServletRequest request )
    {
        LOG.info( "Registering user" );
        String responseString = null;
        SSApiIntegration api = apiBuilder.getIntegrationApi();

        // validate captcha
        CaptchaAPIRequest captchaRequest = new CaptchaAPIRequest();
        captchaRequest.setRemoteAddress( request.getRemoteAddr() );
        captchaRequest.setCaptchaResponse( account.getCaptchaResponse() );
        api.validateCaptcha( captchaRequest );

        // initiate registration
        AccountRegistrationAPIRequest accountRequest = new AccountRegistrationAPIRequest();
        accountRequest.setFirstName( account.getFirstName() );
        accountRequest.setLastName( account.getLastName() );
        accountRequest.setCompanyName( account.getCompanyName() );
        accountRequest.setEmail( account.getEmail() );
        accountRequest.setPhone( account.getPhone() );
        Response response = api.initateRegistration( accountRequest );
        responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        return responseString;
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


    @RequestMapping ( value = "/registeraccount/getcompanyprofile", method = RequestMethod.GET)
    @ResponseBody
    public String getCompanyProfile( @QueryParam ( "companyId") String companyId )
    {
        String responseString = null;
        SSApiIntegration api = apiBuilder.getIntegrationApi();
        Response response = api.getCompanyProfile( companyId );
        responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        return responseString;
    }


    @RequestMapping ( value = "/registeraccount/updatecompanyprofile", method = RequestMethod.PUT)
    @ResponseBody
    public String updateCompanyProfile( @QueryParam ( "companyId") String companyId, @QueryParam ( "stage") String stage,
        @RequestBody CompanyProfile companyProfile )
    {
        String responseString = null;
        SSApiIntegration api = apiBuilder.getIntegrationApi();
        Response response = api.updateCompanyProfile( companyProfile, companyId );
        responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        if ( response.getStatus() == HttpStatus.SC_OK ) {
            response = api.updateCompanyProfileStage( companyId, stage );
            responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        }
        return responseString;
    }


    @RequestMapping ( value = "/registeraccount/getverticals", method = RequestMethod.GET)
    @ResponseBody
    public String getVerticals()
    {
        String responseString = null;
        SSApiIntegration api = apiBuilder.getIntegrationApi();
        Response response = api.getVerticals();
        responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        return responseString;
    }


    @RequestMapping ( value = "/registeraccount/getpaymentplans", method = RequestMethod.GET)
    @ResponseBody
    public String getPaymentPlans()
    {
        String responseString = null;
        SSApiIntegration api = apiBuilder.getIntegrationApi();
        Response response = api.getPaymentPlans();
        responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
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


    @RequestMapping ( value = "/registeraccount/getcompanystage", method = RequestMethod.GET)
    @ResponseBody
    public String getCompanyStage( @QueryParam ( "companyId") String companyId )
    {
        String responseString = null;
        SSApiIntegration api = apiBuilder.getIntegrationApi();
        Response response = api.getCompanyStage( companyId );
        responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        return responseString;
    }


    @RequestMapping ( value = "/registeraccount/getclienttoken", method = RequestMethod.GET)
    @ResponseBody
    public String getClientToken() throws JsonProcessingException
    {
        ObjectMapper mapper = new ObjectMapper();
        String jsonStr = mapper.writeValueAsString( gateway.getClientToken() );
        return jsonStr;
    }


    @RequestMapping ( value = "/registeraccount/generatehierarchy", method = RequestMethod.POST)
    @ResponseBody
    public String generateHierarchy( @QueryParam ( "companyId") String companyId )
    {
        String responseString = null;
        SSApiIntegration api = apiBuilder.getIntegrationApi();
        Response response = api.generateDefaultHierarchy( companyId );
        responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        return responseString;
    }


    @RequestMapping ( value = "/registeraccount/uploadcompanylogo", method = RequestMethod.POST)
    @ResponseBody
    public String uploadCompanyLogo( @QueryParam ( "companyId") String companyId, MultipartHttpServletRequest request )
        throws InvalidInputException
    {
        String responseString = null;
        SSApiIntegration api = apiBuilder.getIntegrationApi();
        Iterator<String> itr = request.getFileNames();
        while ( itr.hasNext() ) {
            String uploadedFile = itr.next();
            MultipartFile file = request.getFile( uploadedFile );
            String logoUrl = fileUploadService.uploadLogo( file, file.getOriginalFilename() );
            logoUrl = amazonEndpoint + CommonConstants.FILE_SEPARATOR + amazonLogoBucket + CommonConstants.FILE_SEPARATOR
                + logoUrl;
            Response response = api.updateCompanyLogo( companyId, logoUrl );
            responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        }
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


    @RequestMapping ( value = "/registeraccount/makepayment", method = RequestMethod.POST)
    @ResponseBody
    public String makePayment( @QueryParam ( "companyId") String companyId, @QueryParam ( "planId") String planId,
        @RequestBody com.realtech.socialsurvey.web.entities.Payment payment )
    {
        String responseString = null;
        SSApiIntegration api = apiBuilder.getIntegrationApi();
        Response response = api.makePayment( companyId, planId, payment );
        responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        if ( response.getStatus() == HttpStatus.SC_OK ) {
            response = api.updateCompanyProfileStage( companyId, RegistrationStage.PAYMENT.getCode() );
            responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        }
        return responseString;
    }


    @RequestMapping ( value = "/registeraccount/setregistrationpassword", method = RequestMethod.GET)
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
        } else {
            return "redirect:/login.do";
        }
        return "redirect:/accountsignup.do";
    }


    @RequestMapping ( value = "/registeraccount/savePassword", method = RequestMethod.PUT)
    @ResponseBody
    public String savePassword( @QueryParam ( "userId") String userId, @RequestBody String password )
    {
        String responseString = null;
        SSApiIntegration api = apiBuilder.getIntegrationApi();
        Response response = api.savePassword( userId, password );
        responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        return responseString;
    }


    // TODO: To be moved from register account to more generic
    @RequestMapping ( value = "/registeraccount/{organizationunit}/initlinkedinconnection", method = RequestMethod.POST)
    @ResponseBody
    public String initiateLinkedInConnection( @RequestBody String id,
        @PathVariable ( "organizationunit") String organizationunit, HttpServletRequest request ) throws JsonProcessingException
    {
        LOG.debug( "Creating linkedin url" );
        String serverBaseUrl = requestUtils.getRequestServerName( request );
        StringBuilder linkedInAuth = new StringBuilder( linkedinAuthUri ).append( "?response_type=" ).append( "code" )
            .append( "&client_id=" ).append( linkedInApiKey ).append( "&redirect_uri=" ).append( serverBaseUrl )
            .append( linkedinRedirectUri ).append( "?unit-id=" ).append( organizationunit ).append( "-" ).append( id )
            .append( "&state=" ).append( "SOCIALSURVEY" ).append( "&scope=" ).append( linkedinScope );
        ObjectMapper mapper = new ObjectMapper();
        String jsonStr = mapper.writeValueAsString( linkedInAuth.toString() );
        LOG.debug( "LinkedIn Auth URL: " + jsonStr );
        return jsonStr;
    }


    // TODO: To be moved from register account to more generic
    @RequestMapping ( value = "/registeraccount/connectlinkedin", method = RequestMethod.GET)
    public String connectToLinkedIn( HttpServletRequest request, Model model ) throws InvalidInputException
    {
        LOG.info( "Connecting to linkedin" );
        String response = null;
        // the unit and id should be there in the response with in the redirect url
        String unit_id = request.getParameter( "unit-id" );
        String unit = null;
        String sId = null;
        if ( unit_id != null ) {
            String[] params = unit_id.split( "-" );
            if ( params != null && params.length == 2 ) {
                unit = params[0];
                sId = params[1];
            }
        }
        LOG.debug( "Unit: " + unit + " and id: " + sId );
        // check if there is error
        String errorCode = request.getParameter( "error" );
        if ( errorCode != null ) {
            LOG.error( "Error code : " + errorCode );
            AuthError error = new AuthError();
            error.setErrorCode( errorCode );
            error.setReason( request.getParameter( "error_description" ) );
            response = new Gson().toJson( error );
        } else {
            try {
                if ( sId != null && unit != null ) {
                    LOG.debug( "Authentication successful." );
                    // Getting Oauth access token for LinkedIn
                    String oauthCode = request.getParameter( "code" );
                    List<NameValuePair> params = new ArrayList<NameValuePair>( 5 );
                    params.add( new BasicNameValuePair( "grant_type", "authorization_code" ) );
                    params.add( new BasicNameValuePair( "code", oauthCode ) );
                    params.add( new BasicNameValuePair( "redirect_uri",
                        requestUtils.getRequestServerName( request ) + linkedinRedirectUri + "?unit-id=" + unit_id ) );
                    params.add( new BasicNameValuePair( "client_id", linkedInApiKey ) );
                    params.add( new BasicNameValuePair( "client_secret", linkedInApiSecret ) );
                    LOG.debug( "oauthCode in param " + oauthCode );
                    LOG.debug( "redirect in param " + requestUtils.getRequestServerName( request ) + linkedinRedirectUri
                        + "?unit-id=" + unit_id );
                    LOG.debug( "linkedInApiKey in param " + linkedInApiKey );
                    LOG.debug( "linkedInApiSecret in param " + linkedInApiSecret );
                    LOG.debug( "linkedinAccessUri " + linkedinAccessUri );

                    // fetching access token
                    HttpClient httpclient = HttpClientBuilder.create().build();
                    HttpPost httpPost = new HttpPost( linkedinAccessUri );
                    httpPost.setEntity( new UrlEncodedFormEntity( params, "UTF-8" ) );
                    String accessTokenStr = httpclient.execute( httpPost, new BasicResponseHandler() );
                    Map<String, Object> map = new Gson().fromJson( accessTokenStr,
                        new TypeToken<Map<String, String>>() {}.getType() );
                    String accessToken = (String) map.get( "access_token" );

                    // fetching LinkedIn profile url
                    HttpGet httpGet = new HttpGet( linkedinProfileUri + accessToken );
                    String basicProfileStr = httpclient.execute( httpGet, new BasicResponseHandler() );
                    LinkedinUserProfileResponse profileData = new Gson().fromJson( basicProfileStr,
                        LinkedinUserProfileResponse.class );
                    String profileLink = profileData.getSiteStandardProfileRequest().getUrl();
                    // get social media tokens
                    SocialMediaTokens tokens = null;
                    if ( unit.equalsIgnoreCase( AGENT_UNIT ) ) {
                        tokens = organizationManagementService.getAgentSocialMediaTokens( Long.parseLong( sId ) );
                        tokens = tokenHandler.updateLinkedInToken( accessToken, tokens, profileLink );
                        // update tokens
                        tokens = socialManagementService.updateSocialMediaTokens( CommonConstants.AGENT_SETTINGS_COLLECTION,
                            Long.parseLong( sId ), tokens );
                        // update LinkedIn data to settings
                        AgentSettings agentSettings = userManagementService.getUserSettings( Long.parseLong( sId ) );
                        agentSettings = (AgentSettings) socialAsyncService.linkedInDataUpdate(
                            MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings, tokens );
                        response = "ok";
                    } else if ( unit.equalsIgnoreCase( BRANCH_UNIT ) ) {
                        //TODO: Handle branch LinkedIn Connection
                        response = "ok";
                    } else if ( unit.equalsIgnoreCase( REGION_UNIT ) ) {
                        //TODO: Handle region LinkedIn Connection
                        response = "ok";
                    } else if ( unit.equalsIgnoreCase( COMPANY_UNIT ) ) {
                        //TODO: Handle company LinkedIn Connection
                        response = "ok";
                    }
                } else {
                    LOG.error( "Expecting id and unit from linkedin." );
                    throw new SSAPIException( "Could not fetch LinkedIn profile. Could not pass parameters " );
                }
            } catch ( IOException ioe ) {
                LOG.error( "Found exception while accessing profile data " + ioe.getMessage(), ioe );
                throw new SSAPIException( "Could not fetch LinkedIn profile. Reason: " + ioe.getMessage() );
            }
        }
        model.addAttribute("isLinkedin", true);
        model.addAttribute("linkedinResponse", response);
        
        return JspResolver.NEW_ACCOUNT_SIGNUP;
    }
}
