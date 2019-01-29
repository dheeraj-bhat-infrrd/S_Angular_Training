package com.realtech.socialsurvey.api.models.request;

import java.io.Serializable;


/**
 * @author Lavanya
 */

public class NotificationRequest implements Serializable
{
    private long companyId;
    private String message;
    private String type;
    private long receivedOn;


    public long getCompanyId()
    {
        return companyId;
    }


    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }


    public String getMessage()
    {
        return message;
    }


    public void setMessage( String message )
    {
        this.message = message;
    }


    public String getType()
    {
        return type;
    }


    public void setType( String type )
    {
        this.type = type;
    }


    public long getReceivedOn()
    {
        return receivedOn;
    }


    public void setReceivedOn( long receivedOn )
    {
        this.receivedOn = receivedOn;
    }


    @Override public String toString()
    {
        return "NotificationRequest{" + "companyId=" + companyId + ", message='" + message + '\'' + ", type='" + type
            + '\'' + ", receivedOn=" + receivedOn + '}';
    }
}
