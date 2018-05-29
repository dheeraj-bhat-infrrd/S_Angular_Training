package com.realtech.socialsurvey.core.integration.stream;

import java.util.List;

import com.realtech.socialsurvey.core.entities.EmailEntity;
import com.realtech.socialsurvey.core.entities.ReportRequest;
import com.realtech.socialsurvey.core.entities.SendGridEventEntity;
import com.realtech.socialsurvey.core.entities.UserEvent;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;


/**
 * Stream api end points
 * @author nishit
 *
 */
public interface StreamApi
{
    @POST ( "/api/v1/stream/mail")
    public Response streamEmailMessage( @Body EmailEntity emailEntity );


    @POST ( "/api/v1/stream/sendgrid/events")
    public Response streamClickEvent( @Body List<SendGridEventEntity> sendGridEventEntity );


    @POST ( "/api/v1/stream/report")
    public Response generateEmailReport( @Body ReportRequest reportRequest );


    @POST ( "/api/v1/stream/batch")
    public Response triggerBatch( @Body ReportRequest reportRequest );
    
    
    @GET("/api/v1/analyze/failed/socialposts")
    public Response queueFailedSocialFeeds();
    

    @POST ( "/api/v1/stream/userevent")
    public Response submitUserEvent( @Header ("Authorization")  String streamApiAccessKey, @Body UserEvent userEvent );
    
}
