package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The persistent class for the license_details database table.
 */
@Entity
@Table ( name = "LICENSE_DETAILS")
@NamedQuery ( name = "LicenseDetail.findAll", query = "SELECT l FROM LicenseDetail l")
public class LicenseDetail implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column ( name = "LICENSE_ID")
    private long licenseId;

    @Column ( name = "SUBSCRIPTION_ID")
    private String subscriptionId;

    @Column ( name = "CREATED_BY")
    private String createdBy;

    @Column ( name = "CREATED_ON")
    private Timestamp createdOn;

    @Column ( name = "LICENSE_END_DATE")
    private Timestamp licenseEndDate;

    @Column ( name = "LICENSE_START_DATE")
    private Timestamp licenseStartDate;

    @Column ( name = "MODIFIED_BY")
    private String modifiedBy;

    @Column ( name = "MODIFIED_ON")
    private Timestamp modifiedOn;

    @Column ( name = "PAYMENT_MODE")
    private String paymentMode;

    @Column ( name = "NEXT_RETRY_TIME")
    private Timestamp nextRetryTime;

    @Column ( name = "PAYMENT_RETRIES")
    private int paymentRetries;

    @Column ( name = "IS_SUBSCRIPTION_DUE")
    private int isSubscriptionDue;

    private int status;

    @Column ( name = "NEXT_INVOICE_BILLING_DATE")
    private Timestamp nextInvoiceBillingDate;
    
    @Column ( name = "SUBSCRIPTION_ID_SOURCE")
    private String subscriptionIdSource;

    @Column ( name = "INVOICE_CYCLE_DATE")
    private int invoiceCycleDate;
    
    @Column ( name = "INVOICE_CYCLE_PERIOD_MONTH")
    private int invoiceCyclePeriodInMonth;
    
    @Column ( name = "RECIPIENT_MAIL_ID")
    private String recipientMailId;
    
    // bi-directional many-to-one association to RetriedTransaction
    @OneToMany ( mappedBy = "licenseDetail", fetch = FetchType.LAZY)
    private List<RetriedTransaction> retriedTransactions;

    // bi-directional many-to-one association to DisabledAccount
    @OneToMany ( mappedBy = "licenseDetail", fetch = FetchType.LAZY)
    private List<DisabledAccount> disabledAccounts;


    public List<RetriedTransaction> getRetriedTransactions()
    {
        return retriedTransactions;
    }


    public void setRetriedTransactions( List<RetriedTransaction> retriedTransactions )
    {
        this.retriedTransactions = retriedTransactions;
    }


    public String getSubscriptionId()
    {
        return subscriptionId;
    }


    public String getSubscriptionIdSource()
    {
        return subscriptionIdSource;
    }


    public void setSubscriptionId( String subscriptionId )
    {
        this.subscriptionId = subscriptionId;
    }


    public void setSubscriptionIdSource( String subscriptionIdSource )
    {
        this.subscriptionIdSource = subscriptionIdSource;
    }

    // bi-directional many-to-one association to AccountsMaster
    @ManyToOne ( fetch = FetchType.LAZY)
    @JoinColumn ( name = "ACCOUNTS_MASTER_ID")
    private AccountsMaster accountsMaster;

    // bi-directional many-to-one association to Company
    @ManyToOne ( fetch = FetchType.LAZY)
    @JoinColumn ( name = "COMPANY_ID")
    private Company company;


    public LicenseDetail()
    {}


    public long getLicenseId()
    {
        return this.licenseId;
    }


    public void setLicenseId( long licenseId )
    {
        this.licenseId = licenseId;
    }


    public String getCreatedBy()
    {
        return this.createdBy;
    }


    public void setCreatedBy( String createdBy )
    {
        this.createdBy = createdBy;
    }


    public Timestamp getCreatedOn()
    {
        return this.createdOn;
    }


    public void setCreatedOn( Timestamp createdOn )
    {
        this.createdOn = createdOn;
    }


    public Timestamp getLicenseEndDate()
    {
        return this.licenseEndDate;
    }


    public void setLicenseEndDate( Timestamp licenseEndDate )
    {
        this.licenseEndDate = licenseEndDate;
    }


    public Timestamp getLicenseStartDate()
    {
        return this.licenseStartDate;
    }


    public void setLicenseStartDate( Timestamp licenseStartDate )
    {
        this.licenseStartDate = licenseStartDate;
    }


    public String getModifiedBy()
    {
        return this.modifiedBy;
    }


    public void setModifiedBy( String modifiedBy )
    {
        this.modifiedBy = modifiedBy;
    }


    public Timestamp getModifiedOn()
    {
        return this.modifiedOn;
    }


    public void setModifiedOn( Timestamp modifiedOn )
    {
        this.modifiedOn = modifiedOn;
    }


    public String getPaymentMode()
    {
        return this.paymentMode;
    }


    public void setPaymentMode( String paymentMode )
    {
        this.paymentMode = paymentMode;
    }


    public Timestamp getNextRetryTime()
    {
        return nextRetryTime;
    }


    public void setNextRetryTime( Timestamp nextRetryTime )
    {
        this.nextRetryTime = nextRetryTime;
    }


    public int getPaymentRetries()
    {
        return paymentRetries;
    }


    public void setPaymentRetries( int paymentRetries )
    {
        this.paymentRetries = paymentRetries;
    }


    public int getIsSubscriptionDue()
    {
        return isSubscriptionDue;
    }


    public void setIsSubscriptionDue( int isSubscriptionDue )
    {
        this.isSubscriptionDue = isSubscriptionDue;
    }


    public int getStatus()
    {
        return this.status;
    }


    public void setStatus( int status )
    {
        this.status = status;
    }


    public Timestamp getNextInvoiceBillingDate()
    {
        return nextInvoiceBillingDate;
    }


    public void setNextInvoiceBillingDate( Timestamp nextInvoiceBillingDate )
    {
        this.nextInvoiceBillingDate = nextInvoiceBillingDate;
    }


    public int getInvoiceCycleDate()
    {
        return invoiceCycleDate;
    }


    public void setInvoiceCycleDate( int invoiceCycleDate )
    {
        this.invoiceCycleDate = invoiceCycleDate;
    }


    public int getInvoiceCyclePeriodInMonth()
    {
        return invoiceCyclePeriodInMonth;
    }


    public void setInvoiceCyclePeriodInMonth( int invoiceCyclePeriodInMonth )
    {
        this.invoiceCyclePeriodInMonth = invoiceCyclePeriodInMonth;
    }

    public String getRecipientMailId()
    {
        return recipientMailId;
    }


    public void setRecipientMailId( String recipientMailId )
    {
        this.recipientMailId = recipientMailId;
    }


    
    public List<DisabledAccount> getDisabledAccounts()
    {
        return disabledAccounts;
    }


    public void setDisabledAccounts( List<DisabledAccount> disabledAccounts )
    {
        this.disabledAccounts = disabledAccounts;
    }


    public AccountsMaster getAccountsMaster()
    {
        return this.accountsMaster;
    }


    public void setAccountsMaster( AccountsMaster accountsMaster )
    {
        this.accountsMaster = accountsMaster;
    }


    public Company getCompany()
    {
        return this.company;
    }


    public void setCompany( Company company )
    {
        this.company = company;
    }

}