package com.realtech.socialsurvey.compute.services.api;

import java.util.List;

import com.realtech.socialsurvey.compute.entities.FileUploadResponse;
import com.realtech.socialsurvey.compute.entities.Keyword;
import com.realtech.socialsurvey.compute.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.compute.entities.SocialMediaTokensPaginated;
import com.realtech.socialsurvey.compute.entities.response.SocialResponseObject;
import com.realtech.socialsurvey.compute.entities.response.TwitterFeedData;
import com.realtech.socialsurvey.compute.entities.response.linkedin.LinkedinFeedData;
import com.realtech.socialsurvey.compute.entity.SurveyInvitationEmailCountMonth;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
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
    Call<List<Keyword>> getKeywordsForCompanyId( @Path ( "companyId") long companyId );


    @Headers ( "Content-Type: application/json")
    @GET ( "v1/companies/mediatokens")
    Call<List<SocialMediaTokenResponse>> getMediaTokens();
    
    @Headers ( "Content-Type: application/json")
    @GET ( "v1/companies/mediaTokensPaginated")
    Call<SocialMediaTokensPaginated> getMediaTokensPaginated(@Query ( "skipCount") int skipCount,@Query ( "batchSize") int batchSize);

    @Headers ( "Content-Type: application/json")
    @POST ( "v1/feeds")
    Call<SocialResponseObject> saveSocialFeed( @Body SocialResponseObject socialPostToMongo );


    @Headers ( "Content-Type: application/json")
    @POST ( "v1/feeds")
    Call<SocialResponseObject<TwitterFeedData>> saveTwitterFeed(
        @Body SocialResponseObject<TwitterFeedData> socialPostToMongo );


    @Headers ( "Content-Type: application/json")
    @POST ( "v1/feeds")
    Call<SocialResponseObject<LinkedinFeedData>> saveLinkedinFeed(
        @Body SocialResponseObject<LinkedinFeedData> socialPostToMongo );


    @Headers ( "Content-Type: application/json")
    @PUT ( "v1/fileUpload/{fileUploadId}/status/{status}")
    Call<FileUploadResponse> updateFileUploadStatus( @Path ( "fileUploadId") long fileUploadId, @Path ( "status") int status );


    @Headers ( "Content-Type: application/json")
    @POST ( "v1/fileUpload/{fileUploadId}/status/{status}")
    Call<FileUploadResponse> updateFileUploadStatusAndLocation( @Path ( "fileUploadId") long fileUploadId,
        @Path ( "status") int status, @Body String fileName );


    @Headers ( "Content-Type: application/json")
    @PUT ( "v1/feeds/hash/{hash}/companyId/{companyId}")
    Call<Long> updateDuplicateCount( @Path ( "hash") int hash, @Path ( "companyId") long companyId );


    @GET ( "v1/trxcount/agent")
    Call<List<SurveyInvitationEmailCountMonth>> getReceivedCountsMonth( @Query ( "startDateInGmt") long startDate,
        @Query ( "endDateInGmt") long endDate, @Query ( "startIndex") int startIndex, @Query ( "batchSize") int batchSize );


    @Headers ( "Content-Type: application/json")
    @POST ( "v1/agentEmailCountsMonth")
    Call<Boolean> saveEmailCountMonthData( @Body List<SurveyInvitationEmailCountMonth> agentEmailCountsMonth );


    @Headers ( "Content-Type: application/json")
    @GET ( "v1/surveyinvitationemailalltime")
	Call<List<SurveyInvitationEmailCountMonth>> getAllTimeDataForSurveyInvitationMail(@Query("startIndex") int startIndex,
			@Query ("batchSize")int bATCH_SIZE);


    @Headers ( "Content-Type: application/json")
    @GET ( "v1/surveyinvitationemail/month/year")
	Call<List<SurveyInvitationEmailCountMonth>> getDataForEmailReport(@Query("month") int month,@Query("year") int year,
			@Query("companyId")  long companyId);

}
