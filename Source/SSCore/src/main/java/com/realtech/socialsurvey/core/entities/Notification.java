package com.realtech.socialsurvey.core.entities;

import com.realtech.socialsurvey.core.enums.NotificationType;


/**
 * @author Lavanya
 */

public class Notification
{
    private String message;
    private long recievedOn;
    private NotificationType notificationType;
    boolean isDisabled;

    public Notification()
    {
    }

    public Notification( String message, long recievedOn, NotificationType notificationType, boolean isDisabled )
    {
        this.message = message;
        this.recievedOn = recievedOn;
        this.notificationType = notificationType;
        this.isDisabled = isDisabled;
    }


    public String getMessage()
    {
        return message;
    }


    public void setMessage( String message )
    {
        this.message = message;
    }


    public long getRecievedOn()
    {
        return recievedOn;
    }


    public void setRecievedOn( long recievedOn )
    {
        this.recievedOn = recievedOn;
    }


    public NotificationType getNotificationType()
    {
        return notificationType;
    }


    public void setNotificationType( NotificationType notificationType )
    {
        this.notificationType = notificationType;
    }


    public boolean isDisabled()
    {
        return isDisabled;
    }


    public void setDisabled( boolean disabled )
    {
        isDisabled = disabled;
    }


    @Override public String toString()
    {
        return "Notification{" + "message='" + message + '\'' + ", recievedOn=" + recievedOn + ", notificationType="
            + notificationType + ", isDisabled=" + isDisabled + '}';
    }
}
