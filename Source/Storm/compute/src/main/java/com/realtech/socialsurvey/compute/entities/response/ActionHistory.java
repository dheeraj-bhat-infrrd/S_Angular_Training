package com.realtech.socialsurvey.compute.entities.response;

import com.realtech.socialsurvey.compute.enums.ActionHistoryType;
import com.realtech.socialsurvey.compute.enums.MessageType;

import java.io.Serializable;


/**
 * Action history for social post
 * @author manish
 *
 */
public class ActionHistory implements Serializable
{
    private static final long serialVersionUID = 1L;
    private long createdDate;
    private String ownerName;
    private String text;
    private String message;
    private MessageType messageType;
    private ActionHistoryType actionType;


    public String getOwnerName()
    {
        return ownerName;
    }


    public String getText()
    {
        return text;
    }

    public void setOwnerName( String ownerName )
    {
        this.ownerName = ownerName;
    }


    public void setText( String text )
    {
        this.text = text;
    }


    public ActionHistoryType getActionType()
    {
        return actionType;
    }


    public void setActionType( ActionHistoryType actionType )
    {
        this.actionType = actionType;
    }
    

    public long getCreatedDate()
    {
        return createdDate;
    }


    public void setCreatedDate( long createdDate )
    {
        this.createdDate = createdDate;
    }


    public String getMessage()
    {
        return message;
    }


    public void setMessage( String message )
    {
        this.message = message;
    }


    public MessageType getMessageType()
    {
        return messageType;
    }


    public void setMessageType( MessageType messageType )
    {
        this.messageType = messageType;
    }


    @Override public String toString()
    {
        return "ActionHistory{" + "createdDate=" + createdDate + ", ownerName='" + ownerName + '\'' + ", text='" + text + '\''
            + ", message='" + message + '\'' + ", messageType=" + messageType + ", actionType=" + actionType + '}';
    }


}
