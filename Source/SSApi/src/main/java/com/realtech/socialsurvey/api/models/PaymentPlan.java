package com.realtech.socialsurvey.api.models;

public class PaymentPlan
{
    private int planId;
    private int level;
    private String planName;
    private String planCurrency;
    private double amount;
    private String terms;
    private String supportingText;


    public int getPlanId()
    {
        return planId;
    }


    public void setPlanId( int planId )
    {
        this.planId = planId;
    }


    public int getLevel()
    {
        return level;
    }


    public void setLevel( int level )
    {
        this.level = level;
    }


    public String getPlanName()
    {
        return planName;
    }


    public void setPlanName( String planName )
    {
        this.planName = planName;
    }


    public String getPlanCurrency()
    {
        return planCurrency;
    }


    public void setPlanCurrency( String planCurrency )
    {
        this.planCurrency = planCurrency;
    }


    public double getAmount()
    {
        return amount;
    }


    public void setAmount( double amount )
    {
        this.amount = amount;
    }


    public String getTerms()
    {
        return terms;
    }


    public void setTerms( String terms )
    {
        this.terms = terms;
    }


    public String getSupportingText()
    {
        return supportingText;
    }


    public void setSupportingText( String supportingText )
    {
        this.supportingText = supportingText;
    }

}
