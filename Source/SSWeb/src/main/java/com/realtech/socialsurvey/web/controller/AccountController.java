package com.realtech.socialsurvey.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;

import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.realtech.socialsurvey.web.api.SSApiIntegration;
import com.realtech.socialsurvey.web.api.builder.SSApiIntergrationBuilder;
import com.realtech.socialsurvey.web.api.entities.AccountRegistrationAPIRequest;
import com.realtech.socialsurvey.web.api.entities.CaptchaAPIRequest;
import com.realtech.socialsurvey.web.entities.PersonalProfile;
import com.realtech.socialsurvey.web.ui.entities.AccountRegistration;

import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


/**
 * Typically used for account registration. The controller should not call services directly but should call APIs
 */
@Controller
public class AccountController
{
    private static final Logger LOG = LoggerFactory.getLogger( AccountController.class );

    @Autowired
    private SSApiIntergrationBuilder apiBuilder;


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
        Response response = api.updateUserProfile( userId, personalProfile );
        responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        if ( response.getStatus() == HttpStatus.SC_OK ) {
            api.updateUserProfileStage( userId, stage );
            responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        }
        return responseString;
    }
}
