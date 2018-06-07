package com.realtech.socialsurvey.compute.entities;

import java.io.Serializable;


public class FailedFtpRequest implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String reasonForFailure;
    private TransactionIngestionMessage transactionIngestionMessage;
    private boolean sendOnlyToSocialSurveyAdmin;


    public String getReasonForFailure()
    {
        return reasonForFailure;
    }


    public void setReasonForFailure( String reasonForFailure )
    {
        this.reasonForFailure = reasonForFailure;
    }


    public TransactionIngestionMessage getTransactionIngestionMessage()
    {
        return transactionIngestionMessage;
    }


    public void setTransactionIngestionMessage( TransactionIngestionMessage transactionIngestionMessage )
    {
        this.transactionIngestionMessage = transactionIngestionMessage;
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
        return "FailedFtpRequest [reasonForFailure=" + reasonForFailure + ", transactionIngestionMessage="
            + transactionIngestionMessage + ", sendOnlyToSocialSurveyAdmin=" + sendOnlyToSocialSurveyAdmin + "]";
    }
}
