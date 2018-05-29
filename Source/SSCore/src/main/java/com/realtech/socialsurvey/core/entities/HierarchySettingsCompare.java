package com.realtech.socialsurvey.core.entities;

import java.math.BigInteger;

public class HierarchySettingsCompare
{
    private long id;
    private BigInteger currentValue;
    private BigInteger expectedValue;


    public long getId()
    {
        return id;
    }


    public void setId( long id )
    {
        this.id = id;
    }


    public BigInteger getCurrentValue()
    {
        return currentValue;
    }


    public void setCurrentValue( BigInteger currentValue )
    {
        this.currentValue = currentValue;
    }


    public BigInteger getExpectedValue()
    {
        return expectedValue;
    }


    public void setExpectedValue( BigInteger expectedValue )
    {
        this.expectedValue = expectedValue;
    }


}
