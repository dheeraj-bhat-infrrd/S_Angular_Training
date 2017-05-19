package com.realtech.socialsurvey.core.integration.zillow;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;

public interface ZillowIntegrationLenderApi
{
    
    @POST( "/getPublishedLenderReviews")
    public Response fetchZillowReviewsByLenderId( @Body FetchZillowReviewBody fetchZillowReviewBody );
    
    @POST( "/getPublishedLenderReviews")
    public Response fetchZillowReviewsByNMLS( @Body FetchZillowReviewBodyByNMLS fetchZillowReviewBodyByNMLS );

}
