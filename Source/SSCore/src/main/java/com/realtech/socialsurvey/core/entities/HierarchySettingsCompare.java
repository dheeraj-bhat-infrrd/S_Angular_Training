package com.realtech.socialsurvey.core.entities;

public class HierarchySettingsCompare
{
    private long id;
    private long currentValue;
    private long expectedValue;


    public long getId()
    {
        return id;
    }


    public void setId( long id )
    {
        this.id = id;
    }


    public long getCurrentValue()
    {
        return currentValue;
    }


    public void setCurrentValue( long currentValue )
    {
        this.currentValue = currentValue;
    }


    public long getExpectedValue()
    {
        return expectedValue;
    }


    public void setExpectedValue( long expectedValue )
    {
        this.expectedValue = expectedValue;
    }


}
