package com.realtech.socialsurvey.compute.services.api;

import com.realtech.socialsurvey.compute.entities.*;

import java.util.List;
import java.util.Map;

import com.realtech.socialsurvey.compute.entities.BulkSurveyPutVO;
import com.realtech.socialsurvey.compute.entities.FailedFtpRequest;
import com.realtech.socialsurvey.compute.entities.FileUploadResponse;
import com.realtech.socialsurvey.compute.entities.Keyword;
import com.realtech.socialsurvey.compute.entities.SocialMediaTokensPaginated;
import com.realtech.socialsurvey.compute.entities.TransactionSourceFtp;
import com.realtech.socialsurvey.compute.entities.response.BulkWriteErrorVO;
import com.realtech.socialsurvey.compute.entities.response.EntitySurveyStatsVO;
import com.realtech.socialsurvey.compute.entities.response.FtpSurveyResponse;
import com.realtech.socialsurvey.compute.entities.response.SocialResponseObject;
import com.realtech.socialsurvey.compute.entities.response.TwitterFeedData;
import com.realtech.socialsurvey.compute.entities.response.linkedin.LinkedinFeedData;
import com.realtech.socialsurvey.compute.entity.SurveyInvitationEmailCountMonth;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;


/**
 * Retrofit endpoint for SS api
 * @author manish
 *
 */
public interface SSApiIntegrationService
{
    @Headers ( "Content-Type: application/json")
    @GET ( "v1/companies/{companyId}/keywords")
    Call<List<Keyword>> getKeywordsForCompanyId( @Path ( "companyId") long companyId,
        @Header ( "authorizationHeader") String authorizationHeader );


    @Headers ( "Content-Type: application/json")
    @GET ( "v1/companies/mediaTokensPaginated")
    Call<SocialMediaTokensPaginated> getMediaTokensPaginated( @Query ( "skipCount") int skipCount,
        @Query ( "batchSize") int batchSize, @Header ( "authorizationHeader") String authorizationHeader );


    @Headers ( "Content-Type: application/json")
    @POST ( "v1/feeds")
    Call<SocialResponseObject> saveSocialFeed( @Body SocialResponseObject socialPostToMongo,
        @Header ( "authorizationHeader") String authorizationHeader );


    @Headers ( "Content-Type: application/json")
    @POST ( "v1/feeds")
    Call<SocialResponseObject<TwitterFeedData>> saveTwitterFeed( @Body SocialResponseObject<TwitterFeedData> socialPostToMongo,
        @Header ( "authorizationHeader") String authorizationHeader );


    @Headers ( "Content-Type: application/json")
    @POST ( "v1/feeds")
    Call<SocialResponseObject<LinkedinFeedData>> saveLinkedinFeed(
        @Body SocialResponseObject<LinkedinFeedData> socialPostToMongo,
        @Header ( "authorizationHeader") String authorizationHeader );


    @Headers ( "Content-Type: application/json")
    @PUT ( "v1/fileUpload/{fileUploadId}/status/{status}")
    Call<FileUploadResponse> updateFileUploadStatus( @Path ( "fileUploadId") long fileUploadId, @Path ( "status") int status );


    @Headers ( "Content-Type: application/json")
    @POST ( "v1/fileUpload/{fileUploadId}/status/{status}")
    Call<FileUploadResponse> updateFileUploadStatusAndLocation( @Path ( "fileUploadId") long fileUploadId,
        @Path ( "status") int status, @Body String fileName );


    @Headers ( "Content-Type: application/json")
    @PUT ( "v1/feeds/id/{id}/hash/{hash}/companyId/{companyId}")
    Call<Long> updateDuplicateCount( @Path ("hash") int hash, @Path ("companyId") long companyId,
        @Path( "id" ) String id, @Header ("authorizationHeader") String authorizationHeader );



    @GET ( "v1/trxcount/agent")
    Call<List<SurveyInvitationEmailCountMonth>> getReceivedCountsMonth( @Query ( "startDateInGmt") long startDate,
        @Query ( "endDateInGmt") long endDate, @Query ( "startIndex") int startIndex, @Query ( "batchSize") int batchSize,
        @Header ( "authorizationHeader") String authorizationHeader );


    @Headers ( "Content-Type: application/json")
    @POST ( "v1/agentEmailCountsMonth")
    Call<Boolean> saveEmailCountMonthData( @Body List<SurveyInvitationEmailCountMonth> agentEmailCountsMonth );


    @Headers ( "Content-Type: application/json")
    @GET ( "v1/surveyinvitationemailalltime")
    Call<List<SurveyInvitationEmailCountMonth>> getAllTimeDataForSurveyInvitationMail( @Query ( "startIndex") int startIndex,
        @Query ( "batchSize") int bATCH_SIZE );


    @Headers ( "Content-Type: application/json")
    @GET ( "v1/surveyinvitationemail/month/year")
    Call<List<SurveyInvitationEmailCountMonth>> getDataForEmailReport( @Query ( "month") int month, @Query ( "year") int year,
        @Query ( "companyId") long companyId );


    @Headers ( "Content-Type: application/json")
    @PUT ( "v1/updateSocialMediaToken/collection/{collection}/iden/{iden}/fieldtoupdate/{fieldtoupdate}/value/{value}")
    Call<Boolean> updateSocialMediaToken( @Path ( "iden") long iden, @Path ( "fieldtoupdate") String fieldToUpdate,
        @Path ( "value") boolean value, @Path ( "collection") String collection,
        @Header ( "authorizationHeader") String authHeader );

    
    
    @Headers ( "Content-Type: application/json")
    @GET ( "v1/socialFeed/companyId/{companyId}")
    Call<List<SocialResponseObject>> getSocialFeedData( @Path ( "companyId") long companyId, @Query ( "keyword") String keyword,
        @Query ( "startTime") long startTime, @Query ( "endTime") long endTime, @Query ( "pageSize") int pageSize,
        @Query ( "skips") int skips, @Header ( "authorizationHeader") String authHeader );


    @Headers ( "Content-Type: application/json")
    @GET ( "v1/socialFeedData/companyId/{companyId}")
    Call<List<SocialResponseObject>> getSocialFeedData( @Path ( "companyId") long companyId,
        @Query ( "startTime") long startTime, @Query ( "endTime") long endTime, @Query ( "pageSize") int pageSize,
        @Query ( "skips") int skips, @Header ( "authorizationHeader") String authHeader );

    @Headers ( "Content-Type: application/json")
    @GET ( "v1/getftpcrm/{companyId}/{ftpId}")
    Call<TransactionSourceFtp> getFtpCrm( @Path ( "companyId") long companyId, @Path ( "ftpId") long ftpId );


    @Headers ( "Content-Type: application/json")
    @PUT ( "v2/bulksurveys")
    Call<Map<String, Object>> postBulkSurveyTransactions( @Header ( "Authorization") String token,
        @Body BulkSurveyPutVO bulkSurveyPutVO );


    @Headers ( "Content-Type: application/json")
    @POST ( "v1/crm/ftp/storm/failed")
    Call<String> manageFtpFileProcessingFailure( @Body FailedFtpRequest failedFtpRequest );


    @Headers ( "Content-Type: application/json")
    @POST ( "v1/crm/ftp/complete/mail")
    Call<String> sendCompletionMail( @Query ( "companyId") long companyId, @Query ( "ftpId") long ftpId,
        @Query ( "s3FileLocation") String s3FileLocation, @Body FtpSurveyResponse ftpSurveyResponse );

    @Headers ( "Content-Type: application/json")
    @POST ( "v1/feeds/saveAndUpdate")
    Call<Long> savePostAndupdateDuplicateCount( @Body SocialResponseObject socialPost, @Header ( "authorizationHeader") String authHeader );


    @Headers ( "Content-Type: application/json")
    @GET ( "v1/unsubscribe/isunsubscribed")
    Call<Boolean> isEmailUnsubscribed(@Query("emailId") String recipient,@Query("companyId") long companyId );


    @GET ( "/v1//checkIfSurveyIsOld")
    Call<String> checkIfSurveyIsOld( @Query ( "customerEmailId") String customerEmailId );

    @Headers ( "Content-Type: application/json")
    @POST( "v1/feeds/bulk" )
    Call<List<BulkWriteErrorVO>> saveSocialFeeds( @Body List<SocialResponseObject> socialPosts, @Header ( "authorizationHeader") String authHeader );

    @Headers ( "Content-Type: application/json")
    @GET ( "v1/hierarchy/company/{identifier}/profilenames")
    Call<Map<String, Map<String, String>>> getProfileNameDateDataForWidgetReport( @Path ( "identifier") long companyId );
    
    @Headers ( "Content-Type: application/json")
    @GET ( "v1/widget/scripts")
    Call<List<String>> getWidgetScripts();

    @Headers ( "Content-Type: application/json")
    @GET ( "v1/fbTokensPaginated")
    Call<SocialMediaTokensPaginated> getFbTokensPaginated( @Query ( "skipCount") int skipCount,
        @Query ( "batchSize") int batchSize, @Header ( "authorizationHeader") String authorizationHeader );

    @Headers( "Content-Type: application/json" )
    @GET ( "v1/branch/{id}" )
    Call<BranchVO> getBranchDetails( @Path ("id") long id, @Header ( "authorizationHeader") String authHeader );

    @Headers( "Content-Type: application/json" )
    @GET( "v1/user/{id}" )
    Call<Map<String,Long>> findPrimaryUserProfileByAgentId(@Path( "id" ) long id, @Header ( "authorizationHeader") String authHeader );

    @Headers( "Content-Type: application/json" )
    @POST( "v2/reviews/bulk" )
    Call<List<BulkWriteErrorVO>> saveOrUpdateReviews( @Body List<SurveyDetailsVO> surveyDetails,
        @Header( "authorizationHeader" ) String authHeader );

    @GET ( "v1/organizationsettings/{placeId}/idens")
    Call<List<OrganizationUnitIds>> getDetailsFromPlaceId( @Path("placeId") String placeId );


    @Headers ( "Content-Type: application/json")
    @PUT ( "v1/updateSocialMediaLastFetched/profile/{profile}/iden/{iden}/socialMedia/{socialMedia}/current/{current}/previous/{previous}")
    Call<Boolean> updateSocialMediaLastFetched( @Path ("iden") long iden, @Path ("current") long current,
        @Path ("previous") long previous, @Path ("profile") String profile, @Path( "socialMedia" ) String socialMedia,
        @Header ("authorizationHeader") String authHeader
         );

    @Headers ( "Content-Type: application/json")
    @PUT("v1/resetSocialMediaLastFetched/profile/{profile}/iden/{iden}/socialMedia/{socialMedia}")
    Call<Boolean> resetSocialMediaLastFetched(@Path( "profile" ) String profile, @Path( "iden" ) long iden,
        @Path( "socialMedia" ) String socialMedia, @Header( "authorizationHeader" ) String authHeader );

    
    @Headers ( "Content-Type: application/json")
    @GET ( "v1/{entityType}/{entityId}/surveystats")
    Call<EntitySurveyStatsVO> getSurveyStatsForEntity(@Path( "entityType") String entityType, @Path("entityId") long entityId);
    
    @Headers ( "Content-Type: application/json")
    @POST ( "v1/{entityType}/{entityId}/surveystats")
    Call<Boolean> updateSurveyStatsForEntity(@Path( "entityType") String entityType, @Path("entityId") long entityId, @Body EntitySurveyStatsVO entitySurveyStatsVO);
}
