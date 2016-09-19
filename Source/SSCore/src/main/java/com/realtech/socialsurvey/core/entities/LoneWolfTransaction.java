package com.realtech.socialsurvey.core.entities;

import java.util.List;


public class LoneWolfTransaction
{
    private String Id;
    private String Status;
    private String StatusCode;
    private List<LoneWolfClientContact> ClientContacts;
    private List<LoneWolfTier> Tiers;
    private String CloseDate;
    private String Number;
    private LoneWolfClassification Classification;

    public String getId()
    {
        return Id;
    }


    public void setId( String id )
    {
        Id = id;
    }

    public LoneWolfClassification getClassification()
    {
        return Classification;
    }


    public void setClassification( LoneWolfClassification classification )
    {
        Classification = classification;
    }


    public String getNumber()
    {
        return Number;
    }


    public void setNumber( String number )
    {
        Number = number;
    }


    public String getStatus()
    {
        return Status;
    }


    public void setStatus( String status )
    {
        Status = status;
    }


    public String getCloseDate()
    {
        return CloseDate;
    }


    public void setCloseDate( String closeDate )
    {
        CloseDate = closeDate;
    }


    public String getStatusCode()
    {
        return StatusCode;
    }


    public void setStatusCode( String statusCode )
    {
        StatusCode = statusCode;
    }


    public List<LoneWolfClientContact> getClientContacts()
    {
        return ClientContacts;
    }


    public void setClientContacts( List<LoneWolfClientContact> clientContacts )
    {
        ClientContacts = clientContacts;
    }


    public List<LoneWolfTier> getTiers()
    {
        return Tiers;
    }


    public void setTiers( List<LoneWolfTier> tiers )
    {
        Tiers = tiers;
    }
}
