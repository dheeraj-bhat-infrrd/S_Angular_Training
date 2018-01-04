package com.realtech.socialsurvey.core.integration.sendgrid;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public interface SendgridIntegrationApi
{
    
    @POST ( "/unsubscribes.add.json")
    public Response unsubscribeEmail( @Query ( "api_user") String apiUser, @Query ( "api_key") String apiKey, @Query("email") String email );
    
    @POST ( "/unsubscribes.delete.json")
    public Response resubscribeEmail( @Query ( "api_user") String apiUser, @Query ( "api_key") String apiKey, @Query("email") String email );
    
    @GET ( "/unsubscribes.get.json")
    public Response getUnsubscribeEmails( @Query ( "api_user") String apiUser, @Query ( "api_key") String apiKey, @Query("date") int date );

}
