package com.realtech.socialsurvey.compute.services.api;

import java.util.List;

import com.realtech.socialsurvey.compute.entities.FileUploadResponse;
import com.realtech.socialsurvey.compute.entities.Keyword;
import com.realtech.socialsurvey.compute.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.compute.entities.response.FacebookFeedData;
import com.realtech.socialsurvey.compute.entities.response.SocialResponseObject;
import com.realtech.socialsurvey.compute.entities.response.TwitterFeedData;
import com.realtech.socialsurvey.compute.entities.response.linkedin.LinkedinFeedData;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;


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
    @POST ( "v1/feeds")
    Call<SocialResponseObject<FacebookFeedData>> saveSocialFeed(
        @Body SocialResponseObject<FacebookFeedData> socialPostToMongo );


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


    @GET ( "v1/feeds/hash/{hash}/companyId/{companyId}")
    Call<Long> getDuplicateCount( @Path ( "hash") int hash, @Path ( "companyId") long companyId );


    @Headers ( "Content-Type: application/json")
    @PUT ( "v1/feeds/hash/{hash}/companyId/{companyId}/duplicateCount/{duplicateCount}")
    Call<Long> updateDuplicateCount( @Path ( "hash") int hash, @Path ( "companyId") long companyId,
        @Path ( "duplicateCount") long duplicateCount );
}
