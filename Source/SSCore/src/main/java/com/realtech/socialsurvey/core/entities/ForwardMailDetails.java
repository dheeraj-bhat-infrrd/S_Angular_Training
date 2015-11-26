package com.realtech.socialsurvey.core.entities;

import java.util.Date;


public class ForwardMailDetails
{

    private String _id;
    private String senderMailId;
    private String recipientMailId;
    private String messageId;
    private Date createdOn;
    private String createdBy;
    private Date modifiedOn;
    private String modifiedBy;
    private int status;


    public String get_id()
    {
        return _id;
    }


    public void set_id( String _id )
    {
        this._id = _id;
    }


    public String getSenderMailId()
    {
        return senderMailId;
    }


    public void setSenderMailId( String senderMailId )
    {
        this.senderMailId = senderMailId;
    }


    public String getRecipientMailId()
    {
        return recipientMailId;
    }


    public void setRecipientMailId( String recipientMailId )
    {
        this.recipientMailId = recipientMailId;
    }


    public String getMessageId()
    {
        return messageId;
    }


    public void setMessageId( String messageId )
    {
        this.messageId = messageId;
    }


    public Date getCreatedOn()
    {
        return createdOn;
    }


    public void setCreatedOn( Date createdOn )
    {
        this.createdOn = createdOn;
    }


    public String getCreatedBy()
    {
        return createdBy;
    }


    public void setCreatedBy( String createdBy )
    {
        this.createdBy = createdBy;
    }


    public Date getModifiedOn()
    {
        return modifiedOn;
    }


    public void setModifiedOn( Date modifiedOn )
    {
        this.modifiedOn = modifiedOn;
    }


    public String getModifiedBy()
    {
        return modifiedBy;
    }


    public void setModifiedBy( String modifiedBy )
    {
        this.modifiedBy = modifiedBy;
    }


    public int getStatus()
    {
        return status;
    }


    public void setStatus( int status )
    {
        this.status = status;
    }


    @Override
    public String toString()
    {
        return "Forward Mail Details : [ senderMailId : " + senderMailId + " recipientMailId : " + recipientMailId
            + " messageId : " + messageId + "]";
    }
}
