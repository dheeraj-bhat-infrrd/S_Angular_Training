package com.realtech.socialsurvey.core.vo;

import java.sql.Date;

public class TransactionMonitorGraphDataVO
{
    private long companyId;
    
    private int totalTransactionsCount;
    
    private int surveyInvitationSentCount;
    
    private long surveyReminderSentCount;
    
    private  int surveycompletedCount;

    private Date transactionDate;

    
    public long getCompanyId()
    {
        return companyId;
    }

    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }

    public int getTotalTransactionsCount()
    {
        return totalTransactionsCount;
    }

    public void setTotalTransactionsCount( int totalTransactionsCount )
    {
        this.totalTransactionsCount = totalTransactionsCount;
    }

    public int getSurveyInvitationSentCount()
    {
        return surveyInvitationSentCount;
    }

    public void setSurveyInvitationSentCount( int surveyInvitationSentCount )
    {
        this.surveyInvitationSentCount = surveyInvitationSentCount;
    }

    public long getSurveyReminderSentCount()
    {
        return surveyReminderSentCount;
    }

    public void setSurveyReminderSentCount( long surveyReminderSentCount )
    {
        this.surveyReminderSentCount = surveyReminderSentCount;
    }

    public int getSurveycompletedCount()
    {
        return surveycompletedCount;
    }

    public void setSurveycompletedCount( int surveycompletedCount )
    {
        this.surveycompletedCount = surveycompletedCount;
    }

    public Date getTransactionDate()
    {
        return transactionDate;
    }

    public void setTransactionDate( Date transactionDate )
    {
        this.transactionDate = transactionDate;
    }
}
