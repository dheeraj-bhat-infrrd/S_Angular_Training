package com.realtech.socialsurvey.core.entities;

import com.realtech.socialsurvey.core.enums.AccountType;


public enum Plan
{
    INDIVIDUAL( 1, AccountType.INDIVIDUAL.getValue(), "Individual", "$", "", "" ),
    BUSINESS( 2, AccountType.ENTERPRISE.getValue(), "Business", "$", "", "" ),
    ENTERPRISE( 3, AccountType.ENTERPRISE.getValue(), "Enterprise", "$", "", "" );

    Plan( int planId, int accountMasterId, String planName, String planCurrency, String terms, String supportingText )
    {
        this.planId = planId;
        this.accountMasterId = accountMasterId;
        this.planName = planName;
        this.planCurrency = planCurrency;
        this.terms = terms;
        this.supportingText = supportingText;
    }

    private int planId;
    private int accountMasterId;
    private String planName;
    private String planCurrency;
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


    public int getAccountMasterId()
    {
        return accountMasterId;
    }


    public void setAccountMasterId( int accountMasterId )
    {
        this.accountMasterId = accountMasterId;
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
