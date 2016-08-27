package com.realtech.socialsurvey.core.integration.lonewolf;

import com.realtech.socialsurvey.core.commons.CommonConstants;

import retrofit.client.Response;
import retrofit.http.EncodedQuery;
import retrofit.http.GET;
import retrofit.http.Header;


public interface LoneWolfIntegrationApi
{
    public static final String loneWolfTestConnectionUrl = "/wolfconnect/transactions/v1?$top=1";
    //public static final String loneWolfTransactionUrl = "/wolfconnect/transactions/v1?$skip=0&$filter=StatusCode+eq+'A'+and+CloseDate+ge+datetimeoffset'2015-03-17'+and+CloseDate+lt+datetimeoffset'2015-07-18'";
    public static final String loneWolfTransactionUrl = "/wolfconnect/transactions/v1";
    public static final String loneWolfMemberUrl = "/wolfconnect/members/v1";


    @GET ( loneWolfTestConnectionUrl)
    public Response testConnection( @Header ( "Authorization") String authorizationHeader,
        @Header ( "Content-MD5") String md5Content );


    @GET ( loneWolfTransactionUrl)
    public Response fetchClosedTransactions( @Header ( "Authorization") String authorizationHeader,
        @Header ( "Content-MD5") String md5Content , @EncodedQuery (CommonConstants.LONEWOLF_QUERY_PARAM_$TOP) String top ,  @EncodedQuery (CommonConstants.LONEWOLF_QUERY_PARAM_$FILTER) String filter , @EncodedQuery (CommonConstants.LONEWOLF_QUERY_PARAM_$ORDERBY) String OrderBy , @EncodedQuery (CommonConstants.LONEWOLF_QUERY_PARAM_$SKIP) String skip  );


    @GET ( loneWolfMemberUrl)
    public Response fetchMemberDetails( @Header ( "Authorization") String authorizationHeader,
        @Header ( "Content-MD5") String md5Content );

}
