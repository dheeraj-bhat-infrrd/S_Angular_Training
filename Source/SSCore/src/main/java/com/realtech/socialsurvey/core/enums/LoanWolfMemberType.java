package com.realtech.socialsurvey.core.enums;

public enum LoanWolfMemberType
{
    SELLING( "S" ), LISTING( "L" );


    private final String mode;


    LoanWolfMemberType( String mode )
    {
        this.mode = mode;
    }


    public String getMode()
    {
        return mode;
    }



}
