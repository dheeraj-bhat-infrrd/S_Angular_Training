package com.realtech.socialsurvey.compute.services.api;

import com.realtech.socialsurvey.compute.entities.response.linkedin.LinkedinFeedResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;


/**
 * Retrofit endpoint for linkedin api
 * @author manish
 *
 */
public interface LinkedinApiIntegrationService
{
    @Headers ( "Content-Type: application/json")
    @GET ( "companies/{lnCompanyId}/updates?format=json")
    Call<LinkedinFeedResponse> fetchFeeds( @Path ( "lnCompanyId") String lnCompanyId, @Query ( "start") int start,
        @Query ( "count") int count, @Query ( "event-type") String eventType,@Query ( "oauth2_access_token") String accessToken );
}
