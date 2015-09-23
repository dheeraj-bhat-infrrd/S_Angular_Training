package com.realtech.socialsurvey.core.integration.dotloop;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;
import retrofit.http.Query;


/**
 * API methods to integrate with Encompass
 *
 */
public interface DotloopIntegrationApi
{
    @GET ( "/profile")
    public Response fetchdotloopProfiles( @Header ( "Authorization") String authorizationHeader );


    @GET ( "/profile/{profile-id}/loop?statusIds=4")
    public Response fetchClosedProfiles( @Header ( "Authorization") String authorizationHeader,
        @Path ( "profile-id") String profileId, @Query ( "batchNumber") int batchNumber );

}
