package com.realtech.socialsurvey.api.models.request;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.realtech.socialsurvey.core.entities.ftp.FtpUploadRequest;


public class FailedStormMessage implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String reasonForFailure;

    @JsonProperty ( "transactionIngestionMessage")
    private FtpUploadRequest ftpUploadRequest;
    
    private boolean sendOnlyToSocialSurveyAdmin;


    public String getReasonForFailure()
    {
        return reasonForFailure;
    }


    public void setReasonForFailure( String reasonForFailure )
    {
        this.reasonForFailure = reasonForFailure;
    }


    public FtpUploadRequest getFtpUploadRequest()
    {
        return ftpUploadRequest;
    }


    public void setFtpUploadRequest( FtpUploadRequest ftpUploadRequest )
    {
        this.ftpUploadRequest = ftpUploadRequest;
    }

    
    

    public boolean isSendOnlyToSocialSurveyAdmin()
    {
        return sendOnlyToSocialSurveyAdmin;
    }


    public void setSendOnlyToSocialSurveyAdmin( boolean sendOnlyToSocialSurveyAdmin )
    {
        this.sendOnlyToSocialSurveyAdmin = sendOnlyToSocialSurveyAdmin;
    }


    @Override
    public String toString()
    {
        return "FailedStormMessage [reasonForFailure=" + reasonForFailure + ", ftpUploadRequest=" + ftpUploadRequest
            + ", sendOnlyToSocialSurveyAdmin=" + sendOnlyToSocialSurveyAdmin + "]";
    }


}
