package com.realtech.socialsurvey.core.integration.zillow;

import com.realtech.socialsurvey.core.commons.CommonConstants;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Query;


/**
 * API methods to integrate with Zillow
 *
 */
public interface ZillowIntegrationApi
{


    @GET ( "/webservice/ProReviews.htm?output=json")
    public Response fetchZillowReviewsByScreenname( @Query ( "zws-id") String zwsId, @Query ( "screenname") String screenname );


    @Headers({
        "Content-Type: application/json; charset=utf-8",
    })
    @GET ( "/" + CommonConstants.ZILLOW_CALL_REQUEST )
    public Response fetchZillowReviewsByScreennameWithMaxCount( @Query ( "zws-id") String zwsId,
        @Query ( "screenname") String screenname );


    @GET ( "/webservice/ProReviews.htm?output=json")
    public Response fetchZillowReviewsByEmail( @Query ( "zws-id") String zwsId, @Query ( "email") String email );

}
