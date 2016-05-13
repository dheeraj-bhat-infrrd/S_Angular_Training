package com.realtech.socialsurvey.web.api;

import com.realtech.socialsurvey.web.api.entities.CaptchaAPIRequest;
import retrofit.http.Body;
import retrofit.http.POST;


public interface SSApiIntegration
{
    @POST( "/utils/nocaptcha/validate" )
    void validateCaptcha(@Body CaptchaAPIRequest captchaRequest);
}
