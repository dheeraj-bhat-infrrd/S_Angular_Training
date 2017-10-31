package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table ( name = "survey_pre_initiation")
public class ReportingSurveyPreInititation implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column ( name = "SURVEY_PRE_INITIATION_ID")
    private long surveyPreIntitiationId;

    @Column ( name = "SURVEY_SOURCE")
    private String surveySource;

    @Column ( name = "SURVEY_SOURCE_ID")
    private String surveySourceId;

    @Column ( name = "COMPANY_ID")
    private long companyId;

    @Column ( name = "AGENT_ID")
    private long agentId;

    @Column ( name = "AGENT_NAME")
    private String agentName;

    @Column ( name = "AGENT_EMAILID")
    private String agentEmailId;


    @Column ( name = "CUSTOMER_FIRST_NAME")
    private String customerFirstName;

    @Column ( name = "CUSTOMER_LAST_NAME")
    private String customerLastName;

    @Column ( name = "CUSTOMER_EMAIL_ID")
    private String customerEmailId;

    @Column ( name = "CUSTOMER_INTERACTION_DETAILS")
    private String customerInteractionDetails;

    @Column ( name = "ENGAGEMENT_CLOSED_TIME")
    private Timestamp engagementClosedTime;

    @Column ( name = "REMINDER_COUNTS")
    private int reminderCounts;

    @Column ( name = "LAST_REMINDER_TIME")
    private Timestamp lastReminderTime;

    @Column ( name = "STATUS")
    private int status;

    @Column ( name = "CREATED_ON")
    private Timestamp createdOn;

    @Column ( name = "MODIFIED_ON")
    private Timestamp modifiedOn;

    @Column ( name = "REGION_COLLECTION_ID")
    private long regionCollectionId;

    @Column ( name = "BRANCH_COLLECTION_ID")
    private long branchCollectionId;

    @Column ( name = "COLLECTION_NAME")
    private String collectionName;

    @Column ( name = "ERROR_CODE")
    private String errorCode;

    @Column ( name = "STATE")
    private String state;

    @Column ( name = "CITY")
    private String city;

    @Column ( name = "TRANSACTION_TYPE")
    private String transactionType;

    @Column ( name = "CREATED_ON_EST")
    private Timestamp createdOnEst;

    @Column ( name = "MODIFIED_ON_EST")
    private Timestamp modifiedOnEst;

    @Column ( name = "ENGAGEMENT_CLOSED_TIME_EST")
    private Timestamp engagementClosedTimeEst;

    @Column ( name = "LAST_REMINDER_TIME_EST")
    private Timestamp lastReminderTimeEst;


    public long getSurveyPreIntitiationId()
    {
        return surveyPreIntitiationId;
    }


    public void setSurveyPreIntitiationId( long surveyPreIntitiationId )
    {
        this.surveyPreIntitiationId = surveyPreIntitiationId;
    }


    public String getSurveySource()
    {
        return surveySource;
    }


    public void setSurveySource( String surveySource )
    {
        this.surveySource = surveySource;
    }


    public String getSurveySourceId()
    {
        return surveySourceId;
    }


    public void setSurveySourceId( String surveySourceId )
    {
        this.surveySourceId = surveySourceId;
    }


    public long getCompanyId()
    {
        return companyId;
    }


    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }


    public long getAgentId()
    {
        return agentId;
    }


    public void setAgentId( long agentId )
    {
        this.agentId = agentId;
    }


    public String getAgentName()
    {
        return agentName;
    }


    public void setAgentName( String agentName )
    {
        this.agentName = agentName;
    }


    public String getAgentEmailId()
    {
        return agentEmailId;
    }


    public void setAgentEmailId( String agentEmailId )
    {
        this.agentEmailId = agentEmailId;
    }


    public String getCustomerFirstName()
    {
        return customerFirstName;
    }


    public void setCustomerFirstName( String customerFirstName )
    {
        this.customerFirstName = customerFirstName;
    }


    public String getCustomerLastName()
    {
        return customerLastName;
    }


    public void setCustomerLastName( String customerLastName )
    {
        this.customerLastName = customerLastName;
    }


    public String getCustomerEmailId()
    {
        return customerEmailId;
    }


    public void setCustomerEmailId( String customerEmailId )
    {
        this.customerEmailId = customerEmailId;
    }


    public String getCustomerInteractionDetails()
    {
        return customerInteractionDetails;
    }


    public void setCustomerInteractionDetails( String customerInteractionDetails )
    {
        this.customerInteractionDetails = customerInteractionDetails;
    }


    public Timestamp getEngagementClosedTime()
    {
        return engagementClosedTime;
    }


    public void setEngagementClosedTime( Timestamp engagementClosedTime )
    {
        this.engagementClosedTime = engagementClosedTime;
    }


    public int getReminderCounts()
    {
        return reminderCounts;
    }


    public void setReminderCounts( int reminderCounts )
    {
        this.reminderCounts = reminderCounts;
    }


    public Timestamp getLastReminderTime()
    {
        return lastReminderTime;
    }


    public void setLastReminderTime( Timestamp lastReminderTime )
    {
        this.lastReminderTime = lastReminderTime;
    }


    public int getStatus()
    {
        return status;
    }


    public void setStatus( int status )
    {
        this.status = status;
    }


    public Timestamp getCreatedOn()
    {
        return createdOn;
    }


    public void setCreatedOn( Timestamp createdOn )
    {
        this.createdOn = createdOn;
    }


    public Timestamp getModifiedOn()
    {
        return modifiedOn;
    }


    public void setModifiedOn( Timestamp modifiedOn )
    {
        this.modifiedOn = modifiedOn;
    }


    public long getRegionCollectionId()
    {
        return regionCollectionId;
    }


    public void setRegionCollectionId( long regionCollectionId )
    {
        this.regionCollectionId = regionCollectionId;
    }


    public long getBranchCollectionId()
    {
        return branchCollectionId;
    }


    public void setBranchCollectionId( long branchCollectionId )
    {
        this.branchCollectionId = branchCollectionId;
    }


    public String getCollectionName()
    {
        return collectionName;
    }


    public void setCollectionName( String collectionName )
    {
        this.collectionName = collectionName;
    }


    public String getErrorCode()
    {
        return errorCode;
    }


    public void setErrorCode( String errorCode )
    {
        this.errorCode = errorCode;
    }


    public String getState()
    {
        return state;
    }


    public void setState( String state )
    {
        this.state = state;
    }


    public String getCity()
    {
        return city;
    }


    public void setCity( String city )
    {
        this.city = city;
    }


    public String getTransactionType()
    {
        return transactionType;
    }


    public void setTransactionType( String transactionType )
    {
        this.transactionType = transactionType;
    }


    public Timestamp getCreatedOnEst()
    {
        return createdOnEst;
    }


    public void setCreatedOnEst( Timestamp createdOnEst )
    {
        this.createdOnEst = createdOnEst;
    }


    public Timestamp getModifiedOnEst()
    {
        return modifiedOnEst;
    }


    public void setModifiedOnEst( Timestamp modifiedOnEst )
    {
        this.modifiedOnEst = modifiedOnEst;
    }


    public Timestamp getEngagementClosedTimeEst()
    {
        return engagementClosedTimeEst;
    }


    public void setEngagementClosedTimeEst( Timestamp engagementClosedTimeEst )
    {
        this.engagementClosedTimeEst = engagementClosedTimeEst;
    }


    public Timestamp getLastReminderTimeEst()
    {
        return lastReminderTimeEst;
    }


    public void setLastReminderTimeEst( Timestamp lastReminderTimeEst )
    {
        this.lastReminderTimeEst = lastReminderTimeEst;
    }


    @Override
    public String toString()
    {
        return "ReportingSurveyPreInititation [surveyPreIntitiationId=" + surveyPreIntitiationId + ", surveySource="
            + surveySource + ", surveySourceId=" + surveySourceId + ", companyId=" + companyId + ", agentId=" + agentId
            + ", agentName=" + agentName + ", agentEmailId=" + agentEmailId + ", customerFirstName=" + customerFirstName
            + ", customerLastName=" + customerLastName + ", customerEmailId=" + customerEmailId
            + ", customerInteractionDetails=" + customerInteractionDetails + ", engagementClosedTime=" + engagementClosedTime
            + ", reminderCounts=" + reminderCounts + ", lastReminderTime=" + lastReminderTime + ", status=" + status
            + ", createdOn=" + createdOn + ", modifiedOn=" + modifiedOn + ", regionCollectionId=" + regionCollectionId
            + ", branchCollectionId=" + branchCollectionId + ", collectionName=" + collectionName + ", errorCode=" + errorCode
            + ", state=" + state + ", city=" + city + ", transactionType=" + transactionType + ", createdOnEst=" + createdOnEst
            + ", modifiedOnEst=" + modifiedOnEst + ", engagementClosedTimeEst=" + engagementClosedTimeEst
            + ", lastReminderTimeEst=" + lastReminderTimeEst + "]";
    }


}
