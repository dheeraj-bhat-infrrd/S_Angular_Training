package com.realtech.socialsurvey.core.integration.pos;

import java.util.List;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import com.realtech.socialsurvey.core.entities.integration.EncompassCredentials;
import com.realtech.socialsurvey.core.entities.integration.EngagementProcessingStatus;
import com.realtech.socialsurvey.core.entities.integration.EngagementWrapper;

/**
 * API methods to integrate with Encompass
 *
 */
public interface EncompassIntegrationAPI {

	/**
	 * Gets the list of closed engagements
	 * @param startFrom
	 * @param numOfRecords
	 * @return
	 */
	@GET("/encompass/engagements/all/closed")
	public EngagementWrapper getClosedEngagements(@Query("startfrom") long startFrom, @Query("numofrecords") int numOfRecords);
	
	/**
	 * Sends a request to update Encompass Credentials
	 * @param credentials
	 * @return
	 */
	@POST("/encompass/credentials")
	public Response updateCredentials(@Body List<EncompassCredentials> credentials);
	
	/**
	 * Updates the status of process engagements
	 * @param statuses
	 * @return
	 */
	@POST("/encompass/engagmements/processes/response")
	public Response updateProcessingStatus(@Body List<EngagementProcessingStatus> statuses);
}
