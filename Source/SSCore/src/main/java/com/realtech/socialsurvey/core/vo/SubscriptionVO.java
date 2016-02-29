package com.realtech.socialsurvey.core.vo;

import java.math.BigDecimal;
import java.util.Calendar;


public class SubscriptionVO
{
    private String id;

    private BigDecimal balance;
    private Integer billingDayOfMonth;
    private Calendar billingPeriodEndDate;
    private Calendar billingPeriodStartDate;
    private Integer currentBillingCycle;

    private Calendar createdAt;
    private Calendar updatedAt;
    private Calendar firstBillingDate;

    private BigDecimal nextBillAmount;
    private Calendar nextBillingDate;
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


    public Calendar getBillingPeriodEndDate()
    {
        return billingPeriodEndDate;
    }


    public void setBillingPeriodEndDate( Calendar billingPeriodEndDate )
    {
        this.billingPeriodEndDate = billingPeriodEndDate;
    }


    public Calendar getBillingPeriodStartDate()
    {
        return billingPeriodStartDate;
    }


    public void setBillingPeriodStartDate( Calendar billingPeriodStartDate )
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


    public Calendar getCreatedAt()
    {
        return createdAt;
    }


    public void setCreatedAt( Calendar createdAt )
    {
        this.createdAt = createdAt;
    }


    public Calendar getUpdatedAt()
    {
        return updatedAt;
    }


    public void setUpdatedAt( Calendar updatedAt )
    {
        this.updatedAt = updatedAt;
    }


    public Calendar getFirstBillingDate()
    {
        return firstBillingDate;
    }


    public void setFirstBillingDate( Calendar firstBillingDate )
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


    public Calendar getNextBillingDate()
    {
        return nextBillingDate;
    }


    public void setNextBillingDate( Calendar nextBillingDate )
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
