package com.realtech.socialsurvey.core.integration.zillow;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;


/**
 * API methods to integrate with Zillow
 *
 */
public interface ZillowIntegrationApi
{


    @GET ( "/webservice/ProReviews.htm?output=json")
    public Response fetchZillowReviewsByScreenname( @Query ( "zws-id") String zwsId, @Query ( "screenname") String screenname );


    @GET ( "/webservice/ProReviews.htm?output=json&count=10")
    public Response fetchZillowReviewsByScreennameWithMaxCount( @Query ( "zws-id") String zwsId,
        @Query ( "screenname") String screenname );


    @GET ( "/webservice/ProReviews.htm?output=json")
    public Response fetchZillowReviewsByEmail( @Query ( "zws-id") String zwsId, @Query ( "email") String email );

}
