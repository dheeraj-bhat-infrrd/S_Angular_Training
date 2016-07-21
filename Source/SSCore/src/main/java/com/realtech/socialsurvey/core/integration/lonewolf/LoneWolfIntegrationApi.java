package com.realtech.socialsurvey.core.integration.lonewolf;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Header;


public interface LoneWolfIntegrationApi
{
    @GET ( "/wolfconnect/transactions/v1?$filter=StatusCode eq 'A'")
    public Response fetchClosedTransactions( @Header ( "Authorization") String authorizationHeader,
        @Header ( "Content-MD5") String md5Content );
    
    @GET ( "/wolfconnect/transactions/v1/test")
    public Response testConnection( @Header ( "Authorization") String authorizationHeader,
        @Header ( "Content-MD5") String md5Content );

}
