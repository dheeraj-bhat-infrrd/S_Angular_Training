package com.realtech.socialsurvey.web.controller;

import com.realtech.socialsurvey.core.entities.api.Phone;
import com.realtech.socialsurvey.web.api.SSApiIntegration;
import com.realtech.socialsurvey.web.api.builder.SSApiIntergrationBuilder;
import com.realtech.socialsurvey.web.api.entities.AccountRegistrationAPIRequest;
import com.realtech.socialsurvey.web.api.entities.CaptchaAPIRequest;
import com.realtech.socialsurvey.web.api.exception.SSAPIException;
import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

import javax.servlet.http.HttpServletRequest;


/**
 * Typically used for account registration. The controller should not call services directly but should call APIs
 */
@Controller
public class AccountController
{
    private static final Logger LOG = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private SSApiIntergrationBuilder apiBuilder;

    @RequestMapping(value = "/registeraccount/initiateregistration", method = RequestMethod.POST)
    @ResponseBody
    public String initateAccountRegistration(HttpServletRequest request){
        LOG.info( "Registering user" );
        String responseString = null;
        SSApiIntegration api = apiBuilder.getIntegrationApi();

        // validate captcha
        CaptchaAPIRequest captchaRequest = new CaptchaAPIRequest();
        captchaRequest.setRemoteAddress( request.getRemoteAddr() );
        captchaRequest.setCaptchaResponse( request.getParameter( "g-recaptcha-response" ) );

        // TODO: Uncomment once captcha is fixed
        // api.validateCaptcha( captchaRequest );

        // initiate registration
        AccountRegistrationAPIRequest accountRequest = new AccountRegistrationAPIRequest();
        accountRequest.setFirstName( "Nishit" );
        accountRequest.setLastName( "Kannan" );
        accountRequest.setCompanyName( "Rare Mile" );
        accountRequest.setEmail( "nishit+"+System.currentTimeMillis()+"@raremile.com" );
        Phone phone = new Phone();
        phone.setCountryCode( "+91" );
        phone.setNumber( "1234567890" );
        phone.setExtension( "1234" );
        accountRequest.setPhone( phone );

        Response response = api.initateRegistration( accountRequest );
        responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );

        return responseString;
    }
}
