package com.realtech.socialsurvey.api.models.v2;

import org.springframework.stereotype.Component;


@Component
public class TransactionInfoGetV2VO
{
    private String transactionRef;
    private String surveySentDateTime;
    private String transactionDateTime;
    private String transactionCity;
    private String transactionState;
    private String transactionType;
    private String customerFirstName;
    private String customerLastName;
    private String customerEmail;


    public String getTransactionRef()
    {
        return transactionRef;
    }


    public void setTransactionRef( String transactionRef )
    {
        this.transactionRef = transactionRef;
    }


    public String getSurveySentDateTime()
    {
        return surveySentDateTime;
    }


    public void setSurveySentDateTime( String surveySentDateTime )
    {
        this.surveySentDateTime = surveySentDateTime;
    }


    public String getTransactionDateTime()
    {
        return transactionDateTime;
    }


    public void setTransactionDateTime( String transactionDateTime )
    {
        this.transactionDateTime = transactionDateTime;
    }


    public String getTransactionCity()
    {
        return transactionCity;
    }


    public void setTransactionCity( String transactionCity )
    {
        this.transactionCity = transactionCity;
    }


    public String getTransactionState()
    {
        return transactionState;
    }


    public void setTransactionState( String transactionState )
    {
        this.transactionState = transactionState;
    }


    public String getTransactionType()
    {
        return transactionType;
    }


    public void setTransactionType( String transactionType )
    {
        this.transactionType = transactionType;
    }


    public String getCustomerFirstName()
    {
        return customerFirstName;
    }


    public void setCustomerFirstName( String customerFirstName )
    {
        this.customerFirstName = customerFirstName;
    }


    public String getCustomerLastName()
    {
        return customerLastName;
    }


    public void setCustomerLastName( String customerLastName )
    {
        this.customerLastName = customerLastName;
    }


    public String getCustomerEmail()
    {
        return customerEmail;
    }


    public void setCustomerEmail( String customerEmail )
    {
        this.customerEmail = customerEmail;
    }
}
