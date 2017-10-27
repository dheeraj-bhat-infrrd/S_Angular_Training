package com.realtech.socialsurvey.core.entities;

public class CompanyDigestRequestData
{
    private long companyId;
    private String companyName;
    private String recipientMailId;


    public long getCompanyId()
    {
        return companyId;
    }


    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }


    public String getCompanyName()
    {
        return companyName;
    }


    public void setCompanyName( String companyName )
    {
        this.companyName = companyName;
    }


    public String getRecipientMailId()
    {
        return recipientMailId;
    }


    public void setRecipientMailId( String recipientMailId )
    {
        this.recipientMailId = recipientMailId;
    }


}
