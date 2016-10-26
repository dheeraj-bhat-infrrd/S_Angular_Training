package com.realtech.socialsurvey.core.entities;


public class LoneWolfClassificationCode
{
    private String Id;
    private String LWCompanyCode;
    private String Code;
    private String Name;
    private String InactiveDate;
    private String loneWolfTransactionParticipantsType;
    
    
    public String getId()
    {
        return Id;
    }
    public void setId( String id )
    {
        Id = id;
    }
    public String getLWCompanyCode()
    {
        return LWCompanyCode;
    }
    public void setLWCompanyCode( String lWCompanyCode )
    {
        LWCompanyCode = lWCompanyCode;
    }
    public String getCode()
    {
        return Code;
    }
    public void setCode( String code )
    {
        Code = code;
    }
    public String getName()
    {
        return Name;
    }
    public void setName( String name )
    {
        Name = name;
    }
    public String getInactiveDate()
    {
        return InactiveDate;
    }
    public void setInactiveDate( String inactiveDate )
    {
        InactiveDate = inactiveDate;
    }
    public String getLoneWolfTransactionParticipantsType()
    {
        return loneWolfTransactionParticipantsType;
    }
    public void setLoneWolfTransactionParticipantsType( String loneWolfTransactionParticipantsType )
    {
        this.loneWolfTransactionParticipantsType = loneWolfTransactionParticipantsType;
    }

}
