package com.realtech.socialsurvey.compute.services.api;

import com.realtech.socialsurvey.compute.entities.FileUploadResponse;
import com.realtech.socialsurvey.compute.entities.Keyword;
import com.realtech.socialsurvey.compute.entities.SocialPost;
import com.realtech.socialsurvey.compute.entity.SurveyInvitationEmailCountMonth;

import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;


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
    @POST ( "v1/feeds")
    Call<SocialPost> saveSocialFeed( @Body SocialPost socialPostToMongo );

    @Headers ( "Content-Type: application/json")
    @PUT( "v1/fileUpload/{fileUploadId}/status/{status}" )
    Call<FileUploadResponse> updateFileUploadStatus(@Path("fileUploadId") long fileUploadId,
                                                    @Path("status") int status);

    @Headers ( "Content-Type: application/json")
    @POST( "v1/fileUpload/{fileUploadId}/status/{status}" )
    Call<FileUploadResponse> updateFileUploadStatusAndLocation(@Path("fileUploadId") long fileUploadId,
                                                               @Path("status") int status,
                                                               @Body String fileName);


    @Headers ( "Content-Type: application/json")
    @GET ( "v1/trxcount/agent")
	Call<List<SurveyInvitationEmailCountMonth>> getReceivedCountsMonth(@Query("startDateInGmt") long startDate,
			@Query("endDateInGmt") long endDate);


    @Headers ( "Content-Type: application/json")
    @POST ( "v1/agentEmailCountsMonth")
	Call<Boolean> saveEmailCountMonthData(
			@Body List<SurveyInvitationEmailCountMonth> agentEmailCountsMonth);

}
