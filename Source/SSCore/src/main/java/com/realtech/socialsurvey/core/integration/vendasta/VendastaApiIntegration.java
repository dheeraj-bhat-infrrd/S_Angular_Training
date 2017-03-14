package com.realtech.socialsurvey.core.integration.vendasta;


import java.util.Map;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Query;
import retrofit.http.QueryMap;


public interface VendastaApiIntegration
{
    @GET ( "/api/v2/account/get/")
    Response getAccountById( @Query ( "apiUser") String apiUser, @Query ( "apiKey") String apiKey,
        @Query ( "customerIdentifier") String customerIdentifier );


    @Headers ( { "Content-Type: application/json" })
    @POST ( "/api/v2/account/create/")
    Response createRmAccount( @QueryMap Map<String, String> queryParams, @Body Map<Object, Object> emptyMap );
}
