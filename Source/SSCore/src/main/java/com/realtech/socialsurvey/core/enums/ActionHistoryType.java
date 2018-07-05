package com.realtech.socialsurvey.core.enums;

public enum ActionHistoryType
{
    FLAGGED(0), UNFLAGGED(1), ESCALATE(2), RESOLVED(3), SUBMIT(4) ;
    
    private int value;

    ActionHistoryType( int value )
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }

}
