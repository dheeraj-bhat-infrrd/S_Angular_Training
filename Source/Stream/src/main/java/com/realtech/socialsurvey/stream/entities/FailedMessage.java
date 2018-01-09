package com.realtech.socialsurvey.stream.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;


/**
 * Created by nishit on 04/01/18.
 */
public class FailedMessage extends BaseMongoEntity implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String messageType;
    private int retryCounts;
    private boolean willRetry;
    private boolean permanentFailure;
    private boolean retrySuccessful;
    private String errorMessage;
    private String thrwStr;
    private String thrwStacktrace;
    private Timestamp createdOn;
    private Timestamp modifiedOn;
    private List<Timestamp> retries;


    public String getMessageType()
    {
        return messageType;
    }


    public void setMessageType( String messageType )
    {
        this.messageType = messageType;
    }


    public int getRetryCounts()
    {
        return retryCounts;
    }


    public void setRetryCounts( int retryCounts )
    {
        this.retryCounts = retryCounts;
    }


    public boolean isWillRetry()
    {
        return willRetry;
    }


    public void setWillRetry( boolean willRetry )
    {
        this.willRetry = willRetry;
    }


    public boolean isPermanentFailure()
    {
        return permanentFailure;
    }


    public void setPermanentFailure( boolean permanentFailure )
    {
        this.permanentFailure = permanentFailure;
    }


    public boolean isRetrySuccessful()
    {
        return retrySuccessful;
    }


    public void setRetrySuccessful( boolean retrySuccessful )
    {
        this.retrySuccessful = retrySuccessful;
    }


    public String getErrorMessage()
    {
        return errorMessage;
    }


    public void setErrorMessage( String errorMessage )
    {
        this.errorMessage = errorMessage;
    }


    public String getThrwStr()
    {
        return thrwStr;
    }


    public void setThrwStr( String thrwStr )
    {
        this.thrwStr = thrwStr;
    }


    public String getThrwStacktrace()
    {
        return thrwStacktrace;
    }


    public void setThrwStacktrace( String thrwStacktrace )
    {
        this.thrwStacktrace = thrwStacktrace;
    }


    public Timestamp getCreatedOn()
    {
        return createdOn;
    }


    public void setCreatedOn( Timestamp createdOn )
    {
        this.createdOn = createdOn;
    }


    public Timestamp getModifiedOn()
    {
        return modifiedOn;
    }


    public void setModifiedOn( Timestamp modifiedOn )
    {
        this.modifiedOn = modifiedOn;
    }


    public List<Timestamp> getRetries()
    {
        return retries;
    }


    public void setRetries( List<Timestamp> retries )
    {
        this.retries = retries;
    }


    @Override public String toString()
    {
        return "FailedMessage{" +
            "retryCounts=" + retryCounts +
            ", willRetry=" + willRetry +
            ", permanentFailure=" + permanentFailure +
            ", retrySuccessful=" + retrySuccessful +
            ", errorMessage='" + errorMessage + '\'' +
            ", thrwStr='" + thrwStr + '\'' +
            ", thrwStacktrace='" + thrwStacktrace + '\'' +
            ", createdOn=" + createdOn +
            ", modifiedOn=" + modifiedOn +
            ", retries=" + retries +
            "} " + super.toString();
    }
}
