package com.realtech.socialsurvey.compute.entities;

public class SurveyPutVO
{
    private TransactionInfoPutVO transactionInfo;
    private ServiceProviderInfo serviceProviderInfo;
    private int lineNumber;


    public TransactionInfoPutVO getTransactionInfo()
    {
        return transactionInfo;
    }


    public void setTransactionInfo( TransactionInfoPutVO transactionInfo )
    {
        this.transactionInfo = transactionInfo;
    }


    public ServiceProviderInfo getServiceProviderInfo()
    {
        return serviceProviderInfo;
    }


    public void setServiceProviderInfo( ServiceProviderInfo serviceProviderInfo )
    {
        this.serviceProviderInfo = serviceProviderInfo;
    }


    public int getLineNumber()
    {
        return lineNumber;
    }


    public void setLineNumber( int lineNumber )
    {
        this.lineNumber = lineNumber;
    }


}
