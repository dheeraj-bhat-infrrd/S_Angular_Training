package com.realtech.socialsurvey.web.entities;

public class Payment
{
    private String nonce;
    private String cardHolderName;
    private String name;
    private String email;
    private String message;


    public String getName()
    {
        return name;
    }


    public void setName( String name )
    {
        this.name = name;
    }


    public String getEmail()
    {
        return email;
    }


    public void setEmail( String email )
    {
        this.email = email;
    }


    public String getMessage()
    {
        return message;
    }


    public void setMessage( String message )
    {
        this.message = message;
    }


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
