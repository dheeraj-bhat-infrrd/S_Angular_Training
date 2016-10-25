package com.realtech.socialsurvey.core.entities;

import com.realtech.socialsurvey.core.enums.LoneWolfTransactionParticipantsType;

public class LoneWolfClassificationCode
{
    private String Id;
    private String LWCompanyCode;
    private String Code;
    private String Name;
    private String InactiveDate;
    private LoneWolfTransactionParticipantsType loneWolfTransactionParticipantsType;
    
    
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
    public LoneWolfTransactionParticipantsType getLoneWolfTransactionParticipantsType()
    {
        return loneWolfTransactionParticipantsType;
    }
    public void setLoneWolfTransactionParticipantsType( LoneWolfTransactionParticipantsType loneWolfTransactionParticipantsType )
    {
        this.loneWolfTransactionParticipantsType = loneWolfTransactionParticipantsType;
    }

}
