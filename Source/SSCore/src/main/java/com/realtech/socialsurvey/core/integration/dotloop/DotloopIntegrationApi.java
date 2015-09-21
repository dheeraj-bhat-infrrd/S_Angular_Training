package com.realtech.socialsurvey.core.integration.dotloop;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;


/**
 * API methods to integrate with Encompass
 *
 */
public interface DotloopIntegrationApi
{
    @GET ( "/profiles")
    public Response fetchdotloopProfiles( @Header ( "authorizationHeader") String authorizationHeader );


    @GET ( "/profile/{profile-id}/loop")
    public Response fetchZillowReviewsByScreennameWithMaxCount( @Path ( "profile-id") String profileId );

}
