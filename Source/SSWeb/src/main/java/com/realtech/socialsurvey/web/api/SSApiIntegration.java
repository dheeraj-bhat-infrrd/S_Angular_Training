package com.realtech.socialsurvey.web.api;

import com.realtech.socialsurvey.web.api.entities.AccountRegistrationAPIRequest;
import com.realtech.socialsurvey.web.api.entities.CaptchaAPIRequest;
import com.realtech.socialsurvey.web.entities.PersonalProfile;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;


public interface SSApiIntegration
{
    @POST ( "/utils/nocaptcha/validate")
    Response validateCaptcha( @Body CaptchaAPIRequest captchaRequest );


    @POST ( "/account/register/init")
    Response initateRegistration( @Body AccountRegistrationAPIRequest registrationRequest );


    @GET ( "/users/profile/details/{userId}")
    Response getUserProfile( @Path ( "userId") String userId );


    @PUT ( "/users/profile/update/{userId}")
    Response updateUserProfile( @Path ( "userId") String userId, @Body PersonalProfile personalProfile );


    @PUT ( "/users/profile/stage/update/{userId}/{stage}")
    Response updateUserProfileStage( @Path ( "userId") String userId, @Path ( "stage") String stage );
}
