package com.realtech.socialsurvey.core.integration.lonewolf;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Header;


public interface LoneWolfIntegrationApi
{
    public static final String loneWolfTestConnectionUrl = "/wolfconnect/transactions/v1?$top=1";
    public static final String loneWolfTransactionUrl = "/wolfconnect/transactions/v1?$top=1";
    public static final String loneWolfMemberUrl = "/wolfconnect/members/v1";


    @GET ( loneWolfTestConnectionUrl)
    public Response testConnection( @Header ( "Authorization") String authorizationHeader,
        @Header ( "Content-MD5") String md5Content );


    @GET ( loneWolfTransactionUrl)
    public Response fetchClosedTransactions( @Header ( "Authorization") String authorizationHeader,
        @Header ( "Content-MD5") String md5Content );


    @GET ( loneWolfMemberUrl)
    public Response fetchMemberDetails( @Header ( "Authorization") String authorizationHeader,
        @Header ( "Content-MD5") String md5Content );

}
