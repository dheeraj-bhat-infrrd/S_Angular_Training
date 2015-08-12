package com.realtech.socialsurvey.core.entities;

public class RealtorToken
{

    private String realtorId;
    private String realtorProfileLink;


    public String getRealtorId()
    {
        return realtorId;
    }


    public void setRealtorId( String realtorId )
    {
        this.realtorId = realtorId;
    }


    public String getRealtorProfileLink()
    {
        return realtorProfileLink;
    }


    public void setRealtorProfileLink( String realtorProfileLink )
    {
        this.realtorProfileLink = realtorProfileLink;
    }


    @Override
    public String toString()
    {
        return "RealtorToken [realtorId=" + realtorId + ", realtorProfileLink=" + realtorProfileLink + "]";
    }
}