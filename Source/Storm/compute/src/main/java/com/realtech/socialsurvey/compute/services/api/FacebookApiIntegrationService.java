package com.realtech.socialsurvey.compute.services.api;

import com.realtech.socialsurvey.compute.entities.response.ConnectedInstagramAccount;
import com.realtech.socialsurvey.compute.entities.response.FacebookResponse;
import com.realtech.socialsurvey.compute.entities.response.InstagramMedia;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;


/**
 * Retrofit endpoint for SS api
 * @author manish
 *
 */
public interface FacebookApiIntegrationService
{
    @Headers ( "Content-Type: application/json")
    @GET ( "/{pageId}/feed?date_format=U")
    Call<FacebookResponse> fetchFeeds( @Path ( "pageId") String pageId, @Query ( "access_token") String accessToken,
                                       @Query ( "since") String since, @Query ( "until") String until, @Query ( "limit") String limit,
                                       @Query ( "__paging_token") String pagingToken, @Query ( "fields") String fields );

    @Headers( "Content-Type: application/json" )
    @GET ( "/{pageId}/media?date_format=U" )
    Call<InstagramMedia> fetchIgFeeds(@Path ( "pageId" ) String pageId, @Query ( "access_token") String accessToken,
                                      @Query( "fields" ) String fields, @Query("limit") String limit, @Query("after") String after );

    @Headers( "Content-Type: application/json" )
    @GET ( "/{pageId}/media?date_format=U" )
    Call<ConnectedInstagramAccount> fetchIgFeeds(@Path ( "pageId" ) String pageId, @Query ( "access_token") String accessToken,
                                                 @Query( "fields" ) String fields );
}
