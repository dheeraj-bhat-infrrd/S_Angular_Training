package com.realtech.socialsurvey.web.controller;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;

import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.Plan;
import com.realtech.socialsurvey.core.entities.RegistrationStage;
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

    private SSApiIntergrationBuilder apiBuilder;
    private UserManagementService userManagementService;
    private Payment gateway;
    private FileUploadService fileUploadService;
    private MessageUtils messageUtils;
    private SessionHelper sessionHelper;

    @Value ( "${AMAZON_LOGO_BUCKET}")
    private String amazonLogoBucket;

    @Value ( "${CDN_PATH}")
    private String amazonEndpoint;

    @Value ( "${ENABLE_CAPTCHA}")
    private String enableCaptcha;

    @Autowired
    public AccountWebController( SSApiIntergrationBuilder apiBuilder, RequestUtils requestUtils, TokenHandler tokenHandler,
        OrganizationManagementService organizationManagementService, UserManagementService userManagementService,
        SocialAsyncService socialAsyncService, SocialManagementService socialManagementService, Payment gateway,
        FileUploadService fileUploadService, MessageUtils messageUtils, SessionHelper sessionHelper,
        ProfileManagementService profileManagementService )
    {
        this.apiBuilder = apiBuilder;
        this.userManagementService = userManagementService;
        this.gateway = gateway;
        this.fileUploadService = fileUploadService;
        this.messageUtils = messageUtils;
        this.sessionHelper = sessionHelper;
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
    
}
