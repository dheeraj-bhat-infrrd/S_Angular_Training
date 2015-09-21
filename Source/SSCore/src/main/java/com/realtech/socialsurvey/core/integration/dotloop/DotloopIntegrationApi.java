package com.realtech.socialsurvey.core.integration.dotloop;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Header;


/**
 * API methods to integrate with Encompass
 *
 */
public interface DotloopIntegrationApi
{
    @GET ( "/profiles")
    public Response fetchdotloopProfiles( @Header ( "authorizationHeader") String authorizationHeader );
}
