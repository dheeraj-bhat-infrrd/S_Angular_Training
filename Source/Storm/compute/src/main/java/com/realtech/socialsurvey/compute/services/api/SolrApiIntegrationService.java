package com.realtech.socialsurvey.compute.services.api;

import java.util.List;

import com.realtech.socialsurvey.compute.entities.SolrEmailMessageWrapper;
import com.realtech.socialsurvey.compute.entities.request.SolrRequest;
import com.realtech.socialsurvey.compute.entities.response.SOLRResponseObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Retrofit endpoint urls for SOLR
 * @author nishit
 *
 */
public interface SolrApiIntegrationService
{

    @Headers("Content-Type: application/json")
    @POST("ss-emails/update/json")
    Call<SOLRResponseObject<SolrEmailMessageWrapper>> postEmail(@Body SolrRequest<SolrEmailMessageWrapper> entity, @Query("commit") boolean commit);
    
    @Headers("Content-Type: application/json")
    @GET("ss-emails/select")
    Call<SOLRResponseObject<SolrEmailMessageWrapper>> getEmailMessage(@Query("q") String query, @Query("fq") String fieldQuery, @Query("start") int start, @Query("rows") int rows, @Query("wt") String wt);

    @Headers("Content-Type: application/json")
    @GET("ss-emails/select")
    Call<SOLRResponseObject<SolrEmailMessageWrapper>> getEmailMessage(@Query("q") String query, @Query("fq") String fieldQuery, @Query("fl") String fieldList,
                                                                      @Query("start") int start, @Query("rows") int rows, @Query("wt") String wt);

    @Headers("Content-Type: application/json")
    @GET("ss-emails/select")
    Call<String> getEmailCounts(@Query("q") String query, @Query("rows") int rows,@Query("fq") String fieldQuery, @Query("wt") String wt,
    		@Query("facet")String facet, @Query("facet.field")String facetField, @Query("facet.pivot") List<String> facetPivot);
}
