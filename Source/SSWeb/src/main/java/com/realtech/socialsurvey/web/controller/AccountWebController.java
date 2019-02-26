package com.realtech.socialsurvey.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
import com.realtech.socialsurvey.core.entities.Plan;
import com.realtech.socialsurvey.core.entities.ProfileStage;
import com.realtech.socialsurvey.core.entities.RegistrationStage;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.payment.Payment;
import com.realtech.socialsurvey.core.services.social.SocialAsyncService;
import com.realtech.socialsurvey.core.services.social.SocialManagementService;
import com.realtech.socialsurvey.core.services.upload.FileUploadService;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.api.SSApiIntegration;
import com.realtech.socialsurvey.web.api.builder.SSApiIntergrationBuilder;
import com.realtech.socialsurvey.web.api.entities.AccountRegistrationAPIRequest;
import com.realtech.socialsurvey.web.api.entities.CaptchaAPIRequest;
import com.realtech.socialsurvey.web.api.exception.SSAPIException;
import com.realtech.socialsurvey.web.common.JspResolver;
import com.realtech.socialsurvey.web.common.TokenHandler;
import com.realtech.socialsurvey.web.entities.CompanyProfile;
import com.realtech.socialsurvey.web.ui.entities.AccountRegistration;
import com.realtech.socialsurvey.web.util.RequestUtils;

import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


/**
 * Typically used for account registration. The controller should not call
 * services directly but should call APIs
 */
@Controller
public class AccountWebController
{
    private static final Logger LOG = LoggerFactory.getLogger( AccountWebController.class );
    private static final String AGENT_UNIT = "agent";
    private static final String BRANCH_UNIT = "branch";
    private static final String REGION_UNIT = "region";
    private static final String COMPANY_UNIT = "company";

    private SSApiIntergrationBuilder apiBuilder;
    private RequestUtils requestUtils;
    private TokenHandler tokenHandler;
    private OrganizationManagementService organizationManagementService;
    private UserManagementService userManagementService;
    private SocialAsyncService socialAsyncService;
    private SocialManagementService socialManagementService;
    private Payment gateway;
    private FileUploadService fileUploadService;
    private ProfileManagementService profileManagementService;
    private MessageUtils messageUtils;
    private SessionHelper sessionHelper;

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

    @Value ( "${ENABLE_CAPTCHA}")
    private String enableCaptcha;
    
    private static final String V1 = "V2";

    @Autowired
    public AccountWebController( SSApiIntergrationBuilder apiBuilder, RequestUtils requestUtils, TokenHandler tokenHandler,
        OrganizationManagementService organizationManagementService, UserManagementService userManagementService,
        SocialAsyncService socialAsyncService, SocialManagementService socialManagementService, Payment gateway,
        FileUploadService fileUploadService, MessageUtils messageUtils, SessionHelper sessionHelper,
        ProfileManagementService profileManagementService )
    {
        this.apiBuilder = apiBuilder;
        this.requestUtils = requestUtils;
        this.tokenHandler = tokenHandler;
        this.organizationManagementService = organizationManagementService;
        this.userManagementService = userManagementService;
        this.socialAsyncService = socialAsyncService;
        this.socialManagementService = socialManagementService;
        this.gateway = gateway;
        this.fileUploadService = fileUploadService;
        this.messageUtils = messageUtils;
        this.sessionHelper = sessionHelper;
        this.profileManagementService = profileManagementService;
    }


    @RequestMapping ( value = "/registeraccount/initiateregistration", method = RequestMethod.POST)
    @ResponseBody
    public String initateAccountRegistration( @RequestBody AccountRegistration account, HttpServletRequest request )
    {
        LOG.info( "Registering user" );
        String responseString = null;
        SSApiIntegration api = apiBuilder.getIntegrationApi();

        // validate captcha
        if(enableCaptcha.equalsIgnoreCase( "Y" )){
            CaptchaAPIRequest captchaRequest = new CaptchaAPIRequest();
            captchaRequest.setRemoteAddress( request.getRemoteAddr() );
            captchaRequest.setCaptchaResponse( account.getCaptchaResponse() );
            api.validateCaptcha( captchaRequest );
        }
        
        // initiate registration
        AccountRegistrationAPIRequest accountRequest = new AccountRegistrationAPIRequest();
        accountRequest.setFirstName( account.getFirstName() );
        accountRequest.setLastName( account.getLastName() );
        accountRequest.setCompanyName( account.getCompanyName() );
        accountRequest.setEmail( account.getEmail() );
        accountRequest.setPhone( account.getPhone() );
        accountRequest.setPlanId( account.getPlanId() );
        Response response = api.initateRegistration( accountRequest );
        responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        return responseString;
    }


    @RequestMapping ( value = "/registeraccount/getcompanyprofile", method = RequestMethod.GET)
    @ResponseBody
    public String getCompanyProfile( @QueryParam ( "companyId") String companyId )
    {
        LOG.info( "Fetching company profile" );
        String responseString = null;
        SSApiIntegration api = apiBuilder.getIntegrationApi();
        Response response = api.getCompanyProfile( companyId );
        responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        return responseString;
    }


    @RequestMapping ( value = "/registeraccount/updatecompanyprofile", method = RequestMethod.PUT)
    @ResponseBody
    public String updateCompanyProfile( @QueryParam ( "companyId") String companyId, @QueryParam ( "userId") String userId,
        @QueryParam ( "stage") String stage, @RequestBody CompanyProfile companyProfile )
    {
        LOG.info( "updating company profile" );
        String responseString = null;
        SSApiIntegration api = apiBuilder.getIntegrationApi();
        Response response = api.updateCompanyProfile( companyProfile, companyId, userId );
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
        LOG.info( "Fetching verticals" );
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
        LOG.info( "Fetching payment plans" );
        String responseString = null;
        SSApiIntegration api = apiBuilder.getIntegrationApi();
        Response response = api.getPaymentPlans();
        responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        return responseString;
    }


    @RequestMapping ( value = "/registeraccount/getusstates", method = RequestMethod.GET)
    @ResponseBody
    public String getUsStates()
    {
        LOG.info( "Fetching states for US" );
        String responseString = null;
        SSApiIntegration api = apiBuilder.getIntegrationApi();
        Response response = api.getUsStates();
        responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        return responseString;
    }


    @RequestMapping ( value = "/registeraccount/getcompanystage", method = RequestMethod.GET)
    @ResponseBody
    public String getCompanyStage( @QueryParam ( "companyId") String companyId )
    {
        LOG.info( "Fetching company stage" );
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
        LOG.info( "Generating hierarchy for company" );
        String responseString = null;
        SSApiIntegration api = apiBuilder.getIntegrationApi();
        Response response = api.generateDefaultHierarchy( companyId );
        responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        return responseString;
    }


    @RequestMapping ( value = "/registeraccount/uploadcompanylogo", method = RequestMethod.POST)
    @ResponseBody
    public String uploadCompanyLogo( @QueryParam ( "companyId") String companyId, @QueryParam ( "userId") String userId,
        MultipartHttpServletRequest request ) throws InvalidInputException
    {
        LOG.info( "Uploading company logo" );
        String responseString = null;
        SSApiIntegration api = apiBuilder.getIntegrationApi();
        Iterator<String> itr = request.getFileNames();
        while ( itr.hasNext() ) {
            String uploadedFile = itr.next();
            MultipartFile file = request.getFile( uploadedFile );
            String logoUrl = fileUploadService.uploadLogo( file, file.getOriginalFilename() );
            logoUrl = amazonEndpoint + CommonConstants.FILE_SEPARATOR + amazonLogoBucket + CommonConstants.FILE_SEPARATOR
                + logoUrl;
            Response response = api.updateCompanyLogo( companyId, userId, logoUrl );
            responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        }
        return responseString;
    }


    @RequestMapping ( value = "/registeraccount/removecompanylogo", method = RequestMethod.PUT)
    @ResponseBody
    public String removeCompanyLogo( @QueryParam ( "companyId") String companyId, @QueryParam ( "userId") String userId )
    {
        LOG.info( "Removing company logo" );
        String responseString = null;
        SSApiIntegration api = apiBuilder.getIntegrationApi();
        Response response = api.removeCompanyLogo( companyId, userId );
        responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        return responseString;
    }


    @RequestMapping ( value = "/registeraccount/makepayment", method = RequestMethod.POST)
    @ResponseBody
    public String makePayment( @QueryParam ( "companyId") String companyId, @QueryParam ( "planId") String planId,
        @RequestBody com.realtech.socialsurvey.web.entities.Payment payment )
    {
        LOG.info( "making payment for company" );
        String responseString = null;
        SSApiIntegration api = apiBuilder.getIntegrationApi();
        Response response = api.makePayment( companyId, planId, payment );
        if ( response.getStatus() == HttpStatus.SC_OK ) {
            response = api.updateCompanyProfileStage( companyId, RegistrationStage.PAYMENT.getCode() );
            if ( response.getStatus() == HttpStatus.SC_OK ) {
                if ( Integer.parseInt( planId ) < Plan.ENTERPRISE.getPlanId() ) {
                    response = api.generateDefaultHierarchy( companyId );
                    if ( response.getStatus() == HttpStatus.SC_OK ) {
                        response = api.updateCompanyProfileStage( companyId, RegistrationStage.COMPLETE.getCode() );
                    }
                }
            }
            responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        }
        return responseString;
    }


    @RequestMapping ( value = "/registeraccount/newloginas")
    public String newLoginAs( @QueryParam ( "userId") String userId, @QueryParam ( "planId") String planId,
        RedirectAttributes redirectAttributes, HttpServletRequest request ) throws NumberFormatException, InvalidInputException
    {
        LOG.info( "New login for a user" );
        long userIdLong = Long.parseLong( userId );
        User user = userManagementService.getUserByUserId( userIdLong );
        if ( user.getCompany().getRegistrationStage().equalsIgnoreCase( RegistrationStage.PAYMENT.getCode() )
            && user.getCompany().getBillingMode().equalsIgnoreCase( CommonConstants.BILLING_MODE_INVOICE ) ) {
            redirectAttributes.addFlashAttribute( "message",
                messageUtils.getDisplayMessage( "ACCOUNT_IN_PROGRESS", DisplayMessageType.ERROR_MESSAGE ) );
            return JspResolver.LOGIN;
        } else if ( !user.getCompany().getRegistrationStage().equalsIgnoreCase( RegistrationStage.COMPLETE.getCode() ) ) {
            redirectAttributes.addFlashAttribute( "userId", user.getUserId() );
            redirectAttributes.addFlashAttribute( "companyId", user.getCompany().getCompanyId() );
            return "redirect:/accountsignupredirect.do?PlanId=" + planId;
        } else {
            sessionHelper.loginOnRegistration( user.getLoginName(), CommonConstants.BYPASS_PWD );
            return "redirect:/userlogin.do";
        }
    }


    @RequestMapping ( value = "/registeraccount/{organizationunit}/initlinkedinconnection", method = RequestMethod.POST)
    @ResponseBody
    public String initiateLinkedInConnection( @RequestBody String id,
        @PathVariable ( "organizationunit") String organizationunit, HttpServletRequest request ) throws JsonProcessingException
    {
        LOG.info( "Creating linkedin url" );
        String serverBaseUrl = requestUtils.getRequestServerName( request );
        StringBuilder linkedInAuth = new StringBuilder( linkedinAuthUri ).append( "?response_type=" ).append( "code" )
            .append( "&client_id=" ).append( linkedInApiKey ).append( "&redirect_uri=" ).append( serverBaseUrl )
            .append( linkedinRedirectUri ).append( "?unit-id=" ).append( organizationunit ).append( "-" ).append( id )
            .append( "&state=" ).append( "SOCIALSURVEY" ).append( "&scope=" ).append( linkedinScope );
        ObjectMapper mapper = new ObjectMapper();
        String jsonStr = mapper.writeValueAsString( linkedInAuth.toString() );
        LOG.debug( "LinkedIn Auth URL: {}", jsonStr );
        return jsonStr;
    }


    @RequestMapping ( value = "/registeraccount/connectlinkedin", method = RequestMethod.GET)
    public String connectToLinkedIn( HttpServletRequest request, RedirectAttributes attributes ) throws InvalidInputException
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
        LOG.debug( "Unit: {} and id: {}",unit, sId );
        // check if there is error
        String errorCode = request.getParameter( "error" );
        if ( errorCode != null ) {
            LOG.warn( "Error code : {}", errorCode );
            response = errorCode;
        } else {
            try {
                if ( sId != null && unit != null ) {
                    LOG.debug( "Authentication successful." );
                    // Getting Oauth access token for LinkedIn
                    String oauthCode = request.getParameter( "code" );
                    String url = requestUtils.getRequestServerName( request ) + linkedinRedirectUri;
                    List<NameValuePair> params = new ArrayList<NameValuePair>( 5 );
                    params.add( new BasicNameValuePair( "grant_type", "authorization_code" ) );
                    params.add( new BasicNameValuePair( "code", oauthCode ) );
                    params.add( new BasicNameValuePair( "redirect_uri",
                        requestUtils.getRequestServerName( request ) + linkedinRedirectUri + "?unit-id=" + unit_id ) );
                    params.add( new BasicNameValuePair( "client_id", linkedInApiKey ) );
                    params.add( new BasicNameValuePair( "client_secret", linkedInApiSecret ) );
                    LOG.debug( "oauthCode in param {}", oauthCode );
                    LOG.debug( "redirect in param {}?unit-id={}",url , unit_id );
                    LOG.debug( "linkedInApiKey in param {}", linkedInApiKey );
                    LOG.debug( "linkedInApiSecret in param {}", linkedInApiSecret );
                    LOG.debug( "linkedinAccessUri {}", linkedinAccessUri );

                    // fetching access token
                    HttpClient httpclient = HttpClientBuilder.create().build();
                    HttpPost httpPost = new HttpPost( linkedinAccessUri );
                    httpPost.setEntity( new UrlEncodedFormEntity( params, "UTF-8" ) );
                    String accessTokenStr = httpclient.execute( httpPost, new BasicResponseHandler() );
                    Map<String, Object> map = new Gson().fromJson( accessTokenStr,
                        new TypeToken<Map<String, String>>() {}.getType() );
                    String accessToken = (String) map.get( "access_token" );
                    String expiresInStr = (String) map.get( "expires_in" );
                    long expiresIn = 0;
                    if(StringUtils.isNotBlank( expiresInStr ))
                        expiresIn = Long.valueOf( expiresInStr ).longValue();

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
                        tokens = tokenHandler.updateLinkedInToken( accessToken, tokens, profileLink, expiresIn, V1 );
                        // update tokens
                        tokens = socialManagementService.updateSocialMediaTokens( CommonConstants.AGENT_SETTINGS_COLLECTION,
                            Long.parseLong( sId ), tokens );
                        // update LinkedIn data to settings
                        AgentSettings agentSettings = userManagementService.getUserSettings( Long.parseLong( sId ) );
                        agentSettings = (AgentSettings) socialAsyncService.linkedInDataUpdate(
                            MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings, tokens );

                        //Update profile stages
                        for ( ProfileStage stage : agentSettings.getProfileStages() ) {
                            if ( stage.getProfileStageKey().equalsIgnoreCase( "LINKEDIN_PRF" ) ) {
                                stage.setStatus( CommonConstants.STATUS_INACTIVE );
                            }
                        }
                        profileManagementService.updateProfileStages( agentSettings.getProfileStages(), agentSettings,
                            MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );

                        User user = userManagementService.getUserByUserId( Long.parseLong( sId ) );
                        attributes.addFlashAttribute( "userId", user.getUserId() );
                        attributes.addFlashAttribute( "companyId", user.getCompany().getCompanyId() );

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
                    LOG.warn( "Expecting id and unit from linkedin" );
                    throw new SSAPIException( "Could not fetch LinkedIn profile. Could not pass parameters " );
                }
            } catch ( IOException ioe ) {
                LOG.warn( "Found exception while accessing profile data {}", ioe.getMessage(), ioe );
                throw new SSAPIException( "Could not fetch LinkedIn profile. Reason: " + ioe.getMessage() );
            }
        }
        attributes.addFlashAttribute( "isLinkedin", true );
        attributes.addFlashAttribute( "linkedinResponse", response );

        return "redirect:/accountsignupredirect.do";
    }
}
