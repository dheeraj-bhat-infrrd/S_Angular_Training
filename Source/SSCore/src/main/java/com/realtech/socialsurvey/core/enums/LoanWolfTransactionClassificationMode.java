package com.realtech.socialsurvey.core.enums;

public enum LoanWolfTransactionClassificationMode
{
    SELLING( "S" ), LISTING( "L" ), DOUBLEAGENT( "A" ), OFFICEAGENTS( "O" );

    private final String mode;


    LoanWolfTransactionClassificationMode( String mode )
    {
        this.mode = mode;
    }


    public String getMode()
    {
        return mode;
    }

}
