package com.realtech.socialsurvey.web.controller;

import com.realtech.socialsurvey.web.api.SSApiIntegration;
import com.realtech.socialsurvey.web.api.builder.SSApiIntergrationBuilder;
import com.realtech.socialsurvey.web.api.entities.CaptchaAPIRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

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

        SSApiIntegration api = apiBuilder.getIntegrationApi();
        // validate captcha
        CaptchaAPIRequest captchaRequest = new CaptchaAPIRequest();
        captchaRequest.setRemoteAddress( request.getRemoteAddr() );
        captchaRequest.setCaptchaResponse( request.getParameter( "g-recaptcha-response" ) );
        api.validateCaptcha( captchaRequest );

        return "{'status':'ok'}";
    }
}
