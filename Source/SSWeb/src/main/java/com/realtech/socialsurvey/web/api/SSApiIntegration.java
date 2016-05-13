package com.realtech.socialsurvey.web.api;

import com.realtech.socialsurvey.web.api.entities.AccountRegistrationAPIRequest;
import com.realtech.socialsurvey.web.api.entities.CaptchaAPIRequest;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;


public interface SSApiIntegration
{
    @POST( "/utils/nocaptcha/validate" )
    Response validateCaptcha(@Body CaptchaAPIRequest captchaRequest);

    @POST( "/account/register/init" )
    Response initateRegistration(@Body AccountRegistrationAPIRequest registrationRequest);
}
