package com.realtech.socialsurvey.core.integration.vendasta;


import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;


public interface VendastaApiIntegration
{
    @GET ( "/api/v2/account/get/")
    Response getAccountById( @Query ( "apiUser") String apiUser, @Query ( "apiKey") String apiKey,
        @Query ( "customerIdentifier") String customerIdentifier );
}
