package com.realtech.socialsurvey.web.entities;

public class Payment
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
}
