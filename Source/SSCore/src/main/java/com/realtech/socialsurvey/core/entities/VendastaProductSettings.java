package com.realtech.socialsurvey.core.entities;

/**
 * holds the vendasta product(Reputation management) details for a particular hierarchy 
 */
public class VendastaProductSettings
{

    public static final String PRODUCT_ID = "RM";
    private String accountId;


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