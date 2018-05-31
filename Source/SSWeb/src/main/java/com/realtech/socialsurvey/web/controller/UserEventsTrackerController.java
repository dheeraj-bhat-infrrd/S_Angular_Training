package com.realtech.socialsurvey.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserEvent;
import com.realtech.socialsurvey.core.enums.EventType;
import com.realtech.socialsurvey.core.integration.stream.StreamApiConnectException;
import com.realtech.socialsurvey.core.integration.stream.StreamApiException;
import com.realtech.socialsurvey.core.integration.stream.StreamApiIntegrationBuilder;

import retrofit.client.Response;


@Controller
@RequestMapping ( value = "/user/trackedevents")
public class UserEventsTrackerController
{
    public static final Logger LOG = LoggerFactory.getLogger( UserEventsTrackerController.class );

    @Autowired
    private StreamApiIntegrationBuilder streamApiIntegrationBuilder;

    @Autowired
    private SessionHelper sessionHelper;


    @RequestMapping ( "/click")
    @ResponseBody
    public String submitClickedEvents( HttpServletRequest request )
    {
        LOG.debug( "Method submitClickedEvents() started" );
        User user = sessionHelper.getCurrentUser();
        HttpSession session = request.getSession( false );

        String message = null;

        if ( user == null || session == null ) {
            LOG.warn( "No session or user information found" );
            message = "unable to submit click event, user session not found";
        } else {

            String clickedEventStr = request.getParameter( "event" );

            if ( StringUtils.isEmpty( clickedEventStr ) ) {
                LOG.warn( "user click event information not found" );
                message = "unable to submit click event, no click event specified";
            } else {

                try {

                    long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
                    String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
                    Long realtechAdminId = (Long) session.getAttribute( CommonConstants.REALTECH_USER_ID );

                    UserEvent clickedEvent = new UserEvent( EventType.CLICK );
                    clickedEvent.setEntityType( entityType );
                    clickedEvent.setEntityId( entityId );
                    clickedEvent.setUserId( user.getUserId() );
                    clickedEvent.setEvent( clickedEventStr );
                    clickedEvent.setTimestamp( System.currentTimeMillis() );
                    clickedEvent.setSuperAdminId( realtechAdminId != null ? realtechAdminId : 0l );
                    
                    LOG.info( "UserClickedEvent : {}", clickedEvent );

                    /* Response response = streamApiIntegrationBuilder.getStreamApi().submitUserEvent( clickedEvent );

                    if ( response != null && response.getStatus() != HttpStatus.CREATED.value() ) {
                        message = "unable to submit click event, stream API server error";
                    } else {
                        message = "User click event submitted successfully";
                    } */

                } catch ( StreamApiException | StreamApiConnectException streamApiError ) {
                    LOG.warn( "stream api error while submitting click event", streamApiError );
                    message = "unable to submit click event, stream API server error";
                }
            }
        }

        LOG.debug( "Method submitClickedEvents() finished" );
        return message;
    }

}