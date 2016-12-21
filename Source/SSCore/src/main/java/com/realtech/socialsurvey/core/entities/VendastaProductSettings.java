package com.realtech.socialsurvey.core.entities;

/**
 * holds the vendasta product(Reputation management) details for a particular hierarchy 
 */
public class VendastaProductSettings
{

    private String accountId;
    private String apiUser;
    private String apiKey;


    public String getApiUser()
    {
        return apiUser;
    }


    public void setApiUser( String apiUser )
    {
        this.apiUser = apiUser;
    }


    public String getApiKey()
    {
        return apiKey;
    }


    public void setApiKey( String apiKey )
    {
        this.apiKey = apiKey;
    }


    public String getAccountId()
    {
        return accountId;
    }


    public void setAccountId( String accountId )
    {
        this.accountId = accountId;
    }


    @Override
    public String toString()
    {
        return "VendastaProductSettings [AccountId=" + accountId + "]";
    }


}