package com.realtech.socialsurvey.core.api;

import java.sql.Timestamp;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;


public interface SSApiBatchIntegration
{

    @GET ( "/v1/getsurveystatsreport")
    Response getReportingSurveyStatsReport( @Query ( "entityId") Long entityId, @Query ( "entityType") String entityType );


    @GET ( "/v1/getuseradoptionreportsforreporting")
    Response getUserAdoption( @Query ( "entityId") Long entityId, @Query ( "entityType") String entityType );


    @GET ( "/v1/getcompanyuserreportsforreporting")
    Response getCompanyUserReport( @Query ( "entityId") Long entityId, @Query ( "entityType") String entityType );


    @GET ( "/v1/getsurveyresultscompanyreportsforreporting")
    Response getSurveyResultsCompany( @Query ( "entityId") Long entityId, @Query ( "entityType") String entityType,
        @Query ( "startDate") Timestamp startDate, @Query ( "endDate") Timestamp endDate );


    //Survey Response api for testing. Not being used anywhere else
    @GET ( "/v1/getsurveyresponseforreporting")
    Response getsurveyresponseforreporting( @Query ( "surveyDetailsId") String surveyDetailsId );


    @GET ( "/v1/getsurveytransactionreportforreporting")
    Response getSurveyTransactionReport( @Query ( "entityId") Long entityId, @Query ( "entityType") String entityType,
        @Query ( "startDate") Timestamp startDate, @Query ( "endDate") Timestamp endDate );


    @GET ( "/v1/getuserrankingreportforreporting")
    Response getUserRankingReport( @Query ( "entityId") Long entityId, @Query ( "entityType") String entityType,
        @Query ( "year") int year, @Query ( "month") int month, @Query ( "type") int type );


    @GET ( "/v1/getcompaniesoptedfordigestmail")
    Response getCompaniesOptedForDigestMail( @Query ( "startIndex") int startIndex, @Query ( "batchSize") int batchSize );


    @GET ( "/v1/buildmonthlydigestaggregate")
    Response buildMonthlyDigestAggregate( @Query ( "companyId") long companyId, @Query ( "companyName") String companyName,
        @Query ( "monthUnderConcern") int monthUnderConcern, @Query ( "year") int year,
        @Query ( "recipientMail") String recipientMail );

}
