package com.realtech.socialsurvey.core.vo;

import java.math.BigDecimal;


public class SubscriptionVO
{
    private String id;

    private BigDecimal balance;
    private Integer billingDayOfMonth;
    private String billingPeriodEndDate;
    private String billingPeriodStartDate;
    private Integer currentBillingCycle;

    private String createdAt;
    private String updatedAt;
    private String firstBillingDate;

    private BigDecimal nextBillAmount;
    private String nextBillingDate;
    private BigDecimal nextBillingPeriodAmount;

    private long companyId;
    private String companyName;
    private long companyAdminId;
    private String companyAdminFirstName;
    private String companyAdminLastName;


    public String getId()
    {
        return id;
    }


    public void setId( String id )
    {
        this.id = id;
    }


    public BigDecimal getBalance()
    {
        return balance;
    }


    public void setBalance( BigDecimal balance )
    {
        this.balance = balance;
    }


    public Integer getBillingDayOfMonth()
    {
        return billingDayOfMonth;
    }


    public void setBillingDayOfMonth( Integer billingDayOfMonth )
    {
        this.billingDayOfMonth = billingDayOfMonth;
    }


    public String getBillingPeriodEndDate()
    {
        return billingPeriodEndDate;
    }


    public void setBillingPeriodEndDate( String billingPeriodEndDate )
    {
        this.billingPeriodEndDate = billingPeriodEndDate;
    }


    public String getBillingPeriodStartDate()
    {
        return billingPeriodStartDate;
    }


    public void setBillingPeriodStartDate( String billingPeriodStartDate )
    {
        this.billingPeriodStartDate = billingPeriodStartDate;
    }


    public Integer getCurrentBillingCycle()
    {
        return currentBillingCycle;
    }


    public void setCurrentBillingCycle( Integer currentBillingCycle )
    {
        this.currentBillingCycle = currentBillingCycle;
    }


    public String getCreatedAt()
    {
        return createdAt;
    }


    public void setCreatedAt( String createdAt )
    {
        this.createdAt = createdAt;
    }


    public String getUpdatedAt()
    {
        return updatedAt;
    }


    public void setUpdatedAt( String updatedAt )
    {
        this.updatedAt = updatedAt;
    }


    public String getFirstBillingDate()
    {
        return firstBillingDate;
    }


    public void setFirstBillingDate( String firstBillingDate )
    {
        this.firstBillingDate = firstBillingDate;
    }


    public BigDecimal getNextBillAmount()
    {
        return nextBillAmount;
    }


    public void setNextBillAmount( BigDecimal nextBillAmount )
    {
        this.nextBillAmount = nextBillAmount;
    }


    public String getNextBillingDate()
    {
        return nextBillingDate;
    }


    public void setNextBillingDate( String nextBillingDate )
    {
        this.nextBillingDate = nextBillingDate;
    }


    public BigDecimal getNextBillingPeriodAmount()
    {
        return nextBillingPeriodAmount;
    }


    public void setNextBillingPeriodAmount( BigDecimal nextBillingPeriodAmount )
    {
        this.nextBillingPeriodAmount = nextBillingPeriodAmount;
    }


    public long getCompanyId()
    {
        return companyId;
    }


    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }


    public String getCompanyName()
    {
        return companyName;
    }


    public void setCompanyName( String companyName )
    {
        this.companyName = companyName;
    }


    public long getCompanyAdminId()
    {
        return companyAdminId;
    }


    public void setCompanyAdminId( long companyAdminId )
    {
        this.companyAdminId = companyAdminId;
    }


    public String getCompanyAdminFirstName()
    {
        return companyAdminFirstName;
    }


    public void setCompanyAdminFirstName( String companyAdminFirstName )
    {
        this.companyAdminFirstName = companyAdminFirstName;
    }


    public String getCompanyAdminLastName()
    {
        return companyAdminLastName;
    }


    public void setCompanyAdminLastName( String companyAdminLastName )
    {
        this.companyAdminLastName = companyAdminLastName;
    }

}
