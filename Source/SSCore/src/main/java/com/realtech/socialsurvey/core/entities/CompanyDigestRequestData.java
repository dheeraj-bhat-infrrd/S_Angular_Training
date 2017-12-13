package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.util.Set;


public class CompanyDigestRequestData implements Serializable
{
    private static final long serialVersionUID = 1L;
    private long companyId;
    private String companyName;
    private Set<String> recipientMailIds;


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


    public Set<String> getRecipientMailIds()
    {
        return recipientMailIds;
    }


    public void setRecipientMailIds( Set<String> recipientMailIds )
    {
        this.recipientMailIds = recipientMailIds;
    }


    @Override
    public String toString()
    {
        return "CompanyDigestRequestData [companyId=" + companyId + ", companyName=" + companyName + ", recipientMailIds="
            + recipientMailIds + "]";
    }
}
