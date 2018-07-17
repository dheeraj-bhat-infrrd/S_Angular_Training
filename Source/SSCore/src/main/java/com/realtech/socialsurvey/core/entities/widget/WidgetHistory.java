package com.realtech.socialsurvey.core.entities.widget;

import java.util.Map;


public class WidgetHistory
{
    private Map<String, String> changes;
    private String requestMessage;
    private long timestamp;
    private long userId;


    public Map<String, String> getChanges()
    {
        return changes;
    }


    public void setChanges( Map<String, String> changes )
    {
        this.changes = changes;
    }


    public long getTimestamp()
    {
        return timestamp;
    }


    public void setTimestamp( long timestamp )
    {
        this.timestamp = timestamp;
    }


    public long getUserId()
    {
        return userId;
    }


    public void setUserId( long userId )
    {
        this.userId = userId;
    }


    public String getRequestMessage()
    {
        return requestMessage;
    }


    public void setRequestMessage( String message )
    {
        this.requestMessage = message;
    }


    @Override
    public String toString()
    {
        return "WidgetHistory [changes=" + changes + ", message=" + requestMessage + ", timestamp=" + timestamp + ", userId=" + userId
            + "]";
    }


}
