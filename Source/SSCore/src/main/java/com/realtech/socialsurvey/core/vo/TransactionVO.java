package com.realtech.socialsurvey.core.vo;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import com.braintreegateway.CreditCard;
import com.braintreegateway.StatusEvent;
import com.braintreegateway.Transaction.Status;

public class TransactionVO
{
    private String id;
    
    private BigDecimal amount;
    private Calendar createdAt;
    private CreditCard creditCard;
    
    private Status status;
    private List<StatusEvent> statusHistory;
    private String subscriptionId;
    
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
    public BigDecimal getAmount()
    {
        return amount;
    }
    public void setAmount( BigDecimal amount )
    {
        this.amount = amount;
    }
    public Calendar getCreatedAt()
    {
        return createdAt;
    }
    public void setCreatedAt( Calendar createdAt )
    {
        this.createdAt = createdAt;
    }
    public CreditCard getCreditCard()
    {
        return creditCard;
    }
    public void setCreditCard( CreditCard creditCard )
    {
        this.creditCard = creditCard;
    }
    public Status getStatus()
    {
        return status;
    }
    public void setStatus( Status status )
    {
        this.status = status;
    }
    public List<StatusEvent> getStatusHistory()
    {
        return statusHistory;
    }
    public void setStatusHistory( List<StatusEvent> statusHistory )
    {
        this.statusHistory = statusHistory;
    }
    public String getSubscriptionId()
    {
        return subscriptionId;
    }
    public void setSubscriptionId( String subscriptionId )
    {
        this.subscriptionId = subscriptionId;
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
