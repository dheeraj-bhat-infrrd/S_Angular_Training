package com.realtech.socialsurvey.api.models.request;

import java.io.Serializable;



public class PaymentRequest implements Serializable
{
    private String nonce;
    private String cardHolderName;


    public String getNonce()
    {
        return nonce;
    }


    public void setNonce( String nonce )
    {
        this.nonce = nonce;
    }


    public String getCardHolderName()
    {
        return cardHolderName;
    }


    public void setCardHolderName( String cardHolderName )
    {
        this.cardHolderName = cardHolderName;
    }


    @Override public String toString()
    {
        return "PaymentRequest{" +
            "nonce='" + nonce + '\'' +
            ", cardHolderName='" + cardHolderName + '\'' +
            '}';
    }
}
