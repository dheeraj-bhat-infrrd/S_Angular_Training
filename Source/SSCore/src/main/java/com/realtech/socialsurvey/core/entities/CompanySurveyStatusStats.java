package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name= "company_status_count")
public class CompanySurveyStatusStats  implements Serializable {
    
    private static final long serialVersionUID = 1L;

    
    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column ( name = "company_status_count_id")
    private String dailySurveyStatusStatsId;
    
    @Column( name = "company_id")
    private long companyId;

    @Column( name = "date_val")
    private Date transactionDate;
    
    @Column(name = "sent")
    private int surveyInvitationSentCount;
    
    @Column(name = "received")
    private int transactionReceivedCount;  
    
    @Column( name = "completed")
    private  int surveycompletedCount;
    
    @Column( name = "reminder_sent")
    private long surveyReminderSentCount;

    @Column( name = "corrupted")
    private long corruptedCount;
    
    
    @Column( name = "duplicate")
    private long duplicateCount;
    
    @Column( name = "old_record")
    private long oldRecordCount;

    @Column( name = "ignored")
    private long ignoredCount;
    
    @Column( name = "mismatched")
    private long mismatchedCount;
    
    @Column( name = "not_allowed")
    private long notAllowedCount;

    public String getDailySurveyStatusStatsId()
    {
        return dailySurveyStatusStatsId;
    }

    public void setDailySurveyStatusStatsId( String dailySurveyStatusStatsId )
    {
        this.dailySurveyStatusStatsId = dailySurveyStatusStatsId;
    }

    public long getCompanyId()
    {
        return companyId;
    }

    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }

  
    public int getSurveyInvitationSentCount()
    {
        return surveyInvitationSentCount;
    }

    public void setSurveyInvitationSentCount( int surveyInvitationSentCount )
    {
        this.surveyInvitationSentCount = surveyInvitationSentCount;
    }

    public int getTransactionReceivedCount()
    {
        return transactionReceivedCount;
    }

    public void setTransactionReceivedCount( int transactionReceivedCount )
    {
        this.transactionReceivedCount = transactionReceivedCount;
    }

    public int getSurveycompletedCount()
    {
        return surveycompletedCount;
    }

    public void setSurveycompletedCount( int surveycompletedCount )
    {
        this.surveycompletedCount = surveycompletedCount;
    }

    public long getSurveyReminderSentCount()
    {
        return surveyReminderSentCount;
    }

    public void setSurveyReminderSentCount( long surveyReminderSentCount )
    {
        this.surveyReminderSentCount = surveyReminderSentCount;
    }

    public long getCorruptedCount()
    {
        return corruptedCount;
    }

    public void setCorruptedCount( long corruptedCount )
    {
        this.corruptedCount = corruptedCount;
    }

    public long getDuplicateCount()
    {
        return duplicateCount;
    }

    public void setDuplicateCount( long duplicateCount )
    {
        this.duplicateCount = duplicateCount;
    }

    public long getOldRecordCount()
    {
        return oldRecordCount;
    }

    public void setOldRecordCount( long oldRecordCount )
    {
        this.oldRecordCount = oldRecordCount;
    }

    public long getIgnoredCount()
    {
        return ignoredCount;
    }

    public void setIgnoredCount( long ignoredCount )
    {
        this.ignoredCount = ignoredCount;
    }

    public long getMismatchedCount()
    {
        return mismatchedCount;
    }

    public void setMismatchedCount( long mismatchedCount )
    {
        this.mismatchedCount = mismatchedCount;
    }

    public long getNotAllowedCount()
    {
        return notAllowedCount;
    }

    public void setNotAllowedCount( long notAllowedCount )
    {
        this.notAllowedCount = notAllowedCount;
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
