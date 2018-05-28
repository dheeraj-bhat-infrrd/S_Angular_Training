package com.realtech.socialsurvey.compute.enums;

public enum ActionHistoryType
{
    FLAGGED(0), UNFLAGGED(1), ESCALATE(2), RESOLVED(3), PRIVATE_MESSAGE(4), EMAIL(5);
    
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
