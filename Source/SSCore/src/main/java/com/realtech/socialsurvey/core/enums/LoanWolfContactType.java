package com.realtech.socialsurvey.core.enums;

public enum LoanWolfContactType
{
    SELLER( "S" ), BUYER( "B" );

    private final String code;


    private LoanWolfContactType( String code )
    {
        this.code = code;
    }


    public String getCode()
    {
        return code;
    }


}
