package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;


/**
 * Holds the preinitiation state of survey
 */
@Entity
@Table ( name = "SURVEY_PRE_INITIATION")
@NamedQuery ( name = "SurveyPreInitiation.findAll", query = "SELECT s FROM SurveyPreInitiation s")
public class SurveyPreInitiation implements Serializable
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

    @Column ( name = "REGION_COLLECTION_ID")
    private long regionCollectionId;

    @Column ( name = "BRANCH_COLLECTION_ID")
    private long branchCollectionId;


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
    
    @Column ( name = "IS_SURVEY_REQUEST_SENT")
    private int isSurveyRequestSent;

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

    @Column ( name = "COLLECTION_NAME")
    private String collectionName;

    @ManyToOne ( fetch = FetchType.LAZY)
    @JoinColumn ( name = "AGENT_ID", referencedColumnName = "USER_ID", insertable = false, updatable = false)
    private User user;

    @Column ( name = "ERROR_CODE")
    private String errorCode;

    @Column ( name = "STATE")
    private String state;

	@Column ( name = "CITY")
	private String city;
	
	@Column ( name = "TRANSACTION_TYPE")
	private String transactionType;

	@Column( name = "PARTICIPANT_TYPE")
	private int participantType;
	
	@Column( name = "PROPERTY_ADDRESS")
	private String propertyAddress;
	
	@Column( name = "LOAN_PROCESSOR_NAME")
	private String loanProcessorName;
	
	@Column( name = "LOAN_PROCESSOR_EMAIL")
	private String loanProcessorEmail;

	@Column( name = "CUSTOM_FIELD_ONE")	
	private String customFieldOne;	
		
	@Column( name = "CUSTOM_FIELD_TWO")	
	private String customFieldTwo;	
		
	@Column( name = "CUSTOM_FIELD_THREE")	
	private String customFieldThree;	
		
	@Column( name = "CUSTOM_FIELD_FOUR")	
	private String customFieldFour;	
		
	@Column( name = "CUSTOM_FIELD_FIVE")	
	private String customFieldFive;

	
        
	@Transient
    private String errorCodeDescription;


    public String getErrorCodeDescription()
    {
        return errorCodeDescription;
    }


    public void setErrorCodeDescription( String errorCodeDescription )
    {
        this.errorCodeDescription = errorCodeDescription;
    }


    public String getErrorCode()
    {
        return errorCode;
    }


    public void setErrorCode( String errorCode )
    {
        this.errorCode = errorCode;
    }


    public User getUser()
    {
        return this.user;
    }


    public void setUser( User user )
    {
        this.user = user;
    }


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


    public int getIsSurveyRequestSent()
    {
        return isSurveyRequestSent;
    }


    public void setIsSurveyRequestSent( int isSurveyRequestSent )
    {
        this.isSurveyRequestSent = isSurveyRequestSent;
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


    public String getCollectionName()
    {
        return collectionName;
    }


    public void setCollectionName( String collectionName )
    {
        this.collectionName = collectionName;
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
    
    public int getParticipantType()
    {
        return participantType;
    }


    public void setParticipantType( int participantType )
    {
        this.participantType = participantType;
    }


	public String getPropertyAddress() {
		return propertyAddress;
	}


	public void setPropertyAddress(String propertyAddress) {
		this.propertyAddress = propertyAddress;
	}


	public String getLoanProcessorName() {
		return loanProcessorName;
	}


	public void setLoanProcessorName(String loanProcessorName) {
		this.loanProcessorName = loanProcessorName;
	}


	public String getLoanProcessorEmail() {
		return loanProcessorEmail;
	}


	public void setLoanProcessorEmail(String loanProcessorEmail) {
		this.loanProcessorEmail = loanProcessorEmail;
	}

	public String getCustomFieldOne() {	
		return customFieldOne;	
	}
	
 	public void setCustomFieldOne(String customFieldOne) {	
		this.customFieldOne = customFieldOne;	
	}	
 	
 	public String getCustomFieldTwo() {	
		return customFieldTwo;	
	}	
 	
 	public void setCustomFieldTwo(String customFieldTwo) {	
		this.customFieldTwo = customFieldTwo;	
	}	
 	
 	public String getCustomFieldThree() {	
		return customFieldThree;	
	}	
 	
 	public void setCustomFieldThree(String customFieldThree) {	
		this.customFieldThree = customFieldThree;	
	}
 	
 	public String getCustomFieldFour() {	
		return customFieldFour;	
	}
 	
 	public void setCustomFieldFour(String customFieldFour) {	
		this.customFieldFour = customFieldFour;	
	}
 	
 	public String getCustomFieldFive() {	
		return customFieldFive;	
	}
 	
 	public void setCustomFieldFive(String customFieldFive) {	
		this.customFieldFive = customFieldFive;	
	}

}
