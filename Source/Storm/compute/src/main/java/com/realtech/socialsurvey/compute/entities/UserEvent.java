package com.realtech.socialsurvey.compute.entities;

import java.io.Serializable;

import com.realtech.socialsurvey.compute.enums.EventType;


public class UserEvent implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String userEventId;
    private EventType eventType;
    private String event;
    private String entityType;
    private long entityId;
    private long userId;
    private long superAdminId;
    private long timestamp;


    public void setUserEventId( String userEventId )
    {
        this.userEventId = userEventId;
    }


    public String getUserEventId()
    {
        return userEventId;
    }


    public EventType getEventType()
    {
        return eventType;
    }


    public void setEventType( EventType eventType )
    {
        this.eventType = eventType;
    }


    public String getEvent()
    {
        return event;
    }


    public void setEvent( String event )
    {
        this.event = event;
    }


    public String getEntityType()
    {
        return entityType;
    }


    public void setEntityType( String entityType )
    {
        this.entityType = entityType;
    }


    public long getEntityId()
    {
        return entityId;
    }


    public void setEntityId( long entityId )
    {
        this.entityId = entityId;
    }


    public long getUserId()
    {
        return userId;
    }


    public void setUserId( long userId )
    {
        this.userId = userId;
    }


    public long getSuperAdminId()
    {
        return superAdminId;
    }


    public void setSuperAdminId( long superAdminId )
    {
        this.superAdminId = superAdminId;
    }


    public long getTimestamp()
    {
        return timestamp;
    }


    public void setTimestamp( long timestamp )
    {
        this.timestamp = timestamp;
    }


    @Override
    public String toString()
    {
        return "UserEvent [userEventId=" + userEventId + ", eventType=" + eventType + ", event=" + event + ", entityType="
            + entityType + ", entityId=" + entityId + ", userId=" + userId + ", superAdminId=" + superAdminId + ", timestamp="
            + timestamp + "]";
    }

}
