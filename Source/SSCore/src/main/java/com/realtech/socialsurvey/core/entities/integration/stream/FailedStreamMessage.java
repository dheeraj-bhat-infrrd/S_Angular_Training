package com.realtech.socialsurvey.core.entities.integration.stream;

import java.io.Serializable;

import org.springframework.data.annotation.Id;


public class FailedStreamMessage<T> implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    private long failedDate;
    private long companyId;
    private boolean streamRetryFailed;
    private String messageClass;
    private T message;


    public String getId()
    {
        return id;
    }


    public void setId( String id )
    {
        this.id = id;
    }


    public long getFailedDate()
    {
        return failedDate;
    }


    public void setFailedDate( long failedDate )
    {
        this.failedDate = failedDate;
    }


    public long getCompanyId()
    {
        return companyId;
    }


    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }


    public boolean isStreamRetryFailed()
    {
        return streamRetryFailed;
    }


    public void setStreamRetryFailed( boolean streamRetryFailed )
    {
        this.streamRetryFailed = streamRetryFailed;
    }


    public String getMessageClass()
    {
        return messageClass;
    }


    public void setMessageClass( String messageClass )
    {
        this.messageClass = messageClass;
    }


    public T getMessage()
    {
        return message;
    }


    public void setMessage( T message )
    {
        this.message = message;
    }


    @Override
    public String toString()
    {
        return "FailedStreamMessage [id=" + id + ", failedDate=" + failedDate + ", companyId=" + companyId
            + ", streamRetryFailed=" + streamRetryFailed + ", messageClass=" + messageClass + ", message=" + message + "]";
    }
}
