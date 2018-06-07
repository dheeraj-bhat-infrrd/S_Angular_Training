package com.realtech.socialsurvey.core.api;

import java.sql.Timestamp;

import com.realtech.socialsurvey.core.entities.ftp.FtpUploadRequest;
import com.realtech.socialsurvey.core.entities.integration.stream.FailedStreamMessage;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;


public interface SSApiBatchIntegration
{

    @GET ( "/v1/getsurveystatsreport")
    Response getReportingSurveyStatsReport( @Query ( "entityId") Long entityId, @Query ( "entityType") String entityType );


    @GET ( "/v1/getuseradoptionreportsforreporting")
    Response getUserAdoption( @Query ( "entityId") Long entityId, @Query ( "entityType") String entityType );


    @GET ( "/v1/getcompanyuserreportsforreporting")
    Response getCompanyUserReport( @Query ( "entityId") Long entityId, @Query ( "entityType") String entityType );


    @GET ( "/v1/getsurveyresultsreport")
    Response getSurveyResultsReport( @Query ( "entityType") String entityType, @Query ( "entityId") Long entityId,
        @Query ( "startDate") Timestamp startDate, @Query ( "endDate") Timestamp endDate, @Query ( "startIndex") int startIndex,
        @Query ( "batchSize") int batchSize );


    @GET ( "/v1/getincompletesurveys")
    Response getIncompleteSurveyResultsReport( @Query ( "entityId") Long entityId, @Query ( "entityType") String entityType,
        @Query ( "startDate") Timestamp startDate, @Query ( "endDate") Timestamp endDate, @Query ( "startIndex") int startIndex,
        @Query ( "batchSize") int batchSize );


    @GET ( "/v1/getmaxquestionforcompany")
    Response getCompanyMaxQuestion( @Query ( "entityType") String entityType, @Query ( "entityId") Long entityId,
        @Query ( "startDate") Timestamp startDate, @Query ( "endDate") Timestamp endDate );


    //Survey Response api for testing. Not being used anywhere else
    @GET ( "/v1/getsurveyresponseforreporting")
    Response getsurveyresponseforreporting( @Query ( "surveyDetailsId") String surveyDetailsId );


    @GET ( "/v1/getsurveytransactionreportforreporting")
    Response getSurveyTransactionReport( @Query ( "entityId") Long entityId, @Query ( "entityType") String entityType,
        @Query ( "month") int month, @Query ( "year") int year );


    @GET ( "/v1/getuserrankingreportforreporting")
    Response getUserRankingReport( @Query ( "entityId") Long entityId, @Query ( "entityType") String entityType,
        @Query ( "year") int year, @Query ( "month") int month, @Query ( "type") int type );


    @GET ( "/v1/getentitiesoptedfordigestmail")
    Response getEntitiesOptedForDigestMail( @Query ( "profileLevel") String profileLevel, @Query ( "startIndex") int startIndex,
        @Query ( "batchSize") int batchSize );


    @GET ( "/v1/buildmonthlydigestaggregate")
    Response buildMonthlyDigestAggregate( @Query ( "profileLevel") String profileLevel, @Query ( "entityId") long entityId,
        @Query ( "entityName") String entityName, @Query ( "monthUnderConcern") int monthUnderConcern,
        @Query ( "year") int year );


    @GET ( "/v1/getcompanydetailsreport")
    Response getCompanyDetailsReport( @Query ( "entityType") String entityType, @Query ( "entityId") Long entityId,
        @Query ( "startIndex") int startIndex, @Query ( "batchSize") int batchSize );


    @GET ( "/v1/nps/week/month")
    Response getNpsReportForWeekOrMonth( @Query ( "week") int week, @Query ( "month") int month,
        @Query ( "companyId") long companyId, @Query ( "year") int year, @Query ( "type") int type );


    @GET ( "/v1/getallactiveenterprisecompanies")
    Response getAllActiveEnterpriseCompanies();


    @GET ( "/v1/getcompanieswithnotransactioninpastndays")
    Response getCompaniesWithNoTransactionInPastNDays( @Query ( "noOfDays") int noOfDays );


    @GET ( "/v1/validatesurveystatsforcompanies")
    Response validateSurveyStatsForCompanies();


    @GET ( "/v1/getsurveystatusstatsforpastonemonth")
    Response getSurveyStatusStatsForPastOneMonth();


    @GET ( "/v1/getcompanyactiveusercountforpastday")
    Response getCompanyActiveUserCountForPastDay();


    @GET ( "/v1/gettotaltransactioncountforpast5days")
    Response getTotalTransactionCountForPast5Days();


    @GET ( "/v1/gettransactioncountforpast3days")
    Response getTransactionCountForPast3Days();


    @GET ( "/v1/getsendsurveycountforpast5days")
    Response getSendSurveyCountForPast5Days();


    @GET ( "/v1/getsurvestatsforpast7daysforallcompanies")
    Response getSurveStatsForPast7daysForAllCompanies();


    @GET ( "/v1/getsurvestatsforlasttoLatweekforallcompanies")
    Response getSurveStatsForLastToLatWeekForAllCompanies();


    @GET ( "/v1/getcompletedsurveycountforpastndays")
    Response getCompletedSurveyCountForPastNDays();


    @GET ( "/v1/branchranking/month/year")
    Response getBranchRankingReport( @Query ( "companyId") long companyId, @Query ( "month") int month,
        @Query ( "year") int year, @Query ( "type") int type );


    @GET ( "/v1/emailreport/month/year")
    Response getSurveyInvitationEmailReport( @Query ( "companyId") long companyId, @Query ( "month") int month,
        @Query ( "year") int year );


    @GET ( "/v1/crm/ftp/info/company")
    Response getFtpConnections( @Query ( "status") String status, @Query ( "startIndex") int startIndex,
        @Query ( "batchSize") int batchSize );


    @POST ( "/v1/crm/ftp/stream/failed")
    Response saveFailedFtpStreamMessage( @Body FailedStreamMessage<FtpUploadRequest> failedStreamMessage );


    @DELETE ( "/v1/crm/ftp/stream/failed")
    Response deleteFailedFtpStreamMessage( @Query ( "failedStreamMessageId") String failedStreamMessageId );


    @GET ( "/v1/crm/ftp/stream/failed")
    Response getFailedFtpStreamMessage( @Query ( "messageClass") String messageClass, @Query ( "startIndex") int startIndex,
        @Query ( "batchSize") int batchSize );


    @GET ( "/v1/getftpcrm/{companyId}/{ftpId}")
    Response getFtpCrm( @Path ( "companyId") long companyId, @Path ( "ftpId") long ftpId );
    
    
    @POST ( "/v1/crm/ftp/stream/failed/{id}/update")
    Response updateRetryFailedForFailedFtpStreamMessage( @Path ( "id" ) String id, @Body Object dummy );
}
