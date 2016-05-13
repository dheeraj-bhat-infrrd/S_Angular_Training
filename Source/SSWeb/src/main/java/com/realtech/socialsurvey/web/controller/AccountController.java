package com.realtech.socialsurvey.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.realtech.socialsurvey.core.entities.api.AccountRegistration;
import com.realtech.socialsurvey.core.entities.api.Phone;
import com.realtech.socialsurvey.web.api.SSApiIntegration;
import com.realtech.socialsurvey.web.api.builder.SSApiIntergrationBuilder;
import com.realtech.socialsurvey.web.api.entities.AccountRegistrationAPIRequest;
import com.realtech.socialsurvey.web.api.entities.CaptchaAPIRequest;

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
}
